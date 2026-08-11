/*
 * File name:        Complement.java (package veryFastPDF.algorithms.numberRep.representations)
 * Author(s):        Lukas König
 * Java version:     8.0 (at generation time)
 * Generation date:  11.02.2016 (10:02:27)
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
import java.math.BigInteger;
import java.util.HashSet;

import veryFastPDF.algorithms.numberRep.HelperMethods;
import veryFastPDF.algorithms.numberRep.NumberRepresentable;
import veryFastPDF.algorithms.numberRep.Numbers;
import veryFastPDF.script.ConversionMethod;
import veryFastPDF.script.MethodWrapper;
import veryFastPDF.script.RepresentableAsPDF;

/**
 * @author Lukas König
 */
public class Complement extends NumberRepresentable<BigInteger> {

    private static final String PAR_NAME_COMPLEMENT_TYPE = "complementType";
    
    private ExcessQ intValue;
    private BigInteger complementType = BigInteger.ONE;
    
    public Complement(Numbers repFather) {
        super(repFather);
        this.intValue = new ExcessQ(repFather);
        this.addAdditionalNR(intValue);
    }
    
    @Override
    public String getScriptPartPrefix() {
        return "complement";
    }
    
    @Override
    protected String calculateCode(BigInteger value) {
        intValue.setViaReflections(PAR_NAME_CODE, null);
        intValue.setViaReflections(ExcessQ.PAR_NAME_RADIX, "2");
        intValue.setViaReflections(PAR_NAME_VALUE, value.abs().toString());
        String code = intValue.getCode();
        
        if (value.compareTo(BigInteger.ZERO) < 0) {
            code = HelperMethods.complement(code);
            
            if (this.complementType.equals(TWO)) {
                code = HelperMethods.increment(code);
            }
        }
        
        return code;
    }

    @Override
    protected BigInteger calculateValue(String code2) {
        intValue.setViaReflections(PAR_NAME_VALUE, null);
        intValue.setViaReflections(ExcessQ.PAR_NAME_RADIX, "2");

        String code = code2;
        BigInteger neg = ONE;
        BigInteger sub = ZERO;
        
        if (code.startsWith("1")) {
            neg = MINUS_ONE;
            if (complementType.equals(TWO)) {
                sub = ONE;
            }
            code = HelperMethods.complement(code);
        }
        
        intValue.setViaReflections(PAR_NAME_CODE, code);
        
        return intValue.getValue().multiply(neg).subtract(sub);
    }

    @Override
    public String toString() {
        intValue.setViaReflections(ExcessQ.PAR_NAME_RADIX, "2");
        HashSet<String> ignoreFields = new HashSet<>();
        ignoreFields.add(PAR_NAME_COMPLEMENT_TYPE);
        
        return this.createCompleteLaTeXString(
                ignoreFields, 
                (this.complementType.intValue() == 2 ? "twos" : "ones") 
                    + this.getScriptPartPrefix() + "-" + intValue.getCompleteLaTeXName());
    }

    @Override
    public Class<BigInteger> myNumClass() {
        return BigInteger.class;
    }

    @Override
    public String getRepName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getRepName_G() {
        return "Komplementdarstellung";
    }

    @Override
    public MethodWrapper dynMethodCreateByCode() {
        try {
            Method method = this.getClass().getDeclaredMethod("createNewNumberByCode", String.class, Integer.TYPE, Integer.TYPE);
            
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
            Method method = this.getClass().getDeclaredMethod("createNewNumberByValue", String.class, Integer.TYPE, Integer.TYPE);
            
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
    public String createNewNumberByCode(String code, int length, int complementType) {
        Complement c = new Complement(this.getRepFather());
        c.setViaReflections(PAR_NAME_CODE, code);
        c.setViaReflections(PAR_NAME_LENGTH, length + "");
        c.setViaReflections(PAR_NAME_COMPLEMENT_TYPE, complementType + "");
        
        this.getRepFather().addNumber(c);
        
        return this.getRepFather().createScriptFromInstance();
    }
    
    @ConversionMethod(plainText = false)
    public String createNewNumberByValue(String value, int length, int complementType) {
        Complement c = new Complement(this.getRepFather());
        c.setViaReflections(PAR_NAME_VALUE, value);
        c.setViaReflections(PAR_NAME_LENGTH, length + "");
        c.setViaReflections(PAR_NAME_COMPLEMENT_TYPE, complementType + "");
        
        this.getRepFather().addNumber(c);
        
        return this.getRepFather().createScriptFromInstance();
    }

    @Override
    public HashSet<Field> ignoreFieldsInVisualizationScript() {
        HashSet<Field> fields = new HashSet<>();
        
//        try {
//            fields.add(FixedPointRational.class.getSuperclass().getDeclaredField(PAR_NAME_LENGTH));
//        } catch (NoSuchFieldException | SecurityException e) {
//            e.printStackTrace();
//        }
        
        return fields;
    }
}
