alter table if exists payment
drop column amount,
add column amount double precision,
add column currency text,
add column type text,
add column type_id text,
add column razorpay_payment_id text;

