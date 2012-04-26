#! /bin/bash
#Max Planck Institute for Software Systems 2012
#Author Daniel Porto

THUMBNAILS_DIR=thumbnails
USER_PREFIX=user
NUMBER_OF_USERS=1000

if [ -n $1 ]
then 
	NUMBER_OF_USERS=$1
fi
echo "Generating $NUMBER_OF_USERS images for users profile"
find $THUMBNAILS_DIR | grep -i jpg > $THUMBNAILS_DIR/pictures.txt
#for (( i=0;i<${NUMBER_OF_USERS};i++ )) do a=`shuf -i 1-20 -n 1`;  p=`sed -n ${a}p $THUMBNAILS_DIR/pictures.txt`; mkdir "$USER_PREFIX${i}";cp ${p} ${USER_PREFIX}${i}/$USER_PREFIX${i}_profile_thumbnail48x48.jpg; done

for (( i=0;i<20;i++ )) do a=`shuf -i 1-20 -n 1`;  p=`sed -n ${a}p $THUMBNAILS_DIR/pictures.txt`; mkdir "testuser${i}";cp ${p} testuser${i}/testuser${i}_profile_thumbnail48x48.jpg; done
