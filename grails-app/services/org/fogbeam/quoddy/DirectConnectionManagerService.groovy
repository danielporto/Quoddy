package org.fogbeam.quoddy
import java.sql.*
import java.util.concurrent.atomic.AtomicInteger

import org.fogbeam.quoddy.system.settings.SystemSettings;

class DirectConnectionManagerService {

    static transactional = true
	static AtomicInteger friendRequestCollectionFactory;
	static AtomicInteger friendCollectionFactory;
	static AtomicInteger iFollowCollectionFactory;
	static AtomicInteger statusUpdateFactory;
	static AtomicInteger shareTargetFactory;
	static AtomicInteger eventBaseFactory;
	static String className = "com.mysql.jdbc.Driver";
	static String connectionUrl = "jdbc:mysql://thor05.mpi-sws.org:53306/quoddy2";
	static String connectionUserName = "root";
	static String connectionPassword = "101010";
	static int maxConnPool = 100;
	static Vector<Connection> availableConnPool = new Vector<Connection>();
	static int delta = 1;
	
	//topology file
	static int dcId;
	static int proxyId;
	static int totalproxies;
	static int globalProxyId;
	
	static void setParameters(){
		//read from a file, then dcId and proxyId be set
		dcId = 0;
		proxyId = 0;
		globalProxyId = 0;
		totalproxies = 1;
		delta = totalproxies;
	}
	
	static Connection createConnection(){
		Connection con = null;
		try{
			Class.forName(className);
			con = DriverManager.getConnection(connectionUrl,
				connectionUserName, connectionPassword);
			con.setAutoCommit(false);
		}catch(ClassNotFoundException e){
			e.printStackTrace();
		}
		return con;
			
	}
	
	static synchronized Connection getConnection(){
		if(availableConnPool.size()>0){
			System.out.println("+++++get Connection available pool size: " +(availableConnPool.size()-1));
			return availableConnPool.pop();
		}
		return null;
	}
	
	static synchronized void initDatabasePool(){
		setParameters();
		println "initialize database pool with number: "+maxConnPool;
		int connectionNum = maxConnPool;
		while(connectionNum > 0){
			Connection conn = createConnection();
			availableConnPool.push(conn);
			connectionNum--;
		}
	}
	
	static synchronized void returnConnection(Connection conn){
		availableConnPool.push(conn);
		System.out.println("-----return Connection available connection pool size: " + availableConnPool.size());
	}
	
	static init(){
		System.err.println("my globalProxyId: " + globalProxyId + " totalproxy " + totalproxies);
		Connection conn = getConnection();
		ResultSet rs = null;
		Statement stmt = conn.createStatement();
		int n ;
		try{
			rs = stmt.executeQuery("select MAX(id) as maxid from friend_request_collection");
			if(rs.next()){
				n = rs.getInt(1);
			}else
				n = 0;
			n = n + globalProxyId;
			friendRequestCollectionFactory = new AtomicInteger(n);
			println "set friend request collection factory id: "+n;
		}catch(SQLException e){
			e.printStackTrace();
		}
		rs.close()
		try{
			rs = stmt.executeQuery("select MAX(id) as maxid from friend_collection");
			if(rs.next()){
				n=rs.getInt(1);
			}else
				n=0;
			n = n + globalProxyId;
			friendCollectionFactory = new AtomicInteger(n);
			println "set friendCollectionFactory id: "+n;
		}catch(SQLException e){
			e.printStackTrace();
		}
		rs.close();
		
		try{
			rs = stmt.executeQuery("select MAX(id) as maxid from ifollow_collection");
			if(rs.next()){
				n = rs.getInt(1);
			}else
				n = 0;
			n = n + globalProxyId;
			iFollowCollectionFactory = new AtomicInteger(n);
			println "set iFollowCollectionFactory id: " + n;
			
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		rs.close();

		
		try{
			rs = stmt.executeQuery("select MAX(id) as maxid from status_update");
			if(rs.next()){
				n = rs.getInt(1);
			}else
				n = 0;
			n = n + globalProxyId;
			statusUpdateFactory = new AtomicInteger(n);
			println "set statusUpdateFactory id: " + n;
			
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		rs.close();
		
		try{
			rs = stmt.executeQuery("select MAX(id) as maxid from event_base");
			if(rs.next()){
				n = rs.getInt(1);
			}else
				n = 0;
			n = n + globalProxyId;
			eventBaseFactory = new AtomicInteger(n);
			println "set eventBaseFactory id: " + n;
			
		}catch(SQLException e){
			e.printStackTrace();
		}
		
		rs.close();
		stmt.close();
		conn.commit();
		returnConnection(conn);
	
	}
	
	
	static int getFriendRequestCollectionIdAndIncrement(){
		return friendRequestCollectionFactory.addAndGet(delta);
	}
	static int getFriendCollectionIdAndIncrement(){
		return friendCollectionFactory.addAndGet(delta);
	}
	static int getiFollowCollectionAndIncrement(){
		return iFollowCollectionFactory.addAndGet(delta);
	}
	static int getStatusUpdateAndIncrement(){
		return statusUpdateFactory.addAndGet(delta);
	}
	static int getShareTargetIdAndIncrement(){
		return shareTargetFactory.addAndGet(delta);
	}
	static int getEventBaseIdAndIncrement(){
		return eventBaseFactory.addAndGet(delta);	
	}
}
