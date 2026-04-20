# 🎰 Stoloto VIP Rooms

Высоконагруженная платформа для проведения игр в закрытых комнатах с real-time механиками.

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.java.net/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![React](https://img.shields.io/badge/React-18-blue.svg)](https://reactjs.org/)
[![Redis](https://img.shields.io/badge/Redis-7-red.svg)](https://redis.io/)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15-blue.svg)](https://www.postgresql.org/)

## 🚀 Быстрый старт

### Запуск одной командой

```bash
./run.sh
```

Скрипт автоматически:
- Определит доступность Docker
- Запустит все сервисы (PostgreSQL, Redis, Backend, Frontend)
- Дождётся готовности системы

### После запуска

| Сервис | URL | Описание |
|--------|-----|----------|
| Frontend | http://localhost:3000 | Веб-интерфейс |
| Backend API | http://localhost:8080/api | REST API |
| Swagger UI | http://localhost:8080/swagger-ui.html | Документация API |

### Другие команды

```bash
./run.sh --docker    # Принудительный запуск через Docker
./run.sh --stop      # Остановить все сервисы
./run.sh --clean     # Остановить и удалить данные
./run.sh --help      # Показать справку
```

## 📋 Требования

Для запуска в Docker режиме:
- Docker 20+
- Docker Compose v2+

Для Native режима (не рекомендуется):
- Java 21
- Maven 3.8+
- Node.js 20+
- PostgreSQL 15
- Redis 7

## 🏗 Архитектура

```
┌─────────────┐     ┌─────────────┐
│   Frontend  │◄───►│   Backend   │
│ React + Vite│ WS  │ Spring Boot │
└─────────────┘     └──────┬──────┘
                           │
              ┌────────────┼────────────┐
              ▼            ▼            ▼
         ┌────────┐  ┌────────┐  ┌─────────┐
         │ Redis  │  │ Postgres│  │Scheduler│
         │ Pub/Sub│  │   DB   │  │ Timers  │
         └────────┘  └────────┘  └─────────┘
```

## 🎮 Игровая логика

### Типы комнат

| Тип | Мин. ставка | Макс. ставка | Цвет |
|-----|-------------|--------------|------|
| 🥉 Bronze | 100 | 1,000 | Бронзовый |
| 🥇 Gold | 1,000 | 10,000 | Золотой |
| 💎 Diamond | 10,000 | 100,000 | Алмазный |

### Процесс игры

1. **Создание комнаты** - пользователь выбирает параметры или система подбирает автоматически
2. **Резервирование средств** - ставка блокируется на балансе
3. **Ожидание (60 сек)** - таймер, заполнение ботами до 10 игроков
4. **Покупка буста** (опционально) - +% к вероятности победы за бонусные баллы
5. **Определение победителя** - взвешенный RNG с учётом ставок и бустов
6. **Выплата выигрыша** - автоматическое начисление

### Математика бустов

```
Вес игрока = Ставка × (1 + ПроцентБуста)
Вероятность победы = Вес игрока / Сумма весов всех участников
```

Пример:
- Игрок A: ставка 1000, без буста → вес 1000
- Игрок B: ставка 1000, буст +20% → вес 1200
- Вероятность A: 1000/2200 = 45.5%
- Вероятность B: 1200/2200 = 54.5%

## 🔒 Безопасность

### Provably Fair RNG

Каждый раунд использует криптографически стойкий генератор:

```
RNG Seed = HMAC_SHA256(ServerSeed + ClientSeed + Nonce)
```

- **ServerSeed** - генерируется сервером, хэшируется перед раундом
- **ClientSeed** - опционально предоставляется клиентом
- **Nonce** - инкрементируемый счётчик

После раунда ServerSeed раскрывается для проверки честности.

### Аудит

Все действия логируются в immutable таблицу `audit_logs`:
- Вход/выход пользователей
- Создание комнат
- Размещение ставок
- Покупка бустов
- Результаты раундов
- Изменения баланса

### Защита транзакций

- Double-entry bookkeeping (двойная запись)
- Idempotency ключи для предотвращения дублирования
- Статусы: PENDING → COMPLETED/ROLLED_BACK
- Автоматические откаты по таймауту

## 📊 Масштабируемость

Архитектура готова к нагрузке 100k+ пользователей:

- **Stateless Backend** - горизонтальное масштабирование
- **Redis Pub/Sub** - распределённые real-time события
- **Sharded каналы** - `rooms:{id}:events`
- **Connection pooling** - оптимизированные подключения к БД

## 🛠 Технологии

### Backend
- Java 21
- Spring Boot 3.2
- Spring Security + JWT
- Spring WebSocket
- Spring Data JPA
- Liquibase
- Redisson (Redis client)

### Frontend
- React 18 + TypeScript
- Vite
- Zustand (state management)
- Axios
- Custom WebSocket hook

### Infrastructure
- PostgreSQL 15
- Redis 7
- Docker & Docker Compose
- Nginx (production)

## 📁 Структура проекта

```
/workspace/
├── run.sh                    # Скрипт запуска
├── README.md                 # Этот файл
├── backend/                  # Spring Boot приложение
│   ├── src/main/java/
│   │   ├── api/             # REST контроллеры
│   │   ├── service/         # Бизнес-логика
│   │   ├── domain/          # JPA entities
│   │   ├── realtime/        # WebSocket handlers
│   │   └── scheduler/       # Таймеры
│   └── resources/
│       └── db/changelog/    # Liquibase миграции
├── frontend/                 # React приложение
│   └── src/
│       ├── auth/            # Страницы аутентификации
│       ├── lobby/           # Лобби комнат
│       ├── game/            # Игровой экран
│       ├── user/            # Личный кабинет
│       └── admin/           # Админ-панель
└── infra/
    ├── docker-compose.yml   # Конфигурация Docker
    └── docker/
        ├── Dockerfile.backend
        └── Dockerfile.frontend
```

## 🧪 Тестирование

### Backend тесты

```bash
cd backend
./mvnw test
```

### Frontend тесты

```bash
cd frontend
npm test
```

### Load тесты

Рекомендуемые инструменты:
- k6 - для HTTP API
- wsk6 - для WebSocket
- Apache JMeter - для комплексных сценариев

## 📝 Лицензия

Proprietary. Все права защищены.

## 📞 Контакты

Для вопросов по интеграции игр обращайтесь к команде архитектуры.
