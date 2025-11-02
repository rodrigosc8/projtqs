package pt.rodrigo.hw1_tqs.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import pt.rodrigo.hw1_tqs.entities.StateChange;

public interface StateChangeRepository extends JpaRepository<StateChange, Long> {
}
