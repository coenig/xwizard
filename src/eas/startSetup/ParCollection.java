/*
 * Datei:          ParCollection.java
 * Autor(en):      Lukas König
 * Java-Version:   6.0
 * Erstellt (vor): 14.03.2008
 *
 * (c) This file and the EAS (Easy Agent Simulation) framework containing it
 * is protected by Creative Commons by-nc-sa license. Any altered or
 * further developed versions of this file have to meet the agreements
 * stated by the license conditions. 
 * 
 * In a nutshell
 * -------------
 * You are free:
 * - to Share -- to copy, distribute and transmit the work
 * - to Remix -- to adapt the work
 * 
 * Under the following conditions:
 * - Attribution -- You must attribute the work in the manner specified by the 
 *   author or licensor (but not in any way that suggests that they endorse 
 *   you or your use of the work).
 * - Noncommercial -- You may not use this work for commercial purposes.
 * - Share Alike -- If you alter, transform, or build upon this work, you may 
 *   distribute the resulting work only under the same or a similar license to 
 *   this one. 
 * 
 * + Detailed license conditions (Germany):
 *   http://creativecommons.org/licenses/by-nc-sa/3.0/de/
 * + Detailed license conditions (unported):
 *   http://creativecommons.org/licenses/by-nc-sa/3.0/deed.en
 * 
 * This header must be placed in the beginning of any version of this file.
 */

package eas.startSetup;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import javax.swing.JFrame;

import eas.GlobalVariables;
import eas.math.geometry.Vector2D;
import eas.math.matrix.Matrix;
import eas.miscellaneous.StaticMethods;
import eas.miscellaneous.useful.ascii3D.Ascii3D;
import eas.plugins.AbstractDefaultPlugin;
import eas.plugins.Plugin;
import eas.plugins.PluginFactory;
import eas.plugins.masterScheduler.MasterScheduler;
import eas.plugins.standard.other.NullMaster;
import eas.simulation.ConstantsSimulation;
import eas.simulation.simSpatial.sim2D.marbSimulation.statistics.ConstantsStatistics;
import eas.startSetup.logging.AbstractMsg;
import eas.startSetup.marbBuilder.ConstantsGraphVis;
import eas.startSetup.parameterDatatypes.ArrayListBool;
import eas.startSetup.parameterDatatypes.ArrayListDouble;
import eas.startSetup.parameterDatatypes.ArrayListInt;
import eas.startSetup.parameterDatatypes.ArrayListLong;
import eas.startSetup.parameterDatatypes.ArrayListString;
import eas.startSetup.parameterDatatypes.ArrayListVec2D;
import eas.startSetup.parameterDatatypes.Datatypes;

/**
 * Implementation of a parameter collection where all the static program 
 * parameters and the generic (plugin) parameters are stored.</BR>
 * </BR>
 * Caution: Parameters of type String cannot contain '"' symbols in their values 
 * as these are used to group parameters including white spaces. Also, parameter 
 * names should not contain dashes (although this limitation is only technical 
 * and may be removed in future).</BR>
 * </BR>
 * To be on the safe side, you might want to avoid all of the following 
 * symbols in both parameter names and values:</BR>
 * '"'</BR>
 * ' ' (allowed in "...")</BR>
 * '-' (allowed in values, except for fixed String set values)</BR>
 * ','</BR>
 * '='</BR>
 * (All of them CAN be used, though, if you know what you are doing.)
 * 
 * @author Lukas König
 */
public class ParCollection implements Serializable {
    
    /**
     * The singleton instance of this parameter collection, used in all
     * regular EAS simulation runs. Don't construct 
     * additional ones, unless you really know what you are doing.
     */
    private static ParCollection singletonInstance;
    
    private Boolean startImmediately;

    private Boolean forceExitAfterSimulationTerminates = null;
    
    public boolean isForceExitAfterSimulationTerminates() {
        return forceExitAfterSimulationTerminates;
    }

    public void setForceExitAfterSimulationTerminates(
            Boolean forceExitAfterSimulationTerminates) {
        this.forceExitAfterSimulationTerminates = forceExitAfterSimulationTerminates;
    }
    
    private String actionOnUncaughtException;
    
    /**
     * The main scheduler that is responsible for the creation of environments
     * and fundamental behavioral properties of a run. 
     */
    private String masterScheduler;

    /**
     * Zeitstempel für die Erzeugung eines Objekts dieses Typs.
     */
    private Date timeStamp;
    
    /**
     * Die VersionsID.
     */
    private static final long serialVersionUID = -4330770423865488211L;

    /**
     * Der Seed-Wert für den Zufallsgenerator.
     */
    private Long seed;

    /**
     * Key-value-pairs of JoSchKa parameters.
     */
    private HashMap<String, String> joschkaParameters = null;
    
    /**
     * Die Stufe des Loggens.<BR>
     * -1: STAGE1 </BR>
     *  0: OUTPUT </BR>
     *  1: DEBUG </BR>
     *  2: INFO </BR>
     *  3: WARNING </BR>
     *  4: ERROR </BR>
     *  5: WEB </BR>
     */
    private Integer logStufe = 2;
    
    /**
     * Eine Liste von Warnungen.
     */
    private LinkedList<AbstractMsg> msgs = new LinkedList<AbstractMsg>();
    
    /**
     * Das Standardverzeichnis zum Speichern von Graphendaten, Aufnahmen, etc.
     */
    private String standardVerz;

    /**
     * Die Liste der in diesem Lauf eingesetzten Plugins.
     */
    private String[] plugins;

    /**
     * @param plugs The plugins to set.
     */
    public void setPlugins(final String[] plugs) {
        this.arr = null;
        this.plugins = plugs;
    }

    /**
     * Ob der Parametersatz schreibgeschützt ist (oder verändert werden kann;
     * standard: false).
     */
    private boolean schreibschutz;
    
    /**
     * Eine Liste generischer Parameter, die über Plugins gesetzt werden
     * können.
     */
    private LinkedList<SingleParameter> generischeParams;
    
    /**
     * Constructor using the parameters of another ParCollection to set its
     * own parameters. Note that in all regular EAS runs there is only one 
     * singleton ParCollection. Don't construct additional ones, unless you
     * really know what you are doing.
     * 
     * @param other  The other parameter collection.
     */
    protected ParCollection(final ParCollection other) {
        super();
        
        this.schreibschutz = false;
        this.generischeParams = new LinkedList<SingleParameter>();
        this.timeStamp = new Date();

        if (other == null) {
            return;
        }
        
        this.generischeParams.addAll(other.generischeParams);
        
        this.seed = other.seed;
        this.logStufe = other.logStufe;
        this.standardVerz = other.standardVerz;
        this.plugins = new String[other.plugins.length];
        for (int i = 0; i < other.plugins.length; i++) {
            this.plugins[i] = other.plugins[i];
        }
        
        this.joschkaParameters = new HashMap<>();
        this.joschkaParameters.putAll(other.joschkaParameters);
        
        this.masterScheduler = other.masterScheduler;
        this.startImmediately = other.startImmediately;
        this.actionOnUncaughtException = other.actionOnUncaughtException;
        this.forceExitAfterSimulationTerminates = other.forceExitAfterSimulationTerminates;
    }
    
    /**
     * Standard constructor.
     * 
     * @param args  The arguments may be passed on directly from the command
     *              line arguments given to a main method.
     *              The list items are interpreted in an alternating way as 
     *              key/value pairs, more precisely, for an even number i:
     *              interpret (args[i], args[i + 1]) as a key/value pair. 
     *              Note that in all regular EAS runs there is only one 
     *              singleton ParCollection. Don't construct additional ones, unless you
     *              really know what you are doing.
     */
    protected ParCollection(final String[] args) {
        super();
        
        this.generischeParams = new LinkedList<SingleParameter>();
        
        this.schreibschutz = false;
        this.timeStamp = new Date();
        this.seed = null;
        this.logStufe = null;
        this.standardVerz = null;
        this.plugins = null;
        this.joschkaParameters = null;
        this.masterScheduler = null;
        this.startImmediately = null;
        this.actionOnUncaughtException = null;
        this.forceExitAfterSimulationTerminates = null;
        
        this.overwriteParameterList(args);
    }
    
    /**
     * Constructor that initializes the parameter collection with standard 
     * values given by program constants. Note that the other constructors 
     * do not automatically use standard values. Note that in all regular 
     * EAS runs there is only one singleton ParCollection. Don't construct 
     * additional ones, unless you really know what you are doing.
     */
    protected ParCollection() {
        this((ParCollection) null);
    }
    
    /**
     * Note that ParCollection is not a Singleton class. For special purposes
     * some program parts such as the job generator are allowed to produce
     * several different objects of this type. Nevertheless, for nearly ALL
     * regular purposes, this is the only instance of the parameter collection
     * relevant.
     * 
     * @return  The (pseudo-)singleton instance of the parameter collection.
     */
    public static ParCollection getSingletonInstance() {
        if (singletonInstance == null) {
            singletonInstance = new ParCollection();
        }
        
        return singletonInstance;
    }
    
//    /**
//     * Ändert alle angegebenen Parameter, belässt die übrigen gleich.
//     * 
//     * @param args  Argumente, die bspw. direkt aus der main-Methode kommen
//     *              können. Es werden immer Paare (args[i], args[i + 1]), 
//     *              i gerade, betrachtet, wobei args[i] der Name und 
//     *              args[i + 1] der Wert eines Attributs ist.
//     *                     
//     * @return  Die Anzahl der geänderten Parameter.
//     */
//    public int neuePars(final String[] args) {
//        return this.neuePars(args, true);
//    }
    
    /**
     * Ändert alle angegebenen Parameter, belässt die übrigen gleich.
     * 
     * @param args  Argumente, die bspw. direkt aus der main-Methode kommen
     *              können. Es werden immer Paare (args[i], args[i + 1]), 
     *              i gerade, betrachtet, wobei args[i] der Name und 
     *              args[i + 1] der Wert eines Attributs ist.
     *                     
     * @return  Die Anzahl der geänderten Parameter.
     */
    public int overwriteParameterList(final String[] args) {
        int zaehler = 0;
        int aenderungen = 0;
        
        if (args == null) {
            return aenderungen;
        }
        
        try {
            for (int i = 0; i < args.length; i += 2) {
                zaehler = i;
                // Auf Verdacht Anzahl Änderungen erhöhen.
                aenderungen++;
                
                if (args[i].toLowerCase().equals(ConstantsStatistics.SEED_ATTR)) {
                    this.seed = Long.parseLong(args[i + 1]);
                } else if (args[i].toLowerCase().equals(ConstantsStatistics.LOG_ATTR)) {
                    this.logStufe = Integer.parseInt(args[i + 1]);
                } else if (args[i].toLowerCase().equals(
                        ConstantsStatistics.VERZEICHNIS_A)) {
                    this.standardVerz = args[i + 1];
                } else if (args[i].equalsIgnoreCase(ConstantsStatistics.PLUGINS_PAR_NAME)) {
                    String[] plug = args[i + 1].split(",");
                    this.plugins = new String[plug.length];
                    for (int z = 0; z < this.plugins.length; z++) {
                        this.plugins[z] = plug[z];
                    }
                } else if (args[i].equalsIgnoreCase(ConstantsStatistics.JOSCHKA_PARAMETERS_NAME)) {
                    this.joschkaParameters = new HashMap<>();
                    for (String s : args[i + 1].split(",")) {
                        String[] keyValue = s.split("=");
                        this.joschkaParameters.put(keyValue[0], keyValue[1]);
                    }
                } else if (args[i].equalsIgnoreCase(
                        ConstantsStatistics.MASTER_SCHEDULER_PAR_NAME)) {
                    this.masterScheduler = args[i + 1];
                } else if (args[i].equalsIgnoreCase(
                        ConstantsStatistics.START_IMMEDIATELY_ATT)) {
                    boolean zwisch = args[i + 1].substring(0, 1).equals("j")
                            || args[i + 1].substring(0, 1).equals("y")
                            || args[i + 1].substring(0, 1).equals("t");
                    
                    this.startImmediately = zwisch;
                } else if (args[i].equalsIgnoreCase(
                        ConstantsSimulation.ACTION_UNCAUGHT_EXCEPTION)) {
                    this.actionOnUncaughtException = args[i + 1];
                } else if (args[i].equalsIgnoreCase(ConstantsSimulation.FORCE_EXIT_AFTER_SIMULATION_TERMINATES)) {
                    boolean zwisch = args[i + 1].substring(0, 1).equals("j")
                            || args[i + 1].substring(0, 1).equals("y")
                            || args[i + 1].substring(0, 1).equals("t");
                    this.forceExitAfterSimulationTerminates = zwisch;
                } else {
                    // Doch keine Veränderung an den Parametern.
                    aenderungen--;
                }
            }
        } catch (final Exception e) {
            // Doch keine Veränderung an den Parametern.
            aenderungen--;
            
            String fehler 
                = "Parameter collection has not been updated by: '"
                    + args[zaehler] + " " + args[zaehler + 1] + "'";
            if (this.logStufe == null) {
                this.logStufe = ConstantsStatistics.STANDARD_LOG_STUFE;
            }
            StaticMethods.log(
                    StaticMethods.LOG_WARNING,
                    fehler,
                    this);
            throw new RuntimeException(fehler);
        }

        // Check if generic parameters have to be generated.
        String nullID = new NullMaster().id();
        if (this.masterScheduler != null 
                && this.masterScheduler.equalsIgnoreCase(nullID)
                && this.plugins != null 
                && this.plugins.length == 1 
                && this.plugins[0].equalsIgnoreCase(nullID)) {
            return aenderungen;
        }
        
        // Generische Parameter registrieren.
        this.regGenParams();
        
        // Mit neuen Werten füttern, falls angegeben.
        for (int i = 0; i < args.length; i += 2) {
            for (int j = 0; j < this.generischeParams.size(); j++) {
                SingleParameter par = this.generischeParams.get(j);
                
                try {
                    if (par.getParameterName().equalsIgnoreCase(args[i])) {
                        // Datentypen-Unterscheidung.
                        
                        // Änderungen auf Verdacht erhöhen.
                        aenderungen++;
                        setParValueFromString(args[i + 1], par);
                    }
                } catch (NumberFormatException e) {
                    // Änderungen doch wieder reduzieren.
                    aenderungen--;
                    this.logError("Error in parameter " + args[i] + ":\n" + e.toString());
                        throw new RuntimeException("Error in Parameter '" + args[i] + "': " + e);
                }
            }
        }
        
        return aenderungen;
    }

    private void setParValueFromString(
            final String arg, 
            SingleParameter par) {
        this.arr = null;
        String[] strArr = arg.split(",");
        
        if (par.getParameterType().equals(Datatypes.BOOLEAN)) {
            // Boolean.
            boolean zwisch = arg.substring(0, 1)
                    .equals("j")
                    || arg.substring(0, 1).equals("y")
                    || arg.substring(0, 1).equals("t");

            par.setParValue(zwisch);
            pushToListener(zwisch, par);
        } else if (par.getParameterType().equals(Datatypes.BOOLEAN_ARR)) {
            ArrayListBool boolListe = new ArrayListBool(
                    strArr.length);
            for (String s : strArr) {
                boolListe.add(s.substring(0, 1).equals("j")
                        || s.substring(0, 1).equals("y")
                        || s.substring(0, 1).equals("t"));
            }
            par.setParValue(boolListe);
            pushToListener(boolListe, par);
        } else if (par.getParameterType().equals(Datatypes.DOUBLE_ARR)) {
            ArrayListDouble doubListe = new ArrayListDouble(
                    strArr.length);
            for (String s : strArr) {
                doubListe.add(Double.parseDouble(s));
            }
            par.setParValue(doubListe);
            pushToListener(doubListe, par);
        } else if (par.getParameterType().equals(Datatypes.DOUBLE) || par.getParameterType().startsWith(Datatypes.DOUBLE_RANGE_PREFIX)) {
            // Double.
            par.setParValue(Double.parseDouble(arg));
            pushToListener(Double.parseDouble(arg), par);
        } else if (par.getParameterType().equals(Datatypes.INTEGER_ARR)) {
            ArrayListInt intListe = new ArrayListInt(
                    strArr.length);
            for (String s : strArr) {
                intListe.add(Integer.parseInt(s));
            }
            par.setParValue(intListe);
            pushToListener(intListe, par);
        } else if (par.getParameterType().equals(Datatypes.INTEGER) || par.getParameterType().startsWith(Datatypes.INTEGER_RANGE_PREFIX)) {
            // Integer.
            par.setParValue(Integer.parseInt(arg));
            pushToListener(Integer.parseInt(arg), par);
        } else if (par.getParameterType().equals(Datatypes.MATRIX)) {
            // Matrix.
            par.setParValue(Matrix.parseMatrix(arg));
            pushToListener(Matrix.parseMatrix(arg), par);
        } else if (par.getParameterType().equals(Datatypes.LONG_ARR)) {
            ArrayListLong longListe = new ArrayListLong(
                    strArr.length);
            for (String s : strArr) {
                longListe.add(Long.parseLong(s));
            }
            par.setParValue(longListe);
            pushToListener(longListe, par);
        } else if (par.getParameterType().equals(Datatypes.LONG)) {
            // Long.
            par.setParValue(Long.parseLong(arg));
            pushToListener(Long.parseLong(arg), par);
        } else if (par.getParameterType().equals(Datatypes.VECTOR2D_ARR)) {
            ArrayListVec2D vekListe = new ArrayListVec2D(
                    strArr.length);
            for (String s : strArr) {
                vekListe.add(Vector2D.parseVector2D(s));
            }
            par.setParValue(vekListe);
            pushToListener(vekListe, par);
        } else if (par.getParameterType().equals(Datatypes.VECTOR2D)) {
            // Vektor2D.
            par.setParValue(Vector2D.parseVector2D(arg));
            pushToListener(Vector2D.parseVector2D(arg), par);
        } else if (par.getParameterType().equals(Datatypes.STRING_ARR)) {
            // String[ ].
            ArrayListString strListe = new ArrayListString(
                    strArr.length);
            for (String s : strArr) {
                strListe.add(s);
            }
            par.setParValue(strListe);
            pushToListener(strListe, par);
        } else {
            // String (falls alle Stricke reißen...).
            par.setParValue(arg);
            pushToListener(arg, par);
        }
    }

    /**
     * Pushes a parameter to the connected Java field using reflection. The
     * method first tries to use the static setter; if this fails (usually
     * if there is no static setter method in the listener class), the 
     * method tries to access to field directly. Warnings are printed if
     * any of this fails.
     * 
     * @param arg  The parameter value to set.
     * @param par  The parameter to change.
     */
    private void pushToListener(final Object arg, SingleParameter par) {
        // List of key-value parameters (String only).
        if (par.getParameterType().equals(Datatypes.STRING_ARR)) {
            for (String p : (ArrayListString) par.getParValue()) {
                if (p.contains("=")) {
                    String pKey = p.split("=")[0];
                    String pValue = p.split("=")[1];
                    pushToListener(pValue, new SingleParameter(pKey, Datatypes.STRING, "", par.getDescription(), par.getParameterCategory(), par.getStaticListenerClass()));
                }
            }
        }
        
        // Real "single" parameter.
        Class<?> listener = par.getStaticListenerClass();
        if (listener != null) {
            try {
                String setterName = "set" 
                        + par.getParameterName().substring(0, 1).toUpperCase() 
                        + par.getParameterName().substring(1);

                Class<?> parClass = arg.getClass();
                Class<?> altParClass = null;

                if (arg.getClass().equals(Integer.class)) {
                    altParClass = Integer.TYPE;
                }
                if (arg.getClass().equals(Double.class)) {
                    altParClass = Double.TYPE;
                }
                if (arg.getClass().equals(Long.class)) {
                    altParClass = Long.TYPE;
                }
                if (arg.getClass().equals(Boolean.class)) {
                    altParClass = Boolean.TYPE;
                }

                Method setterMethod = null;
                
                try {
                    setterMethod = listener.getMethod(setterName, new Class<?>[] {parClass});   
                } catch (Exception e) {
                    setterMethod = listener.getMethod(setterName, new Class<?>[] {altParClass});
                }
                setterMethod.setAccessible(true);
                setterMethod.invoke(null, new Object[] {arg});
            } catch (Exception e) {
                if (par.isWarnAboutMissingSetter()) {
                    this.logWarning(e.toString());
                    this.logWarning("Could not access setter of '" 
                            + par.getParameterName() 
                            + "'. Maybe it is not static or you're trying to set 'int for double' or the like? "
                            + "I will try to set the variable directly (no further message means success).");
                }
                
                pushDirectlyToField(arg, par, listener);
            }
        }
    }

    /**
     * Forces all parameters to be pushed to their listeners as if
     * a change occurred.
     */
    public void pushAllParametersToListeners() {
        this.generischeParams.forEach(par -> pushToListener(par.getParValue(), par));
    }
    
    /**
     * Forces all parameters of a given plugin to be pushed to their listeners
     * as if a change occurred.
     */
    public void pushPluginParametersToListeners(Plugin<?> plug) {
        if (plug == null || plug.getParameters() == null) {
            return;
        }
        
        plug.getParameters().forEach(par -> pushToListener(par.getParValue(), par));
    }
    
    /**
     * Pushes a parameter to the connected Java field using reflection. The field
     * is directly accessed without trying to use a setter. Warnings are printed if
     * any of this fails.
     * 
     * @param arg       The parameter value to set.
     * @param par       The parameter to change.
     * @param listener  The static listener class.
     */
    private void pushDirectlyToField(
            final Object arg, 
            SingleParameter par,
            Class<?> listener) {
        try {
            Field privateStringField = listener.getDeclaredField(par.getParameterName());
            privateStringField.setAccessible(true);
            privateStringField.set(null, arg);
        } catch (Exception e2) {
            this.logWarning(e2.toString());
            this.logWarning("Maybe field '" + par.getParameterName() + "' is not static or type conversion failed?");
        }
    }

    /**
     * Registriert generische Parameter. 
     */
    public void regGenParams() {
        LinkedList<SingleParameter> existingParams = new LinkedList<SingleParameter>();
        
        // Schon vorhandene generische Parameter sichern.
        if (this.generischeParams != null) {
            existingParams.addAll(this.generischeParams);
        }
        
        this.generischeParams = new LinkedList<SingleParameter>();
        
        if (this.plugins != null) {
            // MasterScheduler.
            if (this.masterScheduler != null) {
                Plugin<?> plug = PluginFactory.generatePluginObject(this.masterScheduler,
                        null);
                if (plug != null && plug.getParameters() != null) {
                    this.generischeParams.addAll(plug.getParameters());
                }
            }
            
            // Plugins.
            for (String plugStr : this.plugins) {
                Plugin<?> plug = PluginFactory.generatePluginObject(plugStr, null);
                if (plug != null && plug.getParameters() != null) {
                    this.generischeParams.addAll(plug.getParameters());
                }
            }
        }
        
        // Doppelte herausfinden.
        this.doppelte = new HashSet<String>();
        for (SingleParameter p1 : this.generischeParams) {
            for (SingleParameter p2 : this.generischeParams) {
                if (p1 != p2 && p1.getParameterName().equalsIgnoreCase(p2.getParameterName())) {
                    this.doppelte.add(p1.getParameterName());
                }
            }
        }
        
        // Generisch-Attribut setzen.
        for (SingleParameter gen : this.generischeParams) {
            gen.setGeneric();
        }
        
        /* 
         * Alte Werte der bereits vorhanden gewesenen Parameter übernehmen.
         * Da die neuen Werte nicht in dieser Methode gesetzt werden (sondern
         * danach in der Methode neuePars), kann man hier bedenkelos die 
         * Standardwerte überschreiben.
         */
        for (SingleParameter sp : existingParams) {
            this.setParValue(sp.getParameterName(), sp.getParValue());
        }
    }

    /**
     * Die dopelten Parameternamen.
     */
    private HashSet<String> doppelte;
    
    public HashSet<String> getMultiplyDefinedParNames() {
        return this.doppelte;
    }
    
    public String getActionOnUncaughtException() {
        return this.actionOnUncaughtException;
    }
    
    public Boolean getStartImmediately() {
        return this.startImmediately;
    }
    
    private String getJoSchKaParameter(String name) {
        if (this.joschkaParameters == null) {
            return null;
        }
        return this.joschkaParameters.get(name);
    }
    
    private void setJoSchKaParameter(String name, String value) {
        if (this.joschkaParameters == null) {
            this.joschkaParameters = new HashMap<>();
        }
        this.joschkaParameters.put(name, value);
    }
    
    /**
     * @return  Der Quellpfad für den Starter.
     */
    public String getstarterSourceDirectory() {
        return this.getJoSchKaParameter(ConstantsStatistics.STARTER_QUELL_PFAD);
    }
    
    /**
     * @return  Das JoSchKa-Verzeichnis.
     */
    public String getJoschkaVerz() {
        return this.getJoSchKaParameter(ConstantsStatistics.JOSCHKA_VERZ);
    }
    
    /**
     * @return  Der JoSchKa-JAR-Parameter.
     */
    public String getJoschkaJAR() {
        return this.getJoSchKaParameter(ConstantsStatistics.JOSCHKA_JAR);
    }

    /**
     * @return Returns the complete list of joschkaParameters as hashmap of strings.
     */
    public HashMap<String, String> getJoschkaParameters() {
        return this.joschkaParameters;
    }
    
    /**
     * @return  Der JoSchKa-Plattform-Parameter.
     */
    public String getJoschkaPlatt() {
        return this.getJoSchKaParameter(ConstantsStatistics.JOSCHKA_PLATT);
    }

    /**
     * @return  Der JoSchKa-User-Parameter.
     */
    public String getJoschkaUser() {
        return this.getJoSchKaParameter(ConstantsStatistics.JOSCHKA_USER);
    }

    /**
     * @return  Der JoSchKa-VM-Parameter.
     */
    public String getJoschkaVMpar() {
        return this.getJoSchKaParameter(ConstantsStatistics.JOSCHKA_VM);
    }

    /**
     * @return  Der JoSchKa-Classpath.
     */
    public String getjoschkaCP() {
        return this.getJoSchKaParameter(ConstantsStatistics.JOSCHKA_CP);
    }

    public void setstarterSourceDirectory(String value) {
        this.setJoSchKaParameter(ConstantsStatistics.STARTER_QUELL_PFAD, value);
    }
    
    /**
     * @return  Das JoSchKa-Verzeichnis.
     */
    public void setJoschkaVerz(String value) {
        this.setJoSchKaParameter(ConstantsStatistics.JOSCHKA_VERZ, value);
    }
    
    /**
     * @return  Der JoSchKa-JAR-Parameter.
     */
    public void setJoschkaJAR(String value) {
        this.setJoSchKaParameter(ConstantsStatistics.JOSCHKA_JAR, value);
    }

    /**
     * @return  Der JoSchKa-Plattform-Parameter.
     */
    public void setJoschkaPlatt(String value) {
        this.setJoSchKaParameter(ConstantsStatistics.JOSCHKA_PLATT, value);
    }

    /**
     * @return  Der JoSchKa-User-Parameter.
     */
    public void setJoschkaUser(String value) {
        this.setJoSchKaParameter(ConstantsStatistics.JOSCHKA_USER, value);
    }

    /**
     * @return  Der JoSchKa-VM-Parameter.
     */
    public void setJoschkaVMpar(String value) {
        this.setJoSchKaParameter(ConstantsStatistics.JOSCHKA_VM, value);
    }

    /**
     * @return  Der JoSchKa-Classpath.
     */
    public void setjoschkaCP(String value) {
        this.setJoSchKaParameter(ConstantsStatistics.JOSCHKA_CP, value);
    }

    /**
     * @return Returns the seed.
     */
    public Long getSeed() {
        return this.seed;
    }

    /**
     * @return Returns the logStufe.
     */
    public Integer getLogOutputLevel() {
        return this.logStufe;
    }

    /**
     * @return  Das Standardverzeichnis zum Speichern von Graphdaten, etc.
     */
    public String getStdDirectory() {
        return this.standardVerz;
    }
    
    /**
     * @return Returns the zeitstempel.
     */
    public Date getTimestamp() {
        return this.timeStamp;
    }
    
    /**
     * Gibt den (ersten) Parameter mit dem übergebenen Namen zurück, wobei
     * er automatisch zum angegebenen Typ gecastet wird, falls das möglich ist.
     * Falls nicht, wird der übliche Fehler geworfen.</BR>
     * </BR>
     * This method returns a standard value if no value is stored.</BR>
     * </BR>
     * The method is deprecated as of EAS version 1_0, and should not be
     * used anymore. Instead, the refactoring-based parameter storage and
     * the {@code ParCollection}'s push service should be used to receive
     * parameter values during runtime.
     * 
     * @param paramName  Der Name des Parameters.
     * 
     * @return  Der Wert des Parameters, falls er gefunden wurde, null sonst.
     */
    @Deprecated
    public String getParValueString(final String paramName) {
        if (this.getParValue(paramName) == null) {
            StaticMethods.logStage1(
                    "The parameter '" + paramName + "' has not been instantiated. Using standard value.", 
                    this);
            return Datatypes.ALL_TIME_STANDARD_STRING;
        }
        return ((String) this.getParValue(paramName)).replace("\"", "");
    }
    
    /**
     * Gibt den (ersten) Parameter mit dem übergebenen Namen zurück, wobei
     * er automatisch zum angegebenen Typ gecastet wird, falls das möglich ist.
     * Falls nicht, wird der übliche Fehler geworfen.</BR>
     * </BR>
     * This method returns a standard value if no value is stored.</BR>
     * </BR>
     * The method is deprecated as of EAS version 1_0, and should not be
     * used anymore. Instead, the refactoring-based parameter storage and
     * the {@code ParCollection}'s push service should be used to receive
     * parameter values during runtime.
     * 
     * @param paramName  Der Name des Parameters.
     * 
     * @return  Der Wert des Parameters, falls er gefunden wurde, null sonst.
     */
    @Deprecated
    public Integer getParValueInt(final String paramName) {
        if (this.getParValue(paramName) == null) {
            StaticMethods.logStage1(
                    "The parameter '" + paramName + "' has not been instantiated. Using standard value.", 
                    this);
            return Datatypes.ALL_TIME_STANDARD_INT;
        }

        return (Integer) this.getParValue(paramName);
    }
    
    /**
     * Gibt den (ersten) Parameter mit dem übergebenen Namen zurück, wobei
     * er automatisch zum angegebenen Typ gecastet wird, falls das möglich ist.
     * Falls nicht, wird der übliche Fehler geworfen.</BR>
     * </BR>
     * This method returns a standard value if no value is stored.</BR>
     * </BR>
     * The method is deprecated as of EAS version 1_0, and should not be
     * used anymore. Instead, the refactoring-based parameter storage and
     * the {@code ParCollection}'s push service should be used to receive
     * parameter values during runtime.
     * 
     * @param paramName  Der Name des Parameters.
     * 
     * @return  Der Wert des Parameters, falls er gefunden wurde, null sonst.
     */
    @Deprecated
    public Long getParValueLong(final String paramName) {
        if (this.getParValue(paramName) == null) {
            StaticMethods.logStage1(
                    "The parameter '" + paramName + "' has not been instantiated. Using standard value.", 
                    this);
            return Datatypes.ALL_TIME_STANDARD_LONG;
        }
        return (Long) this.getParValue(paramName);
    }
    
    /**
     * Gibt den (ersten) Parameter mit dem übergebenen Namen zurück, wobei
     * er automatisch zum angegebenen Typ gecastet wird, falls das möglich ist.
     * Falls nicht, wird der übliche Fehler geworfen.</BR>
     * </BR>
     * This method returns a standard value if no value is stored.</BR>
     * </BR>
     * The method is deprecated as of EAS version 1_0, and should not be
     * used anymore. Instead, the refactoring-based parameter storage and
     * the {@code ParCollection}'s push service should be used to receive
     * parameter values during runtime.
     * 
     * @param paramName  Der Name des Parameters.
     * 
     * @return  Der Wert des Parameters, falls er gefunden wurde, null sonst.
     */
    @Deprecated
    public Double getParValueDouble(final String paramName) {
        if (this.getParValue(paramName) == null) {
            StaticMethods.logStage1(
                    "The parameter '" + paramName + "' has not been instantiated. Using standard value.", 
                    this);
            return Datatypes.ALL_TIME_STANDARD_DOUBLE;
        }
        return (Double) this.getParValue(paramName);
    }
    
    /**
     * Gibt den (ersten) Parameter mit dem übergebenen Namen zurück, wobei
     * er automatisch zum angegebenen Typ gecastet wird, falls das möglich ist.
     * Falls nicht, wird der übliche Fehler geworfen.</BR>
     * </BR>
     * This method returns a standard value if no value is stored.</BR>
     * </BR>
     * The method is deprecated as of EAS version 1_0, and should not be
     * used anymore. Instead, the refactoring-based parameter storage and
     * the {@code ParCollection}'s push service should be used to receive
     * parameter values during runtime.
     * 
     * @param paramName  Der Name des Parameters.
     * 
     * @return  Der Wert des Parameters, falls er gefunden wurde, null sonst.
     */
    @Deprecated
    public Vector2D getParValueVector2D(final String paramName) {
        if (this.getParValue(paramName) == null) {
            StaticMethods.logStage1(
                    "The parameter '" + paramName + "' has not been instantiated. Using standard value.", 
                    this);
            return Datatypes.ALL_TIME_STANDARD_VECTOR2D;
        }
        return (Vector2D) this.getParValue(paramName);
    }
    
    /**
     * Gibt den (ersten) Parameter mit dem übergebenen Namen zurück, wobei
     * er automatisch zum angegebenen Typ gecastet wird, falls das möglich ist.
     * Falls nicht, wird der übliche Fehler geworfen.</BR>
     * </BR>
     * This method returns a standard value if no value is stored.</BR>
     * </BR>
     * The method is deprecated as of EAS version 1_0, and should not be
     * used anymore. Instead, the refactoring-based parameter storage and
     * the {@code ParCollection}'s push service should be used to receive
     * parameter values during runtime.
     * 
     * @param paramName  Der Name des Parameters.
     * 
     * @return  Der Wert des Parameters, falls er gefunden wurde, null sonst.
     */
    @Deprecated
    public Boolean getParValueBoolean(final String paramName) {
        if (this.getParValue(paramName) == null) {
            StaticMethods.logStage1(
                    "The parameter '" + paramName + "' has not been instantiated. Using standard value.", 
                    this);
            return Datatypes.ALL_TIME_STANDARD_BOOLEAN;
        }
        return (Boolean) this.getParValue(paramName);
    }
    
    /**
     * Gibt den (ersten) Parameter mit dem übergebenen Namen zurück, wobei
     * er automatisch zum angegebenen Typ gecastet wird, falls das möglich ist.
     * Falls nicht, wird der übliche Fehler geworfen.</BR>
     * </BR>
     * This method returns a standard value if no value is stored.</BR>
     * </BR>
     * The method is deprecated as of EAS version 1_0, and should not be
     * used anymore. Instead, the refactoring-based parameter storage and
     * the {@code ParCollection}'s push service should be used to receive
     * parameter values during runtime.
     * 
     * @param paramName  Der Name des Parameters.
     * 
     * @return  Der Wert des Parameters, falls er gefunden wurde, null sonst.
     */
    @Deprecated
    public Matrix getParValueMatrix(final String paramName) {
        if (this.getParValue(paramName) == null) {
            StaticMethods.logStage1(
                    "The parameter '" + paramName + "' has not been instantiated. Using standard value.", 
                    this);
            return Datatypes.ALL_TIME_STANDARD_MATRIX;
        }
        return (Matrix) this.getParValue(paramName);
    }
    
    /**
     * Gibt den (ersten) Parameter mit dem übergebenen Namen zurück, wobei
     * er automatisch zum angegebenen Typ gecastet wird, falls das möglich ist.
     * Falls nicht, wird der übliche Fehler geworfen.</BR>
     * </BR>
     * This method returns a standard value if no value is stored.</BR>
     * </BR>
     * The method is deprecated as of EAS version 1_0, and should not be
     * used anymore. Instead, the refactoring-based parameter storage and
     * the {@code ParCollection}'s push service should be used to receive
     * parameter values during runtime.
     * 
     * @param paramName  Der Name des Parameters.
     * 
     * @return  Der Wert des Parameters, falls er gefunden wurde, null sonst.
     */
    @Deprecated
    public ArrayListString getParValueArrayListString(
            final String paramName) {
        if (this.getParValue(paramName) == null) {
            StaticMethods.logStage1(
                    "The parameter '" + paramName + "' has not been instantiated. Using standard value.", 
                    this);
            return Datatypes.ALL_TIME_STANDARD_STRING_VECTOR;
        }
        return (ArrayListString) this.getParValue(paramName);
    }
    
    /**
     * Gibt den (ersten) Parameter mit dem übergebenen Namen zurück, wobei
     * er automatisch zum angegebenen Typ gecastet wird, falls das möglich ist.
     * Falls nicht, wird der übliche Fehler geworfen.</BR>
     * </BR>
     * This method returns a standard value if no value is stored.</BR>
     * </BR>
     * The method is deprecated as of EAS version 1_0, and should not be
     * used anymore. Instead, the refactoring-based parameter storage and
     * the {@code ParCollection}'s push service should be used to receive
     * parameter values during runtime.
     * 
     * @param paramName  Der Name des Parameters.
     * 
     * @return  Der Wert des Parameters, falls er gefunden wurde, null sonst.
     */
    @Deprecated
    public ArrayListInt getParValueArrayListInt(
            final String paramName) {
        if (this.getParValue(paramName) == null) {
            StaticMethods.logStage1(
                    "The parameter '" + paramName + "' has not been instantiated. Using standard value.", 
                    this);
            return Datatypes.ALL_TIME_STANDARD_INT_VECTOR;
        }
        return (ArrayListInt) this.getParValue(paramName);
    }
    
    /**
     * Gibt den (ersten) Parameter mit dem übergebenen Namen zurück, wobei
     * er automatisch zum angegebenen Typ gecastet wird, falls das möglich ist.
     * Falls nicht, wird der übliche Fehler geworfen.</BR>
     * </BR>
     * This method returns a standard value if no value is stored.</BR>
     * </BR>
     * The method is deprecated as of EAS version 1_0, and should not be
     * used anymore. Instead, the refactoring-based parameter storage and
     * the {@code ParCollection}'s push service should be used to receive
     * parameter values during runtime.
     * 
     * @param paramName  Der Name des Parameters.
     * 
     * @return  Der Wert des Parameters, falls er gefunden wurde, null sonst.
     */
    @Deprecated
    public ArrayListLong getParValueArrayListLong(
            final String paramName) {
        if (this.getParValue(paramName) == null) {
            StaticMethods.logStage1(
                    "The parameter '" + paramName + "' has not been instantiated. Using standard value.", 
                    this);
            return Datatypes.ALL_TIME_STANDARD_LONG_VECTOR;
        }
        return (ArrayListLong) this.getParValue(paramName);
    }
    
    /**
     * Gibt den (ersten) Parameter mit dem übergebenen Namen zurück, wobei
     * er automatisch zum angegebenen Typ gecastet wird, falls das möglich ist.
     * Falls nicht, wird der übliche Fehler geworfen.</BR>
     * </BR>
     * This method returns a standard value if no value is stored.</BR>
     * </BR>
     * The method is deprecated as of EAS version 1_0, and should not be
     * used anymore. Instead, the refactoring-based parameter storage and
     * the {@code ParCollection}'s push service should be used to receive
     * parameter values during runtime.
     * 
     * @param paramName  Der Name des Parameters.
     * 
     * @return  Der Wert des Parameters, falls er gefunden wurde, null sonst.
     */
    @Deprecated
    public ArrayListDouble getParValueArrayListDouble(final String paramName) {
        if (this.getParValue(paramName) == null) {
            StaticMethods.logStage1(
                    "The parameter '" + paramName + "' has not been instantiated. Using standard value.", 
                    this);
            return Datatypes.ALL_TIME_STANDARD_DOUBLE_VECTOR;
        }
        return (ArrayListDouble) this.getParValue(paramName);
    }
    
    /**
     * Gibt den (ersten) Parameter mit dem übergebenen Namen zurück, wobei
     * er automatisch zum angegebenen Typ gecastet wird, falls das möglich ist.
     * Falls nicht, wird der übliche Fehler geworfen.</BR>
     * </BR>
     * This method returns a standard value if no value is stored.</BR>
     * </BR>
     * The method is deprecated as of EAS version 1_0, and should not be
     * used anymore. Instead, the refactoring-based parameter storage and
     * the {@code ParCollection}'s push service should be used to receive
     * parameter values during runtime.
     * 
     * @param paramName  Der Name des Parameters.
     * 
     * @return  Der Wert des Parameters, falls er gefunden wurde, null sonst.
     */
    @Deprecated
    public ArrayListVec2D getParValueArrayListVector2D(final String paramName) {
        if (this.getParValue(paramName) == null) {
            StaticMethods.logStage1(
                    "The parameter '" + paramName + "' has not been instantiated. Using standard value.", 
                    this);
            return Datatypes.ALL_TIME_STANDARD_VECTOR2D_VECTOR;
        }
        return (ArrayListVec2D) this.getParValue(paramName);
    }
    
    /**
     * Gibt den (ersten) Parameter mit dem übergebenen Namen zurück, wobei
     * er automatisch zum angegebenen Typ gecastet wird, falls das möglich ist.
     * Falls nicht, wird der übliche Fehler geworfen.</BR>
     * </BR>
     * This method returns a standard value if no value is stored.</BR>
     * </BR>
     * The method is deprecated as of EAS version 1_0, and should not be
     * used anymore. Instead, the refactoring-based parameter storage and
     * the {@code ParCollection}'s push service should be used to receive
     * parameter values during runtime.
     * 
     * @param parName  Der Name des Parameters.
     * 
     * @return  Der Wert des Parameters, falls er gefunden wurde, null sonst.
     */
    @Deprecated
    public ArrayListBool getParValueArrayListBoolean(final String parName) {
        if (this.getParValue(parName) == null) {
            StaticMethods.logStage1(
                    "The parameter '" + parName + "' has not been instantiated. Using standard value.", 
                    this);
            return Datatypes.ALL_TIME_STANDARD_BOOLEAN_VECTOR;
        }
        return (ArrayListBool) this.getParValue(parName);
    }
    
    /**
     * Returns the parameter value belonging to the given key.</BR>
     * </BR>
     * This method DOES NOT return a standard value if no value is stored.
     * Use this method to check which parameters were explicitly set. Note
     * that there is no notification if a parameter actually exists or not.
     * In both cases, null might be returned.</BR>
     * </BR>
     * The method is deprecated as of EAS version 1_0, and should not be
     * used anymore. Instead, the refactoring-based parameter storage and
     * the {@code ParCollection}'s push service should be used to receive
     * parameter values during runtime.
     * 
     * @param paramName  The name (key) of the parameter.
     * 
     * @return  The value of the parameter, if it has been found, null otherwise.
     */
    @Deprecated
    public Object getParValue(final String paramName) {
        ArrayList<SingleParameter> allePars = this.getAllPars();
        for (SingleParameter p : allePars) {
            if (p.getParameterName().equalsIgnoreCase(paramName)) {
                return p.getParValue();
            }
        }
        
        return null;
    }

    /**
     * Setzt den (ersten) Parameter mit dem übergebenen Namen auf den 
     * übergebenen Wert, falls er in den GENERISCHEN Parametern gefunden wurde.
     * ACHTUNG: statische Parameter können mit dieser Methode nicht gesetzt 
     * werden.</BR>
     * </BR>
     * Falls der Parameter nicht existiert, wird eine Warnung im Debug-Modus
     * ausgegeben und false zurückgegeben.</BR>
     * 
     * @param paramName  Der Name des generischen Parameters.
     * @param value       Der Wert des Parameters.
     * 
     * @return  Ob der Parameter existiert (und dementsprechend nach der
     *          Zuweisung garantiert den übergebenen Wert angenommen hat).
     */
    public boolean setParValue(final String paramName, final Object value) {
        String oldValue;
        this.arr = null;
        
        for (SingleParameter p : this.generischeParams) {
            if (p.getParameterName().equalsIgnoreCase(paramName)) {
                if (!p.getParValue().equals(value)) {
                    oldValue = p.getParValue().toString();
                    
                    // Falls Parameterwert zu Parametertyp passt: Standard.
                    if (p.getParameterType().equals(Datatypes.STRING) || !value.getClass().isAssignableFrom(String.class)) {
                        p.setParValue(value);
                        this.pushToListener(value, p);
                    } else { // Falls nicht, versuche richtigen Typ aus String zu parsen.
                        try {
                            this.setParValueFromString((String) value, p);
                        } catch (Exception e) {
                        }
                    }
                    
                    if (oldValue.equals(p.getParValue())) {
                        StaticMethods.log(StaticMethods.LOG_WARNING, 
                                "Parameter " 
                                    + paramName 
                                    + " has been changed from "
                                    + oldValue
                                    + " to "
                                    + value, 
                                this);
                    }
                }
                
                return true;
            }
        }

        this.logDebug(
                "<" + this.getClass().getSimpleName() + "> Parameter " 
                    + paramName
                    + " not found. Value '" 
                    + value
                    + "' has not been set.");
        
        return false;
    }

    /**
     * ergänzt fehlende Werte durch die Konstanten oder sonstige Standardwerte.
     */
    public void complete() {
        if (this.seed == null) {
            this.seed = new Time(System.currentTimeMillis()).getTime();
        }
        if (this.logStufe == null) {
            this.logStufe = ConstantsStatistics.STANDARD_LOG_STUFE;
        }
        if (this.standardVerz == null) {
            this.standardVerz = eas.startSetup.marbBuilder.ConstantsGraphVis.STD_PFAD;
        }
        if (this.plugins == null) {
            this.plugins = new String[1];
            this.plugins[0] = PluginFactory.STD_PLUGIN;
        }
        if (this.getJoschkaJAR() == null) {
            this.setJoschkaJAR(ConstantsSimulation.JOSCHKA_JAR);
        }
        if (this.getJoschkaPlatt() == null) {
            this.setJoschkaPlatt(ConstantsSimulation.JOSCHKA_PLATT);
        }
        if (this.getJoschkaUser() == null) {
            this.setJoschkaUser(ConstantsSimulation.JOSCHKA_USER);
        }
        if (this.getJoschkaVMpar() == null) {
            this.setJoschkaVMpar(ConstantsSimulation.JOSCHKA_VM);
        }
        if (this.getJoschkaVerz() == null) {
            this.setJoschkaVerz(ConstantsSimulation.JOSCHKA_VERZ);
        }
        if (this.getjoschkaCP() == null) {
            this.setjoschkaCP(ConstantsSimulation.JOSCHKA_CP);
        }
        if (this.getstarterSourceDirectory() == null) {
            this.setstarterSourceDirectory(ConstantsGraphVis.STD_STARTER_PFAD);
        }
        if (this.getJoSchKaJavaBinCommand() == null) {
            this.setJoSchKaJavaBinCommand(ConstantsSimulation.JOSCHKA_JAVA_PARAMETER_COMMAND);
        }
        if (this.masterScheduler == null) {
            this.masterScheduler 
                = eas.plugins.PluginFactory.STD_MASTER_SCHEDULER;
        }
        if (this.startImmediately == null) {
            this.startImmediately 
                = eas.simulation.ConstantsSimulation.DEFAULT_START_IMMEDIATELY;
        }
        if (this.actionOnUncaughtException == null) {
            this.actionOnUncaughtException = eas.simulation.ConstantsSimulation.ACTION_ON_UNCAUGHT_EXCEPTION_STANDARD;
        }
        if (this.forceExitAfterSimulationTerminates == null) {
            this.forceExitAfterSimulationTerminates = false;
        }
    }

    public void setJoSchKaJavaBinCommand(String value) {
        this.setJoSchKaParameter(ConstantsSimulation.JOSCHKA_JAVA_PARAMETER_COMMAND_NAME, value);
    }

    public Object getJoSchKaJavaBinCommand() {
        return this.getJoSchKaParameter(ConstantsSimulation.JOSCHKA_JAVA_PARAMETER_COMMAND_NAME);
    }

    /**
     * Generiert eine ID-Ausgabe des Programms mit Namen, Versionsnummer und
     * Java-Version.
     * 
     * @return  Der Kopf.
     */
    public String generiereKopf() {
        String s = "";
        s = s + GlobalVariables.PROG_NAME_SHORT_EAS + " version " + GlobalVariables.PROG_VERSION_EAS;
        s = s + " (Java version: " + GlobalVariables.JAVA_VERSION + ")";
        
        return s;
    }

    /**
     * @return  String, der die Parameter als Kommandozeilen-Argumente 
     *          ausgibt. 
     */
    public String parStrPlain() {
        String s = "";

        for (SingleParameter par : this.getAllPars()) {
            s = s + par.getParameterName() + " " + par.getParValue() + "\n";
        }
        
        return s;
    }
    
    /**
     * @return  A parameter view as a list of arguments that can directly be
     *          passed to a main method or the constructor of the 
     *          ParCollection.
     */
    public String[] getAllParsArrayView() {
        return StaticMethods.processStringAsCommandLineParameters(this.parStrPlain().replace('\n', ' '));
    }
    
    @Override
    public String toString() {
        String s1 = this.generiereKopf();
        String s = s1;
        String t = "";
        
        s = "\n\n\n" + Ascii3D.create3D(GlobalVariables.PROG_NAME_SHORT_EAS) + s;

        for (int i = 0; i < s1.length(); i++) {
            t += "=";
        }
        
        s = s + "\n" + t;
        s = s + "\nLoaded Parameters\n";
        s = s + "Time stamp: " + this.timeStamp + "\n";
        
        s = s + this.parStrPlain();
        
        return s;
    }

    public SingleParameter getSinglePar(final String parName) {
        ArrayList<SingleParameter> arr = this.getAllPars();
        for (int i = 0; i < arr.size(); i++) {
            SingleParameter returnPar = arr.get(i);
            if (returnPar.getParameterName().equals(parName)) {
                return returnPar;
            }
        }
        
        return null;
    }

    public ArrayList<SingleParameter> getAllPars() {
        if (arr == null) {
            generateAllPars();
        }
        
        return arr;
    }
    
    private ArrayList<SingleParameter> arr;
    
    /**
     * Alle Parameter werden zurückgegeben.
     * 
     * @return  Alle Parameternamen und ~Typen mit Standardwerten.
     */
    private void generateAllPars() {
        arr = new ArrayList<SingleParameter>(1000);
        arr.add(new SingleParameter(
                ConstantsSimulation.ACTION_UNCAUGHT_EXCEPTION, 
                Datatypes.fixedStringSet(new String[] {
                        ConstantsSimulation.ACTION_ON_UNCAUGHT_EXCEPTION_PRINT_AND_RECOVER,
                        ConstantsSimulation.ACTION_ON_UNCAUGHT_EXCEPTION_PRINT_AND_TERMINATE,
                        ConstantsSimulation.ACTION_ON_UNCAUGHT_EXCEPTION_PRINT_AND_HALT,
                        ConstantsSimulation.ACTION_ON_UNCAUGHT_EXCEPTION_DO_NOTHING,
                        ConstantsSimulation.ACTION_ON_UNCAUGHT_EXCEPTION_REMOVE_SCHEDULER_AND_RECOVER,
                        ConstantsSimulation.ACTION_ON_UNCAUGHT_EXCEPTION_ASK_WHAT_TO_DO}), 
                this.actionOnUncaughtException,
                "Action to perform when main loop catches an unhandled exception from a plugin (note that master schedulers will not be removed when using option '"
                        + ConstantsSimulation.ACTION_ON_UNCAUGHT_EXCEPTION_REMOVE_SCHEDULER_AND_RECOVER
                        + "').",
                "SIMULATION"));
        arr.add(new SingleParameter(
                ConstantsStatistics.SEED_ATTR, 
                Datatypes.LONG,
                this.seed,
                "Seed for random number generator (tripple-right-click in "
                + "yellow field for randomized seed)",
                "RANDOM_NUMBER_GENERATOR"));
        arr.add(new SingleParameter(
                ConstantsStatistics.LOG_ATTR, 
                Datatypes.integerRange(-1, 5),
                this.logStufe,
                "The logging console output level (0 = Output, 1 = Debug, 2 = Info, 3 = "
                + "Warning, 4 = Error; -1 = Stage1 (all); 5 = quiet)",
                "STATISTICS"));
        arr.add(new SingleParameter(
                ConstantsStatistics.START_IMMEDIATELY_ATT, 
                Datatypes.BOOLEAN,
                this.startImmediately,
                "Defines if the selected Master Scheduler will be 'started immediately' on program start,"
                + " i.e., without showing the Starter GUI first. Note that, once set to true (and stored), "
                + "you will not be able to show the GUI unless you "
                + "change the parameter back in the parameters file or as command line argument.",
                "PLUGINS"));
        arr.add(new SingleParameter(
                ConstantsStatistics.VERZEICHNIS_A, 
                Datatypes.STRING,
                this.standardVerz,
                "The standard directory for stored data",
                "SIMULATION"));
        String descriptionPlug = "List of plugins to include in the run. "
                + "Note that only plugins matching the currently selected Master Scheduler are shown in the dialog box. "
                + "There may be (rare) circumstances under which other plugins (or Master Schedulers) can be desired, too. "
                + "Those you have to type manually into the yellow field (separated by commas WITHOUT blanks).";
        arr.add(new SingleParameter(
                ConstantsStatistics.PLUGINS_PAR_NAME,
                Datatypes.STRING_ARR,
                new ArrayListString(this.plugins),
                descriptionPlug,
                "PLUGINS"));
        arr.add(new SingleParameter(
                ConstantsStatistics.MASTER_SCHEDULER_PAR_NAME,
                Datatypes.STRING,
                this.masterScheduler,
                "The master scheduler controlls the fundamental behavior of "
                + "a simulation run. It is an extended plugin that is invoked "
                + "before the other plugins are, and has additional features. "
                + "Selecting a master scheduler in fact means selecting the simulation "
                + "to run.",
                "PLUGINS"));
        arr.add(new SingleParameter(
                ConstantsSimulation.FORCE_EXIT_AFTER_SIMULATION_TERMINATES,
                Datatypes.BOOLEAN,
                this.forceExitAfterSimulationTerminates,
                "If the simulation is forced to exit after the run.",
                "SIMULATION"));
        
        ArrayListString joschkaParArray = new ArrayListString();
        
        for (String key : this.joschkaParameters.keySet()) {
            joschkaParArray.add(key + "=" + this.getJoSchKaParameter(key));
        }
        
        arr.add(new SingleParameter(
                ConstantsStatistics.JOSCHKA_PARAMETERS_NAME,
                Datatypes.STRING_ARR,
                joschkaParArray,
                "List of JoSchKa-Parameters as key-value pairs.",
                "JOSCHKA"));
        
        Collections.sort(arr, new ParamSort());
        
        // Generische Parameter.
        arr.addAll(this.generischeParams);
    }

    public void setActionOnUncaughtException(String name) {
        if (this.actionOnUncaughtException.equals(name)) {
            return;
        }
        
        SingleParameter p = this.getSinglePar(ConstantsSimulation.ACTION_UNCAUGHT_EXCEPTION);
        p.setParValue(name);
        
        if (!p.isConsistent()) {
            throw new RuntimeException(
                    "\n" 
                        + p.getParameterName() 
                        + " - Parameter value \"" 
                        + p.getParValue()
                        + "\" (" 
                        + p.getParValue().getClass() 
                        + ")"
                        + " does not match parameter datatype " 
                        + p.getParameterType()
                        + ".");
        }
        
        this.arr = null;
        this.actionOnUncaughtException = name;
        
        StaticMethods.log(StaticMethods.LOG_WARNING, 
                "<" + this.getClass().getSimpleName() + "> ACTION_ON_UNCAUGHT_EXCEPTION set to the following value: " + name, 
                      this);
    }
    
    /**
     * @param name The name to set.
     */
    public void setStdDirectory(final String name) {
        if (this.standardVerz.equals(name)) {
            return;
        }
        
        this.arr = null;
        this.standardVerz = name;

        StaticMethods.log(StaticMethods.LOG_WARNING, 
                "<" + this.getClass().getSimpleName() + "> STANDARDVERZ set to the following value: " + name, 
                      this);
    }
    
    /**
     * @param value The startImmediately to set.
     */
    public void setStartImmediately(Boolean value) {
        if (this.startImmediately.equals(value)) {
            return;
        }

        this.arr = null;
        this.startImmediately = value;
        
        StaticMethods.log(StaticMethods.LOG_WARNING, 
                "<" + this.getClass().getSimpleName() + "> STARTIMMEDIATELY set to the following value: " + value, 
                this);
    }
    
    /**
     * @param logStufe The logStufe to set.
     */
    public void setLoggingLevel(Integer value) {
        if (this.logStufe.equals(value)) {
            return;
        }

        this.logStufe = value;
        
        StaticMethods.log(StaticMethods.LOG_WARNING, 
                "<" + this.getClass().getSimpleName() + "> LOG set to the following value: " + value, 
                this);
    }
    
    /**
     * Gibt alle Nachrichten zurück, die einem bestimmten Format entsprechen.
     * 
     * @param type  Der Typ der Nachricht. Wenn der Typ <code>null</code> ist,
     *              werden alle Nachrichten zurückgegeben.
     * @param nach  Der fr�heste Zeitpunkt der Nachricht.
     * @param vor   Der späteste Zeitpunkt der Nachricht.
     * 
     * @return  Eine Liste von Nachrichten, die dem Muster entspricht.
     */
    public LinkedList<AbstractMsg> getMsgs(
            final String type,
            final long nach,
            final long vor) {
        LinkedList<AbstractMsg> liste = new LinkedList<AbstractMsg>();
        
        for (AbstractMsg m : this.msgs) {
            if (type == null || type.equals("") || type.equals(m.getType())) {
                if (m.getDatum() >= nach && m.getDatum() <= vor) {
                    liste.add(m);
                }
            }
        }

        return liste;
    }
    
    /**
     * löscht die aktuellen Nachrichten.
     */
    public void flushMsgs() {
        if (this.schreibschutz) {
            throw new RuntimeException("Parameter collection is write-protected, cannot "
                    + "flush messages.");
        }
        
        this.msgs.clear();
    }

    private boolean storeLogMessages = true;
    
    /**
     * If the logging messages are stored in a list that can be written
     * to a file or so. In most cases, you don't need this and it will just
     * consume a lot of memory. NOTE: Standard value is {@code true}!
     * 
     * @param storeLogMessages  Storing logging messages or not.
     */
    public void setStoreLogMessages(boolean storeLogMessages) {
        this.storeLogMessages = storeLogMessages;
    }
    
    /**
     * fügt eine Nachricht zum Parametersatz hinzu.
     * 
     * @param msg  Die hinzugefügte Nachricht.
     */
    public void addMsg(final AbstractMsg msg) {
        if (this.storeLogMessages) {
            if (this.schreibschutz) {
                throw new RuntimeException("Parameter collection is write-protected, cannot "
                        + "add message '" + msg + "'.");
            }
            
            this.msgs.add(msg);
        }
    }
    
    /**
     * Setzt den Parametersatz schreibgeschätzt - der Schreibschutz kann nicht
     * wieder aufgehoben werden. Achtung: Bei Schreibschutz werden Nachrichten
     * nicht gespeichert - es wird aber kein Fehler ausgegeben. Die Setter 
     * können weiterhin verwendet werden.
     */
    public void setWriteProtection() {
        StaticMethods.log(
                StaticMethods.LOG_INFO,
                "Par collection set to write protected.",
                this);
                this.schreibschutz = true;
    }

    /**
     * @return  An array of the IDs of all Plugins.
     */
    public String[] getPlugIDArray() {
        return this.plugins;
    }

    /**
     * Returns the plugin objects for this run. Note that the plugins are
     * newly constructed every time this method is invoked.
     * 
     * @return  The actual plugin objects.
     */
    public LinkedList<Plugin<?>> getPlugins() {
        String[] plugNames = this.getPlugIDArray();
        LinkedList<Plugin<?>> plugins = new LinkedList<Plugin<?>>();
        
        for (String plugName : plugNames) {
            plugins.add(PluginFactory.generatePluginObject(plugName, this));
        }
        
        return plugins;
    }

    /**
     * @param id  Die ID des Plugins.
     * 
     * @return  Ob ein Plugin dieses Typs existiert.
     */
    public boolean existsPlugin(final String id) {
        return this.getPlugIDList().contains(id);
    }

    /**
     * Gets a list of the IDs of all active plugins extracted from this.
     * 
     * @return  A list of the IDs of all active plugins extracted from 
     *          params.
     */
    public LinkedList<String> getPlugIDList() {
        String[] plugList = this.getPlugIDArray();
        LinkedList<String> plugNames = new LinkedList<String>();
        
        for (String p : plugList) {
            plugNames.add(p);
        }
        
        return plugNames;
    }

    /**
     * @return  The ID of the master scheduler.
     */
    public String getMasterSchedulerID() {
        return this.masterScheduler;
    }
 
    /**
     * @return  A newly generated copy of the master scheduler.
     */
    public MasterScheduler<?> getMasterScheduler() {
        return (MasterScheduler<?>) PluginFactory.generatePluginObject(this
                .getMasterSchedulerID(), this);
    }
    
    /**
     * Logging.
     * 
     * @param level           The logging level as defined in StaticMethods.
     * @param loggingMessage  The message to log.
     */
    public void log(int level, String loggingMessage) {
        StaticMethods.log(level, loggingMessage, this);
    }
    
    public void logP(int level, String loggingMessage) {
        StaticMethods.log(level, loggingMessage, this, "plain", null);
    }
    
    /**
     * Logging.
     * 
     * @param loggingMessage  The message to log using a standard logging level.
     */
    public void logWeb(final String s) {
        this.log(StaticMethods.LOG_WEB, s);
    }
    
    /**
     * Logging.
     * 
     * @param loggingMessage  The message to log using a standard logging level.
     */
    public void logError(final String s) {
        this.log(StaticMethods.LOG_ERROR, s);
    }

    /**
     * Logging.
     * 
     * @param loggingMessage  The message to log using a standard logging level.
     */
    public void logWarning(final String s) {
        this.log(StaticMethods.LOG_WARNING, s);
    }

    /**
     * Logging.
     * 
     * @param loggingMessage  The message to log using a standard logging level.
     */
    public void logInfo(final String s) {
        this.log(StaticMethods.LOG_INFO, s);
    }

    /**
     * Logging.
     * 
     * @param loggingMessage  The message to log using a standard logging level.
     */
    public void logDebug(final String s) {
        this.log(StaticMethods.LOG_DEBUG, s);
    }

    /**
     * Logging.
     * 
     * @param loggingMessage  The message to log using a standard logging level.
     */
    public void logOutput(final String s) {
        this.log(StaticMethods.LOG_OUTPUT, s);
    }

    /**
     * Logging.
     * 
     * @param loggingMessage  The message to log using a standard logging level.
     */
    public void logStage1(final String s) {
        this.log(StaticMethods.LOG_STAGE1, s);
    }
    
    /**
     * Logging.
     * 
     * @param loggingMessage  The message to log using a standard logging level.
     */
    public void logWebP(final String s) {
        this.logP(StaticMethods.LOG_WEB, s);
    }
    
    /**
     * Logging.
     * 
     * @param loggingMessage  The message to log using a standard logging level.
     */
    public void logErrorP(final String s) {
        this.logP(StaticMethods.LOG_ERROR, s);
    }

    /**
     * Logging.
     * 
     * @param loggingMessage  The message to log using a standard logging level.
     */
    public void logWarningP(final String s) {
        this.logP(StaticMethods.LOG_WARNING, s);
    }

    /**
     * Logging.
     * 
     * @param loggingMessage  The message to log using a standard logging level.
     */
    public void logInfoP(final String s) {
        this.logP(StaticMethods.LOG_INFO, s);
    }

    /**
     * Logging.
     * 
     * @param loggingMessage  The message to log using a standard logging level.
     */
    public void logDebugP(final String s) {
        this.logP(StaticMethods.LOG_DEBUG, s);
    }

    /**
     * Logging.
     * 
     * @param loggingMessage  The message to log using a standard logging level.
     */
    public void logOutputP(final String s) {
        this.logP(StaticMethods.LOG_OUTPUT, s);
    }

    /**
     * Logging.
     * 
     * @param loggingMessage  The message to log using a standard logging level.
     */
    public void logStage1P(final String s) {
        this.logP(StaticMethods.LOG_STAGE1, s);
    }

    /**
     * Returns a dummy parameters collection to use in programs ouside of the 
     * main EAS program only!
     * <BR><BR>
     * CAUTION: There is no case (I could imagine) where this method should be
     * used in a regular EAS run. In all regular cases, a run's UNIQUE parameter 
     * collection can be retrieved by {@link GlobalVariables#getParameters()} or
     * by several fields and parameters throughout the main classes storing the 
     * collection for convenience. And even hacking into the EAS core can and
     * should be done using the protected constructor rather than this method.
     * 
     * @param args  The parameters.
     * @return  A dummy parameter collection to use for dirty, dirty stuff only.
     */
    public static ParCollection getNewDummyParCollectionUseForDirtyTempProgramsOnly_Seriously(String[] args) {
        return new ParCollection(args);
    }
    
    /**
     * Performs actions to keep the parameters consistent after resuming a
     * serialized simulation. This includes:<BR/>
     *  - Re-initializing Java fields connected to program parameters.
     */
    public void onSimulationResumed() {
        this.logInfo("The " 
                + this.getClass().getSimpleName() 
                + "'s push service is re-initializing all fields connected to program parameters.\n"
                + "I'm not using setters to avoid triggering reset procedures.");
        
        for (SingleParameter p : this.getAllPars()) {
            if (p.getStaticListenerClass() != null) {
                this.pushDirectlyToField(p.getParValue(), p, p.getStaticListenerClass());
                this.logDebug("Re-initialized " + p.getParameterName() + " to '" + p.getParValue() + "' (" + p.getDescription() + ")");
            }
        }
    }
    
    /**
     * Adds a plugin to the parameter collection. This on its own will not
     * register the plugin at the SimulationTime; you can do this by using
     * the SimulationTime's registerPlugin method.
     * 
     * @param pluginID  The plugin to add. Note that a plugin id can only be
     *                  set once using this method (i.e., no double plugins
     *                  are allowed).
     */
    public void addPluginIncludingParameters(String pluginID) {
        for (String plug : this.plugins) {
            if (plug.equals(pluginID)) {
                return; // Plugin already avaliable.
            }
        }
            
        String[] newPlugins = new String[this.plugins.length + 1];
        for (int i = 0; i < this.plugins.length; i++) {
            newPlugins[i] = this.plugins[i];
        }
        newPlugins[newPlugins.length - 1] = pluginID;
        this.setPlugins(newPlugins);
        this.regGenParams();
        this.pushPluginParametersToListeners(PluginFactory.generatePluginObject(pluginID, this));

        StaticMethods.logInfo("<" + this.getClass().getSimpleName() + "> PLUGIN added: '" + pluginID + "'.", this);
        StaticMethods.logInfo("<" + this.getClass().getSimpleName() + "> Plugin parameters have been registered and values have been pushed to listeners.", this);
    }
    
    public void setMasterIncludingParameters(String masterID) {
        if (this.masterScheduler.equals(masterID)) {
            return;
        }
        
        this.arr = null;
        this.masterScheduler = masterID;
        this.regGenParams();
        this.pushPluginParametersToListeners(PluginFactory.generatePluginObject(masterID, this));
        
        StaticMethods.logInfo("<" + this.getClass().getSimpleName() + "> MASTERSCHEDULER set: '" + masterID + "'.", this);
        StaticMethods.logInfo("<" + this.getClass().getSimpleName() + "> Plugin parameters have been registered and values have been pushed to listeners.", this);
    }
}
