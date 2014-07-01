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
package org.pipeline.core.data;

import org.pipeline.core.data.columns.DateColumn;
import org.pipeline.core.data.columns.DoubleColumn;
import org.pipeline.core.data.columns.IntegerColumn;
import org.pipeline.core.data.columns.StringColumn;
import org.pipeline.core.data.manipulation.ColumnPicker;
import org.pipeline.core.xmlstorage.XmlDataStore;
import org.pipeline.core.xmlstorage.XmlStorable;
import org.pipeline.core.xmlstorage.XmlStorageException;

import java.io.Serializable;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;

/**
 * This class represents a set of data that can be passed around a drawing.
 *
 * @author hugo
 */
public class Data implements XmlStorable, Serializable {

    static final long serialVersionUID = -2072743792645339447L;
    /**
     * Set of columns
     */
    private Vector columnVector = new Vector();
    /**
     * Is this data read-only
     */
    private boolean readOnly = false;
    /**
     * Source indexing column. This is used if the data sets needs to keep track
     * of the original source for data
     */
    private Column indexColumn = null;
    /**
     * List of annotations
     */
    private DataAnnotationList annotations = new DataAnnotationList();
    /**
     * List of additional properties
     */
    private XmlDataStore properties = null;

    /**
     * Creates a new instance of Data
     */
    public Data() {
    }

    /**
     * Does this data set have a set of properties
     */
    public boolean hasProperties() {
        if (properties != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Create an empty set of properties
     */
    public void createEmptyProperties() {
        this.properties = new XmlDataStore("DataProperties");
    }

    /**
     * Get the properties
     */
    public XmlDataStore getProperties() {
        if (properties == null) {
            createEmptyProperties();
        }
        return this.properties;
    }

    /**
     * Set the properties
     */
    public void setProperties(XmlDataStore properties) {
        this.properties = properties;
    }

    /**
     * Set the annotations list
     */
    public void setAnnotations(DataAnnotationList annotations) {
        this.annotations = annotations;
    }

    /**
     * Get the annotations
     */
    public DataAnnotationList getAnnotations() {
        return annotations;
    }

    /**
     * Does this data set have and index column
     *
     * @return true if the indexing column exists, false otherwise
     */
    public boolean hasIndexColumn() {
        if (indexColumn != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get the index column
     */
    public Column getIndexColumn() {
        return indexColumn;
    }

    /**
     * Set the index column
     */
    public void setIndexColumn(Column indexColumn) {
        this.indexColumn = indexColumn;
        this.indexColumn.setParentData(this);
    }

    /**
     * Returns a deep copy of this data set. This method produces a new data
     * set.
     *
     * @return An exact copy of this data set
     * @throws DataException
     */
    public Data getCopy() throws DataException {
        Enumeration e = columnVector.elements();
        Data copyData = new Data();
        Column column;
        while (e.hasMoreElements()) {
            copyData.addColumn(((Column) e.nextElement()).getCopy());
        }

        if (indexColumn != null) {
            copyData.setIndexColumn(indexColumn.getCopy());
        }

        if (properties != null) {
            try {
                copyData.setProperties(((XmlDataStore) properties.getCopy()));
            } catch (Exception ex) {
                throw new DataException("Error copying data properties: " + ex.getMessage(), ex);
            }
        }
        return copyData;
    }

    /**
     * Get an empty copy of this data with columns in place, but with no data in
     * them
     *
     * @return Empty copy of this data set
     * @throws DataException
     */
    public Data getEmptyCopy() throws DataException {
        Enumeration e = columnVector.elements();
        Data copyData = new Data();
        Column column;
        Column copyColumn;

        while (e.hasMoreElements()) {
            column = (Column) e.nextElement();
            copyColumn = column.getEmptyCopy();
            if (column.hasProperties()) {
                copyColumn.setProperties((XmlDataStore) column.getProperties().getCopy());
            }
            copyData.addColumn(copyColumn);
        }

        if (indexColumn != null) {
            copyData.setIndexColumn(indexColumn.getEmptyCopy());
        }

        if (properties != null) {
            try {
                copyData.setProperties(((XmlDataStore) properties.getCopy()));
            } catch (Exception ex) {
                throw new DataException("Error copying data properties: " + ex.getMessage(), ex);
            }
        }
        return copyData;
    }

    /**
     * Shift all of the data up a number of rows. This method deletes the top
     * row of data and shifts all of the other rows up one place. This data set
     * will be one row shorter as a result of calling this method.
     *
     * @param rows Number of rows to shift the data in this data set up by
     * @throws IndexOutOfBoundsException
     */
    public void shiftDataUp(int rows) throws IndexOutOfBoundsException {
        Enumeration e = columnVector.elements();
        while (e.hasMoreElements()) {
            ((Column) e.nextElement()).shiftDataUp(rows);
        }
        if (indexColumn != null) {
            indexColumn.shiftDataUp(rows);
        }

    }

    /**
     * Delete a row of data. Yhid method deletes the selected row and shifts the
     * remaining rows of data up by one place.
     *
     * @param row The index of the row of data to delete
     * @throws IndexOutOfBoundsException
     */
    public void removeRow(int row) throws IndexOutOfBoundsException {
        Enumeration e = columnVector.elements();
        // Column column;

        if (row < 0) {
            throw new IndexOutOfBoundsException();
        }

        while (e.hasMoreElements()) {
            if (row > ((Column) e.nextElement()).getRows()) {
                throw new IndexOutOfBoundsException();
            }
        }

        e = columnVector.elements();
        while (e.hasMoreElements()) {
            ((Column) e.nextElement()).removeValue(row);
        }

        if (indexColumn != null) {
            indexColumn.removeValue(row);
        }

    }

    /**
     * Set whether this data is read-only. If this parameter is set, all
     * subsequent attempts to modify this data set will throw an Exception
     *
     * @param readOnly Flag indicating whether this set of data is read only
     */
    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    /**
     * Is this set of data read only. This method returns true if this set of
     * data has been marked as read-only
     *
     * @return true if the data is read-only, false if it may be modified
     */
    public boolean isReadOnly() {
        return readOnly;
    }

    /**
     * Return the number of columns in this data set. This method is a copy of
     * getColumnCount, but has been left here because so many pieces of code
     * depend on it.
     *
     * @return The number of columns contained in this data set
     */
    public int getColumns() {
        return columnVector.size();
    }

    /**
     * Return the number of columns in this data set.
     *
     * @return The number of columns contained in this data set
     */
    public int getColumnCount() {
        return columnVector.size();
    }

    /**
     * Get the largest row count. This method returns the size of the largest
     * column of data in this data set. In cases where a data set has uneven
     * length columns, using this value can result in IndexOutOfBounds
     * exceptions.
     *
     * @return Length of the longest column of data
     */
    public int getLargestRows() {
        int largest = 0;
        Enumeration e = columnVector.elements();
        Column c;
        while (e.hasMoreElements()) {
            c = (Column) e.nextElement();
            if (c.getRows() > largest) {
                largest = c.getRows();
            }
        }
        return largest;
    }

    /**
     * Returns the length of the shortest column of data. The value returned by
     * this method is guaranteed to produce a rectangular set of data as the
     * limiting row count is that contained by the shortest column.
     *
     * @return Length of the shortest column of data
     */
    public int getSmallestRows() {
        if (columnVector.size() > 0) {
            int smallest = Integer.MAX_VALUE;
            Enumeration e = columnVector.elements();
            Column c;
            while (e.hasMoreElements()) {
                c = (Column) e.nextElement();
                if (c.getRows() < smallest) {
                    smallest = c.getRows();
                }
            }
            return smallest;
        } else {
            return 0;
        }
    }

    /**
     * Gets an Enumeration of all of the columns contained in this data set.
     * This is useful for stepping through columns in order.
     *
     * @return An Enumeration of all columns in this data set
     */
    public Enumeration columns() {
        return columnVector.elements();
    }

    /**
     * Get all of the columns in this set of data as an Array. This method
     * returns an Array of type Column[] containing a reference to each column
     * in this data set. Note that this is the original data and not a copy, so
     * modifying data in this array will also affect the data contained in this
     * data set.
     *
     * @return An Array of columns
     */
    public Column[] getColumnAray() {
        return (Column[]) columnVector.toArray(new Column[columnVector.size()]);
    }

    /**
     * Extract a single column of data. This method extracts a column of data
     * from this data set by index.
     *
     * @param column Index of the column to access in this data set
     * @return A reference to one of the columns of data in this data set
     * @throws IndexOutOfBoundsException
     */
    public Column column(int column) throws IndexOutOfBoundsException {
        if (column >= 0 && column < columnVector.size()) {
            return (Column) columnVector.elementAt(column);
        } else {
            throw new IndexOutOfBoundsException("Column not found: " + column);
        }
    }

    /**
     * Extract a single column of data. This method extracts a column of data
     * from this data set by name
     *
     * @param name The name of the column to access in this data set
     * @return A reference to one of the columns of data in this data set
     * @throws DataException
     */
    public Column column(String name) throws DataException {
        Enumeration e = columnVector.elements();
        Column c;
        while (e.hasMoreElements()) {
            c = (Column) e.nextElement();
            if (c.getName().equals(name)) {
                return c;
            }
        }
        // Nothing found
        throw new DataException("Column not found: '" + name + "'");
    }

    /**
     * Get the index of a column given its name.
     *
     * @param name The name of the column to access in this data set
     * @return The index of the column within the data set or -1 if not present
     * @throws DataException
     */
    public int getColumnIndex(String name) throws DataException {
        Enumeration e = columnVector.elements();
        Column c;
        int count = 0;
        while (e.hasMoreElements()) {
            c = (Column) e.nextElement();
            if (c.getName().equals(name)) {
                return count;
            }
            count++;
        }

        // Nothing found
        return -1;
    }

    /**
     * Pick a column from this data set using a column picker. This picker is
     * configured using a String: #n for column 'n' or the plain name to pick a
     * column by name
     *
     * @param id This identifier of the column to pick
     * @return The selected column
     * @throws IndexOutOfBoundsException, DataException
     */
    public Column pickColumn(String identifier) throws IndexOutOfBoundsException, DataException {
        ColumnPicker p = new ColumnPicker(identifier);
        p.setCopyData(false);
        return p.pickColumn(this);
    }

    /**
     * Does this data set contain a named column.
     *
     * @param name Name of the column to search for
     * @return A boolean indicating whether the column is present
     */
    public boolean containsColumn(String name) {
        Enumeration e = columnVector.elements();
        Column c;
        while (e.hasMoreElements()) {
            c = (Column) e.nextElement();
            if (c.getName().equals(name)) {
                return true;
            }
        }
        // Nothing found
        return false;
    }

    /**
     * Extract a single column of data. This method extracts a column of data
     * from this data set by name
     *
     * @param name The name of the column to access in this data set
     * @return A reference to one of the columns of data in this data set
     * @throws DataException
     */
    public Column columnCaseInsentitive(String name) throws DataException {
        Enumeration e = columnVector.elements();
        Column c;
        while (e.hasMoreElements()) {
            c = (Column) e.nextElement();
            if (c.getName().equalsIgnoreCase(name)) {
                return c;
            }
        }
        // Nothing found
        throw new DataException("Column not found: '" + name + "'");
    }

    /**
     * Returns meta-data describing the columns in this data set.
     *
     * @return Meta-data describing columns
     */
    public DataMetaData getMetaData() {
        return new DataMetaData(this);
    }

    /**
     * Remove a column from this data set. This method deletes a column by index
     * from this data set. All columns to the right of the deleted column are
     * shifted one space to the left.
     *
     * @param index Position of data within this data set to delete
     * @throws DataException
     */
    public void removeColumn(int index) throws DataException {
        if (index >= 0 && index < columnVector.size()) {
            Column col = column(index);
            col.setParentData(null);
            columnVector.remove(col);
        }
    }

    /**
     * Replaces a column of data. This method replaces the specified column of
     * data with a different column. The positions of the other columns are
     * unchanged and the replaced column is deleted.
     *
     * @param index Position of column to replace within this data set
     * @param updatedColumn New column of data to add to this data set
     * @throws DataException
     */
    public synchronized void replaceColumn(int index, Column updatedColumn) throws DataException {
        Column oldColumn = column(index);
        columnVector.insertElementAt(updatedColumn, index);
        columnVector.remove(oldColumn);
    }

    /**
     * Adds a column to this data set. This method adds a new column of data to
     * this data set to the right of the existing columns of data.
     *
     * @param column New column to append to this data set
     * @throws DataException
     */
    public void addColumn(Column column) throws DataException {
        if (!readOnly) {
            columnVector.addElement(column);
            column.setParentData(this);
        } else {
            throw new DataException("Data is read-only");
        }
    }

    /**
     * Add a set of columns to the data. This method adds all of the columns
     * contained in an Array of columns to the right hand side of this data set.
     *
     * @param columns Array of columns to add to this data set
     * @throws DataException
     */
    public void addColumns(Column[] columns) throws DataException {
        for (int i = 0; i < columns.length; i++) {
            addColumn(columns[i]);
        }
    }

    /**
     * Insert new column. This method inserts a column of data into this data
     * set at the specified location. All columns to the right of this location
     * are shifted one place to the right to accommodate the new column.
     *
     * @param index Position to insert new column at in this data set
     * @param column Column of data to insert
     * @throws DataException
     */
    public void insertColumn(int index, Column column) throws DataException {
        if (!readOnly) {
            columnVector.insertElementAt(column, index);
        } else {
            throw new DataException("Data is read-only");
        }
    }

    /**
     * Delete all data. This method removes all of the columns of data from this
     * data set and deletes all of the values.
     *
     * @throws DataException
     */
    public void clear() throws DataException {
        if (!readOnly) {
            columnVector.clear();
            indexColumn = null;
        } else {
            throw new DataException("Data is read-only");
        }
    }

    /**
     * Get a subset of the rows of this data set. This method extracts a block
     * of data from this data set spanning all of the columns and containing
     * rows between the specified interval. If padMissing is set to true any
     * columns that do not contain any data within the specified range will be
     * padded with MissingValues
     *
     * @param startRow Position of the first row of data to extract
     * @param endRow Position of the last row of data to extract
     * @param padMissing Specifies whether to pad empty columns with missing
     * values
     * @return A subset of the data in this data set
     * @throws IndexOutOfBoundsException
     * @throws DataException
     */
    public Data getRowSubset(int startRow, int endRow, boolean padMissing) throws IndexOutOfBoundsException, DataException {
        Data data = new Data();
        Column col;
        for (int i = 0; i < getColumns(); i++) {
            col = column(i).getSubset(startRow, endRow, padMissing);
            data.addColumn(col);
        }

        if (indexColumn != null) {
            data.setIndexColumn(indexColumn.getSubset(startRow, endRow, padMissing));
        }

        return data;
    }

    /**
     * Get a subset of columns of this data set. This method creates a new set
     * of data containing a the columns specified between startColumn and
     * endColumn. Note this method creates an entirely new set of data by
     * copying the extracted columns.
     *
     * @param startColumn First column to extract
     * @param endColumn Last column to extract
     * @return Data containing copies of the extracted columns
     * @throws IndexOutOfBoundsException
     * @throws DataException
     */
    public Data getColumnSubset(int startColumn, int endColumn) throws IndexOutOfBoundsException, DataException {
        Data data = new Data();
        for (int i = startColumn; i <= endColumn; i++) {
            data.addColumn(column(i).getCopy());
        }
        if (indexColumn != null) {
            data.setIndexColumn(indexColumn.getCopy());
        }
        return data;
    }

    /**
     * Can rows from a data set be appended to this one. This method checks to
     * see if the rows of data contained in a new set of data can be joined to
     * the bottom of this data set. This is done by checking the number and
     * types of the columns in the new data set.
     *
     * @param rowsToAppend Data set to be appended to this one
     * @return Returns true if the data is compatible
     * @throws DataException
     * @throws IndexOutOfBoundsException
     */
    public boolean canAppendRows(Data rowsToAppend) throws DataException, IndexOutOfBoundsException {
        if (rowsToAppend.getColumns() == getColumns()) {
            // Check data types match
            int c = rowsToAppend.getColumns();

            for (int i = 0; i < c; i++) {
                if (!column(i).getClass().equals(rowsToAppend.column(i).getClass())) {
                    return false;
                }
            }
            return true;

        } else {
            return false;
        }
    }

    /**
     * Join data to this data set. This method appends a copy of the values
     * contained in a new data set to the bottom of this data set. If padMissing
     * is set to true, the resulting data set is forced to be rectangluar by
     * appending MissingValues to the ends of short columns.
     *
     * @param rowsToAppend Data to join to this data set
     * @param padMissing Specifies whether the resulting data will be padded
     * using missing values to be rectangular.
     * @throws DataException
     * @throws IndexOutOfBoundsException
     */
    public void appendRows(Data rowsToAppend, boolean padMissing) throws DataException, IndexOutOfBoundsException {
        if (rowsToAppend.getColumns() == getColumns()) {
            int c = rowsToAppend.getColumns();

            // Check data types match
            boolean match = true;
            for (int i = 0; i < c; i++) {
                if (!column(i).getClass().equals(rowsToAppend.column(i).getClass())) {
                    match = false;
                }
            }
            if (match == false) {
                throw new DataException("Column data types do not match");
            }

            // Do the join
            int r = rowsToAppend.getLargestRows();

            for (int i = 0; i < c; i++) {
                for (int j = 0; j < r; j++) {
                    if (padMissing) {
                        if (j < rowsToAppend.column(i).getRows()) {
                            column(i).appendObjectValue(rowsToAppend.column(i).copyObjectValue(j));
                        } else {
                            column(i).appendObjectValue(MissingValue.get());
                        }

                    } else {
                        column(i).appendObjectValue(rowsToAppend.column(i).copyObjectValue(j));
                    }
                }
            }

            // Sort out the indexing - only add matching columns
            if (indexColumn != null && rowsToAppend.hasIndexColumn() && indexColumn.getDataType().equals(rowsToAppend.getIndexColumn().getDataType())) {
                // Copy in indexes from joined data
                r = rowsToAppend.getIndexColumn().getRows();
                Column ic = rowsToAppend.getIndexColumn();

                for (int i = 0; i < r; i++) {
                    if (ic.isMissing(i)) {
                        indexColumn.appendObjectValue(MissingValue.get());
                    } else {
                        indexColumn.appendObjectValue(ic.copyObjectValue(i));
                    }
                }

            } else if (indexColumn != null) {
                // Have to add missings
                r = rowsToAppend.getIndexColumn().getRows();
                for (int i = 0; i < r; i++) {
                    indexColumn.appendObjectValue(MissingValue.get());
                }
            }
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Trim to a specified size. This method trims this data down to the
     * specified number of rows by deleting values from the end of any columns
     * longer than the specified size
     *
     * @param rows Number of rows to trim to
     */
    public void trimToSize(int rows) {
        Enumeration e = columnVector.elements();
        while (e.hasMoreElements()) {
            ((Column) e.nextElement()).trimToSize(rows);
        }
        if (indexColumn != null) {
            indexColumn.trimToSize(rows);
        }
    }

    /**
     * Pad this data set to the largest row size. This method adds MissingValues
     * to the end of any columns that are shorter than the larges column of data
     * in this data set. This forces the data to be rectangular with the
     * possibility of missing data at the end of any columns that were shorter
     * than the largest.
     */
    public void padToLargestRowSize() {
        int rows = getLargestRows();
        Enumeration e = columnVector.elements();
        while (e.hasMoreElements()) {
            ((Column) e.nextElement()).padToSize(rows);
        }
        if (indexColumn != null) {
            indexColumn.padToSize(rows);
        }

    }

    /**
     * Checks to see if this data has a rectangular shape. This method checks to
     * see if all of the columns of data in this data set are of the same
     * length.
     *
     * @return true if all the columns of data in this data set are of the same
     * length
     */
    public boolean isRectangular() {
        if (columnVector.size() > 0) {
            int rows = ((Column) columnVector.get(0)).getRows();
            Enumeration e = columnVector.elements();
            while (e.hasMoreElements()) {
                if (((Column) e.nextElement()).getRows() != rows) {
                    return false;
                }
            }
            return true;
        } else {
            return true;
        }
    }

    /**
     * Joins another set of data to this data. This method adds a copy of all of
     * the columns contained in a new data set to the right hand side of this
     * data set. If padMissing is set to true, any short columns will be padded
     * to the same size using MissingValues
     *
     * @param dataToJoin Data set to add to the right side of this data set
     * @param padMissing Should short columns be padded with MissingValues
     * @throws IndexOutOfBoundsException
     * @throws DataException
     */
    public void joinData(Data dataToJoin, boolean padMissing) throws IndexOutOfBoundsException, DataException {
        if (!padMissing) {
            // Number of rows in joined data must be identical
            int size = dataToJoin.getLargestRows();
            if (size != getLargestRows()) {
                // Add extra columns
                int cols = dataToJoin.getColumns();
                for (int i = 0; i < cols; i++) {
                    addColumn(dataToJoin.column(i).getCopy());
                }
            } else {
                // Size mismatch
                throw new DataException("Data lengths do not match");
            }

        } else {
            // Join as many rows as possible, pad the rest with missing data
            Column col;
            int cols = dataToJoin.getColumns();
            int size = getLargestRows();
            int rows = dataToJoin.getLargestRows();

            // Pad existing data with missings if necessary
            for (int i = 0; i < getColumns(); i++) {
                col = column(i);
                if (col.getRows() < rows) {
                    while (col.getRows() < rows) {
                        col.appendObjectValue(MissingValue.get());
                    }
                }
            }

            // Get columns and pad them to the correct length
            for (int i = 0; i < cols; i++) {
                col = dataToJoin.column(i).getCopy();
                if (col.getRows() < size) {
                    while (col.getRows() < size) {
                        col.appendObjectValue(MissingValue.get());
                    }
                }
                addColumn(col);
            }
        }

        // This method always loses the index column
        indexColumn = null;
    }

    /**
     * Checks to see if all columns of data in this data set are numerical (i.e.
     * Integer or Double types).
     *
     * @return True if all columns can be treated as numerical data
     */
    public boolean isAllNumerical() {
        boolean numerical = true;
        Enumeration e = columnVector.elements();
        while (e.hasMoreElements()) {
            if (!((Column) e.nextElement() instanceof NumericalColumn)) {
                numerical = false;
            }
        }
        return numerical;
    }

    /**
     * Counts the number of numerical columns of data in this data set.
     *
     * @return The number of numerical columns
     */
    public int getNumericalColumnCount() {
        int count = 0;
        Enumeration e = columnVector.elements();
        while (e.hasMoreElements()) {
            if (((Column) e.nextElement() instanceof NumericalColumn)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Add empty rows. This method inserts a specified number of MissingValues
     * to the the columns in this data.
     *
     * @param startIndex Row index at which to start inserting empty values
     * @param rows Number of empty rows to insert
     * @throws DataException
     * @throws IndexOutOfBoundsException
     */
    public void addEmptyRows(int startIndex, int rows) throws DataException, IndexOutOfBoundsException {
        Enumeration e = columnVector.elements();
        Column col;
        while (e.hasMoreElements()) {
            col = (Column) e.nextElement();
            for (int i = 0; i < rows; i++) {
                col.insertObjectValue(startIndex, MissingValue.get(), true);
            }
        }

        if (indexColumn != null) {
            for (int i = 0; i < rows; i++) {
                indexColumn.insertObjectValue(startIndex, MissingValue.get(), true);
            }
        }
    }

    /**
     * Add empty rows to the end of this data set. This method simply appends
     * the specified number of MissingValues to the end of every column in this
     * data set
     *
     * @param rows Number of empty rows to append to this data set
     * @throws DataException
     * @throws IndexOutOfBoundsException
     */
    public void addEmptyRows(int rows) throws DataException, IndexOutOfBoundsException {
        Enumeration e = columnVector.elements();
        Column col;
        while (e.hasMoreElements()) {
            col = (Column) e.nextElement();
            for (int i = 0; i < rows; i++) {
                col.appendObjectValue(MissingValue.get());
            }
        }

        if (indexColumn != null) {
            for (int i = 0; i < rows; i++) {
                indexColumn.appendObjectValue(MissingValue.get());
            }
        }
    }

    /**
     * Name all of the unnamed columns with default names. This method assigns
     * default names to any columns that either have a null or blank column name
     * .
     */
    public void renameUnnamedColumns() throws DataException {
        if (!readOnly) {
            Enumeration e = columnVector.elements();
            Column col;
            int count = 0;
            while (e.hasMoreElements()) {
                col = (Column) e.nextElement();
                if (col.getName() == null || col.getName().trim().equalsIgnoreCase("")) {
                    col.setName("Column " + count);
                }
                count++;
            }
        } else {
            throw new DataException("Data is read-only");
        }

    }

    /**
     * Does a row of data in this column contain any missing values
     */
    public boolean rowContainsMissingValues(int rowIndex) throws DataException, IndexOutOfBoundsException {
        int rows = getLargestRows();
        if (rowIndex >= 0 && rowIndex < rows) {
            Data row = getRowSubset(rowIndex, rowIndex, true);
            for (int i = 0; i < row.getColumns(); i++) {
                if (row.column(i).getNonMissingRows() != row.column(i).getRows()) {
                    return true;
                }
            }
            return false;
        } else {
            throw new IndexOutOfBoundsException();
        }
    }

    /**
     * Add a singular value in a column to this data. This is a shortcut for
     * creating a column and adding data
     */
    public void addSingleValue(String columnName, String value) throws DataException {
        if (!readOnly) {
            StringColumn col = new StringColumn(columnName);
            col.appendStringValue(value);
            addColumn(col);
            indexColumn = null;
        } else {
            throw new DataException("Data is read-only");
        }
    }

    /**
     * Add a singular value in a column to this data. This is a shortcut for
     * creating a column and adding data
     */
    public void addSingleValue(String columnName, int value) throws DataException {
        if (!readOnly) {
            IntegerColumn col = new IntegerColumn(columnName);
            col.appendIntValue(value);
            addColumn(col);
        } else {
            throw new DataException("Data is read-only");
        }
    }

    /**
     * Add a singular value in a column to this data. This is a shortcut for
     * creating a column and adding data
     */
    public void addSingleValue(String columnName, long value) throws DataException {
        if (!readOnly) {
            IntegerColumn col = new IntegerColumn(columnName);
            col.appendLongValue(value);
            addColumn(col);
            indexColumn = null;
        } else {
            throw new DataException("Data is read-only");
        }
    }

    /**
     * Add a singular value in a column to this data. This is a shortcut for
     * creating a column and adding data
     */
    public void addSingleValue(String columnName, double value) throws DataException {
        if (!readOnly) {
            DoubleColumn col = new DoubleColumn(columnName);
            col.appendDoubleValue(value);
            addColumn(col);
            indexColumn = null;
        } else {
            throw new DataException("Data is read-only");
        }
    }

    /**
     * Add a singular value in a column to this data. This is a shortcut for
     * creating a column and adding data
     */
    public void addSingleValue(String columnName, Date value) throws DataException {
        if (!readOnly) {
            DateColumn col = new DateColumn(columnName);
            col.appendDateValue(value);
            addColumn(col);
            indexColumn = null;
        } else {
            throw new DataException("Data is read-only");
        }
    }

    /**
     * Recreate this object from an XmlDataStore
     */
    public void recreateObject(XmlDataStore store) throws XmlStorageException {
        int size = store.intValue("Columns", 0);
        setReadOnly(store.booleanValue("ReadOnly", false));
        columnVector.clear();
        Column col;
        for (int i = 0; i < size; i++) {
            col = (Column) store.xmlStorableValue("Column" + i);
            col.setParentData(this);
            columnVector.addElement(col);
        }

        if (store.containsName("IndexColumn")) {
            indexColumn = (Column) store.xmlStorableValue("IndexColumn");
            indexColumn.setParentData(this);
        } else {
            indexColumn = null;
        }

        if (store.containsName("Properties")) {
            properties = store.xmlDataStoreValue("Properties");
        } else {
            properties = null;
        }
    }

    /**
     * Save this object to an XmlDataStore
     */
    public XmlDataStore storeObject() throws XmlStorageException {
        XmlDataStore store = new XmlDataStore("Data");
        int size = columnVector.size();
        store.add("Columns", size);
        store.add("ReadOnly", readOnly);
        for (int i = 0; i < size; i++) {
            store.add("Column" + i, (Column) columnVector.elementAt(i));
        }

        if (indexColumn != null) {
            store.add("IndexColumn", indexColumn);
        }

        if (properties != null) {
            store.add("Properties", properties);
        }
        return store;
    }

    /**
     * Gets the 'name' of the data if it exists or null otherwise.
     *
     * @return name
     */
    public String getName() throws XmlStorageException {

        if (this.properties != null) {

            return this.properties.getPropertyString("dataName");
        } else {
            return null;
        }

    }

    /**
     * Sets the 'name' of the data/
     *
     * @param name the name of the data
     */
    public void setName(String name) throws XmlStorageException {

        if (this.properties != null) {
            this.properties.add("dataName", name);
        } else {
            throw new XmlStorageException("Data properties XMLStorage has not been created.");
        }


    }
}
