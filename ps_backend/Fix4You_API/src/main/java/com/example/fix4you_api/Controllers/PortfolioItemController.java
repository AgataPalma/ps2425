package com.example.fix4you_api.Controllers;

import com.example.fix4you_api.Data.Models.*;
import com.example.fix4you_api.Data.MongoRepositories.PortfolioItemRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
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
    public ResponseEntity<String> addPortfolioItem(@Valid @RequestBody PortfolioItem portfolioItem) {
        try {
            this.portfolioItemRepository.save(portfolioItem);
            return ResponseEntity.ok("O item do portefólio foi adicionado com sucesso!");
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
            return (portfolioItems.isPresent() ? ResponseEntity.ok(portfolioItems.get()) : ResponseEntity.ok("Não foi possível encontrar nenhum item do portefólio com o id: '" + id + "'!"));
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
            String msg = (portfolioItem.isPresent() ? "Item do portefólio com o id: '" + id + "' foi eliminado!" : "Não foi possível encontrar nenhum item do portefólio com o id: '" + id + "'!");
            return ResponseEntity.ok(msg);
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Ocorreu um erro ao tentar eliminar o item do portefólio com o id: '" + id + "'!");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePortfolioItem(@PathVariable String id,
                                                 @RequestBody PortfolioItem portfolioItem){
        try {
            Optional<PortfolioItem> portfolioItemOpt = this.portfolioItemRepository.findById(id);
            if (portfolioItemOpt.isPresent()) {

                portfolioItemOpt.get().setProfessionalId(portfolioItem.getProfessionalId());
                portfolioItemOpt.get().setDescription(portfolioItem.getDescription());
                portfolioItemOpt.get().setByteContent(portfolioItem.getByteContent());

                this.portfolioItemRepository.save(portfolioItemOpt.get());
                return ResponseEntity.ok(portfolioItemOpt);
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Não foi possível encontrar nenhum item do portefólio com o id: '" + id + "'!");
            }
        } catch (Exception e) {
            System.out.println("[ERROR] - " + e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @PutMapping("/images/{id}")
    public PortfolioItem updatePortfolioItemImages(@PathVariable String id,
                                                   @Validated @RequestParam("byteContent") byte[][] byteContent) throws IOException {

        Optional<PortfolioItem> portfolioItem = findOrThrow(id);
        portfolioItem.get().setByteContent(byteContent);

        return portfolioItemRepository.save(portfolioItem.get());
    }

    @PatchMapping("/{id}")
    public ResponseEntity<PortfolioItem> partialUpdatePortfolioItem(@PathVariable String id, @RequestBody Map<String, Object> updates) {
        PortfolioItem portfolioItem = portfolioItemRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Item do portefólio não encontrado"));

        updates.forEach((key, value) -> {
            switch (key) {
                case "professionalId" -> portfolioItem.setProfessionalId((String) value);
                case "description" -> portfolioItem.setDescription((String) value);
                default -> throw new RuntimeException("Campo inválido no pedido da atualização!");
            }
        });

        this.portfolioItemRepository.save(portfolioItem);

        return ResponseEntity.ok(portfolioItem);
    }

    private Optional<PortfolioItem> findOrThrow(String id) {
        return portfolioItemRepository.findById(id);
    }
}
