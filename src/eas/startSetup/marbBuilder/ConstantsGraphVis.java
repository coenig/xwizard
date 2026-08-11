/*
 * Dateiname:      Konstanten.java
 * Autor(en):      Lukas König
 * Java-Version:   1.4
 * Erstellt (vor): 14.09.2006
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

package eas.startSetup.marbBuilder;

import java.awt.Color;

/**
 * Konstanten des Pakets graphVis.
 *
 * @author Lukas König
 */
public class ConstantsGraphVis {
    
    /**
     * Ob automatisch beim Speichern eines Automaten auch eine Bilddatei mit
     * dem abgebildeten Automaten im PNG-Format gespeichert werden soll.
     * Der Standardwert ist <code>true</code>, aber durch die SET-Methode im
     * Parametersatz kann der Wert zur Laufzeit angepasst werden.
     */
    public static final boolean PNG_AUT_SP = true;
    
    /**
     * Standardpfad zum Speichern der Graphen.
     */
    public static final String STD_PFAD = "sharedDirectory";

    /**
     * Der Standard-Quelldateienpfad für den Starter.
     */
    public static final String STD_STARTER_PFAD = "sharedDirectory";
    
    /**
     * Dateiname des Graphen.
     */
    public static final String STD_GRAPH_NAME = "graph";

    /**
     * Die Endung von Bilddateien des Typs png, der zur Speicherung von
     * Automatenbildern verwendet wird.
     */
    public static final String PNG_ENDUNG = "png";
    
    /**
     * Dateiendung für Automatendateien.
     */
    public static final String GRAPH_ENDUNG = "gra";

    /**
     * Dateiendung für Translatordateien. Translatoren unterscheiden
     * sich syntaktisch nicht von Verhaltensautomaten; bei der Speicherung
     * wird nur anhand dieser Deteiendung zwischen ihnen unterschieden.
     */
    public static final String TRANS_ENDUNG = "tra";
    
    /**
     * Dateiendung für Bedingungen.
     */
    public static final String BED_ENDUNG = "con";
    
    /**
     * Dateiendung für temporäre Dateien.
     */
    public static final String TEMP_ENDUNG = "tmp";
    
    /**
     * Dateiendung der Koordinaten.
     */
    public static final String KOORD_ENDUNG = "koo";

    /**
     * Grenzen des Zeichenbereichs.
     */
    public static final int ZEICHENBEREICH_X1 = 30;

    /**
     * Grenzen des Zeichenbereichs.
     */
    public static final int ZEICHENBEREICH_X2 = 700;

    /**
     * Grenzen des Zeichenbereichs.
     */
    public static final int ZEICHENBEREICH_Y1 = 190;

    /**
     * Grenzen des Zeichenbereichs.
     */
    public static final int ZEICHENBEREICH_Y2 = 630;

    /**
     * Farbe für allgemeine Hintergrundobjekte.
     */
    public static final Color HINTERGRUND_FARBE = Color.WHITE;

    /**
     * Farbe für allgemeine Linien, Rahmen, etc.
     */
    public static final Color VORDERGRUND_FARBE = Color.BLACK;
}
