package org.fogbeam.quoddy
import java.sql.*

class HomeController {

	def userService;
	def activityStreamService;
	def userStreamService;
	def userListService;
	def userGroupService;
	
    def index = {
		
		long startTime = System.nanoTime();
    	def userId = params.userId;
    	def user = null;
		def activities = null;
		def systemDefinedStreams = new ArrayList<UserStream>();
		def userDefinedStreams = new ArrayList<UserStream>(); 
		def userLists = new ArrayList<UserList>();
		def userGroups = new ArrayList<UserGroup>();
		if( userId != null )
    	{
			//println "getting User by userId: ${userId}";
    		user = userService.findUserByUserId( userId );
    	}
    	else
    	{
			//println "Looking up User in session";
			
    		if( session.user != null )
    		{
				//println "Found User in Session";
    			user = userService.findUserByUserId( session.user.userId );
    		}
			else
			{
				println "No user in Session";
			}
    	}
		
		if( user )
		{
			// TODO: this should take the selected UserStream into account when
			// determining what activities to include in the activities list
			//long startT1 = System.nanoTime();
			activities = activityStreamService.getRecentActivitiesForUser( user, 25 );
			//long endT1 = System.nanoTime();
			//System.out.println("get recent activities in "+ (endT1-startT1)*0.000001 + " ms" )
			//startT1 = System.nanoTime();
			//def tempSysStreams = userStreamService.getSystemDefinedStreamsForUser( user );
			systemDefinedStreams = userStreamService.getSystemDefinedStreamsForUser( user );
			//endT1 = System.nanoTime();
			//System.out.println("get system stream in "+ (endT1-startT1)*0.000001 + " ms" )
			//systemDefinedStreams.addAll( tempSysStreams );
			//startT1 = System.nanoTime();
			//def tempUserStreams = userStreamService.getUserDefinedStreamsForUser( user );
			userDefinedStreams = userStreamService.getUserDefinedStreamsForUser( user );
			//endT1 = System.nanoTime();
			//System.out.println("get user defined stream in "+ (endT1-startT1)*0.000001 + " ms" )
			//userDefinedStreams.addAll( tempUserStreams );
			//startT1 = System.nanoTime();
			//def tempUserLists = userListService.getListsForUser( user );
			userLists = userListService.getListsForUser( user );
			//endT1 = System.nanoTime();
			//System.out.println("get user list in "+ (endT1-startT1)*0.000001 + " ms" )
			//userLists.addAll( tempUserLists );
			//startT1 = System.nanoTime();
			//def tempUserGroups = userGroupService.getAllGroupsForUser( user );
			userGroups = userGroupService.getAllGroupsForUser( user );
			//endT1 = System.nanoTime();
			//System.out.println("get user group in "+ (endT1-startT1)*0.000001 + " ms" )
			//userGroups.addAll( tempUserGroups );
			
			//System.out.println("=====>home with user " + user.userId + " in " + (System.nanoTime()-startTime)*0.000001 + " ms");
		}

    	[user:user, 
		  activities:activities, 
		  sysDefinedStreams:systemDefinedStreams, 
		  userDefinedStreams:userDefinedStreams,
		  userLists:userLists,
		  userGroups:userGroups];

    }
}
