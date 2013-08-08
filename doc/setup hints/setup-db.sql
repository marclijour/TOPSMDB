-- Copyright 2012 Marc Lijour
--     This file is part of TOPSMDB.
-- 
--     TOPSMDB is free software: you can redistribute it and/or modify
--     it under the terms of the GNU General Public License as published by
--     the Free Software Foundation, either version 3 of the License, or
--     (at your option) any later version.
-- 
--     TOPSMDB is distributed in the hope that it will be useful,
--     but WITHOUT ANY WARRANTY; without even the implied warranty of
--     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
--     GNU General Public License for more details.
-- 
--     You should have received a copy of the GNU General Public License
--     along with Foobar.  If not, see <http://www.gnu.org/licenses/>.

-- uncomment this line as needed
connect 'jdbc:derby:/var/lib/tomcat7/topsDB;create=true;user=tops;password=900bst';

-- all members here / flat, one read most of the time (fast retrieval)
-- updated Nov 9, 2012 with leftdate and leftreason
DROP TABLE members;
CREATE TABLE members (
	id INT NOT NULL GENERATED ALWAYS AS IDENTITY(START WITH 0, INCREMENT BY 1), 
	firstname VARCHAR(100) NOT NULL,
	lastname VARCHAR(100) NOT NULL,
	jobtitle VARCHAR(100),
	branch VARCHAR(150),
	ministry VARCHAR(100),
	city VARCHAR(50),
	phone VARCHAR(30),
	email VARCHAR(100),
	heardfrom VARCHAR(100),
	creatdate DATE,
	chapter VARCHAR(100),
	leftdate DATE,
	leftwhy VARCHAR(100),
	newsflash BOOLEAN NOT NULL DEFAULT TRUE,
	topspot BOOLEAN NOT NULL DEFAULT TRUE,
	PRIMARY KEY (id));

	-- to speed up search in the grid
create index firstnameindex on members(firstname);
create index lastnameindex on members(lastname);
create index chapterindex on members(chapter);
create index leftdateindex on members(leftdate);

 
DROP TABLE city_chapter;
CREATE TABLE city_chapter (
	city VARCHAR(100) NOT NULL,
	chapter VARCHAR(100) NOT NULL,
	PRIMARY KEY (city,chapter));

-- revised November 9, 2012
DROP TABLE dbuser;
CREATE TABLE dbuser (
	login VARCHAR(100) NOT NULL,
	email VARCHAR(100) NOT NULL,
	password VARCHAR(100) NOT NULL,
	role VARCHAR(100) NOT NULL,	-- values: 'Admin', 'Guest', 'Provincial Exec', 'Provincial Chair', 'Chapter Chair'
	PRIMARY KEY (login));
	
-- todo once emails are all non null in members table + add email as primary key in members +	
--	FOREIGN KEY (email) REFERENCES members(email) ON DELETE CASCADE ON UPDATE RESTRICT);

	
-- flagged accounts, added Nov 9, 2012
-- id = member id, flag = (no email, bounced email, gone...)
DROP TABLE flaggedmember;
CREATE TABLE flaggedmember (
	id INT NOT NULL,
	flag VARCHAR(100) NOT NULL,
	lastcheck DATE,
	FOREIGN KEY (id) REFERENCES members(id),
	PRIMARY KEY (id));

	
-- TODO Set up your Admin account (with your email and password)
INSERT INTO dbuser VALUES ('admin',   'admin@yoursite.com', 'yourpassword', 'Admin');
-- e.g.
INSERT INTO dbuser VALUES ('marc', 'marc.lijour@ontario.ca', '123', 'Admin');

-- At least one member is expected when the webapp starts 
INSERT INTO members (firstname, lastname, jobtitle, branch, ministry, city, phone, email, heardfrom, creatdate, chapter) VALUES ('Marc', 'Lijour', 'Education Officer', 'FLEPPB', 'EDU', 'Toronto', '416-212-8247', 'marc.lijour@ontario.ca', 'Marc', '2013-08-07', 'Toronto');

-- Known links between cities and chapters
INSERT INTO city_chapter VALUES('Ajax', 'Pickering');
INSERT INTO city_chapter VALUES('Alexandria', 'Ottawa');
INSERT INTO city_chapter VALUES('Aurora', 'North Toronto');
INSERT INTO city_chapter VALUES('Aylmer', 'London');
INSERT INTO city_chapter VALUES('Bancroft', 'Peterborough');
INSERT INTO city_chapter VALUES('Barrie', 'North Toronto');
INSERT INTO city_chapter VALUES('Belleville', 'Kingston');
INSERT INTO city_chapter VALUES('Blind River', 'Sault Ste Marie');
INSERT INTO city_chapter VALUES('Bolton', 'North Toronto');
INSERT INTO city_chapter VALUES('Brampton', 'Mississauga');
INSERT INTO city_chapter VALUES('Brantford', 'Hamilton');
INSERT INTO city_chapter VALUES('Brockville', 'Kingston');
INSERT INTO city_chapter VALUES('Burlington', 'Hamilton');
INSERT INTO city_chapter VALUES('Cambridge', 'Guelph');
INSERT INTO city_chapter VALUES('Chapleau', 'South Porcupine');
INSERT INTO city_chapter VALUES('Chester', 'Peterborough');
INSERT INTO city_chapter VALUES('Clinton', 'London');
INSERT INTO city_chapter VALUES('Cobourg', 'Oshawa');
INSERT INTO city_chapter VALUES('Cochrane', 'South Porcupine');
INSERT INTO city_chapter VALUES('Concord', 'North Toronto');
INSERT INTO city_chapter VALUES('Cornwall', 'Ottawa');
INSERT INTO city_chapter VALUES('Dryden', 'Thunder Bay');
INSERT INTO city_chapter VALUES('Dundas', 'Hamilton');
INSERT INTO city_chapter VALUES('Elora', 'Guelph');
INSERT INTO city_chapter VALUES('Fort Frances', 'Thunder Bay');
INSERT INTO city_chapter VALUES('Garson', 'Sudbury');
INSERT INTO city_chapter VALUES('Gloucester', 'Ottawa');
INSERT INTO city_chapter VALUES('Goderich', 'London');
INSERT INTO city_chapter VALUES('Guelph', 'Guelph');
INSERT INTO city_chapter VALUES('Hamilton', 'Hamilton');
INSERT INTO city_chapter VALUES('Hearst', 'South Porcupine');
INSERT INTO city_chapter VALUES('Huntsville', 'North Toronto');
INSERT INTO city_chapter VALUES('Kemptville', 'Ottawa');
INSERT INTO city_chapter VALUES('Kenora', 'Thunder Bay');
INSERT INTO city_chapter VALUES('Killarney', 'Sault Ste Marie');
INSERT INTO city_chapter VALUES('Kingston', 'Kingston');
INSERT INTO city_chapter VALUES('Kirkland Lake', 'South Porcupine');
INSERT INTO city_chapter VALUES('Kitchener', 'Guelph');
INSERT INTO city_chapter VALUES('Lakeshore', 'London');
INSERT INTO city_chapter VALUES('Lancaster', 'Ottawa');
INSERT INTO city_chapter VALUES('Lindsay', 'Peterborough');
INSERT INTO city_chapter VALUES('London', 'London');
INSERT INTO city_chapter VALUES('Midhurst', 'North Toronto');
INSERT INTO city_chapter VALUES('Milton', 'Mississauga');
INSERT INTO city_chapter VALUES('Mississauga', 'Mississauga');
INSERT INTO city_chapter VALUES('New Liskeard', 'North Bay');
INSERT INTO city_chapter VALUES('Newmarket', 'North Toronto');
INSERT INTO city_chapter VALUES('Nipigon', 'Thunder Bay');
INSERT INTO city_chapter VALUES('North Bay', 'North Bay');
INSERT INTO city_chapter VALUES('Oakville', 'Niagara');
INSERT INTO city_chapter VALUES('Orillia', 'North Toronto');
INSERT INTO city_chapter VALUES('Oshawa', 'Oshawa');
INSERT INTO city_chapter VALUES('Ottawa', 'Ottawa');
INSERT INTO city_chapter VALUES('Owen Sound', 'Guelph');
INSERT INTO city_chapter VALUES('Pembroke', 'Ottawa');
INSERT INTO city_chapter VALUES('Penetanguishene', 'North Toronto');
INSERT INTO city_chapter VALUES('Peterborough', 'Peterborough');
INSERT INTO city_chapter VALUES('Pickering', 'Pickering');
INSERT INTO city_chapter VALUES('Richmond Hill', 'North Toronto');
INSERT INTO city_chapter VALUES('Sarnia', 'London');
INSERT INTO city_chapter VALUES('Sault Ste Marie', 'Sault Ste Marie');
INSERT INTO city_chapter VALUES('Simcoe', 'Hamilton');
INSERT INTO city_chapter VALUES('Sioux Lookout', 'Thunder Bay');
INSERT INTO city_chapter VALUES('Smiths Falls', 'Ottawa');
INSERT INTO city_chapter VALUES('South Porcupine', 'South Porcupine');
INSERT INTO city_chapter VALUES('St Catharines', 'Niagara');
INSERT INTO city_chapter VALUES('St Thomas', 'London');
INSERT INTO city_chapter VALUES('Stratford', 'Guelph');
INSERT INTO city_chapter VALUES('Sudbury', 'Sudbury');
INSERT INTO city_chapter VALUES('Thorold', 'Niagara');
INSERT INTO city_chapter VALUES('Thunder Bay', 'Thunder Bay');
INSERT INTO city_chapter VALUES('Toronto', 'Toronto');
INSERT INTO city_chapter VALUES('Walkerton', 'Guelph');
INSERT INTO city_chapter VALUES('Waterloo', 'Guelph');
INSERT INTO city_chapter VALUES('Wawa', 'Sault Ste Marie');
INSERT INTO city_chapter VALUES('Wheatley', 'London');
INSERT INTO city_chapter VALUES('Whitby', 'Oshawa');
INSERT INTO city_chapter VALUES('Whitney', 'North Toronto');
INSERT INTO city_chapter VALUES('Windsor', 'London');
