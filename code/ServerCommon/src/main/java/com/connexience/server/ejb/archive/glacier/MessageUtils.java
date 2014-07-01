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
package com.connexience.server.ejb.archive.glacier;

public class MessageUtils
{
    public static final String OPCODE_PROPERTYNAME         = "OpCode";
    public static final String ACCESSKEY_PROPERTYNAME      = "AccessKey";
    public static final String SECRETKEY_PROPERTYNAME      = "SecretKey";
    public static final String DOMAINNAME_PROPERTYNAME     = "DomainName";
    public static final String VAULTNAME_PROPERTYNAME      = "VaultName";
    public static final String DOCUMENTID_PROPERTYNAME     = "DocumentId";
    public static final String DATASTOREID_PROPERTYNAME    = "DataStoreId";
    public static final String ARCHIVESTOREID_PROPERTYNAME = "ArchiveStoreId";
    public static final String DOWNLOADID_PROPERTYNAME     = "DownloadId";
    public static final String QUEUEURL_PROPERTYNAME       = "QueueURL";
    public static final String RECEIPTHANDLE_PROPERTYNAME  = "ReceiptHandle";

    public static final int UNKNOWN_OPCODEVALUE                 = 0;
    public static final int ARCHIVE_REQUEST_OPCODEVALUE         = 1;
    public static final int UPLOADRESTART_REQUEST_OPCODEVALUE   = 2;
    public static final int UNARCHIVE_REQUEST_OPCODEVALUE       = 3;
    public static final int DOWNLOAD_REQUEST_OPCODEVALUE        = 4;
    public static final int DOWNLOADRESTART_REQUEST_OPCODEVALUE = 5;
}
