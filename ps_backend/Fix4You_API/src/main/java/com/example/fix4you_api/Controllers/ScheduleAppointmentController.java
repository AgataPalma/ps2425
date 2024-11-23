package com.example.fix4you_api.Controllers;

import com.example.fix4you_api.Data.Enums.ScheduleStateEnum;
import com.example.fix4you_api.Data.Models.ScheduleAppointment;
import com.example.fix4you_api.Data.MongoRepositories.ScheduleAppointmentRepository;
import jakarta.validation.Valid;
import com.example.fix4you_api.Service.ScheduleAppointment.ScheduleAppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.BeanUtils;
import org.springframework.format.annotation.DateTimeFormat;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@RequestMapping("/scheduleAppointments")
@RequiredArgsConstructor
public class ScheduleAppointmentController {
    @Autowired
    private ScheduleAppointmentRepository scheduleAppointmentRepository;

    @Autowired
    private ScheduleAppointmentService scheduleAppointmentService;

    @Autowired
    public ScheduleAppointmentController(ScheduleAppointmentRepository scheduleAppointmentRepository) {
        this.scheduleAppointmentRepository = scheduleAppointmentRepository;
    }

    @PostMapping
    public ResponseEntity<?> addScheduleAppointment(@RequestBody ScheduleAppointment scheduleAppointment) {
        try {

            if (scheduleAppointment.getDateStart().isBefore(LocalDateTime.now())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Start date must be in the future");
            }

            if (scheduleAppointment.getDateFinish().isBefore(LocalDateTime.now())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Finish date must be in the future");
            }

            if (!scheduleAppointment.getDateFinish().isAfter(scheduleAppointment.getDateStart())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Finish date must be after start date");
            }

            var conflicted = false;
            List<ScheduleAppointment> scheduleAppointments = this.scheduleAppointmentRepository.findByProfessionalId(scheduleAppointment.getProfessionalId());
            for (var i=0; i<scheduleAppointments.size(); i++){
                LocalDateTime dateStart = scheduleAppointments.get(i).getDateStart();
                LocalDateTime dateFinish = scheduleAppointments.get(i).getDateFinish();

                if(scheduleAppointment.getDateStart().isAfter(dateStart) && scheduleAppointment.getDateFinish().isBefore(dateFinish)){
                    conflicted = true;
                } else if(scheduleAppointment.getDateStart().isAfter(dateStart) && scheduleAppointment.getDateStart().isBefore(dateFinish)){
                    conflicted = true;
                } else if(scheduleAppointment.getDateFinish().isAfter(dateStart) && scheduleAppointment.getDateFinish().isBefore(dateFinish)){
                    conflicted = true;
                }
                if(conflicted == true){
                    String msg = "Schedule appointment conflicted. Previous one existed where date start: " + dateStart +
                            " and date finish: " + dateFinish;
                    return ResponseEntity.ok(msg);
                }
                conflicted = false;
            }

            scheduleAppointment.setState(ScheduleStateEnum.PENDING);
            this.scheduleAppointmentRepository.save(scheduleAppointment);
            return new ResponseEntity<>(scheduleAppointment, HttpStatus.CREATED);
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllScheduleAppointments() {
        try {
            List<ScheduleAppointment> scheduleAppointments = this.scheduleAppointmentRepository.findAll();
            return ResponseEntity.ok(scheduleAppointments);
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getScheduleAppointmentById(@PathVariable String id) {
        try {
            Optional<ScheduleAppointment> scheduleAppointment = this.scheduleAppointmentRepository.findById(id);
            return (scheduleAppointment.isPresent() ? ResponseEntity.ok(scheduleAppointment.get()) : ResponseEntity.ok("Couldn't find any schedule appointment with the id: '" + id + "'!"));
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/professional/{id}")
    public ResponseEntity<?> getProfessionalScheduleAppointments(@PathVariable("id") String idProfessional) {
        try {
            List<ScheduleAppointment> scheduleAppointments = this.scheduleAppointmentRepository.findByProfessionalId(idProfessional);
            return ResponseEntity.ok(scheduleAppointments);
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/client/{id}")
    public ResponseEntity<?> getClientScheduleAppointments(@PathVariable("id") String idClient) {
        try {
            List<ScheduleAppointment> scheduleAppointments = this.scheduleAppointmentRepository.findByClientId(idClient);
            return ResponseEntity.ok(scheduleAppointments);
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/approve/{id}")
    public ResponseEntity<?> approveScheduleAppointment(@PathVariable("id") String id) {
        try {
            Map<String, Object> updates = new HashMap<>();
            updates.put("state", ScheduleStateEnum.CONFIRMED);

            partialUpdateScheduleAppointment(id, updates);

            Optional<ScheduleAppointment> updatedScheduleAppointment = scheduleAppointmentRepository.findById(id);

            if (updatedScheduleAppointment.isPresent()) {
                return ResponseEntity.ok(updatedScheduleAppointment.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Agendamento de serviço não encontrado!");
            }
        } catch (Exception e) {
            System.err.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro: " + e.getMessage());
        }
    }

    @PutMapping("/disapprove/{id}")
    public ResponseEntity<?> disapproveScheduleAppointment(@PathVariable("id") String id) {
        try {
            Map<String, Object> updates = new HashMap<>();
            updates.put("state", ScheduleStateEnum.CANCELED);

            partialUpdateScheduleAppointment(id, updates);

            Optional<ScheduleAppointment> updatedScheduleAppointment = scheduleAppointmentRepository.findById(id);

            if (updatedScheduleAppointment.isPresent()) {
                return ResponseEntity.ok(updatedScheduleAppointment.get());
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Agendamento de serviço não encontrado!");
            }
        } catch (Exception e) {
            System.err.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Erro: " + e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateScheduleAppointment(@PathVariable String id, @RequestBody ScheduleAppointment scheduleAppointment) {
        try {
            ScheduleAppointment existingScheduleAppointment = scheduleAppointmentRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Agendamento de serviço não encontrado!"));

            var conflicted = false;
            List<ScheduleAppointment> scheduleAppointments = this.scheduleAppointmentRepository.findByProfessionalId(scheduleAppointment.getProfessionalId());

            for (var i=0; i<scheduleAppointments.size(); i++){
                if(!Objects.equals(scheduleAppointment.getId(), scheduleAppointments.get(i).getId())) {
                    LocalDateTime dateStart = scheduleAppointments.get(i).getDateStart();
                    LocalDateTime dateFinish = scheduleAppointments.get(i).getDateFinish();

                    if(scheduleAppointment.getDateStart().isAfter(dateStart) && scheduleAppointment.getDateFinish().isBefore(dateFinish)){
                        conflicted = true;
                    } else if(scheduleAppointment.getDateStart().isAfter(dateStart) && scheduleAppointment.getDateStart().isBefore(dateFinish)){
                        conflicted = true;
                    } else if(scheduleAppointment.getDateFinish().isAfter(dateStart) && scheduleAppointment.getDateFinish().isBefore(dateFinish)){
                        conflicted = true;
                    }
                    if (conflicted == true) {
                        String msg = "Agendamento de serviço com conflitos!. Existia um anterior com a data de início: " + dateStart +
                                " e data de fim: " + dateFinish;
                        return ResponseEntity.ok(msg);
                    }
                }
                conflicted = false;
            }

            BeanUtils.copyProperties(scheduleAppointment, existingScheduleAppointment, "id");
            this.scheduleAppointmentRepository.save(existingScheduleAppointment);
            return ResponseEntity.ok(existingScheduleAppointment);
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> partialUpdateScheduleAppointment(
            @PathVariable String id,
            @RequestBody Map<String, Object> updates) {
        try {
            AtomicBoolean definedDates = new AtomicBoolean(false);
            ScheduleAppointment existingScheduleAppointment = scheduleAppointmentRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Agendamento de serviço não encontrado!"));

            updates.forEach((key, value) -> {
                if(Objects.equals(key, "dateStart") || Objects.equals(key, "dateFinish")){
                    definedDates.set(true);
                }
                switch (key) {
                    case "clientId" -> existingScheduleAppointment.setClientId((String) value);
                    case "professionalId" -> existingScheduleAppointment.setProfessionalId((String) value);
                    case "dateStart" -> existingScheduleAppointment.setDateStart(LocalDateTime.parse((CharSequence) value));
                    case "dateFinish" -> existingScheduleAppointment.setDateFinish(LocalDateTime.parse((CharSequence) value));
                    case "state" -> {
                        try {
                            existingScheduleAppointment.setState(ScheduleStateEnum.valueOf(value.toString().toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        throw new RuntimeException("Valor inválido para o estado: " + value);
                    }
                }

                    default -> throw new RuntimeException("Campo inválido no pedido da atualização!");
                }
            });

            if(definedDates.get()){
                var conflicted = false;
                List<ScheduleAppointment> scheduleAppointments = this.scheduleAppointmentRepository.findByProfessionalId(existingScheduleAppointment.getProfessionalId());
                for (var i=0; i<scheduleAppointments.size(); i++){
                    if(!Objects.equals(existingScheduleAppointment.getId(), scheduleAppointments.get(i).getId())) {
                        LocalDateTime dateStart = scheduleAppointments.get(i).getDateStart();
                        LocalDateTime dateFinish = scheduleAppointments.get(i).getDateFinish();

                        if(existingScheduleAppointment.getDateStart().isAfter(dateStart) && existingScheduleAppointment.getDateFinish().isBefore(dateFinish)){
                            conflicted = true;
                        } else if(existingScheduleAppointment.getDateStart().isAfter(dateStart) && existingScheduleAppointment.getDateStart().isBefore(dateFinish)){
                            conflicted = true;
                        } else if(existingScheduleAppointment.getDateFinish().isAfter(dateStart) && existingScheduleAppointment.getDateFinish().isBefore(dateFinish)){
                            conflicted = true;
                        }
                        if (conflicted == true) {
                            String msg = "Agendamento de serviço com conflitos! Existia um anterior com a data de início: " + dateStart +
                                    " e data de fim: " + dateFinish;
                            return ResponseEntity.ok(msg);
                        }
                    }
                    conflicted = false;
                }
            }

            scheduleAppointmentRepository.save(existingScheduleAppointment);
            return ResponseEntity.ok(existingScheduleAppointment);
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteScheduleAppointment(@PathVariable String id) {
        try {
            Optional<ScheduleAppointment> existingScheduleAppointment = this.scheduleAppointmentRepository.findById(id);
            this.scheduleAppointmentRepository.deleteById(id);
            String msg = (existingScheduleAppointment.isPresent() ? "Agendamento de serviço com o id: '" + id + "' foi eliminado!" : "Não foi possível encontrar nenhum agendamento de serviço com o id: '" + id + "'!");
            return ResponseEntity.ok(msg);
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ocorreu um erro ao tentar eliminar o agendamento de serviço com o id: '" + id + "'!");
        }
    }

    @GetMapping("/connect")
    public ResponseEntity<?> connectGoogle() {
        try {
            String clientId = "980766458886-llsr892dnsvifd706dlog2lc4flr2a1d.apps.googleusercontent.com";
            String redirect_uri = "http://localhost:8080/professionals";
            String scope = "https://www.googleapis.com/auth/calendar";
            String response_type = "code";
            String access_type = "offline";

            String url = String.format("https://accounts.google.com/o/oauth2/v2/auth?client_id=%s&redirect_uri=%s&scope=%s&response_type=%s&access_type=%s", clientId, redirect_uri, scope, response_type, access_type);

            return ResponseEntity.ok(url);
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Could not connect");
        }
    }

    @PostMapping("/save-token")
    public ResponseEntity<?> saveNewToken(@RequestParam String userId, @RequestParam String code) {
        try {
            String clientId = "980766458886-llsr892dnsvifd706dlog2lc4flr2a1d.apps.googleusercontent.com";
            String client_secret = "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";
            String codeURL = code;
            String redirect_uri = "http://localhost:8080/professionals";
            String grant_type = "authorization_code";

            String url = String.format("https://oauth2.googleapis.com/token");

            String requestBody = String.format(
                    "client_id=%s&client_secret=%s&code=%s&grant_type=%s&redirect_uri=%s",
                    URLEncoder.encode(clientId, StandardCharsets.UTF_8),
                    URLEncoder.encode(client_secret, StandardCharsets.UTF_8),
                    URLEncoder.encode(codeURL, StandardCharsets.UTF_8),
                    URLEncoder.encode(grant_type, StandardCharsets.UTF_8),
                    URLEncoder.encode(redirect_uri, StandardCharsets.UTF_8)
            );

            URL obj = new URL(url);
            // Open a connection
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setDoOutput(true); // Needed to send a request body

            // Write request body
            try (BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8))) {
                writer.write(requestBody);
                writer.flush();
            }

            // Read the response
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // error
            if(connection.getResponseCode() != 200) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Could not connect");
            }
            else {
                JSONObject jsonObject = new JSONObject(response.toString());
                String accessToken = jsonObject.getString("access_token");
                String refreshToken = jsonObject.getString("refresh_token");
                scheduleAppointmentService.connectUserToGoogleToken(userId, accessToken, refreshToken);
                return ResponseEntity.ok(accessToken);
            }

        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Could not connect");
        }
    }

    @PostMapping("/create-google-event")
    public ResponseEntity<?> createGoogleAppointment(@RequestParam String userId, @RequestParam String appointmentId) throws IOException {

        return ResponseEntity.ok(scheduleAppointmentService.createGoogleCalendarEvent(userId, appointmentId));
    }

    @GetMapping("/google-events-between")
    public ResponseEntity<?> createGoogleAppointment(@RequestParam String userId,
                                                     @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
                                                     @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end){

        return ResponseEntity.ok(scheduleAppointmentService.getGoogleCalendarEventsBetween(userId, start, end));
    }

    @DeleteMapping("/delete-google-event")
    public ResponseEntity<?> deleteGoogleAppointment(@RequestParam String userId, @RequestParam String eventId) {
        boolean result = scheduleAppointmentService.deleteGoogleCalendarEvent(userId, eventId);
        return ResponseEntity.ok(result ? "Evento eliminado com sucesso!" : "Ocorreu um erro ao eliminar o evento!");
    }

    @GetMapping("/token-valid")
    public ResponseEntity<?> tokenValid(@RequestParam String token) throws IOException {

        return ResponseEntity.ok(scheduleAppointmentService.isTokenValid(token));
    }

    @GetMapping("/token-refresh")
    public ResponseEntity<?> refreshToken(@RequestParam String token) {

        return ResponseEntity.ok(scheduleAppointmentService.refreshToken(token));
    }
}
