-- 1. Роли пользователей
INSERT INTO role (role) VALUES
                                 ('ADMIN'),
                                 ('USER');

-- 2. Пользователи
INSERT INTO users (identify_number, password, role_id) VALUES
                                                 ('123456789121', '$2a$10$kIMlRgpErZXbSf0c/L5LO.dYYd3/nRoHj8ZQKYjd.bbgV0.t8Zk3i', 2),
                                                 ('123456788122', '$2a$10$kIMlRgpErZXbSf0c/L5LO.dYYd3/nRoHj8ZQKYjd.bbgV0.t8Zk3i', 2);

-- 3. Связь пользователей с ролями


-- 4. Граждане
INSERT INTO citizen (first_name, last_name, middle_name, phone, email, identify_number, passport_series, passport_number, address, user_id) VALUES
                                                                                                                                                ('Иван', 'Иванов', 'Иванович', '+375 (29) 111-11-11', 'admin@mail.gov.by', '3250789A001PB1', 'MP', '3250789', 'г. Минск, ул. Притыцкого, 1', 9),
                                                                                                                                                ('Петр', 'Петров', 'Петрович', '+375 (29) 222-22-22', 'user@gmail.com', '3250789A002PB2', 'MP', '3250790', 'г. Минск, ул. Некрасова, 2', 10);

-- 5. Статусы заявок
INSERT INTO bid_status (status) VALUES
                                           ('НОВАЯ'),
                                           ('В ОБРАБОТКЕ'),
                                           ('ВЫПОЛНЕНА'),
                                           ('ОТКЛОНЕНА');

-- 6. Статусы платежей
INSERT INTO payment_status (status) VALUES
                                               ('ОЖИДАЕТ ОПЛАТЫ'),
                                               ('ОПЛАЧЕН'),
                                               ('ОТМЕНЕН');

-- 7. Категории
INSERT INTO category (category) VALUES
                                    ('Социальная защита'),
                                    ('Здравоохранение'),
                                    ('Образование'),
                                    ('Транспорт'),
                                    ('ЖКХ'),
                                    ('Документы и справки');

-- 8. Учреждения
INSERT INTO establishment (name, address, phone,email) VALUES
                                                     ('Центр социальной защиты населения', 'ул. Ленина, 1', '+375 (17) 123-45-67','est@example.com'),
                                                     ('Городская поликлиника №1', 'ул. Пушкина, 10', '+375 (17) 234-56-78','est@example.com'),
                                                     ('Центр административных процедур', 'пр. Независимости, 15', '+375 (17) 345-67-89','est@example.com'),
                                                     ('ФСЗН', 'ул. Гагарина, 5', '+375 (17) 456-78-90','est@example.com'),
                                                     ('ГАИ', 'ул. Советская, 20', '+375 (17) 567-89-01','est@example.com');

-- 9. Услуги
INSERT INTO service (name, description,  category_id, establishment_id) VALUES
                                                                                      ('Оформление пенсии', 'Оформление и расчет пенсии',  1, 5),
                                                                                      ('Медицинская справка для водительского удостоверения', 'Получение медицинской справки о допуске к управлению ТС',  2, 3),
                                                                                      ('Регистрация автомобиля', 'Постановка автомобиля на учет',  4, 6),
                                                                                      ('Оформление биометрического паспорта', 'Получение паспорта гражданина РБ для выезда за границу',  6, 4),
                                                                                      ('Справка о составе семьи', 'Получение справки о составе семьи',  1, 2);

-- 10. Заявки
INSERT INTO bid (date, citizen_id, service_id, status_id) VALUES
                                                              (NOW(), 6, 6, 1),
                                                              (NOW(), 6, 7, 2),
                                                              (NOW(), 7, 8, 3),
                                                              (NOW(), 7, 9, 4);

-- 11. Платежи
INSERT INTO payment (sum, date, status_id, bid_id) VALUES
                                                          (321.50, NOW(), 2, 9),
                                                          (35.50, NOW(), 1, 10),
                                                          (156.00, NOW(), 3, 11);

-- 12. Типы документов
INSERT INTO type (type, description) VALUES
                                         ('Паспорт', 'Паспорт гражданина Республики Беларусь'),
                                         ('Заявление', 'Заявление на получение услуги'),
                                         ('Справка', 'Справка о доходах'),
                                         ('Договор', 'Договор об оказании услуг'),
                                         ('Квитанция', 'Квитанция об оплате'),
                                         ('Удостоверение', 'Удостоверение личности');

-- 13. Документы
INSERT INTO document (name, loading_date, type_id, bid_id, file_path, original_filename) VALUES
                                                                                     ('Паспорт Иванова', NOW(), 1, 9, '/documents/passport1.pdf', 'passport1.pdf'),
                                                                                     ('Заявление на пенсию', NOW(), 2, 9, '/documents/pension_app.pdf', 'pension_app.pdf'),
                                                                                     ('Медицинская справка Петрова', NOW(), 3, 10, '/documents/medical.pdf', 'medical.pdf'),
                                                                                     ('Квитанция об оплате', NOW(), 5, 11, '/documents/payment.pdf', 'payment.pdf');