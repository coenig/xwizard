package eas.plugins;

import java.io.IOException;
import java.util.LinkedList;

import eas.plugins.standard.other.NullMaster;
import eas.plugins.standard.other.NullPlugin;
import eas.simulation.EASRunnable;
import eas.startSetup.ParCollection;

/**
 * Dummy stub for the XWizard/VFP carve-out. XWizard runs without any plugins,
 * so every factory operation returns an empty/absent result.
 */
public class PluginFactory {

    public static final String STD_MASTER_SCHEDULER = new NullMaster().id();

    public static final String STD_PLUGIN = new NullPlugin().id();

    public static final String PLUGIN_STORAGE_FILE_NAME = "plugins.dat";

    private static LinkedList<Class<? extends Plugin<?>>> STORED_PLUGINS =
            new LinkedList<Class<? extends Plugin<?>>>();

    public static LinkedList<Class<?>> getAllClasses(String forPackage) {
        return new LinkedList<Class<?>>();
    }

    public static LinkedList<Class<? extends Plugin<?>>> getSTORED_PLUGINS() {
        return STORED_PLUGINS;
    }

    public static void setSTORED_PLUGINS(LinkedList<Class<? extends Plugin<?>>> plugins) {
        STORED_PLUGINS = plugins;
    }

    public static LinkedList<Class<? extends Plugin<?>>> loadPluginsFromFile(int includeHidden) {
        return new LinkedList<Class<? extends Plugin<?>>>();
    }

    public static LinkedList<Class<? extends Plugin<?>>> findAllNonAbstractPluginClasses(int includeHidden) {
        return new LinkedList<Class<? extends Plugin<?>>>();
    }

    public static void serializePlugins(LinkedList<Class<? extends Plugin<?>>> o, String filename)
            throws IOException {
    }

    public static Plugin<?> generatePluginObject(final String plugName, final ParCollection params) {
        return null;
    }

    public static boolean existsPlugin(final String plugStr) {
        return false;
    }

    public static LinkedList<Class<AbstractDefaultPlugin<?>>> getMatchingADPlugins(EASRunnable rbl) {
        return new LinkedList<Class<AbstractDefaultPlugin<?>>>();
    }
}
