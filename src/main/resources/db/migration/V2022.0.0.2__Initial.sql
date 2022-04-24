CREATE TABLE public.employee (
    id    integer                NOT NULL,
    name  character varying(255) NOT NULL,
    state character varying(255) NOT NULL
);

CREATE TABLE public.action (
    id int8 NOT NULL,
    name varchar(255),
    spel varchar(255),
    primary key (id)
);

CREATE TABLE public.deferred_events (
    jpa_repository_state_id int8 NOT NULL,
     deferred_events VARCHAR(255)
);

CREATE TABLE public.guard (
    id int8 NOT NULL,
    name VARCHAR(255),
    spel VARCHAR(255),
    PRIMARY KEY (id)
);

create table public.state (
    id int8 NOT NULL,
    initial_state BOOLEAN,
    kind int4,
    machine_id VARCHAR(255),
    region VARCHAR(255),
    state VARCHAR(255),
    submachine_id VARCHAR(255),
    initial_action_id int8,
    parent_state_id int8,
    PRIMARY KEY (id)
);

CREATE TABLE public.state_entry_actions (
    jpa_repository_state_id int8 NOT NULL,
    entry_actions_id int8 NOT NULL,
    PRIMARY KEY (jpa_repository_state_id, entry_actions_id)
);

CREATE TABLE public.state_exit_actions (
    jpa_repository_state_id int8 NOT NULL,
    exit_actions_id int8 NOT NULL,
    PRIMARY KEY (jpa_repository_state_id, exit_actions_id)
);

CREATE TABLE public.state_state_actions (
    jpa_repository_state_id int8 NOT NULL,
    state_actions_id int8 NOT NULL,
    PRIMARY KEY (jpa_repository_state_id, state_actions_id)
);

CREATE TABLE public.state_machine (
    machine_id VARCHAR(255) NOT NULL,
    state VARCHAR(255),
    state_machine_context oid,
    PRIMARY KEY (machine_id)
);

CREATE TABLE public.transition (
    id int8 NOT NULL,
    event VARCHAR(255),
    kind int4,
    machine_id VARCHAR(255),
    guard_id int8,
    source_id int8,
    target_id int8,
    PRIMARY KEY (id)
);

CREATE TABLE public.transition_actions (
    jpa_repository_transition_id int8 NOT NULL,
    actions_id int8 NOT NULL,
    PRIMARY KEY (jpa_repository_transition_id, actions_id)
);

ALTER TABLE public.deferred_events ADD CONSTRAINT fk_state_deferred_events FOREIGN KEY (jpa_repository_state_id) REFERENCES state;
ALTER TABLE public.state ADD CONSTRAINT fk_state_initial_action FOREIGN KEY (initial_action_id) REFERENCES public.action;
ALTER TABLE public.state ADD CONSTRAINT fk_state_parent_state FOREIGN KEY (parent_state_id) REFERENCES public.state;
ALTER TABLE public.state_entry_actions ADD CONSTRAINT fk_state_entry_actions_a FOREIGN KEY (entry_actions_id) REFERENCES public.action;
ALTER TABLE public.state_entry_actions ADD CONSTRAINT fk_state_entry_actions_s FOREIGN KEY (jpa_repository_state_id) REFERENCES public.state;
ALTER TABLE public.state_exit_actions ADD CONSTRAINT fk_state_exit_actions_a FOREIGN KEY (exit_actions_id) REFERENCES public.action;
ALTER TABLE public.state_exit_actions ADD CONSTRAINT fk_state_exit_actions_s FOREIGN KEY (jpa_repository_state_id) REFERENCES public.state;
ALTER TABLE public.state_state_actions ADD CONSTRAINT fk_state_state_actions_a FOREIGN KEY (state_actions_id) REFERENCES public.action;
ALTER TABLE public.state_state_actions ADD CONSTRAINT fk_state_state_actions_s FOREIGN KEY (jpa_repository_state_id) REFERENCES public.state;
ALTER TABLE public.transition ADD CONSTRAINT fk_transition_guard FOREIGN KEY (guard_id) REFERENCES public.guard;
ALTER TABLE public.transition ADD CONSTRAINT fk_transition_source FOREIGN KEY (source_id) REFERENCES public.state;
ALTER TABLE public.transition ADD CONSTRAINT fk_transition_target FOREIGN KEY (target_id) REFERENCES public.state;
ALTER TABLE public.transition_actions ADD CONSTRAINT fk_transition_actions_a FOREIGN KEY (actions_id) REFERENCES public.action;
ALTER TABLE public.transition_actions ADD CONSTRAINT fk_transition_actions_t FOREIGN KEY (jpa_repository_transition_id) REFERENCES public.transition;

CREATE SEQUENCE hibernate_sequence START 1 INCREMENT 1;

