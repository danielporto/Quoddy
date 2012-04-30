package org.fogbeam.quoddy

import java.util.Set
import java.sql.*

import txstore.scratchpad.rdbms.jdbc.TxMudConnection;
import txstore.scratchpad.rdbms.util.quoddy.*;

class EventQueueService 
{	
	def userService;
	
	Map<String, Deque<Map>> eventQueues = new HashMap<String, Deque<Map>>();
	
	static expose = ['jms']
	static destination = "uitestActivityQueue"
	
	def onMessage(msg)
	{
		//println "Received message from JMS: ${msg}";
		
		// now, figure out which user(s) are interested in this message, and put it on
		// all the appropriate queues
		Set<Map.Entry<String, Deque<Map>>> entries = eventQueues.entrySet();
		//println "got entrySet from eventQueues object: ${entries}";
		TxMudConnection conn = null;
		if(entries.size()>0){
			conn = DirectConnectionManagerService.getConnection();
		}
		for( Map.Entry<String, Deque<Map>> entry : entries )
		{
			//println "entry: ${entry}";
			//println "key: ${entry.getKey()}";
			
			String key = entry.getKey();
			
			
			// TODO: deal with the case where the post was to a UserGroup, not
			// a direct stream post.  In that case, we only offer it to a user if
			// that user is also a member of the same group?
			// BUT, for now, let's just implement it so that we only offer
			// messages that were to the public stream.  We'll come back to deal with
			// common group membership and other scenarios later.
			
			//def streamPublic = ShareTarget.findByName( ShareTarget.STREAM_PUBLIC);
			String sql="select id, name, uuid from	share_target where name='"+ShareTarget.STREAM_PUBLIC+"'";
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = null;
			ShareTarget streamPublic = null;
			try{
				rs = stmt.executeQuery();
				if(rs.next()){
					streamPublic = new ShareTarget (name:rs.getString("name"),uuid:rs.getString("uuid"));
					streamPublic.id=rs.getInt("id");
				}else{
					println "sorry we didn't find a user with userId " + userId;
				}
			}catch(SQLException e){
				e.printStackTrace();
			}
			stmt.close();
			rs.close();			
			
			if( ! msg.targetUuid.equals( streamPublic.uuid ))
			{
				try{
					//System.out.println("Set empty shadow op for onMessage 1");
					DBQUODDYShdEmpty dEm = DBQUODDYShdEmpty.createOperation();
					conn.setShadowOperation(dEm, 0);
				} catch (IOException e) {
					e.printStackTrace();
				}
				DirectConnectionManagerService.commitAndReturn(conn);
				return;
			}
			
			
			// TODO: don't offer message unless the owner of this queue
			// and the event creator, are friends (or the owner *is* the creator)
			//println "msg creator: ${msg.creator}";
			User msgCreator = userService.findUserByUserId( msg.creator, conn);
			if( msgCreator )
			{
				//println "found User object for ${msgCreator.userId}";
			}
			
			//FriendCollection friendCollection = FriendCollection.findByOwnerUuid( msgCreator.uuid );
			FriendCollection friendCollection = LocalFriendService.findFriendCollectionByOwnerUuid( msgCreator.uuid, conn );
//			if( friendCollection )
//			{
//				println "got a valid friends collection for ${msgCreator.userId}";
//			}
			
			Set<String> friends = friendCollection.friends;
//			if( friends )
//			{
//				println "got valid friends set: ${friends}";
//				for( String friend : friends )
//				{
//					println "friend: ${friend}";
//				}
//			}
			User targetUser = userService.findUserByUserId( key, conn );
			if( friends.contains( targetUser.uuid ) || msgCreator.uuid.equals( targetUser.uuid ) )
			{
				//println "match found, offering message";
				Deque<Map> userQueue = entry.getValue();
				if( msg instanceof Map )
				{
					//println "MapMessage being offered";
					userQueue.offerFirst( msg );
				}
				else
				{
					println "WTF is this? ${msg}";
				}
			}
			
			
			
			
			
		}
		if(conn != null){
			try{
				//System.out.println("Set empty shadow op for onMessage 2");
				DBQUODDYShdEmpty dEm = DBQUODDYShdEmpty.createOperation();
				conn.setShadowOperation(dEm, 0);
			} catch (IOException e) {
				e.printStackTrace();
			}
			DirectConnectionManagerService.commitAndReturn(conn);
		}
		//println "done processing eventQueue instances";
	}
	
	public long getQueueSizeForUser( final String userId )
	{
		long queueSize = 0;
		Deque<Map> userQueue = eventQueues.get( userId ); 
		if( userQueue != null )
		{
			queueSize = userQueue.size();
		}
		
		// println "Queue size for user: ${userId} = ${queueSize}";
		
		return queueSize;	
	}
	
	public List<Map> getMessagesForUser( final String userId, final int msgCount )
	{
		//println "getting messages for user: ${userId}, msgCount: ${msgCount}";
		List<Map> messages = new ArrayList<Map>();
		Deque<Map> userQueue = eventQueues.get( userId );
		if( userQueue != null )
		{
			//println "got userQueue for user ${userId}";
			for( int i = 0; i < msgCount; i++ )
			{
				// get message from queue, put it in return set	
				Map msg = userQueue.pollFirst();
				messages.add( msg ); 
			}
		}
		
		return messages;
	}
	
	
	public void registerEventQueueForUser( final String userId )
	{
		//println "registering eventqueue for user: ${userId}";
		
		if( !eventQueues.containsKey( userId ))
		{
			Deque<String> userQueue = new ArrayDeque<String>();
			eventQueues.put( userId, userQueue ); 
		}
		else
		{
			println "We already have an event queue for this user: ${userId}";
		}
	}

	public void unRegisterEventQueueForUser( final String userId )
	{
		// TODO: implement me
		throw new UnsupportedOperationException( "Not implemented yet!" );
	}
}
