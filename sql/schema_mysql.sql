-- ============================================================
-- Gym Membership Management System - Database Schema (MySQL)
-- ============================================================

DROP DATABASE IF EXISTS gym_management;
CREATE DATABASE gym_management;
USE gym_management;

-- ---------------------------------------------------
-- TRAINERS
-- ---------------------------------------------------
CREATE TABLE trainers (
    trainer_id      INT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    phone           VARCHAR(15)  NOT NULL,
    email           VARCHAR(100),
    specialization  VARCHAR(100) NOT NULL,   -- e.g. Weight Training, Yoga, Cardio
    experience_yrs  INT DEFAULT 0
);

-- ---------------------------------------------------
-- MEMBERS
-- ---------------------------------------------------
CREATE TABLE members (
    member_id       INT AUTO_INCREMENT PRIMARY KEY,
    name            VARCHAR(100) NOT NULL,
    phone           VARCHAR(15)  NOT NULL,
    email           VARCHAR(100),
    age             INT,
    gender          VARCHAR(10),
    join_date       DATE NOT NULL,
    trainer_id      INT,
    FOREIGN KEY (trainer_id) REFERENCES trainers(trainer_id) ON DELETE SET NULL
);

-- ---------------------------------------------------
-- SUBSCRIPTION PLANS (master list of plan types)
-- ---------------------------------------------------
CREATE TABLE plans (
    plan_id         INT AUTO_INCREMENT PRIMARY KEY,
    plan_name       VARCHAR(50) NOT NULL,     -- Basic / Premium / Platinum
    duration_months INT NOT NULL,
    price           DECIMAL(10,2) NOT NULL
);

-- ---------------------------------------------------
-- SUBSCRIPTIONS (a member subscribing to a plan)
-- ---------------------------------------------------
CREATE TABLE subscriptions (
    subscription_id INT AUTO_INCREMENT PRIMARY KEY,
    member_id       INT NOT NULL,
    plan_id         INT NOT NULL,
    start_date      DATE NOT NULL,
    end_date        DATE NOT NULL,
    status          VARCHAR(20) DEFAULT 'ACTIVE',   -- ACTIVE / EXPIRED / CANCELLED
    FOREIGN KEY (member_id) REFERENCES members(member_id) ON DELETE CASCADE,
    FOREIGN KEY (plan_id)   REFERENCES plans(plan_id)
);

-- ---------------------------------------------------
-- WORKOUT PLANS (assigned by a trainer to a member)
-- ---------------------------------------------------
CREATE TABLE workout_plans (
    workout_id      INT AUTO_INCREMENT PRIMARY KEY,
    member_id       INT NOT NULL,
    trainer_id      INT NOT NULL,
    title           VARCHAR(100) NOT NULL,      -- e.g. "Fat Loss - Phase 1"
    description     VARCHAR(500),
    days_per_week   INT DEFAULT 3,
    created_date    DATE NOT NULL,
    FOREIGN KEY (member_id)  REFERENCES members(member_id) ON DELETE CASCADE,
    FOREIGN KEY (trainer_id) REFERENCES trainers(trainer_id)
);

-- ---------------------------------------------------
-- PAYMENTS
-- ---------------------------------------------------
CREATE TABLE payments (
    payment_id      INT AUTO_INCREMENT PRIMARY KEY,
    member_id       INT NOT NULL,
    subscription_id INT,
    amount          DECIMAL(10,2) NOT NULL,
    payment_date    DATE NOT NULL,
    payment_mode    VARCHAR(20) DEFAULT 'CASH',   -- CASH / CARD / UPI
    status          VARCHAR(20) DEFAULT 'PAID',   -- PAID / DUE
    FOREIGN KEY (member_id) REFERENCES members(member_id) ON DELETE CASCADE,
    FOREIGN KEY (subscription_id) REFERENCES subscriptions(subscription_id) ON DELETE SET NULL
);

-- ---------------------------------------------------
-- SAMPLE DATA
-- ---------------------------------------------------
INSERT INTO plans (plan_name, duration_months, price) VALUES
('Basic', 1, 1000.00),
('Premium', 3, 2700.00),
('Platinum', 12, 9000.00);

INSERT INTO trainers (name, phone, email, specialization, experience_yrs) VALUES
('Arun Kumar', '9876543210', 'arun@gym.com', 'Weight Training', 5),
('Sneha Menon', '9876501234', 'sneha@gym.com', 'Yoga & Flexibility', 3),
('Vishnu Das', '9845123456', 'vishnu@gym.com', 'Cardio & CrossFit', 4);

INSERT INTO members (name, phone, email, age, gender, join_date, trainer_id) VALUES
('Rahul Nair', '9123456780', 'rahul@mail.com', 24, 'Male', '2026-01-05', 1),
('Anjali Pillai', '9123456781', 'anjali@mail.com', 28, 'Female', '2026-02-10', 2),
('Kiran Raj', '9123456782', 'kiran@mail.com', 31, 'Male', '2026-03-01', 3);

INSERT INTO subscriptions (member_id, plan_id, start_date, end_date, status) VALUES
(1, 2, '2026-01-05', '2026-04-05', 'ACTIVE'),
(2, 1, '2026-02-10', '2026-03-10', 'EXPIRED'),
(3, 3, '2026-03-01', '2027-03-01', 'ACTIVE');

INSERT INTO workout_plans (member_id, trainer_id, title, description, days_per_week, created_date) VALUES
(1, 1, 'Muscle Gain - Phase 1', 'Compound lifts, progressive overload, 3 sets x 10 reps', 4, '2026-01-06'),
(2, 2, 'Flexibility & Core', 'Sun salutations, core stability, breathing drills', 3, '2026-02-11'),
(3, 3, 'Fat Loss - HIIT', 'High intensity interval training + steady state cardio', 5, '2026-03-02');

INSERT INTO payments (member_id, subscription_id, amount, payment_date, payment_mode, status) VALUES
(1, 1, 2700.00, '2026-01-05', 'UPI', 'PAID'),
(2, 2, 1000.00, '2026-02-10', 'CASH', 'PAID'),
(3, 3, 9000.00, '2026-03-01', 'CARD', 'PAID');
