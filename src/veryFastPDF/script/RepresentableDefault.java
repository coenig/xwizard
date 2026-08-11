/*
 * File name:        RepresentableDefault.java (package eas.math.fundamentalAlgorithms.graphBased.script)
 * Author(s):        Lukas König
 * Java version:     8.0 (at generation time)
 * Generation date:  01.11.2014 (18:39:15)
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

import java.awt.Color;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JPanel;

import org.apache.commons.lang3.StringUtils;

import com.fathzer.soft.javaluator.DoubleEvaluator;

import eas.GlobalVariables;
import eas.math.MiscMath;
import eas.miscellaneous.StaticMethods;
import eas.miscellaneous.convenience.GeneralDialog;
import eas.miscellaneous.convenience.RegExIndexer;
import eas.veryFastPDF.MainLink;
import mainServlet.WebLink;
import net.miginfocom.swing.MigLayout;
import veryFastPDF.HelpTexts;
import veryFastPDF.VFPVariables;
import veryFastPDF.algorithms.latex.LaTeX;
import veryFastPDF.algorithms.plainDOT.Graphviz;
import veryFastPDF.pdfProcessors.GraphViz;
import veryFastPDF.pdfProcessors.PDFProcessor;
import veryFastPDF.pdfProcessors.PDFProcessorFactory;
import veryFastPDF.plugin.FancyScriptConverterJButton;
import veryFastPDF.plugin.VFPWindow;
import veryFastPDF.script.exceptionHandling.LongOperationException;
import veryFastPDF.script.exceptionHandling.MethExistsException;
import veryFastPDF.script.testing.BigIntMethods;

/**
 * This class is the father class of virtually all Objects representable
 * with VFP. Its functionality includes:
 * <UL>
 * <LI>In-script preprocessor handling,</LI>
 * <LI>Declarations handling (including regular preprocessors),</LI>
 * <LI>Rule collapsing and decollapsing,</LI>
 * <LI>...</LI>
 * </UL>
 * 
 * @author Lukas König
 */
public abstract class RepresentableDefault implements RepresentableAsPDF, Wrappable {

    private static final long serialVersionUID = -3670743274897840129L;

    private static final String PREFIX_FOR_TIMED_IDENTIFIERS = "TT";
    private static final String QUALIFIED_IDENT_MARKER = "_";
    
    // Protected symbols.
    private static final char ASSIGNMENT_OPERATOR = '=';
    public static final String PLAIN_TEXT_BEGIN_TAG = "@\"{";
    public static final String PLAIN_TEXT_END_TAG = "}\"@";
    
    public static final String METHOD_PARS_BEGIN_TAG = "[";
    public static final String METHOD_PARS_END_TAG = "]";
    public static final String METHOD_CHAIN_SEPARATOR = ".";

    public static final String CONVERSION_PREFIX = "**>";
    public static final String CONVERSION_POSTFIX = "<**";
    
    public static final String BEGIN_COMMENT = "/*";
    public static final String END_COMMENT = "*/";
    
    public static final String DECL_BEG_TAG = "--declarations--";
    public static final String DECL_END_TAG = "--declarations-end--";
    
    public static final String START_TAG_FOR_NESTED_VARIABLES = "[~(~{";
    public static final String END_TAG_FOR_NESTED_VARIABLES = "}~)~]";
    
    public static final String INSCR_BEG_TAG = "@{";
    public static final String INSCR_END_TAG = "}@";
    public static final char INSCR_PRIORITY_SYMB = '*';

    public static final String VAR_BEG_TAG = "V$${";
    public static final String VAR_END_TAG = "}$$V";

    public static final String INSCR_BEG_TAG_FOR_INTERNAL_USAGE = INSCR_BEG_TAG + "$$$";
    public static final String INSCR_END_TAG_FOR_INTERNAL_USAGE = "$$$" + INSCR_END_TAG;

    public static final char BEGIN_LITERAL = '#';
    public static final char END_LITERAL = '#';
    
    private static final String EXPR_BEG_TAG = "~{";
    private static final String EXPR_END_TAG = "}~";
    
    public static final String THIS_NAME = "this";
    private static final String PREPROCESSOR_FIELD_NAME = "prep";
    private static final String VARIABLE_DELIMITER = "=";
    public static final char END_VALUE = ';';
    private static final String ANIMATE_FIELD_NAME = "animate";
    private static final String EXERCISE_FIELD_LONG_NAME = "exercise";
    private static final String EXERCISE_FIELD_SHORT_NAME = "e";
    private static final String NULL_VALUE = "n";
    public static final String PREAMBLE_FOR_NON_SCRIPT_METHODS = "$$INFO-METHOD-RETURN-VALUE$$\n";

    /**
     * Only for internal usage, this symbol is removed from the script
     * after the translation process is terminated.
     */
    private static final String NOP_SYMBOL = ":$N~O~P$:";
    // EO Protected symbols.
    
    private static final String STEPWISE_EXPANSION_ACTUAL_METHOD_NAME = "stepWiseScriptTranslation";
    private static final String CREATE_EXERCISE_FROM_THIS_SCRIPT_METHOD_NAME = "Create exercise from this script";
    private static final String URL_TO_THIS_SCRIPT_METHOD_NAME = "URL to this script";
    private static final String PLAIN_GENERATOR_CODE_METHOD_NAME = "Plain Generator code";
    private static final String DECOLLAPSE_RULES_RIGHT_METHOD_NAME = "Decollapse rules right";
    private static final String DECOLLAPSE_RULES_LEFT_METHOD_NAME = "Decollapse rules left";
    private static final String FORMAT_SCRIPT_METHOD_NAME = "Format script";
    private static final String ADD_DECLARATIONS_TO_SCRIPT_METHOD_NAME = "Add declarations to script";
    private static final String STEPWISE_EXPANSION_METHOD_NAME = "Stepwise script expansion";
    
    private String exerciseString = NULL_VALUE;
    private Exercise currentExercise;

    public static final HashSet<String> HIDDEN_PREPROCESSORS = new HashSet<>();
    private static HashMap<RepresentableDefault, HashMap<String, String>> alltimePreprocessors = new HashMap<>();

    private HashSet<Field> ignoreFields = new HashSet<>();
    private static boolean ignorePreprocessorsAndAnimateOnce = false;

    private Integer numOfDynamicMethods = null;
    private String processedScript;
    private String animate = THIS_NAME;

    private static int count = 0;

    private static HashMap<String, String> knownPreprocessors = new HashMap<>();

    private String preamble = null;
    private String rawScript;
    
    public static final LinkedList<String> IGNORE_BEG_TAGS_IN_DECL = new LinkedList<>();
    public static final LinkedList<String> IGNORE_END_TAGS_IN_DECL = new LinkedList<>();
    
    public static final String INSCRIPT_STANDARD_PARAMETER_PATTERN = "#n#";
    public static final String NAME_OF_NEW_COMMAND_METHOD = "newMethod";               // Note that after changing this...
    public static final String NAME_OF_NEW_COMMAND_WITH_PATTERN_METHOD = "newMethodD"; // ...
    public static final String NAME_OF_EXECUTE_METHOD = "executeCommand";              // ...the actual method name has to be refactored, too.

    static {
        IGNORE_BEG_TAGS_IN_DECL.add(INSCR_BEG_TAG);
        IGNORE_BEG_TAGS_IN_DECL.add(INSCR_BEG_TAG_FOR_INTERNAL_USAGE);
        IGNORE_END_TAGS_IN_DECL.add(INSCR_END_TAG);
        IGNORE_END_TAGS_IN_DECL.add(INSCR_END_TAG_FOR_INTERNAL_USAGE);
    }

    /**
     * The parent in the object-related tree this script is part of.
     */
    private RepresentableAsPDF superRep;

    @Override
    public RepresentableAsPDF getSuper() {
        return superRep;
    }
    
    /**
     * Ignores the preprocessor and animate variables once - i.e., the
     * variable is reset to false automatically.
     */
    static void ignorePreprocessorsAndAnimateOnce() {
        RepresentableDefault.ignorePreprocessorsAndAnimateOnce = true;
    }

    public RepresentableDefault(Exercise exercise) {
        GlobalVariables.getParameters().setLoggingLevel(5);

        if (exercise == null) {
            exerciseString = NULL_VALUE;
            currentExercise = null;
        } else {
            currentExercise = exercise;
            exerciseString = exercise.getRawExerciseString();
        }
    }

    public abstract String createScriptFromInstance();

    /**
     * These fields will not be used as a script variable in the declaration 
     * area, even if of allowed type (String, int, boolean etc.).
     * 
     * @param fieldToIgnore  The name of the field to ignore.
     */
    public void addIgnoredField(String fieldToIgnore) {
        try {
            this.ignoreFields.add(this.getClass().getDeclaredField(fieldToIgnore));
        } catch (NoSuchFieldException | SecurityException e) {
            GlobalVariables.getParameters().logDebug(
                    "Field '" + fieldToIgnore + "' not ignored in '"
                            + this.getClass().getSimpleName() + "': " + e);
        }
    }
    
    /**
     * @return  The plain script without preprocessors and 
     *          declarations and preamble.
     */
    public String getScriptWithoutPrepAndDeclAndPreamble() {
        return remDecl(this.processedScript);
    }

    /**
     * @return  The current plain script without preprocessors and 
     *          declarations, but including the preamble.
     */
    public String getScriptWithoutPrepAndDecl() {
        return preamble + getScriptWithoutPrepAndDeclAndPreamble().trim();
    }
    
    /**
     * DON'T USE THIS METHOD! In almost every case it's better to use
     * {@link #getScriptWithoutPrepAndDecl()} which outputs the same (if you
     * have a regular RepresentableDefault object and you initialized it
     * correctly), only by assuring that everything is correct with your 
     * preprocessors.</BR>
     * </BR>
     * Cuts out the declaration part of the code. Note that declarations
     * may NOT occur more than once in a script.
     * </BR>
     * This method is for internal use and very special circumstances only.
     * 
     * @param code  The script to cut declarations out from.
     * 
     * @return  The script without declarations.
     */
    public String remDecl(String codeRaw) {
        String code = codeRaw;
        
        if (code == null) {
            return "";
        }
        
        code = this.inferPlaceholdersForPlainText(code);
        
        try {
            String codeWithoutDec = ScriptConversionMethods.removeTaggedPartsOnTopLevel(
                    code, 
                    DECL_BEG_TAG, 
                    DECL_END_TAG,
                    IGNORE_BEG_TAGS_IN_DECL,
                    IGNORE_END_TAGS_IN_DECL);
            return this.undoPlaceholdersForPlainText(codeWithoutDec);
        } catch (Exception e) {
            return codeRaw;
        }
    }
    
    /**
     * Retrieves the declarations from a script, meaning the actual 
     * text block (excluding the tags), by keeping everything as is including 
     * white spaces. Allows for recursive occurrences of begin and end tag
     * within the part to extract. I.e., returns</BR>
     * ((a + b) + c)</BR>
     * And not just</BR>
     * ((a + b)</BR>
     * if ( and ) are the tags. The top-level declarations part does not have 
     * to be unique in the string, i.e., several declarations blocks can be 
     * present, and their combined content will be returned.
     * 
     * @param script      The script to cut declarations out from.
     * @param beginTag    The tag indicating the beginning of the declarations part.
     * @param endTag      The tag indicating the ending of the declarations part.
     * 
     * @return The declarations, if any, empty string otherwise.
     */
    private static String getDeclarations(
            String script, 
            String beginTag, 
            String endTag) {
        try {
            if (!script.contains(beginTag) || !script.contains(endTag)) {
                return "";
            }

            if (beginTag.equals(endTag)) {
                throw new RuntimeException(
                        "It doesn't make sense to use level-wise "
                        + "matching when begin and end tag are equal.");
            }

            LinkedList<String> declList = MiscMath.extractSubstringsLevelwise(
                    script, beginTag, endTag, IGNORE_BEG_TAGS_IN_DECL, IGNORE_END_TAGS_IN_DECL, 0);
            
            String declarations = "";
            
            for (String s : declList) {
                declarations += s;
            }
            
            return declarations;
        } catch (Exception e) {
            GlobalVariables.getParameters().logError(
                    "Exception occurred while I tried to extract "
                    + "declarations from the following script:\n"
                    + script);
            e.printStackTrace();
            throw new RuntimeException("Exception occurred while extracting "
                    + "declarations from script");
        }
    }
    
    /**
     * Removes all preprocessors, hidden preprocessors, stored method part 
     * begin positions and all the "known stuff" from 
     * {@link ScriptConversionMethods}, and requests preprocessor recalculation.
     */
    public static void removeKnownStuff() {
        for (RepresentableAsPDF r : alltimePreprocessors.keySet()) {
            alltimePreprocessors.get(r).clear();
            HIDDEN_PREPROCESSORS.clear();
        }
        
        knownPreprocessors.clear();
//        dynamicExpansion = true;
        createScriptTree = true;
        inscriptMethodParPatterns.clear();
        inscriptMethodDefinitions.clear();
        inscriptMethodParNums.clear();
        generatedPreprocessors.clear();
        methodPartBegins.clear();
        alltimePreprocessors.clear();
        VARIABLES_MAYBE.clear();
        ScriptConversionMethods.removeKnownStuff();
        recalculatePreprocessors = true;
        GeneralDialog.resetLongTimeOperationID(VARIABLE_FINDER_LONGTIME_ID);
    }

    /**
     * Adds a preprocessor to this representable.
     * 
     * @param preprocessorScript  The preprocessor code containing a filename 
     *                            to store the PDF in and the actual 
     *                            preprocessor code.
     * @param hidden        Iff the preprocessor is hidden. For Latex, e.g.,
     *                      preprocessors are used "inscript", hidden from
     *                      the user.
     */
    private void addPreprocessor(String preprocessorScript, boolean hidden) {
        try {
            int indexOf = preprocessorScript.indexOf(VARIABLE_DELIMITER);
            String datnam = StaticMethods.removeWhitespaces(preprocessorScript.substring(0, indexOf));
            String plainPreprocessor = preprocessorScript.substring(indexOf + 1).trim();
            addPreprocessor(plainPreprocessor, datnam, hidden, MiscMath.indexOfOnTopLevel(plainPreprocessor, METHOD_CHAIN_SEPARATOR, 0, INSCR_BEG_TAG, INSCR_END_TAG));
        } catch (Exception e) {
            throw new RuntimeException("Preprocessor malformatted:\n" + preprocessorScript);
        }
    }
    
    private static long allOfIt;
    private static long allOfAllOfIt;
    private static boolean recalculatePreprocessors = true;
    
    /**
     * Adds a preprocessor to this representable. Note that the preprocessor
     * code has to be well-formatted - as opposed to the other method with
     * the same name which first cleans up the preprocessor code.
     * 
     * @param preprocessor  Plain and cleaned preprocessor code.
     * @param filename      The filename to store the preprocessor in.
     * @param hidden        Iff the preprocessor is hidden. For Latex, e.g.,
     *                      preprocessors are used "inscript", hidden from
     *                      the user.
     */
    private void addPreprocessor(String preprocessor, String filename, boolean hidden, Integer indexOfMethodsPartBegin) {
        if (!alltimePreprocessors.containsKey(this)) {
            alltimePreprocessors.put(this, new HashMap<>());
        }

        HashMap<String, String> mymap = alltimePreprocessors.get(this);
        mymap.put(filename, preprocessor.trim());
        if (indexOfMethodsPartBegin != null && indexOfMethodsPartBegin != preprocessor.length() && indexOfMethodsPartBegin >= 0) {
            methodPartBegins.put(preprocessor, indexOfMethodsPartBegin);
        }
        recalculatePreprocessors = true;
        
        if (hidden) {
            HIDDEN_PREPROCESSORS.add(filename);
        }
    }
    
    private static HashMap<String, Integer> methodPartBegins = new HashMap<>();
    
    static HashMap<String, Integer> getMethodPartBegins() {
        return methodPartBegins;
    }
    
    /**
     * Replaces all variable names with the actual code to process.
     * For example, if x1=this.min, y1=x1.sim becomes y1=this.min.sim.</BR>
     * </BR>
     * Note: Pretty sure, this method can be done more efficiently. However, 
     * it's really micro-management - better focus on other efficiency issues.
     */
    private static void processPreprocessors() {
        int count = 0;
        allOfIt = 0;
        
        while (recalculatePreprocessors && !alltimePreprocessors.isEmpty()) { // Do it as often as necessary.
            long time = System.currentTimeMillis();
            recalculatePreprocessors = false;
            
            for (RepresentableAsPDF rep : alltimePreprocessors.keySet()) {
                for (String s : alltimePreprocessors.get(rep).keySet()) {
                    processPreprocessor(s, rep);
                }
            }
            
            long thatsIt = System.currentTimeMillis() - time;
            allOfIt += thatsIt;
            count++;
        }

        allOfAllOfIt += allOfIt;
        
        if (count > 0) {
            GlobalVariables.getParameters().logDebug("Processing time of " + numOfPreprocessors() + " preprocessors: " + allOfIt + "ms. (All-time processing time: " + allOfAllOfIt + "ms.)");
        }
    }
    
    private static int numOfPreprocessors() {
        int c = 0;
        for (HashMap<String, String> h : alltimePreprocessors.values()) {
            c += h.size();
        }
        return c;
    }
    
    /**
     * Replaces a variable name, if any, with the correct script at the 
     * beginning of <code>chain</code>.
     *  
     * @param chain  The chain to process.
     * 
     * @return  The processed chain - can be equal to received chain.
     */
    static String processChain(String chain) {
        processPreprocessors();
        HashMap<String, String> all = new HashMap<>();
        alltimePreprocessors.values().forEach(c -> all.putAll(c));
        
        System.out.println(chain + "     " + all);
        for (String k1 : all.keySet()) {
            String k = getUnqualifiedName(k1);
            String varVal = all.get(getQualifiedIdentifierName(k));
            
            Integer mbeg = methodPartBegins.get(varVal);
            if (mbeg == null) {
                mbeg = varVal.length();
            }
            
            String first = replaceIdentAtBeginningOfChain(chain, k, varVal, mbeg);
            if (first != null) {
                return first;
            }
            
            if (!k1.equals(k)) {
                String second = replaceIdentAtBeginningOfChain(chain, k1, varVal, mbeg);
                if (second != null) {
                    return second;
                }
            }
        }
        
        return chain;
    }

    private static String replaceIdentAtBeginningOfChain(String chain, String k, String varVal, Integer mbeg) {
        if (chain.startsWith(k + ".") || chain.equals(k)) {
            String newChain = varVal + chain.substring(k.length());
            methodPartBegins.put(newChain, mbeg);
            return newChain;
        } else if (chain.startsWith(INSCR_BEG_TAG + k + INSCR_END_TAG)) {
            String newChain = varVal + chain.substring(
                    INSCR_BEG_TAG.length() 
                    + k.length()
                    + INSCR_END_TAG.length());
            methodPartBegins.put(newChain, mbeg);
            return newChain; 
        } else {
            return null;
        }
    }
    
    /**
     * Replaces a possible variable name at the beginning with the actual 
     * preprocessor code.
     */
    private static void processPreprocessor(String rawName, RepresentableAsPDF rep) {
        String name = rawName;
        HashMap<String, String> mapFromAlltimes = alltimePreprocessors.get(rep);
        String code = mapFromAlltimes.get(name);
        int nextPoint1 = MiscMath.indexOfOnTopLevel(code, ".", 0, START_TAG_FOR_NESTED_VARIABLES, END_TAG_FOR_NESTED_VARIABLES);
        int nextPoint2 = MiscMath.indexOfOnTopLevel(code, ".", 0, INSCR_BEG_TAG, INSCR_END_TAG);
        
        if (nextPoint1 < 0 || nextPoint1 != nextPoint2) {
            nextPoint1 = code.length();
        }
        
        String objectName = code.substring(0, nextPoint1).replace(INSCR_BEG_TAG, "").replace(INSCR_END_TAG, "");
        String rest = code.substring(nextPoint1);
        
        for (RepresentableAsPDF r : alltimePreprocessors.keySet()) {
            HashMap<String, String> currPreprocessors = alltimePreprocessors.get(r);
            for (String var : currPreprocessors.keySet()) {
                if (var.equals(objectName)) {
                    String original = currPreprocessors.get(var);
                    Integer mBeg = methodPartBegins.get(original);
                    String newChain = original + rest;
                    
                    GlobalVariables.getParameters().logDebug("Preprocessors -- changing from '" + mapFromAlltimes.get(name) + "' to '" + newChain + "'."); 
                    mapFromAlltimes.put(name, newChain);
                    methodPartBegins.put(newChain, mBeg);

                    recalculatePreprocessors = true;
                    return;
                }
            }
        }
    }
    
    private void setViaReflections(String fieldName, String value) {
        String val = value;
        
        // "e" and "exerciseString" are reserved fields that cannot be used in child classes.
        if (fieldName.equals(EXERCISE_FIELD_LONG_NAME) 
                || fieldName.equals(EXERCISE_FIELD_SHORT_NAME)) {
            this.setExercise(new Exercise(value));
            this.exerciseString = this.getExercise().getRawExerciseString();
            return;
        }

        // Preprocessors.
        if (!ignorePreprocessorsAndAnimateOnce ) {
            if (fieldName.startsWith(PREPROCESSOR_FIELD_NAME)) {
                if (!val.startsWith(START_TAG_FOR_NESTED_VARIABLES) 
                        || !val.endsWith(END_TAG_FOR_NESTED_VARIABLES)) {
                    val = START_TAG_FOR_NESTED_VARIABLES + val + END_TAG_FOR_NESTED_VARIABLES;
                }
                
                String preprocessor = MiscMath.extractFirstSubstringLevelwise(
                            val, 
                            START_TAG_FOR_NESTED_VARIABLES, 
                            END_TAG_FOR_NESTED_VARIABLES, 
                            0);
                
                this.addPreprocessor(preprocessor, false);
            }
            
            if (fieldName.equals(ANIMATE_FIELD_NAME)) {
                animate = value;
            }
        }
        
        try {
            Field field = this.getClass().getDeclaredField(fieldName);
            
            if (this.ignoreFields.contains(field)) {
                return;
            }
            
            field.setAccessible(true);
            
            if (field.getType().equals(Double.class) 
                    || field.getType().equals(Double.TYPE)) {
                field.set(this, Double.parseDouble(value));
            } else if (field.getType().equals(Boolean.class) 
                    || field.getType().equals(Boolean.TYPE)) {
                field.set(this, Boolean.parseBoolean(value));
            } else if (field.getType().equals(Integer.class) 
                    || field.getType().equals(Integer.TYPE)) {
                field.set(this, Integer.parseInt(value));
            } else if (field.getType().equals(String.class)) {
                field.set(this, value);
            }
        } catch (NoSuchFieldException 
                | SecurityException 
                | IllegalArgumentException 
                | IllegalAccessException e) {
        }
    }

    /**
     * Set beginTag or endTag to null if the whole code consists of
     * name-value pairs.
     * 
     * @return  Declaration field-value pairs in individual strings such as
     *          "foo", "bar".
     */
    public static LinkedList<ArrayList<String>> extractNVPairs(
            String codeRaw,
            char beginLiteral,
            char endLiteral,
            char endValue,
            String beginTag,
            String endTag) {
        ArrayList<String> nameValue = new ArrayList<>(2);
        StringBuffer name = new StringBuffer();
        StringBuffer value = new StringBuffer();
        final int nameMode = 0;
        final int valueRegularMode = 1;
        final int valueLiteralMode = 2;
        
        LinkedList<ArrayList<String>> variables = new LinkedList<>();
        
        String declarations = codeRaw;
        if (beginTag != null && endTag != null) {
            declarations = getDeclarations(
                    codeRaw, 
                    beginTag, 
                    endTag);
        }
        
        if (declarations == null) {
            declarations = "";
        }
        
        int mode = nameMode;
        
        for (int i = 0; i < declarations.length(); i++) {
            if (mode == nameMode) {
                if (declarations.charAt(i) == ASSIGNMENT_OPERATOR) {
                    nameValue.add(StaticMethods.removeWhitespaces(name.toString()));

                    if (declarations.indexOf(START_TAG_FOR_NESTED_VARIABLES, i + 1) 
                            == i + 1) {
                        String varValue = MiscMath.extractFirstSubstringLevelwise(
                                        declarations, 
                                        START_TAG_FOR_NESTED_VARIABLES, 
                                        END_TAG_FOR_NESTED_VARIABLES, 
                                        i + 1);
                        
                        nameValue.add(varValue);
                        variables.add(nameValue);
                        nameValue = new ArrayList<>(2);
                        name = new StringBuffer();
                        value = new StringBuffer();
                        mode = nameMode;
                        
                        i += START_TAG_FOR_NESTED_VARIABLES.length()
                                + END_TAG_FOR_NESTED_VARIABLES.length()
                                + varValue.length()
                                + 1; // This is for the semicolon (or whatever) at the end.
                    } else {
                        mode = valueRegularMode;
                    }
                } else {
                    name.append(declarations.charAt(i));
                }
            } else if (mode == valueRegularMode) {
                if (declarations.charAt(i) == beginLiteral) {
                    mode = valueLiteralMode;
                } else if (declarations.charAt(i) == endValue) {
                    nameValue.add(value.toString());
                    variables.add(nameValue);
                    nameValue = new ArrayList<>(2);
                    name = new StringBuffer();
                    value = new StringBuffer();
                    mode = nameMode;
                } else if (!isWhiteSpace(declarations.charAt(i))) {
                    value.append(declarations.charAt(i));
                }
            } else if (mode == valueLiteralMode) {
                if (declarations.charAt(i) == endLiteral) {
                    mode = valueRegularMode;
                } else {
                    value.append(declarations.charAt(i));
                }
            }
        }
        
        if (!name.toString().trim().isEmpty() || !value.toString().trim().isEmpty()) {
            if (nameValue.isEmpty()) {
                nameValue.add(name.toString());
            }
            nameValue.add(value.toString());
            variables.add(nameValue);
        }
        
        return variables;
    }
    
    public static String getExerciseString(String codeRaw) {
        LinkedList<ArrayList<String>> vars = extractNVPairs(
                ScriptConversionMethods.decryptScript(codeRaw), 
                BEGIN_LITERAL, 
                END_LITERAL, 
                END_VALUE, 
                DECL_BEG_TAG, 
                DECL_END_TAG);
        
        for (ArrayList<String> entry : vars) {
            if (entry.get(0).equals(EXERCISE_FIELD_SHORT_NAME) || entry.get(0).equals(EXERCISE_FIELD_LONG_NAME)) {
                return entry.get(1);
            }
        }
        
        return "";
    }
    
    private static boolean isWhiteSpace(char character) {
        return character == ' ' || character == '\n' || character == '\t' || character == '\r';
    }
    
//    private static HashMap<String, RepresentableDefault> knownScripts = new HashMap<>();
//    private static int ccccount = 0;

    private void setDeclaredFields() {
        String withoutPreprocessors = removePreprocessors(this.processedScript);
        
        LinkedList<ArrayList<String>> fieldSetter = extractNVPairs(
                withoutPreprocessors,
                BEGIN_LITERAL,
                END_LITERAL,
                END_VALUE,
                DECL_BEG_TAG,
                DECL_END_TAG);
        
        for (ArrayList<String> nameValue : fieldSetter) {
            try {
                String name = nameValue.get(0);
                String value = nameValue.get(1);
                this.setViaReflections(name, value);
            } catch (Exception e) {
            }
        }
    }

    private boolean isSettable(Field f) {
        if (this.ignoreFields.contains(f)) {
            return false;
        }
        
        if (java.lang.reflect.Modifier.isStatic(f.getModifiers())
                || ((f.getModifiers() & java.lang.reflect.Modifier.FINAL) == java.lang.reflect.Modifier.FINAL)) {
            return false;
        }
        
        return f.getType().equals(Double.class) || f.getType().equals(Double.TYPE)
                || f.getType().equals(Integer.class) || f.getType().equals(Integer.TYPE)
                || f.getType().equals(String.class)
                || f.getType().equals(Boolean.class) || f.getType().equals(Boolean.TYPE);
    }
    
    private ArrayList<Field> getAllSettableFields() {
        ArrayList<Field> list = new ArrayList<>();
        
        for (Field f : this.getClass().getDeclaredFields()) {
            if (isSettable(f)) {
                list.add(f);
            }
        }
        
        return list;
    }
    
    public String generateCompleteDeclarationsBlock() {
        return generateCompleteDeclarationsBlock(true, true, true);
    }

    /**
     * Generates a text block containing the declarations begin and end tags 
     * as well as all available script variables with their current values
     * in between.
     * 
     * @param includeExercise Iff the exercise variable should be included.
     * @param includeAnimate Iff the animate variable should be included.
     * @param includePreprocessors Iff the preprocessor variables should be included.
     * 
     * @return  The declarations text block with the current variable values.
     */
    private String generateCompleteDeclarationsBlock(
            boolean includeExercise,
            boolean includeAnimate,
            boolean includePreprocessors) {
        String s = "";
        
        if (this.getFather() != null) {
            String scriptText = remDecl(this.getFather().getScriptArea().getText());
            if (scriptText.length() > 0 && scriptText.charAt(scriptText.length() - 1) != '\n') {
                s += "\n";
            }
        }
        
        s += DECL_BEG_TAG + "\n";
        ArrayList<Field> fields = getAllSettableFields();
        
        s += includeExercise ? getExerciseStringForDeclarations() : "";
        s += includePreprocessors ? getPreprocessorStringForDeclarations(false, false) : "";
        s += includeAnimate ? getAnimateStringForDeclarations() : "";
        
        if (fields.size() > 0) {
            s += "\n";
//            s += PDFGeneratorWindow.BEGIN_COMMENT + " No declarations available (except default e. Add class fields to create declarations. " + PDFGeneratorWindow.END_COMMENT;
        }
        
        for (int i = 0; i < fields.size() - 1; i++) {
            try {
                Field f = fields.get(i);
                f.setAccessible(true);
                s += f.getName() + VARIABLE_DELIMITER + f.get(this) + ";\n";
            } catch (IllegalArgumentException | IllegalAccessException e) {}
        }

        try {
            Field f = fields.get(fields.size() - 1);
            f.setAccessible(true);
            s += f.getName() + VARIABLE_DELIMITER + f.get(this);
        } catch (Exception e) {}
        

        s += "\n" + DECL_END_TAG;
        return s;
    }

    public String getAnimateStringForDeclarations() {
        return "\n" + ANIMATE_FIELD_NAME + VARIABLE_DELIMITER + animate + ";";
    }

    public String getExerciseStringForDeclarations() {
        return EXERCISE_FIELD_SHORT_NAME 
                + VARIABLE_DELIMITER 
                + BEGIN_LITERAL 
                + (this.getExercise() != null && this.getExercise().isExEncrypted() 
                    ? ScriptConversionMethods.encryptScript(this.exerciseString) 
                    : this.exerciseString) 
                + END_LITERAL 
                + ";";
    }
    
    public String getPreprocessorStringForDeclarations(boolean includeHidden, boolean includeAll) {
        HashMap<String, String> thePreprocessors;
        if (includeAll) {
            thePreprocessors = new HashMap<>();
            alltimePreprocessors.keySet().forEach(p -> thePreprocessors.putAll(alltimePreprocessors.get(p)));
        } else {
            thePreprocessors = getPreprocessors();
        }
        
        if (thePreprocessors == null || thePreprocessors.isEmpty()) {
            return "";
        }
        
        String s = "";
        int num = 1;
        
        ArrayList<String> keySet = new ArrayList<>(thePreprocessors.keySet());
        Collections.sort(keySet);
        
        for (String datnam : keySet) {
            if (includeHidden || includeAll || !isPreprocessorHidden(datnam)) {
                String thePreprocessor = thePreprocessors.get(datnam);
                String oneEntry = "\n" + PREPROCESSOR_FIELD_NAME + num
                        + VARIABLE_DELIMITER 
                        + START_TAG_FOR_NESTED_VARIABLES
                        + datnam + VARIABLE_DELIMITER + thePreprocessor.replace("\n", "\\n").replace("\n", "\\n")
                        + END_TAG_FOR_NESTED_VARIABLES
                        + "" + BEGIN_COMMENT
                        + methodPartBegins.get(thePreprocessor)
                        + END_COMMENT
                        + ";";
                
                s += oneEntry;
                num++;
            }
        }
        
        return s;
    }

    public String getUpperClassDeclarationsBlockOnly() {
        return "\n" + DECL_BEG_TAG 
                + "\n" + getExerciseStringForDeclarations()
                + getPreprocessorStringForDeclarations(false, false)
                + getAnimateStringForDeclarations()
                + DECL_END_TAG;
    }

    public VFPWindow getFather() {
        return VFPWindow.getSINGLETON_INSTANCE();
    }
    
    private boolean allowCollapsingAndDecollapsing = true;
    
    public boolean isAllowCollapsingAndDecollapsing() {
        return this.allowCollapsingAndDecollapsing;
    }

    public void setAllowCollapsingRules(boolean allow) {
        this.allowCollapsingAndDecollapsing = allow;
    }
    
    private static String regExToFilterDynMethods = ".*";
    
    @SuppressWarnings("unused")
    private static void setRegExToFilterDynMethods(
            String regExToFilterDynMethods) {
        RepresentableDefault.regExToFilterDynMethods = regExToFilterDynMethods;
    }
    
    private static String getRegExToFilterDynMethods() {
        return regExToFilterDynMethods;
    }
    
    /**
     * Main method for retrieving conversion buttons of all dynamic methods
     * in desktop mode.
     * 
     * @return The JComponent containing all buttons representing dynamic methods.
     */
    @Override
    public JComponent getAdditionalInfo() {
        JPanel panel = new JPanel(new MigLayout("wrap 1"));

        // Dynamic methods.
        HashMap<String, MethodWrapper> methods = this.getFilteredDynamicMethods(
                RepresentableDefault.getRegExToFilterDynMethods(),
                ".*",
                ".*");

        WebLink.checkMethodsForAvailability(methods, VFPWindow.getSINGLETON_INSTANCE().getAvailableRepTypes());
        
        JPanel panelIntern = new JPanel(new MigLayout("insets 0, wrap 3"));
        
        List<String> methodNames = WebLink.sortMethods(methods);
        
        for (String s : methodNames) {
            MethodWrapper mw = methods.get(s);
            
            if (mw.isMethodButtonVisible()) {
                boolean addScriptPart = mw.isAddMethodReturnValueToOldScript();
                boolean returnValueIsScript = mw.isReturnValueScript();
                String preamble = "";
                String postamble = "";
                if (returnValueIsScript) {
                    preamble = "=> ";
                } else {
                    postamble = "...";
                }
                
                FancyScriptConverterJButton flexButt = new FancyScriptConverterJButton(
                        preamble + s + postamble, 
                        () -> this.invokeScriptConverterMethod(mw, returnValueIsScript), 
                        null,
                        VFPWindow.getSINGLETON_INSTANCE(),
                        addScriptPart,
                        returnValueIsScript);
                
                flexButt.setToolTipText(mw.getTooltip());
                if (!mw.isMethodButtonEnabled()) {
                    flexButt.setEnabled(false);
                }

                if (mw.getBckgnd() != null) {
                    flexButt.setBackground(mw.getBckgnd());
                }
                
                panelIntern.add(flexButt);
            }
        }
        panel.add(panelIntern);
        
        return panel;
    }

    private GraphViz agv = new GraphViz(null, null);

    private String invokeScriptConverterMethod(MethodWrapper mw, boolean returnValueIsScript) {
        String s = "Unknown error occurred during invokation of script converter method (this should never happen: something went really wrong)!";
        Object[] args = ScriptConversionMethods.getParametersFor(mw, null);
        if (args == null) {
            return null;
        }

        try {
            s = mw.invoke(args);
        } catch (LongOperationException e) {
            s = agv.getCodePrefix() + "\n" + agv.safetyCodeInCaseOfLargeCodeOrLongOperation(0);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
//            s = agv.getCodePrefix() + "\n" + agv.safetyCodeForExceptionCase(e.getMessage());
        }
        
        if (returnValueIsScript) {
            return s;
        } else {
            return MethodWrapper.removePreambleFrom(s);
        }
        
    }
    
    /**
     * Extracts the rule part from a script that contains comments or different
     * parts. The script is divided in a preamble, a postamble and the middle 
     * part which contains only rules.
     * 
     * @param script  A script including rules such as A => B | C;
     * @return  S[0]: preamble, S[1]: rule part, S[2]: postamble.
     */
    private String[] extractRules(String scriptRaw) {
        String script = ScriptConversionMethods.removeComments(scriptRaw);
        
        if (!this.allowCollapsingAndDecollapsing || !script.contains(";") || !script.contains("=>")) {
            return new String[] {script, "", ""};
        }
        
        script = script.replaceFirst(":", ":\n");
        
        String[] splitString = new String[3];
        String preamble = "";
        String postamble = "";
        String[] rules = script.split(";");

        try {
            String[] rule0Split = rules[0].split("\n");
            rules[0] = rule0Split[rule0Split.length - 1];
            for (int i = 0; i < rule0Split.length - 1; i++) {
                preamble += rule0Split[i] + "\n";
            }
        } catch (Exception e) {}
        
        if (!script.endsWith(";")) {
            postamble = rules[rules.length - 1];
            rules[rules.length - 1] = "";
        }
        
        splitString[0] = preamble;
        splitString[1] = "";
        splitString[2] = postamble;
        
        for (String r : rules) {
            splitString[1] += r.replace(" ", "").replace("\n", "").replace("\r", "").replace("\t", "") + ";";
        }
        
        return splitString;
    }
    
    /**
     * A | B => C;</BR>
     * </BR>
     * Becomes:</BR>
     * </BR>
     * A => C;</BR>
     * B => C;</BR>
     * </BR>
     * Note that newlines are ignored except for the beginning where the
     * first rule is assumed to begin on a new line. In the end, everything
     * after the last ";" is ignored.
     * 
     * @param scriptRaw2  An arbitrary script.
     * @return  The decollapsed script.
     */
    private String decollapseRulesLeft(String scriptRaw2) {
        try {
            String scriptRaw = remDecl(scriptRaw2);
            String[] splitScript = this.extractRules(scriptRaw);
            
            String preamble = splitScript[0];
            String script = splitScript[1];
            String postamble = splitScript[2];
            
            String newScript = "";
            String[] rules = script.split(";");
            
            for (String r : rules) {
                try {
                    for (String lhs : r.split("=>")[0].split("\\|")) {
                        String rhs = r.split("=>")[1];
                        String lhsProc = lhs;
                        String rhsProc = rhs;
                        
                        if (lhsProc.trim().length() == 0) {
                            lhsProc = "+"; // This is just in case + has been sent over URL and got killed.
                        }
                        if (rhsProc.trim().length() == 0) {
                            rhsProc = "+"; // This is just in case + has been sent over URL and got killed.
                        }

                        newScript += lhsProc.trim() + " => " + rhsProc.trim() + ";\n";
                    }
                } catch (Exception e) {
                    newScript += r + ";\n";
                }
            }

            newScript = newScript.replace(",", ", ").replace("  ", "");

            return (preamble + newScript + postamble).replace("\n\n\n", "\n\n").replace("  ", " ").trim().replace("|", " | ");
        } catch (Exception e) {
            return scriptRaw2;
        }
    }

    /**
     * A => B | C;</BR>
     * </BR>
     * Becomes:</BR>
     * </BR>
     * A => B;</BR>
     * A => C;</BR>
     * </BR>
     * Note that newlines are ignored except for the beginning where the
     * first rule is assumed to begin on a new line. In the end, everything
     * after the last ";" is ignored.
     * 
     * @param scriptRaw2  An arbitrary script.
     * @return  The decollapsed script.
     */
    private String decollapseRulesRight(String scriptRaw2) {
        try {
            String scriptRaw = remDecl(scriptRaw2);
            String[] splitScript = this.extractRules(scriptRaw);
            
            String preamble = splitScript[0];
            String script = splitScript[1];
            String postamble = splitScript[2];
            
            String newScript = "";
            String[] rules = script.split(";");
            
            for (String r : rules) {
                try {
                    for (String rhs : r.split("=>")[1].split("\\|")) {
                        String lhs = r.split("=>")[0];
                        String lhsProc = lhs;
                        String rhsProc = rhs;
                        if (lhsProc.trim().length() == 0) {
                            lhsProc = "+"; // This is just in case + has been sent over URL and got killed.
                        }
                        if (rhsProc.trim().length() == 0) {
                            rhsProc = "+"; // This is just in case + has been sent over URL and got killed.
                        }
                        
                        newScript += lhsProc.trim() + " => " + rhsProc.trim() + ";\n";
                    }
                } catch (Exception e) {
                    newScript += r + ";\n";
                }
            }

            newScript = newScript.replace(",", ", ").replace("  ", "");

            return (preamble + newScript + postamble).replace("\n\n\n", "\n\n").replace("  ", " ").trim().replace("|", " | ");
        } catch (Exception e) {
            return scriptRaw2;
        }
    }

    /**
     * A => B;</BR>
     * A => C;</BR>
     * </BR>
     * Becomes:</BR>
     * </BR>
     * A => B | C;</BR>
     * </BR>
     * Note that newlines are ignored except for the beginning where the
     * first rule is assumed to begin on a new line. In the end, everything
     * after the last ";" is ignored.
     * 
     * @param scriptRaw2  An arbitrary script.
     * @return  The collapsed script.
     */
    private String collapseRulesRight(String scriptRaw2) {
        try {
            this.applyDeclarationsAndPreprocessors(scriptRaw2, null, 0);
            
            String scriptRaw = remDecl(scriptRaw2);
            String[] splitScript = this.extractRules(scriptRaw);
            
            String preamble = splitScript[0];
            String script = this.decollapseRulesRight(splitScript[1]).replace("\n", "").replace("\r", "").replace("\t", "");
            String postamble = splitScript[2];

            String[] rules = script.split(";");
            HashMap<String, LinkedList<String>> allRules = new HashMap<>();
            
            for (String r : rules) {
                String[] split = r.split("=>");
                split[0] = split[0].trim();
                split[1] = split[1].trim();
                
                if (allRules.get(split[0]) == null) {
                    allRules.put(split[0], new LinkedList<>());
                }

                allRules.get(split[0]).add(split[1]);
            }
            
            LinkedList<String> ruleList = new LinkedList<>();
            ArrayList<String> sortedKeyset = new ArrayList<>(allRules.keySet());
            Collections.sort(sortedKeyset, (c1, c2) -> 
            {
                Integer c1Comma = c1.split(",", -1).length - 1;
                Integer c2Comma = c2.split(",", -1).length - 1;
                
                if (c1Comma != c2Comma) {
                    return new Integer(c1Comma).compareTo(new Integer(c2Comma));
                } else {
                    return c1.compareTo(c2);
                }
            });
            
            for (String lhs : sortedKeyset) {
                String oneLine = "";
                
                oneLine += lhs + " => ";
                for (int i = 0; i < allRules.get(lhs).size() - 1; i++) {
                    String rhs = allRules.get(lhs).get(i);
                    oneLine += rhs + " | ";
                }
                for (int i = allRules.get(lhs).size() - 1; i < allRules.get(lhs).size(); i++) {
                    String rhs = allRules.get(lhs).get(i);
                    oneLine += rhs;
                }
                oneLine += ";";
                ruleList.add(oneLine);
            }

            String newScript = "";
            for (String s : ruleList) {
                newScript += s + "\n";
            }
            
            newScript = newScript.replace(",", ", ").replace("  ", " ").trim();

            return (preamble.trim() + "\n" + newScript.trim() + "\n" + postamble.trim() + "\n")
                    .replace("  ", " ").replace("\n\n", "\n");
        } catch (Exception e) {
            return scriptRaw2;
        }
    }

    /**
     * A => C;</BR>
     * B => C;</BR>
     * </BR>
     * Becomes:</BR>
     * </BR>
     * A | B => C;</BR>
     * </BR>
     * Note that newlines are ignored except for the beginning where the
     * first rule is assumed to begin on a new line. In the end, everything
     * after the last ";" is ignored.
     * 
     * @param scriptRaw2  An arbitrary script.
     * @return  The collapsed script.
     */
    private String collapseRulesLeft(String scriptRaw2) {
        try {
            this.applyDeclarationsAndPreprocessors(scriptRaw2, null, 0);
            
            String scriptRaw = remDecl(scriptRaw2);
            String[] splitScript = this.extractRules(scriptRaw);
            
            String preamble = splitScript[0];
            String script = this.decollapseRulesLeft(splitScript[1]).replace("\n", "").replace("\r", "").replace("\t", "");
            String postamble = splitScript[2];

            String[] rules = script.split(";");
            HashMap<String, LinkedList<String>> allRules = new HashMap<>();
            
            for (String r : rules) {
                String[] split = r.split("=>");
                split[0] = split[0].trim();
                split[1] = split[1].trim();

                if (allRules.get(split[1]) == null) {
                    allRules.put(split[1], new LinkedList<>());
                }

                allRules.get(split[1]).add(split[0]);
            }
            
            LinkedList<String> ruleList = new LinkedList<>();
            ArrayList<String> sortedKeyset = new ArrayList<>(allRules.keySet());
            Collections.sort(sortedKeyset, (c1, c2) -> 
             /*
               * Maybe this sorting procedure has to be adapted.
               * I don't remember why the according procedure in the 
               * "right" method is more complex.
               */
               allRules.get(c1).toString().compareTo(allRules.get(c2).toString())
            );
            
            for (String rhs : sortedKeyset) {
                String oneLine = "";
                
                for (int i = 0; i < allRules.get(rhs).size() - 1; i++) {
                    String lhs = allRules.get(rhs).get(i);
                    oneLine += lhs + "|";
                }
                for (int i = allRules.get(rhs).size() - 1; i < allRules.get(rhs).size(); i++) {
                    String lhs = allRules.get(rhs).get(i);
                    oneLine += lhs;
                }

                oneLine += " => " + rhs + ";";
                ruleList.add(oneLine);
            }

            String newScript = "";
            for (String s : ruleList) {
                newScript += s + "\n";
            }
            
            newScript = newScript.replace(",", ", ").replace("  ", " ").replace("|", " | ").trim();

            return (preamble.trim() + "\n" + newScript.trim() + "\n" + postamble.trim() + "\n")
                    .replace("  ", " ").replace("\n\n", "\n");
        } catch (Exception e) {
            return scriptRaw2;
        }
    }

    private static RepresentableDefault staticInstance = new RepresentableDefault(null) {
        private static final long serialVersionUID = -5077676148254130421L;
        @Override public boolean isAcceptableScript(String code) {return false;}
        @Override public String[] getExampleScripts() {return new String[] {};}
        @Override public PDFProcessor generatePDFscript(String pdfPath) {return null;}
        @Override public void createInstanceFromScript(String code, RepresentableAsPDF father) {}
        @Override public HashMap<String, MethodWrapper> getDynamicMethods() {return super.getDynamicMethods();}
        @Override public HashMap<String, String> getMetaProperties() {return new HashMap<>();}
        @Override public String getGermanName() {return "Dummy-Keine-Funktionalität";}
        @Override public Class<? extends PDFProcessor> getPDFProcessorClass() {return null;}
        @Override public String createScriptFromInstance() {return null;}
        @Override public HashMap<String, String> getMethodNameAbbreviations() {return null;}
    };
    
    public static RepresentableDefault getStaticInstance() {
        return staticInstance;
    }
        
    public static String decollapseStatic(String scriptRaw) {
      return staticInstance.decollapseRules(scriptRaw);
    }

    public static String collapseStatic(String scriptRaw) {
        return staticInstance.collapseRulesRtoL(scriptRaw);
    }
 
    private static Color formatButtonsColor = new Color(0xace6cc);
    
    /**
     * Stores the preprocessors already generated in this run. The set is
     * cleared 
     */
    private static final HashSet<String> generatedPreprocessors = new HashSet<>();
    
    /**
     * In general constructs a {@link PDFProcessor} which can be used to create 
     * the PDF file for the given object.</BR>
     * </BR>
     * Note, however, that at this level (the {@link RepresentableDefault} level), 
     * only preprocessors are applied, and no {@link PDFProcessor} is 
     * returned. More precisely, the method uses the {@link #scriptTree} to
     * create all the PDFs required for this object in the order suitable for
     * all the according sub-scripts.</BR>
     * </BR>
     * After the call of this method, the inherited method of the actual specific
     * {@link RepresentableDefault} object can be used to create the 
     * {@link PDFProcessor} and subsequently the final PDF file. By itself, the
     * method creates the single PDF files of the sub-scripts only.
     * 
     * @param pdfPath  The path where the preprocessed files are stored to.
     * 
     * @return  Always {@code null}.
     */
    @Override
    public PDFProcessor generatePDFscript(String pdfPath) {
        LinkedList<String> orderedPreprocessors = orderedPreprocessors();
        LinkedList<String> directPreprocessors = new LinkedList<>(getPreprocessors().keySet());

        for (String prep : directPreprocessors) {
            if (!orderedPreprocessors.contains(prep)) {
                orderedPreprocessors.add(prep); // Append direct preprocessors from this.
            }
        }
        
        for (String prepName : orderedPreprocessors) {
            if (prepName != null && !generatedPreprocessors.contains(prepName)) {
                String chain = getPreprocessorFromAlltimes(prepName);
                RepresentableAsPDF rep = ScriptConversionMethods.evaluateChain(
                        removePreprocessors(this.getRawScript()), this.undoPlaceholdersForPlainText(chain), null);
                
                if (rep == null) { // Maybe do nothing?
                    throw new RuntimeException("Chain\n'" + chain + "'\nevaluated to null during PDF processor generation.");
                }
                
                if (rep != null && !DummyRepresentable.class.equals(rep.getClass())) {
                    PDFProcessor pdfProcessor = rep.generatePDFscript(pdfPath);
                    pdfProcessor.storeAsPDF(WebLink.fileName(prepName), pdfPath);
                    generatedPreprocessors.add(prepName);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Uses the {@link #scriptTree} to determine the order in which the 
     * (non-plain-text) preprocessors have to be translated into PDF to
     * provide for each script the required PDFs defined by sub-scripts.
     * 
     * @return  The ordered preprocessor names to be used as keys of the
     *          {@link #alltimePreprocessors} map values.
     */
    private LinkedList<String> orderedPreprocessors() {
        LinkedList<String> preps = new LinkedList<>();
        ScriptTree root = this.scriptTree.findRoot();
        LinkedList<ScriptTree> orderedNodes = root.traverseDepthFirst();
        for (ScriptTree n : orderedNodes) {
            String name = n.getIdName();
            if (name != null) { // null is the root node that is handled separately.
                preps.add(name);
            }
        }
        return preps;
    }
    
    /**
     * Retrieves the preprocessor with the given name from the static list of 
     * all preprocessors.
     * 
     * @param name  The name of the preprocessor to retrieve.
     * 
     * @return  The preprocessor with this name.
     */
    private static String getPreprocessorFromAlltimes(String name) {
        for (HashMap<String, String> preps : alltimePreprocessors.values()) {
            String prep = preps.get(name);
            if (prep != null) {
                return prep;
            }
        }
        
        return null;
    }
    
//    /**
//     * Determines if the given script is terminal in the sense that it does
//     * not have to be interpreted by subsequent steps in the translation process.
//     * More precisely:
//     * <ul>
//     * <li>"this" is terminal.</li>
//     * <li>A plain-text script is terminal iff it doesn't change during translation 
//     * into a {@link DummyRepresentable} instance.</li>
//     * <li>Any regular script is NOT terminal.</li>
//     * </ul>
//     * 
//     * @param script1  The script to check.
//     * @return  Iff the script is "terminal".
//     */
//    public boolean isTerminal(String script1) {
//        if (RepresentableDefault.THIS_NAME.equals(script1)) {
//            return true;
//        }
//        
//        RepresentableAsPDF rep = ScriptConversionMethods.evaluateChain(
//                removePreprocessors(this.getRawScript()), script1, this);
//        RepresentableDefault repDef = null;
//        
//        if (DummyRepresentable.class.isAssignableFrom(rep.getClass())) {
//            repDef = (DummyRepresentable) rep;
//        } else {
//            return false;
//        }
//        
//        return script1.trim().equals(repDef.getRawScript().trim());
//    }
    
    private DoubleEvaluator evaluator = new DoubleEvaluator();
    
    /* **********************************
     * Here come the conversion methods.
     * **********************************/
    
    private String evalItAll(String n1Str, String n2Str, TwoNumEval evaluate) {
        BigInteger n1 = new BigInteger(n1Str);
        BigInteger n2 = new BigInteger(n2Str);
        return evaluate.eval(n1, n2);
    }

    @ConversionMethod public String smeq(String n1Str, String n2Str) {return evalItAll(n1Str, n2Str, (a, b) -> (a.compareTo(b) <= 0) + "");}
    @ConversionMethod public String sm(String n1Str, String n2Str) {return evalItAll(n1Str, n2Str,   (a, b) -> (a.compareTo(b) < 0)  + "");}
    @ConversionMethod public String greq(String n1Str, String n2Str) {return evalItAll(n1Str, n2Str, (a, b) -> (a.compareTo(b) >= 0) + "");}
    @ConversionMethod public String gr(String n1Str, String n2Str) {return evalItAll(n1Str, n2Str,   (a, b) -> (a.compareTo(b) > 0)  + "");}
    @ConversionMethod public String eq(String n1Str, String n2Str) {return evalItAll(n1Str, n2Str,   (a, b) -> (a.compareTo(b) == 0) + "");}
    @ConversionMethod public String neq(String n1Str, String n2Str) {return evalItAll(n1Str, n2Str,  (a, b) -> (a.compareTo(b) != 0) + "");}
    @ConversionMethod public String add(String num) {return evalItAll(this.getTagFreeRawScript(), num,  (a, b) -> a.add(b).toString());}
    @ConversionMethod public String sub(String num) {return evalItAll(this.getTagFreeRawScript(), num,  (a, b) -> a.add(b.negate()).toString());}
    @ConversionMethod public String mult(String num) {return evalItAll(this.getTagFreeRawScript(), num, (a, b) -> a.multiply(b).toString());}
    @ConversionMethod public String div(String num) {return evalItAll(this.getTagFreeRawScript(), num,  (a, b) -> a.divide(b).toString());}
    @ConversionMethod public String pow(String num) {return evalItAll(this.getTagFreeRawScript(), num,  (a, b) -> a.pow(b.intValue()).toString());}
    @ConversionMethod public String sqrt() {return evalItAll(this.getTagFreeRawScript(), "0", (a, b) -> BigIntMethods.sqrtCeil(a).toString());}
    @ConversionMethod public String mod(String num) {return evalItAll(this.getTagFreeRawScript(), num,  (a, b) -> a.mod(b).toString());}
    @ConversionMethod public String not() {return !this.getTagFreeRawScript().equals("true") + "";}
    @ConversionMethod public String nil() {return "";}
    @ConversionMethod public String id() {return getRawScript();}
    @ConversionMethod public String idd() {String s = getRawScript(); return s.startsWith(PLAIN_TEXT_BEGIN_TAG) ? s : PLAIN_TEXT_BEGIN_TAG + s + PLAIN_TEXT_END_TAG;}
    @ConversionMethod public String get(String varname) {return getFieldValue(varname, this.getClass());}
//    @ConversionMethod public String dynamic() {dynamicExpansion = true; return "";}
    @ConversionMethod public String notree() {createScriptTree = false; return "";}
    @ConversionMethod public String sethard(String value) {knownPreprocessors.put(getTagFreeRawScript(), value); return value;}
    
    /**
     * Retrieves the raw script, but without any <code>@{</code> or <code>@{</code>.
     * Can be used to get the constant <code>2</code> from raw script 
     * <code>@{2}@</code>. But use carefully, since it actually removes ALL
     * tags.
     * 
     * @return  The raw script without any inscript tags.
     */
    private String getTagFreeRawScript() {
        return StringUtils.replaceEach(this.getRawScript(), new String[] {INSCR_BEG_TAG, INSCR_END_TAG}, new String[] {"", ""});
    }
    
    /**
     * Returns the value of the respective field as plain text.
     * <P>Not sure if it's a good idea to have such a powerful method for
     * online usage. But on the other hand it's a "read-only" method.</P> 
     * 
     * @param varname  The name of the field to retrieve from here or from super class.
     * 
     * @return  The field value as string.
     */
    private String getFieldValue(String varname, Class<?> level) {
        if (varname.equals(EXERCISE_FIELD_LONG_NAME) || varname.equals(EXERCISE_FIELD_SHORT_NAME)) {
            Exercise exercise = this.getExercise();
            return "" + (exercise == null ? null : exercise.getRawExerciseString());
        } else if (varname.startsWith(PREPROCESSOR_FIELD_NAME)) {
            return PLAIN_TEXT_BEGIN_TAG +
                    this.getPreprocessorStringForDeclarations(varname.endsWith("h"), varname.endsWith("a"))
                    + PLAIN_TEXT_END_TAG;
        }
        
        try {
            Field f = level.getDeclaredField(varname);
            f.setAccessible(true);
            return f.get(this) + "";
        } catch (NoSuchFieldException e) {
            return getFieldValue(varname, level.getSuperclass());
        } catch (IllegalArgumentException | IllegalAccessException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Names of hidden plain-script methods which take zero string arguments.
     * They are automatically registered as dynamic methods.
     */
    private final static String[] ZERO_PARS_METHOD_NAMES = new String[] {"id", "idd", "nil", "not", "sqrt", "notree"};

    /**
     * Names of hidden plain-script methods which take one string argument.
     * They are automatically registered as dynamic methods.
     */
    private final static String[] ONE_PARS_METHOD_NAMES = new String[] {"add", "sub", "mult", "pow", "div", "mod", "sethard", "get"};

    /**
     * Names of hidden plain-script methods which take two string arguments.
     * They are automatically registered as dynamic methods.
     */
    private final static String[] TWO_PARS_METHOD_NAMES = new String[] {"smeq", "sm", "greq", "gr", "eq", "neq", NAME_OF_NEW_COMMAND_METHOD};

    /**
     * Names of hidden plain-script methods which take three string arguments.
     * They are automatically registered as dynamic methods.
     */
    private final static String[] THREE_PARS_METHOD_NAMES = new String[] {NAME_OF_NEW_COMMAND_WITH_PATTERN_METHOD};

    @ConversionMethod(plainText = false)
    public String showPrepTree() {
        String script = this.getRawScript().trim();
        String pre = INSCR_BEG_TAG;
        String post = INSCR_END_TAG + METHOD_CHAIN_SEPARATOR + PREP_TREE_METHOD_NAME;
        
//        if (script.startsWith(pre) && script.endsWith(post)) {
//            return script;
//        } else {
        return pre + script + post;
//        }
    }
    
    @ConversionMethod
    public String evaluateExpression() {
        return evaluateExpression(0);
    }
    
    @ConversionMethod
    public String evaluateExpression(int decimals) {
        Double result = evaluator.evaluate(this.getRawScript());
        String resStr = StaticMethods.roundStr(result, decimals);
        return resStr;
    }

    @ConversionMethod
    public String createURL() {
        return VFPVariables.URL_TO_DIRECT_XWIZZ_SERVER + WebLink.encodeScriptAsURLPar(this.getRawScript(), true);
    }

    @ConversionMethod(plainText = false)
    public String createExercise(
            String titleString,
            String explanationHTML,
            String solutionString,
            String codeToEarn,
            String regexForAllowedMethodNames,
            String regexForAllowedClassNames,
            String regexForAllowedTargetClassNames,
            String solExp,
            boolean exEncrypt,
            boolean encrypt) {
        String oldString = this.exerciseString;
        Exercise oldEx = this.getExercise();
        
        this.exerciseString = ""
                + Exercise.TITLE_NAME + VARIABLE_DELIMITER + Exercise.BEGIN_LITERAL + titleString + Exercise.END_LITERAL + Exercise.SEPARATOR
                + Exercise.EXPLANATION_NAME + VARIABLE_DELIMITER + Exercise.BEGIN_LITERAL + explanationHTML + Exercise.END_LITERAL + Exercise.SEPARATOR
                + Exercise.SOLUTION_NAME + VARIABLE_DELIMITER + Exercise.BEGIN_LITERAL + solutionString + Exercise.END_LITERAL + Exercise.SEPARATOR
                + Exercise.SOLUTION_CODE_NAME + VARIABLE_DELIMITER + Exercise.BEGIN_LITERAL + codeToEarn + Exercise.END_LITERAL + Exercise.SEPARATOR
                + Exercise.METHOD_NAME_REGEX_NAME + VARIABLE_DELIMITER + Exercise.BEGIN_LITERAL + regexForAllowedMethodNames + Exercise.END_LITERAL + Exercise.SEPARATOR
                + Exercise.CURRENT_CLASS_REGEX_NAME + VARIABLE_DELIMITER + Exercise.BEGIN_LITERAL + regexForAllowedClassNames + Exercise.END_LITERAL + Exercise.SEPARATOR
                + Exercise.TARGET_CLASS_REGEX_NAME + VARIABLE_DELIMITER + Exercise.BEGIN_LITERAL + regexForAllowedTargetClassNames + Exercise.END_LITERAL + Exercise.SEPARATOR
                + Exercise.SCRIPT_ENCRYPTED_NAME + VARIABLE_DELIMITER + Exercise.BEGIN_LITERAL + encrypt + Exercise.END_LITERAL + Exercise.SEPARATOR
                + Exercise.EXERCISE_ENCRYPTED_NAME + VARIABLE_DELIMITER + Exercise.BEGIN_LITERAL + exEncrypt + Exercise.END_LITERAL + Exercise.SEPARATOR
                + Exercise.SOLUTION_EXPLANATION_NAME + VARIABLE_DELIMITER + Exercise.BEGIN_LITERAL + solExp + Exercise.END_LITERAL + Exercise.SEPARATOR
                ;

        this.setExercise(new Exercise(""));
        this.getExercise().setExEncrypted(exEncrypt);
        
        String scriptWithoutDeclarations = remDecl(this.getRawScript());
        String declarations = generateCompleteDeclarationsBlock();
        String completeCode = scriptWithoutDeclarations + declarations;
        
        this.exerciseString = oldString;
        this.setExercise(oldEx);
        
        return completeCode;
    }

    @ConversionMethod(plainText = false)
    public String decollapseRightPrio() {
        return this.decollapseRulesRight(this.getScriptWithoutPrepAndDecl()) 
                + "\n" + this.generateCompleteDeclarationsBlock();
    }

    @ConversionMethod(plainText = false)
    public String decollapseLeftPrio() {
        return this.decollapseRulesLeft(this.getScriptWithoutPrepAndDecl())
                + "\n" + this.generateCompleteDeclarationsBlock();
    }

    @ConversionMethod(plainText = false)
    public String formatScriptRightPrio() {
        return this.collapseRulesLtoR(this.getScriptWithoutPrepAndDecl())
                + this.generateCompleteDeclarationsBlock();
    }

    @ConversionMethod(plainText = false)
    public String formatScriptLeftPrio() {
        return this.collapseRulesRtoL(this.getScriptWithoutPrepAndDecl())
                + this.generateCompleteDeclarationsBlock();
    }

    @ConversionMethod(plainText = false)
    public String addDeclarationsToScript() {
        return this.getScriptWithoutPrepAndDecl() 
                + "\n" + this.generateCompleteDeclarationsBlock();
    }

    @ConversionMethod(plainText = false)
    public String collapseRulesRtoL(String scriptRaw2) {
        return this.collapseRulesLeft(this.collapseRulesRight(scriptRaw2));
    }

    @ConversionMethod(plainText = false)
    public String collapseRulesLtoR(String scriptRaw2) {
        return this.collapseRulesRight(this.collapseRulesLeft(scriptRaw2));
    }

    @ConversionMethod(plainText = false)
    public String decollapseRules(String scriptRaw2) {
        return this.decollapseRulesLeft(this.decollapseRulesRight(scriptRaw2.trim()));
    }
    
    @ConversionMethod(plainText = false)
    public String createPlainPDFScript() {
        String workingDir = WebLink.getWORKING_DIRECTORY();
        if (MainLink.isApplicationOriginDesktop()) {
            workingDir = VFPWindow.getSINGLETON_INSTANCE().getWorkingDirectory().getAbsolutePath();
        }
        
        PDFProcessor pdf = this.generatePDFscript(workingDir);
        String pdfSourceString = pdf.getSourceString()
                .replace(INSCR_BEG_TAG_FOR_INTERNAL_USAGE, INSCR_BEG_TAG)
                .replace(INSCR_END_TAG_FOR_INTERNAL_USAGE, INSCR_END_TAG);
        String pdfCodePrefix = pdf.getCodePrefix();
        
        String code = "";
        if (pdfSourceString != null) {
            code = pdfSourceString;
            if (code.endsWith("\n")) {
                code = code.substring(0, code.length() - 1);
            }
            code = pdfCodePrefix + code;
        } else {
            throw new RuntimeException("Plain PDF code not supported.");
        }
        
        return code;
    }
    
    @ConversionMethod(plainText = false)
    public String prepTree() {
        return this.scriptTree.getGraphvizTree(null);
    }
    
    @ConversionMethod(plainText = false)
    public String stepWiseScriptTranslation(Boolean withPreprocessors) {
        RepresentableDefault dummy = (RepresentableDefault) RepresentableFactory.instanceFromScript(this.getRawScript(), null);
        removeKnownStuff();
        int debugLevel = 1;
        if (withPreprocessors) {
            debugLevel++;
        }
        String debugScript = dummy.applyDeclarationsAndPreprocessors(this.getRawScript(), null, debugLevel);
        return debugScript;
    }
    
        /* *****************************************
         * Inscript-defined conversion methods
         * *****************************************/
        
        private static final HashMap<String, String> inscriptMethodDefinitions = new HashMap<>();
        private static final HashMap<String, Integer> inscriptMethodParNums = new HashMap<>();
        private static final HashMap<String, String> inscriptMethodParPatterns = new HashMap<>();
        
        @ConversionMethod(plainText = true) 
        public String newMethod(String methodName, String numPars) {
            return newMethodD(methodName, numPars, INSCRIPT_STANDARD_PARAMETER_PATTERN);
        }

        @ConversionMethod(plainText = true) 
        public String newMethodD(String methodName, String numPars, String parameterPattern) {
            if (inscriptMethodDefinitions.keySet().contains(methodName) 
                    || this.getDynamicMethods().containsKey(methodName)
                    || this.getMethodNameAbbreviations().containsKey(methodName)) {
                throw new MethExistsException("Method '" + methodName + "' already exists.");
            }
            
            inscriptMethodDefinitions.put(methodName, this.getRawScript());
            inscriptMethodParNums.put(methodName, Integer.parseInt(numPars));
            inscriptMethodParPatterns.put(methodName, parameterPattern);
            return "";
        }

        @ConversionMethod(plainText = true) 
        public String executeCommand(String methodName, String... pars) {
            String methodBody = inscriptMethodDefinitions.get(methodName);
            String pattern = inscriptMethodParPatterns.get(methodName);
            
            int numRepl = pars.length + 1;
            String[] searchList = new String[numRepl];
            String[] replacementList = new String[numRepl];
            
            searchList[0] = makroPattern(0, pattern);
            replacementList[0] = getRawScript();
            for (int i = 1; i < numRepl; i++) {
                searchList[i] = makroPattern(i, pattern);
                replacementList[i] = pars[i - 1];
            }
            
            return StringUtils.replaceEach(methodBody, searchList, replacementList);
        }

        public static String makroPattern(int i, String pattern) {
            if (pattern.replace("n", "").length() != pattern.length() - 1) {
                throw new RuntimeException("Malformed makro parameter pattern pattern '" + pattern + "' does not contain exactly one 'n'.");
            }
            
            String makroPar = pattern.replace("n", i + "");
            return makroPar;
        }
    
        /* *****************************************
         * EO inscript-defined conversion methods
         * *****************************************/
        
        private static final String PREP_TREE_METHOD_NAME = "prepTree";
        private static final String SHOW_PREP_TREE_METHOD_NAME = "Show prepTree";
        private static final String SHOW_PREP_TREE_METHOD_NAME_G = "Zeige prepTree";

        @ConversionMethod(plainText = false)
        public String embedInLaTeX() {
            return LaTeX.LATEX_PREAMBLE 
                    + "%varm|gra%\n\n"
                    + "*** Put your \\LaTeX\\ stuff here\\ldots\n\n"
                    + INSCR_BEG_TAG
                    + this.getRawScript()
                    + INSCR_END_TAG
                    + "\n\n\\ldots and there ***";
        }
        
    /* **********************************
     * EO conversion methods.
     * **********************************/

    @Override
    public HashMap<String, MethodWrapper> getDynamicMethods() {
        HashMap<String, MethodWrapper> methods = RepresentableAsPDF.super.getDynamicMethods();
        
        String addDeclarationsName = ADD_DECLARATIONS_TO_SCRIPT_METHOD_NAME;
        String addDeclarationsName_G = "Deklarationen hinzufügen";
        String formatName = FORMAT_SCRIPT_METHOD_NAME;
        String formatName_G = "Skript formatieren";
        String decollapseLeftName = DECOLLAPSE_RULES_LEFT_METHOD_NAME;
        String decollapseLeftName_G = "Regeln links auffächern";
        String decollapseRightName = DECOLLAPSE_RULES_RIGHT_METHOD_NAME;
        String decollapseRightName_G = "Regeln rechts auffächern";
        String plainCodeName = PLAIN_GENERATOR_CODE_METHOD_NAME;
        String plainCodeName_G = "Zeige Generator-Code";
        String getURLCode = URL_TO_THIS_SCRIPT_METHOD_NAME;
        String getURLCode_G = "Erstelle URL zu diesem Skript";
        String generateExercise = CREATE_EXERCISE_FROM_THIS_SCRIPT_METHOD_NAME;
        String generateExercise_G = "Erzeuge Aufgabe aus Skript";
        String forLoopName = "for";
        String evalName = "eval";
        String evalNameDecimal = "evalD";
        String embedInLatexMethodName = "embedInLaTeX";
        
        try {
            MethodWrapper mwh0 = new MethodWrapper(
                    this.getClass().getMethod("forloop", String.class, Integer.TYPE, Integer.TYPE), 
                    this, 
                    forLoopName);

            MethodWrapper mwh1 = new MethodWrapper(
                    this.getClass().getMethod("evaluateExpression", Integer.TYPE), 
                    this, 
                    evalName);

            MethodWrapper mwh2 = new MethodWrapper(
                    this.getClass().getMethod("evaluateExpression"), 
                    this, 
                    evalName);

            for (String name : ZERO_PARS_METHOD_NAMES) {
                methods.put(name, new MethodWrapper(
                                this.getClass().getMethod(name), 
                                this, 
                                name));
            }

            for (String name : ONE_PARS_METHOD_NAMES) {
                methods.put(name, new MethodWrapper(
                                this.getClass().getMethod(name, String.class), 
                                this, 
                                name));
            }

            for (String name : TWO_PARS_METHOD_NAMES) {
                methods.put(name, new MethodWrapper(
                                this.getClass().getMethod(name, String.class, String.class), 
                                this, 
                                name));
            }

            for (String name : THREE_PARS_METHOD_NAMES) {
                methods.put(name, new MethodWrapper(
                                this.getClass().getMethod(name, String.class, String.class, String.class),
                                this,
                                name));
            }
            
            String createDebugScriptName = STEPWISE_EXPANSION_METHOD_NAME;
            String createDebugScriptName_G = "Schrittweise Script-Evaluation";
            String createDebugScriptMethodName = STEPWISE_EXPANSION_ACTUAL_METHOD_NAME;
            MethodWrapper createDebugScript = new MethodWrapper(
                    this.getClass().getMethod(createDebugScriptMethodName, Boolean.class),
                    LaTeX.class, // Target script class. Important to set correctly!
                    this,
                    "Shows the stepwise expansion of the current script for debugging.",
                    "Erzeugt eine Animation der Expansion dieses Skripts zum Debuggen.",
                    createDebugScriptName,
                    createDebugScriptName_G,
                    6);
            createDebugScript.setParameterExplanation(0, "Set to true to show in each step the currently available preprocessors.");
            createDebugScript.setParameterExplanation_G(0, "Auf true gesetzt, werden auch die in jedem Schritt verfügbaren Preprozessoren angezeigt.");
            if (this.getRawScript() == null || !this.getRawScript().contains(INSCR_BEG_TAG)) {
                createDebugScript.setMethodButtonEnabled(false);
                createDebugScript.setTooltip(createDebugScript.getTooltip() + " (There is nothing to expand.)");
                createDebugScript.setTooltip_G(createDebugScript.getTooltip_G() + " (Es gibt nichts zu expandieren.)");
            }
            createDebugScript.setMethodButtonVisible(true);

            String embedIntoLatexName = "Embed into LaTeX";
            String embedIntoLatexName_G = "Bette ein in LaTeX";
            MethodWrapper mwEmbedIntoLatex = new MethodWrapper(
                    this.getClass().getMethod(embedInLatexMethodName),
                    LaTeX.class, // Target script class. Important to set correctly!
                    this,
                    "Creates a plain LaTeX script with the current script embedded into it.",
                    "Erzeugt ein LaTeX-Skript, worin das aktuelle Skript eingebettet ist.",
                    embedIntoLatexName,
                    embedIntoLatexName_G,
                    5);
            mwEmbedIntoLatex.setMethodButtonVisible(true);

            MethodWrapper mwToggleTree = new MethodWrapper(
                    this.getClass().getMethod("showPrepTree"),
                    Graphviz.class, // Target script class. Important to set correctly!
                    this,
                    "Shows the preprocessor tree of this script. Can be useful for debugging complex scripts.",
                    "Zeigt den Preprozessor-Baum an. Nützlich zum Debuggen.",
                    SHOW_PREP_TREE_METHOD_NAME,
                    SHOW_PREP_TREE_METHOD_NAME_G,
                    6);
            mwToggleTree.setMethodButtonVisible(true);

            MethodWrapper mwTree = new MethodWrapper(
                    this.getClass().getMethod(PREP_TREE_METHOD_NAME),
                    Graphviz.class, // Target script class. Important to set correctly!
                    this,
                    "",
                    "",
                    PREP_TREE_METHOD_NAME,
                    PREP_TREE_METHOD_NAME,
                    3);
            mwTree.setMethodButtonVisible(false);

            MethodWrapper mw1 = new MethodWrapper(
                    this.getClass().getMethod("addDeclarationsToScript"),
                    this.getClass(), // Target script class. Important to set correctly!
                    this,
                    "Adds all availabe parameter declarations to script",
                    "Fügt den Deklarations-Bereich zum Skript hinzu",
                    addDeclarationsName,
                    addDeclarationsName_G,
                    3);
            mw1.setMethodButtonVisible(!this.allowCollapsingAndDecollapsing);
            mw1.setBckgnd(formatButtonsColor);
            
            MethodWrapper mw2 = new MethodWrapper(
                    this.getClass().getMethod("formatScriptLeftPrio"),
                    this.getClass(), // Target script class. Important to set correctly!
                    this,
                    "Collapse rules, add declarations, remove comments etc.",
                    "Formatiert das Skript, fasst insbesondere Regeln zusammen (falls vorhanden)",
                    formatName,
                    formatName_G,
                    3);

            mw2.setMethodButtonVisible(this.allowCollapsingAndDecollapsing);
            mw2.setBckgnd(formatButtonsColor);

            MethodWrapper mw3 = new MethodWrapper(
                    this.getClass().getMethod("decollapseLeftPrio"),
                    this.getClass(), // Target script class. Important to set correctly!
                    this,
                    "Decollapse all rules on the left side of assignments",
                    "Entfächert all Regeln auf der linken Seite der Zuweisungen",
                    decollapseLeftName,
                    decollapseLeftName_G,
                    3);

            mw3.setMethodButtonVisible(this.allowCollapsingAndDecollapsing);
            mw3.setBckgnd(formatButtonsColor);

            MethodWrapper mw4 = new MethodWrapper(
                    this.getClass().getMethod("decollapseRightPrio"),
                    this.getClass(), // Target script class. Important to set correctly!
                    this,
                    "Decollapse all rules on the right side of assignments",
                    "Entfächert all Regeln auf der rechten Seite der Zuweisungen",
                    decollapseRightName,
                    decollapseRightName_G,
                    3);

            mw4.setMethodButtonVisible(this.allowCollapsingAndDecollapsing);
            mw4.setBckgnd(formatButtonsColor);

            HashSet<Class<? extends RepresentableAsPDF>> targetClasses = new HashSet<>();
            
            for (PDFProcessor p : this.getPossiblePDFProcessorClasses()) {
                targetClasses.add(p.getPlainRepresentableClass());                
            }
            
            MethodWrapper mw5 = new MethodWrapper(
                    this.getClass().getMethod("createPlainPDFScript"),
                    targetClasses, // Target script class. Important to set correctly!
                    this,
                    "Create the plain descriptive code for this script (i.e., GraphViz DOT or Latex code)",
                    "Erzeugt den Code des dem Skript zugrundeliegenden PDF-Prozessors (Graphviz DOT oder Latex)",
                    plainCodeName,
                    plainCodeName_G,
                    5);
            mw5.setBckgnd(new Color(0xe6b9ac));
            
            MethodWrapper mw6 = new MethodWrapper(
                    this.getClass().getMethod("createURL"),
                    this.getPDFProcessor().getPlainRepresentableClass(), // Target script class. Important to set correctly!
                    this,
                    "Create the URL code which leads to this script",
                    "Erzeuge die URL, die zu diesem Skript führt",
                    getURLCode,
                    getURLCode_G,
                    5);
            mw6.setReturnValueIsScript(false);
            mw6.setDisplayLevel(5);

            MethodWrapper mw7 = new MethodWrapper(
                    this.getClass().getMethod(
                            "createExercise", 
                            String.class, 
                            String.class, 
                            String.class, 
                            String.class, 
                            String.class, 
                            String.class, 
                            String.class, 
                            String.class, 
                            Boolean.TYPE,
                            Boolean.TYPE
                            ), 
                    this.getClass(), 
                    this, 
                    "Create an exercise script from this script.", 
                    "Erzeuge ein Aufgaben-Skript aus diesem Skript.", 
                    generateExercise, 
                    generateExercise_G, 
                    5);
            
            String regexLink = "https://docs.oracle.com/javase/tutorial/essential/regex/";
            String javaRegexLink = HelpTexts.link(regexLink, "Regular expression (Java)", true);
            String javaRegexLink_G = HelpTexts.link(regexLink, "Regulärer Ausdruck (Java)", true);
            
//            String titleString,
            mw7.setParameterExplanation(0, "Provide a short description or title for the exercise.");
            mw7.setParameterExplanation_G(0, "Eine kurze Beschreibung oder einen Titel für die Aufgabe.");
//            String explanationHTML,
            mw7.setParameterExplanation(1, "Enter a description for the exercise. You can use HTML.");
            mw7.setParameterExplanation_G(1, "Eine lange Beschreibung der Aufgabe ein (HTML erlaubt).");
//            String solutionString,
            mw7.setParameterExplanation(2, "Optional string to be entered by the user as a solution to this exercise.");
            mw7.setParameterExplanation_G(2, "Optional kann hier ein Lösungs-String für die Aufgabe angegeben werden.");
//            String codeToEarn,
            mw7.setParameterExplanation(3, "Optional code string the user can earn by entering the correct solution.");
            mw7.setParameterExplanation_G(3, "Optionaler Belohnungscode, den der Benutzer durch das Lösen der Aufgabe verdienen kann.");
//            String regexForAllowedMethodNames,
            mw7.setParameterExplanation(4, "Optional " + javaRegexLink + " to constrain displayed conversion methods - constrain by method name.");
            mw7.setParameterExplanation_G(4, "Optionaler " + javaRegexLink_G + " zum Einschränken der angezeigten Konversionsmethoden - Methodenname.");
//            String regexForAllowedClassNames,
            mw7.setParameterExplanation(5, "Optional " + javaRegexLink + " to constrain displayed conversion methods - constrain by full script class name.");
            mw7.setParameterExplanation_G(5, "Optionaler " + javaRegexLink_G + " zum Einschränken der angezeigten Konversionsmethoden - Skript-Klasse.");
//          String regexForAllowedTargetClassNames,
            mw7.setParameterExplanation(6, "Optional " + javaRegexLink + " to constrain displayed conversion methods - constrain by full target class name.");
            mw7.setParameterExplanation_G(6, "Optionaler " + javaRegexLink_G + " zum Einschränken der angezeigten Konversionsmethoden - Zielklasse.");
//        String regexForAllowedTargetClassNames,
            mw7.setParameterExplanation(7, "Optional explanation to be displayed with the solution.");
            mw7.setParameterExplanation_G(7, "Optionaler Erklärungstext, der zusammen mit der Lösung angezeigt wird.");
//          boolean exEncrypt
            mw7.setParameterExplanation(8, "Set this to true if the code of the exercise - not the complete script code - should be encrypted.");
            mw7.setParameterExplanation_G(8, "Auf true gesetzt wird die Verschlüsselung der Aufgabe - nicht des gesamten Skripts - veranlasst.");
//          boolean encrypt
            mw7.setParameterExplanation(9, "Set this to true if the complete script code should be encrypted.");
            mw7.setParameterExplanation_G(9, "Auf true gesetzt wird die Verschlüsselung des gesamten Skripts veranlasst.");
 
            if (!canCreateExercise()) {
                mw7.setMethodButtonEnabled(false);
                mw7.setTooltip(mw7.getTooltip() + " (Not available: script already is an exercise or encrypted.)");
                mw7.setTooltip_G(mw7.getTooltip_G() + " (Nicht verfügbar: Skript ist bereits eine Aufgabe oder verschlüsselt.)");
            }
            
            methods.put(forLoopName, mwh0);
            methods.put(evalNameDecimal, mwh1);
            methods.put(evalName, mwh2);
            methods.put(generateExercise, mw7);
            methods.put(getURLCode, mw6);
            methods.put(plainCodeName, mw5);
            methods.put(addDeclarationsName, mw1);
            methods.put(formatName, mw2);
            methods.put(decollapseLeftName, mw3);
            methods.put(decollapseRightName, mw4);
            methods.put(PREP_TREE_METHOD_NAME, mwTree);
            methods.put(SHOW_PREP_TREE_METHOD_NAME, mwToggleTree);
            methods.put(embedIntoLatexName, mwEmbedIntoLatex);
            methods.put(createDebugScriptName, createDebugScript);
            
            for (String methodName : inscriptMethodDefinitions.keySet()) {
                methods.put(methodName, new MethodWrapper(inscriptMethodParNums.get(methodName), this, methodName));
            }
        } catch (NoSuchMethodException | SecurityException e) {
            e.printStackTrace();
        }
        
        return methods;
    }

    private boolean canCreateExercise() {
        return !(this.currentExercise != null 
                && (this.currentExercise.getTitle() != null 
                    || this.currentExercise.isEncrypted()
                    || this.currentExercise.isExEncrypted()));
    }
    
    @Override
    public HashMap<String, String> getMetaProperties() {
        HashMap<String, String> metaProps = new HashMap<>();
        String propPrefix = RepresentableDefault.class.getSimpleName();
        String currentScript = this.getRawScript();
        int scriptLength = -1;
        
        if (currentScript != null) {
            scriptLength = currentScript.length();
        }
        
        if (numOfDynamicMethods == null) {
            HashMap<String, MethodWrapper> dynamicMethods = null;
            
            try {
                dynamicMethods = this.getDynamicMethods();
            } catch (Exception e) {
                GlobalVariables.getParameters().logWeb("Dynamic methods not available for " + this.getClass().getSimpleName());
                e.printStackTrace();
            }
            
            if (dynamicMethods == null) {
                numOfDynamicMethods = -1; // Error case.
            } else {
                numOfDynamicMethods = dynamicMethods.size();
            }
        }
        
        metaProps.put(propPrefix + "_CollapsingAllowed", this.allowCollapsingAndDecollapsing + "");
        metaProps.put(propPrefix + "_NumOfDynMethods", this.numOfDynamicMethods + "");
        metaProps.put(propPrefix + "_ScriptLength", scriptLength + "");
        metaProps.put(propPrefix + "_Exercise", exerciseAvailable(this.currentExercise) + "");
        
        return metaProps;
    }

    public static boolean exerciseAvailable(Exercise exercise) {
        return exercise != null && exercise.getTitle() != null;
    }

    @Override
    public Exercise getExercise() {
        return currentExercise;
    }

    private void setExercise(Exercise currentExercise) {
        this.currentExercise = currentExercise;
    }

    @Override
    public final PDFProcessor getPDFProcessor() {
        Class<? extends PDFProcessor> pdfProcessorClass = this.getPDFProcessorClass();
        return pdfProcessorClass == null
                ? null
                : PDFProcessorFactory.getPrematureInstanceOf(pdfProcessorClass);
    }

    @Override
    public Collection<PDFProcessor> getPossiblePDFProcessorClasses() {
        HashSet<PDFProcessor> processors = new HashSet<>();
        processors.add(this.getPDFProcessor());
        return processors;
    }

    /**
     * Overridden only since final modifier not allowed in super-interface. At least
     * from this level on the method should be unchangeable. (Even
     * though it's possible, don't change the implementation of the super interface
     * either! The getEnglishName method is a convenience method, but it is supposed
     * to be equal to the class name.)
     */
    @Override
    public final String getEnglishName() {
        return RepresentableAsPDF.super.getEnglishName();
    }
    
    @Override
    public HashMap<String, String> getMethodNameAbbreviations() {
        HashMap<String, String> map = new HashMap<>();
        
        map.put("addDeclarations", ADD_DECLARATIONS_TO_SCRIPT_METHOD_NAME);
        map.put("formatScript", FORMAT_SCRIPT_METHOD_NAME);
        map.put("decollapseLeft", DECOLLAPSE_RULES_LEFT_METHOD_NAME);
        map.put("decollapseRight", DECOLLAPSE_RULES_RIGHT_METHOD_NAME);
        map.put("plainCode", PLAIN_GENERATOR_CODE_METHOD_NAME);
        map.put("url", URL_TO_THIS_SCRIPT_METHOD_NAME);
        map.put("exercise", CREATE_EXERCISE_FROM_THIS_SCRIPT_METHOD_NAME);
        map.put("stepwise", STEPWISE_EXPANSION_METHOD_NAME);
                      
        return map;
    }
    
    public HashMap<Integer, ArrayList<String>> getAnimate() {
        HashMap<Integer, ArrayList<String>> animates = new HashMap<>();
        int count = 0;
        
        for (String s : this.animate.split("->")) {
            String[] s2 = s.trim().split("\\.");
            String objectName = StaticMethods.removeWhitespaces(s2[0]);
            String furtherInst = s2.length > 1 ? StaticMethods.removeWhitespaces(s2[1]) : "";
            
            ArrayList<String> animationInstruction = new ArrayList<>(2);
            animationInstruction.add(RepresentableDefault.getQualifiedIdentifierName(objectName));
            animationInstruction.add(furtherInst);
            
            animates.put(count, animationInstruction);
            count++;
        }
        
        return animates;
    }
    
    public HashMap<String, String> getPreprocessors() {
        processPreprocessors();
        HashMap<String, String> myProcessors = alltimePreprocessors.get(this);
        
        if (myProcessors == null) { // This means that no preprocessors have been added for this script.
            myProcessors = new HashMap<>();
        }
        
        return myProcessors;
    }
    
    public String getPreprocessorFromThis(String identifier) {
        return this.getPreprocessors().get(identifier);
    }
    
    public String getPreprocessorFromAll(String identifier) {
        for (RepresentableDefault x : alltimePreprocessors.keySet()) {
            String prep = x.getPreprocessorFromThis(identifier);
            
            if (prep != null) {
                return prep;
            }
        }
        
        return null;
    }
    
    public static HashMap<RepresentableDefault, HashMap<String, String>> getAlltimePreprocessors() {
        return alltimePreprocessors;
    }
    
    private boolean isPreprocessorHidden(String name) {
        return HIDDEN_PREPROCESSORS.contains(name);
    }
    
    @ConversionMethod
    public String forloop(String varname, int from, int to) {
        String loopedVal = "";
        
        for (int i = from; i <= to; i++) {
            String currVal = this.getRawScript();
            currVal = evaluateAll(currVal.replace(varname, i + ""));
            loopedVal += currVal;
        }
        
        return loopedVal;
    }
    
    /**
     * @param string  A string, possibly containing arithmetic expressions.
     * 
     * @return  The string with each expression replaced by its evaluation.
     */
    private String evaluateAll(String string) {
        int index1 = string.indexOf(EXPR_BEG_TAG);
        int index2 = string.indexOf(EXPR_END_TAG, index1);
        
        if (index1 < 0 || index2 < 0) {
            return string;
        }
        
        index2 += 2;
        String beforePart = string.substring(0, index1);
        String toEval = string.substring(index1 + EXPR_BEG_TAG.length(), index2 - EXPR_END_TAG.length());
        String afterPart = string.substring(index2, string.length());

        String newString;
        
        try {
            newString = beforePart + StaticMethods.roundStr(evaluator.evaluate(toEval), 0) + afterPart;
        } catch (Exception e) {
            return string;
        }
        
        return evaluateAll(newString);
    }

    @Override
    public RepresentableAsPDF getRepresentableAsPDF() {
        return this;
    }

//    /**
//     * Set to true, already processed scripts will not be processed again, but
//     * a stored copy of the previously generated object will be used. This can
//     * have undesired side effects for subscripts expanding to actual objects,
//     * i.e., sub-PDFs.
//     */
//    private static boolean dynamicExpansion = true; // TODO: Can probably be removed.

    private static boolean createScriptTree = true;
    
    /**
     * This central script processing method performs the following actions:
     * <UL>
     * <LI>Cuts off the script preamble. Note that script preambles are defined as 
     * script.substring(0, script.indexOf(":")). Therefore, they may not "contain" a colon!</LI>
     * <LI>Sets the declared fields to preliminary values, i.e., only those that are 
     * not subject to in-script preprocessors (including regularly defined preprocessors, though).</LI>
     * <LI>In a loop, as long as the script changes:</LI>
     * <UL>
     * <LI>Processes in-script preprocessors for plain text methods only.</LI>
     * <LI>Sets all now available declared fields.</LI>
     * <LI>Processes ALL in-script preprocessors in the non-declarations part of the script.</LI>
     * </UL>
     * <LI>Sets the main script variable to the resulting script.</LI>
     * </UL>
     * After the call of this method the following is valid:
     * <UL>
     * <LI>The main script variable <code>this.scriptWithoutPreprocessors</code> contains 
     * <B>only plain script code from the middle part of the script</B>, 
     * without declarations or preprocessors. Particularly, the variable is non-null, no matter what.</LI>
     * <LI>All variables are set to the defined values, including in-script and regular preprocessors.</LI>
     * </UL>
     * 
     * @param codeRaw2  The code to parse (without a real parser though). 
     *                  Comments must have been removed already.
     * @param father    The enclosing (parental) representable, if any,
     *                  {@code null} otherwise.
     * @param debugLevel     If 1, a debug script is created, if 2, preprocessors are included.
     * @return  Debug code if requested, <code>null</code> otherwise.
     */
    public String applyDeclarationsAndPreprocessors(String codeRaw2, RepresentableAsPDF father, int debugLevel) {
        this.rawScript = codeRaw2; // Nothing is done to rawScript code.
        this.superRep = father;
        resetDebugVars();
        
        if (codeRaw2 == null || codeRaw2.isEmpty()) {
            this.processedScript = "";
            return null;
        }
        
        String codeRaw = inferPlaceholdersForPlainText(codeRaw2); // Secure plain-text parts.
        
        int colonPos = codeRaw.indexOf(":") + 1;
        this.preamble = codeRaw.substring(0, colonPos);
        this.processedScript = codeRaw.substring(colonPos).trim();
        
        findAllVariables();
        setDeclaredFields();

        // SCRIPT TREE ** creation starts here.
        initializeScriptTree();
        extractInscriptProcessors(new HashMap<>(), debugLevel);                    // Process all inscript preprocessors.
        // EO SCRIPT TREE ** The tree will not be adapted after this point.

        this.processedScript = remDecl(this.processedScript);                      // Cut out declarations.
        this.processedScript = undoPlaceholdersForPlainText(this.processedScript); // Undo placeholder securing.
        this.processedScript = this.processedScript.replace(NOP_SYMBOL, "");       // Clear all NOP symbols from script.
        RepresentableDefault.ignorePreprocessorsAndAnimateOnce = false;            // Reset to not ignoring any fields.
        
        return finalizeDebugScript(debugLevel > 0);
    }

    private void resetDebugVars() {
        this.debugScript = "";
        this.debugAnimateInstruction = "";
        this.debugScriptCounter = 1;
        this.maxHeight = 0;
        this.maxWidth = 0;
    }

    protected void initializeScriptTree() {
        if (this.processedScript == null) {
            this.processedScript = this.rawScript.substring(this.rawScript.indexOf(":") + 1);
        }
        
        this.scriptTree = new ScriptTree();                                                     // Create and...
        this.scriptTree.addScript(null, this.processedScript, 0, processedScript.length() - 1); // ...initialize script tree.
    }
    
    private ScriptTree scriptTree;
    
    /**
     * Goes through the current version of 
     * {@link RepresentableDefault#processedScript} and replaces 
     * all inscript preprocessor parts with their resulting
     * expanded value. In this process, chains are evaluated, possibly
     * leading to the evaluation of new scripts and recursive calls of this
     * method.</BR>
     * </BR> 
     * Non-inscript preprocessors are evaluated during declarations evaluation.
     * 
     * @param allowRegularScripts  If set to false, only plain-text scripts are
     *                             considered, the others ignored.
     * @param debug                If 1, a debug script is created, if 2, preprocessors are included.
     * @return  Iff there has been a change on 
     *          {@link RepresentableDefault#processedScript}.
     */
    private boolean extractInscriptProcessors(HashMap<String, Integer> processed, int debugLevel) {
        // Check for deep regression.
        Integer times = processed.get(processedScript);

        if (times != null && times > 30) {
            throw new RuntimeException("Deep regression detected for script:\n" + this.processedScript);
        }
        
        if (times == null) {
            processed.put(processedScript, 1);
        }
        
        processed.put(processedScript, processed.get(processedScript) + 1);
        String debugScript = this.processedScript;
        
        // Find next preprocessor.
        int indexOfPrep = findNextInscriptPos();
        
        boolean debug = debugLevel > 0;
        boolean debugWithPrep = debugLevel > 1;
        if (indexOfPrep < 0) {
            handleSingleDebugScript(debug, debugScript, -1, -1, debugWithPrep);
            return false; // No preprocessor tags.
        }

        boolean changed = false;
        double scale = 1;
        String identifierName = "u-" + count;
        
        String preprocessorScript = MiscMath.extractFirstSubstringLevelwise(processedScript, INSCR_BEG_TAG, INSCR_END_TAG, indexOfPrep);
        int lengthOfPreprocessor = preprocessorScript.length() + INSCR_BEG_TAG.length() + INSCR_END_TAG.length();
        String partBefore = processedScript.substring(0, indexOfPrep);
        String partAfter = processedScript.substring(indexOfPrep + lengthOfPreprocessor);
        int indexOfSeparator = preprocessorScript.indexOf("|");
        
        int scaleLength = 0;
        if (indexOfSeparator >= 0) {
            String scaleStr = preprocessorScript.substring(0, indexOfSeparator);
            if (MiscMath.isDouble(scaleStr)) { // Scale defined.
                scaleLength = scaleStr.length() + 1;
                scale = Double.parseDouble(scaleStr);
                preprocessorScript = preprocessorScript.substring(indexOfSeparator + 1).trim();
            }
        }
        
        boolean hidden = true;
        int declLength = 0;
        int qualifiedDelta = 0;
        if (indexOfPrep > 1 && processedScript.charAt(indexOfPrep - 1) == ASSIGNMENT_OPERATOR) {
            int indexOfFNBegin = RegExIndexer.lastIndexOf(partBefore, "\\W", partBefore.length() - 1);
            identifierName = partBefore.substring(indexOfFNBegin + 1, partBefore.length() - 1);
            identifierName = raiseAndGetQualifiedIdentifierName(identifierName);
            declLength = partBefore.length() - indexOfFNBegin - 1;
            partBefore = partBefore.substring(0, indexOfFNBegin + 1);
            hidden = false;
            
            String qualifiedIdentifierName = getQualifiedIdentifierName(preprocessorScript);
            if (!qualifiedIdentifierName.equals(preprocessorScript)) {
                // The right side of the assignment has to be qualified.
                qualifiedDelta = qualifiedIdentifierName.length() - preprocessorScript.length();
                preprocessorScript = qualifiedIdentifierName;
                this.scriptTree.shiftEnd(partBefore.length() + declLength, qualifiedDelta);
            }
            
            if (this.getPreprocessorFromThis(identifierName) != null) {
                /* 
                 * If the identifier is re-assigned, known chains containing
                 * it have to be removed.
                 */
                
                ScriptConversionMethods.removeChainsContainingIdentifier(identifierName);
            }
        }
        
        int indexCurr = getNextNonInscriptPosition(partAfter);
        indexCurr = Math.min(indexCurr, partAfter.length());
        
        String methods = partAfter.substring(0, indexCurr); 
        partAfter = partAfter.substring(indexCurr);
        int methodPartBegin = preprocessorScript.length();
        preprocessorScript = preprocessorScript + methods;
        String placeholderForInscript = null;

        // Refresh script tree.
        int wholePrepLength = preprocessorScript.length() 
                + RepresentableDefault.INSCR_BEG_TAG.length() 
                + RepresentableDefault.INSCR_END_TAG.length()
                + declLength
                + scaleLength;
        if (createScriptTree) {
            this.scriptTree.addScript(
                    identifierName, 
                    preprocessorScript,
                    partBefore.length(), 
                    partBefore.length() + wholePrepLength - 1);
        }

        // Store debug script.
        int begin = indexOfPrep - declLength;
        handleSingleDebugScript(debug, debugScript, begin, begin + wholePrepLength + starsIgnored - qualifiedDelta, debugWithPrep);
        
        // Check if the preprocessor is just an identifier name.
        String trimmed = preprocessorScript.trim();
        if (this.isVariable(trimmed)) {
            placeholderForInscript = VAR_BEG_TAG + trimmed + VAR_END_TAG; // TODO: Do we need this?
            this.addPreprocessor(preprocessorScript, identifierName, hidden, methodPartBegin);
        } else {
            this.addPreprocessor(preprocessorScript, identifierName, hidden, methodPartBegin);
            
            if (knownPreprocessors.containsKey(preprocessorScript)) {
                placeholderForInscript = knownPreprocessors.get(preprocessorScript);
            } else {
                placeholderForInscript = placeholderForInscript(
                        identifierName, 
                        preprocessorScript,
                        scale,
                        !MiscMath.isWithinLevelwise(processedScript, indexOfPrep, DECL_BEG_TAG, DECL_END_TAG));
                
//                if (dynamicExpansion) {
                knownPreprocessors.put(preprocessorScript, placeholderForInscript);
//                }
            }
        }

        if (placeholderForInscript != null) {
            String placeholderFinal = checkForPlainTextTags(placeholderForInscript);
            processedScript = partBefore + placeholderFinal + partAfter;
            this.scriptTree.shiftEnd(
                    partBefore.length() + wholePrepLength - 1, 
                    placeholderFinal.length() - wholePrepLength);
            changed = true;
        }
        
        this.processPendingVars();
        setDeclaredFields();
        count++;

        return changed | this.extractInscriptProcessors(processed, debugLevel);
    }

    private static HashMap<String, Integer> identCounts = new HashMap<>();
    
    /**
     * @param identifierName
     * @return
     */
    private String raiseAndGetQualifiedIdentifierName(String identifierName) {
        if (!identifierName.startsWith(PREFIX_FOR_TIMED_IDENTIFIERS)) {
            return identifierName;
        }
        
        if (!identCounts.containsKey(identifierName)) {
            identCounts.put(identifierName, 0);
        } else {
            identCounts.put(identifierName, identCounts.get(identifierName) + 1);
        }
        
        return getQualifiedIdentifierName(identifierName);
    }
    
    /**
     * @param identifierName  The identifier's unqualified name.
     */
    public static String getQualifiedIdentifierName(String identifierName) {
        if (!identifierName.startsWith(PREFIX_FOR_TIMED_IDENTIFIERS)) {
            return identifierName;
        }

        if (identifierName.startsWith("u-")) {
            return identifierName;
        }
        
        if (!identCounts.containsKey(identifierName)) {
            return identifierName;
        }

        return identifierName + QUALIFIED_IDENT_MARKER + identCounts.get(identifierName);
    }

    public static String getUnqualifiedName(String qualifiedName) {
        if (!qualifiedName.startsWith(PREFIX_FOR_TIMED_IDENTIFIERS)) {
            return qualifiedName;
        }

        return qualifiedName.split(QUALIFIED_IDENT_MARKER)[0];
    }
    
    public static List<String> getAllQualifiedIdentifiers(String ident) {
        LinkedList<String> idents = new LinkedList<>();

        if (!ident.startsWith(PREFIX_FOR_TIMED_IDENTIFIERS)) {
            idents.add(ident);
            return idents;
        }
        
        for (Integer i = 0; i <= identCounts.get(ident); i++) {
            idents.add(ident + QUALIFIED_IDENT_MARKER + i);
        }
        
        return idents;
    }

    private String debugScript;
    private String debugAnimateInstruction;
    private int debugScriptCounter;
    private int starsIgnored;
    private int maxWidth;
    private int maxHeight;

    private String finalizeDebugScript(boolean debug) {
        if (debug) {
            int width = (int) Math.round((double) maxWidth * 5.55) + 10;
            int height = (int) Math.round((double) maxHeight * 17.8) + 10;
            int margin = 10;
            
            return "latex:%artlet|gra|bbding|\\usepackage[paperwidth=" + width + "pt,paperheight=" + height + "pt,hmargin={" + margin + "mm," + margin + "mm},vmargin={" + margin + "mm," + margin + "mm}]{geometry}%\n" 
                    + debugScript
                    + DECL_BEG_TAG + "\n"
                    + ANIMATE_FIELD_NAME + VARIABLE_DELIMITER + debugAnimateInstruction + ";"
                    + "\n" + DECL_END_TAG;
        } else {
            return null;
        }
    }

    private void handleSingleDebugScript(boolean debug, final String currScr, int begSel, int endSel, boolean includePreprocessors) {
        String currentScript = currScr;
        
        if (begSel >= 0) {
            String before = currScr.substring(0, begSel);
            String within = currScr.substring(begSel, endSel);
            String after = currScr.substring(endSel);
            currentScript = before + ">>>" + within + "<<<" + after;
        }
        
        if (debug) {
            String overallOutput = currentScript 
                                + (includePreprocessors ? "\n" + this.getPreprocessorStringForDeclarations(true, false) : "");
            this.debugScript += "\n\\noindent{\\huge\\HandCuffRight}\\begin{verbatim}(" + debugScriptCounter + ") " 
                    + PLAIN_TEXT_BEGIN_TAG 
                    + overallOutput 
                    + PLAIN_TEXT_END_TAG 
                    + " \\end{verbatim}"
                    + "\\thispagestyle{empty}\\pagebreak\n";
            this.debugAnimateInstruction += "page" + debugScriptCounter + "->";
            this.debugScriptCounter++;
            this.adjustMaxLinesAndLineLengths("dum\nmy\n" + overallOutput);
        }
    }

    private void adjustMaxLinesAndLineLengths(String str){
       String[] lines = str.split("\n|\r|\n");
       int maxLocalWidth = 0;
       
       for (String line : lines) {
           if (line.length() > maxLocalWidth) {
               maxLocalWidth = line.length();
           }
       }
       
       maxHeight = Math.max(maxHeight, lines.length);
       maxWidth = Math.max(maxWidth, maxLocalWidth);
    }

    /**
     * Finds the inner-most, left-most inscript preprocessor position, by
     * preferring more <code>*</code> symbols in the end over less.</BR> 
     * </BR>
     * For example:
     * <UL>
     * <LI><code>.@{.@{.}@.}@.@{.}@.</code> will return 4.</LI>
     * <LI><code>.@{.@{.}@.}@.@{.}@*.</code> will return 13.</LI>
     * <LI><code>.@{.@{.}@.}@*.@{.}@*.</code> will return 1.</LI>
     * <LI><code>.@{.@{.}@.}@*.@{.}@*.</code> will return 1.</LI>
     * <LI><code>.@{.@{.}@.}@*.@{.}@*.@{.}@**</code> will return 21.</LI>
     * <LI><code>.@{.@{.}@*.}@*.@{.}@*.</code> will return 4.</LI>
     * </UL>
     * As a side effect, the extra <code>@</code> symbols of the end tag
     * matching the returned begin tag position are deleted from 
     * processedScript.
     * 
     * @return  The next inscript begin tag position. If no such position
     *          exists, -1 is returned and no side effects occur.
     */
    private int findNextInscriptPos() {
        String s = "";
        while (processedScript.contains(INSCR_END_TAG + s + INSCR_PRIORITY_SYMB)) {
            s += INSCR_PRIORITY_SYMB;
        }

        starsIgnored = s.length();
        
        int pos = processedScript.indexOf(INSCR_END_TAG + s);
        int count = 0;               // Because we start on an end tag.
        
        for (int i = pos; i >= 0; i--) {
            if (processedScript.startsWith(INSCR_BEG_TAG, i)) {
                count++;
            }

            if (processedScript.startsWith(INSCR_END_TAG, i)) {
                count--;
            }
            
            if (count == 0) {
                int starPos = pos + INSCR_END_TAG.length();
                processedScript = processedScript.substring(0, starPos)
                        + processedScript.substring(starPos + s.length());
                this.scriptTree.shiftEnd(pos - 1, -s.length());
                
                return i;
            }
        }

        return -1;
    }
    
    private void processPendingVars() {
        int indexOfVar = processedScript.indexOf(VAR_BEG_TAG);
        
        if (indexOfVar < 0) {
            return;
        }
        
        String varPart = MiscMath.extractFirstSubstringLevelwise(processedScript, VAR_BEG_TAG, VAR_END_TAG, indexOfVar);
        int lengthOfVar = varPart.length() + VAR_BEG_TAG.length() + VAR_END_TAG.length();
        String partBefore = processedScript.substring(0, indexOfVar);
        String partAfter = processedScript.substring(indexOfVar + lengthOfVar);
        String varVal = processChain(varPart);
        
        if (!varVal.equals(varPart)) {
            String placeholderForInscript = placeholderForInscript(varPart, varVal, 1, true);
            processedScript = 
                    partBefore 
                    + placeholderForInscript // TODO: true?? 
                    + partAfter;
            
            int wholePrepLength = varPart.length() + VAR_BEG_TAG.length() + VAR_END_TAG.length();
            
            this.scriptTree.shiftEnd(
                    partBefore.length() + wholePrepLength - 1, 
                    placeholderForInscript.length() - wholePrepLength);
        }
        
        processPendingVars();
    }
    
    private static final String FIRST_LETTER_END = INSCR_END_TAG.substring(0, 1);
    private static final String LAST_LETTER_BEG = INSCR_BEG_TAG.substring(INSCR_BEG_TAG.length() - 1);
    
    /**
     * If the script is embedded in plain text tags, replace all symbols with
     * placeholders, but leave plain-text tags for later.
     * 
     * @param  script  The script to check.
     * @return  The possibly processed script.
     */
    private String checkForPlainTextTags(String script) {
        String script2 = script;
        
        if (script.startsWith(PLAIN_TEXT_BEGIN_TAG) && script.endsWith(PLAIN_TEXT_END_TAG)) {
            script2 = replacePlaceholders(script, true);
        }
        
        if (script.startsWith(LAST_LETTER_BEG)) { // Starts with "{...":
            script2 = NOP_SYMBOL + script;        // Make "N{..." to avoid  "}@{...".
        }                                         // Note that this would PRINCIPLY work, but the other one wouldn't.

        if (script.endsWith(FIRST_LETTER_END)) { // Ends with "...}":
            script2 = script + NOP_SYMBOL;       // Make "...}N" to avoid "...}@{".
        }

        return script2;
    }

    /**
     * Returns the next position which is after the complete method chain of 
     * the preprocessor.
     * 
     * @param partAfter  The part AFTER the preprocessor base script.
     * @return  The next position outside the preprocessor's method chain.
     */
    private static int getNextNonInscriptPosition(String partAfter) {
        int count = 0;
        boolean lastWasMethodEnd = true;
        
        for (int i = 0; i < partAfter.length(); i++) {
            char currChar = partAfter.charAt(i);
            String currStr = currChar + "";
            
            if (count == 0) {
                if (lastWasMethodEnd) {
                    if (!partAfter.startsWith(METHOD_CHAIN_SEPARATOR, i)) {
                        return i; // No methods more to come (particularly at pos 0 if no methods at all).
                    }
                } else if (!Character.isLetterOrDigit(currChar) 
                        && !currStr.equals(METHOD_PARS_BEGIN_TAG) 
                        && !currStr.equals(METHOD_PARS_END_TAG)
                        && !currStr.equals(METHOD_CHAIN_SEPARATOR)) {
                    return i;
                }
            }
            
            lastWasMethodEnd = false;
            
            if (partAfter.startsWith(METHOD_PARS_BEGIN_TAG, i)) {
                lastWasMethodEnd = false;
                count++;
            }
            
            if (partAfter.startsWith(METHOD_PARS_END_TAG, i)) {
                lastWasMethodEnd = true;
                count--;
            }
            
            if (count < 0) {
                return i;
            }
        }
        
        return partAfter.length(); // Whole partAfter belongs to preprocessor.
    }

    /**
     * Creates a placeholder for an inscript method call. If the result is a 
     * plain text (as opposed to an image), the result itself is the placeholder.
     * If it's an image, the empty string "" is returned. Representables
     * that allow images as inscript preprocessors have to override this method
     * and "do something" when the empty string occurs. (If <code>
     * allowRegularMethodCalls</code> is <code>false</code>, an exception is
     * thrown. If the method call contains a variable name in the beginning, 
     * <code>null</code> is returned.
     * 
     * @param filename             The name of the preprocessor.
     * @param preprocessorScript   The script of the preprocessor.
     * @param scale                The scale of the preprocessor, in case it
     *                             creates an image.
     * @param allowRegularScripts  In the declarations part regular scripts should
     *                             not be expanded, so the script itself is
     *                             returned in this case.
     *                            
     * @return  A placeholder, the plain-text result or <code>null</code> if
     *          a regular method call in the end requires the handling of
     *          a specific rep such as LaTeX.
     */
    protected String placeholderForInscript(
            String filename, // Don't remove this - LaTeX needs it.
            String preprocessorScript, 
            double scale,
            boolean allowRegularScripts) {
        if (isVariable(preprocessorScript)) {
            return null;
        }
        
        RepresentableAsPDF result;
        try {
            result = ScriptConversionMethods.evaluateChain(
                    removePreprocessors(this.getRawScript()), preprocessorScript, this);
            if (result == null) {
                throw new RuntimeException();
            }
        } catch (Exception e) {
            if (ScriptConversionMethods.checkAnyCause(e)) {
                throw e;
            }
            
            return null; // Chain cannot be evaluated (at this point).
        }
        
        if (DummyRepresentable.class.isAssignableFrom(result.getClass())) {
            String replace;
            replace = result.getRawScript().replace(PREAMBLE_FOR_NON_SCRIPT_METHODS, "");
            return replace;
        } else { // Regular script. Expansion allowed ==> null.
            return allowRegularScripts ? null : preprocessorScript;
        }
    }

    private static final HashSet<String> VARIABLES_MAYBE = new HashSet<>();
    private static final String VARIABLE_FINDER_LONGTIME_ID = "$VAR_FINDER$";

    /**
     * Finds all potential variables, i.e., all substrings that stand left
     * of a "=", start with an alphabetic character and contain only
     * alphanumeric characters.
     */
    private void findAllVariables() {
        int indexOf = this.processedScript.indexOf(VARIABLE_DELIMITER);

        while (indexOf > 0) {
            if (!GeneralDialog.continueLongOperation(VARIABLE_FINDER_LONGTIME_ID)) {
                throw new LongOperationException();
            }
            
            int varBegin = RegExIndexer.lastIndexOf(this.processedScript, "\\W", indexOf);
            String varName = this.processedScript.substring(varBegin + 1, indexOf);
            if (!varName.isEmpty() && StringUtils.isAlpha(varName.charAt(0) + "")) {
                VARIABLES_MAYBE.add(varName);
            }
            indexOf = this.processedScript.indexOf(VARIABLE_DELIMITER, indexOf + 1);
        }
    }

    private boolean isVariable(String preprocessorScript) {
        if (!StringUtils.isAlphanumeric(preprocessorScript)) {
            return false;
        }
        
        return VARIABLES_MAYBE.contains(preprocessorScript);
    }
    
    public void setScriptForPreprocessorExtraction(
            String scriptForPreprocessorExtraction) {
        this.processedScript = scriptForPreprocessorExtraction;
    }
    
    /**
     * Create whole script from a given string to paste into the declarations. 
     * This is usually an "animation loop string".
     * 
     * @param loopStr  The loop string as created by
     *                 {@link #createStdAnimation(String, String, String)}.
     *                 
     * @return  The complete script of <code>this</code> including the
     *          loop string in the declarations block.
     */
    public String createCompleteAnimationScript(String loopStr) {
        return this.getScriptWithoutPrepAndDecl()
                + "\n" + this.generateCompleteDeclarationsBlock(true, false, false)
                    .replace(RepresentableDefault.DECL_BEG_TAG, 
                            RepresentableDefault.DECL_BEG_TAG + loopStr);
    }

    /**
     * Create a loop string from three method names.
     * 
     * @param firstMethod  Method to use in the first step.
     * @param loopMethod   Method to use in the loop.
     * @param limitMethod  Method to use to determine loop limit.
     * 
     * @return  The isolated loop string to paste into the declarations.
     */
    public static String createStdAnimation(
            String firstMethod, String loopMethod, String limitMethod) {
//        prep=#x0=this.sim#;
//        @{prep=#xA=x~{A-1}~.sim#;}@.for[A, 1, this.inputLength]
//        animate=this@{->xB}@.for[B, 0, this.inputLength];
        
        String dot = firstMethod.isEmpty() ? "" : ".";
        String ppBegTag = RepresentableDefault.INSCR_BEG_TAG; // Note that in conversion methods obviously
        String ppEndTag = RepresentableDefault.INSCR_END_TAG;   // the standard inscript tag has to be used.
        
        String firstStr = "prep0=#x0=this" + dot + firstMethod + "#;";
        String loop = ppBegTag
                + "prepA=#xA=x~{A-1}~." 
                + loopMethod 
                + "#;"
                + ppEndTag
                + ".for[A, 1, x0." 
                + limitMethod 
                + "]";
        String animate = firstMethod.isEmpty()
                ? "animate="
                        + ppBegTag
                        + "xB->"
                        + ppEndTag
                        + ".for[B, 0, x0." + limitMethod + "];"
                : "animate=this"
                        + ppBegTag
                        + "->xB"
                        + ppEndTag
                        + ".for[B, 0, x0." + limitMethod + "];";
        
        return "/* Here comes the animation part. */\n" 
            + firstStr + "\n" 
            + loop + "\n" 
            + animate
            + "\n/* EO animation part. */";
    }
    
    /**
     * Directly sets the rawScript. Use this method in special circumstances
     * only! Usually, the method {@link #applyDeclarationsAndPreprocessors(String, RepresentableAsPDF)}
     * sets the rawScript field correctly.
     * 
     * @param rawScript  The rawScript field to set. Use only if you know what
     *                   you're doing!
     */
    public void setRawScript(String rawScript) {
        this.rawScript = rawScript;
        this.initializeScriptTree();
    }
    
    /**
     * Directly sets the father. Use this method in special circumstances
     * only! Usually, the method {@link #applyDeclarationsAndPreprocessors(String, RepresentableAsPDF)}
     * sets the father field correctly.
     * 
     * @param father  The superRep field to set. Use only if you know what
     *                you're doing!
     */
    public void setFather(RepresentableAsPDF father) {
        this.superRep = father;
    }
    
    /**
     * Best to not override at all.
     */
    @Override
    public final String getRawScript() {
        return this.rawScript;
    }
    
    static String removePreprocessors(String script) {
        if (script == null) {
            return null;
        }
        
        String newScript = script;
        
        while (newScript.contains(INSCR_END_TAG + INSCR_PRIORITY_SYMB)) {
            newScript = newScript.replace(INSCR_END_TAG + INSCR_PRIORITY_SYMB, INSCR_END_TAG);
        }
        
        int indexOf = newScript.indexOf(INSCR_BEG_TAG);
        
        while (indexOf >= 0) {
            String preprocessorScript = MiscMath.extractFirstSubstringLevelwise(newScript, INSCR_BEG_TAG, INSCR_END_TAG, indexOf);
            
            int lengthOfPreprocessor = preprocessorScript.length() + INSCR_BEG_TAG.length() + INSCR_END_TAG.length();
            String partBefore = newScript.substring(0, indexOf);
            String partAfter = newScript.substring(indexOf + lengthOfPreprocessor);
            
            // Scale defined.
            int indexOf2 = preprocessorScript.indexOf("|");
            
            if (indexOf2 >= 0) {
             /* 
              * Causes trouble if a preprocessors starts with "*doubleVal*|". 
              * But how likely is that... Or... Let's just forbid it :-)
              */
                String scaleStr = preprocessorScript.substring(0, indexOf2);
                if (MiscMath.isDouble(scaleStr)) {
                    preprocessorScript = preprocessorScript.substring(indexOf2 + 1).trim();
                }
            }
            
            if (indexOf > 1 && newScript.charAt(indexOf - 1) == ASSIGNMENT_OPERATOR) {
                int indexOfFilenamebegin = RegExIndexer.lastIndexOf(partBefore, "\\W", partBefore.length() - 1);
                partBefore = partBefore.substring(0, indexOfFilenamebegin + 1);
            }
            
            int indexCurr = getNextNonInscriptPosition(partAfter);
            indexCurr = Math.min(indexCurr, partAfter.length());
            partAfter = partAfter.substring(indexCurr);
            newScript = partBefore + partAfter;
            indexOf = newScript.indexOf(INSCR_BEG_TAG);
        }
        
        return newScript;
    }
    
    /**
     * Not quite sure yet if comments should be kept in areas marked as plain 
     * text. First intuition: Comments are above all, so should be removed.
     */
    public static final boolean KEEP_COMMENTS_IN_PLAIN_TEXT = false;
    public static final HashMap<String, String> PLACEHOLDER_MAPPING = new HashMap<>();
    public static final HashMap<String, String> PLACEHOLDER_INVERSE_MAPPING = new HashMap<>();
    
    /**
     * Stores all symbols that are somehow restricted as they serve a special
     * purpose. Caution: Don't count on this list to be really completely
     * exhaustive. I.e., look at the code now and then for pending changes.
     */
    public static final HashSet<String> SPECIAL_SYMBOLS = new HashSet<>();
    
    private static void putPlaceholderMapping(char symbolName) {
        putPlaceholderMapping("" + symbolName);
    }
    
    private static void putPlaceholderMapping(String symbolName) {
        String before = "$<<$";
        String after = "$>>$";
        String placeholderPlain = before + symbolName.hashCode() + after;

        PLACEHOLDER_MAPPING.put(symbolName, placeholderPlain);
        PLACEHOLDER_INVERSE_MAPPING.put(placeholderPlain, symbolName);
        SPECIAL_SYMBOLS.add(symbolName);
    }
    
    static {
        if (KEEP_COMMENTS_IN_PLAIN_TEXT) {
            putPlaceholderMapping(BEGIN_COMMENT);
            putPlaceholderMapping(END_COMMENT);
        } else {
            SPECIAL_SYMBOLS.add(BEGIN_COMMENT);
            SPECIAL_SYMBOLS.add(END_COMMENT);
        }
        
        putPlaceholderMapping(PLAIN_TEXT_BEGIN_TAG); // Probably unnecessary, depending on how nesting is handled.
        putPlaceholderMapping(PLAIN_TEXT_END_TAG);   // Probably unnecessary, depending on how nesting is handled.
        putPlaceholderMapping(METHOD_PARS_BEGIN_TAG);
        putPlaceholderMapping(METHOD_PARS_END_TAG);
        putPlaceholderMapping(CONVERSION_PREFIX);
        putPlaceholderMapping(CONVERSION_POSTFIX);
        putPlaceholderMapping(DECL_BEG_TAG);
        putPlaceholderMapping(DECL_END_TAG);
        putPlaceholderMapping(START_TAG_FOR_NESTED_VARIABLES);
        putPlaceholderMapping(END_TAG_FOR_NESTED_VARIABLES);
        putPlaceholderMapping(INSCR_BEG_TAG);
        putPlaceholderMapping(INSCR_END_TAG);
        putPlaceholderMapping(INSCR_BEG_TAG_FOR_INTERNAL_USAGE);
        putPlaceholderMapping(INSCR_END_TAG_FOR_INTERNAL_USAGE);
        putPlaceholderMapping(BEGIN_LITERAL);
        putPlaceholderMapping(END_LITERAL);
        putPlaceholderMapping(EXPR_BEG_TAG);
        putPlaceholderMapping(EXPR_END_TAG);
        
        SPECIAL_SYMBOLS.add(THIS_NAME);
        SPECIAL_SYMBOLS.add(PREPROCESSOR_FIELD_NAME);
        SPECIAL_SYMBOLS.add(VARIABLE_DELIMITER);
        SPECIAL_SYMBOLS.add(END_VALUE + "");
        SPECIAL_SYMBOLS.add(ANIMATE_FIELD_NAME);
        SPECIAL_SYMBOLS.add(EXERCISE_FIELD_LONG_NAME);
        SPECIAL_SYMBOLS.add(EXERCISE_FIELD_SHORT_NAME);
        SPECIAL_SYMBOLS.add(NULL_VALUE);
        SPECIAL_SYMBOLS.add(PREAMBLE_FOR_NON_SCRIPT_METHODS);
        SPECIAL_SYMBOLS.add(NOP_SYMBOL);
    }

    /**
     * @param placeholder  The object-specific placeholder in a plain-text 
     *                     section of the script.
     * @return  The original symbol.
     */
    @SuppressWarnings("unused")
    private String placeholderToSymbol(String placeholder) {
        String placeholderPlain = placeholder.replace("" + System.identityHashCode(this), "");
        String symbol = PLACEHOLDER_INVERSE_MAPPING.get(placeholderPlain);

        if (symbol != null) {
            return symbol; // Adds the current object's ID to the placeholder to allow object-specific refactorings.
        }
        
        return placeholder;
    }
    
    /**
     * @param symbol  A symbol to be ignored in a plain-text section of the
     *                script.
     * @return  The object-specific placeholder to replace the symbol with.
     */
    public String symbolToPlaceholder(String symbol) {
        String placeholderPlain = PLACEHOLDER_MAPPING.get(symbol);
        if (placeholderPlain != null) {
            return placeholderPlain + System.identityHashCode(this); // Adds the current object's ID to the placeholder to allow object-specific refactorings.
        }
        
        return symbol; // Nothing to replace.
    }

    /**
     * Replaces all symbols in a string with the according placeholders or vice versa.
     * 
     * @param toReplace  The string to process.
     * @param to         Iff the replacement goes in the direction:
     *                   symbols ==TO==> placeholders (or else: 
     *                   symbols <=FROM= placeholders).
     * @return  The string with replaced symbols or placeholders.
     */
    private String replacePlaceholders(String toReplace, boolean to) {
        String[] searchList = new String[PLACEHOLDER_MAPPING.size()];
        String[] replacementList = new String[PLACEHOLDER_MAPPING.size()];

        if (to) {
            fillLists(searchList, replacementList);
        } else {
            fillLists(replacementList, searchList);
        }
        
        return StringUtils.replaceEach(toReplace, searchList, replacementList);
    }

    /**
     * @param symbolList       This list is filled with all symbols that have to be replaced.
     * @param placeholderList  This list is filled with the according object-specific placeholders.
     */
    private void fillLists(String[] symbolList, String[] placeholderList) {
        int i = 0;
        for (String symbol : PLACEHOLDER_MAPPING.keySet()) {
            String placeholder = symbolToPlaceholder(symbol);
            symbolList[i] = symbol;
            placeholderList[i] = placeholder;
            i++;
        }
    }

    /**
     * Searches for plain-text parts <code>"@{ *PLAINTEXT* }@"</code> in the 
     * script and replaces all symbols within them with placeholders, thereby 
     * also removing the surrounding tags.<BR/>
     * <BR/>
     * Note that there is no nesting of plain-text tags, meaning that
     * <p><code>"@{ text "@{ more text}@"  }@"</code></p>
     * will yield only one plain-text part
     * <p><code>text "@{ more text</code></p>
     * where the inside opening tag will get replaced by a placeholder and
     * re-introduced later, while the extra outside closing tag will be 
     * removed for good leaving a trailing opening tag. In short: 
     * Don't do that!
     * 
     * @param script  A normal script.
     * @param deletePlainTextTags  Iff the plain text tags are to be removed from
     *                             (or else left to remain in) the script.
     * @return  The script without plain-text tags, and all symbols replaced
     *          by placeholders.
     */
    private String inferPlaceholdersForPlainText(String script) {
        String script2 = script;
        int indexOf = script2.indexOf(PLAIN_TEXT_BEGIN_TAG);
        LinkedList<String> snippets = new LinkedList<>();
        String before;
        String inner;
        String after = script2;

        String b = "";
        String e = "";
        if (!this.removePlaintextTagsAfterPreprocessorApplication()) {
            b = PLAIN_TEXT_BEGIN_TAG;
            e = PLAIN_TEXT_END_TAG;
        }
        
        while (indexOf >= 0) {
            int indexOf2 = script2.indexOf(PLAIN_TEXT_END_TAG, indexOf);
            
            if (indexOf2 < 0) {
                throw new RuntimeException("Plain-text begin tag has no matching "
                        + "end tag in script '" + script + "'.");
            }
            
            before = script2.substring(0, indexOf);
            inner = script2.substring(indexOf + PLAIN_TEXT_BEGIN_TAG.length(), indexOf2);
            after = script2.substring(indexOf2 + PLAIN_TEXT_END_TAG.length());

            snippets.add(before.replace(PLAIN_TEXT_BEGIN_TAG, "")
                    .replace(PLAIN_TEXT_END_TAG, ""));
            snippets.add(b + replacePlaceholders(inner, true)
                    .replace(PLAIN_TEXT_BEGIN_TAG, "").replace(PLAIN_TEXT_END_TAG, "") + e);
            
            script2 = after;
            indexOf = script2.indexOf(PLAIN_TEXT_BEGIN_TAG);
        }
        
        snippets.add(after);
        
        script2 = "";
        for (String s : snippets) {
            script2 += s;
        }
        
        return script2;
    }
    
    /**
     * Undoes the placeholder replacement for plain-text parts. As the placeholders
     * were object-specific, we don't care about what has happened in the
     * meantime with the plain-text parts, but just replace all the 
     * placeholders belonging to {@code this} object.
     * 
     * @param script  The whole script.
     * @return        All object-specific placeholders undone.
     */
    private String undoPlaceholdersForPlainText(String script) {
        return replacePlaceholders(script, false);
    }

    /**
     * For a script snippet without a preamble, creates a dummy script with
     * a dummy preamble, applies preprocessors to this dummy script and
     * returns the resulting expanded script (without preamble).
     * 
     * @param father  The <code>super</code> rep.
     * @param script  The String to be expanded.
     * 
     * @return  The expanded string.
     */
    public static String applyPreprocessorsStatic(RepresentableAsPDF father, String script) {
        Allrounder dummy = new Allrounder();
        dummy.createInstanceFromScript("allround:" + script, father);
        String processedScript = dummy.getScriptWithoutPrepAndDeclAndPreamble();
        return processedScript;
    }
}