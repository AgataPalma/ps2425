package com.example.fix4you_api.Controllers;

import com.example.fix4you_api.Data.Enums.ScheduleStateEnum;
import com.example.fix4you_api.Data.Models.ScheduleAppointment;
import com.example.fix4you_api.Data.MongoRepositories.ScheduleAppointmentRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

@RestController
@RequestMapping("/scheduleAppointments")
@RequiredArgsConstructor
public class ScheduleAppointmentController {

    private final ScheduleAppointmentRepository scheduleAppointmentRepository;

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
}