package eas.plugins.masterScheduler;

import eas.plugins.Plugin;
import eas.simulation.EASRunnable;

/**
 * Dummy stub for the XWizard/VFP carve-out.
 */
public interface MasterScheduler<RunnableEnvironment extends EASRunnable>
        extends Plugin<RunnableEnvironment> {

    RunnableEnvironment[] generateRunnables();
}
