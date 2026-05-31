-- Remove foreign keys to align with DDD principles:
-- Aggregates should not enforce referential integrity across boundaries at the database level.

ALTER TABLE schaffbar.tool_certification DROP CONSTRAINT fk_tool_certification_customer;
ALTER TABLE schaffbar.tool_certification DROP CONSTRAINT fk_tool_certification_tool;
ALTER TABLE schaffbar.tool_instructor DROP CONSTRAINT fk_tool_instructor_tool;
