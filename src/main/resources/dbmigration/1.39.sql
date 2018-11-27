-- apply changes
create table rc_tags (
  id                            varchar(255) not null,
  description                   varchar(255),
  constraint pk_rc_tags primary key (id)
);

alter table rc_player_tags add column tag_id varchar(255);

create index ix_rc_player_tags_tag_id on rc_player_tags (tag_id);
alter table rc_player_tags add constraint fk_rc_player_tags_tag_id foreign key (tag_id) references rc_tags (id) on delete restrict on update restrict;

