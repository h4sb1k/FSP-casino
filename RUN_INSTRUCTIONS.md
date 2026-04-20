# 🚀 Stoloto VIP Rooms - Инструкция по запуску

## Быстрый старт

### Вариант 1: Docker (Рекомендуется)

```bash
# Запуск одной командой
./run.sh

# Или явно через Docker
./run.sh --docker
```

**После запуска:**
- Frontend: http://localhost:3000
- Backend: http://localhost:8080
- Swagger API Docs: http://localhost:8080/swagger-ui.html

### Вариант 2: Native (Локально)

```bash
# Автоматический запуск (требует PostgreSQL и Redis)
./run-native.sh

# Или вручную:
# 1. Запустите PostgreSQL на localhost:5432
# 2. Запустите Redis на localhost:6379
# 3. Backend:
cd backend && ./mvnw spring-boot:run

# 4. Frontend (в другом терминале):
cd frontend && npm install && npm run dev
```

**После запуска:**
- Frontend: http://localhost:5173
- Backend: http://localhost:8080

---

## Требования

### Для Docker режима:
- Docker 20.10+
- Docker Compose V2+

### Для Native режима:
- Java 17+
- Node.js 18+
- PostgreSQL 15+
- Redis 7+
- Maven 3.9+ (или использовать mvnw wrapper)

---

## Управление сервисами

```bash
# Остановить все сервисы
./run.sh --stop

# Остановить и удалить данные (Docker)
./run.sh --clean

# Показать справку
./run.sh --help
```

---

## Проверка работоспособности

### 1. Проверка Backend

```bash
# Health check
curl http://localhost:8080/actuator/health

# Swagger UI
Откройте http://localhost:8080/swagger-ui.html
```

### 2. Проверка Frontend

Откройте http://localhost:3000 (Docker) или http://localhost:5173 (Native)

### 3. Проверка WebSocket

Откройте консоль браузера (F12) и проверьте подключение к WebSocket.

---

## Конфигурация баз данных

### PostgreSQL
- **Host:** localhost:5432 (Native) или postgres:5432 (Docker)
- **Database:** stoloto_vip
- **Username:** stoloto_user
- **Password:** stoloto_password_dev

### Redis
- **Host:** localhost:6379 (Native) или redis:6379 (Docker)
- **Port:** 6379
- **Password:** нет (локальная разработка)

---

## Переменные окружения

### Backend (.env или application.yml)

```yaml
SPRING_PROFILES_ACTIVE: dev
SPRING_DATASOURCE_URL: jdbc:postgresql://localhost:5432/stoloto_vip
SPRING_DATASOURCE_USERNAME: stoloto_user
SPRING_DATASOURCE_PASSWORD: stoloto_password_dev
SPRING_REDIS_HOST: localhost
SPRING_REDIS_PORT: 6379
JWT_SECRET: your-secret-key-change-in-production-min-32-chars
JWT_EXPIRATION_MS: 86400000
SERVER_PORT: 8080
```

### Frontend (.env)

```env
VITE_API_BASE_URL=http://localhost:8080/api
VITE_WS_URL=ws://localhost:8080/ws
NODE_ENV=development
```

---

## Troubleshooting

### Ошибка "Connection refused" к PostgreSQL

**Docker:** Убедитесь, что контейнер postgres запущен:
```bash
docker ps | grep stoloto-postgres
```

**Native:** Проверьте, что PostgreSQL запущен:
```bash
pg_isready -h localhost -p 5432
```

### Ошибка "Connection refused" к Redis

**Docker:**
```bash
docker ps | grep stoloto-redis
```

**Native:**
```bash
redis-cli ping
```

### Frontend не подключается к Backend

Проверьте CORS настройки в `application.yml`:
```yaml
cors:
  allowed-origins: http://localhost:3000,http://localhost:5173
```

### Ошибки сборки Maven

Очистите кэш и пересоберите:
```bash
cd backend
./mvnw clean install -U
```

### Ошибки установки npm зависимостей

Удалите node_modules и package-lock.json:
```bash
cd frontend
rm -rf node_modules package-lock.json
npm install
```

---

## Логи

### Backend логи

**Docker:**
```bash
docker logs stoloto-backend -f
```

**Native:** Логи выводятся в консоль при запуске

### Frontend логи

**Docker:**
```bash
docker logs stoloto-frontend -f
```

**Native:** Логи выводятся в консоль при запуске

---

## База данных

### Подключение к PostgreSQL

**Docker:**
```bash
docker exec -it stoloto-postgres psql -U stoloto_user -d stoloto_vip
```

**Native:**
```bash
psql -h localhost -U stoloto_user -d stoloto_vip
```

### Просмотр таблиц

```sql
\dt                    # Список таблиц
SELECT * FROM users;   # Пользователи
SELECT * FROM rooms;   # Комнаты
SELECT * FROM audit_logs; # Аудит
```

### Сброс базы данных

**Docker:**
```bash
./run.sh --clean
./run.sh
```

**Native:**
```sql
DROP SCHEMA public CASCADE;
CREATE SCHEMA public;
```
Затем перезапустите приложение для применения миграций Liquibase.

---

## Тестовые данные

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

## Производительность

### Load Testing

Для тестирования нагрузки используйте Apache JMeter или k6:

```bash
# Пример k6 теста
k6 run load-test.js
```

### Мониторинг

**Backend Actuator:**
- Health: http://localhost:8080/actuator/health
- Metrics: http://localhost:8080/actuator/metrics
- Prometheus: http://localhost:8080/actuator/prometheus

---

## Развертывание в Production

1. Измените `JWT_SECRET` на уникальный ключ
2. Используйте SSL/TLS для всех соединений
3. Настройте external secrets (Vault, AWS Secrets Manager)
4. Включите rate limiting
5. Настройте мониторинг и алертинг

См. `infra/docker-compose.prod.yml` для production конфигурации.

---

## Контакты и поддержка

При возникновении проблем создайте issue в репозитории проекта.
