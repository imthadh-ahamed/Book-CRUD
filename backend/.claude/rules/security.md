# Security rules
- Never commit credentials or real tokens.
- Use environment variables and `.env.example` placeholders.
- Avoid logging secrets or sensitive request fields.
- Use least-privilege SQL Server credentials where feasible.
- Keep error messages safe and avoid permissive CORS without a reason.
- Document absence of authentication if no authentication is implemented.
