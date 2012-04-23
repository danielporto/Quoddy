package org.fogbeam.quoddy

import java.io.Serializable;

class StatusUpdate implements Serializable
{

	String text;
	User creator;
	Date dateCreated;
	
	static belongsTo = [User];
	static mapping = { version false;  id generator: 'assigned'	 }
}
