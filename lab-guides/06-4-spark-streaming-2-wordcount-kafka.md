<link rel='stylesheet' href='assets/css/main.css'/>

# Spark Streaming 2 - Kafka Wordcount

## Overview

In this lab, we will connect Spark and kafka!

## Runtime 

40 minutes

## Step-1: Import Project `spark-streaming`

Project location : `kafka-advanced-labs/labs/spark-streaming`

**Check these settings before proceeding !!!**

### Java-8 in POM.xml

We should be compiling our code to Java-8 target.  Otherwise we will get class mis match errors when connecting to spark-master

Ensure this by in pom.xml:

```xml
<java.version>8</java.version>
```

### Eclipse Settings

Running this program with Eclipse's own JRE might result in `illegal access` errors!

Change the default JRE to JDK-11 as follows:

* **Eclipse --> Window --> Preferences**
* Expand **Java --> Installed JREs**
* Click **Add**
* Select **Standard VM**
* in JRE_HOME type in **/usr/lib/jvm/java-11-openjdk-amd64**.  You can navigate to **/usr/lib/jvm** directory and select this folder too
* Make this as **default JRE**

## Step-2: Start kafka cluster

```bash
# on docker host

cd ~/kafka-in-docker

bash start-kafka-multi.sh
```

## Step-3: Inspect `WordCount2_Kafka`

file : [labs/spark-streaming/src/main/java/x/spark_streaming/WordCount2_Kafka.java](labs/spark-streaming/src/main/java/x/spark_streaming/WordCount2_Kafka.java)

## Step-4: Run WordCount from within Eclipse

Class :  x.spark_streaming.WordCount2_Kafka

Watch for following output:

```console
root
 |-- key: binary (nullable = true)
 |-- value: binary (nullable = true)
 |-- topic: string (nullable = true)
 |-- partition: integer (nullable = true)
 |-- offset: long (nullable = true)
 |-- timestamp: timestamp (nullable = true)
 |-- timestampType: integer (nullable = true)

root
 |-- key: string (nullable = true)
 |-- value: string (nullable = true)

root
 |-- value: string (nullable = true)

-------------------------------------------
Batch: 0
-------------------------------------------
+-----+-----+
|value|count|
+-----+-----+
+-----+-----+
```

This means now we are connected to Kafka!

## Step-5: Start kafka-dev

```bash
# on docker host machine
cd  /labs/spark-streaming

bash run-kafka-dev.sh 
```

And within container use kafkacat like this

```bash
# within kafka-dev container

    kafkacat -b kafka1:19092  -t test -P 
```

And enter the following text

```text
a  b  c
A     B    C
```

You can exit kafkacat by pressing `Ctrl+d`

## Step-6: Verify Spark Streaming Output

Initially you will see the data going in as binary.  Remember, Kakfa treats all data as binary.

**Work through step by step by fixing TODO items in the code.  Watch how the output changes**

Final output should look like

```text
-------------------------------------------
Batch: 2
-------------------------------------------
+-----+-----+
|value|count|
+-----+-----+
|    c|    2|
|    b|    2|
|    a|    2|
+-----+-----+
```

### Excellent!  We have connected and Spark and Kafka 👍

Now that we have verified our code within Eclipse, let's run it as a Spark application.

For this we will use our `spark-dev` container

## Step-7: Start `Spark-Dev` container


Lunch `spark-dev` container as follows

```bash
# on docker host

# Be in project dir
cd  ~/kafka-advanced-labs/labs/spark-streaming

bash run-spark-dev.sh  /bin/bash
```

## Step-8: Submit Spark Application

within spark docker run this

```bash
# within  spark-dev container
# we should be   in /workspace dir, this will have our code

# build the app first
mvn clean package

# spark-submit using local master
spark-submit  \
        --master 'local[*]' \
        --packages org.apache.spark:spark-sql-kafka-0-10_2.12:3.3.0 \
        --class  x.spark_streaming.WordCount2_Kafka  \
        target/spark-streaming-1.0.jar  

```

### Troubleshooting Tips

Sometimes when we run spark-submit it will fail with cryptic messages like `download failed`.  This is can be fixed by clearing the cached downloads directory.

Try this

```bash
# from within spark-dev container

rm -rf   ~/.ivy2/jars   ~/.ivy2/cache   ~/.m2/repository/
```

And then submit again!

## Step-9: Send data through Kafkacat

Switch to kafkacat terminal  and send some data like this

```text
a b 
b c
```

## Step-10: Verify Spark Streaming output

Look at Spark console

```text
-------------------------------------------
Batch: 2
-------------------------------------------
+-----+-----+
|value|count|
+-----+-----+
|    c|    1|
|    b|    2|
|    a|    1|
+-----+-----+
```

