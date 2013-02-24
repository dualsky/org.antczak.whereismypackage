CREATE TABLE tags_packages (
	id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL UNIQUE
	,id_tag INTEGER NOT NULL
	,id_package INTEGER NOT NULL
	,FOREIGN KEY (id_tag) REFERENCES tags(id)
	,FOREIGN KEY (id_package) REFERENCES packages(id)
	)