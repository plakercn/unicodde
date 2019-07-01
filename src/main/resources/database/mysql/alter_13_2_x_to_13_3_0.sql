-- vfs metadata
create table o_vfs_metadata (
   id bigint not null auto_increment,
   creationdate datetime not null,
   lastmodified datetime not null,
   f_uuid varchar(64) not null,
   f_deleted boolean default 0 not null,
   f_filename varchar(256) not null,
   f_relative_path varchar(2048) not null,
   f_directory bool default false,
   f_lastmodified datetime not null,
   f_size bigint default 0,
   f_uri varchar(2000) not null,
   f_uri_protocol varchar(16) not null,
   f_cannot_thumbnails bool default false,
   f_download_count bigint default 0,
   f_comment text(32000),
   f_title varchar(2000),
   f_publisher varchar(2000),
   f_creator varchar(2000),
   f_source varchar(2000),
   f_city varchar(256),
   f_pages varchar(16),
   f_language varchar(16),
   f_url text(1024),
   f_pub_month varchar(16),
   f_pub_year varchar(16),
   f_license_type_name varchar(256),
   f_license_text mediumtext,
   f_licensor text(4000),
   f_locked_date timestamp,
   f_locked bool default false,
   f_revision_nr bigint default 0 not null,
   f_revision_comment text(32000),
   f_migrated varchar(12),
   f_m_path_keys varchar(1024),
   fk_locked_identity bigint,
   fk_license_type bigint,
   fk_author bigint,
   fk_parent bigint,
   primary key (id)
);

alter table o_vfs_metadata ENGINE = InnoDB;

alter table o_vfs_metadata add constraint fmeta_to_author_idx foreign key (fk_locked_identity) references o_bs_identity (id);
alter table o_vfs_metadata add constraint fmeta_to_lockid_idx foreign key (fk_author) references o_bs_identity (id);
alter table o_vfs_metadata add constraint fmeta_to_lic_type_idx foreign key (fk_license_type) references o_lic_license_type (id);
alter table o_vfs_metadata add constraint fmeta_to_parent_idx foreign key (fk_parent) references o_vfs_metadata (id);
create index f_m_path_keys_idx on o_vfs_metadata (f_m_path_keys(100));
create index f_m_rel_path_idx on o_vfs_metadata (f_relative_path(255));
create index f_m_filename_idx on o_vfs_metadata (f_filename(255));
create index f_m_file_idx on o_vfs_metadata (f_relative_path(255),f_filename(255));


create table o_vfs_thumbnail (
   id bigint not null auto_increment,
   creationdate datetime not null,
   lastmodified datetime not null,
   f_size bigint default 0 not null,
   f_max_width bigint default 0 not null,
   f_max_height bigint default 0 not null,
   f_final_width bigint default 0 not null,
   f_final_height bigint default 0 not null,
   f_fill bool default false not null,
   f_filename varchar(256) not null,
   fk_metadata bigint not null,
   primary key (id)
);

alter table o_vfs_thumbnail ENGINE = InnoDB;

alter table o_vfs_thumbnail add constraint fthumb_to_meta_idx foreign key (fk_metadata) references o_vfs_metadata (id);


create table o_vfs_revision (
   id bigint not null auto_increment,
   creationdate datetime not null,
   lastmodified datetime not null,
   f_revision_size bigint default 0 not null,
   f_revision_nr bigint default 0 not null,
   f_revision_filename varchar(256) not null,
   f_revision_comment text(32000),
   f_revision_lastmodified datetime not null,
   f_comment text(32000),
   f_title varchar(2000),
   f_publisher varchar(2000),
   f_creator varchar(2000),
   f_source varchar(2000),
   f_city varchar(256),
   f_pages varchar(16),
   f_language varchar(16),
   f_url text(1024),
   f_pub_month varchar(16),
   f_pub_year varchar(16),
   f_license_type_name varchar(256),
   f_license_text mediumtext,
   f_licensor text(4000),
   fk_license_type bigint,
   fk_author bigint,
   fk_metadata bigint not null,
   primary key (id)
);

alter table o_vfs_revision ENGINE = InnoDB;

alter table o_vfs_revision add constraint fvers_to_author_idx foreign key (fk_author) references o_bs_identity (id);
alter table o_vfs_revision add constraint fvers_to_meta_idx foreign key (fk_metadata) references o_vfs_metadata (id);
alter table o_vfs_metadata add constraint fvers_to_lic_type_idx foreign key (fk_license_type) references o_lic_license_type (id);



