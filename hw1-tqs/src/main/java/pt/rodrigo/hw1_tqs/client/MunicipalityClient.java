package pt.rodrigo.hw1_tqs.client;

import pt.rodrigo.hw1_tqs.entities.Municipality;

import java.util.List;

public interface MunicipalityClient {
    
    List<Municipality> fetchMunicipalities();
}
