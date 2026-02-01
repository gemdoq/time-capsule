-- TimeCapsule Database Schema

-- Users table
CREATE TABLE IF NOT EXISTS users (
    id              BIGSERIAL PRIMARY KEY,
    email           VARCHAR(255) NOT NULL UNIQUE,
    password        VARCHAR(255) NOT NULL,
    nickname        VARCHAR(50) NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_users_email ON users(email);

-- Predictions table
CREATE TABLE IF NOT EXISTS predictions (
    id              BIGSERIAL PRIMARY KEY,
    author_id       BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title           VARCHAR(200) NOT NULL,
    content         TEXT NOT NULL,
    release_date    DATE NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_predictions_author ON predictions(author_id);
CREATE INDEX IF NOT EXISTS idx_predictions_release_date ON predictions(release_date);

-- Prediction Recipients table
CREATE TABLE IF NOT EXISTS prediction_recipients (
    id                  BIGSERIAL PRIMARY KEY,
    prediction_id       BIGINT NOT NULL REFERENCES predictions(id) ON DELETE CASCADE,
    recipient_email     VARCHAR(255),
    recipient_user_id   BIGINT REFERENCES users(id) ON DELETE SET NULL,
    access_code         VARCHAR(32) NOT NULL UNIQUE,
    notification_sent   BOOLEAN DEFAULT FALSE,
    notified_at         TIMESTAMP,
    viewed_at           TIMESTAMP,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_recipients_prediction ON prediction_recipients(prediction_id);
CREATE INDEX IF NOT EXISTS idx_recipients_access_code ON prediction_recipients(access_code);
CREATE INDEX IF NOT EXISTS idx_recipients_user ON prediction_recipients(recipient_user_id);

-- Attachments table
CREATE TABLE IF NOT EXISTS attachments (
    id              BIGSERIAL PRIMARY KEY,
    prediction_id   BIGINT NOT NULL REFERENCES predictions(id) ON DELETE CASCADE,
    original_name   VARCHAR(255) NOT NULL,
    stored_name     VARCHAR(255) NOT NULL,
    file_path       VARCHAR(500) NOT NULL,
    file_size       BIGINT NOT NULL,
    mime_type       VARCHAR(100) NOT NULL,
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_attachments_prediction ON attachments(prediction_id);

-- Recipient Claims table
CREATE TABLE IF NOT EXISTS recipient_claims (
    id              BIGSERIAL PRIMARY KEY,
    prediction_id   BIGINT NOT NULL REFERENCES predictions(id) ON DELETE CASCADE,
    requester_id    BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    target_user_id  BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    status          VARCHAR(20) DEFAULT 'PENDING',
    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_claims_target ON recipient_claims(target_user_id);
CREATE INDEX IF NOT EXISTS idx_claims_prediction ON recipient_claims(prediction_id);
