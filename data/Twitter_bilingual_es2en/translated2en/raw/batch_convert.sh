#!/bin/bash
outbound=10
for i in $(seq 0 $outbound)
do
	python ConvertForSSInput.py -l en -i train.$i.coll.en
done

read -p "Press any key to continue... " -n1 -s