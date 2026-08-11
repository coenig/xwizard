/*
 * File name:        NumberRepresentation.java (package veryFastPDF.algorithms.numberRep)
 * Author(s):        Lukas König
 * Java version:     8.0 (at generation time)
 * Generation date:  04.02.2015 (20:28:15)
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

package veryFastPDF.algorithms.numberRep;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.TreeSet;

import eas.GlobalVariables;
import eas.miscellaneous.StaticMethods;
import veryFastPDF.algorithms.numberRep.representations.ExcessQ;
import veryFastPDF.pdfProcessors.GraphViz;
import veryFastPDF.script.MethodWrapper;
import veryFastPDF.script.RepresentableAsPDF;
import veryFastPDF.script.RepresentableDefault;
import veryFastPDF.script.Wrappable;

/**
 * @author Lukas König
 */
public abstract class NumberRepresentable<N extends Number> implements Wrappable {

    public static final String PAR_NAME_VALUE = "value";
    public static final String PAR_NAME_LENGTH = "length";
    public static final String PAR_NAME_CODE = "code";
    public static final String TOO_LONG_MARKER = "|";
    
    public static final BigInteger ZERO = BigInteger.ZERO;
    public static final BigInteger ONE = BigInteger.ONE;
    public static final BigInteger MINUS_ONE = new BigInteger("-1");
    public static final BigInteger TWO = new BigInteger("2");
    public static final BigDecimal ZERO_D = BigDecimal.ZERO;
    public static final BigDecimal ONE_D = BigDecimal.ONE;
    public static final BigDecimal MINUS_ONE_D = new BigDecimal("-1");
    public static final BigDecimal TWO_D = new BigDecimal("2");

    private N value;
    private String code;
    private int length = 32;
    private HashMap<String, Object> valuesGiven = new HashMap<>();
    private HashSet<Field> ignoreFields = new HashSet<>();
    private HashSet<Field> ignoreFieldsExtreme = new HashSet<>();
    private HashSet<NumberRepresentable<?>> additionalNRs = new HashSet<>();
    private HashSet<String> ignoreInOutput = new HashSet<>();
    private HashMap<String, String> parDesc = new HashMap<>();
    private HashMap<String, String> parDesc_G = new HashMap<>();
    
    private HashSet<String> hiddenPars = new HashSet<>();
    
    private Numbers repFather;

    private String nameSuffix = "";
    
    /**
     * The name used for this Representable object in the script.
     * 
     * @return  The script name (preferably short).
     */
    public abstract String getScriptPartPrefix();
    protected abstract String calculateCode(N value);
    protected abstract N calculateValue(String code);
    @Override public abstract String toString();

    public NumberRepresentable(Numbers rep) {
        this.parDesc.put(PAR_NAME_VALUE, "The number's value in standard decimal representation, e.g., '34.1289'.");
        this.parDesc_G.put(PAR_NAME_VALUE, "Der Wert der Zahl in Standard-Decimalzahl-Representation, bspw., '34.1289'.");
        this.parDesc.put(PAR_NAME_CODE, "The number's code in the given representation, e.g., plain binary: '110101101010'.");
        this.parDesc_G.put(PAR_NAME_CODE, "Der Code der Zahl in der gewünschten Representation, bspw., als reine Binärzahl: '101001010'.");
        this.parDesc.put(PAR_NAME_LENGTH, "The number's code length.");
        this.parDesc_G.put(PAR_NAME_CODE, "Die Länge des Codes dieser Zahl.");

        this.addIgnoredField("zeroSign", ignoreFieldsExtreme);
        this.addIgnoredField("nameSuffix", ignoreFieldsExtreme);
        
        this.repFather = rep;
    }
    
    /**
     * Add an additional NumberRepresentable to apply parameters to.
     */
    public void addAdditionalNR(NumberRepresentable<?> nr) {
        this.additionalNRs.add(nr);
    }

    public final String getParDescription(String parName, boolean english) {
        return english ? parDesc.get(parName) : parDesc_G.get(parName);
    }

    public void setFromParameters(String parameters) {
        for (NumberRepresentable<?> nr : this.additionalNRs) {
            nr.setFromParameters(parameters);
        }

        LinkedList<ArrayList<String>> nvPairs = RepresentableDefault.extractNVPairs(
                parameters, 
                RepresentableDefault.BEGIN_LITERAL, 
                RepresentableDefault.END_LITERAL, 
                ',', 
                null, 
                null);

        for (ArrayList<String> nameValue : nvPairs) {
            try {
                String name = nameValue.get(0);
                
                if (name.startsWith("*")) {
                    name = name.substring(1);
                    this.hiddenPars.add(name);
                    this.ignoreInOutput.add(name);
                }
                
                String value = nameValue.get(1);
                this.setViaReflections(name, value);
            } catch (Exception e) {
            }
        }
    }

    @SuppressWarnings("unchecked")
    private final void setValue(String val) {
        if (this.myNumClass().isAssignableFrom(BigInteger.class)) {
            this.value = (N) new BigInteger(val);
            return;
        }
        
        if (this.myNumClass().isAssignableFrom(BigDecimal.class)) {
            this.value = (N) new BigDecimal(val);
            return;
        }

        throw new RuntimeException("Could not instantiate value '" + val + "' in " + this.getClass().getSimpleName() + ".");
    }
    
    /**
     * Recalculates and returns the representation code.
     */
    public final String getCode() {
        if (this.code == null) {
            this.code = this.calculateCode(this.value);
        }
        
        return this.code;
    }

    /**
     * Recalculates and returns the value.
     */
    public final N getValue() {
        if (this.value == null) {
            this.value = this.calculateValue(this.code);
        }
        
        return this.value;
    }

    @SuppressWarnings("unchecked")
    public final Object createInstance() {
        NumberRepresentable<N> excessQ;
        try {
            excessQ = this.getClass().getConstructor(Numbers.class).newInstance(this.repFather);
            return excessQ;
        } catch (InstantiationException 
                | IllegalAccessException 
                | IllegalArgumentException 
                | InvocationTargetException 
                | NoSuchMethodException 
                | SecurityException e) {
            throw new RuntimeException(e);
        }
    }

    public void addIgnoredField(String fieldToIgnore) {
        addIgnoredField(fieldToIgnore, this.ignoreFields);
    }
    
    /**
     * These fields will not be used as a script variable in the declaration 
     * area, even if of allowed type (String, int, boolean etc.).
     * 
     * @param fieldToIgnore  The name of the field to ignore.
     */
    public void addIgnoredField(String fieldToIgnore, HashSet<Field> where) {
        try {
            where.add(this.getClass().getDeclaredField(fieldToIgnore));
        } catch (NoSuchFieldException | SecurityException e) {
            try {
                where.add(this.getClass().getSuperclass().getDeclaredField(fieldToIgnore));
            } catch (Exception e2) {
                GlobalVariables.getParameters().logDebug(
                        "Field '" + fieldToIgnore + "' not ignored in '"
                                + this.getClass().getSimpleName() + "': " + e2);
                throw new RuntimeException(e2);
            }
        }
    }
    
    public HashSet<String> getIgnoredFieldsString() {
        HashSet<String> fields = new HashSet<>();
        
        for (Field f : this.ignoreFields) {
            fields.add(f.getName());
        }
        
        return fields;
    }
    
    public void setViaReflections(String fieldName, String value2) {
        String value = value2;
        
        for (NumberRepresentable<?> nr : this.additionalNRs) {
            nr.setViaReflections(fieldName, value2);
        }
        
        // Recursive definition.
        if (value != null && value.contains("[")) {
            value = this.repFather.createNumberRep(value).getValue() + "";
        }

        try {
            Field field = null;
            
            try {
                field = this.getClass().getDeclaredField(fieldName);
            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException e) {
            }
            
            if (field == null) {
                field = this.getClass().getSuperclass().getDeclaredField(fieldName);
            }
            
            if (this.ignoreFields.contains(field)) {
                return;
            }
            
            field.setAccessible(true);
            
            if (value == null) {
                field.set(this, null);
            } else if (field.getType().equals(BigInteger.class)) {
                field.set(this, new BigInteger(value));
            } else if (field.getType().equals(BigDecimal.class)) {
                field.set(this, new BigDecimal(value));
            } else if (field.getType().equals(Double.class) || field.getType().equals(Double.TYPE)) {
                field.set(this, Double.parseDouble(value));
            } else if (field.getType().equals(Boolean.class) || field.getType().equals(Boolean.TYPE)) {
                field.set(this, Boolean.parseBoolean(value));
            } else if (field.getType().equals(Integer.class) || field.getType().equals(Integer.TYPE)) {
                field.set(this, Integer.parseInt(value));
            } else if (field.getType().equals(String.class)) {
                field.set(this, value);
            } else if (field.getType().equals(Number.class)) {
                this.setValue(value);
            }
            
            this.valuesGiven.put(fieldName, value);
        } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
//            e.printStackTrace();
        }
    }

    private boolean isSettable(Field f, boolean ignoreIgnoredFields, boolean includeNumberReps) {
        if (ignoreIgnoredFields && this.ignoreFields.contains(f)
                || this.ignoreFieldsExtreme.contains(f)) {
            return false;
        }
        
        if (java.lang.reflect.Modifier.isStatic(f.getModifiers())
                || ((f.getModifiers() & java.lang.reflect.Modifier.FINAL) == java.lang.reflect.Modifier.FINAL)) {
            return false;
        }
        
        return f.getType().equals(Double.class) || f.getType().equals(Double.TYPE) || f.getType().equals(BigDecimal.class)
                || f.getType().equals(Integer.class) || f.getType().equals(Integer.TYPE)  || f.getType().equals(BigInteger.class)
                || f.getType().equals(Number.class)
                || f.getType().equals(String.class)
                || f.getType().equals(Boolean.class) || f.getType().equals(Boolean.TYPE)
                || (includeNumberReps && NumberRepresentable.class.isAssignableFrom(f.getType()));
    }
    
    public HashMap<Field, Object> getAllSettableFields(boolean ignoreIgnoredFields, boolean considerSubsequentFields, boolean includeNumberreps) {
        HashMap<Field, Object> list = new HashMap<>();

        if (considerSubsequentFields) {
            /*
             * This has to come first as value, code and other fields might have to 
             * get overwritten by fields from {@code this}.
             */
            for (NumberRepresentable<?> nr : this.additionalNRs) {
                list.putAll(nr.getAllSettableFields(ignoreIgnoredFields, considerSubsequentFields, includeNumberreps));
            }
        }
        
        Field[] declaredFields = this.getClass().getDeclaredFields();
        Field[] declaredFieldsSuper = this.getClass().getSuperclass().getDeclaredFields();
        
        for (Field f : declaredFields) {
            if (isSettable(f, ignoreIgnoredFields, includeNumberreps)) {
                list.put(f, this);
            }
        }

        for (Field f : declaredFieldsSuper) {
            if (isSettable(f, ignoreIgnoredFields, includeNumberreps)) {
                list.put(f, this);
            }
        }
        
        return list;
    }

    public final String exportToParameters(boolean ignoreIgnoredFields) {
        String s = "";
        
        HashMap<Field, Object> fields = this.getAllSettableFields(ignoreIgnoredFields, true, false);
        HashSet<String> alreadyThere = new HashSet<>();
        
        for (Field f : fields.keySet()) {
            String fieldName = f.getName();
            
            if (!alreadyThere.contains(fieldName)) {
                Object fieldValue = null;
                
                try {
                    f.setAccessible(true);
                    fieldValue = f.get(fields.get(f));
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
                
                // Only include code and value if given.
                if (fieldName.equals(PAR_NAME_VALUE) && !this.valueGiven()
                        || fieldName.equals(PAR_NAME_CODE) && !this.codeGiven()) {
                    fieldValue = null;
                }
                
                s += fieldValue == null 
                        ? "" 
                        : (this.hiddenPars.contains(fieldName) || !this.valuesGiven.containsKey(fieldName) 
                                ? "*" 
                                : "") + fieldName + "=" + fieldValue + ",";
                
                alreadyThere.add(fieldName);
            }
        }
        
        return s;
    }

    protected boolean codeGiven() {
        return this.valuesGiven.get(PAR_NAME_CODE) != null;
    }
    
    protected boolean valueGiven() {
        return this.valuesGiven.get(PAR_NAME_VALUE) != null;
    }
    
    public void setNameSuffix(String nameSuffix) {
        this.nameSuffix = nameSuffix;
    }

    private String getParsLaTeX(HashSet<String> ignoreFields, HashMap<String, Object> dontIgnoreFields, String name, boolean showOptions) {
        String plainPars = "";

        if (showOptions) {
            if (this.getLength() == 32) {
                ignoreFields.add(PAR_NAME_LENGTH);
            }
    
            ignoreFields.add(PAR_NAME_CODE);
            ignoreFields.add(PAR_NAME_VALUE);
            
            ignoreFields.addAll(ignoreInOutput);
    
            ignoreFields.removeAll(dontIgnoreFields.keySet());
            
            HashSet<String> alreadyThere = new HashSet<>();
            HashMap<Field, Object> allSettableFields = this.getAllSettableFields(true, true, false);
            
            for (Field f : allSettableFields.keySet()) {
                String fieldName = f.getName();
                
                if (!alreadyThere.contains(fieldName)) {
                    Object fieldValue = this.valuesGiven.get(fieldName);
                    
                    if (fieldValue == null) {
                        try {
                            f.setAccessible(true);
                            Object object = allSettableFields.get(f);
                            
                            Object specificObject = dontIgnoreFields.get(fieldName);
                            if (specificObject != null) {
                                object = specificObject;
                            }
                            
                            fieldValue = f.get(object);
                        } catch (IllegalArgumentException | IllegalAccessException
                                | SecurityException e) {
                        }
                    }
                    
                    plainPars += fieldValue != null && !ignoreFields.contains(fieldName) 
                            ? ", " + fieldName + "=" + fieldValue 
                            : "";
                    
                    alreadyThere.add(fieldName);
                }
            }
    
            if (plainPars.startsWith(", ")) {
                plainPars = plainPars.substring(2);
            }
        }

        String numRepString = name + nameSuffix + (plainPars.isEmpty() ? "" : "\\left[" + plainPars + "\\right]");

        return numRepString;
    }

    public String createCompleteLaTeXString(
            HashSet<String> ignoreFields2, 
            String name) {
        return createCompleteLaTeXString(ignoreFields2, new HashMap<>(), name);
    }

    public String createCompleteLaTeXString(
            HashSet<String> ignoreFields, 
            HashMap<String, Object> dontIgnoreFields, 
            String name) {
        ignoreFields.addAll(this.getIgnoredFieldsString());
        
        N val = this.value;
        String repNameString = null;
        boolean infinity = false;
        boolean nan = false;
        
        if (this.codeGiven()) {
            try {
                BigDecimalSpecial dec = (BigDecimalSpecial) this.getValue();
                if (dec.isInfinity()) {
                    infinity = true;
                }
                if (dec.isNaN()) {
                    nan = true;
                }
            } catch (Exception e) {
            }
        }
        
        if (this.codeGiven() && this.valueGiven()) {
            this.value = null;
            boolean ok = val.equals(this.getValue());
            boolean approx = false;
            
            try {
                double epsilon = 0.9;
                BigDecimal decVal1 = (BigDecimal) val, decVal2 = (BigDecimal) this.getValue();
                
                ok = decVal1.compareTo(decVal2) == 0;
                
                if (decVal1.subtract(decVal2).abs().compareTo(new BigDecimal(epsilon)) < 0) {
                    approx = true;
                }
            } catch (Exception e) {}
                    
            String representation2 = this.getCode();
            int indexOf = representation2.indexOf(TOO_LONG_MARKER);
            
            String valSpecial = valSpecial(val, infinity, nan);
            
            repNameString = this.getParsLaTeX(ignoreFields, dontIgnoreFields, name, this.repFather.isShowOptions());
            repNameString += "(" + this.zeroSign(val) + valSpecial + ") "
                    + (ok ? "\\stackrel{\\sqrt{}}{=}" : (approx ? "\\approx" : "\\neq"))
                    + " " + "\\mbox{" 
                    + (indexOf >= 0 ? "\\begin{color}{red}" + representation2.substring(0, indexOf) + "\\end{color}" : "")
                    + representation2.substring(indexOf + 1)
                    + "}";
        } else if (this.codeGiven()) {
            N value2 = this.getValue();
            repNameString = this.getParsLaTeX(ignoreFields, dontIgnoreFields, name, this.repFather.isShowOptions());
            String valSpecial = valSpecial(value2, infinity, nan);

            String formattedCode = format(this.code);
            
            repNameString = "\\mbox{" + formattedCode + "}\\ensuremath{_{" + repNameString + "}} = " + this.zeroSign(value2) + valSpecial + "";
        } else if (this.valueGiven()) {
            String representation2 = this.getCode();
            int indexOf = representation2.indexOf(TOO_LONG_MARKER);
            repNameString = this.getParsLaTeX(ignoreFields, dontIgnoreFields, name, this.repFather.isShowOptions());

            repNameString += "(" + this.zeroSign(val) + val + ") = " 
                    + "\\mbox{"
                    + (indexOf >= 0 ? "\\begin{color}{red}" + representation2.substring(0, indexOf) + "\\end{color}" : "")
                    + representation2.substring(indexOf + 1)
                    + "}";
        }
        
        return "\\mbox{$" + repNameString + "$}";
    }

    private String format(String code2) {
        String code = code2;
        
        for (Integer gap : this.gaps) {
            code = code.substring(0, gap) + "\\ " + code.substring(gap);
        }
        
        return code;
    }
    private String valSpecial(N val, boolean infinity, boolean nan) {
        return infinity
                ? "\\infty"
                : (nan
                        ? "\\mbox{NaN}"
                        : val.toString());
    }
    
    public int getLength() {
        return length;
    }

    public Numbers getRepFather() {
        return this.repFather;
    }

    private String zeroSign(Number n) {
        try {
            BigInteger bi = (BigInteger) n;
            return bi.signum() == 0 ? zeroSign : "";
        } catch (Exception e) {}

        try {
            BigDecimal bd = (BigDecimal) n;
            return bd.signum() == 0 ? zeroSign : "";
        } catch (Exception e) {}
        
        return "";
    }

    private String zeroSign = "";
    
    protected void setZeroSign(String sign) {
        zeroSign = sign;
    }

    private TreeSet<Integer> gaps = new TreeSet<>((c1, c2) -> c2.compareTo(c1));
    
    protected void addGap(int num) {
        gaps.add(num);
    }
    
    public final String visualisationScript(GraphViz gv2, String hookNode, String nameSpace) {
        GraphViz gv = gv2;
        HashMap<Field, Object> allSettableFields = this.getAllSettableFields(false, false, true);
        allSettableFields.keySet().removeAll(this.ignoreFieldsInVisualizationScript());
        
        String name = "name";
        if (hookNode != null) {
            name = hookNode;
        }

        if (gv2 == null) {
            gv = new GraphViz("", null);
            gv.addln(GraphViz.startDigraph());
    
            gv.addln(name + " [label=<" 
                    + (this.codeGiven() ? this.getCode() : this.getValue()) 
                    + " (" + this.getClass().getSimpleName() + ")"
                    + "> shape=\"rectangle\"];");
        }
        
        HashSet<String> alreadyThere = new HashSet<>();
        
        if (this.codeGiven()) {
            alreadyThere.add(PAR_NAME_CODE);
        } else {
            alreadyThere.add(PAR_NAME_VALUE);
        }
        
        int count = 0;
        for (Field field : allSettableFields.keySet()) {
            String fieldName = field.getName();
            String fieldNameVar = "A" + nameSpace + count;
            String fieldValueVar = "B" + nameSpace + count;

            if (NumberRepresentable.class.isAssignableFrom(field.getType())) {
                field.setAccessible(true);
                try {
                    NumberRepresentable<?> nr = (NumberRepresentable<?>) field.get(this);
                    
                    fieldNameVar += "x";
                    fieldName = "<B>" + fieldName.toUpperCase() + "</B>: " + (nr.codeGiven() ? nr.getCode() : nr.getValue()) 
                            + " (" + field.getType().getSimpleName() + ")";
                    gv.addln(fieldNameVar + " [label=<" + fieldName + "> shape=\"rectangle\" style=filled fillcolor=\"" + "white" + "\"];");
                    gv.addln(name + " ->" + fieldNameVar + ";");
                    
                    nr.visualisationScript(gv, fieldNameVar, nameSpace + StaticMethods.removeWhitespaces(fieldNameVar));
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    e.printStackTrace();
                }
            } else {
                if (!alreadyThere.contains(fieldName)) {
                    Object fieldValue = null;
                    field.setAccessible(true);
                    Object object = allSettableFields.get(field);
                    
                    try {
                        fieldValue = field.get(object);
                    } catch (IllegalArgumentException | IllegalAccessException e) {
                    }
                            
                    if (fieldValue != null) {
                        String shape = "diamond";
                        
                        String colorName = "lightgrey";
                        String colorValue = "lightgrey";
                        if (Boolean.class.isAssignableFrom(fieldValue.getClass())
                                || Boolean.TYPE.isAssignableFrom(fieldValue.getClass())) {
                            colorValue = (boolean) fieldValue ? "green" : "red";
                            colorName = colorValue;
                        }
                        
                        if (fieldName.equals(PAR_NAME_VALUE) || fieldName.equals(PAR_NAME_CODE)) {
                            colorValue = "white";
                            colorName = "white";
                            shape = "rectangle";
                        }
                        
                        if (fieldName.toLowerCase().contains("length")) {
                            colorName = "yellow";
                            colorValue = "yellow";
                        }
                        
                        if (fieldName.equals(ExcessQ.PAR_NAME_Q)) {
                            colorName = "orange";
                            colorValue = "orange";
                        }
    
                        if (fieldName.equals(ExcessQ.PAR_NAME_RADIX)) {
                            colorName = "orange";
                            colorValue = "orange";
                        }
                        
                        gv.addln(fieldNameVar + " [label=\"" + fieldName + "\" shape=\"ellipse\" style=filled fillcolor=\"" + colorName + "\"];");
                        gv.addln(fieldValueVar + " [label=\"" + fieldValue + "\" shape=\"" + shape + "\" style=filled fillcolor=\"" + colorValue + "\"];");
        
                        gv.addln(name + " ->" + fieldNameVar + ";");
                        gv.addln(fieldNameVar + "->" + fieldValueVar + ";");
                        
                        count++;
                        alreadyThere.add(fieldName);
                    }
                }
            }
        }
        
        if (gv2 == null) {
            gv.addln(GraphViz.endGraph());
        }
        
        return gv.getPlainPDFScript();
    }

    public abstract HashSet<Field> ignoreFieldsInVisualizationScript();
    
    public abstract Class<N> myNumClass();
    public abstract String getRepName();
    public abstract String getRepName_G();
    
    public abstract MethodWrapper dynMethodCreateByCode();
    public abstract MethodWrapper dynMethodCreateByValue();

    @Override
    public RepresentableAsPDF getRepresentableAsPDF() {
        return repFather;
    }
}
