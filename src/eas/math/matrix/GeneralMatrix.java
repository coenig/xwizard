/*
 * File name:        GeneralMatrix.java (package eas.math.matrix)
 * Author(s):        hq0976
 * Java version:     8.0 (at generation time)
 * Generation date:  09.05.2017 (14:21:18)
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

package eas.math.matrix;

import java.math.BigDecimal;


/**
 * @author hq0976
 */
public abstract class GeneralMatrix {

    /**
     * This value is used for random matrices to create test cases.
     * In the CEGPM framework, this value is (and should be) never used.
     */
    private double randomMaxValue = 14;

    /**
     * This value is used for random matrices to create test cases.
     * In the CEGPM framework, this value is (and should be) never used.
     */
    private double randomMinValue = -10;
    
    
    
    
    /**
     * This method is used for random matrices to create test cases.
     * In the CEGPM framework, it is (and should be) never used.
     */
    public void setRandomParameters(double randMaxValue, double randMinValue) {
        this.setRandomMaxValue(randMaxValue);
        this.setRandomMinValue(randMinValue);
    }

    /**
     * This value is used for random matrices to create test cases.
     * In the CEGPM framework, this value is (and should be) never used.
     */
    public double getRandomMaxValue() {
        return randomMaxValue;
    }

    /**
     * This value is used for random matrices to create test cases.
     * In the CEGPM framework, this value is (and should be) never used.
     */
    public void setRandomMaxValue(double randomMaxValue) {
        this.randomMaxValue = randomMaxValue;
    }

    /**
     * This value is used for random matrices to create test cases.
     * In the CEGPM framework, this value is (and should be) never used.
     */
    public double getRandomMinValue() {
        return randomMinValue;
    }

    /**
     * This value is used for random matrices to create test cases.
     * In the CEGPM framework, this value is (and should be) never used.
     */
    public void setRandomMinValue(double randomMinValue) {
        this.randomMinValue = randomMinValue;
    }

    public abstract int getColumnCount();
    
    public abstract int getRowCount();
}
