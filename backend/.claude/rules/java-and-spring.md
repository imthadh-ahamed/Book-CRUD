# Java and Spring rules

- Use Java 21 idioms without unnecessary cleverness.
- Use constructor injection; avoid field injection.
- Keep methods focused and classes cohesive.
- Use `BigDecimal` for money.
- Use `Optional` primarily for return values where absence is meaningful, not as entity fields or request fields.
- Validate external input with Jakarta Bean Validation and enforce important business invariants in the application/domain layer.
- Use explicit, consistent exception handling and meaningful HTTP status codes.
- Use `@Transactional` at clear application boundaries; keep transactions short.
- Do not catch exceptions only to log and swallow them.
- Avoid exposing stack traces, SQL text, secrets, or internal implementation details to API clients.
- Avoid adding security features not requested; clearly document unauthenticated endpoints.
