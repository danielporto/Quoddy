package org.fogbeam.quoddy

class FriendCollection 
{
	//daniel - debug
	Long id_;
	Long version_;

	String ownerUuid;
	Date dateCreated;

	Set<String> friends;	

	static hasMany = [friends:String]
	static transients = [ "id_", "version_"]
	
}
