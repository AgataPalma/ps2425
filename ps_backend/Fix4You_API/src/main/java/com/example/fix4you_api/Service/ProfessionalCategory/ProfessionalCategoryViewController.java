package com.example.fix4you_api.Service.ProfessionalCategory;

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

    @PostMapping
    public List<ProfessionalCategoryView> createSimplifiedProfessionalView() {
        return professionalCategoryViewService.createProfessionalCategoryView();
    }
}
