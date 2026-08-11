package eas.simulation;

import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 * Dummy stub for the XWizard/VFP carve-out. The full EAS simulation runnable
 * hierarchy is not used by XWizard; this marker interface only exists so the
 * retained parameter/plugin framework compiles.
 */
public interface EASRunnable extends Serializable {

    default int id() {
        return 0;
    }

    default BufferedImage getOutsideView() {
        return null;
    }
}
