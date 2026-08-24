# consumer

Java + Spring Boot API skeleton, mirroring the `provider` project's structure and stack.
Generated without business logic (service impls just throw `UnsupportedOperationException("TODO: ...")`).

This service is meant to consume the `provider` API (see `provider.api.base-url` in
`application.yml`) - no HTTP client is wired up yet on purpose, that's left for you to add
(e.g. a `WebClient`/`RestClient` bean in `config/`) once you implement the service layer.

## Stack

Same as `provider`: Java 21, Spring Boot 4.1.0, Gradle (wrapper), `spring-boot-starter-webmvc`,
`spring-boot-starter-data-jpa` + MySQL, `spring-boot-starter-kafka`, Lombok,
`springdoc-openapi-starter-webmvc-ui` (Swagger UI + OpenAPI 3).

To avoid port clashes when running both APIs locally at the same time:
- `consumer` HTTP port defaults to `8081` (provider: `8080`)
- `consumer` MySQL (compose.yaml) maps to host port `3307` (provider: `3306`)
- `consumer` Kafka (compose.yaml) maps to host port `9093` (provider: `9092`)

## Package structure

Same layout as `provider`:

```
br.com.jhonatan.consumer
├── config       # @Configuration (OpenApiConfig is already here)
├── controller   # @RestController
├── service      # business logic
├── repository   # Spring Data JPA repositories
├── model        # JPA entities
├── dto          # transfer objects (request/response)
├── exception    # custom exceptions + @ControllerAdvice
└── kafka
    ├── producer
    ├── consumer
    └── config
```

## Endpoints

All under `/api/customers/{username}/subscriptions`:

| Method | Path | Description |
|---|---|---|
| GET | `/` | List the customer's subscriptions |
| POST | `/{subscription}/activate` | Activate a subscription |
| POST | `/{subscription}/cancellation` | Cancel a subscription |
| POST | `/{subscription}/reactivate` | Reactivate a subscription |
| POST | `/{subscription}/block` | Block a subscription |
| POST | `/{subscription}/unblock` | Unblock a subscription |
| PUT | `/{subscription}` | Update the contact info (email/phone) tied to a subscription |

> The paths in the original spec were inconsistent (some endpoints missing the
> `customers/{username}` prefix, `?` placeholders, one literally `api/XXXXX/update`).
> I normalized everything under the table above to keep it consistent with `provider` and
> RESTful. `username`/`subscription` are taken from the path instead of being repeated in the
> request body. Adjust the mappings in `SubscriptionsController` if you'd rather match the
> original spec exactly.

## API docs (Swagger / OpenAPI)

With the app running:

- Swagger UI: `http://localhost:8081/swagger-ui.html`
- OpenAPI JSON: `http://localhost:8081/v3/api-docs`

## Running locally (without Docker for now)

Same as `provider`: `application.yml` uses in-memory H2 and has
`spring.docker.compose.enabled: false` until Docker is set up. Just run:

```bash
./gradlew bootRun
```

## Tests

```bash
./gradlew test
```
