alter table interview
   add column interview_structure_name text;

alter table interview_structure
    add CONSTRAINT constraint_name UNIQUE (name);
