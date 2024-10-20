<link rel='stylesheet' href='assets/css/main.css'/>

# Spark Setup - Native

## Overview

We will setup Spark natively

## Runtime

15 mins

## Step-1: Download and Install Spark

- Download latest Spark from [here](https://spark.apache.org/downloads.html)
- Unzip the downloaded zip file
- where Spark is unzipped is the SPARK_HOME - here we are installing it in `~/apps/spark`

```bash
cd ~/apps

wget https://dlcdn.apache.org/spark/spark-3.5.1/spark-3.5.1-bin-hadoop3.tgz

tar xvf spark-3.5.1-bin-hadoop3.tgz

mv spark-3.5.1-bin-hadoop3 spark

```

## Step-2: Test run

Start pyspark

Here we are assuming you have installed spark in `~/apps/spark` directory.  Adjust your paths accordingly

```bash
~/apps/spark/bin/pyspark
```

This will drop you into pyspark shell.

Try this following

```python
>spark.range(1,10).show()
```

You will get an output like below:

```console
+---+
| id|
+---+
|  1|
|  2|
|  3|
|  4|
|  5|
|  6|
|  7|
|  8|
|  9|
+---+
```

Done!
