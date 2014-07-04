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
package com.connexience.server.workflow.engine;
import org.pipeline.core.drawing.*;

import java.io.*;

/**
 * Transfer data objects that implement this interface support data streaming
 * @author nhgh
 */
public interface StreamableTransferData {
    /** Start reading the data from a stream */
    public void beginReading(InputStream stream) throws DrawingException;

    /** Start writing the data to a stream */
    public void beginWriting(OutputStream stream) throws DrawingException;

    /** Is the stream finished */
    public boolean isFinished();

    /** Close the reader / writer */
    public void close() throws DrawingException;

    /** Set whether this object is in streaming mode */
    public void setStreaming(boolean streaming);

    /** Set the streaming chunk size */
    public void setChunkSize(int chunkSize);

    /** Set the total number of bytes that will be read if known */
    public void setTotalBytesToRead(long bytesToRead);

    /** Get the total number of bytes that will be read by this data source. */
    public long getTotalBytesToRead();

    /** Set whether or not the total number of bytes to read is known */
    public void setTotalBytesKnown(boolean totalBytesKnown);

    /** Are the total number of bytes to read known */
    public boolean isTotalBytesKnown();

    /** Get the number of bytes actuall read */
    public long getActualBytesRead();
}