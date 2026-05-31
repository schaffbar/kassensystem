ALTER TABLE schaffbar.tool ADD COLUMN area VARCHAR(50) NOT NULL DEFAULT 'HOLZ';
ALTER TABLE schaffbar.tool ADD COLUMN certification_requirement VARCHAR(50) NOT NULL DEFAULT 'RED';

-- Remove defaults after migration (they were only needed for existing rows)
ALTER TABLE schaffbar.tool ALTER COLUMN area DROP DEFAULT;
ALTER TABLE schaffbar.tool ALTER COLUMN certification_requirement DROP DEFAULT;
