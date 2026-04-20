#!/bin/bash

# Stoloto VIP Rooms - Native запуск (без Docker)
set -e

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'

log_info() { echo -e "${BLUE}[INFO]${NC} $1"; }
log_success() { echo -e "${GREEN}[SUCCESS]${NC} $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }
log_step() { echo -e "${CYAN}[STEP]${NC} $1"; }

echo ""
echo "╔════════════════════════════════════════╗"
echo "║ Stoloto VIP Rooms - Native запуск     ║"
echo "╚════════════════════════════════════════╝"
echo ""

# Проверка зависимостей
log_step "Проверка зависимостей..."

if ! command -v java &> /dev/null; then
    log_error "Java не найдена. Установите JDK 17+"
    exit 1
fi

if ! command -v node &> /dev/null; then
    log_error "Node.js не найден. Установите Node.js 18+"
    exit 1
fi

if ! command -v psql &> /dev/null; then
    log_error "PostgreSQL client не найден. Установите PostgreSQL"
    exit 1
fi

log_success "Все зависимости найдены"

# Запуск PostgreSQL и Redis (предполагается, что они уже запущены локально)
log_info "Убедитесь, что PostgreSQL и Redis запущены локально"
log_info "PostgreSQL: localhost:5432, Redis: localhost:6379"

# Сборка и запуск Backend
log_step "Сборка Backend..."
cd /workspace/backend

if [ ! -f "mvnw" ]; then
    log_error "Maven wrapper не найден. Установите Maven или создайте wrapper"
    exit 1
fi

chmod +x mvnw
./mvnw clean package -DskipTests -q

log_step "Запуск Backend..."
java -jar target/*.jar &
BACKEND_PID=$!
log_info "Backend запущен (PID: $BACKEND_PID)"

# Ожидание старта Backend
log_info "Ожидание запуска Backend (15 сек)..."
sleep 15

# Установка и запуск Frontend
log_step "Установка зависимостей Frontend..."
cd /workspace/frontend
npm install --silent

log_step "Запуск Frontend..."
npm run dev &
FRONTEND_PID=$!
log_info "Frontend запущен (PID: $FRONTEND_PID)"

echo ""
log_success "=========================================="
log_success "Stoloto VIP Rooms запущен в Native режиме!"
log_success "=========================================="
echo ""
log_info "Frontend: http://localhost:5173"
log_info "Backend:  http://localhost:8080"
log_info "Swagger:  http://localhost:8080/swagger-ui.html"
echo ""
log_info "Для остановки нажмите Ctrl+C"

# Ожидание завершения
wait
