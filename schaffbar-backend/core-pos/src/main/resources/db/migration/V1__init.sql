
CREATE TABLE schaffbar.customer
(
    id UUID NOT NULL,
    first_name VARCHAR(255),
    last_name VARCHAR(255),
    email VARCHAR(255),
    phone VARCHAR(255),
    address_line1 VARCHAR(255),
    address_line2 VARCHAR(255),
    postal_code VARCHAR(255),
    city VARCHAR(255),
    country VARCHAR(255),
    created_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITHOUT TIME ZONE NOT NULL,
    CONSTRAINT pk_customer PRIMARY KEY (id)
);

GRANT ALL ON TABLE schaffbar.customer TO schadmin;

CREATE TABLE schaffbar.token_assignment
(
    id UUID NOT NULL,
    customer_id UUID NOT NULL UNIQUE,
    token_id VARCHAR(255) UNIQUE,
    token_type VARCHAR(255) NOT NULL,
    assignment_date TIMESTAMP WITHOUT TIME ZONE,
    unassignment_date TIMESTAMP WITHOUT TIME ZONE,
    status VARCHAR(255) NOT NULL,
    update_time TIMESTAMP WITHOUT TIME ZONE,
    CONSTRAINT pk_token_assignment PRIMARY KEY (id)
);

GRANT ALL ON TABLE schaffbar.token_assignment TO schadmin;
