package pt.rodrigo.hw1_tqs.services;

import org.slf4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pt.rodrigo.hw1_tqs.entities.Booking;
import pt.rodrigo.hw1_tqs.entities.BookingStatus;
import pt.rodrigo.hw1_tqs.entities.StateChange;
import pt.rodrigo.hw1_tqs.repositories.BookingRepository;

import java.time.LocalDate;
import java.util.Optional;

import static java.lang.invoke.MethodHandles.lookup;
import static org.slf4j.LoggerFactory.getLogger;

@Service
public class BookingServiceImpl implements BookingService {

    static final Logger log = getLogger(lookup().lookupClass());

    private final BookingRepository bookingRepository;

    public BookingServiceImpl(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    @Override
    @Transactional
    public Booking createBooking(Booking booking) {
        log.info("Creating new booking for municipality: {}, date: {}", 
                 booking.getMunicipality(), booking.getDate());

        // Validar data (não pode ser no passado)
        if (booking.getDate().isBefore(LocalDate.now())) {
            log.warn("Attempted to create booking with past date: {}", booking.getDate());
            throw new IllegalArgumentException("Date cannot be in the past");
        }

        // Garantir que o status inicial é RECEIVED
        booking.setStatus(BookingStatus.RECEIVED);

        Booking saved = bookingRepository.save(booking);
        log.info("Booking created successfully with token: {}", saved.getToken());

        return saved;
    }

    @Override
    public Booking getBookingByToken(String token) {
        log.info("Fetching booking with token: {}", token);

        Optional<Booking> booking = bookingRepository.findByToken(token);
        if (booking.isEmpty()) {
            log.warn("Booking not found with token: {}", token);
            throw new IllegalArgumentException("Booking not found");
        }

        return booking.get();
    }

    @Override
    public Page<Booking> searchBookings(String municipality, LocalDate date, 
                                        BookingStatus status, Pageable pageable) {
        log.info("Searching bookings - municipality: {}, date: {}, status: {}", 
                 municipality, date, status);

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
        log.info("Updating booking status for token: {}, new status: {}", token, newStatus);

        Optional<Booking> bookingOpt = bookingRepository.findByToken(token);
        if (bookingOpt.isEmpty()) {
            log.warn("Booking not found with token: {}", token);
            throw new IllegalArgumentException("Booking not found");
        }

        Booking booking = bookingOpt.get();
        BookingStatus oldStatus = booking.getStatus();

        // Atualizar status
        booking.setStatus(newStatus);

        // Criar StateChange
        StateChange stateChange = new StateChange(booking, newStatus, note);
        booking.getStateHistory().add(stateChange);

        Booking updated = bookingRepository.save(booking);
        log.info("Booking status updated from {} to {} for token: {}", 
                 oldStatus, newStatus, token);

        return updated;
    }

    @Override
    @Transactional
    public void cancelBooking(String token) {
        log.info("Cancelling booking with token: {}", token);

        Optional<Booking> bookingOpt = bookingRepository.findByToken(token);
        if (bookingOpt.isEmpty()) {
            log.warn("Booking not found with token: {}", token);
            throw new IllegalArgumentException("Booking not found");
        }

        Booking booking = bookingOpt.get();

        // Validar se pode cancelar
        if (booking.getStatus() == BookingStatus.COMPLETED || 
            booking.getStatus() == BookingStatus.CANCELLED) {
            log.warn("Cannot cancel booking with status: {}", booking.getStatus());
            throw new IllegalStateException("Cannot cancel booking with status: " + booking.getStatus());
        }

        // Atualizar para CANCELLED
        booking.setStatus(BookingStatus.CANCELLED);
        StateChange stateChange = new StateChange(booking, BookingStatus.CANCELLED, "Cancelled by user");
        booking.getStateHistory().add(stateChange);

        bookingRepository.save(booking);
        log.info("Booking cancelled successfully with token: {}", token);
    }
}
