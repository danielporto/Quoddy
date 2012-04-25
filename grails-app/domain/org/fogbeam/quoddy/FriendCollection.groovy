package org.fogbeam.quoddy

class FriendCollection 
{
	String ownerUuid;
	Date dateCreated;

	Set<String> friends;	

	static hasMany = [friends:String]
	static mapping = { version false;  id generator: 'assigned'	 }
	
}
