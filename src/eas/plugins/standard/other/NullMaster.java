package eas.plugins.standard.other;

import java.util.ArrayList;
import java.util.List;

import eas.plugins.masterScheduler.MasterScheduler;
import eas.simulation.EASRunnable;

/**
 * Dummy stub for the XWizard/VFP carve-out. Represents the "null" master
 * scheduler, i.e. the no-op default used by XWizard.
 */
public class NullMaster implements MasterScheduler<EASRunnable> {

    @Override
    public String id() {
        return "null-master";
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
