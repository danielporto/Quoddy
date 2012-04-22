package org.fogbeam.quoddy

import java.util.Set;
import java.util.Date;
import java.util.List;
import org.fogbeam.quoddy.DirectConnectionManagerService;

class LocalFriendService 
{
	def directConnectionManagerService; 
	
	//added
	public FriendCollection findFriendCollectionByOwnerUuid( final String uuid )
	{
		def conn = DirectConnectionManagerService.getConnection();
		String sql = "select id, version, date_created,	owner_uuid	from friend_collection 	where	owner_uuid='"+uuid+"'";
		
		def row = conn.firstRow(sql);
		FriendCollection friendsCollection = new FriendCollection(id_:row.id,version_:row.version, dateCreated:row.date_created,ownerUuid:row.owner_uuid);

		friendsCollection.friends= new HashSet<String>();
		sql="select friend_collection_id , friends_string from friend_collection_friends where friend_collection_id="+friendsCollection.id_;
		conn.eachRow(sql){row2 ->  friendsCollection.friends.add(row2.friends_string); }
		return 	friendsCollection;
	}

	//added
	public FriendRequestCollection findFriendRequestCollectionByOwnerUuid( final String uuid )
	{
		def conn = DirectConnectionManagerService.getConnection();
		String sql = "select id, version,  date_created, owner_uuid from	friend_request_collection where	owner_uuid='"+uuid+"'";

		def row = conn.firstRow(sql)
		println "--------------------------------------------------------------------"
		FriendRequestCollection friendsRequestCollection = new FriendRequestCollection(id_:row.id,version_:row.version, dateCreated:row.date_created,ownerUuid:row.owner_uuid)

		friendsRequestCollection.friendRequests= new HashSet<String>();
		sql="select friend_request_collection_id , friend_requests_string from friend_request_collection_friend_requests where friend_request_collection_id="+row.id;
		conn.eachRow(sql){row2 ->  friendsRequestCollection.friendRequests.add(row2.friend_requests_string); }
		return 	friendsRequestCollection;
	}
	
	public void addToFollow( final User destinationUser, final User targetUser )
	{
		//IFollowCollection iFollowCollection = IFollowCollection.findByOwnerUuid( destinationUser.uuid );
		IFollowCollection iFollowCollection = null;
		def conn = DirectConnectionManagerService.getConnection();
		//first get ifollow_collection_id
		
		String sql1= " select id,version,date_created,owner_uuid from ifollow_collection where owner_uuid='"+destinationUser.uuid+"'";
		def row1 = conn.firstRow(sql1);
		if(row1.size()==0){
			println("no user found");
		}else{
		
			String sql2 = " select ifollow_collection_id, i_follow_string from" +
						" ifollow_collection_i_follow where ifollow_collection_id="+row1.id;
			iFollowCollection = new IFollowCollection(ownerUuid:row1.owner_uuid,dateCreated:row1.date_created);
			iFollowCollection.iFollow = new HashSet<String>();
			
			conn.eachRow(sql2){row ->
				iFollowCollection.iFollow.add(row.i_follow_string);
			}
		}
		if( iFollowCollection == null )
		{
			throw new RuntimeException( "can't get iFollowCollection for user: ${destinationUser.userId}" );	
		}

		//iFollowCollection.addToIFollow( targetUser.uuid );
		//iFollowCollection.save();		
		
		//update iFollowCollection
		
		sql1= "update ifollow_collection set version="+(row1.version+1)+",date_created='"+row1.date_created+"',owner_uuid='"+destinationUser.uuid+"' where id="+row1.id+" and version="+row1.version;
		conn.execute(sql1);
		
		sql1= "insert into ifollow_collection_i_follow (ifollow_collection_id, i_follow_string) values ("+row1.id+", '"+targetUser.uuid+"')";
		conn.execute(sql1)
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
		
		FriendCollection friendCollectionCU = this.findFriendCollectionByOwnerUuid( currentUser.uuid );
		FriendCollection friendCollectionNF = this.findFriendCollectionByOwnerUuid( newFriend.uuid );
		FriendRequestCollection friendRequestsCU = this.findFriendRequestCollectionByOwnerUuid( currentUser.uuid );

			
		//friendRequestsCU.removeFromFriendRequests( newFriend.uuid );
		
		//remove all requests and insert the users
		def conn = DirectConnectionManagerService.getConnection();
		java.sql.Date now = new java.sql.Date(System.currentTimeMillis());

		//update control tables
		String sql = "update friend_collection \
					set version="+(friendCollectionCU.version_+1) +", 	date_created='"+now+"', 	owner_uuid='"+friendCollectionCU.ownerUuid+"' \
					where	id="+friendCollectionCU.id_+" and version="+friendCollectionCU.version_;
		conn.execute(sql);
		
		sql = "update friend_collection \
					set version="+(friendCollectionNF.version_+1) +", 	date_created='"+now+"', 	owner_uuid='"+friendCollectionNF.ownerUuid+"' \
					where	id="+friendCollectionNF.id_+" and version="+friendCollectionNF.version_;
		conn.execute(sql);

		sql = "update	friend_request_collection	\
		set	version="+(friendRequestsCU.version_+1)+",	date_created='"+now+"',	owner_uuid='"+friendRequestsCU.ownerUuid+"' \
		where id="+friendRequestsCU.id_+" and version="+friendRequestsCU.version_;
		conn.execute(sql);

		//insert bidirectional links
		sql = "insert	into friend_collection_friends (friend_collection_id, friends_string) values ("+friendCollectionCU.id_+",'"+friendCollectionNF.ownerUuid+"' )"
		conn.execute(sql);
		
		sql = "insert	into friend_collection_friends (friend_collection_id, friends_string) values ("+friendCollectionNF.id_+",'"+friendCollectionCU.ownerUuid+"' )"
		conn.execute(sql);

		//remove pending request
		sql ="delete from friend_request_collection_friend_requests \
			 where friend_request_collection_id="+friendRequestsCU.id_+"	and friend_requests_string='"+newFriend.uuid+"'";
		conn.execute(sql);
	
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
		def conn = DirectConnectionManagerService.getConnection();
		String sql = "select id,version,date_created,owner_uuid from friend_request_collection where owner_uuid='"+newFriend.uuid+"'";
		
		def row = conn.firstRow(sql)
		
		sql = "update friend_request_collection set version="+(row.version+1)+",date_created='"+row.date_created+"',owner_uuid='"+newFriend.uuid+"' where id="+row.id+" and version="+row.version;
		conn.execute(sql);
		
		sql = "insert into friend_request_collection_friend_requests (friend_request_collection_id, friend_requests_string) values ("+row.id+", '"+currentUser.uuid+"')";
		conn.execute(sql);
//		if( friendRequests == null )
//		{
//			throw new RuntimeException( "can't get friendRequests for user: ${destinationUser.userId}" );
//		}
//	
//		friendRequests.addToFriendRequests( currentUser.uuid );
//		friendRequests.save();
	}

	
	public List<User> listFriends( final User user )
	{
		List<User> friends = new ArrayList<User>();
		FriendCollection friendsCollection = this.findFriendCollectionByOwnerUuid( user.uuid );

		Set<String> friendUuids = friendsCollection.friends;
		for( String friendUuid : friendUuids )
		{
			//User friend = User.findByUuid( friendUuid );
			def userService = new UserService();
			User friend = userService.findUserByUuid(friendUuid);
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
		def conn = DirectConnectionManagerService.getConnection();
		
		String sql = "select id,version,date_created,owner_uuid from ifollow_collection inner join ifollow_collection_i_follow ifollow1_ on id=ifollow1_.ifollow_collection_id"+ 
				" where '"+user.uuid+"' in (ifollow1_.i_follow_string)";
			
	
		List<IFollowCollection> iFollowCollections = new ArrayList<IFollowCollection>();
		conn.eachRow(sql){row ->
			iFollowCollections.add(new IFollowCollection(ownerUuid:row.owner_uuid,dateCreated:row.date_created));
		}
	
//		List<IFollowCollection> iFollowCollections = 
//			IFollowCollection.executeQuery( 
//				"select collection from IFollowCollection as collection join collection.iFollow iFollow where ? in (iFollow)", [user.uuid] );
//		
		for( IFollowCollection collection: iFollowCollections )
		{
			//User follower = User.findByUuid( collection.ownerUuid );
			def userService = new UserService();
			User follower = userService.findUserByUuid(collection.ownerUuid);
			followers.add( follower ); 	
		}
		
		return followers;
	}
	

	public List<User> listIFollow( final User user )
	{
		List<User> peopleIFollow = new ArrayList<User>();
//		directConnectionManagerService = new DirectConnectionManagerService();
//		def conn = directConnectionManagerService.getConnection();
		
		def conn = DirectConnectionManagerService.getConnection();
		//first get ifollow_collection_id
		
		String sql1= " select id,version,date_created,owner_uuid from ifollow_collection where owner_uuid='"+user.uuid+"'";
		def row1 = conn.firstRow(sql1);
		if(row1.size()==0){
			println("no user found");
			return peopleIFollow;
		}
		
		String sql2 = " select ifollow_collection_id, i_follow_string from" +
        			" ifollow_collection_i_follow where ifollow_collection_id="+row1.id;
				
		IFollowCollection iFollowCollection = null;
		iFollowCollection = new IFollowCollection(ownerUuid:row1.owner_uuid,dateCreated:row1.date_created);
		iFollowCollection.iFollow = new HashSet<String>();
		
		conn.eachRow(sql2){row ->
			iFollowCollection.iFollow.add(row.i_follow_string);
		}
		//String sql = "select ifollow_collection_id, i_follow_string from ifollow_collection_i_follow where ifollow_collection_id="+row.id;
		
		//IFollowCollection iFollowCollection = IFollowCollection.findByOwnerUuid( user.uuid );
		
		Set<String> iFollowUuids = iFollowCollection.iFollow;
		
		for( String iFollowUuid : iFollowUuids )
		{
			def userService = new UserService();
			User iFollow = userService.findUserByUuid( iFollowUuid );
			//User iFollow = User.findByUuid( iFollowUuid );
			peopleIFollow.add( iFollow );
		}
				
		return peopleIFollow;
	}
	
	public List<FriendRequest> listOpenFriendRequests( final User user )
	{
		List<FriendRequest> openFriendRequests = new ArrayList<FriendRequest>();

		//FriendRequestCollection friendRequestCollection = FriendRequestCollection.findByOwnerUuid( user.uuid );
		FriendRequestCollection friendRequestCollection = this.findFriendRequestCollectionByOwnerUuid( user.uuid );
		
		Set<String> unconfirmedFriendUuids = friendRequestCollection.friendRequests;
		def userService = new UserService()
		for( String unconfirmedFriendUuid : unconfirmedFriendUuids )
		{
			
			//User unconfirmedFriend = User.findByUuid( unconfirmedFriendUuid );
			User unconfirmedFriend =userService.findUserByUuid( unconfirmedFriendUuid );
			FriendRequest friendRequest = new FriendRequest( user, unconfirmedFriend );
			openFriendRequests.add( friendRequest );
		}
		
		return openFriendRequests;		
	}

	
	
}
