/* ======================================================== */
/* ======================================================== */
/* Create Tables */
/* ======================================================== */
/* ======================================================== */

/* 
- Not sure how to store profile_photo. Right now I have it that the user
  must select from a list of 100 stock photos and we store the id of the photo
*/

CREATE TABLE Users(
	userid INT,
	gender CHAR(1) NOT NULL, /* M (male), F (female) or O (other) only */
	birth_date DATE,
	name VARCHAR(80) NOT NULL,
	email VARCHAR(320) NOT NULL UNIQUE, /* 64 local + 1 @ + 255 domain */
	password VARCHAR(128) NOT NULL,
	date_time_created DATE,
	profile_photo INT DEFAULT 0,
	is_active BOOLEAN,
	is_matchmaking BOOLEAN,
	location VARCHAR(100),
	PRIMARY KEY(userid),
	CONSTRAINT genderCheck CHECK(gender = 'M' OR gender = 'F' OR gender = 'O'),
	CONSTRAINT emailCheck CHECK(email LIKE '%@%'),
	CONSTRAINT photoChoice CHECK(profile_photo >= 0 AND profile_photo < 100)
);

CREATE TABLE Dating_Preferences(
	type VARCHAR(30),
	PRIMARY KEY(type)
);

CREATE TABLE Interests(
	type VARCHAR(30),
	category VARCHAR(30),
	PRIMARY KEY(type)
);

CREATE TABLE Topics(
	subject VARCHAR(30),
	PRIMARY KEY(subject)
);

CREATE TABLE Chats(
	chatid INT,
	date_time_created DATE,
	is_active BOOLEAN,
	requestid INT,
	PRIMARY KEY(chatid)
);

CREATE TABLE Conversations(
	conversation_number INT,
	chatid INT,
	PRIMARY KEY(conversation_number, chatid),
	FOREIGN KEY(chatid) REFERENCES Chats
);

CREATE TABLE Connections(
	ctnid INT,
	status VARCHAR(30),
	userid1 INT,
	userid2 INT,
	data_time_created DATE,
	PRIMARY KEY(ctnid),
	FOREIGN KEY(userid1) REFERENCES Users,
	FOREIGN KEY(userid2) REFERENCES Users
);

CREATE TABLE Messages(
	msgid INT,
	status VARCHAR(30),
	timestamp DATE,
	content VARCHAR(300),
	userid INT, 
	chatid INT,
	conversation_number INT,
	PRIMARY KEY(msgid),
	FOREIGN KEY(userid) REFERENCES Users, /* For "posts" */
	FOREIGN KEY(conversation_number, chatid) REFERENCES Conversations /* For "contains" */
);

/*
- Not sure how to model time_duration
*/
CREATE TABLE Icebreakers(
	conversation_number INT,
	chatid INT,
	subject VARCHAR(30), /* For "guides" */
	time_duration INT,
	PRIMARY KEY(conversation_number, chatid, subject),
	FOREIGN KEY(conversation_number, chatid) REFERENCES Conversations,
	FOREIGN KEY(subject) REFERENCES Topics
);

/* -------------------------------------------------------- */
/* -------------------------------------------------------- */
/* -------------------------------------------------------- */
/* -------------------------------------------------------- */

CREATE TABLE relates(
	type1 VARCHAR(30),
	type2 VARCHAR(30),
	strength INT,
	PRIMARY KEY(type1, type2),
	FOREIGN KEY(type1) REFERENCES Interests,
	FOREIGN KEY(type2) REFERENCES Interests,
	CONSTRAINT min_max_str CHECK(strength >= 0 AND strength <= 10)
);

/*
- Participation constraints not met
*/
CREATE TABLE generates(
	type VARCHAR(30),
	subject VARCHAR(30),
	PRIMARY KEY(type, subject),
	FOREIGN KEY(type) REFERENCES Interests,
	FOREIGN KEY(subject) REFERENCES Topics
);

/*
- Participation constraint not met
*/
CREATE TABLE possesses(
	userid INT,
	type VARCHAR(30),
	PRIMARY KEY(userid, type),
	FOREIGN KEY(userid) REFERENCES Users,
	FOREIGN KEY(type) REFERENCES Dating_Preferences
);

/*
- Participation constraint not met
*/
CREATE TABLE likes(
	userid INT,
	type VARCHAR(30),
	PRIMARY KEY(userid, type),
	FOREIGN KEY(userid) REFERENCES Users,
	FOREIGN KEY(type) REFERENCES Interests
);

/*
- Participation constraint not met
*/
CREATE TABLE participates(
	userid INT,
	chatid INT,
	--type VARCHAR(30),
	PRIMARY KEY(userid, chatid),
	FOREIGN KEY(userid) REFERENCES Users,
	FOREIGN KEY(chatid) REFERENCES Chats
	--FOREIGN KEY(type) REFERENCES Interests
);

/*
- Not totally sure what to do here
*/
CREATE TABLE requests(
	userid_Request INT NOT NULL,
	userid_Requestee INT NOT NULL,
	chatid INT,
	ctnid INT NOT NULL,
	PRIMARY KEY(ctnid),
	FOREIGN KEY(userid_Request) REFERENCES Users,
	FOREIGN KEY(userid_Requestee) REFERENCES Users,
	FOREIGN KEY(chatid) REFERENCES Chats,
	FOREIGN KEY(ctnid) REFERENCES Connections
);

/* Key-Part constraint => No table */

/* ------------------------------
CREATE TABLE guides(
);
 --------------------------------*/

/* Weak-Ent constraint => No table */

/* ------------------------------
CREATE TABLE belongs(
);
 --------------------------------*/

CREATE TABLE has(
	userid INT,
 	ctnid INT,
	PRIMARY KEY(userid, ctnid),
	FOREIGN KEY(userid) REFERENCES Users,
	FOREIGN KEY(ctnid) REFERENCES Connections
);

/* Key-Part constraint => No table */

/* ------------------------------
CREATE TABLE posts(
	
);
 --------------------------------*/


/* Key-Part constraint => No table */

/* ------------------------------
CREATE TABLE contains(
);
 --------------------------------*/

\d
