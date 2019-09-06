#!/bin/bash

export jid=`databricks jobs list | grep "FUNB" | awk '{print $1}'`

databricks jobs run-now --job-id $jid
