/* =================================================================
 *                     conneXience Data Pipeline
 * =================================================================
 *
 * Copyright 2006 Hugo Hiden and Adrian Conlin
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

package org.pipeline.core.data.manipulation.text;

import org.pipeline.core.data.Column;
import org.pipeline.core.data.Data;
import org.pipeline.core.data.DataException;
import org.pipeline.core.data.columns.StringColumn;
import org.pipeline.core.data.manipulation.ColumnCreator;
import org.pipeline.core.data.manipulation.ColumnPickerCollection;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorageException;
/**
 * This class joins a set of text columns into a single text column
 * @author hugo
 */
public class TextColumnJoiner extends ColumnCreator {
    /** Column pickers used to select columns to join */
    private ColumnPickerCollection pickers = new ColumnPickerCollection();
    
    /** Joining text for columns */
    private String joiningText = "";
    
    /** Creates a new instance of TextColumnJoiner */
    public TextColumnJoiner() {
        super("string-column");
    }

    /** Get the picker collection */
    public ColumnPickerCollection getPickers(){
        return pickers;
    }
    
    /** Create the column of data */
    public Column createColumn(Data data) throws DataException {
        StringColumn column = (StringColumn)createEmptyColumn();
        pickers.setPickersCopyData(false);
        Data extracted = pickers.extractData(data);
        
        int size = extracted.getLargestRows();
        int cols = extracted.getColumns();
        StringBuffer buffer;
        
        for(int i=0;i<size;i++){
            buffer = new StringBuffer();
            for(int j=0;j<cols;j++){
                if(!extracted.column(j).isMissing(i)){
                    buffer.append(extracted.column(j).getStringValue(i));
                } else {
                    buffer.append("");
                }
                
                // Joining text
                if(j<(cols-1) && cols>1){
                    buffer.append(joiningText);
                }
            }
            column.appendStringValue(buffer.toString());
        }
        return column;
    }    
    
    /** Recreate from storage */
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        super.recreateObject(store);
        pickers = (ColumnPickerCollection)store.xmlStorableValue("ColumnSelection");
    }

    /** Save to storage */
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = super.storeObject();
        store.add("ColumnSelection", pickers);
        return store;
    }

    /** Get the text used for joining columns */
    public String getJoiningText() {
        return joiningText;
    }

    /** Set the text used for joining columns */
    public void setJoiningText(String joiningText) {
        this.joiningText = joiningText;
    }
}