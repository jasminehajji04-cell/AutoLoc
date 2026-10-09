# Atelier 3 : notes sur la couche Repository

## Choix des interfaces

| Interface | Étend | Justification |
|---|---|---|
| IAgenceRepository | JpaRepository<Agence, Long> | CRUD complet, `findAll` renvoie une `List`, tri et pagination disponibles. |
| IEmployeRepository | JpaRepository<Employe, Long> | Idem. |
| IVehiculeRepository | JpaRepository<Vehicule, Long> | Idem, avec `saveAndFlush` pour une écriture immédiate. |
| IEquipementRepository | JpaRepository<Equipement, Long> | Idem. |
| IClientRepository | JpaRepository<Client, Long> | Idem. |
| IReservationRepository | JpaRepository<Reservation, Long> | Idem. |
| IContratRepository | JpaRepository<Contrat, Long> | CRUD complet. Les suppressions unitaires respectent la cascade et l'orphanRemoval vers les paiements. |
| IPaiementRepository | JpaRepository<Paiement, Long> | Sert à lire les paiements. Création et retrait passent par le Contrat (composition). |
| IMaintenanceRepository | JpaRepository<Maintenance, Long> | CRUD complet, tri et pagination pour l'historique. |

JpaRepository a été retenue pour toutes les entités : elle regroupe CRUD, listes, tri, pagination et flush.

**Attention** : `deleteAllInBatch` et `deleteAllByIdInBatch` contournent le contexte de persistance. La cascade et l'orphanRemoval ne s'appliquent pas : sur `Contrat`, ils laisseraient des paiements orphelins ou échoueraient sur la contrainte de clé étrangère.

## Anomalies SonarQube for IDE

| Anomalie | Règle / explication | Correction apportée |
|---|---|---|
| `Maintenance.java` : import inutilisé `java.util.List` | `java:S1128` : un import inutilisé alourdit le code et peut induire en erreur. | Import supprimé (Optimize Imports). |
