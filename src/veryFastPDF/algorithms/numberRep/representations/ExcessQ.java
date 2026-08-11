/*
 * File name:        ExcessQ.java (package veryFastPDF.algorithms.numberRep)
 * Author(s):        Lukas König
 * Java version:     8.0 (at generation time)
 * Generation date:  04.02.2015 (22:25:46)
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

import veryFastPDF.algorithms.numberRep.NumberRepresentable;
import veryFastPDF.algorithms.numberRep.Numbers;
import veryFastPDF.script.ConversionMethod;
import veryFastPDF.script.MethodWrapper;
import veryFastPDF.script.RepresentableAsPDF;

/**
 * Represents an excess q representation with arbitrary radix. 
 * Use q=0 for the plain representation.
 * 
 * @author Lukas König
 */
public class ExcessQ extends NumberRepresentable<BigInteger> {

    public ExcessQ(Numbers rep) {
        super(rep);
    }

    public static final String EXCESSQ_PREFIX = "excessq";
    public static final String PAR_NAME_Q = "q";
    public static final String PAR_NAME_RADIX = "radix";
    
    private BigInteger q = BigInteger.ZERO;
    private int radix = 2;

    @Override
    public String getScriptPartPrefix() {
        return EXCESSQ_PREFIX;
    }

    @Override
    protected BigInteger calculateValue(String fromRepresentation) {
        BigInteger val = new BigInteger(fromRepresentation.replace(TOO_LONG_MARKER, ""), getRadix());
        return val.subtract(q);
    }

    @Override
    protected String calculateCode(BigInteger val) {
        BigInteger value = val.add(q);
        String result = value.toString(getRadix());
        
        while (result.length() < this.getLength()) {
            result = "0" + result;
        }
        
        if (result.length() > this.getLength()) {
            result = result.substring(0, result.length() - this.getLength()) + TOO_LONG_MARKER + result.substring(result.length() - this.getLength());
        }
        
        return result;
    }
    
    @Override
    public void setFromParameters(String parameters) {
        this.q = BigInteger.ZERO;
        this.radix = 2;
        super.setFromParameters(parameters);
    }
    
    @Override
    public String toString() {
        HashSet<String> ignoreFields = new HashSet<>();
        ignoreFields.add(PAR_NAME_Q);

        String completeLaTeXName = getCompleteLaTeXName();
        
        if (this.getRadix() == 2 || !completeLaTeXName.startsWith("excess")) {
            ignoreFields.add("radix");
        }
        
        return createCompleteLaTeXString(ignoreFields, completeLaTeXName);
    }

    public String getCompleteLaTeXName() {
        String name = "excess";
        String realQ = this.q.toString();
        
        if (this.q.equals(BigInteger.ZERO)) {
            switch (this.getRadix()) {
                case 1:  name = "unary"; break;
                case 2:  name = "binary"; break;
                case 3:  name = "ternary"; break;
                case 4:  name = "quaternary"; break;
                case 5:  name = "quinary"; break;
                case 6:  name = "senary"; break;
                case 7:  name = "septenary"; break;
                case 8:  name = "octal"; break;
                case 9:  name = "novenary"; break;
                case 10: name = "decimal"; break;
                case 16: name = "hexadecimal"; break;
                default: name = "radix\\,{" + this.getRadix() + "}"; break;
            }
            
            if (!name.equals("excess")) {
                realQ = "";
            }
        }

        return "" + name + "\\ensuremath{_{" + realQ + "}}";
    }

    public int getRadix() {
        return radix;
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
        return "Exzess-Q";
    }

    @Override
    public MethodWrapper dynMethodCreateByCode() {
        try {
            Method method = this.getClass().getDeclaredMethod("createNewNumberByCode",
                    String.class, 
                    Integer.TYPE, 
                    Long.TYPE,
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
            Method method = this.getClass().getDeclaredMethod(
                    "createNewNumberByValue", 
                    String.class, 
                    Integer.TYPE, 
                    Long.TYPE,
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
    public String createNewNumberByCode(String code, int length, long q, int radix) {
        ExcessQ excessq = new ExcessQ(this.getRepFather());
        excessq.setViaReflections(PAR_NAME_CODE, code);
        excessq.setViaReflections(PAR_NAME_LENGTH, length + "");
        excessq.setViaReflections(PAR_NAME_Q, q + "");
        excessq.setViaReflections(PAR_NAME_RADIX, radix + "");
        
        this.getRepFather().addNumber(excessq);
        
        return this.getRepFather().createScriptFromInstance();
    }
    
    @ConversionMethod(plainText = false)
    public String createNewNumberByValue(String value, int length, long q, int radix) {
        ExcessQ excessq = new ExcessQ(this.getRepFather());
        excessq.setViaReflections(PAR_NAME_VALUE, value);
        excessq.setViaReflections(PAR_NAME_LENGTH, length + "");
        excessq.setViaReflections(PAR_NAME_Q, q + "");
        excessq.setViaReflections(PAR_NAME_RADIX, radix + "");
        
        this.getRepFather().addNumber(excessq);
        
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
