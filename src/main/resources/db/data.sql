insert into users (id, first_name, last_name, image, email, password, role)
values (1, 'Datka', 'Mamatzhanova', 'photoLink', 'datka@gmail.com',
        '$2a$12$0POgw5oJtXlmcuRhE5unzuoyjp0uwFif7/45xVKO1RB06Bg380LLW','SUPER_ADMIN'),

       (2, 'Nurmuhammad', 'Nurbekov', 'photoLink', 'admin@gmail.com',
        '$2a$12$TbmCs13/KkzrcHd.rSnZeOH5qvVc79nuGb/bYhbC3sT8dYK9unj2q', 'ADMIN'),

       (3, 'Nursultan', 'Askarov', 'users_photoLink', 'user@gmail.com',
        '$2a$12$vkwGsKlu3LmvlxpkMz./QOHE9TrcGBU5yjPqTeqjuvjGoFk3pkSlS', 'USER');

insert into workspaces (id, name, is_favorite, lead_id)
values (1, 'Workspace Name', false,  1);

insert into boards(id, title, background, is_archive, is_favorite, workspace_id)
values (1, 'First Board', 'board_photo', false, false,  1);

insert into columns (id, title, is_archive, board_id)
values (1, 'First Column', false, 1);

insert into times (id, hour, minute)
values (1, 10, 20),
       (2, 12,20);

insert into estimations (id, start_date, start_time_id, due_date, deadline_time_id, reminder, text, user_id)
values (1, '2022-02-01', 1, '2022-12-12', 2, 5, 'First Text', 1);

insert into cards (id, title, description, is_archive, created_at, creator_id, estimation_id, board_id, column_id, workspace_id)
values (1, 'First Card', 'First Description', false, '2022-10-31',  1, 1, 1, 1, 1);

insert into checklists (id, title, count, card_id)
values (1, 'First Checklist', 1, 1);

insert into subtasks (id, description, is_done, checklist_id, estimation_id)
values (1, 'Subtasks description ', false, 1, 1);

insert into notifications (id, message, is_read, user_id, sub_task_id, card_id, column_id, estimation_id)
values (1, 'First Text', false, 1, 1, 1, 1, 1);

insert into labels (id, color, description, card_id)
values (1, 'RED', 'White', 1);

insert into comments (id, created_date, text, user_id, card_id)
values (1, '2022-10-23', 'First text', 1, 1);

insert into attachments (id, document_link, attached_date, card_id)
values (1, 'link', '2022-10-12', 1);

insert into baskets (id, archive_date, board_id, card_id, column_id)
values (1, '2000-04-30', 1, 1, 1),
       (2, '2020-04-30', 1, 1, 1);

insert into users_boards (members_id, boards_id)
values (1, 1);

insert into user_workspace_roles (id, user_id, workspace_id, role)
values (1, 1, 1, 'SUPER_ADMIN');

insert into cards_members (card_id, members_id)
values (1, 1);