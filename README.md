## OpenTutor - a flashcard system to learn words 

### Notice

**The activity in this repository is frozen. 
The new project's home is https://github.com/crowdproj/opentutor**

A simple opensource application that provides flashcards to learn foreign words.    
It is supposed to be similar to Lingvo Tutor.  
Right now it is under development.  

Consists of two modules:
- [SPEAKER](speaker) - a TTS service
- [TUTOR](tutor) - a body of open-tutor, logic with flashcards

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
##### Build and run using docker-compose:
```
$ docker-compose -f docker-compose.yml up -d
```
_NOTE: to make the TTS service work need to obtain voice rss api key and store it in the `.env` file_