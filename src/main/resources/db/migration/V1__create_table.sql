create sequence if not exists user_seq;
create sequence if not exists workspace_seq;
create sequence if not exists board_seq;
create sequence if not exists column_seq;
create sequence if not exists card_seq;
create sequence if not exists checklist_seq;
create sequence if not exists sub_task_seq;
create sequence if not exists time_seq;
create sequence if not exists estimation_seq;
create sequence if not exists notification_seq;
create sequence if not exists label_seq;
create sequence if not exists comment_seq;
create sequence if not exists attachment_seq;
create sequence if not exists basket_seq;
create sequence if not exists user_workspace_roles_seq;


create table users
(
    id         bigint not null
        primary key,
    email      varchar(255),
    first_name varchar(255),
    last_name  varchar(255),
    password   varchar(255),
    photo_link varchar(255),
    role       varchar(255)
);

alter table users
    owner to postgres;


create table workspaces
(
    id          bigint not null
        primary key,
    is_favorite boolean,
    name        varchar(255),
    lead_id     bigint
        constraint fkjdpinmmcwu1mxjok65q0mwy0m
            references users
);

alter table workspaces
    owner to postgres;


create table boards
(
    id           bigint not null
        primary key,
    background   varchar(255),
    is_archive   boolean,
    is_favorite  boolean,
    title        varchar(255),
    workspace_id bigint
        constraint fkthpwqhdcr9u32c3tkrh7isid4
            references workspaces
);

alter table boards
    owner to postgres;


create table columns
(
    id         bigint not null
        primary key,
    is_archive boolean,
    title      varchar(255),
    board_id   bigint
        constraint fkiylg7oiwdt1tnoff75rkbihc0
            references boards
);

alter table columns
    owner to postgres;


create table cards
(
    id            bigint not null
        primary key,
    created_at    date,
    description   varchar(10000),
    is_archive    boolean,
    title         varchar(255),
    board_id      bigint
        constraint fkk0nnnx4q6pmiiwp0u5i26vhlm
            references boards,
    column_id     bigint
        constraint fklpqsumqub2x95veana5uak4gc
            references columns,
    creator_id    bigint
        constraint fke8eko8v0i2chvreac36b5xlw3
            references users,
    moved_user_id bigint
        constraint fkjhs3mbhdy31tt66wkj5woyv85
            references users
);

alter table cards
    owner to postgres;


create table checklists
(
    id      bigint  not null
        primary key,
    count   integer not null,
    title   varchar(255),
    card_id bigint
        constraint fkoj4bw9uwdrk2h3yfglgyxuq2u
            references cards
);

alter table checklists
    owner to postgres;


create table subtasks
(
    id           bigint not null
        primary key,
    description  varchar(10000),
    is_done      boolean,
    checklist_id bigint
        constraint fks6srigtw2nj1vxtm7a3xscty
            references checklists
);

alter table subtasks
    owner to postgres;


create table times
(
    id     bigint  not null
        primary key,
    hour   integer not null,
    minute integer not null
);

alter table times
    owner to postgres;


create table estimations
(
    id               bigint  not null
        primary key,
    due_date         date,
    reminder         integer not null,
    start_date       date,
    text             varchar(10000),
    card_id          bigint
        constraint fkdvsissxwr5s1h3mryf46ikj72
            references cards,
    deadline_time_id bigint
        constraint fkbmbxp8b0x78sq9hdku29silry
            references times,
    start_time_id    bigint
        constraint fkabclh86osm4fymwjkyehw1j5r
            references times,
    sub_task_id      bigint
        constraint fkqhq5e6q5jhe1j0mov63uhygec
            references subtasks,
    user_id          bigint
        constraint fk517ocqm6x0mxuxwuku3krp9ge
            references users
);

alter table estimations
    owner to postgres;


create table notifications
(
    id                bigint not null
        primary key,
    created_at        timestamp,
    is_read           boolean,
    message           varchar(10000),
    notification_type varchar(255),
    card_id           bigint
        constraint fkaglsotkhmudvh71vtprbcusyf
            references cards,
    column_id         bigint
        constraint fk1196pyimnq15nnyechrnum7sg
            references columns,
    estimation_id     bigint
        constraint fk25bftgxb70j9l9ql42q8vd1cs
            references estimations,
    from_user_id      bigint
        constraint fkpalb3w8yony75cf2odwxks4ns
            references users,
    sub_task_id       bigint
        constraint fkd2asc4jnkp253ol782e3b77o6
            references subtasks,
    user_id           bigint
        constraint fk9y21adhxn0ayjhfocscqox7bh
            references users
);

alter table notifications
    owner to postgres;


create table labels
(
    id          bigint not null
        primary key,
    color       varchar(255),
    description varchar(10000),
    card_id     bigint
        constraint fko8natb445mhd8r1d4fi4k17n7
            references cards
);

alter table labels
    owner to postgres;


create table comments
(
    id           bigint not null
        primary key,
    created_date timestamp,
    text         varchar(10000),
    card_id      bigint
        constraint fkq1d8ryms7bmgcdllfk7k50oe4
            references cards,
    user_id      bigint
        constraint fk8omq0tc18jd43bu5tjh6jvraq
            references users
);

alter table comments
    owner to postgres;


create table attachments
(
    id            bigint not null
        primary key,
    attached_date timestamp,
    document_link varchar(255),
    card_id       bigint
        constraint fk8a70ieewfki0nbv4mjs3mof23
            references cards
);

alter table attachments
    owner to postgres;


create table baskets
(
    id           bigint not null
        primary key,
    archive_date date,
    board_id     bigint
        constraint fkf9xdnak9s2xd6tkc6wfyp8x7j
            references boards,
    card_id      bigint
        constraint fk6el06emipio2u866smudss8dl
            references cards,
    column_id    bigint
        constraint fknqshp33d31kb0tw82dghexwho
            references columns
);

alter table baskets
    owner to postgres;


create table users_boards
(
    members_id bigint not null
        constraint fkg7lb2iwmqrqkov0mt07pbyemj
            references users,
    boards_id  bigint not null
        constraint fkde2dsqttk1ppphhh1u31s2cpq
            references boards
);

alter table users_boards
    owner to postgres;


create table user_workspace_roles
(
    id           bigint not null
        primary key,
    role         varchar(255),
    user_id      bigint
        constraint fki9m71gois8uxii6n8ag04b8ty
            references users,
    workspace_id bigint
        constraint fkod8du16sgq755sle4r0ybvcyb
            references workspaces
);

alter table user_workspace_roles
    owner to postgres;


create table workspaces_all_issues
(
    workspace_id  bigint not null
        constraint fke2o7ca8x5ccb8hee64rbvgts7
            references workspaces,
    all_issues_id bigint not null
        constraint fkd932qv7hd8wf6k6che08r758x
            references cards
);

alter table workspaces_all_issues
    owner to postgres;


create table cards_members
(
    card_id    bigint not null
        constraint fknfg1s8nddn6tin4atukm3dih8
            references cards,
    members_id bigint not null
        constraint fk89fiogrruk6qv7xchl9hoc6ke
            references users
);

alter table cards_members
    owner to postgres;


create table subtasks_workspaces_members
(
    sub_task_id           bigint not null
        constraint fk9oxrs1kk7ph2b6jtsccl8ltco
            references subtasks,
    workspaces_members_id bigint not null
        constraint fk7c13h02mi9arijlc6xvpgyx5n
            references users
);

alter table subtasks_workspaces_members
    owner to postgres;
