
ALTER TABLE highlight ADD COLUMN IF NOT EXISTS skill_ids text[];

ALTER TABLE highlight_question ADD COLUMN IF NOT EXISTS speaker text;

ALTER TABLE interview ADD COLUMN IF NOT EXISTS are_highlights_complete boolean DEFAULT false;
