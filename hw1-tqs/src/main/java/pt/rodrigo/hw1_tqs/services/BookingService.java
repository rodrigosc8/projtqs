package pt.rodrigo.hw1_tqs.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import pt.rodrigo.hw1_tqs.entities.Booking;
import pt.rodrigo.hw1_tqs.entities.BookingStatus;

import java.time.LocalDate;

public interface BookingService {
    
    Booking createBooking(Booking booking);
    
    Booking getBookingByToken(String token);
    
    Page<Booking> searchBookings(String municipality, LocalDate date, BookingStatus status, Pageable pageable);
    
    Booking updateBookingStatus(String token, BookingStatus newStatus, String note);
    
    void cancelBooking(String token);
}
