## OpenTutor - a flashcard system to learn words 

A simple opensource application that provides flashcards to learn foreign words.    
It is supposed to be similar to Lingvo Tutor.  
Right now it is under development.  

Consists of two modules:
- [speaker](speaker) - a tts service-wrapper
- [tutor](tutor) - a body of open-tutor, logic with flashcards

##### Requirements:

- Git
- Java **11**
- Maven **3+**

##### Build and run:
```
$ git clone git@gitlab.com:sszuev/flashcards.git
$ cd flashcards
$ mvn clean package
$ java -jar tutor/target/flashcards.jar
```
##### Build and run using docker:
```
$ docker build -f db.Dockerfile -t flashcards-db .
$ docker build -f app.Dockerfile -t flashcards-app .
$ docker network create flashcards-net
$ docker run --network flashcards-net --name flashcards-db -d -p 5432:5432 flashcards-db
$ docker run --network flashcards-net -e JAVA_OPTS="-Dspring.profiles.active=docker -Dspring.datasource.url=jdbc:postgresql://flashcards-db:5432/flashcards" -d -p 8080:8080 flashcards-app
```