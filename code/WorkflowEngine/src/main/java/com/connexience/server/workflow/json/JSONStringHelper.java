package com.connexience.server.workflow.json;


public class JSONStringHelper
{
    static String unescapeString(String escapedString)
    {
        if (escapedString == null) {
            return null;
        } else {
            int len = escapedString.length();
            StringBuilder sb = new StringBuilder(len);
            boolean escape = false;

            for (int i = 0; i < len; i++) {
                char c = escapedString.charAt(i);
                switch (c) {
                case '\\':
                    if (escape) {
                        sb.append('\\');
                        escape = false;
                    } else {
                        escape = true;
                    }
                    break;
                case 't':
                    if (escape) {
                        sb.append('\t');
                        escape = false; 
                    } else {
                        sb.append(c);
                    }
                    break;
                case 'n':
                    if (escape) {
                        sb.append('\n');
                        escape = false;
                    } else {
                        sb.append(c);
                    }
                    break;
                case 'r':
                    if (escape) {
                        sb.append('\r');
                        escape = false;
                    } else {
                        sb.append(c);
                    }
                    break;
                default:
                    sb.append(c);
                    break;
                }
            }

            // A special case for a single backslash at the end of the string.
            if (escape) {
                sb.append('\\');
            }

            return sb.toString();
        }
    }
    
    static String escapeString(String unescapedString)
    {
        if (unescapedString == null) {
            return null;
        } else {
            int len = unescapedString.length();
            StringBuilder sb = new StringBuilder(len);
            for (int i = 0; i < len; i++) {
                char c = unescapedString.charAt(i);
                switch (c) {
                case '\t':
                    sb.append('\\');
                    sb.append('t');
                    break;
                case '\r':
                    sb.append('\\');
                    sb.append('r');
                    break;
                case '\n':
                    sb.append('\\');
                    sb.append('n');
                    break;
                default:
                    sb.append(c);
                    break;
                }
            }

            return sb.toString();
        }
    }
}
