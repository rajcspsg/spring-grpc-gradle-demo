DROP TABLE IF EXISTS "USER" CASCADE;
CREATE TABLE "user" AS SELECT * FROM CSVREAD('classpath:user.csv');