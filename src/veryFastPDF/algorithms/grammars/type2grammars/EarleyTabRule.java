/*
 * Datei:            EarleyTabRegel.java
 * Autor(en):        Lukas König
 * Java-Version:     1.4
 * Erstellt (vor): 10.06.2007
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

package veryFastPDF.algorithms.grammars.type2grammars;

import java.io.Serializable;

/**
 * Eine EarleyRegel in der Parse-Tabelle.
 *
 * @author Lukas König
 */
class EarleyTabRule implements Serializable {

    /**
     * Generiert am 26.07.2007.
     */
    private static final long serialVersionUID = -7972421820752876552L;

    /**
     * Die X-Koordinate der Regel in der Tabelle.
     */
    private int x;

    /**
     * Die Y-Koordinate der Regel in der Tabelle.
     */
    private int y;

    /**
     * Die Regel, die zu dem Tabelleneintrag gehört.
     */
    private EarleyRule regel;

    /**
     * Konstruktor.
     *
     * @param xK   Die X-Koordinate der Regel in der Tabelle.
     * @param yK   Die Y-Koordinate der Regel in der Tabelle.
     * @param reg  Die Regel, die zu dem Tabelleneintrag gehört.
     */
    protected EarleyTabRule(final int xK,
                          final int yK,
                          final EarleyRule reg) {
        this.x = xK;
        this.y = yK;
        this.regel = reg;
    }

    /**
     * Erzeugt eine Kopie von <code>this</code> und gibt sie zurück.
     *
     * @return  Eine Kopie von <code>this</code>.
     */
    @Override
    protected Object clone() {
        EarleyTabRule retReg = new EarleyTabRule(
                                             this.x,
                                             this.y,
                                             (EarleyRule) this.regel.clone());

        return retReg;
    }

    /**
     * Die equals-Methode.
     *
     * @param arg0  Das Objekt, mit dem verglichen werden soll.
     *
     * @return  Ob die Objekte gleich sind.
     */
    @Override
    public boolean equals(final Object arg0) {
        EarleyTabRule andere;

        if (arg0 == this) {
            return true;
        }

        if (arg0 == null) {
            return false;
        } else if (arg0.getClass() != this.getClass()) {
            return false;
        } else {
            andere = (EarleyTabRule) arg0;
            return this.x == andere.x
                   && this.y == andere.y
                   && this.regel.equals(andere.regel);
        }
    }

    /**
     * Die hashCode-Methode.
     *
     * @return  Der Hashcode.
     */
    @Override
    public int hashCode() {
        return this.regel.hashCode() + this.x + this.y;
    }

    /**
     * @return Returns the regel.
     */
    public EarleyRule getRegel() {
        return this.regel;
    }

    /**
     * @return Returns the x.
     */
    public int getX() {
        return this.x;
    }

    /**
     * @return Returns the y.
     */
    public int getY() {
        return this.y;
    }

    /**
     * Die Textausgabe.
     *
     * @return  Die Textausgabe von <code>this</code>.
     */
    @Override
    public String toString() {
        return "<" + this.regel + ", " + this.x + ", " + this.y + ">";
    }
}
