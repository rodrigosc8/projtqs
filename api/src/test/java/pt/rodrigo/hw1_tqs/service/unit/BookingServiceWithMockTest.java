package pt.rodrigo.hw1_tqs.service.unit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import pt.rodrigo.hw1_tqs.entities.Booking;
import pt.rodrigo.hw1_tqs.entities.BookingStatus;

import pt.rodrigo.hw1_tqs.repositories.BookingRepository;
import pt.rodrigo.hw1_tqs.services.BookingServiceImpl;
import pt.rodrigo.hw1_tqs.services.MunicipalityService;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Alínea B) Service Tests (with mocks)
 */
class BookingServiceWithMocksTest {

    private BookingRepository bookingRepository;
    private MunicipalityService municipalityService;
    private BookingServiceImpl service;

    @BeforeEach
    void setUp() {
        bookingRepository = mock(BookingRepository.class);
        municipalityService = mock(MunicipalityService.class);
        service = new BookingServiceImpl(bookingRepository, municipalityService);
        service.maxBookingsPerDay = 3;
    }

    /**
     * Caso 1: Município válido
     */
    @Test
    void municipioValido_CriaBookingComSucesso() {
        Booking booking = new Booking();
        booking.setMunicipality("Braga");
        booking.setDate(LocalDate.now().plusDays(1));
        booking.setTimeslot("Manhã");
        booking.setDescription("Teste com município válido");

        // Simula que o MunicipalityService devolve uma lista que contém "Braga"
        when(municipalityService.getAllMunicipalities()).thenReturn(List.of("Braga", "Porto", "Lisboa"));
        when(bookingRepository.countByMunicipalityAndDate("Braga", booking.getDate())).thenReturn(0L);
        when(bookingRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Booking result = service.createBooking(booking);

        assertNotNull(result);
        assertNotNull(result.getToken());
        assertEquals("Braga", result.getMunicipality());
        
        // Verifica que o repositório foi chamado
        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    /**
     * Caso 2: Município inválido
     */
    @Test
    void municipioInvalido_LancaExcecao() {
        Booking booking = new Booking();
        booking.setMunicipality("Faro"); // município não existente na lista
        booking.setDate(LocalDate.now().plusDays(1));
        booking.setTimeslot("Tarde");
        booking.setDescription("Teste com município inválido");

        // Simula que o MunicipalityService devolve lista SEM "Faro"
        when(municipalityService.getAllMunicipalities()).thenReturn(List.of("Braga", "Porto", "Lisboa"));

        Exception e = assertThrows(IllegalArgumentException.class, () ->
            service.createBooking(booking)
        );
        
        assertTrue(e.getMessage().contains("Invalid municipality"));
        
        // Verifica que o repositório não foi chamado (falhou antes)
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    /**
     * Caso 3: Falha no serviço externo
     */
    @Test
    void falhaDoMunicipalityService_LancaExcecaoControlada() {
        Booking booking = new Booking();
        booking.setMunicipality("Braga");
        booking.setDate(LocalDate.now().plusDays(1));
        booking.setTimeslot("Noite");
        booking.setDescription("Teste com serviço externo em falha");

        // Simula que o MunicipalityService está offline/indisponível
        when(municipalityService.getAllMunicipalities())
            .thenThrow(new RuntimeException("Municipality service unavailable"));

        Exception e = assertThrows(RuntimeException.class, () ->
            service.createBooking(booking)
        );
        
        assertTrue(e.getMessage().contains("Municipality service unavailable"));
        
        // Verifica que o repositório não foi chamado
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    /**
     * Caso 4: Lista vazia devolvida pelo serviço
     */
    @Test
    void municipalityServiceDevolveListaVazia_LancaExcecao() {
        Booking booking = new Booking();
        booking.setMunicipality("Braga");
        booking.setDate(LocalDate.now().plusDays(1));
        booking.setTimeslot("Manhã");
        booking.setDescription("Teste com lista vazia");

        // Simula que o serviço devolve lista vazia (caso de erro)
        when(municipalityService.getAllMunicipalities()).thenReturn(List.of());

        Exception e = assertThrows(IllegalArgumentException.class, () ->
            service.createBooking(booking)
        );
        
        assertTrue(e.getMessage().contains("Invalid municipality"));
        
        // Verifica que o repositório não foi chamado
        verify(bookingRepository, never()).save(any(Booking.class));
    }
    /**
     * Caso 5: Timeslot vazio - deve lançar exceção
     */
    @Test
    void timeslotVazio_LancaExcecao() {
        Booking booking = new Booking();
        booking.setMunicipality("Braga");
        booking.setDate(LocalDate.now().plusDays(1));
        booking.setTimeslot(""); // vazio
        booking.setDescription("Teste timeslot vazio");

        when(municipalityService.getAllMunicipalities()).thenReturn(List.of("Braga"));

        Exception e = assertThrows(IllegalArgumentException.class, () ->
            service.createBooking(booking)
        );
        assertTrue(e.getMessage().contains("Timeslot is required"));
    }

    /**
     * Caso 6: Description vazia - deve lançar exceção
     */
    @Test
    void descriptionVazia_LancaExcecao() {
        Booking booking = new Booking();
        booking.setMunicipality("Braga");
        booking.setDate(LocalDate.now().plusDays(1));
        booking.setTimeslot("Tarde");
        booking.setDescription(""); // vazia

        when(municipalityService.getAllMunicipalities()).thenReturn(List.of("Braga"));

        Exception e = assertThrows(IllegalArgumentException.class, () ->
            service.createBooking(booking)
        );
        assertTrue(e.getMessage().contains("Description is required"));
    }

    /**
     * Caso 7: Data nula - deve lançar exceção
     */
    @Test
    void dataNull_LancaExcecao() {
        Booking booking = new Booking();
        booking.setMunicipality("Braga");
        booking.setDate(null); // nula
        booking.setTimeslot("Manhã");
        booking.setDescription("Teste data nula");

        when(municipalityService.getAllMunicipalities()).thenReturn(List.of("Braga"));

        Exception e = assertThrows(IllegalArgumentException.class, () ->
            service.createBooking(booking)
        );
        assertTrue(e.getMessage().contains("Date is required"));
    }

    /**
     * Caso 8: Data no passado - deve lançar exceção
     */
    @Test
    void dataNoPassado_LancaExcecao() {
        Booking booking = new Booking();
        booking.setMunicipality("Braga");
        booking.setDate(LocalDate.now().minusDays(1)); // passada
        booking.setTimeslot("Manhã");
        booking.setDescription("Teste data passada");

        when(municipalityService.getAllMunicipalities()).thenReturn(List.of("Braga"));

        Exception e = assertThrows(IllegalArgumentException.class, () ->
            service.createBooking(booking)
        );
        assertTrue(e.getMessage().contains("Date cannot be in the past"));
    }

    /**
     * Caso 9: Capacidade máxima atingida - deve lançar exceção
     */
    @Test
    void capacidadeMaximaAtingida_LancaExcecao() {
        Booking booking = new Booking();
        booking.setMunicipality("Braga");
        booking.setDate(LocalDate.now().plusDays(1));
        booking.setTimeslot("Manhã");
        booking.setDescription("Teste capacidade cheia");

        when(municipalityService.getAllMunicipalities()).thenReturn(List.of("Braga"));
        when(bookingRepository.countByMunicipalityAndDate("Braga", booking.getDate()))
            .thenReturn(3L); // máximo atingido

        Exception e = assertThrows(IllegalArgumentException.class, () ->
            service.createBooking(booking)
        );
        assertTrue(e.getMessage().contains("Maximum capacity reached"));
    }

    /**
     * Caso 10: Cancelar booking com sucesso
     */
    @Test
    void cancelarBooking_ComSucesso() {
        Booking booking = new Booking();
        booking.setToken("test-token-123");
        booking.setStatus(BookingStatus.RECEIVED);

        when(bookingRepository.findByToken("test-token-123"))
            .thenReturn(java.util.Optional.of(booking));
        when(bookingRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        service.cancelBooking("test-token-123");

        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
        verify(bookingRepository, times(1)).save(booking);
    }

    /**
     * Caso 11: Cancelar booking inexistente
     */
    @Test
    void cancelarBookingInexistente_LancaExcecao() {
        when(bookingRepository.findByToken("token-inexistente"))
            .thenReturn(java.util.Optional.empty());

        Exception e = assertThrows(IllegalArgumentException.class, () ->
            service.cancelBooking("token-inexistente")
        );
        assertTrue(e.getMessage().contains("Booking not found"));
    }

    /**
     * Caso 12: Cancelar booking já cancelado
     */
    @Test
    void cancelarBookingJaCancelado_LancaExcecao() {
        Booking booking = new Booking();
        booking.setToken("test-token-456");
        booking.setStatus(BookingStatus.CANCELLED);

        when(bookingRepository.findByToken("test-token-456"))
            .thenReturn(java.util.Optional.of(booking));

        Exception e = assertThrows(IllegalArgumentException.class, () ->
            service.cancelBooking("test-token-456")
        );
        assertTrue(e.getMessage().contains("already cancelled"));
    }



}
