package org.fogbeam.quoddy

import java.util.List

class UserGroupService
{
	
	public UserGroup findByGroupId( final Integer groupId )
	{
		UserGroup group = UserGroup.findById( groupId );
		
		return group;	
	}
	
	public List<UserGroup> getGroupsOwnedByUser( final User user )
	{
		List<UserGroup> groups = new ArrayList<UserGroup>();
		
		List<UserGroup> tempGroups = UserGroup.executeQuery( "select thegroup from UserGroup as thegroup where thegroup.owner = :owner",
														  ['owner':user] );
		if( tempGroups )
		{
			groups.addAll( tempGroups );
		}
		
		return groups;
	}		

	public List<UserGroup> getGroupsOwnedByUser( final User user, final int maxCount )
	{
		List<UserGroup> groups = new ArrayList<UserGroup>();

		List<UserGroup> tempGroups = UserGroup.executeQuery( "select thegroup from UserGroup as thegroup where thegroup.owner = :owner",
														  ['owner':user], ['max':maxCount] );

		if( tempGroups )
		{
			groups.addAll( tempGroups );
		}
											  										  
		return groups;
	}

	public List<UserGroup> getGroupsWhereUserIsMember( final User user )
	{
		List<UserGroup> groups = new ArrayList<UserGroup>();
		
		List<UserGroup> tempGroups = UserGroup.executeQuery( "select thegroup from UserGroup as thegroup, User as user where user in elements(thegroup.groupMembers) and user = :theUser", ['theUser':user] );

		if( tempGroups )
		{
			groups.addAll( tempGroups );
		}
		
		
		return groups;
	}
	
	public List<UserGroup> getAllGroups()
	{
		List<UserGroup> groups = new ArrayList<UserGroup>();
		
		List<UserGroup> tempGroups = UserGroup.executeQuery( "from UserGroup" );

		if( tempGroups )
		{
			groups.addAll( tempGroups );
		}
		
		
		return groups;
	}

	// get all groups where the User is the Owner OR is a member
	public List<UserGroup> getAllGroupsForUser( final User user )
	{
		List<UserGroup> groups = new ArrayList<UserGroup>();
		
		def conn = DirectConnectionManagerService.getConnection();
		
		String sql = "select user_group.id, user_group.version, user_group.date_created, user_group.description, user_group.name, user_group.owner_id, user_group.require_join_confirmation, user_group.uuid"+ 
			" from user_group, uzer where uzer.id="+user.id+"  and (user_group.owner_id=uzer.id  or uzer.id in (" + 
                "select user_id from  user_group_uzer  where user_group.id=user_group_uzer.user_group_group_members_id))";
				
		conn.eachRow(sql){row ->
			groups.add(new UserGroup(name:row.name,uuid:row.uuid, description:row.description, requireJoinConfirmation:row.require_join_confirmation,owner:user,dateCreated:row.date_created));
		}
		
//		List<UserGroup> tempGroups = UserGroup.executeQuery( "select ugroup from UserGroup as ugroup, User as user where user = ? and ( ugroup.owner = user OR user in elements (ugroup.groupMembers))", [user] );
//
//		if( tempGroups )
//		{
//			groups.addAll( tempGroups );
//		}
		
		
		return groups;
	}
	
	public List<Activity> getRecentActivitiesForGroup( final UserGroup group, final int maxCount )
	{
		println "getRecentActivitiesForGroup: ${group.id} - ${maxCount}";
			
		List<Activity> recentActivities = new ArrayList<Activity>();
		
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.HOUR_OF_DAY, -600 );
		Date cutoffDate = cal.getTime();
		
		println "Using ${cutoffDate} as cutoffDate";

		
		List<Activity> queryResults =
			Activity.executeQuery( "select activity from Activity as activity where activity.dateCreated >= :cutoffDate and activity.targetUuid = :targetUuid order by activity.dateCreated desc",
			['cutoffDate':cutoffDate, 'targetUuid':group.uuid], ['max': maxCount ]);

		if( queryResults )
		{
			println "adding ${queryResults.size()} activities read from DB";
			recentActivities.addAll( queryResults );	
		}
		
		
		return recentActivities;
				
	}
}
