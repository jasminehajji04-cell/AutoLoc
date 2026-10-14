# Note technique — Atelier 4 : IoC, injection de dépendances et couche Service

## A. Modes d'injection comparés

| Critère | Constructeur | Attribut |
|---|---|---|
| Champ `final` possible | Oui | Non |
| Dépendance visible dans la signature de la classe | Oui (paramètre du constructeur) | Non (cachée dans le corps de la classe) |
| Classe utilisable avec `new` hors conteneur, sans dépendance `null` | Oui, le constructeur l'exige | Non, l'objet peut exister avec `contratRepository = null` |
| Signalé par SonarQube for IDE | Non | Oui (règle **S6813** — *"Remove this field injection and use constructor injection instead"*) |

**Conclusion** : l'injection par constructeur, combinée à `@RequiredArgsConstructor` de Lombok, garantit qu'un service ne peut jamais exister dans un état incomplet. C'est la convention retenue pour tous les services AutoLoc.

## B. Services

| Service | Dépendances injectées | Mode | Justification |
|---|---|---|---|
| ContratServiceImpl | IContratRepository | Constructeur | Accès CRUD au contrat ; cascade ALL + orphanRemoval sur les paiements gérée au niveau JPA. |
| AgenceServiceImpl | IAgenceRepository | Constructeur | Accès CRUD à l'agence ; valide que le nom est renseigné. |
| EmployeServiceImpl | IEmployeRepository | Constructeur | Accès CRUD à l'employé ; valide que le nom et le prénom sont renseignés. |
| VehiculeServiceImpl | IVehiculeRepository | Constructeur | Accès CRUD au véhicule ; refuse un tarif journalier négatif. |
| EquipementServiceImpl | IEquipementRepository | Constructeur | Accès CRUD à l'équipement ; refuse un libellé vide. |
| ClientServiceImpl | IClientRepository | Constructeur | Accès CRUD au client ; valide que le nom et l'e-mail sont renseignés. |
| ReservationServiceImpl | IReservationRepository | Constructeur | Accès CRUD à la réservation ; refuse une date de fin antérieure à la date de début. |
| PaiementServiceImpl | IPaiementRepository | Constructeur | Lecture seule (findById, findAll) : un paiement n'est créé, modifié ou supprimé qu'à travers le Contrat (composition, Atelier 2). |
| MaintenanceServiceImpl | IMaintenanceRepository | Constructeur | Accès CRUD à la maintenance ; refuse une date de fin antérieure à la date de début. |

Tous les services utilisent **exclusivement** l'injection par constructeur via `@RequiredArgsConstructor`, avec un champ `final` typé par l'interface du repository correspondant. Aucun `new` de repository, aucune injection par attribut.

## C. Messages d'erreur

**Message 1 — `NoSuchBeanDefinitionException`**

Deux causes les plus probables côté `ContratServiceImpl` :
1. L'annotation `@Service` a été oubliée sur la classe : sans elle, le scan ne détecte pas la classe comme bean, donc aucune implémentation de `IContratService` n'existe pour le conteneur.
2. La classe `ContratServiceImpl` est placée en dehors du package racine scanné (`tn.esprit.yasminehajji4emecce11` et ses sous-packages) : le scan de `@SpringBootApplication` ne couvre que ce package.

**Message 2 — `NoUniqueBeanDefinitionException`**

Deux corrections possibles :
1. **`@Primary`**, posée sur la classe d'un des deux beans (ex. `@Primary` sur `EmailNotificateur`) : elle désigne ce bean comme candidat par défaut.
2. **`@Qualifier("nomDuBean")`**, posée sur le paramètre du constructeur du consommateur : elle précise explicitement quel bean injecter à cet endroit.

**Message 3 — Dépendance circulaire**

L'injection par constructeur exige que chaque bean soit entièrement construit avant d'être fourni à un autre. Si `ClientServiceImpl` a besoin de `ReservationServiceImpl` dans son constructeur, et inversement, aucun des deux ne peut être instancié en premier : le conteneur détecte l'impasse et refuse de démarrer, plutôt que de masquer le problème (ce que l'injection par attribut ou setter permettrait, en créant les deux objets "vides" puis en les reliant après coup).

Piste de conception : extraire la responsabilité commune dans un troisième composant (ex. un repository interrogé directement, ou un service dédié sans dépendance vers les deux premiers), pour que `ClientServiceImpl` et `ReservationServiceImpl` ne se référencent plus mutuellement.

## D. Anomalies SonarQube for IDE

| Anomalie | Règle / explication | Correction apportée |
|---|---|---|
| Injection par attribut (`@Autowired` sur un champ) dans ContratServiceImpl | **S6813** — l'injection par attribut cache la dépendance, empêche le champ `final` et rend l'objet instanciable dans un état incomplet hors conteneur. | Remplacée par l'injection par constructeur avec `@RequiredArgsConstructor` et champ `final`. |
| Levée d'une exception générique (`RuntimeException`) dans `findById` | **S112** — une exception générique ne documente pas la nature de l'erreur et empêche un traitement ciblé par les appelants. | Remplacée par l'exception métier dédiée `ResourceNotFoundException`, qui documente explicitement le cas "ressource absente". |
| Import inutilisé (`java.time.LocalDate`) dans IContratService | **S1128** — un import non utilisé alourdit le fichier et peut induire en erreur sur les dépendances réelles de la classe. | Import supprimé. |

## E. Questions de compréhension

**1. Où est le `new` de `ContratServiceImpl` et qui l'exécute ?**
Il n'y a aucun `new ContratServiceImpl(...)` écrit dans le code. C'est le conteneur Spring (`ApplicationContext`) qui, au démarrage, détecte la classe via `@Service`, résout sa dépendance (`IContratRepository`) et appelle lui-même le constructeur généré par Lombok pour créer le bean.

**2. Pourquoi un contrôleur dépendra-t-il de `IContratService` et non de `ContratServiceImpl` ?**
Pour dépendre d'une abstraction plutôt que d'une implémentation concrète : le contrôleur peut ainsi fonctionner avec n'importe quelle implémentation de `IContratService` (la vraie, une version de test, une version alternative), sans jamais être modifié si l'implémentation change.

**3. Pourquoi un service singleton doit-il rester sans état ? Exemple de bug.**
Un singleton est une seule instance partagée par tous les threads et toutes les requêtes HTTP simultanées. Si le service stockait un état propre à un appel (ex. un champ `contratCourant`), deux utilisateurs traitant des contrats différents au même moment écraseraient mutuellement cette valeur partagée, provoquant des résultats incohérents ou des données mélangées entre utilisateurs.

**4. Pourquoi `update` charge-t-il l'entité existante avant d'appeler `save` au lieu de lui passer directement l'objet reçu ?**
Parce que l'objet reçu en paramètre peut être incomplet (ex. venant d'une requête HTTP partielle) : le passer directement à `save` écraserait toutes les colonnes, y compris celles non transmises, et pourrait notamment vider la collection de paiements d'un contrat à cause de `orphanRemoval = true`. Charger l'existant puis recopier uniquement les champs modifiables préserve les données non concernées par la mise à jour.

**5. Différence entre `@Component` et `@Bean` ? Entre `@Primary` et `@Qualifier` ? Pourquoi `IPaiementService` n'expose-t-il ni création, ni modification, ni suppression ?**
- `@Component` s'applique **sur une classe** qu'on peut modifier : Spring la détecte via le scan. `@Bean` s'applique **sur une méthode** d'une classe `@Configuration`, pour déclarer un bean qu'on ne peut pas annoter directement (classe d'une bibliothèque externe, par exemple).
- `@Primary` désigne un **candidat par défaut** parmi plusieurs beans du même type, sans préciser lequel à chaque injection. `@Qualifier` fait un **choix explicite**, posé au point d'injection, indépendamment d'un éventuel `@Primary`.
- `IPaiementService` est volontairement en lecture seule car, selon le modèle métier (Atelier 2), un `Paiement` est en relation de composition avec son `Contrat` (`cascade = ALL`, `orphanRemoval = true`) : il n'a de sens que rattaché à un contrat, et sa création ou sa suppression doit donc passer exclusivement par le service `Contrat`, qui gère la cohérence de la collection de paiements.