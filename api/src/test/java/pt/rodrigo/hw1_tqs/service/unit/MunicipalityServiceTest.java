package pt.rodrigo.hw1_tqs.service.unit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import pt.rodrigo.hw1_tqs.services.MunicipalityServiceImpl;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MunicipalityServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private MunicipalityServiceImpl service;

    @SuppressWarnings("unchecked")
    @Test
    void getAllMunicipalities_ReturnsListOfMunicipalities() {
        List<String> mockMunicipalities = Arrays.asList("Aveiro", "Braga", "Lisboa");
        ResponseEntity<List<String>> mockResponse = ResponseEntity.ok(mockMunicipalities);

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            isNull(),
            any(ParameterizedTypeReference.class)
        )).thenReturn(mockResponse);

        List<String> municipalities = service.getAllMunicipalities();

        assertNotNull(municipalities);
        assertEquals(3, municipalities.size());
        assertTrue(municipalities.contains("Aveiro"));
        assertTrue(municipalities.contains("Braga"));
        assertTrue(municipalities.contains("Lisboa"));
    }

    @SuppressWarnings("unchecked")
    @Test
    void getAllMunicipalities_WhenApiReturnsEmpty_ReturnsEmptyList() {
        ResponseEntity<List<String>> mockResponse = ResponseEntity.ok(Collections.emptyList());

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            isNull(),
            any(ParameterizedTypeReference.class)
        )).thenReturn(mockResponse);

        List<String> municipalities = service.getAllMunicipalities();

        assertNotNull(municipalities);
        assertTrue(municipalities.isEmpty());
    }

    @SuppressWarnings("unchecked")
    @Test
    void getAllMunicipalities_WhenApiThrowsException_ReturnsEmptyList() {
        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            isNull(),
            any(ParameterizedTypeReference.class)
        )).thenThrow(new RuntimeException("API unavailable"));

        List<String> municipalities = service.getAllMunicipalities();

        assertNotNull(municipalities);
        assertTrue(municipalities.isEmpty());
    }

    @SuppressWarnings("unchecked")
    @Test
    void getAllMunicipalities_WhenResponseBodyIsNull_ReturnsEmptyList() {
        ResponseEntity<List<String>> mockResponse = ResponseEntity.ok(null);

        when(restTemplate.exchange(
            anyString(),
            eq(HttpMethod.GET),
            isNull(),
            any(ParameterizedTypeReference.class)
        )).thenReturn(mockResponse);

        List<String> municipalities = service.getAllMunicipalities();

        assertNotNull(municipalities);
        assertTrue(municipalities.isEmpty());
    }
}
