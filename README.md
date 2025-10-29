# Currency Exchange

A microservices application for managing currency exchange operations, built with **Quarkus** and **Java 21**. The system demonstrates enterprise-grade patterns
including gRPC communication, REST APIs, fault tolerance, server-rendered web UI and containerization with Docker.

## Overview

Currency Exchange consists of three independent microservices:

- **Portfolio Service** (Port 8080) - Web UI for managing user portfolios and executing trades
- **Currency Service** (Port 8082) - gRPC service providing real-time exchange rates
- **Trades Service** (Port 8083) - REST API for trade execution and history

## Technology Stack

- **Java 21** - Modern Java with records and enhanced features
- **Quarkus 3.28.5** - Supersonic Subatomic Java Framework
- **Maven** - Multi-module project management
- **gRPC** - High-performance inter-service communication
- **REST/JSON** - HTTP APIs with OpenAPI documentation
- **Protocol Buffers** - Service contract definitions
- **Renarde + Qute** - Web framework and templating
- **Bootstrap 5** - Responsive UI design
- **SmallRye Fault Tolerance** - Circuit breakers and fallbacks
- **SmallRye Health** - Health checks and monitoring
- **Docker** - Containerization for deployment
- **GraalVM** - Native compilation for optimized performance

## Features

- 💱 Real-time currency exchange rates for 6 currencies (AUD, CAD, CHF, EUR, GBP, JPY)
- 📊 Dynamic rate fluctuation using algorithmic simulation
- 💼 Multi-currency portfolio management
- 📈 Trade execution and history tracking
- 🔒 User authentication and session management
- 🛡️ Fault-tolerant service communication
- 📝 OpenAPI documentation for REST endpoints
- 🐳 Docker containerization support
- 🚀 Native compilation support with GraalVM

## Architecture

```
┌─────────────────────────────────────────┐
│     Portfolio Service (Port 8080)       │
│   (Web UI + Orchestration Layer)        │
│  - Renarde Web Framework                │
│  - Qute Templates                       │
│  - gRPC Client (Currency)               │
│  - REST Client (Trades)                 │
└──────────┬──────────────┬───────────────┘
           │              │
    ┌──────▼──┐      ┌────▼──────┐
    │Currency │      │   Trades   │
    │Service  │      │  Service   │
    │gRPC:8082│      │REST:8083   │
    └─────────┘      └────────────┘
```

## Prerequisites

- **Java 21** or later
- **Maven 3.8+** or use the included Maven wrapper (`./mvnw`)
- **Docker** (optional, for containerization)
- **GraalVM** (optional, for native compilation)

## Getting Started

### 1. Clone the Repository

```bash
git clone <repository-url>
cd currency
```

### 2. Build All Services

```bash
mvn clean package
```

### 3. Run the Services

Open three terminal windows and start each service:

**Terminal 1 - Currency Service:**

```bash
cd currency
./mvnw quarkus:dev
```

**Terminal 2 - Trades Service:**

```bash
cd trades
./mvnw quarkus:dev
```

**Terminal 3 - Portfolio Service:**

```bash
cd portfolio
./mvnw quarkus:dev
```

### 4. Access the Application

- **Portfolio Web UI**: http://localhost:8080
- **Trades API Documentation**: http://localhost:8083/q/swagger-ui
- **Health Checks**:
  - Portfolio: http://localhost:8080/q/health
  - Currency: http://localhost:8082/q/health
  - Trades: http://localhost:8083/q/health

### 5. Login

Use one of the test accounts (password: `password` for all):

- john.doe@example.com
- jane.smith@example.com
- bob.johnson@example.com

## Development

### Running in Development Mode

Quarkus Dev Mode provides live reload, Dev UI, and continuous testing:

```bash
cd <service-name>
quarkus dev
```

### Accessing Dev UI

Each service has a Dev UI console accessible from the web UI "Dev UI" menu, or directly:

- Portfolio: http://localhost:8080/q/dev-ui
- Currency: http://localhost:8082/q/dev-ui
- Trades: http://localhost:8083/q/dev-ui

### Project Structure

```
currency/
├── pom.xml                    # Parent POM aggregator
├── bootstrap.sh               # Project generation script
├── README.md                  # This file
├── CLAUDE.md                  # AI assistant guidance
├── currency/                  # Currency service (gRPC)
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/.../currency/
│       │   │   ├── CurrencyRateServiceImpl.java
│       │   │   └── CurrencyRateData.java
│       │   ├── proto/
│       │   │   └── currency.proto
│       │   └── resources/
│       │       └── application.properties
│       └── test/
├── trades/                    # Trades service (REST)
│   ├── pom.xml
│   └── src/
│       ├── main/
│       │   ├── java/.../trade/
│       │   │   ├── TradeResource.java
│       │   │   ├── TradeService.java
│       │   │   ├── Trade.java
│       │   │   └── TradeHealthCheck.java
│       │   └── resources/
│       │       └── application.properties
│       └── test/
└── portfolio/                 # Portfolio service (Web UI)
    ├── pom.xml
    └── src/
        ├── main/
        │   ├── java/.../portfolio/
        │   │   ├── web/
        │   │   │   ├── WebApplication.java
        │   │   │   ├── UserSession.java
        │   │   │   └── TemplateGlobals.java
        │   │   ├── PortfolioService.java
        │   │   ├── Portfolio.java
        │   │   └── User.java
        │   ├── proto/
        │   │   └── currency.proto
        │   └── resources/
        │       ├── application.properties
        │       └── templates/
        │           └── WebApplication/
        └── test/
```

## Testing

### Run Unit Tests

```bash
# All services
mvn test

# Specific service
cd <service-name>
mvn test
```

### Testing the gRPC Currency service with grpcurl

The Currency service has gRPC reflection enabled for easy testing:

```bash
# Check service
grpcurl --plaintext localhost:8082 list
grpcurl --plaintext localhost:8082 describe currency.CurrencyRateService

# Get currency rates
grpcurl --plaintext localhost:8082 currency.CurrencyRateService/GetAllCurrentRates
grpcurl --plaintext -d '{"currency_code": "AUD"}' localhost:8082 currency.CurrencyRateService/GetCurrentRate
```

### Testing the REST Trade service with curl

The Trade service can be accessed with curl for easy testing:

```bash
curl -v -X 'POST' \
  'http://localhost:8083/api/trades' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "userId": "user123",
  "usdAmount": 125,
  "toCurrency": "EUR",
  "exchangeRate": 0.9217
}'

curl -X 'GET' \
  'http://localhost:8083/api/trades/user123' \
  -H 'accept: application/json' | jq
```

## API Documentation

### Trades Service REST API

When running in dev mode, access the OpenAPI documentation:

- Swagger UI: http://localhost:8083/q/swagger-ui
- OpenAPI Spec: http://localhost:8083/q/openapi

**Endpoints:**

- `POST /api/trades` - Execute a trade
- `GET /api/trades/{userId}` - Get user trade history

### Currency Service gRPC API

**Methods:**

- `GetCurrentRate(CurrencyRequest)` - Get rate for a specific currency
- `GetAllCurrentRates(Empty)` - Get rates for all 6 currencies

**Supported Currencies:** AUD, CAD, CHF, EUR, GBP, JPY

## Native Compilation

### Build Native Executables

**With GraalVM installed locally:**

```bash
# Using Quarkus CLI
quarkus build --clean --no-tests --native

# Or with Maven
mvn package -Dnative
```

**Using container build:**

```bash
mvn package -Dnative -Dquarkus.native.container-build=true
```

### Run Native Executable

```bash
./target/<service-name>-1.0.0-SNAPSHOT-runner
```

## Docker Support

### Build Docker Images

```bash
# JVM image
quarkus image build docker -Dquarkus.container-image.tag=jvm

# Native image
quarkus image build docker --native -Dquarkus.native.container-build=true -Dquarkus.container-image.tag=native
```

### Running Individual Containers

```bash
# Currency Service
docker run -i --rm -p 8082:8082 currencyexchange/currency:jvm

# Trades Service
docker run -i --rm -p 8083:8083 currencyexchange/trades:jvm

# Portfolio Service
docker run -i --rm -p 8080:8080 \
  -e QUARKUS_GRPC_CLIENTS_CURRENCY_HOST=currency \
  -e QUARKUS_REST_CLIENT_TRADES_URL=http://trades:8083 \
  currencyexchange/portfolio:jvm
```

### Available Dockerfiles

Each service includes multiple Dockerfile variants in `src/main/docker/`:

- `Dockerfile.jvm` - Standard JVM image
- `Dockerfile.native` - GraalVM native image
- `Dockerfile.native-micro` - Minimal native image
- `Dockerfile.legacy-jar` - Uber-jar image

### Running All Services with Docker Compose

**JVM containers:**

```bash
docker compose -p currencyexchange-jvm -f docker-compose-jvm.yml up -d
```

**Native containers:**

```bash
docker compose -p currencyexchange-native -f docker-compose-native.yml up -d
```

Access the application at `http://localhost:8080`

**Stop services:**

```bash
docker compose -p currencyexchange-jvm -f docker-compose-jvm.yml down
docker compose -p currencyexchange-native -f docker-compose-native.yml down
```

## Configuration

### Service Ports

- Portfolio: 8080
- Currency: 8082
- Trades: 8083

### Key Configuration Properties

**Currency Service** (`currency/src/main/resources/application.properties`):

```properties
quarkus.grpc.server.port=8082
quarkus.grpc.server.enable-reflection-service=true
```

**Trades Service** (`trades/src/main/resources/application.properties`):

```properties
quarkus.http.port=8083
quarkus.smallrye-openapi.info-title=Currency Exchange Trades API
```

**Portfolio Service** (`portfolio/src/main/resources/application.properties`):

```properties
quarkus.http.port=8080
quarkus.grpc.clients.currency.host=localhost
quarkus.grpc.clients.currency.port=8082
quarkus.rest-client.trades.url=http://localhost:8083
```

## Business Logic

### Exchange Rate Calculation

Currency rates fluctuate dynamically using a sin-based algorithm:

- Base rates stored in static map
- Per-currency seeds (1000-6000L) create independent movements
- Fluctuation range: ±1 unit around base rate
- Precision: 4 decimal places (2 for JPY)

### Trade Execution

1. User selects currency and amount in Portfolio UI
2. Portfolio retrieves current rate from Currency service (gRPC)
3. Trade is sent to Trades service for execution (REST)
4. Trades service calculates converted amount and sets status
5. Portfolio updates user balance in-memory

## Troubleshooting

### Services Won't Start

Ensure ports 8080, 8082, and 8083 are available:

```bash
lsof -i :8080
lsof -i :8082
lsof -i :8083
```

### gRPC Connection Issues

Verify Currency service is running and accessible:

```bash
grpcurl -plaintext localhost:8082 list
```

### Tests Failing

Some tests may fail if services are already running on the same ports. Stop running services before executing tests.

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## Resources

- [Quarkus Documentation](https://quarkus.io/guides/)
- [gRPC Java Documentation](https://grpc.io/docs/languages/java/)
- [Renarde Framework](https://quarkiverse.github.io/quarkiverse-docs/quarkus-renarde/dev/)
- [SmallRye Fault Tolerance](https://smallrye.io/docs/smallrye-fault-tolerance/)

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

Built with ❤️ using [Quarkus](https://quarkus.io/) - the Supersonic Subatomic Java Framework.
