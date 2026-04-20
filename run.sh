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

show_help() {
    cat << EOF
╔════════════════════════════════════════╗
║   Stoloto VIP Rooms - Справка          ║
╚════════════════════════════════════════╝

Использование:
  ./run.sh [ОПЦИИ]

Опции:
  --docker      Принудительный запуск через Docker
  --native      Принудительный Native режим
  --stop        Остановить все сервисы
  --clean       Остановить и удалить данные
  --help        Показать эту справку

Примеры:
  ./run.sh                  # Авто-выбор режима
  ./run.sh --docker         # Только Docker
  ./run.sh --stop           # Остановить всё

После запуска:
  Frontend:  http://localhost:3000
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

    log_info "Проверка docker-compose..."
    if ! docker compose version &> /dev/null; then
        log_error "docker compose не найден"
        exit 1
    fi

    log_info "Запуск сервисов..."
    docker compose -f docker-compose.yml up --build -d

    log_info "Ожидание готовности (30 сек)..."
    sleep 30

    local attempt=1
    while [ $attempt -le 20 ]; do
        if curl -s http://localhost:8080/actuator/health > /dev/null 2>&1; then
            log_success "Backend готов!"
            break
        fi
        log_info "Попытка $attempt/20..."
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
    log_error "Native режим требует ручной настройки PostgreSQL и Redis"
    log_error "Используйте Docker режим: ./run.sh --docker"
    exit 1
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
