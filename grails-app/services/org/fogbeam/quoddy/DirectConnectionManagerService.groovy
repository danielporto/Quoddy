package org.fogbeam.quoddy
import groovy.sql.*

class DirectConnectionManagerService {

    static transactional = true

	def getConnection(){
		return Sql.newInstance("jdbc:mysql://localhost:53306/quoddy2","root","101010","com.mysql.jdbc.Driver")		
	}
    
}
