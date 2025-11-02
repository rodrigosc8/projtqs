package pt.rodrigo.hw1_tqs.service.unit;

import org.junit.jupiter.api.Test;
import pt.rodrigo.hw1_tqs.entities.Booking;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BookingTest {

    @Test
    void gettersAndSettersFuncionam() {
        Booking b = new Booking();
        b.setMunicipality("Porto");
        b.setDate(LocalDate.of(2030, 1, 2));
        b.setTimeslot("Manhã");
        b.setDescription("Exemplo");

        assertEquals("Porto", b.getMunicipality());
        assertEquals(LocalDate.of(2030, 1, 2), b.getDate());
        assertEquals("Manhã", b.getTimeslot());
        assertEquals("Exemplo", b.getDescription());
    }

}
