/*
 * File name:        TM.java (package eas.miscellaneous.graphToPDF)
 * Author(s):        lko
 * Java version:     8.0
 * Generation date:  14.02.2013 (14:41:12)
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

package veryFastPDF.algorithms.turing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Random;
import java.util.Set;

import javax.swing.JComponent;
import javax.swing.JPanel;

import eas.GlobalVariables;
import eas.miscellaneous.StaticMethods;
import eas.miscellaneous.convenience.GeneralDialog;
import eas.veryFastPDF.MainLink;
import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;
import veryFastPDF.HelpTexts;
import veryFastPDF.algorithms.grammars.Grammar;
import veryFastPDF.algorithms.latex.LaTeX;
import veryFastPDF.algorithms.latex.LaTeXCommands;
import veryFastPDF.pdfProcessors.GraphViz;
import veryFastPDF.pdfProcessors.LaTeXPDF;
import veryFastPDF.pdfProcessors.PDFProcessor;
import veryFastPDF.pdfProcessors.PDFProcessorFactory;
import veryFastPDF.plugin.FancyJLabel;
import veryFastPDF.script.ConversionMethod;
import veryFastPDF.script.Exercise;
import veryFastPDF.script.MethodWrapper;
import veryFastPDF.script.RepresentableAsPDF;
import veryFastPDF.script.RepresentableDefault;
import veryFastPDF.web.ConvenienceMethods;
import veryFastPDF.web.Webproof;

@Webproof(useInProductiveMode = true)
public class Turing extends RepresentableDefault {
    
    private static final long serialVersionUID = 6186331936732427309L;
    private LinkedList<String> tape;
    private String blankSymbol;
    private ListIterator<String> head;
    private HashMap<StateTapeSymbolPair, ArrayList<Transition>> transitions;
    private Set<String> terminalStates;
    private String initialState;
    
    /**
     * Set by declarations.
     */
    private String s0 = "s0";
    
    /**
     * Set by declarations.
     */
    private String F = "s0";
    
    /**
     * Set by declarations.
     */
    private String blank = "*";
    
    /**
     * Set by declarations.
     */
    private String inputs = "*";
    
    private String output = "";
    private boolean quiet = false;
    private String lastCode = "";
    private LinkedList<String>[] tapes;
    
    public Turing(Set<String> terminalStates, String initialState, String blankSymbol, Exercise exercise) {
        super(exercise);
        this.blankSymbol = blankSymbol;
        this.transitions = new HashMap<>();
        this.terminalStates = terminalStates;
        this.initialState = initialState;
        this.declareIgnoredFields();
    }
    
    public Turing(Exercise exercise) {
        this(new HashSet<String>(), "s0", "*", exercise);
    }
    
    @SuppressWarnings("unchecked")
    public Turing(Turing other) {
        super(other.getExercise());
        this.tape = new LinkedList<>(other.tape);
        this.head = this.tape.listIterator(other.head.nextIndex());
        this.blankSymbol = other.blankSymbol;
        this.initialState = other.initialState;
        this.inLatexMode = other.inLatexMode;
        this.lastCode = other.lastCode;
        this.output = other.output;
        this.quiet = other.quiet;
        this.runStepsScript = other.runStepsScript;
        if (other.tapes != null) {
            this.tapes = new LinkedList[other.tapes.length];
            for (int i = 0; i < other.tapes.length; i++) {
                this.tapes[i] = new LinkedList<String>(other.tapes[i]);
            }
        }
        this.terminalStates = new HashSet<String>(other.terminalStates);
        this.transitions = new HashMap<>(other.transitions);
        this.s0 = other.s0;
        this.F = other.F;
        this.inputs = other.inputs;
        this.blank = other.blank;
        this.displayMode = other.displayMode;
        
        this.declareIgnoredFields();
    }
    
    private void declareIgnoredFields() {
        this.addIgnoredField("blankSymbol");
        this.addIgnoredField("initialState");
        this.addIgnoredField("output");
        this.addIgnoredField("quiet");
        this.addIgnoredField("lastCode");
        this.addIgnoredField("inLatexMode");
        this.addIgnoredField("keepDeterministic");
        this.addIgnoredField("tempInputToCreateCodeFrom");
    }

    public void initializeTape(LinkedList<String> input) { // Arbitrary Strings as symbols.
        this.tape = input;
    }
    
    public void initializeTape(String input) { // Uses single characters as symbols.
        this.tape = new LinkedList<String>();
        for (int i = 0; i < input.length(); i++) {
            this.tape.add(input.charAt(i) + "");
        }
    }
    
    public Trace runDeterministicToTermination() {
        return runDeterministic(Integer.MAX_VALUE);
    }

    public Trace runDeterministic(int max) {
        return this.runDeterministic(max, false);
    }

    private void println() {
        this.print("\n");
    }
    
    private void println(String s) {
        this.print(s);
        this.println();
    }
    
    private void print(String s) {
        if (quiet) {
            output += s;
        } else {
            GlobalVariables.getParameters().logInfo(s);
        }
    }
    
    private String headNext() {
        return this.head.next();
    }
    
    private String headPrevious() {
        return this.head.previous();
    }
    
    public Trace runDeterministic(int max, boolean latexMode) { // Returns null if not in terminal state.
        Trace trace = new Trace();
        boolean broken = false;
        
        if (latexMode) {
            this.println("\\begin{tabular}{|c|c|}\\hline\n Tape & Transition \\\\\\hline\\hline");
            this.setLatexMode(true);
        } else {
            this.setLatexMode(false);
        }
        
        if (this.tape.size() == 0) {
            this.tape.add(this.blankSymbol);
        }
        
        this.head = this.tape.listIterator();
        this.headNext();
        this.headPrevious();
        
        StateTapeSymbolPair tsp = new StateTapeSymbolPair(this.initialState, this.tape.get(0), this);
        
        int i = 0;
        int num = 0;
        boolean nextAvoid = false;
        
        GeneralDialog.resetLongTimeOperationID("Turing-Det-Simulation-ID");
        
        while (this.transitions.containsKey(tsp)) { // While a matching transition exists.
            if (!GeneralDialog.continueLongOperation(
                    "Long-time calculation", 
                    "This seems to be a long-time calculation - proceed?", 
                    Grammar.ASKING_TIME, 
                    "Turing-Det-Simulation-ID")) {
                throw new RuntimeException("User-requested abort.");
            }
            
            num++;
            
            if (latexMode) {
                this.println(this.toString() + " & $" + StaticMethods.formatCollection(this.transitions.get(tsp)) + "$ \\\\");
            } else {
                this.println(this.toString() + "\t" + this.transitions.get(tsp));
            }
            
            Transition trans = this.transitions.get(tsp).get(0); // Use only first transition (others should not exist).

            if (!this.shortTrace || !nextAvoid) { // For short trace.
                trace.add(new TraceElement(
                        new Tape(this.tape, this.head.nextIndex()), 
                        this.transitions.get(tsp).get(0), num));
            }
            
            if (trans.to.tapeSymbol.equals(tsp.tapeSymbol)) {
                nextAvoid = true;
            } else {
                nextAvoid = false;
            }
            
            this.head.set(trans.to.tapeSymbol); // Write tape symbol.
            tsp.state = trans.to.state; // Change state.
            if (trans.direction == -1) { // Go left.
                if (!this.head.hasPrevious()) {
                    this.head.add(this.blankSymbol); // Extend tape.
                }
                tsp.tapeSymbol = this.headPrevious(); // Memorize tape symbol.
            } else if (trans.direction == 1) { // Go right.
                this.headNext(); // Memorize tape symbol.
                if (!this.head.hasNext()) {
                    this.head.add(this.blankSymbol); // Extend tape.
                    this.headPrevious();
                }
                tsp.tapeSymbol = this.headNext();
                this.headPrevious();
            } else {
                tsp.tapeSymbol = trans.to.tapeSymbol;
            }
            
            i++;
            if (i >= max) {
                if (!this.isInLatexMode()) {
                    this.println("Abbruch da Maximalzahl " + max + " überschritten.");
                }
                broken = true;
                break;
            }
        }
        
        if (latexMode) {
            this.print(this.toString() + " & $");
            try {
                this.print(this.transitions.get(tsp).toString().replace("$", "\\$"));
            } catch (Exception e) {
            }
           this.println(" $ \\\\\\hline\n\\end{tabular}");
        } else {
            try {
                this.print(this.transitions.get(tsp).toString());
            } catch (Exception e) {
            }
        }
        
        if (this.terminalStates.contains(tsp.state) && !broken) {
            trace.add(new TraceElement(new Tape(this.tape, this.head.nextIndex()), 
                    new Transition(
                            new StateTapeSymbolPair("", "accepted", this), 
                            null, 0, this), num + 1));
        } else {
            trace.add(new TraceElement(new Tape(this.tape, this.head.nextIndex()), null, num + 1));
        }
        
        return trace;
    }

    public void runNondeterministic(int max, GraphViz gv) {
        this.head = this.tape.listIterator();
        this.headNext();
        this.headPrevious();
        this.quiet = false;
        GeneralDialog.resetLongTimeOperationID("Turing-Nondet-Simulation-ID");

        try {
            this.runNondeterministic(max + 1, gv, 0, "", 0);
        } catch (Exception e) {
            gv.resetDotSource();
            gv.addln("digraph {a[label=\"" + Grammar.ABORTED_STRING + "\"];}");
        }
    }

    private String runNondeterministic(int max, GraphViz gv, int currentNodeId, String idPrefix, int currentDepth) {
        String connectionNode = idPrefix + currentNodeId;
        int depth = currentDepth;

        if (!GeneralDialog.continueLongOperation(
                "Long-time calculation", 
                "This seems to be a long-time calculation - proceed?", 
                Grammar.ASKING_TIME, 
                "Turing-Nondet-Simulation-ID")) {
            throw new RuntimeException("User-requested abort.");
        }

        StateTapeSymbolPair tsp = new StateTapeSymbolPair(this.initialState, this.tape.get(this.head.nextIndex()), this);
        
        int currID = currentNodeId;
        int i;
        String idWithPrefixI = null, idWithPrefixCurrent;
        idWithPrefixCurrent = idPrefix + currID;
        String shape = "";
        String acc = "";
        
        if (this.transitions.containsKey(tsp)) {
            connectionNode = idPrefix + (currentNodeId + 1);
        } else {
            if (this.terminalStates.contains(tsp.state)) {
                shape = "shape=rectangle,peripheries=2,";
//                acc = "\\naccepted";
            } else {
                shape = "shape=rectangle,";
            }
            gv.addln("a"
                    + idWithPrefixCurrent
                    + "["
                    + shape
                    + "label=<"
                    + this.printTapeWithHeadUnicode(new Tape(this.tape,
                            this.head.nextIndex())) + " (" + this.convertStateHTML(tsp.state) + ")"
                    + acc + ">];");
        }
        
//        gv.addln("a" + idWithPrefixCurrent + " -> a" + idWithPrefixNext + ";");
        
        Transition transit = null;
        boolean first = true;
        while (this.transitions.containsKey(tsp)) { // While a matching transition exists.
            // Avoid running into Heap space overflow ON SERVER ONLY!
            if (gv.getSourceString().length() > gv.getCodeSizeToBeConsideredLarge()) {
                if (!MainLink.isApplicationOriginDesktop()) {
                    throw new RuntimeException("User-requested abort.");
                }
            }
            
            depth++;
            i = currID + 1;
            idWithPrefixCurrent = idPrefix + currID;
            idWithPrefixI = idPrefix + i;
            
            if (!first) {
                gv.addln("a" 
                            + idWithPrefixCurrent 
                            + " -> a" 
                            + idWithPrefixI 
                            + "[label=\" " 
                            + transit.to.tapeSymbol
                            + ","
                            + this.convertDirectionToLatex(transit.direction)
                            + "\"];");
            }
            gv.addln("a"
                    + idWithPrefixI
                    + "[label=<"
                    + this.printTapeWithHeadUnicode(new Tape(this.tape,
                            this.head.nextIndex())) + " ("
                    + this.convertStateHTML(tsp.state) + ")>];");
            
            if (depth >= max) {
                return connectionNode;
            }

            first = false;
            
            List<Transition> transList = this.transitions.get(tsp);
            if (transList.size() == 1) { // Make deterministic calculation.
                transit = transList.get(0); 
                this.head.set(transit.to.tapeSymbol); // Write tape symbol.
                tsp.state = transit.to.state; // Change state.
                if (transit.direction == -1) { // Go left.
                    if (!this.head.hasPrevious()) {
                        this.head.add(this.blankSymbol); // Extend tape.
                    }
                    tsp.tapeSymbol = this.headPrevious(); // Memorize tape symbol.
                } else if (transit.direction == 1) { // Go right.
                    this.headNext(); // Memorize tape symbol.
                    if (!this.head.hasNext()) {
                        this.head.add(this.blankSymbol); // Extend tape.
                        this.headPrevious();
                    }
                    tsp.tapeSymbol = this.headNext();
                    this.headPrevious();
                } else {
                    tsp.tapeSymbol = transit.to.tapeSymbol;
                }
                
                currID++;
                
                if (!this.transitions.containsKey(tsp)) {
                    String originalInitialState = this.initialState;
                    String newPrefix = idPrefix + 1 + "b";
                    this.initialState = transit.to.state; // Change state.
                    String connNode = this.runNondeterministic(max, gv, i + 1, newPrefix, depth);
                    gv.addln("a" 
                                    + idWithPrefixI 
                                    + " -> a" 
                                    + connNode 
                                    + "[label=\" " 
                                    + transit.to.tapeSymbol
                                    + ","
                                    + this.convertDirectionToLatex(transit.direction)
                                    + "\"];");
                    this.initialState = originalInitialState;
                }
            } else {
                int count = 1;
                for (Transition trans : transList) {
                    Turing ndetCopy = new Turing(this);
                    ndetCopy.head.next();
                    ndetCopy.head.previous();
                    ndetCopy.head.set(trans.to.tapeSymbol);
                    ndetCopy.initialState = trans.to.state; // Change state.
                    if (trans.direction == -1) { // Go left.
                        if (!ndetCopy.head.hasPrevious()) {
                            ndetCopy.head.add(this.blankSymbol); // Extend tape.
                        }
                        ndetCopy.headPrevious(); // Memorize tape symbol.
                    } else if (trans.direction == 1) { // Go right.
                        ndetCopy.headNext(); // Memorize tape symbol.
                        if (!ndetCopy.head.hasNext()) {
                            ndetCopy.head.add(this.blankSymbol); // Extend tape.
                            ndetCopy.headPrevious();
                        }
                        ndetCopy.headNext();
                        ndetCopy.headPrevious();
                    }
                    String newPrefix = idPrefix + count + "b";
                    String connNode = ndetCopy.runNondeterministic(max, gv, i + 1, newPrefix, depth);
                    gv.addln("a" 
                                    + idWithPrefixI 
                                    + " -> a" 
                                    + connNode 
                                    + "[label=\" " 
                                    + trans.to.tapeSymbol
                                    + ","
                                    + this.convertDirectionToLatex(trans.direction)
                                    + "\"];");
                    count++;
                }
                return connectionNode;
            }
        }

        return connectionNode;
    }

    @Override
    public String toString() {
        try {
            String latex = "";
            if (isInLatexMode()) {
                latex = "$";
            }
            
            String s = latex;
            
            int headPos = this.head.nextIndex();
            
            for (int i = 0; i < headPos; i++) {
                s += this.convertTapeSymbolLatex(this.tape.get(i)) + " ";
            }

            if (this.isInLatexMode()) {
                s += "\\hat{" + this.convertTapeSymbolLatex(this.tape.get(headPos)) + "} ";
            } else {
                s += "[H] " + this.tape.get(headPos) + " ";
            }
            
            for (int i = headPos + 1; i < this.tape.size(); i++) {
                s += this.convertTapeSymbolLatex(this.tape.get(i)) + " ";
            }
            
            return s + latex;
        } catch (Exception e) {
            return "";
        }
    }
    
    private boolean inLatexMode = false;
    private int runStepsScript = 100;
    
    public LinkedList<String> getTape() {
        return this.tape;
    }
    
    public void setRunStepsScript(int runStepsScript) {
        this.runStepsScript = runStepsScript;
    }
    
    public void setLatexMode(boolean inLatexMode) {
        this.inLatexMode = inLatexMode;
    }
    
    public HashSet<String> getTapeAlphabet() {
        HashSet<String> alphabet = new HashSet<String>();
        
        for (List<Transition> list : this.transitions.values()) {
            for (Transition t : list) {
                alphabet.add(t.from.tapeSymbol);
                alphabet.add(t.to.tapeSymbol);
            }
        }
        
        return alphabet;
    }
    
    public HashSet<String> getStates() {
        HashSet<String> states = new HashSet<String>();
        
        if (transitions != null) {
            for (List<Transition> list : this.transitions.values()) {
                for (Transition t : list) {
                    states.add(t.from.state);
                    states.add(t.to.state);
                }
            }
        }
        
        return states;
    }

    private String convertStateHTML(String state) {
        return "" + state.charAt(0) + "<SUB>" + state.substring(1) + "</SUB>";
    }
    
    public String convertStateLatex(String state) {
        if (!this.isInLatexMode()) {
            return state;
        }
        return state.charAt(0) + "_{" + state.substring(1) + "}";
    }
    
    public String convertTapeSymbolLatex(String symbol) {
        if (!isInLatexMode()) {
            return symbol;
        }
        
        if ("*".equals(symbol)) {
            return "\\star";
        }
        
        if ("$".equals(symbol)) {
            return "\\$";
        }
        
        return symbol;
    }
    
    public String convertDirectionToLatex(int direction) {
        if (direction == -1) {
            return "L";
        }
        
        if (direction == 1) {
            return "R";
        }
        
        return "N";
    }

    public String getLatexTable() {
        this.setLatexMode(true);
        String s = "";
        
        ArrayList<String> states = new ArrayList<String>(this.getStates());
        ArrayList<String> alphabet = new ArrayList<String>(this.getTapeAlphabet());
        Collections.sort(states);
        Collections.sort(alphabet, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                if ("*".equals(o1) && !"*".equals(o2)) {
                    return 1;
                }
                
                if (Character.isLowerCase(o1.charAt(0)) && !Character.isLowerCase(o2.charAt(0))) {
                    return -1;
                } else if (Character.isLowerCase(o2.charAt(0)) && !Character.isLowerCase(o1.charAt(0))) {
                    return 1;
                }
                
                return o1.compareTo(o2);
            }
        });
        
        s += "\\begin{tabular}{|c||";
        for (int i = 0; i < alphabet.size(); i++) {
            s += "c|";
        }
        s += "}\n \\hline\n";
        for (String symbol : alphabet) {
            s += " & " + "$" + this.convertTapeSymbolLatex(symbol) + "$";
        }
        s += " \\\\ \\hline\\hline\n";
        
        for (String state : states) {
            s += "$" + convertStateLatex(state) + "$";
            for (String symbol : alphabet) {
                List<Transition> list = this.transitions.get(new StateTapeSymbolPair(state, symbol, this));
                String toState = "";
                
                if (!this.isDeterministic()) {
                    toState += "\\{";
                }
                if (list != null) {
                    boolean first = true;
                    for (Transition t : list) {
                        if (t != null) {
                            if (!first) {
                                toState += ", ";
                            }
                            
                            toState += 
                                    "$(" 
                            + convertStateLatex(t.to.state) 
                            + ", " 
                            + this.convertTapeSymbolLatex(t.to.tapeSymbol) 
                            + ", " 
                            + convertDirectionToLatex(t.direction) 
                            + ")$";
                        }
                        
                        first = false;
                    }
                }
                if (!this.isDeterministic()) {
                    toState += "\\}";
                    
                    if (toState.equals("\\{\\}")) {
                        toState = "$\\emptyset$";
                    }
                }

                s += " & " + toState;
            }
            s += " \\\\ \\hline\n";
        }
        
        s += "\\end{tabular}";
        return s;
    }
    
    public boolean isDeterministic() {
        for (StateTapeSymbolPair stp : this.transitions.keySet()) {
            List<Transition> list = transitions.get(stp);
            
            if (list != null && list.size() > 1) {
                return false;
            }
        }
        
        return true;
    }
    
    public void addTransition(String fromState, String fromSymbol, String toState, String toSymbol, int dir) {
        StateTapeSymbolPair from = new StateTapeSymbolPair(fromState, fromSymbol, this);
        StateTapeSymbolPair to = new StateTapeSymbolPair(toState, toSymbol, this);
        
        ArrayList<Transition> currTrans = this.transitions.get(from);
        
        if (currTrans == null) {
            currTrans = new ArrayList<Transition>();
        }

        if (this.keepDeterministic && currTrans.size() > 0) {
            return;
        }
        
        currTrans.add(new Transition(from, to, dir, this));
        
        this.transitions.put(from, currTrans);
    }
    
    public boolean isInLatexMode() {
        return this.inLatexMode;
    }

    @Override
    public String[] getExampleScripts() {
        String s0 = "turing:\n" + 
                "/* Sorting: */\n" +
                "(s0, *) => (se, *, L); (s0, a) => (s0, a, R); (s0, b) => (s1, B, R);\n" + 
                "(s1, *) => (s2, *, L); (s1, a) => (s1, a, R); (s1, b) => (s1, b, R);\n" + 
                "(s2, B) => (se, b, L); (s2, a) => (s3, b, L); (s2, b) => (s2, b, L);\n" + 
                "(s3, B) => (s0, a, R); (s3, a) => (s3, a, L); (s3, b) => (s3, b, L);\n" + 
                "(se, *) => (see, *, R); (se, a) => (se, a, L);\n" + 
                "--declarations--\n" + 
                "s0=s0;\n" + 
                "F=see;\n" + 
                "blank=*;\n" + 
                "inputs=abbbab,a,*;\n" + 
                "runStepsScript=100;\n" + 
                "shortTrace=false\n" + 
                "--declarations-end--";
        
        String s1 = "turing:\n" + 
                "/* 4-state busy beaver: */\n" +
                "(A,*)=>(B,1,R);\n" + 
                "(A,1)=>(B,1,L);\n" + 
                "(B,*)=>(A,1,L);\n" + 
                "(B,1)=>(C,*,L);\n" + 
                "(C,*)=>(H,1,R);\n" + 
                "(C,1)=>(D,1,L);\n" + 
                "(D,*)=>(D,1,R);\n" + 
                "(D,1)=>(A,*,R);\n" + 
                "--declarations--\n" + 
                "s0=A;\n" + 
                "F=H;\n" + 
                "blank=*;\n" + 
                "inputs=*;\n" + 
                "runStepsScript=120;\n" + 
                "shortTrace=false\n" + 
                "--declarations-end--";
        
        String s2 = "turing:\n" + 
                "/* (NON-DETERMINISTIC) */\n" + 
                "(s0, a) => (s2, a, R) | (s3, a, R);\n" + 
                "(s0, b) => (s1, b, R) | (s4, b, R);\n" + 
                "(s1, a) => (s2, a, R);\n" + 
                "(s1, b) => (s1, b, R);\n" + 
                "(s2, a) => (s1, a, R) | (s5, a, R);\n" + 
                "(s2, b) => (s2, b, R);\n" + 
                "(s3, a) => (s3, a, R);\n" + 
                "(s3, b) => (s4, b, R);\n" + 
                "(s4, a) => (s4, a, R);\n" + 
                "(s4, b) => (s3, b, R) | (s5, b, R);\n" + 
                "(s5, *) => (sf, *, N);\n" + 
                "--declarations--\n" + 
                "s0=s0;\n" + 
                "F=sf;\n" + 
                "blank=*;\n" + 
                "inputs=aabab;\n" + 
                "runStepsScript=120;\n" + 
                "shortTrace=false\n" + 
                "--declarations-end--";
        
        String s3 = "turing:\n" + 
                "/* (NON-DETERMINISTIC) */\n" + 
                "(s0, *) => (s1, $, L) | (s2, $, L);\n" + 
                "(s0, v) => (s0, 0, R) | (s0, 1, R);\n" + 
                "(s1, *) => (sf, $, R);\n" + 
                "(s1, 0) => (s1, 0, L);\n" + 
                "(s2, *) => (sf, $, R);\n" + 
                "(s2, 1) => (s2, 1, L);\n" + 
                "--declarations--\n" + 
                "s0=s0;\n" + 
                "F=sf;\n" + 
                "blank=*;\n" + 
                "inputs=vvv;\n" + 
                "runStepsScript=120;\n" + 
                "shortTrace=false\n" + 
                "--declarations-end--";
        
        return new String[] {s0, s1, s2, s3};
    }
    
    @Override
    public boolean isAcceptableScript(String code) {
        return (code + "").toLowerCase().startsWith("turing:");
    }
    
    private boolean shortTrace = false;
    
    private String tempInputToCreateCodeFrom;
    
    public void setTempInputToCreateCodeFrom(String tempInputToCreateCodeFrom) {
        this.tempInputToCreateCodeFrom = tempInputToCreateCodeFrom;
    }

    @Override
    public String createScriptFromInstance() {
        String finalStates = "";
        String input = tempInputToCreateCodeFrom;
        int steps = 8;
        
        
        if (this.isDeterministic()) {
            steps = 100;
        }
        
        for (String state : this.terminalStates) {
            finalStates += state + ",";
        }
        
        this.s0 = this.initialState;
        this.F = finalStates;
        this.blank = this.blankSymbol;
        this.inputs = input;
        this.runStepsScript = steps;
        
        String code = "turing:\n"; // First line.
        
        for (StateTapeSymbolPair t : this.transitions.keySet()) {
            for (Transition trans : this.transitions.get(t)) {
                code += "(" + trans.from.state + ", " + trans.from.tapeSymbol + ")"
                        + " => "
                        + "(" + trans.to.state + ", " + trans.to.tapeSymbol + ", " 
                        + this.convertIntToDir(trans.direction) + ");\n";
            }
        }
        
        code += this.generateCompleteDeclarationsBlock();

        return code;
    }
    
    @Override
    @SuppressWarnings("unchecked")
    public void createInstanceFromScript(String codeOriginal, RepresentableAsPDF father) {
        this.applyDeclarationsAndPreprocessors(codeOriginal, father, 0);
        String code = this.getScriptWithoutPrepAndDecl();
        
        code = this.decollapseRules(code);
        
        this.lastCode = code;
        String[] segments = code.replace(" ", "").replace("\n", "").replace("\r", "").replace("\t", "").split(":");
        this.initialState = this.s0;
        this.terminalStates = new HashSet<>();
        this.blankSymbol = this.blank;
        this.tape = new LinkedList<>();
        this.transitions = new HashMap<>();
        String[] tapesTemp = this.inputs.split(",");
        this.tapes = new LinkedList[tapesTemp.length];
        
        for (int j = 0; j < tapesTemp.length; j++) {
            this.tapes[j] = new LinkedList<String>();
            for (int i = 0; i < tapesTemp[j].length(); i++) {
                this.tapes[j].add(tapesTemp[j].charAt(i) + "");
            }
        }

        this.tape = this.tapes[0];
        
        this.head = this.tape.listIterator();
        
        for (String s : this.F.split(",")) {
            this.terminalStates.add(s);
        }

        String[] transitions = segments[1].split(";");
        for (String tString : transitions) {
            Transition t = createTrans(tString);
            this.addTransition(t.from.state, t.from.tapeSymbol, t.to.state, t.to.tapeSymbol, t.direction);
//            this.transitions.put(t.from, t);
        }
    }

    private Transition createTrans(String tStr) {
        String tString = tStr.replaceAll("\\(", "").replaceAll("\\)", "");
        String[] from = tString.split("=>")[0].split(",");
        String[] to = tString.split("=>")[1].split(",");
        
        Transition t = new Transition(
                new StateTapeSymbolPair(from[0], from[1], this), 
                new StateTapeSymbolPair(to[0], to[1], this), 
                this.convertDirToInt(to[2]),
                this);
        
        return t;
    }

    private int convertDirToInt(String dir) {
        if ("R".equals(dir)) {
            return 1;
        }
        if ("L".equals(dir)) {
            return -1;
        }
        return 0;
    }

    private String convertIntToDir(int dir) {
        if (dir == 1) {
            return "R";
        }
        if (dir == -1) {
            return "L";
        }
        return "N";
    }

    private int displayMode = 0;
    
    @Override
    public PDFProcessor generatePDFscript(String pdfPath) {
        super.generatePDFscript(pdfPath);
        boolean deterministic = this.isDeterministic();
        PDFProcessor classicView = generateClassicView(pdfPath, deterministic);

        if (displayMode % 3 > 0) {
            String table = this.getLatexTable();
            
            String classicScript = "";
            
            if (displayMode % 3 == 1) {
                table = LaTeX.subscript(
                        -1.0, 
                        LaTeX.LATEX_PREAMBLE + LaTeXCommands.PREAMBLE_CROP_PAGE + table + LaTeXCommands.POSTAMBLE_STANDARD);

                classicScript = LaTeX.subscript(-1.0, classicView.getPlainPDFScript());
            }

            String allOfIt = table + "\n\\bigbreak\n" + classicScript;

            return new LaTeXPDF(
                    LaTeXCommands.PREAMBLE_CROP_PAGE_PREVIEW + allOfIt + LaTeXCommands.POSTAMBLE_STANDARD, 
                    pdfPath,
                    this);
        }
        
        return classicView;
    }

    public PDFProcessor generateClassicView(String pdfPath,
            boolean deterministic) {
        // Nice view in latex, if det. and only one tape.
        if (isSimpleCalc()) {
            LaTeX laTeX = new LaTeX(this.getExercise());
            laTeX.createInstanceFromScript(this.computationTraceLatex(), null);
            PDFProcessor latexPDF = laTeX.generatePDFscript(pdfPath);
            return latexPDF;
        }
        
        this.quiet = true;
        GraphViz gv = new GraphViz(pdfPath, this);
        
        gv.addln("digraph G {");

        if (this.tapes != null && this.tapes.length > 0) {
            if (deterministic) {
                for (int j = 0; j < this.tapes.length; j++) {
                    this.tape = this.tapes[j];
                    int i = 1;
                    
                    Trace trace;
                    try {
                        trace = this.runDeterministic(this.runStepsScript);
                    } catch (Exception e) {
                        gv.resetDotSource();
                        gv.addln("digraph {a[label=\"" + Grammar.ABORTED_STRING + "\"];}");
                        return gv;
                    }
                    
                    gv.addln("a" + j + " [shape=boxed, label=\"");
                    for (TraceElement el : trace) {
                        if (el.getNum() != i) {
                            gv.addln("...\\n");
                        }
                        
                        i = el.getNum() + 1;
                        
                        String transition = ("" + el.getTransition()).toString(); // Lass das "" stehen, du Esel!
                        gv.addln("(" + el.getNum() + ")  " + this.printTapeWithHead(el.getTapeContents()) + "  ||  " + transition.replace("=>", " =\\> ") + "\\n");
    //                    i++;
                    }
                    gv.addln("\"];");
                }
            } else { // Use only tapes[0];
                this.tape = this.tapes[0];
                this.runNondeterministic(this.runStepsScript, gv);
            }
            
            this.tape = this.tapes[0];
        }
        
        gv.addln("};");

        this.quiet = false;
        return gv;
    }

    public boolean isSimpleCalc() {
        return this.isDeterministic() && this.tapes != null && this.tapes.length == 1;
    }
    
    private String printTapeWithHead(Tape tape) {
        String tapePrinted = "";
        
        for (int i = 0; i < tape.size(); i++) {
            if (i == tape.getHeadPos()) {
                tapePrinted += "|";
            }
            tapePrinted += tape.get(i);
            if (i == tape.getHeadPos()) {
                tapePrinted += "|";
            }
        }
        
        return tapePrinted;
    }
    
    private String printTapeWithHeadUnicode(Tape tape) {
        String tapePrinted = "";
        
        for (int i = 0; i < tape.size(); i++) {
            if (i == tape.getHeadPos()) {
                tapePrinted += "|";
            }
            if (tape.get(i).equals("*")) {
                tapePrinted += "&#42;";
            } else {
                tapePrinted += tape.get(i);
            }
            if (i == tape.getHeadPos()) {
//                tapePrinted += "&#817;";
                tapePrinted += "|";
            }
        }
        
        return tapePrinted;
    }

    @ConversionMethod
    public String generateExercise() {
        String s = "";
        s += "These are the non-deterministic transitions:\n";
        s += StaticMethods.formatCollection(this.getNondeterministicTrans()).replace(",(", "\\\\\n(");
        
        return s;
    }
//
//    public String autoGenerateScript() {
//        Integer num = GeneralDialog.getNumberFromUser(
//                "Creating \"busy beaver\"-like random machine with the following number of states", 
//                "5",
//                "Turing.BusyBeaver1");
//        
//        Integer numMachines = GeneralDialog.getNumberFromUser(
//                "I will create the following maximum number of machines and take the busiest one", 
//                "10000",
//                "Turing.BusyBeaver2");
//
//        if (num == null || numMachines == null) {
//            return null;
//        }
//        
//        return createRandomBBMachineScript(num, numMachines);
//    }

    @ConversionMethod(plainText = false)
    public String createRandomBBMachineScript(int numOfStates, int numOfRandomMachinesToTry) {
        LinkedList<String> states = new LinkedList<String>();
        LinkedList<String> symbols = new LinkedList<String>();
        
        for (int i = 0; i < numOfStates; i++) {
            states.add("s" + i);
        }
        
        symbols.add("*");
        symbols.add("1");
        
        final int simulationLength = 25;
        int i = 0;
        
        Turing bestTur = null;
        int bestNum = 0;
        
        GeneralDialog.resetLongTimeOperationID("TM--BusyBeaver-ID");
        while (i < numOfRandomMachinesToTry) {
            Turing tur = Turing.randomMachine(
                    states.size() * symbols.size(), 
                    states, 
                    symbols, 
                    "*", 
                    "s0", 
                    true,
                    this.getExercise());
            
            tur.tape = new LinkedList<>();
            tur.tape.add("*");
            tur.quiet = true;
            Trace t = tur.runDeterministic(simulationLength);
            HashSet<String> stateSet = new HashSet<String>();
            t.forEach(element -> {
                try {
                    stateSet.add(element.getTransition().from.state);
                    stateSet.add(element.getTransition().to.state);
                } catch (Exception e) {
                }
            });

            int numToOptimize = countNonBlankSymbols(tur.tape, tur.blankSymbol);
            
            if (bestNum < numToOptimize && t.size() <= simulationLength) {
                bestNum = numToOptimize;
                bestTur = tur;
            }
            
            if (i % 10000 == 9999) {
                GlobalVariables.getParameters().logDebug("Generated " + (i + 1) + " random machines. Busiest: " + bestNum);
            }
            
            i++;

            // Avoid very long operations ON SERVER ONLY. Return best so far in that case.
            if (!MainLink.isApplicationOriginDesktop() && !GeneralDialog.continueLongOperation("TM--BusyBeaver-ID")) {
                bestTur.tempInputToCreateCodeFrom = "*";
                return bestTur.createScriptFromInstance();
            }
        }
        
        bestTur.tempInputToCreateCodeFrom = "*";
        return bestTur.createScriptFromInstance();
    }

    private int countNonBlankSymbols(List<String> tape, String blankSymbol) {
        int num = 0;
        
        for (String s : tape) {
            if (!s.equals(blankSymbol)) {
                num++;
            }
        }
        
        return num;
    }
    
    private boolean keepDeterministic = false;
    
    /**
     * If nondeterministic transitions are allowed. Does not affect already
     * existing transitions.
     */
    public void setKeepDeterministic(boolean keepDeterministic) {
        this.keepDeterministic = keepDeterministic;
    }
    
    private static Random rand = new Random();

    /**
     * All states are set to terminal states.
     */
    public static Turing randomMachine(
            int numOfTrans, 
            Collection<String> states, 
            Collection<String> symbols, 
            String blankSymbol, 
            String initialState,
            boolean deterministic, 
            Exercise exercise) {
        HashSet<String> ts = new HashSet<String>(states);
        Turing tur = new Turing(ts, "s0", "*", exercise);
        ArrayList<String> sts = new ArrayList<String>(states);
        ArrayList<String> symbs = new ArrayList<String>(symbols);
        
        tur.setKeepDeterministic(deterministic);
        
        Collections.shuffle(sts);
        Collections.shuffle(symbs);
        
        StateTapeSymbolPair from = new StateTapeSymbolPair(initialState, blankSymbol, tur);
        
        for (int i = 0; i < numOfTrans; i++) {
            StateTapeSymbolPair to = new StateTapeSymbolPair(sts.get(0), symbs.get(0), tur);

            int dir = 1;
            if (rand.nextBoolean()) {
                dir = -1;
            }
            
            tur.addTransition(from.state, from.tapeSymbol, to.state, to.tapeSymbol, dir);

            Collections.shuffle(sts);
            Collections.shuffle(symbs);
            from = new StateTapeSymbolPair(sts.get(0), symbs.get(0), tur);
            Collections.shuffle(sts);
            Collections.shuffle(symbs);
        }

        return tur;
    }
    
    private LinkedList<Transition> getNondeterministicTrans() {
        LinkedList<Transition> nondet = new LinkedList<Transition>();
        
        for (ArrayList<Transition> list : this.transitions.values()) {
            if (list.size() != 1) {
                nondet.addAll(list);
            }
        }
        
        return nondet;
    }

    @Override
    public Class<? extends PDFProcessor> getPDFProcessorClass() {
        return this.isSimpleCalc() ? LaTeXPDF.class : GraphViz.class;
    }
    
    @Override
    public JComponent getAdditionalInfo() {
        JPanel panel = new JPanel(new MigLayout("wrap 1"));
        FancyJLabel butt1 = new FancyJLabel("Det. TM");

        panel.add(butt1, new CC().wrap());

        if (!this.isDeterministic()) {
            butt1.setText("NonDet. TM");
        }

        panel.add(super.getAdditionalInfo(), new CC().wrap());
        
        return panel;
    }

    @ConversionMethod(plainText = false)
    public String createTuringTableLatexScrtip() {
        this.displayMode++;
        tempInputToCreateCodeFrom = this.inputs;
        return this.createScriptFromInstance();
    }

    public String computationTraceLatex() {
        output = "";
        this.createInstanceFromScript(this.lastCode, null);
        dummyDetSim();
        return "latex: " + LaTeXCommands.PREAMBLE_CROP_PAGE + output + LaTeXCommands.POSTAMBLE_STANDARD + this.getUpperClassDeclarationsBlockOnly();
   }

    public void dummyDetSim() {
        this.quiet = true;
        this.tape = this.tapes[0];
        this.runDeterministic(this.runStepsScript, true);
        this.quiet = false;
    }

    @Override
    public HashMap<String, MethodWrapper> getDynamicMethods() {
        HashMap<String, MethodWrapper> methods = super.getDynamicMethods();

        String turTabName = "Toggle Turing table";
        String randBeavName = "Random Busy Beaver";
        String showNonDetName = "Show nondet. transitions";
//        String compTraceLatexName = "Computation trace (Latex)";
        String turTabName_G = "Wechsle Anzeige der Turing-Tafel";
        String randBeavName_G = "Zufälliger Busy-Beaver";
        String showNonDetName_G = "Zeige nichtdet. Transitionen";
//        String compTraceLatexName_G = "Berechnungsschritte (Latex)";

        try {
            MethodWrapper mw1 = new MethodWrapper(
                    this.getClass().getMethod("createTuringTableLatexScrtip"),
                    this.getClass(), // Target script class. Important to set correctly!
                    this,
                    "Show or hide the turing table of this script",
                    "Zeige oder verstecke die Turingtafel zu diesem Skript",
                    turTabName,
                    turTabName_G);
            
            MethodWrapper mw2 = new MethodWrapper(
                    this.getClass().getMethod("createRandomBBMachineScript", Integer.TYPE, Integer.TYPE),
                    Turing.class, // Target script class. Important to set correctly!
                    this,
                    "Create new random 'Busy Beaver'-like Turing machine",
                    "Erzeuge eine neue, zufällige Turingmaschine, die einen 'Busy-Beaver' approximiert",
                    randBeavName,
                    randBeavName_G);
            mw2.setParameterExplanation(0, "The number of states which the "
                    + HelpTexts.link("https://en.wikipedia.org/wiki/Busy_beaver", "Busy Beaver", true)
                    + "-like Turing machine is supposed to have.");
            mw2.setParameterExplanation_G(0, "Die Anzahl an Zuständen, die die einen "
                    + HelpTexts.link("https://de.wikipedia.org/wiki/Flei%C3%9Figer_Biber", "Busy-Beaver", true)
                    + " annähernde Turingmaschine haben soll.");
            mw2.setParameterExplanation(1, "Creating a Busy Beaver for any given number of states is incomputable. "
                    + "Therefore, this method creates a number of random Turing machines and returns the 'busiest'. "
                    + "This number is specified here.");
            mw2.setParameterExplanation_G(1, "Einen Busy-Beaver für eine beliebige Anzahl an Zuständen anzugeben, "
                    + "ist unmöglich, da das ein unberechenbares Problem ist. Daher generiert diese Methode eine "
                    + "gewisse Anzahl an zufälligen Turingmaschinen und gibt diejenige zurück, die die meisten "
                    + "Einsen auf das Band schreibt, also einem Busy-Beaver am nächsten ist. Die Anzahl der zu testenden "
                    + "Turingmaschinen wird hier angegeben.");
            
            MethodWrapper mw3 = new MethodWrapper(
                    this.getClass().getMethod("generateExercise"),
                    Turing.class, // Target script class. Here irrelevant!
                    this,
                    "Show additional information for the Turing machine",
                    "Zeige zusätzliche Informationen für diese Turingmaschine",
                    showNonDetName,
                    showNonDetName_G);
            mw3.setReturnValueIsScript(false);
            
//            MethodWrapper mw4 = new MethodWrapper(
//                    this.getClass().getMethod("computationTraceLatex"),
//                    LaTeXCode.class, // Target script class. Here irrelevant!
//                    this,
//                    "Show the latex computation trace for (the first of) the current input(s)",
//                    "Zeige die Berechnungsschritte für den (ersten der) aktuellen Input(s) im Latex-Modus",
//                    compTraceLatexName,
//                    compTraceLatexName_G);
            
            methods.put(turTabName, mw1);
            methods.put(randBeavName, mw2);
            methods.put(showNonDetName, mw3);
//            methods.put(compTraceLatexName, mw4);
        } catch (SecurityException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        
        return methods;
    }
    
    @Override
    public String getGermanName() {
        return "Turingmaschine";
    }
    
    @Override
    public String getModeDependentInfo(String mode, boolean english) {
        if (mode.equals(ConvenienceMethods.INFO_II_MODE_NAME)) {
            return ConvenienceMethods.createInfo2ModeString(
                    4, 
                    8, 
                    1, 
                    "http://www.dasinfobuch.de/links/Turingmaschinen.html",
                    "http://info2.aifb.kit.edu/qa/index.php?qa=353&qa_1=band-i-kapitel-8",
                    english
                    );
        }

        return "";
    }
    
    @Override
    public HashMap<String, String> getMetaProperties() {
        String className = this.getClass().getSimpleName();
        HashMap<String, String> props = super.getMetaProperties();
        int numStates = this.getStates() == null ? 0 : this.getStates().size();
        int numTrans = this.transitions == null ? 0 : this.transitions.size();
        boolean det = this.transitions == null ? false : this.isDeterministic();
        
        props.put(className + "_numStates", numStates + "");
        props.put(className + "_numTransitions", numTrans + "");
        props.put(className + "_deterministic", det + "");
        props.put(className + "_inputs", this.inputs + "");
        props.put(className + "_initialState", this.s0 + "");
        props.put(className + "_finalStates", this.F + "");
        props.put(className + "_blank", this.blank + "");
        props.put(className + "_runSteps", this.runStepsScript + "");
        props.put(className + "_shortTrace", this.shortTrace + "");

        return props;
    }
    
    @Override
    public Collection<PDFProcessor> getPossiblePDFProcessorClasses() {
        return PDFProcessorFactory.allWebPDFProcessors();
    }
}
