package com.kushal.hireflow.vacancy.controller;


import com.kushal.hireflow.common.response.PageResponse;
import com.kushal.hireflow.vacancy.dto.CreateVacancyRequest;
import com.kushal.hireflow.vacancy.dto.UpdateVacancyRequest;
import com.kushal.hireflow.vacancy.dto.VacancyFilterRequest;
import com.kushal.hireflow.vacancy.dto.VacancyResponse;
import com.kushal.hireflow.vacancy.service.VacancyService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/vacancies")
@RequiredArgsConstructor
public class VacancyController {

    private final VacancyService vacancyService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VacancyResponse createVacancy(@Valid @RequestBody CreateVacancyRequest request) {
        return vacancyService.createVacancy(request);
    }

    @GetMapping
    public PageResponse<VacancyResponse> getAllVacancies(@ModelAttribute VacancyFilterRequest request) {
        return vacancyService.getAllVacancies(request);
    }

    @GetMapping("/{id}")
    public VacancyResponse getVacancyById(@PathVariable Long id) {
        return vacancyService.getVacancyById(id);
    }

    @PutMapping("/{id}")
    public VacancyResponse updateVacancy(@PathVariable Long id,
                                         @Valid @RequestBody UpdateVacancyRequest request) {
        return vacancyService.updateVacancy(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteVacancy(@PathVariable Long id) {
        vacancyService.deleteVacancy(id);
    }
}
