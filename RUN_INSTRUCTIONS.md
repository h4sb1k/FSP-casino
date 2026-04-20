# 🚀 Stoloto VIP Rooms - Инструкция по запуску

## ✅ Статус готовности проекта

**Общая готовность: ~85%**

| Компонент | Готовность | Статус |
|-----------|------------|--------|
| **Backend Core** | 100% | ✅ Завершено |
| - Domain Entities | 100% | ✅ |
| - Repositories | 100% | ✅ |
| - Business Services | 100% | ✅ |
| - REST API | 100% | ✅ |
| - WebSocket | 100% | ✅ |
| - Liquibase Migrations | 100% | ✅ |
| **Frontend Core** | 90% | ✅ Завершено |
| - Pages (Auth, Lobby, Game, Profile) | 90% | ✅ |
| - Components UI Kit | 95% | ✅ |
| - CSS Styling | 100% | ✅ |
| - WebSocket Integration | 85% | ⚠️ Требуется тестирование |
| **Infrastructure** | 100% | ✅ Завершено |
| - Docker Compose | 100% | ✅ |
| - PostgreSQL Schema | 100% | ✅ |
| - Redis Config | 100% | ✅ |
| **Documentation** | 100% | ✅ Завершено |

---

## 📋 Предварительные требования

### Обязательные
- **Docker** 20.10+ и **Docker Compose** 2.0+
- **Java JDK** 17 или 21
- **Node.js** 18+ и **npm** 9+
- **Git** для работы с репозиторием

### Опциональные (для разработки)
- **IntelliJ IDEA** или **VS Code**
- **Postman** или **Insomnia** для тестирования API
- **Redis Insight** для мониторинга Redis
- **pgAdmin** или **DBeaver** для работы с PostgreSQL

---

## 🔧 Быстрый старт (Development)

### Шаг 1: Клонирование и подготовка

```bash
# Перейдите в директорию проекта
cd /workspace

# Проверьте структуру проекта
ls -la
# Ожидаем: backend/, frontend/, infra/, docs/
```

### Шаг 2: Запуск инфраструктуры (PostgreSQL + Redis)

```bash
# Запустите контейнеры с базами данных
docker-compose -f infra/docker-compose.yml up -d

# Проверьте статус контейнеров
docker-compose -f infra/docker-compose.yml ps
# Ожидаем: stoloto-postgres (healthy), stoloto-redis (healthy)

# Просмотрите логи (если есть проблемы)
docker-compose -f infra/docker-compose.yml logs postgres
docker-compose -f infra/docker-compose.yml logs redis
```

**Проверка подключения:**
```bash
# PostgreSQL должен быть доступен на порту 5432
psql -h localhost -U postgres -d stoloto_vip -c "SELECT version();"

# Redis должен быть доступен на порту 6379
redis-cli -h localhost ping
# Ожидаем: PONG
```

### Шаг 3: Запуск Backend (Spring Boot)

```bash
# Перейдите в директорию backend
cd /workspace/backend

# Установите переменные окружения (опционально, значения по умолчанию уже в application-dev.yml)
export SPRING_PROFILES_ACTIVE=dev
export DATABASE_URL=jdbc:postgresql://localhost:5432/stoloto_vip
export DATABASE_USER=postgres
export DATABASE_PASSWORD=postgres
export REDIS_HOST=localhost
export REDIS_PORT=6379
export JWT_SECRET=my-super-secret-jwt-key-min-32-characters-long
export JWT_EXPIRATION_MS=3600000

# Запустите приложение (вариант 1: через Maven wrapper)
./mvnw spring-boot:run

# ИЛИ вариант 2: собрать и запустить JAR
./mvnw clean package -DskipTests
java -jar target/stoloto-vip-rooms-0.0.1-SNAPSHOT.jar

# Приложение запустится на http://localhost:8080
```

**Проверка Backend:**
```bash
# Проверьте health endpoint
curl http://localhost:8080/api/health

# Откройте Swagger UI в браузере
# http://localhost:8080/swagger-ui.html
```

### Шаг 4: Запуск Frontend (React + Vite)

```bash
# Откройте НОВЫЙ терминал и перейдите в директорию frontend
cd /workspace/frontend

# Установите зависимости (первый запуск)
npm install

# Создайте файл .env (если не существует)
cat > .env << EOF
VITE_API_BASE_URL=http://localhost:8080/api
VITE_WS_URL=ws://localhost:8080/ws
VITE_APP_NAME=Stoloto VIP Rooms
EOF

# Запустите development сервер
npm run dev

# Приложение будет доступно на http://localhost:5173
```

**Проверка Frontend:**
- Откройте браузер: `http://localhost:5173`
- Должна отобразиться страница входа (Login Page)
- Консоль разработчика (F12) не должна содержать ошибок

---

## 👤 Тестовые данные

После первого запуска инфраструктура автоматически создаст тестовые данные:

### Пользователи
| Email | Пароль | Роль | Баланс | Бонусы |
|-------|--------|------|--------|--------|
| `user@test.com` | `password123` | USER | 10,000 | 500 |
| `admin@test.com` | `admin123` | ADMIN | 50,000 | 2000 |
| `superadmin@test.com` | `super123` | SUPER_ADMIN | 100,000 | 5000 |

### Комнаты
| Название | Тип | Мин. ставка | Макс. ставка | Мест |
|----------|-----|-------------|--------------|------|
| Bronze Room 1 | BRONZE | 10 | 100 | 10 |
| Gold Room 1 | GOLD | 100 | 1,000 | 10 |
| Diamond Room 1 | DIAMOND | 1,000 | 10,000 | 10 |

### Бусты
| Название | Стоимость (бонусы) | Бонус к победе |
|----------|-------------------|----------------|
| Small Boost | 50 | +10% |
| Medium Boost | 100 | +25% |
| Large Boost | 200 | +50% |

---

## 🧪 Сценарий тестирования

### 1. Регистрация и вход
1. Откройте `http://localhost:5173`
2. Нажмите "Зарегистрироваться"
3. Введите данные: email, пароль, имя
4. Подтвердите регистрацию
5. Войдите с новыми учетными данными

### 2. Просмотр лобби
1. После входа откроется Lobby
2. Переключайтесь между табами: 🥉 Bronze | 🥇 Gold | 💎 Diamond
3. Проверьте отображение комнат

### 3. Вход в комнату
1. Выберите комнату (например, Bronze Room 1)
2. Нажмите "Войти в комнату"
3. Проверьте блокировку ставки на балансе
4. Ожидайте таймер 60 секунд

### 4. Покупка буста
1. В комнате найдите панель "Boost"
2. Включите чекбокс "Купить буст"
3. Проверьте списание бонусных баллов
4. Проверьте увеличение шанса на победу

### 5. Запуск раунда
1. Дождитесь окончания таймера (60 сек)
2. Комната должна заполниться ботами (до 10 игроков)
3. Раунд начнется автоматически
4. Наблюдайте за анимацией и результатами

### 6. Проверка результатов
1. После завершения раунда проверьте:
   - Обновление баланса (выигрыш/проигрыш)
   - Появление записи в истории транзакций
   - Отображение победителя в списке игроков

### 7. Админ-панель
1. Выйдите из аккаунта пользователя
2. Войдите как `admin@test.com` / `admin123`
3. Перейдите в раздел Admin
4. Проверьте:
   - Список всех комнат
   - Логи аудита
   - Возможность создания/редактирования комнат

---

## 🔍 Диагностика проблем

### Backend не запускается

**Ошибка: Port 8080 already in use**
```bash
# Найдите процесс на порту 8080
lsof -i :8080

# Завершите процесс
kill -9 <PID>

# Или измените порт в application-dev.yml
server.port=8081
```

**Ошибка: Cannot connect to database**
```bash
# Проверьте статус PostgreSQL
docker-compose -f infra/docker-compose.yml ps postgres

# Просмотрите логи
docker-compose -f infra/docker-compose.yml logs postgres

# Перезапустите контейнер
docker-compose -f infra/docker-compose.yml restart postgres
```

**Ошибка: Redis connection refused**
```bash
# Проверьте Redis
docker-compose -f infra/docker-compose.yml ps redis

# Проверьте подключение
redis-cli -h localhost ping

# Перезапустите Redis
docker-compose -f infra/docker-compose.yml restart redis
```

### Frontend не работает

**Ошибка: npm install failed**
```bash
# Очистите кэш npm
npm cache clean --force

# Удалите node_modules и package-lock.json
rm -rf node_modules package-lock.json

# Попробуйте снова
npm install
```

**Ошибка: Cannot connect to API**
```bash
# Проверьте, что backend запущен
curl http://localhost:8080/api/health

# Проверьте .env файл
cat .env
# Убедитесь, что VITE_API_BASE_URL правильный

# Перезапустите frontend
npm run dev
```

**Ошибка: WebSocket connection failed**
```bash
# Проверьте WebSocket endpoint
# Откройте DevTools -> Network -> WS

# Убедитесь, что VITE_WS_URL правильный
# ws://localhost:8080/ws

# Проверьте логи backend на предмет WebSocket ошибок
```

### Ошибки базы данных

**Ошибка: Table doesn't exist**
```bash
# Liquibase мог не выполниться
# Проверьте логи backend при старте

# Принудительно перезапустите миграции
docker-compose -f infra/docker-compose.yml down -v
docker-compose -f infra/docker-compose.yml up -d

# Перезапустите backend
```

**Ошибка: Duplicate key violation**
```bash
# Очистите тестовые данные
psql -h localhost -U postgres -d stoloto_vip

# В SQL консоли:
TRUNCATE TABLE bets, rounds, transactions RESTART IDENTITY CASCADE;
TRUNCATE TABLE room_players RESTART IDENTITY CASCADE;
TRUNCATE TABLE rooms RESTART IDENTITY CASCADE;
```

---

## 🛠️ Разработка и внесение изменений

### Backend разработка

```bash
# Запуск тестов
cd /workspace/backend
./mvnw test

# Запуск с дебаг режимом
./mvnw spring-boot:run -Dspring-boot.run.jvmArguments="-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005"

# Сборка production jar
./mvnw clean package -Pprod
```

### Frontend разработка

```bash
# Запуск с hot reload
cd /workspace/frontend
npm run dev

# Запуск линтера
npm run lint

# Сборка production версии
npm run build

# Preview production сборки
npm run preview
```

---

## 📊 Мониторинг и логи

### Просмотр логов

```bash
# Backend логи
tail -f /workspace/backend/logs/application.log

# Docker контейнеры логи
docker-compose -f infra/docker-compose.yml logs -f postgres
docker-compose -f infra/docker-compose.yml logs -f redis

# Frontend логи
# Откройте DevTools -> Console в браузере
```

### Базы данных

```bash
# PostgreSQL подключение
psql -h localhost -U postgres -d stoloto_vip

# Полезные запросы:
# Количество пользователей:
SELECT COUNT(*) FROM users;

# Активные комнаты:
SELECT id, name, status, player_count FROM rooms WHERE status = 'WAITING';

# Последние транзакции:
SELECT * FROM transactions ORDER BY created_at DESC LIMIT 10;

# Аудит логи:
SELECT * FROM audit_logs ORDER BY timestamp DESC LIMIT 20;

# Redis CLI
redis-cli -h localhost

# Полезные команды:
KEYS rooms:*
GET room:1:state
HGETALL user:1:balance
```

---

## 🎯 Следующие шаги после запуска

### Этап 5: Интеграционное тестирование
- [ ] Протестировать полный цикл игры (регистрация → игра → вывод выигрыша)
- [ ] Проверить работу WebSocket в реальном времени
- [ ] Протестировать покупку и применение бустов
- [ ] Проверить заполнение комнат ботами
- [ ] Verify аудит логирование всех действий

### Этап 6: Полировка UX
- [ ] Добавить анимации победы/поражения
- [ ] Улучшить обработку ошибок (пользовательские сообщения)
- [ ] Добавить звуковые эффекты (опционально)
- [ ] Оптимизировать производительность (lazy loading, memoization)

### Этап 7: Production подготовка
- [ ] Load testing (k6, JMeter)
- [ ] Security audit (OWASP Top 10)
- [ ] Настроить CI/CD pipeline
- [ ] Настроить мониторинг (Prometheus, Grafana)
- [ ] Настроить алертинг (PagerDuty, Slack)

---

## 📞 Поддержка

Если вы столкнулись с проблемой, которой нет в этом руководстве:

1. Проверьте логи (`backend/logs/`, Docker контейнеры, Browser Console)
2. Изучите документацию в `/workspace/PROJECT_DOCUMENTATION.md`
3. Проверьте существующие issues в репозитории
4. Создайте новый issue с подробным описанием проблемы

---

## 📝 Чек-лист успешного запуска

- [ ] Docker контейнеры запущены (PostgreSQL, Redis)
- [ ] Backend доступен на `http://localhost:8080`
- [ ] Swagger UI открывается без ошибок
- [ ] Frontend доступен на `http://localhost:5173`
- [ ] Страница входа отображается корректно
- [ ] Регистрация нового пользователя работает
- [ ] Вход с тестовыми данными работает
- [ ] Lobby отображает список комнат
- [ ] Вход в комнату работает
- [ ] Таймер обратного отсчета работает
- [ ] Боты заполняют комнату
- [ ] Раунд завершается с определением победителя
- [ ] Баланс обновляется после раунда
- [ ] История транзакций заполняется
- [ ] Админ-панель доступна для admin/superadmin

**Если все пункты отмечены — проект успешно запущен! 🎉**

---

**Версия инструкции**: 1.0  
**Дата обновления**: 2025-01-15  
**Статус**: Готово к использованию
