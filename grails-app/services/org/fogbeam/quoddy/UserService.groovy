package org.fogbeam.quoddy;

import java.util.Date;
import java.util.List
import java.sql.*

import org.fogbeam.quoddy.profile.Profile
import org.springframework.jdbc.core.RowCallbackHandler;

class UserService {

	// injected by Spring, might be backed by LDAP version OR by local dB version;
	// but we should be able to not care which is configured
	def friendService;
	
	// same deal as the friendService...
	def groupService;
	
	// and again...  when finished, this will either be LdapPersonService or LocalAccountService, but we don't care.
	def accountService;
	
	public User findUserByUserId( String userId, Connection conn=null )
	{
		User user=null;
		/*def conn = DirectConnectionManagerService.getConnection();
//		String sql = "select id, version, current_status_id, date_created, email, first_name, full_name,\
//		last_name,	profile_id,	user_id, uuid from uzer where user_id='"+userId+"'";
		String sql = "select id, current_status_id, date_created, email, first_name, full_name,\
		last_name,	profile_id,	user_id, uuid from uzer where user_id='"+userId+"'";
		def row = conn.firstRow(sql)
		User user = null
		if(row ==null){
			println "no user found"
		}else{
		//User user = User.findByUserId( userId );
			 user = new User(uuid:row.uuid, userId:row.user_id, dateCreated:row.date_created,firstName:row.first_name,lastName:row.last_name,email:row.email)
			 user.id=row.id;
		}*/
		boolean needToCommit = false;
		if(conn == null){
			conn = DirectConnectionManagerService.getConnection();
			needToCommit = true;
		}
		
		String sql = "select id, current_status_id, date_created, email, first_name, full_name,\
		last_name,	profile_id,	user_id, uuid from uzer where user_id='"+userId+"'";
		PreparedStatement stmt = conn.prepareStatement(sql);
		ResultSet rs = null;
		try{
			rs = stmt.executeQuery();
			if(rs.next()){
				//println "we found a user with userId " + userId;
				user = new User(uuid:rs.getString("uuid"), userId:rs.getString("user_id"), dateCreated:rs.getDate("date_created"),firstName:rs.getString("first_name"),lastName:rs.getString("last_name"),email:rs.getString("email"));
				user.id = (long)rs.getInt("id");
				println "user uuid from db: " + rs.getString("uuid");
				println "user uuid is : " + user.uuid;
			}else{
				println "sorry we didn't find a user with userId " + userId;
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		stmt.close();
		rs.close();
		
		if(needToCommit){
			conn.commit();
			DirectConnectionManagerService.returnConnection(conn);
		}
		
		return user;
	}	
	
	/*
	 * create a function to fetch user by integer user id
	 */
	
	public User findUserById( final int id, Connection conn )
	{
		//User user = User.findByUuid( uuid );
//		String sql = "select id ,version,current_status_id, date_created, email, first_name, full_name, last_name, profile_id, user_id, "+
//		             "uuid from uzer where uuid='"+uuid+"'"
		User user = null;
		String sql = "select id, current_status_id, date_created, email, first_name, full_name, last_name, profile_id, user_id, "+
		"uuid from uzer where id='"+id+"'"

		PreparedStatement stmt = conn.prepareStatement(sql);
		ResultSet rs = null;
		try{
			rs = stmt.executeQuery();
			if(rs.next()){
				//println "we find a user with " + uuid;
				user = new User(uuid:rs.getString("uuid"), userId:rs.getString("user_id"), dateCreated:rs.getDate("date_created"),firstName:rs.getString("first_name"),lastName:rs.getString("last_name"),email:rs.getString("email"));
				user.id = (long)rs.getInt("id");
			}else{
				println "sorry we didn't find a user with userId " + uuid;
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		stmt.close();
		rs.close();
		
		return user;
			
	}
	
	
	public User findUserByUuid( final String uuid, Connection conn )
	{
		//User user = User.findByUuid( uuid );
//		String sql = "select id ,version,current_status_id, date_created, email, first_name, full_name, last_name, profile_id, user_id, "+
//		             "uuid from uzer where uuid='"+uuid+"'"
		User user = null;
		String sql = "select id, current_status_id, date_created, email, first_name, full_name, last_name, profile_id, user_id, "+
		"uuid from uzer where uuid='"+uuid+"'"

		PreparedStatement stmt = conn.prepareStatement(sql);
		ResultSet rs = null;
		try{
			rs = stmt.executeQuery();
			if(rs.next()){
				//println "we find a user with " + uuid;
				user = new User(uuid:rs.getString("uuid"), userId:rs.getString("user_id"), dateCreated:rs.getDate("date_created"),firstName:rs.getString("first_name"),lastName:rs.getString("last_name"),email:rs.getString("email"));
				user.id = (long)rs.getInt("id");
			}else{
				println "sorry we didn't find a user with userId " + uuid;
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		stmt.close();
		rs.close();
		
		return user;
			
	}
//	public User findUserByUuid( final String uuid )
//	{
//		def conn = DirectConnectionManagerService.getConnection();
//		String sql = "";
//		def row = conn.firstRow(sql)
//
//		return  new User(uuid:row.uuid, userId:row.user_id, dateCreated:row.date_created,firstName:row.first_name,lastName:row.last_name,email:row.email);
//			
//	}
//	
	public void createUser( User user ) 
	{
		
		/* save the user into the uzer table, we need that for associations with other
		* "system things"
		*/
		if( user.profile == null )
		{
			user.profile = new Profile();	
		}
		
		if( user.save() )
		{
			accountService.createUser( user );
			// ldapPersonService.createUser( user );
			// create system defined Stream entries for this newly created user
			UserStream defaultStream = new UserStream();
			defaultStream.name = "Default";
			defaultStream.definedBy = UserStream.DEFINED_SYSTEM;
			defaultStream.owner = user;
			
			if( !defaultStream.save())
			{
				defaultStream.errors.allErrors.each { println it };
				throw new RuntimeException( "couldn't create Default UserStream record for user: ${user.userId}" );
			}
		}
		else
		{
			user.errors.allErrors.each { println it };
			throw new RuntimeException( "couldn't create User record for user: ${user.userId}" );
			
		}
		
	}

	public void importUser( User user )
	{
		// this is a User with an external authSource, so all we create is the entry in the uzer table
		// we don't create an account here.
		
		if( user.profile == null )
		{
			user.profile = new Profile();
		}
		
		if( !user.save() )
		{
			user.errors.allErrors.each { println it };
			throw new RuntimeException( "couldn't create User record for user: ${user.userId}" );
		}	
	}
		
	public User updateUser( User user )
	{
		throw new RuntimeException( "not implemented yet" );
	}
	
	public void addToFollow( User destinationUser, User targetUser )
	{
		friendService.addToFollow( destinationUser, targetUser );	
	}

	/* note: this is a "two way" operation, so to speak.  That is, the initial
	 * request was half of the overall operation of adding a friend... now that
	 * the requestee has confirmed, we have to update *both* users to show the
	 * new confirmed friend connection.  We also have to remove the "pending" request.
	 */
	public void confirmFriend( User currentUser, User newFriend )
	{
		friendService.confirmFriend( currentUser, newFriend );
	}
	
	public void addToFriends( User currentUser, User newFriend )
	{
		friendService.addToFriends( currentUser, newFriend );
	}
		
	public List<User> findAllUsers() 
	{
		List<User> users = new ArrayList<User>();
		Connection conn = DirectConnectionManagerService.getConnection();
		Random diceRoller = new Random();
		double range = diceRoller.nextDouble();
		String sql = "select id, version,current_status_id,date_created,email,first_name ,full_name ,last_name ,profile_id,user_id ,uuid  from uzer where id > \
					((select max(id) from uzer)*"+range+") and id <= (((select max(id) from uzer)*"+range+")+50)";
		PreparedStatement stmt = conn.prepareStatement(sql);
		ResultSet rs = null;
		try{
			rs = stmt.executeQuery();
			while(rs.next()){
				users.add(new User(uuid:rs.getString("uuid"), userId:rs.getString("user_id"), dateCreated:rs.getDate("date_created"),firstName:rs.getString("first_name"),lastName:rs.getString("last_name"),email:rs.getString("email")));
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		stmt.close();
		rs.close();
		conn.commit();
		DirectConnectionManagerService.returnConnection(conn);
//		List<User> temp = User.findAll();
//		if( temp )
//		{
//			users.addAll( temp );	
//		}
	
		return users;	
	}

	public List<User> listFriends( User user ) 
	{
		//getConnection here
		System.out.println("list friends ");
		Connection conn = DirectConnectionManagerService.getConnection();
		
		List<User> friends = new ArrayList<User>();
		List<User> temp = friendService.listFriends( user, conn );
		if( temp )
		{
			friends.addAll( temp );
		}
	
		conn.commit();
		DirectConnectionManagerService.returnConnection(conn);
		return friends;	
	}
		// ---
	public List<User> listFollowers( User user )
	{
		List<User> followers = new ArrayList<User>();
		List<User> temp = friendService.listFollowers( user );
		if( temp )
		{
			followers.addAll( temp );
		}
	
		return followers;
	}
	
	public List<User> listIFollow( User user )
	{
		List<User> iFollow = new ArrayList<User>();
		List<User> temp = friendService.listIFollow( user );
		if( temp )
		{
			iFollow.addAll( temp );
		}
	
		return iFollow;
	}
	
	public List<FriendRequest> listOpenFriendRequests( User user )
	{
		List<FriendRequest> openRequests = new ArrayList<FriendRequest>();
		
		List<FriendRequest> temp = friendService.listOpenFriendRequests( user );
		if( temp )
		{
			openRequests.addAll( temp );	
		}
		
		return openRequests;
	}	
}
