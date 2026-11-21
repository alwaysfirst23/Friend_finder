-- =====================================================================
-- V2: синхронизация справочника categories с дизайн-системой (icons.js)
-- =====================================================================

UPDATE categories SET name = 'Музыка'    WHERE name = 'Концерт';
UPDATE categories SET name = 'Музеи'     WHERE name = 'Выставка';
UPDATE categories SET name = 'Театры'    WHERE name = 'Кино';
UPDATE categories SET name = 'Фестивали' WHERE name = 'Фестиваль';
UPDATE categories SET name = 'Прогулки'  WHERE name = 'Прогулка';

INSERT INTO categories (name) VALUES
    ('Кафе'),
    ('Клубы'),
    ('Общение'),
    ('Готовка'),
    ('Рыбалка'),
    ('Домашние питомцы'),
    ('Фотосъёмки'),
    ('Программирование'),
    ('Рестораны'),
    ('Бег'),
    ('Шоппинг'),
    ('Учёба'),
    ('Видео игры'),
    ('Волонтёрство')
ON CONFLICT (name) DO NOTHING;