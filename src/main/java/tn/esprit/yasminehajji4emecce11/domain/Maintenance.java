package tn.esprit.yasminehajji4emecce11.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

import java.util.List;

@Entity
@Table(name = "maintenance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Maintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMaintenance;

    @Column(nullable = false)
    private LocalDate dateDebut;

    private LocalDate dateFin;

    @Column(length = 255)
    private String description;
    @ManyToOne(fetch = FetchType.LAZY)
    private Vehicule vehicule;

}