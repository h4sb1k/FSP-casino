#!/bin/bash

# =============================================================================
# Stoloto VIP Rooms - Универсальный скрипт запуска
# =============================================================================
set -e

# Цвета
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m'

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
INFRA_DIR="$SCRIPT_DIR/infra"

# =============================================================================
# Функции
# =============================================================================

log_info() { echo -e "${BLUE}[INFO]${NC} $1"; }
log_success() { echo -e "${GREEN}[SUCCESS]${NC} $1"; }
log_error() { echo -e "${RED}[ERROR]${NC} $1"; }
log_step() { echo -e "${CYAN}[STEP]${NC} $1"; }

check_docker() {
    command -v docker &> /dev/null && docker --version &> /dev/null
}

check_java() {
    command -v java &> /dev/null
}

check_node() {
    command -v node &> /dev/null
}

show_help() {
    cat << EOF
╔════════════════════════════════════════╗
║   Stoloto VIP Rooms - Справка          ║
╚════════════════════════════════════════╝

Использование:
  ./run.sh [ОПЦИИ]

Опции:
  --docker      Принудительный запуск через Docker
  --native      Принудительный Native режим (локально)
  --stop        Остановить все сервисы
  --clean       Остановить и удалить данные
  --help        Показать эту справку

Примеры:
  ./run.sh                  # Авто-выбор режима
  ./run.sh --docker         # Только Docker
  ./run.sh --native         # Только Native (требует PostgreSQL, Redis, Java, Node)
  ./run.sh --stop           # Остановить всё

После запуска:
  Docker:
    Frontend:  http://localhost:3000
    Backend:   http://localhost:8080
    API Docs:  http://localhost:8080/swagger-ui.html
  
  Native:
    Frontend:  http://localhost:5173
    Backend:   http://localhost:8080
    API Docs:  http://localhost:8080/swagger-ui.html

EOF
}

start_docker() {
    log_step "Запуск в режиме Docker..."
    
    if ! check_docker; then
        log_error "Docker не найден. Установите Docker или используйте --native"
        exit 1
    fi

    cd "$INFRA_DIR"

    # Enable BuildKit for better caching
    export DOCKER_BUILDKIT=1

    log_info "Проверка docker compose..."
    if ! docker compose version &> /dev/null; then
        log_error "docker compose не найден. Установите Docker Compose V2"
        exit 1
    fi

    log_info "Запуск сервисов через docker compose..."
    docker compose -f docker-compose.yml up --build -d postgres redis backend frontend

    log_info "Ожидание готовности (45 сек)..."
    sleep 45

    local attempt=1
    while [ $attempt -le 30 ]; do
        if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
            log_success "Backend готов!"
            break
        fi
        log_info "Попытка $attempt/30..."
        sleep 3
        attempt=$((attempt + 1))
    done

    echo ""
    log_success "=========================================="
    log_success "Stoloto VIP Rooms запущен!"
    log_success "=========================================="
    echo ""
    log_info "Frontend:  ${CYAN}http://localhost:3000${NC}"
    log_info "Backend:   ${CYAN}http://localhost:8080${NC}"
    log_info "API Docs:  ${CYAN}http://localhost:8080/swagger-ui.html${NC}"
    echo ""
    log_info "Логи: ${YELLOW}docker compose -f infra/docker-compose.yml logs -f${NC}"
    log_info "Стоп:   ${YELLOW}./run.sh --stop${NC}"
    echo ""
}

stop_docker() {
    log_step "Остановка Docker сервисов..."
    cd "$INFRA_DIR"
    docker compose -f docker-compose.yml down
    log_success "Сервисы остановлены"
}

clean_docker() {
    log_step "Очистка Docker данных..."
    cd "$INFRA_DIR"
    docker compose -f docker-compose.yml down -v
    log_success "Данные очищены"
}

start_native() {
    log_step "Запуск в Native режиме..."
    
    # Проверка зависимостей
    if ! check_java; then
        log_error "Java не найдена. Установите JDK 17+"
        exit 1
    fi
    
    if ! check_node; then
        log_error "Node.js не найден. Установите Node.js 18+"
        exit 1
    fi
    
    log_success "Все зависимости найдены"
    
    echo ""
    log_info "=========================================="
    log_info "Native режим требует ручной настройки!"
    log_info "=========================================="
    echo ""
    log_info "1. Убедитесь, что PostgreSQL запущен на localhost:5432"
    log_info "   DB: stoloto_vip, User: stoloto_user, Pass: stoloto_password_dev"
    echo ""
    log_info "2. Убедитесь, что Redis запущен на localhost:6379"
    echo ""
    log_info "3. Запустите Backend:"
    log_info "   cd /workspace/backend && ./mvnw spring-boot:run"
    echo ""
    log_info "4. Запустите Frontend:"
    log_info "   cd /workspace/frontend && npm install && npm run dev"
    echo ""
    log_info "Или используйте скрипт run-native.sh для автоматизации:"
    log_info "   ./run-native.sh"
    echo ""
}

# =============================================================================
# Main
# =============================================================================

echo ""
echo "╔════════════════════════════════════════╗"
echo "║   Stoloto VIP Rooms - Запуск системы  ║"
echo "╚════════════════════════════════════════╝"
echo ""

MODE="auto"

for arg in "$@"; do
    case $arg in
        --docker) MODE="docker"; shift ;;
        --native) MODE="native"; shift ;;
        --stop) stop_docker; exit 0 ;;
        --clean) clean_docker; exit 0 ;;
        --help) show_help; exit 0 ;;
        *) log_error "Неизвестная опция: $arg"; show_help; exit 1 ;;
    esac
done

if [ "$MODE" = "auto" ]; then
    if check_docker; then
        log_info "Docker обнаружен. Используем Docker режим."
        start_docker
    else
        log_warn "Docker не найден. Переключаемся на Native режим."
        start_native
    fi
elif [ "$MODE" = "docker" ]; then
    start_docker
elif [ "$MODE" = "native" ]; then
    start_native
fi
