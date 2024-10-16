<link rel='stylesheet' href='assets/css/main.css'/>

# Java App development in Dockerized Kafka environment - Part 1

## Overview

We are going to build and run Java app in Dockerized Kafka environment

## Runtime

20 mins

## Step 1: Make sure Kafka stack is running

Follow the previous step to ensure Kafka stack is running

## Step 2: Open `kafka-api` in Eclipse

In Eclipse:

- `Import projects`
- `Import existing maven projects`
- and choose `kafka-advanced-labs/labs/kafka-api` project

## Step-3: Inspect Consumer

Inspect file : [labs/kafka-api/src/main/java/x/api/SimpleConsumer.java](labs/kafka-api/src/main/java/x/api/SimpleConsumer.java)

Pay special attention to `bootstrap.servers` property.

Run `SimpleConsumer`

You should see message like

```console
[INFO ] 2022-07-18 02:33:38.822 [main] SimpleConsumer:main(44) - listening on test topic
```

## Step 4: Inspect Producer

Inspect file : [labs/kafka-api/src/main/java/x/api/SimpleProducer.java](labs/kafka-api/src/main/java/x/api/SimpleProducer.java)

Pay special attention to `bootstrap.servers` property.


Run `SimpleProducer`

## Step 5: Verify Messages are Going Through

Check Producer and Consumer consoles.

Also verify using Kafka Manager.




    