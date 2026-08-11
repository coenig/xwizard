/*
 * Datei:        PopVert.java
 * Autor(en):    Lukas König
 * Java-Version: 6.0
 * Erstellt:     27.03.2010
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

package eas.miscellaneous.datatypes;

/**
 * Ein Array aus zwei Integerwerten
 * 
 * @author Lukas König
 */
public class Integer2D {
    
    /**
     * X-Wert.
     */
    public int gute;
    
    /**
     * Y-Wert.
     */
    public int schlechte;

    /**
     * Konstruktor.
     * 
     * @param gute       Wert 1.
     * @param schlechte  Wert 2.
     */
    public Integer2D(final int gute, final int schlechte) {
        this.gute = gute;
        this.schlechte = schlechte;
    }
    
    public static Integer2D parseInteger2D(String s) {
        Integer2D t;
        
        try {
            t = new Integer2D(Integer.parseInt(s.split("/")[0]),
                    Integer.parseInt(s.split("/")[1]));
        } catch (Exception e) {
            t = null;
        }
        
        return t;
    }
    
    /**
     * @return  Der String.
     */
    @Override
    public String toString() {
        return "" + this.gute + "/" + this.schlechte + "";
    }

    /**
     * @return  HashCode.
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + this.gute;
        result = prime * result + this.schlechte;
        return result;
    }

    /**
     * @param obj  Das zu vergleichende Objekt.
     * 
     * @return Equals.
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Integer2D other = (Integer2D) obj;
        if (this.gute != other.gute)
            return false;
        if (this.schlechte != other.schlechte)
            return false;
        return true;
    }
}

