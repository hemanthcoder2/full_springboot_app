-- Run once on MySQL startup — creates a separate DB for each service
-- (Database-per-service pattern — each service only touches its own DB)

CREATE DATABASE IF NOT EXISTS auth_db;
CREATE DATABASE IF NOT EXISTS company_db;
CREATE DATABASE IF NOT EXISTS employee_db;
CREATE DATABASE IF NOT EXISTS team_db;
CREATE DATABASE IF NOT EXISTS dashboard_db;
CREATE DATABASE IF NOT EXISTS role_db;
CREATE DATABASE IF NOT EXISTS pricing_db;

-- Each service connects with root (for local dev)
-- In production: create a dedicated user per service with limited privileges
