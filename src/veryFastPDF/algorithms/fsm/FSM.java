/*
 * File name:        FSM.java (package eas.miscellaneous.graphToPDF)
 * Author(s):        lko
 * Java version:     7.0
 * Generation date:  10.05.2013 (21:35:12)
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

package veryFastPDF.algorithms.fsm;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.JPanel;

import eas.GlobalVariables;
import eas.miscellaneous.StaticMethods;
import eas.miscellaneous.convenience.GeneralDialog;
import mainServlet.WebLink;
import net.miginfocom.swing.MigLayout;
import veryFastPDF.algorithms.fsm.script.FSMScript;
import veryFastPDF.algorithms.fsm.script.FSMScriptNames;
import veryFastPDF.algorithms.grammars.Grammar;
import veryFastPDF.algorithms.grammars.Nonterminal;
import veryFastPDF.algorithms.grammars.Rule;
import veryFastPDF.algorithms.grammars.Symbol;
import veryFastPDF.algorithms.grammars.Terminal;
import veryFastPDF.algorithms.grammars.Word;
import veryFastPDF.algorithms.latex.LaTeX;
import veryFastPDF.algorithms.latex.LaTeXCommands;
import veryFastPDF.algorithms.pda.PDA;
import veryFastPDF.algorithms.pda.StateKellersymbols;
import veryFastPDF.algorithms.pda.StateTapesymbolKellersymbol;
import veryFastPDF.algorithms.regEx.RegEx;
import veryFastPDF.algorithms.regEx.RegExCharacter;
import veryFastPDF.algorithms.regEx.RegExEmpty;
import veryFastPDF.algorithms.regEx.RegExLambda;
import veryFastPDF.algorithms.regEx.RegularExpression;
import veryFastPDF.algorithms.turing.Turing;
import veryFastPDF.pdfProcessors.GraphViz;
import veryFastPDF.pdfProcessors.LaTeXPDF;
import veryFastPDF.pdfProcessors.PDFProcessor;
import veryFastPDF.plugin.FancyJButton;
import veryFastPDF.plugin.FancyJLabel;
import veryFastPDF.script.ConversionMethod;
import veryFastPDF.script.Exercise;
import veryFastPDF.script.MethodWrapper;
import veryFastPDF.script.RepresentableAsPDF;
import veryFastPDF.script.RepresentableDefault;
import veryFastPDF.script.exceptionHandling.LongOperationException;
import veryFastPDF.web.ConvenienceMethods;
import veryFastPDF.web.Webproof;

/**
 * @author lko
 */
@Webproof(useInProductiveMode = true)
public class FSM extends RepresentableDefault {

    private static final String TOGGLE_MINIMIZATION_TABLE_METHOD_NAME = "Toggle minimization table";
    private static final String METHOD_NAME_ANIMATE_FSM_SIMULATION = "Animate FSM simulation";
    private static final String REGULAR_EXPRESSION_PLAIN_METHOD_NAME = "regexpPlain";
    private static final String REGULAR_EXPRESSION_METHOD_NAME = "Regular Expression";
    private static final String SIMULATE_ONE_STEP_METHOD_NAME = "Simulate one step";
    private static final String DETERMINIZE_METHOD_NAME = "Determinize";
    private static final String MINIMIZE_METHOD_NAME = "Minimize";
    private static final String RANDOMIZE_METHOD_NAME = "Randomize";
    private static final String RANDOMIZE_D_METHOD_NAME = RANDOMIZE_METHOD_NAME + " (seed)";

    private static final long serialVersionUID = -3116959725544694303L;
    public static final HashMap<String, String> STANDARD_MAPPING = fillStandardMapping();
    
    private static HashMap<String, String> fillStandardMapping() {
        HashMap<String, String> mapping = new HashMap<String, String>();
        mapping = new HashMap<String, String>();

        for (int i = 0; i < 1000; i++) {
            mapping.put("S" + i, "<S<SUB>" + i + "</SUB>>");
            mapping.put("s" + i, "<s<SUB>" + i + "</SUB>>");
            mapping.put("A" + i, "<A<SUB>" + i + "</SUB>>");
            mapping.put("B" + i, "<B<SUB>" + i + "</SUB>>");
            mapping.put("S0" + i, "<S<SUB>0" + i + "</SUB>>"); // Capture one leading zero.
            mapping.put("s0" + i, "<s<SUB>0" + i + "</SUB>>");
//            mapping.put("s'" + i, "<s'<SUB>" + i + "</SUB>>");
//            mapping.put("S'" + i, "<S'<SUB>" + i + "</SUB>>");
        }
        
        mapping.put("s000", "<s<SUB>000</SUB>>");
        mapping.put("s001", "<s<SUB>001</SUB>>");
        mapping.put("s010", "<s<SUB>010</SUB>>");
        mapping.put("s011", "<s<SUB>011</SUB>>");
        mapping.put("s100", "<s<SUB>100</SUB>>");
        mapping.put("s101", "<s<SUB>101</SUB>>");
        mapping.put("s110", "<s<SUB>110</SUB>>");
        mapping.put("s111", "<s<SUB>111</SUB>>");

        mapping.put("sF", "<s<SUB>F</SUB>>");
        mapping.put("sE", "<s<SUB>E</SUB>>");
        
        mapping.put("strue", "<s<SUB>true</SUB>>");
        mapping.put("sfalse", "<s<SUB>false</SUB>>");
        
        return mapping;
    }
    
    private static String[] examplesStatic;
    
    private String initialState;
    private HashSet<String> finalStates;
    private HashSet<String> singleStates;
    private LinkedList<Transition> transitions;
    
    private HashSet<String> currentStates = new HashSet<>();
    private HashSet<String> nextStates = new HashSet<>();
    private int simulateToStep = -1;
    private String input;

    private String s0 = "s0";
    private String F = "s0";
    private int displayMode = 0; // 0 only FSM, 1 with minimization, 2 only minimization
    private boolean showMinimizedFSM = false;
    private boolean showDeterministicFSM = false;
    public static final String NO_FINAL_STATES = "null";
    
    public FSM(Exercise exercise) {
        this(0, 0, 2, exercise);
    }
    
    public FSM(String initialSt, String[] finalSt, Transition[] trans, Exercise exercise) {
        this(initialSt, finalSt, new String[0], trans, exercise);    
    }
    
    public FSM(String initialSt, String[] finalSt, String[] singleSt, Transition[] trans, Exercise exercise) {
        this(initialSt, getHashSet(finalSt), getHashSet(singleSt), getList(trans), exercise);
    }

    public FSM(FSM other) {
        this(other.initialState, new HashSet<>(other.finalStates), new HashSet<>(other.singleStates), copyTrans(other.transitions), other.getExercise());
        this.input = other.input;
//        this.currentStates = new HashSet<>(other.currentStates);
//        this.nextStates = new HashSet<>(other.nextStates);
        this.simulateToStep = other.simulateToStep;
        this.F = other.F;
        this.s0 = other.s0;
        this.displayMode = other.displayMode;
        this.showDeterministicFSM = other.showDeterministicFSM;
        this.showMinimizedFSM = other.showMinimizedFSM;
    }

    public FSM(
            String initialSt, 
            Collection<String> finalSt, 
            Collection<String> singleSt, 
            Collection<Transition> trans, 
            Exercise exercise) {
        super(exercise);
        this.resetSimulation();
        this.output = "";
        this.setIgnoredFields();
        this.initialState = initialSt;
        this.finalStates = new HashSet<String>(finalSt);
        this.singleStates = new HashSet<String>(singleSt);
        this.transitions = new LinkedList<Transition>(trans);
    }
    
    /**
     * Creates a new random FSM.
     */
    public FSM(int numStates, int numTrans, int numInputSymbols, Exercise exercise) {
        super(exercise);
        this.setIgnoredFields();
        String[] symbols =  new String[] {"a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m", "n", "o"};
        
        HashSet<String> alphabet = new HashSet<String>();
        
        for (int i = 0; i < numInputSymbols; i++) {
            alphabet.add(symbols[i]);
        }
        
        this.randomizeFSMnonDet(numStates, numTrans, alphabet, this.randomGen);
    }

    /**
     * Adds fields to the "ignored list" of the super class which defines
     * fields not to be displayed in the declarations area.
     */
    private void setIgnoredFields() {
        this.addIgnoredField("initialState");
        this.addIgnoredField("output");
    }

    @ConversionMethod(plainText = false)
    public String animateSim() {
        String loopStr = "\n" + RepresentableDefault.createStdAnimation(
                "sim", "sim", "inputLength");
        return createCompleteAnimationScript(loopStr);
    }
    
    @ConversionMethod(plainText = false)
    public String animateSim(String inputWordToSimulateOn) {
        String loopStr = "\n" + RepresentableDefault.createStdAnimation(
                "sim[" + inputWordToSimulateOn + "]", "sim", "inputLength");
        return createCompleteAnimationScript(loopStr);
    }

    @ConversionMethod(plainText = false)
    public String simulateOneStep() {
        this.simulateToStep = (this.simulateToStep + 1) % (("" + this.input).length() + 2);
        return this.createScriptFromInstance();
    }

    @ConversionMethod(plainText = false)
    public String simulateOneStep(String inputWordToSimulateOn) {
        this.input = inputWordToSimulateOn;
        return this.simulateOneStep();
    }

    private void simulateToRequestedStep() {
        if (input == null || input.equals("") || input.equals("null")) {
            return;
        }
        
        this.resetSimulation();
        
        for (int i = 0; i < simulateToStep + 1; i++) {
            if (i < this.input.length() + 1) {
                if (i == 0) {
                    this.currentStates.add(this.initialState);
                } else if (i > 0) {
                    HashSet<String> newStates = new HashSet<>();
                    for (String currState : this.currentStates) {
                        for (Transition t : this.getTransitionsFrom(currState, "" + input.charAt(i - 1))) {
                            newStates.add(t.getDestination());
                        }
                    }
                    this.currentStates = newStates;
                }
            } else {
                this.resetSimulation();
                return;
            }
        }
        
        int i = simulateToStep + 1;
        if (i < this.input.length() + 1) {
            if (i == 0) {
                nextStates.add(this.initialState);
            } else if (i > 0) {
                HashSet<String> newStates = new HashSet<>();
                for (String currState : this.currentStates) {
                    for (Transition t : this.getTransitionsFrom(currState, "" + input.charAt(i - 1))) {
                        newStates.add(t.getDestination());
                    }
                }
                this.nextStates = newStates;
            }
        }
    }
    
    private void resetSimulation() {
        this.currentStates.clear();
        this.nextStates.clear();
    }
    
    private static HashSet<String> getHashSet(String[] array) {
        HashSet<String> hashSet = new HashSet<String>();
        
        for (String s : array) {
            hashSet.add(s);
        }
        
        return hashSet;
    }
    
    public void addTransition(Transition trans) {
        this.transitions.add(trans);
        this.singleStates.remove(trans.getSource());
        this.singleStates.remove(trans.getDestination());
    }
    
    public void addFinalState(String fstate) {
        this.finalStates.add(fstate);
        this.singleStates.remove(fstate);
    }
    
    public void addSingleState(String sstate) {
        this.singleStates.add(sstate);
    }

    public void setInitialState(String istate) {
        this.initialState = istate;
    }

    private static Collection<Transition> copyTrans(Collection<Transition> set) {
        HashSet<Transition> newTrans = new HashSet<Transition>(set.size());
        for (Transition t : set) {
            newTrans.add(new Transition(t.isDirected(), t.getSource(), t.getDestination(), t.getLabel()));
        }
        return newTrans;
    }
    
    private static ArrayList<Transition> getList(Transition[] array) {
        ArrayList<Transition> transitions = new ArrayList<Transition>(array.length);
        
        for (Transition t : array) {
            transitions.add(t);
        }
        
        return transitions;
    }
    
    public HashSet<String> getInputAlphabet() {
        HashSet<String> symbs = new HashSet<String>();
        
        for (Transition t : this.transitions) {
            symbs.add(t.getLabel());
        }
        
        return symbs;
    }
    
    public void extractPDFFromFSM(
            final String pdfPath) {
        this.extractPDFFromFSM(pdfPath, FSM.STANDARD_MAPPING);
    }
    
    public void extractPDFFromFSM(
            final String pdfPath,
            final HashMap<String, String> optionalLabelMapping) {
        this.generateGVcode(pdfPath, optionalLabelMapping, false);
    }
    
    private String getCurrentInputSymbol() {
        if (this.input == null || this.input.length() <= this.simulateToStep
                || this.simulateToStep < 0) {
            return "";
        }
        
        return this.input.charAt(this.simulateToStep) + "";
    }
    
    public GraphViz generateGVcode(
            final String pdfPath,
            final HashMap<String, String> optionalLabelMapping,
            final boolean completeOutput) {
        LinkedList<Transition> transit = new LinkedList<Transition>();

        // Create new transitions with more than one symbol per arrow.
        for (String s1 : this.getAllStates()) {
            for (String s2 : this.getAllStates()) {
                String newTransSymb = "";
                LinkedList<Transition> transitionsFromTo = this.getTransitions(s1, s2);
                
                Collections.sort(transitionsFromTo);
                
                for (int i = 0; i < transitionsFromTo.size(); i++) {
                    if (i < transitionsFromTo.size() - 1) {
                        newTransSymb += transitionsFromTo.get(i).getLabel() + " / ";
                    } else {
                        newTransSymb += transitionsFromTo.get(i).getLabel();
                    }
                }
                if (!newTransSymb.isEmpty()) {
                    transit.add(new Transition(true, s1, s2, newTransSymb));
                }
            }
        }
        
        GraphViz gv = new GraphViz(pdfPath, this);
        
        String label;
        String edgeSymbol;
        
        if (transit.size() == 0 || transit.get(0).isDirected()) {
            if (completeOutput) println("Directed graph.");
            gv.addln(GraphViz.startDigraph());
            edgeSymbol = GraphViz.edgeSymbolDirected;
        } else {
            if (completeOutput) println("Undirected graph.");
            gv.addln(GraphViz.startGraph());
            edgeSymbol = GraphViz.edgeSymbolUnDirected;
        }
        
        gv.addln("rankdir=LR;");
//      gv.addln("ranksep=0.12;"); 
//      gv.addln("splines=ortho;"); 
//      gv.addln("decorate=true;");
        
        // Count states.
        HashSet<String> set = new HashSet<String>();
        set.add(initialState);
        for (String s : finalStates) {
            set.add(s);
        }
        for (String s : singleStates) {
            set.add(s);
        }
        for (Transition t : transit) {
            set.add(t.getSource());
            set.add(t.getDestination());
        }
        LinkedList<String> list = new LinkedList<String>(set);
        Collections.sort(list);
        if (completeOutput) println("States (" + set.size() + "): " + list);
        
        // Non-labelled states.
        HashSet<String> nonLabelled = new HashSet<String>();
        for (String s : set) {
            if (optionalLabelMapping == null || !optionalLabelMapping.containsKey(s)) {
                nonLabelled.add(s);
            }
        }
        
        // Additional lines.
        for (String s : additionalLinesForFSM) {
            gv.addln(s);
        }
        
        // Initial state.
        if (completeOutput) println("Initial state: " + initialState);
        gv.addln("node [shape = point ]; qi");
        
        if (finalStates.contains(initialState)) { // Initial state is final state.
            gv.addln("node [shape = doublecircle];");
        } else {
            gv.addln("node [shape = circle];");
        }
        
        if (optionalLabelMapping == null || optionalLabelMapping.get(initialState) == null) {
            label = initialState;
        } else {
            label = optionalLabelMapping.get(initialState);
        }

        String st = initialState;
        String filled = filledString(st);
        
        gv.addln("qi " + edgeSymbol + " " + initialState + ";");
        gv.addln(initialState + "[label=" + label + filled + "];");

        // Single States.
        if (completeOutput) println("Single states (" + singleStates.size() + "): " + singleStates);
        gv.addln("node [shape = circle];");
        for (String s : singleStates) {
            if (optionalLabelMapping == null || optionalLabelMapping.get(s) == null) {
                label = s;
            } else {
                label = optionalLabelMapping.get(s);
            }

            filled = filledString(s);
            gv.addln(s + "[label=" + label + filled + "];");
        }
        
        // Final states.
        if (completeOutput) println("Final states (" + finalStates.size() + "): " + finalStates);
        gv.addln("node [shape = doublecircle];");
        for (String s : finalStates) {
            if (optionalLabelMapping == null || optionalLabelMapping.get(s) == null) {
                label = s;
            } else {
                label = optionalLabelMapping.get(s);
            }
            
            filled = filledString(s);
            gv.addln(s + "[label=" + label + filled + "];");
        }
        
        gv.addln("node [shape = circle];");

        // Transitions.
        if (completeOutput) println("Transitions (" + transit.size() + "): " + transit);
        for (Transition t : transit) {
            String s1 = t.getSource();
            String s2 = t.getDestination();
            String edgeThickness = "";
            if (this.currentStates.contains(s1) && this.nextStates.contains(s2)
                    && this.getDestinationStates(s1, this.getCurrentInputSymbol()).contains(s2)) {
                edgeThickness = ",penwidth=3";
            }
            
            if (optionalLabelMapping == null || optionalLabelMapping.get(s1) == null) {
                label = s1;
            } else {
                label = optionalLabelMapping.get(s1);
            }

            filled = filledString(s1);
            gv.addln(s1 + "[label=" + label + filled + "];");

            if (optionalLabelMapping == null || optionalLabelMapping.get(s2) == null) {
                label = s2;
            } else {
                label = optionalLabelMapping.get(s2);
            }
            
            filled = filledString(s2);
            gv.addln(s2 + "[label=" + label + filled + "];");
            gv.addln(t.toString(edgeThickness));
        }

        // Input word.
        if (this.input != null && !"".equals(this.input) && !"null".equals(this.input)) {
            for (int i = 0; i < this.input.length(); i++) {
                filled = "";
                if (i == this.simulateToStep) {
                    filled = ",style = filled";
                }
                gv.addln("w" + i + "[label=" + this.input.charAt(i) + ",shape=box" + filled + "]");
            }
            for (int i = 0; i < this.input.length() - 1; i++) {
                gv.addln("w" + i + edgeSymbol + "w" + (i + 1) + ";");
            }
        }
        
        gv.addln(GraphViz.endGraph());

        if (completeOutput) println("Not explicitly labelled states (" + nonLabelled.size() + "): " + nonLabelled);
        
        return gv;
    }

    private String filledString(String st) {
        String filled = "";
        if (this.currentStates != null && this.currentStates.contains(st)) {
            filled = ",style = filled";
        }
        return filled;
    }
    
    private static String[] additionalLinesForFSM = new String[] {
//      "S0 [pin=true]",
//      "S1 [pin=true]",
//      "S2 [pin=true]",
//      "S3 [pin=true]"
    };
    
    private LinkedList<Transition> getTransitionsFrom(final String sourceState) {
        LinkedList<Transition> transitions = new LinkedList<Transition>();
        
        for (Transition t : this.transitions) {
            if (t.getSource().equals(sourceState)) {
                transitions.add(t);
            }
        }
        
        return transitions;
    }
    
    private LinkedList<Transition> getTransitions(final String from, final String to) {
        LinkedList<Transition> transitions = new LinkedList<Transition>();
        
        for (Transition t : this.transitions) {
            if (t.getSource().equals(from) && t.getDestination().equals(to)) {
                transitions.add(t);
            }
        }
        
        return transitions;
    }
    
    public HashSet<String> getAllStates() {
        HashSet<String> states = new HashSet<String>();
        
        if (initialState == null || finalStates == null || singleStates == null || transitions == null) {
            return states;
        }
        
        states.add(initialState);
        
        for (String s : this.finalStates) {
            states.add(s);
        }
        
        for (String s : this.singleStates) {
            states.add(s);
        }
        
        for (Transition t : this.transitions) {
            states.add(t.getDestination());
            states.add(t.getSource());
        }
        
        return states;
    }
    
    public HashSet<String> getAllReachableStates() {
        HashSet<String> reachable = new HashSet<String>();
        reachable.add(this.initialState);
        this.getReachableStatesRecursively(reachable);
        return reachable;
    }
    
    private void getReachableStatesRecursively(HashSet<String> reachable) {
        HashSet<String> newStates = new HashSet<String>();
        
        for (String state : reachable) {
            for (Transition t : this.getTransitionsFrom(state)) {
                if (!reachable.contains(t.getDestination())) {
                    newStates.add(t.getDestination());
                }
            }
        }
        
        if (!newStates.isEmpty()) {
            reachable.addAll(newStates);
            getReachableStatesRecursively(reachable);
        }
    }
    
    private String getDestinationState(final String sourceState, final String symbol) {
        return this.getTransitionFrom(sourceState, symbol).getDestination();
    }
    
    /**
     * @return  All destination states with all input symbols reachable from sourceState.
     */
    private StateSet getDestinationStates(final StateSet sourceStates, final String symbol) {
        HashSet<String> states = new HashSet<String>();
        
        for (String state : sourceStates.getStates()) {
            for (Transition t : this.getTransitionsFrom(state, symbol)) {
                states.add(t.getDestination());
            }
        }
        
        return new StateSet(states);
    }
    
    /**
     * In non-deterministic case the first matching transition is returned.
     */
    private Transition getTransitionFrom(final String sourceState, final String symbol) {
        LinkedList<Transition> transitions = this.getTransitionsFrom(sourceState);
        
        for (Transition t : transitions) {
            if (t.getLabel().equals(symbol)) {
                return t;
            }
        }
        
        return null;
    }
    
    /**
     * In non-deterministic case arbitrary many transitions, in deterministic
     * case exactly one.
     * 
     * @param sourceState  Source state.
     * @param symbol       Input symbol.
     * @return  Transitions from source state with input symbol.
     */
    private LinkedList<Transition> getTransitionsFrom(final String sourceState, final String symbol) {
        LinkedList<Transition> allTransitionsFrom = this.getTransitionsFrom(sourceState);
        LinkedList<Transition> matchingTrans = new LinkedList<Transition>();
        
        for (Transition t : allTransitionsFrom) {
            if (t.getLabel().equals(symbol)) {
                matchingTrans.add(t);
            }
        }
        
        return matchingTrans;
    }
    
    private HashSet<String> getDestinationStates(final String sourceState, final String symbol) {
        HashSet<String> states = new HashSet<>();
        LinkedList<Transition> transitions = this.getTransitionsFrom(sourceState, symbol);
        transitions.forEach(t -> states.add(t.getDestination()));
        return states;
    }
    
    public HashMap<StatePair, String> minimize() {
        // Remove unreachable states and transitions.
        this.singleStates.clear();
        HashSet<String> unreachableStates = this.getAllStates();
        HashSet<String> reachableStates = this.getAllReachableStates();
        unreachableStates.removeAll(reachableStates);
        LinkedList<Transition> unneccessaryTrans = new LinkedList<Transition>();
        
        for (Transition t : this.transitions) {
            if (unreachableStates.contains(t.getDestination())
                    || unreachableStates.contains(t.getSource())) {
                unneccessaryTrans.add(t);
            }
        }
        
        this.transitions.removeAll(unneccessaryTrans);
        this.finalStates.removeAll(unreachableStates);
        this.singleStates.clear();
        
        HashMap<StatePair, String> minimizationTable = new HashMap<StatePair, String>();
        boolean somethingHasChanged = true;
        
        // Mark X0.
        for (String s1 : reachableStates) {
            for (String s2 : reachableStates) {
                if ((this.finalStates.contains(s1) && !this.finalStates.contains(s2))
                        || (this.finalStates.contains(s2) && !this.finalStates.contains(s1))) {
                    minimizationTable.put(new StatePair(s1, s2), "X0");
                }
            }
        }
        
        int i = 1;
        
        HashMap<StatePair, String> minimizationTableTemp;
        
        // Mark Xk.
        while (somethingHasChanged) {
            somethingHasChanged = false;
            minimizationTableTemp = new HashMap<StatePair, String>(minimizationTable);
            for (String s1 : reachableStates) {
                for (String s2 : reachableStates) {
                    if (minimizationTable.get(new StatePair(s1, s2)) == null) {
                        for (String symb : this.getInputAlphabet()) {
                            String dest1 = this.getDestinationState(s1, symb);
                            String dest2 = this.getDestinationState(s2, symb);
                            
                            if (minimizationTable.get(new StatePair(dest1, dest2)) != null) {
                                minimizationTableTemp.put(new StatePair(s1, s2), "X" + i);
                                somethingHasChanged = true;
                            }
                        }
                    }
                }
            }
            
            minimizationTable = new HashMap<StatePair, String>(minimizationTableTemp);
            i++;
        }

//        println((minimizationTable));
//        println(this.equivalentStates(minimizationTable));

        this.reduce(this.equivalentStates(minimizationTable));
        
        return minimizationTable;
    }
    
    public void reduce(HashSet<StatePair> equivalentStates) {
        HashSet<String> alreadyBad = new HashSet<String>();
        
        for (StatePair sp : equivalentStates) {
            String[] a = new String[2];
            ArrayList<String> aa = new ArrayList<String>(2);
            sp.getStates().toArray(a);
            aa.add(a[0]);
            aa.add(a[1]);
            
            Collections.sort(aa);
            
            String good = aa.get(0);
            String bad = aa.get(1);

            if (alreadyBad.contains(good)) {
                String temp = good;
                good = bad;
                bad = temp;
            }
            
            if (bad.equals(this.initialState)) {
                String temp = good;
                good = bad;
                bad = temp;
//                good = aa.get(1);
//                bad = aa.get(0);
            }
            
            alreadyBad.add(bad);
            
            LinkedList<Transition> toRemove = new LinkedList<Transition>();
            // For all (good, bad) let all transitions with target "bad" point to "good".
            // Remove all transitions with source "bad".
            for (Transition t : this.transitions) {
                if (t.getDestination().equals(bad)) {
                    t.setDestination(good);
                }
                if (t.getSource().equals(bad)) {
                    toRemove.add(t);
                }
            }
            
            this.transitions.removeAll(toRemove);
            
            // Remove all states "bad".
            this.finalStates.remove(bad);
            this.singleStates.remove(bad); // This should never happen when called during minimization.
        }

//        println("% " + this.transitions);
    }
    
    /**
     * @param minimizationTable  The finished minimization table.
     * 
     * @return  The equivalent states from the minimiziation table.
     */
    public HashSet<StatePair> equivalentStates(HashMap<StatePair, String> minimizationTable) {
        HashSet<StatePair> equivalent = new HashSet<StatePair>();
        
        HashSet<String> reach = this.getAllReachableStates();
        
        for (String s1 : reach) {
            for (String s2 : reach) {
                if (!s1.equals(s2) && !minimizationTable.containsKey(new StatePair(s1, s2))) {
                    equivalent.add(new StatePair(s1, s2));
                }
            }
        }
        
        return equivalent;
    }
    
    public void printTriangleTableHuman(HashMap<StatePair, ?> table) {
        HashSet<String> reach = new HashSet<String>();
        reach.addAll(this.getAllStates());
        for (StatePair sp : table.keySet()) {
            reach.addAll(sp.getStates());
        }
        
        ArrayList<String> states = new ArrayList<String>(reach);
        Collections.sort(states);
        
        println();
        for (int i = 1; i < states.size(); i++) {
            print(states.get(i) + " ");
            for (int j = 0; j < i; j++) {
                String x = null;
                if (table.get(new StatePair(states.get(i), states.get(j))) != null) {
                    x = table.get(new StatePair(states.get(i), states.get(j))).toString();
                }
                if (x != null) {
                    print(x + " ");
                } else {
                    print("-- ");
                }
            }
            println();
        }
        
        print("   ");
        
        for (int j = 0; j < states.size() - 1; j++) {
            print(states.get(j) + " ");
        }
        println();
    }
    
    private String getTriangleTableLaTeX(HashMap<StatePair, ?> table) {
        String tableStr = "";
        
        HashSet<String> reach = new HashSet<String>();
        reach.addAll(this.getAllStates());
        for (StatePair sp : table.keySet()) {
            reach.addAll(sp.getStates());
        }
        
        int num = 0;
        
        ArrayList<String> states = new ArrayList<String>(reach);
        Collections.sort(states);
        
//        println();
        tableStr += ("\\dreieckstabelle\n");
        for (int i = 1; i < states.size(); i++) {
            tableStr += ("{" + this.convString(states.get(i)) + " & ");
            for (int j = 0; j < i; j++) {
                String x = null;
                if (table.get(new StatePair(states.get(i), states.get(j))) != null) {
                    x = table.get(new StatePair(states.get(i), states.get(j))).toString();
                }
                
                if (x != null) {
                    if (j == i - 1) {
                        tableStr += ("$\\times_{" + x.replace("X", "") + "}$");
                    } else {
                        tableStr += ("$\\times_{" + x.replace("X", "") + "}$ & ");
                    }
                } else {
                    if (j == i - 1) {
                        tableStr += ("$-$");
                    } else {
                        tableStr += ("$-$ & ");
                    }
                }
            }
            tableStr += ("}\n");
            num++;
        }
        
        tableStr += ("{ & ");
        for (int j = 0; j < states.size() - 2; j++) {
            tableStr += (this.convString(states.get(j)) + " & ");
        }
        tableStr += (this.convString(states.get(states.size() - 2)));
        
        tableStr += ("}\n");
        num++;
        
        for (int i = 0; i < 18 - num; i++) {
            tableStr += ("{}");
        }

        if (num > 18) {
            tableStr = "Sorry, maximally 18 states allowed for minimization table.";
        }
        
        LaTeX.setPaperWidthForTriangularTables((int) (num * 1.0 + 2));
        LaTeX.setPaperHeightForTriangularTables((int) (num * 0.6 + 1));
        
        return tableStr;
    }
    
    public void printTriangleTableLaTeX(HashMap<StatePair, ?> table) {
        HashSet<String> reach = new HashSet<String>();
        reach.addAll(this.getAllStates());
        for (StatePair sp : table.keySet()) {
            reach.addAll(sp.getStates());
        }
        
        int num = 0;
        
        ArrayList<String> states = new ArrayList<String>(reach);
        Collections.sort(states);
        
//        println();
        println("\\dreieckstabelle");
        for (int i = 1; i < states.size(); i++) {
            print("{" + this.convString(states.get(i)) + " & ");
            for (int j = 0; j < i; j++) {
                String x = null;
                if (table.get(new StatePair(states.get(i), states.get(j))) != null) {
                    x = table.get(new StatePair(states.get(i), states.get(j))).toString();
                }
                
                if (x != null) {
                    if (j == i - 1) {
                        print("$\\times_{" + x.replace("X", "") + "}$");
                    } else {
                        print("$\\times_{" + x.replace("X", "") + "}$ & ");
                    }
                } else {
                    if (j == i - 1) {
                        print("$-$");
                    } else {
                        print("$-$ & ");
                    }
                }
            }
            println("}");
            num++;
        }
        
        print("{ & ");
        for (int j = 0; j < states.size() - 2; j++) {
            print(this.convString(states.get(j)) + " & ");
        }
        print(this.convString(states.get(states.size() - 2)));
        
        println("}");
        num++;
        
        for (int i = 0; i < 18 - num; i++) {
            print("{}");
        }
        println();
        println("% ");
    }
    
    private class ThriceInt {
        private int i, j, k;
        
        public ThriceInt(int k, int i, int j) {
            this.i = i;
            this.j = j;
            this.k = k;
        }
        
        @Override
        public int hashCode() {
            final int prime = 31;
            int result = 1;
            result = prime * result + getOuterType().hashCode();
            result = prime * result + this.i;
            result = prime * result + this.j;
            result = prime * result + this.k;
            return result;
        }
        
        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            ThriceInt other = (ThriceInt) obj;
            if (!getOuterType().equals(other.getOuterType()))
                return false;
            if (this.i != other.i)
                return false;
            if (this.j != other.j)
                return false;
            if (this.k != other.k)
                return false;
            return true;
        }
        
        private FSM getOuterType() {
            return FSM.this;
        }
        
        @Override
        public String toString() {
            return "(" + k + " | " + i + "/" + j + ")";
        }
    }
    
    private HashMap<ThriceInt, RegEx> calculatedRegExs;
    
    /**
     * TODO: Very inefficient.
     * 
     * @param i  The ith state to return.
     * @return  The ith state.
     */
    private String getIthState(int i) {
        ArrayList<String> states = new ArrayList<>(this.getAllReachableStates());
        Collections.sort(states);
        return states.get(i);
    }
    
    /**
     * Using Kleene's algorithm as shown in the
     * <a href="http://en.wikipedia.org/wiki/Kleene%27s_algorithm">Wikipedia</a>,
     * the sets R(k,i,j) of all strings that take fsm M from state qi to qj 
     * without going through any state numbered higher than k are computed.
     * 
     * @param k  The threshold number.
     * @param i  The "from state".
     * @param j  The "to state".
     */
    private RegEx getRegExp(int k, int i, int j) {
        if (!GeneralDialog.continueLongOperation(REGEX_LONGTIME_ID)) {
            throw new LongOperationException();
        }

        RegEx calculated = calculatedRegExs.get(new ThriceInt(k, i, j));
        RegEx toReturn = null;
        
        // Return value already stored in table.
        if (calculated != null) {
            return calculated;
        }
        
        if (k == -1) { // Initial definition of RegEx sets.
            RegEx initial = new RegEx();
            String statei = this.getIthState(i);
            String statej = this.getIthState(j);
            
            // Add all characters reachable within one transition.
            for (Transition t : this.getTransitions(statei, statej)) {
                initial.union(new RegExCharacter(t.getLabel()));
            }
            
            // If source = destination, add empty word.
            if (i == j) {
                initial.union(new RegExLambda());
            }
            
            toReturn = initial;
        } else { // Definition of RegEx sets in later steps k.
            RegEx rikDecr = new RegEx(this.getRegExp(k - 1, i, k));
            RegEx rkkDecr = new RegEx(this.getRegExp(k - 1, k, k));
            RegEx rkjDecr = new RegEx(this.getRegExp(k - 1, k, j));
            RegEx rijDecr = new RegEx(this.getRegExp(k - 1, i, j));
            RegEx rij = rkkDecr;
            rij.kleene();
            rij.concatFirst(rikDecr);
            rij.concat(rkjDecr);
            rij.union(rijDecr);
            toReturn = rij;
        }
        
        this.calculatedRegExs.put(new ThriceInt(k, i, j), toReturn);
        return toReturn;
    }
    
    private static final String REGEX_LONGTIME_ID = "$FSM_TO_REGEX$";
    
    public RegEx generateRegExp() {
        GeneralDialog.resetLongTimeOperationID(REGEX_LONGTIME_ID);
        
        this.calculatedRegExs = new HashMap<>();
        int n = this.getAllReachableStates().size() - 1;
        RegEx regExComplete = new RegEx();
        
        for (int j = 0; j <= n; j++) {
            String ithState = this.getIthState(j);
            if (this.finalStates.contains(ithState)) {
                try {
                    regExComplete.union(this.getRegExp(n, 0, j));
                } catch (Exception e) {
                    GlobalVariables.getParameters().logError("Sorry, regular expression or operation time too long. Returning empty expression.");
                    return new RegEx(new RegExEmpty());
                }
            }
        }
        
        return regExComplete;
    }
    
    private String replaceAllIndices(final String str) {
        String ret = "";
        boolean inNumMode = false;
        
        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '0' || str.charAt(i) == '1' || str.charAt(i) == '2' || str.charAt(i) == '3' || str.charAt(i) == '4' || str.charAt(i) == '5' || str.charAt(i) == '6' || str.charAt(i) == '7' || str.charAt(i) == '8' || str.charAt(i) == '9') {
                if (!inNumMode) {
                    ret += "{";
                    inNumMode = true;
                }
                
                ret += str.charAt(i);
            } else {
                if (inNumMode) {
                    ret += "}";
                    inNumMode = false;
                }
                
                ret += str.charAt(i);
            }
        }
        
        return ret;
    }
    
    private String convString(final Object toBeConverted) {
        return this.convString(toBeConverted, null, null, false);
    }
    
    private String convString(final Object toBeConverted, HashMap<StateSet, Integer> mapping, StateSet set, boolean forceNondet) {
        String addition = "";
        if (mapping != null && set != null && mapping.containsKey(set)) {
            addition = "\\hat{=} s_" + mapping.get(set).toString();
        }
        
        if (this.isDeterministic() && addition.isEmpty() && !forceNondet) {
            return this.replaceAllIndices("$" + (toBeConverted.toString()
                    .replace("s", "s_")
                    .replace("S", "S_")
                    .replace("[]", "\\emptyset")
                    .replace("[", "")
                    .replace("]", "") + "$"));
        } else {
            return this.replaceAllIndices("$" + (toBeConverted.toString()
                    .replace("s", "s_")
                    .replace("S", "S_")
                    .replace("[]", "\\emptyset")
                    .replace("[", "\\{")
                    .replace("]", "\\}") + addition + "$"));
        }
    }
    
    /**
     * Prints a table of all transitions that are REACHABLE
     * in LaTeXCode format.
     */
    public void printTransitionTableLatex() {
        try {
            HashMap<StateSetInputSymbolPair, StateSet> table = this.getTransitionTable();
            printTableLatex(table);
        } catch (Exception e) {
            println("Creating transition table took too long.");
        }
    }
    
    public void printTableLatex(HashMap<StateSetInputSymbolPair, StateSet> table) {
        this.printTableLatex(table, null);
    }
    
    /**
     * Prints the table given by the parameter in latex format.
     * 
     * @param table
     */
    public void printTableLatex(HashMap<StateSetInputSymbolPair, StateSet> table, HashMap<StateSet, Integer> mapping) {
        String firstLine = "\\begin{mytabular}{|c||";
        String secondLine = "";
        String lastLine = "\\end{mytabular}";
        
        ArrayList<String> alphabet = new ArrayList<String>(this.getInputAlphabet());
        Collections.sort(alphabet);
        HashSet<StateSet> stateSetList = new HashSet<StateSet>();
        ArrayList<StateSet> stateSetListSorted;
        
        for (StateSetInputSymbolPair key : table.keySet()) {
            stateSetList.add(new StateSet(key.getStatesSet()));
        }
        
        stateSetListSorted = new ArrayList<StateSet>(stateSetList);
        Collections.sort(stateSetListSorted);
        
        for (int i = 0; i < alphabet.size(); i++) {
            firstLine += "c|";
            secondLine += " & $" + alphabet.get(i) + "$";
        }
        
        secondLine += " \\\\";
        
        firstLine += "}";
        
        println(firstLine);
        println("\\hline");
        print(secondLine);
        print("\\hline");
        print("\\hline\n");
        
        for (StateSet stSet : stateSetListSorted) {
            print(convString(stSet, mapping, stSet, mapping != null));
            for (String symbol : alphabet) {
                print(" & " + convString(table.get(new StateSetInputSymbolPair(stSet, symbol)), mapping, table.get(new StateSetInputSymbolPair(stSet, symbol)), mapping != null));
            }
            print(" \\\\");
            println("\\hline");
        }
        
        println(lastLine);
    }
    
    private StateSet initialStateList;
    
    /**
     * Creates a table of all transitions that are REACHABLE
     * in LaTeXCode format. If the FSM is not deterministic, a table of all
     * deterministic state set transitions is created (i.e., the table from
     * the power set construction making the FSM deterministic).
     * 
     * @return  A complete table of reachable transitions (may have exponential
     *          size.
     */
    public HashMap<StateSetInputSymbolPair, StateSet> getTransitionTable() {
        HashMap<StateSetInputSymbolPair, StateSet> table = new HashMap<StateSetInputSymbolPair, StateSet>();
        HashSet<StateSet> finishedStates = new HashSet<StateSet>();
        HashSet<StateSet> newChangedStates = new HashSet<StateSet>();

        // Initialization.
        initialStateList = new StateSet();
        initialStateList.add(this.initialState);
        for (String symbol : this.getInputAlphabet()) {
            StateSet destStates = this.getDestinationStates(initialStateList, symbol);
            table.put(
                    new StateSetInputSymbolPair(initialStateList, symbol), 
                    destStates);
            newChangedStates.add(destStates);
        }
        
        // The other states.
        while (!finishedStates.containsAll(newChangedStates)) {
            HashSet<StateSet> unfinishedStateSets = new HashSet<StateSet>();
            unfinishedStateSets.addAll(newChangedStates);
            unfinishedStateSets.removeAll(finishedStates);
            finishedStates.addAll(newChangedStates);
            
            for (StateSet stateSet : unfinishedStateSets) {
                continueDeterminization();
                
                for (String symbol : this.getInputAlphabet()) {
                    StateSet destStates = this.getDestinationStates(stateSet, symbol);
                    table.put(
                            new StateSetInputSymbolPair(stateSet, symbol), 
                            destStates);
                    newChangedStates.add(destStates);
                }
            }
        }
        
        return table;
    }

    private void continueDeterminization() {
        if (!GeneralDialog.continueLongOperation(DET_LONG_OP_ID)) {
            throw new LongOperationException();
        }
    }
    
    private static final String DET_LONG_OP_ID = "$$DETERMINIZE-FSM$$";
    
    public void makeDeterministic(boolean quiet) {
        if (this.isDeterministic() && this.getAllReachableStates().size() == this.getAllStates().size()) {
            return;
        }
        
        HashMap<StateSetInputSymbolPair, StateSet> table;
        table = this.getTransitionTable();

        // Make deterministic according to table.
        this.initialState = "s0";
        HashSet<String> oldFinalStates = new HashSet<String>(this.finalStates);
        this.finalStates.clear();
        this.singleStates.clear();
        this.transitions.clear();
        HashMap<StateSet, Integer> stateNameMapping = new HashMap<StateSet, Integer>();
        int i = 0;

        HashSet<StateSet> stateSetList = new HashSet<StateSet>();
        ArrayList<StateSet> stateSetListSorted;
        
        for (StateSetInputSymbolPair key : table.keySet()) {
            continueDeterminization();
            stateSetList.add(new StateSet(key.getStatesSet()));
        }
        
        stateSetListSorted = new ArrayList<StateSet>(stateSetList);
        Collections.sort(stateSetListSorted);
        
        for (StateSet set : stateSetListSorted) {
            continueDeterminization();
            stateNameMapping.put(set, i);
            i++;
        }

        for (StateSetInputSymbolPair key : table.keySet()) {
            continueDeterminization();

            for (String state : key.getStatesSet().getStates()) {
                if (oldFinalStates.contains(state)) {
                    this.finalStates.add("s" + stateNameMapping.get(key.getStatesSet()));
                }
            }
            
            // Transitions.
            this.transitions.add(new Transition(
                    true, 
                    "s" + stateNameMapping.get(key.getStatesSet()), 
                    "s" + stateNameMapping.get(table.get(key)), 
                    key.getInputSymbol()));
        }

        if (!quiet) {
            this.printTableLatex(table, stateNameMapping);
        }
        
        initialStateList = null;
    }
    
    public boolean isDeterministic() {
        for (String state : this.getAllReachableStates()) {
            for (String symbol : this.getInputAlphabet()) {
                if (this.getTransitionsFrom(state, symbol).size() != 1) {
                    return false;
                }
            }
        }
        
        return true;
    }

    /**
     * @return If the (deterministic) automaton is minimal (false in all other cases).
     */
    public boolean isMinimal() {
        FSM minFSM = new FSM(this);
        minFSM.minimize();
        return minFSM.getAllStates().size() == this.getAllStates().size();
    }
    
    private Random randomGen = new Random(1);
    
    /**
     * Clears the states and transitions and creates a new random FSM.
     */
    public void randomizeFSMnonDet(int numStates, int numTrans) {
        this.randomizeFSMnonDet(numStates, numTrans, this.getInputAlphabet());
    }
    
    public void randomizeFSMnonDet(int numStates, int numTrans, HashSet<String> alphabet) {
        this.randomizeFSMnonDet(numStates, numTrans, alphabet, randomGen);
    }

    /**
     * Clears the states and transitions and creates a new random FSM.
     */
    public void randomizeFSMnonDet(
            int maxNumStates, 
            int numTrans, 
            HashSet<String> alphabet,
            Random rand) {
        this.clear();

        double finalStateProb = 0.2;
        
        ArrayList<String> alph = new ArrayList<String>(alphabet);
        HashSet<String> states = new HashSet<String>();
        this.initialState = "s0";
        
        for (int i = 0; i < numTrans; i++) {
            if (!continueLongOperationRandomize()) {
                this.randomizeFSMnonDet(5, 15, alphabet, rand);
                return;
            }
            
            String state1 = "s" + rand.nextInt(maxNumStates);
            String state2 = "s" + rand.nextInt(maxNumStates);
            String symbol = alph.get(rand.nextInt(alph.size()));
            Transition trans = new Transition(true, state1, state2, symbol);

            while (this.transitions.contains(trans)) {
                state1 = "s" + rand.nextInt(maxNumStates);
                state2 = "s" + rand.nextInt(maxNumStates);
                symbol = alph.get(rand.nextInt(alph.size()));
                trans = new Transition(true, state1, state2, symbol);
            }
            
            if (!states.contains(state1)) {
                states.add(state1);
                if (rand.nextDouble() < finalStateProb) {
                    this.finalStates.add(state1);
                }
            }
            if (!states.contains(state2)) {
                states.add(state2);
                if (rand.nextDouble() < finalStateProb) {
                    this.finalStates.add(state2);
                }
            }
            
            this.transitions.add(trans);
        }
        
        if (this.getAllReachableStates().size() != this.getAllStates().size()
                || maxNumStates > 1 &&
                (this.finalStates.size() == 0 || this.finalStates.size() == this.getAllStates().size())) {
            this.randomizeFSMnonDet(maxNumStates, numTrans, alphabet, rand);
        }
    }
    
    public void clear() {
        this.initialState = "";
        this.finalStates = new HashSet<String>();
        this.singleStates = new HashSet<String>();
        this.transitions = new LinkedList<Transition>();
    }

    public void randomizeFSMdet(int numStates, HashSet<String> alphabet) {
        this.randomizeFSMdet(numStates, alphabet, this.randomGen);
    }
            
    /**
     * Clears the states and transitions and creates a new random FSM.
     */
    public void randomizeFSMdet(
            int numStates, 
            HashSet<String> alphabet,
            Random rand) {
        this.clear();
        
        double finalStateProb = 0.5;
        
        this.initialState = "s0";
        
        for (int i = 0; i < numStates; i++) {
            if (!continueLongOperationRandomize()) {
                this.randomizeFSMdet(5, alphabet, rand);
                return;
            }
            
            for (String symb : alphabet) {
                String destState = "s" + rand.nextInt(numStates);
                this.transitions.add(new Transition(true, "s" + i, destState, symb));
            }
            if (rand.nextDouble() < finalStateProb) {
                this.finalStates.add("s" + i);
            }
        }
        
        if (!this.isDeterministic()) {
            throw new RuntimeException("Something went wrong during random det FSM generation.");
        }
        
        if (this.getAllReachableStates().size() != this.getAllStates().size() || 
                numStates > 2 && // For two or less states, the result cannot be non-trivial and non-minimal.
                (this.isMinimal()
                || this.finalStates.size() == 0 || this.finalStates.size() == this.getAllStates().size())) {
            this.randomizeFSMdet(numStates, alphabet, rand);
        }
    }
    
    private String output = "";
    
    private void println() {
        output += "\n";
    }
    
    private void println(String s) {
        output += s + "\n";
    }
    
    private void print(String s) {
        output += s;
    }

    @ConversionMethod
    public String getMinimizationChain() {
        return createExercise(null, null, new HashMap<>(FSM.STANDARD_MAPPING));
    }
    
    /**
     * Creates an exercise including PDF output. If filename or pdfpath are
     * null, no PDF files are produced.
     * 
     * @param filename  Base file name for output PDFs.
     * @param pdfPath   Path to store PDFs.
     * @param mapping   Mapping to name states.
     * @return  A text trace of the calculation.
     */
    public String createExercise(
            String filename, 
            String pdfPath,
            HashMap<String, String> mapping) {
        String filename1 = filename + "-1-nondeterministic";
        String filename2 = filename + "-2-deterministic";
        String filename3 = filename + "-3-deterministic-minimized";

        if (pdfPath != null && filename != null) {
            StaticMethods.deleteDAT(pdfPath + "/" + filename1 + ".pdf");
            StaticMethods.deleteDAT(pdfPath + "/" + filename2 + ".pdf");
            StaticMethods.deleteDAT(pdfPath + "/" + filename3 + ".pdf");
            StaticMethods.deleteDAT(pdfPath + "/" + filename1 + ".java");
            StaticMethods.deleteDAT(pdfPath + "/" + filename2 + ".java");
            StaticMethods.deleteDAT(pdfPath + "/" + filename3 + ".java");
        }
        
        FSM raw = new FSM(this);
        
        int sizePlain, sizeDet, sizeMin;
        sizePlain = raw.getAllStates().size();
        
        raw.println("\n% Script (nondet): \n" + raw.createScriptFromInstance().replace("\n", "\n% "));
        
        if (raw.isDeterministic()) {
            raw.println("% Automaton is already deterministic (storing only once).");
        } else {
            if (pdfPath != null && filename != null) {
                GraphViz gv = raw.generateGVcode(pdfPath, mapping, false);         // Speichere Graph.
                gv.storeAsPDF(filename1, pdfPath);
            }
            raw.println("% Power set construction of deterministic automaton:");
            raw.makeDeterministic(false);                                       // Erzeuge deterministische Version und gib Tabelle aus.
        }
        
        sizeDet = raw.getAllStates().size();
        
        if (raw.isMinimal()) {
            raw.println("%\n% Automaton is already minimal (storing only once).");
        } else {
            if (pdfPath != null && filename != null) {
                GraphViz gv = raw.generateGVcode(pdfPath, mapping, false);         // Speichere Graph.
                gv.storeAsPDF(filename2, pdfPath);
            }
            raw.println("% Deterministic state transition table:");
            raw.printTransitionTableLatex();                               // Gib deterministische Transitionstabelle aus.
            raw.println("\n% Script (det): \n" + raw.createScriptFromInstance().replace("\n", "\n% "));
        }

        HashMap<StatePair, ?> map = raw.minimize();

        raw.println("%\n% Minimization table:");
        if (map.size() > 0) {
            raw.printTriangleTableLaTeX(map); // Minimiere und gib Tabelle aus.
        } else {
            raw.println("% Do you really need help with this trivial table (hint: just make it all empty...)?");
        }
    
        sizeMin = raw.getAllStates().size();
        if (pdfPath != null && filename != null) {
            GraphViz gv = raw.generateGVcode(pdfPath, mapping, false);         // Speichere Graph.
            gv.storeAsPDF(filename3, pdfPath);
        }
        raw.println("% Minimized state transition table:");
        raw.printTransitionTableLatex();                               // Gib minimierte Transitionstabelle aus.
        raw.println("%\n% Script (detmin): \n" + raw.createScriptFromInstance().replace("\n", "\n% "));
        
        raw.println("%\n% States (plain / det / min): " + sizePlain + " / " + sizeDet + " / " + sizeMin);
        
        return raw.output;
    }
    
    private String getNotation(String state) {
        String not = "";
        if (this.finalStates.contains(state)) {
            not += FSMScriptNames.POSTFIX_FINAL_STATE;
        }
        if (state.equals(this.initialState)) {
            not += FSMScriptNames.POSTFIX_INITIAL_STATE;
        }
        if (not.length() > 0) {
            not = FSMScriptNames.TRENNER_POSTFIX + not;
        }
        return not;
    }
    
    @Override
    public String createScriptFromInstance() {
        return createCodeFromInstance(false);
    }
    
    public String createCodeFromInstance(boolean checkForLongTimeOp) {
        String script = "fsm:\n";
        String initialState = this.initialState;
        HashSet<String> finalStates = new HashSet<>(this.finalStates);
        
        for (Transition t : this.transitions) {
            if (checkForLongTimeOp) {
                continueDeterminization();
            }
            
            String not1 = this.getNotation(t.getSource());
            String not2 = this.getNotation(t.getDestination());
            
            String label = t.getLabel();
            String state1 = t.getSource();
            String state2 = t.getDestination();
            script += "(" + state1 + ", " + label + ") " + FSMScriptNames.ARROW + " " + state2 + FSMScriptNames.TRENNER_ZEILE + "\n";
            
            if (not1.contains("" + FSMScriptNames.POSTFIX_INITIAL_STATE)) {
                initialState = state1;
            }
            if (not2.contains("" + FSMScriptNames.POSTFIX_INITIAL_STATE)) {
                initialState = state2;
            }
            if (not1.contains("" + FSMScriptNames.POSTFIX_FINAL_STATE)) {
                finalStates.add(state1);
            }
            if (not2.contains("" + FSMScriptNames.POSTFIX_FINAL_STATE)) {
                finalStates.add(state2);
            }
        }
        
        this.s0 = initialState;
        if (finalStates.size() > 0) {
            this.F = StaticMethods.formatCollection(finalStates);
        } else {
            this.F = NO_FINAL_STATES;
        }

        FSM newFSM = new FSM(this);
        newFSM.createInstanceFromScript(script, null);
        script = newFSM.formatScriptLeftPrio();
        
        return script;
    }
    
    @Override
    public String[] getExampleScripts() {
        if (examplesStatic == null) {
            String e1 = "fsm:\n" + 
                    "(s0, a) => s2;\n" + 
                    "(s0, b) => s1;\n" + 
                    "(s1, a) => s0;\n" + 
                    "(s1, b) => s4;\n" + 
                    "(s2, a) => s4;\n" + 
                    "(s2, b) => s3;\n" + 
                    "(s3, a) => s2;\n" + 
                    "(s3, b) => s1;\n" + 
                    "(s4, a) => s2;\n" + 
                    "(s4, b) => s3;\n" + 
                    "--declarations--\n" + 
                    "s0=s0;\n" + 
                    "F=s0;\n" + 
                    "simulateToStep=0;\n" +
                    "input=aabba;\n" +
                    "--declarations-end--";
            
            String e2 = "fsm:\n" + 
                    "(s0, b) => s5 | s7;\n" + 
                    "(s1, a) => s4;\n" + 
                    "(s1, b) => s6 | s3 | s7;\n" + 
                    "(s2, a) => s2 | s1;\n" + 
                    "(s2, b) => s5;\n" + 
                    "(s3, a) => s1 | s7;\n" + 
                    "(s3, b) => s4 | s0 | s2;\n" + 
                    "(s4, a) => s2 | s4;\n" + 
                    "(s4, b) => s0 | s2;\n" + 
                    "(s5, a) => s5;\n" + 
                    "(s5, b) => s4;\n" + 
                    "(s6, a) => s1 | s2;\n" + 
                    "(s6, b) => s0 | s4;\n" + 
                    "--declarations--\n" + 
                    "s0=s0;\n" + 
                    "F=s0\n" + 
                    "--declarations-end--";

            String e3 = "fsm:\n" + 
                    "(s0, a) => s2 | s0;\n" + 
                    "(s1, a) => s0;\n" + 
                    "(s1, b) | (s2, a) => s3;\n" + 
                    "(s2, b) => s1 | s4 | s3 | s2;\n" + 
                    "(s3, a) => s1;\n" + 
                    "(s3, b) => s0 | s1;\n" + 
                    "(s4, a) => s4;\n" + 
                    "(s4, b) => s3 | s2;\n" + 
                    "--declarations--\n" + 
                    "e=#n#;\n" + 
                    "simulateToStep=-1;\n" + 
                    "input=null;\n" + 
                    "s0=s0;\n" + 
                    "F=s0;\n" + 
                    "displayMode=0;\n" + 
                    "showMinimizedFSM=true;\n" + 
                    "showDeterministicFSM=true\n" + 
                    "--declarations-end--";
            
            String e4 = "fsm:\n" + 
                    "(s0, a) | (s1, b) => s1;\n" + 
                    "(s0, b) | (s3, a) => s3;\n" + 
                    "(s1, a) | (s4, a) => s2;\n" + 
                    "(s2, a) | (s4, b) | (s5, b) | (s7, b) => s4;\n" + 
                    "(s2, b) | (s6, b) | (s8, a) | (s8, b) => s9;\n" + 
                    "(s3, b) | (s5, a) => s5;\n" + 
                    "(s6, a) | (s9, b) => s7;\n" + 
                    "(s7, a) => s6;\n" + 
                    "(s9, a) => s8;\n" + 
                    "--declarations--\n" + 
                    "e=#n#;\n" + 
                    "simulateToStep=-1;\n" + 
                    "input=null;\n" + 
                    "s0=s0;\n" + 
                    "F=s4,s6,s7,s8,s9,s0,s1,s2;\n" + 
                    "displayMode=1;\n" + 
                    "showMinimizedFSM=false;\n" + 
                    "showDeterministicFSM=false\n" + 
                    "--declarations-end--";

//            FSM fsm1 = new FSM(new Exercise("n"));
//            fsm1.createInstanceFromScript(e1);
//            FSM fsm2 = new FSM(new Exercise("n"));
//            fsm2.createInstanceFromScript(e2);
//            FSM fsm3 = new FSM(new Exercise("n"));
//            fsm3.createInstanceFromScript(e3);
//            FSM fsm4 = new FSM(new Exercise("n"));
//            fsm4.createInstanceFromScript(e4);

            FSM.examplesStatic = new String[] {
                    e1, 
                    e2,
                    e4,
                    e3,
                    };
        }
        
        return examplesStatic;
    }

    @Override
    public boolean isAcceptableScript(String code) {
        return (code + "").toLowerCase().startsWith("fsm:");
    }

    @Override
    public void createInstanceFromScript(String codeRaw, RepresentableAsPDF father) {
        this.applyDeclarationsAndPreprocessors(codeRaw, father, 0);
        String code = getScriptWithoutPrepAndDeclAndPreamble();
        code = this.decollapseRules(code);
        
        FSM other = new FSMScript(code).execute();
        
        this.finalStates = other.finalStates;
        this.initialState = other.initialState;
        this.initialStateList = other.initialStateList;
        this.output = other.output;
        this.randomGen = other.randomGen;
        this.singleStates = other.singleStates;
        this.transitions = other.transitions;
        
        this.initialState = s0;
        this.finalStates = new HashSet<>();
        if (!NO_FINAL_STATES.equalsIgnoreCase(this.F)) {
            for (String f : StaticMethods.removeWhitespaces(F).split(",")) {
                this.finalStates.add(f);
            }
        }
        
        this.resetSimulation();
        this.simulateToRequestedStep();
    }

    @Override
    public PDFProcessor generatePDFscript(String pdfPath) {
        super.generatePDFscript(pdfPath);
        PDFProcessor pdf = null;
        
        if (this.displayMode == 1 || this.displayMode == 2 || this.showDeterministicFSM || this.showMinimizedFSM) {
            String latexCode = LaTeXCommands.PREAMBLE_CROP_PAGE_PREVIEW;
            
            if (this.displayMode >= 1) {
                latexCode = "%var50|dreck%\n" + 
                        "~\\\\[1cm]\n";
            }
            
            String fsmScript = "FSM:\\par"
                    + LaTeX.subscript(-1.0, "dot:" + this.generateGVcode(pdfPath, FSM.STANDARD_MAPPING, false).getSourceString());
            String minimizedScript = "";
            String determinizedScript = "";

            if (this.showDeterministicFSM) {
                if (this.isDeterministic()) {
                    determinizedScript = "Make deterministic: \\textit{Nothing to do}\\bigbreak";
                } else {
                    FSM copy = new FSM(this);
                    copy.makeDeterministic(true);
                    copy.simulateToRequestedStep();
                    determinizedScript = "Make deterministic:\\par"
                            + LaTeX.subscript(-1.0, "dot:" + copy.generateGVcode(pdfPath, FSM.STANDARD_MAPPING, false).getSourceString());
                }
            }

            if (this.showMinimizedFSM) {
                FSM ccopy = this;
                if (!ccopy.isDeterministic()) {
                    ccopy = new FSM(this);
                    ccopy.makeDeterministic(true);
                }
                
                if (ccopy.isMinimal()) {
                    minimizedScript = "Minimize: \\textit{Nothing to do}\\bigbreak";
                } else {
                    FSM copy = new FSM(ccopy);
                    copy.minimize();
                    copy.simulateToRequestedStep();
                    minimizedScript = "Minimize:\\par"
                            + LaTeX.subscript(-1.0, "dot:" + copy.generateGVcode(pdfPath, FSM.STANDARD_MAPPING, false).getSourceString());
                }
            }

            latexCode += ""
                    + fsmScript + "\n"
                    + determinizedScript + "\n"
                    + minimizedScript + "\n";
            
            if (this.displayMode == 2) {
                latexCode += "Transition table:\\par\n";
                FSM copy = new FSM(this);
                
                if (this.isDeterministic()) {
                    copy.printTransitionTableLatex();
                } else {
                    copy.makeDeterministic(false);
                }
                
                latexCode += copy.output;
                latexCode += "\n\\bigbreak\n";
            }

            if (this.displayMode >= 1) {
                latexCode += "Minimization table:\\par\n";
                latexCode += getPlainLatexMinimizationTable();
            } else {
                latexCode += LaTeXCommands.POSTAMBLE_STANDARD;
            }
            
            PDFProcessor latexPDF = new LaTeXPDF(latexCode, pdfPath, this);
            
            pdf = latexPDF;
        } else if (this.displayMode == 0) {
            pdf = this.generateGVcode(pdfPath, FSM.STANDARD_MAPPING, false);
        }
        
        return pdf;
    }

    public String generateExercise(String pdfPath, String filename) {
        return createExercise(filename, pdfPath, new HashMap<>(FSM.STANDARD_MAPPING));
    }

    private static Random rand = new Random();

    public static final String RANDOMIZE_LONGTIME_ID = "$$RANDOMIZING-FSM$$";
    public static final int RANDOMIZE_LONGTIME = GeneralDialog.DESKTOP_MAXTIME_FOR_LONG_OPERATIONS;
    
    @ConversionMethod(plainText = false)
    public String createRandomFSMScript(int numOfStates, boolean deterministic) {
        return createRandomFSMScriptSeed(numOfStates, deterministic, rand.nextLong());
    }
    
    @ConversionMethod(plainText = false)
    public String createRandomFSMScriptSeed(int numOfStates, boolean deterministic, long seed) {
        GeneralDialog.resetLongTimeOperationID(RANDOMIZE_LONGTIME_ID);
        
        rand = new Random(seed);
        
//        if (numOfStates < 2) {
//            return RepresentableDefault.BEGIN_COMMENT
//                    + "Please enter at least 2 states for random FSM generation."
//                    + RepresentableDefault.END_COMMENT + "\n"
//                    + this.createScriptFromInstance();
//        }
        
        HashSet<String> alphabet = new HashSet<>();
        alphabet.add("a");
        alphabet.add("b");
        
        FSM fsm = new FSM(this.getExercise());
        fsm.createInstanceFromScript(this.getRawScript(), null);
        if (deterministic) {
            fsm.randomizeFSMdet(numOfStates, alphabet, rand);
        } else {
            fsm.randomizeFSMnonDet(numOfStates, 
                            numOfStates > 1 ? numOfStates * 3 : numOfStates * 2, 
                            alphabet, 
                            rand);
        }
        
        return fsm.createScriptFromInstance();
    }

    private boolean continueLongOperationRandomize() {
        return GeneralDialog.continueLongOperation(
                "Long-time operation", 
                "Do you want to continue randomizing FSM?", 
                RANDOMIZE_LONGTIME, 
                RANDOMIZE_LONGTIME_ID);
    }
    
    public Grammar generateType3Grammar() {
        Grammar g = new Grammar(new Nonterminal(this.initialState), this.getExercise());

        LinkedList<String> reachableStates = new LinkedList<>(this.getAllReachableStates());
        
        Collections.sort(reachableStates);
        
        for (String fromState : reachableStates) {
            for (Transition targetTransition : this.getTransitionsFrom(fromState)) {
                String toState = targetTransition.getDestination();
                String symbol = targetTransition.getLabel();
                
                g.addRule(new Rule(
                        new Word(new Symbol[] {new Nonterminal(fromState)}), 
                        new Word(new Symbol[] {
                                new Terminal(symbol),
                                new Nonterminal(toState)})));
            }
            
            if (this.finalStates.contains(fromState)) {
                g.addRule(new Rule(
                        new Word(new Symbol[] {new Nonterminal(fromState)}), 
                        new Word(new Symbol[] {})));
            }
        }
        
        boolean sensing = g.isSenseLongTimeOperations();
        g.setSenseLongTimeOperations(false);
        g.removeUnreachableRules();
        g.setSenseLongTimeOperations(sensing);
        
        return g;
    }
    
    public PDA generatePDA() {
        PDA pda = new PDA(this.getExercise());
        pda.reset();
        pda.setInitialState(this.initialState);
        
        if (this.input != null && !this.input.isEmpty()) {
            pda.setInput(this.input);
        } else {
            pda.setInput(this.getInputAlphabet().toString().replace("[", "").replace("]", "").replace(", ", "")
                    + this.getInputAlphabet().toString().replace("[", "").replace("]", "").replace(", ", ""));
        }
        
        this.finalStates.forEach(state -> pda.addFinalState(state));

        this.transitions.forEach(trans -> pda.addTransition(
                new veryFastPDF.algorithms.pda.Transition(
                        new StateTapesymbolKellersymbol(trans.getSource(), trans.getLabel(), pda.getKellerZeichen()), 
                        new StateKellersymbols(trans.getDestination(), pda.getKellerZeichen()))));
        
        return pda;
    }
    
    public Turing generateTuring() {
        String init = this.initialState;
        HashSet<String> term = new HashSet<>(this.finalStates);
        
        Turing turing = new Turing(term, init, "*", this.getExercise());
        turing.initializeTape(this.getInputAlphabet().toString().replace("[", "").replace("]", "").replace(", ", "")
                + this.getInputAlphabet().toString().replace("[", "").replace("]", "").replace(", ", ""));
        
        this.transitions.forEach(trans -> turing.addTransition(
                trans.getSource(), 
                trans.getLabel(), 
                trans.getDestination(), 
                trans.getLabel(), 
                1));
        
        turing.setRunStepsScript(turing.getTape().size() + 1);
        
        return turing;
    }
    
    @Override
    public JComponent getAdditionalInfo() {
        JPanel panel = new JPanel(new MigLayout("wrap 1"));
        String title = this.isDeterministic() ? (this.isMinimal() ? "Deterministic minimal FSM" : "Deterministic FSM") : "Nondeterministic FSM";
        
        FancyJLabel labelFSMtype = new FancyJLabel(title);
        
        FancyJButton buttGenerateExercise = new FancyJButton(
                "Generate exercise...", 
                () -> this.generateExercise(
                        super.getFather().getWorkingDirectory().getAbsolutePath(), 
                        WebLink.fileName(WebLink.DEFAULT_OUTPUT_FILE_NAME)));
        
        panel.add(labelFSMtype);
        panel.add(buttGenerateExercise);
        panel.add(super.getAdditionalInfo());
        
        return panel;
    }

    @ConversionMethod(plainText = false)
    public String convertToDeterministicFSMScript() {
        GeneralDialog.resetLongTimeOperationID(DET_LONG_OP_ID);

        FSM detFSM = new FSM(this);
        try {
            detFSM.makeDeterministic(true);
        } catch (Exception e) {
            throw new RuntimeException("Determinization failed.");
        }

        return detFSM.createCodeFromInstance(true);
    }

    @ConversionMethod(plainText = false)
    public String convertToMinimalFSMScript() {
        FSM minFSM = new FSM(this);
        minFSM.minimize();
        return minFSM.createScriptFromInstance();
    }

    @ConversionMethod(plainText = false)
    public String convertToRLGrammarScript() {
        return generateType3Grammar().createScriptFromInstance();
    }

    @ConversionMethod(plainText = false)
    public String convertToEquivalentTMScript() {
        Turing generatedTM = generateTuring();
        generatedTM.setTempInputToCreateCodeFrom(StaticMethods.formatCollection(this.getInputAlphabet()));
        return generatedTM.createScriptFromInstance();
    }

    @ConversionMethod(plainText = false)
    public String convertToRegExLatex() {
        String tempScript = RegularExpression.SCRIPT_PREAMBLE 
                + "\n" 
                + RegularExpression.createExpression(generateRegExp().toString());
        
        RegularExpression regex = new RegularExpression(getExercise());
        
        return tempScript + "\n" + regex.generateCompleteDeclarationsBlock();
    }

    @ConversionMethod(plainText = false)
    public String convertToPDAScript() {
        return generatePDA().createScriptFromInstance();
    }
//
//    public String createLatexMinimizationTable() {
//        return LaTeXCode.LATEX_PREFIX + "\n" + LaTeXCode.PREAMBLE_STANDARD
//                + getPlainLatexMinimizationTable()
//                + "\\thispagestyle{empty}"
//                + LaTeXCode.POSTAMBLE_STANDARD
//                + this.getUpperClassDeclarationsBlockOnly();
//    }

    public String getPlainLatexMinimizationTable() {
        if (!this.isDeterministic()) {
            return "\\par \\textit{Not available, FSM is not deterministic.}";
        }
        
        FSM copy = new FSM(this);
        return this.getTriangleTableLaTeX(copy.minimize());
    }

    @Override
    public Class<? extends PDFProcessor> getPDFProcessorClass() {
        return this.displayMode > 0 ? LaTeXPDF.class : GraphViz.class;
    }
    
    @ConversionMethod(plainText = false)
    public String switchDisplayModes() {
        this.displayMode = (this.displayMode + 1) % 3;
        return this.createScriptFromInstance();
    }
    
    @ConversionMethod(plainText = false)
    public String switchAdditionalFSMsModes() {
        if (this.showDeterministicFSM && this.showMinimizedFSM) {
            this.showDeterministicFSM = false;
            this.showMinimizedFSM = false;
        } else if (!this.showDeterministicFSM && this.showMinimizedFSM) {
            this.showDeterministicFSM = false;
            this.showMinimizedFSM = false;
        } else if (this.showDeterministicFSM && !this.showMinimizedFSM) {
            this.showDeterministicFSM = true;
            this.showMinimizedFSM = true;
        } else if (!this.showDeterministicFSM && !this.showMinimizedFSM) {
            this.showDeterministicFSM = true;
            this.showMinimizedFSM = false;
        }
        
        return this.createScriptFromInstance();
    }
    
    @ConversionMethod(plainText = true)
    public String getNumStates() {
        String string = this.getAllReachableStates().size() + "";
        return string;
    }

    @ConversionMethod(plainText = true)
    public String getRegexPlain() {
        String tempScript = RegularExpression.SCRIPT_PREAMBLE 
                + "\n" 
                + RegularExpression.createExpression(generateRegExp().toString());
        
        RegularExpression regex = new RegularExpression(getExercise());
        tempScript += "\n" + regex.generateCompleteDeclarationsBlock();
        regex.createInstanceFromScript(tempScript, null);
        String result = regex.getExpressionPlain().toString(true);
        
        return "$" + result + "$";
    }
    
    @ConversionMethod
    public String getInputLength() {
        if (this.input == null) {
            return 0 + "";
        }
        
        return this.input.length() + "";
    }
    
    @Override
    public HashMap<String, MethodWrapper> getDynamicMethods() {
        HashMap<String, MethodWrapper> methods = super.getDynamicMethods();

        String minChainName = "Show minimization chain";
        String pdaName = "PDA";
        String regExName = REGULAR_EXPRESSION_METHOD_NAME;
        String tmName = "TM";
        String rlGrammName = "Right-linear Grammar";
        String minimizeName = MINIMIZE_METHOD_NAME;
        String detName = DETERMINIZE_METHOD_NAME;
        String simulateName = SIMULATE_ONE_STEP_METHOD_NAME;
        String showMinimizationTableName = TOGGLE_MINIMIZATION_TABLE_METHOD_NAME;
        String toggleMinDetView = "Toggle minimized/determinized FSM";
        String minChainName_G = "Zeige Minimierungs-Ablauf";
        String randName = RANDOMIZE_METHOD_NAME;
        String randName_G = "Zufälliger Automat";
        String randNameD = RANDOMIZE_D_METHOD_NAME;
        String randNameD_G = "Zufälliger Automat (mit Seed)";
        String pdaName_G = "Kellerautomat";
        String regExName_G = "Regulärer Ausdruck";
        String tmName_G = "Turingmaschine";
        String rlGrammName_G = "Rechtslineare Grammatik";
        String minimizeName_G = "Minimiere";
        String detName_G = "Mache deterministisch";
        String simulateName_G = "Simuliere einen Schritt";
        String showMinimizationTableName_G = "Anzeige der Minimierungstabelle umschalten";
        String toggleMinDetView_G = "Anzeige des minimierten/deterministischen Automaten umschalten";
        String numStates = "states";
        String regExPlain = REGULAR_EXPRESSION_PLAIN_METHOD_NAME;
        String inputLengthName = "inputLength";
        String animateSimName = METHOD_NAME_ANIMATE_FSM_SIMULATION;
        String animateSimName_G = "Animiere EA-Simulation";

        try {
            MethodWrapper mwh2 = new MethodWrapper(
                    this.getClass().getMethod("getInputLength"), 
                    this, 
                    inputLengthName);
            
            MethodWrapper mwh0 = new MethodWrapper(
                    this.getClass().getMethod("getNumStates"), 
                    this,
                    numStates);
            
            MethodWrapper mwh1 = new MethodWrapper(
                    this.getClass().getMethod("getRegexPlain"), 
                    this,
                    regExPlain);
            
            MethodWrapper mw0 = new MethodWrapper(
                    this.getClass().getMethod("getMinimizationChain"), 
                    (Class<? extends RepresentableAsPDF>) null,
                    this,
                    "Show information for determinization and minimization.",
                    "Zeige Informationen zum deterministisch Machen und zur Minimierung.",
                    minChainName,
                    minChainName_G);
            mw0.setReturnValueIsScript(false);
            
            MethodWrapper mw1 = new MethodWrapper(
                    this.getClass().getMethod("createRandomFSMScript", Integer.TYPE, Boolean.TYPE),
                    FSM.class, // Target script class. Important to set correctly!
                    this,
                    "Create new random FSM.",
                    "Erzeuge neuen, zufälligen endlichen Automaten.",
                    randName,
                    randName_G);
            mw1.setParameterExplanation(0, "The number of states in the random target automaton.");
            mw1.setParameterExplanation_G(0, "Die Anzahl der Zustände im zufälligen Automaten.");
            mw1.setParameterExplanation(1, "If set to true, the resulting automaton will be deterministic.");
            mw1.setParameterExplanation_G(1, "Setze diesen Parameter auf true, um einen deterministischen Automaten zu erzeugen.");
            
            MethodWrapper mw1d = new MethodWrapper(
                    this.getClass().getMethod("createRandomFSMScriptSeed", Integer.TYPE, Boolean.TYPE, Long.TYPE),
                    FSM.class, // Target script class. Important to set correctly!
                    this,
                    "Create new random FSM by providing a fixed random seed.",
                    "Erzeuge neuen, zufälligen endlichen Automaten unter Angabe eines Seed-Werts für den Zufallsgenerator",
                    randNameD,
                    randNameD_G);
            mw1d.setParameterExplanation(0, "The number of states in the random target automaton.");
            mw1d.setParameterExplanation_G(0, "Die Anzahl der Zustände im zufälligen Automaten.");
            mw1d.setParameterExplanation(1, "If set to true, the resulting automaton will be deterministic.");
            mw1d.setParameterExplanation_G(1, "Setze diesen Parameter auf true, um einen deterministischen Automaten zu erzeugen.");
            mw1d.setParameterExplanation(2, "The random seed.");
            mw1d.setParameterExplanation_G(2, "Der Seed für den Zufallsgenerator.");

            MethodWrapper mw2 = new MethodWrapper(
                    this.getClass().getMethod("convertToPDAScript"),
                    PDA.class, // Target script class. Important to set correctly!
                    this,
                    "Convert current FSM to equivalent PDA",
                    "Konvertiere in äquivalenten Kellerautomaten",
                    pdaName,
                    pdaName_G);
            MethodWrapper mw3 = new MethodWrapper(
                    this.getClass().getMethod("convertToRegExLatex"),
                    RegularExpression.class, // Target script class. Important to set correctly!
                    this,
                    "Convert into a regular expression equivalent to current FSM",
                    "Erzeuge äquivalenten regulären Ausdruck",
                    regExName,
                    regExName_G);
            MethodWrapper mw4 = new MethodWrapper(
                    this.getClass().getMethod("convertToEquivalentTMScript"),
                    Turing.class, // Target script class. Important to set correctly!
                    this,
                    "Convert current FSM to equivalent Turing machine",
                    "Konvertiere in äquivalente Turingmaschine",
                    tmName,
                    tmName_G);
            MethodWrapper mw5 = new MethodWrapper(
                    this.getClass().getMethod("convertToRLGrammarScript"),
                    Grammar.class, // Target script class. Important to set correctly!
                    this,
                    "Convert current FSM to equivalent right-linear Grammar",
                    "Konvertiere in äquivalente rechtslineare Grammatik",
                    rlGrammName,
                    rlGrammName_G);
            MethodWrapper mw6 = new MethodWrapper(
                    this.getClass().getMethod("convertToMinimalFSMScript"),
                    FSM.class, // Target script class. Important to set correctly!
                    this,
                    "Minimize current FSM",
                    "Minimiere endlichen Automaten",
                    minimizeName,
                    minimizeName_G);
            MethodWrapper mw9 = new MethodWrapper(
                    this.getClass().getMethod("switchDisplayModes"),
                    FSM.class, // Target script class. Important to set correctly!
                    this,
                    "Switches between different display modes, e.g., showing the plain graph or the minimization table",
                    "Schaltet zwischen den verschiedenen Anzeigemodi um, bspw. um den endlichen Automaten oder seine Minimierungstabelle anzuzeigen",
                    showMinimizationTableName,
                    showMinimizationTableName_G);
            mw9.setDisplayLevel(2.5);
            mw9.setDisplayLevelName(2.5, "Display modes", "Anzeigemodi");
            MethodWrapper mw10 = new MethodWrapper(
                    this.getClass().getMethod("switchAdditionalFSMsModes"),
                    FSM.class, // Target script class. Important to set correctly!
                    this,
                    "Switches between different visualization modes of a deterministic/minimized version of the current FSM in the same graph",
                    "Schaltet zwischen verschiedenen Varianten der Anzeige der deterministischen/minimierten Version dieses Automaten um",
                    toggleMinDetView,
                    toggleMinDetView_G);
            mw10.setDisplayLevel(2.5);
            
            MethodWrapper mw7 = new MethodWrapper(
                    this.getClass().getMethod("convertToDeterministicFSMScript"),
                    FSM.class, // Target script class. Important to set correctly!
                    this,
                    "Make current FSM deterministic",
                    "Mache endlichen Automaten deterministisch",
                    detName,
                    detName_G);
            if (this.isDeterministic()) {
                mw7.setMethodButtonEnabled(false);
                mw7.setTooltip(mw7.getTooltip() + " (not available: FSM is already deterministic)");
                mw7.setTooltip_G(mw7.getTooltip_G() + " (nicht verfügbar: endlicher Automat ist bereits deterministisch)");
                if (this.isMinimal()) {
                    mw6.setMethodButtonEnabled(false);
                    mw6.setTooltip(mw6.getTooltip() + " (not available: FSM is already minimal)");
                    mw6.setTooltip_G(mw6.getTooltip_G() + " (nicht verfügbar: endlicher Automat is bereits minimal)");
                }
            } else {
//                mw9.setMethodButtonEnabled(false);
//                mw9.setTooltip(mw9.getTooltip() + " (not available: determinize FSM first)");
//                mw9.setTooltip_G(mw9.getTooltip_G() + " (nicht verfügbar: mache den Automaten erst deterministisch)");
                mw6.setMethodButtonEnabled(false);
                mw6.setTooltip(mw6.getTooltip() + " (not available: determinize FSM first)");
                mw6.setTooltip_G(mw6.getTooltip_G() + " (nicht verfügbar: mache den Automaten erst deterministisch)");
            }

            MethodWrapper mw8;
            MethodWrapper mwh8;
            
            String tooltipToShowInGUI = "Simulate for one step according to given input string";
            String tooltipToShowInGIU_G = "Simuliere endlichen Automaten für einen Schritt";
            String tooltipToShowInGUI2 = "Create a standard animation simulating a given input.";
            String tooltipToShowInGIU2_G = "Standardanimation zur Simulation eines gegebenen Inputworts.";
            if (this.input == null || "".equals(this.input) || "null".equals(this.input)) {
                // Zuerst Abfrage nach zu simulierendem Wort.

                String explanation = "Enter an input word to simulate.";
                String explanation_G = "Gib ein Eingabewort ein, auf dem der Automat simuliert werden soll.";
                mwh8 = new MethodWrapper(
                        this.getClass().getMethod("animateSim", String.class),
                        FSM.class, // Target script class. Important to set correctly!
                        this,
                        tooltipToShowInGUI2,
                        tooltipToShowInGIU2_G,
                        animateSimName,
                        animateSimName_G);
                mwh8.setParameterExplanation(0, explanation);
                mwh8.setParameterExplanation_G(0, explanation_G);
                
                mw8 = new MethodWrapper(
                        this.getClass().getMethod("simulateOneStep", String.class),
                        FSM.class, // Target script class. Important to set correctly!
                        this,
                        tooltipToShowInGUI,
                        tooltipToShowInGIU_G,
                        simulateName,
                        simulateName_G);
                mw8.setParameterExplanation(0, explanation);
                mw8.setParameterExplanation_G(0, explanation_G);
            } else {
                // Direkt simulieren, Wort existiert schon.
                mwh8 = new MethodWrapper(
                        this.getClass().getMethod("animateSim"),
                        FSM.class, // Target script class. Important to set correctly!
                        this,
                        tooltipToShowInGUI2,
                        tooltipToShowInGIU2_G,
                        animateSimName,
                        animateSimName_G);

                mw8 = new MethodWrapper(
                        this.getClass().getMethod("simulateOneStep"),
                        FSM.class, // Target script class. Important to set correctly!
                        this,
                        tooltipToShowInGUI,
                        tooltipToShowInGIU_G,
                        simulateName,
                        simulateName_G);
            }
            mwh8.setDisplayLevel(5);
            
            methods.put(minChainName, mw0);
            methods.put(randName, mw1);
            methods.put(randNameD, mw1d);
            methods.put(pdaName, mw2);
            methods.put(regExName, mw3);
            methods.put(tmName, mw4);
            methods.put(rlGrammName, mw5);
            methods.put(minimizeName, mw6);
            methods.put(detName, mw7);
            methods.put(simulateName, mw8);
            methods.put(animateSimName, mwh8);
            methods.put(showMinimizationTableName, mw9);
            methods.put(toggleMinDetView, mw10);
            methods.put(numStates, mwh0);
            methods.put(regExPlain, mwh1);
            methods.put(inputLengthName, mwh2);
        } catch (SecurityException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        
        return methods;
    }
    
    @Override
    public HashMap<String, String> getMetaProperties() {
        String fsmClassName = FSM.class.getSimpleName();
        
        // Det.
        GeneralDialog.resetLongTimeOperationID(DET_LONG_OP_ID);
        boolean deterministic = this.isDeterministic();
        // Minimized.
        Boolean minimized = deterministic ? this.isMinimal() : null;
        // Input alphabet.
        String inputAlphabet = this.getInputAlphabet().toString();
        // Number of transitions.
        int numOfTransitions = this.transitions.size();
        // Number of final states.
        int numOfFinalStates = this.finalStates.size();
        // Number of states.
        int numStates = this.getAllStates().size();
        // Hashcode.this
        int hash = (this.getAllReachableStates() + "" + this.transitions + this.finalStates + this.initialState).hashCode();
        
        HashMap<String, String> props = super.getMetaProperties();
        props.put(fsmClassName + "_Minimized", "" + minimized);
        props.put(fsmClassName + "_Deterministic", "" + deterministic);
        props.put(fsmClassName + "_InputAlphabet", "" + inputAlphabet);
        props.put(fsmClassName + "_NumTransitions", "" + numOfTransitions);
        props.put(fsmClassName + "_NumFinalStates", "" + numOfFinalStates);
        props.put(fsmClassName + "_NumStates", "" + numStates);
        props.put(fsmClassName + "_TaskID", "" + hash);
        
        return props;
    }

    @Override
    public String getGermanName() {
        return "Endlicher-Automat";
    }
    
    @Override
    public String getModeDependentInfo(String mode, boolean english) {
        if (mode.equals(ConvenienceMethods.INFO_II_MODE_NAME)) {
            return ConvenienceMethods.createInfo2ModeString(
                    2, 
                    2, 
                    1, 
                    "http://www.dasinfobuch.de/links/Endliche-Automaten-ohne-Ausgabe.html",
                    "http://info2.aifb.kit.edu/qa/index.php?qa=347&qa_1=band-i-kapitel-2",
                    english
                    );
        }

        return "";
    }

    @Override
    public HashMap<String, String> getMethodNameAbbreviations() {
        HashMap<String, String> methodNameAbbreviations = super.getMethodNameAbbreviations();
        
        methodNameAbbreviations.put("rand", RANDOMIZE_METHOD_NAME);
        methodNameAbbreviations.put("randD", RANDOMIZE_D_METHOD_NAME);
        methodNameAbbreviations.put("min", MINIMIZE_METHOD_NAME);
        methodNameAbbreviations.put("det", DETERMINIZE_METHOD_NAME);
        methodNameAbbreviations.put("sim", SIMULATE_ONE_STEP_METHOD_NAME);
        methodNameAbbreviations.put("regex", REGULAR_EXPRESSION_PLAIN_METHOD_NAME);
        methodNameAbbreviations.put("animate", METHOD_NAME_ANIMATE_FSM_SIMULATION);
        methodNameAbbreviations.put("minTable", TOGGLE_MINIMIZATION_TABLE_METHOD_NAME);

        return methodNameAbbreviations;
    }
}
