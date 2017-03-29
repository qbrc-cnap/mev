create database if not exists mevdb;
use mevdb;
create table if not exists Subscriber (email varchar(255) not null, name varchar(255), primary key (email));