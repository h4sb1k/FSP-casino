-- Stoloto VIP Rooms - Seed Data (Initial Test Data)
-- PostgreSQL 15+

-- ==================== DEFAULT ADMIN USER ====================
-- Password: admin123 (BCrypt hash for demo purposes)
INSERT INTO users (id, username, email, password_hash, first_name, last_name, role, status, balance, bonus_points)
VALUES 
    ('00000000-0000-0000-0000-000000000001', 'admin', 'admin@stoloto-vip.ru', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'System', 'Administrator', 'SUPER_ADMIN', 'ACTIVE', 10000.00, 5000),
    ('00000000-0000-0000-0000-000000000002', 'moderator', 'mod@stoloto-vip.ru', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'John', 'Moderator', 'MODERATOR', 'ACTIVE', 5000.00, 2000),
    ('00000000-0000-0000-0000-000000000003', 'user1', 'user1@test.ru', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Ivan', 'Petrov', 'USER', 'ACTIVE', 1000.00, 500),
    ('00000000-0000-0000-0000-000000000004', 'user2', 'user2@test.ru', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'Maria', 'Sidorova', 'USER', 'ACTIVE', 2500.00, 750);

-- ==================== BOOST CONFIGURATIONS ====================
INSERT INTO boost_configs (id, name, multiplier, duration_seconds, cost_points, cooldown_seconds, enabled, applicable_room_types)
VALUES
    ('10000000-0000-0000-0000-000000000001', 'x2 Boost', 2.00, 300, 100, 600, true, '["STANDARD", "VIP"]'),
    ('10000000-0000-0000-0000-000000000002', 'x3 Boost', 3.00, 180, 250, 900, true, '["VIP", "PREMIUM"]'),
    ('10000000-0000-0000-0000-000000000003', 'x5 Premium', 5.00, 120, 500, 1800, true, '["PREMIUM", "TOURNAMENT"]'),
    ('10000000-0000-0000-0000-000000000004', 'x1.5 Standard', 1.50, 600, 50, 300, true, '["STANDARD"]');

-- ==================== BOT CONFIGURATIONS ====================
INSERT INTO bot_configs (id, name, bot_type, strategy, min_bet, max_bet, activity_level, win_rate_modifier, enabled)
VALUES
    ('20000000-0000-0000-0000-000000000001', 'Conservative Bot', 'STANDARD', 'CONSERVATIVE', 10.00, 100.00, 'LOW', 0.45, true),
    ('20000000-0000-0000-0000-000000000002', 'Aggressive Bot', 'STANDARD', 'AGGRESSIVE', 50.00, 500.00, 'HIGH', 0.55, true),
    ('20000000-0000-0000-0000-000000000003', 'Balanced Bot', 'STANDARD', 'BALANCED', 25.00, 250.00, 'MEDIUM', 0.50, true),
    ('20000000-0000-0000-0000-000000000004', 'Random Bot', 'STANDARD', 'RANDOM', 10.00, 1000.00, 'MEDIUM', 0.48, true),
    ('20000000-0000-0000-0000-000000000005', 'VIP Conservative', 'VIP', 'CONSERVATIVE', 100.00, 1000.00, 'LOW', 0.47, true),
    ('20000000-0000-0000-0000-000000000006', 'VIP Aggressive', 'VIP', 'AGGRESSIVE', 200.00, 2000.00, 'HIGH', 0.53, true);

-- ==================== ROOMS ====================
INSERT INTO rooms (id, name, description, room_type, status, min_bet, max_bet, max_players, round_duration_seconds, bot_enabled, bot_count, boost_multiplier, created_by)
VALUES
    ('30000000-0000-0000-0000-000000000001', 'Standard Room #1', 'Базовая комната для новых игроков', 'STANDARD', 'ACTIVE', 10.00, 500.00, 10, 60, true, 3, 1.00, '00000000-0000-0000-0000-000000000001'),
    ('30000000-0000-0000-0000-000000000002', 'Standard Room #2', 'Ещё одна стандартная комната', 'STANDARD', 'ACTIVE', 20.00, 1000.00, 8, 90, true, 2, 1.00, '00000000-0000-0000-0000-000000000001'),
    ('30000000-0000-0000-0000-000000000003', 'VIP Lounge', 'Эксклюзивная комната для VIP игроков', 'VIP', 'ACTIVE', 100.00, 5000.00, 6, 120, true, 2, 1.50, '00000000-0000-0000-0000-000000000002'),
    ('30000000-0000-0000-0000-000000000004', 'Premium Hall', 'Премиум комната с повышенными ставками', 'PREMIUM', 'ACTIVE', 500.00, 10000.00, 5, 180, false, 0, 2.00, '00000000-0000-0000-0000-000000000001'),
    ('30000000-0000-0000-0000-000000000005', 'Tournament Arena', 'Турнирная арена для соревнований', 'TOURNAMENT', 'INACTIVE', 1000.00, 50000.00, 20, 300, false, 0, 3.00, '00000000-0000-0000-0000-000000000001');

-- ==================== ROOM BOTS ASSIGNMENT ====================
-- Assign bots to Standard Room #1
INSERT INTO room_bots (room_id, bot_config_id, bot_name, current_balance, active)
SELECT 
    '30000000-0000-0000-0000-000000000001',
    id,
    'Bot_' || SUBSTRING(id::text FROM 1 FOR 8),
    1000.00,
    true
FROM bot_configs 
WHERE strategy IN ('CONSERVATIVE', 'BALANCED', 'RANDOM')
LIMIT 3;

-- Assign bots to Standard Room #2
INSERT INTO room_bots (room_id, bot_config_id, bot_name, current_balance, active)
SELECT 
    '30000000-0000-0000-0000-000000000002',
    id,
    'Bot_' || SUBSTRING(id::text FROM 1 FOR 8),
    1500.00,
    true
FROM bot_configs 
WHERE strategy IN ('AGGRESSIVE', 'BALANCED')
LIMIT 2;

-- Assign bots to VIP Lounge
INSERT INTO room_bots (room_id, bot_config_id, bot_name, current_balance, active)
SELECT 
    '30000000-0000-0000-0000-000000000003',
    id,
    'VIP_Bot_' || SUBSTRING(id::text FROM 1 FOR 8),
    5000.00,
    true
FROM bot_configs 
WHERE bot_type = 'VIP'
LIMIT 2;

-- ==================== SAMPLE TRANSACTIONS ====================
INSERT INTO transactions (user_id, transaction_type, amount, balance_before, balance_after, description, status)
VALUES
    -- Admin initial deposit
    ('00000000-0000-0000-0000-000000000001', 'DEPOSIT', 10000.00, 0.00, 10000.00, 'Initial admin balance', 'COMPLETED'),
    -- Moderator initial deposit
    ('00000000-0000-0000-0000-000000000002', 'DEPOSIT', 5000.00, 0.00, 5000.00, 'Initial moderator balance', 'COMPLETED'),
    -- User1 initial deposit
    ('00000000-0000-0000-0000-000000000003', 'DEPOSIT', 1000.00, 0.00, 1000.00, 'Initial user balance', 'COMPLETED'),
    -- User2 initial deposit
    ('00000000-0000-0000-0000-000000000004', 'DEPOSIT', 2500.00, 0.00, 2500.00, 'Initial user balance', 'COMPLETED'),
    -- User1 bonus
    ('00000000-0000-0000-0000-000000000003', 'BONUS', 100.00, 1000.00, 1100.00, 'Welcome bonus', 'COMPLETED');

-- ==================== SAMPLE BONUS OPERATIONS ====================
INSERT INTO bonus_operations (user_id, operation_type, points, balance_before, balance_after, description)
VALUES
    ('00000000-0000-0000-0000-000000000001', 'EARNED', 5000, 0, 5000, 'Admin welcome bonus'),
    ('00000000-0000-0000-0000-000000000002', 'EARNED', 2000, 0, 2000, 'Moderator welcome bonus'),
    ('00000000-0000-0000-0000-000000000003', 'EARNED', 500, 0, 500, 'User welcome bonus'),
    ('00000000-0000-0000-0000-000000000004', 'EARNED', 750, 0, 750, 'User welcome bonus');

-- ==================== SAMPLE AUDIT LOG ====================
INSERT INTO audit_logs (actor_id, actor_type, action, entity_type, entity_id, description, ip_address)
VALUES
    ('00000000-0000-0000-0000-000000000001', 'USER', 'SYSTEM_INIT', 'SYSTEM', NULL, 'System initialization and seed data loaded', '127.0.0.1'::INET),
    ('00000000-0000-0000-0000-000000000001', 'USER', 'ROOM_CREATED', 'ROOM', '30000000-0000-0000-0000-000000000001', 'Created Standard Room #1', '127.0.0.1'::INET),
    ('00000000-0000-0000-0000-000000000001', 'USER', 'ROOM_CREATED', 'ROOM', '30000000-0000-0000-0000-000000000002', 'Created Standard Room #2', '127.0.0.1'::INET),
    ('00000000-0000-0000-0000-000000000002', 'USER', 'ROOM_CREATED', 'ROOM', '30000000-0000-0000-0000-000000000003', 'Created VIP Lounge', '127.0.0.1'::INET);

-- Update sequences (if needed for future inserts)
-- Note: Since we're using UUIDs, sequences are not needed for primary keys
