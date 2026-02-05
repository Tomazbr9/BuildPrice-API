ALTER TABLE tb_projects DROP CONSTRAINT fk_user_id;

ALTER TABLE tb_projects
ADD CONSTRAINT fk_user_id FOREIGN KEY (user_id) REFERENCES tb_user(id)
ON DELETE CASCADE;

ALTER TABLE tb_project_items DROP CONSTRAINT fk_project_id;

ALTER TABLE tb_project_items
ADD CONSTRAINT fk_project_items FOREIGN KEY (project_id) REFERENCES tb_projects(id)
ON DELETE CASCADE;

