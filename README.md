## WebMonitoring - Final Project Directory

Synthethic Transaction Monitoring application developed with the intuit of testing how synthethic transactions can be used to detect the origin of problems encountered by users when navigate a website.

# Requirements

JDK11

Maven

Docker

Docker Compose

# Setup

Package Application with Maven in the parent directory of the application (monitoring)

Windows  
```
 mvn clean install -P packageAsDocker
 cd docker
 docker build -t monitoring:latest .
 cd .. 
 cd ..
 docker-compose up
```

Linux Based 
```
 mvn clean install -P packageAsDocker
 cd ..
 docker-compose up
```

# Project Structure

The development of the application is split into 4 main components:
* Data Capture - data is generated with Selenium by simulating a user interaction with a webpage and [BrowserMob Proxy](https://github.com/lightbody/browsermob-proxy) is used to capture the HTTP requests and responses to a HAR file.
* Storing Data - the HAR files are broken down into smaller JSON and saved as individual transactions on ElasticSearch using [Jest](https://github.com/searchbox-io/Jest).
* Data Visualisation - stored data can be viewed through a dashboard in Kibana where it is presented through graphs and charts. (Import chart to Kibana from: XXXX)
* Deployment - The components were packaged into Docker containers and can be started using the DockerCompose file in the main directory.

The complete report about this project can be found [Here](https://github.com/LEITE5/WebMonitoring/blob/main/Documentation/LEITE_COM616_AE2.pdf). 

Deployment Diagram

![Deployment Diagram](https://github.com/LEITE5/WebMonitoring/blob/main/Diagrams/DeploymentDiagram.png)
