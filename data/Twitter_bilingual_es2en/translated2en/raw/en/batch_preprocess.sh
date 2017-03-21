#!/bin/bash
outbound=10
for i in $(seq 0 $outbound)
do
	python add_features.py en train.$i.coll.en train.$i
done

read -p "Press any key to continue... " -n1 -s