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
package com.connexience.server.ejb.util;

import com.connexience.server.ConnexienceException;
import com.connexience.server.ejb.acl.AccessControlRemote;
import com.connexience.server.ejb.admin.AdminRemote;
import com.connexience.server.ejb.archive.ArchiveRemote;
import com.connexience.server.ejb.archive.glacier.ArchiveMapRemote;
import com.connexience.server.ejb.certificate.CertificateAccessRemote;
import com.connexience.server.ejb.directory.*;
import com.connexience.server.ejb.notifications.NotificationsRemote;
import com.connexience.server.ejb.properties.PropertiesRemote;
import com.connexience.server.ejb.provenance.PerformanceRemote;
import com.connexience.server.ejb.provenance.ProvenanceRemote;
import com.connexience.server.ejb.remove.ObjectRemovalRemote;
import com.connexience.server.ejb.smtp.SMTPRemote;
import com.connexience.server.ejb.social.*;
import com.connexience.server.ejb.storage.MetaDataRemote;
import com.connexience.server.ejb.storage.StorageRemote;
import com.connexience.server.ejb.ticket.TicketRemote;
import com.connexience.server.ejb.workflow.WorkflowEnactmentRemote;
import com.connexience.server.ejb.workflow.WorkflowLockRemote;
import com.connexience.server.ejb.workflow.WorkflowManagementRemote;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.util.Properties;

public abstract class RemoteEJBLocator {

    private static Properties jndiProps = null;
    private static final String appName = "inkspot";
    private static final String moduleName = "server-beans";
    private static final String distinctName = "";
    public static String initialContextFactory = "org.jboss.naming.remote.client.InitialContextFactory";
    public static String urlPkgPrefixes = "org.jboss.ejb.client.naming";
    public static String providerUrl = "remote://localhost:4447";
    public static String securityPrincipal = "connexience";
    public static String securityCredentials = "1234";


    private static void checkJndiProps() {
        if (jndiProps == null) {
            jndiProps = new Properties();
            jndiProps.put(Context.INITIAL_CONTEXT_FACTORY, initialContextFactory);
            jndiProps.put(Context.URL_PKG_PREFIXES, urlPkgPrefixes);
            jndiProps.put(Context.PROVIDER_URL, providerUrl);
            jndiProps.put(Context.SECURITY_PRINCIPAL, securityPrincipal);
            jndiProps.put(Context.SECURITY_CREDENTIALS, securityCredentials);
            jndiProps.put("jboss.naming.client.ejb.context", true);
        }
    }
    private static AccessControlRemote acRemote = null;
    private static StorageRemote storageRemote = null;
    private static TicketRemote ticketRemote = null;
    private static AdminRemote adminRemote = null;
    private static UserDirectoryRemote userDirectoryRemote = null;
    private static OrganisationDirectoryRemote organisationRemote = null;
    private static CertificateAccessRemote certificateRemote = null;
    private static GroupDirectoryRemote groupDirectoryRemote = null;
    private static ObjectInfoRemote objectInfoRemote = null;
    private static ObjectRemovalRemote objectRemovalRemote = null;
    private static PropertiesRemote propertiesRemote = null;
    private static ObjectDirectoryRemote objectDirectoryRemote = null;
    private static MetaDataRemote metadataRemote = null;
    private static SearchRemote searchRemote = null;
    private static NotificationsRemote notificationsRemote = null;
    private static SMTPRemote smtpRemote = null;
    private static RequestRemote requestRemote = null;
    private static LinkRemote linkRemote = null;
    private static TagRemote tagRemote = null;
    private static MessageRemote messageRemote = null;
    private static EventRemote eventRemote = null;
    private static CommentRemote commentRemote = null;
    private static ProvenanceRemote provRemote = null;
    private static PerformanceRemote perfRemote = null;
    private static WorkflowEnactmentRemote wfEnactmentRemote = null;
    private static WorkflowManagementRemote wfManagementRemote = null;
    private static WorkflowLockRemote wfLockRemote = null;    
    private static ArchiveRemote archiveRemote = null;    
    private static ArchiveMapRemote archiveMapRemote = null;    

    private static Object getRemoteInterface(String beanName, String viewClassName) throws NamingException {
        checkJndiProps();
        Context c = new InitialContext(jndiProps);
        return c.lookup("ejb:" + appName + "/" + moduleName + "/" + distinctName + "/" + beanName + "!" + viewClassName);
    }

    /**
     * Get hold of the document storage bean
     */
    public static StorageRemote lookupStorageBean() throws ConnexienceException {
        try {
            if (storageRemote == null) {
                String beanName = "StorageBean";
                String viewClassName = StorageRemote.class.getName();
                storageRemote = (StorageRemote) getRemoteInterface(beanName, viewClassName);
            }
            return storageRemote;
        } catch (NamingException ne) {
            throw new ConnexienceException("Cannot locate storage bean: " + ne.getMessage());
        }
    }

    /**
     * Get hold of the ticket bean
     */
    public static TicketRemote lookupTicketBean() throws ConnexienceException {
        try {
            if (ticketRemote == null) {
                String beanName = "TicketBean";
                String viewClassName = TicketRemote.class.getName();
                ticketRemote = (TicketRemote) getRemoteInterface(beanName, viewClassName);
            }
            return ticketRemote;
        } catch (NamingException ne) {
            throw new ConnexienceException("Cannot locate Ticket bean: " + ne.getMessage(), ne);
        }
    }

    public static AdminRemote lookupAdminBean() throws ConnexienceException {
        try {
            if (adminRemote == null) {
                String beanName = "AdminBean";
                String viewClassName = AdminRemote.class.getName();
                adminRemote = (AdminRemote) getRemoteInterface(beanName, viewClassName);
            }
            return adminRemote;
        } catch (NamingException ne) {
            ne.printStackTrace();
            throw new ConnexienceException("Cannot locate admin bean: " + ne.getMessage());
        }
    }

    /**
     * Get hold of the access control bean
     */
    public static AccessControlRemote lookupAccessControlBean() throws ConnexienceException {
        try {
            if (acRemote == null) {
                String beanName = "AccountBean";
                String viewClassName = AccessControlRemote.class.getName();
                acRemote = (AccessControlRemote) getRemoteInterface(beanName, viewClassName);
            }
            return acRemote;
        } catch (NamingException ne) {
            ne.printStackTrace();
            throw new ConnexienceException("Cannot locate access control bean: " + ne.getMessage());
        }
    }

    /**
     * Get hold of the access control bean
     */
    public static UserDirectoryRemote lookupUserDirectoryBean() throws ConnexienceException {
        try {
            if (userDirectoryRemote == null) {
                String beanName = "UserDirectoryBean";
                String viewClassName = UserDirectoryRemote.class.getName();
                userDirectoryRemote = (UserDirectoryRemote) getRemoteInterface(beanName, viewClassName);
            }
            return userDirectoryRemote;
        } catch (NamingException ne) {
            throw new ConnexienceException("Cannot locate user directory: " + ne.getMessage());
        }
    }

    /**
     * Get hold of the organisation directory bean
     */
    public static OrganisationDirectoryRemote lookupOrganisationDirectoryBean() throws ConnexienceException {
        try {
            if (organisationRemote == null) {
                String beanName = "OrganisationDirectoryBean";
                String viewClassName = OrganisationDirectoryRemote.class.getName();
                organisationRemote = (OrganisationDirectoryRemote) getRemoteInterface(beanName, viewClassName);
            }
            return organisationRemote;
        } catch (NamingException ne) {
            throw new ConnexienceException("Cannot locate user directory: " + ne.getMessage());
        }
    }

    /**
     * Get hold of certificate bean
     */
    public static CertificateAccessRemote lookupCertificateBean() throws ConnexienceException {
        try {
            if (certificateRemote == null) {
                String beanName = "CertificateAccessBean";
                String viewClassName = CertificateAccessRemote.class.getName();
                certificateRemote = (CertificateAccessRemote) getRemoteInterface(beanName, viewClassName);
            }
            return certificateRemote;
        } catch (NamingException ne) {
            throw new ConnexienceException("Cannot locate certificate access: " + ne.getMessage());
        }
    }

    /**
     * Get hold of group directory bean
     */
    public static GroupDirectoryRemote lookupGroupDirectoryBean() throws ConnexienceException {
        try {
            if (groupDirectoryRemote == null) {
                String beanName = "GroupDirectoryBean";
                String viewClassName = GroupDirectoryRemote.class.getName();
                groupDirectoryRemote = (GroupDirectoryRemote) getRemoteInterface(beanName, viewClassName);
            }
            return groupDirectoryRemote;
        } catch (NamingException ne) {
            throw new ConnexienceException("Cannot locate group directory: " + ne.getMessage());
        }
    }

    /**
     * Get hold of object information bean
     */
    public static ObjectInfoRemote lookupObjectInfoBean() throws ConnexienceException {
        try {
            if (objectInfoRemote == null) {
                String beanName = "ObjectInfoBean";
                String viewClassName = ObjectInfoRemote.class.getName();
                objectInfoRemote = (ObjectInfoRemote) getRemoteInterface(beanName, viewClassName);
            }
            return objectInfoRemote;
        } catch (NamingException ne) {
            throw new ConnexienceException("Cannot locate object info bean: " + ne.getMessage());
        }
    }

    /**
     * Get hold of an object removal bean
     */
    public static ObjectRemovalRemote lookupObjectRemovalBean() throws ConnexienceException {
        try {
            if (objectRemovalRemote == null) {
                String beanName = "ObjectRemovalBean";
                String viewClassName = ObjectRemovalRemote.class.getName();
                objectRemovalRemote = (ObjectRemovalRemote) getRemoteInterface(beanName, viewClassName);
            }
            return objectRemovalRemote;
        } catch (NamingException ne) {
            throw new ConnexienceException("Cannot locate object removal bean: " + ne.getMessage());
        }
    }

    /**
     * Get hold of the properties bean
     */
    public static PropertiesRemote lookupPropertiesBean() throws ConnexienceException {
        try {
            if (propertiesRemote == null) {
                String beanName = "PropertiesBean";
                String viewClassName = PropertiesRemote.class.getName();
                propertiesRemote = (PropertiesRemote) getRemoteInterface(beanName, viewClassName);
            }
            return propertiesRemote;
        } catch (NamingException ne) {
            throw new ConnexienceException("Cannot locate properties bean: " + ne.getMessage());
        }
    }

    /**
     * Get hold of the properties bean
     */
    public static ObjectDirectoryRemote lookupObjectDirectoryBean() throws ConnexienceException {
        try {
            if (objectDirectoryRemote == null) {
                String beanName = "ObjectDirectoryBean";
                String viewClassName = ObjectDirectoryRemote.class.getName();
                objectDirectoryRemote = (ObjectDirectoryRemote) getRemoteInterface(beanName, viewClassName);
            }
            return objectDirectoryRemote;
        } catch (NamingException ne) {
            throw new ConnexienceException("Cannot locate object directory bean: " + ne.getMessage());
        }
    }

    /**
     * Get hold of the metadata bean
     */
    public static MetaDataRemote lookupMetaDataBean() throws ConnexienceException {
        try {
            if (metadataRemote == null) {
                String beanName = "MetaDataBean";
                String viewClassName = MetaDataRemote.class.getName();
                metadataRemote = (MetaDataRemote) getRemoteInterface(beanName, viewClassName);
            }
            return metadataRemote;
        } catch (NamingException ne) {
            throw new ConnexienceException("Cannot locate meta data bean: " + ne.getMessage());
        }
    }

    /**
     * Get hold of the search bean
     */
    public static SearchRemote lookupSearchBean() throws ConnexienceException {
        try {
            if (searchRemote == null) {
                String beanName = "SearchBean";
                String viewClassName = SearchRemote.class.getName();
                searchRemote = (SearchRemote) getRemoteInterface(beanName, viewClassName);
            }
            return searchRemote;
        } catch (NamingException ne) {
            throw new ConnexienceException("Cannot locate search bean: " + ne.getMessage());
        }
    }

    public static NotificationsRemote lookupNotificationsBean() throws ConnexienceException {
        try {
            if (notificationsRemote == null) {
                String beanName = "NotificationsBean";
                String viewClassName = NotificationsRemote.class.getName();
                notificationsRemote = (NotificationsRemote) getRemoteInterface(beanName, viewClassName);
            }
            return notificationsRemote;
        } catch (NamingException ne) {
            throw new ConnexienceException("Cannot locate notifications bean: " + ne.getMessage());
        }
    }

    public static SMTPRemote lookupSMTPBean() throws ConnexienceException {
        try {
            if (smtpRemote == null) {
                String beanName = "SMTPBean";
                String viewClassName = SMTPRemote.class.getName();
                smtpRemote = (SMTPRemote) getRemoteInterface(beanName, viewClassName);
            }
            return smtpRemote;
        } catch (NamingException ne) {
            throw new ConnexienceException("Cannot locate notifications bean: " + ne.getMessage());
        }
    }

    /**
     * Get hold of the social networking request bean
     */
    public static RequestRemote lookupRequestBean() throws ConnexienceException {
        try {
            if (requestRemote == null) {
                String beanName = "RequestBean";
                String viewClassName = RequestRemote.class.getName();
                requestRemote = (RequestRemote) getRemoteInterface(beanName, viewClassName);

            }
            return requestRemote;
        } catch (NamingException ne) {
            throw new ConnexienceException("Cannot locate social network request bean: " + ne.getMessage());
        }
    }

    /**
     * Get hold of the link management bean
     */
    public static LinkRemote lookupLinkBean() throws ConnexienceException {
        try {
            if (linkRemote == null) {
                String beanName = "LinkBean";
                String viewClassName = LinkRemote.class.getName();
                linkRemote = (LinkRemote) getRemoteInterface(beanName, viewClassName);

            }
            return linkRemote;
        } catch (NamingException ne) {
            throw new ConnexienceException("Cannot locate social network link bean: " + ne.getMessage());
        }
    }

    /**
     * Get hold of an Application bean
     */
    public static TagRemote lookupTagBean() throws ConnexienceException {
        try {
            if (tagRemote == null) {
                String beanName = "TagBean";
                String viewClassName = TagRemote.class.getName();
                tagRemote = (TagRemote) getRemoteInterface(beanName, viewClassName);

            }
            return tagRemote;
        } catch (NamingException ne) {
            throw new ConnexienceException("Cannot locate com.connexience.server.social.tag bean: ", ne);
        }
    }

    /**
     * Get hold of an Application bean
     */
    public static MessageRemote lookupMessageBean() throws ConnexienceException {
        try {
            if (messageRemote == null) {
                String beanName = "MessageBean";
                String viewClassName = MessageRemote.class.getName();
                messageRemote = (MessageRemote) getRemoteInterface(beanName, viewClassName);

            }
            return messageRemote;
        } catch (NamingException ne) {
            throw new ConnexienceException("Cannot locate message bean: ", ne);
        }
    }

    /**
     * Get hold of an Application bean
     */
    public static EventRemote lookupEventBean() throws ConnexienceException {
        try {
            if (eventRemote == null) {
                String beanName = "EventBean";
                String viewClassName = EventRemote.class.getName();
                eventRemote = (EventRemote) getRemoteInterface(beanName, viewClassName);
            }
            return eventRemote;
        } catch (NamingException ne) {
            throw new ConnexienceException("Cannot locate event bean: ", ne);
        }
    }

    /**
     * Get hold of an Application bean
     */
    public static CommentRemote lookupCommentBean() throws ConnexienceException {
        try {
            if (commentRemote == null) {
                String beanName = "CommentBean";
                String viewClassName = CommentRemote.class.getName();
                commentRemote = (CommentRemote) getRemoteInterface(beanName, viewClassName);
            }
            return commentRemote;
        } catch (NamingException ne) {
            throw new ConnexienceException("Cannot locate comment bean: ", ne);
        }
    }

    /**
     * Get hold of a Provenance bean
     */
    public static ProvenanceRemote lookupProvenanceBean() throws ConnexienceException {
        try {
            if (provRemote == null) {
                String beanName = "ProvenanceBean";
                String viewClassName = ProvenanceRemote.class.getName();
                provRemote = (ProvenanceRemote) getRemoteInterface(beanName, viewClassName);
            }
            return provRemote;
        } catch (NamingException ne) {
            throw new ConnexienceException("Cannot locate provenance bean: ", ne);
        }
    }

    public static PerformanceRemote lookupPerformanceBean() throws ConnexienceException {
        try {
            if (perfRemote == null) {
                String beanName = "PerformanceBean";
                String viewClassName = PerformanceRemote.class.getName();
                perfRemote = (PerformanceRemote) getRemoteInterface(beanName, viewClassName);
            }
            return perfRemote;
        } catch (NamingException ne) {
            throw new ConnexienceException("Cannot locate performance bean: ", ne);
        }
    }

    /**
     * Get hold of an Archive bean
     */
    public static ArchiveRemote lookupArchiveBean() throws ConnexienceException {
        try {
            if (archiveRemote == null) {
                String beanName = "ArchiveBean";
                String viewClassName = ArchiveRemote.class.getName();
                archiveRemote = (ArchiveRemote) getRemoteInterface(beanName, viewClassName);
            }
            return archiveRemote;
        } catch (NamingException ne) {
            throw new ConnexienceException("Cannot locate archive bean: " + ne.getMessage());
        }
    }

    /**
     * Get hold of an ArchiveMap bean
     */
    public static ArchiveMapRemote lookupArchiveMapBean() throws ConnexienceException {
        try {
            if (archiveMapRemote == null) {
                String beanName = "ArchiveMapBean";
                String viewClassName = ArchiveMapRemote.class.getName();
                archiveMapRemote = (ArchiveMapRemote) getRemoteInterface(beanName, viewClassName);
            }
            return archiveMapRemote;
        } catch (NamingException ne) {
            throw new ConnexienceException("Cannot locate archive map bean: " + ne.getMessage());
        }
    }

    /**
     * Get hold of a workflow management bean
     */
    public static WorkflowManagementRemote lookupWorkflowManagementBean() throws ConnexienceException {
        try {
            if (wfManagementRemote == null) {
                String beanName = "WorkflowManagementBean";
                String viewClassName = WorkflowManagementRemote.class.getName();
                wfManagementRemote = (WorkflowManagementRemote) getRemoteInterface(beanName, viewClassName);
            }
            return wfManagementRemote;
        } catch (NamingException ne) {
            throw new ConnexienceException("Cannot locate workflow management bean: " + ne.getMessage());
        }
    }

    /**
     * Get hold of a workflow execution bean
     */
    public static WorkflowEnactmentRemote lookupWorkflowEnactmentBean() throws ConnexienceException {
        try {
            if (wfEnactmentRemote == null) {
                String beanName = "WorkflowEnactmentBean";
                String viewClassName = WorkflowEnactmentRemote.class.getName();
                wfEnactmentRemote = (WorkflowEnactmentRemote) getRemoteInterface(beanName, viewClassName);
            }
            return wfEnactmentRemote;
        } catch (NamingException ne) {
            throw new ConnexienceException("Cannot locate workflow enactment bean: " + ne.getMessage());
        }
    }

    /**
     * Get hold of a workflow lock bean
     */
    public static WorkflowLockRemote lookupWorkflowLockBean() throws ConnexienceException {
        try {
            if (wfLockRemote == null) {
                String beanName = "WorkflowLockBean";
                String viewClassName = WorkflowLockRemote.class.getName();
                wfLockRemote = (WorkflowLockRemote) getRemoteInterface(beanName, viewClassName);
            }
            return wfLockRemote;
        } catch (NamingException ne) {
            throw new ConnexienceException("Cannot locate workflow lock bean: " + ne.getMessage());
        }
    }
}
