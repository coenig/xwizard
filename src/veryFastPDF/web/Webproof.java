/*
 * File name:        Webproof.java (package veryFastPDF.script)
 * Author(s):        Lukas König
 * Java version:     8.0 (at generation time)
 * Generation date:  23.07.2015 (17:30:09)
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

package veryFastPDF.web;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * If a {@code RepresentableAsPDF} should be uploaded on the server,
 * annotate the class with this annotation. If the default mode is
 * used, the {@code RepresentableAsPDF} is uploaded in DEBUG mode
 * only, and it is considered potentially unstable. If 
 * {@code allowInProductiveMode()} is set to true, it
 * is uploaded in any case and it is considered stable.
 * This property is to be given to Representables which have been thought
 * through to be web-proof. This means that not only there are no
 * exceptions and other stuff thrown to the top level, but moreover, long-time
 * operations have to be properly handled.
 * 
 * @author Lukas König
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Webproof {

    /**
     * @return  {@code true} if and only if the representable is web-ready.
     *          Use this method's default return value ({@code false}) to test 
     *          your implementation in debug mode. 
     *          It will not be uploaded in productive mode. Switch
     *          to {@code true} only if you're absolutely sure, all is ok.
     *          Note: by debug mode, {@code MainLink#debugMode} is meant.
     */
    boolean useInProductiveMode() default false;
}
