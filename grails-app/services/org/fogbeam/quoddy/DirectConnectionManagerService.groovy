package org.fogbeam.quoddy

import java.sql.*
import java.util.concurrent.atomic.AtomicInteger;
import network.ParallelPassThroughNetworkQueue
import network.netty.NettyTCPReceiver
import network.netty.NettyTCPSender

//gemini packages
import txstore.scratchpad.rdbms.jdbc.TxMudConnection;
import txstore.scratchpad.rdbms.jdbc.TxMudDriver;
import txstore.util.Operation;
import txstore.proxy.ApplicationInterface;
import txstore.proxy.ClosedLoopProxy;
import applications.microbenchmark.TxMudTest.ExecuteScratchpadFactory;
import txstore.scratchpad.rdbms.util.quoddy.*;

class DirectConnectionManagerService {

    static transactional = true
	static AtomicInteger friendRequestCollectionFactory;
	static AtomicInteger friendCollectionFactory;
	static AtomicInteger iFollowCollectionFactory;
	static AtomicInteger statusUpdateFactory;
	static AtomicInteger shareTargetFactory;
	static AtomicInteger eventBaseFactory;
	static String driver = "txstore.scratchpad.rdbms.jdbc.TxMudDriver";
	static String jdbcPath = "jdbc:txmud:test";
	static String padClass = "txstore.scratchpad.rdbms.DBExecuteScratchpad";
	static int maxConnPool = 100;
	static Vector<TxMudConnection> availableConnPool = new Vector<TxMudConnection>();
	static int delta = 1;
	public static QUODDY_TxMud_Proxy proxy;
	
	static TxMudConnection createConnection(){
		TxMudDriver.proxy = proxy.imp;
		TxMudConnection con = (TxMudConnection) DriverManager.getConnection(jdbcPath);
		con.setAutoCommit(false);
		return con;
			
	}
	
	static synchronized TxMudConnection getConnection(){
		if(availableConnPool.size()>0){
			return availableConnPool.pop();
		}
		return null;
	}
	
	static synchronized void initDatabasePool(){
		println "initialize database pool with number: "+maxConnPool;
		int connectionNum = maxConnPool;
		while(connectionNum > 0){
			TxMudConnection conn = createConnection();
			availableConnPool.push(conn);
			connectionNum--;
		}
	}
	
	static synchronized void returnConnection(TxMudConnection conn){
		System.out.println("available connection pool is " + availableConnPool.size());
		availableConnPool.push(conn);
	}
	
	static init(){
		TxMudConnection conn = getConnection();
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
		
		try{
			System.out.println("Set empty shadow op for init");
			DBQUODDYShdEmpty dEm = DBQUODDYShdEmpty.createOperation();
			conn.setShadowOperation(dEm, 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
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

class QUODDY_TxMud_Proxy implements ApplicationInterface {
	String proxy_cnf="";
	int dcId=0;
	int proxyId=0;
	int proxyThreads=10;
	boolean tcpnodelay=true ;
	ClosedLoopProxy imp;
	NettyTCPSender sendNet;
	ParallelPassThroughNetworkQueue ptnq;
	NettyTCPReceiver rcv;

	public QUODDY_TxMud_Proxy(int dcid, int proxyid, int threads, String file, int c, int ssId, String dbXmlFile, int s){
		this.proxy_cnf=file;
		this.dcId=dcid;
		this.proxyId=proxyid;
		this.proxyThreads=threads;
		System.out.println("proxy initializing");
		this.imp = new ClosedLoopProxy(proxy_cnf, dcId, proxyId, this,new ExecuteScratchpadFactory(c, dcid, 0, dbXmlFile,s));
		System.out.println("proxy initialized");
		// set up the networking channels
		//sender
		sendNet = new NettyTCPSender();
		imp.setSender(sendNet);
		sendNet.setTCPNoDelay(tcpnodelay);
		//receiver
		ptnq = new ParallelPassThroughNetworkQueue(imp, proxyThreads);
		rcv = new NettyTCPReceiver(imp.getMembership().getMe().getInetSocketAddress(), ptnq, proxyThreads);
	}
	
	public int getMyGlobalProxyId(){
		if(this.dcId == 0)
			return this.proxyId;
		int dcCount = this.imp.getDatacenterCount();
		int globalProxyId = 0;
		for(int i = 0 ; i < dcCount; i++){
			if(i== this.dcId)
				break;
			else{
				globalProxyId += this.imp.getMembership().getProxyCount(i);
			}
		}
		globalProxyId = globalProxyId + this.proxyId;
		return globalProxyId;
	}
	public int selectStorageServer(Operation op) {
		return 0;
	}

	public int selectStorageServer(byte[] op) {
		return 0;
	}

	public int getBlueTransactions(){
		System.err.println("Blue transactions:"+imp.bluetnxcounter.get());
		return imp.bluetnxcounter.get();
		
	}
	public int getRedTransactions(){
		System.err.println("Red transactions:"+imp.redtnxcounter.get());
		return imp.redtnxcounter.get();
	}
	public int getAbortedTransactions(){
		System.err.println("Aborted transactions:"+imp.aborttnxcounter.get());
		return imp.aborttnxcounter.get();
	}
	public void setMeasurementInterval(long startmi, long endmi){
		System.err.println("Set measurement Interval:"+new Date(startmi) +" to "+new Date(endmi));
		imp.startmi=startmi;
		imp.endmi=endmi;
		imp.bluetnxcounter.set(0);
		imp.redtnxcounter.set(0);
		imp.aborttnxcounter.set(0);

	}
}
