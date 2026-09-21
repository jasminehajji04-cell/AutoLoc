# AutoLoc

Plateforme de gestion de location de véhicules multi-agences.

## Objectifs du projet

AutoLoc est une application permettant de gérer la location de véhicules
à travers plusieurs agences. Elle vise à faciliter :
- la réservation et la location de véhicules par les clients ;
- la gestion du parc automobile par les agences ;
- le suivi des contrats de location et des paiements ;
- la supervision globale de l'activité par l'administration.

## Acteurs identifiés

- **Client** : consulte le catalogue de véhicules, effectue une réservation,
  suit ses locations.
- **Agent d'agence** : gère les réservations, enregistre les départs/retours
  de véhicules, met à jour l'état du parc.
- **Responsable d'agence** : supervise l'activité de son agence, valide
  certaines opérations, consulte les statistiques.
- **Administrateur** : gère les comptes utilisateurs, les agences, et
  paramètre l'application globalement.

## Premiers cas d'utilisation

- Rechercher un véhicule disponible
- Réserver un véhicule
- Enregistrer le départ / retour d'un véhicule
- Gérer le parc automobile d'une agence
- Consulter l'historique des locations
- Gérer les comptes utilisateurs

## Stack technique

- Java 17, Spring Boot, Spring Data JPA
- MySQL
- Maven
- Postman (tests API)