FROM maven:3.8.1-jdk-11 as DEPS

ENV APP_DIR=/opt/app

WORKDIR $APP_DIR
COPY speaker/pom.xml ${APP_DIR}/speaker/pom.xml
COPY tutor/pom.xml ${APP_DIR}/tutor/pom.xml
COPY pom.xml .

RUN mvn -e -C dependency:go-offline -DexcludeArtifactIds=speaker

FROM maven:3.8.1-jdk-11 as BUILDER

ENV APP_DIR=/opt/app

WORKDIR $APP_DIR
COPY --from=DEPS /root/.m2 /root/.m2
COPY --from=DEPS ${APP_DIR} ${APP_DIR}
COPY audio ${APP_DIR}/audio
COPY speaker/src ${APP_DIR}/speaker/src
COPY tutor/src ${APP_DIR}/tutor/src

RUN mvn -e clean package

FROM openjdk:11.0.10-oracle

ENV APP_NAME=flashcards
ENV APP_DIR=/opt/app
ENV JAR_PATH=${APP_DIR}/tutor/target/${APP_NAME}.jar
ENV JAVA_OPTS="-Dspring.profiles.active=docker"

WORKDIR $APP_DIR
COPY --from=BUILDER $JAR_PATH $JAR_PATH

EXPOSE 8080
RUN echo "DO:::RUN ${JAR_PATH}"
CMD java $JAVA_OPTS -jar ${JAR_PATH}
