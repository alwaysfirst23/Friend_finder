-- Лимит участников на мероприятии (например, 3/5; NULL — без лимита)
ALTER TABLE events
    ADD COLUMN IF NOT EXISTS max_participants SMALLINT,
    ADD CONSTRAINT chk_events_max_participants
        CHECK (max_participants IS NULL OR max_participants > 0);

-- Избранное (сердечко на карточке)
CREATE TABLE IF NOT EXISTS event_favorites (
    user_id    BIGINT      NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    event_id   BIGINT      NOT NULL REFERENCES events (id) ON DELETE CASCADE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    PRIMARY KEY (user_id, event_id)
);