package tn.esprit.yasminehajji4emecce11.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.yasminehajji4emecce11.domain.Employe;

public interface IEmployeRepository extends JpaRepository<Employe, Long> {
}