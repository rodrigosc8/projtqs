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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BookingServiceUnitTest {

    private BookingRepository bookingRepository;
    private MunicipalityService municipalityService;
    private BookingServiceImpl service;

    @BeforeEach
    void setUp() {
        bookingRepository = mock(BookingRepository.class);
        municipalityService = mock(MunicipalityService.class);
        service = new BookingServiceImpl(bookingRepository, municipalityService);
        service.maxBookingsPerDay = 3; // define o limite diário para testes
    }

    @Test
    void naoPermiteBookingComDataPassada() {
        Booking booking = new Booking();
        booking.setMunicipality("Braga");
        booking.setDate(LocalDate.now().minusDays(1)); // data passada
        booking.setTimeslot("Manhã");
        booking.setDescription("Teste");

        when(municipalityService.getAllMunicipalities()).thenReturn(List.of("Braga"));

        Exception e = assertThrows(IllegalArgumentException.class, () ->
            service.createBooking(booking)
        );
        assertTrue(e.getMessage().contains("Date cannot be in the past"));
    }

    @Test
    void naoPermiteBookingLimiteAtingido() {
        Booking booking = new Booking();
        booking.setMunicipality("Braga");
        booking.setDate(LocalDate.now().plusDays(1)); // data futura
        booking.setTimeslot("Tarde");
        booking.setDescription("Teste limite");

        when(municipalityService.getAllMunicipalities()).thenReturn(List.of("Braga"));
        when(bookingRepository.countByMunicipalityAndDate("Braga", booking.getDate())).thenReturn(3L); // já atingiu

        Exception e = assertThrows(IllegalArgumentException.class, () ->
            service.createBooking(booking)
        );
        assertTrue(e.getMessage().contains("Maximum capacity"));
    }

    @Test
    void aceitaBookingValido() {
        Booking booking = new Booking();
        booking.setMunicipality("Braga");
        booking.setDate(LocalDate.now().plusDays(1));
        booking.setTimeslot("Noite");
        booking.setDescription("Tudo ok");

        when(municipalityService.getAllMunicipalities()).thenReturn(List.of("Braga"));
        when(bookingRepository.countByMunicipalityAndDate("Braga", booking.getDate())).thenReturn(0L);
        when(bookingRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Booking result = service.createBooking(booking);
        assertEquals("Braga", result.getMunicipality());
        assertEquals("Noite", result.getTimeslot());
        assertNotNull(result.getToken());
        assertEquals(booking.getDate(), result.getDate());
    }
    
    @Test
    void naoPermiteBookingComTimeslotVazio() {
        Booking booking = new Booking();
        booking.setMunicipality("Braga");
        booking.setDate(LocalDate.now().plusDays(1));
        booking.setTimeslot(""); // vazio
        booking.setDescription("Teste");

        when(municipalityService.getAllMunicipalities()).thenReturn(List.of("Braga"));

        Exception e = assertThrows(IllegalArgumentException.class, () ->
            service.createBooking(booking)
        );
        assertTrue(e.getMessage().contains("Timeslot is required"));
    }

    
    @Test
    void naoPermiteBookingComDescriptionVazia() {
        Booking booking = new Booking();
        booking.setMunicipality("Braga");
        booking.setDate(LocalDate.now().plusDays(1));
        booking.setTimeslot("Manhã");
        booking.setDescription(""); // vazia

        when(municipalityService.getAllMunicipalities()).thenReturn(List.of("Braga"));

        Exception e = assertThrows(IllegalArgumentException.class, () ->
            service.createBooking(booking)
        );
        assertTrue(e.getMessage().contains("Description is required"));
    }

    
    @Test
    void cancelaBookingComSucesso() {
        Booking booking = new Booking();
        booking.setToken("test-token-123");
        booking.setStatus(BookingStatus.RECEIVED);
        booking.setMunicipality("Braga");
        booking.setDate(LocalDate.now().plusDays(1));

        when(bookingRepository.findByToken("test-token-123"))
            .thenReturn(java.util.Optional.of(booking));
        when(bookingRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        service.cancelBooking("test-token-123");

        assertEquals(BookingStatus.CANCELLED, booking.getStatus());
        verify(bookingRepository, times(1)).save(booking);
    }

    
    @Test
    void naoPermiteCancelarBookingInexistente() {
        when(bookingRepository.findByToken("token-inexistente"))
            .thenReturn(java.util.Optional.empty());

        Exception e = assertThrows(IllegalArgumentException.class, () ->
            service.cancelBooking("token-inexistente")
        );
        assertTrue(e.getMessage().contains("Booking not found"));
    }

    
    @Test
    void naoPermiteCancelarBookingJaCancelado() {
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
