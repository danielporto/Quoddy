package org.fogbeam.quoddy

public class LocalAccount 
{
	// we'll just need username/password for now.  And a uuid or whatever
	// hangs it all together.
	
	public LocalAccount(String uuid=null,String username=null,String password=null)
	{
		if(uuid!=null) this.uuid=uuid else	this.uuid = java.util.UUID.randomUUID().toString();
		if(username!=null) this.username=username
		if(password!=null) this.password=password
	}
	
	String uuid;
	String username;
	String password;
}
