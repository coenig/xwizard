/*
 * Datei:          Konstanten.java
 * Autor(en):      Lukas König
 * Java-Version:   1.4
 * Erstellt (vor): 14.06.2007
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

/**
 * @author Lukas König
 */
public class ConstantsEarley {

    /**
     * Definiert die Trennsequenz zwischen zwei Tokens in der Grammatikdatei.
     * Kann insbesondere "" sein, falls jedes Zeichen als Token angesehen
     * werden soll.
     */
    protected static final String TRENN_GR = ",";

    /**
     * Definiert die Trennsequenz zwischen zwei Tokens in einer Eingabe.
     * Kann insbesondere "" sein, falls jedes Zeichen als Token angesehen
     * werden soll.
     */
    protected static final String TRENN_EING = ",";

    /**
     * Der Bezeichner für das leere Wort.
     */
    public static final String EPSILON = "epsilon";

    /**
     * Gibt an, ob der Debug-Modus aktiviert ist. Falls ja, wird ein Trace
     * des Erkenners ausgegeben. Die LOG-Stufe ist dabei "Stage1".
     */
    protected static final boolean DEBUG = true;

    /**
     * Name der Grammatikdatei, in der die Grammatik für ein Byte steht.
     */
    public static final String GRAMM_BY = "GRAMM_Byte.txt";

    /**
     * Name der Grammatikdatei, in der die Grammatik für eine Bedingung steht.
     */
    public static final String GRAMM_BE = "GRAMM_Bedingung.txt";

    /**
     * Name der Grammatikdatei, in der die Grammatik für eine Sequenz steht.
     */
    public static final String GRAMM_SE = "GRAMM_Sequenz.txt";
}
