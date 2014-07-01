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
package com.connexience.server.model.organisation;

import com.connexience.server.model.ServerObject;

/**
 * This class represents an organisation, which can contain
 * groups, users, documents, etc
 *
 * @author hugo
 */
public class Organisation extends ServerObject {
    /**
     * Class version UID.
     * 
     * Please increment this value whenever your changes may cause 
     * incompatibility with the previous version of this class. If unsure, ask 
     * one of the core development team or read:
     *   http://docs.oracle.com/javase/6/docs/api/java/io/Serializable.html
     * and
     *   http://docs.oracle.com/javase/6/docs/platform/serialization/spec/version.html#6678
     */
    private static final long serialVersionUID = 2L;


    /** Administration group */
    private String adminGroupId;

    /** ID of the users folder */
    private String userFolderId;

    /** Default group for new users */
    private String defaultGroupId;

    /** ID of the groups folder */
    private String groupFolderId;

    /** ID of the data folder */
    private String dataFolderId;

    /** ID of the data store */
    private String dataStoreId;

    /** ID of the archive store */
    private String archiveStoreId;
    
    /** ID of the data types folder */
    private String documentTypesFolderId;

    /** ID of the services folder */
    private String servicesFolderId;

    /** ID of the applications folder */
    private String applicationsFolderId;

    /** ID of the configuration folder */
    private String configFolderId;
    
    /** ID of the default user that can be assigned if nobody is logged in */
    private String defaultUserId;

    /** ID of the public access group */
    private String publicGroupId;

    /** User Id in gmail to send emails from */
    private String gmailUser;

    /** Password for gmail user */
    private String gmailPassword;

    /** 
     * Access key for this organisation. This is used to unlock the data store
     *  if the stored organisation MAC address matches the detected MAC address
     */
    private String accessKey;
    
    /** Constructor */
    public Organisation() {
    }

    /** Is a folder a special folder */
    public boolean isSpecialFolder(String folderId) {
        if (folderId != null) {
            if (folderId.equals(applicationsFolderId) || folderId.equals(dataFolderId)
                    || folderId.equals(documentTypesFolderId) || folderId.equals(groupFolderId)
                    || folderId.equals(servicesFolderId)
                    || folderId.equals(userFolderId)) {
                return true;

            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    /** Get the id of the group that can administer this organisation */
    public String getAdminGroupId() {
        return adminGroupId;
    }

    /** Set the id of the group that can administer this organisation */
    public void setAdminGroupId(String adminGroupId) {
        this.adminGroupId = adminGroupId;
    }

    /** Get the id of the user folder object */
    public String getUserFolderId() {
        return userFolderId;
    }

    /** Set the ID of the user folder for this organisation */
    public void setUserFolderId(String userFolderId) {
        this.userFolderId = userFolderId;
    }

    /** Set the ID of the groups folder for this organisation */
    public String getGroupFolderId() {
        return groupFolderId;
    }

    /** Set the ID of the groups folder for this organisation */
    public void setGroupFolderId(String groupFolderId) {
        this.groupFolderId = groupFolderId;
    }

    /** Get the ID of the data folder */
    public String getDataFolderId() {
        return dataFolderId;
    }

    /** Set the id of the data folder */
    public void setDataFolderId(String dataFolderId) {
        this.dataFolderId = dataFolderId;
    }

    /** Get the ID of the data store for this organisation */
    public String getDataStoreId() {
        return dataStoreId;
    }

    /** Set the ID of the data store for this organisation */
    public void setDataStoreId(String dataStoreId) {
        this.dataStoreId = dataStoreId;
    }

    /** Get the ID of the archive store for this organisation */
    public String getArchiveStoreId() {
        return archiveStoreId;
    }

    /** Set the ID of the archive store for this organisation */
    public void setArchiveStoreId(String archiveStoreId) {
        this.archiveStoreId = archiveStoreId;
    }

    /** Get the id of the data types folder */
    public String getDocumentTypesFolderId() {
        return documentTypesFolderId;
    }

    /** Set the id of the data types folder */
    public void setDocumentTypesFolderId(String documentTypesFolderId) {
        this.documentTypesFolderId = documentTypesFolderId;
    }

    /** Set the default group ID */
    public void setDefaultGroupId(String defaultGroupId) {
        this.defaultGroupId = defaultGroupId;
    }

    /** Get the default group ID */
    public String getDefaultGroupId() {
        return defaultGroupId;
    }

    /** Get the services folder ID */
    public String getServicesFolderId() {
        return servicesFolderId;
    }

    /** Set the ID of the services folder */
    public void setServicesFolderId(String servicesFolderId) {
        this.servicesFolderId = servicesFolderId;
    }

    /** @return the applicationsFolderId */
    public String getApplicationsFolderId() {
        return applicationsFolderId;
    }

    /** @param applicationsFolderId the applicationsFolderId to set */
    public void setApplicationsFolderId(String applicationsFolderId) {
        this.applicationsFolderId = applicationsFolderId;
    }

    /** @return the defaultUserId */
    public String getDefaultUserId() {
        return defaultUserId;
    }

    /** @param defaultUserId the defaultUserId to set */
    public void setDefaultUserId(String defaultUserId) {
        this.defaultUserId = defaultUserId;
    }

    /** @return the publicGroupId */
    public String getPublicGroupId() {
        return publicGroupId;
    }

    /** @param publicGroupId the publicGroupId to set */
    public void setPublicGroupId(String publicGroupId) {
        this.publicGroupId = publicGroupId;
    }

    public String getGmailUser() {
        return gmailUser;
    }

    public void setGmailUser(String gmailUser) {
        this.gmailUser = gmailUser;
    }

    public String getGmailPassword() {
        return gmailPassword;
    }

    public void setGmailPassword(String gmailPassword) {
        this.gmailPassword = gmailPassword;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getConfigFolderId() {
        return configFolderId;
    }

    public void setConfigFolderId(String configFolderId) {
        this.configFolderId = configFolderId;
    }
}