create table if not exists user_details (
    id varchar(100) primary key,
    first_Name varchar(100),
    last_Name varchar(100),
    alma_Mater varchar(100),
    birth_Date varchar(20),
    category varchar(2),
    country varchar(100),
    designation varchar(100),
    email varchar(100),
    expert_Domains text[],
    initials varchar(100),
    domains text[],
    skill_ids text[],
    cost varchar(256),
    linked_In_Profile varchar(256),
    peer_Domains text[],
    phone varchar(20),
    work_Experience varchar(100),
    work_Experience_In_Months int,
    current_CTC varchar(100),
    is_Expert_Partner bool,
    resume_Document_Id varchar(100),
    current_Company_Id varchar(100),
    current_Company_Name varchar(100),
    last_Companies text[],
    achievements text[],
    created_on timestamp,
	updated_on timestamp
);

create table if not exists APP_CONFIG (
    name varchar (100) PRIMARY KEY,
    value varchar(100),
    created_on timestamp,
	updated_on timestamp
);

create table if not exists availability (
    id serial primary key,
	user_Id varchar(100),
	start_Date bigint,
	end_Date bigint,
	created_on timestamp,
	updated_on timestamp
);

create table if not exists booked_slot (
   id serial primary key,
	user_Id varchar(100),
	start_Date bigint,
	end_Date bigint,
	booked_By varchar (100),
	ttl bigint,
	created_on timestamp,
	updated_on timestamp
);

create table if not exists cart_item (
    id serial primary key,
	user_Id varchar(100),
    item_Id varchar(100),
    count int,
    type varchar(100),
    price json,
    created_on timestamp,
	updated_on timestamp
);

create table if not exists company (
    id varchar(100) primary key,
    name varchar(100),
    url varchar (256),
    logo varchar (256),
    created_on timestamp,
	updated_on timestamp
);

create table if not exists document (
	user_Id varchar(100) primary key,
    document_Id varchar(256),
    name varchar(256),
    s3_Url varchar(256),
     created_on timestamp,
	updated_on timestamp
);

create table if not exists target_job_attribute (
   user_Id  varchar(100)  primary key,
   companies text[],
   desired_Role varchar(256),
   time_To_Start_Applications  varchar(256),
   skills_To_Focus text[],
    created_on timestamp,
	updated_on timestamp
);

create table if not exists interview (
	id varchar(100) primary key,
    interviewer_Id varchar(100),
    interviewee_Id varchar(100),
    skill_Ids varchar(256),
    interview_Round varchar(100),
    start_Date int,
    end_Date int,
    status varchar(100),
    cost json,
    created_on timestamp,
	updated_on timestamp
);

create table if not exists payment (
    id serial primary key,
	user_Id varchar(100),
    payment_Id varchar(100),
    order_Id varchar(100),
    amount json,
    interviews json,
	status varchar(100),
     created_on timestamp,
	updated_on timestamp
);

create table if not exists phone_otp(
    id serial primary key,
    phone varchar(100),
    otp varchar(100),
    is_verified bool,
    is_valid bool,
    ttl int,
     created_on timestamp,
	updated_on timestamp
);

create table if not exists workflow (
   id varchar(100) primary key,
   step_Id varchar(100),
   completed_On bigint,
   created_on timestamp,
   updated_on timestamp
);

create table if not exists razorpay_webhook_payload (
   order_Id varchar(100),
   id varchar(100) primary key,
   payload text,
   created_on timestamp,
	updated_on timestamp
);

create table if not exists role (
    id varchar(100) primary key,
    name varchar(100),
    created_on timestamp,
	updated_on timestamp
);

create table if not exists  skills (
    id serial  primary key,
    domain text,
    name text,
    created_on timestamp,
	updated_on timestamp
)
