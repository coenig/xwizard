/*
 * Datei:            Grammatik.java
 * Autor(en):        Lukas König
 * Java-Version:     1.4
 * Erstellt (vor): 08.06.2007
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

import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;

import eas.miscellaneous.StaticMethods;
import veryFastPDF.algorithms.grammars.Grammar;
import veryFastPDF.algorithms.grammars.Rule;
import veryFastPDF.algorithms.grammars.Symbol;


/**
 * Implementiert eine Datenstruktur für eine kontextfreie Grammatik in der
 * Form, wie sie der Earley-Parser benötigt (mit "Punkt"). Der Punkt befindet
 * sich immer am Anfang der Regeln. Die Grammatik sollte während
 * des Parsens nicht verändert werden.
 * Durch Aufruf der Methode <code>init()</code> kann die Grammatik wieder auf
 * den ursprünglichen Zustand gesetzt werden.
 *
 * @author Lukas König
 */
public class EarleyGrammar implements Serializable {

    /**
     * Generiert am 26.07.2007.
     */
    private static final long serialVersionUID = -8810080615113388683L;

    /**
     * Grammatikregeln vom Typ <code>EarleyRegel</code> der Grammatik.
     */
    private ArrayList<EarleyRule> regeln;

    /**
     * Die Terminalsymbole der Grammatik.
     */
    private ArrayList<String> terminale;

    /**
     * Die Nichtterminalsymbole der Grammatik.
     */
    private ArrayList<String> nichtTerminale;

    /**
     * Konstruktor, der eine Grammatik aus einer Datei einliest und alle Felder
     * initialisiert.
     *
     * @param dateiname  Der Name der Grammatikdatei.
     */
    public EarleyGrammar(final String dateiname) {
        this.liesGrammatik(dateiname);
        this.extrahiereSymbole(null);
    }

    public EarleyGrammar(final Grammar type0Grammar) {
        if (type0Grammar.retrieveHighestGrammarType() < 2) {
            throw new IllegalArgumentException("Cannot convert grammar of type lower than 2 into an Earley grammar.");
        }
        
        this.regeln = new ArrayList<EarleyRule>();
        
        for (Rule r : type0Grammar.getRules()) {
            ArrayList<String> rightSide = new ArrayList<String>(r.getImmutableRightSide().getSymbols().size());
            for (Symbol s : r.getImmutableRightSide().getSymbols()) {
                rightSide.add(s.toString());
            }
            
            EarleyRule rule = new EarleyRule(
                    r.getImmutableLeftSide().getSymbols().get(0).toString(), 
                    rightSide);
            this.regeln.add(rule);
        }
        
        this.extrahiereSymbole(type0Grammar.getStartSymbol().getSymbolAsString());
    }
    
    /**
     * Trägt die zu den Grammatikregeln gehörenden Terminal- und
     * Nichtterminalsymbole in die Felder <code>terminale</code>
     * und <code>nichtterminale</code> ein. Dabei muss bei allen Regeln der
     * Punkt ganz links stehen!
     * <P>
     * Nichtterminalsymbole sind alle Symbole, die mindestens einmal
     * auf der linken Seite einer Regel stehen. Terminalsymbole sind alle
     * übrigen Symbole. Als Startsymbol wird das Symbol definiert, das als
     * erstes in der Struktur steht, also
     * <code>this.nichtTerminale.get(0)</code>.
     * Dies entspricht auch dem ersten Symbol in der Grammatikdatei.
     */
    private void extrahiereSymbole(String startSymb) {
        Iterator<EarleyRule> it;
        Iterator<String> it2;
        EarleyRule aktR;
        String aktSymb;

        this.terminale = new ArrayList<String>();
        this.nichtTerminale = new ArrayList<String>();

        if (startSymb != null) {
            this.nichtTerminale.add(startSymb);
        }
        
        it = this.regeln.iterator();
        while (it.hasNext()) {
            aktR = it.next();
            if (!this.nichtTerminale.contains(aktR.getKopf())) {
                this.nichtTerminale.add(aktR.getKopf());
            }
        }

        it = this.regeln.iterator();
        while (it.hasNext()) {
            aktR = it.next();
            it2 = aktR.getNPunkt().iterator();
            while (it2.hasNext()) {
                aktSymb = it2.next();
                if (!this.nichtTerminale.contains(aktSymb)
                    && !this.terminale.contains(aktSymb)) {
                    this.terminale.add(aktSymb);
                }
            }
        }
    }

    /**
     * Liest eine Grammatik aus der angegebenen Textdatei und erzeugt eine
     * entsprechende Struktur in <code>this</code>. Dabei wird das Feld
     * <code>this.regeln</code> mit den Regeln aus der Grammatikdatei
     * initialisiert, nicht jedoch die Felder <code>this.terminale</code>
     * und <code>this.nichtTerminale</code>. Letztere müssen durch
     * nachtr�glichen Aufruf von <code>this.extrahiereSymbole</code>
     * initialisiert werden.
     *
     * @param name  Der Name der Grammatikdatei.
     */
    @SuppressWarnings("deprecation")
    private void liesGrammatik(final String name) {
        Iterator<String> it;
        String aktR;
        ArrayList<String> stringRegeln = new ArrayList<String>();
        EarleyRule aktRegel;
        ArrayList<String> rechts;
        String links;

        try {
            RandomAccessFile file = new RandomAccessFile(name, "r");
            while (file.getFilePointer() < file.length()) {
                stringRegeln.add(file.readLine());
            }
            file.close();
        } catch (final IOException e) {
            System.err.println(e);
        }

        this.regeln = new ArrayList<EarleyRule>();

        it = stringRegeln.iterator();
        while (it.hasNext()) {
            aktR = it.next();
            rechts = StaticMethods.zerlege(aktR, ConstantsEarley.TRENN_GR);
            links = rechts.get(0);
            rechts.remove(0);
            aktRegel = new EarleyRule(links, rechts);
            regeln.add(aktRegel);
        }
    }

    /**
     * Textausgabe einer Grammatik.
     *
     * @return  Die Textausgabe.
     */
    @Override
    public String toString() {
        String s = "";
        Iterator<EarleyRule> it = this.regeln.iterator();
        Iterator<String> it2;
        
        s = s + "Regeln:\n\n";
        while (it.hasNext()) {
            s = s + it.next().toSimpleString() + "\n";
        }

        s = s + "\nNichtterminale: ";
        it2 = this.nichtTerminale.iterator();
        while (it2.hasNext()) {
            String zwisch = it2.next();
            s = s + zwisch;
            if (zwisch.equals(this.strSymb())) {
                s = s + " (Startsymbol)";
            }
            if (it2.hasNext()) {
                s = s + ConstantsEarley.TRENN_EING + " ";
            }
        }

        s = s + "\nTerminale:      ";
        it2 = this.terminale.iterator();
        while (it2.hasNext()) {
            s = s + it2.next();
            if (it2.hasNext()) {
                s = s + ConstantsEarley.TRENN_EING + " ";
            }
        }

        return s;
    }

    /**
     * Gibt die Nummer des zu <code>symb</code> gehörenden Symbols zurück.
     * Dabei gilt, dass für jedes Symbol aus "Nichtterminale U Terminale" genau
     * eine positive Integer-Zahl zurückgegeben wird und für alle paarweise
     * verschiedenen Symbole auch unterschiedliche Zahlen zurückgegeben werden.
     * <P>
     * Falls Das übergebene Symbol nicht in der Grammatik existiert, wird -1
     * zurückgegeben.
     *
     * @param symb  Das Symbol, dessen Nummer berechnet werden soll.
     *
     * @return  Die Nummer des Symbols.
     */
    protected int symbNum(final String symb) {
        Iterator<String> it1 = this.nichtTerminale.iterator();
        Iterator<String> it2 = this.terminale.iterator();
        int i = 0;

        while (it1.hasNext()) {
            if (it1.next().equals(symb)) {
                return i;
            }
            i++;
        }

        while (it2.hasNext()) {
            if (it2.next().equals(symb)) {
                return i;
            }
            i++;
        }

        return -1;
    }

    /**
     * Gibt das Startsymbol zurück.
     *
     * @return  Das Startsymbol.
     */
    protected String strSymb() {
        return this.nichtTerminale.get(0);
    }

    /**
     * Ob ein Symbol ein Terminalsymbol ist.
     *
     * @param symb  Das zu überprüfende Symbol.
     *
     * @return  Ob Terminalsymbol.
     */
    private boolean istTerminal(final String symb) {
        Iterator<String> it = this.terminale.iterator();
        while (it.hasNext()) {
            if (it.next().equals(symb)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Ob ein Symbol ein Nichtterminalsymbol ist.
     *
     * @param symb  Das zu überprüfende Symbol.
     *
     * @return  Ob Nichtterminalsymbol.
     */
    protected boolean istNTerm(final String symb) {
        return !this.istTerminal(symb);
    }

    /**
     * @return Returns the nichtTerminale.
     */
    public ArrayList<String> getNichtTerminale() {
        return this.nichtTerminale;
    }

    /**
     * @return Returns the regeln.
     */
    public ArrayList<EarleyRule> getRegeln() {
        return this.regeln;
    }

    /**
     * @return Returns the terminale.
     */
    public ArrayList<String> getTerminale() {
        return this.terminale;
    }

// TODO UCdetector: Remove unused code: 
//     /**
//      * Verschiebt den Punkt einer Regel um eins nach rechts.
//      *
//      * @param r  Die Regel, deren Punkt verschoben werden soll.
//      */
//     public void verschPunktR(final EarleyRegel r) {
//         r.verschPunktR();
//     }

    /**
     * Setzt alle Regeln (ihre Punkte) auf den Anfangszustand zurück.
     */
    protected void init() {
        Iterator<EarleyRule> it = this.regeln.iterator();
        EarleyRule aktR;

        while (it.hasNext()) {
            aktR = it.next();
            aktR.init();
        }
    }
}
