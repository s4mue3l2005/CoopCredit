# CoopCredit 🏦

![Java 17](https://img.shields.io/badge/Java-17-orange?style=for-the-badge&logo=java)
![Spring Boot 3](https://img.shields.io/badge/Spring_Boot-3-brightgreen?style=for-the-badge&logo=spring-boot)
![Docker](https://img.shields.io/badge/Docker-20.10-blue?style=for-the-badge&logo=docker)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue?style=for-the-badge&logo=postgresql)
![JWT](https://img.shields.io/badge/JWT-Security-black?style=for-the-badge&logo=json-web-tokens)

**A distributed system for managing credit applications using Hexagonal Architecture.**

---

## 📖 About the Project

CoopCredit is a robust microservices-based system designed to handle credit application workflows with a focus on maintainability and scalability.

It is composed of two main microservices:
*   **`credit-application-service` (Core):** Implements the domain logic using **Hexagonal Architecture** (Ports & Adapters) to keep the core business rules isolated from external dependencies.
*   **`risk-central-mock-service` (External Simulation):** A simulation service that acts as an external credit risk evaluator, built using a standard **Layered Architecture**.

---

## 🏗️ Architecture & Design

### 1. Hexagonal Architecture
![Hexagonal Architecture Diagram](./Diagrama_de_Arquitectura_Hexagonal.png)
*Visual representation of the Ports & Adapters pattern used in the core service, showing the isolation of the Domain from the Infrastructure.*

### 2. Use Cases (Roles)
![Use Case Diagram](./Diagrama_de_Casos-de_Uso.png)
*Detailed workflow for the three primary roles: Affiliate (Applicants), Analyst (Reviewers), and Admin (System Managers).*

### 3. Microservices & Network
![Microservices & Docker Network Diagram](./Diagrama_de_Microservicios.png)
*Overview of the Docker container interactions, including the Core Application, Risk Mock Service, and PostgreSQL Database.*

---

## 📂 Project Structure

```text
CoopCredit/
├── credit-application-service/
│   ├── src/main/java/com/coopcredit/core/
│   │   ├── application/        # Use Cases Implementation
│   │   │   └── service/
│   │   ├── domain/             # Pure Java (Model, Ports)
│   │   │   ├── model/
│   │   │   └── port/
│   │   │       ├── in/
│   │   │       └── out/
│   │   └── infrastructure/     # Adapters & Configuration
│   │       ├── adapter/
│   │       │   ├── in/
│   │       │   │   └── web/
│   │       │   │       ├── advice/ # Exception Handlers
│   │       │   │       └── dto/    # Request/Response Objects
│   │       │   └── out/
│   │       │       ├── external/   # Risk Service Client
│   │       │       └── persistence/
│   │       │           ├── adapter/
│   │       │           ├── entity/
│   │       │           ├── mapper/
│   │       │           └── repository/
│   │       └── config/         # Spring Configuration (Security, OpenAPI)
├── risk-central-mock-service/
│   └── src/main/java/com/coopcredit/risk/  # Controller-Service-Repository Pattern
│       ├── controller/
│       ├── model/
│       ├── repository/
│       └── service/
├── docker-compose.yml
└── README.md
```

---

## 🚀 Getting Started

Follow these instructions to get a copy of the project implemented and running on your local machine.

### Prerequisites
*   **Java 17** SDK
*   **Maven** 3.8+
*   **Docker** & **Docker Compose**

### Installation

1.  **Clone the repository:**
    ```bash
    git clone https://github.com/your-username/CoopCredit.git
    cd CoopCredit
    ```

2.  **Run with Docker:**
    Build and start the services (this ensures the database and both microservices are up):
    ```bash
    docker-compose up -d --build
    ```

3.  **Verify Status:**
    Check if the containers are running:
    ```bash
    docker ps
    ```

### Accessing Interfaces

*   **Swagger UI (API Documentation):**
    [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)
*   **Grafana (Monitoring - Optional):**
    [http://localhost:3000](http://localhost:3000)

---

## 📡 API Reference

Here are the main endpoints available in the system:

| Domain | Method | Endpoint | Description |
| :--- | :--- | :--- | :--- |
| **Auth** | `POST` | `/api/v1/auth/register` | Register a new user |
| **Auth** | `POST` | `/api/v1/auth/login` | Authenticate and get JWT |
| **Affiliates** | `POST` | `/api/v1/affiliates` | Create a new affiliate |
| **Affiliates** | `GET` | `/api/v1/affiliates/{id}` | Get affiliate details |
| **Credits** | `POST` | `/api/v1/credits` | Apply for a new credit |
| **Credits** | `GET` | `/api/v1/credits` | List all credit applications |

---

## 🧪 Testing

To ensure the system's stability and compliance with business rules, we include a comprehensive test suite.

### Running Unit Tests
Executes isolation tests for domain logic and service layers.
```bash
mvn test
```

### Running Integration Tests
Executes end-to-end scenarios using **Testcontainers** (requires Docker).
```bash
mvn verify
```

> **Note:** The integration tests verify critical flows like Affiliate Registration and Credit Application with real database interactions.

---

## 🛠️ Configuration & Environment

The application is configured via `application.properties` and environment variables.

| Variable | Description | Default |
| :--- | :--- | :--- |
| `SERVER_PORT` | Port for the Credit Service | `8080` (mapped to `8082`) |
| `DB_URL` | PostgreSQL Connection URL | `jdbc:postgresql://postgres:5432/coopcredit` |
| `DB_USERNAME` | Database User | `postgres` |
| `DB_PASSWORD` | Database Password | `postgres` |
| `RISK_SERVICE_URL` | URL of the Risk Mock Service | `http://risk-service:8081` |
| `JWT_SECRET` | Secret key for token generation | *(Set in properties)* |

---

## 💡 API Usage Examples

### 1. Register an Affiliate
```bash
curl -X POST http://localhost:8082/api/v1/affiliates \
  -H "Authorization: Bearer <YOUR_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Jane Doe",
    "email": "jane@example.com",
    "document": "DOC12345",
    "salary": 5000.00,
    "enrollmentDate": "2023-01-01"
  }'
```

### 2. Apply for Credit
```bash
curl -X POST http://localhost:8082/api/v1/credits \
  -H "Authorization: Bearer <YOUR_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "affiliateId": 1,
    "amount": 20000.00,
    "term": 24
  }'
```

---

## 🤝 Contributing

1.  Fork the Project
2.  Create your Feature Branch (`git checkout -b feature/AmazingFeature`)
3.  Commit your Changes (`git commit -m 'Add some AmazingFeature'`)
4.  Push to the Branch (`git push origin feature/AmazingFeature`)
5.  Open a Pull Request

---

## 📄 License

Distributed under the MIT License. See `LICENSE` for more information.