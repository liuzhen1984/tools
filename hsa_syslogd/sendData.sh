#!/bin/sh
i = 0
while [ True ] 
do
   ./client
   ((i++))
   echo ${i}
done
