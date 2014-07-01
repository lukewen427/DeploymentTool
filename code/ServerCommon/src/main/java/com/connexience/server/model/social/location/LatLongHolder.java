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
package com.connexience.server.model.social.location;

import java.io.Serializable;

/**
 * Author: Simon
 * Date: Jul 6, 2009
 */
public class LatLongHolder implements Serializable
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


    /*
     * Latitude of the point
     * */
    private double latitude;

    /*
     * Longitude of the point
     * */
    private double longitude;

    /*
     * The number of times this point occurs
     * */
    private Long numPoints = (long) 1;

    /*
     * An optional message associated with this point, e.g. log message text
     * */
    private String message;

    public LatLongHolder()
    {
    }

    public LatLongHolder(double latitude, double longitude, String message)
    {
        this.latitude = latitude;
        this.longitude = longitude;
        this.message = message;
    }

    public LatLongHolder(double latitude, double longitude, Long numPoints)
    {
        this.latitude = latitude;
        this.longitude = longitude;
        this.numPoints = numPoints;
    }

    public LatLongHolder(double latitude, double longitude, Long numPoints, String message)
    {
        this.latitude = latitude;
        this.longitude = longitude;
        this.numPoints = numPoints;
        this.message = message;
    }

    public double getLatitude()
    {
        return latitude;
    }

    public void setLatitude(double latitude)
    {
        this.latitude = latitude;
    }

    public double getLongitude()
    {
        return longitude;
    }

    public void setLongitude(double longitude)
    {
        this.longitude = longitude;
    }

    public Long getNumPoints()
    {
        return numPoints;
    }

    public void setNumPoints(Long numPoints)
    {
        this.numPoints = numPoints;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (!(o instanceof LatLongHolder)) return false;

        LatLongHolder latLong = (LatLongHolder) o;

        if (Double.compare(latLong.latitude, latitude) != 0) return false;
        if (Double.compare(latLong.longitude, longitude) != 0) return false;

        return true;
    }

    @Override
    public int hashCode()
    {
        int result;
        long temp;
        temp = latitude != +0.0d ? Double.doubleToLongBits(latitude) : 0L;
        result = (int) (temp ^ (temp >>> 32));
        temp = longitude != +0.0d ? Double.doubleToLongBits(longitude) : 0L;
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}
