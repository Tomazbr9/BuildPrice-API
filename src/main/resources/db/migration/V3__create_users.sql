CREATE TABLE tb_user (
                     id UUID PRIMARY KEY,
                     email VARCHAR(50) NOT NULL UNIQUE,
                     password VARCHAR(125) NOT NULL
);