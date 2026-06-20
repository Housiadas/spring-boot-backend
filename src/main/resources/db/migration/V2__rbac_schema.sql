CREATE TABLE roles (
    id          uuid         PRIMARY KEY,
    name        varchar(50)  NOT NULL UNIQUE,
    description varchar(255),
    created_by  varchar(255),
    updated_by  varchar(255),
    created_at  timestamp(6) NOT NULL,
    updated_at  timestamp(6)
);

CREATE TABLE permissions (
    id          uuid         PRIMARY KEY,
    name        varchar(100) NOT NULL UNIQUE,
    description varchar(255),
    created_at  timestamp(6) NOT NULL,
    updated_at  timestamp(6)
);

CREATE TABLE user_roles (
    user_id uuid NOT NULL REFERENCES users (id) ON DELETE CASCADE,
    role_id uuid NOT NULL REFERENCES roles (id) ON DELETE CASCADE,
    PRIMARY KEY (user_id, role_id)
);
CREATE INDEX idx_user_roles_role_id ON user_roles (role_id);

CREATE TABLE role_permissions (
    role_id       uuid NOT NULL REFERENCES roles (id) ON DELETE CASCADE,
    permission_id uuid NOT NULL REFERENCES permissions (id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);
CREATE INDEX idx_role_permissions_permission_id ON role_permissions (permission_id);

-- Seed roles
INSERT INTO roles (id, name, description, created_at) VALUES
    (gen_random_uuid(), 'ROLE_ADMIN', 'System administrator with full access', now()),
    (gen_random_uuid(), 'ROLE_USER',  'Standard authenticated user',          now());

-- Seed permissions
INSERT INTO permissions (id, name, description, created_at) VALUES
    (gen_random_uuid(), 'user:read',   'Read own user data',          now()),
    (gen_random_uuid(), 'user:write',  'Update own user data',        now()),
    (gen_random_uuid(), 'user:delete', 'Delete own user account',     now()),
    (gen_random_uuid(), 'admin:read',  'Read any user / admin data',  now()),
    (gen_random_uuid(), 'admin:write', 'Modify any user / admin data', now());

-- ROLE_ADMIN gets every permission
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r
CROSS JOIN permissions p
WHERE r.name = 'ROLE_ADMIN';

-- ROLE_USER gets user:read
INSERT INTO role_permissions (role_id, permission_id)
SELECT r.id, p.id
FROM roles r, permissions p
WHERE r.name = 'ROLE_USER' AND p.name = 'user:read';
