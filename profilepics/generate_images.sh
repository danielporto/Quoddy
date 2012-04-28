#! /bin/bash
#Max Planck Institute for Software Systems 2012
#Author Daniel Porto

THUMBNAILS_DIR=thumbnails
USER_PREFIX=user

find $THUMBNAILS_DIR | grep -i jpg > $THUMBNAILS_DIR/pictures.txt
TOTAL_IMAGES=`wc -l $THUMBNAILS_DIR/pictures.txt | cut -f 1 -d " "`

IMAGE_ROOT=/var/tmp/Quoddy/profilepics


#//FUNCTIONS

help(){
	echo "this is the help message"
}

generate_images(){
	NUMBER_OF_USERS=$1
	echo "Generating $NUMBER_OF_USERS images for users profile as you requested"
	for (( i=0; i < ${NUMBER_OF_USERS}; i++ )) do 
		a=`shuf -i 1-${TOTAL_IMAGES} -n 1`;  
		p=`sed -n ${a}p $THUMBNAILS_DIR/pictures.txt`; 
		#mkdir -p "${IMAGE_ROOT}/${USER_PREFIX}${i}";
		#cp ${p} ${IMAGE_ROOT}/${USER_PREFIX}${i}/$USER_PREFIX${i}_profile_thumbnail48x48.jpg;
		cp ${p} ${IMAGE_ROOT}/$USER_PREFIX${i}_profile_thumbnail48x48.jpg; 
	done

}

generate_links(){
	NUMBER_OF_USERS=$1
	echo "Generating $NUMBER_OF_USERS images for users profile as you requested"
	for (( i=0; i < ${NUMBER_OF_USERS}; i++ )) do 
		a=`shuf -i 1-${TOTAL_IMAGES} -n 1`;  
		p=`sed -n ${a}p $THUMBNAILS_DIR/pictures.txt`; 
		#mkdir -p "${IMAGE_ROOT}/${USER_PREFIX}${i}";
		#ln -s `pwd`/${p} ${IMAGE_ROOT}/${USER_PREFIX}${i}/$USER_PREFIX${i}_profile_thumbnail48x48.jpg; 
		ln -s `pwd`/${p} ${IMAGE_ROOT}/$USER_PREFIX${i}_profile_thumbnail48x48.jpg;
	done
}


#//MAIN

if [ -z $2 ]; 
then 
		echo "You must tell the number of images to create"
		exit 0;
fi
mkdir -p "${IMAGE_ROOT}";
case $1 in 
	"links") 			generate_links $2;;						
	"images") 			generate_images $2;;
	*) 						help;;
esac
echo "Script finished"

