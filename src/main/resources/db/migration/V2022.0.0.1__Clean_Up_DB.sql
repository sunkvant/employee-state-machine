CREATE SCHEMA IF NOT EXISTS public;

DROP TABLE IF EXISTS public.employee CASCADE ;

DROP TABLE IF EXISTS public.action CASCADE;
DROP TABLE IF EXISTS public.deferred_events CASCADE;
DROP TABLE IF EXISTS public.guard CASCADE;
DROP TABLE IF EXISTS public.state CASCADE;
DROP TABLE IF EXISTS public.state_entry_actions CASCADE;
DROP TABLE IF EXISTS public.state_exit_actions CASCADE;
DROP TABLE IF EXISTS public.state_machine CASCADE;
DROP TABLE IF EXISTS public.state_state_actions CASCADE;
DROP TABLE IF EXISTS public.transition CASCADE;
DROP TABLE IF EXISTS public.transition_actions CASCADE;

DROP SEQUENCE IF EXISTS hibernate_sequence;