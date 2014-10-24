# Java EE 7 - Angular - Arquillian - Sample Application

## How to run ? ##

* You need JDK 7 or higher and Maven 3 to run the application.

* Build the code using Maven with the command: `mvn clean install`.

* Change directory to web

* Execute  `mvn wildfly:run -P wildfly-run -Dmaven.test.skip=true`

* Browse to: http://localhost:8080/web-1.0.0-SNAPSHOT/


## Javascript Package Management (optional) ##
_Taken from the orginial, not tested_

* The required JS libraries are included in the project, but it also possible to manage them with the next steps.

* You need NPM. Please go to http://nodejs.org/download/ to get a copy.

* Once NPM is installed run the command `npm install`.

* Install Grunt `npm install -g grunt-cli`  for more information please go to http://gruntjs.com/getting-started.

* Run the command 'grunt' to download all the web dependencies and build an optimized version of the project.
