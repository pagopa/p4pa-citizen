# p4pa-citizen

This application belong to the **inbound/outbound** tier of the **Piattaforma Unitaria** product.

See [PU Microservice Architecture](https://pagopa.atlassian.net/wiki/spaces/SPAC/pages/1405845916/Architettura+microservizi) for more details.

## 🧱 Role

* To expose data towards ARpu:
  * It authorizes requests retrieving user info through [p4pa-auth](https://github.com/pagopa/p4pa-auth);

## 🌐 APIs
See [OpenAPI](openapi/generated.openapi.json), exposed through the following path:
* `/swagger-ui/index.html`

See [Postman collection](/postman/p4pa-citizen.postman_collection.json) and [Postman Environment](https://pagopa.atlassian.net/wiki/spaces/SPAC/pages/1094615081/Environment+collection+postman).

### 📌 Common HTTP status returned:
* `401`: Invalid access token provided, thus a new login is required;
* `403`: Trying to access a not authorized resource.

## 🔎 Monitoring
See available actuator endpoints through the following path:
* `/actuator`

### 📌 Relevant endpoints
* Health (provide an accessToken to see details): `/actuator/health`
  * Liveness: `/actuator/health/liveness`
  * Readiness: `/actuator/health/readiness`
* Metrics: `/actuator/metrics`
  * Prometheus: `/actuator/prometheus`

Further endpoints are exposed through the JMX console.

## ✏️ Logging
See [log configured pattern](/src/main/resources/logback-spring.xml).

## 🔗 Dependencies

### 🧩 Microservices
* [p4pa-auth](https://github.com/pagopa/p4pa-auth):
  * To validate access token and retrieve user info;
* [p4pa-debt-positions](https://github.com/pagopa/p4pa-debt-positions):
  * To access to domain data and operations;
* [p4pa-organization](https://github.com/pagopa/p4pa-organization):
  * To access to domain data and operations;

## 🔧 Configuration

See [application.yml](src/main/resources/application.yml) for each configurable property.

### 📌 Relevant configurations

#### 🌐 Application Server
| ENV               | DESCRIPTION                                                                     | DEFAULT               |
|-------------------|---------------------------------------------------------------------------------|-----------------------|
| SERVER_PORT       | Application server listening port                                               | 8080                  |

#### ✏️ Logging
| ENV                                   | DESCRIPTION                                                                                                                                                                     | DEFAULT |
|---------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|---------|
| LOG_LEVEL_ROOT                        | Base level                                                                                                                                                                      | INFO    |
| LOG_LEVEL_PAGOPA                      | Base level of custom classes                                                                                                                                                    | INFO    |
| LOG_LEVEL_SPRING                      | Level applied to Spring framework                                                                                                                                               | INFO    |
| LOG_LEVEL_SPRING_BOOT_AVAILABILITY    | To print availability events                                                                                                                                                    | DEBUG   |
| LOGGING_LEVEL_API_REQUEST_EXCEPTION   | Level applied to APIs exception                                                                                                                                                 | INFO    |
| LOG_LEVEL_PERFORMANCE_LOG             | Level applied to [PerformanceLog](https://pagopa.atlassian.net/wiki/spaces/SPAC/pages/1540096383/Logging#2.2.-Log-di-performance)                                               | INFO    |
| LOG_LEVEL_PERFORMANCE_LOG_API_REQUEST | Level applied to [API Performance Log](https://pagopa.atlassian.net/wiki/spaces/SPAC/pages/1540096383/Logging#2.2.2.1.-Log-di-perfomance-per-le-API)                            | INFO    |
| LOG_LEVEL_PERFORMANCE_LOG_REST_INVOKE | Level applied to [REST invoke Performance Log](https://pagopa.atlassian.net/wiki/spaces/SPAC/pages/1540096383/Logging#2.2.2.2.-Log-di-performance-per-i-servizi-REST-integrati) | INFO    |

#### 🔁 Integrations

##### 🔗 REST
| ENV                                               | DESCRIPTION                               | DEFAULT |
|---------------------------------------------------|-------------------------------------------|---------|
| DEFAULT_REST_CONNECTION_POOL_SIZE                 | Default connection pool size              | 10      |
| DEFAULT_REST_CONNECTION_POOL_SIZE_PER_ROUTE       | Default connection pool size per route    | 5       |
| DEFAULT_REST_CONNECTION_POOL_TIME_TO_LIVE_MINUTES | Default connection pool TTL (minutes)     | 10      |
| DEFAULT_REST_TIMEOUT_CONNECT_MILLIS               | Default connection timeout (milliseconds) | 120000  |
| DEFAULT_REST_TIMEOUT_READ_MILLIS                  | Default read timeout (milliseconds)       | 120000  |

##### 🧩 Microservices
| ENV                                   | DESCRIPTION                                            | DEFAULT |
|---------------------------------------|--------------------------------------------------------|---------|
| AUTH_BASE_URL                         | Auth microservice URL                                  |         |
| AUTH_MAX_ATTEMPTS                     | Auth API max attempts                                  | 3       |
| AUTH_WAIT_TIME_MILLIS                 | Auth retry waiting time (milliseconds)                 | 500     |
| AUTH_PRINT_BODY_WHEN_ERROR            | To print body when an error occurs                     | true    |
| DEBT_POSITIONS_BASE_URL               | DebtPosition microservice URL                          |         |
| DEBT_POSITIONS_MAX_ATTEMPTS           | DebtPosition API max attempts                          | 3       |
| DEBT_POSITIONS_WAIT_TIME_MILLIS       | DebtPosition retry waiting time (milliseconds)         | 500     |
| DEBT_POSITIONS_PRINT_BODY_WHEN_ERROR  | To print body when an error occurs                     | true    |
| ORGANIZATION_BASE_URL                 | Organization microservice URL                          |         |
| ORGANIZATION_MAX_ATTEMPTS             | Organization API max attempts                          | 3       |
| ORGANIZATION_WAIT_TIME_MILLIS         | Organization retry waiting time (milliseconds)         | 500     |
| ORGANIZATION_PRINT_BODY_WHEN_ERROR    | To print body when an error occurs                     | true    |

#### 💼 Business logic
| ENV                                   | DESCRIPTION                     | DEFAULT   |
|---------------------------------------|---------------------------------|-----------|
| REST_SPONTANEOUS_DUE_DATE_OFFSET_DAYS | Offset for spontaneous dueDate  | 1         |


## 🛠️ Getting Started

### 📝 Prerequisites

Ensure the following tools are installed on your machine:

1. **Java 21+**
2. **Gradle** (or use the Gradle wrapper included in the repository)
3. **Docker** (to build and run on an isolated environment, optional)

### 🔐 Write Locks

```sh
./gradlew dependencies --write-locks
```

### ⚙️ Build

```sh
./gradlew clean build
```

### 🧪 Test

#### 📌 JUnit
```sh
./gradlew test
```

### 🚀 Run local

```sh
./gradlew bootRun
```

### 🐳 Build & run through Docker
```sh
docker build -t <APP_NAME> .
docker run --env-file <ENV_FILE> <APP_NAME>
```
