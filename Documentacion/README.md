# KinoStats

App Android para registrar y estadistizar películas vistas. Backend REST en Spring Boot + cliente móvil en Jetpack Compose.

> **Estado actual:** la app usa datos mock locales. La integración con el backend está en desarrollo (ver `retrotest/`).

---

## Stack

| Capa | Tecnología |
|------|-----------|
| Backend | Java 21 · Spring Boot 4.0 · PostgreSQL · Maven |
| Frontend | Kotlin · Jetpack Compose · Android API 26+ |
| APIs externas | TMDB · SendGrid |

---

## Backend

### Prerrequisitos

- Java 21
- PostgreSQL 14+
- API key de [TMDB](https://themoviedb.org/settings/api)
- API key de [SendGrid](https://sendgrid.com) (opcional, para recuperación de contraseña)

### Configuración

1. Crea la base de datos:
   ```sql
   CREATE DATABASE "KinoStatsDBTest";
   ```

2. Completa `src/main/resources/application.properties`:
   ```properties
   spring.datasource.username=tu_usuario
   spring.datasource.password=tu_contraseña
   tmdb.api.key=tu_clave_tmdb
   sendgrid.api-key=tu_clave_sendgrid
   sendgrid.from-email=tu_email
   ```

3. Ejecuta:
   ```bash
   ./mvnw spring-boot:run        # Linux/macOS
   mvnw.cmd spring-boot:run      # Windows
   ```

El servidor levanta en `http://localhost:8080`. Las tablas se crean automáticamente.

### Verificar

```
GET /api/v1/users/welfarecheck  →  "I Doing Oke"
```

---

## App Android

### Prerrequisitos

- Android Studio Hedgehog+
- Emulador o dispositivo físico con Android 8.0+

### Ejecutar

1. Abre el proyecto en Android Studio (`File → Open`).
2. Espera la sincronización de Gradle.
3. Presiona **Run ▶**.

Para conectar al backend, edita `retrotest/client.kt` y actualiza la `BASE_URL`.  
En emulador usa `http://10.0.2.2:8080` en lugar de `localhost`.

---

## Endpoints principales

| Método | Ruta | Descripción |
|--------|------|-------------|
| `GET` | `/api/v1/auth/login` | Autenticación (HTTP Basic) |
| `POST` | `/api/v1/users/add` | Registrar usuario |
| `GET` | `/api/v1/logs/all/{userId}` | Logs de un usuario |
| `POST` | `/api/v1/logs/add` | Registrar película vista |
| `GET` | `/api/v1/movies/search/{query}` | Buscar en TMDB |
| `POST` | `/api/v1/stats/get` | Calcular estadísticas |
| `POST` | `/api/auth/forgot-password` | Solicitar reset de contraseña |
| `POST` | `/api/auth/reset-password` | Restablecer contraseña |

---

## Variables de entorno (producción)

```bash
export DB_USERNAME=...
export DB_PASSWORD=...
export TMDB_API_KEY=...
export SENDGRID_API_KEY=...
export SENDGRID_FROM_EMAIL=...

./mvnw spring-boot:run -Dspring-boot.run.profiles=prod
```
