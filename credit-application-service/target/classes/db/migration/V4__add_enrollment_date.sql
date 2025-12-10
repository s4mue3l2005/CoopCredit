ALTER TABLE affiliates ADD COLUMN enrollment_date DATE;

-- Initialize existing records with a default value (e.g., today or a past date) to avoid null issues if needed.
-- For now, we leave it nullable or set a default. Let's set it to CURRENT_DATE for existing records.
UPDATE affiliates SET enrollment_date = CURRENT_DATE WHERE enrollment_date IS NULL;

-- Make it not null after update if strictness is required, but keeping it nullable for now might be safer unless we are sure.
-- Requirement says "Registrar afiliados con: ... fecha de afiliación". So it should likely be mandatory.
ALTER TABLE affiliates ALTER COLUMN enrollment_date SET NOT NULL;
