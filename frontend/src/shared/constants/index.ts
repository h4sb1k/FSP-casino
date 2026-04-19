/**
 * Константы приложения Stoloto VIP Rooms
 */

// ============================================
// Роли пользователей
// ============================================

export const USER_ROLES = {
  GUEST: 'GUEST' as const,
  USER: 'USER' as const,
  MODERATOR: 'MODERATOR' as const,
  ADMIN: 'ADMIN' as const,
  SUPER_ADMIN: 'SUPER_ADMIN' as const,
};

export const ROLE_PRIORITY = {
  GUEST: 0,
  USER: 1,
  MODERATOR: 2,
  ADMIN: 3,
  SUPER_ADMIN: 4,
};

// ============================================
// Типы комнат
// ============================================

export const ROOM_TYPES = {
  STANDARD: 'STANDARD' as const,
  VIP: 'VIP' as const,
  PREMIUM: 'PREMIUM' as const,
  TOURNAMENT: 'TOURNAMENT' as const,
};

export const ROOM_TYPE_LABELS: Record<string, string> = {
  STANDARD: 'Стандарт',
  VIP: 'VIP',
  PREMIUM: 'Премиум',
  TOURNAMENT: 'Турнир',
};

export const ROOM_TYPE_COLORS: Record<string, string> = {
  STANDARD: 'bg-gray-500',
  VIP: 'bg-primary-500',
  PREMIUM: 'bg-purple-500',
  TOURNAMENT: 'bg-gold-500',
};

// ============================================
// Статусы комнат
// ============================================

export const ROOM_STATUS = {
  WAITING: 'WAITING' as const,
  ACTIVE: 'ACTIVE' as const,
  FINISHED: 'FINISHED' as const,
};

export const ROOM_STATUS_LABELS: Record<string, string> = {
  WAITING: 'Ожидание',
  ACTIVE: 'Активна',
  FINISHED: 'Завершена',
};

// ============================================
// Статусы раундов
// ============================================

export const ROUND_STATUS = {
  WAITING_FOR_PLAYERS: 'WAITING_FOR_PLAYERS' as const,
  BETTING: 'BETTING' as const,
  IN_PROGRESS: 'IN_PROGRESS' as const,
  CALCULATING: 'CALCULATING' as const,
  FINISHED: 'FINISHED' as const,
};

export const ROUND_STATUS_LABELS: Record<string, string> = {
  WAITING_FOR_PLAYERS: 'Ожидание игроков',
  BETTING: 'Приём ставок',
  IN_PROGRESS: 'В процессе',
  CALCULATING: 'Подсчёт',
  FINISHED: 'Завершён',
};

// ============================================
// Типы транзакций
// ============================================

export const TRANSACTION_TYPES = {
  DEPOSIT: 'DEPOSIT' as const,
  WITHDRAWAL: 'WITHDRAWAL' as const,
  BET: 'BET' as const,
  WIN: 'WIN' as const,
  BOOST_PURCHASE: 'BOOST_PURCHASE' as const,
  ENTRY_FEE: 'ENTRY_FEE' as const,
  REFUND: 'REFUND' as const,
  BONUS: 'BONUS' as const,
  COMMISSION: 'COMMISSION' as const,
};

export const TRANSACTION_TYPE_LABELS: Record<string, string> = {
  DEPOSIT: 'Пополнение',
  WITHDRAWAL: 'Вывод',
  BET: 'Ставка',
  WIN: 'Выигрыш',
  BOOST_PURCHASE: 'Покупка буста',
  ENTRY_FEE: 'Входной взнос',
  REFUND: 'Возврат',
  BONUS: 'Бонус',
  COMMISSION: 'Комиссия',
};

// ============================================
// Статусы транзакций
// ============================================

export const TRANSACTION_STATUS = {
  PENDING: 'PENDING' as const,
  COMPLETED: 'COMPLETED' as const,
  FAILED: 'FAILED' as const,
  CANCELLED: 'CANCELLED' as const,
};

export const TRANSACTION_STATUS_LABELS: Record<string, string> = {
  PENDING: 'В обработке',
  COMPLETED: 'Завершено',
  FAILED: 'Ошибка',
  CANCELLED: 'Отменено',
};

// ============================================
// Поведение ботов
// ============================================

export const BOT_BEHAVIORS = {
  CONSERVATIVE: 'CONSERVATIVE' as const,
  NORMAL: 'NORMAL' as const,
  AGGRESSIVE: 'AGGRESSIVE' as const,
  RANDOM: 'RANDOM' as const,
};

export const BOT_BEHAVIOR_LABELS: Record<string, string> = {
  CONSERVATIVE: 'Консервативный',
  NORMAL: 'Нормальный',
  AGGRESSIVE: 'Агрессивный',
  RANDOM: 'Случайный',
};

// ============================================
// Типы событий аудита
// ============================================

export const AUDIT_EVENT_TYPES = {
  USER_LOGIN: 'USER_LOGIN' as const,
  USER_LOGOUT: 'USER_LOGOUT' as const,
  USER_REGISTER: 'USER_REGISTER' as const,
  ROOM_CREATED: 'ROOM_CREATED' as const,
  ROOM_STARTED: 'ROOM_STARTED' as const,
  ROOM_FINISHED: 'ROOM_FINISHED' as const,
  ROUND_STARTED: 'ROUND_STARTED' as const,
  ROUND_FINISHED: 'ROUND_FINISHED' as const,
  BET_PLACED: 'BET_PLACED' as const,
  BOOST_USED: 'BOOST_USED' as const,
  TRANSACTION_CREATED: 'TRANSACTION_CREATED' as const,
  TRANSACTION_COMPLETED: 'TRANSACTION_COMPLETED' as const,
  BOT_ACTION: 'BOT_ACTION' as const,
  ADMIN_ACTION: 'ADMIN_ACTION' as const,
  CONFIG_CHANGED: 'CONFIG_CHANGED' as const,
  ERROR_OCCURRED: 'ERROR_OCCURRED' as const,
};

// ============================================
// WebSocket типы сообщений
// ============================================

export const WS_MESSAGE_TYPES = {
  ROOM_UPDATE: 'ROOM_UPDATE' as const,
  ROOM_PLAYER_JOINED: 'ROOM_PLAYER_JOINED' as const,
  ROOM_PLAYER_LEFT: 'ROOM_PLAYER_LEFT' as const,
  ROUND_START: 'ROUND_START' as const,
  ROUND_STATUS: 'ROUND_STATUS' as const,
  ROUND_RESULT: 'ROUND_RESULT' as const,
  BET_CONFIRMED: 'BET_CONFIRMED' as const,
  BALANCE_UPDATE: 'BALANCE_UPDATE' as const,
  CHAT_MESSAGE: 'CHAT_MESSAGE' as const,
  ERROR: 'ERROR' as const,
  PING: 'PING' as const,
  PONG: 'PONG' as const,
};

// ============================================
// Типы бонусных операций
// ============================================

export const BONUS_OPERATION_TYPES = {
  WELCOME_BONUS: 'WELCOME_BONUS' as const,
  DEPOSIT_BONUS: 'DEPOSIT_BONUS' as const,
  CASHBACK: 'CASHBACK' as const,
  PROMO_CODE: 'PROMO_CODE' as const,
  REFERRAL: 'REFERRAL' as const,
  COMPENSATION: 'COMPENSATION' as const,
  ADJUSTMENT: 'ADJUSTMENT' as const,
};

export const BONUS_OPERATION_TYPE_LABELS: Record<string, string> = {
  WELCOME_BONUS: 'Приветственный бонус',
  DEPOSIT_BONUS: 'Бонус за депозит',
  CASHBACK: 'Кэшбэк',
  PROMO_CODE: 'Промокод',
  REFERRAL: 'Реферальная программа',
  COMPENSATION: 'Компенсация',
  ADJUSTMENT: 'Корректировка',
};

// ============================================
// Лимиты и настройки по умолчанию
// ============================================

export const LIMITS = {
  // Комнаты
  MIN_ROOM_PLAYERS: 2,
  MAX_ROOM_PLAYERS: 10,
  DEFAULT_ROOM_DURATION_MINUTES: 30,
  
  // Ставки
  MIN_BET_AMOUNT: 10,
  MAX_BET_AMOUNT: 100000,
  
  // Бусты
  MAX_BOOSTS_PER_ROUND: 3,
  BOOST_LEVEL_1_COST: 10,
  BOOST_LEVEL_2_COST: 20,
  BOOST_LEVEL_3_COST: 30,
  BOOST_WIN_CHANCE_BONUS: 5, // %
  
  // Баланс
  MIN_BALANCE: 0,
  MAX_BALANCE: 10000000,
  
  // Пагинация
  DEFAULT_PAGE_SIZE: 20,
  MAX_PAGE_SIZE: 100,
  
  // WebSocket
  PING_INTERVAL_MS: 30000,
  RECONNECT_DELAY_MS: 5000,
  MAX_RECONNECT_ATTEMPTS: 5,
};

// ============================================
// Маршруты приложения
// ============================================

export const ROUTES = {
  // Публичные
  HOME: '/',
  LOGIN: '/login',
  REGISTER: '/register',
  
  // Пользовательские
  DASHBOARD: '/dashboard',
  LOBBY: '/lobby',
  ROOM: '/room/:id',
  PROFILE: '/profile',
  TRANSACTIONS: '/transactions',
  HISTORY: '/history',
  
  // Административные
  ADMIN: '/admin',
  ADMIN_DASHBOARD: '/admin/dashboard',
  ADMIN_ROOMS: '/admin/rooms',
  ADMIN_BOTS: '/admin/bots',
  ADMIN_BOOSTS: '/admin/boosts',
  ADMIN_ECONOMY: '/admin/economy',
  ADMIN_USERS: '/admin/users',
  ADMIN_AUDIT: '/admin/audit',
  ADMIN_ANALYTICS: '/admin/analytics',
} as const;

// ============================================
// Сообщения об ошибках
// ============================================

export const ERROR_MESSAGES = {
  // Аутентификация
  INVALID_CREDENTIALS: 'Неверный email или пароль',
  USER_NOT_FOUND: 'Пользователь не найден',
  EMAIL_ALREADY_EXISTS: 'Email уже зарегистрирован',
  USERNAME_ALREADY_EXISTS: 'Имя пользователя занято',
  WEAK_PASSWORD: 'Пароль должен содержать минимум 8 символов',
  TOKEN_EXPIRED: 'Срок действия токена истёк',
  TOKEN_INVALID: 'Неверный токен',
  
  // Баланс
  INSUFFICIENT_BALANCE: 'Недостаточно средств',
  INSUFFICIENT_BONUS: 'Недостаточно бонусных баллов',
  BET_TOO_LOW: 'Ставка слишком маленькая',
  BET_TOO_HIGH: 'Ставка слишком большая',
  
  // Комнаты
  ROOM_FULL: 'Комната заполнена',
  ROOM_NOT_FOUND: 'Комната не найдена',
  ROOM_ALREADY_STARTED: 'Игра уже началась',
  NOT_IN_ROOM: 'Вы не в этой комнате',
  
  // Бусты
  BOOST_LIMIT_REACHED: 'Достигнут лимит использования бустов',
  BOOST_NOT_AVAILABLE: 'Буст недоступен',
  
  // Общие
  NETWORK_ERROR: 'Ошибка сети. Проверьте подключение.',
  SERVER_ERROR: 'Ошибка сервера. Попробуйте позже.',
  UNAUTHORIZED: 'Требуется авторизация',
  FORBIDDEN: 'Нет доступа к этому ресурсу',
  NOT_FOUND: 'Ресурс не найден',
  VALIDATION_ERROR: 'Ошибка валидации данных',
} as const;

// ============================================
// Успешные сообщения
// ============================================

export const SUCCESS_MESSAGES = {
  LOGIN_SUCCESS: 'Вход выполнен успешно',
  LOGOUT_SUCCESS: 'Выход выполнен успешно',
  REGISTER_SUCCESS: 'Регистрация успешна',
  BET_PLACED: 'Ставка принята',
  BOOST_ACTIVATED: 'Буст активирован',
  ROOM_JOINED: 'Вы присоединились к комнате',
  ROOM_LEFT: 'Вы покинули комнату',
  PROFILE_UPDATED: 'Профиль обновлён',
  SETTINGS_SAVED: 'Настройки сохранены',
} as const;

// ============================================
// Форматирование
// ============================================

export const DATE_FORMATS = {
  DATETIME: 'dd.MM.yyyy HH:mm',
  DATE: 'dd.MM.yyyy',
  TIME: 'HH:mm',
  RELATIVE: 'relative',
};

export const NUMBER_FORMATS = {
  CURRENCY: 'ru-RU',
  DECIMAL: 'ru-RU',
  PERCENT: 'ru-RU',
};

// ============================================
// API endpoints (базовые пути)
// ============================================

export const API_ENDPOINTS = {
  AUTH: '/api/auth',
  USERS: '/api/users',
  ROOMS: '/api/rooms',
  ROUNDS: '/api/rounds',
  BETS: '/api/bets',
  TRANSACTIONS: '/api/transactions',
  BONUSES: '/api/bonuses',
  BOOSTS: '/api/boosts',
  BOTS: '/api/bots',
  ADMIN: '/api/admin',
  AUDIT: '/api/audit',
  ANALYTICS: '/api/analytics',
} as const;

// ============================================
// WebSocket каналы
// ============================================

export const WS_CHANNELS = {
  ROOMS: 'rooms',
  ROUNDS: 'rounds',
  NOTIFICATIONS: 'notifications',
  CHAT: 'chat',
  ADMIN: 'admin',
} as const;
