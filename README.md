# 🎰 Stoloto VIP Rooms - High-Load Gaming Platform

[![Java](https://img.shields.io/badge/Java-17-blue.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18-blue.svg)](https://reactjs.org/)
[![Redis](https://img.shields.io/badge/Redis-7-red.svg)](https://redis.io/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)

Платформа для проведения игр в закрытых VIP комнатах с real-time механиками, системой бустов, ботов и полным аудитом всех действий.

---

## 🚀 Быстрый старт

### Запуск одной командой (Docker)

```bash
./run.sh
```

**После запуска:**
- 🌐 Frontend: http://localhost:3000
- 🔧 Backend: http://localhost:8080
- 📖 Swagger API Docs: http://localhost:8080/swagger-ui.html

### Native запуск (локально)

```bash
./run-native.sh
```

Требует предварительно установленных PostgreSQL и Redis.

---

## 📋 Оглавление

- [О проекте](#-о-проекта)
- [Архитектура](#-архитектура)
- [Игровая механика](#-игровая-механика)
- [API Документация](#-api-документация)
- [Безопасность](#-безопасность)
- [Масштабируемость](#-масштабируемость)
- [Установка и запуск](#-установка-и-запуск)
- [Конфигурация](#-конфигурация)
- [Тестирование](#-тестирование)
- [Развертывание](#-развертывание)

---

## 🎯 О проекте

**Stoloto VIP Rooms** — это высоконагруженная платформа-оркестратор для игровых механик, разрабатываемых сторонними командами.

### Ключевые возможности

✅ **Система комнат** — Bronze, Gold, Diamond с разными лимитами ставок  
✅ **Real-time gameplay** — WebSocket + Redis Pub/Sub для мгновенных обновлений  
✅ **Бусты** — покупка за бонусные баллы, +% к вероятности победы  
✅ **Боты** — заполнение комнат для реалистичности, выигрыш уходит в казино  
✅ **Аудит** — полное логирование всех действий с криптографической подписью  
✅ **RNG** — Provably Fair генератор случайных чисел  
✅ **RBAC** — разграничение доступа (User, Admin, Super Admin)  

### Технологический стек

| Компонент | Технология |
|-----------|------------|
| **Backend** | Java 17, Spring Boot 3.2, Spring Security, Spring Data JPA |
| **Frontend** | React 18, TypeScript, Vite, Zustand, CSS Modules |
| **Базы данных** | PostgreSQL 15, Redis 7 |
| **Real-time** | WebSocket (STOMP), Redis Pub/Sub |
| **Миграции** | Liquibase |
| **Контейнеризация** | Docker, Docker Compose |
| **Мониторинг** | Spring Boot Actuator, Prometheus (опционально) |

---

## 🏗 Архитектура

```
┌─────────────┐     WebSocket      ┌─────────────┐
│   Frontend  │◄──────────────────►│   Backend   │
│  (React)    │   SSE fallback     │ (Spring)    │
└─────────────┘                    └──────┬──────┘
                                          │
                         ┌────────────────┼────────────────┐
                         │                │                │
                         ▼                ▼                ▼
                  ┌─────────────┐ ┌─────────────┐ ┌─────────────┐
                  │   Redis     │ │ PostgreSQL  │ │  Scheduler  │
                  │  Pub/Sub    │ │   (JPA)     │ │  (@Scheduled)│
                  │  Channels:  │ │  Tables:    │ │             │
                  │  - rooms:*  │ │  - users    │ │  - room     │
                  │  - rounds:* │ │  - rooms    │ │    timer    │
                  │  - users:*  │ │  - rounds   │ │  - bot      │
                  └─────────────┘ └─────────────┘ └─────────────┘
```

### Структура проекта

```
/workspace/
├── backend/                    # Spring Boot приложение
│   ├── api/                    # REST контроллеры
│   ├── service/                # Бизнес-логика
│   ├── domain/                 # JPA сущности
│   ├── realtime/               # WebSocket + Redis
│   ├── scheduler/              # Таймеры и планировщики
│   └── rng/                    # RNG сервис
├── frontend/                   # React приложение
│   ├── src/auth/               # Аутентификация
│   ├── src/lobby/              # Лобби комнат
│   ├── src/game/               # Игровой процесс
│   ├── src/user/               # Личный кабинет
│   └── src/admin/              # Админ-панель
├── infra/                      # Docker конфигурации
├── run.sh                      # Скрипт запуска (Docker)
├── run-native.sh               # Скрипт запуска (Native)
└── docs/                       # Документация
```

---

## 🎮 Игровая механика

### Типы комнат

| Комната | Мин. ставка | Макс. ставка | Цвет |
|---------|-------------|--------------|------|
| 🥉 Bronze | 10 | 100 | #CD7F32 |
| 🥇 Gold | 100 | 1,000 | #FFD700 |
| 💎 Diamond | 1,000 | 10,000 | #B9F2FF |

### Процесс игры

1. **Выбор комнаты** — пользователь выбирает комнату или создает новую с параметрами
2. **Резервирование средств** — ставка резервируется при входе в комнату
3. **Таймер ожидания** — 60 секунд на сбор игроков
4. **Заполнение ботами** — пустые места автоматически заполняются ботами (макс. 10 игроков)
5. **Покупка буста** — опционально, за бонусные баллы (+% к вероятности победы)
6. **Начало раунда** — определение победителя через взвешенный RNG
7. **Распределение выигрыша** — победитель получает пул за вычетом комиссии
8. **Аудит** — запись всех событий в immutable лог

### Математика выигрыша

**Формула веса игрока:**
```
Weight = Bet × (1 + BoostBonus)
```

Где:
- `Bet` — сумма ставки
- `BoostBonus` — процент буста (например, 0.05 для +5%)

**Вероятность победы:**
```
P(win) = Weight_player / Σ(Weight_all_players)
```

**Пример:**
- Игрок A: ставка 100, буст 5% → вес = 105
- Игрок B: ставка 200, без буста → вес = 200
- Бот C: ставка 100, без буста → вес = 100
- **Общий пул весов:** 405
- **Шанс игрока A:** 105/405 ≈ 25.9%

### Бусты

| Буст | Стоимость (баллы) | Бонус к победе |
|------|-------------------|----------------|
| Small | 50 | +5% |
| Medium | 100 | +10% |
| Large | 200 | +20% |

**Важно:** Буст можно купить только один за раунд, до начала игры.

---

## 📡 API Документация

### Основные эндпоинты

#### Аутентификация
```http
POST /api/auth/register
POST /api/auth/login
POST /api/auth/refresh
```

#### Комнаты
```http
GET  /api/rooms                    # Список комнат
GET  /api/rooms/{id}               # Информация о комнате
POST /api/rooms                    # Создание комнаты
POST /api/rooms/{id}/join          # Присоединение
POST /api/rooms/{id}/boost         # Покупка буста
POST /api/rooms/{id}/leave         # Выход из комнаты
```

#### Пользователь
```http
GET /api/user/profile              # Профиль
GET /api/user/balance              # Баланс
GET /api/user/transactions         # История транзакций
GET /api/user/history              # История игр
```

#### Администрирование
```http
GET    /api/admin/rooms            # Все комнаты
POST   /api/admin/rooms/{id}/start # Принудительный старт
POST   /api/admin/rooms/{id}/complete # Завершение
GET    /api/admin/audit            # Логи аудита
GET    /api/admin/audit/export     # Экспорт логов
```

### WebSocket события

**Client → Server:**
```json
{ "type": "JOIN_ROOM", "roomId": "r_123" }
{ "type": "LEAVE_ROOM", "roomId": "r_123" }
{ "type": "PLACE_BET", "roomId": "r_123", "amount": 100 }
{ "type": "BUY_BOOST", "roomId": "r_123", "boostId": 2 }
```

**Server → Client:**
```json
{ "type": "ROOM_STATE_UPDATE", "payload": {...} }
{ "type": "ROUND_STARTED", "payload": { "roundId": "rnd_456" } }
{ "type": "ROUND_RESULT", "payload": { "winners": [...] } }
{ "type": "BALANCE_UPDATED", "payload": { "balance": 5000 } }
```

**Swagger UI:** http://localhost:8080/swagger-ui.html

---

## 🔒 Безопасность

### Реализованные меры

✅ **JWT Authentication** — токены с expiration и refresh  
✅ **RBAC** — роли USER, ADMIN, SUPER_ADMIN  
✅ **Rate Limiting** — защита от DDoS и брутфорса  
✅ **Idempotency** — защита от дублирования транзакций  
✅ **Provably Fair RNG** — аудируемый генератор случайных чисел  
✅ **Double-Entry Bookkeeping** — полная трассируемость финансов  
✅ **Immutable Audit Log** — криптографическая подпись записей  
✅ **SQL Injection Protection** — Spring Data JPA  
✅ **XSS Protection** — React auto-escaping  

### Provably Fair RNG

Каждый раунд использует криптографически стойкий генератор:

1. **Перед раундом:** Генерируется `ServerSeed` (хранится хэшированным)
2. **Во время раунда:** Результат = `HMAC_SHA256(ServerSeed + ClientSeed + Nonce)`
3. **После раунда:** `ServerSeed` раскрывается для проверки

Любой пользователь может воспроизвести результат раунда самостоятельно.

---

## 📈 Масштабируемость

### Целевые метрики

- **100,000+** одновременных пользователей
- **<100ms** задержка WebSocket сообщений
- **10,000+** операций в секунду
- **99.9%** uptime

### Архитектурные решения

✅ **Stateless Backend** — горизонтальное масштабирование без состояния  
✅ **Redis Pub/Sub** — шардирование real-time событий  
✅ **Connection Pooling** — оптимизированные подключения к БД  
✅ **Async Processing** — асинхронная обработка аудита  
✅ **Caching** — кэширование часто запрашиваемых данных  

---

## 🛠 Установка и запуск

### Требования

| Компонент | Версия | Для режима |
|-----------|--------|------------|
| Docker | 20.10+ | Docker |
| Docker Compose | V2+ | Docker |
| Java | 17+ | Native |
| Node.js | 18+ | Native |
| PostgreSQL | 15+ | Native |
| Redis | 7+ | Native |

### Docker режим (рекомендуется)

```bash
# Запуск
./run.sh

# Остановка
./run.sh --stop

# Очистка данных
./run.sh --clean
```

### Native режим

```bash
# Автоматический запуск
./run-native.sh

# Или вручную:
# 1. Запустить PostgreSQL и Redis
# 2. Backend:
cd backend && ./mvnw spring-boot:run

# 3. Frontend (в другом терминале):
cd frontend && npm install && npm run dev
```

**Подробная инструкция:** см. [RUN_INSTRUCTIONS.md](RUN_INSTRUCTIONS.md)

---

## ⚙️ Конфигурация

### Переменные окружения (Backend)

```yaml
SPRING_PROFILES_ACTIVE: dev
SPRING_DATASOURCE_URL: jdbc:postgresql://localhost:5432/stoloto_vip
SPRING_DATASOURCE_USERNAME: stoloto_user
SPRING_DATASOURCE_PASSWORD: stoloto_password_dev
SPRING_REDIS_HOST: localhost
SPRING_REDIS_PORT: 6379
JWT_SECRET: your-secret-key-min-32-chars
JWT_EXPIRATION_MS: 86400000
SERVER_PORT: 8080
```

### Переменные окружения (Frontend)

```env
VITE_API_BASE_URL=http://localhost:8080/api
VITE_WS_URL=ws://localhost:8080/ws
NODE_ENV=development
```

---

## 🧪 Тестирование

### Unit тесты

```bash
cd backend
./mvnw test
```

### Integration тесты

```bash
cd backend
./mvnw verify -Pintegration
```

### Load тесты

```bash
# k6 пример
k6 run load-test.js
```

---

## 🚀 Развертывание

### Production чеклист

- [ ] Изменить `JWT_SECRET` на уникальный ключ
- [ ] Включить HTTPS/TLS
- [ ] Настроить external secrets (Vault/AWS Secrets Manager)
- [ ] Включить rate limiting
- [ ] Настроить мониторинг и алертинг
- [ ] Настроить backup баз данных
- [ ] Провести security audit

### Production запуск

```bash
docker compose -f infra/docker-compose.prod.yml up -d
```

---

## 📊 Мониторинг

### Spring Boot Actuator

```bash
# Health check
curl http://localhost:8080/actuator/health

# Metrics
curl http://localhost:8080/actuator/metrics

# Prometheus endpoint
curl http://localhost:8080/actuator/prometheus
```

---

## 👥 Тестовые данные

После первого запуска создаются:

### Пользователи
- **admin** / admin@stoloto.ru / Admin123! (ADMIN)
- **user1** / user1@stoloto.ru / User123! (USER)
- **user2** / user2@stoloto.ru / User123! (USER)

### Комнаты
- 🥉 Bronze Room (ставка: 10-100)
- 🥇 Gold Room (ставка: 100-1000)
- 💎 Diamond Room (ставка: 1000-10000)

### Боты
- Bot_Alex, Bot_Maria, Bot_Dmitry, Bot_Olga, Bot_Sergey

---

## 📄 Лицензия

Proprietary. Все права защищены.

---

## 📞 Контакты

При возникновении проблем создайте issue в репозитории проекта.

**Документация:**
- [RUN_INSTRUCTIONS.md](RUN_INSTRUCTIONS.md) — Инструкция по запуску
- [PROJECT_DOCUMENTATION.md](PROJECT_DOCUMENTATION.md) — Полная архитектура
