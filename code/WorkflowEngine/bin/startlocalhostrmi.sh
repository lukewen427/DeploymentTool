#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

for i in $DIR/../lib/*.jar; do
  CP="$CP:$i"
done

java -Djava.rmi.server.hostname=localhost -Dlog4j.configuration=enginedebug.properties -Djava.library.path=$DIR/lib  -cp $CP  com.connexience.server.workflow.cloud.CloudWorkflowEngine
