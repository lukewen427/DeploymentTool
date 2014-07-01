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

import java.util.regex.Pattern;

/**
 * This class provides wildcard matching
 *
 * @author hugo
 */
public abstract class WildcardUtils
{
    /**
     * This is a convenience method equivalent to call <code>createRegexFromGlob(pattern).matcher(text).matches()</code>.
     * 
     * If you intend to call it with the same pattern multiple times, it's more efficient to use
     * {@link #createRegexFromGlob(String) createRegexFromGlob(pattern)} to generate a {@link Pattern} once and then <code>{@link Pattern#matcher(CharSequence)} to
     * match text multiple times. 
     * 
     * @param text
     * @param pattern
     * @return
     */
    public static boolean wildCardMatch(String text, String pattern)
    {
        return createRegexFromGlob(pattern).matcher(text).matches();
    }


    /**
     * <p>This method creates a Java regular expression pattern from a glob pattern used to describe POSIX filenames (e.g. *.txt, [a-z]*.txt).</p>
     * 
     * <p>Thanks to Dave Ray and Paul Tombin for their 
     * <a href="http://stackoverflow.com/questions/1247772/is-there-an-equivalent-of-java-util-regex-for-glob-type-patterns">stackoverflow answer</a>
     * modified to simplify handling of '{', '}' and ','.</p>
     * 
     * <p>TODO: test it thoroughly and check conformance with POSIX 
     * <a href="http://www.opengroup.org/onlinepubs/009695399/utilities/xcu_chap02.html#tag_02_13">pattern matching notation</a>.</p>
     * 
     * @param glob
     * @return
     */
    public static Pattern createRegexFromGlob(String glob)
    {
        int len = glob.length();
        StringBuilder sb = new StringBuilder(len);
        sb.append('^');
        boolean escaping = false;

        for (int i = 0; i < len; i++) {
            char c = glob.charAt(i);
            switch (c) {
            case '*': 
                sb.append(escaping ? "\\*" : ".*");
                escaping = false;
                break;
            case '?': 
                sb.append(escaping ? "\\?" : ".");
                escaping = false;
                break;
            case '.':
            case '(':
            case ')':
            case '+':
            case '|':
            case '^':
            case '$':
            case '@':
            case '%':
            case '{':
            case '}':
            case ',':
                sb.append("\\");
                sb.append(c);
                escaping = false;
                break;
            case '\\':
                if (escaping) {
                    sb.append("\\\\");
                    escaping = false;
                } else {
                    escaping = true;
                }
                break;
            default:
                sb.append(c);
                escaping = false;
                break;
            }
        }
        sb.append('$');

        return Pattern.compile(sb.toString());
    }


    /*
    This method matches wrong patterns: e.g. pattern = '*.bbb' matches text = 'aaa.bbb' but also 'aaa.bbb.ccc', which is not desired/correct.
    Also it is too simplistic as it doesn't allow for valid glob patterns like 'a?a' or 'a[ab]c'.

    public static boolean wildCardMatch(String text, String pattern) {
        // Create the cards by splitting using a RegEx. If more speed 
        // is desired, a simpler character based splitting can be done.
        String[] cards = pattern.split("\\*");

        // Iterate over the cards.
        for (String card : cards) {
            int idx = text.indexOf(card);

            // Card not detected in the text.
            if (idx == -1) {
                return false;
            }

            // Move ahead, towards the right of the text.
            text = text.substring(idx + card.length());
        }

        return true;
    }
    */
    
}