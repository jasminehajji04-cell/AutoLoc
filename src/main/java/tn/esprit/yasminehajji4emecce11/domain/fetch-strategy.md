# Stratégie Fetch et Cascade — AutoLoc

| Association | Fetch | Cascade | Justification |
|---|---|---|---|
| Contrat → Paiement | LAZY | ALL + orphanRemoval | Un paiement n'existe que rattaché à son contrat ; supprimer le contrat supprime ses paiements. |
| Agence → Vehicule | LAZY | Aucune | Un véhicule survit à la suppression de son agence. |
| Agence → Employe | LAZY | Aucune | Un employé n'est pas supprimé automatiquement si son agence l'est. |
| Vehicule ↔ Equipement | LAZY | Aucune | Les équipements sont partagés entre plusieurs véhicules ; aucune dépendance de cycle de vie. |
| Client → Reservation | LAZY | PERSIST | Créer un client avec ses réservations les enregistre automatiquement, mais supprimer un client ne doit pas supprimer ses réservations. |
| Reservation → Vehicule | LAZY | Aucune | Un véhicule existe indépendamment de ses réservations. |
| Reservation ↔ Contrat | LAZY | ALL (côté Reservation) | Un contrat est créé avec sa réservation ; supprimer la réservation supprime le contrat associé. |
| Vehicule → Maintenance | LAZY | PERSIST | Créer un véhicule avec ses maintenances les enregistre automatiquement ; supprimer un véhicule ne supprime pas forcément l'historique de maintenance. |

## Fetch : pourquoi LAZY partout ?

Toutes les associations sont configurées en `FetchType.LAZY`, y compris les `@ManyToOne` et `@OneToOne` (EAGER par défaut en JPA). Cela évite de charger systématiquement toute la chaîne d'objets liés à chaque requête, ce qui alourdirait les performances. Les données liées ne sont chargées que lorsque le code y accède explicitement.