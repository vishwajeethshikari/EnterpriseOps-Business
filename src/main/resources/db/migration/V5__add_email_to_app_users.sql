
ALTER TABLE app_users
ADD COLUMN email VARCHAR(254);

CREATE UNIQUE INDEX uq_app_users_email
ON app_users (LOWER(email))
WHERE email IS NOT NULL;
