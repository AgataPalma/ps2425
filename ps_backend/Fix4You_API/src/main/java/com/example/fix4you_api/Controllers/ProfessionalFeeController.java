package com.example.fix4you_api.Controllers;

import com.example.fix4you_api.Data.Enums.PaymentStatusEnum;
import com.example.fix4you_api.Data.Models.Dtos.ProfessionalsFeeSaveDTO;
import com.example.fix4you_api.Data.Models.Professional;
import com.example.fix4you_api.Data.Models.ProfessionalTotalSpent;
import com.example.fix4you_api.Data.Models.ProfessionalsFee;
import com.example.fix4you_api.Data.Models.Notification;
import com.example.fix4you_api.Service.Notification.NotificationService;
import com.example.fix4you_api.Service.Professional.ProfessionalService;
import com.example.fix4you_api.Service.ProfessionalsFee.ProfessionalsFeeService;
import com.itextpdf.text.DocumentException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("professionalFees")
@RequiredArgsConstructor
public class ProfessionalFeeController {

    private final NotificationService notificationService;
    private final ProfessionalsFeeService professionalsFeeService;
    private final ProfessionalService professionalService;

    // Map para associar o ID do profissional ao seu SseEmitter
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    // Endpoint SSE modificado para receber o ID do profissional
    @CrossOrigin(origins = "http://localhost:3000")
    @GetMapping(value = "/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter streamSse(@RequestParam String professionalId) {
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
    private void sendSseMessageToProfessional(String professionalId, String message) {
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

    @PostMapping
    public ResponseEntity<ProfessionalsFee> createProfessionalFee(@RequestBody ProfessionalsFeeSaveDTO professionalsFeeDto) {
        ProfessionalsFee createdProfessionalsFee = professionalsFeeService.createProfessionalsFee(professionalsFeeDto.toDomain());

        // Criar uma notificação com detalhes da taxa
        Notification notification = new Notification();
        notification.setProfessionalId(createdProfessionalsFee.getProfessional().getId());
        notification.setMessage("Tem taxas para pagar!");
        notification.setRead(false);
        notification.setType("fee");
        notification.setReferenceId(createdProfessionalsFee.getId());
        notification.setCreatedAt(new Date());

        // Adicionar detalhes da taxa
        notification.setFeeValue(createdProfessionalsFee.getValue());
        notification.setNumberServices(createdProfessionalsFee.getNumberServices());
        notification.setRelatedMonthYear(createdProfessionalsFee.getRelatedMonthYear());

        // Corrigir paymentStatus
        notification.setPaymentStatus(createdProfessionalsFee.getPaymentStatus().toString());

        // Corrigir paymentDate
        LocalDateTime localDateTime = createdProfessionalsFee.getPaymentDate();
        if (localDateTime != null) {
            Date date = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
            notification.setPaymentDate(date);
        } else {
            notification.setPaymentDate(null);
        }

        notificationService.createNotification(notification);

        // Enviar notificação via SSE
        sendSseMessageToProfessional(notification.getProfessionalId(), "Você tem uma nova notificação");

        return new ResponseEntity<>(createdProfessionalsFee, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ProfessionalsFee>> getProfessionalFee() {
        List<ProfessionalsFee> professionalsFee = professionalsFeeService.getAllProfessionalsFee();
        return new ResponseEntity<>(professionalsFee, HttpStatus.OK);
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<List<ProfessionalsFee>> getUserProfessionalFee(@PathVariable("id") String professionalId) {
        List<ProfessionalsFee> professionalsFee = professionalsFeeService.getProfessionalsFeeForProfessionalId(professionalId);
        return new ResponseEntity<>(professionalsFee, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProfessionalsFee> getProfessionalFee(@PathVariable String id) {
        ProfessionalsFee professionalsFee = professionalsFeeService.getProfessionalsFeeById(id);
        return new ResponseEntity<>(professionalsFee, HttpStatus.OK);
    }

    @GetMapping("/topExpensesProfessionals")
    public ResponseEntity<?> getTopExpensesProfessionals() {
        List<ProfessionalTotalSpent> clientIds = professionalsFeeService.getTopPriceProfessionals();
        return ResponseEntity.ok(clientIds);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProfessionalFee(@PathVariable String id) {
        professionalsFeeService.deleteProfessionalFee(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProfessionalsFee> updateProfessionalFee(@PathVariable String id,
                                                                  @RequestBody ProfessionalsFeeSaveDTO professionalFee
    ) {
        ProfessionalsFee updatedProfessionalsFee = professionalsFeeService.updateProfessionalsFee(id, professionalFee.toDomain());
        return new ResponseEntity<>(updatedProfessionalsFee, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ProfessionalsFee> partialUpdateProfessionalsFee(@PathVariable String id, @RequestBody Map<String, Object> updates) {
        ProfessionalsFee updatedProfessionalsFee = professionalsFeeService.partialUpdateProfessionalsFee(id, updates);
        return new ResponseEntity<>(updatedProfessionalsFee, HttpStatus.OK);
    }

    @PutMapping("/{id}/pay")
    public ResponseEntity<ProfessionalsFee> setFeeAsPaid(@PathVariable String id) throws DocumentException {
        ProfessionalsFee fee = professionalsFeeService.setFeeAsPaid(id);

        // check if he has more fees to pay -> if not (suspended = false)
        boolean anythingToPay = false;
        Professional professional = professionalService.getProfessionalById(fee.getProfessional().getId());
        boolean currentSuspendedStatus = professional.isSupended();
        List<ProfessionalsFee> feesList = professionalsFeeService.getProfessionalsFeeForProfessionalId(fee.getProfessional().getId());

        for (ProfessionalsFee currentFee : feesList) {
            if (!currentFee.getPaymentStatus().equals(PaymentStatusEnum.COMPLETED)) {
                anythingToPay = true;
            }
        }

        if (!anythingToPay && currentSuspendedStatus) {
            professionalService.setProfessionalIsSuspended(fee.getProfessional().getId(), false);
        } else if (anythingToPay && !currentSuspendedStatus) {
            professionalService.setProfessionalIsSuspended(fee.getProfessional().getId(), true);
        }

        //Generate invoice
        return new ResponseEntity<>(fee, HttpStatus.OK);
    }

    @GetMapping("{id}/invoice")
    public ResponseEntity<byte[]> getInvoice(@PathVariable String id) {
        ProfessionalsFee professionalsFee = professionalsFeeService.getProfessionalsFeeById(id);
        if (professionalsFee.getInvoice() == null) {
            throw new RuntimeException("A taxa ainda não foi paga!");
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "invoice_" + id + ".pdf");
        return new ResponseEntity<>(professionalsFee.getInvoice(), headers, HttpStatus.OK);
    }
}