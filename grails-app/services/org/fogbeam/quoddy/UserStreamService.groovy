package org.fogbeam.quoddy

import java.util.Date;
import java.sql.*;

import txstore.scratchpad.rdbms.jdbc.TxMudConnection;
import txstore.scratchpad.rdbms.util.quoddy.*;

class UserStreamService
{
	public List<UserStream> getSystemDefinedStreamsForUser( final User user, TxMudConnection conn = null )
	{
		List<UserStream> streams = new ArrayList<UserStream>();
		boolean needToCommit = false;
		if(conn == null){
			conn = DirectConnectionManagerService.getConnection();
			needToCommit = true;
		}
		String sql = "select id ,version, date_created, defined_by,name, owner_id,\
        uuid from user_stream where owner_id="+user.id+" and defined_by='"+UserStream.DEFINED_SYSTEM+"'";
		PreparedStatement stmt = conn.prepareStatement(sql);
		ResultSet rs = null;
		try{
			rs = stmt.executeQuery();
			while(rs.next()){
				streams.add(new UserStream(name:rs.getString("name"),uuid:rs.getString("uuid"),definedBy:UserStream.DEFINED_SYSTEM,owner:user,dateCreated:rs.getDate("date_created")));
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		stmt.close();
		rs.close();
		if(needToCommit){
			try{
				//System.out.println("Set empty shadow op for getting system defined streams for user");
				DBQUODDYShdEmpty dEm = DBQUODDYShdEmpty.createOperation();
				conn.setShadowOperation(dEm, 0);
			} catch (IOException e) {
				e.printStackTrace();
			}
			conn.commit();
			DirectConnectionManagerService.returnConnection(conn);
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

		
	public List<UserStream> getUserDefinedStreamsForUser( final User user, TxMudConnection conn=null )
	{
		List<UserStream> streams = new ArrayList<UserStream>();
		boolean needToCommit = false;
		if(conn == null){
			conn = DirectConnectionManagerService.getConnection();
			needToCommit = true;
		}
		
		String sql = "select id, version,date_created,defined_by,name,owner_id,uuid from \
		user_stream where owner_id="+user.id+" and defined_by='"+UserStream.DEFINED_USER+"'";
		PreparedStatement stmt = conn.prepareStatement(sql);
		ResultSet rs = null;
		try{
			rs = stmt.executeQuery();
			while(rs.next()){
				streams.add(new UserStream(name:rs.getString("name"), uuid:rs.getString("uuid"),definedBy:UserStream.DEFINED_USER,owner:user,dateCreated:rs.getDate("date_created")));
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		stmt.close();
		rs.close();
		if(needToCommit){
			try{
				//System.out.println("Set empty shadow op for getUserDefined streams for users");
				DBQUODDYShdEmpty dEm = DBQUODDYShdEmpty.createOperation();
				conn.setShadowOperation(dEm, 0);
			} catch (IOException e) {
				e.printStackTrace();
			}
			conn.commit();
			DirectConnectionManagerService.returnConnection(conn);
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
