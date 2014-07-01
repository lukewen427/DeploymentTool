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
package com.connexience.server.model.metadata.synonyms;

import com.connexience.server.model.metadata.MetadataItem;
import com.connexience.server.model.metadata.MetadataSynonym;
import com.connexience.server.model.metadata.types.NumericalMetadata;
import com.connexience.server.model.metadata.types.TextMetadata;

/**
 * This synonym replaces text with numerical data
 * @author hugo
 */
public class TextToNumericalMetadataSynonym extends MetadataSynonym {    
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


    @Override
    public boolean isMetadataSupported(MetadataItem item) {
        if(item instanceof TextMetadata){
            return true;
        } else {
            return false;
        }
    }

    @Override
    public MetadataItem processMetadataItem(MetadataItem item) {
        if(item instanceof TextMetadata){
            if(item.getCategory()!=null && item.getCategory().equals(getCategory()) && item.getName()!=null && item.getName().equals(getName())){
                NumericalMetadata newItem = new NumericalMetadata();
                newItem.populateBasicProperties(item);
                try {
                    newItem.setDoubleValue(Double.parseDouble(((TextMetadata)item).getTextValue()));
                } catch (Exception e){
                    newItem.setDoubleValue(Double.NaN);
                }
                return newItem;
            } else {
                return item;
            }
        } else {
            return item;
        }
    }
    
}
