package org.fogbeam.quoddy;

import java.util.Date;
import org.fogbeam.quoddy.Activity;
import org.fogbeam.quoddy.StatusUpdate;
import org.fogbeam.quoddy.User;

class StatusController {

	def userService;
	def activityStreamService;
	def jmsService;
	
	def updateStatus = {
		
		User user = null;
		
		if( !session.user )
		{
			flash.message = "Must be logged in before updating status";
		}
		else
		{
			println "logged in; so proceeding...";
			
			// get our user
			user = userService.findUserByUserId( session.user.userId );
			
			println "constructing our new StatusUpdate object...";
			// construct a status object
			println "statusText: ${params.statusText}";
			StatusUpdate newStatus = new StatusUpdate( text: params.statusText, creator: user );
			
			// put the old "currentStatus" in the oldStatusUpdates collection
			// addToComments
			if( user.currentStatus != null )
			{
				StatusUpdate previousStatus = user.currentStatus;
				// TODO: do we need to detach this or something?
				user.addToOldStatusUpdates( previousStatus );
			}
			
			// set the current status
			println "setting currentStatus";
			user.currentStatus = newStatus;
			if( !user.save() )
			{
				println( "Saving user FAILED");
				user.errors.allErrors.each { println it };
			}
			else
			{
				// handle failure to update User
			}
			
			session.user = user;
			
			// TODO: if the user update was successful
			Activity activity = new Activity(content:newStatus.text);
			ShareTarget streamPublic = ShareTarget.findByName( ShareTarget.STREAM_PUBLIC );
			
			activity.title = "Internal Activity";
			activity.url = new URL( "http://www.example.com" );
			activity.verb = "status_update";
			activity.published = new Date(); // set published to "now"
			activity.targetUuid = streamPublic.uuid;
			activity.owner = user;
			
			// NOTE: we added "name" to EventBase, but how is it really going
			// to be used?  Do we *really* need this??
			activity.name = activity.title;
			activity.effectiveDate = activity.published;
			
			activityStreamService.saveActivity( activity );
			
			
			Map msg = new HashMap();
			msg.creator = activity.owner.userId;
			msg.text = newStatus.text;
			msg.targetUuid = activity.targetUuid;
			msg.originTime = activity.dateCreated.time;
			
			
			println "sending message to JMS";
			jmsService.send( queue: 'uitestActivityQueue', msg, 'standard', null );
			
		}
		
		println "redirecting to home:index";
		redirect( controller:"home", action:"index", params:[userId:user.userId]);
	}

	def listUpdates =
	{
		User user = null;
		List<StatusUpdate> updates = new ArrayList<StatusUpdate>();
		
		if( !session.user )
		{
			flash.message = "Must be logged in before updating status";
		}
		else
		{
			println "logged in; so proceeding...";
			
			// get our user
			//user = userService.findUserByUserId( session.user.userId );
			
			//updates.addAll( user.oldStatusUpdates.sort { it.dateCreated }.reverse() );
			def conn = DirectConnectionManagerService.getConnection();
			
			String sql = "select id from uzer where user_id='"+session.user.userId+"'";
			def row1 = conn.firstRow(sql);
			sql = "select creator_id, id,version,creator_id,date_created,text from status_update where creator_id=" + row1.id;
			conn.eachRow(sql){row ->
				updates.add(new StatusUpdate(text:row.text,creator:session.user,dateCreated:row.date_created));
			}
			if(updates.size>0)
				updates = updates.sort(it.dateCreated).reverse();
		}
		
		[updates:updates]
	}
	
}
