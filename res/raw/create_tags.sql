CREATE TABLE tags (
	id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL
	,NAME VARCHAR NOT NULL UNIQUE
	,type INTEGER NOT NULL
	,sort INTEGER NOT NULL
	)