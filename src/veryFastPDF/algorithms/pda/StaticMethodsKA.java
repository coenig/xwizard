/*
 * File name:        StaticMethods.java (package eas.math.fundamentalAlgorithms.graphBased.pushDown)
 * Author(s):        Lukas König
 * Java version:     7.0
 * Generation date:  18.04.2014 (14:11:04)
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
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import eas.miscellaneous.StaticMethods;

/**
 * @author Lukas König
 */
public class StaticMethodsKA {
    
    public static String convertTraceElementLatex(TraceElement el, String kellerZeichen, String lambda, boolean det) {
        if (el == null) {
            return "\\hline\n\\multicolumn{3}{|c|}{accepted}";
        }
        
        String s = "";
        s += "$";
        
        try {
            
            for (int i = 0; i < el.getHeadPos(); i++) {
                s += el.getInput().get(i);
            }
            
            s += "\\hat{" + el.getInput().get(el.getHeadPos()) + "} ";
            
            for (int i = el.getHeadPos() + 1; i < el.getInput().size(); i++) {
                s += el.getInput().get(i);
            }
        } catch (Exception e) {s += " ";}

        s += "$ & $";
        
        try {
            LinkedList<String> keller = new LinkedList<>();
            
            // Reverse ordering of keller string.
            for (String k : el.getKeller()) {
                keller.addFirst(k);
            }
            
            s += "[\\hat " + StaticMethods.formatCollection(keller).replace(kellerZeichen, "k_0").replace(",", "") + "]";
        } catch (Exception e) {s += " ";}
        s += "$ & $";
        
        try {
            String transition = convertTransitionLatex(el.getTransition(), kellerZeichen, lambda, det);

            if (TraceElement.isAccepted(el)) {
                transition = "\\mbox{accepted}";
            } else if (TraceElement.isNotAccepted(el)) {
                transition = "\\mbox{not accepted}";
            }

            s += transition;
        } catch (Exception e) {s += " ";}
        
        s += "$";
        
        return s;
    }
    
    public static String convertTransitionLatex(Transition t, String kellerZeichen, String lambda, boolean det) {
        String ob = "";
        String cb = "";
        if (!det) {
            ob = "\\{";
            cb = "\\}";
        }
        
        return t.toString().replace(kellerZeichen, "k_0")
                .replace(lambda, "\\lambda").replace("[]", "\\lambda")
                .replace("(s", "(s_").replace("=>", "\\Rightarrow" + ob).replace("[", "").replace("]", "") + cb;
    }

    public static String getTraceLatex(Trace t, String kellerZeichen, String lambda, boolean det) {
        String s = "\\begin{tabular}{|l|r|l|}\n";

        s += "\\hline\n";

        s += "Input & Stack & Transition \\\\";
        
        s += "\\hline\n";
        s += "\\hline\n";

        for (TraceElement el : t) {
            s += convertTraceElementLatex(el, kellerZeichen, lambda, det) + "\\\\\n";
        }
        
        s += "\\hline";
        
        s += "\\end{tabular}";
        
        return s;
    }
    
    public static String getConfigurationTraceLatex(Trace trace, String kellerZeichen) {
        String s = "\\begin{align*}\n";
        
        TraceElement el = trace.get(0);

        s += "("
                + extractState(el)
                + ","
                + extractElementInput(el)
                + ","
                + el.getKeller().toString().replace(", ", "").replace("[", "").replace("]", "").replace(kellerZeichen, "k_0")
                + ")";
        s += " &\\vdash ";

        int size = -s.length();
        for (int i = 1; i < trace.size() - 1; i++) {
            el = trace.get(i);
            
            s += "("
                    + extractState(el)
                    + ","
                    + extractElementInput(el)
                    + ","
                    + el.getKeller().toString().replace(", ", "").replace("[", "").replace("]", "").replace(kellerZeichen, "k_0")
                    + ")";
            
            if (s.length() + size > 55) {
                s += " \\\\\n&\\vdash ";
                size = -s.length();
            } else {
                s += " \\vdash ";
            }
        }
        
        el = trace.get(trace.size() - 1);
        if (el != null) {
            s += "("
                    + extractState(el)
                    + ","
                    + extractElementInput(el)
                    + ","
                    + el.getKeller().toString().replace(", ", "").replace("[", "").replace("]", "").replace(kellerZeichen, "k_0")
                    + ")";
        } else {
            s += " " + el;
        }
        
        s += "\n\\end{align*}";
        
        return s;
    }

    private static String extractState(TraceElement el) {
        if (TraceElement.isAccepted(el)) {
            return "accepted";
        }
        
        if (TraceElement.isNotAccepted(el)) {
            return "not accepted";
        }
        
        return el.getTransition().from.state.charAt(0) + "_{" + el.getTransition().from.state.substring(1) + "}";
    }

    private static String extractElementInput(TraceElement el) {
        List<String> remainderInput = el.getInput().subList(el.getHeadPos(), el.getInput().size());
        if (remainderInput.size() == 0) {
            return "\\lambda";
        }
        return remainderInput.toString().replace(", ", "").replace("[", "").replace("]", "");
    }

    public static String printInputWithHead(int headPos, ArrayList<String> input) {
        String tapePrinted = "";
        
        for (int i = 0; i < input.size(); i++) {
            if (i == headPos) {
                tapePrinted += "|";
            }
            tapePrinted += input.get(i);
            if (i == headPos) {
                tapePrinted += "|";
            }
        }
        
        return tapePrinted;
    }
    
    public static String getTransitionsLatex(
            HashMap<StateTapesymbolKellersymbol, Transition> transitions, 
            String kellerZeichen, 
            String lambda, 
            boolean det) {
        String s = "$$A = (E, S, K, \\delta, s_0, k_0, F)$$\n\\begin{tabular}{rcl}\n";
        
        List<Transition> trans = new ArrayList<>(transitions.values());
        
        Collections.sort(trans, (c1, c2) -> c1.toString().compareTo(c2.toString()));
        
        for (Transition t : trans) {
            s += "$" + convertTransitionLatex(t, kellerZeichen, lambda, det).replace("\\Rightarrow", "$ & $\\Rightarrow$ & $") + "$\\\\\n";
        }
        
        s += "\\end{tabular}";
        
        return s;
    }
    
    public static LinkedList<Transition> getNondetTrans(
            HashMap<StateTapesymbolKellersymbol, Transition> transitionsRegular,
            HashMap<StateTapesymbolKellersymbol, Transition> transitionsLambda,
            String lambda) {
        HashMap<StateTapesymbolKellersymbol, Transition> transitionsAll = new HashMap<>(
                transitionsLambda.size() + transitionsRegular.size());
        
        transitionsAll.putAll(transitionsLambda);
        transitionsAll.putAll(transitionsRegular);

        LinkedList<Transition> trans = new LinkedList<Transition>();
        
        Object[] regNondet = transitionsAll.values().stream().filter(t -> !t.isDeterministic()).toArray();
        
        // Refular nondeterministic transitions.
        for (int i = 0; i < regNondet.length; i++) {
            trans.add((Transition) regNondet[i]);
        }
        
        Object[] lambdaNondet = transitionsRegular.values().stream().filter(
                t -> transitionsLambda.containsKey(
                        new StateTapesymbolKellersymbol(t.from.state, lambda, t.from.kellerSymbol))).toArray();

        for (int i = 0; i < lambdaNondet.length; i++) {
            Transition lambdaTransRegPart = (Transition) lambdaNondet[i];
            Transition lambdaTransLambdaPart = transitionsLambda.get(
                    new StateTapesymbolKellersymbol(lambdaTransRegPart.from.state, lambda, lambdaTransRegPart.from.kellerSymbol));
            
            trans.add(lambdaTransRegPart);
            trans.add(lambdaTransLambdaPart);
        }

        return trans;
    }
}
