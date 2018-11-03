-- apply changes
alter table rc_disguises add column skin_owner_name varchar(255);
alter table rc_disguises add column mineskin_id integer default 0 not null;

