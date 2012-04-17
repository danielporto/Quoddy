
dataSource {
	pooled = true
	// driverClassName = "org.hsqldb.jdbcDriver"
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
			dbCreate = "update" // one of 'create', 'create-drop','update'
			url = "jdbc:mysql://localhost:53306/quoddy2";
			// dbCreate = "create-drop"
			// url = "jdbc:hsqldb:mem:devDb
		}
	}
	test {
		dataSource {
			dbCreate = "create-drop"
			// dbCreate = "create-drop"
			url = "jdbc:mysql://localhost:53306/quoddy2";
		}
	}
	production {
		dataSource {
			dbCreate = "update" // one of 'create', 'create-drop','update'
			// dbCreate = "create-drop"
			url = "jdbc:mysql://localhost:53306/quoddy2";
		}
	}
}
