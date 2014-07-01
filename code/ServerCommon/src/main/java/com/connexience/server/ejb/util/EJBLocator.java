/**
 * e-Science Central Copyright (C) 2008-2013 School of Computing Science,
 * Newcastle University
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License version 2 as published by the
 * Free Software Foundation at: http://www.gnu.org/licenses/gpl-2.0.html
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, 5th Floor, Boston, MA 02110-1301, USA.
 */
package com.connexience.server.ejb.util;

import com.connexience.server.ConnexienceException;
import com.connexience.server.ejb.acl.AccessControlRemote;
import com.connexience.server.ejb.admin.AdminRemote;
import com.connexience.server.ejb.archive.ArchiveRemote;
import com.connexience.server.ejb.archive.glacier.ArchiveMapRemote;
import com.connexience.server.ejb.archive.glacier.JobMonitorRemote;
import com.connexience.server.ejb.archive.glacier.UnarchiveJobMonitorRemote;
import com.connexience.server.ejb.certificate.CertificateAccessRemote;
import com.connexience.server.ejb.dashboard.DashboardRemote;
import com.connexience.server.ejb.datasets.DatasetsRemote;
import com.connexience.server.ejb.directory.*;
import com.connexience.server.ejb.notifications.NotificationsRemote;
import com.connexience.server.ejb.preferences.PreferenceStoreRemote;
import com.connexience.server.ejb.properties.PropertiesRemote;
import com.connexience.server.ejb.provenance.PerformanceRemote;
import com.connexience.server.ejb.provenance.ProvenanceRemote;
import com.connexience.server.ejb.quota.QuotaRemote;
import com.connexience.server.ejb.remove.ObjectRemovalRemote;
import com.connexience.server.ejb.smtp.SMTPRemote;
import com.connexience.server.ejb.social.*;
import com.connexience.server.ejb.storage.DataStoreMigrationRemote;
import com.connexience.server.ejb.storage.MetaDataRemote;
import com.connexience.server.ejb.storage.StorageRemote;
import com.connexience.server.ejb.project.*;
import com.connexience.server.ejb.scanner.ScannerRemote;
import com.connexience.server.ejb.ticket.TicketRemote;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

/**
 * This class looks up EJB remote interfaces for the beans contained in this
 * project.
 *
 * @author hugo
 */
public abstract class EJBLocator {

    private static AccessControlRemote acRemote = null;
    private static StorageRemote storageRemote = null;
    private static AdminRemote adminRemote = null;
    private static UserDirectoryRemote userDirectoryRemote = null;
    private static OrganisationDirectoryRemote organisationRemote = null;
    private static TicketRemote ticketRemote = null;
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
    private static ArchiveRemote archiveRemote = null;
    private static ArchiveMapRemote archiveMapRemote = null;
    private static JobMonitorRemote jobMonitorRemote = null;
    private static UnarchiveJobMonitorRemote unarchiveJobMonitorRemote = null;
    private static DatasetsRemote datasetsRemote = null;
    private static DashboardRemote dashboardRemote = null;
    private static DataStoreMigrationRemote dataStoreMigrationRemote = null;
    private static QuotaRemote quotaRemote = null;
    private static ProjectsRemote projectsRemote = null;
    private static StudyRemote studyRemote = null;
    private static UploaderRemote uploaderRemote = null;
    private static SubjectsRemote subjectsRemote = null;
    private static LoggersRemote loggersRemote = null;
    private static ScannerRemote scannerRemote = null;
    private static PreferenceStoreRemote preferencesRemote = null;
    private static CredentialsDirectoryRemote credentialsDirectoryRemote = null;

	private static StudyParserRemote studyParserRemote = null;
    /**
     * Get hold of the dashboard bean
     */
    public static DashboardRemote lookupDashboardBean() throws ConnexienceException {
        try {
            if (dashboardRemote == null) {
                Context c = new InitialContext();
                dashboardRemote = (DashboardRemote) c.lookup("java:global/ejb/DashboardBean");
            }
            return dashboardRemote;
        } catch (NamingException ne) {
            throw new ConnexienceException("Cannot locate data sets bean: " + ne.getMessage());
        }
    }

    /**
     * Get hold of the datasets bean
     */
    public static DatasetsRemote lookupDatasetsBean() throws ConnexienceException {
        try {
            if (datasetsRemote == null) {
                Context c = new InitialContext();
                datasetsRemote = (DatasetsRemote) c.lookup("java:global/ejb/DatasetsBean");
            }
            return datasetsRemote;
        } catch (NamingException ne) {
            throw new ConnexienceException("Cannot locate data sets bean: " + ne.getMessage());
        }
    }

    /**
     * Get hold of a quota bean
     */
    public static QuotaRemote lookupQuotaBean() throws ConnexienceException {
        try {
            if (quotaRemote == null) {
                Context c = new InitialContext();
                quotaRemote = (QuotaRemote) c.lookup("java:global/ejb/QuotaBean");
            }
            return quotaRemote;
        } catch (NamingException ne) {
            throw new ConnexienceException("Cannot locate quota bean: " + ne.getMessage());
        }
    }

    /**
     * Get hold of the document storage bean
     */
    public static StorageRemote lookupStorageBean() throws ConnexienceException {
        try {
            if (storageRemote == null) {
                Context c = new InitialContext();
                storageRemote = (StorageRemote) c.lookup("java:global/ejb/StorageBean");
            }
            return storageRemote;
        } catch (NamingException ne) {
            throw new ConnexienceException("Cannot locate storage bean: " + ne.getMessage());
        }
    }

    public static AdminRemote lookupAdminBean() throws ConnexienceException {
        try {
            if (adminRemote == null) {
                Context c = new InitialContext();
                adminRemote = (AdminRemote) c.lookup("java:global/ejb/AdminBean");
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
                Context c = new InitialContext();
                acRemote = (AccessControlRemote) c.lookup("java:global/ejb/AccessControlBean");
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
                Context c = new InitialContext();
                userDirectoryRemote = (UserDirectoryRemote) c.lookup("java:global/ejb/UserDirectoryBean");

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
                Context c = new InitialContext();
                organisationRemote = (OrganisationDirectoryRemote) c.lookup("java:global/ejb/OrganisationDirectoryBean");
            }
            return organisationRemote;
        } catch (NamingException ne) {
            throw new ConnexienceException("Cannot locate user directory: " + ne.getMessage());
        }
    }

    /**
     * Get hold of ticket bean
     */
    public static TicketRemote lookupTicketBean() throws ConnexienceException {
        try {
            if (ticketRemote == null) {
                Context c = new InitialContext();
                ticketRemote = (TicketRemote) c.lookup("java:global/ejb/TicketBean");
            }
            return ticketRemote;
        } catch (NamingException ne) {
            throw new ConnexienceException("Cannot locate ticket issuer: " + ne.getMessage());
        }
    }

    /**
     * Get hold of certificate bean
     */
    public static CertificateAccessRemote lookupCertificateBean() throws ConnexienceException {
        try {
            if (certificateRemote == null) {
                Context c = new InitialContext();
                certificateRemote = (CertificateAccessRemote) c.lookup("java:global/ejb/CertificateAccessBean");
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
                Context c = new InitialContext();
                groupDirectoryRemote = (GroupDirectoryRemote) c.lookup("java:global/ejb/GroupDirectoryBean");
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
                Context c = new InitialContext();
                objectInfoRemote = (ObjectInfoRemote) c.lookup("java:global/ejb/ObjectInfoBean");
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
                Context c = new InitialContext();
                objectRemovalRemote = (ObjectRemovalRemote) c.lookup("java:global/ejb/ObjectRemovalBean");
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
                Context c = new InitialContext();
                propertiesRemote = (PropertiesRemote) c.lookup("java:global/ejb/PropertiesBean");
            }
            return propertiesRemote;
        } catch (NamingException ne) {
            throw new ConnexienceException("Cannot locate cluster bean: " + ne.getMessage());
        }
    }

    /**
     * Get hold of the properties bean
     */
    public static ObjectDirectoryRemote lookupObjectDirectoryBean() throws ConnexienceException {
        try {
            if (objectDirectoryRemote == null) {
                Context c = new InitialContext();
                objectDirectoryRemote = (ObjectDirectoryRemote) c.lookup("java:global/ejb/ObjectDirectoryBean");
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
                Context c = new InitialContext();
                metadataRemote = (MetaDataRemote) c.lookup("java:global/ejb/MetaDataBean");
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
                Context c = new InitialContext();
                searchRemote = (SearchRemote) c.lookup("java:global/ejb/SearchBean");
            }
            return searchRemote;
        } catch (NamingException ne) {
            throw new ConnexienceException("Cannot locate search bean: " + ne.getMessage());
        }
    }

    public static NotificationsRemote lookupNotificationsBean() throws ConnexienceException {
        try {
            if (notificationsRemote == null) {
                Context c = new InitialContext();
                notificationsRemote = (NotificationsRemote) c.lookup("java:global/ejb/NotificationsBean");
            }
            return notificationsRemote;
        } catch (NamingException ne) {
            throw new ConnexienceException("Cannot locate notifications bean: " + ne.getMessage());
        }
    }

    public static SMTPRemote lookupSMTPBean() throws ConnexienceException {
        try {
            if (smtpRemote == null) {
                Context c = new InitialContext();
                smtpRemote = (SMTPRemote) c.lookup("java:global/ejb/SMTPBean");
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
                Context c = new InitialContext();
                requestRemote = (RequestRemote) c.lookup("java:global/ejb/RequestBean");
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
                Context c = new InitialContext();
                linkRemote = (LinkRemote) c.lookup("java:global/ejb/LinkBean");
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
                Context c = new InitialContext();
                tagRemote = (TagRemote) c.lookup("java:global/ejb/TagBean");
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
                Context c = new InitialContext();
                messageRemote = (MessageRemote) c.lookup("java:global/ejb/MessageBean");
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
                Context c = new InitialContext();
                eventRemote = (EventRemote) c.lookup("java:global/ejb/EventBean");
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
                Context c = new InitialContext();
                commentRemote = (CommentRemote) c.lookup("java:global/ejb/CommentBean");
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
                Context c = new InitialContext();
                provRemote = (ProvenanceRemote) c.lookup("java:global/ejb/ProvenanceBean");
            }
            return provRemote;
        } catch (NamingException ne) {
            throw new ConnexienceException("Cannot locate Provenance bean: ", ne);
        }
    }

    public static PerformanceRemote lookupPerformanceBean() throws ConnexienceException {
        try {
            if (perfRemote == null) {
                Context c = new InitialContext();
                perfRemote = (PerformanceRemote) c.lookup("java:global/ejb/PerformanceBean");
            }
            return perfRemote;
        } catch (NamingException ne) {
            throw new ConnexienceException("Cannot locate Provenance bean: ", ne);
        }
    }

    /**
     * Get hold of an Archive bean
     */
    public static ArchiveRemote lookupArchiveBean() throws ConnexienceException {
        try {
            if (archiveRemote == null) {
                Context c = new InitialContext();
                archiveRemote = (ArchiveRemote) c.lookup("java:global/ejb/ArchiveBean");
            }
            return archiveRemote;
        } catch (NamingException ne) {
            throw new ConnexienceException("Cannot locate ArchiveBean bean: ", ne);
        }
    }

    /**
     * Get hold of an ArchiveMap bean
     */
    public static ArchiveMapRemote lookupArchiveMapBean() throws ConnexienceException {
        try {
            if (archiveMapRemote == null) {
                Context c = new InitialContext();
                archiveMapRemote = (ArchiveMapRemote) c.lookup("java:global/ejb/ArchiveMapBean");
            }
            return archiveMapRemote;
        } catch (NamingException ne) {
            throw new ConnexienceException("Cannot locate ArchiveMapBean bean: ", ne);
        }
    }

    /**
     * Get hold of a JobMonitor bean
     */
    public static JobMonitorRemote lookupJobMonitorBean() throws ConnexienceException {
        try {
            if (jobMonitorRemote == null) {
                Context c = new InitialContext();
                jobMonitorRemote = (JobMonitorRemote) c.lookup("java:global/ejb/JobMonitorBean");
            }
            return jobMonitorRemote;
        } catch (NamingException ne) {
            throw new ConnexienceException("Cannot locate JobMonitorRemote bean: ", ne);
        }
    }

    /**
     * Get hold of a UnarchiveJobMonitor bean
     */
    public static UnarchiveJobMonitorRemote lookupUnarchiveJobMonitorBean() throws ConnexienceException {
        try {
            if (unarchiveJobMonitorRemote == null) {
                Context c = new InitialContext();
                unarchiveJobMonitorRemote = (UnarchiveJobMonitorRemote) c.lookup("java:global/ejb/UnarchiveJobMonitorBean");
            }
            return unarchiveJobMonitorRemote;
        } catch (NamingException ne) {
            throw new ConnexienceException("Cannot locate UnarchiveJobMonitorRemote bean: ", ne);
        }
    }

    /**
     * Get hold of a data store migration bean
     */
    public static DataStoreMigrationRemote lookupDataStoreMigrationBean() throws ConnexienceException {
        try {
            if (dataStoreMigrationRemote == null) {
                Context c = new InitialContext();
                dataStoreMigrationRemote = (DataStoreMigrationRemote) c.lookup("java:global/ejb/DataStoreMigrationBean");
            }
            return dataStoreMigrationRemote;
        } catch (NamingException ne) {
            throw new ConnexienceException("Cannot locate DataStoreMigrationRemote bean: ", ne);
        }
    }

    public static ProjectsRemote lookupProjectsBean() throws ConnexienceException {
        try {
            if (projectsRemote == null) {
                Context c = new InitialContext();
                projectsRemote = (ProjectsRemote) c.lookup("java:global/ejb/ProjectsBean");
            }
            return projectsRemote;
        } catch (NamingException ne) {
            throw new ConnexienceException("Cannot locate ProjectsBean bean: ", ne);
        }
    }

    public static StudyRemote lookupStudyBean() throws ConnexienceException {
        try {
            if (studyRemote == null) {
                Context c = new InitialContext();
                studyRemote = (StudyRemote) c.lookup("java:global/ejb/StudyBean");
            }
            return studyRemote;
        } catch (NamingException ne) {
            throw new ConnexienceException("Cannot locate StudyBean bean: ", ne);
        }
    }

    public static UploaderRemote lookupUploaderBean() throws ConnexienceException {
        try {
            if (uploaderRemote == null) {
                Context c = new InitialContext();
                uploaderRemote = (UploaderRemote) c.lookup("java:global/ejb/UploaderBean");
            }

            return uploaderRemote;
        } catch (NamingException ne) {
            throw new ConnexienceException("Cannot locate UploaderBean bean: ", ne);
        }
    }

    public static SubjectsRemote lookupSubjectsBean() throws ConnexienceException {
        try {
            if (subjectsRemote == null) {
                Context c = new InitialContext();
                subjectsRemote = (SubjectsRemote) c.lookup("java:global/ejb/SubjectsBean");
            }

            return subjectsRemote;
        } catch (NamingException ne) {
            throw new ConnexienceException("Cannot locate SubjectsBean bean: ", ne);
        }
    }

    public static LoggersRemote lookupLoggersBean() throws ConnexienceException {
        try {
            if (loggersRemote == null) {
                Context c = new InitialContext();
                loggersRemote = (LoggersRemote) c.lookup("java:global/ejb/LoggersBean");
            }

            return loggersRemote;
        } catch (NamingException ne) {
            throw new ConnexienceException("Cannot locate LoggersBean bean: ", ne);
        }
    }

    public static ScannerRemote lookupScannerBean() throws ConnexienceException {
        try {
            if (scannerRemote == null) {
                Context c = new InitialContext();
                scannerRemote = (ScannerRemote) c.lookup("java:global/ejb/ScannerBean");
            }

            return scannerRemote;
        } catch (NamingException ne) {
            throw new ConnexienceException("Cannot locate ScannerBean bean: ", ne);
        }
    }

	public static StudyParserRemote lookupStudyParserBean() throws ConnexienceException {
		try {
			if (studyParserRemote == null) {
				Context c = new InitialContext();
				studyParserRemote = (StudyParserRemote) c.lookup("java:global/study-file-parser/study-file-parser-ejb/StudyParserBean");
			}

			return studyParserRemote;
		} catch (NamingException ne) {
			throw new ConnexienceException("Cannot locate StudyParserBean: ", ne);
		}
	}
        
        public static PreferenceStoreRemote lookupPreferencesBean() throws ConnexienceException {
		try {
			if (preferencesRemote == null) {
				Context c = new InitialContext();
				preferencesRemote = (PreferenceStoreRemote) c.lookup("java:global/ejb/PreferenceStoreBean");
			}

			return preferencesRemote;
		} catch (NamingException ne) {
			throw new ConnexienceException("Cannot locate PreferencesStoreBean: ", ne);
		}            
        }
        
        public static CredentialsDirectoryRemote lookupCredentialsDirectoryBean() throws ConnexienceException {
		try {
			if (credentialsDirectoryRemote == null) {
				Context c = new InitialContext();
				credentialsDirectoryRemote = (CredentialsDirectoryRemote) c.lookup("java:global/ejb/CredentialsDirectoryBean");
			}

			return credentialsDirectoryRemote;
		} catch (NamingException ne) {
			throw new ConnexienceException("Cannot locate PreferencesStoreBean: ", ne);
		}            
        }        
}
