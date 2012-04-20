package org.fogbeam.quoddy;

import java.util.Date;
import java.util.List

import org.fogbeam.quoddy.profile.Profile

class UserService {

	// injected by Spring, might be backed by LDAP version OR by local dB version;
	// but we should be able to not care which is configured
	def friendService;
	
	// same deal as the friendService...
	def groupService;
	
	// and again...  when finished, this will either be LdapPersonService or LocalAccountService, but we don't care.
	def accountService;
	
	public User findUserByUserId( String userId )
	{
		def conn = DirectConnectionManagerService.getConnection();
		String sql = "select id, version, current_status_id, date_created, email, first_name, full_name,\
		last_name,	profile_id,	user_id, uuid from uzer where user_id='"+userId+"'";
		def row = conn.firstRow(sql)
		User user = null
		if(row ==null){
			println "no user found"
		}else{
		//User user = User.findByUserId( userId );
			user = new User(uuid:row.uuid, userId:row.user_id, dateCreated:row.date_created,firstName:row.first_name,lastName:row.last_name,email:row.email)
		}
		
		return user;
	}	
	
	public User findUserByUuid( final String uuid )
	{
		//User user = User.findByUuid( uuid );
		def conn = DirectConnectionManagerService.getConnection();
		String sql = "select id ,version,current_status_id, date_created, email, first_name, full_name, last_name, profile_id, user_id, "+
		             "uuid from uzer where uuid='"+uuid+"'"
		def row = conn.firstRow(sql)
		User user = null
		if(row.size()==0){
			println "no user found"
		}else{
			//User user = User.findByUserId( userId );			 
			user = new User(uuid:row.uuid, userId:row.user_id, dateCreated:row.date_created,firstName:row.first_name,lastName:row.last_name,email:row.email)
		}
		
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
		def conn = DirectConnectionManagerService.getConnection();
		String sql = "select id, version,current_status_id,date_created,email,first_name ,full_name ,last_name ,profile_id,user_id ,uuid  from uzer";
		
		conn.eachRow(sql){row ->
			users.add(new User(uuid:row.uuid, userId:row.user_id, dateCreated:row.date_created,firstName:row.first_name,lastName:row.last_name,email:row.email));
		}
//		List<User> temp = User.findAll();
//		if( temp )
//		{
//			users.addAll( temp );	
//		}
	
		return users;	
	}

	public List<User> listFriends( User user ) 
	{
		List<User> friends = new ArrayList<User>();
		List<User> temp = friendService.listFriends( user );
		if( temp )
		{
			friends.addAll( temp );
		}
	
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
