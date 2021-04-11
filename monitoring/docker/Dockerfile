ARG geckopath=/usr/local/bin/geckodriver


FROM fabric8/java-centos-openjdk11-jdk

USER root

RUN yum install -y wget firefox xvfb && \
	wget https://github.com/mozilla/geckodriver/releases/download/v0.29.0/geckodriver-v0.29.0-linux64.tar.gz && \
	tar -xf geckodriver-v0.29.0-linux64.tar.gz && \
	mv geckodriver /usr/local/bin/

COPY /target/docker/assembly-0.0.1-SNAPSHOT-onejar.jar /deployments/onejar.jar 
ENTRYPOINT ["java", "-jar", "/deployments/onejar.jar", "/usr/local/bin/geckodriver"]

