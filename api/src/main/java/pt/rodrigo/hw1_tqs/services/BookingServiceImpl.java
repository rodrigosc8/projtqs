package pt.rodrigo.hw1_tqs.services;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.rodrigo.hw1_tqs.entities.Booking;
import pt.rodrigo.hw1_tqs.entities.BookingStatus;
import pt.rodrigo.hw1_tqs.entities.StateChange;
import pt.rodrigo.hw1_tqs.repositories.BookingRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

@Service
public class BookingServiceImpl implements BookingService {

    static final Logger log = getLogger(lookup().lookupClass());
    
    private final BookingRepository bookingRepository;
    private final MunicipalityService municipalityService;
    
    @Value("${booking.max-per-day:5}") // ✅ Valor default: 5
    public int maxBookingsPerDay;

    public BookingServiceImpl(BookingRepository bookingRepository, 
                              MunicipalityService municipalityService) {
        this.bookingRepository = bookingRepository;
        this.municipalityService = municipalityService;
    }

    @Override
    @Transactional
    public Booking createBooking(Booking booking) {
        log.info("Creating new booking");


        validateRequiredFields(booking);

        validateMunicipality(booking.getMunicipality());

        validateDate(booking.getDate());

        validateCapacity(booking.getMunicipality(), booking.getDate());

        booking.setToken(UUID.randomUUID().toString());
        booking.setStatus(BookingStatus.RECEIVED);
        booking.setCreatedAt(LocalDateTime.now());

        StateChange initialState = new StateChange(booking, BookingStatus.RECEIVED, "Booking created");
        booking.addStateChange(initialState);

        Booking savedBooking = bookingRepository.save(booking);
        log.info("Booking created successfully with token: {}", savedBooking.getToken());
        
        return savedBooking;
    }


    private void validateRequiredFields(Booking booking) {
        if (booking.getMunicipality() == null || booking.getMunicipality().trim().isEmpty()) {
            throw new IllegalArgumentException("Municipality is required");
        }
        if (booking.getDate() == null) {
            throw new IllegalArgumentException("Date is required");
        }
        if (booking.getTimeslot() == null || booking.getTimeslot().trim().isEmpty()) {
            throw new IllegalArgumentException("Timeslot is required");
        }
        if (booking.getDescription() == null || booking.getDescription().trim().isEmpty()) {
            throw new IllegalArgumentException("Description is required");
        }
    }

    private void validateMunicipality(String municipality) {
        List<String> validMunicipalities = municipalityService.getAllMunicipalities();
        if (!validMunicipalities.contains(municipality)) {
            log.warn("Invalid municipality attempted: {}", municipality);
            throw new IllegalArgumentException("Invalid municipality: " + municipality);
        }
    }

    private void validateDate(LocalDate date) {
        if (date.isBefore(LocalDate.now())) {
            log.warn("Attempted to create booking with past date: {}", date);
            throw new IllegalArgumentException("Date cannot be in the past");
        }
    }

    private void validateCapacity(String municipality, LocalDate date) {
        long currentCount = bookingRepository.countByMunicipalityAndDate(municipality, date);
        
        log.info("Checked booking capacity against maxBookingsPerDay.");

        
        if (currentCount >= maxBookingsPerDay) {
            log.warn("Maximum capacity reached for {} on {}", municipality, date);
            throw new IllegalArgumentException(
                String.format("Maximum capacity reached for %s on %s (max: %d)", 
                    municipality, date, maxBookingsPerDay)
            );
        }
    }

    @Override
    public Booking getBookingByToken(String token) {
        log.info("Fetching booking by token: {}", token);
        return bookingRepository.findByToken(token)
            .orElseThrow(() -> new IllegalArgumentException("Booking not found with token: " + token));
    }

    @Override
    public Page<Booking> searchBookings(String municipality, LocalDate date, BookingStatus status, Pageable pageable) {
        log.info("Searching bookings - municipality: {}, date: {}, status: {}", municipality, date, status);
        
        if (municipality != null && status != null) {
            return bookingRepository.findByMunicipalityAndStatus(municipality, status, pageable);
        } else if (municipality != null) {
            return bookingRepository.findByMunicipality(municipality, pageable);
        } else if (status != null) {
            return bookingRepository.findByStatus(status, pageable);
        } else if (date != null) {
            return bookingRepository.findByDate(date, pageable);
        } else {
            return bookingRepository.findAll(pageable);
        }
    }

    @Override
    @Transactional
    public Booking updateBookingStatus(String token, BookingStatus newStatus, String note) {
        log.info("Updating booking status - token: {}, newStatus: {}", token, newStatus);
        
        Booking booking = getBookingByToken(token);
        
        validateStatusTransition(booking.getStatus(), newStatus);

        booking.setStatus(newStatus);
        StateChange stateChange = new StateChange(booking, newStatus, note);
        booking.addStateChange(stateChange);

        Booking updatedBooking = bookingRepository.save(booking);
        log.info("Booking status updated successfully");
        
        return updatedBooking;
    }

    private void validateStatusTransition(BookingStatus currentStatus, BookingStatus newStatus) {
        if (currentStatus == newStatus) {
            throw new IllegalArgumentException("Booking is already in status: " + newStatus);
        }

        if (currentStatus == BookingStatus.CANCELLED) {
            throw new IllegalArgumentException("Cannot change status of a cancelled booking");
        }

        if (currentStatus == BookingStatus.COMPLETED && newStatus != BookingStatus.CANCELLED) {
            throw new IllegalArgumentException("Completed booking can only be cancelled");
        }

        if (newStatus == BookingStatus.RECEIVED) {
            throw new IllegalArgumentException("Cannot change status back to RECEIVED");
        }
    }

    @Override
    @Transactional
    public void cancelBooking(String token) {
        log.info("Cancelling booking with token: {}", token);
        
        Booking booking = getBookingByToken(token);
        
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            log.warn("Booking already cancelled");
            throw new IllegalArgumentException("Booking is already cancelled");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        StateChange stateChange = new StateChange(booking, BookingStatus.CANCELLED, "Booking cancelled by user");
        booking.addStateChange(stateChange);

        bookingRepository.save(booking);
        log.info("Booking cancelled successfully");
    }
}
