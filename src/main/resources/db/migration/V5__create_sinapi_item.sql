CREATE TABLE tb_sinapi_item (
                                id UUID PRIMARY KEY,
                                cod_sinapi VARCHAR(50) NOT NULL,
                                description TEXT NOT NULL,
                                classification VARCHAR(50) NOT NULL,
                                unit VARCHAR(10) NOT NULL,
                                uf VARCHAR(2) NOT NULL,
                                price NUMERIC(12, 2) NOT NULL DEFAULT 0.0,
                                tax_relief VARCHAR(10) NOT NULL
);