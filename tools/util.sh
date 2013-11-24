#!/bin/sh

get_ips() {
	#euca-describe-instances | grep ami-00000200 | awk '{print $4}'
	cat ips > /tmp/ips
	echo "/tmp/ips"
}