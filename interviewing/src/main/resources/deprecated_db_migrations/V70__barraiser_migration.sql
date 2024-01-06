create table if not exists modified_weightages(
    id serial primary key,
    difficulty text,
    rating decimal,
    modified_weightage decimal
);

insert into modified_weightages(difficulty,rating,modified_weightage)
values('VERY_EASY',1,4),('VERY_EASY',2,4),('VERY_EASY',3,4),('VERY_EASY',4,3.5),('VERY_EASY',5,3.5),('VERY_EASY',6,1),('VERY_EASY',7,1),('VERY_EASY',8,1),('VERY_EASY',9,1),('VERY_EASY',10,1),
('EASY',1,3),('EASY',2,3),('EASY',3,3),('EASY',4,2.5),('EASY',5,2.5),('EASY',6,1),('EASY',7,1),('EASY',8,1),('EASY',9,1),('EASY',10,1),
('MODERATE',1,1),('MODERATE',2,1),('MODERATE',3,1),('MODERATE',4,1),('MODERATE',5,1),('MODERATE',6,1),('MODERATE',7,1),('MODERATE',8,1),('MODERATE',9,1),('MODERATE',10,1),
('HARD',1,1),('HARD',2,1),('HARD',3,1),('HARD',4,1),('HARD',5,1),('HARD',6,2.5),('HARD',7,2.5),('HARD',8,2.5),('HARD',9,3),('HARD',10,3),
('VERY_HARD',1,1),('VERY_HARD',2,1),('VERY_HARD',3,1),('VERY_HARD',4,1),('VERY_HARD',5,1),('VERY_HARD',6,3.5),('VERY_HARD',7,3.5),('VERY_HARD',8,3.5),('VERY_HARD',9,4),('VERY_HARD',10,4);