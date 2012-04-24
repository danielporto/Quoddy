package org.fogbeam.quoddy

class FriendRequestCollection 
{
	String ownerUuid;
	Date dateCreated;
	
	Set<String> friendRequests;

	static hasMany = [friendRequests:String]
	static mapping = { version false;  id generator: 'assigned'	 }
}
