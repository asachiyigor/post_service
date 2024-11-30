CREATE TYPE visibility AS ENUM ('ALL', 'SUBSCRIBERS', 'FAVORITES', 'OWNER');
ALTER TABLE Album ADD column VISIBILITY visibility DEFAULT 'ALL';
ALTER TABLE Album ADD column favorites varchar DEFAULT '{}';