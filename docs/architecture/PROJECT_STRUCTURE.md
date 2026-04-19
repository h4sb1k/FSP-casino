# Stoloto VIP Rooms - Структура проекта

## 📁 Обзор структуры

```
stoloto-vip-rooms/
├── backend/                          # Spring Boot (Java 21)
│   ├── src/main/java/com/stoloto/viprooms/
│   │   ├── Application.java          # Точка входа Spring Boot
│   │   │
│   │   ├── core/                     # Доменные сущности, бизнес-логика
│   │   │   ├── domain/               # Агрегаты и сущности
│   │   │   ├── service/              # Бизнес-сервисы
│   │   │   ├── repository/           # Data access layer
│   │   │   └── validation/           # Валидаторы доменных объектов
│   │   │
│   │   ├── realtime/                 # WebSocket, Redis Pub/Sub, таймеры
│   │   │   ├── websocket/            # WebSocket конфигурация и обработчики
│   │   │   ├── redis/                # Redis конфигурация и Pub/Sub
│   │   │   ├── scheduler/            # Планировщики задач
│   │   │   └── dto/                  # DTO для real-time сообщений
│   │   │
│   │   ├── engine/                   # Игровая логика, RNG, экономика
│   │   │   ├── WinnerService.java    # Определение победителей
│   │   │   ├── BalanceService.java   # Расчёты баланса
│   │   │   ├── BoostCalculator.java  # Логика бустов
│   │   │   ├── PrizePoolService.java # Призовой фонд
│   │   │   └── rng/                  # RNG провайдер и аудит
│   │   │
│   │   ├── config/                   # Конфигурация приложения
│   │   │   ├── RoomConfig.java       # Настройки комнат
│   │   │   ├── BotConfig.java        # Настройки ботов
│   │   │   ├── EconomicRules.java    # Правила экономики
│   │   │   ├── SecurityConfig.java   # Spring Security, JWT
│   │   │   ├── CorsConfig.java       # CORS настройки
│   │   │   └── SchedulerConfig.java  # Конфигурация планировщиков
│   │   │
│   │   ├── audit/                    # Журналирование и аудит
│   │   │   ├── entity/               # Сущности аудита
│   │   │   ├── service/              # Сервисы аудита
│   │   │   └── aspect/               # AspectJ для автоматического логирования
│   │   │
│   │   ├── integration/              # Интеграции с внешними API
│   │   │   ├── stoloto/              # Заглушки для API Столото
│   │   │   └── mock/                 # Мок-сервисы для разработки
│   │   │
│   │   ├── api/                      # REST контроллеры
│   │   │   ├── auth/                 # Аутентификация и авторизация
│   │   │   ├── user/                 # Пользовательские эндпоинты
│   │   │   ├── room/                 # Управление комнатами
│   │   │   ├── game/                 # Игровые эндпоинты
│   │   │   └── admin/                # Административные эндпоинты
│   │   │
│   │   └── exception/                # Обработка исключений
│   │
│   ├── src/main/resources/
│   │   ├── application.yml           # Основная конфигурация
│   │   ├── application-dev.yml       # Dev профиль
│   │   ├── application-prod.yml      # Prod профиль
│   │   ├── db/migration/             # Liquibase миграции
│   │   └── seed/                     # Стартовые данные
│   │
│   ├── src/test/                     # Тесты
│   │   ├── java/                     # Unit и интеграционные тесты
│   │   └── resources/                # Тестовые данные
│   │
│   ├── build.gradle                  # Gradle сборка
│   └── Dockerfile                    # Docker образ backend
│
├── frontend/                         # React + Vite + TypeScript
│   ├── src/
│   │   ├── main.tsx                  # Точка входа
│   │   ├── App.tsx                   # Корневой компонент
│   │   │
│   │   ├── user/                     # Личный кабинет пользователя
│   │   │   ├── pages/                # Страницы ЛК
│   │   │   ├── components/           # Компоненты ЛК
│   │   │   └── hooks/                # Хуки ЛК
│   │   │
│   │   ├── lobby/                    # Лобби комнат
│   │   │   ├── pages/                # Страница лобби
│   │   │   ├── components/           # Компоненты лобби
│   │   │   └── hooks/                # Хуки лобби
│   │   │
│   │   ├── game/                     # Экран игровой комнаты
│   │   │   ├── pages/                # Страница игры
│   │   │   ├── components/           # Игровые компоненты
│   │   │   └── hooks/                # Игровые хуки
│   │   │
│   │   ├── admin/                    # Административная панель
│   │   │   ├── pages/                # Страницы админки
│   │   │   ├── components/           # Компоненты админки
│   │   │   └── hooks/                # Хуки админки
│   │   │
│   │   ├── auth/                     # Авторизация
│   │   │   ├── pages/                # Страницы входа/регистрации
│   │   │   ├── components/           # Формы авторизации
│   │   │   └── hooks/                # Хуки аутентификации
│   │   │
│   │   ├── shared/                   # Общие ресурсы
│   │   │   ├── ui-kit/               # UI компоненты
│   │   │   ├── layout/               # Layout компоненты
│   │   │   ├── clients/              # API и WebSocket клиенты
│   │   │   ├── hooks/                # Общие хуки
│   │   │   ├── utils/                # Утилиты
│   │   │   └── types/                # TypeScript типы
│   │   │
│   │   └── routes/                   # Маршрутизация с RBAC
│   │
│   ├── public/                       # Статические файлы
│   ├── package.json                  # Зависимости npm
│   ├── vite.config.ts                # Конфигурация Vite
│   ├── tsconfig.json                 # Конфигурация TypeScript
│   └── Dockerfile                    # Docker образ frontend
│
├── infra/                            # Инфраструктура
│   ├── docker/
│   │   ├── docker-compose.yml        # Локальная разработка
│   │   ├── docker-compose.prod.yml   # Production
│   │   └── */Dockerfile              # Dockerfile сервисов
│   │
│   ├── k8s/                          # Kubernetes manifests
│   │   ├── deployments/
│   │   ├── services/
│   │   ├── configmaps/
│   │   ├── secrets/
│   │   └── ingress/
│   │
│   └── scripts/
│       ├── deploy.sh
│       └── migrate.sh
│
├── docs/                             # Документация
│   ├── openapi/                      # OpenAPI спецификация
│   ├── architecture/                 # Архитектурные документы
│   ├── scenarios/                    # Пользовательские сценарии
│   ├── economy/                      # Правила экономики
│   └── decisions/                    # Architecture Decision Records
│
├── scripts/                          # Скрипты разработки
│   ├── seed-data/                    # Генерация тестовых данных
│   ├── load-testing/                 # Нагрузочное тестирование
│   ├── migrations/                   # Скрипты миграций
│   └── dev-setup/                    # Настройка локального окружения
│
├── .gitignore                        # Git ignore правила
├── .editorconfig                     # EditorConfig
└── README.md                         # Этот файл
```

---

## 🔑 Ключевые модули

### Backend модули

| Модуль | Назначение | Ключевые классы |
|--------|------------|-----------------|
| `core` | Доменная логика | User, Room, Round, Bet, Transaction |
| `realtime` | Real-time взаимодействие | WebSocketHandler, RedisPubSubService, RoomTimerScheduler |
| `engine` | Игровая механика | WinnerService, BalanceService, BoostCalculator |
| `audit` | Журналирование | AuditService, AuditAspect, LogExporter |
| `api` | REST API | AuthController, UserController, RoomController, GameController, Admin*Controller |

### Frontend модули

| Модуль | Назначение | Ключевые компоненты |
|--------|------------|---------------------|
| `user` | Личный кабинет | Dashboard, Balance, Bonuses, History |
| `lobby` | Лобби комнат | RoomList, RoomCard, RoomFilters |
| `game` | Игровой экран | GameRoom, BetPanel, BoostButton, RoomTimer |
| `admin` | Админ-панель | RoomsConfig, BotsConfig, AuditLogs, Analytics |
| `shared` | Общие компоненты | UI-kit, Layout, API clients, Hooks |

---

## 👥 Роли пользователей

| Роль | Описание | Доступные разделы |
|------|----------|-------------------|
| `GUEST` | Неавторизованный | Лендинг, вход, регистрация |
| `USER` | Игрок | ЛК, лобби, комнаты, ставки, история |
| `MODERATOR` | Модератор | Всё как у USER + логи комнат |
| `ADMIN` | Администратор | Настройка комнат, ботов, бустов, аудит |
| `SUPER_ADMIN` | Супер-админ | Всё + управление ролями |

---

## 📊 Основные сущности

### Domain Entities

- **User** — пользователь (баланс, бонусы, роль)
- **Room** — игровая комната (конфиг, статус, игроки)
- **Round** — раунд в комнате (время, ставки, результаты)
- **Bet** — ставка пользователя (сумма, раунд, множитель)
- **Transaction** — финансовая операция (тип, сумма, баланс)
- **Bonus** — бонусные баллы (начисление, списание, активация)
- **Bot** — бот-участник (стратегия, лимиты, активность)
- **AuditLog** — запись аудита (действие, контекст, данные)

---

## 🔄 Real-time архитектура

### WebSocket события

**Client → Server:**
- `JOIN_ROOM`, `LEAVE_ROOM`
- `PLACE_BET`, `ACTIVATE_BOOST`
- `SUBSCRIBE_ROUND`

**Server → Client:**
- `ROOM_STATE_UPDATE`
- `ROUND_STARTED`, `ROUND_ENDING`, `ROUND_RESULT`
- `BET_CONFIRMED`, `BALANCE_UPDATED`
- `ERROR`

### Redis каналы (Pub/Sub)

| Канал | Назначение |
|-------|------------|
| `rooms:{roomId}:state` | Состояние комнаты |
| `rooms:{roomId}:round:*` | События раунда |
| `rooms:{roomId}:bets` | Новые ставки |
| `users:{userId}:balance` | Обновления баланса |
| `users:{userId}:notifications` | Уведомления |
| `admin:alerts` | Системные алерты |

---

## 🛣️ Маршруты приложения

### Публичные маршруты
```
/           → Лендинг
/login      → Вход
/register   → Регистрация
```

### Маршруты пользователя (требуют авторизации)
```
/dashboard     → Личный кабинет
/profile       → Профиль
/balance       → Баланс
/bonuses       → Бонусы
/history       → История игр
/lobby         → Лобби комнат
/room/:id      → Игровая комната
```

### Маршруты администратора (требуют роль ADMIN+)
```
/admin            → Админ-дашборд
/admin/rooms      → Настройка комнат
/admin/bots       → Управление ботами
/admin/economy    → Экономика и бусты
/admin/audit      → Журналы аудита
/admin/analytics  → Аналитика
```

---

## 📝 Следующие шаги

Этот шаблон готов к поэтапной реализации. Рекомендуемый порядок:

1. **Инфраструктура** — docker-compose, базовая структура
2. **Backend Core** — доменные сущности, миграции, CRUD API
3. **Auth & Security** — JWT, RBAC, защищённые эндпоинты
4. **Real-time** — WebSocket, Redis Pub/Sub, таймеры
5. **Frontend Base** — ЛК, лобби, игровой экран
6. **Admin Panel** — конфигураторы, аудит, аналитика
7. **Polish** — анимации, оптимизация, нагрузочное тестирование

---

*Документ является живым и обновляется по мере развития проекта.*
