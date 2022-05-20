CREATE KEYSPACE todolist
WITH replication = {'class':'SimpleStrategy', 'replication_factor' : 1 };