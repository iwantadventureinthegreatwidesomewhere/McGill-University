/* -----------------------------------
-------------------------------------*/
/* Check constraints */
/* -----------------------------------
-------------------------------------*/
INSERT INTO Users VALUES
	('505', 'W', '1996-01-02', 'Melissa', 'melissa123@hotmail.com', '123abc', '2020-01-13', 1, TRUE, TRUE, 'Montreal'); /* Not valid gender */

INSERT INTO Users VALUES
	('505', 'F', '1996-01-02', 'Melissa', 'melissa123ahotmail.com', '123abc', '2020-01-13', 1, TRUE, TRUE, 'Montreal'); /* Not valid email */

INSERT INTO Users VALUES
	('505', 'F', '1996-01-02', 'Melissa', 'melissa123@hotmail.com', '123abc', '2020-01-13', 100, TRUE, TRUE, 'Montreal'); /* Not valid photo id */

INSERT INTO Users VALUES
	('505', 'F', '1996-01-02', 'Melissa', 'melissa123@hotmail.com', '123abc', '2020-01-13', -1, TRUE, TRUE, 'Montreal'); /* Not valid photo id */

INSERT INTO relates VALUES
	('Soccer', 'Soccer', 11); /* Not valid strength value */

INSERT INTO relates VALUES
	('Soccer', 'Soccer', -1); /* Not valid strength value */
