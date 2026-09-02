# Consumer API

Spring Boot API that manages a customer's subscriptions and keeps them in sync with the
`provider` API. It owns its own `users` / `subscriptions` / `user_subscriptions` data, and
calls out to `provider` (over HTTP, using a client-credentials JWT it requests for itself)
whenever a subscription action needs to be reflected on the partner side.

## Stack

| Layer | Technology |
|---|---|
| Language / runtime | Java 21 (LTS) |
| Framework | Spring Boot 4.1.0 |
| Build | Gradle 9.7 (wrapper, `./gradlew`) |
| Web | `spring-boot-starter-webmvc`                                                                                                         |
| Persistence | `spring-boot-starter-data-jpa` + MySQL 8.4 (H2 available at runtime for quick local boots)                                           |
| Messaging | `spring-boot-starter-kafka` (dependency wired, no producer/consumer implemented yet)                                                   |
| Provider integration | `RestClient` (`ProviderClientConfig`), client-credentials token fetched from `provider`'s `/api/auth/token`                          |
| API docs | `springdoc-openapi-starter-webmvc-ui` 3.1.0 (Swagger UI + OpenAPI 3)                                                                 |
| Observability | Spring Boot Actuator + Micrometer (Prometheus), Log4j2 (console + JSON rolling file), Fluent Bit -> Elasticsearch -> Kibana          |
| Other | Lombok, JUnit 5                                                                                                                      |

To avoid port clashes when running both APIs locally at the same time, `consumer` uses a
different set of host ports than `provider` everywhere - see the [service/port table](#services--ports)
below.

## Package structure

```
br.com.jhonatan.consumer
├── client        # RestClient integration with the provider API
│   ├── dto       # Provider request/response DTOs
│   └── exceptions
├── config        # @Configuration (OpenApiConfig)
├── controller    # @RestController
├── dto           # transfer objects (request/response) for this API's own endpoints
├── enums         # SubscriptionStatus, Actions
├── exception     # this API's own domain exceptions
├── infra
│   └── exception # @RestControllerAdvice (RestExceptionHandler)
├── kafka
│   ├── producer  # empty for now
│   ├── consumer  # empty for now
│   └── config    # empty for now
├── model         # JPA entities
├── repository    # Spring Data JPA repositories
└── service       # business logic
```

## Prerequisites

- JDK 21
- Docker (used by `spring-boot-docker-compose` to start MySQL/Kafka/Prometheus/Grafana/Elasticsearch/Kibana/Fluent Bit automatically on `bootRun`)
- A running `provider` instance reachable at `provider.api.base-url` (defaults to `http://localhost:8080`), with a client already registered there for `consumer` to authenticate as (see `provider`'s `security.clients` config)

## Configuration

`application.yml` reads everything from environment variables, with local-friendly defaults for
most of them:

| Variable | Default | Purpose |
|---|---|---|
| `SERVER_PORT` | `8081` | HTTP port |
| `SPRING_DATASOURCE_URL` | `jdbc:mysql://localhost:3307/consumer_db` | MySQL connection |
| `SPRING_DATASOURCE_USERNAME` / `SPRING_DATASOURCE_PASSWORD` | `consumer_user` / `consumer_pass` | MySQL credentials |
| `SPRING_KAFKA_BOOTSTRAP_SERVERS` | `localhost:9093` | Kafka broker |
| `PROVIDER_API_BASE_URL` | `http://localhost:8080` | Base URL of the `provider` API |
| `PROVIDER_CLIENT_ID` | *(none)* | Client id this app authenticates as against `provider`'s `/api/auth/token` |
| `PROVIDER_CLIENT_SECRET` | *(none)* | Matching plaintext secret for that client |

> `PROVIDER_CLIENT_ID`/`PROVIDER_CLIENT_SECRET` have **no default** - the app will fail to start
> with an unresolved placeholder error if they aren't set. Whatever client you register on the
> `provider` side, use its plaintext secret here (unlike `provider`'s own `.env`, which needs the
> BCrypt hash - `consumer` sends the plaintext straight to `/api/auth/token`).

## Running locally

Docker Compose support is enabled (`spring.docker.compose.enabled: true`), so `compose.yaml` is
started automatically:

```bash
./gradlew bootRun
```

Make sure `provider` is already running and `PROVIDER_CLIENT_ID`/`PROVIDER_CLIENT_SECRET` are
set first (see [Configuration](#configuration)).

| Service       | Port | Notes |
|---------------|---|---|
| Consumer API  | `8081` | `provider`'s equivalent is `8080` |
| MySQL         | `3307` | `provider`'s equivalent is `3306` |
| Kafka         | `9093` | `provider`'s equivalent is `9092` |
| Prometheus    | `9091` | `provider`'s equivalent is `9090` |
| Grafana       | `3001` | `provider`'s equivalent is `3000` |
| Elasticsearch | `9201` | `provider` uses OpenSearch on `9200` instead |
| Kibana        | `5602` | `provider` uses OpenSearch Dashboards on `5601` instead |

## How it talks to `provider`

`ProviderTokenClient` calls `provider`'s `POST /api/auth/token` with `PROVIDER_CLIENT_ID` /
`PROVIDER_CLIENT_SECRET` and gets back a short-lived bearer token. `SubscriptionsServiceImpl`
requests a fresh token before **every** call it makes through `ProviderSubscriptionsClient`
(create user, create/cancel subscription, update user) and attaches it as
`Authorization: Bearer <token>`. There's no caching/reuse of the token between calls yet - see
[Known issues](#known-issues--open-items).

## API

All endpoints are under `/api/users/{document}/subscriptions`:

| Method | Path | Description |
|---|---|---|
| GET | `/` | List the user's subscriptions |
| POST | `/{subscriptionCode}/activate` | Activate a subscription (creates the user/subscription on `provider` if new, or reactivates it if it was previously canceled) |
| DELETE | `/{subscriptionCode}/cancellation` | Cancel a subscription |
| POST | `/{subscriptionCode}/block` | Block a subscription (local-only, no call to `provider`) |
| POST | `/{subscriptionCode}/unblock` | Unblock a subscription (local-only, no call to `provider`) |
| PUT | `/{subscriptionCode}` | Update the contact info (email/phone) tied to a subscription |

> There's a single `activate` endpoint, not a separate `activate`/`reactivate` pair - the service
> decides which behavior applies based on whether the user already has that subscription and in
> what state.

### API docs (Swagger / OpenAPI)

With the app running:

- Swagger UI: `http://localhost:8081/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8081/v3/api-docs`

### Error responses

`RestExceptionHandler` maps domain and partner-integration exceptions to HTTP status codes:

| Status | When |
|---|---|
| 400 | Invalid request body, or `provider` rejected the data it was sent (`PartnerInvalidDataException`) |
| 404 | User or subscription not found locally, or not found on `provider` (`PartnerUserNotFoundException`) |
| 409 | Subscription already has that state locally, or `provider` reports a conflict (already exists / already has subscription / already canceled) |
| 502 | Generic failure talking to `provider` (`PartnerIntegrationException`) |
| 500 | Anything else, including an unauthorized response from `provider` |

## Observability

- Actuator endpoints exposed: `info`, `health`, `metrics`, `prometheus`
- Prometheus scrapes `consumer` and stores metrics; Grafana is available to build dashboards on top
- Logs go to the console and to `logs/consumer.log` as JSON (Logstash layout); Fluent Bit tails that file and ships it to Elasticsearch, browsable in Kibana

## Tests

```bash
./gradlew test
```

- `SubscriptionsControllerTest` - controller unit tests
- `*RepositoryTest` - JPA repository tests
- `SubscriptionsControllerIT` - full integration test (`@SpringBootTest`, random port, embedded
  test database); `ProviderSubscriptionsClient` and `ProviderTokenClient` are mocked with
  `@MockitoBean` so it never actually calls a real `provider` instance

## Known issues / open items

- **No token caching**: a new bearer token is requested from `provider` on every single
  provider-facing call. Fine for now, but worth caching the token until shortly before it expires
  once call volume grows.
- **`PROVIDER_CLIENT_ID`/`PROVIDER_CLIENT_SECRET` have no default**: intentional for not baking a
  secret into the repo, but there's no fallback and no clear startup error pointing at *which* variable is
  missing.
