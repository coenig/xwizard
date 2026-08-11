/*
 * Datei: Datatypes.java
 * Autor(en):        Lukas König
 * Java-Version:     6.0
 * Erstellt:         19.03.2010
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

package eas.startSetup.parameterDatatypes;

import java.util.Collection;

import eas.GlobalVariables;
import eas.math.geometry.Vector2D;
import eas.math.matrix.Matrix;

/**
 * Definition of datatypes (strings, in essence, denoting the datatypes'
 * type or range) which can be used to specify program parameters for the
 * <code>ParCollection</code>.
 * 
 * @author Lukas König
 */
public class Datatypes {

    public static final double ALL_TIME_STANDARD_DOUBLE = 0.0;
    public static final int ALL_TIME_STANDARD_INT = 0;
    public static final long ALL_TIME_STANDARD_LONG = 0;
    public static final String ALL_TIME_STANDARD_STRING = "-";
    public static final boolean ALL_TIME_STANDARD_BOOLEAN = true;
    public static final Vector2D ALL_TIME_STANDARD_VECTOR2D = new Vector2D(Vector2D.NULL_VECTOR);
    public static final String ALL_TIME_STANDARD_FIXSTRINGSET = "-";
    public static final ArrayListDouble ALL_TIME_STANDARD_DOUBLE_VECTOR = new ArrayListDouble();
    public static final ArrayListInt ALL_TIME_STANDARD_INT_VECTOR = new ArrayListInt();
    public static final ArrayListLong ALL_TIME_STANDARD_LONG_VECTOR = new ArrayListLong();
    public static final ArrayListString ALL_TIME_STANDARD_STRING_VECTOR = new ArrayListString();
    public static final ArrayListBool ALL_TIME_STANDARD_BOOLEAN_VECTOR = new ArrayListBool();
    public static final ArrayListVec2D ALL_TIME_STANDARD_VECTOR2D_VECTOR = new ArrayListVec2D();
    public static final Matrix ALL_TIME_STANDARD_MATRIX = new Matrix(0, 0);

    public static final String FIXED_STRING_SET_PREFIX = "fixedstringset";
    public static final String INTEGER_RANGE_PREFIX = "integerrange";
    public static final String DOUBLE_RANGE_PREFIX = "doublerange";
    
    /**
     * The "Fixed String Set" datatype.
     */
    public static String fixedStringSet(final String[] possibleValues) {
        String s = FIXED_STRING_SET_PREFIX + "-";
        
        for (String value : possibleValues) {
            if (value.contains("-")) {
                GlobalVariables.getParameters().logError(
                        "Values of fixed string set type may not contain dashes: " + value);
            }
            s += value + "-";
        }
        
        return s;
    }
    
    public static String fixedStringSet(final Collection<String> possibleValues) {
        String[] vals = new String[possibleValues.size()];
        
        int i = 0;
        for (String s : possibleValues) {
            vals[i] = s;
            i++;
        }
        
        return fixedStringSet(vals);
    }
    
    /**
     * The "Integer Range" datatype.
     */
    public static String integerRange(final int min, final int max) {
        String s = INTEGER_RANGE_PREFIX + "|" + min + "|" + max;
        return s;
    }
    
    public static String doubleRange(final double min, final double max, final int numDecimals) {
        String s = DOUBLE_RANGE_PREFIX + "|" + min + "|" + max + "|" + numDecimals;
        return s;
    }
    
    public static final String NULL_VALUE = "null";
    
    public static ArrayListString getNULLStringArray() {
        ArrayListString nullList = new ArrayListString();
        nullList.add(NULL_VALUE);
        return nullList;
    }

    /**
     * The Matrix datatype.
     */
    public static final String MATRIX = "matrix";
    
    /**
     * The Integer datatype.
     */
    public static final String INTEGER = "int";
    
    /**
     * The Long datatype.
     */
    public static final String LONG = "long";
    
    /**
     * The Double datatype.
     */
    public static final String DOUBLE = "double";
    
    /**
     * The String datatype.
     */
    public static final String STRING = "string";
    
    /**
     * The Boolean datatype.
     */
    public static final String BOOLEAN = "boolean";
    
    /**
     * The Vektor2D datatype.
     */
    public static final String VECTOR2D = "vector2d";

    /**
     * The Double-Array datatype.
     */
    public static final String DOUBLE_ARR = "double[ ]";
    
    /**
     * The Integer-Array datatype.
     */
    public static final String INTEGER_ARR = "int[ ]";
    
    /**
     * The Long-Array datatype.
     */
    public static final String LONG_ARR = "long[ ]";
    
    /**
     * The String-Array datatype.
     */
    public static final String STRING_ARR = "string[ ]";
    
    /**
     * The Boolean-Array datatype.
     */
    public static final String BOOLEAN_ARR = "boolean[ ]";
    
    /**
     * The Boolean-Array datatype.
     */
    public static final String VECTOR2D_ARR = "vector2d[ ]";
}
