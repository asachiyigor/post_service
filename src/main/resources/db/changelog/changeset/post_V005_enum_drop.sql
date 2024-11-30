ALTER TABLE Album DROP column visibility;
ALTER TABLE Album ADD column visibility varchar DEFAULT 'ALL';