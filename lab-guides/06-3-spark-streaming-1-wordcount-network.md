        <link rel='stylesheet' href='assets/css/main.css'/>

# Spark Streaming 1 - Network Wordcount

## Overview

We build a network word count program in spark

## Runtime

20-30 minutes

## Step-1: Import Project `spark-streaming`

Project location : `/labs/spark-streaming`

### Eclipse Settings

Running this program with Eclipse's own JRE might result in `illegal access` errors!

Change the default JRE to JDK-11 as follows:

* **Eclipse --> Window --> Preferences**
* Expand **Java --> Installed JREs**
* Click **Add**
* Select **Standard VM**
* in JRE_HOME type in **/usr/lib/jvm/java-11-openjdk-amd64**.  You can navigate to **/usr/lib/jvm** directory and select this folder too
* Make this as **default JRE**

## Step-2: Run Netcat

Open a terminal and run netcat as follows

```bash
nc -lk 10000
```

## Step-3:  Inspect `WordCount1_Network.java`

file : [labs/spark-streaming/src/main/java/x/spark_streaming/WordCount1_Network.java](labs/spark-streaming/src/main/java/x/spark_streaming/WordCount1_Network.java)

class :  `x.spark_streaming.WordCount1_Network`

## Step-4: Run WordCount1 within Eclipse

Run this class from within Eclipse

## Step-5: Send data via Netcat

On netcat terminal type some words as below

```text
a b
B  c
A b
```

You will see output like 

TODO

```console
Batch: 3
-------------------------------------------
+-----+-----+
|value|count|
+-----+-----+
|    B|    1|
|    A|    1|
|    c|    1|
|    b|    2|
|    a|    1|
+-----+-----+
```

## Step-6: Fix Case Sensitivity

You will notice the word count is case sensitive.  so 'a' and 'A' are treated as different words.

Fix it as below to ignore case

```java
// around line 32

// Split the lines into words
    Dataset<String> words = lines.as(Encoders.STRING())
                                 .flatMap((FlatMapFunction<String, String>) x -> 
                                    Arrays.asList(x.toLowerCase().split("\\s+")).iterator(), 
                                    Encoders.STRING());
```

## Step-7: Test Again

Run the program in Eclipse again and send in some more test data using netcat

```text
a b
B  c
A b
```

**Q:Is the ignore-case fix working?**

**ACTION: Try various inputs and see how the wordcount program reacts**

## Step-8: Run it from command line on Host Machine

We have successfully tested the WordCount program from Eclipse.  Now let's try to run it as a spark program.

First we are going to compile code:

Make sure you are in project dir

```bash
cd   ~/kafka-advanced-labs/labs/spark-streaming

#  Start spark-dev container
./run-spark-dev.sh  bash
```

This will drop you into spark-dev container, in `/workspace` directory.  That will contain all our code

Let's kick off compiling

```bash
# within container
mvn clean package

ls -l  target
```

This would actually compile the code a create jar files.

Exit the container by presssing `Ctrl+D` or typing `exit`

## Step-9:  Keep Running netcat

If you can have previous instance of netcat running, you can continue to use that.

Otherwise you can start one like this:

```bash
nc -lk  10000
```

## Step-10:  Launch Spark program

on another terminal 

```bash
# from native host (not within docker container)

cd   ~/kafka-advanced-labs/labs/spark-streaming

~/apps/spark/bin/spark-submit   --class  x.spark_streaming.WordCount1_Network  target/spark-streaming-1.0.jar
```

## Step-11:  Send data via Netcat

On netcat terminal type some words as below

```text
a b c
a b
```

You will see output from Spark program as follows:

```console
-------------------------------------------
Batch: 2
-------------------------------------------
+-----+-----+
|value|count|
+-----+-----+
|    c|    1|
|    b|    2|
|    a|    2|
+-----+-----+
```

## Step-12: Running a Spark program from within docker container

We just ran Spark natively on our machine.

Now let's try it in docker.  For this we will use `elephantscale/spark-dev` container

```bash
# on docker host

# be in project dir
cd   ~/kafka-advanced-labs/labs/spark-streaming

bash ./run-spark-dev.sh  bash
```

This will start a docker container.

This will drop you into spark-dev container, in `/workspace` directory.  That will contain all our code

Let's compile our code.

```bash
# within spark-dev container
mvn clean package

ls -l  target
```

You will see compiled jar files

## Step-13:  Login to the container

Find the container id of the `spark-dev` container by doing the following on a **different terminal**

```bash
# on docker host

docker ps --format 'table {{ .ID }}\t{{ .Names }}\t{{.Image}}'

# docker ps | grep spark-dev
```

You might see an output like this

```console
aaaabbbb   elephantscale/spark-dev 
```

So in this case `aaaabbbb` is our container id.

**Login to the same container using docker**

```bash
#   on docker host

docker exec -it   CONTAINER_ID  /bin/bash
# substitute your container id
```

This will log you into the same container as Spark-dev

**Start netcat from here**

```bash
# from within the spark-dev container

nc  -lk 10000
```

Now we are running netcat within `spark-dev` container!

## Step-14: Run spark-submit

Now switch back to the other `spark-dev` console.

From within the `spark-dev` container, run this:

```bash
# within spark-dev container

spark-submit   --class  x.spark_streaming.WordCount1_Network  target/spark-streaming-1.0.jar
```

## Step-15: Send data using Netcat

On netcat terminal type some words as below

```text
a b c
a b
```

And you will see following output on Spark terminal

```console
-------------------------------------------
Batch: 2
-------------------------------------------
+-----+-----+
|value|count|
+-----+-----+
|    c|    1|
|    b|    2|
|    a|    2|
+-----+-----+
```

## Lab is done!  👏

---

## Dev Notes

### TO-Investigate: Root permission needed?

Do we need to start the container as root?

Start the container as ROOT.  This is very important as Spark will check for user name and it will fail on non-existing username.

CURRENT_USER=root   ./start-kafka-dev.sh
