/*
 * File name:        DummyRepresentable.java (package veryFastPDF.script)
 * Author(s):        Lukas König
 * Java version:     8.0 (at generation time)
 * Generation date:  14.07.2015 (20:46:03)
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

import java.math.BigInteger;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Random;

import eas.math.MiscMath;
import eas.miscellaneous.StaticMethods;
import veryFastPDF.algorithms.latex.LaTeXCommands;
import veryFastPDF.pdfProcessors.GraphViz;
import veryFastPDF.pdfProcessors.LaTeXPDF;
import veryFastPDF.pdfProcessors.PDFProcessor;

/**
 * A representable holding chunks of plain text that do not translate
 * to PDF or sequences of such chunks.
 * 
 * @author Lukas König
 */
public class DummyRepresentable extends RepresentableDefault {

    public static final String BEGIN_TAG_IN_SEQUENCE = "@(";
    public static final String END_TAG_IN_SEQUENCE = ")@";
    
    public DummyRepresentable(RepresentableAsPDF repToEmbed) {
        super(null); // Dummy can't have an exercise.
        this.embeddedRep = repToEmbed;
    }

    private static final long serialVersionUID = 499789189261691645L;

    private RepresentableAsPDF embeddedRep;
    
    @Override
    public String[] getExampleScripts() {
        return new String[0];
    }

    @Override
    public boolean isAcceptableScript(String code) {
        return true;
    }

    private LinkedList<String> scriptSequence = new LinkedList<>();
    
    @Override
    public void createInstanceFromScript(String code, RepresentableAsPDF father) {
        super.setRawScript(code.replace(
                RepresentableDefault.PREAMBLE_FOR_NON_SCRIPT_METHODS, 
                "")); // Because applyScriptsAndPreprocessors is not called by Dummy.
        this.scriptSequence.clear();
        processSequence(code);
    }

    /**
     * Looks for a sequence of several chunks of 
     * <code>@(...)@@(...)@@(...)@</code> in the code and puts them into
     * <code>scriptSequence</code>.
     * 
     * @param code  The raw code to process.
     */
    private void processSequence(String code) {
        String processed = code.trim();
        
        if (StaticMethods.removeWhitespaces(code).isEmpty()) {
            return;
        }
        
        String rest = null;
        
        if (processed.startsWith(BEGIN_TAG_IN_SEQUENCE)
                && processed.endsWith(END_TAG_IN_SEQUENCE)) {
            // Sequence of scripts given.
            int indexTo = MiscMath.findMatchingEndTagLevelwise(
                    processed, BEGIN_TAG_IN_SEQUENCE, END_TAG_IN_SEQUENCE, 0);
            rest = processed.substring(indexTo + END_TAG_IN_SEQUENCE.length());
            processed = processed.substring(BEGIN_TAG_IN_SEQUENCE.length(), indexTo);
            processSequence(processed);
            processSequence(rest);
        } else {
            String proc = processed;
            
            if (proc.startsWith(RepresentableDefault.INSCR_BEG_TAG) && proc.endsWith(RepresentableDefault.INSCR_END_TAG)) {
                proc = proc.substring(RepresentableDefault.INSCR_BEG_TAG.length(), proc.length() - RepresentableDefault.INSCR_END_TAG.length());
            }
            
            scriptSequence.add(proc);
        }
    }

    @Override
    public PDFProcessor generatePDFscript(String pdfPath) {
        LaTeXPDF latex = new LaTeXPDF(
                LaTeXCommands.PREAMBLE_CROP_PAGE_PREVIEW_MAXDIM
                + "\\begin{verbatim}"
                + RepresentableDefault.PLAIN_TEXT_BEGIN_TAG
                + this.getRawScript()
                + RepresentableDefault.PLAIN_TEXT_END_TAG
                + "\\end{verbatim}"
                + LaTeXCommands.POSTAMBLE_STANDARD, 
                pdfPath,
                this);
        
        return latex;
    }
    
    @Override
    public String getGermanName() {
        return "Dummy";
    }

    @Override
    public Class<? extends PDFProcessor> getPDFProcessorClass() {
        return GraphViz.class;
    }

    @Override
    public String createScriptFromInstance() {
        String result = "";
        
        for (String s : this.scriptSequence) {
            result += BEGIN_TAG_IN_SEQUENCE + s + END_TAG_IN_SEQUENCE;
        }
        
        return result;
    }

    @Override
    public HashMap<String, String> getMethodNameAbbreviations() {
        return super.getMethodNameAbbreviations();
    }

    /**
     * @param embeddedRep  The embeddedRep object to set.
     */
    public void setEmbeddedRep(RepresentableAsPDF embeddedRep) {
        this.embeddedRep = embeddedRep;
    }
    
    @Override
    public RepresentableAsPDF getRepresentableAsPDF() {
        if (this.embeddedRep != null) {
            return this.embeddedRep;
        }
        
        return this;
    }
    
    @ConversionMethod
    public String ifChoice(String bool) {
        Boolean boolVal = ScriptConversionMethods.isStringTrue(bool);
        int count = scriptSequence.size();
        
        if (count > 2 || count < 1) { // Error case 1.
            throw new RuntimeException(count + " objects given to IF clause in '" + this.getRawScript() + "'.");
        } else if (boolVal == null) { // Error case 2.
            throw new RuntimeException("'" + bool + "' is not a Boolean value.");
        } else if (boolVal) { // True case.
            return scriptSequence.getFirst();
        } else { // False case.
            if (scriptSequence.size() == 1) { // No ELSE case defined.
                return "";
            } else {
                return scriptSequence.getLast();
            }
        }
    }
    
    @ConversionMethod
    public String element(Integer num) {
        if (num < this.scriptSequence.size()) {
            return this.scriptSequence.get(num);
        }
        
        throw new RuntimeException("Element with index " + num + " not available in sequence of length " + this.scriptSequence.size());
    }
    
    @ConversionMethod
    public String length() {
        return this.getRawScript().length() + "";
    }
    
    @ConversionMethod
    public String size() {
        return this.scriptSequence.size() + "";
    }
    
    @ConversionMethod
    public String replace(String toReplace, String replaceWith) {
        return this.getRawScript().replaceAll(toReplace, replaceWith);
    }
    
    @ConversionMethod
    public String substring(String beg, String end) {
        return this.getRawScript().substring(Integer.parseInt(beg), Integer.parseInt(end));
    }
    
    private Random rand = new Random();
    
    @ConversionMethod
    public String rand(String len) {
        return randD(len, rand.nextLong() + "");
    }
    
    @ConversionMethod
    public String randAll(String len) {
        return randDAll(len, rand.nextLong() + "");
    }
    
    @ConversionMethod
    public String randDAll(String len, String seed) {
        this.setRawScript("abcdefghijklmnopqrstuvwxyz0123456789");
        return randD(len, seed + "");
    }
    
    @ConversionMethod
    public String randNum() {
        return randDNum(rand.nextLong() + "");
    }
    
    @ConversionMethod
    public String randDNum(String seed) {
        return new Random(Long.parseLong(seed)).nextInt(Integer.parseInt(this.getRawScript())) + "";
    }
    
    @ConversionMethod
    public String randD(String len, String seed) {
        String s = "";
        Random r = new Random(Long.parseLong(seed));
        
        while (s.length() < Integer.parseInt(len)) {
            s += this.getRawScript().charAt(r.nextInt(this.getRawScript().length()));
        }
        
        return s;
    }
    
    @ConversionMethod public String nextPrime() {
        return new BigInteger(this.getRawScript()).nextProbablePrime().toString();
    }
    
    @ConversionMethod public String isPrime() {
        return new BigInteger(this.scriptSequence.get(0)).isProbablePrime(Integer.MAX_VALUE) + "";
    }
    
    @ConversionMethod public String convertBase(String from, String to) {
        return new BigInteger(this.getRawScript(), Integer.parseInt(from)).toString(Integer.parseInt(to));
    }

    @Override
    public HashMap<String, MethodWrapper> getDynamicMethods() {
        HashMap<String, MethodWrapper> dynamicMethods = super.getDynamicMethods();
        String ifName = "if";
        String elName = "element";
        
        try {
            MethodWrapper mwEl = new MethodWrapper(
                    this.getClass().getMethod("element", Integer.class), 
                    this, 
                    elName);
            
            MethodWrapper mwIF = new MethodWrapper(
                    this.getClass().getMethod("ifChoice", String.class), 
                    this, 
                    ifName);

            MethodWrapper nextPrime = new MethodWrapper(
                    this.getClass().getMethod("nextPrime"), 
                    this, 
                    "nextPrime");

            MethodWrapper isPrime = new MethodWrapper(
                    this.getClass().getMethod("isPrime"), 
                    this, 
                    "isPrime");

            MethodWrapper length = new MethodWrapper(
                    this.getClass().getMethod("length"), 
                    this, 
                    "len");

            MethodWrapper size = new MethodWrapper(
                    this.getClass().getMethod("size"), 
                    this, 
                    "size");

            MethodWrapper replace = new MethodWrapper(
                    this.getClass().getMethod("replace", String.class, String.class), 
                    this, 
                    "replace");

            MethodWrapper substring = new MethodWrapper(
                    this.getClass().getMethod("substring", String.class, String.class), 
                    this, 
                    "substring");

            MethodWrapper randD = new MethodWrapper(
                    this.getClass().getMethod("randD", String.class, String.class), 
                    this, 
                    "randD");

            MethodWrapper randAll = new MethodWrapper(
                    this.getClass().getMethod("randAll", String.class), 
                    this, 
                    "randAll");

            MethodWrapper randDAll = new MethodWrapper(
                    this.getClass().getMethod("randDAll", String.class, String.class), 
                    this, 
                    "randDAll");

            MethodWrapper rand = new MethodWrapper(
                    this.getClass().getMethod("rand", String.class), 
                    this, 
                    "rand");

            MethodWrapper randNum = new MethodWrapper(
                    this.getClass().getMethod("randNum"), 
                    this, 
                    "randNum");

            MethodWrapper randDNum = new MethodWrapper(
                    this.getClass().getMethod("randDNum", String.class), 
                    this, 
                    "randDNum");

            MethodWrapper convertBase = new MethodWrapper(
                    this.getClass().getMethod("convertBase", String.class, String.class), 
                    this, 
                    "convertBase");

            dynamicMethods.put(ifName, mwIF);
            dynamicMethods.put(elName, mwEl);
            dynamicMethods.put("nextPrime", nextPrime);
            dynamicMethods.put("isPrime", isPrime);
            dynamicMethods.put("len", length);
            dynamicMethods.put("size", size);
            dynamicMethods.put("replace", replace);
            dynamicMethods.put("substring", substring);
            dynamicMethods.put("randD", randD);
            dynamicMethods.put("randAll", randAll);
            dynamicMethods.put("randDAll", randDAll);
            dynamicMethods.put("rand", rand);
            dynamicMethods.put("randNum", randNum);
            dynamicMethods.put("randDNum", randDNum);
            dynamicMethods.put("convertBase", convertBase);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        
        return dynamicMethods;
    }
//    
//    @Override
//    public String createPlainPDFScript() {
//        return super.createPlainPDFScript();
//    }
}
