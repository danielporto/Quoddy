package org.fogbeam.quoddy

import java.util.Date;
import java.sql.*
import java.util.List;

import txstore.scratchpad.rdbms.jdbc.TxMudConnection;
import txstore.scratchpad.rdbms.util.quoddy.*;

class UserListService
{
	public List<UserList> getListsForUser( final User user, TxMudConnection conn=null )
	{
		List<UserList> lists = new ArrayList<UserList>();
		boolean needToCommit = false;
		if(conn == null){
			conn = DirectConnectionManagerService.getConnection();
			needToCommit = true;
		}
		
		String sql = "select id ,version,date_created, description, name,owner_id, uuid\
				from user_list  where owner_id="+user.id;
		PreparedStatement stmt = conn.prepareStatement(sql);
		ResultSet rs = null;
		try{
			rs = stmt.executeQuery();
			while(rs.next()){
				lists.add(new UserList(name:rs.getString("name"),uuid:rs.getString("uuid"), description:rs.getString("description"), owner:user,dateCreated:rs.getDate("date_created")));
			}
		}catch(SQLException e){
			e.printStackTrace();
		}
		stmt.close();
		rs.close();
				
		if(needToCommit){
			try{
				System.out.println("Set empty shadow op for getting list for users");
				DBQUODDYShdEmpty dEm = DBQUODDYShdEmpty.createOperation();
				conn.setShadowOperation(dEm, 0);
			} catch (IOException e) {
				e.printStackTrace();
			}
			conn.commit();
			DirectConnectionManagerService.returnConnection(conn);
		}
//		List<UserList> tempLists = UserList.executeQuery( "select list from UserList as list where list.owner = :owner",
//														  ['owner':user] );
//		if( tempLists )
//		{
//			lists.addAll( tempLists );
//		}
		
		return lists;	
	}
	
	public List<UserList> getListsForUser( final User user, final int maxCount )
	{
		List<UserList> lists = new ArrayList<UserList>();

		List<UserList> tempLists = UserList.executeQuery( "select list from UserList as list where list.owner = :owner",
														  ['owner':user], ['max':maxCount] );

		if( tempLists )
		{
			lists.addAll( tempLists );
		}
											  
													  		
		return lists;
	}

	public List<User> getEligibleUsersForList( final UserList list )
	{
		List<User> eligibleUsers = new ArrayList<User>();
		
		def queryResults = User.executeQuery( "select user from User as user, UserList as list where user not in elements(list.members) and user <> list.owner" );
		
		eligibleUsers.addAll( queryResults ); 
		
		return eligibleUsers;
			
	}	

	public List<Activity> getRecentActivitiesForList( final UserList list, final int maxCount )
	{
		println "getRecentActivitiesForList: ${list.id} - ${maxCount}";
		
		List<Activity> recentActivities = new ArrayList<Activity>();
	
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR_OF_DAY, -600 );
		Date cutoffDate = cal.getTime();
	
		println "Using ${cutoffDate} as cutoffDate";

	
		List<Activity> queryResults =
			Activity.executeQuery( 
					"select activity from Activity as activity, UserList as ulist where activity.dateCreated >= :cutoffDate " + 
					" and activity.owner in elements(ulist.members) and ulist = :thelist order by activity.dateCreated desc",
              ['cutoffDate':cutoffDate, 'thelist':list], ['max': maxCount ]);
			
		if( queryResults )
		{
			println "adding ${queryResults.size()} activities read from DB";
			recentActivities.addAll( queryResults );
		}
	
		return recentActivities;
	}
}
