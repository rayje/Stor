#!/bin/sh

scripts="start-all util.sh list-storage ub"

if [ ! -d $HOME/bin ]; then
	mkdir $HOME/bin
fi

for script in $scripts; do 
	wget "https://raw.github.com/rayje/Stor/master/tools/$script" -P $HOME/bin/
	chmod 755 $HOME/bin/$script
done