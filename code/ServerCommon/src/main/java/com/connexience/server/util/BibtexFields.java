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

/**
 * Author: Simon
 * Date: Sep 3, 2008
 * Fields taken from http://bibliographic.openoffice.org/bibtex-defs.html
 */
public class BibtexFields
{

  private static final String[] rArticle = new String[]{"author", "title", "journal", "year"};
  private static String[] oArticle = new String[]{"volume", "number", "pages", "month", "note"};

  private static final String[] rBook = new String[]{"editor", "title", "publisher", "year"};
  private static String[] oBook = new String[]{"volume", "series", "address", "edition", "month", "note"};

  private static final String[] rBooklet = new String[]{"title"};
  private static String[] oBooklet = new String[]{"author", "howpublished", "address", "month", "year", "note"};

  private static final String[] rInBook = new String[]{"title", "author", "chapter", "pages", "year"};
  private static String[] oInBook = new String[]{"volume", "series", "type", "address", "month", "edition", "note"};

  private static final String[] rInCollection = new String[]{"title", "author", "booktitle", "publisher", "year"};
  private static String[] oInCollection = new String[]{"editor", "volume", "series", "type", "chapter", "pages", "address", "month", "edition", "note"};

  private static final String[] rInProceedings = new String[]{"title", "author", "booktitle", "year"};
  private static String[] oInProceedings = new String[]{"editor", "volume", "series", "organization", "pages", "address", "month", "publisher", "note"};

  private static final String[] rManual = new String[]{"title"};
  private static String[] oManual = new String[]{"author", "organization", "adress", "edition", "month", "year", "note"};

  private static final String[] rMastersThesis = new String[]{"title", "author", "school", "year"};
  private static String[] oMastersThesis = new String[]{"type", "adress", "month", "note"};

  private static final String[] rMisc = new String[]{};
  private static String[] oMisc = new String[]{"author", "title", "howpublished", "month", "note"};

  private static final String[] rPhdThesis = new String[]{"title", "author", "school", "year"};
  private static String[] oPhdThesis = new String[]{"type", "adress", "month", "note"};

  private static final String[] rProceedings = new String[]{"title", "year"};
  private static String[] oProceedings = new String[]{"editor", "volume", "series", "eddress", "month", "organization", "publisher", "note"};

  private static final String[] rTechReport = new String[]{"author", "title", "institutiion", "year"};
  private static String[] oTechReport = new String[]{"type", "number", "eddress", "month", "note"};

  private static final String[] rUnpublished = new String[]{"author", "title", "note"};
  private static String[] oUnpublished = new String[]{"month", "year"};


  public static String[] getRequiredFields(String type) throws BibtexException
  {
    if (type.equalsIgnoreCase("article"))
      return rArticle;
    else if (type.equalsIgnoreCase("book"))
      return rBook;
    else if (type.equalsIgnoreCase("booklet"))
      return rBooklet;
    else if (type.equalsIgnoreCase("inbook"))
      return rInBook;
    else if (type.equalsIgnoreCase("incollection"))
      return rInCollection;
    else if (type.equalsIgnoreCase("inproceedings"))
      return rInProceedings;
    else if (type.equalsIgnoreCase("manual"))
      return rManual;
    else if (type.equalsIgnoreCase("mastersthesis"))
      return rMastersThesis;
    else if (type.equalsIgnoreCase("misc"))
      return rMisc;
    else if (type.equalsIgnoreCase("phdthesis"))
      return rPhdThesis;
    else if (type.equalsIgnoreCase("proceedings"))
      return rProceedings;
    else if (type.equalsIgnoreCase("techreport"))
      return rTechReport;
    else if (type.equalsIgnoreCase("unpublished"))
      return rUnpublished;
    else
      throw new BibtexException("unsupported bibtex format");
  }

  public static String[] getOptionalFields(String type) throws BibtexException
  {
    if (type.equalsIgnoreCase("article"))
      return oArticle;
    else if (type.equalsIgnoreCase("book"))
      return oBook;
    else if (type.equalsIgnoreCase("booklet"))
      return oBooklet;
    else if (type.equalsIgnoreCase("inbook"))
      return oInBook;
    else if (type.equalsIgnoreCase("incollection"))
      return oInCollection;
    else if (type.equalsIgnoreCase("inproceedings"))
      return oInProceedings;
    else if (type.equalsIgnoreCase("manual"))
      return oManual;
    else if (type.equalsIgnoreCase("mastersthesis"))
      return oMastersThesis;
    else if (type.equalsIgnoreCase("misc"))
      return oMisc;
    else if (type.equalsIgnoreCase("phdthesis"))
      return oPhdThesis;
    else if (type.equalsIgnoreCase("proceedings"))
      return oProceedings;
    else if (type.equalsIgnoreCase("techreport"))
      return oTechReport;
    else if (type.equalsIgnoreCase("unpublished"))
      return oUnpublished;
    else
      throw new BibtexException("unsupported bibtex format");
  }


}
