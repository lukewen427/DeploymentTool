#!/bin/bash
DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

for i in $DIR/../lib/*.jar; do
  CP="$CP:$i"
done

java  -Xdebug -Xrunjdwp:transport=dt_socket,address=5005,server=y,suspend=y -Dlog4j.configuration=enginedebug.properties -Djava.library.path=$DIR/lib  -cp $CP com.connexience.server.workflow.cloud.CloudWorkflowEngine &
