#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

for i in $DIR/../lib/*.jar; do
  CP="$CP:$i"
done

java -cp $CP com.connexience.server.workflow.cloud.cmd.FlushLibrary
