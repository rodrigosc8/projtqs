package pt.rodrigo.hw1_tqs.services;

import pt.rodrigo.hw1_tqs.entities.Municipality;

import java.util.List;

public interface MunicipalityService {
    
    List<Municipality> getAllMunicipalities();
    
    void loadMunicipalitiesFromAPI();
    
    Municipality getMunicipalityByName(String name);
}
