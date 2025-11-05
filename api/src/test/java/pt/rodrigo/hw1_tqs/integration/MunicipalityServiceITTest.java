package pt.rodrigo.hw1_tqs.integration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pt.rodrigo.hw1_tqs.services.MunicipalityService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class MunicipalityServiceIT {

    @Autowired
    private MunicipalityService municipalityService;

    @Test
    void getAllMunicipalities_IntegrationTest() {
        List<String> municipalities = municipalityService.getAllMunicipalities();

        assertNotNull(municipalities);
        assertFalse(municipalities.isEmpty());
        assertTrue(municipalities.size() > 0);
    }
}
