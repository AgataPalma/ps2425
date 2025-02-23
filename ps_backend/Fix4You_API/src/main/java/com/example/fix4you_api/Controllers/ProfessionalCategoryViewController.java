package com.example.fix4you_api.Controllers;

import com.example.fix4you_api.Data.Models.Views.FlattenedProfessionalCategoryView;
import com.example.fix4you_api.Data.Models.Views.ProfessionalCategoryView;
import com.example.fix4you_api.Service.ProfessionalCategory.ProfessionalCategoryViewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/professional-category-views")
public class ProfessionalCategoryViewController {

    private final ProfessionalCategoryViewService professionalCategoryViewService;

    @GetMapping
    public ResponseEntity<List<ProfessionalCategoryView>> getViewData(@RequestParam(value = "filter", required = false) String filter) {
        List<ProfessionalCategoryView> data = professionalCategoryViewService.getProfessionalCategoryViews(filter);
        return ResponseEntity.ok(data);

    }

    @GetMapping("/flattened")
    public ResponseEntity<List<FlattenedProfessionalCategoryView>> getFlattenedViewData(@RequestParam(value = "filter", required = false) String filter) {
        List<FlattenedProfessionalCategoryView> data = professionalCategoryViewService.getFlattenedProfessionalCategoryViews(filter);
        return ResponseEntity.ok(data);

    }

    @PostMapping
    public List<ProfessionalCategoryView> createProfessionalCategoryView() {
        return professionalCategoryViewService.createViews();
    }
}
