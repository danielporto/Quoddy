package org.fogbeam.quoddy
import java.sql.*
import java.util.concurrent.atomic.AtomicInteger

class DirectConnectionManagerService {

    static transactional = true
	static AtomicInteger friendRequestCollectionFactory;
	static AtomicInteger friendCollectionFactory;
	static AtomicInteger iFollowCollectionFactory;
	static AtomicInteger statusUpdateFactory;
	static AtomicInteger shareTargetFactory;
	static AtomicInteger eventBaseFactory;
	static String className = "com.mysql.jdbc.Driver";
	static String connectionUrl = "jdbc:mysql://localhost:50000/quoddy2";
	static String connectionUserName = "root";
	static String connectionPassword = "101010";
	static int maxConnPool = 100;
	static Vector<Connection> availableConnPool = new Vector<Connection>();
	static int delta = 1;
	
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
			return availableConnPool.pop();
		}
		return null;
	}
	
	static synchronized void initDatabasePool(){
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
	}
	
	static init(){
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
