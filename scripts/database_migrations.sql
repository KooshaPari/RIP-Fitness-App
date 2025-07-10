-- Database Migration Scripts for Production Deployment
-- Version: 1.0 -> 2.0
-- Purpose: Optimize database schema for production performance

-- Migration 1: Add database indices for performance
-- =================================================

-- User table optimizations
CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);
CREATE INDEX IF NOT EXISTS idx_users_created_at ON users(created_at);
CREATE INDEX IF NOT EXISTS idx_users_last_sync ON users(last_sync_timestamp);

-- Workout related indices
CREATE INDEX IF NOT EXISTS idx_workouts_user_id ON workouts(user_id);
CREATE INDEX IF NOT EXISTS idx_workouts_date ON workouts(date);
CREATE INDEX IF NOT EXISTS idx_workouts_user_date ON workouts(user_id, date);

CREATE INDEX IF NOT EXISTS idx_workout_exercises_workout_id ON workout_exercises(workout_id);
CREATE INDEX IF NOT EXISTS idx_workout_exercises_exercise_id ON workout_exercises(exercise_id);

CREATE INDEX IF NOT EXISTS idx_workout_sets_exercise_id ON workout_sets(workout_exercise_id);

-- Nutrition related indices
CREATE INDEX IF NOT EXISTS idx_meals_user_id ON meals(user_id);
CREATE INDEX IF NOT EXISTS idx_meals_date ON meals(date);
CREATE INDEX IF NOT EXISTS idx_meals_user_date ON meals(user_id, date);

CREATE INDEX IF NOT EXISTS idx_meal_foods_meal_id ON meal_foods(meal_id);
CREATE INDEX IF NOT EXISTS idx_meal_foods_food_id ON meal_foods(food_id);

-- Routine related indices
CREATE INDEX IF NOT EXISTS idx_routines_user_id ON routines(user_id);
CREATE INDEX IF NOT EXISTS idx_routine_exercises_routine_id ON routine_exercises(routine_id);

-- Sync and caching indices
CREATE INDEX IF NOT EXISTS idx_sync_queue_priority ON sync_queue(priority, created_at);
CREATE INDEX IF NOT EXISTS idx_cache_entries_key ON cache_entries(cache_key);
CREATE INDEX IF NOT EXISTS idx_cache_entries_expiry ON cache_entries(expiry_time);

-- Migration 2: Add performance monitoring tables
-- =============================================

-- Performance metrics table
CREATE TABLE IF NOT EXISTS performance_metrics (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    metric_name TEXT NOT NULL,
    metric_value REAL NOT NULL,
    metric_unit TEXT,
    recorded_at INTEGER NOT NULL DEFAULT (strftime('%s', 'now')),
    additional_data TEXT -- JSON for extra context
);

CREATE INDEX IF NOT EXISTS idx_performance_metrics_name_time ON performance_metrics(metric_name, recorded_at);

-- App usage analytics
CREATE TABLE IF NOT EXISTS app_usage_analytics (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    feature_name TEXT NOT NULL,
    action_type TEXT NOT NULL, -- 'view', 'click', 'complete', etc.
    duration_ms INTEGER,
    timestamp INTEGER NOT NULL DEFAULT (strftime('%s', 'now')),
    user_session_id TEXT,
    additional_context TEXT -- JSON
);

CREATE INDEX IF NOT EXISTS idx_usage_analytics_feature ON app_usage_analytics(feature_name, timestamp);
CREATE INDEX IF NOT EXISTS idx_usage_analytics_session ON app_usage_analytics(user_session_id);

-- Migration 3: Data optimization and cleanup
-- ==========================================

-- Remove old cache entries (older than 30 days)
DELETE FROM cache_entries 
WHERE expiry_time < strftime('%s', 'now', '-30 days');

-- Optimize sync queue (remove completed items older than 7 days)
DELETE FROM sync_queue 
WHERE status = 'completed' 
AND updated_at < strftime('%s', 'now', '-7 days');

-- Migration 4: Add security audit tables
-- ======================================

-- Security events logging
CREATE TABLE IF NOT EXISTS security_events (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    event_type TEXT NOT NULL, -- 'login_attempt', 'api_key_access', 'encryption_operation'
    severity TEXT NOT NULL, -- 'info', 'warning', 'error', 'critical'
    description TEXT NOT NULL,
    user_id INTEGER,
    ip_address TEXT,
    user_agent TEXT,
    timestamp INTEGER NOT NULL DEFAULT (strftime('%s', 'now')),
    additional_data TEXT -- JSON
);

CREATE INDEX IF NOT EXISTS idx_security_events_type ON security_events(event_type, timestamp);
CREATE INDEX IF NOT EXISTS idx_security_events_severity ON security_events(severity, timestamp);
CREATE INDEX IF NOT EXISTS idx_security_events_user ON security_events(user_id, timestamp);

-- API key usage tracking
CREATE TABLE IF NOT EXISTS api_key_usage (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    service_name TEXT NOT NULL,
    request_count INTEGER NOT NULL DEFAULT 1,
    last_used INTEGER NOT NULL DEFAULT (strftime('%s', 'now')),
    daily_quota INTEGER,
    monthly_quota INTEGER,
    response_time_ms INTEGER,
    success_rate REAL, -- 0.0 to 1.0
    error_count INTEGER DEFAULT 0
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_api_key_usage_service ON api_key_usage(service_name);

-- Migration 5: Health Connect integration tables
-- ==============================================

-- Health Connect sync status
CREATE TABLE IF NOT EXISTS health_connect_sync (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    data_type TEXT NOT NULL, -- 'steps', 'heart_rate', 'sleep', etc.
    last_sync_time INTEGER NOT NULL,
    sync_status TEXT NOT NULL, -- 'success', 'failed', 'partial'
    records_synced INTEGER DEFAULT 0,
    error_message TEXT,
    next_sync_time INTEGER
);

CREATE UNIQUE INDEX IF NOT EXISTS idx_health_connect_data_type ON health_connect_sync(data_type);

-- Health data mapping
CREATE TABLE IF NOT EXISTS health_data_mapping (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    health_connect_id TEXT NOT NULL,
    local_record_id INTEGER NOT NULL,
    data_type TEXT NOT NULL,
    sync_direction TEXT NOT NULL, -- 'import', 'export', 'bidirectional'
    created_at INTEGER NOT NULL DEFAULT (strftime('%s', 'now')),
    updated_at INTEGER NOT NULL DEFAULT (strftime('%s', 'now'))
);

CREATE INDEX IF NOT EXISTS idx_health_mapping_hc_id ON health_data_mapping(health_connect_id);
CREATE INDEX IF NOT EXISTS idx_health_mapping_local_id ON health_data_mapping(local_record_id, data_type);

-- Migration 6: Performance optimization triggers
-- =============================================

-- Trigger to automatically clean old performance metrics
CREATE TRIGGER IF NOT EXISTS cleanup_old_performance_metrics
AFTER INSERT ON performance_metrics
BEGIN
    DELETE FROM performance_metrics 
    WHERE recorded_at < strftime('%s', 'now', '-90 days');
END;

-- Trigger to update cache entry access time
CREATE TRIGGER IF NOT EXISTS update_cache_access_time
AFTER SELECT ON cache_entries
FOR EACH ROW
BEGIN
    UPDATE cache_entries 
    SET last_accessed = strftime('%s', 'now') 
    WHERE cache_key = NEW.cache_key;
END;

-- Migration 7: Add database schema version tracking
-- ================================================

CREATE TABLE IF NOT EXISTS schema_version (
    version INTEGER PRIMARY KEY,
    applied_at INTEGER NOT NULL DEFAULT (strftime('%s', 'now')),
    description TEXT
);

-- Insert current schema version
INSERT OR REPLACE INTO schema_version (version, description) 
VALUES (2, 'Production optimization migration - performance indices, security logging, Health Connect integration');

-- Migration 8: Create views for common queries
-- ===========================================

-- User workout summary view
CREATE VIEW IF NOT EXISTS user_workout_summary AS
SELECT 
    u.id as user_id,
    u.email,
    COUNT(DISTINCT w.id) as total_workouts,
    COUNT(DISTINCT DATE(w.date / 1000, 'unixepoch')) as workout_days,
    AVG(w.duration_minutes) as avg_workout_duration,
    MAX(w.date) as last_workout_date
FROM users u
LEFT JOIN workouts w ON u.id = w.user_id
GROUP BY u.id, u.email;

-- Daily nutrition summary view
CREATE VIEW IF NOT EXISTS daily_nutrition_summary AS
SELECT 
    m.user_id,
    DATE(m.date / 1000, 'unixepoch') as date,
    SUM(mf.calories) as total_calories,
    SUM(mf.protein_grams) as total_protein,
    SUM(mf.carbs_grams) as total_carbs,
    SUM(mf.fat_grams) as total_fat,
    COUNT(DISTINCT m.id) as meals_logged
FROM meals m
JOIN meal_foods mf ON m.id = mf.meal_id
GROUP BY m.user_id, DATE(m.date / 1000, 'unixepoch');

-- Performance metrics summary view
CREATE VIEW IF NOT EXISTS performance_summary AS
SELECT 
    metric_name,
    COUNT(*) as measurement_count,
    AVG(metric_value) as avg_value,
    MIN(metric_value) as min_value,
    MAX(metric_value) as max_value,
    MAX(recorded_at) as last_recorded
FROM performance_metrics
WHERE recorded_at > strftime('%s', 'now', '-7 days')
GROUP BY metric_name;

-- Final optimization: Analyze and optimize database
-- ================================================

-- Update table statistics for query optimizer
ANALYZE;

-- Vacuum database to reclaim space and improve performance
-- Note: This should be done during maintenance window
-- VACUUM;

-- Log migration completion
INSERT INTO security_events (event_type, severity, description) 
VALUES ('database_migration', 'info', 'Production optimization migration completed successfully');

-- Migration completion summary
SELECT 
    'Migration completed successfully' as status,
    (SELECT COUNT(*) FROM sqlite_master WHERE type='table') as total_tables,
    (SELECT COUNT(*) FROM sqlite_master WHERE type='index') as total_indices,
    (SELECT COUNT(*) FROM sqlite_master WHERE type='view') as total_views,
    (SELECT COUNT(*) FROM sqlite_master WHERE type='trigger') as total_triggers,
    datetime('now') as completed_at;