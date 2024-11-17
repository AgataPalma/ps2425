package com.example.fix4you_api.Data.Models;

import java.time.LocalDateTime;

public class AppointmentCalendar {
    private LocalDateTime dateStart;
    private LocalDateTime dateFinish;
    private String title;

    public LocalDateTime getDateStart() {
        return dateStart;
    }

    public LocalDateTime getDateFinish() {
        return dateFinish;
    }

    public String getTitle() {
        return title;
    }

    public void setDateStart(LocalDateTime dateStart) {
        this.dateStart = dateStart;
    }

    public void setDateFinish(LocalDateTime dateFinish) {
        this.dateFinish = dateFinish;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
