package com.example.fix4you_api.Utils;

import com.example.fix4you_api.Data.Models.AppointmentCalendar;
import net.fortuna.ical4j.data.CalendarBuilder;

import net.fortuna.ical4j.model.*;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.parameter.Value;
import net.fortuna.ical4j.model.property.RRule;
import net.fortuna.ical4j.model.property.Summary;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.InputStream;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ICSParser {
    public static List<AppointmentCalendar> parseICSFile(MultipartFile multipartFile, LocalDateTime dateStartLimit, LocalDateTime dateEndLimit) {
        List<AppointmentCalendar> eventDetailsList = new ArrayList<>();

        try (InputStream in = multipartFile.getInputStream()) {
            CalendarBuilder builder = new CalendarBuilder();
            net.fortuna.ical4j.model.Calendar calendar = builder.build(in);

            DateTime periodStart = new DateTime(java.util.Date.from(dateStartLimit.atZone(ZoneId.systemDefault()).toInstant()));
            DateTime periodEnd = new DateTime(java.util.Date.from(dateEndLimit.atZone(ZoneId.systemDefault()).toInstant()));
            Period period = new Period(periodStart, periodEnd);

            for (Component component : calendar.getComponents(Component.VEVENT)) {
                VEvent event = (VEvent) component;

                // Extract DTSTART and DTEND properties
                Property dtStartProp = event.getProperty(Property.DTSTART);
                Property dtEndProp = event.getProperty(Property.DTEND);

                if (dtStartProp == null) {
                    continue; // Skip events with no start date
                }

                Date startDate;
                Date endDate;

                // Determine if DTSTART and DTEND are date-only or date-time, and use the appropriate class
                try {
                    startDate = dtStartProp.getValue().contains("T") ? new DateTime(dtStartProp.getValue()) : new Date(dtStartProp.getValue());
                    endDate = (dtEndProp != null && dtEndProp.getValue().contains("T")) ? new DateTime(dtEndProp.getValue()) : new Date(dtStartProp.getValue());
                } catch (ParseException e) {
                    continue; // Skip events that have parsing issues
                }

                LocalDateTime eventStart = startDate instanceof DateTime
                        ? ((DateTime) startDate).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
                        : startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate().atStartOfDay();

                LocalDateTime eventEnd = endDate instanceof DateTime
                        ? ((DateTime) endDate).toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()
                        : eventStart;

                List<LocalDateTime> eventDates = new ArrayList<>();

                // Handle recurring events with RRULE
                RRule rrule = event.getProperty(Property.RRULE);
                if (rrule != null) {
                    Recur recur = rrule.getRecur();
                    DateList occurrences = recur.getDates(startDate, period, startDate instanceof DateTime ? Value.DATE_TIME : Value.DATE);

                    for (Object occurrence : occurrences) {
                        DateTime occurrenceStart = (DateTime) occurrence;
                        eventDates.add(occurrenceStart.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
                    }
                } else if (period.includes(startDate)) {
                    eventDates.add(eventStart);
                }

                // For each occurrence, add to event details if within the date range
                for (LocalDateTime occurrenceStart : eventDates) {
                    if (!occurrenceStart.isBefore(dateStartLimit) && !occurrenceStart.isAfter(dateEndLimit)) {
                        AppointmentCalendar eventDetails = new AppointmentCalendar();
                        eventDetails.setDateStart(occurrenceStart);

                        // Calculate the duration between the original event start and end
                        Duration eventDuration = Duration.between(eventStart, eventEnd);
                        LocalDateTime occurrenceEnd = occurrenceStart.plus(eventDuration);
                        eventDetails.setDateFinish(occurrenceEnd);

                        // Parse title (SUMMARY)
                        Summary summary = event.getProperty(Property.SUMMARY);
                        if (summary != null) {
                            eventDetails.setTitle(summary.getValue());
                        }

                        eventDetailsList.add(eventDetails);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return eventDetailsList;
    }

}
