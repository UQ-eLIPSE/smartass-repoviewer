# SmartAss Repo Editor

SmartAss repo editor is a desktop GUI application for managing the SmartAss database.

## Requirements
The following software is required

* Java JDK 8
* Gradle

## Installing
To install clone the git repo and install gradle. The project also requires jDvi, which can be downloaded using the downloadJars task

```shell
$ gradle wrapper
$ ./gradlew downloadJars
```

## Running
To run simply call the gradle run task

```shell
$ ./gradlew build
$ ./gradlew run
```

## Code Walkthrough
Entry point -> repository/MainForm.java
