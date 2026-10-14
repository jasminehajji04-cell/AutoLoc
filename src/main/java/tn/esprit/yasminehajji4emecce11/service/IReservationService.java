package tn.esprit.yasminehajji4emecce11.service;

import java.util.List;
import tn.esprit.yasminehajji4emecce11.domain.Reservation;

public interface IReservationService {
    Reservation create(Reservation reservation);
    Reservation findById(Long id);
    List<Reservation> findAll();
    Reservation update(Long id, Reservation reservation);
    void deleteById(Long id);
}