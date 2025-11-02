package pt.rodrigo.hw1_tqs.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import pt.rodrigo.hw1_tqs.entities.Booking;
import pt.rodrigo.hw1_tqs.entities.BookingStatus;

import java.time.LocalDate;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    
    Optional<Booking> findByToken(String token);
    
    Page<Booking> findByMunicipality(String municipality, Pageable pageable);
    
    Page<Booking> findByStatus(BookingStatus status, Pageable pageable);
    
    Page<Booking> findByMunicipalityAndStatus(String municipality, BookingStatus status, Pageable pageable);
    
    Page<Booking> findByDate(LocalDate date, Pageable pageable);
    
    @Query("SELECT COUNT(b) FROM Booking b WHERE b.municipality = ?1 AND b.date = ?2")
    long countByMunicipalityAndDate(String municipality, LocalDate date);
}
