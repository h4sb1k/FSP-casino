# Stoloto VIP Rooms - Infrastructure Setup Guide

## 📋 Содержание

1. [Требования](#требования)
2. [Быстрый старт](#быстрый-старт)
3. [Конфигурация](#конфигурация)
4. [Запуск в development](#запуск-в-development)
5. [Запуск в production](#запуск-в-production)
6. [Мониторинг](#мониторинг)
7. [База данных](#база-данных)
8. [Troubleshooting](#troubleshooting)

---

## Требования

### Обязательные:
- Docker 24.0+
- Docker Compose 2.20+
- Git

### Опциональные (для разработки):
- Java 21 JDK
- Maven 3.9+
- Node.js 20+
- npm 10+

---

## Быстрый старт

### 1. Клонирование и настройка

```bash
cd /workspace

# Скопируйте файл окружения
cp infra/.env.example infra/.env

# Отредактируйте .env файл (обязательно смените пароли!)
nano infra/.env
```

### 2. Запуск всех сервисов

```bash
# Development режим (полный стек)
docker compose -f infra/docker-compose.yml --profile dev up -d

# Или только backend + БД
docker compose -f infra/docker-compose.yml --profile backend-only up -d

# Или только frontend (требуется запущенный backend)
docker compose -f infra/docker-compose.yml --profile frontend-only up -d
```

### 3. Проверка статуса

```bash
docker compose -f infra/docker-compose.yml ps
```

### 4. Доступ к сервисам

| Сервис | URL | Логин/Пароль |
|--------|-----|--------------|
| Frontend | http://localhost:3000 | - |
| Backend API | http://localhost:8080 | - |
| Adminer (DB GUI) | http://localhost:8081 | stoloto_user / stoloto_password_dev |
| RedisInsight | http://localhost:5540 | - |

---

## Конфигурация

### Переменные окружения

Основные переменные в `infra/.env`:

```bash
# Database
POSTGRES_DB=stoloto_vip
POSTGRES_USER=stoloto_admin
POSTGRES_PASSWORD=<secure_password>

# Redis
REDIS_PASSWORD=<secure_password>

# JWT
JWT_SECRET=<min_32_characters_secret>
JWT_EXPIRATION_MS=86400000

# URLs (production)
API_BASE_URL=https://api.yourdomain.com
WS_URL=wss://api.yourdomain.com/ws
```

### Профили Docker Compose

| Профиль | Описание | Сервисы |
|---------|----------|---------|
| `dev` | Полная development среда | postgres, redis, backend, frontend |
| `full` | То же что и dev | postgres, redis, backend, frontend |
| `backend-only` | Только backend с БД | postgres, redis, backend |
| `frontend-only` | Только frontend | frontend |
| `monitoring` | Инструменты мониторинга | redisinsight, adminer |

---

## Запуск в Development

### Полный стек

```bash
docker compose -f infra/docker-compose.yml --profile dev up -d
```

### Просмотр логов

```bash
# Все логи
docker compose -f infra/docker-compose.yml logs -f

# Лог конкретного сервиса
docker compose -f infra/docker-compose.yml logs -f backend
docker compose -f infra/docker-compose.yml logs -f frontend
```

### Перезапуск сервисов

```bash
# Перезапустить backend
docker compose -f infra/docker-compose.yml restart backend

# Пересобрать и перезапустить
docker compose -f infra/docker-compose.yml up -d --build backend
```

### Остановка

```bash
docker compose -f infra/docker-compose.yml down

# С остановкой и удалением volumes (осторожно!)
docker compose -f infra/docker-compose.yml down -v
```

---

## Запуск в Production

### 1. Подготовка

```bash
# Скопируйте production конфиг
cp infra/.env.example infra/.env.prod

# Отредактируйте с production значениями
nano infra/.env.prod
```

### 2. Генерация секретов

```bash
# JWT Secret (минимум 32 символа)
openssl rand -base64 32

# PostgreSQL Password
openssl rand -base64 24

# Redis Password
openssl rand -base64 24
```

### 3. SSL сертификаты (опционально)

```bash
mkdir -p infra/ssl

# Self-signed для тестирования
openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
  -keyout infra/ssl/server.key \
  -out infra/ssl/server.crt \
  -subj "/CN=localhost"
```

### 4. Запуск production

```bash
docker compose -f infra/docker-compose.prod.yml --env-file infra/.env.prod up -d
```

---

## Мониторинг

### RedisInsight

```bash
# Запуск с профилем monitoring
docker compose -f infra/docker-compose.yml --profile monitoring up -d redisinsight

# Доступ: http://localhost:5540
```

### Adminer (PostgreSQL GUI)

```bash
docker compose -f infra/docker-compose.yml --profile monitoring up -d adminer

# Доступ: http://localhost:8081
# Server: postgres
# Username: stoloto_user
# Password: stoloto_password_dev
# Database: stoloto_vip
```

### Health Check

```bash
# Backend health
curl http://localhost:8080/actuator/health

# Frontend health
curl http://localhost:3000/health

# Redis ping
docker exec stoloto-redis redis-cli ping

# PostgreSQL check
docker exec stoloto-postgres pg_isready -U stoloto_user -d stoloto_vip
```

---

## База данных

### Инициализация

При первом запуске автоматически выполняются SQL скрипты из:
- `backend/resources/db/init/01_create_schema.sql` - схема БД
- `backend/resources/db/init/02_seed_data.sql` - тестовые данные

### Подключение к PostgreSQL

```bash
# Через docker exec
docker exec -it stoloto-postgres psql -U stoloto_user -d stoloto_vip

# Или через Adminer: http://localhost:8081
```

### Полезные запросы

```sql
-- Проверка пользователей
SELECT id, username, email, role, balance, bonus_points 
FROM users;

-- Проверка комнат
SELECT id, name, room_type, status, max_players 
FROM rooms;

-- Проверка транзакций
SELECT user_id, transaction_type, amount, created_at 
FROM transactions 
ORDER BY created_at DESC 
LIMIT 10;

-- Аудит логи
SELECT action, entity_type, created_at 
FROM audit_logs 
ORDER BY created_at DESC 
LIMIT 20;
```

### Бэкап базы данных

```bash
# Создать бэкап
docker exec stoloto-postgres pg_dump -U stoloto_user stoloto_vip > backup_$(date +%Y%m%d_%H%M%S).sql

# Восстановить из бэкапа
cat backup_YYYYMMDD_HHMMSS.sql | docker exec -i stoloto-postgres psql -U stoloto_user -d stoloto_vip
```

---

## Troubleshooting

### Backend не запускается

```bash
# Проверить логи
docker compose -f infra/docker-compose.yml logs backend

# Проверить подключение к БД
docker exec stoloto-backend wget -qO- http://postgres:5432 || echo "Postgres not reachable"
```

### Frontend не видит backend

Проверьте переменные окружения в `docker-compose.yml`:
```yaml
VITE_API_BASE_URL: http://localhost:8080/api
VITE_WS_URL: ws://localhost:8080/ws
```

### Redis не подключается

```bash
# Проверить статус Redis
docker exec stoloto-redis redis-cli ping

# Посмотреть логи
docker compose -f infra/docker-compose.yml logs redis
```

### Проблемы с портами

Если порты заняты, измените их в `docker-compose.yml`:
```yaml
ports:
  - "8081:8080"  # Измените первый порт (хост)
```

### Сброс состояния

```bash
# Полная очистка и пересоздание
docker compose -f infra/docker-compose.yml down -v
docker compose -f infra/docker-compose.yml up -d --build
```

---

## Следующие шаги

После успешного запуска инфраструктуры:

1. **Backend разработка**: Перейдите к `/backend/README.md`
2. **Frontend разработка**: Перейдите к `/frontend/README.md`
3. **API документация**: См. `/docs/api/openapi.yaml`

---

## Контакты и поддержка

- Документация: `/docs/`
- Issues: GitHub Issues
- Логи: `./backend/logs/`
