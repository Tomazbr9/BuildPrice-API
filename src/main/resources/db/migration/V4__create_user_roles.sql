CREATE TABLE tb_user_roles (
                               user_id UUID NOT NULL,
                               role_id UUID NOT NULL,

                               CONSTRAINT pk_user_roles PRIMARY KEY (user_id, role_id),

                               CONSTRAINT fk_user_roles_user
                                   FOREIGN KEY (user_id)
                                       REFERENCES tb_user (id)
                                       ON DELETE CASCADE,

                               CONSTRAINT fk_user_roles_role
                                   FOREIGN KEY (role_id)
                                       REFERENCES tb_role (id)
                                       ON DELETE CASCADE
);
