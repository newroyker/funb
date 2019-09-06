#!/bin/bash

sbt clean coverage test
sbt coverageReport
open lib/target/scala-2.11/scoverage-report/index.html
