
CREATE TABLE app_users (
    id UUID PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL,
    CONSTRAINT chk_app_users_role
        CHECK (role IN ('ADMIN', 'SUPPORT', 'USER'))
);
