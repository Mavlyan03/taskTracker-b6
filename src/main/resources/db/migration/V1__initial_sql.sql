insert into users (id, first_name, last_name, photo_link, email, password, role)
values (1, 'Esen', 'Niyazov', 'photoLink', 'esen@gmail.com',
        '$2a$12$m3cYSQU6bNoMc0B6PFFQku2eDX6fWEZOY/G/E9D77p7SCXlamH9Tq','SUPER_ADMIN'),

       (2, 'Datka', 'Mamatzhanova', 'photoLink', 'admin@gmail.com',
        '$2a$12$TbmCs13/KkzrcHd.rSnZeOH5qvVc79nuGb/bYhbC3sT8dYK9unj2q', 'ADMIN'),

       (3, 'Nursultan', 'Askarov', 'users_photoLink', 'user@gmail.com',
        '$2a$12$LcHYIlKllzvoMqfQpLOdqeNedFtk.SMb.CeRUyX/n7lBIPx00rtgW', 'USER');

insert into workspaces (id, name, is_favorite, lead_id)
values (1, 'Workspace Name', false,  1);

insert into boards(id, title, background, is_archive, is_favorite, workspace_id)
values (1, 'First Board', 'board_photo', false, false,  1);

insert into columns (id, title, is_archive, board_id)
values (1, 'First Column', false, 1);

insert into cards (id, title, description, is_archive, created_at, creator_id, board_id, column_id)
values (1, 'First Card', 'First Description', false, '2022-10-31',  1, 1, 1);

insert into checklists (id, title, count, card_id)
values (1, 'First Checklist', 1, 1);

insert into subtasks (id, description, is_done, checklist_id)
values (1, 'Subtasks description ', false,  1);

insert into times (id, hour, minute)
values (1, 10, 20),
       (2, 12,20);

insert into estimations (id, start_date, start_time_id, due_date, deadline_time_id, reminder, text, card_id, user_id, sub_task_id)
values (1, '2022-02-01', 1, '2022-12-12', 2, 5, 'First Text', 1, 1,  1);

insert into notifications (id, message, created_at, notification_type, from_user_id, user_id, sub_task_id, card_id, column_id, estimation_id)
values (1, 'First Text', '2022-10-31', 'ASSIGN' 1, 2, 1, 1, 1, 1);

insert into labels (id, color, description, card_id)
values (1, 'RED', 'Complete this task immediately', 1);

insert into comments (id, created_at, text, user_id, card_id)
values (1, '2022-10-23', 'First text', 1, 1);

insert into attachments (id, document_link, attached_date, card_id)
values (1, 'link', '2022-10-12', 1);

insert into baskets (id, archive_date, board_id, card_id, column_id)
values (1, '2000-04-30', 1, 1, 1);

insert into users_boards (members_id, boards_id)
values (1, 1);

insert into user_workspace_roles (id, user_id, workspace_id, role)
values (1, 1, 1, 'SUPER_ADMIN');

insert into workspaces_all_issues (workspace_id, all_issues_id)
values (1, 1);

insert into cards_members (card_id, members_id)
values (1, 1);

insert into subtasks_workspaces_members(sub_task_id, workspaces_members_id)
values (1, 1);