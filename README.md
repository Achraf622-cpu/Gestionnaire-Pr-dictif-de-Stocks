# 📦 Gestionnaire Prédictif de Stocks

> Système intelligent de gestion des stocks avec prédictions basées sur l'IA

[![CI/CD Pipeline](https://github.com/Achraf622-cpu/Gestionnaire-Pr-dictif-de-Stocks/actions/workflows/ci.yml/badge.svg)](https://github.com/Achraf622-cpu/Gestionnaire-Pr-dictif-de-Stocks/actions)
![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.9-green)
![Spring AI](https://img.shields.io/badge/Spring%20AI-1.1.2-blue)
![License](https://img.shields.io/badge/License-MIT-yellow)

## 📋 Description

**Gestionnaire Prédictif de Stocks** est une application backend développée en **Spring Boot** qui permet la gestion complète des stocks d'entreprise avec une fonctionnalité unique : **la prédiction des besoins en stock grâce à l'intelligence artificielle**.

L'application utilise **Ollama** comme moteur d'IA local pour analyser l'historique des ventes et générer des prévisions précises sur les besoins futurs en approvisionnement.

---

## ✨ Fonctionnalités Principales

### 🏢 Gestion des Entités
- **Produits** : CRUD complet avec gestion des catégories et unités de mesure
- **Stocks** : Suivi des quantités en temps réel avec seuils d'alerte
- **Entrepôts** : Gestion multi-entrepôts avec localisation
- **Historique des Ventes** : Traçabilité complète des mouvements de stock

### 🤖 Intelligence Artificielle
- **Prédictions automatiques** : Analyse des tendances de vente via Ollama
- **Recommandations de réapprovisionnement** : Suggestions basées sur les données historiques
- **Évaluation des risques** : Classification des niveaux de risque (Faible, Moyen, Élevé, Critique)

### 🔐 Sécurité
- **Authentification JWT** : Tokens sécurisés pour l'accès à l'API
- **Gestion des rôles** : `ADMIN` et `USER` avec permissions différenciées
- **Chiffrement des données** : Protection des informations sensibles

### 📚 Documentation API
- Interface **Swagger UI** intégrée pour tester et explorer l'API
- Accessible à l'adresse : `http://localhost:8080/swagger-ui.html`

---

## 🛠️ Technologies Utilisées

| Catégorie | Technologies |
|-----------|-------------|
| **Backend** | Java 17, Spring Boot 3.5.9 |
| **Base de données** | PostgreSQL 16 |
| **IA** | Spring AI 1.1.2, Ollama |
| **Sécurité** | Spring Security, JWT (jjwt 0.12.6) |
| **ORM** | Spring Data JPA, Hibernate |
| **Mapping** | MapStruct 1.5.5 |
| **Documentation** | SpringDoc OpenAPI 2.8.14 |
| **Conteneurisation** | Docker, Docker Compose |
| **CI/CD** | GitHub Actions |

---

## 📁 Structure du Projet

```
src/main/java/com/team/sys_ai/
├── config/               # Configuration (OpenAPI, DataInitializer)
├── controller/           # Contrôleurs REST
│   ├── AuthController.java
│   ├── EntrepotController.java
│   ├── HistoriqueVenteController.java
│   ├── PrevisionController.java
│   ├── ProduitController.java
│   ├── StockController.java
│   └── UserController.java
├── dto/                  # Data Transfer Objects
├── entity/               # Entités JPA
│   ├── Entrepot.java
│   ├── HistoriqueVente.java
│   ├── Prevision.java
│   ├── Produit.java
│   ├── Stock.java
│   └── User.java
├── exception/            # Gestion des exceptions personnalisées
├── mapper/               # Mappers MapStruct
├── repository/           # Repositories JPA
├── security/             # Configuration sécurité & JWT
└── service/              # Logique métier
```

---

## 🚀 Installation et Démarrage

### Prérequis

- **Java 17** ou supérieur
- **Docker** et **Docker Compose**
- **Maven** (optionnel, wrapper inclus)

### 1️⃣ Cloner le projet

```bash
git clone https://github.com/Achraf622-cpu/Gestionnaire-Pr-dictif-de-Stocks.git
cd Gestionnaire-Pr-dictif-de-Stocks
```

### 2️⃣ Configurer les variables d'environnement

Créez un fichier `.env` à la racine du projet :

```bash
cp .env.example .env
```

Modifiez les valeurs selon vos besoins :

```env
# Base de données
DB_USER=stockuser
DB_PASSWORD=admin

# JWT (générez une clé secrète de 256 bits)
JWT_SECRET=votre_cle_secrete_256_bits

# Chiffrement (générez une clé de chiffrement)
ENCRYPTION_KEY=votre_cle_de_chiffrement
```

### 3️⃣ Lancer avec Docker Compose

```bash
docker compose up -d
```

Cette commande démarre :
- 🐘 **PostgreSQL** sur le port `5432`
- 🤖 **Ollama** sur le port `11434`
- 🚀 **Application** sur le port `8080`

### 4️⃣ Configurer le modèle IA (première utilisation)

Après le démarrage d'Ollama, téléchargez un modèle :

```bash
docker exec -it stock-manager-ollama ollama pull llama3.2
```

---

## 🔧 Développement Local

### Sans Docker

```bash
# Compiler le projet
./mvnw clean compile

# Lancer les tests
./mvnw test

# Démarrer l'application
./mvnw spring-boot:run
```

### Avec profil de développement

L'application démarre automatiquement avec le support Docker Compose en mode développement.

---

## 📖 Documentation API

Une fois l'application démarrée, accédez à :

| Ressource | URL |
|-----------|-----|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |

### Endpoints Principaux

| Méthode | Endpoint | Description |
|---------|----------|-------------|
| `POST` | `/api/auth/login` | Authentification |
| `GET` | `/api/produits` | Liste des produits |
| `GET` | `/api/stocks` | État des stocks |
| `GET` | `/api/previsions` | Prédictions IA |
| `GET` | `/api/entrepots` | Liste des entrepôts |
| `GET` | `/api/historique-ventes` | Historique des ventes |

---

## 🔐 Authentification

L'API utilise des tokens JWT. Pour accéder aux endpoints protégés :

1. Authentifiez-vous via `/api/auth/login`
2. Récupérez le token JWT dans la réponse
3. Incluez le token dans l'en-tête : `Authorization: Bearer <token>`

---

## 🐳 Architecture Docker

```yaml
services:
  app:          # Application Spring Boot (port 8080)
  postgres:     # Base de données PostgreSQL (port 5432)
  ollama:       # Serveur IA Ollama (port 11434)
```

---

## 🔄 CI/CD

Le projet utilise **GitHub Actions** pour l'intégration continue :

- ✅ Build automatique sur push (`master`, `develop`)
- ✅ Exécution des tests unitaires
- ✅ Construction de l'image Docker
- ✅ Upload des artefacts


