package com.example.fix4you_api.Controllers;

import com.example.fix4you_api.Data.Enums.ServiceStateEnum;
import com.example.fix4you_api.Data.Models.Category;
import com.example.fix4you_api.Data.Models.Professional;
import com.example.fix4you_api.Data.Models.Service;
import com.example.fix4you_api.Data.MongoRepositories.ServiceRepository;
import com.example.fix4you_api.Service.Category.CategoryService;
import com.example.fix4you_api.Service.Professional.ProfessionalService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/services")
@RequiredArgsConstructor
public class ServiceController {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private ProfessionalService professionalService;

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<?> addService(@RequestBody Service service) {
        try {
            if(service.getClientId().equals(service.getProfessionalId())) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Professional and client cant be the same");
            }
            this.serviceRepository.save(service);
            return ResponseEntity.ok(service.getId());
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getService() {
        try {
            List<Service> services = this.serviceRepository.findAll();
            return ResponseEntity.ok(services);
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/professional/{id}")
    public ResponseEntity<?> getProfessionalServices(@PathVariable("id") String idProfessional) {
        try {
            List<Service> services = this.serviceRepository.findByProfessionalId(idProfessional);
            return ResponseEntity.ok(services);
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/client/{id}")
    public ResponseEntity<?> getClientServices(@PathVariable("id") String idClient) {
        try {
            List<Service> services = this.serviceRepository.findByClientId(idClient);
            return ResponseEntity.ok(services);
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getService(@PathVariable String id) {
        try {
            Optional<Service> service = this.serviceRepository.findById(id);
            return (service.isPresent() ? ResponseEntity.ok(service.get()) : ResponseEntity.ok("Couldn't find any Service with the id: '" + id + "'!"));
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateService(@PathVariable String id, @RequestBody Service service) {
        try {
            Optional<Service> serviceOpt = this.serviceRepository.findById(id);
            if (serviceOpt.isPresent()) {
                this.serviceRepository.save(service);
                return ResponseEntity.ok(service);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Couldn't find any service with the id: '" + id + "'!");
            }
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/accept-service")
    public ResponseEntity<?> acceptService(@RequestParam String professionalId, @RequestParam String serviceId) {
        try {
            Optional<Service> serviceOpt = this.serviceRepository.findById(serviceId);
            if (serviceOpt.isPresent()) {
                Service service = serviceOpt.get();

                if(service.getProfessionalId() != null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("This service is already assigned to other professional");
                }

                // check if the professional is suspended
                Professional professional = professionalService.getProfessionalById(professionalId);
                if(professional == null) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Professional not found!");
                }

                if(professional.isSupended()) {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Professional is suspended!");
                }

                service.setProfessionalId(professionalId);
                service.setState(ServiceStateEnum.ACCEPTED);
                serviceRepository.save(service);

                return ResponseEntity.ok(service);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Couldn't find any service with the id: '" + serviceId + "'!");
            }
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<?> partialUpdateService(
            @PathVariable String id,
            @RequestBody Map<String, Object> updates) {
        try {
            Service service = serviceRepository.findById(id)
                    .orElseThrow(() -> new NoSuchElementException("Service not found"));

            updates.forEach((key, value) -> {
                switch (key) {
                    case "clientId" -> service.setClientId((String) value);
                    case "professionalId" -> service.setProfessionalId((String) value);
                    case "price" -> service.setPrice(((Double) value).floatValue());
                    case "address" -> service.setAddress((String) value);
                    case "postalCode" -> service.setPostalCode((String) value);
                    case "category" -> service.setCategory((Service.Category) value);
                    case "title" -> service.setTitle((String) value);
                    case "state" -> service.setState((ServiceStateEnum) value);
                    default -> throw new RuntimeException("Invalid field update request");
                }
            });

            serviceRepository.save(service);
            return ResponseEntity.ok(service);
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PatchMapping("/search")
    public ResponseEntity<?> searchServices(
            @RequestBody Map<String, Object> updates) {
        try {
            List<Service> filteredServices = new ArrayList<>(this.serviceRepository.findAll());

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
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteService(@PathVariable String id) {
        try {
            Optional<Service> service = this.serviceRepository.findById(id);
            this.serviceRepository.deleteById(id);
            String msg = (service.isPresent() ? "Service with id '" + id + "' was deleted!" : "Couldn't find any service with the id: '" + id + "'!");
            return ResponseEntity.ok(msg);
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There was an error trying to delete the service with id: '" + id + "'!");
        }
    }
}
