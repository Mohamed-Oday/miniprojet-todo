-- Drop database if exists
DROP DATABASE IF EXISTS `accounts`;

-- Create table: categories
CREATE TABLE categories (
    category_id INT NOT NULL AUTO_INCREMENT,
    user_id VARCHAR(255),
    category_name VARCHAR(255),
    PRIMARY KEY (category_id),
    FOREIGN KEY (user_id) REFERENCES userinforamtion(username)
);

-- Create table: saveduser
CREATE TABLE saveduser (
    username VARCHAR(50) NOT NULL,
    PRIMARY KEY (username)
);

-- Create table: tasks
CREATE TABLE tasks (
    task_id INT NOT NULL AUTO_INCREMENT,
    user_id VARCHAR(50) NOT NULL,
    task_name VARCHAR(255) NOT NULL,
    task_description TEXT,
    task_dueDate DATE,
    task_status VARCHAR(10) NOT NULL,
    categories VARCHAR(50),
    is_important TINYINT(1) DEFAULT 0,
    task_startDate DATE,
    category_id INT DEFAULT 0,
    task_priority VARCHAR(50) DEFAULT 'Low',
    PRIMARY KEY (task_id),
    FOREIGN KEY (user_id) REFERENCES userinforamtion(username),
    FOREIGN KEY (category_id) REFERENCES categories(category_id)
);

-- Create table: userinformation
CREATE TABLE userinforamtion (
    username VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    PRIMARY KEY (username)
);

-- Create table: comments

CREATE TABLE comments (
  comment_id INT NOT NULL AUTO_INCREMENT PRIMARY KEY,
  comment VARCHAR(255),
  task_id INT NOT NULL,
  user_id VARCHAR(255) NOT NULL,
  creation_date DATE NOT NULL,
  FOREIGN KEY (task_id) REFERENCES tasks(task_id) ON DELETE CASCADE,
  FOREIGN KEY (user_id) REFERENCES userinforamtion(username)
);

CREATE TABLE notifications (
    id INT NOT NULL AUTO_INCREMENT,
    user_id VARCHAR(255) NOT NULL,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    time TIMESTAMP NULL DEFAULT CURRENT_TIMESTAMP,
    is_read TINYINT(1) DEFAULT '0',
    PRIMARY KEY (id),
    KEY user_id (user_id),
    FOREIGN KEY (user_id) REFERENCES userinformation (username)
);