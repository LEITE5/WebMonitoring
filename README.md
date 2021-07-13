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
