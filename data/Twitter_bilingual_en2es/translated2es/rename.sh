outbound=22
for i in $(seq 0 $outbound)
do

mv train_translated_es.$i.coll train.$i.coll

done
