package org.fogbeam.quoddy
import groovy.sql.*
import java.util.concurrent.atomic.AtomicInteger

class DirectConnectionManagerService {

    static transactional = true
	static AtomicInteger friendRequestCollectionFactory;
	static AtomicInteger friendCollectionFactory;
	static AtomicInteger iFollowCollectionFactory;
	static AtomicInteger statusUpdateFactory;
	static AtomicInteger shareTargetFactory;
	static AtomicInteger eventBaseFactory;
	
	static def getConnection(){
		return Sql.newInstance("jdbc:mysql://localhost:53306/quoddy2","root","101010","com.mysql.jdbc.Driver")
			
	}
	static init(){
		def conn = getConnection();
		def row = conn.firstRow("select MAX(id) as maxid from friend_request_collection");
		if(row.maxid==null){
			friendRequestCollectionFactory = new AtomicInteger(0);
		}else{
			println "valor do rowid :"+(int)row.maxid;
			friendRequestCollectionFactory = new AtomicInteger((int)row.maxid+1);
		}
		/////////////
		row = conn.firstRow("select MAX(id) as maxid from friend_collection");
		if(row.maxid==null){
			friendCollectionFactory = new AtomicInteger(0);
		}else{
		println "valor do rowid :"+(int)row.maxid;
		friendCollectionFactory = new AtomicInteger((int)row.maxid+1);
		}
		////
		row = conn.firstRow("select MAX(id) as maxid from ifollow_collection");
		if(row.maxid==null){
			iFollowCollectionFactory = new AtomicInteger(0);
		}else{
		println "valor do rowid :"+(int)row.maxid;
		iFollowCollectionFactory = new AtomicInteger((int)row.maxid+1);
		}
		///
		row = conn.firstRow("select MAX(id) as maxid from status_update");
		if(row.maxid==null){
			statusUpdateFactory = new AtomicInteger(0);
		}else{
		println "valor do rowid :"+(int)row.maxid;
			statusUpdateFactory  = new AtomicInteger((int)row.maxid+1);
		}
		/////
		row = conn.firstRow("select MAX(id) as maxid from event_base");
		if(row.maxid==null){
			eventBaseFactory = new AtomicInteger(0);
		}else{
		println "valor do rowid :"+(int)row.maxid;
			eventBaseFactory= new AtomicInteger((int)row.maxid+1);
		}
	
	}
	
	
	static int getFriendRequestCollectionIdAndIncrement(){
		return friendRequestCollectionFactory.getAndIncrement();
	}
	static int getFriendCollectionIdAndIncrement(){
		return friendCollectionFactory.getAndIncrement();
	}
	static int getiFollowCollectionAndIncrement(){
		return iFollowCollectionFactory.getAndIncrement();
	}
	static int getStatusUpdateAndIncrement(){
		return statusUpdateFactory.getAndIncrement();
	}
	static int getShareTargetIdAndIncrement(){
		return shareTargetFactory.getAndIncrement();
	}
	static int getEventBaseIdAndIncrement(){
		return eventBaseFactory.getAndIncrement();	
	}
}
