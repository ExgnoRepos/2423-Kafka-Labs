<link rel='stylesheet' href='assets/css/main.css'/>

# Kafka Streams Labs

## Overview

Getting to learn Kafka Streams API

## Runtime

1.5 - 2 hrs

## Step 1: Import `kafka-streams` project

Import this project `labs/kafka-streams`  as **existing maven project**

## Step 2: Create a `clickstream` topic

Create `clickstream` topic as follows
- parition = 10
- replica = 3

You can use Kafka Manager (easy) or CLI (Look at lab 3.1 for example usage)

---

## Step 3: Steaming Intro Lab


### Step 3-A Clickstream Producer

File : `src/main/java/x/utils/ClickStreamProducer.java`

We will  continue use this producer

* Run the producer in Eclipse
* Make sure it is sending messages as follows
    - key : Domain
    - value : clickstream data
    - example:

```console
  key=facebook.com, value={"timestamp":1451635200005,"session":"session_251","domain":"facebook.com","cost":91,"user":"user_16","campaign":"campaign_5","ip":"ip_67","action":"clicked"}
```

### Step 3-B Streaming Consumer 1

This consumer will read and print a KafkaStream.

File : `src/main/java/x/kafka_streams/StreamsConsumer1.java`

**Run the `kafka_streams/StreamsConsumer1` in Eclipse**

### Step 3-C: Run `ClickStreamProducer`

**Run the `utils.ClickStreamProducer` in Eclipse**

Expected output

```console
[INFO ] 2018-07-22 14:15:41.248 [main] StreamsConsumer1:main(48) - kstreams starting on clickstream

[KSTREAM-SOURCE-0000000000]: facebook.com, {"timestamp":1451635200005,"session":"session_251","domain":"facebook.com","cost":91,"user":"user_16","campaign":"campaign_5","ip":"ip_67","action":"clicked"}

[KSTREAM-SOURCE-0000000000]: foxnews.com, {"timestamp":1451635200010,"session":"session_224","domain":"foxnews.com","cost":17,"user":"user_89","campaign":"campaign_4","ip":"ip_57","action":"viewed"}

[KSTREAM-SOURCE-0000000000]: facebook.com, {"timestamp":1451635200015,"session":"session_160","domain":"facebook.com","cost":73,"user":"user_53","campaign":"campaign_1","ip":"ip_20","action":"blocked"}
```

---

## Additional Lab Files

## Step 4 :  Streaming Consumer ForEach

This consumer will read and print a KafkaStream.

File : `labs/kafka-streams/src/main/java/x/kafka_streams/StreamingConsumer2_Foreach.java`

**Run the `StreamsConsumer2_Foreach` in Eclipse**

**Run the `utils.ClickStreamProducer` in Eclipse**

Expected output

```console

[INFO ] 2018-07-22 14:18:52.552 [main] StreamsConsumer2_Foreach:main(55) - kstreams starting on clickstream

[KSTREAM-SOURCE-0000000000]: facebook.com, {"timestamp":1451635200005,"session":"session_251","domain":"facebook.com","cost":91,"user":"user_16","campaign":"campaign_5","ip":"ip_67","action":"clicked"}

[DEBUG] 2018-07-22 14:18:52.712 [kafka-streams-consumer1-9a276a4b-0a94-4907-957b-4b973dd06a2b-StreamThread-1] StreamsConsumer2_Foreach:apply(47) - key:facebook.com, value:{"timestamp":1451635200005,"session":"session_251","domain":"facebook.com","cost":91,"user":"user_16","campaign":"campaign_5","ip":"ip_67","action":"clicked"}

[KSTREAM-SOURCE-0000000000]: cnn.com, {"timestamp":1451635200020,"session":"session_66","domain":"cnn.com","cost":31,"user":"user_29","campaign":"campaign_3","ip":"ip_49","action":"blocked"}

[DEBUG] 2018-07-22 14:18:52.712 [kafka-streams-consumer1-9a276a4b-0a94-4907-957b-4b973dd06a2b-StreamThread-1] StreamsConsumer2_Foreach:apply(47) - key:cnn.com, value:{"timestamp":1451635200020,"session":"session_66","domain":"cnn.com","cost":31,"user":"user_29","campaign":"campaign_3","ip":"ip_49","action":"blocked"}

[KSTREAM-SOURCE-0000000000]: foxnews.com, {"timestamp":1451635200010,"session":"session_224","domain":"foxnews.com","cost":17,"user":"user_89","campaign":"campaign_4","ip":"ip_57","action":"viewed"}

```

## Step 5: Streaming Consumer Filter

This consumer will read a KafkaStream and extract only `action=clicked` events.

**Run the `StreamsConsumer3_Filter` in Eclipse**

**Run the `utils.ClickStreamProducer` in Eclipse**

Expected output below.

Notice `KStream-filtered-CLICKED` will only have 'action=clicked' events.

```console
[INFO ] 2018-07-22 14:20:38.542 [main] StreamsConsumer3_Filter:main(53) - kstreams starting on clickstream

[KSTREAM-FILTER-0000000001]: facebook.com, {"timestamp":1451635200005,"session":"session_251","domain":"facebook.com","cost":91,"user":"user_16","campaign":"campaign_5","ip":"ip_67","action":"clicked"}

[KSTREAM-FILTER-0000000001]: google.com, {"timestamp":1451635200025,"session":"session_29","domain":"google.com","cost":16,"user":"user_1","campaign":"campaign_5","ip":"ip_74","action":"clicked"}

[KSTREAM-FILTER-0000000001]: twitter.com, {"timestamp":1451635200040,"session":"session_259","domain":"twitter.com","cost":8,"user":"user_1","campaign":"campaign_7","ip":"ip_60","action":"clicked"}

[KSTREAM-FILTER-0000000001]: google.com, {"timestamp":1451635200050,"session":"session_89","domain":"google.com","cost":77,"user":"user_55","campaign":"campaign_5","ip":"ip_12","action":"clicked"}

[KSTREAM-FILTER-0000000001]: npr.org, {"timestamp":1451635200035,"session":"session_72","domain":"npr.org","cost":85,"user":"user_40","campaign":"campaign_2","ip":"ip_16","action":"clicked"}

[KSTREAM-FILTER-0000000001]: foxnews.com, {"timestamp":1451635200045,"session":"session_53","domain":"foxnews.com","cost":46,"user":"user_26","campaign":"campaign_6","ip":"ip_58","action":"clicked"}
```

## Step 6:  Streaming Consumer Map

Apply `map` transformation for KStreams

This consumer will read a KafkaStream and map the incoming record into key value pair with action.

Example 1:
```
Incoming :
{"timestamp":1451635200005,"session":"session_251","domain":"facebook.com","cost":91,"user":"user_16","campaign":"campaign_5","ip":"ip_67","action":"clicked"}

Output:
("clicked", 1)
```

Example 2:
```
Input :
{"timestamp":1451635200010,"session":"session_224","domain":"foxnews.com","cost":17,"user":"user_89","campaign":"campaign_4","ip":"ip_57","action":"viewed"}

Output:
("viewed", 1)
```

**Run the `StreamsConsumer3_Map` in Eclipse**

**Run the `utils.ClickStreamProducer` in Eclipse**

Expected output:

Notice `KStream-Action` will only have (action,1)

```console

[INFO ] 2018-07-22 14:21:27.487 [main] StreamsConsumer4_Map:main(81) - kstreams starting on clickstream

[KSTREAM-SOURCE-0000000000]: facebook.com, {"timestamp":1451635200005,"session":"session_251","domain":"facebook.com","cost":91,"user":"user_16","campaign":"campaign_5","ip":"ip_67","action":"clicked"}

[DEBUG] 2018-07-22 14:21:27.649 [kafka-streams-consumer3-cc3c7211-fb5f-4b99-93c5-daf403beb1b5-StreamThread-1] StreamsConsumer4_Map:apply(62) - map() : got : {"timestamp":1451635200005,"session":"session_251","domain":"facebook.com","cost":91,"user":"user_16","campaign":"campaign_5","ip":"ip_67","action":"clicked"}

[DEBUG] 2018-07-22 14:21:27.649 [kafka-streams-consumer3-cc3c7211-fb5f-4b99-93c5-daf403beb1b5-StreamThread-1] StreamsConsumer4_Map:apply(66) - map() : returning : KeyValue(clicked, 1)

[KSTREAM-MAP-0000000002]: clicked, 1
----

[KSTREAM-SOURCE-0000000000]: cnn.com, {"timestamp":1451635200020,"session":"session_66","domain":"cnn.com","cost":31,"user":"user_29","campaign":"campaign_3","ip":"ip_49","action":"blocked"}

[DEBUG] 2018-07-22 14:21:27.650 [kafka-streams-consumer3-cc3c7211-fb5f-4b99-93c5-daf403beb1b5-StreamThread-1] StreamsConsumer4_Map:apply(62) - map() : got : {"timestamp":1451635200020,"session":"session_66","domain":"cnn.com","cost":31,"user":"user_29","campaign":"campaign_3","ip":"ip_49","action":"blocked"}

[DEBUG] 2018-07-22 14:21:27.650 [kafka-streams-consumer3-cc3c7211-fb5f-4b99-93c5-daf403beb1b5-StreamThread-1] StreamsConsumer4_Map:apply(66) - map() : returning : KeyValue(blocked, 1)

[KSTREAM-MAP-0000000002]: blocked, 1
----

[KSTREAM-SOURCE-0000000000]: foxnews.com, {"timestamp":1451635200010,"session":"session_224","domain":"foxnews.com","cost":17,"user":"user_89","campaign":"campaign_4","ip":"ip_57","action":"viewed"}

[DEBUG] 2018-07-22 14:21:27.651 [kafka-streams-consumer3-cc3c7211-fb5f-4b99-93c5-daf403beb1b5-StreamThread-1] StreamsConsumer4_Map:apply(62) - map() : got : {"timestamp":1451635200010,"session":"session_224","domain":"foxnews.com","cost":17,"user":"user_89","campaign":"campaign_4","ip":"ip_57","action":"viewed"}

[DEBUG] 2018-07-22 14:21:27.651 [kafka-streams-consumer3-cc3c7211-fb5f-4b99-93c5-daf403beb1b5-StreamThread-1] StreamsConsumer4_Map:apply(66) - map() : returning : KeyValue(viewed, 1)

[KSTREAM-MAP-0000000002]: viewed, 1
----

[KSTREAM-SOURCE-0000000000]: google.com, {"timestamp":1451635200025,"session":"session_29","domain":"google.com","cost":16,"user":"user_1","campaign":"campaign_5","ip":"ip_74","action":"clicked"}

[DEBUG] 2018-07-22 14:21:27.651 [kafka-streams-consumer3-cc3c7211-fb5f-4b99-93c5-daf403beb1b5-StreamThread-1] StreamsConsumer4_Map:apply(62) - map() : got : {"timestamp":1451635200025,"session":"session_29","domain":"google.com","cost":16,"user":"user_1","campaign":"campaign_5","ip":"ip_74","action":"clicked"}

[DEBUG] 2018-07-22 14:21:27.651 [kafka-streams-consumer3-cc3c7211-fb5f-4b99-93c5-daf403beb1b5-StreamThread-1] StreamsConsumer4_Map:apply(66) - map() : returning : KeyValue(clicked, 1)

[KSTREAM-MAP-0000000002]: clicked, 1
----

```

## Step 7 : Kafka Streaming - Groupby

This consumer will apply the following transformation to a KafkaStream
- map the incoming record into key value pair with (action,1)
- aggregate all actions by
  - groupby action
  - and counting them

**Run the `StreamsConsumer5_GroupBy` in Eclipse**

**Run the `utils.ClickStreamProducer` in Eclipse**

Expected output:

```console
....

[KTABLE-TOSTREAM-0000000009]: clicked, 13
[KTABLE-TOSTREAM-0000000009]: blocked, 7
[KTABLE-TOSTREAM-0000000009]: viewed, 3
[KTABLE-TOSTREAM-0000000009]: clicked, 14
[KTABLE-TOSTREAM-0000000009]: blocked, 8
[KTABLE-TOSTREAM-0000000009]: clicked, 15
[KTABLE-TOSTREAM-0000000009]: blocked, 9
[KTABLE-TOSTREAM-0000000009]: clicked, 16
[KTABLE-TOSTREAM-0000000009]: clicked, 17
[KTABLE-TOSTREAM-0000000009]: blocked, 10
```

#### Run Producer Again

**Run the `utils.ClickStreamProducer` in Eclipse**

**Notice the counts, what do you see?**

### Bonus Lab: Inspect `action-count` topic

In this bonus lab, instead of printing the results to console, we are going to write it out to another Kafka topic.

Once you make the code changes, you can inspect Kafka topic as follows

Using Kafkacat

```bash
$   kafkacat -q -C -b localhost:9092 -t action-count  -s key=s  -s value=q -f '%k:%s\n'
```

or using console consumer

```bash
  $   ~/apps/kafka/bin/kafka-console-consumer.sh\
      --bootstrap-server "localhost:9092"\
      --from-beginning\
      --property print.key=true\
      --property value.deserializer=org.apache.kafka.common.serialization.LongDeserializer\
      --topic action-count
```

## Step 8: Kafka Streaming - Window

This consumer will apply the following transformation to a KafkaStream
- map the incoming record into key value pair with (action,1)
- aggregate all actions by
  - groupby action
  - and counting them

**Run the `StreamsConsumer5_Window` in Eclipse**

**Run the `utils.ClickStreamProducer` in Eclipse**

Expected output

```console

[KTABLE-TOSTREAM-0000000009]: clicked, 13
[KTABLE-TOSTREAM-0000000009]: blocked, 7
[KTABLE-TOSTREAM-0000000009]: viewed, 3
[KTABLE-TOSTREAM-0000000009]: clicked, 14
[KTABLE-TOSTREAM-0000000009]: blocked, 8
[KTABLE-TOSTREAM-0000000009]: clicked, 15
[KTABLE-TOSTREAM-0000000009]: blocked, 9
[KTABLE-TOSTREAM-0000000009]: clicked, 16
[KTABLE-TOSTREAM-0000000009]: clicked, 17
[KTABLE-TOSTREAM-0000000009]: blocked, 10
```

**Run the `utils.ClickStreamProducer` in Eclipse**

**Notice the counts, what do you see?**






