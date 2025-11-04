package pt.rodrigo.hw1_tqs.service.unit;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import pt.rodrigo.hw1_tqs.controllers.BookingController;
import pt.rodrigo.hw1_tqs.entities.Booking;
import pt.rodrigo.hw1_tqs.services.BookingService;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    
    @Test
    void createBooking_ReturnsCreatedOnSuccess() throws Exception {
        Booking booking = new Booking();
        booking.setToken("test-token");
        
        when(bookingService.createBooking(any(Booking.class))).thenReturn(booking);
        
        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"municipality\":\"Aveiro\",\"date\":\"2026-12-12\",\"timeslot\":\"Manhã\",\"description\":\"Test\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").value("test-token"));
    }

    @Test
    void createBooking_ReturnsBadRequestOnInvalidData() throws Exception {
        when(bookingService.createBooking(any(Booking.class)))
                .thenThrow(new IllegalArgumentException("Municipality is required"));
        
        mockMvc.perform(post("/api/bookings")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"municipality\":\"\",\"date\":\"2026-12-12\"}"))
                .andExpect(status().isBadRequest());
    }

    
    @Test
    void getBookingByToken_ReturnsOkWhenFound() throws Exception {
        Booking booking = new Booking();
        booking.setToken("token-123");
        
        when(bookingService.getBookingByToken("token-123")).thenReturn(booking);
        
        mockMvc.perform(get("/api/bookings/token-123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("token-123"));
    }

    @Test
    void getBookingByToken_ReturnsNotFoundWhenNotExists() throws Exception {
        when(bookingService.getBookingByToken("invalid-token"))
                .thenThrow(new IllegalArgumentException("Booking not found"));
        
        mockMvc.perform(get("/api/bookings/invalid-token"))
                .andExpect(status().isNotFound());
    }

    
    @Test
    void searchBookings_ReturnsOk() throws Exception {
        when(bookingService.searchBookings(any(), any(), any(), any()))
                .thenReturn(org.springframework.data.domain.Page.empty());
        
        mockMvc.perform(get("/api/bookings"))
                .andExpect(status().isOk());
    }

    
    @Test
    void updateBookingStatus_ReturnsOkOnSuccess() throws Exception {
        Booking updated = new Booking();
        updated.setToken("token-ok");
        
        when(bookingService.updateBookingStatus(eq("token-ok"), any(), anyString()))
                .thenReturn(updated);
        
        mockMvc.perform(put("/api/bookings/token-ok/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"newStatus\":\"COMPLETED\", \"note\":\"Done\"}"))
                .andExpect(status().isOk());
    }

    @Test
    void updateBookingStatus_ReturnsNotFoundWhenTokenInvalid() throws Exception {
        when(bookingService.updateBookingStatus(eq("token-nf"), any(), anyString()))
                .thenThrow(new IllegalArgumentException("Not found"));
        
        mockMvc.perform(put("/api/bookings/token-nf/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"newStatus\":\"COMPLETED\"}"))
                .andExpect(status().isNotFound());
    }

    @Test
    void updateBookingStatus_ReturnsBadRequestOnIllegalState() throws Exception {
        when(bookingService.updateBookingStatus(eq("token-bad"), any(), anyString()))
                .thenThrow(new IllegalStateException("Invalid transition"));
        
        mockMvc.perform(put("/api/bookings/token-bad/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"newStatus\":\"RECEIVED\"}"))
                .andExpect(status().isBadRequest());
    }

    
    @Test
    void cancelBooking_ReturnsNoContentOnSuccess() throws Exception {
        doNothing().when(bookingService).cancelBooking("token-ok");
        
        mockMvc.perform(delete("/api/bookings/token-ok"))
                .andExpect(status().isNoContent());
    }

    @Test
    void cancelBooking_ReturnsNotFoundWhenNotExists() throws Exception {
        doThrow(new IllegalArgumentException("Not found"))
                .when(bookingService).cancelBooking("token-nf");
        
        mockMvc.perform(delete("/api/bookings/token-nf"))
                .andExpect(status().isNotFound());
    }

    @Test
    void cancelBooking_ReturnsBadRequestOnIllegalState() throws Exception {
        doThrow(new IllegalStateException("Already cancelled"))
                .when(bookingService).cancelBooking("token-bad");
        
        mockMvc.perform(delete("/api/bookings/token-bad"))
                .andExpect(status().isBadRequest());
    }
}
