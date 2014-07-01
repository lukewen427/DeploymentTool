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
package com.connexience.server.util;

import java.text.NumberFormat;

/**
 * This class formats a disk space into a human readable string
 * @author hugo
 */
public class DiskSpaceFormatter {

    /** Size in bytes */
    private long sizeInBytes = 0;
    private NumberFormat format;

    public DiskSpaceFormatter(long sizeInBytes) {
        this.sizeInBytes = sizeInBytes;
        format = NumberFormat.getNumberInstance();
        format.setMaximumFractionDigits(2);
        format.setMinimumFractionDigits(2);        
    }
    
    public String format() {
        double storageKb = (double) sizeInBytes / 1024.0;
        String msg;
        if (storageKb <= 0) {
            msg = "0 bytes";
        } else if (storageKb < 1024) {
            // Less than 1MB
            msg = format.format(Math.abs(storageKb)) + " kilobytes";

        } else if (storageKb >= 1024 && storageKb < 1048576) {
            // More than 1MB, but less than 1 GB
            msg = format.format((Math.abs(storageKb) / 1024.0)) + " MB";
        } else {
            // In the gigabytes..
            msg = format.format(Math.abs(storageKb) / 1048576.0) + " GB";
        }
        return msg;
    }
}
