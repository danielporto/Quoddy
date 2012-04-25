package org.fogbeam.quoddy;

public class IFollowCollection 
{
	String ownerUuid;
	Date dateCreated;
	
	Set<String> iFollow;
	
	static hasMany = [iFollow:String];
	static mapping = { version false;  id generator: 'assigned'	 }
}
