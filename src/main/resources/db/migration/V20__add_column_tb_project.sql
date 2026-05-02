ALTER TABLE tb_projects
DROP COLUMN client_name,
ADD COLUMN client_id UUID NOT NULL,
ADD CONSTRAINT fk_client FOREIGN KEY (client_id) REFERENCES tb_clients(id);