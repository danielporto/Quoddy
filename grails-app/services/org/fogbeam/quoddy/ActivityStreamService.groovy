package org.fogbeam.quoddy;
import java.util.Date;
import java.sql.*;



import java.util.Calendar

class ActivityStreamService {

	def userService;
	def jmsService;
	def eventQueueService;
	
	public void saveActivity( Activity activity )
	{
		println "about to save activity...";
		if( !activity.save(flush:true) )
		{
			println( "Saving activity FAILED");
			activity.errors.allErrors.each { println it };
		}
		else 
		{
			println "Successfully saved Activity: ${activity.id}";	
		}
		
	}

	
	/* Note: we're cheating here and just dealing with one queue, one user, etc., just to prove
	 * the interaction from the UI layer down to here.  The real stuff will obviously pull in 
	 * activities based on friends, and whatever other "stuff" the user has registered interest in.
	*/
	
	public List<Activity> getRecentActivitiesForUser( final User user, final int maxCount )
	{
		println "getRecentActivitiesForUser: ${user.userId} - ${maxCount}";
		/*
		 
		 so what do we do here?  Ok... we receive a request for up to maxCount recent activities.
		 Since, by definition, the stuff in the queue is most recent, we read up to maxCount entries
		 from the queue. If the queue has more than maxCount activities we ??? (what? Blow away the
		 extras? Leave 'em hanging around for later? Force a flush to the db? ???)
		 
		 If the queue had less than maxCount records (down to as few as NONE), we retrieve
		 up to (maxCount - readfromQueueCount) matching records from the db. 
		
		 The resulting list is the union of the set of activities retrieved from the queue and
		 the activities loaded from the DB.
		
		 Note: Since we really want to show "newest at top" or "newest first" we really wish this
		 "queue" were actually a stack, so we'd be starting with the newest messages and
		 getting progressively older ones.  We need to explore the possibility of having our
		 underlying messaging system offer stack semantics, OR implement an intermediate 
		 queue, that reads the messages from the underlying messaging fabric, and offers them
		 to us in the right order.  Possibly explore using Camel for this, or roll our own
		 thing?
		
		 we could also just read everything that's currently on the queue, sort by timestamp,
		 use up to maxCount of the messages, and then throw away anything that's left-over.
		 but if we do too much of this, we wind up throwing away a lot of queued messages, which
		 negates the benefit of not having to read from the DB.
		
		 ok, just to get something prototyped... let's pretend that the queue we're reading from
		 right here *is* the "intermediate queue" and everything is just magically in the right order.
		 "no problem in computer science that you can't solve by adding a layer of abstraction" right?
		
		  Also, for now let's pretend that the queue we're reading from has already been filtered so that
		  it only contains messages that we are interested in; including expiring messages for age, etc.
		
		*/
		
		int msgsOnQueue = eventQueueService.getQueueSizeForUser( user.userId );
		println "Messages available on queue: ${msgsOnQueue}";
		int msgsToRead = 0;
		if( msgsOnQueue > 0 )
		{
			if( msgsOnQueue <= maxCount )
			{
				msgsToRead = msgsOnQueue;
			}
			else 
			{
				msgsToRead = maxCount - msgsOnQueue;	
			}
		}
		
		println "Messages to read from queue: ${msgsToRead}";
		
		// long oldestOriginTime = Long.MAX_VALUE;
		long oldestOriginTime = new Date().getTime();
		
		// NOTE: we could avoid iterating over this list again by returning the "oldest message time"
		// as part of this call.  But it'll mean wrapping this stuff up into an object of some
		// sort, or returning a Map of Maps instead of a List of Maps
		List<Map> messages = eventQueueService.getMessagesForUser( user.userId, msgsToRead );
		for( Map msg : messages )
		{
			println "msg.originTime: ${msg.originTime}";
			if( msg.originTime < oldestOriginTime )
			{
				oldestOriginTime = msg.originTime;
			}
		}
		
		println "oldestOriginTime: ${oldestOriginTime}";
		println "as date: " + new Date( oldestOriginTime);
		
		// convert our messages to Activity instances and
		// put them in this list...
		List<Activity> recentActivities = new ArrayList<Activity>();
		
		// NOTE: we wouldn't really want to iterate over this list here... better
		// to build up this list above, and never bother storing the JMS Message instances
		// at all...  but for now, just to get something so we can prototype the
		// behavior up through the UI...
		Connection conn1 = DirectConnectionManagerService.getConnection();
		for( int i = 0; i < messages.size(); i++ )
		{
			Map msg = messages.get(i);
			println "got message: ${msg} off of queue";
			Activity activity = new Activity();
			
			// println "msg class: " + msg?.getClass().getName();
			activity.owner = userService.findUserByUserId( msg.creator, conn1 ); 
			activity.content = msg.text;
			activity.dateCreated = new Date( msg.originTime );
			recentActivities.add( activity );	
		}
		if(messages.size()>0)
			conn1.commit();
		DirectConnectionManagerService.returnConnection(conn1);
		println "recentActivities.size() = ${recentActivities.size()}"
		
		/* NOTE: here, we need to make sure we don't retrieve anything NEWER than the OLDEST
		 * message we may have in hand - that we received from the queue.  Otherwise, we risk
		 * showing the same event twice.
		 */
		
		// now, do we need to go to the DB to get some more activities?
		if( maxCount > msgsToRead ) 
		{
			int recordsToRetrieve = maxCount - msgsToRead;
			println "retrieving up to ${recordsToRetrieve} records from the database";
			
			// NOTE: get up to recordsToRetrieve records, but don't retrieve anything that
			// would already be in our working set.
			// also... we need to make a distinction between the "get recent" method which has
			// this cutoff logic and the generic "get older" method that can be used to incrementally
			// step backwards into history as far as (they want to go | as far as we let them go)
			
			
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.HOUR_OF_DAY, -600 );
			Date cutoffDate = cal.getTime();
			
			println "Using ${cutoffDate} as cutoffDate";
			println "Using ${new Date(oldestOriginTime)} as oldestOriginTime";
						
			List<User> friends = userService.listFriends( user );
			StringBuilder sb = new StringBuilder(); 
			boolean friends_added = false; 
			if( friends != null && friends.size() >= 0 ) 
			{
				println "Found ${friends.size()} friends";
				List<Integer> friendIds = new ArrayList<Integer>();
				for( User friend: friends )
				{
					def id = friend.id;
					println( "Adding friend id: ${id}, userId: ${friend.userId} to list" );
					friendIds.add( id );
					
					sb.append(id);
					sb.append(",");
					friends_added=true;
				}
				if(friends_added)
					sb.deleteCharAt(sb.length()-1);
				else
					sb.append("null");
			
				
				// for the purpose of this query, treat a user as their own friend... that is, we
				// will want to read Activities created by this user (we see out own updates in our
				// own feed)
				friendIds.add( user.id );

				//ShareTarget streamPublic = ShareTarget.findByName( ShareTarget.STREAM_PUBLIC );
				Connection conn = DirectConnectionManagerService.getConnection();
				String sql = "select	id , version,	name, uuid from share_target where	name='"+ShareTarget.STREAM_PUBLIC +"'"; 
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = null;
				ShareTarget streamPublic = null;
				try{
					rs = stmt.executeQuery();
					if(rs.next()){
						streamPublic = new ShareTarget(id:rs.getInt("id"),version:rs.getInt("version"),name:rs.getString("name"),uuid:rs.getString("uuid"));
					}
				}catch(SQLException e){
					e.printStackTrace();
				}
				stmt.close();
				rs.close();
				
//				List<EventBase> queryResults = 
//					EventBase.executeQuery( "select event from EventBase as event where event.effectiveDate >= :cutoffDate and event.owner.id in (:friendIds) and event.effectiveDate < :oldestOriginTime and event.targetUuid = :targetUuid order by event.effectiveDate desc",
//						['cutoffDate':cutoffDate, 
//						 'oldestOriginTime':new Date(oldestOriginTime), 
//						 'friendIds':friendIds, 
//						 'targetUuid':streamPublic.uuid], 
//					    ['max': recordsToRetrieve ]);
				  
				sql = "select \
					eventbase0_.id ,\
					eventbase0_.date_created ,\
					eventbase0_.effective_date ,\
					eventbase0_.name ,\
					eventbase0_.owner_id ,\
					eventbase0_.target_uuid ,\
					eventbase0_1_.actor_content ,\
					eventbase0_1_.actor_display_name ,\
					eventbase0_1_.actor_image_height ,\
					eventbase0_1_.actor_image_url ,\
					eventbase0_1_.actor_image_width ,\
					eventbase0_1_.actor_object_type ,\
					eventbase0_1_.actor_url ,\
					eventbase0_1_.actor_uuid ,\
					eventbase0_1_.content ,\
					eventbase0_1_.generator_url ,\
					eventbase0_1_.icon ,\
					eventbase0_1_.object_content ,\
					eventbase0_1_.object_display_name ,\
					eventbase0_1_.object_image_height ,\
					eventbase0_1_.object_image_url ,\
					eventbase0_1_.object_image_width ,\
					eventbase0_1_.object_object_type ,\
					eventbase0_1_.object_url ,\
					eventbase0_1_.object_uuid ,\
					eventbase0_1_.provider_url ,\
					eventbase0_1_.published ,\
					eventbase0_1_.target_content ,\
					eventbase0_1_.target_display_name ,\
					eventbase0_1_.target_image_height ,\
					eventbase0_1_.target_image_url ,\
					eventbase0_1_.target_image_width ,\
					eventbase0_1_.target_object_type ,\
					eventbase0_1_.target_url ,\
					eventbase0_1_.title ,\
					eventbase0_1_.updated ,\
					eventbase0_1_.url ,\
					eventbase0_1_.uuid ,\
					eventbase0_1_.verb ,\
					eventbase0_2_.date_event_created ,\
					eventbase0_2_.description ,\
					eventbase0_2_.end_date ,\
					eventbase0_2_.geo_lat ,\
					eventbase0_2_.geo_long ,\
					eventbase0_2_.last_modified ,\
					eventbase0_2_.location ,\
					eventbase0_2_.start_date ,\
					eventbase0_2_.status ,\
					eventbase0_2_.summary ,\
					eventbase0_2_.uid ,\
					eventbase0_2_.url ,\
					eventbase0_2_.uuid ,\
					case\
						when eventbase0_1_.id is not null then 1\
						when eventbase0_2_.id is not null then 2\
						when eventbase0_.id is not null then 0\
					end as clazz_\
					from event_base eventbase0_"+
					" left outer join 	activity eventbase0_1_	on eventbase0_.id=eventbase0_1_.id "+ 
					" left outer join	calendar_event eventbase0_2_ on eventbase0_.id=eventbase0_2_.id \
					where	eventbase0_.effective_date>='"+cutoffDate+"'	and (	eventbase0_.owner_id in (	"+sb.toString()+"		)	)	and eventbase0_.effective_date<'"+new Date( oldestOriginTime)+"' and eventbase0_.target_uuid='"+streamPublic.uuid+"' \
					order by eventbase0_.effective_date desc limit "+ recordsToRetrieve;

					stmt = conn.prepareStatement(sql);
					try{
						rs = stmt.executeQuery();
						List<EventBase> queryResults= new ArrayList<EventBase>();
						while(rs.next()){
							queryResults.add(new EventBase(owner:rs.getInt("eventbase0_.owner_id"), dateCreated:rs.getDate("eventbase0_.date_created"), effectiveDate:rs.getDate("eventbase0_.effective_date"), name:rs.getString("eventbase0_.name"), targetUuid:rs.getString("eventbase0_.target_uuid")));
							}
						println "adding ${queryResults.size()} activities read from DB";
						recentActivities.addAll( queryResults );
					}catch(SQLException e){
						e.printStackTrace();
					}
					stmt.close();
					rs.close();
					//println " query "+ sql;		
					conn.commit();
					DirectConnectionManagerService.returnConnection(conn);
					
			}
			else
			{
				println( "no friends, so no activity read from DB" );	
			}
		}
		else
		{
			println "Reading NO messages from DB";	
		}
		
		println "recentActivities.size() = ${recentActivities.size()}";
		return recentActivities;
	}	
}
