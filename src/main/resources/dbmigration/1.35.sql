-- apply changes
create table rc_disguises (
  id                            bigint auto_increment not null,
  alias                         varchar(255),
  skin_texture                  varchar(255),
  skin_signature                varchar(255),
  skin_owner                    varchar(255),
  skin_url                      varchar(255),
  version                       bigint not null,
  when_created                  datetime(6) not null,
  when_modified                 datetime(6) not null,
  constraint pk_rc_disguises primary key (id)
);

