package org.fogbeam.quoddy

class FriendRequestCollection 
{
	//daniel - debug
	Long id_;
	Long version_;

	String ownerUuid;
	Date dateCreated;
	
	Set<String> friendRequests;

	static hasMany = [friendRequests:String]
	static transients = [ "id_", "version_"]
	
}
