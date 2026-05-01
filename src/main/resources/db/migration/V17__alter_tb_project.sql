ALTER TABLE tb_projects DROP COLUMN bdi;

ALTER TABLE tb_project_items
DROP COLUMN project_id CASCADE,
ADD COLUMN budget_id UUID NOT NULL,
ADD CONSTRAINT fk_budget_id FOREIGN KEY (budget_id) REFERENCES tb_budgets(id) ON DELETE CASCADE;

