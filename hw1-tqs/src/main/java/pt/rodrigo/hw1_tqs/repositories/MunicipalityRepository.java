package pt.rodrigo.hw1_tqs.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import pt.rodrigo.hw1_tqs.entities.Municipality;

import java.util.Optional;

public interface MunicipalityRepository extends JpaRepository<Municipality, Long> {
    
    Optional<Municipality> findByName(String name);
    
    boolean existsByName(String name);
}
