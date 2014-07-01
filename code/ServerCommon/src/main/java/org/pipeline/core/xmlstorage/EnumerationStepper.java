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

package org.pipeline.core.xmlstorage;

import java.util.Enumeration;
import java.util.Vector;

/**
 * This class steps through an Enumeration to return a numbered item
 * @author  hugo
 */
public class EnumerationStepper {
    private Enumeration enm = null;
    
    /** Creates a new instance of EnumerationStepper */
    public EnumerationStepper(Enumeration en) {
        enm = en;
    }
    
    /** Step to an element */
    public Object stepTo(int iIndex){
        int iCount = 0;
        while(iCount<=iIndex){
            if(iCount==iIndex){
                return enm.nextElement();
            } else {
                iCount++;
                enm.nextElement();
            }
        }
        return null;
    }
    
    /** Return the index of an object */
    public int indexOf(Object oObject){
        int iCount=0;
        while(enm.hasMoreElements()){
            if(enm.nextElement().equals(oObject)){
                return iCount;
            } else {
                enm.nextElement();
                iCount++;
            }
        }
        return -1;
    }
    
    /** Put the data into a new Vector */
    public Vector toVector(){
    	Vector vector = new Vector();
    	while(enm.hasMoreElements()){
    		vector.add(enm.nextElement());
    	}
    	return vector;
    }
}
