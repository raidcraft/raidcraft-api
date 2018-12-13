-- apply changes
alter table rc_minecraft_items add column deprecated_since varchar(255);

alter table rc_tags add column auto_generated tinyint(1) default 0 not null;

