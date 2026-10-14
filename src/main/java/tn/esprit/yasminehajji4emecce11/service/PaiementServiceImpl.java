package tn.esprit.yasminehajji4emecce11.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.yasminehajji4emecce11.domain.Paiement;
import tn.esprit.yasminehajji4emecce11.exception.ResourceNotFoundException;
import tn.esprit.yasminehajji4emecce11.repository.IPaiementRepository;

@Service
@RequiredArgsConstructor
public class PaiementServiceImpl implements IPaiementService {

    private final IPaiementRepository paiementRepository;

    @Override
    public Paiement findById(Long id) {
        return paiementRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Paiement", id));
    }

    @Override
    public List<Paiement> findAll() {
        return paiementRepository.findAll();
    }
}