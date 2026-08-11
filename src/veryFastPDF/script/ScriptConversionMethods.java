/*
 * File name:        ScriptConversionMethods.java (package veryFastPDF.script)
 * Author(s):        Lukas König
 * Java version:     8.0 (at generation time)
 * Generation date:  23.03.2016 (08:46:05)
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

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import eas.GlobalVariables;
import eas.math.MiscMath;
import eas.miscellaneous.StaticMethods;
import eas.miscellaneous.convenience.GeneralDialog;
import mainServlet.WebLink;
import veryFastPDF.algorithms.latex.LaTeX;
import veryFastPDF.pdfProcessors.PDFProcessor;
import veryFastPDF.pdfProcessors.UnsupportedOutputFormatException;
import veryFastPDF.script.exceptionHandling.MethExistsException;
import veryFastPDF.script.exceptionHandling.MethNotFoundException;

/**
 * @author Lukas König
 */
public class ScriptConversionMethods {

    /**
     * If the script contains a conversion tag at the end, the conversion is 
     * performed and the converted representable is returned. If there is no
     * such tag, {@code null} is returned. Side effects: Note that matchingRep
     * is altered by this method.
     * 
     * @param scriptWithoutComments  The script to convert.
     * @return  The converted script or {@code null}.
     */
    public static RepresentableAsPDF getConvertedRepresentableIfAny(
            String scriptWithoutComments,
            RepresentableAsPDF matchingRep,
            Collection<Class<? extends RepresentableAsPDF>> pdfTypes) {
        try {
            if (!scriptWithoutComments.endsWith(RepresentableDefault.CONVERSION_POSTFIX)) {
                return null;
            }
            
            // Get conversion tag.
            String conversionTag = getConversionTag(scriptWithoutComments);
            
            MethodWrapper mw = getWrappedMethod(conversionTag, matchingRep);
            
            if (mw == null) {
                return null;
            }
            
            String[] parameterValues = getMethodParameters(conversionTag);
            Object[] actualParameters = getParametersFor(mw, parameterValues, null);
            
            matchingRep.createInstanceFromScript(removeConversionTagsFrom(scriptWithoutComments), null);
            String converted = mw.invoke(actualParameters);
            
            if (converted.endsWith(RepresentableDefault.CONVERSION_POSTFIX)) {
                throw new RuntimeException("Conversion failed, script still contains conversion tag. "
                        + "Check conversion tag syntax: " + conversionTag);
            }

            // Checks if decryption necessary first.
            converted = decryptScript(converted);
            
            return getApplicablePDFType(converted, pdfTypes, null);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static String getConversionTag(String scriptWithoutComments) {
        String conversionTag = "";
        for (int i = scriptWithoutComments.length() - 1
                - RepresentableDefault.CONVERSION_POSTFIX.length(); i >= 0; i--) {
            conversionTag = scriptWithoutComments.charAt(i) + conversionTag;
            if (i - RepresentableDefault.CONVERSION_PREFIX.length() < 0
                    || scriptWithoutComments.substring(i - RepresentableDefault.CONVERSION_PREFIX.length(), i)
                            .equals(RepresentableDefault.CONVERSION_PREFIX)) {
                break;
            }
        }
        return conversionTag;
    }

    private static MethodWrapper getWrappedMethod(
            String conversionTag, RepresentableAsPDF r) {
        String methodName = conversionTag.split("\\[")[0];
        HashMap<String, String> methodNameAbbreviations = 
                r == null 
                    ? new HashMap<>() 
                    : r.getMethodNameAbbreviations();

        if (methodNameAbbreviations.containsKey(methodName)) {
            methodName = methodNameAbbreviations.get(methodName);
        }

        HashMap<String, MethodWrapper> conversionMethods = 
                r == null 
                    ? new HashMap<>() 
                    : r.getDynamicMethods();
                    
        MethodWrapper methodWrapper = conversionMethods.get(methodName);
        
        if (methodWrapper == null) {
            MethNotFoundException exception = new MethNotFoundException( 
                    "Conversion method '" 
                    + conversionTag 
                    + "' not found for script type '" 
                    + r.getClass().getSimpleName() 
                    + "' given by "
                    + "["
                    + r.getRawScript()
                    + "].");
            
            throw exception;
        }
        
        return methodWrapper;
    }

    private static LinkedList<String> getMethodParametersWithTags(String conversionTag) {
        if (conversionTag.startsWith(",")) {
            return getMethodParametersWithTags(conversionTag.substring(1));
        }
        
        LinkedList<String> pars = new LinkedList<>();
        
        int nextComma = MiscMath.indexOfOnTopLevel(
                conversionTag, 
                ",", 
                0, 
                RepresentableDefault.START_TAG_FOR_NESTED_VARIABLES, 
                RepresentableDefault.END_TAG_FOR_NESTED_VARIABLES);
        
        String par = "";

        if (nextComma < 0) {
            par = conversionTag.trim();
            
            par = MiscMath.extractFirstSubstringLevelwise(
                    par, 
                    RepresentableDefault.START_TAG_FOR_NESTED_VARIABLES, 
                    RepresentableDefault.END_TAG_FOR_NESTED_VARIABLES, 
                    0);
            
            pars.add(par);
            return pars;
        }

        par = conversionTag.substring(0, nextComma).trim();
        
        par = MiscMath.extractFirstSubstringLevelwise(
                par, 
                RepresentableDefault.START_TAG_FOR_NESTED_VARIABLES, 
                RepresentableDefault.END_TAG_FOR_NESTED_VARIABLES, 
                0);
        
        pars.add(par);
        pars.addAll(getMethodParametersWithTags(conversionTag.substring(nextComma)));
        
        return pars;
    }
    
    private static String[] getMethodParameters(String conversionTag) {
        if (!conversionTag.contains(RepresentableDefault.METHOD_PARS_BEGIN_TAG)
                || !conversionTag.contains(RepresentableDefault.METHOD_PARS_END_TAG)) {
            return new String[0];
        }
        
        String methPars = MiscMath.extractFirstSubstringLevelwise(
            conversionTag, 
            RepresentableDefault.METHOD_PARS_BEGIN_TAG, 
            RepresentableDefault.METHOD_PARS_END_TAG);
        
        String conversionTag2 = conversionTag.contains(RepresentableDefault.METHOD_PARS_BEGIN_TAG) 
                ? methPars
                : conversionTag;
        
        // Parameters with nested parts.
        if (conversionTag.contains(RepresentableDefault.START_TAG_FOR_NESTED_VARIABLES)) {
            conversionTag2 = conversionTag.trim();
            int beg = conversionTag2.indexOf(RepresentableDefault.METHOD_PARS_BEGIN_TAG);
            int end = conversionTag2.lastIndexOf(RepresentableDefault.METHOD_PARS_END_TAG);
            conversionTag2 = conversionTag2.substring(beg + 1, end);
            
            LinkedList<String> methodParametersWithTags = getMethodParametersWithTags(conversionTag2);
            String[] methodParametersArray = new String[methodParametersWithTags.size()];
            
            for (int i = 0; i < methodParametersWithTags.size(); i++) {
                methodParametersArray[i] = methodParametersWithTags.get(i);
            }
            
            return methodParametersArray;
        }
        
        boolean inString = false;
        LinkedList<String> pars = new LinkedList<>();
        String currentPart = "";
        
        for (int i = 0; i < conversionTag2.length(); i++) {
            if (conversionTag2.charAt(i) == ',') {
                if (inString                       // Within a "string".
                    || MiscMath.isWithinLevelwise( // Within [ and ].
                            conversionTag2, 
                            i, 
                            RepresentableDefault.METHOD_PARS_BEGIN_TAG, 
                            RepresentableDefault.METHOD_PARS_END_TAG)) {
                    currentPart += ",";
                } else { // Not in special-char block.
                    pars.add(currentPart);
                    currentPart = "";
                }
            }
            
            if (conversionTag2.charAt(i) == '\"') {
                inString = !inString;
            } else if (!inString && conversionTag2.charAt(i) != ' ' && conversionTag2.charAt(i) != ',') {
                currentPart += conversionTag2.charAt(i);
            }
            
            if (inString && conversionTag2.charAt(i) != '\"') {
                currentPart += conversionTag2.charAt(i);
            }
        }
        
        pars.add(currentPart);
        
        String[] parsArray = new String[pars.size()];
        for (int i = 0; i < pars.size(); i++) {
            parsArray[i] = pars.get(i);
        }
        
        return parsArray;
    }

    /**
     * Decrypts the script if it is encrypted, returns it unchanged otherwise.
     */
    public static String decryptScript(String encrypted) {
        if (encrypted != null && encrypted.startsWith(Exercise.ENCRYPTED_PREFIX)) {
            try {
                return StaticMethods.decompressFromSafeString(encrypted.substring(Exercise.ENCRYPTED_PREFIX.length()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return encrypted;
    }
    
    public static String encryptScript(String plain) {
        if (plain != null && !plain.startsWith(Exercise.ENCRYPTED_PREFIX)) {
            try {
                return Exercise.ENCRYPTED_PREFIX + StaticMethods.compressSafeString(plain);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return plain;
    }
    
    /**
     * Helper method for constructing a convenience method for converting
     * a script into a PDF script.
     * 
     * @param scriptTextWithoutComments  The script to be converted.
     * @param pdfTypes                   The available pdf types.
     * @param father                     The super rep of this rep.
     * @return  The representable corresponding to the script. Returns
     *          a DummyRepresentable for plain text scripts (even if
     *          not in list). Returns the Representable representing the
     *          evaluation of the method chain, if any (even if the type to
     *          which the chain expands is not in the list).
     */
    public static RepresentableAsPDF getApplicablePDFType(
            String scriptTextWithoutComments,
            Collection<Class<? extends RepresentableAsPDF>> pdfTypes,
            RepresentableAsPDF father) {
        if (scriptTextWithoutComments.startsWith(RepresentableDefault.INSCR_BEG_TAG)) {
            // Seems to be a script with method calls.
            return ScriptConversionMethods.evaluateChain(null, scriptTextWithoutComments, father);
        }

        if (scriptTextWithoutComments.startsWith(
                RepresentableDefault.PREAMBLE_FOR_NON_SCRIPT_METHODS)) {
            DummyRepresentable dummy = new DummyRepresentable(null);
            dummy.createInstanceFromScript(scriptTextWithoutComments, father);
            return dummy;
        }
        
        RepresentableAsPDF acc = null;
        
        for (Class<? extends RepresentableAsPDF> repClass : pdfTypes) {
            RepresentableAsPDF r = RepresentableFactory.getRepByClass(repClass);
            
            if (r.isAcceptableScript(scriptTextWithoutComments)) {
                if (acc == null) {
                    acc = r;
                    acc.createInstanceFromScript(removeConversionTagsFrom(scriptTextWithoutComments), father);
                    return acc;
                } else {
                    GlobalVariables.getParameters().logDebug(
                            "The script code was accepted by more than one pdf type.");
                }
            }
        }
        
        return acc;
    }

    /**
     * Same as 
     * {@link #getApplicablePDFType(String, Collection, RepresentableAsPDF)},
     * only plain text is accepted as DummyRepresentable. Call this method if
     * you know you're processing a top-level script.
     */
    public static RepresentableAsPDF getApplicablePDFTypeToplevel(String scriptWithoutComments2,
            List<Class<? extends RepresentableAsPDF>> availableRepTypes, RepresentableAsPDF father) {
        String scriptWithoutComments = scriptWithoutComments2;
        String trimmed = scriptWithoutComments.trim();
        
        if (trimmed.startsWith(RepresentableDefault.INSCR_BEG_TAG)) {
            boolean regular = false;
            for (Class<? extends RepresentableAsPDF> repClass : availableRepTypes) {
                RepresentableAsPDF r = RepresentableFactory.getRepByClass(repClass);
                if (r.isAcceptableScript(trimmed.substring(RepresentableDefault.INSCR_BEG_TAG.length()))) {
                    regular = true;
                    break;
                }
            }
            
            if (!regular) {
                boolean endsWithEndTag = trimmed.endsWith(RepresentableDefault.INSCR_END_TAG);
                
                if (endsWithEndTag) {
                    trimmed = trimmed
                            .substring(0, trimmed.length() - RepresentableDefault.INSCR_END_TAG.length())
                            .substring(RepresentableDefault.INSCR_BEG_TAG.length());
                }
                
                String dummyScr = RepresentableDefault.applyPreprocessorsStatic(father, trimmed);
                DummyRepresentable dummy = new DummyRepresentable(null);
                dummy.createInstanceFromScript(dummyScr, father);

                if (endsWithEndTag) {
                    return getApplicablePDFTypeToplevel(dummyScr, availableRepTypes, father);
                }
                
                return dummy;
            }
        }
        
        RepresentableAsPDF r = getApplicablePDFType(scriptWithoutComments, availableRepTypes, father);

        if (r == null) { // TODO: Do this during script processing.
            r = new DummyRepresentable(null);
            r.createInstanceFromScript(scriptWithoutComments, null);
        }
        
        return r;
    }

    public static String removeConversionTagsFrom(String script) {
        String result = removeTaggedPartsOnTopLevel(
                script, 
                RepresentableDefault.CONVERSION_PREFIX, 
                RepresentableDefault.CONVERSION_POSTFIX,
                new LinkedList<>(),
                new LinkedList<>());
        return result;
    }
    
    /**
     * Removes all the tagged parts on the top level of the given string. More
     * precisely, for tags "(", ")", the string: 
     * "((test) more test) still testing (not (quite) finished); (almost (there))."
     * will yield:
     * " still testing ; ." Note that all tags will be removed - this makes a
     * difference for malformed strings, such as "(test))" which will NOT
     * yield ")", but "".
     * 
     * @param code           The string to remove tagged parts from.
     * @param beginTag       The begin tag.
     * @param endTag         The end tag.
     * @param ignoreBegTags  List of tags to ignore in between.
     * @param ignoreEndTags  List of tags to ignore in between.
     * @return  The string without tagged parts.
     */
    public static String removeTaggedPartsOnTopLevel(
            String code, 
            String beginTag, 
            String endTag, 
            LinkedList<String> ignoreBegTags, 
            LinkedList<String> ignoreEndTags) {
        StringBuffer s;
        try {
            s = new StringBuffer("");
            int count = 0;
            
            for (int i = 0; i < code.length(); i++) {
                if (code.startsWith(beginTag, i) 
                        && !MiscMath.isWithinAnyLevelwise(
                                code, 
                                i, 
                                ignoreBegTags, 
                                ignoreEndTags)) {
                    count++;
                    i += beginTag.length() - 1;
                } else if (code.startsWith(endTag, i) 
                        && !MiscMath.isWithinAnyLevelwise(
                                code, 
                                i, 
                                ignoreBegTags, 
                                ignoreEndTags)) {
                    count--;
                    i += endTag.length() - 1;
                } else if (count == 0) {
                    s.append(code.charAt(i));
                }
            }
        } catch (Exception e) {
            return code;
        }
        
        return s.toString();
    }
    
    public static String removeComments(String code) {
        String removeTaggedParts = 
                ScriptConversionMethods.removeTaggedPartsOnTopLevel(
                        code,
                        RepresentableDefault.BEGIN_COMMENT, 
                        RepresentableDefault.END_COMMENT,
                        new LinkedList<>(),
                        new LinkedList<>());
        return removeTaggedParts == null ? null : removeTaggedParts.replace("\\begin{document}@", "\\begin{document} @"); // TODO: Remove Pfusch!
    }
    
    public static String encryptExScript(String plain, Collection<Class<? extends RepresentableAsPDF>> pdfTypes2) {
        if (plain != null && !plain.startsWith(Exercise.ENCRYPTED_PREFIX)) {
            try {
                RepresentableDefault r = (RepresentableDefault) ScriptConversionMethods.getApplicablePDFType(
                        removeComments(plain), 
                        pdfTypes2,
                        null);
                
                return RepresentableDefault.getStaticInstance().remDecl(r.getRawScript()) + r.generateCompleteDeclarationsBlock();
            } catch (Exception e) {
                return plain;
            }
        }
        
        return plain;
    }

    /**
     * Helper method for constructing a convenience method for converting
     * a script into a PDF script.
     * 
     * @param r  A representable reflecting the instance of a script.
     * @param absolutePDFpath  The path to store the resulting PDF in.
     *                         If no PDF is to be created, {@code null}
     *                         is allowed.
     * @return  The PDF processor containing the PDF script.
     */
    public static PDFProcessor getPDFProcessorFrom(
            RepresentableAsPDF r,
            String absolutePDFpath) {
        return r.generatePDFscript(absolutePDFpath);
    }

    public static RepresentableAsPDF LAST_TRANSLATED_REP;
    
    public static String translateScript(String script, String fileName, int hash) {
        String decryptedScript = decryptScript(script);
        String scriptWithoutComments = ScriptConversionMethods.removeComments(decryptedScript);
        RepresentableAsPDF r = getApplicablePDFTypeToplevel(scriptWithoutComments, WebLink.availablePDFTypes, null);
        
        LAST_TRANSLATED_REP = r; // Store for database info from "outside".
        
        RepresentableAsPDF rNew = ScriptConversionMethods.getConvertedRepresentableIfAny(
                scriptWithoutComments, 
                r,
                WebLink.availablePDFTypes);

        if (rNew != null) {
            return translateScript(rNew.getRawScript(), fileName, hash);
        }
            
        String workingDir = WebLink.getWORKING_DIRECTORY();
        PDFProcessor pdfProcessor = getPDFProcessorFrom(r, workingDir);
        String realScript = pdfProcessor.getPreparedSourceString();

        try {
            return pdfProcessor.getSVGCode(fileName, workingDir, realScript, hash);
        } catch (UnsupportedOutputFormatException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param rep              The representable to apply the method to.
     * @param methodSignature  The method internal name + parameters.
     * 
     * @return  A new representable with the method applied. Note that rep
     *          can be changed in the process.
     */
    public static RepresentableAsPDF applyMethod(
            RepresentableAsPDF rep,
            String methodSignature) {
        String conversionTag = getConversionTag(
                RepresentableDefault.CONVERSION_PREFIX 
                    + methodSignature 
                    + RepresentableDefault.CONVERSION_POSTFIX);
        MethodWrapper mw = getWrappedMethod(conversionTag, rep); 
        String[] pars = getMethodParameters(conversionTag);
        Object[] actualParameters = getParametersFor(mw, pars, rep.getSuper());
        String newScript = mw.invoke(actualParameters);
        return RepresentableFactory.instanceFromScript(newScript, rep.getSuper());
    }
    
    /**
     * Applies a method chain to the given representable. The list of methods 
     * has to comply with the object types generated in the process, i.e., in:</BR>
     *           [script].m1.m2.m3.m4.m5</BR>
     * [script] must provide a method named m1, [script].m1 must provide a method
     * named m2 and so on.
     * 
     * @param original        The original script.
     * @param methodsToApply  A list of methods to apply as:
     *                        "*internalName*["par1", "par2", ...]"
     * 
     * @return  A new representable with the methods applied.
     */
    public static RepresentableAsPDF applyMethodChain(
            RepresentableAsPDF original, 
            String... methodsToApply) {
        RepresentableAsPDF newRep = original;
        
        for (String methodSignature : methodsToApply) {
            newRep = applyMethod(newRep, methodSignature);
            
            if (DummyRepresentable.class.equals(newRep.getClass())) {
                GlobalVariables.getParameters().logDebug(
                        "Plain text method call: " 
                                + methodSignature 
                                + " (results in " 
                                + newRep.getRawScript().replace(
                                        RepresentableDefault.PREAMBLE_FOR_NON_SCRIPT_METHODS, 
                                        "") 
                                + ")");
            } else {
                GlobalVariables.getParameters().logDebug(
                        "Regular method call: " 
                                + methodSignature
                                + " (results in "
                                + newRep.getRawScript()
                                + ")");
            }
        }
        
        return newRep;
    }
    
    private static LinkedList<String> getMethodSinaturesFromChain(String chainRest) {
        if (chainRest.startsWith(RepresentableDefault.METHOD_CHAIN_SEPARATOR)) {
            return getMethodSinaturesFromChain(chainRest.substring(1));
        }
        
        LinkedList<String> signatures = new LinkedList<>();

        int nextPoint1 = MiscMath.indexOfOnTopLevel(
                chainRest, 
                RepresentableDefault.METHOD_CHAIN_SEPARATOR, 
                0, 
                RepresentableDefault.START_TAG_FOR_NESTED_VARIABLES, 
                RepresentableDefault.END_TAG_FOR_NESTED_VARIABLES);

        int nextPoint2 = MiscMath.indexOfOnTopLevel(
                chainRest, 
                RepresentableDefault.METHOD_CHAIN_SEPARATOR, 
                0, 
                RepresentableDefault.METHOD_PARS_BEGIN_TAG, 
                RepresentableDefault.METHOD_PARS_END_TAG);

        
        if (nextPoint1 < 0 || nextPoint2 < 0) {
            signatures.add(chainRest.trim());
            return signatures;
        }
        
        int nextPoint = Math.max(nextPoint1, nextPoint2);
        
        String signature = chainRest.substring(0, nextPoint);
        String rest = chainRest.substring(nextPoint);
        
        signatures.add(signature.trim());
        signatures.addAll(getMethodSinaturesFromChain(rest));

        return signatures;
    }
    
    private static String singleCharSeq(char c, int length) {
        StringBuilder b = new StringBuilder();
        
        for (int i = 0; i < length; i++) {
            b.append(c);
        }
        
        return b.toString();
    }
    
    private static String overwriteBrackets(String s) {
        int index1 = s.indexOf(RepresentableDefault.START_TAG_FOR_NESTED_VARIABLES);
        int index2 = MiscMath.findMatchingEndTagLevelwise(
                s, 
                RepresentableDefault.START_TAG_FOR_NESTED_VARIABLES, 
                RepresentableDefault.END_TAG_FOR_NESTED_VARIABLES, 
                index1);
        
        if (index1 < 0 || index2 < 0) {
            return s;
        }
        
        String substring = s.substring(index1, index2 + RepresentableDefault.END_TAG_FOR_NESTED_VARIABLES.length());
        
        return overwriteBrackets(s.replace(substring, singleCharSeq('-', substring.length())));
    }
    
    /**
     * In a String *EXPR*.m1[p11, p12, ...].m2[p21, p22, ...].m3[...]...
     * extract the *EXPR* part. *EXPR* is determined by cutting off from position x
     * which is the position of the first dot "." after which only alpha-numeric 
     * characters, white space, dots or parts in brackets "[" and "]" occur.
     * 
     * @param chain  The string to extract from.
     * 
     * @return  The expression part (the whole string if there are no dots or 
     *          brackets).
     */
    private static String extractExpressionPart(String chain2) {
        String chain = overwriteBrackets(chain2);
        
        int lastDotIndex = chain.length();
        int level = 0;
        
        for (int i = lastDotIndex - 1; i >= 0; i--) {
            if (level == 0 && chain.charAt(i) == RepresentableDefault.METHOD_CHAIN_SEPARATOR.charAt(0)) {
                lastDotIndex = i;
            } else if (chain.charAt(i) == RepresentableDefault.METHOD_PARS_BEGIN_TAG.charAt(0)) {
                level++;
            } else if (chain.charAt(i) == RepresentableDefault.METHOD_PARS_END_TAG.charAt(0)) {
                level--;
            } else if (level == 0 
                    && !StringUtils.isAlphanumeric(chain.charAt(i) + "") 
                    && chain.charAt(i) != ' ' 
                    && chain.charAt(i) != '\n' 
                    && chain.charAt(i) != '\r' 
                    && chain.charAt(i) != '\t') {
                break;
            }
        }
        
        return chain2.substring(0, lastDotIndex);
    }
    
    private static HashMap<String, RepresentableAsPDF> knownChains = new HashMap<>();
    private static HashMap<String, RepresentableAsPDF> knownReps = new HashMap<>();

    public static void removeChainsContainingIdentifier(String ident) {
        RepresentableDefault.getAllQualifiedIdentifiers(ident).forEach(c -> removeChainsContainingQualifiedIdentifier(c));
    }

    private static void removeChainsContainingQualifiedIdentifier(String qualIdent) {
        String idName = qualIdent;
        
        if (!(qualIdent.startsWith(RepresentableDefault.INSCR_BEG_TAG)
                && qualIdent.endsWith(RepresentableDefault.INSCR_END_TAG))) {
            idName = RepresentableDefault.INSCR_BEG_TAG + idName + RepresentableDefault.INSCR_END_TAG;
        }
        
        HashSet<String> toRemove = new HashSet<>();
        
        for (String chain : knownChains.keySet()) {
            if (chain.contains(idName)) {
                toRemove.add(chain);
            }
        }
        
        toRemove.forEach(c -> knownChains.remove(c));
    }
    
    public static void removeKnownStuff() {
        knownChains.clear();
        knownReps.clear();
    }
    
    /**
     * Evaluates a single chain of conversion method applications to a script.
     * 
     * @param repScrThis  The representable script to be considered "this". Can be
     *                    {@code null} if chain does not begin with "this".
     * @param chain       The method chain to apply as: "*script*.m1.m2.m3.m4..." 
     *                    where *script* is a script enclosed in @{ ... }@
     *                    or "this", and m1, m2, ... are method signatures.
     * @param father      The rep {@code this} is a sub-script of.
     *               
     * @return  A representable with the methods applied. Can return <code>null
     *          </code> when the chain is not valid (e.g. when starting with a
     *          variable).
     */
    public static RepresentableAsPDF evaluateChain(String repScrThis, String chain, RepresentableAsPDF father) {
        String processedChain = RepresentableDefault.processChain(chain.trim());
        String processedRaw = processedChain;
        RepresentableAsPDF repThis;
        
        if (knownChains.containsKey(processedRaw)) {
            return knownChains.get(processedRaw);
        }
        
        if (knownReps.containsKey(repScrThis)) {
            repThis = knownReps.get(repScrThis);
        } else if (repScrThis == null) {
            repThis = null;
        } else {
            repThis = createThisObject(null, repScrThis, father);
            knownReps.put(repScrThis, repThis);
        }
        
        RepresentableAsPDF repToProcess = null;
        Integer methodBegin = RepresentableDefault.getMethodPartBegins().get(processedRaw);

        if (processedChain.startsWith(RepresentableDefault.THIS_NAME)) {
            repToProcess = RepresentableFactory.copyRep(repThis, false);
//            repToProcess = rep;
            
            if (repToProcess == null) {
                throw new RuntimeException("Cannot apply methods to \"this\", no reference representable given.");
            }

            RepresentableDefault.ignorePreprocessorsAndAnimateOnce();
            repToProcess = createThisObject(repToProcess, repScrThis, father);
            processedChain = processedChain.substring(RepresentableDefault.THIS_NAME.length());
            
            try {
//                RepresentableDefault.removeAllPreprocessors();
            } catch (Exception e) {
            }
        } else if (processedChain.startsWith(RepresentableDefault.INSCR_BEG_TAG)
                && (methodBegin == null 
                    || MiscMath.findMatchingEndTagLevelwise(
                        processedChain, 
                        RepresentableDefault.INSCR_BEG_TAG, 
                        RepresentableDefault.INSCR_END_TAG, 0).equals(methodBegin - RepresentableDefault.INSCR_END_TAG.length()))) { // Whole script part surrounded by @{...}@.
            String repPart = MiscMath.extractFirstSubstringLevelwise(
                    processedChain, 
                    RepresentableDefault.INSCR_BEG_TAG, 
                    RepresentableDefault.INSCR_END_TAG, 
                    0);
            
            if (methodBegin == null) { // Possibly regular script.
                repToProcess = RepresentableFactory.instanceFromScript(repPart, father);
    
                if (repToProcess == null) { // String is no script, but plain expression.
                    repToProcess = new DummyRepresentable(repThis);
                    processedChain = createDummyrep(processedChain, repToProcess, father);
                } else {
                    processedChain = processedChain.substring(
                            repPart.length() 
                            + RepresentableDefault.INSCR_BEG_TAG.length() 
                            + RepresentableDefault.INSCR_END_TAG.length());
                }
            } else { // Treat as plain text.
                repToProcess = new DummyRepresentable(repThis);
                repToProcess.createInstanceFromScript(
                        RepresentableDefault.INSCR_BEG_TAG + repPart + RepresentableDefault.INSCR_END_TAG, 
                        father);
                processedChain = processedRaw.substring(methodBegin + 1);
            }
        } else {
            if (methodBegin != null) { // String is script without delimiters, but with method calls.
                String script = processedRaw.substring(0, methodBegin);
                processedChain = processedRaw.substring(methodBegin + 1);
                repToProcess = RepresentableFactory.instanceFromScript(script, father);
                
                if (repToProcess == null) { // String is no script, but plain expression.
                    repToProcess = new DummyRepresentable(repThis);
                    repToProcess.createInstanceFromScript(script, father);
//                    processed = createDummyrep(processed, repToProcess);
                } else {
//                    processed = "";
                }
            } else { // Assume whole string is expression without method calls.
                repToProcess = RepresentableFactory.instanceFromScript(processedChain, father);
                
                if (repToProcess == null) { // String is no script, but plain expression.
                    repToProcess = new DummyRepresentable(repThis);
                    processedChain = createDummyrep(processedChain, repToProcess, father);
                } else {
                    processedChain = "";
                }
            }
        }
        
        if (StaticMethods.removeWhitespaces(processedChain).isEmpty()) {
            knownChains.put(processedRaw, repToProcess);
            return repToProcess;
        }
        
        LinkedList<String> methodSignatures = getMethodSinaturesFromChain(processedChain);
        String[] methodSignaturesArray = new String[methodSignatures.size()];
        
        for (int i = 0; i < methodSignatures.size(); i++) {
            methodSignaturesArray[i] = methodSignatures.get(i);
        }
        
        try {
            RepresentableAsPDF applyMethodChain = applyMethodChain(repToProcess, methodSignaturesArray);
            knownChains.put(processedRaw, applyMethodChain);
            return applyMethodChain;
        } catch (Exception e) {
            if (checkAnyCause(e)) {
                e.printStackTrace();
                throw e;
            }
            
            if (WebLink.isDebugMode()) {
                GlobalVariables.getParameters().logDebug(LaTeX.NO_SERIOUS_ERROR_STRING + e.toString());
                e.printStackTrace();
            }
            
            return null;
        }
    }

    private static RepresentableAsPDF createThisObject(RepresentableAsPDF repThis, String repScrThis, RepresentableAsPDF father) {
        try {
            if (repThis == null) {
                repThis = RepresentableFactory.instanceFromScript(repScrThis, father);
            } else {
                repThis.createInstanceFromScript(repScrThis, father);
            }
            
            if (repThis == null) {
                repThis = new DummyRepresentable(null);
                repThis.createInstanceFromScript(repScrThis, father);
            }
        } catch (Exception e) {
            repThis = RepresentableFactory.getPlainApplicableTypeWithoutTagsAndNoEvaluation(repScrThis.trim());
        }
        
        return repThis;
    }

    public static boolean checkAnyCause(Exception e) {
       return checkAnyCause(e, MethNotFoundException.class, MethExistsException.class);
    }

    /**
     * Checks if any of the causes in the Throwable hierarchy of an exception
     * equals one of several RuntimeException classes. Can be used to treat 
     * some exceptions differently than others.
     * 
     * @param e                      The exception to check.
     * @param classesToCheckAgainst  The list of classes to check.
     * @return  <code>true</code> iff any of the causes of this exception
     *          is of the type of any of the classes given in the list.
     */
    @SafeVarargs
    private static boolean checkAnyCause(Exception e, Class<? extends RuntimeException>... classesToCheckAgainst) {
        Throwable e2 = e;
        
        while (e2 != null) {
            for (Class<? extends RuntimeException> classToCheckAgainst : classesToCheckAgainst) {
                if (e2.getClass().equals(classToCheckAgainst)) {
                    return true;
                }
            }
            
            e2 = e2.getCause();
        }
        
        return false;
    }
    
    /*
     * If you try to avoid assigning the parameter, 
     * be careful, it's not as simple as it seems.
     */
    @SuppressWarnings("all")
    private static String createDummyrep(
            String processed,
            RepresentableAsPDF repToProcess,
            RepresentableAsPDF father) {
        repToProcess.createInstanceFromScript(processed, father);
        String extractExpressionPart = extractExpressionPart(processed);
        repToProcess.createInstanceFromScript(extractExpressionPart, father);
        
//        processed = processed.replace(extractExpressionPart, "");
        processed = processed.substring(extractExpressionPart.length());
        
        if (processed.startsWith(RepresentableDefault.METHOD_CHAIN_SEPARATOR)) {
            processed = processed.substring(RepresentableDefault.METHOD_CHAIN_SEPARATOR.length());
        }
        
        return processed;
    }

    /**
     * Creates an array of objects corresponding to valid parameters to the
     * given method. The parameters are obtained by asking the user.
     * 
     * @param mw       The method whose parameters are desired.
     * @param father   The super representable.
     *   
     * @return
     */
    public static Object[] getParametersFor(MethodWrapper mw, RepresentableAsPDF father) {
        return getParametersFor(mw, null, father);
    }
    
    /**
     * Creates an array of objects corresponding to valid parameters to the
     * given method. The parameters are either obtained by asking the user
     * or by converting the given String array.
     * 
     * @param mw       The method whose parameters are desired.
     * @param rawPars  Optionally an array of String values encoding
     *                 the parameters. If {@code null}, the user is
     *                 prompted.
     * @param father   The super representable.  
     * @return         Parameter objects for the given method.
     */
    public static Object[] getParametersFor(
            MethodWrapper mw,
            String[] rawPars,
            RepresentableAsPDF father) {
        String[] parameters = null;
        Method m = mw.getMethodToWrap();
        Object[] list = new Object[m.getParameterCount()];

        if (rawPars != null) {
            if (list.length != rawPars.length && !m.getName().equals(RepresentableDefault.NAME_OF_EXECUTE_METHOD)) {
                throw new RuntimeException(
                        "Method '" + mw.getDisplayName() + "' "
                        + "expected " + m.getParameterCount() + " parameters, "
                        + "but received " + Arrays.toString(rawPars));
            }
            
            parameters = new String[rawPars.length];
            for (int i = 0; i < rawPars.length; i++) {
                RepresentableAsPDF processedPar = ScriptConversionMethods.evaluateChain(
                        RepresentableDefault.removePreprocessors(
                                mw.getObjectToInvokeOn().getRepresentableAsPDF().getRawScript()),
                        rawPars[i], father);
                
                if (DummyRepresentable.class.isAssignableFrom(processedPar.getClass())) {
                    parameters[i] = ((DummyRepresentable) processedPar).getRawScript()
                            .replace(RepresentableDefault.PREAMBLE_FOR_NON_SCRIPT_METHODS, "");
                } else {
                    throw new RuntimeException("Parameter " + rawPars[i] + " could not be evaluated.");
                }
            }
            
            if (list.length != rawPars.length) { // Special case: we have an inscript-defined method call.
                return parameters;
            }
        }
        
        // Regular case.
        boolean stdValBool = false;
        int stdValNum = 5;
        int i = 0;

        try {
            for (Parameter par : m.getParameters()) {
                Object stdVal = par.getType().equals(Boolean.class) 
                        || par.getType().equals(Boolean.TYPE)
                        ? stdValBool
                        : stdValNum;
                stdVal = par.getType().equals(String.class) ? "string" : stdVal;
                String methodDescription = 
                        "Value for " 
                                + par.getType() 
                                + " parameter '" 
                                + par.getName() 
                                + "' (method " 
                                + m.getName() 
                                + ")";
                
                if (mw.getMethodDescription() != null) {
                    methodDescription = mw.getMethodDescription();
                }
                
                String t;
                
                if (parameters == null) {
                    t = GeneralDialog.getStringFromUser(
                            methodDescription, 
                            stdVal + "", 
                            "$$" + m.getName() + "--" + par.getName() + "$$");
                } else {
                    t = parameters[i];
                }
                
                if (t == null) {
                    return null;
                }
                
                if (par.getType().equals(Boolean.class) || par.getType().equals(Boolean.TYPE)) {
                    t = isStringTrue(t) ? "true" : "false";
                    list[i] = Boolean.parseBoolean(t);
                } else 
                if (par.getType().equals(Double.class) || par.getType().equals(Double.TYPE)) {
                    list[i] = Double.parseDouble(t);
                } else 
                if (par.getType().equals(Float.class) || par.getType().equals(Float.TYPE)) {
                    list[i] = Float.parseFloat(t);
                } else 
                if (par.getType().equals(Integer.class) || par.getType().equals(Integer.TYPE)) {
                    list[i] = Integer.parseInt(t);
                } else 
                if (par.getType().equals(Long.class) || par.getType().equals(Long.TYPE)) {
                    list[i] = Long.parseLong(t);
                } else 
                if (par.getType().equals(String.class)) {
                    list[i] = t;
                }
                
                i++;
            }
        } catch (NumberFormatException e) {
            GlobalVariables.getParameters().logWarning(
                    "Method invokation failed for method '" 
                    + m.getName() + "' due to the following exception: " + e.getMessage());
        }
        
        return list;
    }
    
    public static Boolean isStringTrue(String bool) {
        if (bool == null) {
            return null;
        }
        
        if ("true".equals(StaticMethods.removeWhitespaces(bool).toLowerCase())) {
            return true;
        } else if ("false".equals(StaticMethods.removeWhitespaces(bool).toLowerCase())) {
            return false;
        } else {
            return null;
        }
    }

    /**
     * Checks for preprocessor tags inferred during the creation of the PDF code
     * (usually only for {@link LaTeX} code).
     * 
     * @param code    The code to check.
     * @param caller  Only required to determine if the plain-text tags should 
     *                be removed or not.
     * @return  Iff the script contains at least one opening and one closing
     *          plain-text tag for internal usage.
     */
    public static boolean containsInscriptPreprocessorsForInternalUsage(String code, RepresentableDefault caller) {
        String codeWithoutDeclarations = 
                caller.remDecl(
                        ScriptConversionMethods.removeComments(code));
        
        return code != null && codeWithoutDeclarations.contains(RepresentableDefault.INSCR_BEG_TAG_FOR_INTERNAL_USAGE) 
                && codeWithoutDeclarations.contains(RepresentableDefault.INSCR_END_TAG_FOR_INTERNAL_USAGE);
    }
}
