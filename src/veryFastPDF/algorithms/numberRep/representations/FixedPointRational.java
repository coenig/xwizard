/*
 * File name:        FixedPointRational.java (package veryFastPDF.algorithms.numberRep)
 * Author(s):        Lukas König
 * Java version:     8.0 (at generation time)
 * Generation date:  06.02.2015 (21:47:23)
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

package veryFastPDF.algorithms.numberRep.representations;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;

import veryFastPDF.algorithms.numberRep.NumberRepresentable;
import veryFastPDF.algorithms.numberRep.Numbers;
import veryFastPDF.script.ConversionMethod;
import veryFastPDF.script.MethodWrapper;
import veryFastPDF.script.RepresentableAsPDF;

/**
 * @author Lukas König
 */
public class FixedPointRational extends NumberRepresentable<BigDecimal> {

    public static final String PAR_NAME_DECIMAL = "afterDec";
    public static final String PAR_NAME_DECIMAL_VAL = "afterDecVal";

    private Integer afterDecVal = 5;
    private Integer afterDec = 5;
    private ExcessQ intValue;
    
    public FixedPointRational(Numbers repFather) {
        super(repFather);
        this.intValue = new ExcessQ(repFather);
        this.addAdditionalNR(intValue);
    }

    @Override
    public String getScriptPartPrefix() {
        return "fixedpoint";
    }

    @Override
    protected String calculateCode(BigDecimal value) {
        BigDecimal radix = new BigDecimal(intValue.getRadix() + "");
        BigDecimal multiplicand = radix.pow(this.afterDec);
        BigDecimal calc = value.multiply(multiplicand);
        String string = calc.toString();
        int indexOf = string.indexOf(".");

        if (indexOf < 0) {
            indexOf = string.length();
        }
        
        intValue.setViaReflections(PAR_NAME_VALUE, string.substring(0, indexOf));
        intValue.setViaReflections(PAR_NAME_CODE, null);

        String code = intValue.getCode();
        int pos = code.length() - afterDec;
        
        if (code.indexOf(NumberRepresentable.TOO_LONG_MARKER) >= pos) {
            pos--;
        }
        
        return code.substring(0, pos) + "." + code.substring(pos);
    }

    @Override
    protected BigDecimal calculateValue(String code) {
        intValue.setViaReflections(PAR_NAME_CODE, code.replace(".", ""));
        intValue.setViaReflections(PAR_NAME_VALUE, null);
        
        int indexOf = code.indexOf(".");
        if (indexOf < 0) {
            indexOf = code.length() - 1;
        }
        int numBitsAfterPoint = code.length() - indexOf - 1;
        
        BigDecimal radix = new BigDecimal(intValue.getRadix() + "");
        BigDecimal divisor = radix.pow(numBitsAfterPoint);
        return new BigDecimal(intValue.getValue().toString()).divide(divisor, afterDecVal, RoundingMode.HALF_UP);
    }

    @Override
    public String toString() {
        HashSet<String> ignoreFields = new HashSet<>();
        return super.createCompleteLaTeXString(ignoreFields, this.getScriptPartPrefix() + "-" + intValue.getCompleteLaTeXName());
    }

    @Override
    public Class<BigDecimal> myNumClass() {
        return BigDecimal.class;
    }

    @Override
    public String getRepName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getRepName_G() {
        return "Festpunktdarstellung";
    }

    @Override
    public MethodWrapper dynMethodCreateByCode() {
        try {
            Method method = this.getClass().getDeclaredMethod("createNewNumberByCode",
                    String.class, 
                    Integer.TYPE, 
                    Long.TYPE,
                    Integer.TYPE,
                    Integer.TYPE,
                    Integer.TYPE);
            
            MethodWrapper mw = new MethodWrapper(
                    method,
                    (Class<? extends RepresentableAsPDF>) null, // Target script class. Important to set correctly!
                    this,
                    null,
                    null,
                    null,
                    null,
                    1);
            
            return mw;
        } catch (NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public MethodWrapper dynMethodCreateByValue() {
        try {
            Method method = this.getClass().getDeclaredMethod("createNewNumberByValue",
                    String.class, 
                    Integer.TYPE, 
                    Long.TYPE,
                    Integer.TYPE,
                    Integer.TYPE,
                    Integer.TYPE);
            
            MethodWrapper mw = new MethodWrapper(
                    method,
                    (Class<? extends RepresentableAsPDF>) null, // Target script class. Important to set correctly!
                    this,
                    null,
                    null,
                    null,
                    null,
                    1);
            
            return mw;
        } catch (NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }
    
    @ConversionMethod(plainText = false)
    public String createNewNumberByCode(String code, int length, long q, int radix, int numDecimals, int numDecimalsValue) {
        FixedPointRational fixedRat = new FixedPointRational(this.getRepFather());
        
        fixedRat.setViaReflections(PAR_NAME_CODE, code);
        fixedRat.setViaReflections(PAR_NAME_LENGTH, length + "");
        fixedRat.setViaReflections(ExcessQ.PAR_NAME_Q, q + "");
        fixedRat.setViaReflections(ExcessQ.PAR_NAME_RADIX, radix + "");
        fixedRat.setViaReflections(PAR_NAME_DECIMAL, numDecimals + "");
        fixedRat.setViaReflections(PAR_NAME_DECIMAL_VAL, numDecimalsValue + "");
        
        this.getRepFather().addNumber(fixedRat);
        
        return this.getRepFather().createScriptFromInstance();
    }
    
    @Override
    public void setFromParameters(String parameters) {
        super.setFromParameters(parameters);
    }
    
    @ConversionMethod(plainText = false)
    public String createNewNumberByValue(String value, int length, long q, int radix, int numDecimals, int numDecimalsValue) {
        FixedPointRational fixedRat = new FixedPointRational(this.getRepFather());
        fixedRat.setViaReflections(PAR_NAME_VALUE, value);
        fixedRat.setViaReflections(PAR_NAME_LENGTH, length + "");
        fixedRat.setViaReflections(ExcessQ.PAR_NAME_Q, q + "");
        fixedRat.setViaReflections(ExcessQ.PAR_NAME_RADIX, radix + "");
        fixedRat.setViaReflections(PAR_NAME_DECIMAL, numDecimals + "");
        fixedRat.setViaReflections(PAR_NAME_DECIMAL_VAL, numDecimalsValue + "");
        
        this.getRepFather().addNumber(fixedRat);
        
        return this.getRepFather().createScriptFromInstance();
    }

    @Override
    public HashSet<Field> ignoreFieldsInVisualizationScript() {
        HashSet<Field> fields = new HashSet<>();
        
        try {
            fields.add(this.getClass().getSuperclass().getDeclaredField(PAR_NAME_LENGTH));
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        
        return fields;
    }
    
    @Override
    public void setViaReflections(String fieldName, String value2) {
        super.setViaReflections(fieldName, value2);
        
        if (PAR_NAME_DECIMAL.equals(fieldName) || PAR_NAME_CODE.equals(fieldName)) {
            intValue.setViaReflections(PAR_NAME_LENGTH, this.getCode().replace(".",  "").length() + "");
        }
    }
}
