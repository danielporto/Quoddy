package org.fogbeam.quoddy

class ShareTarget
{
	public static final String STREAM_PUBLIC = "STREAM_PUBLIC";
	
	public ShareTarget()
	{
		this.uuid = java.util.UUID.randomUUID().toString();
	}
	
	static constraints =
	{}
	
	String name;
	String uuid;
	//static mapping = { version false;  id generator: 'assigned'	 }
}
