
ALTER TABLE public.user_comment DROP COLUMN is_active;

ALTER TABLE public.user_comment ADD deleted_on timestamp NULL;
