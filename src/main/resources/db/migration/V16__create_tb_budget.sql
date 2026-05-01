CREATE TABLE tb_budgets (
    id UUID PRIMARY KEY,
    name VARCHAR(100),
    bdi NUMERIC(5, 2) NOT NULL DEFAULT 0.0,
    created_at TIMESTAMP NOT NULL,

    project_id UUID NOT NULL,

    CONSTRAINT fk_project_id
        FOREIGN KEY (project_id)
            REFERENCES tb_projects(id)
                        ON DELETE CASCADE

);