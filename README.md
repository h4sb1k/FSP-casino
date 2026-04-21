# Stoloto VIP Rooms

Система управления VIP-комнатами для платформы Stoloto. Предоставляет интерфейс для администрирования игровых комнат, управления пользователями, мониторинга транзакций и настройки ботов.

## 🚀 Быстрый старт

### Требования
- Docker и Docker Compose
- Java 21 (для локальной разработки)
- Node.js 20+ (для локальной разработки фронтенда)

### Запуск через Docker (Рекомендуемый способ)

```bash
# Клонирование репозитория
git clone <repository-url>
cd stoloto-vip

# Запуск всех сервисов
sudo ./run.sh
```

Скрипт автоматически:
1. Проверит наличие Docker
2. Соберет образы backend и frontend
3. Запустит контейнеры PostgreSQL, Redis, Backend и Frontend

После успешного запуска сервисы будут доступны по адресам:
- **Frontend:** http://localhost:80
- **Backend API:** http://localhost:8080
- **Swagger UI:** http://localhost:8080/swagger-ui.html

### Остановка и очистка

```bash
# Остановить сервисы
sudo docker compose -f infra/docker-compose.yml down

# Полная очистка (удаление контейнеров, сетей и томов с данными)
sudo docker compose -f infra/docker-compose.yml down -v
```

## 🔐 Доступ к системе

Используйте следующие учетные данные для входа в систему (данные создаются при первом запуске через Liquibase):

| Роль | Email / Логин | Пароль | Описание |
|------|---------------|--------|----------|
| **Super Admin** | `admin@stoloto.test` | `password123` | Полный доступ ко всем функциям системы |
| **Moderator** | `moderator@stoloto.test` | `password123` | Управление комнатами и пользователями |
| **User** | `player1@stoloto.test` | `password123` | Стандартный пользователь |
| **User** | `player2@stoloto.test` | `password123` | Стандартный пользователь |
| **User** | `player3@stoloto.test` | `password123` | Стандартный пользователь |

> **Примечание:** При входе используйте поле **Email**.

## 🏗 Архитектура проекта

Проект использует микросервисную архитектуру, развертываемую в Docker-контейнерах:

- **Frontend (`frontend/`)**:
  - React + TypeScript + Vite
  - TailwindCSS для стилизации
  - Axios для HTTP-запросов
  - Nginx в качестве веб-сервера в продакшене

- **Backend (`backend/`)**:
  - Spring Boot 3.2.0 (Java 21)
  - Spring Data JPA (Hibernate)
  - Spring Security (JWT аутентификация)
  - Liquibase (управление миграциями БД)
  - Maven для сборки

- **Базы данных и кэш**:
  - **PostgreSQL**: Основное хранилище данных
  - **Redis**: Кэширование сессий и временных данных

## 📂 Структура проекта

```text
.
├── backend/                 # Исходный код backend (Spring Boot)
│   ├── src/main/java/       # Java классы
│   ├── src/main/resources/  # Конфигурации и миграции Liquibase
│   └── pom.xml              # Maven зависимости
├── frontend/                # Исходный код frontend (React)
│   ├── src/                 # React компоненты и хуки
│   ├── package.json         # NPM зависимости
│   └── vite.config.ts       # Конфигурация Vite
├── infra/                   # Инфраструктурные файлы
│   ├── docker-compose.yml   # Оркестрация контейнеров
│   └── docker/              # Dockerfile и конфиги (nginx.conf)
├── run.sh                   # Скрипт запуска проекта
└── README.md                # Документация
```

## 🛠 Локальная разработка

### Backend

```bash
cd backend
./mvnw spring-boot:run -Dspring-boot.run.profiles=local
```
*Требуется локально запущенные PostgreSQL и Redis.*

### Frontend

```bash
cd frontend
npm install
npm run dev
```
*Запустится на http://localhost:5173. Необходимо настроить прокси на backend.*

## 🔧 Настройка конфигурации

Основные параметры настраиваются через переменные окружения в `infra/docker-compose.yml` или в файлах `application.yml`:

- **База данных:** URL, пользователь, пароль (по умолчанию: `postgres` / `postgres`)
- **Redis:** Хост и порт (по умолчанию: `redis` / `6379`)
- **JWT Secret:** Ключ для подписи токенов (необходимо изменить в продакшене!)

## ❓ Решение проблем (Troubleshooting)

### Контейнер backend не запускается
1. Проверьте логи:
   ```bash
   sudo docker logs stoloto-backend
   ```
2. Частая причина: проблема с миграциями Liquibase. Попробуйте очистить базу данных:
   ```bash
   sudo docker compose -f infra/docker-compose.yml down -v
   sudo ./run.sh
   ```

### Ошибка "Connection refused" к базе данных
Убедитесь, что контейнер `stoloto-postgres` находится в статусе `healthy`:
```bash
sudo docker ps
```
Если нет, проверьте его логи: `sudo docker logs stoloto-postgres`.

### Проблемы со сборкой Maven/NPM
Очистите кэш Docker и пересоберите образы:
```bash
sudo docker builder prune -a
sudo ./run.sh --clean
```

### Ошибки аутентификации
Убедитесь, что используете правильный **Email** (например, `admin@stoloto.test`), а не username. Пароль по умолчанию: `password123`.

## 📄 Лицензия

Проект разработан для внутренней использования в рамках платформы Stoloto.
