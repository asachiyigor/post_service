ALTER TABLE Album DROP column IF EXISTS visibility;
ALTER TABLE Album DROP column IF EXISTS favorites;
ALTER TABLE Album ADD column visibility varchar DEFAULT 'ALL';
ALTER TABLE Album ADD column favorites varchar DEFAULT '[]';