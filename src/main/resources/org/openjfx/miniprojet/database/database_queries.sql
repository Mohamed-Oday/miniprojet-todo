-- Creating the Database
CREATE DATABASE IF NOT EXISTS todo;

-- Users table - Stores user information and statistics
CREATE TABLE users (
    username varchar(255) NOT NULL,
    password varchar(255) NOT NULL,
    total_tasks int NOT NULL DEFAULT 0,
    completed_tasks int NOT NULL DEFAULT 0,
    deleted_tasks int NOT NULL DEFAULT 0,
    abondoned_tasks int NOT NULL DEFAULT 0,
    completion_rate decimal(5,3) NOT NULL DEFAULT 00.000,
    PRIMARY KEY (`username`)
);

-- Categories table - Stores task categories
CREATE TABLE categories (
    category_id int NOT NULL AUTO_INCREMENT,
    category_name varchar(255) NOT NULL,
    user_id varchar(255) NOT NULL,
    PRIMARY KEY (category_id),
    FOREIGN KEY (user_id) REFERENCES users(username)
);

-- Category collaboration table - Manages shared categories between users
CREATE TABLE category_collaboration (
    category_name varchar(255) NOT NULL,
    owner_name varchar(255) NOT NULL,
    collaborated_user varchar(255) NOT NULL,
    is_accepted tinyint(1) DEFAULT 0,
    permission enum('Read','Write') NOT NULL DEFAULT 'Read',
    PRIMARY KEY (category_name, owner_name, collaborated_user)
);

-- Tasks table - Stores task information
CREATE TABLE tasks (
    task_id int NOT NULL AUTO_INCREMENT,
    task_name varchar(255) NOT NULL,
    task_description text,
    task_startDate date NOT NULL,
    task_dueDate date NOT NULL,
    task_categoryID int DEFAULT NULL,
    task_priority varchar(255) NOT NULL DEFAULT 'Low',
    is_important tinyint(1) NOT NULL DEFAULT 0,
    status_overriden tinyint(1) NOT NULL DEFAULT 0,
    user_id varchar(255) NOT NULL,
    task_status varchar(255) NOT NULL,
    category_name varchar(255) DEFAULT NULL,
    PRIMARY KEY (task_id),
    FOREIGN KEY (task_categoryID) REFERENCES categories(category_id) ON DELETE SET NULL,
    FOREIGN KEY (user_id) REFERENCES users(`username`)
);

-- Comments table - Stores task comments
CREATE TABLE comments (
    comment_id int NOT NULL AUTO_INCREMENT,
    comment varchar(255) NOT NULL,
    task_id int NOT NULL,
    user_id varchar(255) NOT NULL,
    creation_date date NOT NULL,
    PRIMARY KEY (comment_id),
    FOREIGN KEY (task_id) REFERENCES tasks (task_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users (username)
);

-- Notifications table - Stores user notifications
CREATE TABLE notifications (
    id int NOT NULL AUTO_INCREMENT,
    user_id varchar(255) NOT NULL,
    title varchar(255) NOT NULL,
    message text NOT NULL,
    time timestamp NULL DEFAULT CURRENT_TIMESTAMP,
    is_read tinyint(1) DEFAULT 0,
    PRIMARY KEY (id),
    FOREIGN KEY (user_id) REFERENCES users(username)
);

-- Saved users table - Stores user bookmarks
CREATE TABLE saveduser (
    username varchar(255) NOT NULL,
    PRIMARY KEY (username)
);

-- Create a table to store daily task statistics
CREATE TABLE task_history (
    username VARCHAR(255),
    date_recorded DATE,
    total_tasks INT,
    completed_tasks INT,
    FOREIGN KEY (username) REFERENCES users(username),
    PRIMARY KEY (username, date_recorded)
);

-- Create a trigger to automatically record daily statistics
DELIMITER //
CREATE TRIGGER record_daily_stats
AFTER UPDATE ON users
FOR EACH ROW
BEGIN
    INSERT INTO task_history (username, date_recorded, total_tasks, completed_tasks)
    VALUES (NEW.username, CURDATE(), NEW.total_tasks, NEW.completed_tasks)
    ON DUPLICATE KEY UPDATE
        total_tasks = NEW.total_tasks,
        completed_tasks = NEW.completed_tasks;
END;
//
DELIMITER ;

-- Add a column to store task completion date
ALTER TABLE tasks ADD COLUMN completion_date DATE;

-- Add a column for reminders
ALTER TABLE tasks ADD COLUMN reminder int DEFAULT 1;

-- Create a table to store task completion history
CREATE TABLE `task_completionhistory` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `user_id` VARCHAR(255) DEFAULT NULL,
  `completion_date` DATE DEFAULT NULL,
  `task_id` INT DEFAULT NULL,
  `task_name` VARCHAR(255) DEFAULT NULL,
  `completed_timestamp` TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
);