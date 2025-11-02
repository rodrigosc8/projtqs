package pt.rodrigo.hw1_tqs.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pt.rodrigo.hw1_tqs.services.MunicipalityService;

import java.util.List;

@RestController
@RequestMapping("/api/municipalities")
@CrossOrigin(origins = "*")
public class MunicipalityController {

    private final MunicipalityService municipalityService;

    public MunicipalityController(MunicipalityService municipalityService) {
        this.municipalityService = municipalityService;
    }

    @GetMapping
    public ResponseEntity<List<String>> getAllMunicipalities() {
        List<String> municipalities = municipalityService.getAllMunicipalities();
        return ResponseEntity.ok(municipalities);
    }
}
