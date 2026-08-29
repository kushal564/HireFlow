-- Seeds demo users and a small recruiter/candidate data set for local development and demos.

INSERT INTO users (full_name, email, password, role_id, email_verified, created_at)
SELECT
    'Demo Admin',
    'admin@local.dev',
    '$2a$10$fDv6F0/XltCyI8vKZdSOA.TS2ruT.h6sY7nUMiDzqND6XzHeABmdi',
    roles.id,
    TRUE,
    TIMESTAMP '2026-01-10 09:00:00'
FROM roles
WHERE roles.name = 'ADMIN'
  AND NOT EXISTS (
      SELECT 1
      FROM users
      WHERE email = 'admin@local.dev'
  );

INSERT INTO users (full_name, email, password, role_id, email_verified, created_at)
SELECT
    'Demo Recruiter',
    'recruiter@local.dev',
    '$2a$10$elScq2txbPseL6vNUYMUPe7d0gpplq0YddrCBRddhScLZZPrwnz0i',
    roles.id,
    TRUE,
    TIMESTAMP '2026-01-10 09:05:00'
FROM roles
WHERE roles.name = 'RECRUITER'
  AND NOT EXISTS (
      SELECT 1
      FROM users
      WHERE email = 'recruiter@local.dev'
  );

INSERT INTO users (full_name, email, password, role_id, email_verified, created_at)
SELECT
    'Demo Candidate',
    'candidate@local.dev',
    '$2a$10$W7ih3usVAkyPQSf79.Upyek81swoHLDpys9n/ok7rxwDnQV4Fmw5i',
    roles.id,
    TRUE,
    TIMESTAMP '2026-01-10 09:10:00'
FROM roles
WHERE roles.name = 'CANDIDATE'
  AND NOT EXISTS (
      SELECT 1
      FROM users
      WHERE email = 'candidate@local.dev'
  );

INSERT INTO companies (name, description, location, recruiter_id)
SELECT
    'Acme HR',
    'Demo recruitment company for local development',
    'Tashkent',
    recruiter.id
FROM users recruiter
WHERE recruiter.email = 'recruiter@local.dev'
  AND NOT EXISTS (
      SELECT 1
      FROM companies
      WHERE name = 'Acme HR'
        AND recruiter_id = recruiter.id
  );

INSERT INTO vacancies (title, description, location, salary_from, salary_to, company_id, created_at)
SELECT
    'Java Backend Developer',
    'Build and maintain Spring Boot services',
    'Tashkent',
    1500,
    3000,
    companies.id,
    TIMESTAMP '2026-01-10 10:00:00'
FROM companies
JOIN users recruiter ON recruiter.id = companies.recruiter_id
WHERE companies.name = 'Acme HR'
  AND recruiter.email = 'recruiter@local.dev'
  AND NOT EXISTS (
      SELECT 1
      FROM vacancies
      WHERE title = 'Java Backend Developer'
        AND company_id = companies.id
  );

INSERT INTO applications (candidate_id, vacancy_id, status, created_at)
SELECT
    candidate.id,
    vacancy.id,
    'PENDING',
    TIMESTAMP '2026-01-10 11:00:00'
FROM users candidate
JOIN vacancies vacancy ON vacancy.title = 'Java Backend Developer'
JOIN companies companies ON companies.id = vacancy.company_id
JOIN users recruiter ON recruiter.id = companies.recruiter_id
WHERE candidate.email = 'candidate@local.dev'
  AND recruiter.email = 'recruiter@local.dev'
  AND companies.name = 'Acme HR'
  AND NOT EXISTS (
      SELECT 1
      FROM applications
      WHERE candidate_id = candidate.id
        AND vacancy_id = vacancy.id
  );
