package tn.esprit.yasminehajji4emecce11.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.yasminehajji4emecce11.domain.Reservation;

public interface IReservationRepository extends JpaRepository<Reservation, Long> {
}