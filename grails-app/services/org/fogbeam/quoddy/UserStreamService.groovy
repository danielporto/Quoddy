package org.fogbeam.quoddy

import java.util.Date;

class UserStreamService
{
	public List<UserStream> getSystemDefinedStreamsForUser( final User user )
	{
		List<UserStream> streams = new ArrayList<UserStream>();
		
		def conn = DirectConnectionManagerService.getConnection();
		String sql = "select id ,version, date_created, defined_by,name, owner_id,\
        uuid from user_stream where owner_id="+user.id+" and defined_by='"+UserStream.DEFINED_SYSTEM+"'";
	
		conn.eachRow(sql){row ->
		  streams.add(new UserStream(name:row.name,	uuid:row.uuid,definedBy:UserStream.DEFINED_SYSTEM,owner:user,dateCreated:row.date_created));
		}
		
		// select activity from Activity as activity where
		//List<UserStream> tempStreams = UserStream.executeQuery( "select stream from UserStream as stream where stream.owner = :owner and stream.definedBy = :definedBy", 
			//													['owner':user,'definedBy':UserStream.DEFINED_SYSTEM] );
//		if( tempStreams )
//		{
//			streams.addAll( tempStreams );
//		}

			
		return streams;
	}

	
	public List<UserStream> getSystemDefinedStreamsForUser( final User user, final int maxCount )
	{
		List<UserStream> streams = new ArrayList<UserStream>();
		
		List<UserStream> tempStreams = UserStream.executeQuery( "select stream from UserStream as stream where stream.owner = :owner and stream.definedBy = :definedBy", 
																['owner':user,'definedBy':UserStream.DEFINED_SYSTEM], ['max':maxCount] );
		if( tempStreams )
		{
			streams.addAll( tempStreams ); 
		}
		
		return streams;
	}

		
	public List<UserStream> getUserDefinedStreamsForUser( final User user )
	{
		List<UserStream> streams = new ArrayList<UserStream>();
		
		def conn = DirectConnectionManagerService.getConnection();
		
		String sql = "select id, version,date_created,defined_by,name,owner_id,uuid from \
		user_stream where owner_id="+user.id+" and defined_by='"+UserStream.DEFINED_USER+"'";
		
		
		conn.eachRow(sql){row ->
			streams.add(new UserStream(name:row.name, uuid:row.uuid,definedBy:UserStream.DEFINED_USER,owner:user,dateCreated:row.date_created));
		}
		
//		List<UserStream> tempStreams = UserStream.executeQuery( "select stream from UserStream as stream where stream.owner = :owner and stream.definedBy = :definedBy", 
//																['owner':user,'definedBy':UserStream.DEFINED_USER] );
//		if( tempStreams )
//		{
//			streams.addAll( tempStreams );
//		}

		
		return streams;
	}
	
	public List<UserStream> getUserDefinedStreamsForUser( final User user, final int maxCount )
	{
		List<UserStream> streams = new ArrayList<UserStream>();
		
		List<UserStream> tempStreams = UserStream.executeQuery( "select stream from UserStream as stream where stream.owner = :owner and stream.definedBy = :definedBy", 
																['owner':user,'definedBy':UserStream.DEFINED_USER], ['max':maxCount] );
		if( tempStreams )
		{
			streams.addAll( tempStreams );
		}

		
		return streams;
	}
		
}
