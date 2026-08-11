/*
 * Datei:            ArrayListBool.java
 * Autor(en):        Lukas König
 * Java-Version:     6.0
 * Erstellt:         22.03.2010
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

import java.util.ArrayList;
import java.util.List;

import eas.miscellaneous.StaticMethods;

/**
 * @author Lukas König
 */
public class ArrayListBool extends ArrayList<Boolean> {

    /**
     * Erzeugt eine Liste aus einer String-Liste.
     * 
     * @param array
     *            Der String-Vektor.
     */
    public ArrayListBool(final List<Boolean> liste) {
        if (liste != null) {
            for (Boolean s : liste) {
                this.add(s);
            }
        }
    }

    /**
     * Erzeugt eine Liste aus einem String-Vektor.
     * 
     * @param array
     *            Der String-Vektor.
     */
    public ArrayListBool(final boolean[] array) {
        if (array != null) {
            for (Boolean s : array) {
                this.add(s);
            }
        }
    }

    /**
     * Standardkonstruktor.
     */
    public ArrayListBool() {
        super();
    }

    /**
     * Konstruktor.
     * 
     * @param size
     *            Die erwartete Größe der Liste.
     */
    public ArrayListBool(final int size) {
        super(size);
    }

    /**
     * UID vom 22. März 2010.
     */
    private static final long serialVersionUID = 7480390940849225982L;

    /**
     * @return String-Ausgabe der Liste.
     */
    @Override
    public String toString() {
        return StaticMethods.ListToString(this);
    }
}