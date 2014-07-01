/**
 * e-Science Central
 * Copyright (C) 2008-2013 School of Computing Science, Newcastle University
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * version 2 as published by the Free Software Foundation at:
 * http://www.gnu.org/licenses/gpl-2.0.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, 5th Floor, Boston, MA 02110-1301, USA.
 */
package com.connexience.server.security.login;

import com.connexience.server.util.HexUtils;
import org.jboss.security.SimpleGroup;
import org.jboss.security.SimplePrincipal;
import org.jboss.security.auth.spi.UsernamePasswordLoginModule;

import javax.naming.InitialContext;
import javax.security.auth.login.LoginException;
import javax.sql.DataSource;
import java.security.MessageDigest;
import java.security.Principal;
import java.security.acl.Group;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * This class provides a login module that authenticates against the login
 * details table in the connexience database.
 * @author hugo
 */
public class ConnexienceLoginModule extends UsernamePasswordLoginModule {
    
    @Override
    protected String getUsersPassword() throws LoginException {
        Principal p = getIdentity();
        if(p!=null){
            Connection c = null;
            PreparedStatement s = null;
            ResultSet r = null;
            try {
                c = getSQLConnection();
                s = c.prepareStatement("SELECT * FROM logondetails WHERE LOWER(logonname)=?");
                s.setString(1, p.getName().toLowerCase());
                r = s.executeQuery();
                if(r.next()){
                    return r.getString("hashedpassword");
                } else {
                    throw new LoginException("No such user");
                }
                
            } catch (Exception e){
                throw new LoginException("Error logging in: " + e.getMessage());
            } finally {
                try {r.close();}catch(Exception e){}
                try {s.close();}catch(Exception e){}
                try {c.close();}catch(Exception e){}
            }
                    
        } else {
            throw new LoginException("No identity available");
        }
    }

    @Override
    protected boolean validatePassword(String inputPassword, String expectedPassword) {
        try{
            if(hashPassword(inputPassword).equals(expectedPassword)){
                return true;
            } else {
                return false;
            }
        } catch (Exception e){
            return false;
        }
    }
   
    @Override
    protected Group[] getRoleSets() throws LoginException {
        // select name from objectsflat where id in(select groupid from groupmembership where userid='8a808282328c961d01328c9a99130000')
        SimpleGroup group = new SimpleGroup("Roles");
        Principal p = getIdentity();
        Connection c = null;
        PreparedStatement s = null;
        ResultSet r = null;
        
        try {
            c = getSQLConnection();
            s = c.prepareStatement("select name from objectsflat where id in(select groupid from groupmembership where userid in (select userid from logondetails where LOWER(logonname)=?))");
            s.setString(1, p.getName().toLowerCase());
            r = s.executeQuery();
            while(r.next()){
                group.addMember(new SimplePrincipal(r.getString("name")));
            }
        } catch (Exception e){
        } finally{
            try {r.close();}catch(Exception e){}
            try {s.close();}catch(Exception e){}
            try {c.close();}catch(Exception e){}
        }
        
        
        return new Group[] { group };
    }
    
    /** Get a database connection */
    private Connection getSQLConnection() throws Exception {
        InitialContext ctx = new InitialContext();
        DataSource source = (DataSource) ctx.lookup("java:jboss/datasources/ConnexienceDB");
        return source.getConnection();
    }    
    
    /** Hash a password in the same way as the user directory bean */
    private String hashPassword(String plainPassword) throws Exception {
        //compute the SHA-1 hash of the password
        MessageDigest msgDigest = MessageDigest.getInstance("SHA-1");
        msgDigest.update(plainPassword.getBytes());
        byte rawByte[] = msgDigest.digest();
        return HexUtils.getHexString(rawByte);
    }    
}