-- apply changes
create table rc_minecraft_items (
  id                            bigint auto_increment not null,
  namespaced_key                           varchar(255),
  name                          varchar(255),
  block_data                    varchar(1024),
  since                         varchar(255),
  depcrecated_since             varchar(255),
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_modified                 datetime(6) not null,
  constraint uq_rc_minecraft_items_key unique (key),
  constraint pk_rc_minecraft_items primary key (id)
);

