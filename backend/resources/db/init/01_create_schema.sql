-- Stoloto VIP Rooms - Initial Database Schema
-- PostgreSQL 15+

-- Enable UUID extension
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- ==================== USERS ====================
CREATE TABLE users (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    phone VARCHAR(20),
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    bonus_points BIGINT NOT NULL DEFAULT 0,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    last_login_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT chk_role CHECK (role IN ('GUEST', 'USER', 'MODERATOR', 'ADMIN', 'SUPER_ADMIN')),
    CONSTRAINT chk_status CHECK (status IN ('ACTIVE', 'INACTIVE', 'BANNED', 'PENDING'))
);

CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_role ON users(role);
CREATE INDEX idx_users_status ON users(status);

-- ==================== ROOMS ====================
CREATE TABLE rooms (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL,
    description TEXT,
    room_type VARCHAR(50) NOT NULL DEFAULT 'STANDARD',
    status VARCHAR(20) NOT NULL DEFAULT 'INACTIVE',
    min_bet DECIMAL(15, 2) NOT NULL DEFAULT 10.00,
    max_bet DECIMAL(15, 2) NOT NULL DEFAULT 10000.00,
    max_players INTEGER NOT NULL DEFAULT 10,
    round_duration_seconds INTEGER NOT NULL DEFAULT 60,
    bot_enabled BOOLEAN NOT NULL DEFAULT false,
    bot_count INTEGER NOT NULL DEFAULT 0,
    boost_multiplier DECIMAL(5, 2) NOT NULL DEFAULT 1.00,
    created_by UUID REFERENCES users(id),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_room_status CHECK (status IN ('INACTIVE', 'ACTIVE', 'FULL', 'MAINTENANCE')),
    CONSTRAINT chk_room_type CHECK (room_type IN ('STANDARD', 'VIP', 'PREMIUM', 'TOURNAMENT')),
    CONSTRAINT chk_bot_count CHECK (bot_count >= 0 AND bot_count <= 100)
);

CREATE INDEX idx_rooms_status ON rooms(status);
CREATE INDEX idx_rooms_type ON rooms(room_type);

-- ==================== ROUNDS ====================
CREATE TABLE rounds (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    room_id UUID NOT NULL REFERENCES rooms(id) ON DELETE CASCADE,
    round_number INTEGER NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    started_at TIMESTAMP WITH TIME ZONE,
    ended_at TIMESTAMP WITH TIME ZONE,
    winner_id UUID REFERENCES users(id),
    winning_amount DECIMAL(15, 2),
    total_pool DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    house_cut DECIMAL(15, 2) NOT NULL DEFAULT 0.00,
    rng_seed VARCHAR(255),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_round_status CHECK (status IN ('PENDING', 'ACTIVE', 'COMPLETED', 'CANCELLED', 'TIMEOUT'))
);

CREATE INDEX idx_rounds_room ON rounds(room_id);
CREATE INDEX idx_rounds_status ON rounds(status);
CREATE INDEX idx_rounds_created ON rounds(created_at);

-- ==================== BETS ====================
CREATE TABLE bets (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    round_id UUID NOT NULL REFERENCES rounds(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    amount DECIMAL(15, 2) NOT NULL,
    bet_type VARCHAR(50),
    bet_data JSONB,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    payout DECIMAL(15, 2) DEFAULT 0.00,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_bet_status CHECK (status IN ('PENDING', 'ACCEPTED', 'WON', 'LOST', 'REFUNDED'))
);

CREATE INDEX idx_bets_round ON bets(round_id);
CREATE INDEX idx_bets_user ON bets(user_id);
CREATE INDEX idx_bets_status ON bets(status);

-- ==================== TRANSACTIONS ====================
CREATE TABLE transactions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    transaction_type VARCHAR(30) NOT NULL,
    amount DECIMAL(15, 2) NOT NULL,
    balance_before DECIMAL(15, 2) NOT NULL,
    balance_after DECIMAL(15, 2) NOT NULL,
    reference_type VARCHAR(50),
    reference_id UUID,
    description TEXT,
    status VARCHAR(20) NOT NULL DEFAULT 'COMPLETED',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    processed_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT chk_transaction_type CHECK (transaction_type IN ('DEPOSIT', 'WITHDRAWAL', 'BET', 'WIN', 'BONUS', 'ADJUSTMENT', 'REFUND')),
    CONSTRAINT chk_transaction_status CHECK (status IN ('PENDING', 'COMPLETED', 'FAILED', 'CANCELLED'))
);

CREATE INDEX idx_transactions_user ON transactions(user_id);
CREATE INDEX idx_transactions_type ON transactions(transaction_type);
CREATE INDEX idx_transactions_created ON transactions(created_at);

-- ==================== BONUS OPERATIONS ====================
CREATE TABLE bonus_operations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE RESTRICT,
    operation_type VARCHAR(50) NOT NULL,
    points BIGINT NOT NULL,
    balance_before BIGINT NOT NULL,
    balance_after BIGINT NOT NULL,
    reference_type VARCHAR(50),
    reference_id UUID,
    description TEXT,
    expires_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_bonus_operation_type CHECK (operation_type IN ('EARNED', 'REDEEMED', 'EXPIRED', 'ADJUSTMENT', 'TRANSFER'))
);

CREATE INDEX idx_bonus_user ON bonus_operations(user_id);
CREATE INDEX idx_bonus_type ON bonus_operations(operation_type);

-- ==================== AUDIT LOGS ====================
CREATE TABLE audit_logs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    actor_id UUID REFERENCES users(id),
    actor_type VARCHAR(20) NOT NULL DEFAULT 'USER',
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(50) NOT NULL,
    entity_id UUID,
    old_values JSONB,
    new_values JSONB,
    ip_address INET,
    user_agent TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_actor ON audit_logs(actor_id);
CREATE INDEX idx_audit_action ON audit_logs(action);
CREATE INDEX idx_audit_entity ON audit_logs(entity_type, entity_id);
CREATE INDEX idx_audit_created ON audit_logs(created_at);

-- ==================== BOT CONFIGURATIONS ====================
CREATE TABLE bot_configs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL,
    bot_type VARCHAR(50) NOT NULL DEFAULT 'STANDARD',
    strategy VARCHAR(50) NOT NULL DEFAULT 'RANDOM',
    min_bet DECIMAL(15, 2) NOT NULL DEFAULT 10.00,
    max_bet DECIMAL(15, 2) NOT NULL DEFAULT 1000.00,
    activity_level VARCHAR(20) NOT NULL DEFAULT 'MEDIUM',
    win_rate_modifier DECIMAL(5, 4) NOT NULL DEFAULT 0.50,
    enabled BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_activity_level CHECK (activity_level IN ('LOW', 'MEDIUM', 'HIGH', 'AGGRESSIVE')),
    CONSTRAINT chk_strategy CHECK (strategy IN ('RANDOM', 'CONSERVATIVE', 'AGGRESSIVE', 'BALANCED'))
);

CREATE INDEX idx_bot_configs_type ON bot_configs(bot_type);
CREATE INDEX idx_bot_configs_enabled ON bot_configs(enabled);

-- ==================== ROOM BOT ASSIGNMENTS ====================
CREATE TABLE room_bots (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    room_id UUID NOT NULL REFERENCES rooms(id) ON DELETE CASCADE,
    bot_config_id UUID NOT NULL REFERENCES bot_configs(id) ON DELETE RESTRICT,
    bot_name VARCHAR(100) NOT NULL,
    current_balance DECIMAL(15, 2) NOT NULL DEFAULT 1000.00,
    active BOOLEAN NOT NULL DEFAULT true,
    joined_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    left_at TIMESTAMP WITH TIME ZONE,
    UNIQUE(room_id, bot_name)
);

CREATE INDEX idx_room_bots_room ON room_bots(room_id);
CREATE INDEX idx_room_bots_active ON room_bots(active);

-- ==================== BOOST CONFIGURATIONS ====================
CREATE TABLE boost_configs (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    name VARCHAR(100) NOT NULL,
    multiplier DECIMAL(5, 2) NOT NULL DEFAULT 1.00,
    duration_seconds INTEGER NOT NULL DEFAULT 300,
    cost_points BIGINT NOT NULL DEFAULT 0,
    cooldown_seconds INTEGER NOT NULL DEFAULT 600,
    enabled BOOLEAN NOT NULL DEFAULT true,
    applicable_room_types JSONB NOT NULL DEFAULT '["STANDARD", "VIP", "PREMIUM"]',
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT chk_boost_multiplier CHECK (multiplier >= 1.00 AND multiplier <= 10.00)
);

CREATE INDEX idx_boost_configs_enabled ON boost_configs(enabled);

-- ==================== USER BOOSTS (Active) ====================
CREATE TABLE user_boosts (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    boost_config_id UUID NOT NULL REFERENCES boost_configs(id) ON DELETE RESTRICT,
    room_id UUID REFERENCES rooms(id),
    activated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    used BOOLEAN NOT NULL DEFAULT false,
    CONSTRAINT chk_boost_expires CHECK (expires_at > activated_at)
);

CREATE INDEX idx_user_boosts_user ON user_boosts(user_id);
CREATE INDEX idx_user_boosts_expires ON user_boosts(expires_at);

-- ==================== SESSIONS (for JWT blacklist if needed) ====================
CREATE TABLE sessions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash VARCHAR(255) NOT NULL,
    device_info VARCHAR(255),
    ip_address INET,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    revoked BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, token_hash)
);

CREATE INDEX idx_sessions_user ON sessions(user_id);
CREATE INDEX idx_sessions_expires ON sessions(expires_at);

-- ==================== UPDATE TIMESTAMP FUNCTION ====================
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Apply to tables with updated_at
CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_rooms_updated_at BEFORE UPDATE ON rooms
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_bot_configs_updated_at BEFORE UPDATE ON bot_configs
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_boost_configs_updated_at BEFORE UPDATE ON boost_configs
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

COMMENT ON TABLE users IS 'Пользователи системы с балансом и бонусами';
COMMENT ON TABLE rooms IS 'Игровые комнаты с настройками';
COMMENT ON TABLE rounds IS 'Раунды в комнатах';
COMMENT ON TABLE bets IS 'Ставки пользователей в раундах';
COMMENT ON TABLE transactions IS 'Финансовые транзакции пользователей';
COMMENT ON TABLE bonus_operations IS 'Операции с бонусными баллами';
COMMENT ON TABLE audit_logs IS 'Журнал аудита всех действий';
COMMENT ON TABLE bot_configs IS 'Конфигурации ботов для симуляции игроков';
COMMENT ON TABLE room_bots is 'Боты, назначенные в комнаты';
COMMENT ON TABLE boost_configs IS 'Конфигурации бустов (множителей)';
COMMENT ON TABLE user_boosts IS 'Активированные бусты пользователей';
