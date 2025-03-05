CREATE USER 'clusteradmin'@'%' IDENTIFIED BY 'todo1234!';

GRANT ALL privileges ON *.* TO 'clusteradmin'@'%' with grant option;

reset master;