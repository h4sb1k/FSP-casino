# Этап 2: Backend Core - Реализация завершена

## Обзор

На втором этапе разработки были реализованы ключевые сервисы backend-части системы Stoloto VIP Rooms. Все компоненты готовы к интеграции с REST API и WebSocket контроллерами.

## Созданные компоненты

### 1. DTO классы (`/api/dto/`)

#### RoomDto.java
- `RoomInfo` - основная информация о комнате
- `PlayerInRoom` - данные игрока в комнате
- `RoomState` - полное состояние комнаты
- `CreateRoomRequest`, `JoinRoomRequest`, `BoostRequest` - запросы
- `RoomResult`, `PlayerResult` - результаты раунда

#### BalanceDto.java
- `BalanceInfo` - информация о балансе пользователя
- `ReserveRequest`, `CommitRequest`, `RollbackRequest` - операции с транзакциями
- `TransactionInfo` - детали транзакции

#### WsDto.java
- `WsMessage<T>` - базовое WebSocket сообщение
- События: `PlayerJoinedEvent`, `PlayerLeftEvent`, `BetPlacedEvent`, `BoostActivatedEvent`
- События раунда: `RoundStartingEvent`, `RoundEndingSoonEvent`, `RoundResultEvent`
- Системные: `ErrorEvent`, `BalanceUpdateEvent`, `NotificationEvent`

### 2. Игровой сервис (`/service/game/RoomService.java`)

**Основные методы:**
- `createRoom()` - создание комнаты типа Bronze/Gold/Diamond
- `joinRoom()` - присоединение игрока с резервированием ставки
- `activateBoost()` - покупка и активация буста
- `startRound()` - запуск раунда с заполнением ботами
- `determineWinner()` - определение победителя через взвешенный RNG

**Ключевые особенности:**
- Автоматическое заполнение ботами до 10 человек
- Поддержка idempotency для всех операций
- Интеграция с AuditService для логирования
- Расчет весов с учетом бустов (+% к вероятности победы)

### 3. Сервис баланса (`/service/balance/BalanceService.java`)

**Реализованные функции:**
- `getBalanceInfo()` - получение баланса (main + bonus + reserved)
- `reserveForBet()` - резервирование средств для ставки
- `reserveForBoost()` - резервирование бонусов для буста
- `commitBet()`, `commitBoostPurchase()` - подтверждение транзакций
- `creditWin()` - начисление выигрыша игроку
- `creditHouse()` - начисление выигрыша казино (при победе бота)
- `rollbackTransaction()` - откат транзакции при ошибках

**Безопасность:**
- Паттерн двойной записи (все операции через таблицу transactions)
- IdempotencyKey для защиты от дублирования
- Статусы транзакций: PENDING → COMPLETED / ROLLED_BACK

### 4. RNG сервис (`/rng/RngService.java`)

**Provably Fair реализация:**
- `generateServerSeed()` - генерация секретного seed (32 байта)
- `generateClientSeed()` - генерация клиентского seed
- `combineSeeds()` - комбинация seed'ов через SHA-256
- `getRandomValue()` - получение числа [0.0, 1.0] из хеша
- `generateHashedServerSeed()` - публичный хеш для аудита
- `verifyRound()` - верификация честности пользователем

**Аудит:**
- ServerSeed хранится хэшированным до конца раунда
- После раунда раскрывается для проверки
- Любой пользователь может воспроизвести результат

### 5. Сервис аудита (`/service/audit/AuditService.java`)

**Логируемые события:**
- ROOM_CREATED, PLAYER_JOINED_ROOM, BOOST_ACTIVATED
- ROUND_STARTED, ROUND_COMPLETED
- TRANSACTION_CREATED, TRANSACTION_COMPLETED, TRANSACTION_ROLLED_BACK
- HOUSE_INCOME (выигрыш бота)
- ADMIN_ACTION, USER_LOGIN, USER_LOGOUT
- SECURITY_EVENT (подозрительная активность)

**Особенности:**
- Асинхронная запись (@Async)
- Immutable записи (только добавление)
- Контекстные данные в JSON формате
- Срок хранения: 365 дней

### 6. Планировщик игр (`/scheduler/GameScheduler.java`)

**Задачи:**
- `checkStartingRooms()` (каждые 5 сек) - запуск раундов после 60 сек ожидания
- `checkActiveRounds()` (каждые 5 сек) - завершение раундов через 30 сек
- `cleanupStaleRooms()` (каждую минуту) - удаление пустых комнат > 5 мин

**Обработка ошибок:**
- `rollbackRoomBets()` - возврат ставок при ошибке запуска
- `rollbackRoundBets()` - возврат ставок при ошибке завершения
- Все откаты логируются в audit

### 7. Redis Pub/Sub (`/realtime/redis/RedisPubSubPublisher.java`)

**Каналы:**
- `rooms:{id}:events` - события комнаты (старт, результаты, ошибки)
- `rooms:{id}:state` - текущее состояние
- `rooms:{id}:bets` - сделанные ставки
- `users:{id}:balance` - обновления баланса
- `users:{id}:notifications` - push-уведомления
- `admin:alerts` - системные алерты

**Публикуемые события:**
- ROUND_START, ROUND_RESULT
- PLAYER_JOINED, BET_PLACED
- BALANCE_UPDATE, NOTIFICATION
- ERROR, ROOM_STATE_UPDATE

## Архитектурные решения

### 1. Безопасность транзакций
```
┌─────────────┐    ┌──────────────┐    ┌─────────────┐
│   Request   │───►│ Idempotency  │───►│  Reserve    │
│  (с ключом) │    │   Check      │    │  Funds      │
└─────────────┘    └──────────────┘    └─────────────┘
                                              │
┌─────────────┐    ┌──────────────┐    ┌─────▼─────┐
│   Response  │◄───│   Commit     │◄───│  Process  │
│             │    │   Result     │    │   Game    │
└─────────────┘    └──────────────┘    └───────────┘
```

### 2. Механика бустов
- Буст покупается за бонусные баллы
- Увеличивает вес игрока в RNG на заданный процент
- Пример: Ставка 100 + Буст 10% = Вес 110 в лотерее
- Боты не имеют бустов

### 3. Взвешенный RNG
```
Total Weight = Σ(bet_i * (1 + boostPercent_i/100))
Random Value ∈ [0, TotalWeight]
Winner = первый участник, где cumulativeWeight >= RandomValue
```

### 4. Обработка ошибок
Все критические операции обернуты в try-catch с автоматическим откатом:
- Ошибка старта раунда → rollback всех ставок
- Ошибка определения победителя → rollback всех ставок
- Дублирование запроса → возврат существующей транзакции

## Готовность к следующему этапу

### ✅ Выполнено:
- [x] DTO для всех API операций
- [x] RoomService с полной игровой логикой
- [x] BalanceService с двойной записью
- [x] RngService с Provably Fair
- [x] AuditService для всех событий
- [x] GameScheduler для таймеров
- [x] RedisPubSubPublisher для real-time

### ⏳ Требуется реализовать (Этап 3):
- [ ] REST контроллеры (Auth, Room, Game, User, Admin)
- [ ] WebSocket обработчики событий
- [ ] BotService для заполнения комнат
- [ ] Интеграция с фронтендом
- [ ] Тестирование нагрузки

## Структура файлов

```
backend/src/main/java/com/stoloto/vip/
├── api/dto/
│   ├── RoomDto.java          ✅
│   ├── BalanceDto.java       ✅
│   └── WsDto.java            ✅
├── service/
│   ├── game/
│   │   └── RoomService.java  ✅
│   ├── balance/
│   │   └── BalanceService.java ✅
│   └── audit/
│       └── AuditService.java ✅
├── rng/
│   └── RngService.java       ✅
├── scheduler/
│   └── GameScheduler.java    ✅
└── realtime/redis/
    └── RedisPubSubPublisher.java ✅
```

## Следующие шаги

1. **REST API контроллеры** - создать endpoints для всех операций
2. **WebSocket handlers** - обработка client→server событий
3. **Bot Service** - реалистичное поведение ботов с задержкой
4. **Интеграционные тесты** - проверка полного цикла игры
5. **Frontend integration** - подключение React приложения

---

**Статус этапа 2: ЗАВЕРШЕН**  
**Готовность проекта: ~65%**
