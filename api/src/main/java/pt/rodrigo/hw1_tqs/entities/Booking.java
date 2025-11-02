package pt.rodrigo.hw1_tqs.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "booking")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @NotNull
    @Size(min = 1, max = 255)
    @Column(nullable = false)
    private String municipality;

    @NotNull
    @Size(min = 1, max = 500)
    @Column(nullable = false, length = 500)
    private String description;

    @NotNull
    @Column(nullable = false)
    private LocalDate date;

    @NotNull
    @Size(min = 1, max = 100)
    @Column(nullable = false)
    private String timeslot;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<StateChange> stateHistory = new ArrayList<>();

    public Booking() {
    }

    public Booking(String municipality, String description, LocalDate date, String timeslot) {
        this.token = UUID.randomUUID().toString();
        this.municipality = municipality;
        this.description = description;
        this.date = date;
        this.timeslot = timeslot;
        this.status = BookingStatus.RECEIVED;
        this.createdAt = LocalDateTime.now();
    }

    public void addStateChange(StateChange stateChange) {
        stateHistory.add(stateChange);
        stateChange.setBooking(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getTimeslot() {
        return timeslot;
    }

    public void setTimeslot(String timeslot) {
        this.timeslot = timeslot;
    }

    public BookingStatus getStatus() {
        return status;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<StateChange> getStateHistory() {
        return stateHistory;
    }

    public void setStateHistory(List<StateChange> stateHistory) {
        this.stateHistory = stateHistory;
    }
}
