<link rel='stylesheet' href='assets/css/main.css'/>

# Java App development using Kafka-dev environment

## Overview

We are going to build our Java app using kafka-dev container as a stand alone executable jar file

## Runtime

15 mins

## Step 1: Ensure Kafka stack is running

Follow the previous lab to make sure your Kafka stack is running.

## Step 2: Locate to the labs directory of the lab repository

You have to be in the lab directory with the POM.xml file for the java-api labs *before* you run the Kafka dev container.

```bash
cd  ~/2423-Kafka-Labs/labs/kafka-api
bash ~/kafka-in-docker/kafka-dev/run-kafka-dev.sh 
```
You will be in `kafka-dev` container in the `/workspace` directory in container which is mapped to the host directory where you started the container. Confirm you are there by running `ls` in the container. You should see something like this

```shell
I have no name!@kakfa-dev:/workspace$ ls
pom.xml  src  target
```

## Step 3: Build the app

Use maven to build an executable jar file. From within `kafka-dev` container, run the command:

```bash
# within container

mvn  clean package
```

## Step 4: Run Consumer

Now run the consumer from the jar file

```bash
# within container

java -cp target/kafka-api-1.0-jar-with-dependencies.jar   x.api.SimpleConsumer
```

You will see output like:

```console
[INFO ] 2022-07-18 02:33:38.822 [main] SimpleConsumer:main(44) - listening on test topic
```

## Step 5: Run Producer

Start a second kafka-dev container using the same process you followed for the first. In a new terminaL

```bash
# on docker host
cd  ~/2423-Kafka-Labs/labs/kafka-api
bash ~/kafka-in-docker/kafka-dev/run-kafka-dev.sh 
```

In the second container, run the producer code from the jar file.

```bash
# in container

java -cp target/kafka-api-1.0-jar-with-dependencies.jar   x.api.SimpleProducer
```

You will see output from Producer like:

```console
[DEBUG] 2022-07-18 02:35:55.086 [main] SimpleProducer:main(47) - Sent record [1] (key:1658111754595, value:Hello world @ 1658111754595), meta (partition=1, offset=19, timestamp=1658111754997), time took = 466.76 ms 
```

## Step 6: Verify Messages in Consumer

Check the consumer terminal,

```bash
[DEBUG] 2022-07-18 02:36:45.126 [main] SimpleConsumer:main(50) - Got 1 messages
[DEBUG] 2022-07-18 02:36:45.126 [main] SimpleConsumer:main(53) - Received message [1] : [ConsumerRecord(topic = test, partition = 0, leaderEpoch = 0, offset = 64, CreateTime = 1658111805121, serialized key size = 13, serialized value size = 27, headers = RecordHeaders(headers = [], isReadOnly = false), key = 1658111805121, value = Hello world @ 1658111805121)]
```


## Step 7: Clean Up

End both of the kafka-dev containers. 

Shut down the dockerized kafka by running the following command from the `kafka-in-docker` directory:

```bash
bash stop-kafka-single.sh
```

Run `docker ps` to confirm the containers are stopped/

---

## End Lab
