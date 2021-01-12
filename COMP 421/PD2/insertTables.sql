/* ======================================================== */
/* ======================================================== */
/* Insert Tables */
/* ======================================================== */
/* ======================================================== */

/* Insert 1 */
/* ----------------------------------------------------*/
INSERT INTO Users VALUES(
	500, 'M', '1996-01-02', 'Bob', 'bob1y4u3@hotmail.com', '123abc', 
		'2020-01-13', 1, TRUE, TRUE, 'Montreal');

INSERT INTO possesses VALUES(500, 'F');

/* Insert 2*/
/* ----------------------------------------------------*/
INSERT INTO Users VALUES(
	501, 'F', '1995-02-03', 'Ashley', 'ashley15748@hotmail.com', 'password',
		 '2020-03-12', 1, FALSE, FALSE, 'Montreal');

INSERT INTO possesses VALUES(501, 'M');

/* Insert 3 */
/* ----------------------------------------------------*/
INSERT INTO Users VALUES(
	502, 'M', '1989-11-27', 'Charles', 'charles153290@gmail.com', '123abc', 
		'2019-05-29', 15, TRUE, TRUE, 'New York');

INSERT INTO possesses VALUES(502, 'M/F');

/* Insert 4 */
/* ----------------------------------------------------*/
INSERT INTO Users VALUES(
	503, 'M', '1995-07-14', 'George', 'george8492672@gmail.com', 'password',
	 	'2020-01-11', 2, TRUE, TRUE, 'Montreal');

INSERT INTO possesses VALUES(503, 'F');

/* Insert 5 */
/* ----------------------------------------------------*/
INSERT INTO Users VALUES(
	504, 'F', '1994-09-23', 'James', 'james1239201@hotmail.com', 
	'123abc', '2020-03-11', 1, TRUE, FALSE, 'New York');

INSERT INTO possesses VALUES(504, 'M');

/* Other inserts (Maybe can be automated in java) */
INSERT INTO Interests VALUES
	('Soccer', 'Sports'),
	('Hockey', 'Sports'),
	('Rock', 'Music'),
	('Hip-Hop', 'Music'),
	('Adventures', 'Misc.');
