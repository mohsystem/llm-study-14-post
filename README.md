# llm-study-14-post

Spring Boot backend user-management module implemented with Gradle and H2.

## Run

```bash
./gradlew bootRun
```

## Test

```bash
./gradlew test
```

## Implemented APIs

- `POST /api/auth/register`
- `POST /api/auth/login`
- `POST /api/auth/refresh`
- `POST /api/auth/logout`
- `POST /api/auth/change-password`
- `POST /api/auth/reset-request`
- `POST /api/auth/reset-confirm`
- `POST /api/auth/recovery/encrypt`
- `PUT /api/admin/security/password-policy`
- `GET /api/admin/security/password-policy`
- `POST /api/admin/security/hash/preview`

All endpoints return JSON responses and use consistent JSON error payloads.
