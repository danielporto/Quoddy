// backingStore options: "ldap" or "localdb"
// for now, assume they have to change in tandem: all "ldap" or all "localdb"
// we're not yet - if ever - interested in any really bizarre hybrid scenarios on this
friends.backingStore="localdb";
groups.backingStore="localdb";
enable.self.registration=true;
created.accounts.backingStore="localdb";

dataSource.pooled=true
dataSource.driverClassName="com.mysql.jdbc.Driver"
dataSource.username="root"
dataSource.password="101010"
dataSource.logSql=false

dataSource.dbCreate="update"
dataSource.url="jdbc:mysql://localhost:53306/quoddy2"
