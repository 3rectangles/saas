CREATE TABLE "expert_skills" (
	"id" TEXT NOT NULL,
	"expert_id" TEXT NOT NULL,
	"skill_id" TEXT NOT NULL,
	"proficiency" float4,
	"experience" int4,
	"created_on" timestamp,
	"updated_on" timestamp,
	CONSTRAINT "expert_skills_pk" PRIMARY KEY ("id")
) WITH (
  OIDS=FALSE
);

ALTER TABLE "expert_skills" ADD CONSTRAINT "expert_skills_fk0" FOREIGN KEY ("expert_id") REFERENCES "user_details"("id");
ALTER TABLE "expert_skills" ADD CONSTRAINT "expert_skills_fk1" FOREIGN KEY ("skill_id") REFERENCES "skill"("id");
ALTER TABLE "expert_skills" ADD CONSTRAINT "expert_skills_unique0" UNIQUE("expert_id", "skill_id");

