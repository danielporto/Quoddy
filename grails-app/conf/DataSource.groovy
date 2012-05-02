
dataSource {
	pooled = true
	// driverClassName = "org.hsqldb.jdbcDriver"
	//driverClassName = "txstore.scratchpad.rdbms.jdbc.TxMudDriver"
	driverClassName = "com.mysql.jdbc.Driver"
	// username = "sa"
	username = "root"
	password = "101010"
	// password = ""
	logSql=false
}
hibernate {
    cache.use_second_level_cache=true
    cache.use_query_cache=true
    cache.provider_class='net.sf.ehcache.hibernate.EhCacheProvider'
}
// environment specific settings
environments {
	development {
		dataSource {
			String fileName = "/var/tmp/proxy.txt";
			String dbHost = "";
			int port =0;
			try{
				// Open the file that is the first
				// command line parameter
				FileInputStream fstream = new FileInputStream(fileName);
				// Get the object of DataInputStream
				DataInputStream inputStr = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(inputStr));
				String strLine = br.readLine();
				if(strLine == null){
					System.out.println("Could not find my id file");
					System.exit(-1);
				}
				String[] tmp = strLine.split(" ");
				dbHost = tmp[9];
				port = Integer.parseInt(tmp[11]);
				//Close the input stream
				inputStr.close();
			}catch (Exception e){//Catch exception if any
				System.err.println("Error: " + e.getMessage());
			}
			dbCreate = "update" // one of 'create', 'create-drop','update'
			url = "jdbc:mysql://"+dbHost+":"+port+"/quoddy2";
			System.out.println("set my url " + url);
			// dbCreate = "create-drop"
			// url = "jdbc:hsqldb:mem:devDb
		}
	}
	test {
		dataSource {
			String fileName = "/var/tmp/proxy.txt";
			String dbHost = "";
			int port =0;
			try{
				// Open the file that is the first
				// command line parameter
				FileInputStream fstream = new FileInputStream(fileName);
				// Get the object of DataInputStream
				DataInputStream inputStr = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(inputStr));
				String strLine = br.readLine();
				if(strLine == null){
					System.out.println("Could not find my id file");
					System.exit(-1);
				}
				String[] tmp = strLine.split(" ");
				dbHost = tmp[9];
				port = Integer.parseInt(tmp[11]);
				//Close the input stream
				inputStr.close();
			}catch (Exception e){//Catch exception if any
				System.err.println("Error: " + e.getMessage());
			}
			dbCreate = "create-drop"
			// dbCreate = "create-drop"
			url = "jdbc:mysql://"+dbHost+":"+port+"/quoddy2";
			System.out.println("set my url " + url);
		}
	}
	production {
		dataSource {
			String fileName = "/var/tmp/proxy.txt";
			String dbHost = "";
			int port =0;
			try{
				// Open the file that is the first
				// command line parameter
				FileInputStream fstream = new FileInputStream(fileName);
				// Get the object of DataInputStream
				DataInputStream inputStr = new DataInputStream(fstream);
				BufferedReader br = new BufferedReader(new InputStreamReader(inputStr));
				String strLine = br.readLine();
				if(strLine == null){
					System.out.println("Could not find my id file");
					System.exit(-1);
				}
				String[] tmp = strLine.split(" ");
				dbHost = tmp[9];
				port = Integer.parseInt(tmp[11]);
				//Close the input stream
				inputStr.close();
			}catch (Exception e){//Catch exception if any
				System.err.println("Error: " + e.getMessage());
			}
			dbCreate = "update" // one of 'create', 'create-drop','update'
			// dbCreate = "create-drop"
			url = "jdbc:mysql://"+dbHost+":"+port+"/quoddy2";
			System.out.println("set my url " + url);
		}
	}
}
