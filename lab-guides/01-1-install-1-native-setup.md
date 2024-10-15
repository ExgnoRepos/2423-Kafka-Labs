<link rel='stylesheet' href='assets/css/main.css'/>

# INSTALL-1: Kafka Native Install

## Overview

Install and run Kafka natively on the machine

## Run time

20 mins

## Step 1 : Download and Unpack Kafka

Here we are assuming that you are installing kafka in `~/apps/kafka`.  Adjust command paths according to your kafka location.

**Scala 2.12 version - recommended for maximum compatibility with Spark**

```bash
## Scala 2.12 version
cd   ~/apps

wget 'https://downloads.apache.org/kafka/3.7.1/kafka_2.12-3.7.1.tgz'

tar xvf kafka_2.12-3.7.1.tgz

mv kafka_2.12-3.7.1  kafka
```

## Step 2: Start Zookeeper

Open a terminal and start Zookeeper

```bash
~/apps/kafka/bin/zookeeper-server-start.sh  ~/apps/kafka/config/zookeeper.properties   > zoo.log 2>&1  &
```

## Step 3: Start Kafka

If you have not run zookeeper as a background job, then open a new terminal to start kafka because closing the terminal in which zookeeper is running will shut it down.

```bash
JMX_PORT=9999  ~/apps/kafka/bin/kafka-server-start.sh -daemon  ~/apps/kafka/config/server.properties  > kafka.log 2>&1  
```

We are setting the optional JMX port so we can get metrics easily.

## Step 4: Verify Kafka is Running

Try JPS command to see if Kafka is running

```bash
jps
```

Output may look like this.  We have Zookeeper and Kafka running

```console
4737 QuorumPeerMain
5361 Kafka
5418 Jps
```

## Lab is Done! 👏

---

## Troubleshooting Tip: Stopping Kafka

**Do NOT stop Kafka yet.  This is just for reference**

To shut down kafka use the command below. It is good practice do to a graceful shutdown of the server, then zookeeper

```bash 
 ~/apps/kafka/bin/kafka-server-stop.sh
```

Then shut down zookeeper with the command below

```bash
~/apps/kafka/bin/zookeeper-server-stop.sh
```
And confirm with `jps` that they are no longer running.

Use `jps` to find te process ids

```bash
jps
7522 Jps
```

### Alternative Shutdown

Find the process is for kafka and zookeeper.

```text
jps
1111 QuorumPeerMain
2222 Kafka
3333 Jps
```

Then issue a kill command to for appropriate process ids

```bash
# replace it with correct process ids

# first kill kafka
kill  2222

# and then zookeeper
kill  1111
```

## Enable Deleting Topics

By default, kafka does not allow topics to be deleted by users. Since this is a useful feature in our lab work, once kafka is stopped, we can modify the configuration file to allow user deletion of topics.

Make sure kafka is stopped then execute the following

```bash
echo -e "\n\ndelete.topic.enable=true \n" >> ~/apps/kafka/config/server.properties
```

And restart Kafka and leave it running for the next lab.

--- 

## End Lab
