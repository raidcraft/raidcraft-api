-- apply changes
create table rc_player_tags (
  id                            bigint auto_increment not null,
  player_id                     varchar(40) not null,
  player                        varchar(255),
  tag                           varchar(255) not null,
  duration                      varchar(255),
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_modified                 datetime(6) not null,
  constraint pk_rc_player_tags primary key (id)
);

