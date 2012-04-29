package org.fogbeam.quoddy;

import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Calendar;

import org.fogbeam.quoddy.Activity;
import org.fogbeam.quoddy.User;

class ActivityStreamService_OLD {

	def userService;
	
	public List<Activity> getRecentFriendActivitiesForUser( User user )
	{
		List<Activity> recentActivities = new ArrayList<Activity>();
		
		List<User> friends = userService.listFriends( user );
		println "Found ${friends.size()} friends";
		List<Integer> friendIds = new ArrayList<Integer>();
		for( User friend: friends )
		{
			def id = friend.id;
			println( "Adding friend id: ${id}, userId: ${friend.userId} to list" );
			friendIds.add( id );	
		}
		
		// Entry.executeQuery( "select entry from Entry as entry, User as user where user.userId = ? and entry not in elements(user.hiddenEntries) order by entry.dateCreated desc", [user.userId] )
		Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
		DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		cal.add(Calendar.HOUR_OF_DAY, -14 );
		String cutoffDate = dateFormat.format(cal.getTime());
		
		println "Using ${cutoffDate} as cutoffDate";
		
		List<Activity> queryResults = Activity.executeQuery( "select activity from Activity as activity where activity.dateCreated >= :cutoffDate and activity.creator.id in (:friendIds)",  ['cutoffDate':cutoffDate, 'friendIds':friendIds]);
		recentActivities.addAll( queryResults );
		return recentActivities;
	}

	public void saveActivity( Activity activity )
	{
		println "about to save activity...";
		if( !activity.save() )
		{
			println( "Saving activity FAILED");
			activity.errors.allErrors.each { println it };
		}
	}
}
