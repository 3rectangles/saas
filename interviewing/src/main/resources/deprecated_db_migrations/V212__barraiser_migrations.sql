create table if not exists feedback_sentiment(
id text,
feedback_id text,
feedback text,
rating numeric,
feedback_sentiment_label text,
feedback_sentiment_score numeric,
looks_good boolean,
created_on timestamp,
updated_on timestamp
)
