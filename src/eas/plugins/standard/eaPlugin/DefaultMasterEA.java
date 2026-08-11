package eas.plugins.standard.eaPlugin;

import java.util.ArrayList;
import java.util.List;

import eas.plugins.masterScheduler.MasterScheduler;
import eas.simulation.EASRunnable;

/**
 * Dummy stub for the XWizard/VFP carve-out.
 */
public class DefaultMasterEA implements MasterScheduler<EASRunnable> {

    public DefaultMasterEA() {
    }

    @Override
    public String id() {
        return "default-master-ea";
    }

    @Override
    public List<String> getRequiredPlugins() {
        return new ArrayList<String>();
    }

    @Override
    public EASRunnable[] generateRunnables() {
        return new EASRunnable[0];
    }
}
