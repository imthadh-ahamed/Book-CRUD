# OpenAPI / Swagger rules
- Springdoc OpenAPI version must match the chosen Spring Boot version.
- Document all endpoints, DTO fields, stock enum values, parameters, and common errors.
- Verify `/swagger-ui/index.html` and `/v3/api-docs`.
- Keep docs consistent with actual status codes and behavior.
- Do not expose entities, SQL details, stack traces, or secrets.
- Add meaningful examples and descriptions; avoid excessive annotations.
