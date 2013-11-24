#!/bin/sh

get_ips() {
	if [ ! -d ".tmp" ]; then
		mkdir -p .tmp
	fi

	euca-describe-instances | grep ami-00000200 | awk '{print $4}' > .tmp/ips
	echo >> .tmp/ips
	echo ".tmp/ips"
}

list_inst() {
	euca-describe-instances | grep ami-00000200
}