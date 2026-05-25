
CREATE TABLE schaffbar.tool_instructor
(
    tool_id       UUID NOT NULL,
    instructor_id UUID NOT NULL,
    CONSTRAINT pk_tool_instructor PRIMARY KEY (tool_id, instructor_id),
    CONSTRAINT fk_tool_instructor_tool FOREIGN KEY (tool_id) REFERENCES schaffbar.tool (id) ON DELETE CASCADE
);

GRANT ALL ON TABLE schaffbar.tool_instructor TO schadmin;
