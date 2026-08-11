/*
 * Datei:            EarleyErkenner.java
 * Autor(en):        Lukas König
 * Java-Version:     1.4
 * Erstellt (vor):   14.06.2007
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
import java.util.LinkedList;

import eas.miscellaneous.StaticMethods;
import eas.startSetup.ParCollection;


/**
 * Implementierung eines Earley-Erkenners.
 *
 * @author Lukas König
 */
public class EarleyParser implements Serializable {

    /**
     * Generiert am 26.07.2007.
     */
    private static final long serialVersionUID = 8755143727130019355L;

    /**
     * Die zum Erkenner gehörende kontextfreie Grammatik.
     */
    private EarleyGrammar gramm;

    /**
     * Die "Predicted"-Matrx.
     */
    private boolean[][] predicted;

    /**
     * Die "Completed"-Matrix.
     */
    private boolean[][][] completed;

    /**
     * Das ChartArray t.
     */
    private LinkedList<EarleyTabRule>[] t;

    /**
     * Die Parameter.
     */
    private ParCollection pars;
    
    /**
     * Der Konstruktor, der die Grammatik initialisiert.
     *
     * @param grDatei  Name der Datei, aus der die Grammatik gelesen werden
     *                 soll.
     * @param params   Die Parameter.
     */
    public EarleyParser(
            final String grDatei,
            final ParCollection params) {
        this.gramm = new EarleyGrammar(grDatei);
        this.pars = params;
        this.init();
    }

    public EarleyParser(
            final EarleyGrammar grammar,
            final ParCollection params) {
        this.gramm = grammar;
        this.pars = params;
        this.init();
    }

    /**
     * Zerlegt einen String in Tokens, wobei <code>Konstanten.TRENN_SEQ2</code>
     * als Trennsymbol verwendet wird.
     *
     * @param text   Die zu zerlegende Sequenz.
     *
     * @return  Die Zerlegung in einer <code>ArrayList</code>.
     */
    @SuppressWarnings("deprecation")
    private ArrayList<String> zerlege(final String text) {
        return StaticMethods.zerlege(text, ConstantsEarley.TRENN_EING);
    }

    /**
     * Startet den Earley-Erkenner.
     *
     * @param s  Der zu parsende String.
     * 
     * @return  Ob <code>s</code> zur Grammatik von <code>this</code> gehört.
     */
    @SuppressWarnings("unchecked")
    public boolean erkenne(final String s) {
        ArrayList<String> sequenz = this.zerlege(s);
        int symbAnz = this.gramm.getNichtTerminale().size();
        int n = sequenz.size();
        String jInput;
        EarleyTabRule aktTabReg;
        EarleyRule aktRegel;

        this.predicted = new boolean[symbAnz][n + 1];
        this.completed = new boolean[symbAnz][n + 1][n + 1];
        this.t = new LinkedList[n + 1];

        t[0] = new LinkedList<EarleyTabRule>();
        this.predict(this.gramm.strSymb(), 0);
        for (int j = 1; j <= n; j++) {
            t[j] = new LinkedList<EarleyTabRule>();
            jInput = sequenz.get(j - 1);
            Iterator<EarleyTabRule> it = t[j - 1].iterator();
            while (it.hasNext()) {
                aktTabReg = it.next();
                aktRegel = aktTabReg.getRegel();
                if (aktTabReg.getY() == j - 1
                    && aktRegel.getNPunkt().size() > 0
                    && aktRegel.getNPunkt().get(0).equals(jInput)) {
                    aktRegel = (EarleyRule) aktRegel.clone();
                    aktRegel.verschPunktR();
                    this.add(aktRegel, aktTabReg.getX(), j);
                }
            }
        }

        Iterator<EarleyTabRule> it = t[n].iterator();
        while (it.hasNext()) {
            aktTabReg = it.next();
            if (aktTabReg.getX() == 0 && aktTabReg.getY() == n
                && aktTabReg.getRegel().getKopf().equals(this.gramm.strSymb())
                && aktTabReg.getRegel().getNPunkt().size() == 0) {
                this.init();
                return true;
            }
        }
        
        this.init();
        return false;
    }

    /**
     * Die add-Methode des Earley-Algorithmus.
     *
     * @param a  Die Earley-Regel, die hinzugefügt werden soll.
     * @param i  Der Tabellenplatz der Regel.
     * @param k  Der Tabellenplatz der Regel.
     */
    private void add(final EarleyRule a,
                     final int         i,
                     final int         k) {
        EarleyTabRule tabReg = new EarleyTabRule(i, k, a);
        EarleyRule reg = tabReg.getRegel();
        EarleyTabRule aktTabReg;
        EarleyRule aktReg;
        String symbB;
        boolean gefunden;

        if (ConstantsEarley.DEBUG) {
            StaticMethods.log(
                    StaticMethods.LOG_STAGE1,
                    "ADD (" + a + ", " + i + ", " + k + ")",
                    this.pars);
        }

        if (!this.t[k].contains(tabReg)) {
            t[k].add(tabReg);
            if (reg.getNPunkt().size() == 0) {  // Beta ist leeres Wort.
                this.complete(reg.getKopf(), i, k);
            } else if (this.gramm.istNTerm(reg.getNPunkt().get(0))) {
                symbB = reg.getNPunkt().get(0);
                Iterator<EarleyTabRule> it = this.t[k].iterator();
                gefunden = false;
                while (!gefunden && it.hasNext()) {
                    aktTabReg = it.next();
                    if (aktTabReg.getX() == k && aktTabReg.getY() == k
                        && aktTabReg.getRegel().getKopf().equals(symbB)
                        && aktTabReg.getRegel().getNPunkt().size() == 0) {
                        gefunden = true;
                    }
                }
                if (gefunden) {
                    aktReg = (EarleyRule) reg.clone();
                    aktReg.verschPunktR();
                    this.add(aktReg, i, k);
                } else {
                    this.predict(symbB, k);
                }
            }
        }
    }

    /**
     * Die predict-Methode des Earley-Algorithmus.
     *
     * @param symb  Das Symbol, das vorhergesehen werden soll.
     * @param i     Die Chartposition des Symbols.
     */
    private void predict(final String symb,
                         final int    i) {
        int a = this.gramm.symbNum(symb);
        Iterator<EarleyRule> it;
        EarleyRule aktReg;

        if (ConstantsEarley.DEBUG) {
            StaticMethods.log(
                    StaticMethods.LOG_STAGE1,
                    "PRD (" + symb + ", " + i + ")",
                    this.pars);
        }

        if (!this.predicted[a][i]) {
            this.predicted[a][i] = true;

            it = this.gramm.getRegeln().iterator();
            while (it.hasNext()) {
                aktReg = it.next();
                if (aktReg.getKopf().equals(symb)) {
                    this.add((EarleyRule) aktReg.clone(), i, i);
                }
            }
        }
    }

    /**
     * Die complete-Methode des Earley-Algorithmus.
     *
     * @param symb  Das Symbol, das komplettiert werden soll.
     * @param j     Der Tabellenplatz des Symbols.
     * @param k     Der Tabellenplatz des Symbols.
     */
    private void complete(final String symb,
                          final int    j,
                          final int    k) {
        int a = this.gramm.symbNum(symb);
        EarleyTabRule aktTabReg;
        EarleyRule aktReg;
        Iterator<EarleyTabRule> it;
        ArrayList<EarleyTabRule> erledigt = new ArrayList<EarleyTabRule>();

        if (ConstantsEarley.DEBUG) {
            StaticMethods.log(
                    StaticMethods.LOG_STAGE1,
                    "CMP (" + symb + ", " + j + ", " + k + ")",
                    this.pars);
        }

        if (!this.completed[a][j][k]) {
            this.completed[a][j][k] = true;

            it = this.t[j].iterator();
            while (it.hasNext()) {
                aktTabReg = it.next();
                aktReg = aktTabReg.getRegel();

                if (aktTabReg.getY() == j
                    && aktReg.getNPunkt().size() > 0
                    && aktReg.getNPunkt().get(0).equals(symb)
                    && !erledigt.contains(aktTabReg)) {
                    aktReg = (EarleyRule) aktReg.clone();
                    aktReg.verschPunktR();
                    this.add(aktReg, aktTabReg.getX(), k);
                    erledigt.add(aktTabReg);
                    it = this.t[j].iterator();
                }
            }
        }
    }

    /**
     * Setzt den Erkenner zurück.
     */
    private void init() {
        this.gramm.init();
        this.completed = null;
        this.predicted = null;
        this.t = null;
    }

    /**
     * Gibt eine Ausgabe der Grammatik des Erkenners zurück.
     *
     * @return  Die Ausgabe der Grammatik.
     */
    @Override
    public String toString() {
       return this.gramm.toString();
    }
}