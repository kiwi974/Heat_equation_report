i=1;
k=81;
while read line;
do
    xterm -hold -e ssh $line "cd 2A/Systemes_concurrents/Integration_Hadoop/src ; java hdfs.HdfsServeur 80$k" &
    echo "Serveur $i lancé";
    let i=$i+1;
    let k=$k+1;
done < ../config/daemons.txt
