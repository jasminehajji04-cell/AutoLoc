package tn.esprit.yasminehajji4emecce11.service;

import java.util.List;
import tn.esprit.yasminehajji4emecce11.domain.Paiement;

public interface IPaiementService {
    Paiement findById(Long id);
    List<Paiement> findAll();
}