# Quiz App

This is a sample project to demo my abilities with Java and Spring.

### Version
0.0.1

### Prerequisites
You must have the following installed:
1. Java 1.8 (1.7 may work, but it was tested on 1.8)
2. Gradle
3. Maven
4. MySQL

### Development Installation/Setup
First, Setup MySQL by creating the following:
1. Create a schema called `quiz`
2. Create a user `quiz_user` with the password `quiz_user`
3. Grant all privaliges to the `quiz_user` for the `quiz` schema

Next, bring down the repository by executing

```sh
$ git clone https://github.com/mbabauer/quiz_sample.git
```

Once the repository has been cloned, change to that directory and run

```sh
$ ./gradlew jar copyToLibs
```

```bat
$ ./gradlew.bat jar copyToLibs
```

This will produce a JAR in the path:
```
$ ./build/libs/quiz-0.0.1-SNAPSHOT.jar
```

Finally, to run the app from CLI, run
```sh
$ ./gradlew run
```

```bat
$ ./gradlew.bat run
```


   [localhost:8080]: <http://localhost:8080>
