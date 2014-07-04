#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

for i in $DIR/../lib/*.jar; do
  CP="$CP:$i"
done

java -Dlog4j.configuration=engineproduction.properties -Djava.library.path=$DIR/lib  -cp $CP  com.connexience.server.workflow.cloud.CloudWorkflowEngine
