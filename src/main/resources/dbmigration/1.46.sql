-- apply changes
alter table rc_minecraft_items drop index uq_rc_minecraft_items_key;
alter table rc_minecraft_items add constraint uq_rc_minecraft_items_namespaced_key unique  (namespaced_key);
alter table rc_player_tags add column count integer default 0 not null;

