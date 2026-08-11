package eas.plugins.standard.other;

import java.util.ArrayList;
import java.util.List;

import eas.plugins.Plugin;
import eas.simulation.EASRunnable;

/**
 * Dummy stub for the XWizard/VFP carve-out. Represents the "null" plugin used
 * by XWizard.
 */
public class NullPlugin implements Plugin<EASRunnable> {

    @Override
    public String id() {
        return "null-plugin";
    }

    @Override
    public List<String> getRequiredPlugins() {
        return new ArrayList<String>();
    }
}
