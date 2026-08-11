/*
 * File name:        FloatingPointRational.java (package veryFastPDF.algorithms.numberRep)
 * Author(s):        Lukas König
 * Java version:     8.0 (at generation time)
 * Generation date:  04.02.2015 (22:12:15)
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
import java.math.BigInteger;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.HashSet;

import veryFastPDF.algorithms.numberRep.BigDecimalSpecial;
import veryFastPDF.algorithms.numberRep.NumberRepresentable;
import veryFastPDF.algorithms.numberRep.Numbers;
import veryFastPDF.script.ConversionMethod;
import veryFastPDF.script.MethodWrapper;
import veryFastPDF.script.RepresentableAsPDF;

/**
 * @author Lukas König
 */
public class FloatingPointRational extends NumberRepresentable<BigDecimalSpecial> {

    public static final String PAR_NAME_MANTISSA_LENGTH = "mantissaLength";
    public static final String PAR_NAME_CHARACTERISTIC_LENGTH = "characteristicLength";
    public static final String PAR_NAME_DECIMAL_VAL = "afterDecVal";
    public static final String PAR_NAME_AUTO_Q = "autoQ";
    public static final String PAR_NAME_IEEE754 = "ieee754";
    public static final String PAR_NAME_LENGTHS_FIXED = "lengthsFixed";
    public static final String PAR_NAME_POSITIVE = "positive";

    // Reflected fields.
    private boolean autoQ = true;
    private boolean ieee754 = true;
    private int characteristicLength = 8; // Length of mantissa without one bit for the sign.
    private int mantissaLength = 32 - characteristicLength - 1; // Length of mantissa without one bit for the sign.
    private Integer afterDecVal = 20;

    // Fields reflected for recursive output only.
    private ExcessQ exponent;
    private FixedPointRational mantissa;

    // Unreflected fields.
    private char partitioner = ':';
    
    @SuppressWarnings("unused")
    private boolean positive = true; // TODO
    
    public FloatingPointRational(Numbers repFather) {
        super(repFather);
    }

    private void reset(Numbers repFather) {
        this.addIgnoredField(PAR_NAME_POSITIVE);
        this.addIgnoredField("partitioner");
        this.addIgnoredField("mantissa");
        this.addIgnoredField("exponent");
        this.addIgnoredField(PAR_NAME_LENGTHS_FIXED);
        
        this.exponent = new ExcessQ(repFather);
        this.mantissa = new FixedPointRational(repFather);
//        this.mantissa.addIgnoredField("afterDec");
        
        this.addAdditionalNR(exponent);
        this.addAdditionalNR(mantissa);
    }
    
    @Override
    public void setFromParameters(String parameters) {
        this.reset(this.getRepFather());
        super.setFromParameters(parameters);
    }
    
    @Override
    public String getScriptPartPrefix() {
        return "floatingpoint";
    }

    @Override
    protected String calculateCode(BigDecimalSpecial value) {
        manageSubordinatePars(false);
        
        return null;
    }

    @Override
    protected BigDecimalSpecial calculateValue(String code) {
        manageSubordinatePars(true);
        BigDecimalSpecial result;
        BigDecimal sign = code.startsWith("0") ? ONE_D : MINUS_ONE_D;
        boolean infinity = false;
        boolean nan = false;

        positive = code.startsWith("0");
        
        boolean normalize = this.ieee754 || this.exponent.getRadix() == 2;
        String characteristicCode = code.substring(1, characteristicLength + 1);

        if (characteristicCode.replace("0", "").isEmpty()) { // Denormalize.
            this.setNameSuffix("-denorm.");
            normalize = false;
        }

        String plainMantissaCode = code.substring(characteristicLength + 1);
        
        // Normalize binary.
        String mantissaCode = normalize
                ? "1." + code.substring(1 + characteristicLength)
                : code.charAt(characteristicLength + 1) + "." + code.substring(2 + characteristicLength);
        
        this.exponent.setViaReflections(PAR_NAME_CODE, characteristicCode);
        this.mantissa.setViaReflections(PAR_NAME_CODE, mantissaCode);
        this.mantissa.setViaReflections(FixedPointRational.PAR_NAME_DECIMAL, "" + (this.mantissaLength - (ieee754 ? 0 : 1)));
                
        if (this.ieee754 && code.substring(1).replace("0", "").isEmpty()) {
            // Special ieee754 value: 0
            result = new BigDecimalSpecial(BigDecimal.ZERO);
        } else if (this.ieee754 && characteristicCode.replace("1", "").isEmpty()) {
            if (plainMantissaCode.replace("0", "").isEmpty()) {
                // Special case infinity.
                result = new BigDecimalSpecial(BigDecimal.ZERO);
                infinity = true;
            } else {
                // Special case NaN.
                result = new BigDecimalSpecial(BigDecimal.ZERO);
                nan = true;
            }
        } else {
            int exp = this.exponent.getValue().intValue();
            BigDecimal mant = this.mantissa.getValue();
            BigDecimal multiplicand;
            
            BigDecimal pow = new BigDecimal(this.exponent.getRadix() + "").pow(Math.abs(exp));
            
            if (exp >= 0) {
                multiplicand = pow;
            } else {
                multiplicand = BigDecimal.ONE.divide(pow, 100, RoundingMode.HALF_UP);
            }
            
            result = new BigDecimalSpecial(mant.multiply(multiplicand));
        }
        
        result = new BigDecimalSpecial(result.setScale(afterDecVal + result.scale() - result.precision() + 1, BigDecimal.ROUND_DOWN).stripTrailingZeros());
        result = new BigDecimalSpecial(result.multiply(sign));

        this.setZeroSign(sign.signum() > 0 ? "+" : "-");
        result.setInfinity(infinity);
        result.setNaN(nan);
        
        this.addGap(this.characteristicLength + 1);
        this.addGap(1);
        
        return result;
    }

    private boolean lengthsFixed = false;
    
    @Override
    public void setViaReflections(String fieldName, String value2) {
        if (lengthsFixed 
                && (fieldName.equals(PAR_NAME_CHARACTERISTIC_LENGTH)
                        || fieldName.equals(PAR_NAME_MANTISSA_LENGTH)
                        || fieldName.equals(PAR_NAME_LENGTH))) {
            return;
        }
        
        super.setViaReflections(fieldName, value2);
        
        if (fieldName.equals(PAR_NAME_CODE)) {
            String code2 = this.getCode();

            if (this.getCode().charAt(1) == partitioner) {
                code2 = this.getCode().charAt(0) + this.getCode().substring(2);
            }

            String code = code2.replace(partitioner + "", "");
            
            super.setViaReflections(PAR_NAME_CODE, code);
            
            if (code2.contains(partitioner + "")) {
                String[] codeSplit = code2.split("" + partitioner);
                String characteristicCode = codeSplit[0].substring(1);
                String mantissaCode = codeSplit[1];
                characteristicLength = characteristicCode.length();
                mantissaLength = mantissaCode.length();

                // This has to come first, as it overwrites lengths at subsequent levels, too.
                super.setViaReflections(PAR_NAME_LENGTH, "" + (characteristicLength + mantissaLength + 1));
                
                this.exponent.setViaReflections(NumberRepresentable.PAR_NAME_CODE, "" + characteristicCode);
                this.exponent.setViaReflections(NumberRepresentable.PAR_NAME_LENGTH, "" + characteristicLength);
                
                this.mantissa.setViaReflections(NumberRepresentable.PAR_NAME_CODE, "" + mantissaCode);
                this.mantissa.setViaReflections(NumberRepresentable.PAR_NAME_LENGTH, "" + (mantissaLength + 1));
                this.mantissa.setViaReflections(FixedPointRational.PAR_NAME_DECIMAL, "" + mantissaLength);
                
                this.lengthsFixed = true;
            }
        }
    }
    
    @Override
    public String toString() {
        HashSet<String> ignoreFields = new HashSet<>();
        HashMap<String, Object> dontIgnoreFields = getNotIgnoredFields();
        dontIgnoreFields.clear();
        
        ignoreFields.add(PAR_NAME_IEEE754);
        
        String scriptPartPrefix = this.getScriptPartPrefix() + "-" + this.exponent.getCompleteLaTeXName();
        
        if (ieee754) {
            scriptPartPrefix = PAR_NAME_IEEE754;
            ignoreFields.add(PAR_NAME_AUTO_Q);
        }

        
        return super.createCompleteLaTeXString(ignoreFields, dontIgnoreFields, scriptPartPrefix);
    }

    private HashMap<String, Object> getNotIgnoredFields() {
        HashMap<String, Object> dontIgnoreFields = new HashMap<>();
        dontIgnoreFields.put(ExcessQ.PAR_NAME_Q, this.exponent);
        return dontIgnoreFields;
    }

    @Override
    public Class<BigDecimalSpecial> myNumClass() {
        return BigDecimalSpecial.class;
    }

    private void manageSubordinatePars(boolean value) {
        this.setNameSuffix("");

        if (value) {
            this.mantissa.setViaReflections(PAR_NAME_VALUE, null);
            this.exponent.setViaReflections(PAR_NAME_VALUE, null);
        } else {
            this.mantissa.setViaReflections(PAR_NAME_CODE, null);
            this.exponent.setViaReflections(PAR_NAME_CODE, null);
        }

        this.mantissa.setViaReflections(ExcessQ.PAR_NAME_Q, "0");
        
        if (this.ieee754) {
            this.exponent.setViaReflections(ExcessQ.PAR_NAME_RADIX, "2");
            this.mantissa.setViaReflections(ExcessQ.PAR_NAME_RADIX, "2");
            this.setViaReflections(PAR_NAME_AUTO_Q, "true");
        }
        
        if (this.autoQ) {
            BigInteger q = new BigInteger(this.exponent.getRadix() + "").pow(characteristicLength).divide(TWO).subtract(ONE);
            this.exponent.setViaReflections(ExcessQ.PAR_NAME_Q, q.toString());
        }
    }

    @Override
    public String getRepName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public String getRepName_G() {
        return "Gleitpunktdarstellung";
    }

    @Override
    public MethodWrapper dynMethodCreateByCode() {
        try {
            Method method = this.getClass().getDeclaredMethod(
                    "createNewNumberByCode", 
                    String.class, 
                    Boolean.TYPE,
                    Boolean.TYPE,
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

            mw.setUseInWebProductiveMode(true);

            return mw;
        } catch (NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }
    
    @Override
    public MethodWrapper dynMethodCreateByValue() {
        try {
            Method method = this.getClass().getDeclaredMethod("createNewNumberByValue", String.class, Integer.TYPE, Boolean.TYPE);
            
            MethodWrapper mw = new MethodWrapper(
                    method,
                    (Class<? extends RepresentableAsPDF>) null, // Target script class. Important to set correctly!
                    this,
                    null,
                    null,
                    null,
                    null,
                    1);
            
            mw.setUseInWebProductiveMode(false);
            
            return mw;
        } catch (NoSuchMethodException | SecurityException e) {
            throw new RuntimeException(e);
        }
    }
    
    @ConversionMethod(plainText = false)
    public String createNewNumberByCode(String code, boolean ieee754, boolean autoQ, int radix, int numDecimalsValue) {
        FloatingPointRational floatRat = new FloatingPointRational(this.getRepFather());
        floatRat.setViaReflections(PAR_NAME_CODE, code);
        floatRat.setViaReflections(ExcessQ.PAR_NAME_RADIX, radix + "");
        floatRat.setViaReflections(PAR_NAME_DECIMAL_VAL, numDecimalsValue + "");
        floatRat.setViaReflections(PAR_NAME_AUTO_Q, autoQ + "");
        floatRat.setViaReflections(PAR_NAME_IEEE754, ieee754 + "");
        
        this.getRepFather().addNumber(floatRat);
        
        return this.getRepFather().createScriptFromInstance();
    }
    
    @ConversionMethod(plainText = false)
    public String createNewNumberByValue(String value, int length, boolean complementType) {
        return "bdd:101010";
    }

    @Override
    public HashSet<Field> ignoreFieldsInVisualizationScript() {
        HashSet<Field> fields = new HashSet<>();
        
        try {
            fields.add(this.getClass().getDeclaredField(PAR_NAME_CHARACTERISTIC_LENGTH));
            fields.add(this.getClass().getDeclaredField(PAR_NAME_MANTISSA_LENGTH));
            fields.add(this.getClass().getDeclaredField(PAR_NAME_DECIMAL_VAL));
            fields.add(this.getClass().getDeclaredField(PAR_NAME_LENGTHS_FIXED));
//            fields.add(this.getClass().getDeclaredField(PAR_NAME_POSITIVE));
        } catch (NoSuchFieldException | SecurityException e) {
            e.printStackTrace();
        }
        
        return fields;
    }
}
