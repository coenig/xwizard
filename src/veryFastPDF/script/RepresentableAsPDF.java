/*
 * File name:        RepresentableByGraphViz.java (package eas.fundamentalAlgorithms.graphBased)
 * Author(s):        Lukas König
 * Java version:     7.0
 * Generation date:  02.12.2013 (10:50:26)
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

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.swing.JComponent;

import eas.veryFastPDF.MainLink;
import mainServlet.WebLink;
import veryFastPDF.HelpTexts;
import veryFastPDF.algorithms.latex.LaTeX;
import veryFastPDF.pdfProcessors.LaTeXPDF;
import veryFastPDF.pdfProcessors.PDFProcessor;
import veryFastPDF.plugin.VFPWindow;
import veryFastPDF.web.ConvenienceMethods;

/**
 * @author Lukas König
 */
public interface RepresentableAsPDF extends Serializable {
    
    /**
     * A set of example scripts for this class.
     * 
     * @return  An arbitrary number of valid scripts for this class.
     */
    String[] getExampleScripts();
    
    /**
     * Check if a given script represents an instance of this class.
     * Note that only one representable class may return true for
     * any given script.
     * 
     * @param code  The script to check.
     * 
     * @return  Iff the script can be converted into an object of this
     *          representable.
     */
    boolean isAcceptableScript(String script);
    
    /**
     * Central script conversion method. Creates a rep. object out of a given
     * script.
     * 
     * @param script  The script from which the rep. is to be created.
     * @param father  The enclosing representable, if {@code this} 
     *                representable is a sub-script of another representable.
     *                Otherwise, this parameter can be set to {@code null}.
     */
    void createInstanceFromScript(String script, RepresentableAsPDF father);

    /**
     * @return  The super rep of this rep, can be {@code null}.
     */
    RepresentableAsPDF getSuper();

    /**
     * Has to work safely even in premature stages when no script has been 
     * applied and when pdfPath is null.
     * 
     * @param pdfPath    The path where the preprocessed files are stored to.
     * 
     * @return The PDF processor for this representable.
     */
    PDFProcessor generatePDFscript(String pdfPath);
    
    /**
     * For the desktop version, a JComponent to manipulate the script and
     * offer additional methods. On RepresentableDefault level
     * the implementing classes do not have to override this method as
     * a standard JComponent is generated automatically. (Sometimes
     * additional specific implementations may make sense, however,
     * these will not have a representation in Web mode.
     * 
     * @return  A JComponent with additional information.
     */
    JComponent getAdditionalInfo();
    
    /**
     * A very quick helpt text in English.<BR/>
     * <BR/>
     * Don't override this method unless you know what you're doing.
     * (This construction within the interface is not so nice, but
     * due to legacy reasons it seems the most elegant whilst pragmatic.)
     * 
     * @return  A quick help text for the underlying class.
     */
    default String veryQuickHelpText() {
        HashMap<Class<?>, String> texts;
        String errorText;
            
        texts = HelpTexts.VERY_QUICK_HELP_TEXTS;
        errorText = HelpTexts.HELP_ERROR_NONE_PROVIDED;
        
        String addText = "";
        try {
            RepresentableDefault repDef = (RepresentableDefault) this;
            if (repDef.isAllowCollapsingAndDecollapsing()) {
                addText = HelpTexts.HOW_TO_COLLAPSE_RULES_QUICK;
            }
        } catch (Exception e) {
        }
        
        String helpText = texts.get(this.getClass());
        
        if (helpText == null) {
            return errorText;
        } else {
            return helpText + addText;
        }
    }
    
    /**
     * A very quick helpt text in German.<BR/>
     * <BR/>
     * Don't override this method unless you know what you're doing.
     * (This construction within the interface is not so nice, but
     * due to legacy reasons it seems the most elegant whilst pragmatic.)
     * 
     * @return  A long help text for the underlying class.
     */
    default String veryQuickHelpText_G() {
        HashMap<Class<?>, String> texts;
        String errorText;
        texts = HelpTexts.VERY_QUICK_HELP_TEXTS_G;
        
        if (texts.get(this.getClass()) == null) {
            texts = HelpTexts.VERY_QUICK_HELP_TEXTS; // Showing English help is better than nothing.
        }
        
        String addText = "";
        try {
            RepresentableDefault repDef = (RepresentableDefault) this;
            if (repDef.isAllowCollapsingAndDecollapsing()) {
                addText = HelpTexts.HOW_TO_COLLAPSE_RULES_QUICK_G;
            }
        } catch (Exception e) {
        }

        errorText = HelpTexts.HELP_ERROR_NONE_PROVIDED_G;
        String helpText = texts.get(this.getClass());

        if (helpText == null) {
            return ConvenienceMethods.replaceSpecialCharsHTML_G(errorText);
        } else {
            return ConvenienceMethods.replaceSpecialCharsHTML_G(helpText + addText);
        }
    }

    /**
     * Long help text English.<BR/>
     * <BR/>
     * Don't override this method unless you know what you're doing.
     * (This construction within the interface is not so nice, but
     * due to legacy reasons it seems the most elegant whilst pragmatic.)
     * 
     * @return  A long help text for the underlying class.
     */
    default String helpText() {
        List<Class<? extends RepresentableAsPDF>> availableTypes = getAvailablePDFTypes();
        
        HashMap<Class<?>, String> texts;
        texts = HelpTexts.LONG_HELP_TEXTS;
        
        String addText = "";
        try {
            RepresentableDefault repDef = (RepresentableDefault) this;
            if (repDef.isAllowCollapsingAndDecollapsing()) {
                addText = HelpTexts.HOW_TO_COLLAPSE_RULES;
            }
        } catch (Exception e) {
        }
        
        String helpText = texts.get(this.getClass());
        
        if (helpText == null) {
            return null; // Don't catch null case.
        }
        
        helpText = helpText.replace(
                HelpTexts.CONVERSION_BUTTONS_PLACEHOLDER, 
                "<CENTER>" + WebLink.getDynamicMethodButtonsHTMLString(
                        this, 
                        "",
                        true, 
                        true, 
                        availableTypes,
                        ".*", ".*", ".*", null) + "</CENTER>"); // TODO: correct filter according to exercise?
        
        return helpText + addText;
    }

    default List<Class<? extends RepresentableAsPDF>> getAvailablePDFTypes() {
        List<Class<? extends RepresentableAsPDF>> availableTypes;
        if (MainLink.isApplicationOriginDesktop()) {
            availableTypes = VFPWindow.getSINGLETON_INSTANCE().getAvailableRepTypes();
        } else {
            availableTypes = WebLink.availablePDFTypes;
        }
        return availableTypes;
    }

    /**
     * Long help text German.<BR/>
     * <BR/>
     * Don't override this method unless you know what you're doing.
     * (This construction within the interface is not so nice, but
     * due to legacy reasons it seems the most elegant whilst pragmatic.)
     * 
     * @return  A long help text for the underlying class.
     */
    default String helpText_G() {
        List<Class<? extends RepresentableAsPDF>> availableTypes = getAvailablePDFTypes();

        HashMap<Class<?>, String> texts;
        texts = HelpTexts.LONG_HELP_TEXTS_G;

        if (texts.get(this.getClass()) == null) {
            texts = HelpTexts.LONG_HELP_TEXTS; // Showing English help is better than nothing.
        }

        String addText = "";
        try {
            RepresentableDefault repDef = (RepresentableDefault) this;
            if (repDef.isAllowCollapsingAndDecollapsing()) {
                addText = HelpTexts.HOW_TO_COLLAPSE_RULES_G;
            }
        } catch (Exception e) {
        }
        
        String helpText = texts.get(this.getClass());
        
        if (helpText == null) {
            return null;  // Don't catch null case.
        }

        helpText = helpText.replace(
                HelpTexts.CONVERSION_BUTTONS_PLACEHOLDER, 
                "<CENTER>" + WebLink.getDynamicMethodButtonsHTMLString(
                        this, 
                        "",
                        false, 
                        true, 
                        availableTypes,
                        ".*", ".*", ".*", null) + "</CENTER>"); // TODO: correct filter according to exercise?

        return ConvenienceMethods.replaceSpecialCharsHTML_G(helpText + addText);
    }

    /**
     * This has to be the raw script as it has been given to this class from
     * outside. Don't return any processed version of the script (unless you
     * know what you're doing)!
     * 
     * @return  The current script which has been used to create this class.
     */
    String getRawScript();
    
    /**
     * Has to retrieve result safely even when no script has been applied.
     * 
     * @return  Meta properties of this Representable.
     */
    HashMap<String, String> getMetaProperties();
    
    /**
     * Provides additional information for this rep which is shown when the
     * Wizard is running in a certain mode.<BR/>
     * <BR/>
     * Modes:<BR/>
     * .i2
     * .lb
     * 
     * @return  The mapping of modes to additional info.
     */
    default String getModeDependentInfo(String mode, boolean english) {
        return "";
    }

    /**
     * All methods that create a new script out of the current representable.
     * (Usually, such a method is in the same class as the implementation of
     * the {@code getConverterMethods} method, but in general all objects from
     * all classes can be used. For this, the corresponding object has to be
     * passed to the constructor of the {@code MethodWrapper} object.)
     * 
     * @return  A new script converted from the current object.
     */
     // TODO: Don't create HashMap every time the method is called (on implementation level).
    default HashMap<String, MethodWrapper> getDynamicMethods() {
        return new HashMap<>();
    }
    
    /**
     * Don't override this method!
     * 
     * @return  Only the available dynamic methods after having been filtered.
     */
    default HashMap<String, MethodWrapper> getFilteredDynamicMethods(
            String regExEnglishMethodName,
            String regExThisClass,
            String regExTargetClass) {
        HashMap<String, MethodWrapper> filteredMethods = new HashMap<>();

        this.getDynamicMethods().forEach((name, method) -> {
            Class<? extends Object> baseClass = method.getObjectToInvokeOn().getClass();
            Collection<Class<? extends RepresentableAsPDF>> targetClasses = method.getClassesOfTargetScript();

            boolean matchesName = name.matches(regExEnglishMethodName);
            boolean matchesBaseClass = baseClass == null || baseClass.toString().matches(regExThisClass);
            boolean matchesTargetClass = false;
            
            for (Class<? extends RepresentableAsPDF> targetClass : targetClasses) {
                matchesTargetClass = matchesTargetClass
                        || (targetClass == null || targetClass.toString().matches(regExTargetClass));
            }
            
            if (matchesName
                    && matchesBaseClass
                    && matchesTargetClass) {
                filteredMethods.put(name, method);
            }
        });
        
        return filteredMethods;
    }
    
    /**
     * @return  The class of the PDF processor CURRENTLY used by this Representable.
     */
    Class<? extends PDFProcessor> getPDFProcessorClass();

    /**
     * @return  All classes of PDF processors this Representable CAN use.
     */
    Collection<PDFProcessor> getPossiblePDFProcessorClasses();
    
    PDFProcessor getPDFProcessor();

    HashMap<String, String> getMethodNameAbbreviations();
    
    /**
     * Don't override, this should always be the English name!
     */
    default String getEnglishName() {
        return this.getClass().getSimpleName();
    }
    
    String getGermanName();

    default Exercise getExercise() {
        return null;
    }

    RepresentableAsPDF getRepresentableAsPDF();
    
    /**
     * <p>Plain-text tags mark regions that are not supposed to be subject to
     * pre-processors or sub-scripts or declaration handling. Usually, they 
     * are read during first processing in 
     * {@link RepresentableDefault#applyDeclarationsAndPreprocessors(String)}
     * and deleted afterward to avoid any side effects during the actual
     * code translation by the specific lower-level Representable. Therefore,
     * the default return value of this method is {@code true}. However,
     * it may be desireable to keep the plain-text tags in the code and remove
     * them on the lower level, to be able to identify them later if the
     * script has to be translated another time.</p> 
     * <p>This is, at implementation
     * time of this method, only the case for {@link LaTeX} scripts. (There,
     * the tags are just not cared about during translation as they do not
     * hurt, and finally removed by {@link LaTeXPDF} after a possible second
     * translation run.)</p>
     * 
     * @return  Iff the plain-text tags should be removed after the application
     *          of pre-processors and declarations (or else kept in the code
     *          for later use).
     */
    default boolean removePlaintextTagsAfterPreprocessorApplication() {
        return true;
    }
}
