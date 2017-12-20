i=1;
chemin=`pwd`;
while read line; 
do
	xterm -hold -e ssh $line "cd $chemin; echo `pwd`; java ordo.DaemonImpl $i" &
	echo "Daemon $i lancé";
	let i=$i+1;
done < ../config/daemons.txt
