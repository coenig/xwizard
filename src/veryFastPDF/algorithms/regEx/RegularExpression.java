/*
 * File name:        RegularExpression.java (package veryFastPDF.algorithms.regEx)
 * Author(s):        Lukas König
 * Java version:     8.0 (at generation time)
 * Generation date:  11.09.2015 (13:07:35)
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

package veryFastPDF.algorithms.regEx;

import java.util.Collection;
import java.util.HashMap;

import eas.GlobalVariables;
import eas.miscellaneous.StaticMethods;
import veryFastPDF.algorithms.grammars.Grammar;
import veryFastPDF.algorithms.grammars.type2grammars.EarleyGrammar;
import veryFastPDF.algorithms.grammars.type2grammars.EarleyParser;
import veryFastPDF.algorithms.latex.LaTeX;
import veryFastPDF.algorithms.regEx.math.Expression;
import veryFastPDF.pdfProcessors.LaTeXPDF;
import veryFastPDF.pdfProcessors.PDFProcessor;
import veryFastPDF.script.ConversionMethod;
import veryFastPDF.script.Exercise;
import veryFastPDF.script.MethodWrapper;
import veryFastPDF.script.RepresentableAsPDF;
import veryFastPDF.script.RepresentableDefault;
import veryFastPDF.web.ConvenienceMethods;
import veryFastPDF.web.Webproof;
import veryFastPDF.VFPVariables;

/**
 * @author Lukas König
 */
@Webproof(useInProductiveMode = true)
public class RegularExpression extends RepresentableDefault {

    private static final long serialVersionUID = -5802825364499693724L;
    public static final String SCRIPT_PREAMBLE = "regex:";
    private RegEx regEx;
    private Expression expressionPlain;
    
    public static final Grammar regExGramm = new Grammar((Exercise) null);
    
    public static final String REG_EX_GRAMM_SCRIPT = "grammar parse(XkaX, a,+,b,XkzX,*)--0: /* RegEx */\n" + 
            "S => T | XkaX, S, +, S, XkzX | S, +, S,  | S, S | S, ., S | XkaX, S, S, XkzX | XkaX, S, ., S, XkzX | S, * | XkaX, S, XkzX, *;\n" + 
            "T => a | b | c | d | e | f | g | h | i | j | k | l | m | n | o | p | q | r | s | t | u | v | w | x | y | z "
            + "| 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9 | - | O;\n" + 
            "--declarations--\n" + 
            "N=S,T;\n" + 
            "S=S;\n" + 
            "parseTreeNum=1;\n" + 
            "displayMode=0;\n" +
            "multiLetterSymbolsHaveIndex=true;\n" + 
            "maxdepth=5;\n" + 
            "cutNonTerminalBranches=true;\n" + 
            "cutTerminalDoubleBranches=true;\n" + 
            "maxLengthWords=6\n" + 
            "--declarations-end--";

    public RegularExpression(RegularExpression other) {
        this(other.getExercise());
        this.setRawScript(other.getRawScript());
        this.regEx = other.regEx;
        this.setExpressionPlain(other.getExpressionPlain());
    }

    public RegularExpression(Exercise exercise) {
        super(exercise);
        this.setAllowCollapsingRules(false);
    }
    
    @Override
    public String[] getExampleScripts() {
        return new String[] {
                SCRIPT_PREAMBLE + "\n(a+b)*c+O*\n"
                        + "--declarations--\n"
                        + "showSomeWords=true;\n"
                        + "--declarations-end--",
                SCRIPT_PREAMBLE + "\ninfo2-ist-((sehr+nicht-so)-)*toll\n" + 
                        "--declarations--\n" + 
                        "e=#n#;\n" + 
                        "showSomeWords=true\n" + 
                        "--declarations-end--",
                SCRIPT_PREAMBLE + "\n(0+1)*1+0*"
                };
    }
    
    @Override
    public boolean isAcceptableScript(String code) {
        return code != null && code.startsWith(SCRIPT_PREAMBLE);
    }

    @Override
    public void createInstanceFromScript(String codeRaw, RepresentableAsPDF father) {
        this.applyDeclarationsAndPreprocessors(codeRaw, father, 0);
        String codeProcessed = StaticMethods.removeWhitespaces(this.getScriptWithoutPrepAndDeclAndPreamble());
        
        // Check via parsing if script correct.
        if (regExGramm.getRawScript() == null) {
            regExGramm.createInstanceFromScript(REG_EX_GRAMM_SCRIPT, null);
        }
        
        String characterWise = "";
        for (int i = 0; i < codeProcessed.length(); i++) {
            String comma = ",";
            if (i == codeProcessed.length() - 1) {
                comma = "";
            }
            
            if (codeProcessed.charAt(i) == '(') {
                characterWise += "XkaX" + comma;
            } else if (codeProcessed.charAt(i) == ')') {
                characterWise += "XkzX" + comma;
            } else {
                characterWise += codeProcessed.charAt(i) + comma;
            }
        }
        
        EarleyGrammar grammarEarley = new EarleyGrammar(regExGramm);
        EarleyParser parser = new EarleyParser(grammarEarley, GlobalVariables.getParameters());
        boolean parseable = parser.erkenne(characterWise);
        
        if (parseable) {
            setExpressionPlain(createExpression(codeProcessed));
        } else {
            setExpressionPlain(createExpression("cannot+parse+regular+expression"));
        }
        
        regEx = new RegEx(getExpressionPlain());
        getExpressionPlain().createFromRegEx(regEx);
    }

    public static Expression createExpression(String expressionString) {
        Expression expPlain = new Expression("(", ")", new String[] {"*"}, "+", ".");
        expPlain.setInvisibleOp(".");
        expPlain.createFromExpString(expressionString);
        return expPlain;
    }

    private String formatDerivedWords(Collection<String> words) {
        String s = "& \\mbox{\\textbf{First " + words.size() +  " words:} }";
        String old = "";
        
        for (String word : words) {
            if (word.equals("")) {
                s += "\\lambda,";
            } else {
                s += word + ", ";
            }
            
            if (s.length() - old.length() > 50) {
                s += "\\\\\n&";
                old = s;
            }
        }
        
        return s + "\\ldots";
    }
    
    private boolean showSomeWords = true;
    
    @Override
    public PDFProcessor generatePDFscript(String pdfPath) {
        super.generatePDFscript(pdfPath);

        String code = LaTeX.createAlignEnv(
                "& " + this.getExpressionPlain().toString(true) + "" 
                + (showSomeWords ? "\\\\\n" + formatDerivedWords(this.regEx.deriveWords(1)) : ""), 
                this);
        LaTeX rep = new LaTeX(this.getExercise());
        rep.createInstanceFromScript(code, null);
        return rep.generatePDFscript(pdfPath);
    }

    @Override
    public String getGermanName() {
        return "Regulärer-Ausdruck";
    }
    
    @Override
    public Class<? extends PDFProcessor> getPDFProcessorClass() {
        return LaTeXPDF.class;
    }

    @Override
    public String createScriptFromInstance() {
        this.getExpressionPlain().createFromRegEx(this.regEx);
        String withoutDeclarations = SCRIPT_PREAMBLE + "\n" + this.getExpressionPlain().toString();
        RegularExpression exp = new RegularExpression(this);
        exp.createInstanceFromScript(withoutDeclarations, null);
        return exp.addDeclarationsToScript();
    }
    
    @ConversionMethod(plainText = false)
    public String simplify() {
        this.regEx.simplify();
        return this.createScriptFromInstance();
    }
    
    @Override
    public HashMap<String, MethodWrapper> getDynamicMethods() {
        HashMap<String, MethodWrapper> methods = super.getDynamicMethods();

        String simplify = "Simplify (a little)";
        String simplify_G = "Vereinfache (ein bisschen)";
        
        try {
            MethodWrapper mw1 = new MethodWrapper(
                    this.getClass().getMethod("simplify"), 
                    RegularExpression.class, // Target script class. Important to set correctly!
                    this,
                    "Performs some simplification heuristics - the result will usually not be minimal",
                    "Wendet einige Vereinfachungsheuristiken an - eine Minimierung ist im Allgemeinen allerdings nicht möglich",
                    simplify,
                    simplify_G);
            methods.put(simplify, mw1);
        } catch (SecurityException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
        
        return methods;
    }
    
    @Override
    public String getModeDependentInfo(String mode, boolean english) {
        if (mode.equals(ConvenienceMethods.INFO_II_MODE_NAME)) {
            return ConvenienceMethods.createInfo2ModeString(
                    2, 
                    4, 
                    1, 
                    "http://www.dasinfobuch.de/links/RL-Grammatiken-Reg-Ausdr%C3%BCcke.html",
                    VFPVariables.BASE_QA_ADDRESS + "?qa=349&qa_1=band-i-kapitel-4",
                    english
                    );
        }

        return "";
    }
    
    @Override
    public HashMap<String, String> getMetaProperties() {
        String className = this.getClass().getSimpleName();
        HashMap<String, String> metaProperties = super.getMetaProperties();
        String reservedCharacters = "";
        
        if (this.getRawScript() != null && this.getExpressionPlain() != null) {
            reservedCharacters = this.getExpressionPlain().getReservedCharacters().toString();
        }
        
        metaProperties.put(className + "_reservedCharacters", reservedCharacters);

        return metaProperties;
    }

    /**
     * @return Returns the expressionPlain.
     */
    public Expression getExpressionPlain() {
        return expressionPlain;
    }

    /**
     * @param expressionPlain The expressionPlain to set.
     */
    public void setExpressionPlain(Expression expressionPlain) {
        this.expressionPlain = expressionPlain;
    }
}
