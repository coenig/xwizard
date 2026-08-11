/*
 * Datei:            AbstractMessage.java
 * Autor(en):        Lukas König
 * Java-Version:     6.0
 * Erstellt:         22.02.2009
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

package eas.startSetup.logging;

import java.io.Serializable;

/**
 * @author Lukas König
 */
public abstract class AbstractMsg implements Serializable {

    /**
     * Die Default-ID.
     */
    private static final long serialVersionUID = 1L;

    /**
     * Die Nachricht.
     */
    private String message;
    
    /**
     * Die exakte Zeit der Nachricht.
     */
    private long datum;
    
    /**
     * Ein beliebiges Objekt, das die Nachricht genauer spezifiziert.
     */
    private Object zusatz;
    
    /**
     * Der konstruktor.
     * 
     * @param msg     Die Nachricht.
     * @param dat     Die exakte Zeit derNachricht.
     * @param zus  Ein beliebiges Objekt, das die Nachricht genauer 
     *                spezifiziert.
     */
    public AbstractMsg(
            final String msg, 
            final long dat,
            final Object zus) {
        this.message = msg;
        this.datum = dat;
        this.zusatz = zus;
    }

    /**
     * @return Returns the message.
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * @return Returns the datum.
     */
    public long getDatum() {
        return this.datum;
    }
    
    /**
     * @return Returns the type.
     */
    public abstract String getType();
    
    /**
     * @return  String.
     */
    @Override
    public String toString() {
        return this.getType() 
            + ": (" 
            + this.message 
            + " / " 
            + this.datum 
            + ")";
    }

    /**
     * @return  Der Zusatz.
     */
    public Object getZusatz() {
        return this.zusatz;
    }
    
    /**
     * @return  Die Stufe der Nachricht.
     */
    public abstract int getStufe();
}
