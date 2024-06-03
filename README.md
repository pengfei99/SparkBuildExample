# SparkBuildExample

In this tutorial, we will use sbt, scala to build a spark job which can be submitted to any spark cluster

## 1. Prerequisites

You need to install and configure 
- java(jdk 11 or later)
- scala(2.12 or later)
- sbt(1.9 or later)


Below is my project setup

```shell
$ java -version
openjdk version "11.0.2" 2019-01-15
OpenJDK Runtime Environment 18.9 (build 11.0.2+9)
OpenJDK 64-Bit Server VM 18.9 (build 11.0.2+9, mixed mode)

$ scala -version
Scala code runner version 2.12.19 -- Copyright 2002-2024, LAMP/EPFL and Lightbend, Inc.


$ sbt -version
sbt version in this project: 1.10.0
sbt script version: 1.10.0

```

## 2. Build the project skeleton

Suppose we want to create a spark job which will count word of a list of files.

### 2.1 Build the basic project layout

The project name is `WordCountSbt`

```shell
mkdir WordCountSbt

cd WordCountSbt
mkdir -p src/main/scala
mkdir -p src/test/scala
```

### 2.2. Build the module layout

Suppose your organization name is `example`, you can build your module like below

```shell
mkdir -p src/main/scala/com/example
mkdir -p src/test/scala/com/example
```

### 2.3 Build the package

I call the package `wordcount`, you can call your package as you want

```shell
mkdir -p src/main/scala/com/example/wordcount
mkdir -p src/test/scala/com/example/wordcount
```

### 2.4 Create the build.sbt file for the project WordCountSbt

The `build.sbt` file is the main config of your sbt project. It must be located under the root path of `WordCount`.
It defines:
1. define the version of the project build
2. define the scala version
3. define the organization
4. define the project name

Below is an example.
```shell
ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.12.19"
ThisBuild / organization := "org.casd"

lazy val root = (project in file("."))
  .settings(
    name := "WordCount"

  )

```

> This is the minimum setup for a sbt project. We will enrich it to add spark and build jars after.
> 

### 2.5 Valid the project structure

If everything went well, we should have a valid project structure which allow us to run some basic scala code.

Create a scala object with the name `WordCountMain.scala`, add put the below code.

```scala
package org.casd.wordcount

object WordCountMain extends App {
  println("hello world")
}
```
If you can run the code and see the output, we can go to section 3. Otherwise, you need to debug

### 2.6 Use sbt command 

If you don't have IDE, you can use the sbt command line to run your project

```shell
cd path/to/WordCountSbt

# start a sbt shell
sbt

# run the project
run

# exit the sbt shell
exit
```

### 3. Add spark dependencies

To use spark in this project, we need to add spark as dependencies, add the following lines in the `build.sbt`

```shell
val sparkVersion = "3.5.1"
libraryDependencies ++= Seq(
  "org.apache.spark" %% "spark-core" % sparkVersion % "provided",
  "org.apache.spark" %% "spark-sql" % sparkVersion % "provided"
)

fork := true
```
The `libraryDependencies ++= Seq()` specifies all dependencies that the project requires. In this example, we need
- spark-core
- spark-sql

The `fork := true` is required to run the project from the command line, It means `run the code in a new instance of JVM`. 
Without it, the program ends on an exception.

More details can be found in this post [why fork:=true](https://stackoverflow.com/questions/44298847/why-do-we-need-to-add-fork-in-run-true-when-running-spark-sbt-application?source=post_page-----80e2680d3528--------------------------------)

#### 3.1 Add the spark sample code

To build the jar file

### 4. Build the jar file



### 5. Submit the generated jar file

```shell
cd path/to/WordCountSbt/target/scala-2.12

spark-submit --class org.casd.wordcount.WordCountMain --master local spark-wordcount.jar
```

The fork := true is required to run the project from the command line, otherwise after finishing the job, the program ends on an exception. This runs the code in a new instance of JVM.