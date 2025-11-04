package pt.rodrigo.hw1_tqs.integration;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pt.rodrigo.hw1_tqs.repositories.BookingRepository;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Alínea C) Integration Tests (MockMvc)
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class BookingControllerITTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private BookingRepository bookingRepository;

    @AfterEach
    public void resetDb() {
        bookingRepository.deleteAll();
    }

    /**
     *Criar booking (POST /api/bookings) - Caso positivo
     */
    @Test
    @DisplayName("POST /api/bookings - OK")
    void whenCreateValidBooking_thenReturnsOk() throws Exception {
        String bookingJson = """
            {
                "municipality": "Braga",
                "date": "2025-12-01",
                "timeslot": "Manhã",
                "description": "Teste integração"
            }
            """;

        mvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookingJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").exists())
                .andExpect(jsonPath("$.municipality", is("Braga")))
                .andExpect(jsonPath("$.timeslot", is("Manhã")));
    }

    /**
     * Criar booking - Caso negativo (data passada)
     */
    @Test
    @DisplayName("POST /api/bookings - BAD REQUEST (data passada)")
    void whenCreateBookingWithPastDate_thenReturnsBadRequest() throws Exception {
        String bookingJson = """
            {
                "municipality": "Braga",
                "date": "2020-01-01",
                "timeslot": "Manhã",
                "description": "Data passada"
            }
            """;

        mvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookingJson))
                .andExpect(status().isBadRequest());
    }

    /**
     * Obter booking por token (GET /api/bookings/{token})
     */
    @Test
    @DisplayName("GET /api/bookings/{token} - OK")
    void whenGetBookingByValidToken_thenReturnsOk() throws Exception {
        // Primeiro, cria um booking
        String bookingJson = """
            {
                "municipality": "Porto",
                "date": "2025-12-15",
                "timeslot": "Tarde",
                "description": "Teste GET por token"
            }
            """;

        String responseBody = mvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookingJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extrai o token da resposta (usa regex simples ou parser JSON)
        String token = responseBody.split("\"token\":\"")[1].split("\"")[0];

        // Agora busca pelo token
        mvc.perform(get("/api/bookings/" + token)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())                
                .andExpect(jsonPath("$.token", is(token)))
                .andExpect(jsonPath("$.municipality", is("Porto")))
                .andExpect(jsonPath("$.timeslot", is("Tarde")));
    }

    /**
     * Obter booking por token - Caso negativo (token inexistente)
     */
    @Test
    @DisplayName("GET /api/bookings/{token} - NOT FOUND")
    void whenGetBookingByInvalidToken_thenReturnsNotFound() throws Exception {
        mvc.perform(get("/api/bookings/token-inexistente")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    /**
     * Listar bookings por município (GET /api/bookings?municipality=Braga)
     */
    @Test
    @DisplayName("GET /api/bookings?municipality=Braga - OK")
    void whenListBookingsByMunicipality_thenReturnsFiltered() throws Exception {
        // Cria 2 bookings para Braga
        String booking1 = """
            {
                "municipality": "Braga",
                "date": "2025-12-20",
                "timeslot": "Manhã",
                "description": "Booking 1"
            }
            """;
        
        String booking2 = """
            {
                "municipality": "Braga",
                "date": "2025-12-21",
                "timeslot": "Tarde",
                "description": "Booking 2"
            }
            """;

        // Cria 1 booking para Porto (não deve aparecer na lista)
        String booking3 = """
            {
                "municipality": "Porto",
                "date": "2025-12-22",
                "timeslot": "Noite",
                "description": "Booking Porto"
            }
            """;

        mvc.perform(post("/api/bookings").contentType(MediaType.APPLICATION_JSON).content(booking1))
                .andExpect(status().isCreated());
        mvc.perform(post("/api/bookings").contentType(MediaType.APPLICATION_JSON).content(booking2))
                .andExpect(status().isCreated());
        mvc.perform(post("/api/bookings").contentType(MediaType.APPLICATION_JSON).content(booking3))
                .andExpect(status().isCreated());

        // Lista apenas os de Braga
        mvc.perform(get("/api/bookings").param("municipality", "Braga")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)))
                .andExpect(jsonPath("$.content[0].municipality", is("Braga")))
                .andExpect(jsonPath("$.content[1].municipality", is("Braga")));
    }

    /**
     * Atualizar estado (PUT /api/bookings/{token}/state?newState=IN_PROGRESS)
     */
    @Test
    @DisplayName("PUT /api/bookings/{token}/state - OK")
    void whenUpdateBookingState_thenReturnsOk() throws Exception {
        // Cria booking
        String bookingJson = """
            {
                "municipality": "Lisboa",
                "date": "2025-12-25",
                "timeslot": "Manhã",
                "description": "Teste update estado"
            }
            """;

        String responseBody = mvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookingJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = responseBody.split("\"token\":\"")[1].split("\"")[0];

        // Atualiza o estado para IN_PROGRESS
        String body = """
            { "newStatus": "IN_PROGRESS" }
            """;

        mvc.perform(put("/api/bookings/" + token + "/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is("IN_PROGRESS")));

    }

    /**
     * 4 - Atualizar estado - Caso negativo (estado inválido)
     */
    @Test
    @DisplayName("PUT /api/bookings/{token}/status - BAD REQUEST (estado inválido)")
    void whenUpdateBookingWithInvalidState_thenReturnsBadRequest() throws Exception {
        // Cria booking
        String bookingJson = """
            {
                "municipality": "Coimbra",
                "date": "2025-12-30",
                "timeslot": "Tarde",
                "description": "Teste estado inválido"
            }
            """;

        String responseBody = mvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(bookingJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        String token = responseBody.split("\"token\":\"")[1].split("\"")[0];

        // Tenta atualizar com estado inválido
        mvc.perform(put("/api/bookings/" + token + "/status")
                .param("newState", "ESTADO_INVALIDO")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}
