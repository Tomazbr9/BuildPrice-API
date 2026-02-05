CREATE TABLE tb_project_items(
    id UUID PRIMARY KEY,
    quantity INTEGER NOT NULL,
    price NUMERIC(10, 2) NOT NULL DEFAULT 0.0,

    project_id UUID NOT NULL,
    sinapi_item_id UUID NOT NULL,

    CONSTRAINT fk_project_id FOREIGN KEY (project_id) REFERENCES tb_projects (id),
    CONSTRAINT fk_sinapi_item_id FOREIGN KEY (sinapi_item_id) REFERENCES tb_sinapi_item (id)

);