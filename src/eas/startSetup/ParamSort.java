/*
 * Datei: alphaSort.java
 * Autor(en):        Lukas König
 * Java-Version:     6.0
 * Erstellt:         26.06.2009
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

package eas.startSetup;

import java.io.Serializable;
import java.util.Comparator;


/**
 * Comparator für eine alphanumerische Sortierung bei Strings
 * (Sortiert werden können alle Objekte über ihre toString()-Methode).
 * 
 * @author Lukas König
 */
public class ParamSort implements Comparator<SingleParameter>, Serializable {

    private static final long serialVersionUID = -6553330400587971438L;

    /**
     * Die Vergleichsmethode. Achtung: compare(x, y) == 0 impliziert NICHT,
     * dass x.equals(y) == true!
     * 
     * @param arg0  Das erste zu vergleichende Element.
     * @param arg1  Das zweite zu vergleichende Element.
     * 
     * @return  -1, falls arg0 < arg1;
     *          0, falls arg0 == arg1;
     *          1, falls arg0 > arg1;
     */
    @Override
    public int compare(final SingleParameter arg0, final SingleParameter arg1) {
        if (arg0.getParameterCategory().equals(arg1.getParameterCategory())) {
            return arg0.getParameterName().compareTo(arg1.getParameterName());
        } else if (arg0.getParameterCategory().equals("SIMULATION")) {
            return -1;
        } else if (arg1.getParameterCategory().equals("SIMULATION")) {
            return 1;
        } else if (arg0.getParameterCategory().equals("PLUGINS")) {
            return -1;
        } else if (arg1.getParameterCategory().equals("PLUGINS")) {
            return 1;
        } 
        
        return arg0.getParameterCategory().compareTo(arg1.getParameterCategory());
    }
}
