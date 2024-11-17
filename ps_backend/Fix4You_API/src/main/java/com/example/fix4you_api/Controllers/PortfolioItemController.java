package com.example.fix4you_api.Controllers;

import com.example.fix4you_api.Data.Enums.LanguageEnum;
import com.example.fix4you_api.Data.Enums.PaymentTypesEnum;
import com.example.fix4you_api.Data.Models.CategoryDescription;
import com.example.fix4you_api.Data.Models.Image;
import com.example.fix4you_api.Data.Models.PortfolioItem;
import com.example.fix4you_api.Data.Models.Professional;
import com.example.fix4you_api.Data.MongoRepositories.PortfolioItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

@RestController
@RequestMapping("portfolioItems")
public class PortfolioItemController {
    @Autowired
    private PortfolioItemRepository portfolioItemRepository;

    @Autowired
    public PortfolioItemController(PortfolioItemRepository portfolioItemRepository) {
        this.portfolioItemRepository = portfolioItemRepository;
    }

    @PostMapping
    public ResponseEntity<String> addPortfolioItem(PortfolioItem portfolioItem,
                                                   @RequestParam("files") MultipartFile[] files) {
        try {

            if(files.length > 0) {
                List<Image> images = new ArrayList<>();
                for (var i=0; i< files.length; i++) {
                    String fileName = files[i].getOriginalFilename();
                    String contentType = files[i].getContentType();
                    byte[] bytes = files[i].getBytes();

                    Image image = new Image();
                    image.setFilename(fileName);
                    image.setContentType(contentType);
                    image.setBytes(bytes);

                    images.add(image);

                }
                portfolioItem.setImages(images);
            }

            this.portfolioItemRepository.save(portfolioItem);
            return ResponseEntity.ok("Portfolio item Added!");
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getPortfolioItem() {
        try {
            List<PortfolioItem> portfolioItems = this.portfolioItemRepository.findAll();
            return ResponseEntity.ok(portfolioItems);
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<?> getUserPortfolioItem(@PathVariable("id") String idProfessional) {
        try {
            List<PortfolioItem> portfolioItems = this.portfolioItemRepository.findByProfessionalId(idProfessional);
            return ResponseEntity.ok(portfolioItems);
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getPortfolioItem(@PathVariable String id) {
        try {
            Optional<PortfolioItem> portfolioItems = this.portfolioItemRepository.findById(id);
            return (portfolioItems.isPresent() ? ResponseEntity.ok(portfolioItems.get()) : ResponseEntity.ok("Couldn't find any portfolio item with the id: '" + id + "'!"));
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletePortfolioItem(@PathVariable String id) {
        try {
            Optional<PortfolioItem> portfolioItem = this.portfolioItemRepository.findById(id);
            this.portfolioItemRepository.deleteById(id);
            String msg = (portfolioItem.isPresent() ? "Portfolio item with id '" + id + "' was deleted!" : "Couldn't find any portfolio item with the id: '" + id + "'!");
            return ResponseEntity.ok(msg);
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("There was an error trying to delete the portfolio item with id: '" + id + "'!");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePortfolioItem(@PathVariable String id,
                                                 PortfolioItem portfolioItem,
                                                 @RequestParam("files") MultipartFile[] files ){
        try {
            Optional<PortfolioItem> portfolioItemOpt = this.portfolioItemRepository.findById(id);
            if (portfolioItemOpt.isPresent()) {
                if(files.length > 0) {
                    List<Image> images = new ArrayList<>();
                    for (var i=0; i< files.length; i++) {
                        String fileName = files[i].getOriginalFilename();
                        String contentType = files[i].getContentType();
                        byte[] bytes = files[i].getBytes();

                        Image image = new Image();
                        image.setFilename(fileName);
                        image.setContentType(contentType);
                        image.setBytes(bytes);

                        images.add(image);

                    }
                    portfolioItemOpt.get().setImages(images);

                } else {
                    portfolioItemOpt.get().setImages(null);
                }

                portfolioItemOpt.get().setProfessionalId(portfolioItem.getProfessionalId());
                portfolioItemOpt.get().setDescription(portfolioItem.getDescription());

                this.portfolioItemRepository.save(portfolioItemOpt.get());
                return ResponseEntity.ok(portfolioItemOpt);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Couldn't find any portfolio item with the id: '" + id + "'!");
            }
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/images/{id}")
    public ResponseEntity<?> updatePortfolioItemImages(@PathVariable String id,
                                                 @RequestParam("files") MultipartFile[] files ){
        try {
            Optional<PortfolioItem> portfolioItemOpt = this.portfolioItemRepository.findById(id);
            if (portfolioItemOpt.isPresent()) {
                if(files.length > 0) {
                    List<Image> images = new ArrayList<>();
                    for (var i=0; i< files.length; i++) {
                        String fileName = files[i].getOriginalFilename();
                        String contentType = files[i].getContentType();
                        byte[] bytes = files[i].getBytes();

                        Image image = new Image();
                        image.setFilename(fileName);
                        image.setContentType(contentType);
                        image.setBytes(bytes);

                        images.add(image);

                    }
                    portfolioItemOpt.get().setImages(images);

                } else {
                    portfolioItemOpt.get().setImages(null);
                }

                this.portfolioItemRepository.save(portfolioItemOpt.get());
                return ResponseEntity.ok(portfolioItemOpt);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Couldn't find any portfolio item with the id: '" + id + "'!");
            }
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PortfolioItem> partialUpdatePortfolioItem(@PathVariable String id, @RequestBody Map<String, Object> updates) {
        PortfolioItem portfolioItem = portfolioItemRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Portfolio Item not found"));

        updates.forEach((key, value) -> {
            switch (key) {
                case "professionalId" -> portfolioItem.setProfessionalId((String) value);
                case "description" -> portfolioItem.setDescription((String) value);
                default -> throw new RuntimeException("Invalid field update request");
            }
        });

        this.portfolioItemRepository.save(portfolioItem);

        return ResponseEntity.ok(portfolioItem);
    }
}
