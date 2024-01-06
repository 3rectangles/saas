$## Setup and Getting started with gitpod

* Create an account on https://gitpod.io/
* Get access to the BarRaiser backend project
* Download VS Code.
* Set the ssh key on gitpod https://www.gitpod.io/docs/configure/user-settings/ssh


* Install the Gitpod browser plugin
* In the browser, open the Gitlab page for the Baraiser backend branch that you want to work on.
* Click on the gitpod browser plugin[you can also start workspace from Gitpod dashboard from the backend project]
	It will create a workspace(read more about gitpod workspace).
	It should auto-open VS Code with the branch checked out.
	Hopefully, these will be prebuilt
	- all DB will be configured and started with the latest staging data.
	- dependencies will be installed and packaged
	(if prebuild is not present it will take some time and you will see this happening in the terminal)

* To run and debug: F5 or go to "run and debug" section of vs code. Click the green play button on top. Terminal command: __"gp url 5000"__ to get url of the api end point.

**Important**
To clean compile: __"mvn -Dmaven.repo.local=/workspace/m2-repository clean package -Dmaven.test.skip=true"__

* Check the ports tab in the terminal widow in VScode. port 3433 will be green. if not please run in the terminal: __"docker-compose start"__ . If this port is not there add manually.

* To modify data in DB: download pgadmin desktop app and connect to server at 3433(user: barraiser, password:barraiser1234)

* To refresh database with most latest staging db: bash /scripts/local_database_setup.sh

* You can start multiple terminal by clicking on + sign in termnail window header

* If the workspace shuts down(no activity for 30 mins, you can change this time). Workspace can be restarted from gitpod dashboard.

Happy coding from laptop/ipad/mobile/watch! Please remember to keep saving file changes if any.

## Setup and Getting started on local

### Installations

* Java 11
* Maven
* AWS cli

### Pre Requisites

* AWS credentials
* [Local database setup](https://barraiser.atlassian.net/wiki/spaces/TECH/pages/395542572/Setup+local+database+POSTGRES)

### Building & Running the code

copy the pre-commit file to .git/hooks/ folder

```
cp pre-commit ./.git/hooks/pre-commit
```

Build code with maven

```
mvn clean package
```

build code without running the tests (not recommended before raising a merge request)

```
mvn clean package -Dmaven.test.skip=true
```

Before you can run the service, do get aws credentials from someone in the team

```
java  -Xmx512m -Xms256m -jar -Dspring.profiles.active=local interviewing/target/interviewing-1.0.0.jar
```

### Code formatting

We use [spotless](https://github.com/diffplug/spotless/tree/main/plugin-maven#eclipse-jdt) to keep our code formatting
uniform. We have started it recently, hence some code will have bad formatting. Spotless runs with
every `mvn clean package` command. it will fail in pipeline if your code is not formatted.

### DataBase Connection
#### Localhost
* Write your local db credentials in **local-db.conf** to connect to local db in the following format:
* ```
	'USER_NAME=your_local_db_urser_name
	'PASS_WORD=your_local_db_pass_word
	'ENGINE=postgres
	'HOST=localhost
	'PORT=5432
	'DB_INSTANCE_IDENTIFIER=development
	```
* This file should be in barraiser folder.

### Flyway command

#### Dev

```
mvn clean install  #  Do this else you will have dependency issue.
mvn -Dflyway.configFiles=./flyway-dev.conf flyway:migrate
```

#### Prod

```
mvn -Dflyway.configFiles=./flyway-prod.conf flyway:migrate
```
