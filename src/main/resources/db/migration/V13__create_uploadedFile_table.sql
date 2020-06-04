create table uploadedFiles
(
	id int auto_increment,
	url VARCHAR(600),
	name varchar(600),
	path VARCHAR(600)
);

create unique index uploadedFiles_id_uindex
	on uploadedFiles (id);

alter table uploadedFiles
	add constraint uploadedFiles_pk
		primary key (id);
