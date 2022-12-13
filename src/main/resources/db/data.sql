insert into users (id, first_name, last_name, image, email, password, role)
values (1, 'Esen', 'Niyazov',
        'https://st-1.akipress.org/st_runews/.storage/limon3/images/NOVEMBER2020/17964a41dffb4fdc4fe1ba6f41b0a464.jpg',
        'esen@gmail.com',
        '$2a$12$m3cYSQU6bNoMc0B6PFFQku2eDX6fWEZOY/G/E9D77p7SCXlamH9Tq', 'SUPER_ADMIN'),

       (2, 'Datka', 'Mamatzhanova', 'https://i.pinimg.com/564x/59/db/00/59db008b7e4727fdbab1060ead482fc5.jpg',
        'admin@gmail.com',
        '$2a$12$TbmCs13/KkzrcHd.rSnZeOH5qvVc79nuGb/bYhbC3sT8dYK9unj2q', 'ADMIN'),

       (3, 'Nursultan', 'Askarov', 'https://i.pinimg.com/564x/98/7b/9c/987b9c1a2c16192d4d86f996f97fc5dd.jpg',
        'user@gmail.com',
        '$2a$12$LcHYIlKllzvoMqfQpLOdqeNedFtk.SMb.CeRUyX/n7lBIPx00rtgW', 'USER'),

       (4, 'Kamchybek', 'Kuzobaev', 'image', 'manasbekovich@gmail.com',
        '$2a$12$sfgAFhShn3b2usZuwvvPV.9G.5dVhwWPC4XfwRz6FaGkpYoB6jAf.', 'ADMIN'),

       (5, 'Tynychbek', 'Kursanali Uulu', 'image', 'tkursanaliuulu01@gmail.com',
        '$2a$12$BB1Vljti/nWN4SfGtnBW6.IYPf4x3ChoXKUI/tuLkdwwTOfM4AdTa', 'USER'),

       (6, 'Daniel', 'Kamilzhanov', 'image', 'zadrot105217@gmail.com',
        '$2a$12$aAfKQF9kDMarOhLlbGN9ke5oBBIHUyklIUIedqp9eq1h.ffcDkCzu', 'ADMIN'),

       (7, 'Nurmuhammad', 'Nurbekov', 'image', 'nur.nurbekov.01@gmail.com',
        '$2a$12$bTfSe4x3gy7OiD/yTp2sc.CW8eR5hpG3RunZZLJjZR/9gsv7MDLD6', 'USER'),

       (8, 'Ulan', 'Mamatisaev', 'image', 'umamatisaev@gmail.com',
        '$2a$12$CYpA7sQefuc6j0BUUOU9AO6FlI7EzMBr8LPonf9YAKBfiDZDafqSG', 'USER'),

       (9, 'Nurbek', 'Isakov', 'image', 'nurbisak@gmail.com',
        '$2a$12$uTJIUPEgw80zM4wS7KMF2.8g3NexM00C/l8Vg8qvK8O6h6t/v.MRK', 'USER'),

       (10, 'Maksat', 'Omuraliev', 'image', 'bilalimus@gmail.com', 'bilalimus@gmail.com', 'USER'),

       (11, 'Tynchtyk', 'Akmatov', 'image', 'tynchtyk7@mail.ru',
        '$2a$12$30Zbq/rKLDFuqhB8JJauVu0RGaWqK4.RDF.l2Q5R.13fLRm3rmWo2', 'USER');


insert into workspaces (id, name, is_favorite, lead_id)
values (1, 'Workspace', false, 1),
       (2, 'Task Tracker', false, 2),
       (3, 'Bilingual', false, 4);

insert into boards(id, title, background, is_archive, is_favorite, workspace_id)
values (1, 'First Board',
        'https://burst.shopifycdn.com/photos/city-lights-through-rain-window.jpg?width=1200&format=pjpg&exif=1&iptc=1',
        false, false, 1),
       (2, 'Backend', 'background', false, false, 2);

insert into columns (id, title, is_archive, board_id, creator_id)
values (1, 'First Column', false, 1, 1),
       (2, 'Second Column', false, 2, 2);

insert into cards (id, title, description, is_archive, created_at, creator_id, moved_user_id, column_id)
values (1, 'First Card', 'First Description', false, '2022-10-31', 1, 1, 1),
       (2, 'Second Card', 'Description', false, '2022-11-10', 2, 2, 2);

insert into checklists (id, title, count, card_id)
values (1, 'First Checklist', 1, 1);

insert into subtasks (id, description, is_done, checklist_id)
values (1, 'Subtasks description ', false, 1);

insert into times (id, hour, minute)
values (1, 10, 20),
       (2, 12, 20);

insert into estimations (id, start_date, start_time_id, due_date, deadline_time_id, reminder, text, card_id, user_id,
                         sub_task_id)
values (1, '2022-02-01', 1, '2022-12-12', 2, 5, 'First Text', 1, 1, 1);

insert into notifications (id, message, created_at, notification_type, from_user_id, user_id, card_id, column_id,
                           board_id)
values (1, 'First Text', '2022-11-21T16:28', 'ASSIGN', 1, 2, 1, 1, 1);

insert into labels (id, color, description, card_id)
values (1, 'RED', 'Complete this task immediately', 1);

insert into comments (id, created_at, text, user_id, card_id)
values (1, '2022-10-23', 'First text', 1, 1);

insert into attachments (id, document_link, attached_date, card_id)
values (1, 'link', '2022-10-12', 1);

insert into users_boards (members_id, boards_id)
values (1, 1),
       (3, 1),
       (5, 1),
       (6, 1),
       (7, 1),
       (3, 2),
       (11, 2),
       (10, 2),
       (9, 2),
       (8, 2),
       (7, 2);

insert into user_workspace_roles (id, user_id, workspace_id, role)
values (1, 1, 1, 'SUPER_ADMIN'),
       (2, 2, 2, 'ADMIN'),
       (3, 3, 1, 'USER'),
       (4, 5, 1, 'USER'),
       (5, 6, 1, 'USER'),
       (6, 7, 1, 'USER'),
       (7, 3, 2, 'USER'),
       (8, 11, 2, 'USER'),
       (9, 10, 2, 'USER'),
       (10, 9, 2, 'USER'),
       (11, 8, 2, 'USER'),
       (12, 7, 2, 'USER');

insert into cards_members (card_id, members_id)
values (1, 1),
       (1, 3),
       (1, 5),
       (1, 6),
       (1, 7),
       (2, 3),
       (2, 11),
       (2, 10),
       (2, 9),
       (2, 8),
       (2, 7);

insert into subtasks_workspaces_members(sub_task_id, workspaces_members_id)
values (1, 1);


