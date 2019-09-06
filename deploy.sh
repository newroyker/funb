#!/bin/bash

set -e

sbt lib/clean lib/test lib/package

sbt dp/clean dp/package

databricks fs cp --overwrite lib/target/scala-2.11/lib_2.11-0.1.0-SNAPSHOT.jar dbfs:/tmp/roy/funb_lib/lib_2.11-0.1.0-SNAPSHOT.jar

databricks fs cp --overwrite dp/target/scala-2.11/dp_2.11-0.1.0-SNAPSHOT.jar dbfs:/tmp/roy/funb_lib/dp_2.11-0.1.0-SNAPSHOT.jar

export jid=`databricks jobs create --json-file dp/src/main/resources/job_spec.json | sed -n 2p | awk '{print $2}'`

databricks jobs get --job-id $jid