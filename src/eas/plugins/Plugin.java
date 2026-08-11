package eas.plugins;

import java.io.Serializable;
import java.util.List;

import eas.simulation.EASRunnable;
import eas.startSetup.SingleParameter;

/**
 * Dummy stub for the XWizard/VFP carve-out. The real EAS plugin framework is
 * not used by XWizard (which runs with a null plugin); this minimal interface
 * only exposes the members referenced by the retained parameter framework.
 */
public interface Plugin<Runnble extends EASRunnable> extends Serializable {

    String id();

    List<String> getRequiredPlugins();

    default List<String> getSupportedPlugins() {
        return new java.util.ArrayList<String>();
    }

    default List<SingleParameter> getParameters() {
        return new java.util.ArrayList<SingleParameter>();
    }
}
