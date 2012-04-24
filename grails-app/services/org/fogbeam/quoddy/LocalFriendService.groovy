package org.fogbeam.quoddy

import java.sql.*
import java.util.Set;
import java.util.Date;
import java.util.List;
import org.fogbeam.quoddy.DirectConnectionManagerService;

class LocalFriendService 
{
	def directConnectionManagerService; 
	
	//added
	public static FriendCollection findFriendCollectionByOwnerUuid( final String uuid, Connection conn )
	{
		//String sql = "select id, version, date_created,	owner_uuid	from friend_collection 	where	owner_uuid='"+uuid+"'";
		FriendCollection friendsCollection = null;
		String sql = "select id, date_created,	owner_uuid	from friend_collection 	where	owner_uuid='"+uuid+"'";
		PreparedStatement stmt = conn.prepareStatement(sql);
		ResultSet rs = null;
		try{
			rs = stmt.executeQuery();
			if(rs.next()){
				println "we find a friend collection with " + uuid;
				friendsCollection = new FriendCollection(dateCreated:rs.getDate("date_created"),ownerUuid:rs.getString("owner_uuid"));
				friendsCollection.id=rs.getInt("id");
			}else{
				println "sorry we didn't find a friend collection with userId " + uuid;
				return friendsCollection;
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		stmt.close();
		rs.close();
		

		friendsCollection.friends= new HashSet<String>();
		sql="select friend_collection_id , friends_string from friend_collection_friends where friend_collection_id="+friendsCollection.id;
		stmt = conn.prepareStatement(sql);
		try{	
			rs = stmt.executeQuery();
			while(rs.next()){
				friendsCollection.friends.add(rs.getString("friends_string"));
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		stmt.close();
		rs.close();
		return 	friendsCollection;
	}

	//added
	public static FriendRequestCollection findFriendRequestCollectionByOwnerUuid( final String uuid, Connection conn )
	{
		//String sql = "select id, version,  date_created, owner_uuid from	friend_request_collection where	owner_uuid='"+uuid+"'";
		String sql = "select id,  date_created, owner_uuid from	friend_request_collection where	owner_uuid='"+uuid+"'";

		FriendRequestCollection friendsRequestCollection = null;
		PreparedStatement stmt = conn.prepareStatement(sql);
		ResultSet rs = null;
		
		try{
			rs = stmt.executeQuery();
			if(rs.next()){
				friendsRequestCollection = new FriendRequestCollection(dateCreated:rs.getDate("date_created"),ownerUuid:rs.getString("owner_uuid"));
				friendsRequestCollection.id=rs.getInt("id");
				friendsRequestCollection.friendRequests= new HashSet<String>();
				sql="select friend_request_collection_id , friend_requests_string from friend_request_collection_friend_requests where friend_request_collection_id="+friendsRequestCollection.id;
				PreparedStatement stmt1 = conn.prepareStatement(sql);
				ResultSet rs1 = stmt1.executeQuery();
				while(rs1.next()){
					friendsRequestCollection.friendRequests.add(rs1.getString("friend_requests_string")); 
				}
				stmt1.close();
				rs1.close();
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		stmt.close();
		rs.close();

		return 	friendsRequestCollection;
	}
	
	public void addToFollow( final User destinationUser, final User targetUser )
	{
		//IFollowCollection iFollowCollection = IFollowCollection.findByOwnerUuid( destinationUser.uuid );
		IFollowCollection iFollowCollection = null;
		Connection conn = DirectConnectionManagerService.getConnection();
		//first get ifollow_collection_id
		
		//String sql1= " select id,version,date_created,owner_uuid from ifollow_collection where owner_uuid='"+destinationUser.uuid+"'";
		String sql1= " select id,date_created,owner_uuid from ifollow_collection where owner_uuid='"+destinationUser.uuid+"'";
		
		PreparedStatement stmt = conn.prepareStatement(sql1);
		ResultSet rs = null;
		try{
			rs = stmt.executeQuery();
			if(rs.next()){
				String sql2 = " select ifollow_collection_id, i_follow_string from" +
				" ifollow_collection_i_follow where ifollow_collection_id="+rs.getInt("id");
			iFollowCollection = new IFollowCollection(ownerUuid:rs.getString("owner_uuid"),dateCreated:rs.getDate("date_created"));
			iFollowCollection.id=rs.getInt("id");
			iFollowCollection.iFollow = new HashSet<String>();
			PreparedStatement stmt1 = conn.prepareStatement(sql2);
			ResultSet rs1 = null;
			rs1 = stmt1.executeQuery();
			while(rs1.next()){
				iFollowCollection.iFollow.add(rs1.getString("i_follow_string"));
			}
			stmt1.close();
			rs1.close();
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		stmt.close();
		rs.close();
		
		if( iFollowCollection == null )
		{
			throw new RuntimeException( "can't get iFollowCollection for user: ${destinationUser.userId}" );	
		}

		//iFollowCollection.addToIFollow( targetUser.uuid );
		//iFollowCollection.save();		
		
		//update iFollowCollection
		
		//sql1= "update ifollow_collection set version="+(row1.version+1)+",date_created='"+row1.date_created+"',owner_uuid='"+destinationUser.uuid+"' where id="+row1.id+" and version="+row1.version;
		sql1= "update ifollow_collection set date_created='"+iFollowCollection.dateCreated+"',owner_uuid='"+destinationUser.uuid+"' where id="+iFollowCollection.id;
		stmt = conn.prepareStatement(sql1);
		try{
			stmt.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
		stmt.close();
		sql1= "insert into ifollow_collection_i_follow (ifollow_collection_id, i_follow_string) values ("+iFollowCollection.id+", '"+targetUser.uuid+"')";
		stmt = conn.prepareStatement(sql1);
		
		try{
			stmt.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
		stmt.close();
		conn.commit();
		DirectConnectionManagerService.returnConnection(conn);
	}

	/* note: this is a "two way" operation, so to speak.  That is, the initial
	 * request was half of the overall operation of adding a friend... now that
	 * the requestee has confirmed, we have to update *both* users to show the
	 * new confirmed friend connection.  We also have to remove the "pending" request.
	 */
	public void confirmFriend( final User currentUser, final User newFriend )
	{
		
		// currentUser is the one confirming a request, newFriend is the one
		// who requested it originally.  So, remove the "pending" request from
		// currentUser, and then insert an entry for newUser into currentUser's
		// "confirmed friends" group and an entry for currentUser into newUser's
		// "confirmed friends" group.
		
//		FriendCollection friendCollectionCU = FriendCollection.findByOwnerUuid( currentUser.uuid );
//		FriendCollection friendCollectionNF = FriendCollection.findByOwnerUuid( newFriend.uuid );
//      FriendRequestCollection friendRequestsCU = FriendRequestCollection.findByOwnerUuid( currentUser.uuid );		
		
		Connection conn = DirectConnectionManagerService.getConnection();
		
		FriendCollection friendCollectionCU = LocalFriendService.findFriendCollectionByOwnerUuid( currentUser.uuid, conn );
		FriendCollection friendCollectionNF = LocalFriendService.findFriendCollectionByOwnerUuid( newFriend.uuid, conn );
		FriendRequestCollection friendRequestsCU = LocalFriendService.findFriendRequestCollectionByOwnerUuid( currentUser.uuid, conn );

			
		//friendRequestsCU.removeFromFriendRequests( newFriend.uuid );
		
		//remove all requests and insert the users
		java.sql.Date now = new java.sql.Date(System.currentTimeMillis());

		//update control tables
//		String sql = "update friend_collection \
//					set version="+(friendCollectionCU.version_+1) +", 	date_created='"+now+"', 	owner_uuid='"+friendCollectionCU.ownerUuid+"' \
//					where	id="+friendCollectionCU.id_+" and version="+friendCollectionCU.version_;
		String sql = "update friend_collection \
		set date_created='"+now+"', 	owner_uuid='"+friendCollectionCU.ownerUuid+"' \
		where	id="+friendCollectionCU.id;
		PreparedStatement stmt = conn.prepareStatement(sql);
		try{
			stmt.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
		stmt.close();
		
//		sql = "update friend_collection \
//					set version="+(friendCollectionNF.version_+1) +", 	date_created='"+now+"', 	owner_uuid='"+friendCollectionNF.ownerUuid+"' \
//					where	id="+friendCollectionNF.id_+" and version="+friendCollectionNF.version_;
		sql = "update friend_collection \
		set date_created='"+now+"', 	owner_uuid='"+friendCollectionNF.ownerUuid+"' \
		where	id="+friendCollectionNF.id;
		stmt = conn.prepareStatement(sql);
		try{
			stmt.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
		stmt.close();

//		sql = "update	friend_request_collection	\
//		set	version="+(friendRequestsCU.version_+1)+",	date_created='"+now+"',	owner_uuid='"+friendRequestsCU.ownerUuid+"' \
//		where id="+friendRequestsCU.id_+" and version="+friendRequestsCU.version_;
		sql = "update	friend_request_collection	\
		set	date_created='"+now+"',	owner_uuid='"+friendRequestsCU.ownerUuid+"' \
		where id="+friendRequestsCU.id;
		stmt = conn.prepareStatement(sql);
		try{
			stmt.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
		stmt.close();

		//insert bidirectional links
		sql = "insert	into friend_collection_friends (friend_collection_id, friends_string) values ("+friendCollectionCU.id+",'"+friendCollectionNF.ownerUuid+"' )"
		stmt = conn.prepareStatement(sql);
		try{
			stmt.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
		stmt.close();
		
		sql = "insert	into friend_collection_friends (friend_collection_id, friends_string) values ("+friendCollectionNF.id+",'"+friendCollectionCU.ownerUuid+"' )"
		stmt = conn.prepareStatement(sql);
		try{
			stmt.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
		stmt.close();

		//remove pending request
		sql ="delete from friend_request_collection_friend_requests \
			 where friend_request_collection_id="+friendRequestsCU.id+"	and friend_requests_string='"+newFriend.uuid+"'";
		stmt = conn.prepareStatement(sql);
		try{
			stmt.executeUpdate();
		}catch(SQLException e){
			e.printStackTrace();
		}
		stmt.close();
	
		conn.commit();
		DirectConnectionManagerService.returnConnection(conn);
//		friendCollectionCU.addToFriends( newFriend.uuid );
//		friendCollectionNF.addToFriends( currentUser.uuid );
//		friendRequestsCU.save();
//		friendCollectionCU.save();
//		friendCollectionNF.save();
		
	}
	
	public void addToFriends( final User currentUser, final User newFriend )
	{
		println "UserService.addTofriends: ${currentUser.userId} / ${newFriend.userId}";
		
		//FriendRequestCollection friendRequests = FriendRequestCollection.findByOwnerUuid( newFriend.uuid );
		FriendRequestCollection friendRequests = null;
		Connection conn = DirectConnectionManagerService.getConnection();
		//String sql = "select id,version,date_created,owner_uuid from friend_request_collection where owner_uuid='"+newFriend.uuid+"'";
		String sql = "select id,date_created,owner_uuid from friend_request_collection where owner_uuid='"+newFriend.uuid+"'";
		PreparedStatement stmt = conn.prepareStatement(sql);
		ResultSet rs = null;
		try{
			rs = stmt.executeQuery();
			if(rs.next()){
				//sql = "update friend_request_collection set version="+(row.version+1)+",date_created='"+row.date_created+"',owner_uuid='"+newFriend.uuid+"' where id="+row.id+" and version="+row.version;
				sql = "update friend_request_collection set date_created='"+rs.getString("date_created")+"',owner_uuid='"+newFriend.uuid+"' where id="+rs.getInt("id");
				PreparedStatement stmt1 = conn.prepareStatement(sql);
				stmt1.executeUpdate();
				stmt1.close();
				
				sql = "insert into friend_request_collection_friend_requests (friend_request_collection_id, friend_requests_string) values ("+rs.getInt("id")+", '"+currentUser.uuid+"')";
				stmt1 = conn.prepareStatement(sql);
				stmt1.executeUpdate();
				stmt1.close();
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		stmt.close();
		rs.close();
		conn.commit();
		DirectConnectionManagerService.returnConnection(conn);
		
//		if( friendRequests == null )
//		{
//			throw new RuntimeException( "can't get friendRequests for user: ${destinationUser.userId}" );
//		}
//	
//		friendRequests.addToFriendRequests( currentUser.uuid );
//		friendRequests.save();
	}

	
	public List<User> listFriends( final User user, Connection conn )
	{
		List<User> friends = new ArrayList<User>();
		FriendCollection friendsCollection = this.findFriendCollectionByOwnerUuid( user.uuid, conn );

		Set<String> friendUuids = friendsCollection.friends;
		for( String friendUuid : friendUuids )
		{
			//User friend = User.findByUuid( friendUuid );
			def userService = new UserService();
			User friend = userService.findUserByUuid(friendUuid, conn);
			friends.add( friend );
		}
		println "returning friends: ${friends}";
		return friends;
	}
	
	public List<User> listFollowers( final User user )
	{
		/* list the users who follow the supplied user */
		List<User> followers = new ArrayList<User>();
		
		// select ownerUuid from IFollowCollection where iFollow contains user.uuid 
		// from Item item join item.labels lbls where 'hello' in (lbls)
		Connection conn = DirectConnectionManagerService.getConnection();
		
//		String sql = "select id,version,date_created,owner_uuid from ifollow_collection inner join ifollow_collection_i_follow ifollow1_ on id=ifollow1_.ifollow_collection_id"+ 
//				" where '"+user.uuid+"' in (ifollow1_.i_follow_string)";
		String sql = "select id,date_created,owner_uuid from ifollow_collection inner join ifollow_collection_i_follow ifollow1_ on id=ifollow1_.ifollow_collection_id"+
		" where '"+user.uuid+"' in (ifollow1_.i_follow_string)";
			
		List<IFollowCollection> iFollowCollections = new ArrayList<IFollowCollection>();
		
		PreparedStatement stmt = conn.prepareStatement(sql);
		ResultSet rs = null;
		try{
			rs = stmt.executeQuery();
			while(rs.next()){
				IFollowCollection ifc = new IFollowCollection(ownerUuid:rs.getString("owner_uuid"),dateCreated:rs.getDate("date_created"));
				ifc.id=rs.getInt("id");
				iFollowCollections.add(ifc);
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		
//		List<IFollowCollection> iFollowCollections = 
//			IFollowCollection.executeQuery( 
//				"select collection from IFollowCollection as collection join collection.iFollow iFollow where ? in (iFollow)", [user.uuid] );
//		
		for( IFollowCollection collection: iFollowCollections )
		{
			//User follower = User.findByUuid( collection.ownerUuid );
			def userService = new UserService();
			User follower = userService.findUserByUuid(collection.ownerUuid, conn);
			followers.add( follower ); 	
		}
		conn.commit();
		DirectConnectionManagerService.returnConnection(conn);
		return followers;
	}
	

	public List<User> listIFollow( final User user )
	{
		List<User> peopleIFollow = new ArrayList<User>();
//		directConnectionManagerService = new DirectConnectionManagerService();
//		def conn = directConnectionManagerService.getConnection();
		
		Connection conn = DirectConnectionManagerService.getConnection();
		//first get ifollow_collection_id
		
		//String sql1= " select id,version,date_created,owner_uuid from ifollow_collection where owner_uuid='"+user.uuid+"'";
		String sql1= " select id,date_created,owner_uuid from ifollow_collection where owner_uuid='"+user.uuid+"'";
		PreparedStatement stmt = conn.prepareStatement(sql1);
		IFollowCollection iFollowCollection = null;
		ResultSet rs = null;
		try{
			rs = stmt.executeQuery();
			if(rs.next()){
				iFollowCollection = new IFollowCollection(ownerUuid:rs.getString("owner_uuid"),dateCreated:rs.getDate("date_created"));
				iFollowCollection.id=rs.getInt("id");
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		stmt.close();
		rs.close();
		
		iFollowCollection.iFollow = new HashSet<String>();
		
		String sql2 = " select ifollow_collection_id, i_follow_string from" +
		" ifollow_collection_i_follow where ifollow_collection_id="+iFollowCollection.id;
		
		stmt = conn.prepareStatement(sql2);
		try{
			rs = stmt.executeQuery();
			while(rs.next()){
				iFollowCollection.iFollow.add(rs.getString("i_follow_string"));
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		stmt.close();
		rs.close();
		//String sql = "select ifollow_collection_id, i_follow_string from ifollow_collection_i_follow where ifollow_collection_id="+row.id;
		
		//IFollowCollection iFollowCollection = IFollowCollection.findByOwnerUuid( user.uuid );
		
		Set<String> iFollowUuids = iFollowCollection.iFollow;
		
		for( String iFollowUuid : iFollowUuids )
		{
			def userService = new UserService();
			User iFollow = userService.findUserByUuid( iFollowUuid, conn );
			//User iFollow = User.findByUuid( iFollowUuid );
			peopleIFollow.add( iFollow );
		}
		
		conn.commit();
		DirectConnectionManagerService.returnConnection(conn);
				
		return peopleIFollow;
	}
	
	public List<FriendRequest> listOpenFriendRequests( final User user )
	{
		List<FriendRequest> openFriendRequests = new ArrayList<FriendRequest>();
		
		Connection conn = DirectConnectionManagerService.getConnection();
		//FriendRequestCollection friendRequestCollection = FriendRequestCollection.findByOwnerUuid( user.uuid );
		FriendRequestCollection friendRequestCollection = this.findFriendRequestCollectionByOwnerUuid( user.uuid, conn);
		
		Set<String> unconfirmedFriendUuids = friendRequestCollection.friendRequests;
		def userService = new UserService()
		for( String unconfirmedFriendUuid : unconfirmedFriendUuids )
		{
			
			//User unconfirmedFriend = User.findByUuid( unconfirmedFriendUuid );
			User unconfirmedFriend =userService.findUserByUuid( unconfirmedFriendUuid, conn );
			FriendRequest friendRequest = new FriendRequest( user, unconfirmedFriend );
			openFriendRequests.add( friendRequest );
		}
		conn.commit();
		DirectConnectionManagerService.returnConnection(conn);
		return openFriendRequests;		
	}

	
	
}
