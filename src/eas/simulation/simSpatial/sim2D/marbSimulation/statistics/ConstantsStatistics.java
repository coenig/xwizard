/*
 * Datei: Konstanten.java
 * Autor(en):        Lukas König
 * Java-Version:     6.0
 * Erstellt (vor):   02.04.2008
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

package eas.simulation.simSpatial.sim2D.marbSimulation.statistics;

import eas.GlobalVariables;

/**
 * Die Konstanten des Pakets statistik.
 * 
 * @author Lukas König
 */
public class ConstantsStatistics {
    
    public static final String USE_ONLY_STD_DECODING = "useOnlyStdDecoding";
    
    public static final String START_IMMEDIATELY_ATT = "startimmediately";

    /**
     * Der Quellpfad für den Starter.
     */
    public static final String STARTER_QUELL_PFAD = "starterSourceDirectory";

    /**
     * The parameter name of the root scheduler.
     */
    public static final String MASTER_SCHEDULER_PAR_NAME = "masterScheduler";
    
    /**
     * Attribut für die JoSchKa-Parameter-Name.
     */
    public static final String JOSCHKA_PARAMETERS_NAME = "joschkaParameters";

    /**
     * Attribut für die JoSchKa-JAR-Datei.
     */
    public static final String JOSCHKA_JAR = "joschkajar";

    /**
     * Attribut für die JoSchKa-Plattform.
     */
    public static final String JOSCHKA_PLATT = "joschkaplatform";
    
    /**
     * Attribut für den JoSchKa-User.
     */
    public static final String JOSCHKA_USER = "joschkauser";
    
    /**
     * Attribut für die JoSchKa-VM.
     */
    public static final String JOSCHKA_VM = "joschkavmparameter";

    /**
     * Attribut für das JoSchKa-Verzeichnis.
     */
    public static final String JOSCHKA_VERZ = "joschkadirectory";

    /**
     * Der JoSchKa-Classpath.
     */
    public static final String JOSCHKA_CP = "joschkaclasspath";
    
    /**
     * Attribut für Anzahl der Simulationsschritte, nach denen in den 
     * Simulationsmodus umgeschaltet werden soll (Beenden der Evolution und 
     * Neuplatzieren der Roboter sowie zurücksetzen ihrer Fitness). 
     */
    public static final String SIM_UMSCH_ATTR = "umschaltsim";
    
    /**
     * Das Attribut für Plugins.
     */
    public static final String PLUGINS_PAR_NAME = "plugin";
    
    /**
     * Das Attribut für die Art der Fitnessberechnung.
     */
    public static final String FIT_VERFAHREN_AT = "fitnessverfahren";
    
    /**
     * Das Attribut für das Rekombinationsverfahren.
     */
    public static final String REK_VERFAHREN_AT = "rekart";
    
    /**
     * Das Attribut für die Art der Verhaltensmutation.
     */
    public static final String MUT_ART_VERH_AT = "mutartverh";

    /**
     * Das Attribut für die Art der Verhaltensmutation.
     */
    public static final String MUT_ART_TRANS_AT = "mutarttrans";
    
    /**
     * Attribut (siehe eas.endlAutomat.mutation.mutationSeq.Konstanten).
     */
    public static final String A_MUT_TR_STDABW = "mutTransStdAbw";
    
    /**
     * Attribut.
     */
    public static final String A_MUT_VE_STDABW = "mutVerhStdAbw";
    
    /**
     * Attribut.
     */
    public static final String A_MUT_TR_WKEIT = "mutTransWkeit";
    
    /**
     * Attribut.
     */
    public static final String A_MUT_VE_WKEIT = "mutVerhWkeit";
    
    /**
     * Attribut.
     */
    public static final String A_MIN_FAKTOR_TR = "mutMinFaktorTrans";
    
    /**
     * Attribut.
     */
    public static final String A_MAX_FAKTOR_TR = "mutMaxFaktorTrans";
    
    /**
     * Attribut.
     */
    public static final String A_MIN_FAKTOR_VE = "mutMinFaktorVerh";
    
    /**
     * Attribut.
     */
    public static final String A_MAX_FAKTOR_VE = "mutMaxFaktorVerh";
    
    /**
     * Attribut.
     */
    public static final String A_SEQ_LEN_KON_TR = "mutSeqLenKonstTrans";
    
    /**
     * Attribut.
     */
    public static final String A_SEQ_LEN_KON_VE = "mutSeqLenKonstVerh";

    /**
     * Der Name des Attributs für Translatormutationszyklen.
     */
    public static final String MUT_TR_ZYK_ATT = "muttranszyk";
    
    /**
     * Der Attributname für das Kompatibilit�tsmodus-Attribut.
     */
    public static final String KOMP_MOD_ATT = "kompatibel";
    
    /**
     * Der Standard-Kompatibilit�tsmodus.
     */
    public static final String STD_KOMP_MODUS = "mostrecent";
    
    /**
     * Das Attribut für den PNG-Parameter:
     * 
     * Ob automatisch beim Speichern eines Automaten auch eine Bilddatei mit
     * dem abgebildeten Automaten im PNG-Format gespeichert werden soll.
     */
    public static final String PNG_ATTRIBUT = "autpng";
    
    /**
     * Die Bezierkonstante.
     */
    public static final String BEZIER_KONST = "bezier";
    
    /**
     * Das Darstellungsattribut.
     */
    public static final String EINFACHE_DARSTELLUNG = "einfach";
    
    /**
     * Die Endung des Dateinamens von Statistikdateien.
     */
    public static final String STAT_DAT_ENDUNG = "dat";
    
    /**
     * Das Bedingungsmutationsattribut.
     */
    public static final String COND_MUT_AT = "condmut";
    
    /**
     * Das Anzeigemodusattribut.
     */
    public static final String ANZ_MOD_ATTR = "anzeige";
    
    /**
     * Der Name des Mutationsattributs.
     */
    public static final String MUT_ZYK_ATTR = "mut";

    /**
     * Der Name des Rekombinationsattributs.
     */
    public static final String REK_ZYK_ATTR = "rek";

    /**
     * Der Name des Fitnessatributs.
     */
    public static final String FIT_ZYK_ATTR = "fit";

    /**
     * Der Name des Fitnessreduktionsattributs.
     */
    public static final String FIT_EVAP_ZYK_ATTR = "fitredzyk";

    /**
     * Der Name des Fitnessreduktionswertattributs.
     */
    public static final String FIT_EVAP_VAL_ATTR = "fitredwert";

    /**
     * Der Name des ExperimentLängeattributs.
     */
    public static final String EXP_LEN_ATTR = "simulationlength";

    /**
     * Der Name des Seedattributs.
     */
    public static final String SEED_ATTR = "seed";
    
    /**
     * Der Name des Logging-Attributs.
     */
    public static final String LOG_ATTR = "log";

    /**
     * Der Name des Verzeichnis-Attributs.
     */
    public static final String VERZEICHNIS_A = "directory";
    
    /**
     * Name des Attributs für den Fitnessabzug im Fall eines Unfalls.
     */
    public static final String UNFALL_ATTR = "unfall";

    /**
     * Das Attribut, das das Memoryinterval für Translatoren bezeichnet.
     */
    public static final String MEM_AT_TRANS = "memtrans";

    /**
     * Das Attribut, das das Memoryinterval bezeichnet.
     */
    public static final String REK_NORM = "reknorm";

    /**
     * Das Attribut, das das Memoryinterval bezeichnet.
     */
    public static final String REK_KIND = "rekkinder";

    /**
     * Das Attribut, das das Memoryinterval bezeichnet.
     */
    public static final String REK_ELT = "rekeltern";

    /**
     * Das Attribut, das das Memoryinterval bezeichnet.
     */
    public static final String REK_MAX_SIZE = "rekmaxsize";

    /**
     * Das Attribut, das bezeichnet, ob evolviert werden soll.
     */
    public static final String EVOL_ATTR = "evolution";
    
    /**
     * Attribut für die Mindestverzögerung zwischen zwei Simulationsschritten.
     */
    public static final String VERZOEG_ATTR = "verzoegerung";
    
    /**
     * Die Standardstufe für das Logging.
     */
    public static final int STANDARD_LOG_STUFE = 0;
    
    /**
     * Der Name des Verzerr-Attributs.
     */
    public static final String VERZERR_ATTR = "verzerrung";
    
    public static void main(String[] args) {
        GlobalVariables.getParameters().logInfo(
                "This is EAS version " 
                        + GlobalVariables.PROG_VERSION_EAS
                        + " (Java version "
                        + GlobalVariables.JAVA_VERSION
                        + ").");
    }
}
