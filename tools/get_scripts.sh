#!/bin/sh

scripts="start-all util.sh"

for script in $scripts; do 
	wget "https://raw.github.com/rayje/Stor/master/tools/$script" .
done