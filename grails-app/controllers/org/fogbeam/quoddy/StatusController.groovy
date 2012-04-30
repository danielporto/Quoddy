package org.fogbeam.quoddy;

import java.util.Date;
import java.sql.*;
import org.fogbeam.quoddy.Activity;
import org.fogbeam.quoddy.StatusUpdate;
import org.fogbeam.quoddy.User;
import txstore.scratchpad.rdbms.jdbc.TxMudConnection;
import txstore.scratchpad.rdbms.util.quoddy.*;

class StatusController {

	def userService;
	def activityStreamService;
	def jmsService;
	
	def updateStatus = {
		//println "UPDATE STATUS ===============================================================================begin"
		User user = null;
		if( !session.user )
		{
			flash.message = "Must be logged in before updating status";
		}
		else
		{
			TxMudConnection conn = DirectConnectionManagerService.getConnection();
			//println "logged in; so proceeding...";
			
			// get our user
			user = userService.findUserByUserId( session.user.userId, conn);
			
			//println "constructing our new StatusUpdate object...";
			// construct a status object
			//println "statusText: ${params.statusText}";
			StatusUpdate newStatus = new StatusUpdate( text: params.statusText, creator: user );

			// put the old "currentStatus" in the oldStatusUpdates collection
			// addToComments
			if( user.currentStatus != null )
			{
				//println "current status is not null"
				StatusUpdate previousStatus = user.currentStatus;
				// TODO: do we need to detach this or something?
				user.addToOldStatusUpdates( previousStatus );

			}
			
			newStatus.id=DirectConnectionManagerService.getStatusUpdateAndIncrement();
			//println "id got from the factory:"+newStatus.id
			// set the current status
			//println "setting currentStatus";
			
//			user.currentStatus = newStatus;
//			if( !user.save() )
//			{
//				println( "Saving user FAILED");
//				user.errors.allErrors.each { println it };
//			}
//			else
//			{
//				// handle failure to update User
//			}
			java.sql.Date now = new java.sql.Date(System.currentTimeMillis()); 
			String sql= "insert into status_update (id,creator_id, date_created, text)	values	("+newStatus.id+", "+user.id+", '"+now+"', '"+params.statusText+"')";
			PreparedStatement statement = conn.prepareStatement(sql);
			try{
				statement.executeUpdate();
			}catch(SQLException e){
				e.printStackTrace();
			}
			statement.close();	
			session.user = user;
			
			// TODO: if the user update was successful
			
			Activity activity = new Activity(content:newStatus.text);
			activity.id=DirectConnectionManagerService.getEventBaseIdAndIncrement();
			//ShareTarget streamPublic = ShareTarget.findByName( ShareTarget.STREAM_PUBLIC );
			sql="select id, name, uuid from	share_target where name='"+ShareTarget.STREAM_PUBLIC+"'";
			statement = conn.prepareStatement(sql);
			ResultSet rs = null;
			ShareTarget streamPublic = null;
			try{
				rs = statement.executeQuery();
				if(rs.next()){
					streamPublic = new ShareTarget (name:rs.getString("name"),uuid:rs.getString("uuid"));
					streamPublic.id=rs.getInt("id");
				}
			}catch(SQLException e){
				e.printStackTrace();
			}
			rs.close();
			statement.close();
			activity.title = "Internal Activity";
			activity.url = new URL( "http://www.example.com" );
			activity.verb = "status_update";
			activity.published = now; //new Date(); // set published to "now"
			activity.targetUuid = streamPublic.uuid;
			activity.owner = user;
			activity.dateCreated=now;
			// NOTE: we added "name" to EventBase, but how is it really going
			// to be used?  Do we *really* need this??
			activity.name = activity.title;
			activity.effectiveDate = activity.published;
			
			//activityStreamService.saveActivity( activity );
			//=======
			sql = "insert into	event_base (id, date_created, effective_date, name, owner_id, target_uuid) 	\
					values	("+activity.id+", '"+now+"', '"+activity.effectiveDate+"', '"+activity.title+"', "+activity.owner.id+", '"+activity.targetUuid+"')";
			
			statement = conn.prepareStatement(sql);
			try{
				statement.executeUpdate();
			}catch(SQLException e){
				e.printStackTrace();
			}
			statement.close();
			
			sql = "insert into	activity \
					(actor_content, actor_display_name, actor_image_height, actor_image_url, actor_image_width, actor_object_type, actor_url, actor_uuid, content, generator_url, icon, object_content, object_display_name, object_image_height, object_image_url, object_image_width, object_object_type, object_url, object_uuid, provider_url, published, target_content, target_display_name, target_image_height, target_image_url, target_image_width, target_object_type, target_url, title, updated, url, uuid, verb, id) \
				values \
					(NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '"+activity.content+"', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL, '"+now+"', NULL, NULL, NULL, NULL, NULL, NULL, NULL, '"+activity.title+"', NULL, '"+activity.url+"', '"+activity.targetUuid+"', '"+activity.verb+"', '"+activity.id+"')";
			statement = conn.prepareStatement(sql);
			try{
				statement.executeUpdate();
			}catch(SQLException e){
				e.printStackTrace();
			}
			statement.close();
			sql = "update uzer set current_status_id="+newStatus.id+", date_created='"+now+"' where id="+user.id;
			statement = conn.prepareStatement(sql);
			try{
				statement.executeUpdate();
			}catch(SQLException e){
				e.printStackTrace();
			}
			statement.close();
			//set shadow operation
			try{
				System.out.println("set shadow operation for update status");
				DBQUODDYShdUpdateStatus dUS = DBQUODDYShdUpdateStatus.createOperation((int)newStatus.id, (int)user.id, now.toString(), newStatus.text, (int)activity.id,
					activity.effectiveDate.toString(), activity.title, (int)activity.owner.id, activity.targetUuid, activity.content, activity.url.toString(), activity.verb);
				conn.setShadowOperation(dUS, 0);
			} catch (IOException e) {
				e.printStackTrace();
			}
			conn.commit();
			DirectConnectionManagerService.returnConnection(conn);
			//=======
			
			Map msg = new HashMap();
			msg.creator = activity.owner.userId;
			msg.text = newStatus.text;
			msg.targetUuid = activity.targetUuid;
			msg.originTime = activity.dateCreated.time;
			
			
			//println "sending message to JMS";
			jmsService.send( queue: 'uitestActivityQueue', msg, 'standard', null );
			
		}
		
		//println "redirecting to home:index";
		redirect( controller:"home", action:"index", params:[userId:user.userId]);
		//println "UPDATE STATUS ===============================================================================end"
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
			//println "logged in; so proceeding...";
			
			// get our user
			//user = userService.findUserByUserId( session.user.userId );
			
			//updates.addAll( user.oldStatusUpdates.sort { it.dateCreated }.reverse() );
			TxMudConnection conn = DirectConnectionManagerService.getConnection();
			
			String sql = "select id from uzer where user_id='"+session.user.userId+"'";
			PreparedStatement stmt = conn.prepareStatement(sql);
			ResultSet rs = null;
			int id;
			try{
				rs = stmt.executeQuery();
				if(rs.next()){
					id = rs.getInt(1);
				}
			}catch(SQLException e){
				e.printStackTrace();
			}
			stmt.close();
			rs.close();
			//sql = "select creator_id, id,version,creator_id,date_created,text from status_update where creator_id=" + row1.id;
			sql = "select creator_id, id,creator_id,date_created,text from status_update where creator_id=" + id;
			stmt = conn.prepareStatement(sql);
			try{
				rs = stmt.executeQuery();
				while(rs.next()){
				StatusUpdate st = new StatusUpdate(text:rs.getString("text"),creator:session.user);
				st.id=rs.getInt("id"); 
				st.dateCreated=rs.getDate("date_created");
				updates.add(st);
				}
			}catch(SQLException e){
				e.printStackTrace();
			}
			stmt.close();
			rs.close();
			
			//println "sorting the list"
			if(updates.size>0)
				updates = updates.sort{ it.dateCreated }.reverse();
			//set shadow operation
			try{
				//System.out.println("Set empty shadow op for list update");
				DBQUODDYShdEmpty dEm = DBQUODDYShdEmpty.createOperation();
				conn.setShadowOperation(dEm, 0);
			} catch (IOException e) {
				e.printStackTrace();
			}
			conn.commit();
			DirectConnectionManagerService.returnConnection(conn);
		}
		//println "now is going to do something wird"
		[updates:updates]
	}
	
}
