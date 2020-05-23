create table comment
(
	id BIGINT auto_increment,
	parent_id BIGINT not null,
	type int not null,
	commentator int not null,
	gmt_create bigint not null,
	gmt_modified bigint not null,
	like_count bigint default 0,
	constraint comment_pk
		primary key (id)
);

comment on column comment.type is 'parent type';

comment on column comment.commentator is 'id of commentator';