package com.example.fix4you_api.Controllers;

import com.example.fix4you_api.Data.Enums.ServiceStateEnum;
import com.example.fix4you_api.Data.Models.Category;
import com.example.fix4you_api.Data.Models.Professional;
import com.example.fix4you_api.Data.Models.Service;
import com.example.fix4you_api.Service.Category.CategoryService;
import com.example.fix4you_api.Service.Professional.ProfessionalService;
import com.example.fix4you_api.Service.Service.ServiceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
public class ServiceController {

    private final ProfessionalService professionalService;
    private final CategoryService categoryService;
    private final ServiceService serviceService;

    @PostMapping
    public ResponseEntity<?> addService(@RequestBody Service service) {
        if (service.getClientId().equals(service.getProfessionalId())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Professional and client cant be the same");
        }
        this.serviceService.createService(service);
        return ResponseEntity.ok(service.getId());
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

    @PutMapping("/{id}")
    public ResponseEntity<?> updateService(@PathVariable String id, @RequestBody Service service) {
        return ResponseEntity.ok(serviceService.updateService(id, service));
    }

    @PutMapping("/accept-service")
    public ResponseEntity<?> acceptService(@RequestParam String professionalId, @RequestParam String serviceId) {
        Service service = this.serviceService.getById(serviceId);

        if (service.getProfessionalId() != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This service is already assigned to other professional");
        }

        // check if the professional is suspended
        Professional professional = professionalService.getProfessionalById(professionalId);
        if (professional == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Professional not found!");
        }

        if (professional.isSupended()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Professional is suspended!");
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
                case "category" -> service.setCategory((Service.Category) value);
                case "title" -> service.setTitle((String) value);
                case "state" -> {
                    try {
                        service.setState(ServiceStateEnum.valueOf(value.toString().toUpperCase()));
                    } catch (IllegalArgumentException e) {
                        throw new RuntimeException("Invalid value for state: " + value);
                    }
                }
                default -> throw new RuntimeException("Invalid field update request");
            }
        });

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
