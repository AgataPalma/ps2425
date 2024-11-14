package com.example.fix4you_api.Controllers;

import com.example.fix4you_api.Data.Models.PortfolioFile;
import com.example.fix4you_api.Data.Models.PortfolioItem;
import com.example.fix4you_api.Data.MongoRepositories.PortfolioItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
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
    public ResponseEntity<String> addPortfolioItem(@RequestParam String professionalId,
                                                   @RequestParam String description,
                                                   @RequestParam("files") MultipartFile[] files) {
        try {
            PortfolioItem portfolioItem = new PortfolioItem();
            portfolioItem.setProfessionalId(professionalId);
            portfolioItem.setDescription(description);

            if(files.length > 0) {
                List<PortfolioFile> portfolioFiles = new ArrayList<>();
                for (var i=0; i< files.length; i++) {
                    String fileName = files[i].getOriginalFilename();
                    String contentType = files[i].getContentType();
                    byte[] bytes = files[i].getBytes();

                    PortfolioFile portfolioFile = new PortfolioFile();
                    portfolioFile.setFilename(fileName);
                    portfolioFile.setContentType(contentType);
                    portfolioFile.setBytes(bytes);

                    portfolioFiles.add(portfolioFile);

                }
                portfolioItem.setFiles(portfolioFiles);
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
            for (var i=0; i < portfolioItems.size(); i++){
                if(portfolioItems.get(i).getFiles() != null) {
                    for (var y = 0; y < portfolioItems.get(i).getFiles().size(); y++) {
                        if(portfolioItems.get(i).getFiles().get(y) != null) {
                            byte[] bytes = portfolioItems.get(i).getFiles().get(y).getBytes();
                            if (bytes != null) {
                                String Base64Encoder = Base64.getEncoder().encodeToString(bytes);
                                portfolioItems.get(i).getFiles().get(y).setBase64Encoder(Base64Encoder);
                                portfolioItems.get(i).getFiles().get(y).setBytes(null);
                            }
                        }
                    }
                }
            }
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
            for (var i=0; i < portfolioItems.size(); i++){
                if(portfolioItems.get(i).getFiles() != null) {
                    for (var y = 0; y < portfolioItems.get(i).getFiles().size(); y++) {
                        if(portfolioItems.get(i).getFiles().get(y) != null) {
                            byte[] bytes = portfolioItems.get(i).getFiles().get(y).getBytes();
                            if (bytes != null) {
                                String Base64Encoder = Base64.getEncoder().encodeToString(bytes);
                                portfolioItems.get(i).getFiles().get(y).setBase64Encoder(Base64Encoder);
                                portfolioItems.get(i).getFiles().get(y).setBytes(null);
                            }
                        }
                    }
                }
            }
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
            if(portfolioItems.get().getFiles() != null) {
                for (var y = 0; y < portfolioItems.get().getFiles().size(); y++) {
                    if(portfolioItems.get().getFiles().get(y) != null) {
                        byte[] bytes = portfolioItems.get().getFiles().get(y).getBytes();
                        if (bytes != null) {
                            String Base64Encoder = Base64.getEncoder().encodeToString(bytes);
                            portfolioItems.get().getFiles().get(y).setBase64Encoder(Base64Encoder);
                            portfolioItems.get().getFiles().get(y).setBytes(null);
                        }
                    }
                }
            }
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
                                                 @RequestParam String professionalId,
                                                 @RequestParam String description,
                                                 @RequestParam("files") MultipartFile[] files ){
        try {
            Optional<PortfolioItem> portfolioItemOpt = this.portfolioItemRepository.findById(id);
            if (portfolioItemOpt.isPresent()) {
                if(files.length > 0) {
                    List<PortfolioFile> portfolioFiles = new ArrayList<>();
                    for (var i=0; i< files.length; i++) {
                        String fileName = files[i].getOriginalFilename();
                        String contentType = files[i].getContentType();
                        byte[] bytes = files[i].getBytes();

                        PortfolioFile portfolioFile = new PortfolioFile();
                        portfolioFile.setFilename(fileName);
                        portfolioFile.setContentType(contentType);
                        portfolioFile.setBytes(bytes);

                        portfolioFiles.add(portfolioFile);

                    }
                    portfolioItemOpt.get().setFiles(portfolioFiles);

                } else {
                    portfolioItemOpt.get().setFiles(null);
                }

                portfolioItemOpt.get().setProfessionalId(professionalId);
                portfolioItemOpt.get().setDescription(description);

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
                    List<PortfolioFile> portfolioFiles = new ArrayList<>();
                    for (var i=0; i< files.length; i++) {
                        String fileName = files[i].getOriginalFilename();
                        String contentType = files[i].getContentType();
                        byte[] bytes = files[i].getBytes();

                        PortfolioFile portfolioFile = new PortfolioFile();
                        portfolioFile.setFilename(fileName);
                        portfolioFile.setContentType(contentType);
                        portfolioFile.setBytes(bytes);

                        portfolioFiles.add(portfolioFile);

                    }
                    portfolioItemOpt.get().setFiles(portfolioFiles);

                } else {
                    portfolioItemOpt.get().setFiles(null);
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
}
