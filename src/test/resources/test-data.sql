    create table for_test (
        id bigint not null,
        create_date timestamp not null,
        modify_date timestamp not null,
        version integer not null,
        name varchar(255) not null,
        description varchar(255),
        primary key (id)
    );
    
    INSERT INTO for_test (
	create_date,
	modify_date,
	VERSION,
	description,
	NAME,
	ID
	)
	VALUES
		(
			TIMESTAMP '2017-04-15 17:33:09.187',
			TIMESTAMP '2017-04-15 17:33:09.187',
			0,
			'for test',
			'testname',
			1
		)