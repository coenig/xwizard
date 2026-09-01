/*
 * File name:        PDA.java (package eas.fundamentalAlgorithms.graphBased.pushDown)
 * Author(s):        Lukas König
 * Java version:     7.0
 * Generation date:  10.01.2014 (14:23:22)
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

package veryFastPDF.algorithms.pda;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;

import eas.GlobalVariables;
import eas.miscellaneous.StaticMethods;
import eas.miscellaneous.convenience.GeneralDialog;
import eas.veryFastPDF.MainLink;
import net.miginfocom.swing.MigLayout;
import veryFastPDF.algorithms.fsm.StatePair;
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
import veryFastPDF.script.exceptionHandling.LongOperationException;
import veryFastPDF.web.ConvenienceMethods;
import veryFastPDF.web.Webproof;
import veryFastPDF.VFPVariables;

/**
 * @author Lukas König
 */
@Webproof(useInProductiveMode = true)
public class PDA extends RepresentableDefault {

    private static final String SIMULATE_ONE_STEP_METHOD_NAME = "Simulate one step";
    private static final String METHOD_NAME_ANIMATE_PDA_SIMULATION = "Animate PDA simulation";

    private static final long serialVersionUID = -4491455653570476250L;
    private String kellerZeichen = "k";
    private static final String LAMBDA = "lambda";
    private LinkedList<String> keller;
    private ArrayList<String> input;
    private HashMap<StateTapesymbolKellersymbol, Transition> transitionsRegular = new HashMap<>();
    private HashMap<StateTapesymbolKellersymbol, Transition> transitionsLambda = new HashMap<>();
    private int headPos;
    private String initialState;
    private String currentState;
    private LinkedList<String> finalStates = new LinkedList<String>();
    @SuppressWarnings("rawtypes")
    private ArrayList[] tapes;
    private String lastCode;
    
    private String s0 = "s0";
    private String F = "s0";
    private String kSymb = "k";
    private String inputs = "a";
    private int simSteps = -1;
    private int maxNondetCalcDepth = 12;
    
    private Transition currentSimTrans = null;
    private String currentSimState = null;
    private LinkedList<String> currentSimKeller = null;

    public static String getLambda() {
        return LAMBDA;
    }
    
    public PDA(Exercise exercise) {
        super(exercise);
        this.addIgnoredField("currentSimState");
        this.addIgnoredField("accepted");
        this.addIgnoredField("kellerZeichen");
        this.addIgnoredField("keller");
        this.addIgnoredField("input");
        this.addIgnoredField("transitionsRegular");
        this.addIgnoredField("transitionsLambda");
        this.addIgnoredField("headPos");
        this.addIgnoredField("initialState");
        this.addIgnoredField("currentState");
        this.addIgnoredField("finalStates");
        this.addIgnoredField("tapes");
        this.addIgnoredField("lastCode");
        this.addIgnoredField("longTimeIDNondetSim");
    }
    
    public PDA(PDA other) {
        this(other.getExercise());
        this.currentState = other.currentState;
        this.finalStates = new LinkedList<>(other.finalStates);
        this.headPos = other.headPos;
        this.input = new ArrayList<>(other.input);
        this.keller = new LinkedList<>(other.keller);
        this.kellerZeichen = other.kellerZeichen;
        this.lastCode = other.lastCode;
        this.tapes = new ArrayList[other.tapes.length];
        for (int i = 0; i < tapes.length; i++) {
            this.tapes[i] = other.tapes[i];
        }
        this.transitionsLambda = new HashMap<>(other.transitionsLambda);
        this.transitionsRegular = new HashMap<>(other.transitionsRegular);
        this.currentSimKeller = other.currentSimKeller == null ? null : new LinkedList<>(other.currentSimKeller);
        this.currentSimState = other.currentSimState;
        this.currentSimTrans = other.currentSimTrans == null ? null : new Transition(other.currentSimTrans.from, other.currentSimTrans.to);
        this.accepted = other.accepted;
        this.simSteps = other.simSteps;
        this.maxNondetCalcDepth = other.maxNondetCalcDepth;
        this.displayMode = other.displayMode;
    }
    
    public void addFinalState(String state) {
        finalStates.add(state);
    }
    
    public void setInitialState(String initialState) {
        this.initialState = initialState;
    }
    
    public void setKellerZeichen(String kellerZeichen) {
        this.kellerZeichen = kellerZeichen;
    }
    
    public void addTransition(
            String fromState,
            String inputSymbol,
            String kellerSymbol,
            String toState,
            String kellerSymbolChain) {
        HashMap<StateTapesymbolKellersymbol, Transition> transitions = getCorrectTransitionSet(inputSymbol);
        StateTapesymbolKellersymbol stk = new StateTapesymbolKellersymbol(fromState, inputSymbol, kellerSymbol);
        Transition t = transitions.get(stk);
        
        if (t == null) {
            ArrayList<StateKellersymbols> list = new ArrayList<>();
            list.add(new StateKellersymbols(toState, kellerSymbolChain));
            t = new Transition(stk, list);
        } else {
            t.to.add(new StateKellersymbols(toState, kellerSymbolChain));
        }
                
        this.addTransition(t);
    }

    private HashMap<StateTapesymbolKellersymbol, Transition> getCorrectTransitionSet(String inputSymbol) {
        HashMap<StateTapesymbolKellersymbol, Transition> transitions = transitionsRegular;
        if (inputSymbol.equals(PDA.LAMBDA)) {
            transitions = this.transitionsLambda;
        }
        
        return transitions;
    }
    
    /**
     * Stores a new deterministic or non-deterministic transition in the HashMap.
     * If a transition with the same key already exists, the new transitions
     * additional to-pairs are added to the existing transition (making the
     * automaton possibly non-deterministic).
     * 
     * @param t  The transition to add.
     */
    public void addTransition(Transition t) {
        HashMap<StateTapesymbolKellersymbol, Transition> transitions = getCorrectTransitionSet(t.from.tapeSymbol);
        Transition exists = transitions.get(t.from);
        
        if (exists == null) {
            transitions.put(t.from, t);
        } else {
            exists.to.addAll(t.to);
        }
        
        this.states.add(t.from.state);
        for (StateKellersymbols sks : t.to) {
            this.states.add(sks.state);
        }
    }

    public void setInput(ArrayList<String> input) {
        this.input = input;
        this.tapes = new ArrayList[1];
        this.tapes[0] = this.input;
    }
    
    public void setInput(String inputAsCharacters) {
        ArrayList<String> input = new ArrayList<String>();
        
        for (int i = 0; i < inputAsCharacters.length(); i++) {
            input.add(inputAsCharacters.charAt(i) + "");
        }
        
        this.setInput(input);
    }
    
    public void reset() {
        initialize();
        this.finalStates = new LinkedList<String>();
        this.input = new ArrayList<String>();
        this.transitionsRegular.clear();
        this.transitionsLambda.clear();
    }

    private void initialize() {
        this.currentState = initialState;
        this.keller = new LinkedList<String>();
        this.keller.add(this.kellerZeichen);
        this.headPos = 0;
    }
    
    private String longTimeIDNondetSim = "TTeSST";
    private String accepted = null;
    
    @SuppressWarnings("unchecked")
    private void simulateNonDeterministic(GraphViz gv) {
//        longTimeIDNondetSim = GeneralDialog.getUniqueRandomOneTimeID();
        GeneralDialog.resetLongTimeOperationID(longTimeIDNondetSim);
        
        this.currentState = this.initialState;
        this.input = new ArrayList<String>(tapes[0]);
        try {
            this.simulateNonDeterministic(gv, "");
        } catch (LongOperationException e) {
            gv.resetDotSource();
            gv.add(PDFProcessorFactory.getPrematureInstanceOf(GraphViz.class).safetyCodeInCaseOfLargeCodeOrLongOperation(0));
            gv.setWriteProtected(true);
        }
    }

    private void simulateNonDeterministic(GraphViz gv, String idPrefix) {
        this.simulateNonDeterministicInner(gv, idPrefix, 0);
    }
    
    private void simulateNonDeterministicInner(GraphViz gv, String idPrefix, int recDepth) {
        if (!GeneralDialog.continueLongOperation(longTimeIDNondetSim)) {
            throw new LongOperationException();
        }
        
        String tape = StaticMethodsKA.printInputWithHead(this.headPos, this.input);
        String kellerTape = this.keller.toString().replace(", ", "").replace(this.kellerZeichen, "k<sub>0</sub>");
        String state = this.currentState.charAt(0) + "<sub>" + this.currentState.substring(1) + "</sub>";
        String id = "a";
        String shape = "";
        
        LinkedList<TKFS> applicable = new LinkedList<PDA.TKFS>();
        
        try {
            applicable = this.getApplicableFollowingState(
                    new StateTapesymbolKellersymbol(
                            this.currentState, this.input.get(this.headPos), this.keller.getLast()));
        } catch (Exception e) {
            Transition matches = this.transitionsLambda.get(new StateTapesymbolKellersymbol(
                    this.currentState, PDA.LAMBDA, this.keller.getLast()));
            
            if (matches != null) {
                for (StateKellersymbols sk : matches.to) {
                    applicable.add(new TKFS(PDA.LAMBDA, sk));
                }
            }
        }
        
        if (applicable.size() == 0 || this.headPos >= this.input.size()) {
            shape = "shape=rectangle,";
        }
        
        if (this.headPos >= this.input.size() && this.finalStates.contains(this.currentState)) {
            shape = "shape=rectangle,peripheries=2,";
        }
        
        gv.addln(id + idPrefix + " [" + shape + "label=<" + tape + "<BR/>" + kellerTape  + " (" + state + ")>];");
        
        if (recDepth > maxNondetCalcDepth) {
            gv.add("abcde [label=\"LARGE-TREE-CUT-OFF-AT-DEPTH-" + maxNondetCalcDepth + "\",shape=diamond];");
            return;
        }

        int i = 1;
        
        for (TKFS app : applicable) {
            PDA newKA = new PDA(this);
            newKA.applyTransition(app.inputSymbol, app.sk);
            newKA.simulateNonDeterministicInner(gv, idPrefix + "b" + i, recDepth + 1);
            gv.addln(id + idPrefix + " -> " + id + idPrefix + "b" + i + "[label=\" " + app + "\"]");
            i++;
        }
    }

    private class TKFS {
        private String inputSymbol;
        private StateKellersymbols sk;

        public TKFS(String inputSymbol, StateKellersymbols sk) {
            super();
            this.inputSymbol = inputSymbol;
            this.sk = sk;
        }

        @Override
        public String toString() {
            String symb = this.inputSymbol;
            
            if (PDA.LAMBDA.equals(inputSymbol)) {
                symb = "&#955;";
            }
            return "'" + symb + "'";
        }
    }
    
    private LinkedList<TKFS> getApplicableFollowingState(StateTapesymbolKellersymbol from) {
        LinkedList<TKFS> mapping = new LinkedList<PDA.TKFS>();
        
        try {
            for (StateKellersymbols sk : this.transitionsRegular.get(from).to) {
                mapping.add(new TKFS(from.tapeSymbol, sk));
            }
        } catch (Exception e) {}
        try {
            for (StateKellersymbols sk : this.transitionsLambda.get(new StateTapesymbolKellersymbol(from.state, PDA.LAMBDA, from.kellerSymbol)).to) {
                mapping.add(new TKFS(PDA.LAMBDA, sk));
            }
        } catch (Exception e) {}
        
        return mapping;
    }

    private void applyTransition(String inputSymbol, StateKellersymbols trans) {
        this.currentState = trans.state;
        
        this.keller.removeLast();
        
        for (int i = trans.kellersymbols.size() - 1; i >= 0; i--) {
            this.keller.add(trans.kellersymbols.get(i));
        }
        
        if (!inputSymbol.equals(PDA.LAMBDA)) {
            this.headPos++;
        }
    }
    
    public Trace simulateDeterministic(int simSteps) {
        Trace trace = new Trace();
        HashMap<StateTapesymbolKellersymbol, Transition> allTrans = this.getAllTransitions();
        int currSteps = 0;
        this.currentSimState = null;
        this.currentSimTrans = null;
        this.accepted = null;
        
        boolean found;
        while (currSteps <= simSteps && this.headPos < this.input.size()) {
            this.currentSimKeller = new LinkedList<>(this.keller);
            currSteps++;
            
            found = false;
            
            for (Transition t : allTrans.values()) {
                if (t.from.kellerSymbol.equals(this.keller.getLast())
                        && t.from.state.equals(this.currentState)
                        && (t.from.tapeSymbol.equals(this.input.get(this.headPos))
                                || t.from.tapeSymbol.equals(PDA.LAMBDA))) {
                    trace.add(new TraceElement(input, keller, t, this.headPos));
                    
                    found = true;
                    this.currentSimState = this.currentState;
                    this.currentState = t.to.get(0).state;
                    this.currentSimTrans = t;
                    
                    this.keller.removeLast();
                    
                    for (int i = t.to.get(0).kellersymbols.size() - 1; i >= 0; i--) {
                        this.keller.add(t.to.get(0).kellersymbols.get(i));
                    }
                    
                    if (!t.from.tapeSymbol.equals(PDA.LAMBDA)) {
                        this.headPos++;
                    }
                    
                    break;
                }
            }
            
            if (!found) {
                break;
            }
        }

        found = true;
        while (currSteps <= simSteps && found) {
            this.currentSimKeller = new LinkedList<>(this.keller);
            currSteps++;
            
            found = false;
            for (Transition t : allTrans.values()) {
                if (t.from.kellerSymbol.equals(this.keller.getLast())
                        && t.from.state.equals(this.currentState)
                        && t.from.tapeSymbol.equals(PDA.LAMBDA)) {
                    trace.add(new TraceElement(input, keller, t, this.headPos));
                    
                    found = true;
                    this.currentSimState = this.currentState;
                    this.currentState = t.to.get(0).state;
                    this.currentSimTrans = t;

                    this.keller.removeLast();
                    
                    for (int i = t.to.get(0).kellersymbols.size() - 1; i >= 0; i--) {
                        this.keller.add(t.to.get(0).kellersymbols.get(i));
                    }
                    
                    if (!t.from.tapeSymbol.equals(PDA.LAMBDA)) {
                        this.headPos++;
                    }
                    
                    break;
                }
            }
        }
        
        if (!found) {
            this.currentSimState = this.currentState;
            this.currentSimTrans = null;
            this.accepted = this.finalStates.contains(this.currentState) && this.headPos >= this.input.size()
                    ? "accepted"
                    : "not accepted";
            
            if (this.accepted.equals("accepted")) {
                trace.add(TraceElement.accepted(input, currentSimKeller, this.headPos));
            } else {
                trace.add(TraceElement.notAccepted(input, currentSimKeller, this.headPos));
            }
        }
        
        return trace;
    }
    
    private boolean isTerminatedDetSim() {
        return this.accepted != null;
    }
    
    private Transition createTrans(String tStr) {
        String tString = tStr.replaceAll("\\(", "").replaceAll("\\)", "");
        String[] from = tString.split("=>")[0].split(",");
        String[] to = tString.split("=>")[1].split(",");
        
        String toKeller = to[1];
        
        if (toKeller.equals(PDA.LAMBDA)) {
            toKeller = "";
        }
        
        ArrayList<StateKellersymbols> list = new ArrayList<>();
        list.add(new StateKellersymbols(to[0], toKeller));
        
        Transition t = new Transition(
                new StateTapesymbolKellersymbol(from[0], from[1], from[2]), 
                list);
        
        return t;
    }

    private boolean isDeterministic() {
        boolean allSingleTransDet = this.transitionsRegular.values().stream().allMatch(t -> t.isDeterministic());
        boolean allSingleTransLambdaDet = this.transitionsLambda.values().stream().allMatch(t -> t.isDeterministic());
        
        if (!allSingleTransDet) {
            return false;
        }
        if (!allSingleTransLambdaDet) {
            return false;
        }
        
        boolean isLambdaNondet = this.transitionsRegular.values().stream().anyMatch(
                t -> this.transitionsLambda.containsKey(
                        new StateTapesymbolKellersymbol(t.from.state, PDA.LAMBDA, t.from.kellerSymbol)));
        
        return !isLambdaNondet;
    }
    
    @Override
    public String[] getExampleScripts() {
        String s0 = "pda:\n" + 
                "(s1, 1, 0) => (s2, lambda);\n" + 
                "(s3, 1, b) => (s3, b1);\n" + 
                "(s3, 0, 1) => (s3, b);\n" + 
                "(s3, 0, b) => (s3, lambda);\n" + 
                "(s1, 0, 0) => (s1, 00);\n" + 
                "(s3, 1, 1) => (s3, 11);\n" + 
                "(s3, lambda, k) => (s0, k);\n" + 
                "(s1, lambda, k) => (s0, k);\n" + 
                "(s2, lambda, k) => (s3, bk);\n" + 
                "(s0, 1, k) => (s3, 1k);\n" + 
                "(s0, 0, k) => (s1, 0k);\n" + 
                "(s2, lambda, 0) => (s1, lambda);\n" + 
                "--declarations--\n" + 
                "e=#n#;\n" + 
                "s0=s0;\n" + 
                "F=s0;\n" + 
                "kSymb=k;\n" + 
                "inputs=000101010;\n" + 
                "simSteps=0\n" + 
                "--declarations-end--";
        
        String s1 = "pda:\n" + 
                "(s0, 0, k) => (s1, 0k);\n" + 
                "(s0, 1, k) => (s3, 1k);\n" + 
                "(s1, 0, 0) => (s1, 00);\n" + 
                "(s1, 1, 0) => (s2, lambda);\n" + 
                "(s2, lambda, 0) => (s1, lambda);\n" + 
                "(s1, lambda, k) => (s0, k);\n" + 
                "(s2, lambda, k) => (s3, bk);\n" + 
                "(s3, 1, 1) => (s3, 11);\n" + 
                "(s3, 0, 1) => (s3, b);\n" + 
                "(s3, 0, b) => (s3, lambda);\n" + 
                "(s3, 1, b) => (s3, b1);\n" + 
                "(s3, lambda, k) => (s0, k);\n" + 
                "--declarations--\n" + 
                "s0=s0;\n" + 
                "F=s0;\n" + 
                "kSymb=k;\n" + 
                "inputs=001000110001, 1111100;\n" +
                "displayMode=1;\n" +
                "--declarations-end--";
        
        String s2 = "pda:\n" + 
                "(s0, 0, k) => (s1, 0k);\n" + 
                "(s1, 0,0) => (s1, 00);\n" + 
                "(s1, 0,0) =>(s2, lambda);\n" + 
                "(s1, 0,0) =>(s4, 0);\n" + 
                "(s1, 1,0) => (s2, lambda);\n" + 
                "(s1, 1,0) => (s4, 0);\n" + 
                "(s2, 0,0) => (s2, lambda);\n" + 
                "(s2, 1,0) => (s2, lambda);\n" + 
                "(s2, lambda,k) => (se,k);\n" + 
                "(s4, 0,0) => (s3, lambda);\n" + 
                "(s4, 1,0) => (s3, lambda);\n" + 
                "(s3, 0,0) => (s4, 0);\n" + 
                "(s3, 1,0) => (s4, 0);\n" + 
                "(s3, lambda,k) => (se, k);\n" + 
                "--declarations--\n" + 
                "s0=s0;\n" + 
                "F=se;\n" + 
                "kSymb=k;\n" + 
                "inputs=001110\n" + 
                "--declarations-end--";

        return new String[] {s0, s2, s1};
    }

    @Override
    public boolean isAcceptableScript(String code) {
        return (code + "").toLowerCase().startsWith("pda:");
    }

    @SuppressWarnings("unchecked")
    @Override
    public void createInstanceFromScript(String codeRaw, RepresentableAsPDF father) {
        // Limit input length and number of inputs, in web mode even more than in desktop mode.
        int inputMaxLength = MainLink.isApplicationOriginDesktop() ? 100 : 15;
        int inputMaxNum = MainLink.isApplicationOriginDesktop() ? 10 : 5;
        this.applyDeclarationsAndPreprocessors(codeRaw, father, 0);
        String code = this.getScriptWithoutPrepAndDeclAndPreamble();
        code = this.decollapseRules(code);
        
        this.lastCode = code;

        this.reset();
        this.initialState = s0;
        this.finalStates = new LinkedList<String>();
        this.kellerZeichen = kSymb;
        String[] tapesTemp = inputs.split(",");
        this.tapes = new ArrayList[Math.min(tapesTemp.length, inputMaxNum)];
        
        // Limit number of inputs.
        if (tapesTemp.length >= inputMaxNum) {
            GlobalVariables.getParameters().logError("Sorry, only " + inputMaxNum + " inputs allowed at once for PDA.");
        }
        
        for (int j = 0; j < this.tapes.length; j++) {
            this.tapes[j] = new ArrayList<String>(tapesTemp[j].length());
            for (int i = 0; i < tapesTemp[j].length(); i++) {
                this.tapes[j].add(tapesTemp[j].charAt(i) + "");

                // Limit length of single input.
                if (i >= inputMaxLength) {
                    GlobalVariables.getParameters().logError("Sorry, PDA input\n'" + tapesTemp[j] + "'\ntoo long (" + tapesTemp[j].length() + "); cutting off at " + inputMaxLength + ".");
                    break;
                }
            }
        }

        this.input = this.tapes[0];
        
        for (String s : F.split(",")) {
            this.finalStates.add(s);
        }
        
        String[] transitions = StaticMethods.removeWhitespaces(code).split(";");
        for (String tString : transitions) {
            Transition t = createTrans(tString);
            this.addTransition(t);
        }

        if (!this.isInClassicMode()) {
            this.initialize();
            this.input = this.tapes[0];
            this.simulateDeterministic(simSteps);
        }
        
        GlobalVariables.getParameters().logDebug(getDeterministicStateString());
    }
    
    private String getDeterministicStateString() {
        String s = "";
        
        if (!this.isDeterministic()) {
            s += ("Nondeterministic " + this.getEnglishName().toUpperCase() 
                    + " - these are the nondeterministic transitions: ");
            LinkedList<Transition> nondetTrans = StaticMethodsKA.getNondetTrans(
                    this.transitionsRegular, this.transitionsLambda, PDA.LAMBDA);
            s += (nondetTrans.toString());
        } else {
            s += ("Deterministic " + this.getEnglishName().toUpperCase()) + ".";
        }
        
        return s;
    }

    private String getLatexTableInput() {
        String cs = "";
        
        for (@SuppressWarnings("unused") String s : this.input) {
            cs += "c";
        }
        
        String latex = (this.input.size() > 10 ? "\\resizebox{0.7\\linewidth}{!}{\n" : "{\\huge\n") 
                + "\\begin{tabular}{" + cs + "}\n"
                + "\\hline\n";
        
        for (int i = 0; i < this.input.size(); i++) {
            String cellColor = this.headPos == i + 1 && !this.isTerminatedDetSim()
                    ? "\\cellcolor{gray!25}"
                    : "";
            
            latex += i == this.input.size() - 1 
                    ? "\\multicolumn{1}{|c|}{~" + cellColor + "$" + this.input.get(i) + "$~~} " + "\\\\" 
                    : "\\multicolumn{1}{|c}{~" + cellColor + "$" + this.input.get(i) + "$~~} " + "& ";
        }

        latex += "\\hline\n";

        for (int i = 0; i < this.input.size(); i++) {
            String arrow = this.headPos == i + 1 && !this.isTerminatedDetSim()
                    ? "$\\Uparrow$"
                    : "\\vphantom{$\\Uparrow$}";
            
            latex += i == this.input.size() - 1 
                    ? arrow + "\\\\" 
                    : arrow + "& ";
        }

        
        latex += "\\end{tabular}}\n\n";
        return latex;
    }

    private String getLatexTableStack() {
        String latex = "\\huge\\begin{tabular}[b]{c|c|}\n" + 
                "& \\\\\n" + 
                "\\cline{2-2}";
        
        List<String> keller = new LinkedList<>(this.currentSimKeller);
        Collections.reverse(keller);

        int i = 0;
        for (String symb : keller) {
            if (i == 0) {
                latex += "$\\Rightarrow$  & \\cellcolor{gray!25}~$" + symb.replace(this.kSymb, "k_0") + "$~~\\\\";
            } else {
                latex += "& ~$" + symb.replace(this.kSymb, "k_0") + "$~~\\\\";
            }
            
            latex += "\\cline{2-2}";
            i++;
        }
        
        latex += "\\end{tabular}";
        
        return latex;
    }
    
    private int displayMode = 0;
    
    @Override
    public PDFProcessor generatePDFscript(String pdfPath) {
        super.generatePDFscript(pdfPath);
        
        PDFProcessor pdf;
        
        if (isInClassicMode()) {
            if (this.tapes.length == 1 && this.isDeterministic()) {
                pdf = new LaTeXPDF(
                        LaTeXCommands.PREAMBLE_CROP_PAGE
                            + this.initializeAndGetTraceLatex()
                            + "\n" + LaTeXCommands.POSTAMBLE_STANDARD, 
                        pdfPath,
                        this);
            } else {
                pdf = generateClassicView(pdfPath);
            }
        } else {
            String preprocessor = generatePreprocessorScript();

            String latexCode = LaTeXCommands.SHORT_CROP_PAGE_PREVIEV // LaTeXCommands.PREAMBLE_CROP_PAGE_PREVIEW
                    + this.getLatexTableInput()
                    + "\\fbox{"
                    + INSCR_BEG_TAG_FOR_INTERNAL_USAGE
                    + "0.7|dot:\n" + preprocessor
                    + INSCR_END_TAG_FOR_INTERNAL_USAGE
                    + "}"
                    + this.getLatexTableStack();
//                    + LaTeXCommands.POSTAMBLE_STANDARD;
            
            PDFProcessor latexPDF = new LaTeXPDF(latexCode, pdfPath, this);
            
            pdf = latexPDF;
        }
        
        if (displayMode % 3 == 1) {
            String restScript = pdf.getPlainPDFScript();
            pdf = new LaTeXPDF(
                    LaTeXCommands.SHORT_CROP_PAGE_PREVIEV
                        + StaticMethodsKA.getTransitionsLatex(
                            this.getAllTransitions(),
                            this.kellerZeichen, 
                            PDA.LAMBDA,
                            this.isDeterministic())
                        + "\n\\bigbreak" + INSCR_BEG_TAG_FOR_INTERNAL_USAGE + "-1|"
                        + restScript
                        + INSCR_END_TAG_FOR_INTERNAL_USAGE + "\n",
//                        + LaTeXCommands.POSTAMBLE_STANDARD, 
                        pdfPath,
                        this);
        }

        if (displayMode % 3 == 2) {
//            String restScript = pdf.getPlainPDFScript();
            pdf = new LaTeXPDF(
                    LaTeXCommands.SHORT_CROP_PAGE_PREVIEV
                        + StaticMethodsKA.getTransitionsLatex(
                            this.getAllTransitions(),
                            this.kellerZeichen, 
                            PDA.LAMBDA,
                            this.isDeterministic()),
//                        + "\n\\bigbreak" + INSCRIPT_BEGIN_TAG + "-1|"
//                        + restScript
//                        + INSCRIPT_END_TAG + "\n"
//                        + LaTeXCommands.POSTAMBLE_STANDARD, 
                        pdfPath,
                        this);
        }

        return pdf;
    }

    private boolean isInClassicMode() {
        return this.simSteps < 0 || this.tapes == null || !this.isDeterministic();
    }

    private String generatePreprocessorScript() {
        String gv = "";
        
        gv += ("digraph G {\n");
        gv += ("node [shape = doublecircle];\n" + this.finalStates.toString().replace("[", "").replace("]", "") + ";\n");
        gv += ("node [shape = circle];\n");
        
        HashSet<StatePair> allTrans = new HashSet<>();
        
        for (Transition t : this.getAllTransitions().values()) {
            for (StateKellersymbols sks : t.to) {
                allTrans.add(new StatePair(t.from.state, sks.state));
            }
        }
        
        for (String s1 : this.states) {
            String filled = "";
            
            if (s1.equals(this.currentSimState)) {
                filled = ",style=filled";
            }
            
            gv += (s1 + " [label=<" + s1.charAt(0) + "<sub>" + s1.substring(1) + "</sub>>" + filled + "];\n");
            
            for (String s2 : this.states) {
                if (allTrans.contains(new StatePair(s1, s2))) {
                    if (this.currentSimTrans != null
                            && this.currentSimTrans.from.state.equals(s1)
                            && this.currentSimTrans.to.get(0).state.equals(s2)) {
                        gv += (s1 + "->" + s2 + "[penwidth=3];\n");
                    } else {
                        gv += (s1 + "->" + s2 + ";\n");
                    }
                } else {
                    gv += (s1 + "->" + s2 + " [style=invis];\n");
                }
            }
        }

        gv += ("trans [shape=rectangle,style=filled,label=<" 
                + plainDetTrans(this.currentSimTrans) 
                + ">];\n");
        gv += ("}");
        
        return gv;
    }
    
    private String plainDetTrans(Transition t) {
        if (t == null) {
            if (this.accepted == null) {
                return "no transition";
            } else {
                return this.accepted;
            }
        }
        
        String s1Plain = t.from.state;
        String s2Plain = t.to.get(0).state;
        String s1 = s1Plain.charAt(0) + "<sub>" + s1Plain.substring(1) + "</sub>";
        String s2 = s2Plain.charAt(0) + "<sub>" + s2Plain.substring(1) + "</sub>";
        String ksymbs = t.to.get(0).kellersymbols.toString().replace("[", "").replace("]", "").replace(", ", "");
        
        String arrow = "&#8594;";
        String string = "(" + s1 + ", " + t.from.tapeSymbol + ", " + t.from.kellerSymbol.replace(this.kSymb, "k<sub>0</sub>") + ")"
                + " " + arrow  + " (" + s2 + ", " 
                + (ksymbs.length() == 0 ? "lambda" : ksymbs.replace(this.kSymb, "k<sub>0</sub>")) + ")";
        
        return string.replace("lambda", "&lambda;");
    }

    private HashSet<String> states = new HashSet<>();

    @SuppressWarnings("unchecked")
    private GraphViz generateClassicView(String pdfPath) {
        GraphViz gv = new GraphViz(pdfPath, this);
        
        try {
            gv.addln("digraph G {");

            if (this.tapes != null && this.tapes.length > 0) {
                if (this.isDeterministic()) {
                    for (int j = 0; j < this.tapes.length; j++) {
                        this.initialize();
                        this.input = this.tapes[j];
                        
                        int i = 1;
                        Trace trace = this.simulateDeterministic(Integer.MAX_VALUE);
                        gv.addln("a" + j + " [shape=boxed, label=\"");
                        for (TraceElement el : trace) {
                            String transition;
                            if (TraceElement.isAccepted(el)) {
                                transition = "accepted";
                            } else if (TraceElement.isNotAccepted(el)) {
                                transition = "not accepted";
                            } else {
                                transition = (el.getTransition() + "").toString().replace(
                                        "=>", " =\\> ").replace("[", "").replace("]", "");
                            }
                            
                            gv.addln("("
                                    + i
                                    + ")  "
                                    + StaticMethodsKA.printInputWithHead(el.getHeadPos(), this.input)
                                    + "  ||  "
                                    + el.getKeller()
                                    + "  ||  "
                                    + transition + "\\n");
                            i++;
                        }
                        gv.addln("\"]");
                    }
                } else {
                    this.simulateNonDeterministic(gv);
                }

                this.input = this.tapes[0];
            }

            gv.addln("};");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return gv;
    }
    
    private HashMap<StateTapesymbolKellersymbol, Transition> getAllTransitions() {
        HashMap<StateTapesymbolKellersymbol, Transition> trans = new HashMap<>(
                this.transitionsLambda.size() + this.transitionsRegular.size());
        
        trans.putAll(transitionsLambda);
        trans.putAll(transitionsRegular);
        
        return trans;
    }
    
    @SuppressWarnings("unchecked")
    @ConversionMethod
    public String additionalPDAInformation() {
        String s = "";
        try {
            this.initialize();
            this.input = this.tapes[0];

            boolean det = this.isDeterministic();
            
            Trace t = this.simulateDeterministic(Integer.MAX_VALUE);
            s = "PDA information.\n\n";
            s += getDeterministicStateString().replace(":",  ":\n");
            s += "\n\n--- KA-Definition:\n";
            s += StaticMethodsKA.getTransitionsLatex(this.getAllTransitions(), this.kellerZeichen, PDA.LAMBDA, det);
            s += "\n\n--- Calculation as table (LaTeXCode):\n";
            s += StaticMethodsKA.getTraceLatex(t, kellerZeichen, LAMBDA, det);
            s += "\n\n--- Calculation as configuration trace (LaTeXCode):\n";
            s += StaticMethodsKA.getConfigurationTraceLatex(t, kellerZeichen);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return s;
    }

    @Override
    public JComponent getAdditionalInfo() {
        JPanel panel = new JPanel(new MigLayout("wrap 1"));
        FancyJLabel infoTitle = new FancyJLabel(this.isDeterministic()? "Deterministic PDA" : "Nondeterministic PDA");
        
        panel.add(infoTitle);
        panel.add(super.getAdditionalInfo());
        
        return panel;
    }

    @ConversionMethod(plainText = false)
    public String createLatexCalculationTraceScript() {
        if (this.simSteps < 0) {
            this.simSteps = 0;
        } else {
            this.simSteps = -1;
        }
        
        return this.createScriptFromInstance();
    }

    @ConversionMethod(plainText = false)
    public String createLatexTransitionsScript() {
        this.displayMode = (this.displayMode + 1) % 3;
        return this.createScriptFromInstance();
    }

    @SuppressWarnings("unchecked")
    private String initializeAndGetTraceLatex() {
        this.initialize();
        this.input = this.tapes[0];
        Trace t = this.simulateDeterministic(Integer.MAX_VALUE);
        return StaticMethodsKA.getTraceLatex(t, kellerZeichen, LAMBDA, this.isDeterministic());
    }
    
    /*
     * pda: s0: se: k: 001110:
     *   (s0, 0, k) => (s1, 0k);
     *   (s1 ,0,0) => (s1,00);
     *   (s1 ,0,0) =>(s2 ,lambda);
     *   (s1 ,0,0) =>(s4 ,0);
     *   (s1 ,1,0) => (s2,lambda);
     *   (s1 ,1,0) => (s4 ,0);
     *   (s2 ,0,0) => (s2,lambda);
     *   (s2 ,1,0) => (s2,lambda);
     *   (s2 ,lambda,k) => (se,k);
     *   (s4 ,0,0) => (s3,lambda);
     *   (s4 ,1,0) => (s3,lambda);
     *   (s3 ,0,0) => (s4,0);
     *   (s3 ,1,0) => (s4,0);
     *   (s3 ,lambda,k) => (se,k);
     */
    @Override
    public String createScriptFromInstance() {
        String s = "pda:\n";
        
        for (Transition t : this.getAllTransitions().values()) {
            for (StateKellersymbols sk : t.to) {
                s += t.from + " => " + sk.toString().replace("]", "").replace("[", "") + ";\n";
            }
        }
        
        this.F = StaticMethods.formatCollection(this.finalStates);
        this.s0 = this.initialState;
        
        if (this.tapes == null) {
            this.tapes = new ArrayList[0];
        }
        
        for (int i = 0; i < this.tapes.length; i++) {
            if (this.tapes[i] == null) {
                this.tapes[i] = new ArrayList<>();
            }
        }
        
        @SuppressWarnings("unchecked")
        LinkedList<ArrayList<String>> myTapes = new LinkedList<ArrayList<String>>(Arrays.asList((ArrayList<String>[]) this.tapes));
        
        this.inputs = (this.tapes != null && this.tapes.length > 0) 
                ? StaticMethods.formatCollection(myTapes, true) 
                : "null";
        
        s += this.generateCompleteDeclarationsBlock();
        
        return s;
    }
    
    public String getKellerZeichen() {
        return this.kellerZeichen;
    }

    @Override
    public Class<? extends PDFProcessor> getPDFProcessorClass() {
        return this.isInClassicMode() ? GraphViz.class : LaTeXPDF.class;
    }

    @ConversionMethod(plainText = false)
    public String simulateOneStep() {
        this.simSteps++;
        if (this.currentSimTrans == null) {
            this.simSteps = 0;
        }
        return this.createScriptFromInstance();
    }

    @ConversionMethod(plainText = false)
    public String animateSim() {
        String loopStr = "\n" + RepresentableDefault.createStdAnimation(
                "sim", "sim", "maxSteps");
        return createCompleteAnimationScript(loopStr);
    }
    
    @ConversionMethod
    public String getNumStates() {
        String string = this.states.size() + "";
        return string;
    }

    @ConversionMethod
    public String getMaxSimSteps() {
        PDA newpda = new PDA(this);
//        newpda.reset();
//        newpda.input = newpda.tapes[0];
        int max = newpda.simulateDeterministic(255).size();
        return max + "";
    }
    
    @Override
    public HashMap<String, MethodWrapper> getDynamicMethods() {
        HashMap<String, MethodWrapper> methods = super.getDynamicMethods();

        String addInfoName = "Show additional information";
        String definitionLatexName = "Show PDA definition";
        String calculationName = "Toggle complete calculation trace";
        String addInfoName_G = "Zeige zusätzliche Informationen";
        String definitionLatexName_G = "Zeige Definition";
        String calculationName_G = "Zeige ganzen Berechnungsablauf";
        String simulateOneStep = SIMULATE_ONE_STEP_METHOD_NAME;
        String simulateOneStep_G = "Simuliere einen Schritt";
        String animateSimName = METHOD_NAME_ANIMATE_PDA_SIMULATION;
        String animateSimName_G = "Animiere KA-Simulation";
        String numStates = "states";
        String maxSimStepsName = "maxSteps";
        
        try {
            MethodWrapper mwh0 = new MethodWrapper(
                    this.getClass().getMethod("getNumStates"), 
                    this,
                    numStates);

            MethodWrapper mwh2 = new MethodWrapper(
                    this.getClass().getMethod("getMaxSimSteps"), 
                    this, 
                    maxSimStepsName);
            
            MethodWrapper mwh8;
            String tooltipToShowInGUI2 = "Create a standard animation simulating a given input.";
            String tooltipToShowInGIU2_G = "Standardanimation zur Simulation eines gegebenen Inputworts.";

            // Animation from Simulation, input already given.
            mwh8 = new MethodWrapper(
                    this.getClass().getMethod("animateSim"),
                    PDA.class, // Target script class. Important to set correctly!
                    this,
                    tooltipToShowInGUI2,
                    tooltipToShowInGIU2_G,
                    animateSimName,
                    animateSimName_G);
            
            mwh8.setDisplayLevel(5);
            if (this.tapes != null && this.tapes.length > 1 || !this.isDeterministic()) {
                mwh8.setMethodButtonEnabled(false);
            }
            if (this.tapes != null && this.tapes.length > 1) {
                mwh8.setTooltip(mwh8.getTooltip() + " (Only one input allowed.)");
                mwh8.setTooltip_G(mwh8.getTooltip_G() + " (Nur ein Eingabewort erlaubt.)");
            }
            if (!this.isDeterministic()) {
                mwh8.setTooltip(mwh8.getTooltip() + " (Only deterministic PDA allowed.)");
                mwh8.setTooltip_G(mwh8.getTooltip_G() + " (Nur deterministische Kellerautomaten erlaubt.)");
            }
            
            MethodWrapper mw0 = new MethodWrapper(
                    this.getClass().getMethod("additionalPDAInformation"), 
                    (Class<? extends RepresentableAsPDF>) null, 
                    this, 
                    "Show non-deterministic transitions, calculation trace and others.",
                    "Zeige nichtdeterministische Transitionen, Berechnungsschritte u.a.",
                    addInfoName,
                    addInfoName_G);
            mw0.setReturnValueIsScript(false);
            
            MethodWrapper mw1 = new MethodWrapper(
                    this.getClass().getMethod("createLatexTransitionsScript"),
                    LaTeX.class, // Target script class. Important to set correctly!
                    this,
                    "Show/hide the definition of this PDA",
                    "Zeige/verstecke die Definition des Kellerautomaten",
                    definitionLatexName,
                    definitionLatexName_G);
            MethodWrapper mw2 = new MethodWrapper(
                    this.getClass().getMethod("createLatexCalculationTraceScript"),
                    LaTeX.class, // Target script class. Important to set correctly!
                    this,
                    "Toggles between complete calculation trace and stepwise automaton animation",
                    "Wechselt zwischen der Anzeige des kompletten Berechnungsablaufs und der schrittweisen Animation",
                    calculationName,
                    calculationName_G);
            MethodWrapper mw3 = new MethodWrapper(
                    this.getClass().getMethod("simulateOneStep"),
                    this.getClass(), // Target script class. Important to set correctly!
                    this,
                    "Simulate for one step only. Note that only one input word can be simulated at once",
                    "Simuliere diesen Kellerautomaten für einen Schritt (nur ein Eingabewort auf einmal)",
                    simulateOneStep,
                    simulateOneStep_G);

            if (!this.isDeterministic()) {
                String string = " (only available for deterministic PDAs)";
                String string_G = " (nur für deterministische Kellerautomaten verfügbar)";

                mw2.setMethodButtonEnabled(false);
                mw2.setTooltip(mw2.getTooltip() + string);
                mw2.setTooltip_G(mw2.getTooltip_G() + string_G);

                mw3.setMethodButtonEnabled(false);
                mw3.setTooltip(mw3.getTooltip() + string);
                mw3.setTooltip_G(mw3.getTooltip_G() + string_G);
            }
            
            mw0.setDisplayLevelName(2, "Display modes", "Anzeigemodi");
            mw1.setDisplayLevelName(2, "Display modes", "Anzeigemodi");
            mw2.setDisplayLevelName(2, "Display modes", "Anzeigemodi");
            mw3.setDisplayLevelName(2, "Display modes", "Anzeigemodi");
            
            methods.put(numStates, mwh0);
            methods.put(maxSimStepsName, mwh2);
            methods.put(animateSimName, mwh8);
            methods.put(addInfoName, mw0);
            methods.put(definitionLatexName, mw1);
            methods.put(calculationName, mw2);
            methods.put(simulateOneStep, mw3);
        } catch (SecurityException | NoSuchMethodException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        
        return methods;
    }
    
    @Override
    public String getGermanName() {
        return "Kellerautomat";
    }
    
    @Override
    public String getModeDependentInfo(String mode, boolean english) {
        if (mode.equals(ConvenienceMethods.INFO_II_MODE_NAME)) {
            return ConvenienceMethods.createInfo2ModeString(
                    3, 
                    5, 
                    1, 
                    "http://www.dasinfobuch.de/links/Kellerautomaten.html",
                    VFPVariables.BASE_QA_ADDRESS + "?qa=350&qa_1=band-i-kapitel-5",
                    english
                    );
        }

        return "";
    }
    
    @Override
    public HashMap<String, String> getMetaProperties() {
        String className = this.getClass().getSimpleName();
        HashMap<String, String> metaProperties = super.getMetaProperties();
        boolean deterministic = (this.transitionsLambda != null && this.transitionsRegular != null) 
                ? this.isDeterministic() : false;
        
        int numTransReg = this.transitionsRegular == null ? 0 : this.transitionsRegular.size();
        int numTransLambda = this.transitionsLambda == null ? 0 : this.transitionsLambda.size();
        
        metaProperties.put(className + "_finalStates", this.F);
        metaProperties.put(className + "_initialState", this.s0);
        metaProperties.put(className + "_inputs", this.inputs);
        metaProperties.put(className + "_stackSymbol", this.kellerZeichen);
        metaProperties.put(className + "_deterministic", deterministic + "");
        metaProperties.put(className + "_numRegularTransitions", numTransReg + "");
        metaProperties.put(className + "_numLambdaTransitions", numTransLambda + "");

        return metaProperties;
    }
    
    @Override
    public Collection<PDFProcessor> getPossiblePDFProcessorClasses() {
        return PDFProcessorFactory.allWebPDFProcessors();
    }
    
    @Override
    public HashMap<String, String> getMethodNameAbbreviations() {
        HashMap<String, String> methodNameAbbreviations = super.getMethodNameAbbreviations();
        
        methodNameAbbreviations.put("sim", SIMULATE_ONE_STEP_METHOD_NAME);
        methodNameAbbreviations.put("animate", METHOD_NAME_ANIMATE_PDA_SIMULATION);

        return methodNameAbbreviations;
    }
}
