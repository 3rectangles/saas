CREATE TABLE IF NOT EXISTS url_regex_query_url (
    id Text PRIMARY KEY,
    host TEXT,
    regex TEXT,
    query TEXT,
    url_pattern TEXT,
    description TEXT,
	created_on TIMESTAMP,
    updated_on TIMESTAMP
);

CREATE INDEX IF NOT EXISTS regex_host ON url_regex_query_url (host);
ALTER table partner_company add column if not exists host_allowed_redirect text;
