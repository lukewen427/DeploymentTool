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
package com.connexience.server.model.social;

import java.io.Serializable;

/**
 * The visual representation of a tag cloud element - tag text and magnitude etc.
 * User: nsjw7
 * Date: Aug 6, 2009
 * Time: 1:03:45 PM
 */
public class TagCloudElementImpl implements TagCloudElement, Serializable
{
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
    private static final long serialVersionUID = 1L;


    private String tagText;

    private String fontSize;

    private long weight;

    public TagCloudElementImpl()
    {
    }

    public TagCloudElementImpl(String tagText, long weight)
    {
        this.weight = weight;
        this.tagText = tagText;
    }

    public String getFontSize()
    {
        return fontSize;
    }

    public void setFontSize(String fontSize)
    {
        this.fontSize = fontSize;
    }

    public String getTagText()
    {
        return tagText;
    }

    public void setTagText(String tagText)
    {
        this.tagText = tagText;
    }

    public long getWeight()
    {
        return weight;
    }

    public void setWeight(long weight)
    {
        this.weight = weight;
    }

    public int compareTo(TagCloudElement o)
    {
        return this.tagText.toLowerCase().compareTo(o.getTagText().toLowerCase());
    }
}
