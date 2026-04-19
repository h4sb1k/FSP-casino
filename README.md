# FSP-casino
FSP Competition, making own casino

---

## Архитектурный шаблон проекта Stoloto VIP Rooms

### 1. Краткое понимание задачи

Разрабатывается веб-приложение для проведения закрытых игровых комнат (VIP rooms) с real-time механиками. Система включает:
- **Личные кабинеты пользователей** с балансом и бонусными баллами
- **Игровые комнаты** с таймерами, бустами, ботами
- **Административную панель** для конфигурации экономики, ботов, комнат
- **Real-time взаимодействие** через WebSocket + Redis Pub/Sub
- **Систему аудита** всех действий и транзакций

Технологический стек:
- Backend: Java 21, Spring Boot, Redis
- Frontend: React + Vite / Next.js, TypeScript
- Infra: Docker, docker-compose, k8s (target V2)

---

### 2. Предлагаемая структура проекта

```
stoloto-vip-rooms/
├── backend/                          # Spring Boot (Java 21)
│   ├── src/main/java/com/stoloto/viprooms/
│   │   ├── Application.java          # Точка входа
│   │   │
│   │   ├── core/                     # Доменные сущности, бизнес-логика
│   │   │   ├── domain/               # Агрегаты и сущности
│   │   │   │   ├── User.java
│   │   │   │   ├── Room.java
│   │   │   │   ├── Round.java
│   │   │   │   ├── Bet.java
│   │   │   │   ├── Transaction.java
│   │   │   │   ├── Bonus.java
│   │   │   │   └── Bot.java
│   │   │   │
│   │   │   ├── service/              # Бизнес-сервисы
│   │   │   │   ├── UserService.java
│   │   │   │   ├── RoomService.java
│   │   │   │   ├── RoundService.java
│   │   │   │   ├── BetService.java
│   │   │   │   └── BonusService.java
│   │   │   │
│   │   │   ├── repository/           # Data access
│   │   │   │   ├── UserRepository.java
│   │   │   │   ├── RoomRepository.java
│   │   │   │   ├── RoundRepository.java
│   │   │   │   ├── TransactionRepository.java
│   │   │   │   └── BotRepository.java
│   │   │   │
│   │   │   └── validation/           # Валидаторы доменных объектов
│   │   │       ├── BetValidator.java
│   │   │       └── RoomConfigValidator.java
│   │   │
│   │   ├── realtime/                 # WebSocket, Redis Pub/Sub, таймеры
│   │   │   ├── websocket/
│   │   │   │   ├── WebSocketConfig.java
│   │   │   │   ├── WebSocketHandler.java
│   │   │   │   └── SessionManager.java
│   │   │   │
│   │   │   ├── redis/
│   │   │   │   ├── RedisConfig.java
│   │   │   │   ├── RedisPubSubService.java
│   │   │   │   └── RedisChannelEnum.java
│   │   │   │
│   │   │   ├── scheduler/
│   │   │   │   ├── RoomTimerScheduler.java
│   │   │   │   ├── RoundTimeoutHandler.java
│   │   │   │   └── BotActionScheduler.java
│   │   │   │
│   │   │   └── dto/
│   │   │       ├── RoomStateDTO.java
│   │   │       ├── RoundResultDTO.java
│   │   │       └── BroadcastMessageDTO.java
│   │   │
│   │   ├── engine/                   # Игровая логика, RNG, экономика
│   │   │   ├── WinnerService.java    # RNG, определение победителей
│   │   │   ├── BalanceService.java   # Расчёты баланса, холдирование
│   │   │   ├── BoostCalculator.java  # Логика расчёта бустов
│   │   │   ├── PrizePoolService.java # Расчёт призового фонда
│   │   │   └── rng/
│   │   │       ├── RngProvider.java
│   │   │       └── RngAuditRecord.java
│   │   │
│   │   ├── config/                   # Конфигурация приложения
│   │   │   ├── RoomConfig.java       # @ConfigurationProperties для комнат
│   │   │   ├── BotConfig.java        # Настройки ботов
│   │   │   ├── EconomicRules.java    # Правила экономики (комиссии, лимиты)
│   │   │   ├── SecurityConfig.java   # Spring Security, JWT
│   │   │   ├── CorsConfig.java
│   │   │   └── SchedulerConfig.java
│   │   │
│   │   ├── audit/                    # Журналирование, аудит, экспорт
│   │   │   ├── entity/
│   │   │   │   ├── AuditLog.java
│   │   │   │   ├── RoundAudit.java
│   │   │   │   └── TransactionAudit.java
│   │   │   │
│   │   │   ├── service/
│   │   │   │   ├── AuditService.java
│   │   │   │   ├── LogExporter.java
│   │   │   │   └── AuditQueryService.java
│   │   │   │
│   │   │   └── aspect/
│   │   │       ├── AuditAspect.java
│   │   │       └── TransactionAuditAspect.java
│   │   │
│   │   ├── integration/              # Заглушки для внешних API
│   │   │   ├── stoloto/
│   │   │   │   ├── StolotoAuthClient.java
│   │   │   │   ├── StolotoBalanceClient.java
│   │   │   │   └── dto/
│   │   │   │       ├── AuthRequest.java
│   │   │   │       └── BalanceResponse.java
│   │   │   │
│   │   │   └── mock/
│   │   │       └── MockExternalServices.java
│   │   │
│   │   ├── api/                      # REST контроллеры
│   │   │   ├── auth/
│   │   │   │   ├── AuthController.java
│   │   │   │   └── dto/
│   │   │   │       ├── LoginRequest.java
│   │   │   │       └── TokenResponse.java
│   │   │   │
│   │   │   ├── user/
│   │   │   │   ├── UserController.java
│   │   │   │   └── dto/
│   │   │   │       ├── UserProfileResponse.java
│   │   │   │       └── BalanceResponse.java
│   │   │   │
│   │   │   ├── room/
│   │   │   │   ├── RoomController.java
│   │   │   │   └── dto/
│   │   │   │       ├── RoomListResponse.java
│   │   │   │       ├── RoomCreateRequest.java
│   │   │   │       └── RoomStateResponse.java
│   │   │   │
│   │   │   ├── game/
│   │   │   │   ├── GameController.java
│   │   │   │   └── dto/
│   │   │   │       ├── PlaceBetRequest.java
│   │   │   │       └── RoundHistoryResponse.java
│   │   │   │
│   │   │   └── admin/
│   │   │       ├── AdminRoomController.java
│   │   │       ├── AdminBotController.java
│   │   │       ├── AdminAuditController.java
│   │   │       └── AdminEconomyController.java
│   │   │
│   │   └── exception/
│   │       ├── GlobalExceptionHandler.java
│   │       ├── BusinessException.java
│   │       ├── InsufficientBalanceException.java
│   │       ├── RoomNotAvailableException.java
│   │       └── ErrorResponse.java
│   │
│   ├── src/main/resources/
│   │   ├── application.yml           # Основная конфигурация
│   │   ├── application-dev.yml       # Dev-профиль
│   │   ├── application-prod.yml      # Prod-профиль
│   │   │
│   │   ├── db/
│   │   │   └── migration/            # Liquibase миграции
│   │   │       ├── 001_init_schema.xml
│   │   │       ├── 002_users_roles.xml
│   │   │       ├── 003_rooms_config.xml
│   │   │       ├── 004_rounds_bets.xml
│   │   │       ├── 005_transactions.xml
│   │   │       ├── 006_audit_logs.xml
│   │   │       └── 007_bots_config.xml
│   │   │
│   │   └── seed/
│   │       └── initial_data.sql      # Стартовые данные (роли, настройки)
│   │
│   ├── src/test/
│   │   ├── java/
│   │   │   └── com/stoloto/viprooms/
│   │   │       ├── core/
│   │   │       ├── realtime/
│   │   │       ├── engine/
│   │   │       └── api/
│   │   │
│   │   └── resources/
│   │       └── test-data/
│   │
│   ├── build.gradle                  # или pom.xml
│   └── Dockerfile
│
├── frontend/                         # React + Vite / Next.js (TS)
│   ├── src/
│   │   ├── main.tsx                  # Точка входа
│   │   ├── App.tsx                   # Корневой компонент с роутингом
│   │   │
│   │   ├── user/                     # Личный кабинет пользователя
│   │   │   ├── pages/
│   │   │   │   ├── Dashboard.tsx     # Главная ЛК
│   │   │   │   ├── Profile.tsx       # Профиль
│   │   │   │   ├── Balance.tsx       # Баланс и история транзакций
│   │   │   │   ├── Bonuses.tsx       # Бонусные баллы
│   │   │   │   └── History.tsx       # История игр
│   │   │   │
│   │   │   ├── components/
│   │   │   │   ├── BalanceCard.tsx
│   │   │   │   ├── BonusWidget.tsx
│   │   │   │   ├── TransactionTable.tsx
│   │   │   │   └── GameHistoryList.tsx
│   │   │   │
│   │   │   └── hooks/
│   │   │       ├── useBalance.ts
│   │   │       └── useTransactionHistory.ts
│   │   │
│   │   ├── lobby/                    # Лобби комнат
│   │   │   ├── pages/
│   │   │   │   └── Lobby.tsx
│   │   │   │
│   │   │   ├── components/
│   │   │   │   ├── RoomList.tsx
│   │   │   │   ├── RoomCard.tsx
│   │   │   │   ├── RoomFilters.tsx
│   │   │   │   └── RoomStatusBadge.tsx
│   │   │   │
│   │   │   └── hooks/
│   │   │       └── useRooms.ts
│   │   │
│   │   ├── game/                     # Экран игровой комнаты
│   │   │   ├── pages/
│   │   │   │   └── GameRoom.tsx
│   │   │   │
│   │   │   ├── components/
│   │   │   │   ├── RoomTimer.tsx
│   │   │   │   ├── BetPanel.tsx
│   │   │   │   ├── BoostButton.tsx
│   │   │   │   ├── PlayersList.tsx
│   │   │   │   ├── RoundResults.tsx
│   │   │   │   └── VisualWrapper.tsx
│   │   │   │
│   │   │   └── hooks/
│   │   │       ├── useWebSocket.ts
│   │   │       ├── useRoomState.ts
│   │   │       ├── useRoundTimer.ts
│   │   │       └── useBetPlacement.ts
│   │   │
│   │   ├── admin/                    # Административная панель
│   │   │   ├── pages/
│   │   │   │   ├── AdminDashboard.tsx
│   │   │   │   ├── RoomsConfig.tsx
│   │   │   │   ├── BotsConfig.tsx
│   │   │   │   ├── EconomyRules.tsx
│   │   │   │   ├── AuditLogs.tsx
│   │   │   │   └── Analytics.tsx
│   │   │   │
│   │   │   ├── components/
│   │   │   │   ├── RoomConfigForm.tsx
│   │   │   │   ├── BotConfigForm.tsx
│   │   │   │   ├── BoostEditor.tsx
│   │   │   │   ├── AuditTable.tsx
│   │   │   │   ├── AnalyticsChart.tsx
│   │   │   │   └── UserSearch.tsx
│   │   │   │
│   │   │   └── hooks/
│   │   │       ├── useRoomConfigs.ts
│   │   │       ├── useBotConfigs.ts
│   │   │       ├── useAuditLogs.ts
│   │   │       └── useAnalytics.ts
│   │   │
│   │   ├── auth/                     # Авторизация
│   │   │   ├── pages/
│   │   │   │   ├── Login.tsx
│   │   │   │   └── Register.tsx
│   │   │   │
│   │   │   ├── components/
│   │   │   │   ├── LoginForm.tsx
│   │   │   │   └── ProtectedRoute.tsx
│   │   │   │
│   │   │   └── hooks/
│   │   │       ├── useAuth.ts
│   │   │       └── useTokenRefresh.ts
│   │   │
│   │   ├── shared/                   # Общие компоненты и утилиты
│   │   │   ├── ui-kit/
│   │   │   │   ├── Button.tsx
│   │   │   │   ├── Input.tsx
│   │   │   │   ├── Modal.tsx
│   │   │   │   ├── Table.tsx
│   │   │   │   ├── Card.tsx
│   │   │   │   ├── Badge.tsx
│   │   │   │   ├── Spinner.tsx
│   │   │   │   └── Toast.tsx
│   │   │   │
│   │   │   ├── layout/
│   │   │   │   ├── Header.tsx
│   │   │   │   ├── Sidebar.tsx
│   │   │   │   ├── Footer.tsx
│   │   │   │   └── AdminLayout.tsx
│   │   │   │
│   │   │   ├── clients/
│   │   │   │   ├── apiClient.ts      # Axios instance
│   │   │   │   ├── wsClient.ts       # WebSocket клиент
│   │   │   │   └── sseClient.ts      # SSE клиент (опционально)
│   │   │   │
│   │   │   ├── hooks/
│   │   │   │   ├── useApi.ts
│   │   │   │   └── useDebounce.ts
│   │   │   │
│   │   │   ├── utils/
│   │   │   │   ├── formatters.ts
│   │   │   │   ├── validators.ts
│   │   │   │   └── constants.ts
│   │   │   │
│   │   │   └── types/
│   │   │       ├── api.ts
│   │   │       ├── user.ts
│   │   │       ├── room.ts
│   │   │       └── admin.ts
│   │   │
│   │   └── routes/                   # RBAC-маршрутизация
│   │       ├── index.tsx             # Определение маршрутов
│   │       ├── protected.tsx         # Защищённые роуты
│   │       ├── public.tsx            # Публичные роуты
│   │       └── roles.ts              # Константы ролей
│   │
│   ├── public/
│   │   ├── index.html
│   │   └── assets/
│   │
│   ├── package.json
│   ├── vite.config.ts                # или next.config.js
│   ├── tsconfig.json
│   └── Dockerfile
│
├── infra/                            # Инфраструктура
│   ├── docker/
│   │   ├── docker-compose.yml        # Локальная разработка
│   │   ├── docker-compose.prod.yml   # Production
│   │   ├── backend/
│   │   │   └── Dockerfile
│   │   ├── frontend/
│   │   │   └── Dockerfile
│   │   └── redis/
│   │       └── redis.conf
│   │
│   ├── k8s/                          # Kubernetes manifests (V2 target)
│   │   ├── namespace.yaml
│   │   ├── deployments/
│   │   │   ├── backend-deployment.yaml
│   │   │   └── frontend-deployment.yaml
│   │   ├── services/
│   │   │   ├── backend-service.yaml
│   │   │   └── frontend-service.yaml
│   │   ├── configmaps/
│   │   │   └── app-config.yaml
│   │   ├── secrets/
│   │   │   └── app-secrets.yaml
│   │   └── ingress/
│   │       └── ingress.yaml
│   │
│   └── scripts/
│       ├── deploy.sh
│       └── migrate.sh
│
├── docs/                             # Документация
│   ├── openapi/
│   │   └── api-spec.yaml             # OpenAPI спецификация
│   ├── architecture/
│   │   ├── system-overview.md
│   │   ├── data-flow.md
│   │   └── deployment.md
│   ├── scenarios/
│   │   ├── user-journey.md
│   │   ├── admin-workflow.md
│   │   └── round-lifecycle.md
│   ├── economy/
│   │   ├── rules.md
│   │   ├── boosts.md
│   │   └── commissions.md
│   └── decisions/
│       └── adr-001-tech-stack.md
│
├── scripts/                          # Скрипты разработки
│   ├── seed-data/
│   │   └── generate-test-users.py
│   ├── load-testing/
│   │   ├── k6-script.js
│   │   └── jmeter-plan.jmx
│   ├── migrations/
│   │   └── run-migrations.sh
│   └── dev-setup/
│       └── init-local-env.sh
│
├── .gitignore
├── .editorconfig
└── README.md
```

---

### 3. Роли и доступы

| Роль | Описание | Доступ |
|------|----------|--------|
| `GUEST` | Неавторизованный пользователь | Лендинг, регистрация, вход |
| `USER` | Авторизованный игрок | ЛК, лобби, вход в комнаты, ставки, просмотр истории |
| `MODERATOR` | Модератор комнат | Все права USER + просмотр логов комнат, управление активностью |
| `ADMIN` | Администратор | Полная админ-панель: настройка комнат, ботов, бустов, экономики, аудит |
| `SUPER_ADMIN` | Супер-админ | Все права ADMIN + управление ролями, системные настройки |

**RBAC матрица доступа:**

| Раздел | GUEST | USER | MODERATOR | ADMIN | SUPER_ADMIN |
|--------|-------|------|-----------|-------|-------------|
| Регистрация/Вход | ✅ | ✅ | ✅ | ✅ | ✅ |
| Личный кабинет | ❌ | ✅ | ✅ | ✅ | ✅ |
| Лобби комнат | ❌ | ✅ | ✅ | ✅ | ✅ |
| Вход в комнату | ❌ | ✅ | ✅ | ✅ | ✅ |
| Ставки | ❌ | ✅ | ✅ | ✅ | ✅ |
| История игр | ❌ | ✅ | ✅ | ✅ | ✅ |
| Логи комнат | ❌ | ❌ | ✅ | ✅ | ✅ |
| Настройка комнат | ❌ | ❌ | ❌ | ✅ | ✅ |
| Настройка ботов | ❌ | ❌ | ❌ | ✅ | ✅ |
| Настройка бустов | ❌ | ❌ | ❌ | ✅ | ✅ |
| Экономика | ❌ | ❌ | ❌ | ✅ | ✅ |
| Аудит | ❌ | ❌ | ❌ | ✅ | ✅ |
| Управление ролями | ❌ | ❌ | ❌ | ❌ | ✅ |

---

### 4. Основные страницы и модули

#### 4.1 Публичная зона (Guest)
- **Landing Page** — описание платформы, преимущества
- **Login** — форма входа (email/пароль или интеграция со Столото)
- **Register** — форма регистрации

#### 4.2 Личный кабинет пользователя (User)
- **Dashboard** — сводка: баланс, бонусы, последние игры, активные комнаты
- **Profile** — личные данные, настройки уведомлений
- **Balance** — текущий баланс, пополнение (заглушка), вывод (заглушка)
- **Bonuses** — бонусные баллы, история начислений, активация
- **History** — история раундов, ставок, выигрышей с фильтрами

#### 4.3 Лобби комнат (User)
- **Lobby** — список комнат с фильтрами:
  - По статусу (ожидание, активна, завершена)
  - По ставке (мин/макс)
  - По количеству игроков
  - По типу комнаты (VIP, стандарт)
- **RoomCard** — превью комнаты: название, ставка, игроки, таймер до старта

#### 4.4 Игровая комната (User)
- **GameRoom** — основной экран игры:
  - **RoomTimer** — обратный отсчёт до конца раунда
  - **BetPanel** — панель размещения ставки
  - **BoostButton** — кнопка активации буста
  - **PlayersList** — список игроков в комнате
  - **VisualWrapper** — визуальная обёртка (анимации, эффекты)
  - **RoundResults** — результаты раунда (победители, выплаты)

#### 4.5 Навигация
```
Гость:
  / → Лендинг
  /login → Вход
  /register → Регистрация

Авторизованный пользователь:
  /dashboard → Личный кабинет
  /profile → Профиль
  /balance → Баланс
  /bonuses → Бонусы
  /history → История
  /lobby → Лобби комнат
  /room/:id → Игровая комната

Администратор:
  /admin → Админ-дашборд
  /admin/rooms → Настройка комнат
  /admin/bots → Настройка ботов
  /admin/economy → Экономика
  /admin/audit → Аудит
  /admin/analytics → Аналитика
```

---

### 5. Административная часть

#### 5.1 Админ-дашборд (`/admin`)
- Статистика по платформе:
  - Активные пользователи
  - Активные комнаты
  - Оборот за период
  - Топ игроков
- Быстрые действия: создать комнату, заблокировать пользователя

#### 5.2 Конфигуратор комнат (`/admin/rooms`)
- Список всех комнат (активные, архивные)
- Создание/редактирование комнаты:
  - Название, описание
  - Мин/макс ставка
  - Максимум игроков
  - Длительность раунда
  - Комиссия платформы
  - Статус (активна/скрыта)
- Шаблоны комнат (быстрое создание)

#### 5.3 Управление ботами (`/admin/bots`)
- Список ботов с настройками:
  - Имя, аватар (опционально)
  - Стратегия поведения (агрессивная, консервативная, случайная)
  - Вероятность участия в раунде
  - Диапазон ставок
  - Лимиты на участие
- Включение/выключение ботов
- Логи действий ботов

#### 5.4 Настройка бустов (`/admin/economy/boosts`)
- Типы бустов:
  - Множитель выигрыша
  - Страховка ставки
  - Бесплатное участие
- Параметры:
  - Стоимость в бонусных баллах
  - Длительность действия
  - Условия активации
  - Лимиты на использование

#### 5.5 Экономика (`/admin/economy`)
- Комиссия платформы (%)
- Лимиты на ставки (мин/макс)
- Лимиты на вывод (заглушка)
- Курсы обмена бонусных баллов
- Налоговая политика (для будущих интеграций)

#### 5.6 Аудит и логи (`/admin/audit`)
- Журнал раундов:
  - ID раунда, комната, время
  - Участники, ставки
  - Победители, выплаты
  - RNG-сид (для проверки честности)
- Журнал транзакций:
  - Тип (ставка, выигрыш, комиссия, бонус)
  - Сумма, пользователь, время
- Журнал действий админов:
  - Кто, что изменил, когда
- Экспорт логов (CSV, JSON)

#### 5.7 Аналитика (`/admin/analytics`)
- Графики:
  - Активность по времени
  - Распределение ставок
  - Win rate по комнатам
  - Доход платформы
- Фильтры по периодам
- Сравнение периодов

---

### 6. Real-time часть на Redis

#### 6.1 Архитектура real-time взаимодействия

```
┌─────────────┐     WebSocket      ┌─────────────┐
│   Frontend  │ ◄────────────────► │   Backend   │
│  (React TS) │                    │ (Spring Boot)│
└─────────────┘                    └──────┬──────┘
                                          │
                                          │ Redis Pub/Sub
                                          ▼
                                   ┌─────────────┐
                                   │    Redis    │
                                   │  Channels:  │
                                   │  - rooms:*  │
                                   │  - rounds:* │
                                   │  - users:*  │
                                   └─────────────┘
```

#### 6.2 Redis каналы (Pub/Sub)

| Канал | Описание | Пример сообщения |
|-------|----------|------------------|
| `rooms:{roomId}:state` | Состояние комнаты | `{status: "ACTIVE", players: 5, timer: 45}` |
| `rooms:{roomId}:round:start` | Начало раунда | `{roundId: 123, startTime: ...}` |
| `rooms:{roomId}:round:end` | Конец раунда | `{roundId: 123, winners: [...]}` |
| `rooms:{roomId}:bets` | Новые ставки | `{userId: "u1", amount: 100, timestamp: ...}` |
| `users:{userId}:balance` | Обновление баланса | `{balance: 5000, bonus: 200}` |
| `users:{userId}:notifications` | Уведомления пользователю | `{type: "WIN", message: "Вы выиграли 500!"}` |
| `admin:alerts` | Системные алерты для админов | `{type: "ERROR", message: "RNG failure"}` |

#### 6.3 Таймеры и планировщики

- **RoomTimerScheduler** — отслеживает состояние комнат:
  - `WAITING` → `ACTIVE` (когда набрано мин. игроков или истёк таймер ожидания)
  - `ACTIVE` → `COMPLETED` (по истечении времени раунда)
- **RoundTimeoutHandler** — принудительное завершение раунда при таймауте
- **BotActionScheduler** — симуляция действий ботов с рандомными задержками

#### 6.4 WebSocket события (клиент ↔ сервер)

**От клиента к серверу:**
- `JOIN_ROOM` — вход в комнату
- `LEAVE_ROOM` — выход из комнаты
- `PLACE_BET` — размещение ставки
- `ACTIVATE_BOOST` — активация буста
- `SUBSCRIBE_ROUND` — подписка на обновления раунда

**От сервера к клиенту:**
- `ROOM_STATE_UPDATE` — обновление состояния комнаты
- `ROUND_STARTED` — раунд начался
- `ROUND_ENDING` — раунд скоро закончится (таймер < 10 сек)
- `ROUND_RESULT` — результаты раунда
- `BET_CONFIRMED` — ставка принята
- `BALANCE_UPDATED` — баланс обновлён
- `ERROR` — ошибка (недостаточно средств, комната закрыта и т.д.)

---

### 7. Логи, аудит и мониторинг

#### 7.1 Уровни логирования

| Уровень | Что логируется | Хранение |
|---------|----------------|----------|
| `ERROR` | Ошибки системы, исключения, сбои RNG | 90 дней, алерт админам |
| `WARN` | Предупреждения (подозрительные ставки, таймауты) | 90 дней |
| `INFO` | Действия пользователей (вход, ставки, выигрыши) | 30 дней |
| `DEBUG` | Детали работы сервисов (dev-окружение) | 7 дней |
| `AUDIT` | Все финансовые операции, изменения конфигов | 365 дней, неизменяемо |

#### 7.2 Структура аудиторской записи

```json
{
  "id": "uuid",
  "timestamp": "2025-01-15T10:30:00Z",
  "type": "ROUND_COMPLETED",
  "actor": {
    "type": "SYSTEM",
    "id": null
  },
  "context": {
    "roomId": "r_123",
    "roundId": "rnd_456"
  },
  "data": {
    "participants": ["u1", "u2", "bot_1"],
    "totalBets": 1500,
    "winners": [
      {"userId": "u1", "bet": 100, "payout": 500}
    ],
    "rngSeed": "abc123xyz",
    "platformCommission": 75
  },
  "signature": "sha256_hash_for_integrity"
}
```

#### 7.3 Мониторинг (для будущей реализации)

- **Метрики:**
  - Количество активных комнат
  - Среднее время раунда
  - Конверсия посетителей в игроков
  - Ошибки RNG
  - Задержки WebSocket
- **Алерты:**
  - Превышение порога ошибок
  - Аномальная активность (возможный фрод)
  - Недостаточно средств у бота
  - Таймауты раундов

---

### 8. Что нужно уточнить перед следующими этапами

#### 8.1 Технические вопросы
1. **Фреймворк фронтенда:** окончательно выбрать между React + Vite или Next.js?
   - Vite: проще, быстрее для SPA
   - Next.js: SSR, SEO, но сложнее инфраструктура
2. **Аутентификация:** 
   - Собственная система (email/пароль + JWT)?
   - Интеграция со Столото (OAuth, SSO)?
3. **База данных:** какая СУБД предпочтительна?
   - PostgreSQL (рекомендуется)
   - MySQL
   - Другая
4. **Хранение файлов:** где хранить логи, экспорты?
   - Локально (dev)
   - S3-compatible хранилище (prod)

#### 8.2 Бизнес-вопросы
1. **Валюта:** 
   - Реальные деньги (требуется лицензия, платёжные шлюзы)?
   - Виртуальная валюта (баллы, очки)?
2. **Боты:**
   - Какое соотношение ботов к реальным игрокам допустимо?
   - Должны ли боты быть помечены как "бот" для пользователей?
3. **Бусты:**
   - Какие именно типы бустов нужны на старте?
   - Можно ли покупать бусты за реальные деньги?
4. **Лимиты:**
   - Мин/макс ставка по умолчанию?
   - Максимальный выигрыш за раунд?
   - Дневные лимиты для пользователей?

#### 8.3 Интеграции
1. **Столото API:** есть ли документация по ожидаемым эндпоинтам?
2. **Платёжные системы:** какие шлюзы планируется подключать?
3. **Уведомления:** email, SMS, push — что нужно?

---

### 9. Пробелы и неоднозначности в ТЗ

| № | Вопрос | Почему важно | Рекомендация |
|---|--------|--------------|--------------|
| 1 | Механика игры не описана | Непонятно, как определяется победитель | Требуется отдельный документ с правилами игры |
| 2 | Типы комнат не определены | Влияет на структуру данных и UI | Определить 2-3 типа на старте (например, Standard, VIP, High Roller) |
| 3 | Логика бустов не детализирована | Непонятно, как считать и применять | Требуется таблица с формулами расчёта бустов |
| 4 | Поведение ботов не формализовано | Сложно реализовать реалистичную симуляцию | Описать стратегии ботов (агрессивная, консервативная, рандом) |
| 5 | Требования к безопасности не указаны | Риски фрода, манипуляций | Определить требования: HTTPS, rate limiting, анти-фрод правила |
| 6 | Масштабируемость | Сколько одновременных пользователей ожидается? | Заложить горизонтальное масштабирование backend с первого дня |
| 7 | Юридические ограничения | Азартные игры регулируются законодательством | Проконсультироваться с юристом, добавить дисклеймеры |
| 8 | Требования к производительности | Как быстро должны приходить real-time обновления? | Целевые метрики: <100ms для WebSocket сообщений |
| 9 | Резервное копирование | Как часто делать бэкапы БД? | Определить политику: ежедневно + point-in-time recovery |
| 10 | Мультиязычность | Нужна ли поддержка нескольких языков? | Если да — заложить i18n с начала разработки |

---

## Следующие шаги

Этот шаблон готов к поэтапной реализации. Рекомендуемый порядок разработки:

1. **Фаза 1: Инфраструктура**
   - Настроить docker-compose (backend + frontend + Redis + DB)
   - Создать базовую структуру папок
   - Настроить CI/CD пайплайны (опционально)

2. **Фаза 2: Backend Core**
   - Реализовать доменные сущности (User, Room, Round, Bet)
   - Настроить Liquibase миграции
   - Реализовать CRUD API для комнат и пользователей

3. **Фаза 3: Аутентификация и авторизация**
   - JWT токены
   - RBAC middleware
   - Защищённые эндпоинты

4. **Фаза 4: Real-time**
   - WebSocket шлюз
   - Redis Pub/Sub интеграция
   - Таймеры комнат

5. **Фаза 5: Frontend базовый**
   - Личный кабинет
   - Лобби комнат
   - Экран игры (без анимаций)

6. **Фаза 6: Админ-панель**
   - Конфигуратор комнат
   - Управление ботами
   - Просмотр аудит-логов

7. **Фаза 7: Полировка**
   - Визуальные эффекты
   - Оптимизация производительности
   - Нагрузочное тестирование

---

*Документ является живым и должен обновляться по мере уточнения требований.*
