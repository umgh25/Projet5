# Yoga App — Application Full‑stack

Une application web de gestion de séances de yoga. Projet full‑stack composé d'un frontend Angular et d'un backend Spring Boot.

## 🎯 Objectif
Fournir une application de démonstration complète pour :
- gérer des utilisateurs (admin, enseignants, élèves)
- organiser des séances et sessions de yoga
- fournir des API REST sécurisées et une interface Angular réactive

## 🧭 Structure du dépôt
- `back/` : backend Spring Boot (Java, Maven)
- `front/` : frontend Angular (Node, npm)
- `ressources/postman/` : collection Postman (API)

## ⚙️ Prérequis
- Java 21
- Maven 3.6+
- Git
 - Node.js 16+
 - npm (fourni avec Node)
 - MySQL 8 (ou autre base compatible) — la configuration est dans `back/src/main/resources`/`application.properties`
 - Git

Conseil : utilisez PowerShell (Windows) pour suivre les commandes ci‑dessous.


## 🚀 Démarrage rapide (Windows PowerShell)
1) Backend — compiler et démarrer

```powershell
cd back
mvn clean install
mvn spring-boot:run
```


```powershell
cd front
npm install
npm start
```

Notes :
- Pour ignorer les tests Maven lors de l'installation : `mvn clean install -DskipTests`
- Le front utilise `proxy.config.json` pour rediriger les appels API vers le backend en développement.

## 🔐 Compte administrateur par défaut (dev)
- Email : `yoga@studio.com`
- Mot de passe : `test!1234`

Changez ces identifiants en production ou supprimez le jeu de données par défaut.

## 🗄️ Base de données

Le projet utilise MySQL (par défaut) mais une autre base compatible JDBC peut être utilisée. Le schéma peut être généré automatiquement par Spring JPA/Hibernate au démarrage ; un script d'initialisation est fourni si vous préférez créer la base manuellement.

Voici des instructions pas‑à‑pas (Windows PowerShell) pour préparer la base et configurer l'application :


Rapports de couverture frontend (chemins observés dans le dépôt) :

- ➡️ Unitaires (Jest) : `front/coverage/jest/` ou `front/coverage/lcov-report/index.html` (selon la configuration de génération)
- ➡️ Intégration : `front/coverage/integration/lcov-report/index.html`
- ➡️ End-to-End (Cypress) : si vous générez la couverture E2E, elle apparaîtra sous `front/coverage/e2e/` (génération via `npm run e2e:coverage` ou l'équivalent)

Vérifiez le dossier `front/coverage/` pour trouver les rapports index.html correspondant à chaque type de test.
Ouvrez PowerShell et lancez le client MySQL :

```powershell
mysql -u root -p
```

Puis exécutez les commandes SQL suivantes (remplacez les identifiants par vos choix) :

```sql
CREATE DATABASE yoga_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
CREATE USER 'yoga_user'@'localhost' IDENTIFIED BY 'change_me';
GRANT ALL PRIVILEGES ON yoga_db.* TO 'yoga_user'@'localhost';
FLUSH PRIVILEGES;
EXIT;
```

3) Exécuter le script d'initialisation fourni (optionnel)

Si vous souhaitez charger des données initiales via le script présent dans le dépôt :

```powershell
mysql -u yoga_user -p yoga_db < ressources/sql/script.sql
```

4) Exemple de configuration Spring Boot

Mettez à jour `back/src/main/resources/application.properties` (ou créez `application-test.properties` pour les tests) avec vos paramètres :

```
spring.datasource.url=jdbc:mysql://localhost:3306/yoga_db?useSSL=false&serverTimezone=UTC
spring.datasource.username=yoga_user
spring.datasource.password=change_me
spring.jpa.hibernate.ddl-auto=update
# autres propriétés utiles
spring.jpa.show-sql=false
```

## 🧪 Tests

Backend (JUnit + Failsafe + JaCoCo)

Les commandes suivantes sont celles à utiliser dans `back/` :

```powershell
cd back
# 1) Tests unitaires (génère le rapport UT)
mvn clean test

# 2) Tests d'intégration (Failsafe) — génère le rapport IT
mvn -DskipTests=false verify

# Optionnel : tout en une seule commande (unit + integration + rapport fusionné)
mvn clean verify
```

Sorties / rapports JaCoCo attendus :
- Unit tests -> `back/target/site/jacoco-ut/index.html`
- Integration tests -> `back/target/site/jacoco-it/index.html`
- Rapport fusionné (après `mvn clean verify`) disponible dans le dossier `target/site/` (selon configuration du build)

Frontend (Jest + Cypress)

Dans `front/`, utilisez les commandes suivantes selon le type de test :

```powershell
cd front

# Tests unitaires
npm run test
npm run test -- --coverage

# Tests d'intégration
npm run test:integration
npm run coverage:integration

# E2E Cypress
npm run cypress:run
npm run e2e:coverage
```

Rapports de couverture frontend (chemins précis) :

- ➡️ Unitaires : `front/coverage/unit/lcov-report/index.html`
- ➡️ Intégration : `front/coverage/integration/lcov-report/index.html`
- ➡️ End-to-End (Cypress) : `front/coverage/e2e/lcov-report/index.html`

Rapports de couverture frontend : vérifiez `front/coverage/` (unit / integration / e2e) — ouvrez les `index.html` correspondants pour la synthèse.

## 🧾 API & Postman
La collection Postman est fournie dans `ressources/postman/yoga.postman_collection.json`.

## 🛠️ Variables d'environnement / Configuration
- Backend : regardez `back/src/main/resources/application.properties` et `application-test.properties` pour les paramètres (DB, JWT, ports).
- Frontend : `front/src/environments/` contient les configurations d'environnement.

## 📦 Build et déploiement
- Backend : `mvn clean package` produit un JAR dans `back/target/`.
- Frontend : `npm run build` (ou `ng build`) génère les fichiers statiques à déployer.

