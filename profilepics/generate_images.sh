#! /bin/bash
#Max Planck Institute for Software Systems 2012
#Author Daniel Porto

THUMBNAILS_DIR=thumbnails
USER_PREFIX=user

find $THUMBNAILS_DIR | grep -i jpg > $THUMBNAILS_DIR/pictures.txt
TOTAL_IMAGES=`wc -l $THUMBNAILS_DIR/pictures.txt | cut -f 1 -d " "`

IMAGE_ROOT=/var/tmp/Quoddy/profilepics

if [ $1 ];
then 
	NUMBER_OF_USERS=$1
	echo "Generating $NUMBER_OF_USERS images for users profile as you requested"
	for (( i=0; i < ${NUMBER_OF_USERS}; i++ )) do 
		a=`shuf -i 1-${TOTAL_IMAGES} -n 1`;  
		p=`sed -n ${a}p $THUMBNAILS_DIR/pictures.txt`; 
		mkdir -p "${IMAGE_ROOT}/${USER_PREFIX}${i}";
		cp ${p} ${IMAGE_ROOT}/${USER_PREFIX}${i}/$USER_PREFIX${i}_profile_thumbnail48x48.jpg; 
	done
else
	echo "Generating $NUMBER_OF_USERS images for users profile"
	for (( i=0;i<20;i++ )) do 
		a=`shuf -i 1-${TOTAL_IMAGES} -n 1`;  
		p=`sed -n ${a}p $THUMBNAILS_DIR/pictures.txt`; 
		mkdir -p "${IMAGE_ROOT}/testuser${i}";
		cp ${p} ${IMAGE_ROOT}/testuser${i}/testuser${i}_profile_thumbnail48x48.jpg; 
	done
fi