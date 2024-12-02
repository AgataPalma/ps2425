package com.example.fix4you_api.Controllers;

import com.example.fix4you_api.Data.Enums.ServiceStateEnum;
import com.example.fix4you_api.Data.Models.*;
import com.example.fix4you_api.Data.Models.Dtos.SimpleCategoryDTO;
import com.example.fix4you_api.Data.MongoRepositories.CategoryRepository;
import com.example.fix4you_api.Data.MongoRepositories.ServiceRepository;
import com.example.fix4you_api.Service.Category.CategoryService;
import com.example.fix4you_api.Service.Professional.ProfessionalService;
import com.example.fix4you_api.Service.Service.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ProfessionalService professionalService;
    private final CategoryService categoryService;
    private final ServiceService serviceService;
    private final ServiceRepository serviceRepository;
    private final CategoryRepository categoryRepository;

    @PostMapping
    public ResponseEntity<?> addService(@RequestBody Service service) {
        try {
            if(service.getClientId().equals(service.getProfessionalId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("O profissional e o cliente não podem ser o mesmo utilizador!");
            }
            if(service.getClientId() != null && service.getProfessionalId() != null){
                service.setAgreementDate(LocalDateTime.now());
            }

            service.setDateCreation(LocalDateTime.now());
            service.setState(ServiceStateEnum.PENDING);

            this.serviceService.createService(service);
            return ResponseEntity.ok(service.getId());
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getServices(
            @RequestParam(value = "filter", required = false) String filter,
            @RequestParam(value = "sort", required = false) String sort
    ) {
        List<Service> services = this.serviceService.getServices(filter, sort);
        return ResponseEntity.ok(services);
    }

    @GetMapping("/professional/{id}")
    public ResponseEntity<?> getProfessionalServices(@PathVariable("id") String idProfessional) {
        List<Service> services = this.serviceService.getServicesByProfessionalId(idProfessional);
        return ResponseEntity.ok(services);
    }

    @GetMapping("/client/{id}")
    public ResponseEntity<?> getClientServices(@PathVariable("id") String idClient) {
        List<Service> services = this.serviceService.getServicesByClientId(idClient);
        return ResponseEntity.ok(services);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getService(@PathVariable String id) {
        Service service = serviceService.getById(id);
        return new ResponseEntity<>(service, HttpStatus.OK);
    }

    @GetMapping("/topActivitiesClients")
    public ResponseEntity<?> getTopActivitiesClients() {
        List<ClientServiceCount> clientIds = this.serviceService.getTopActivitiesClients();
        return ResponseEntity.ok(clientIds);
    }

    @GetMapping("/topActivitiesProfessionals")
    public ResponseEntity<?> getTopActivitiesProfessionals() {
        List<ProfessionalServiceCount> professionalIds = this.serviceService.getTopActivitiesProfessionals();
        return ResponseEntity.ok(professionalIds);
    }

    @GetMapping("/topExpensesClients")
    public ResponseEntity<?> getTopExpensesClients() {
        List<ClientTotalSpent> clientIds = this.serviceService.getTopPriceClients();
        return ResponseEntity.ok(clientIds);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateService(@PathVariable String id, @RequestBody Service service) {
        try {
            if(service.getClientId() != null && service.getProfessionalId() != null){
                service.setAgreementDate(LocalDateTime.now());
            }
            if(service.getState() == ServiceStateEnum.COMPLETED){
                Category category = categoryService.getCategoryByName(service.getCategory().getName());
                category.setCompletedServices(category.getCompletedServices()+1);

                List<Service> services = serviceRepository.findByCategoryAndState(service.getCategory().getName(), ServiceStateEnum.COMPLETED);
                float[] medianPrices = new float[services.size()];
                for (var i=0; i<services.size(); i++){
                    medianPrices[i] = services.get(i).getPrice();
                }

                Arrays.sort(medianPrices);
                float median = 0;
                if(medianPrices.length > 0) {
                    if (medianPrices.length % 2 == 0)
                        median = (medianPrices[medianPrices.length / 2] + medianPrices[medianPrices.length / 2 - 1]) / 2;
                    else
                        median = medianPrices[medianPrices.length / 2];
                }

                category.setMedianValue(median);
                categoryRepository.save(category);
            }
            return ResponseEntity.ok(serviceService.updateService(id, service));
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/accept-service")
    public ResponseEntity<?> acceptService(@RequestParam String professionalId, @RequestParam String serviceId) {
        Service service = this.serviceService.getById(serviceId);

        if (service.getProfessionalId() != null) {
            if(!service.getProfessionalId().equals(professionalId) ) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Este serviço já está atribuído a outro profissional!");
            }

            if(service.getState().equals(ServiceStateEnum.ACCEPTED)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Este serviço já foi aceito por si!");
            }
        }

        // check if the professional is suspended
        Professional professional = professionalService.getProfessionalById(professionalId);
        if (professional == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Profissional não encontrado!");
        }

        if (professional.isSupended()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("O Profissional está suspenso!");
        }
        if(service.getClientId() != null && service.getProfessionalId() != null){
            service.setAgreementDate(LocalDateTime.now());
        }
        service.setProfessionalId(professionalId);
        service.setState(ServiceStateEnum.ACCEPTED);
        serviceService.createService(service);

        return ResponseEntity.ok(service);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> partialUpdateService(
            @PathVariable String id,
            @RequestBody Map<String, Object> updates) {
        Service service = serviceService.getById(id);

        updates.forEach((key, value) -> {
            switch (key) {
                case "clientId" -> service.setClientId((String) value);
                case "professionalId" -> service.setProfessionalId((String) value);
                case "price" -> service.setPrice(((Double) value).floatValue());
                case "address" -> service.setAddress((String) value);
                case "postalCode" -> service.setPostalCode((String) value);
                case "category" -> service.setCategory((SimpleCategoryDTO) value);
                case "title" -> service.setTitle((String) value);
                case "state" -> {
                    try {
                        service.setState(ServiceStateEnum.valueOf(value.toString().toUpperCase()));

                        if(service.getState() == ServiceStateEnum.COMPLETED){
                            Category category = categoryService.getCategoryByName(service.getCategory().getName());
                            category.setCompletedServices(category.getCompletedServices()+1);

                            List<Service> services = serviceRepository.findByCategoryAndState(service.getCategory().getName(), ServiceStateEnum.COMPLETED);
                            float[] medianPrices = new float[services.size()];
                            for (var i=0; i<services.size(); i++){
                                medianPrices[i] = services.get(i).getPrice();
                            }

                            Arrays.sort(medianPrices);
                            float median = 0;
                            if(medianPrices.length > 0) {
                                if (medianPrices.length % 2 == 0)
                                    median = (medianPrices[medianPrices.length / 2] + medianPrices[medianPrices.length / 2 - 1]) / 2;
                                else
                                    median = medianPrices[medianPrices.length / 2];
                            }

                            category.setMedianValue(median);
                            categoryRepository.save(category);
                        }
                    } catch (IllegalArgumentException e) {
                        throw new RuntimeException("Valor inválido para o estado: " + value);
                    }
                }
                default -> throw new RuntimeException("Campo inválido no pedido da atualização!");
            }
        });
        if(service.getClientId() != null && service.getProfessionalId() != null){
            service.setAgreementDate(LocalDateTime.now());
        }
        serviceService.createService(service);
        return ResponseEntity.ok(service);
    }

    @PatchMapping("/search")
    public ResponseEntity<?> searchServices(
            @RequestBody Map<String, Object> updates) {
        List<Service> filteredServices = new ArrayList<>(this.serviceService.getServices(null, null));

        // Apply filters based on the updates map
        if (updates.containsKey("title")) {
            String title = (String) updates.get("title");
            filteredServices.removeIf(service -> !service.getTitle().contains(title));
        }

        if (updates.containsKey("category")) {
            String categoryName = (String) updates.get("category");
            Category category = categoryService.getCategoryByName(categoryName);
            filteredServices.removeIf(service -> !service.getCategory().getId().contains(category.getId()));
        }

        if (updates.containsKey("price")) {
            Object priceValue = updates.get("price");

            if (priceValue instanceof Double) {
                Double price = (Double) priceValue;
                filteredServices.removeIf(service -> service.getPrice() != price.floatValue());
            } else if (priceValue instanceof Float) {
                Float price = (Float) priceValue;
                filteredServices.removeIf(service -> service.getPrice() != price);
            }
        }

        if (updates.containsKey("address")) {
            String address = (String) updates.get("address");
            filteredServices.removeIf(service -> !service.getAddress().contains(address));
        }

        // Return the final filtered list of services
        return ResponseEntity.ok(filteredServices);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteService(@PathVariable String id) {
        serviceService.deleteService(id);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}
