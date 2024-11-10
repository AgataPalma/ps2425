package com.example.fix4you_api.Controllers;

import com.example.fix4you_api.Data.Enums.ScheduleStateEnum;
import com.example.fix4you_api.Data.Models.ScheduleAppointment;
import com.example.fix4you_api.Data.MongoRepositories.ScheduleAppointmentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private ScheduleAppointmentRepository scheduleAppointmentRepository;

    @Autowired
    public ScheduleAppointmentController(ScheduleAppointmentRepository scheduleAppointmentRepository) {
        this.scheduleAppointmentRepository = scheduleAppointmentRepository;
    }

    @PostMapping
    public ResponseEntity<String> addScheduleAppointment(@RequestBody ScheduleAppointment scheduleAppointment) {
        try {
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
            this.scheduleAppointmentRepository.save(scheduleAppointment);
            return ResponseEntity.ok("Schedule Appointment Added!");
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getScheduleAppointment() {
        try {
            List<ScheduleAppointment> scheduleAppointments = this.scheduleAppointmentRepository.findAll();
            return ResponseEntity.ok(scheduleAppointments);
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
            Optional<ScheduleAppointment> scheduleAppointment = this.scheduleAppointmentRepository.findById(id);
            scheduleAppointment.get().setState(ScheduleStateEnum.CONFIRMED);
            return ResponseEntity.ok(scheduleAppointment);
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/disapprove/{id}")
    public ResponseEntity<?> disapproveScheduleAppointment(@PathVariable("id") String id) {
        try {
            Optional<ScheduleAppointment> scheduleAppointment = this.scheduleAppointmentRepository.findById(id);
            scheduleAppointment.get().setState(ScheduleStateEnum.CANCELED);
            return ResponseEntity.ok(scheduleAppointment);
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getScheduleAppointment(@PathVariable String id) {
        try {
            Optional<ScheduleAppointment> scheduleAppointment = this.scheduleAppointmentRepository.findById(id);
            return (scheduleAppointment.isPresent() ? ResponseEntity.ok(scheduleAppointment.get()) : ResponseEntity.ok("Couldn't find any schedule appointment with the id: '" + id + "'!"));
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateScheduleAppointment(@PathVariable String id, @RequestBody ScheduleAppointment scheduleAppointment) {
        try {
            Optional<ScheduleAppointment> scheduleAppointmentOpt = this.scheduleAppointmentRepository.findById(id);
            if (scheduleAppointmentOpt.isPresent()) {
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
                            String msg = "Schedule appointment conflicted. Previous one existed where date start: " + dateStart +
                                    " and date finish: " + dateFinish;
                            return ResponseEntity.ok(msg);
                        }
                    }
                    conflicted = false;
                }
                this.scheduleAppointmentRepository.save(scheduleAppointment);
                return ResponseEntity.ok(scheduleAppointment);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Couldn't find any schedule appointment with the id: '" + id + "'!");
            }
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
            ScheduleAppointment scheduleAppointment = scheduleAppointmentRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Schedule Appointment not found"));

            updates.forEach((key, value) -> {
                if(Objects.equals(key, "dateStart") || Objects.equals(key, "dateFinish")){
                    definedDates.set(true);
                }
                switch (key) {
                    case "clientId" -> scheduleAppointment.setClientId((String) value);
                    case "professionalId" -> scheduleAppointment.setProfessionalId((String) value);
                    case "dateStart" -> scheduleAppointment.setDateStart(LocalDateTime.parse((CharSequence) value));
                    case "dateFinish" -> scheduleAppointment.setDateFinish(LocalDateTime.parse((CharSequence) value));
                    case "state" -> scheduleAppointment.setState((ScheduleStateEnum) value);
                    default -> throw new RuntimeException("Invalid field update request");
                }
            });

            if(definedDates.get()){
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
                            String msg = "Schedule appointment conflicted. Previous one existed where date start: " + dateStart +
                                    " and date finish: " + dateFinish;
                            return ResponseEntity.ok(msg);
                        }
                    }
                    conflicted = false;
                }
            }

            scheduleAppointmentRepository.save(scheduleAppointment);
            return ResponseEntity.ok(scheduleAppointment);
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteScheduleAppointment(@PathVariable String id) {
        try {
            Optional<ScheduleAppointment> scheduleAppointment = this.scheduleAppointmentRepository.findById(id);
            this.scheduleAppointmentRepository.deleteById(id);
            String msg = (scheduleAppointment.isPresent() ? "Schedule appointment with id '" + id + "' was deleted!" : "Couldn't find any schedule appointment with the id: '" + id + "'!");
            return ResponseEntity.ok(msg);
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There was an error trying to delete the schedule appointment with id: '" + id + "'!");
        }
    }
}
