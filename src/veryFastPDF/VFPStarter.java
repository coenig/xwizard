/*
 * File name:        VFPStarter.java (package veryFastPDF)
 * Author(s):        hq0976
 * Java version:     8.0 (at generation time)
 * Generation date:  03.02.2017 (08:32:48)
 * Part of the EAS => VFP => XWizard webapp implementation.
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

package veryFastPDF;

import java.util.ArrayList;
import java.util.List;

import eas.GlobalVariables;
import eas.miscellaneous.StaticMethods;
import eas.startSetup.ParCollection;
import eas.startSetup.parameterDatatypes.ArrayListString;
import eas.veryFastPDF.MainLink;
import mainServlet.WebLink;
import veryFastPDF.plugin.VFPParameters;
import veryFastPDF.plugin.VFPWindow;
import veryFastPDF.script.RepresentableAsPDF;
import veryFastPDF.script.RepresentableFactory;

/**
 * Desktop entry point for XWizard/VeryFastPDF.
 *
 * <p>Historically the desktop application was launched through the full EAS
 * plugin/simulation framework (Starter GUI &rarr; SimulationStarter &rarr;
 * master scheduler plugin). XWizard does not use that machinery, so this
 * starter opens the {@link VFPWindow} directly, taking its configuration from
 * an optional {@code parameters.txt} file and the command line arguments.</p>
 *
 * @author hq0976
 */
public class VFPStarter {

    /**
     * Runs the VeryFastPDF desktop application.
     *
     * @param args  Optional parameters as {@code name value} pairs (the same
     *              format as used in {@code parameters.txt}), e.g.
     *              {@code studentVersion false specifyTypes FSM,PDA,Turing}.
     */
    public static void main(String[] args) {
        WebLink.setDebugMode(true); // This is, among initialization, to force class loader to load MainLink.
        MainLink.setDesktopMode();
        MainLink.setApplicationOriginVFP(true); // Establish cool new VFP mode.

        // Collect parameters from 'parameters.txt' (if present) and the command line.
        List<String> tokenList = new ArrayList<String>();
        try {
            List<String> argsFileRaw = StaticMethods.readTextArrayFromFile(".", "parameters.txt", null, true);
            String[] argsFile = StaticMethods.processStringAsCommandLineParameters(argsFileRaw.get(0));
            for (String s : argsFile) {
                tokenList.add(s);
            }
        } catch (Exception e) {
            // No 'parameters.txt' found - default parameters and command line arguments are used.
        }
        for (String s : args) {
            tokenList.add(s);
        }
        String[] tokens = tokenList.toArray(new String[tokenList.size()]);

        // Set up the global parameter collection (standard directory, logging, seed, ...).
        ParCollection params = ParCollection.getSingletonInstance();
        try {
            params.overwriteParameterList(tokens);
        } catch (Exception e) {
            StaticMethods.logWarning("Some parameters could not be applied: " + e, params);
        }
        params.complete();
        GlobalVariables.setParameters(params);

        // Configure the VFP-specific options directly (previously injected via the plugin framework).
        VFPParameters.setStudentVersion(getBooleanToken(tokens, "studentVersion", false));
        String specifyTypes = getToken(tokens, "specifyTypes");
        ArrayListString types;
        if (specifyTypes != null) {
            types = new ArrayListString();
            for (String t : specifyTypes.split(",")) {
                if (!t.isEmpty()) {
                    types.add(t);
                }
            }
        } else {
            types = new ArrayListString(RepresentableFactory.getAllRepNames());
        }
        VFPParameters.setSpecifyTypes(types);

        // Load external tool paths and open the main window directly.
        WebLink.loadPaths();
        List<RepresentableAsPDF> allRepresentables =
                RepresentableFactory.getRepsByNames(VFPParameters.getSpecifyTypes());
        new VFPWindow(allRepresentables);
    }

    /**
     * Returns the value following the given key in a {@code name value} token
     * array, or {@code null} if the key is not present.
     */
    private static String getToken(final String[] tokens, final String key) {
        for (int i = 0; i < tokens.length - 1; i++) {
            if (tokens[i].equalsIgnoreCase(key)) {
                return tokens[i + 1];
            }
        }
        return null;
    }

    /**
     * Returns the boolean value following the given key, or the default value
     * if the key is not present.
     */
    private static boolean getBooleanToken(final String[] tokens, final String key, final boolean defaultValue) {
        String value = getToken(tokens, key);
        if (value == null || value.isEmpty()) {
            return defaultValue;
        }
        char c = Character.toLowerCase(value.charAt(0));
        return c == 'j' || c == 'y' || c == 't';
    }
}
