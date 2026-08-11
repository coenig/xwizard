/*
 * File name:        RegExIndexer.java (package eas.miscellaneous.convenience)
 * Author(s):        hq0976
 * Java version:     8.0 (at generation time)
 * Generation date:  01.01.2017 (12:58:31)
 *
 * (c) This file and the EAS (Easy Agent Simulation) framework containing it
 * is protected by Creative Commons by-nc-sa license. Any altered or
 * further developed versions of this file have to meet the agreements
 * stated by the license conditions. 
 * 
 * In a nutshell
 * -------------
 * You are free:
 * - to Share -- to copy, distribute and transmit the work
 * - to Remix -- to adapt the work
 * 
 * Under the following conditions:
 * - Attribution -- You must attribute the work in the manner specified by the 
 *   author or licensor (but not in any way that suggests that they endorse 
 *   you or your use of the work).
 * - Noncommercial -- You may not use this work for commercial purposes.
 * - Share Alike -- If you alter, transform, or build upon this work, you may 
 *   distribute the resulting work only under the same or a similar license to 
 *   this one. 
 * 
 * + Detailed license conditions (Germany):
 *   http://creativecommons.org/licenses/by-nc-sa/3.0/de/
 * + Detailed license conditions (unported):
 *   http://creativecommons.org/licenses/by-nc-sa/3.0/deed.en
 * 
 * This header must be placed in the beginning of any version of this file.
 */

package eas.miscellaneous.convenience;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author hq0976, http://stackoverflow.com/questions/2560780/find-last-index-of-by-regex-in-java
 */
public class RegExIndexer {
    /**
     * Indicates that a String search operation yielded no results.
     */
    public static final int NOT_FOUND = -1;

    /**
     * Version of lastIndexOf that uses regular expressions for searching.
     * By Tomer Godinger.
     * 
     * @param str String in which to search for the pattern.
     * @param toFind Pattern to locate.
     * @return The index of the requested pattern, if found; NOT_FOUND (-1) otherwise.
     */
    public static int lastIndexOf(String str, String toFind)
    {
        Pattern pattern = Pattern.compile(toFind);
        Matcher matcher = pattern.matcher(str);

        // Default to the NOT_FOUND constant
        int lastIndex = NOT_FOUND;

        // Search for the given pattern
        while (matcher.find())
        {
            lastIndex = matcher.start();
        }

        return lastIndex;
    }

    /**
     * Finds the last index of the given regular expression pattern in the given string,
     * starting from the given index (and conceptually going backwards).
     * By Tomer Godinger.
     * 
     * @param str String in which to search for the pattern.
     * @param toFind Pattern to locate.
     * @param fromIndex Maximum allowed index.
     * @return The index of the requested pattern, if found; NOT_FOUND (-1) otherwise.
     */
    public static int lastIndexOf(String str, String toFind, int fromIndex)
    {
        // Limit the search by searching on a suitable substring
        return lastIndexOf(str.substring(0, fromIndex), toFind);
    }
}
