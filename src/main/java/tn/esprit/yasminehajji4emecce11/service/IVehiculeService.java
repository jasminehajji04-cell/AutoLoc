package tn.esprit.yasminehajji4emecce11.service;

import java.util.List;
import tn.esprit.yasminehajji4emecce11.domain.Vehicule;

public interface IVehiculeService {
    Vehicule create(Vehicule vehicule);
    Vehicule findById(Long id);
    List<Vehicule> findAll();
    Vehicule update(Long id, Vehicule vehicule);
    void deleteById(Long id);
}