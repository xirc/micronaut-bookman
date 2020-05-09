CREATE TABLE book (
    id VARCHAR(36) PRIMARY KEY,
    title VARCHAR(512) NOT NULL DEFAULT '',
    created_date DATETIME NOT NULL DEFAULT NOW(),
    updated_date DATETIME NOT NULL DEFAULT NOW()
);