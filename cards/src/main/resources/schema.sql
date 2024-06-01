CREATE DATABASE IF NOT EXISTS cardsdb;
USE cardsdb;

CREATE TABLE IF NOT EXISTS card (
  card_id int AUTO_INCREMENT PRIMARY KEY,
  customer_id int NOT NULL,
  account_number int NOT NULL,
  card_number varchar(12) NOT NULL UNIQUE,
  card_type varchar(20) NOT NULL,
  monthly_purchase_limit int NOT NULL,
  amount_used decimal NOT NULL,
  current_available_amount decimal NOT NULL,
  created_at date NOT NULL,
  created_by varchar(20) NOT NULL,
  updated_at date DEFAULT NULL,
  updated_by varchar(20) DEFAULT NULL
);