package pt.rodrigo.hw1_tqs.controllers;

import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import pt.rodrigo.hw1_tqs.dto.BookingDTO;
import pt.rodrigo.hw1_tqs.entities.Booking;
import pt.rodrigo.hw1_tqs.entities.BookingStatus;
import pt.rodrigo.hw1_tqs.services.BookingService;

import java.time.LocalDate;
import java.util.Map;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

@RestController
@RequestMapping("/api/bookings")
@CrossOrigin(origins = "*")
public class BookingController {

    static final Logger log = getLogger(lookup().lookupClass());

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<Booking> createBooking(@RequestBody BookingDTO dto) {
        log.info("POST /api/bookings - Creating booking");
        log.info("Received new booking request DTO");

        
        try {
            Booking booking = new Booking();
            booking.setMunicipality(dto.getMunicipality());
            booking.setDate(LocalDate.parse(dto.getDate())); 
            booking.setTimeslot(dto.getTimeslot());
            booking.setDescription(dto.getDescription());
            
            Booking created = bookingService.createBooking(booking);
            return new ResponseEntity<>(created, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            log.error("Validation error: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }



    @GetMapping("/{token}")
    public ResponseEntity<Booking> getBookingByToken(@PathVariable String token) {
        log.info("GET /api/bookings/{token} - Fetching booking");
        try {
            Booking booking = bookingService.getBookingByToken(token);
            return new ResponseEntity<>(booking, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("Booking not found: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping
    public ResponseEntity<Page<Booking>> searchBookings(
            @RequestParam(required = false) String municipality,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(required = false) BookingStatus status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        log.info("GET /api/bookings - Searching bookings");
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Booking> bookings = bookingService.searchBookings(municipality, date, status, pageable);
            return new ResponseEntity<>(bookings, HttpStatus.OK);
        } catch (Exception e) {
            log.error("Error searching bookings: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @PutMapping("/{token}/status")
    public ResponseEntity<Booking> updateBookingStatus(
            @PathVariable String token,
            @RequestBody Map<String, String> statusUpdate
    ) {
        log.info("PUT /api/bookings/{token}/status - Updating status");
        try {
            BookingStatus newStatus = BookingStatus.valueOf(statusUpdate.get("newStatus"));
            String note = statusUpdate.getOrDefault("note", "");
            
            Booking updated = bookingService.updateBookingStatus(token, newStatus, note);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (IllegalArgumentException e) {
            log.error("Error updating status: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            log.error("Invalid request: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @DeleteMapping("/{token}")
    public ResponseEntity<Void> cancelBooking(@PathVariable String token) {
        log.info("DELETE /api/bookings/{token} - Cancelling booking");
        try {
            bookingService.cancelBooking(token);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (IllegalArgumentException e) {
            log.error("Booking not found: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (IllegalStateException e) {
            log.error("Cannot cancel booking: {}", e.getMessage());
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }
}
