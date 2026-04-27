ALTER TABLE tb_project_items DROP CONSTRAINT fk_sinapi_item_id;
ALTER TABLE tb_project_items ADD CONSTRAINT
fk_sinapi_item_id FOREIGN KEY (sinapi_item_id) REFERENCES tb_sinapi_item (id) ON DELETE CASCADE;