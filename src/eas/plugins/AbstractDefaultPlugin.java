package eas.plugins;

import java.util.ArrayList;
import java.util.List;

import eas.simulation.EASRunnable;

/**
 * Dummy stub for the XWizard/VFP carve-out.
 */
public abstract class AbstractDefaultPlugin<T extends EASRunnable> implements Plugin<T> {

    @Override
    public List<String> getRequiredPlugins() {
        return new ArrayList<String>();
    }
}
