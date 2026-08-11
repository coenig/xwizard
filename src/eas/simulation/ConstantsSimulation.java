package eas.simulation;

import eas.startSetup.ParCollection;

/**
 * Dummy stub for the XWizard/VFP carve-out. Only the constants and helper
 * methods referenced by the retained parameter framework are kept; the JoSchKa
 * (distributed grid) and MARB translator features are not used by XWizard.
 * Constant values mirror the original EAS defaults.
 */
public class ConstantsSimulation {

    public static final String ACTION_ON_UNCAUGHT_EXCEPTION_PRINT_AND_RECOVER = "LogErrorMessageAndRecover";
    public static final String ACTION_ON_UNCAUGHT_EXCEPTION_PRINT_AND_TERMINATE = "LogErrorMessageAndTerminate";
    public static final String ACTION_ON_UNCAUGHT_EXCEPTION_PRINT_AND_HALT = "LogErrorMessageAndHalt";
    public static final String ACTION_ON_UNCAUGHT_EXCEPTION_DO_NOTHING = "DoNothing";
    public static final String ACTION_ON_UNCAUGHT_EXCEPTION_REMOVE_SCHEDULER_AND_RECOVER = "RemoveCausingPluginAndRecover";
    public static final String ACTION_ON_UNCAUGHT_EXCEPTION_ASK_WHAT_TO_DO = "AskWhatToDo";
    public static final String ACTION_ON_UNCAUGHT_EXCEPTION_STANDARD = ACTION_ON_UNCAUGHT_EXCEPTION_ASK_WHAT_TO_DO;
    public static final String ACTION_UNCAUGHT_EXCEPTION = "actionOnUncaughtException";

    public static final String FORCE_EXIT_AFTER_SIMULATION_TERMINATES = "forceExitAfterSimulationTerminates";
    public static final Boolean DEFAULT_START_IMMEDIATELY = false;

    public static final String JOSCHKA_JAVA_PARAMETER_COMMAND = "java";
    public static final String JOSCHKA_JAVA_PARAMETER_COMMAND_NAME = "joschkaJavaBinCommand";
    public static final String JOSCHKA_VERZ = "simulation";
    public static final String JOSCHKA_JAR = "eas.jar";
    public static final String JOSCHKA_PLATT = "WJ";
    public static final String JOSCHKA_USER = "hq0976";
    public static final String JOSCHKA_VM = "-Xmx900M";
    public static final String JOSCHKA_CP = "eas.jar";

    public static final int MIN_INST_BEH = 1;
    public static final int MAX_INST_BEH = 5;
    public static final int MIN_INST_TRANS = 1;
    public static final int MAX_INST_TRANS_WC = 1;
    public static final int MAX_INST_TRANS_WOC = 1;

    public static int minInstTranslator() {
        return MIN_INST_TRANS;
    }

    public static int maxInstTranslator() {
        return MAX_INST_TRANS_WOC;
    }

    public static String[] getBefehlNamenArray(final ParCollection params) {
        return new String[0];
    }
}
