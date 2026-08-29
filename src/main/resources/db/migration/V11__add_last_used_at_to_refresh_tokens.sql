ALTER TABLE refresh_tokens
    ADD COLUMN last_used_at TIMESTAMP;

UPDATE refresh_tokens
SET last_used_at = created_at
WHERE last_used_at IS NULL;

ALTER TABLE refresh_tokens
    ALTER COLUMN last_used_at SET NOT NULL;