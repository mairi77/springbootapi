
CREATE TABLE IF NOT EXISTS todo (
    id VARCHAR(36) NOT NULL PRIMARY KEY,
    title VARCHAR(128) NOT NULL,
    description TEXT NOT NULL,
    --status VARCHAR(255) NOT NULL DEFAULT 'PENDING',
    createdAt DATETIME NOT NULL,
    updatedAT DATETIME,
    finishedAt DATETIME
);


