# MindShift — Anxiety Tracker

Prueba técnica: aplicación móvil para medir ansiedad de pacientes.

## Stack

| Capa | Tecnología |
|---|---|
| Backend | NestJS + TypeORM + MySQL + Bull Queue (Redis) + JWT |
| Mobile | Kotlin + Jetpack Compose + Room + WorkManager + Hilt + Retrofit |

---

## Arquitectura

```
[App Android]
     │  Guarda clics offline en Room (synced=false)
     │  WorkManager sync cada 15 min (o manual)
     ▼
POST /api/clicks/sync ──► Bull Queue "clicks-sync"
                                    │
                              Job processor
                              (chunks de 100)
                                    │
                                    ▼
                              MySQL (clicks table)
```

### Decisiones de diseño

**Cola de mensajes (NestJS Bull)**
Se eligió `@nestjs/bull` + Redis. El endpoint `POST /clicks/sync` no hace insert directo a la base de datos: encola un job con todos los clics del usuario. El processor (`ClicksProcessor`) los procesa en **chunks de 100** con bulk insert via TypeORM `createQueryBuilder().insert()`. Esto permite:
- Respuesta inmediata al cliente (no espera el insert)
- Bulk inserts eficientes en lugar de N inserts individuales
- Desacoplamiento entre recepción y persistencia

**Modo offline (Android)**
Los clics se guardan primero en Room (`synced=false`). La sincronización ocurre en dos momentos:
- **Manual:** botón "Sincronizar" en la pantalla principal
- **Automático:** WorkManager con `PeriodicWorkRequest` de 15 minutos (mínimo de Android) cuando hay red disponible

Después de sincronizar exitosamente, los clics se marcan `synced=true` (no se eliminan, quedan como historial local).

**Stats sin tiempo real**
Los endpoints de estadísticas y de historial de clics no se muestran en la app móvil. Existen como endpoints REST para uso de especialistas vía herramientas externas (ej: Postman, dashboard web separado).

**Advertencia de logout con clics pendientes**
Si el usuario intenta cerrar sesión con clics no sincronizados, la app muestra un diálogo de confirmación indicando cuántos clics se perderán. Si no hay clics pendientes, el logout es inmediato.

---

## Backend (NestJS)

### Setup local

```bash
cd backend

# Con Docker (MySQL + Redis + App)
docker-compose up

# O desarrollo local (requiere MySQL y Redis corriendo)
cp .env.example .env
npm install
npm run start:dev
```

### Endpoints

```
POST   /api/auth/register            Registrar paciente
POST   /api/auth/login               Iniciar sesión
POST   /api/clicks/sync              Sincronizar clics (requiere JWT)
GET    /api/clicks?page=1&limit=20   Historial de clics del usuario autenticado (requiere JWT)
GET    /api/clicks/stats             Clics agrupados por día del usuario autenticado (requiere JWT)
GET    /api/stats/top-patients       Top 10 pacientes por clics
GET    /api/stats/total              Total de clics
```

#### POST /api/auth/register
```json
// Body
{ "name": "Juan Pérez", "email": "juan@example.com", "password": "123456" }

// Response 201
{ "access_token": "eyJ...", "user": { "id": 1, "name": "Juan Pérez", "email": "juan@example.com" } }
```

#### POST /api/auth/login
```json
// Body
{ "email": "juan@example.com", "password": "123456" }

// Response 200
{ "access_token": "eyJ...", "user": { "id": 1, "name": "Juan Pérez", "email": "juan@example.com" } }
```

#### POST /api/clicks/sync
```json
// Header: Authorization: Bearer eyJ...
// Body
{ "clicks": [ { "clicked_at": "2024-01-15T14:30:00.000Z" }, { "clicked_at": "2024-01-15T14:31:05.123Z" } ] }

// Response 201
{ "message": "2 clicks encolados para procesamiento", "queued": 2 }
```

#### GET /api/clicks
```json
// Header: Authorization: Bearer eyJ...
// Query params opcionales: page (default 1), limit (default 20)

// Response 200
{
  "data": [
    { "id": 42, "clicked_at": "2026-06-01T05:10:00.000Z" },
    { "id": 41, "clicked_at": "2026-06-01T05:09:30.000Z" }
  ],
  "total": 42,
  "page": 1,
  "last_page": 3
}
```

#### GET /api/clicks/stats
```json
// Header: Authorization: Bearer eyJ...

// Response 200
[
  { "date": "2026-06-01", "count": 15 },
  { "date": "2026-05-31", "count": 27 }
]
```

#### GET /api/stats/top-patients?from=2024-01-01&to=2024-01-31
```json
{
  "data": [
    { "user_id": 5, "name": "Ana López", "email": "ana@example.com", "count": 142 },
    { "user_id": 1, "name": "Juan Pérez", "email": "juan@example.com", "count": 98 }
  ],
  "from": "2024-01-01",
  "to": "2024-01-31"
}
```

#### GET /api/stats/total?from=2024-01-01&to=2024-01-31
```json
{ "total": 8432, "from": "2024-01-01", "to": "2024-01-31" }
```

> Si no se pasan `from`/`to`, ambos endpoints usan el día actual por defecto.

### Base de datos (MySQL)

```sql
CREATE TABLE users (
  id          INT PRIMARY KEY AUTO_INCREMENT,
  name        VARCHAR(255) NOT NULL,
  email       VARCHAR(255) UNIQUE NOT NULL,
  password    VARCHAR(255) NOT NULL,
  created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE clicks (
  id          BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id     INT NOT NULL,
  clicked_at  DATETIME NOT NULL,       -- cuando ocurrió en el dispositivo
  created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,  -- cuando se guardó en DB
  INDEX idx_clicked_at (clicked_at),
  FOREIGN KEY (user_id) REFERENCES users(id)
);
```

TypeORM crea las tablas automáticamente con `synchronize: true` (modo desarrollo).

---

## Mobile (Android)

### Setup

1. Abrir la carpeta `mobile/` en Android Studio
2. Esperar sync de Gradle
3. Correr el backend localmente en puerto 3000
4. Ejecutar en emulador (la URL `10.0.2.2:3000` apunta al localhost del host)

> Para dispositivo físico, cambiar `BASE_URL` en `di/AppModule.kt` a la IP local de tu máquina.

### Flujo de la app

```
Launch
  └─► ¿Token guardado?
        ├── No  → AuthScreen (Login / Registro)
        └── Sí  → AnxietyScreen
                    ├── Botón "Tengo ansiedad" → guarda en Room (offline)
                    ├── Contador de clics pendientes
                    ├── Botón "Sincronizar" → envía al backend
                    ├── WorkManager → sync automático cada 15 min
                    └── Logout → diálogo de advertencia si hay clics pendientes
```

---

## Estructura del proyecto

```
mindshift/
├── backend/          NestJS API
│   ├── src/
│   │   ├── auth/     JWT register/login
│   │   ├── users/    Entidad User
│   │   ├── clicks/   Endpoint sync + Bull processor
│   │   └── stats/    Endpoints de estadísticas
│   └── docker-compose.yml
└── mobile/           Android App
    └── app/src/main/java/com/mindshift/anxiety/
        ├── data/     Room, Retrofit, DataStore, Repositories
        ├── di/       Hilt modules
        ├── ui/       Compose screens + theme + navigation
        ├── viewmodel/
        └── work/     SyncWorker (WorkManager)
```
