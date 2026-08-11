/*
 * File name:        MethodWrapper.java (package veryFastPDF.script)
 * Author(s):        Lukas König
 * Java version:     8.0 (at generation time)
 * Generation date:  29.06.2015 (18:50:54)
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
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import eas.GlobalVariables;

/**
 * Mere data class which stores all information required to automatically create
 * buttons for script conversion methods. To register a method for being shown
 * automatically, use the {@code getConverterMethods} method of 
 * {@code RepresentableAsPDF}. (Note that only {@code RepresentableDefault}
 * objects can automatically create those buttons. If you have a {@code RepresentableAsPDF}
 * it won't work and you'll have to do it manually. Anyway, you shouldn't use
 * plain {@code RepresentableAsPDF}, unless you have a good reason.)
 * 
 * @author Lukas König
 */
public class MethodWrapper {

    private Method methodToWrap;
    private Collection<Class<? extends RepresentableAsPDF>> classesOfTargetScript;
    private Wrappable objectToInvokeOn;
    private boolean isMethodButtonVisible = true;
    private boolean isMethodButtonEnabled = true;
    private String tooltipForButton;
    private String tooltipForButton_G;
    private String methodDescription;
    private Color bckgnd = new Color(0xaface6);
    private boolean addMethodReturnValueToOldScript = false;
    private boolean returnValueIsRegularScript = true;
    private String displayName;
    private String displayName_G;
    private double displayLevel;
    private String[] parameterExplanations;
    private String[] parameterExplanations_G;
    private boolean useInWebProductiveMode = true;

//    /**
//     * Returns the execute command method with fake parameters. If their 
//     * number is -1, the real method is returned.
//     * 
//     * @param fakeParsNum  Number of fake string parameters to fool method:
//     *                  {@link ScriptConversionMethods#getParametersFor(MethodWrapper, String[], RepresentableAsPDF)}
//     * @return  The (fake or real) method.
//     * @throws SecurityException 
//     * @throws NoSuchMethodException 
//     */
//    private static Method getExecuteCommandMethod(int fakeParsNum, Wrappable objectToInvokeOn) throws NoSuchMethodException, SecurityException {
//        if (fakeParsNum == -1) {
//            return objectToInvokeOn.getClass().getMethod("executeCommand", String[].class);
//        }
//
//        Class<?>[] classArr = new Class[fakeParsNum];
//        
//        for (int i = 0; i < classArr.length; i++) {
//            classArr[i] = String.class;
//        }
//        
//        return objectToInvokeOn.getClass().getMethod("executeCommand", classArr);
//    }
    
    /**
     * Constructor for inscript-defined methods.
     * 
     * @throws SecurityException 
     * @throws NoSuchMethodException 
     */
    public MethodWrapper(
            int methodPars,
            Wrappable objectToInvokeOn,
            String shortName) throws NoSuchMethodException, SecurityException {
        this(objectToInvokeOn.getClass().getMethod(RepresentableDefault.NAME_OF_EXECUTE_METHOD, String.class, String[].class),
                objectToInvokeOn,
                shortName);
    }
    
    /**
     * Constructor for hidden plain-text methods.
     * 
     * @param methodToWrap
     * @param objectToInvokeOn
     * @param shortName
     */
    public MethodWrapper(
            Method methodToWrap,
            Wrappable objectToInvokeOn,
            String shortName) {
        this(methodToWrap, objectToInvokeOn.getRepresentableAsPDF().getPDFProcessor().getPlainRepresentableClass(), objectToInvokeOn, "", "", shortName, shortName);
        this.isMethodButtonVisible = false;
        this.returnValueIsRegularScript = false;
        this.checkAnnotation(true);
    }
    
    public MethodWrapper(
            Method methodToWrap, 
            Class<? extends RepresentableAsPDF> classOfConvertedScript,
            Wrappable objectToInvokeOn,
            String tooltipToShowInGUI,
            String tooltipToShowInGIU_G,
            String displayName,
            String displayName_G) {
        this(methodToWrap, 
                createColl(classOfConvertedScript),
                objectToInvokeOn,
                tooltipToShowInGUI,
                tooltipToShowInGIU_G,
                displayName,
                displayName_G);
    }

    public MethodWrapper(
            Method methodToWrap, 
            Collection<Class<? extends RepresentableAsPDF>> classOfConvertedScript,
            Wrappable objectToInvokeOn,
            String tooltipToShowInGUI,
            String tooltipToShowInGIU_G,
            String displayName,
            String displayName_G) {
        this(
                methodToWrap, 
                classOfConvertedScript, 
                objectToInvokeOn, 
                tooltipToShowInGUI, 
                tooltipToShowInGIU_G, 
                displayName, 
                displayName_G, 
                (objectToInvokeOn != null && objectToInvokeOn.getClass().equals(classOfConvertedScript))
                    ? 1
                    : 2);
    }

    public MethodWrapper(
            Method methodToWrap, 
            Class<? extends RepresentableAsPDF> classOfTargetScript,
            Wrappable objectToInvokeOn,
            String tooltipToShowInGUI,
            String tooltipToShowInGIU_G,
            String displayName,
            String displayName_G,
            int displayLevel) {
        this(
                methodToWrap, 
                createColl(classOfTargetScript),
                objectToInvokeOn,
                tooltipToShowInGUI,
                tooltipToShowInGIU_G,
                displayName,
                displayName_G,
                displayLevel);
    }

    /**
     * A wrapper class containing some information about a method to be invoked
     * by reflections.
     * 
     * @param methodToWrap           The method to be invoked later.
     * @param classesOfTargetScript  The class representing the returned script.
     * @param objectToInvokeOn       The object to invoke the method on.
     * @param tooltipToShowInGUI     A tooltip to show on GUI elements
     *                               which trigger the invokation of this
     *                               method.
     */
    public MethodWrapper(
            Method methodToWrap, 
            Collection<Class<? extends RepresentableAsPDF>> classOfTargetScript,
            Wrappable objectToInvokeOn,
            String tooltipToShowInGUI,
            String tooltipToShowInGIU_G,
            String displayName,
            String displayName_G,
            double displayLevel) {
        this.methodToWrap = methodToWrap;
        this.methodToWrap.setAccessible(true);
        this.classesOfTargetScript = classOfTargetScript;
        this.objectToInvokeOn = objectToInvokeOn;
        this.setTooltip(tooltipToShowInGUI);
        this.tooltipForButton_G = tooltipToShowInGIU_G;
        this.displayName = displayName;
        this.displayName_G = displayName_G;
        this.displayLevel = displayLevel;
        
        // Target class different from current class.
        if (objectToInvokeOn != null && !objectToInvokeOn.getClass().equals(classOfTargetScript)) {
            bckgnd = new Color(0xbce6ac);
        }
        
        int parameterCount = this.methodToWrap.getParameterCount();
        this.parameterExplanations = new String[parameterCount];
        this.parameterExplanations_G = new String[parameterCount];
        
        for (int i = 0; i < parameterCount; i++) {
            this.parameterExplanations[i] = "";
            this.parameterExplanations_G[i] = "";
        }
        
        try {
            this.setDisplayLevelName(
                    1, 
                    ((RepresentableAsPDF) this.objectToInvokeOn).getEnglishName() + " conversion", 
                    "Konversionen f&uuml;r " + ((RepresentableAsPDF) this.objectToInvokeOn).getGermanName());
        } catch (Exception e) {
            this.setDisplayLevelName(1, "", "");
        }
        
        this.setDisplayLevelName(2, "Conversion into", "Konversion in");
        this.setDisplayLevelName(3, "Script formatting", "Skriptformatierung");
        this.setDisplayLevelName(4, "Additional information", "Weitere Informationen");
        this.setDisplayLevelName(5, "For teachers", "F&uuml;r Lehrende");
        this.setDisplayLevelName(6, "Debugging", "Debuggen");
        
        this.checkAnnotation(false);
    }

    private static Collection<Class<? extends RepresentableAsPDF>> createColl(
            Class<? extends RepresentableAsPDF> classOfConvertedScript) {
        Collection<Class<? extends RepresentableAsPDF>> coll = new HashSet<>();
        coll.add(classOfConvertedScript);
        return coll;
    }
    
    public double getDisplayLevel() {
        return this.displayLevel;
    }
    
    public String getTooltip_G() {
        return this.tooltipForButton_G;
    }

    public String getDisplayNameWithDots() {
        String dots = dotsIfAny();
        return this.displayName + dots;
    }

    public String getDisplayName() {
        return this.displayName;
    }
    
    public String dotsIfAny() {
        String dots = "";
        
        if (this.methodToWrap.getParameterTypes().length > 0
                || !this.returnValueIsRegularScript) {
            dots = "...";
        }
        return dots;
    }
    
    public String getDisplayName_G() {
        return this.displayName_G;
    }

    public String getDisplayNameWithDots_G() {
        String dots = dotsIfAny();
        return this.displayName_G + dots;
    }
    
    public Wrappable getObjectToInvokeOn() {
        return this.objectToInvokeOn;
    }
    
    public boolean isReturnValueScript() {
        return this.returnValueIsRegularScript;
    }

    public void setReturnValueIsScript(boolean returnValueIsScript) {
        this.returnValueIsRegularScript = returnValueIsScript;
        this.setBckgnd(new Color(0xe3e6ac));
        this.displayLevel = 4;
    }
    
    public boolean isAddMethodReturnValueToOldScript() {
        return this.addMethodReturnValueToOldScript;
    }

    public void setAddMethodReturnValueToOldScript(
            boolean addMethodReturnValueToOldScript) {
        this.addMethodReturnValueToOldScript = addMethodReturnValueToOldScript;
    }

    public Color getBckgnd() {
        return this.bckgnd;
    }
    
    public void setBckgnd(Color bckgnd) {
        this.bckgnd = bckgnd;
    }
    
    public boolean isMethodButtonVisible() {
        return this.isMethodButtonVisible;
    }

    public void setMethodButtonVisible(boolean isMethodButtonVisible) {
        this.isMethodButtonVisible = isMethodButtonVisible;
    }

    public boolean isMethodButtonEnabled() {
        return this.isMethodButtonEnabled;
    }

    public void setMethodButtonEnabled(boolean isMethodButtonEnabled) {
        this.isMethodButtonEnabled = isMethodButtonEnabled;
    }

    public Method getMethodToWrap() {
        return this.methodToWrap;
    }
    
    public Collection<Class<? extends RepresentableAsPDF>> getClassesOfTargetScript() {
        return this.classesOfTargetScript;
    }

    public String getTooltip() {
        return tooltipForButton;
    }

    public void setTooltip_G(String tooltipForButton_G) {
        this.tooltipForButton_G = tooltipForButton_G;
    }
    
    public void setTooltip(String tooltip) {
        this.tooltipForButton = tooltip;
    }

    public String getMethodDescription() {
        return methodDescription;
    }

    public void setMethodDescription(String methodDescription) {
        this.methodDescription = methodDescription;
    }

    public void setParameterExplanation(int i, String explanation) {
        this.parameterExplanations[i] = explanation;
    }

    public void setParameterExplanation_G(int i, String explanation) {
        this.parameterExplanations_G[i] = explanation;
    }

    public String getParameterExplanation(int i) {
        return this.parameterExplanations[i];
    }

    public String getParameterExplanation_G(int i) {
        return this.parameterExplanations_G[i];
    }

    public static String removePreambleFrom(String preambledString) {
        return preambledString.substring(RepresentableDefault.PREAMBLE_FOR_NON_SCRIPT_METHODS.length());
    }
    
    public String invoke(Object[] pars) {
        this.checkAnnotation(true);

        Object[] parameters = pars;
        
        String methodName = this.methodToWrap.getName();
        if (methodName.equals(RepresentableDefault.NAME_OF_EXECUTE_METHOD)) {
            parameters = new Object[] {this.displayName, pars}; // Wrap inside another array to treat as single parameter.
        }
        
        String methodPreamble = this.isReturnValueScript() ? "" : RepresentableDefault.PREAMBLE_FOR_NON_SCRIPT_METHODS;
        
        try {
            return methodPreamble 
                    + this.methodToWrap.invoke(this.objectToInvokeOn, parameters).toString();
        } catch (IllegalAccessException | IllegalArgumentException
                | InvocationTargetException e) {
            try {
                return methodPreamble 
                        + this.methodToWrap.invoke(
                                this.objectToInvokeOn.getRepresentableAsPDF(), 
                                parameters).toString();
            } catch (Exception e2) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Does not throw exception, only returns message.
     */
    private static boolean ignoreAnnotationFaults = false;
    
    /**
     * Throws an exception if the conversion method is not annotated correctly.
     * If this happens, add the annotation
     * {@link ConversionMethod} to the method that threw the exception. Add
     * {@code (plainText = false)} for regular conversion methods or nothing
     * for plain text methods ({@code true} is default value). 
     * <p>
     * Example: <code>@ConversionMethod(plainText = false)</code>
     * </p>
     * @return  {@code null} if allright, exception otherwise.
     */
    private String checkAnnotation(boolean checkPlaintext) {
        ConversionMethod annotation = this.methodToWrap.getAnnotation(ConversionMethod.class);
        String methodString = "Method '" 
                + this.methodToWrap.getName() 
                + "' of class '"
                + this.methodToWrap.getDeclaringClass().getSimpleName()
                + "', used as conversion method '"
                + this.displayName
                + "'";
        
        if (annotation == null) {
            return ignoreOrNot(methodString + ", has no ConversionMethod annotation.");
        }
        
        if (checkPlaintext) {
            if (this.returnValueIsRegularScript == annotation.plainText()) {
                if (this.returnValueIsRegularScript) {
                    return ignoreOrNot(methodString + ", is defined to return a regular script, but its annotation states plain text.");
                } else {
                    return ignoreOrNot(methodString + ", is defined to return plain text, but its annotation states regular script.");
                }
            }
        }
        
        return null;
    }

    private String ignoreOrNot(String string) {
        if (ignoreAnnotationFaults) {
            return string;
        } else {
            throw new RuntimeException(string);
        }
    }
    
    private HashMap<Double, String> groupName = new HashMap<>();
    private HashMap<Double, String> groupName_G = new HashMap<>();
    
    public void setDisplayLevelName(double level, String name, String name_G) {
        groupName.put(level, name);
        groupName_G.put(level, name_G);
    }
    
    public String getLevelName() {
        return groupName.get(this.displayLevel);
    }

    public String getLevelName_G() {
        return groupName_G.get(this.displayLevel);
    }
    
    public void setDisplayLevel(double displayLevel) {
        this.displayLevel = displayLevel;
    }
    
    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }
    
    public void setDisplayName_G(String displayName_G) {
        this.displayName_G = displayName_G;
    }
    
    public void addClassOfTargetScript(
            Class<? extends RepresentableAsPDF> classOfTargetScript) {
        if (this.classesOfTargetScript == null) {
            this.classesOfTargetScript = new HashSet<>();
        }
        
        this.classesOfTargetScript.add(classOfTargetScript);
    }

    public boolean isUseInWebProductiveMode() {
        return useInWebProductiveMode;
    }

    public void setUseInWebProductiveMode(boolean useInWebProductiveMode) {
        this.useInWebProductiveMode = useInWebProductiveMode;
    }
    
    public int getParameterCount() {
        return this.methodToWrap.getParameterTypes().length;
    }
    
    /**
     * Don't use this method programmatically, but just by manual call now and 
     * then. It is not thread-safe, and I'm also not completely sure if it gets
     * all wrong annotations of conversion methods.
     */
    private static void checkAllAnotations() {
        ignoreAnnotationFaults = true;
        
        for (Class<? extends RepresentableAsPDF> rc : RepresentableFactory.getAvailableTypes()) {
            RepresentableAsPDF r = RepresentableFactory.getRepByClass(rc);
            HashMap<String, MethodWrapper> dynamicMethods = r.getDynamicMethods();
            for (MethodWrapper mw : dynamicMethods.values()) {
                String s = mw.checkAnnotation(true);
                if (s != null) {
                    GlobalVariables.getParameters().logWarning(s);
                }
            }
        }

        ignoreAnnotationFaults = false;
    }
    
    public static void main(String[] args) {
        checkAllAnotations();
    }
}
