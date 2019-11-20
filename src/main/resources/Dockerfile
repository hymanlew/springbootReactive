
FROM 192.168.13.129:23456/openjdk:8-jdk-alpine
VOLUME /tmp
ARG JAVA_OPTS
ENV JAVA_OPTS=$JAVA_OPTS
ADD target/springboot-redis-1.0.0-SNAPSHOT.jar springboot-redis.jar
EXPOSE 8080
# ENTRYPOINT exec java $JAVA_OPTS -jar springboot-redis.jar
# For Spring-Boot project, use the entrypoint below to reduce Tomcat startup time.
ENTRYPOINT exec java $JAVA_OPTS -Djava.security.egd=file:/dev/./urandom -jar springboot-redis.jar
