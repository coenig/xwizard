/*
 * File name:        GlobalVariables.java (package eas.startSetup)
 * Author(s):        Lukas König
 * Java version:     8.0
 * Generation date:  31.08.2011 (19:27:07)
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

package eas;

import java.util.Calendar;

import eas.startSetup.ParCollection;
import eas.veryFastPDF.MainLink;


/**
 * Collection of global static variables.
 * 
 * @author Lukas König
 */
public class GlobalVariables {
    
    /**
     * This date is used in case no date can be retrieved from the
     * compilation time information.
     */
    private static final String FALLBACK_DATE_FOR_VERSION_INFO = "_2019-02-23";

    /**
     * Retrieves the date of the last compilation of EAS to create the exact
     * version number. The date is retrieved as follows:
     * </BR>
     * 1) If we're in web mode, go through all classes and store which was created latest.</BR>
     * 2) Use the stored value as the date to create the version number.</BR>
     * 3) If something goes wrong, use the fall-back date.
     * 
     * @return  The date for the version number.
     */
    private static String DATE_FOR_VERSION_INFO() {
        return FALLBACK_DATE_FOR_VERSION_INFO; // TODO: This should look up compilation time first.
    }
    
    /**
     * The root package of the eas package hierarchy (used in reflections).
     */
    public static final String ROOT_PACKAGE_NAME_EAS = "eas";
    public static final String PROG_VERSION_SHORT_EAS = "2.0.1";
    public static final String JAVA_VERSION = "8.0";
    public static final String PROG_VERSION_EAS = PROG_VERSION_SHORT_EAS + DATE_FOR_VERSION_INFO();
    public static final String PROG_NAME_SHORT_EAS = "EAS";
    public static final String PROG_NAME_EAS = "Easy Agent Simulation";
    public static final int COPYRIGHT_START_YEAR = 2007;
    public static final int COPYRIGHT_END_YEAR = Calendar.getInstance().get(Calendar.YEAR);
    public static final String LINK_TO_EAS_PROJECT = "https://sourceforge.net/projects/easyagentsimulation";
    public static final String LINK_TO_XWIZ_PROJECT = "https://sourceforge.net/projects/xwiz";

    /**
     * <H1>Global (semi-)unique parameter collection.</H1>
     * A premature parameter collection that provides at the very beginning of 
     * a run parameters (possibly no plugin parameters) read from the 
     * command line, then successively more and more parameters until the 
     * collection is equal to the "real" parameters established through the
     * command line, the parameter file and the standard values defined by
     * program constants. This collection can be used if the real parameter
     * collection for a run is unavailable (eg. at the beginning of a run).
     * At initialization time of the plugins the ParCollection is guaranteed
     * to be complete.
     * 
     * Cf. Starter.main(.) for the exact procedure of collecting parameters.
     */
    private static ParCollection prematureParameters;

    /**
     * <H1>Global (semi-)unique parameter collection.</H1>
     * A premature parameter collection that provides at the very beginning of 
     * a run parameters (possibly no plugin parameters) read from the 
     * command line, then successively more and more parameters until the 
     * collection is equal to the real parameters established through the
     * command line, the parameter file and the standard values defined by
     * program constants. This collection can be used if the real parameter
     * collection for a run is unavailable (eg., at the beginning of a run).
     * <BR>
     * Cf. Starter.main(.) for the exact procedure of collecting parameters.
     * <BR>
     * Not that this method will always yield a non-null parameter collection,
     * however, some parameters may not be set in the very beginning.
     * Also, in web mode, the parameter collection will not be completed,
     * i.e., the parameters may be null.
     */
    public static ParCollection getParameters() {
        if (prematureParameters == null) {
            prematureParameters = ParCollection.getSingletonInstance();
            
            if (MainLink.isApplicationOriginDesktop()) { // Don't complete in web mode.
                prematureParameters.complete();
            }
        }
        
        return prematureParameters;
    }

    /**
     * This method is public, but shouldn't be used by users in an "unsafe" way.
     * On the other hand, any "safe" way would mean setting it to the
     * exact same value it already has, so only core programmers who know what they
     * are doing should use this method.
     * 
     * @param prematureParameters  The most far-instantiated ParCollection so
     *                             far available (and usually NOTHING ELSE!).
     */
    public static void setParameters(final ParCollection prematureParameters) {
        GlobalVariables.prematureParameters = prematureParameters;
    }
}
