CREATE TABLE schaffbar.tool_certification
(
    id           UUID                        NOT NULL,
    customer_id  UUID                        NOT NULL,
    tool_id      UUID                        NOT NULL,
    certified_by UUID,
    status       VARCHAR(20)                 NOT NULL,
    certified_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_tool_certification PRIMARY KEY (id),
    CONSTRAINT uq_tool_certification_customer_tool UNIQUE (customer_id, tool_id),
    CONSTRAINT fk_tool_certification_customer FOREIGN KEY (customer_id) REFERENCES schaffbar.customer (id),
    CONSTRAINT fk_tool_certification_tool FOREIGN KEY (tool_id) REFERENCES schaffbar.tool (id)
);

CREATE INDEX idx_tool_certification_status ON schaffbar.tool_certification (status);

GRANT ALL ON TABLE schaffbar.tool_certification TO schadmin;
