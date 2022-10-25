insert into users (id, first_name, last_name, photo_link, email, password,
                   role)
values (1, 'Esen', 'Niyazov', 'photoLink', 'esen@gmail.com',
        '$2a$12$CfCkLOcM6zwdxxryeGF6tuqiaSlWpDxdFaUXpBPjNMPwSoMfA8nMa',
        'SUPER_ADMIN'),

       (2, 'Nurmuhammad', 'Nurbekov', 'photoLink', 'admin@gmail.com',
        '$2a$12$TbmCs13/KkzrcHd.rSnZeOH5qvVc79nuGb/bYhbC3sT8dYK9unj2q', 'ADMIN'),

       (3, 'Nursultan', 'Askarov', 'users_photoLink', 'user@gmail.com',
        '$2a$12$oRaUgIHxClArpaNXqFIPD.xUSju4iSsGFGEwlQgSWV2aii/3LqYyW', 'USER');

insert into workspaces (id, name, is_favorite, lead_id)
values (1, 'Workspace Name', false,  1);

insert into boards(id, title, photo_link, is_archive, is_favorite, workspace_id)
values (1, 'First Board', 'board_photo', false, false, 1, 1);

insert into lines (id, title, board_id)
values (1, 'First Line', 1);

insert into cards (id, title, description, is_archive, board_id, line_id)
values (1, 'First Card', 'First Description', false, 1, 1);

insert into checklists (id, name, task_tracker, card_id)
values (1, 'First Card', 1, 1);

insert into estimations (id, created_date, deadline_date, reminder, text, card_id, user_id)
values (1, '2022-02-01', '2022-12-12', 5, 'First Text', 1, 1);

insert into subtasks (id, description, is_done, checklist_id, estimation_id)
values (1, 'Subtasks description ', false, 1, 1);

insert into notifications (id, text, is_read, user_id, sub_task_id, card_id, line_id, estimation_id)
values (1, 'First Text', false, 1, 1, 1, 1, 1);

insert into labels (id, color, description, card_id)
values (1, 'First Description', 'White', 1);

insert into comments (id, created_date, text, user_id, card_id)
values (1, '2022-10-23', 'First text', 1, 1);

insert into attachments (id, document_link, attached_date, card_id)
values (1, 'link', '2022-10-12', 1);

insert into baskets (id, archive_date, board_id, card_id, line_id)
values (1, '2000-04-30', 1, 1, 1);
values (1, '2020-04-30', 1, 1, 1);

insert into users_workspaces (members_id, workspaces_id)
values (1, 1);

insert into users_boards (members_id, boards_id)
values (1, 1);

insert into user_workspace_roles (id, user_id, workspace_id, role)
values (1, 1, 1, 'SUPER_ADMIN');

insert into workspaces_all_issues (workspace_id, all_issues_id)
values (1, 1);

insert into cards_members (card_id, members_id)
values (1, 1);