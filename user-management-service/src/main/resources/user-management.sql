-- Create Users Table

CREATE TABLE users
(

    user_id         INT AUTO_INCREMENT PRIMARY KEY,

    full_name       VARCHAR(255)        NOT NULL,

    email           VARCHAR(255) UNIQUE NOT NULL,

    password_hash   VARCHAR(255)        NOT NULL,

    phone           VARCHAR(20) UNIQUE NULL,

    profile_picture VARCHAR(500) NULL,

    otp_secret      VARCHAR(255) NULL,

    sms_enabled     BOOLEAN   DEFAULT FALSE,

    is_active       BOOLEAN   DEFAULT TRUE,

    created_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    updated_at      TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    deleted_at      TIMESTAMP NULL

);

-- Addresses Table (Separated from Users Table)

CREATE TABLE addresses
(

    address_id INT AUTO_INCREMENT PRIMARY KEY,

    user_id    INT          NOT NULL,

    address    VARCHAR(500) NOT NULL,

    city       VARCHAR(100) NOT NULL,

    state      VARCHAR(100) NOT NULL,

    country    VARCHAR(100) NOT NULL,

    zip_code   VARCHAR(20)  NOT NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE

);


-- Create Roles Table

CREATE TABLE roles
(

    role_id   INT AUTO_INCREMENT PRIMARY KEY,

    role_name VARCHAR(50) UNIQUE NOT NULL

);

-- Create Permissions Table

CREATE TABLE permissions
(

    permission_id   INT AUTO_INCREMENT PRIMARY KEY,

    permission_name VARCHAR(100) UNIQUE NOT NULL

);

-- Create User-Role Relationship Table (Many-to-Many)

CREATE TABLE user_roles
(

    user_id INT,

    role_id INT,

    PRIMARY KEY (user_id, role_id),

    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE,

    FOREIGN KEY (role_id) REFERENCES roles (role_id) ON DELETE CASCADE

);

-- Create Role-Permission Relationship Table (Many-to-Many)

CREATE TABLE role_permissions
(

    role_id       INT,

    permission_id INT,

    PRIMARY KEY (role_id, permission_id),

    FOREIGN KEY (role_id) REFERENCES roles (role_id) ON DELETE CASCADE,

    FOREIGN KEY (permission_id) REFERENCES permissions (permission_id) ON DELETE CASCADE

);

-- Create Refresh Tokens Table

CREATE TABLE refresh_tokens
(

    token_id   INT AUTO_INCREMENT PRIMARY KEY,

    user_id    INT          NOT NULL,

    token      VARCHAR(500) NOT NULL,

    expires_at TIMESTAMP    NOT NULL,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    FOREIGN KEY (user_id) REFERENCES users (user_id) ON DELETE CASCADE

);
