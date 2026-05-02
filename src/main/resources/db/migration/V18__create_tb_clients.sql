CREATE TABLE tb_clients (
    id UUID PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    phone VARCHAR(13),
    email VARCHAR(100),
    user_id UUID NOT NULL,

    CONSTRAINT fk_user_id
        FOREIGN KEY (user_id)
            REFERENCES tb_user(id)
            ON DELETE CASCADE
);

