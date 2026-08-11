/*
 * File name:        ChartParser.java (package veryFastPDF.algorithms.grammars.type2grammars.charty)
 * Author(s):        Leo Woerteler, Lukas König
 * Java version:     8.0 (at generation time)
 * Generation date:  ?
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

package veryFastPDF.algorithms.grammars.type2grammars.charty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import eas.miscellaneous.convenience.GeneralDialog;
import veryFastPDF.algorithms.grammars.Grammar;
import veryFastPDF.algorithms.grammars.type2grammars.charty.semirelevantChartyStuff.ParserException;
import veryFastPDF.algorithms.grammars.type2grammars.charty.semirelevantChartyStuff.ParserInfoListener;

/**
 * This class implements a chart parser.
 *
 * @author Leo Woerteler, Lukas König
 */
public final class ChartParser {

    /** Grammar. */
    private final CtxtFreeGrammar grammar;
    /** Tokens to parse. */
    private final String[] tokens;

    /** Optional listener for parser events. */
    private final ParserInfoListener listener;

    /** List of edges. */
    private final List<Edge> chart = new ArrayList<Edge>();

    /**
     * Constructor taking the grammar an tokens to parse.
     * 
     * @param g
     *            grammar
     * @param tok
     *            tokens to parse
     * @param list
     *            info listener, may be {@code null}
     */
    ChartParser(final CtxtFreeGrammar g, final String[] tok,
            final ParserInfoListener list, Grammar father) {
        grammar = g;
        tokens = tok;
        listener = list;
        this.fatherGr = father;
    }

    private Grammar fatherGr;
    
    /**
     * Parses a sequence of tokens.
     * 
     * @param g
     *            grammar definition
     * @param tok
     *            tokens to parse
     * @param listener
     *            info listener, may be {@code null}
     * @return generated parse trees
     * @throws ParserException
     *             if the parser isn't successful
     */
    public static ParseTree[] parse(final CtxtFreeGrammar g, final String[] tok,
            final ParserInfoListener listener, Grammar father) 
                    throws ParserException {
        final List<ParseTree> trees = new ChartParser(g, g.getTerminalDummyTokenString(tok), listener, father).parse();
        return trees.toArray(new ParseTree[trees.size()]);
    }

    /**
     * Performs the parse.
     *
     * @return list of parse trees
     * @throws ParserException in case of errors
     */
    private List<ParseTree> parse() throws ParserException {
        GeneralDialog.resetLongTimeOperationID("Grammar-ctxtfree-long-time-ID");

        int pos = 0;
        for (int i = 0; i < getTokens().length; i++) {
            final String tok = getTokens()[i];
            // initialize with input token
            for (final String lhs : grammar.getLHS(tok)) {
                doLongtimeOperationCheck();
                chart.add(new Edge(i, i + 1, 1, lhs, new String[] { tok },
                        new ArrayList<Edge>(), fatherGr, this, false, false));
                log("I", "Adding edge: " + chart.get(chart.size() - 1));
            }

            boolean change;
            do {
                doLongtimeOperationCheck();
                final int len = chart.size();
                change = ruleInvocation(pos);
                pos = len;
                change |= fundamentalRule();
            } while (change);
        }
        
        final ArrayList<ParseTree> res = new ArrayList<>();
        for (final Edge e : chart) {
            doLongtimeOperationCheck();
            if (e.isActive()) {
                log("Chart", "Active: " + e);
            } else {
                log("Chart", "Inactive: " + e);
                if (e.isOverspanning()) {
                    res.add(new ParseTree(e));
                }
            }

        }

        return res;
    }

    private void doLongtimeOperationCheck() {
        if (!GeneralDialog.continueLongOperation(
                "Long-time calculation", 
                "This seems to be a long-time calculation - proceed?", 
                Grammar.ASKING_TIME, 
                "Grammar-ctxtfree-long-time-ID")) {
            throw new RuntimeException("User-requested abort.");
        }
    }

    /**
     * The fundamental rule of chart parsing generates new edges by combining
     * fitting active and inactive edges.
     *
     * @return change flag
     */
    private boolean fundamentalRule() {
        boolean change = false;
        for (int i = 0; i < chart.size(); i++) {
            final Edge e = chart.get(i);
            if (e.isActive()) {
                for (int k = 0; k < chart.size(); k++) {
                    final Edge e2 = chart.get(k);
                    if (!e2.isActive() && e.matches(e2)) {
                        final Edge nw = new Edge(e, e2, fatherGr, this, false, false);
                        if (!chart.contains(nw)) {
                            chart.add(nw);
                            change = true;
                            log("FR", "Adding edge: " + nw);
                        }
                    }
                }
            }
        }
        return change;
    }

    /**
     * Add all the rules of the grammar to the chart that are relevant: Find the
     * rule with the LHS of edge as the leftmost RHS symbol and maximally the
     * remaining length of the input.
     *
     * @param pos
     *            current position in the chart
     * @return change flag
     */
    private boolean ruleInvocation(final int pos) {
        boolean change = false;
        for (int i = pos; i < chart.size(); i++) {
            final Edge e = chart.get(i);
            if (!e.isActive()) {
                for (final String lhs : grammar.withLeftmost(e.lhs)) {
                    for (final String[] rhs : grammar.rhs(lhs)) {
                        doLongtimeOperationCheck();
                        
                        if (!rhs[0].equals(e.lhs)
                                || rhs.length > getTokens().length - e.getStart()) {
                            continue;
                        }
                        final Edge nw = new Edge(e.getStart(), e.getEnd(), 1, lhs, rhs,
                                new ArrayList<Edge>(Arrays.asList(e)), fatherGr, this, false, false);
                        if (!chart.contains(nw)) {
                            chart.add(nw);
                            change = true;
                            log("IV", "Adding edge: " + nw);
                        }
                    }
                }
            }
        }
        return change;
    }

    /**
     * Logs a message.
     *
     * @param cat
     *            category
     * @param desc
     *            message
     */
    private void log(final String cat, final String desc) {
        if (listener != null) {
            listener.info(cat, desc);
        }
    }

    /**
     * @return Returns the tokens.
     */
    public String[] getTokens() {
        return tokens;
    }

    public static String currentStartSymbol;
}
