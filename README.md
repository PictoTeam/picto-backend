# Picto Backend

Spring Boot application for Picto game backend with WebSocket support.

## 🚀 Quick Start

### Prerequisites

- Java 24
- Docker and Docker Compose
- Git

### Running with Docker Compose

### Running with Docker Compose (Development)

1. Clone the repository:
```bash
git clone https://github.com/PictoTeam/picto-backend.git
cd picto-backend
```

2. Set up environment variables:
```bash
cp .env.example .env
# Edit .env file with your configuration
```

3. Start PostgreSQL database:
```bash
docker-compose up -d
```

### Running locally (Development)

1. Start PostgreSQL database:
```bash
docker-compose up postgres -d
```

2. Run the application:
```bash
./gradlew bootRun
```

### Production Deployment

1. Set up production environment:
```bash
cp .env.prod.example .env.prod
# Edit .env.prod with your production configuration
```

2. Deploy to production:
```bash
./deploy-prod.sh
```

Or manually:
```bash
docker-compose -f compose.prod.yaml up -d
```

The production setup includes:
- Application container with resource limits
- PostgreSQL database with persistent storage
- Nginx reverse proxy with SSL support
- Health checks and auto-restart policies
- Security headers and rate limiting

2. Run the application:
```bash
./gradlew bootRun
```

## 🐳 Docker

### Building the image

```bash
docker build -t picto-backend .
```

### Running the container

```bash
docker run -p 8080:8080 \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host.docker.internal:5432/picto \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=postgres \
  picto-backend
```

## 🔧 Development

### Running tests

```bash
./gradlew test
```

### Building the application

```bash
./gradlew build
```

### Code documentation

Generate Dokka documentation:
```bash
./gradlew dokkaHtml
```

Documentation will be available in `build/dokka/`

## 📋 API Endpoints

### Health Check

- `GET /actuator/health` - Application health status
- `GET /actuator/info` - Application information
- `GET /actuator/metrics` - Application metrics

### Game API

- WebSocket endpoint: `/ws/game`
- REST endpoints under `/api/v1/`

## 🚀 CI/CD

This project uses GitHub Actions for continuous integration and deployment:

- **Test**: Runs unit and integration tests
- **Build**: Creates application JAR
- **Docker**: Builds and pushes Docker image to GitHub Container Registry
- **Security**: Runs dependency vulnerability scans

### GitHub Actions Workflows

1. **Main CI/CD Pipeline** (`ci.yml`):
   - Triggered on: `master`, `main`, `develop`, `feature/ci` branches
   - Full pipeline with Docker build and push (only for `master`/`main`)

2. **Feature Branch CI** (`feature.yml`):
   - Triggered on: all `feature/**` branches
   - Lightweight testing and Docker build validation (no push)

3. **Release Pipeline** (`release.yml`):
   - Triggered on: version tags (e.g., `v1.0.0`)
   - Creates GitHub releases with Docker images

## 🌐 Environment Variables

| Variable | Description | Default |
|----------|-------------|---------|
| `DATABASE_NAME` | PostgreSQL database name | `picto` |
| `DATABASE_USER` | PostgreSQL username | `postgres` |
| `DATABASE_PASSWORD` | PostgreSQL password | `postgres` |
| `DATABASE_PORT` | PostgreSQL port | `5432` |
| `SPRING_PROFILES_ACTIVE` | Active Spring profile | `default` |

## 📁 Project Structure

```
src/
├── main/
│   ├── kotlin/pl/umcs/picto3/
│   │   ├── config/          # Configuration classes
│   │   ├── game/            # Game logic and WebSocket handlers
│   │   ├── gameconfig/      # Game configuration management
│   │   ├── image/           # Image management
│   │   ├── player/          # Player entities
│   │   ├── round/           # Game round logic
│   │   ├── session/         # Session management
│   │   └── symbol/          # Symbol management
│   └── resources/
│       ├── db/changelog/    # Liquibase database migrations
│       └── static/          # Static resources (images, symbols)
└── test/                    # Test files
```

---

# Original Documentation

## Zcentralizowany error handler

---

## Stworzenie matchMakera

1. podejście iteracyjne w ramach stage’y danych rund
2. freeze obecnego stanu w ramach pauzy
3. zatrzymanie tworzenia się nowych rund

---

## Serwis do websocketów z funkcjami manipulującymi graczem

### 1. heartbeat (5 sec z timeoutem):

- **1.** heartbeat (usuwać z puli, jeżeli się w niej znajduje)  
- **2.** heartbeat (mogą być problemy partnera)  
- **3–5.** heartbeat (próbuje przywrócić twojego partnera)  
- **6.** heartbeat (przerwanie rundy)

### 2. action

- stwórz konstelację symboli

### 3. answer

- wybierz odpowiedź

### 4. wait

- poczekaj na “słowo” (akcja dla obydwu stron)

### 5. informacja zwrotna

- zgadzają się czy nie?
- może ile punktów?

---

## TODO

### Wgranie zasobów

- [ ] Wgranie obrazków na serwer/dolaczenie ich do kontekstu
- [ ] Wgranie symboli na serwer/dolaczenie ich do kontekstu

### Setupowanie gry 0.1 (przetestować)

- [ ] reużywalność configów

### Dołączenie do gry

- [ ] dobicie się po ID
- [ ] powiązanie z pseudonimem
- [ ] trackowanie wszystkich userów w ramach sesji
- [ ] nawiązanie socketu
- [ ] ustawienie jego stanu na ready/in matchmaker

---

## Przebieg gry ← websocket, główna pętla

### Wystartować grę

- [ ] info dla MM: hej, dobieraj pary
- [ ] kiedy para is found → emit zmiany czynności
- [ ] powinien przejść cykl aktywności:

#### 1. Initial mówca

- [ ] action
- [ ] wait (poczekaj na odpowiedź)
- [ ] informacja zwrotna
- [ ] wróć do puli

#### 2. Initial listener

- [ ] wait (poczekaj aż opponent wybierze)
- [ ] answer
- [ ] informacja zwrotna
- [ ] wróć do puli

- [ ] zapisz rundę (async bo serwer się zatryzma xd)
- [ ] przekazanie pary z oznaczeniem, że się widzieli, do MMa

---

### Ponowny rejoin do gry

#### 1. w trakcie rundy

- [ ] heartbeat > 6 → wróć do puli MM, szukać nowego & cancel poprzedniej rundy (wyczyszczenie rundy z pamięci)
- [ ] heartbeat <= 6 → runda/MM będzie znać stany, więc płynnie powracamy do etapu/iteracji, w której był

#### 2. w trakcie czekania na parę

- [ ] podejście z heartbeata de facto

---

## Zakończenie gry

- [ ] hej MM, nie startuj nowych rund

---

## Optional <Podsumowanie>

- [ ] Szanowny Pan Kosela musi powiedzieć co chce 
