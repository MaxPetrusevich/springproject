-- Пользователи (пароль: 'password' захешированный через BCrypt)
INSERT INTO users (email, password, first_name, last_name, role, enabled, created_at) VALUES
('admin@example.com', '$2a$10$ZV7UgXL6P0JhwR8.1/ZFZ.HaW4E3HH3GLJZcPtGgqbVv3ZQ6HG5Ym', 'Админ', 'Админов', 'ADMIN', true, NOW()),
('organiser@example.com', '$2a$10$ZV7UgXL6P0JhwR8.1/ZFZ.HaW4E3HH3GLJZcPtGgqbVv3ZQ6HG5Ym', 'Организатор', 'Тестов', 'ORGANIZER', true, NOW()),
('organiser2@example.com', '$2a$10$ZV7UgXL6P0JhwR8.1/ZFZ.HaW4E3HH3GLJZcPtGgqbVv3ZQ6HG5Ym', 'Иван', 'Организаторов', 'ORGANIZER', true, NOW()),
('user@example.com', '$2a$10$ZV7UgXL6P0JhwR8.1/ZFZ.HaW4E3HH3GLJZcPtGgqbVv3ZQ6HG5Ym', 'Пользователь', 'Обычный', 'USER', true, NOW()),
('user2@example.com', '$2a$10$ZV7UgXL6P0JhwR8.1/ZFZ.HaW4E3HH3GLJZcPtGgqbVv3ZQ6HG5Ym', 'Анна', 'Пользователева', 'USER', true, NOW()),
('user3@example.com', '$2a$10$ZV7UgXL6P0JhwR8.1/ZFZ.HaW4E3HH3GLJZcPtGgqbVv3ZQ6HG5Ym', 'Петр', 'Тестовый', 'USER', true, NOW());

-- Организации
INSERT INTO organisations (name, description, owner_id, created_at) VALUES
('ООО Тест', 'Тестовая организация', 2, NOW()),
('ИП Иванов', 'Индивидуальный предприниматель', 2, NOW()),
('ООО Технологии', 'Инновационные решения', 3, NOW()),
('Образовательный центр', 'Онлайн курсы', 3, NOW()),
('Фитнес клуб', 'Спортивные программы', 2, NOW());

-- Продукты
INSERT INTO products (name, description, category, organisation_id, active, created_at) VALUES
('Премиум подписка', 'Премиум доступ ко всем функциям', 'Подписки', 1, true, NOW()),
('Базовый план', 'Базовый набор функций', 'Подписки', 1, true, NOW()),
('Про версия', 'Профессиональная версия продукта', 'Программы', 2, true, NOW()),
('Онлайн курс Java', 'Программирование на Java', 'Образование', 4, true, NOW()),
('Онлайн курс Python', 'Программирование на Python', 'Образование', 4, true, NOW()),
('Фитнес тренировки', 'Персональные тренировки онлайн', 'Спорт', 5, true, NOW()),
('Йога онлайн', 'Занятия йогой с инструктором', 'Спорт', 5, true, NOW()),
('Облачное хранилище', 'Облачный сервис хранения данных', 'Сервисы', 3, true, NOW()),
('Антивирус Pro', 'Защита от вирусов', 'Безопасность', 3, true, NOW());

-- Планы подписок
INSERT INTO subscription_plans (name, description, price, period_days, product_id, active, created_at) VALUES
('Месячный', 'Премиум на месяц', 999.00, 30, 1, true, NOW()),
('Годовой', 'Премиум на год', 9990.00, 365, 1, true, NOW()),
('Базовый месяц', 'Базовый план на месяц', 299.00, 30, 2, true, NOW()),
('Про месяц', 'Про версия на месяц', 1499.00, 30, 3, true, NOW()),
('Java Базовый', 'Базовый курс Java', 4990.00, 90, 4, true, NOW()),
('Java Продвинутый', 'Продвинутый курс Java', 7990.00, 120, 4, true, NOW()),
('Python Старт', 'Стартовый курс Python', 3990.00, 60, 5, true, NOW()),
('Фитнес Месяц', 'Месяц тренировок', 2990.00, 30, 6, true, NOW()),
('Фитнес Квартал', '3 месяца тренировок', 7990.00, 90, 6, true, NOW()),
('Йога Месяц', 'Месяц занятий йогой', 2490.00, 30, 7, true, NOW()),
('Облако 100GB', '100GB облачного хранилища', 199.00, 30, 8, true, NOW()),
('Облако 1TB', '1TB облачного хранилища', 999.00, 30, 8, true, NOW()),
('Антивирус Базовый', 'Базовая защита', 290.00, 30, 9, true, NOW()),
('Антивирус Премиум', 'Премиум защита', 690.00, 30, 9, true, NOW());

-- Подписки
INSERT INTO subscriptions (user_id, plan_id, start_date, end_date, status, active, created_at) VALUES
(4, 1, NOW(), NOW() + INTERVAL '30 days', 'ACTIVE', true, NOW()),
(4, 3, NOW() - INTERVAL '60 days', NOW() - INTERVAL '30 days', 'EXPIRED', false, NOW() - INTERVAL '60 days'),
(5, 5, NOW() - INTERVAL '30 days', NOW() + INTERVAL '60 days', 'ACTIVE', true, NOW() - INTERVAL '30 days'),
(5, 8, NOW(), NOW() + INTERVAL '30 days', 'ACTIVE', true, NOW()),
(6, 10, NOW() - INTERVAL '15 days', NOW() + INTERVAL '15 days', 'ACTIVE', true, NOW() - INTERVAL '15 days'),
(6, 13, NOW() - INTERVAL '90 days', NOW() - INTERVAL '60 days', 'CANCELLED', false, NOW() - INTERVAL '90 days'),
(4, 11, NOW() - INTERVAL '45 days', NOW() - INTERVAL '15 days', 'EXPIRED', false, NOW() - INTERVAL '45 days'),
(5, 6, NOW() - INTERVAL '150 days', NOW() - INTERVAL '30 days', 'EXPIRED', false, NOW() - INTERVAL '150 days');

-- Платежи
INSERT INTO payments (subscription_id, user_id, amount, status, payment_date, created_at) VALUES
(1, 4, 999.00, 'COMPLETED', NOW(), NOW()),
(2, 4, 299.00, 'COMPLETED', NOW() - INTERVAL '60 days', NOW() - INTERVAL '60 days'),
(3, 5, 4990.00, 'COMPLETED', NOW() - INTERVAL '30 days', NOW() - INTERVAL '30 days'),
(4, 5, 2990.00, 'COMPLETED', NOW(), NOW()),
(5, 6, 2490.00, 'COMPLETED', NOW() - INTERVAL '15 days', NOW() - INTERVAL '15 days'),
(6, 6, 290.00, 'COMPLETED', NOW() - INTERVAL '90 days', NOW() - INTERVAL '90 days'),
(6, 6, 290.00, 'REFUNDED', NOW() - INTERVAL '60 days', NOW() - INTERVAL '60 days'),
(7, 4, 199.00, 'COMPLETED', NOW() - INTERVAL '45 days', NOW() - INTERVAL '45 days'),
(8, 5, 7990.00, 'COMPLETED', NOW() - INTERVAL '150 days', NOW() - INTERVAL '150 days'); 