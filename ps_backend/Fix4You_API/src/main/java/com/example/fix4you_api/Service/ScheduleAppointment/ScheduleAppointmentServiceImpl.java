package com.example.fix4you_api.Service.ScheduleAppointment;

import com.example.fix4you_api.Data.Enums.ScheduleStateEnum;
import com.example.fix4you_api.Data.Models.GoogleTokenUser;
import com.example.fix4you_api.Data.Models.Professional;
import com.example.fix4you_api.Data.Models.ScheduleAppointment;
import com.example.fix4you_api.Data.Models.Service;
import com.example.fix4you_api.Data.MongoRepositories.*;
import com.example.fix4you_api.Service.ScheduleAppointment.DTOs.GoogleCalendarEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.json.JSONObject;

import org.springframework.transaction.annotation.Transactional;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ScheduleAppointmentServiceImpl implements ScheduleAppointmentService {

    private final ScheduleAppointmentRepository scheduleAppointmentRepository;
    private final ProfessionalRepository professionalRepository;
    private final GoogleTokenUserRepository googleTokenUserRepository;
    private final ServiceRepository serviceRepository;

    @Override
    public List<ScheduleAppointment> getScheduleAppointmentsByServiceIdAndStateAndDateFinishBetween(String serviceId, ScheduleStateEnum state, LocalDateTime startDate, LocalDateTime endDate){
        return this.scheduleAppointmentRepository.findByServiceIdAndStateAndDateFinishBetween(serviceId, state, startDate, endDate);
    }

    @Override
    @Transactional
    public void deleteScheduleAppointment(String serviceId) {
        scheduleAppointmentRepository.deleteByServiceId(serviceId);
    }


    @Override
    public void connectUserToGoogleToken(String userId, String token, String refreshToken) {
        GoogleTokenUser existingUser = googleTokenUserRepository.findByUserId(userId);
        if(existingUser != null) {
            existingUser.setToken(token);
            googleTokenUserRepository.save(existingUser);
        }
        else {
            // create new userToken
            Optional<Professional> professional = professionalRepository.findById(userId);
            if(professional.isPresent()) {
                GoogleTokenUser googleTokenUser = new GoogleTokenUser();
                googleTokenUser.setEmail(professional.get().getEmail());
                googleTokenUser.setToken(token);
                googleTokenUser.setRefreshToken(refreshToken);
                googleTokenUser.setUserId(professional.get().getId());
                googleTokenUserRepository.save(googleTokenUser);
            }
        }
    }

    public List<GoogleCalendarEvent> getGoogleCalendarEventsBetween(String userId, LocalDateTime start, LocalDateTime end) {
        try {
            GoogleTokenUser tokenUser = googleTokenUserRepository.findByUserId(userId);

            if(!isTokenValid(tokenUser.getToken())) {
                String newToken = refreshToken(tokenUser.getRefreshToken());
                tokenUser.setToken(newToken);
                googleTokenUserRepository.save(tokenUser);
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
            String formattedStart = start.format(formatter);
            String formattedEnd = end.format(formatter);

            String url = String.format("https://www.googleapis.com/calendar/v3/calendars/primary/events?" +
                    "timeMin=%s&timeMax=%s",formattedStart, formattedEnd);

            HttpURLConnection connection = null;
            URL obj = new URL(url);
            // Open a connection
            connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("GET");
            //connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Authorization", "Bearer " + tokenUser.getToken());

            // Read the response
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            System.out.println(connection.getResponseMessage());

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            List<GoogleCalendarEvent> googleEvents = new ArrayList<>();

            // Parse the JSON
            ObjectMapper mapper = new ObjectMapper();
            JsonNode rootNode = mapper.readTree(response.toString());
            JsonNode itemsNode = rootNode.path("items");

            // Iterate through each event
            for (JsonNode item : itemsNode) {
                GoogleCalendarEvent event = new GoogleCalendarEvent();

                // Map fields
                event.setTitle(item.path("summary").asText());
                event.setDescription(item.path("description").asText(null)); // Null-safe
                event.setLocation(item.path("location").asText(null));
                event.setEventId(item.path("id").asText(null));

                // Parse start time
                JsonNode startNode = item.path("start").path("dateTime");
                if (!startNode.isMissingNode()) {
                    event.setStartTime(ZonedDateTime.parse(startNode.asText()).toLocalDateTime());
                }

                // Parse end time
                JsonNode endNode = item.path("end").path("dateTime");
                if (!endNode.isMissingNode()) {
                    event.setEndTime(ZonedDateTime.parse(endNode.asText()).toLocalDateTime());
                }

                googleEvents.add(event);
            }
            return googleEvents;
        }
        catch (IOException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public String createGoogleCalendarEvent(String userId, String appointmentId) throws IOException {
        HttpURLConnection connection = null;
        try {
            GoogleTokenUser tokenUser = googleTokenUserRepository.findByUserId(userId);
            Optional<ScheduleAppointment> appointment = scheduleAppointmentRepository.findById(appointmentId);
            if(!appointment.isPresent()) {
                return "Appointment não encontrado";
            }

            Optional<Service> service = serviceRepository.findById(appointment.get().getServiceId());
            if(!service.isPresent()) {
                return "Serviço não encontrado";
            }

            if(!isTokenValid(tokenUser.getToken())) {
                String newToken = refreshToken(tokenUser.getRefreshToken());
                tokenUser.setToken(newToken);
                googleTokenUserRepository.save(tokenUser);
            }

            String token = tokenUser.getToken();

            String url = String.format("https://www.googleapis.com/calendar/v3/calendars/primary/events");
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");

            String requestBody = String.format(
                    "{\n" +
                            "  \"summary\": \"%s\",\n" +
                            "  \"location\": \"%s\",\n" +
                            "  \"description\": \"%s\",\n" +
                            "  \"start\": {\n" +
                            "    \"dateTime\": \"%s\",\n" +
                            "    \"timeZone\": \"UTC\"\n" + // Timezone might be required, adjust if needed
                            "  },\n" +
                            "  \"end\": {\n" +
                            "    \"dateTime\": \"%s\",\n" +
                            "    \"timeZone\": \"UTC\"\n" + // Timezone might be required, adjust if needed
                            "  }\n" +
                            "}",
                    "Fix4You - " + service.get().getTitle(),
                    service.get().getLocation(),
                    service.get().getId(),
                    formatter.format(appointment.get().getDateStart()) + "+00:00",
                    formatter.format(appointment.get().getDateFinish()) + "+00:00"
            );

            URL obj = new URL(url);
            // Open a connection
            connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Authorization", "Bearer " + token);
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

            System.out.println(connection.getResponseMessage());

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            // error
            if (connection.getResponseCode() != 200) {
                return "Could not connect";
            } else {
                return "created";
            }
        } catch (Exception e) {
            // Log the exception message
            System.out.println("Exception occurred: " + e.getMessage());
            // Optionally log the stack trace for more details
            e.printStackTrace();

            // Read the error stream for response content (it may contain more helpful details)
            BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            StringBuilder errorResponse = new StringBuilder();
            String errorLine;
            while ((errorLine = errorReader.readLine()) != null) {
                errorResponse.append(errorLine);
            }
            errorReader.close();
            System.out.println("Error response: " + errorResponse.toString()); // Log the error response
            return "Error occurred: " + e.getMessage();
        }
    }

    public boolean deleteGoogleCalendarEvent(String userId, String eventId) {
        try {
            // Retrieve the Google token user from the repository
            GoogleTokenUser tokenUser = googleTokenUserRepository.findByUserId(userId);

            // Check if the token is valid, and refresh if necessary
            if (!isTokenValid(tokenUser.getToken())) {
                String newToken = refreshToken(tokenUser.getRefreshToken());
                tokenUser.setToken(newToken);
                googleTokenUserRepository.save(tokenUser);
            }

            // Construct the URL to delete the specific event
            String url = String.format("https://www.googleapis.com/calendar/v3/calendars/primary/events/%s", eventId);

            // Open an HTTP connection
            HttpURLConnection connection = null;
            URL obj = new URL(url);
            connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Authorization", "Bearer " + tokenUser.getToken());

            // Get the response code
            int responseCode = connection.getResponseCode();

            // Check if the deletion was successful (HTTP 204 No Content indicates success)
            return responseCode == HttpURLConnection.HTTP_NO_CONTENT;
        } catch (IOException e) {
            System.out.println("Erro ao eliminar o evento: " + e.getMessage());
            return false;
        }
    }

    public boolean editGoogleCalendarEvent(String userId, String eventId, GoogleCalendarEvent updatedEvent) {
        try {
            // Retrieve the Google token user from the repository
            GoogleTokenUser tokenUser = googleTokenUserRepository.findByUserId(userId);

            // Check if the token is valid, and refresh if necessary
            if (!isTokenValid(tokenUser.getToken())) {
                String newToken = refreshToken(tokenUser.getRefreshToken());
                tokenUser.setToken(newToken);
                googleTokenUserRepository.save(tokenUser);
            }

            // Construct the URL for updating the event
            String url = String.format("https://www.googleapis.com/calendar/v3/calendars/primary/events/%s", eventId);

            // Open an HTTP connection
            HttpURLConnection connection = null;
            URL obj = new URL(url);
            connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Authorization", "Bearer " + tokenUser.getToken());
            connection.setRequestProperty("Content-Type", "application/json");

            // Build the JSON payload with the updated event details
            ObjectMapper mapper = new ObjectMapper();
            ObjectNode eventNode = mapper.createObjectNode();

            // Add updated fields
            eventNode.put("summary", updatedEvent.getTitle());
            eventNode.put("description", updatedEvent.getDescription());
            eventNode.put("location", updatedEvent.getLocation());

            // Add start and end times
            ObjectNode startNode = eventNode.putObject("start");
            startNode.put("dateTime", updatedEvent.getStartTime().format(DateTimeFormatter.ISO_DATE_TIME));
            startNode.put("timeZone", "UTC");

            ObjectNode endNode = eventNode.putObject("end");
            endNode.put("dateTime", updatedEvent.getEndTime().format(DateTimeFormatter.ISO_DATE_TIME));
            endNode.put("timeZone", "UTC");

            // Convert the payload to a JSON string
            String jsonPayload = mapper.writeValueAsString(eventNode);

            // Send the JSON payload
            connection.setDoOutput(true);
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonPayload.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // Get the response code
            int responseCode = connection.getResponseCode();

            // Check if the update was successful (HTTP 200 OK indicates success)
            return responseCode == HttpURLConnection.HTTP_OK;
        } catch (IOException e) {
            System.out.println("Erro ao editar google event: " + e.getMessage());
            return false;
        }
    }

    public boolean isTokenValid(String accessToken) {
        try {
            String url = String.format("https://oauth2.googleapis.com/tokeninfo?access_token=%s", accessToken);

            URL obj = new URL(url);

            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

            // Read the response
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            if (connection.getResponseCode() == 200) {
                return true;
            }

            return false;
        } catch (IOException e) {
            return false;
        }
    }

    public String refreshToken(String refreshToken) {
        try {
            String clientId = "980766458886-llsr892dnsvifd706dlog2lc4flr2a1d.apps.googleusercontent.com";
            String client_secret = "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";
            String grant_type = "refresh_token";

            String url = String.format("https://oauth2.googleapis.com/token");

            String requestBody = String.format(
                    "client_id=%s&client_secret=%s&refresh_token=%s&grant_type=%s",
                    URLEncoder.encode(clientId, StandardCharsets.UTF_8),
                    URLEncoder.encode(client_secret, StandardCharsets.UTF_8),
                    URLEncoder.encode(refreshToken, StandardCharsets.UTF_8),
                    URLEncoder.encode(grant_type, StandardCharsets.UTF_8)
            );

            URL obj = new URL(url);
            // Open a connection
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("POST");
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
                return null;
            }
            else if (connection.getResponseCode() == 200) {
                JSONObject jsonObject = new JSONObject(response.toString());
                String newAccessToken = jsonObject.getString("access_token");
                return newAccessToken;
            }
            return null;
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return null;
        }
    }

    public boolean IsUserAuthenticatedGoogle(String userId) {
        GoogleTokenUser tokenUser = googleTokenUserRepository.findByUserId(userId);
        if(tokenUser != null) {
            return true;
        }
        return false;
    }
}