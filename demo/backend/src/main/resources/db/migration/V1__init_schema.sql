-- =====================================================================
-- V1: начальная схема БД «Найдёмся» (по документу «Проектирование ER схемы БД»)
-- =====================================================================

-- ---------- ENUM-типы ----------
CREATE TYPE gender_enum             AS ENUM ('male', 'female');
CREATE TYPE gender_restriction_enum AS ENUM ('any', 'male', 'female');
CREATE TYPE application_status_enum AS ENUM ('pending', 'approved', 'rejected');

-- ---------- Справочники ----------
CREATE TABLE roles (
    id          SMALLSERIAL PRIMARY KEY,
    code        VARCHAR(30) NOT NULL UNIQUE,      -- user / organizer / admin
    name        VARCHAR(50) NOT NULL,
    description TEXT
);

CREATE TABLE categories (
    id   SMALLSERIAL PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE event_types (
    id   SMALLSERIAL PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE,             -- company / poster
    name VARCHAR(50) NOT NULL UNIQUE
);

-- Начальные данные справочников.
-- Роль «Пользователь» должна получить id = 1: на него ссылается DEFAULT у users.role_id.
INSERT INTO roles (code, name, description) VALUES
    ('user',      'Пользователь',  'Создаёт мероприятия и откликается на чужие'),
    ('organizer', 'Организатор',   'Создаёт мероприятия и рассматривает заявки, откликаться не может'),
    ('admin',     'Администратор', 'Модерирует контент, блокирует пользователей, удаляет любые мероприятия');

INSERT INTO event_types (code, name) VALUES
    ('company', 'Компания'),
    ('poster',  'Афиша');

INSERT INTO categories (name) VALUES
    ('Концерт'), ('Выставка'), ('Кино'), ('Фестиваль'), ('Прогулка'), ('Спорт');

-- ---------- Пользователи ----------
CREATE TABLE users (
    id            BIGSERIAL    PRIMARY KEY,
    role_id       SMALLINT     NOT NULL DEFAULT 1 REFERENCES roles (id),
    email         VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    nickname      VARCHAR(50)  NOT NULL UNIQUE,
    birth_date    DATE         NOT NULL,
    gender        gender_enum,
    about         TEXT,
    contact       VARCHAR(64),
    is_blocked    BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at    TIMESTAMPTZ  NOT NULL DEFAULT now()
);

-- ---------- Мероприятия ----------
CREATE TABLE events (
    id                 BIGSERIAL    PRIMARY KEY,
    author_id          BIGINT       NOT NULL REFERENCES users (id),
    category_id        SMALLINT     NOT NULL REFERENCES categories (id),
    type_id            SMALLINT     NOT NULL REFERENCES event_types (id),
    title              VARCHAR(150) NOT NULL,
    description        TEXT         NOT NULL,
    starts_at          TIMESTAMPTZ  NOT NULL,
    address            VARCHAR(255) NOT NULL,
    min_age            SMALLINT,
    gender_restriction gender_restriction_enum NOT NULL DEFAULT 'any',
    created_at         TIMESTAMPTZ  NOT NULL DEFAULT now(),
    updated_at         TIMESTAMPTZ  NOT NULL DEFAULT now(),
    CONSTRAINT chk_events_title_not_blank       CHECK (btrim(title) <> ''),
    CONSTRAINT chk_events_description_not_blank CHECK (btrim(description) <> ''),
    CONSTRAINT chk_events_address_not_blank     CHECK (btrim(address) <> ''),
    CONSTRAINT chk_events_min_age               CHECK (min_age IS NULL OR min_age BETWEEN 0 AND 100)
);

CREATE INDEX idx_events_starts_at   ON events (starts_at);
CREATE INDEX idx_events_category_id ON events (category_id);
CREATE INDEX idx_events_type_id     ON events (type_id);
CREATE INDEX idx_events_author_id   ON events (author_id);

-- ---------- Заявки ----------
CREATE TABLE applications (
    id         BIGSERIAL   PRIMARY KEY,
    event_id   BIGINT      NOT NULL REFERENCES events (id) ON DELETE CASCADE,
    user_id    BIGINT      NOT NULL REFERENCES users (id),
    message    TEXT        NOT NULL,
    contact    VARCHAR(64) NOT NULL,
    status     application_status_enum NOT NULL DEFAULT 'pending',
    created_at TIMESTAMPTZ NOT NULL DEFAULT now(),
    decided_at TIMESTAMPTZ,
    CONSTRAINT uq_applications_event_user UNIQUE (event_id, user_id),
    CONSTRAINT chk_applications_decided_at CHECK (
        (status = 'pending' AND decided_at IS NULL)
        OR (status <> 'pending' AND decided_at IS NOT NULL)
    )
);

CREATE INDEX idx_applications_user_id ON applications (user_id);

-- ---------- Участники мероприятия ----------
CREATE TABLE event_participants (
    id             BIGSERIAL   PRIMARY KEY,
    event_id       BIGINT      NOT NULL REFERENCES events (id) ON DELETE CASCADE,
    user_id        BIGINT      NOT NULL REFERENCES users (id),
    application_id BIGINT      NOT NULL UNIQUE REFERENCES applications (id) ON DELETE CASCADE,
    event_date     TIMESTAMPTZ NOT NULL,
    CONSTRAINT uq_event_participants_event_user UNIQUE (event_id, user_id)
);

-- =====================================================================
-- Вспомогательные функции: кто выполняет действие
-- Приложение после входа передаёт id пользователя: SET app.user_id = '<id>'
-- =====================================================================
CREATE FUNCTION app_current_user_id() RETURNS BIGINT
LANGUAGE sql STABLE AS $$
    SELECT NULLIF(current_setting('app.user_id', true), '')::BIGINT
$$;

CREATE FUNCTION app_is_admin() RETURNS BOOLEAN
LANGUAGE sql STABLE AS $$
    SELECT EXISTS (
        SELECT 1
        FROM users u JOIN roles r ON r.id = u.role_id
        WHERE u.id = app_current_user_id() AND r.code = 'admin'
    )
$$;

-- =====================================================================
-- Триггеры
-- =====================================================================

-- --- events: создание (роль, блокировка, дата не в прошлом) ---
CREATE FUNCTION trg_events_before_insert() RETURNS trigger
LANGUAGE plpgsql AS $$
DECLARE
    v_role    VARCHAR(30);
    v_blocked BOOLEAN;
BEGIN
    SELECT r.code, u.is_blocked INTO v_role, v_blocked
    FROM users u JOIN roles r ON r.id = u.role_id
    WHERE u.id = NEW.author_id;

    IF v_role NOT IN ('user', 'organizer') THEN
        RAISE EXCEPTION 'Создавать мероприятия могут только роли «Пользователь» и «Организатор»';
    END IF;
    IF v_blocked THEN
        RAISE EXCEPTION 'Заблокированный пользователь не может создавать мероприятия';
    END IF;
    IF NEW.starts_at < now() THEN
        RAISE EXCEPTION 'Дата мероприятия не может быть в прошлом';
    END IF;
    RETURN NEW;
END;
$$;

CREATE TRIGGER events_before_insert
    BEFORE INSERT ON events
    FOR EACH ROW EXECUTE FUNCTION trg_events_before_insert();

-- --- events: изменение (дата не в прошлом при переносе, updated_at) ---
CREATE FUNCTION trg_events_before_update() RETURNS trigger
LANGUAGE plpgsql AS $$
BEGIN
    IF NEW.starts_at IS DISTINCT FROM OLD.starts_at AND NEW.starts_at < now() THEN
        RAISE EXCEPTION 'Дата мероприятия не может быть в прошлом';
    END IF;
    NEW.updated_at := now();
    RETURN NEW;
END;
$$;

CREATE TRIGGER events_before_update
    BEFORE UPDATE ON events
    FOR EACH ROW EXECUTE FUNCTION trg_events_before_update();

-- --- events: перенос мероприятия -> обновить event_date у всех участников ---
CREATE FUNCTION trg_events_after_update_date() RETURNS trigger
LANGUAGE plpgsql AS $$
BEGIN
    UPDATE event_participants
    SET event_date = NEW.starts_at
    WHERE event_id = NEW.id;
    RETURN NEW;
END;
$$;

CREATE TRIGGER events_after_update_date
    AFTER UPDATE OF starts_at ON events
    FOR EACH ROW
    WHEN (NEW.starts_at IS DISTINCT FROM OLD.starts_at)
    EXECUTE FUNCTION trg_events_after_update_date();

-- --- events: удалить может только автор или администратор ---
CREATE FUNCTION trg_events_before_delete() RETURNS trigger
LANGUAGE plpgsql AS $$
BEGIN
    IF app_current_user_id() IS DISTINCT FROM OLD.author_id AND NOT app_is_admin() THEN
        RAISE EXCEPTION 'Удалить мероприятие может только его автор или администратор';
    END IF;
    RETURN OLD;
END;
$$;

CREATE TRIGGER events_before_delete
    BEFORE DELETE ON events
    FOR EACH ROW EXECUTE FUNCTION trg_events_before_delete();

-- --- applications: проверки при подаче заявки ---
CREATE FUNCTION trg_applications_before_insert() RETURNS trigger
LANGUAGE plpgsql AS $$
DECLARE
    v_role       VARCHAR(30);
    v_blocked    BOOLEAN;
    v_birth      DATE;
    v_gender     gender_enum;
    v_author_id  BIGINT;
    v_min_age    SMALLINT;
    v_restrict   gender_restriction_enum;
    v_starts_at  TIMESTAMPTZ;
BEGIN
    SELECT r.code, u.is_blocked, u.birth_date, u.gender
    INTO v_role, v_blocked, v_birth, v_gender
    FROM users u JOIN roles r ON r.id = u.role_id
    WHERE u.id = NEW.user_id;

    SELECT e.author_id, e.min_age, e.gender_restriction, e.starts_at
    INTO v_author_id, v_min_age, v_restrict, v_starts_at
    FROM events e
    WHERE e.id = NEW.event_id;

    IF v_role <> 'user' THEN
        RAISE EXCEPTION 'Откликаться на мероприятия может только роль «Пользователь»';
    END IF;
    IF v_blocked THEN
        RAISE EXCEPTION 'Заблокированный пользователь не может подавать заявки';
    END IF;
    IF NEW.user_id = v_author_id THEN
        RAISE EXCEPTION 'Автор не может подать заявку на своё мероприятие';
    END IF;
    IF v_min_age IS NOT NULL
       AND date_part('year', age(v_starts_at::date, v_birth)) < v_min_age THEN
        RAISE EXCEPTION 'Возраст пользователя меньше минимального для мероприятия';
    END IF;
    IF v_restrict <> 'any' AND v_gender IS DISTINCT FROM v_restrict::text::gender_enum THEN
        RAISE EXCEPTION 'Пол пользователя не подходит под ограничение мероприятия';
    END IF;
    RETURN NEW;
END;
$$;

CREATE TRIGGER applications_before_insert
    BEFORE INSERT ON applications
    FOR EACH ROW EXECUTE FUNCTION trg_applications_before_insert();

-- --- applications: одобрение -> участник, отмена одобрения -> удалить участника ---
CREATE FUNCTION trg_applications_sync_participants() RETURNS trigger
LANGUAGE plpgsql AS $$
BEGIN
    IF NEW.status = 'approved' AND (TG_OP = 'INSERT' OR OLD.status <> 'approved') THEN
        INSERT INTO event_participants (event_id, user_id, application_id, event_date)
        SELECT NEW.event_id, NEW.user_id, NEW.id, e.starts_at
        FROM events e WHERE e.id = NEW.event_id;
    ELSIF TG_OP = 'UPDATE' AND OLD.status = 'approved' AND NEW.status <> 'approved' THEN
        DELETE FROM event_participants WHERE application_id = OLD.id;
    END IF;
    RETURN NEW;
END;
$$;

CREATE TRIGGER applications_sync_participants
    AFTER INSERT OR UPDATE OF status ON applications
    FOR EACH ROW EXECUTE FUNCTION trg_applications_sync_participants();

-- --- users: блокировать может только администратор ---
CREATE FUNCTION trg_users_before_update_block() RETURNS trigger
LANGUAGE plpgsql AS $$
BEGIN
    IF NEW.is_blocked IS DISTINCT FROM OLD.is_blocked AND NOT app_is_admin() THEN
        RAISE EXCEPTION 'Блокировать пользователей может только администратор';
    END IF;
    RETURN NEW;
END;
$$;

CREATE TRIGGER users_before_update_block
    BEFORE UPDATE OF is_blocked ON users
    FOR EACH ROW EXECUTE FUNCTION trg_users_before_update_block();
