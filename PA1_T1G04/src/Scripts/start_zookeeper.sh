#!/bin/bash

konsole -e "ls"
exit

cd ..
cd ..

kafka_2.11-1.0.1/bin/zookeeper-server-start.sh kafka_2.11-1.0.1/config/zookeeper.properties
