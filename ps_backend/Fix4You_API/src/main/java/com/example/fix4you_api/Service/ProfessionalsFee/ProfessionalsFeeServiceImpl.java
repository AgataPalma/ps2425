package com.example.fix4you_api.Service.ProfessionalsFee;

import com.example.fix4you_api.Data.Enums.PaymentStatusEnum;
import com.example.fix4you_api.Data.Enums.ScheduleStateEnum;
import com.example.fix4you_api.Data.Enums.ServiceStateEnum;
import com.example.fix4you_api.Data.Models.*;
import com.example.fix4you_api.Data.Models.Dtos.SimpleProfessionalDTO;
import com.example.fix4you_api.Data.MongoRepositories.ProfessionalFeeRepository;
import com.example.fix4you_api.Event.ProfessionalFee.FeeCreationEvent;
import com.example.fix4you_api.Event.ProfessionalFee.FeePaymentCompletionEvent;
import com.example.fix4you_api.Service.Notification.NotificationService;
import com.example.fix4you_api.Service.Professional.ProfessionalService;
import com.example.fix4you_api.Service.ScheduleAppointment.ScheduleAppointmentService;
import com.example.fix4you_api.Service.Service.ServiceService;
import com.example.fix4you_api.Service.User.UserService;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProfessionalsFeeServiceImpl implements ProfessionalsFeeService{

    private final ProfessionalFeeRepository professionalFeeRepository;
    private final ProfessionalService professionalService;
    private final UserService userService;
    private final ServiceService serviceService;
    private final ScheduleAppointmentService scheduleAppointmentService;
    private final ApplicationEventPublisher eventPublisher;
    private final NotificationService notificationService;

    // Map para associar o ID do profissional ao seu SseEmitter
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Override
    public List<ProfessionalsFee> getAllProfessionalsFee() {
        List<ProfessionalsFee> professionalsFeeList = professionalFeeRepository.findAll();
        Iterator<ProfessionalsFee> iterator = professionalsFeeList.iterator();

        while (iterator.hasNext()) {
            ProfessionalsFee fee = iterator.next();
            Professional professional = professionalService.getProfessionalByIdNotThrow(fee.getProfessional().getId());
            if (professional == null) {
                iterator.remove(); // Safely remove the element
            } else {
                fee.getProfessional().setEmail(professional.getEmail());
                fee.getProfessional().setName(professional.getName());
                fee.getProfessional().setNif(professional.getNif());
            }
        }
        return professionalsFeeList;
    }

    @Override
    public ProfessionalsFee getProfessionalsFeeById(String id) {
        return findOrThrow(id);
    }

    @Override
    public List<ProfessionalsFee> getProfessionalsFeeForProfessionalId(String professionalId) {
        return professionalFeeRepository.findByProfessional_Id(professionalId);
    }

    @Override
    public List<ProfessionalsFee> getProfessionalsFeeForProfessionalIdAndPaymentStatus(String professionalId, PaymentStatusEnum paymentStatusEnum) {
        return professionalFeeRepository.findByProfessional_IdAndPaymentStatus(professionalId, paymentStatusEnum);
    }

    @Override
    public List<ProfessionalTotalSpent> getTopPriceProfessionals() {
        // Fetch the raw results: clientId and totalSpent
        List<ProfessionalsFee> results = professionalFeeRepository.findTopProfessionalsByTotalSpending();

        // Group by clientId and count services
        Map<String, Double> clientServiceCounts = results.stream()
                .filter(professionalFee -> professionalFee.getProfessional().getId() != null)
                .collect(Collectors.groupingBy(professionalFee -> professionalFee.getProfessional().getId(), Collectors.summingDouble(ProfessionalsFee::getValue)));

        // Process the results
        List<ProfessionalTotalSpent> professionalTotalSpents = clientServiceCounts.entrySet().stream()
                .map(result -> new ProfessionalTotalSpent(
                        (String) result.getKey(),
                        professionalService.getProfessionalById(result.getKey()).getName(),
                        ((Number) result.getValue()).doubleValue()     // totalSpent
                ))
                .sorted((a, b) -> Double.compare(b.getTotalSpent(), a.getTotalSpent())) // Sort by totalSpent (descending)
                .limit(10) // Top 10 clients
                .collect(Collectors.toList());

        for (var i=0; i<professionalTotalSpents.size(); i++){
            try{
                ProfessionalTotalSpent professionalTotalSpent = professionalTotalSpents.get(i);
                professionalTotalSpent.setProfessionalName(professionalService.getProfessionalById(professionalTotalSpent.getProfessionalId()).getName());
            } catch(Exception e){
                professionalTotalSpents.remove(i);
            }
        }

        return professionalTotalSpents;
    }

    @Override
    public List<ProfessionalTotalSpent> sendEmailTopPriceProfessionals() {
        List<ProfessionalTotalSpent> professionalTotalSpents = this.getTopPriceProfessionals();

        for(var i=0; i< professionalTotalSpents.size(); i++){
            if(professionalTotalSpents.get(i).getProfessionalId() != null) {
                User user = userService.getUser(professionalTotalSpents.get(i).getProfessionalId());
                if(user != null) {
                    userService.sendEmailTopUsers(user);
                }
            }
        }

        return professionalTotalSpents;
    }

    @Override
    @Transactional
    public ProfessionalsFee createProfessionalsFee(ProfessionalsFee professionalsFee) {
        ProfessionalsFee fee = professionalFeeRepository.save(professionalsFee);
        eventPublisher.publishEvent(new FeeCreationEvent(this, fee));
        return fee;
    }

    @Override
    @Transactional
    public ProfessionalsFee createProfessionalFeeForRespectiveMonth(String professionalId, int numberServices, String relatedMonthYear) {
        Professional professional = professionalService.getProfessionalById(professionalId);

        Map<String, Object> updates = new HashMap<>();
        updates.put("isSuspended", true);
        professionalService.partialUpdateProfessional(professionalId, updates);

        SimpleProfessionalDTO feeProfessional = new SimpleProfessionalDTO();
        feeProfessional.setId(professional.getId());
        feeProfessional.setEmail(professional.getEmail());
        feeProfessional.setName(professional.getName());
        feeProfessional.setNif(professional.getNif());

        ProfessionalsFee newProfessionalsFee = new ProfessionalsFee(feeProfessional, numberServices, relatedMonthYear, PaymentStatusEnum.PENDING);
        eventPublisher.publishEvent(new FeeCreationEvent(this, newProfessionalsFee));

        //Notificação

        Notification notification = new Notification();
        notification.setProfessionalId(newProfessionalsFee.getProfessional().getId());
        notification.setMessage("Tem taxas para pagar!");
        notification.setRead(false);
        notification.setType("fee");
        notification.setReferenceId(newProfessionalsFee.getId());
        notification.setCreatedAt(new Date());

        // Adicionar detalhes da taxa
        notification.setFeeValue(newProfessionalsFee.getValue());
        notification.setNumberServices(newProfessionalsFee.getNumberServices());
        notification.setRelatedMonthYear(newProfessionalsFee.getRelatedMonthYear());

        // Corrigir paymentStatus
        notification.setPaymentStatus(newProfessionalsFee.getPaymentStatus().toString());

        // Corrigir paymentDate
        LocalDateTime localDateTime = newProfessionalsFee.getPaymentDate();
        if (localDateTime != null) {
            Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
            notification.setPaymentDate(date);
        } else {
            notification.setPaymentDate(null);
        }

        notificationService.createNotification(notification);

        // Enviar notificação via SSE
        sendSseMessageToProfessional(notification.getProfessionalId(), "Você tem uma nova notificação");

        return professionalFeeRepository.save(newProfessionalsFee);
    }

    @Override
    @Transactional
    public ProfessionalsFee updateProfessionalsFee(String id, ProfessionalsFee professionalsFee) {
        ProfessionalsFee existingProfessionalsFee = findOrThrow(id);
        BeanUtils.copyProperties(professionalsFee, existingProfessionalsFee, "id");
        return professionalFeeRepository.save(existingProfessionalsFee);
    }

    @Override
    @Transactional
    public ProfessionalsFee partialUpdateProfessionalsFee(String id, Map<String, Object> updates) {
        ProfessionalsFee existingProfessionalsFee = findOrThrow(id);

        updates.forEach((key, value) -> {
            switch (key) {
                case "professional" -> existingProfessionalsFee.setProfessional((SimpleProfessionalDTO) value);
                case "value" -> existingProfessionalsFee.setValue((float) value);
                case "numberServices" -> existingProfessionalsFee.setNumberServices((int) value);
                case "relatedMonthYear" -> existingProfessionalsFee.setRelatedMonthYear((String) value);
                case "paymentDate" -> {
                    if (value instanceof LocalDateTime) {
                        existingProfessionalsFee.setPaymentDate((LocalDateTime) value);
                    } else if (value instanceof String) {
                        existingProfessionalsFee.setPaymentDate(LocalDateTime.parse((CharSequence) value));
                    }
                }
                case "paymentStatus" -> {
                    try {
                        existingProfessionalsFee.setPaymentStatus(PaymentStatusEnum.valueOf(value.toString().toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        throw new RuntimeException("Valor inválido para o estado: " + value);
                    }
                }
                default -> throw new RuntimeException("Campo inválido no pedido da atualização!\n");
            }
        });

        return professionalFeeRepository.save(existingProfessionalsFee);
    }

    @Override
    @Transactional
    public void deleteProfessionalFee(String id) {
        professionalFeeRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void deleteProfessionalFeesForProfessional(String professionalId) {
        professionalFeeRepository.deleteByProfessional_Id(professionalId);
    }

    @Override
    @Transactional
    public void checkAndCreateMonthlyFees() {
        YearMonth lastMonth = YearMonth.now().minusMonths(1);
        LocalDateTime startOfLastMonth = lastMonth.atDay(1).atStartOfDay();
        LocalDateTime endOfLastMonth = lastMonth.atEndOfMonth().atStartOfDay();

        List<Professional> professionals = professionalService.getAllActiveProfessionals();

        for (Professional professional : professionals) {

            //Se já tiver alguma fee pendente, não cria mais
            List<ProfessionalsFee> professionalsFeesPending = getProfessionalsFeeForProfessionalIdAndPaymentStatus(professional.getId(), PaymentStatusEnum.PENDING);
            if(!professionalsFeesPending.isEmpty()) {
                continue;
            }

            int completedLastMonthServicesCount = 0;

            List<com.example.fix4you_api.Data.Models.Service> completedServices = serviceService.getServicesByProfessionalIdAndState(professional.getId(), ServiceStateEnum.COMPLETED);

            for (com.example.fix4you_api.Data.Models.Service service : completedServices) {
                List<ScheduleAppointment> completedLastMonthScheduleAppointments = scheduleAppointmentService.getScheduleAppointmentsByServiceIdAndStateAndDateFinishBetween(service.getId(), ScheduleStateEnum.COMPLETED, startOfLastMonth, endOfLastMonth);

                if(!completedLastMonthScheduleAppointments.isEmpty()) {
                    completedLastMonthServicesCount++;
                }

            }

            if (completedLastMonthServicesCount >= 10) {
                createProfessionalFeeForRespectiveMonth(professional.getId(), completedLastMonthServicesCount, lastMonth.toString());
            }

        }
    }

    @Transactional
    @Override
    public ProfessionalsFee setFeeAsPaid(String id) throws DocumentException {
        ProfessionalsFee fee = findOrThrow(id);
        Professional professional = professionalService.getProfessionalById(fee.getProfessional().getId());

        fee.setPaymentStatus(PaymentStatusEnum.COMPLETED);
        fee.setPaymentDate(LocalDateTime.now());
        fee.setInvoice(generateInvoice(fee, professional));
        ProfessionalsFee updatedFee = professionalFeeRepository.save(fee);

        eventPublisher.publishEvent(new FeePaymentCompletionEvent(this, updatedFee, professional));

        return fee;
    }

    @Override
    public SseEmitter streamSseEmitter(String professionalId) {
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);

        // Associa o emitter ao ID do profissional
        emitters.put(professionalId, emitter);

        // Remover o emitter quando a conexão for finalizada
        emitter.onCompletion(() -> emitters.remove(professionalId));
        emitter.onTimeout(() -> {
            emitter.complete();
            emitters.remove(professionalId);
        });
        emitter.onError((e) -> {
            emitter.completeWithError(e);
            emitters.remove(professionalId);
        });

        return emitter;
    }

    // Método para enviar mensagens a um profissional específico
    @Override
    public void sendSseMessageToProfessional(String professionalId, String message) {
        SseEmitter emitter = emitters.get(professionalId);
        if (emitter != null) {
            try {
                emitter.send(SseEmitter.event()
                        .name("message")
                        .data(message));
            } catch (IOException e) {
                emitter.completeWithError(e);
                emitters.remove(professionalId);
            }
        }
    }

    public byte[] generateInvoice(ProfessionalsFee fee, Professional professional) throws DocumentException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, byteArrayOutputStream);

        document.open();
        document.add(new Paragraph("Invoice"));
        document.add(new Paragraph("Amount paid: " + fee.getValue()));
        document.add(new Paragraph("Name: " + professional.getName()));
        document.add(new Paragraph("NIF: " + professional.getNif()));
        document.add(new Paragraph("Invoice Date: " + LocalDateTime.now()));

        document.close();

        return byteArrayOutputStream.toByteArray();
    }

    private ProfessionalsFee findOrThrow(String id) {
        return professionalFeeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(String.format("Taxa do profissional com o id %s não encontrada!", id)));
    }

}