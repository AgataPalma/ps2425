package com.example.fix4you_api.Controllers;

import com.example.fix4you_api.Data.Models.PortfolioItem;
import com.example.fix4you_api.Data.MongoRepositories.PortfolioItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<String> addPortfolioItem(@RequestBody PortfolioItem portfolioItem) {
        try {
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
    public ResponseEntity<?> updatePortfolioItem(@PathVariable String id, @RequestBody PortfolioItem portfolioItem) {
        try {
            Optional<PortfolioItem> portfolioItemOpt = this.portfolioItemRepository.findById(id);
            if (portfolioItemOpt.isPresent()) {
                this.portfolioItemRepository.save(portfolioItem);
                return ResponseEntity.ok(portfolioItem);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Couldn't find any portfolio item with the id: '" + id + "'!");
            }
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}
