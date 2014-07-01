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

package org.pipeline.core.data.text;

import org.pipeline.core.data.Column;
import org.pipeline.core.data.DataException;

import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class searches a text column using a regular expression. It returns
 * a list of matching and non-matching row numbers
 * @author nhgh
 */
public class RegExpColumnSearcher {
    /** Column to search */
    private Column column;
    
    /** Matching rows */
    private Vector matchingRows = new Vector();
    
    /** Non-matching rows */
    private Vector excludedRows = new Vector();
    
    /** Text pattern */
    private String patternText;
    
    /** Creates a new instance of RegExpColumnSearcher */
    public RegExpColumnSearcher(Column column) {
        this.column = column;
    }
    
    /** Do the search and return the list of matching rows */
    public Vector search(String patternText) throws DataException {
        if(column!=null){
            this.patternText = patternText;
            matchingRows.clear();
            excludedRows.clear();
            Pattern pattern = null;

            // Create the pattern
            try {
                pattern = Pattern.compile(patternText);
            } catch (Exception e){
                throw new DataException("Cannot compile pattern: " + e.getLocalizedMessage());
            }

            int size = column.getRows();
            Matcher match;
            
            for(int i=0;i<size;i++){
                try {
                    if(!column.isMissing(i)){
                        match = pattern.matcher(column.getStringValue(i));
                        if(match.matches()){
                            matchingRows.addElement(new Integer(i)); 
                        } else {
                            excludedRows.addElement(new Integer(i));
                        }
                    } else {
                        // Missing values go to excluded rows
                        excludedRows.addElement(new Integer(i));
                    }
                    
                    
                } catch (Exception e){
                    // Errors get added to the excluded rows
                    excludedRows.addElement(new Integer(i));
                }
            }
            
            return matchingRows;
            
        } else {
            throw new DataException("No data present");
        }
    }
    
    /** Get the matching row indices */
    public Vector getMatchingRows(){
        return matchingRows;
    }
    
    /** Get the non-matching row indices */
    public Vector getExcludedRows(){
        return excludedRows;
    }
}
