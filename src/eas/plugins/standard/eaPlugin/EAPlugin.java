package eas.plugins.standard.eaPlugin;

import eas.plugins.AbstractDefaultPlugin;
import eas.simulation.EASRunnable;

/**
 * Dummy stub for the XWizard/VFP carve-out. The real evolutionary-algorithm
 * plugin is not used by XWizard; only the referenced static members remain.
 */
public class EAPlugin extends AbstractDefaultPlugin<EASRunnable> {

    public static boolean UseTranslatorWITHCompletingTransitions;

    public static String UMGEBUNG_PAR_NAME = "umgebung";

    public EAPlugin() {
    }

    @Override
    public String id() {
        return "ea";
    }
}
