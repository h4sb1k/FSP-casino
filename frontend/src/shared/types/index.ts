/**
 * Основные типы данных приложения Stoloto VIP Rooms
 */

// ============================================
// Пользователь и аутентификация
// ============================================

export type UserRole = 'GUEST' | 'USER' | 'MODERATOR' | 'ADMIN' | 'SUPER_ADMIN';

export interface User {
  id: string;
  username: string;
  email: string;
  firstName?: string;
  lastName?: string;
  avatarUrl?: string;
  role: UserRole;
  balance: number;
  bonusBalance: number;
  isBot: boolean;
  isActive: boolean;
  createdAt: string;
  lastLoginAt?: string;
}

export interface AuthTokens {
  accessToken: string;
  refreshToken: string;
  expiresIn: number;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  username: string;
  firstName?: string;
  lastName?: string;
}

export interface AuthState {
  user: User | null;
  tokens: AuthTokens | null;
  isAuthenticated: boolean;
  isLoading: boolean;
}

// ============================================
// Комнаты и игры
// ============================================

export type RoomStatus = 'WAITING' | 'ACTIVE' | 'FINISHED';
export type RoomType = 'BRONZE' | 'GOLD' | 'DIAMOND';

export interface Room {
  id: string;
  name: string;
  type: RoomType;
  status: RoomStatus;
  minBet: number;
  maxBet: number;
  maxPlayers: number;
  currentPlayers: number;
  entryFee: number;
  prizePool: number;
  botCount: number;
  createdAt: string;
  startedAt?: string;
  finishedAt?: string;
}

export interface RoomPlayer {
  id: string;
  userId: string;
  username: string;
  avatarUrl?: string;
  isBot: boolean;
  balance: number;
  betAmount?: number;
  hasBoost: boolean;
  boostLevel: number;
  joinedAt: string;
}

export interface RoomConfig {
  id: string;
  name: string;
  type: RoomType;
  minBet: number;
  maxBet: number;
  maxPlayers: number;
  entryFee: number;
  botMinCount: number;
  botMaxCount: number;
  roundDurationSeconds: number;
  isActive: boolean;
}

// ============================================
// Раунды и ставки
// ============================================

export type RoundStatus = 'WAITING_FOR_PLAYERS' | 'BETTING' | 'IN_PROGRESS' | 'CALCULATING' | 'FINISHED';

export interface Round {
  id: string;
  roomId: string;
  roundNumber: number;
  status: RoundStatus;
  startTime: string;
  endTime?: string;
  totalBets: number;
  playersCount: number;
  winnerId?: string;
  winningAmount?: number;
}

export interface Bet {
  id: string;
  roundId: string;
  userId: string;
  amount: number;
  boostUsed: boolean;
  boostLevel: number;
  boostCost: number;
  winAmount?: number;
  isWinner: boolean;
  createdAt: string;
}

// ============================================
// Бусты
// ============================================

export interface BoostConfig {
  id: string;
  name: string;
  level: number;
  winChanceBonus: number; // процент (например, 5 = +5%)
  baseCost: number;
  costMultiplier: number; // множитель стоимости для следующего применения
  maxUsesPerRound: number;
  description: string;
  isActive: boolean;
}

export interface UserBoost {
  id: string;
  userId: string;
  boostConfigId: string;
  roundId: string;
  level: number;
  cost: number;
  usedAt: string;
}

export interface BoostUsage {
  level: number;
  cost: number;
  winChanceBonus: number;
  remainingUses: number;
}

// ============================================
// Боты
// ============================================

export type BotBehavior = 'CONSERVATIVE' | 'NORMAL' | 'AGGRESSIVE' | 'RANDOM';

export interface BotConfig {
  id: string;
  name: string;
  behavior: BotBehavior;
  minBetPercent: number; // % от баланса
  maxBetPercent: number;
  boostProbability: number; // вероятность использования буста
  avgResponseTimeMs: number; // среднее время реакции
  isActive: boolean;
  description: string;
}

export interface BotStats {
  totalGames: number;
  wins: number;
  losses: number;
  totalWon: number;
  totalLost: number;
  winRate: number;
}

// ============================================
// Транзакции
// ============================================

export type TransactionType = 
  | 'DEPOSIT'
  | 'WITHDRAWAL'
  | 'BET'
  | 'WIN'
  | 'BOOST_PURCHASE'
  | 'ENTRY_FEE'
  | 'REFUND'
  | 'BONUS'
  | 'COMMISSION';

export type TransactionStatus = 'PENDING' | 'COMPLETED' | 'FAILED' | 'CANCELLED';

export interface Transaction {
  id: string;
  userId: string;
  type: TransactionType;
  amount: number;
  balanceBefore: number;
  balanceAfter: number;
  status: TransactionStatus;
  description?: string;
  metadata?: Record<string, unknown>;
  createdAt: string;
  processedAt?: string;
}

// ============================================
// Бонусы
// ============================================

export type BonusOperationType = 
  | 'WELCOME_BONUS'
  | 'DEPOSIT_BONUS'
  | 'CASHBACK'
  | 'PROMO_CODE'
  | 'REFERRAL'
  | 'COMPENSATION'
  | 'ADJUSTMENT';

export interface BonusOperation {
  id: string;
  userId: string;
  type: BonusOperationType;
  amount: number;
  balanceBefore: number;
  balanceAfter: number;
  description?: string;
  expiresAt?: string;
  createdAt: string;
}

// ============================================
// Аудит и логи
// ============================================

export type AuditEventType = 
  | 'USER_LOGIN'
  | 'USER_LOGOUT'
  | 'USER_REGISTER'
  | 'ROOM_CREATED'
  | 'ROOM_STARTED'
  | 'ROOM_FINISHED'
  | 'ROUND_STARTED'
  | 'ROUND_FINISHED'
  | 'BET_PLACED'
  | 'BOOST_USED'
  | 'TRANSACTION_CREATED'
  | 'TRANSACTION_COMPLETED'
  | 'BOT_ACTION'
  | 'ADMIN_ACTION'
  | 'CONFIG_CHANGED'
  | 'ERROR_OCCURRED';

export interface AuditLog {
  id: string;
  eventType: AuditEventType;
  userId?: string;
  username?: string;
  roomId?: string;
  roundId?: string;
  transactionId?: string;
  description: string;
  metadata?: Record<string, unknown>;
  ipAddress?: string;
  userAgent?: string;
  createdAt: string;
}

// ============================================
// API ответы
// ============================================

export interface ApiResponse<T> {
  success: boolean;
  data?: T;
  error?: ApiError;
  timestamp: string;
}

export interface ApiError {
  code: string;
  message: string;
  details?: Record<string, string[]>;
}

export interface PaginatedResponse<T> {
  items: T[];
  total: number;
  page: number;
  pageSize: number;
  totalPages: number;
}

// ============================================
// WebSocket сообщения
// ============================================

export type WsMessageType =
  | 'ROOM_UPDATE'
  | 'ROOM_PLAYER_JOINED'
  | 'ROOM_PLAYER_LEFT'
  | 'ROUND_START'
  | 'ROUND_STATUS'
  | 'ROUND_RESULT'
  | 'BET_CONFIRMED'
  | 'BALANCE_UPDATE'
  | 'CHAT_MESSAGE'
  | 'ERROR'
  | 'PING'
  | 'PONG';

export interface WsMessage<T = unknown> {
  type: WsMessageType;
  payload: T;
  timestamp: string;
}

export interface RoomUpdatePayload {
  roomId: string;
  status: RoomStatus;
  players: RoomPlayer[];
  currentRound?: Round;
}

export interface RoundStartPayload {
  roomId: string;
  roundId: string;
  roundNumber: number;
  startTime: string;
  bettingEndsAt: string;
  players: RoomPlayer[];
}

export interface RoundResultPayload {
  roomId: string;
  roundId: string;
  winnerId: string;
  winningAmount: number;
  bets: Bet[];
}

export interface BalanceUpdatePayload {
  userId: string;
  balance: number;
  bonusBalance: number;
  change: number;
  reason: string;
}

// ============================================
// Фильтры и параметры запросов
// ============================================

export interface RoomFilters {
  type?: RoomType;
  status?: RoomStatus;
  minEntryFee?: number;
  maxEntryFee?: number;
  minPlayers?: number;
  maxPlayers?: number;
  search?: string;
}

export interface PaginationParams {
  page: number;
  pageSize: number;
  sortBy?: string;
  sortOrder?: 'asc' | 'desc';
}

export interface TransactionFilters {
  type?: TransactionType;
  status?: TransactionStatus;
  dateFrom?: string;
  dateTo?: string;
  minAmount?: number;
  maxAmount?: number;
}

// ============================================
// Статистика и аналитика
// ============================================

export interface UserStats {
  totalGames: number;
  totalWins: number;
  totalLosses: number;
  winRate: number;
  totalBet: number;
  totalWon: number;
  totalLost: number;
  netProfit: number;
  avgBet: number;
  biggestWin: number;
}

export interface RoomStats {
  totalRooms: number;
  activeRooms: number;
  totalPlayers: number;
  totalRounds: number;
  totalPrizePool: number;
  avgRoomOccupancy: number;
}

export interface DashboardStats {
  totalUsers: number;
  activeUsers: number;
  totalRooms: number;
  activeRooms: number;
  totalTransactions24h: number;
  volume24h: number;
  revenue24h: number;
  topRooms: Room[];
  recentTransactions: Transaction[];
}

// ============================================
// Настройки приложения
// ============================================

export interface AppSettings {
  maintenanceMode: boolean;
  registrationEnabled: boolean;
  minDeposit: number;
  maxDeposit: number;
  minWithdrawal: number;
  maxWithdrawal: number;
  withdrawalFeePercent: number;
  defaultBonusPercent: number;
  referralBonusPercent: number;
  cashbackPercent: number;
}

export interface EconomicRules {
  minBet: number;
  maxBet: number;
  houseEdgePercent: number;
  maxWinMultiplier: number;
  boostCommissionPercent: number;
  roomCreationFee: number;
}
