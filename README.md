# Stoloto VIP Rooms - High-Load Gaming Platform

## 📋 О проекте

Платформа для проведения закрытых игровых сессий (VIP rooms) с real-time механиками, системой бустов, ботами и полной аудиторской проверкой всех операций.

**Ключевые особенности:**
- 🎮 **3 типа комнат**: Bronze, Gold, Diamond (разные лимиты ставок)
- 🤖 **Умные боты**: Заполняют пустые места для реалистичности
- 🚀 **Бусты**: +% к вероятности победы за бонусные баллы
- 🔒 **Provably Fair**: Аудируемый RNG с SHA-256 подписью
- ⚡ **Real-time**: WebSocket + Redis Pub/Sub для мгновенных обновлений
- 📊 **Двойная бухгалтерия**: Полный аудит всех транзакций
- 🛡️ **Безопасность**: JWT аутентификация, RBAC, rate limiting

---

## 🏗️ Архитектура

```
┌─────────────┐     WebSocket      ┌─────────────┐
│   Frontend  │◄──────────────────►│   Backend   │
│ React + Vite│   REST API         │ Spring Boot │
└─────────────┘                    └──────┬──────┘
                                          │
                    ┌─────────────────────┼─────────────────────┐
                    │                     │                     │
                    ▼                     ▼                     ▼
             ┌─────────────┐      ┌─────────────┐      ┌─────────────┘
             │   PostgreSQL│      │    Redis    │      │  Scheduler  │
             │   (JPA)     │      │  Pub/Sub    │      │  (@Scheduled)│
             │  - users    │      │  Channels:  │      │  - room     │
             │  - rooms    │      │  - rooms:*  │      │    timer    │
             │  - rounds   │      │  - rounds:* │      │  - bot      │
             │  - bets     │      │  - users:*  │      │    actions  │
             │  - audit    │      └─────────────┘      └─────────────┘
             └─────────────┘
```

---

## 📁 Структура проекта

```
/workspace
├── backend/                          # Spring Boot 3 + Java 17/21
│   ├── src/main/java/com/stoloto/vip
│   │   ├── api/                      # REST контроллеры
│   │   │   ├── auth/                 # Аутентификация (login, register)
│   │   │   ├── room/                 # Комнаты (join, leave, boost)
│   │   │   ├── user/                 # Профиль, баланс, история
│   │   │   ├── admin/                # Админ-панель
│   │   │   └── dto/                  # Request/Response DTO
│   │   ├── domain/                   # JPA Entities
│   │   ├── repository/               # Spring Data Repositories
│   │   ├── service/                  # Бизнес-логика
│   │   │   ├── game/                 # WinnerService, BoostCalculator
│   │   │   ├── balance/              # Транзакции, холдирование
│   │   │   ├── security/             # JWT, RBAC
│   │   │   └── audit/                # Аудит событий
│   │   ├── realtime/                 # WebSocket + Redis
│   │   │   ├── handler/              # Обработчики WS событий
│   │   │   └── redis/                # Pub/Sub publisher
│   │   ├── bot/                      # Логика ботов
│   │   ├── scheduler/                # Таймеры комнат и раундов
│   │   ├── rng/                      # Provably Fair RNG
│   │   └── config/                   # Конфигурации Spring
│   └── resources/
│       ├── db/changelog/             # Liquibase миграции
│       └── application.yml           # Конфигурация
│
├── frontend/                         # React + Vite + TypeScript
│   ├── src/
│   │   ├── pages/                    # Страницы приложения
│   │   │   ├── auth/                 # Login, Register
│   │   │   ├── lobby/                # Список комнат
│   │   │   ├── game/                 # Игровой экран
│   │   │   ├── profile/              # Кабинет пользователя
│   │   │   └── admin/                # Админ-панель
│   │   ├── components/               # UI компоненты
│   │   ├── services/                 # API клиенты
│   │   ├── hooks/                    # Custom React hooks
│   │   └── store/                    # State management
│   └── public/
│
├── infra/                            # Docker инфраструктура
│   ├── docker-compose.yml            # App, DB, Redis
│   └── nginx/                        # Reverse proxy
│
└── docs/                             # Документация
    ├── API_SPEC.md                   # OpenAPI спецификация
    └── SECURITY_AUDIT.md             # Описание безопасности
```

---

## 🚀 Быстрый старт

### Требования
- Java 17+
- Node.js 18+
- Docker & Docker Compose

### 1. Запуск инфраструктуры

```bash
cd infra
docker-compose up -d
```

Это запустит:
- PostgreSQL (порт 5432)
- Redis (порт 6379)
- pgAdmin (порт 5050) - опционально

### 2. Запуск Backend

```bash
cd backend
./mvnw spring-boot:run
```

Backend доступен на: `http://localhost:8080`

### 3. Запуск Frontend

```bash
cd frontend
npm install
npm run dev
```

Frontend доступен на: `http://localhost:5173`

---

## 📡 API Endpoints

### Аутентификация (`/api/auth`)

| Метод | Endpoint | Описание |
|-------|----------|----------|
| POST | `/register` | Регистрация нового пользователя |
| POST | `/login` | Вход в систему |
| POST | `/refresh` | Обновление JWT токена |

### Комнаты (`/api/rooms`)

| Метод | Endpoint | Описание |
|-------|----------|----------|
| GET | `/` | Список комнат (фильтры: type, status) |
| GET | `/{roomId}` | Информация о комнате |
| POST | `/` | Создать комнату (admin) |
| POST | `/{roomId}/join` | Присоединиться к комнате |
| POST | `/{roomId}/boost` | Купить буст |
| POST | `/{roomId}/leave` | Покинуть комнату |

### Пользователь (`/api/user`)

| Метод | Endpoint | Описание |
|-------|----------|----------|
| GET | `/profile` | Профиль пользователя |
| GET | `/balance` | Текущий баланс |
| GET | `/transactions` | История транзакций |
| GET | `/history` | История игр |

### Админка (`/api/admin`) - требуется роль ADMIN

| Метод | Endpoint | Описание |
|-------|----------|----------|
| GET | `/rooms` | Все комнаты |
| POST | `/rooms/{id}/start` | Принудительный старт |
| POST | `/rooms/{id}/complete` | Принудительное завершение |
| GET | `/audit` | Логи аудита (фильтры) |
| GET | `/audit/export` | Экспорт логов |

---

## 🎮 Игровая логика

### Жизненный цикл комнаты

1. **Создание**: Админ или система создаёт комнату с параметрами (тип, мин/макс ставка, вместимость).
2. **Ожидание (60 сек)**: 
   - Игроки присоединяются через `/join`
   - Ставка резервируется на балансе
   - Боты заполняют пустые места (с задержкой 100-600ms)
   - Игроки могут купить буст
3. **Старт раунда (30 сек)**:
   - Ставки фиксируются
   - RNG определяет победителя (взвешенный случайный выбор)
   - Формула веса: `Weight = Bet * (1 + Boost%)`
4. **Завершение**:
   - Победитель получает приз (пул всех ставок)
   - Выигрыш бота уходит в казино
   - Транзакции коммитятся, логи записываются
5. **Откат (если ошибка)**:
   - Все резервы разблокируются
   - Статус транзакций → `ROLLED_BACK`

### Типы комнат

| Тип | Мин. ставка | Макс. ставка | Входной взнос |
|-----|-------------|--------------|---------------|
| 🥉 Bronze | 100 | 1,000 | 0 |
| 🥇 Gold | 1,000 | 10,000 | 100 |
| 💎 Diamond | 10,000 | 100,000 | 1,000 |

### Бусты

- **Механика**: Покупается за бонусные баллы до начала раунда
- **Эффект**: +% к весу в RNG (например, +5% = вес умножается на 1.05)
- **Ограничение**: 1 буст на игру

---

## 🔐 Безопасность

### Provably Fair RNG

Каждый раунд использует криптографически стойкий генератор:

```
result = HMAC_SHA256(serverSeed + clientSeed + nonce)
```

- `serverSeed`: Генерируется сервером, хранится хэшированным до конца раунда
- `clientSeed`: Опционально предоставляется клиентом
- `nonce`: Уникальный номер раунда

После раунда `serverSeed` раскрывается в логах аудита для проверки честности.

### Двойная бухгалтерия

Все изменения баланса записываются как транзакции:

```java
Transaction {
    type: "BET" | "WIN" | "BOOST" | "ROLLBACK",
    status: "PENDING" | "COMPLETED" | "ROLLED_BACK",
    amount: Long,
    balanceAfter: Long,
    idempotencyKey: UUID // Защита от дублирования
}
```

### RBAC (Role-Based Access Control)

| Роль | Доступ |
|------|--------|
| USER | Игры, профиль, история |
| ADMIN | Настройка комнат, ботов, бустов |
| SUPER_ADMIN | Всё + управление ролями, экспорт аудита |

---

## ⚡ Real-time механики

### WebSocket события

**Client → Server:**
```json
{ "type": "JOIN_ROOM", "payload": { "roomId": 1, "betAmount": 500 } }
{ "type": "BUY_BOOST", "payload": { "roomId": 1, "boostConfigId": 3 } }
{ "type": "LEAVE_ROOM", "payload": { "roomId": 1 } }
```

**Server → Client:**
```json
{ "type": "ROOM_STATE_UPDATE", "status": "SUCCESS", "payload": {...} }
{ "type": "ROUND_STARTED", "status": "BROADCAST", "payload": {...} }
{ "type": "ROUND_RESULT", "status": "BROADCAST", "payload": {...} }
{ "type": "ERROR", "status": "ERROR", "payload": { "message": "..." } }
```

### Redis каналы

| Канал | Назначение |
|-------|------------|
| `rooms:{id}:state` | Состояние комнаты (таймер, игроки) |
| `rooms:{id}:round:start` | Начало раунда |
| `rooms:{id}:round:end` | Результаты раунда |
| `users:{id}:balance` | Обновления баланса |
| `users:{id}:notifications` | Push-уведомления |

---

## 📊 Мониторинг и логи

### Уровни логирования

| Уровень | Что логируется | Хранение |
|---------|----------------|----------|
| ERROR | Ошибки системы, сбои RNG | 90 дней |
| WARN | Подозрительные ставки, таймауты | 90 дней |
| INFO | Действия пользователей | 30 дней |
| AUDIT | Финансовые операции, изменения конфигов | 365 дней (immutable) |

### Аудит

Таблица `audit_logs` содержит:
- Тип события (LOGIN, BET_PLACED, ROUND_COMPLETED, etc.)
- Actor (USER, BOT, SYSTEM, ADMIN)
- Context (roomId, roundId)
- Payload (детали действия в JSON)
- Signature (SHA256 для целостности)

---

## 🧪 Тестирование

### Запуск тестов

```bash
cd backend
./mvnw test
```

### Seed данные

При старте загружаются:
- 3 тестовых пользователя (user1, user2, admin)
- 3 комнаты (Bronze, Gold, Diamond)
- 5 конфигураций ботов
- 2 конфигурации бустов

**Тестовые креды:**
- User: `user@test.com` / `password`
- Admin: `admin@test.com` / `admin`

---

## 📈 Масштабируемость

### Горизонтальное масштабирование

1. **Stateless Backend**: Любое количество инстансов Spring Boot
2. **Redis Cluster**: Шардинг каналов Pub/Sub
3. **PostgreSQL Read Replicas**: Для тяжёлых SELECT запросов

### Целевые метрики

- **Concurrent users**: 100,000+
- **Rooms per second**: 1,000+
- **WebSocket latency**: < 100ms
- **RPS (REST)**: 10,000+

---

## 🔧 Конфигурация

### Переменные окружения (backend)

```yaml
SPRING_DATASOURCE_URL: jdbc:postgresql://localhost:5432/stoloto_vip
SPRING_DATASOURCE_USERNAME: postgres
SPRING_DATASOURCE_PASSWORD: postgres
SPRING_REDIS_HOST: localhost
SPRING_REDIS_PORT: 6379
JWT_SECRET: your-secret-key-min-32-chars
JWT_EXPIRATION: 3600000  # 1 hour
REFRESH_TOKEN_EXPIRATION: 604800000  # 7 days
```

---

## 📝 Лицензия

Проект разработан для интеграции в экосистему Столото.

---

## 👥 Контакты

По вопросам архитектуры и интеграции обращаться к команде разработки.
