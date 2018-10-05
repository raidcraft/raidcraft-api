-- apply changes
create table rc_object_storage (
  id                            integer auto_increment not null,
  storage_name                  varchar(255) not null,
  serialization                 TEXT not null,
  constraint pk_rc_object_storage primary key (id)
);

create table raidcraft_peristant_requirements (
  id                            integer auto_increment not null,
  plugin                        varchar(255),
  requirement                   varchar(255),
  uuid                          varchar(40),
  constraint pk_raidcraft_peristant_requirements primary key (id)
);

create table raidcraft_persistant_requirement_mappings (
  id                            integer auto_increment not null,
  requirement_id                integer,
  mapped_key                    varchar(255),
  mapped_value                  varchar(255),
  constraint pk_raidcraft_persistant_requirement_mappings primary key (id)
);

create table rcinv_inventories (
  id                            integer auto_increment not null,
  title                         varchar(255),
  created                       datetime(6),
  last_update                   datetime(6),
  size                          integer not null,
  constraint pk_rcinv_inventories primary key (id)
);

create table rcinv_slots (
  id                            integer auto_increment not null,
  inventory_id                  integer,
  object_id                     integer not null,
  slot                          integer not null,
  constraint pk_rcinv_slots primary key (id)
);

create table rc_player_inventories (
  id                            integer auto_increment not null,
  player                        varchar(40) not null,
  inventory_id                  integer not null,
  object_helmet                 integer not null,
  object_chestplate             integer not null,
  object_leggings               integer not null,
  object_boots                  integer not null,
  exp                           float not null,
  level                         integer not null,
  locked                        tinyint(1) default 0 not null,
  created_at                    datetime(6),
  updated_at                    datetime(6),
  version                       bigint not null,
  constraint pk_rc_player_inventories primary key (id)
);

create index ix_raidcraft_persistant_requirement_mappings_requirement_id on raidcraft_persistant_requirement_mappings (requirement_id);
alter table raidcraft_persistant_requirement_mappings add constraint fk_raidcraft_persistant_requirement_mappings_requirement_id foreign key (requirement_id) references raidcraft_peristant_requirements (id) on delete restrict on update restrict;

create index ix_rcinv_slots_inventory_id on rcinv_slots (inventory_id);
alter table rcinv_slots add constraint fk_rcinv_slots_inventory_id foreign key (inventory_id) references rcinv_inventories (id) on delete restrict on update restrict;

