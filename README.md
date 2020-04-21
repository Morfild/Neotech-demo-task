## How to run

Go into the root of the project.

Build the container images
```
$ docker-compose build
```

Start docker-compose and watch the containers start
```
$ docker-compose up
```

### Workaround
You have to give grant privileges to a MySQL user, after the container is ready, use
```
$ docker exec -it neotech-database /bin/bash
```
Now you can connect to database, password: rootpasswd
```
$ mysql -u root -p
```
Use the following queries to give the privileges
```
$ GRANT ALL PRIVILEGES ON neotech.* TO 'user'@'%' IDENTIFIED BY 'userpassw';
$ FLUSH PRIVILEGES;
```
Congratulations!!! Now the database is ready for connections.
Let's restart the container and see the magic
```
$ docker-compose restart
```
The app will be run in default mode, every n seconds (TIMER_PERIOD) will insert timestamp to the database,
in case of a bad connection to the database for n seconds (CONNECTION_TIMEOUT) the app will try to insert the entry again.
The environment variables can be changed in docker-compose.yml -> app -> environment.

### Mode to print all saved entries
Use the following commands to start the app in print mode
```
$ docker-compose build --build-arg args=-p
$ docker-compose up
```

### Tests
In the root folder of project execute following comand to run all test
```
$ mvn test
```