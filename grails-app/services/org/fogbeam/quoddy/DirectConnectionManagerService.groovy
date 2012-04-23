package org.fogbeam.quoddy
import groovy.sql.*
import java.util.concurrent.atomic.AtomicInteger

class DirectConnectionManagerService {

    static transactional = true
	static AtomicInteger friendRequestCollectionFactory;
	static AtomicInteger friendCollectionFactory;
	static AtomicInteger iFollowCollectionFactory;
	
	
	static def getConnection(){
		return Sql.newInstance("jdbc:mysql://localhost:53306/quoddy2","root","101010","com.mysql.jdbc.Driver")
			
	}
	static init(){
		friendRequestCollectionFactory = new AtomicInteger(0);
		friendCollectionFactory = new AtomicInteger(0);
		iFollowCollectionFactory = new AtomicInteger(0);
		}
	
	
	static int getFriendRequestCollectionIdAndIncrement(){
		return friendRequestCollectionFactory.getAndIncrement();
	}
	static int getFriendCollectionIdAndIncrement(){
		return friendCollectionFactory.getAndIncrement();
	}
	static int getiFollowCollectionFactory(){
		return iFollowCollectionFactory.getAndIncrement();
	}
}
