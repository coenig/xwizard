/*
 * File name:        RepresentableFactory.java (package veryFastPDF.script)
 * Author(s):        Lukas König
 * Java version:     8.0 (at generation time)
 * Generation date:  29.05.2015 (21:07:30)
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

package veryFastPDF.script;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import eas.miscellaneous.system.rits.cloning.Cloner;
import mainServlet.WebLink;
import veryFastPDF.algorithms.bdd.BDD;
import veryFastPDF.algorithms.circuits.LogicCircuit;
import veryFastPDF.algorithms.fsm.FSM;
import veryFastPDF.algorithms.gnuPlot.GNUPlotCode;
import veryFastPDF.algorithms.grammars.Grammar;
import veryFastPDF.algorithms.huffman.Huffman;
import veryFastPDF.algorithms.latex.LaTeX;
import veryFastPDF.algorithms.metaProperties.MetaProperties;
import veryFastPDF.algorithms.numberRep.Numbers;
import veryFastPDF.algorithms.patTree.PatTree;
import veryFastPDF.algorithms.pda.PDA;
import veryFastPDF.algorithms.plainDOT.Graphviz;
import veryFastPDF.algorithms.regEx.RegularExpression;
import veryFastPDF.algorithms.searchTree.redblacktree.RedBlackTree;
import veryFastPDF.algorithms.searchTree.tree234.Tree234;
import veryFastPDF.algorithms.turing.Turing;
import veryFastPDF.plugin.VFPParameters;
import veryFastPDF.script.testing.Tester;
import veryFastPDF.web.ConvenienceMethods;
import veryFastPDF.web.Webproof;

/**
 * @author Lukas König
 */
public class RepresentableFactory {

    @SuppressWarnings("unchecked")
    public static final Class<? extends RepresentableAsPDF>[] THERETICAL_CLASSES = new Class[] {
            FSM.class, 
            PDA.class,
            Turing.class,
            Grammar.class,
            RegularExpression.class, 
            Tree234.class,
            RedBlackTree.class,
            PatTree.class,
    };
    
    @SuppressWarnings("unchecked")
    public static final Class<? extends RepresentableAsPDF>[] PRACTICAL_CLASSES = new Class[] {
            BDD.class,
            Huffman.class,
            LogicCircuit.class,
            Numbers.class,
    };
    
    @SuppressWarnings("unchecked")
    public static final Class<? extends RepresentableAsPDF>[] NATIVE_CLASSES = new Class[] {
            LaTeX.class,
            Graphviz.class,
//            JavaPDFCode.class,
//            GNUPlotCode.class,
    };
    
    @SuppressWarnings("unchecked")
    public static final Class<? extends RepresentableAsPDF>[] MISC_CLASSES = new Class[] {
            Tester.class,
            MetaProperties.class,
    };

    
    public static final HashMap<String, List<Class<? extends RepresentableAsPDF>>> CLASS_COLLECTIONS = new HashMap<>();
    
    public static final String THEORY_NAME = "Theoretical Informatics";
    public static final String PRACTICE_NAME = "Computer Engineering";
    public static final String NATIVE_NAME = "PDF Code (all scripts translate into one of these)";
    public static final String MISC_NAME = "Other";
    public static final String FCS_NAME_G = "Theoretische Informatik";
    public static final String TREES_NAME_G = "Technische Informatik";
    public static final String NATIVE_NAME_G = "PDF-Code (Zwischenschritt vom Skript zum PDF)";
    public static final String MISC_NAME_G = "Sonstige";
    
    public static final HashMap<String, String> ABBREVIATIONS = new HashMap<>();
    public static final HashMap<String, String> ABBREVIATIONS_G = new HashMap<>();
    public static final HashMap<String, String> NAMES_G = new HashMap<>();
    
    public static final Comparator<String> COMPARATOR_CATEGORIES;
    
    static {
        ABBREVIATIONS.put(ConvenienceMethods.INFO_II_MODE_NAME, "Info II mode");
        ABBREVIATIONS.put(ConvenienceMethods.TEXTBOOK_MODE_NAME, "Textbook mode");
        ABBREVIATIONS.put(ConvenienceMethods.EFFALG_MODE, "Efficient Algorithms mode");
        ABBREVIATIONS_G.put(ConvenienceMethods.INFO_II_MODE_NAME, "Info-II-Modus");
        ABBREVIATIONS_G.put(ConvenienceMethods.TEXTBOOK_MODE_NAME, "Lehrbuch-Modus");
        ABBREVIATIONS_G.put(ConvenienceMethods.EFFALG_MODE, "'Effiziente Algorithmen'-Modus");
        
        NAMES_G.put(THEORY_NAME, FCS_NAME_G);
        NAMES_G.put(PRACTICE_NAME, TREES_NAME_G);
        NAMES_G.put(NATIVE_NAME, NATIVE_NAME_G);
        NAMES_G.put(MISC_NAME, MISC_NAME_G);
        
        ArrayList<Class<? extends RepresentableAsPDF>> info2List = new ArrayList<>();
        ArrayList<Class<? extends RepresentableAsPDF>> textbookList = new ArrayList<>();

        textbookList.add(FSM.class);
        textbookList.add(PDA.class);
        textbookList.add(Turing.class);
        textbookList.add(Grammar.class);
        textbookList.add(RegularExpression.class);
        textbookList.add(LaTeX.class);
        textbookList.add(Graphviz.class);
        textbookList.add(MetaProperties.class);
        
        info2List.add(FSM.class);
        info2List.add(PDA.class);
        info2List.add(Turing.class);
        info2List.add(Grammar.class);
        info2List.add(RegularExpression.class); 
        info2List.add(BDD.class);
        info2List.add(Huffman.class);
        info2List.add(LogicCircuit.class);
        info2List.add(Numbers.class);

        ArrayList<Class<? extends RepresentableAsPDF>> effalgList = new ArrayList<>();
        effalgList.add(Tree234.class);
        effalgList.add(RedBlackTree.class);
        
        CLASS_COLLECTIONS.put(ConvenienceMethods.INFO_II_MODE_NAME, info2List);
        CLASS_COLLECTIONS.put(ConvenienceMethods.TEXTBOOK_MODE_NAME, textbookList);
        CLASS_COLLECTIONS.put(ConvenienceMethods.EFFALG_MODE, effalgList);
        
        COMPARATOR_CATEGORIES = new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                if (isMisc(o1)) {
                    return 1;
                } else if (isMisc(o2)) {
                    return -1;
                } else if (isNative(o1)) {
                    return 1;
                } else if (isNative(o2)) {
                    return -1;
                }
                
                return o2.compareTo(o1);
            }

            public boolean isNative(String o1) {
                return o1.equals(NATIVE_NAME) || o1.equals(NATIVE_NAME_G);
            }

            public boolean isMisc(String o1) {
                return o1.equals(MISC_NAME) || o1.equals(MISC_NAME_G);
            }
        };
    }
    
    public static HashMap<String, List<Class<? extends RepresentableAsPDF>>> getAvailableTypesGrouped() {
        HashMap<String, List<Class<? extends RepresentableAsPDF>>> map = new HashMap<>();

        map.put(THEORY_NAME, Arrays.asList(THERETICAL_CLASSES));
        map.put(PRACTICE_NAME, Arrays.asList(PRACTICAL_CLASSES));
        map.put(NATIVE_NAME, Arrays.asList(NATIVE_CLASSES));
        map.put(MISC_NAME, Arrays.asList(MISC_CLASSES));
        
        return map;
    }
    
    /**
     * Add types that are AVAILABLE here (they may not be automatically included
     * in the run if studentVersion or specifyTypes is given explicitly). Also,
     * for the web version, only "webproof" reps are included.
     * 
     * @return  All available types to represent PDF scripts.
     */
    public static ArrayList<Class<? extends RepresentableAsPDF>> getAvailableTypes() {
        ArrayList<Class<? extends RepresentableAsPDF>> list = new ArrayList<>();
        
        getAvailableTypesGrouped().values().forEach(c -> list.addAll(c));
        
        return list;
    }
    
    /**
     * Remove here types that are not for the students' version.
     * 
     * @return  A restricted list of types, customized for students, to 
     *          represent PDF scripts.
     */
    private static List<Class<? extends RepresentableAsPDF>> getStudentTypes() {
        List<Class<? extends RepresentableAsPDF>> list = getAvailableTypes();
        
        list.remove(PatTree.class);
        list.remove(RedBlackTree.class);
        list.remove(Tree234.class); 
//        list.remove(LaTeXCode.class);
//        list.remove(RegularExpression.class);
        list.remove(GNUPlotCode.class); // PDF processor Gnuplot.
        
        return list;
    }
    
    /**
     * @param fromTypes  A list of representable types.
     * @return  The corresponding list of representable objects.
     * @throws Exception 
     */
    private static List<RepresentableAsPDF> getReps(
            List<Class<? extends RepresentableAsPDF>> fromTypes)
            throws Exception {
        LinkedList<RepresentableAsPDF> list = new LinkedList<>();
        
        for (Class<? extends RepresentableAsPDF> c : fromTypes) {
            RepresentableAsPDF rep;
            rep = newInstance(c);
            list.add(rep);
        }
        
        return list;
    }

    private static RepresentableAsPDF newInstance(
            Class<? extends RepresentableAsPDF> repClass)
                    throws Exception {
        RepresentableAsPDF rep;
        
        try {
            rep = repClass.newInstance(); // Add basic types without exercise support.
        } catch (Exception e1) { // If it fails, try with nulled exercise constructor.
            try {
                rep = repClass.getDeclaredConstructor(Exercise.class).newInstance((Exercise) null);
            } catch (IllegalArgumentException | InvocationTargetException
                    | NoSuchMethodException | SecurityException e) {
                throw e;
            }
        }
        
        return rep;
    }
    
    /**
     * Creates a rep instance from the given script by using all theoretically 
     * available PDF types. I.e., even types not available in the actual current
     * run can be used here. If the script starts with the non-script preamble,
     * indicating the application of a conversion method with plain-text 
     * output, a dummy representa
     * 
     * @param script  The script to create the rep instance from.
     * @param father  The super representable of this script or {@code null}.
     * 
     * @return  The rep instance.
     */
    public static RepresentableAsPDF instanceFromScript(String script, RepresentableAsPDF father) {
        if (script.startsWith(RepresentableDefault.PREAMBLE_FOR_NON_SCRIPT_METHODS)) {
            DummyRepresentable dummy = new DummyRepresentable(null);
            dummy.createInstanceFromScript(script, father);
            return dummy;
        }
        
        RepresentableAsPDF applicablePDFType = ScriptConversionMethods.getApplicablePDFType(
                ScriptConversionMethods.removeComments(script), 
                getAvailableTypes(),
                father);
        
        if (applicablePDFType == null) {
            return null;
//            throw new RuntimeException("No applicable 'Representable' type found for script: " + script);
        } else {
            applicablePDFType.createInstanceFromScript(script, father);
        }
        
        return applicablePDFType;
    }
    
    /**
     * @return  A list of all class simple names of the available representable
     *          types.
     */
    public static LinkedList<String> getAllRepNames() {
        LinkedList<String> names = new LinkedList<>();

        for (Class<? extends RepresentableAsPDF> c : getAvailableTypes()) {
            names.add(c.getSimpleName());
        }
        
        return names;
    }
    
    /**
     * Returns a list of representable objects corresponding to the class
     * simple names given in the list.
     * 
     * @param names  The class simple names (may be ignored if in student mode).
     * 
     * @return  A list of representable objects.
     */
    public static List<RepresentableAsPDF> getRepsByNames(Collection<String> names) {
        List<Class<? extends RepresentableAsPDF>> classList = new LinkedList<>();

        // Auto mode.
        if (VFPParameters.isStudentVersion()) {
            classList = getStudentTypes();
        } else {
            for (String name : names) {
                for (Class<? extends RepresentableAsPDF> c : getAvailableTypes()) {
                    if (name.equals(c.getSimpleName())) {
                        classList.add(c);
                        break;
                    }
                }
            }
        }
        
        try {
            return getReps(classList);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Generates a list of representables that is the subset of all representables
     * which is ready for the web.
     * 
     * @return  The list of web-ready representables.
     */
    public static List<Class<? extends RepresentableAsPDF>> getRepsForWeb() {
        List<Class<? extends RepresentableAsPDF>> list = getAvailableTypes();
        List<Class<? extends RepresentableAsPDF>> remove = new LinkedList<>();
        
        for (Class<? extends RepresentableAsPDF> repClass : list) {
            Webproof webproof = repClass.getAnnotation(Webproof.class);
            
            if (webproof == null
                    || !WebLink.isDebugMode() && !webproof.useInProductiveMode()) {
                remove.add(repClass);
            }
        }
        
        list.removeAll(remove);
        
        return list;
    }
    
    public static RepresentableAsPDF getRepByClass(Class<? extends RepresentableAsPDF> repClass) {
        try {
            return newInstance(repClass);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public static List<Class<? extends RepresentableAsPDF>> getAvailableWebRepNamesWhichMightBeUnstable() {
        List<Class<? extends RepresentableAsPDF>> list = getAvailableTypes();
        List<Class<? extends RepresentableAsPDF>> remove = new LinkedList<>();
        
        for (Class<? extends RepresentableAsPDF> repClass : list) {
            Webproof webproof = repClass.getAnnotation(Webproof.class);
            
            if (webproof == null || webproof.useInProductiveMode()) {
                remove.add(repClass);
            }
        }
        
        list.removeAll(remove);

        return list;
    }
    
    public static Class<? extends RepresentableAsPDF> repClassBySimpleName(String simpleName) {
        for (Class<? extends RepresentableAsPDF> c : getAvailableTypes()) {
            if (c.getSimpleName().equals(simpleName)) {
                return c;
            }
        }
        
        return null;
    }
    
    public static String getGermanNameByClass(Class<? extends RepresentableAsPDF> repClass) {
        return getRepByClass(repClass).getGermanName();
    }
    
    /**
     * Returns a plain empty object of the type given by the script. The script 
     * can, in principle, contain subscripts, it only has to start with a 
     * correct preamble. For all non-applicable scripts a DummyRepresentable 
     * object is returned. The script is NOT applied to the representable, though,
     * it is only set as rawScript.
     *  
     * @param script    The script to find a matching representable for.
     * @param pdfTypes  The available PDF types.
     * @return  An empty object of the respective type.
     */
    public static RepresentableAsPDF getPlainApplicableTypeWithoutTagsAndNoEvaluation(String script) {
        for (Class<? extends RepresentableAsPDF> repClass : WebLink.availablePDFTypes) {
            RepresentableAsPDF r = RepresentableFactory.getRepByClass(repClass);

            if (r.isAcceptableScript(script)) {
                if (RepresentableDefault.class.isAssignableFrom(r.getClass())) {
                    ((RepresentableDefault) r).setRawScript(script);
                }
                
                return r;
            }
        }

        return new DummyRepresentable(null);
    }
    
    /*
     * TODO: This is in test phase.
     */
    public static RepresentableAsPDF copyRep(RepresentableAsPDF original, boolean withPreprocessors) {
        if (original == null) {
            return null;
        }
        
        Cloner cloner = new Cloner();
        return cloner.deepClone(original);
        
//        try {
//            RepresentableAsPDF copy = newInstance(original.getClass());
//
//            if (!withPreprocessors) {
//                RepresentableDefault.ignorePreprocessorsAndAnimateOnce();
//            }
//
//            copy.createInstanceFromScript(original.getCurrentScript());
//            
//            return copy;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
    }
}
