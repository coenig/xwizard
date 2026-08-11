/*
 * Datei:            EarleyRegel.java
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Lukas König
 */
class EarleyRule implements Serializable {

    /**
     * Generiert am 26.07.2007.
     */
    private static final long serialVersionUID = -9077195777726691312L;

    /**
     * Der Kopf (linke Seite) der Regel.
     */
    private String kopf;

    /**
     * Die Symbole vor dem Punkt.
     */
    private List<String> vPunkt;

    /**
     * Die Symbole nach dem Punkt.
     */
    private List<String> nPunkt;

    /**
     * Der Konstruktor der Regel.
     *
     * @param linkeSeite   Linke Seite oder Kopf der Regel.
     * @param rechteSeite  Rechte Seite der Regel.
     */
    protected EarleyRule(
            final String linkeSeite, 
            final List<String> rechteSeite) {
        this.kopf = linkeSeite;
        this.vPunkt = new ArrayList<String>();
        this.nPunkt = rechteSeite;
    }

    /**
     * Verschiebt den Punkt um eine Stelle nach rechts.
     */
    protected void verschPunktR() {
        if (this.nPunkt.size() > 0) {
            this.vPunkt.add(this.nPunkt.get(0));
            this.nPunkt.remove(0);
        } else {
            throw new RuntimeException("Punkt ist bereits ganz rechts.");
        }
    }

// TODO UCdetector: Remove unused code: 
//     /**
//      * Gibt zurück, ob die Regel aktiv ist.
//      *
//      * @return  Ob aktiv.
//      */
//     public boolean aktiv() {
//         return this.nPunkt.size() > 0;
//     }

    /**
     * Setzt den Punkt dieser Regel auf die linkeste Position.
     */
    protected void init() {
        this.nPunkt.addAll(0, this.vPunkt);
        this.vPunkt = new ArrayList<String>();
    }

    /**
     * Textausgabe einer Regel.
     *
     * @return  Die Textausgabe.
     */
    @Override
    public String toString() {
        String s = "";

        s = s + this.kopf + " -> " + this.vPunkt + " . " + this.nPunkt;

        return s;
    }

    /**
     * Textausgabe einer Regel ohne Punkt (und ohne Teile VOR dem Punkt!).
     *
     * @return  Die Textausgabe.
     */
    protected String toSimpleString() {
        String s = "";

        s = s + this.kopf + " -> " + this.nPunkt;

        return s;
    }

    /**
     * Erzeugt eine Kopie von <code>this</code> und gibt sie zurück.
     *
     * @return  Eine Kopie von <code>this</code>.
     */
    @Override
    public Object clone() {
        EarleyRule retReg = new EarleyRule(null, null);
        String aktV;
        String aktN;
        Iterator<String> it;

        retReg.kopf = new String(this.kopf);
        retReg.vPunkt = new ArrayList<String>();
        retReg.nPunkt = new ArrayList<String>();

        it = this.vPunkt.iterator();
        while (it.hasNext()) {
            aktV = new String(it.next());
            retReg.vPunkt.add(aktV);
        }

        it = this.nPunkt.iterator();
        while (it.hasNext()) {
            aktN = new String(it.next());
            retReg.nPunkt.add(aktN);
        }

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
        EarleyRule andere;

        if (arg0 == this) {
            return true;
        }

        if (arg0 == null) {
            return false;
        } else if (arg0.getClass() != this.getClass()) {
            return false;
        } else {
            andere = (EarleyRule) arg0;
            return (this.kopf.equals(andere.kopf)
                && this.nPunkt.equals(andere.nPunkt)
                && this.vPunkt.equals(andere.vPunkt));
        }
    }

    /**
     * Die hashCode-Methode.
     *
     * @return  Der Hashcode.
     */
    @Override
    public int hashCode() {
        return this.kopf.hashCode()
               + this.nPunkt.hashCode()
               + this.vPunkt.hashCode();
    }

    /**
     * @return Returns the kopf.
     */
    public String getKopf() {
        return this.kopf;
    }

    /**
     * @return Returns the nPunkt.
     */
    public List<String> getNPunkt() {
        return this.nPunkt;
    }

    /**
     * @return Returns the vPunkt.
     */
    public List<String> getVPunkt() {
        return this.vPunkt;
    }
}
