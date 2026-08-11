/*
 * Datei: JoSchKa.java
 * Autor(en):        Lukas König
 * Java-Version:     6.0
 * Erstellt:         22.04.2009
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
import java.util.ArrayList;

import eas.miscellaneous.StaticMethods;

/**
 * @author Lukas König
 */
public class JoSchKa implements Serializable {
    
    private static final long serialVersionUID = -3166434586339836036L;

    /**
     * @param setzPars      Die zu setzenden Parameter.
     * @param params        Die Parameter.
     * @param werte         Die Werte der zu setzenden Parameter.
     * @param setzParsFest  Parameter, die nicht mit allen anderen kombiniert,
     *                      sondern fortlaufen eingefügt werden.
     * @param werteFest     zugehörige Werte. Die Anzahl der Werte muss immer
     *                      genau die Anzahl der insgesamt zu erzeugenden 
     *                      Parametersätze sein.
     * 
     * @return  Ausgabe.
     */
    public ArrayList<String> erzeuge(
            final ParCollection params, 
            final ArrayList<String> setzPars,
            final ArrayList<String[]> werte,
            final ArrayList<String> setzParsFest,
            final ArrayList<String[]> werteFest,
            final int gesAnzahl) {
        ArrayList<String> ausgabe;
        int[] laengen = new int[setzPars.size()];
        int[] zaehler = new int[setzPars.size()];
        boolean b = false;
        String[] aenderungen 
            = new String[setzPars.size() * 2 + setzParsFest.size() * 2];
        int j;
        int gesLen = 1;
        int max = 0;
        int gesZaehler = 0;
        String aktSatz;
        String aktAusg;
        
        // Zählerarray initialisieren.
        for (int i = 0; i < laengen.length; i++) {
            laengen[i] = werte.get(i).length;
            zaehler[i] = 0;
            gesLen *= werte.get(i).length;
            if (laengen[i] > max) {
                max = laengen[i];
            }
        }

        ausgabe = new ArrayList<String>(gesLen);
        
        // Einträge erzeugen.
        while (!b && max > 1) {
            aktSatz = "";
            
            // Alle Kombinationen.
            for (int i = 0; i < zaehler.length; i++) {
                j = 2 * i;
                aenderungen[j] = setzPars.get(i);
                aenderungen[j + 1] = werte.get(i)[zaehler[i]];

                if (werte.get(i).length > 1) {
                    aktSatz += "-" + aenderungen[j];
                    aktSatz += "_" + aenderungen[j + 1];
                }
            }

            // Feste Werte.
            for (int i = zaehler.length; i * 2 < aenderungen.length; i++) {
                j = 2 * i;
                
                aenderungen[j] = setzParsFest.get(i - zaehler.length);
                aenderungen[j + 1] 
                            = werteFest.get(i - zaehler.length)[gesZaehler];
            }
            
            aktAusg = this.erzeugeEinzPar(params, aenderungen);
            aktAusg = aktAusg.replaceAll("#", aktSatz); // Ersetze "#".
            ausgabe.add(aktAusg);
            
            for (int i = 0; i < zaehler.length; i++) {
                zaehler[i] = (zaehler[i] + 1) % laengen[i];
                if (zaehler[i] != 0) {
                    break;
                }
            }
            
            b = true;
            for (int i = 0; i < laengen.length; i++) {
                if (zaehler[i] < laengen[i] - 1) {
                    b = false;
                }
            }
            
            gesZaehler++;
            
            StaticMethods.logInfo(
                    "Parameters generated: " 
                        + gesZaehler 
                        + " / " 
                        + (gesAnzahl - 1), 
                    params);
        }

        // Letzten Eintrag erzeugen.
        // Alle Kombinationen.
        aktSatz = "";
        for (int i = 0; i < zaehler.length; i++) {
            j = 2 * i;
            aenderungen[j] = setzPars.get(i);
            aenderungen[j + 1] = werte.get(i)[zaehler[i]];
            
            if (werte.get(i).length > 1) {
                aktSatz += "-" + aenderungen[j];
                aktSatz += "_" + aenderungen[j + 1];
            }
        }
        
        // Feste Werte.
        for (int i = zaehler.length; i * 2 < aenderungen.length; i++) {
            j = 2 * i;
            aenderungen[j] = setzParsFest.get(i - zaehler.length);
            aenderungen[j + 1] 
                        = werteFest.get(i - zaehler.length)[gesZaehler];
        }
        
        aktAusg = this.erzeugeEinzPar(params, aenderungen);        
        aktAusg = aktAusg.replaceAll("#", aktSatz); // Ersetze "#".
        ausgabe.add(aktAusg);

        return ausgabe;
    }

    /**
     * Erzeugt einen einzelnen Parametersatz mit den in <code>params</code>
     * gespeicherten Parametern und den angegebenen änderungen.
     * 
     * @param params       Die zugrundeliegenden Parameter. Kann 
     *                     <code>null</code> sein.
     * @param aenderungen  Die änderungen.
     * 
     * @return  Der einzelne Parametersatz als String.
     */
    public String erzeugeEinzPar(
            final ParCollection params,
            final String[] aenderungen) {
        ParCollection neu = new ParCollection(params);
        neu.complete();
        neu.overwriteParameterList(aenderungen);
        return neu.parStrPlain().replace('\n', ' ');
    }
    
    /**
     * @param anf      Der Anfangswert (inkl).
     * @param end      Der Endwert (inkl, falls nicht übersprungen).
     * @param schritt  Die Schrittweite.
     * 
     * @return  Die Zahlen innerhalb der Range.
     */
    public String[] erzeugeRange(
            final int anf, 
            final int end, 
            final int schritt) {
        String[] ausg = new String[(end - anf) / schritt + 1];
        int zaehler = 0;
        
        for (int i = anf; i <= end; i += schritt) {
            ausg[zaehler] = "" + i;
            zaehler++;
        }
        
        return ausg;
    }

    /**
     * @param anf      Der Anfangswert (inkl).
     * @param end      Der Endwert (inkl, falls nicht übersprungen).
     * @param schritt  Die Schrittweite.
     * 
     * @return  Die Zahlen innerhalb der Range.
     */
    public String[] erzeugeRangeDouble(
            final double anf, 
            final double end, 
            final double schritt) {
        String[] ausg = new String[(int) ((end - anf) / schritt + 1)];
        int zaehler = 0;
        
        for (double i = anf; i <= end; i += schritt) {
            ausg[zaehler] = "" + i;
            zaehler++;
        }
        
        return ausg;
    }

    /**
     * Erzeugt fertige Jobdatensätze für JoSchKa, die direkt in eine Job-Datei
     * eingefügt werden können. UserIdentifier und PreUserIdentifier werden 
     * automatisch eingefügt.
     * 
     * @param jobType           Jobverzeichnisbeschreibung.
     * @param platform          Plattform.
     * @param startKommando     Das Startkommando (Parameter werden angefügt).
     * @param resultFiles       Welche Dateien wieder zurückgeholt werden 
     *                          sollen.
     * @param maintainOutput    MainTainOutPut.
     * @param files             Hochzuladende Dateien.
     * @param mailNotification  Email-Benachrichtigung.
     * @param periodicUpload    Periodischer Upload.
     * @param param             Die Parameter für den Start.
     * 
     * @return       Die JoSchKa-Datensätze.
     */
    public ArrayList<String> erzeugeJoSchKa(
            final String jobType,
            final String platform,
            final String startKommando,
            final String resultFiles,
            final String maintainOutput,
            final String files,
            final String mailNotification,
            final String periodicUpload,
            final ArrayList<String> param) {
        ArrayList<String> joschka = new ArrayList<String>(param.size());
        
        for (int i = 0; i < param.size(); i++) {
            joschka.add("");
            joschka.set(i, joschka.get(i) + jobType);
            joschka.set(i, joschka.get(i) + "\t" + platform);
            joschka.set(
                    i, 
                    joschka.get(i) 
                        + "\t" 
                        + startKommando 
                        + " " 
                        + param.get(i));
            joschka.set(i, joschka.get(i) + "\t" + resultFiles);
            joschka.set(i, joschka.get(i) + "\t" + maintainOutput);
            joschka.set(i, joschka.get(i) + "\t" + files);
            joschka.set(i, joschka.get(i) + "\t" + mailNotification);
            joschka.set(i, joschka.get(i) + "\t" + periodicUpload);
            joschka.set(i, joschka.get(i) + "\t" + i); // UserIdentifier.
            joschka.set(i, joschka.get(i) + "\t"); // PreUserIdentifier.
        }
        
        return joschka;
    }

    /**
     * Erzeugt fertige Jobdatensätze für JoSchKa, die direkt in eine Job-Datei
     * eingefügt werden können. UserIdentifier und PreUserIdentifier sowie
     * alle Standardparameter werden automatisch eingefügt.
     * 
     * @param jobType           Jobverzeichnisbeschreibung.
     * @param startKommando     Das Startkommando (Parameter werden angefügt).
     * @param param             Die Parameter für den Start.
     * @param params            Der Parametersatz.
     * 
     * @return       Die JoSchKa-Datensätze.
     */
    public ArrayList<String> erzeugeJoSchKaStd(
            final String jobType,
            final String startKommando,
            final ArrayList<String> param,
            final ParCollection params) {
        return this.erzeugeJoSchKa(
                jobType, 
                params.getJoschkaPlatt(), 
                startKommando, 
                "*", 
                "no", 
//                "*.gra;*.tra;*.con;*.koo;*.bmp;*.jar;*.txt",
                "*",
                "no", 
                "yes", 
                param);
    }
}
