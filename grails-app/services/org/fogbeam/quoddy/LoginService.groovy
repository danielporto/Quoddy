package org.fogbeam.quoddy

import java.security.MessageDigest 
import java.security.NoSuchAlgorithmException 
import org.fogbeam.quoddy.ldap.LDAPPerson 
import sun.misc.BASE64Encoder 
import java.sql.*

import txstore.scratchpad.rdbms.jdbc.TxMudConnection;
import txstore.scratchpad.rdbms.util.quoddy.*;

class LoginService 
{
	def userService;
	def ldapPersonService;
	def localAccountService;
	def directConnectionManagerService;
	
	public User doUserLogin( final String userId, final String password )
	{
		User user = null;
		//get a connection here
		TxMudConnection conn = DirectConnectionManagerService.getConnection();
		
		// TODO: deal with authsource stuff, conver this stuff to use JAAS?
		LocalAccount account = localAccountService.findAccountByUserId( userId, conn);
		
//		def conn = DirectConnectionManagerService.getConnection();
//		String sql = "select id, version, password , username , uuid from local_account where username='"+userId+"'";
//		def row = conn.firstRow(sql)
//		LocalAccount account = new LocalAccount(row.uuid,userId,row.password)
		
		boolean trySecondAuthSource = true;
		if( account )
		{
			trySecondAuthSource = false;  // this is a local user
			// verify credentials, verify is existing User, load User	
			String md5HashSubmitted = digestMd5( password );
			//println "md5HashSubmitted: ${md5HashSubmitted}";
			if( md5HashSubmitted.equals( account.password ))
			{
				//println "login successful";
				
				// now find a User that matches this account
				user = userService.findUserByUserId( account.username, conn);
				
			}
			else
			{
				println "login failed on password match.  "
			}
			
		}
		
		if( trySecondAuthSource )
		{
			LDAPPerson person = ldapPersonService.findPersonByUserId( userId );
			if( person )
			{
				// verify credentials, verify is existing User, load User
				String md5HashSubmitted = digestMd5( password );
				//println "md5HashSubmitted: ${md5HashSubmitted}";
				if( md5HashSubmitted.equals( person.userpassword ))
				{
					//println "login successful";
					
					// now find a User that matches this account
					user = userService.findUserByUserId( person.uid );
			
					user = LdapPersonService.copyPersonToUser( person, user );
							
				}
				else
				{
					println "login failed on password match.  "
				}
				
			}
			
		}
		//println "commit in the login ";
		//set shadow operation
		try{
			//System.out.println("Set empty shadow op for login");
			DBQUODDYShdEmpty dEm = DBQUODDYShdEmpty.createOperation();
			conn.setShadowOperation(dEm, 0);
		} catch (IOException e) {
			e.printStackTrace();
		}
		conn.commit();
		DirectConnectionManagerService.returnConnection(conn);
		return user;	
	}	

	private static String digestMd5(final String password)
	{
		String base64;
		try
		{
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(password.getBytes());
			base64 = new BASE64Encoder().encode(digest.digest());
		}
		catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	  
		return "{MD5}" + base64;
	}
}
