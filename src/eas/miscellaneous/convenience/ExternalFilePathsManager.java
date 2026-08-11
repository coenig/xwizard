/*
 * File name:        ExternalFilePathsManager.java (package eas.miscellaneous.system.windowFrames)
 * Author(s):        Lukas König
 * Java version:     8.0 (at generation time)
 * Generation date:  30.12.2014 (12:25:28)
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

package eas.miscellaneous.convenience;

import java.awt.FileDialog;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import javax.swing.JFileChooser;
import javax.swing.JFrame;

import eas.GlobalVariables;
import eas.miscellaneous.StaticMethods;
import eas.miscellaneous.system.FileNamePostfixFilter;
import eas.veryFastPDF.GraphVizEASVersion;

/**
 * @author Lukas König
 */
public class ExternalFilePathsManager {
    
    // Register all file path IDs here (non-registered ids will be removed).
    private static final HashSet<String> registeredFileIDs = new HashSet<>();
    public static final String PATH_TO_GNUPLOT_ID = registerFilepathID("PATH_TO_gnuplot.exe");
    public static final String PATH_TO_GRAPHVIZ_ID = registerFilepathID("PATH_TO_dot.exe");
    public static final String PATH_TO_PDFLATEX_ID = registerFilepathID("PATH_TO_pdflatex.exe");
    public static final String PATH_TO_SUMATRA_ID = registerFilepathID("PATH_TO_sumatrapdf.exe");
    public static final String PATH_TO_WORKING_DIR_ID = registerFilepathID("PATH_TO_PDF_GENERATOR_WORKING_DIRECTORY");
    public static final String PATH_TO_PYTHON_ID = registerFilepathID("PATH_TO_python.exe");

    /**
     * Registers the given filepath id as one that is currently in use. 
     * Unregistered file ids will be treated as obsolete, suggesting to the
     * user to delete them or just deleting them right away.<BR>
     * <BR>
     * It's safest to register all new IDs here as done above.
     * 
     * @param id  The filepath id to register.
     * @return  The id for further use, say, to initialize a static field.
     */
    public static String registerFilepathID(String id) {
        registeredFileIDs.add(id);
        return id;
    }
    
    private static HashMap<String, File> externalPaths = new HashMap<>();
    private static HashMap<String, Boolean> externalPathsAreFiles = new HashMap<>();
    private static HashMap<String, String> externalPathsHelpTexts = new HashMap<>();
    private static String externalFilePathsFileName = "externalFilePaths.dat";
    private static File fileToStoreExternalFilePaths;
    private static final String separatorString = "=";
    
    /**
     * Prompts the user to change the stored file paths.
     * 
     * @param tempDir  The temp dir to store the external file paths file in.
     */
    @SuppressWarnings("unused")
    public static void promptForResettingExternalPaths() {
        String oneTimeID1 = GeneralDialog.getUniqueRandomOneTimeID();
        String oneTimeID2 = GeneralDialog.getUniqueRandomOneTimeID();
        
        // Remove unregistered ids.
        HashSet<String> unregisteredIDs = new HashSet<>(externalPaths.keySet());
        unregisteredIDs.removeAll(registeredFileIDs);
        for (String unregID : unregisteredIDs) {
            if (true || GeneralDialog.yesRememberNoRememberAnswer(
                    unregisteredIDs.size() + " unregistered file path variable detected", 
                    "The following file path variable has not been registered properly and is therefore considered obsolete:\n \n"
                    + unregID + ": " + externalPaths.get(unregID) + "\n \n"
                    + "Du you wish to delete it (recommended)?", 
                    oneTimeID2, false, false)) {
                externalPaths.remove(unregID);
                storeFilePathsToFile();
                GlobalVariables.getParameters().logWarning("Deleted unregistered file id: " + unregID);
            }
        }
        
        // Manage and reset variables.
        if (GeneralDialog.yesNoAnswer("Reset one or several file paths?", 
                "The following file paths are stored:\n \n" 
                        + externalPaths.toString().replace("{", "").replace("}", "").replace(", ", "\n").replace("=", ": ") + "\n \n"
                        + "Do you wish to change any of them?")) {
            LinkedList<String> fileIDsToRemove = new LinkedList<>();
            
            int count = externalPaths.size();
            for (String fileID : externalPaths.keySet()) {
                if (true || GeneralDialog.yesRememberNoRememberAnswer(
                        "Reset the file path to " + fileID + "?", 
                        "Do you want to change the file path for '" + fileID + "'?\n \n"
                        + "Currently set to:          '" + externalPaths.get(fileID) + "'"
                        + "\n \n"
                        + "Choose '" + GeneralDialog.YES + "' to be prompted to re-enter the file path at the end. (" + --count + " remaining.)", 
                        oneTimeID1, false, false)) {
                    fileIDsToRemove.add(fileID);
                }
            }
            
            for (String fileID : fileIDsToRemove) {
                boolean fileSel = externalPathsAreFiles.get(fileID);
                String helpText = externalPathsHelpTexts.get(fileID);

                if (GeneralDialog.yesNoRememberAnswer("Set path variable '" + fileID
                        + "' from file tree!",
                        helpText + "\n \nCurrent value:            '"
                                + externalPaths.get(fileID)
                                + "'\n \nDo you wish to proceed?", 
                                oneTimeID1, false, false)) {
                    fileToStoreExternalFilePaths.delete();
                    File newPath;
                    if (fileSel) {
                        newPath = promptForFilePath(fileID, null);
                    } else {
                        newPath = promptForDirectoryPath(fileID);
                    }
                    
                    if (newPath != null) {
                        externalPaths.put(fileID, newPath);
                    }
                    
                    // Re-save changed file path.
                    storeFilePathsToFile();
                }
            }
        }
    }

    private static void storeToAddConfFile(
            File file,
            File optionalAdditionalConfFile, 
            Integer optionalAdditionalConfFilePos) {
        if (optionalAdditionalConfFile != null && optionalAdditionalConfFilePos != null) {
            if (!optionalAdditionalConfFile.exists()) {
                try {
                    optionalAdditionalConfFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            
            LinkedList<String> paths = StaticMethods.readTextArrayFromFile(optionalAdditionalConfFile);
            while (optionalAdditionalConfFilePos >= paths.size()) {
                paths.add("no path stored for entry " + paths.size());
            }
            try {
                paths.set(optionalAdditionalConfFilePos, file.getCanonicalPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            StaticMethods.storeCollectionElementsAsText(optionalAdditionalConfFile, paths);
            
//            GlobalVariables.getParameters().logWeb(
//                    "Additional conf file stored in '" + optionalAdditionalConfFile.getAbsolutePath() + "'.");
//            GlobalVariables.getParameters().logWeb(
//                    "Note that you will have to copy that file to the tomcat working folder.");
        }
    }
    
    /**
     * If the file path is already known, it will just be returned.
     * If not, the file path will be tried to be loaded from a config file
     * in the tempDir. If it exists there, this path will be remembered
     * for next time and returned. If not, the user will be prompted to
     * select the file path. Afterward, the path will be stored in the
     * tempDir to avoid a prompt next time, and as well it will
     * be remembered for the next call during the current session.
     * NOTE that this method prompts the user for input only if the path
     * has not been specified before, and EFFICIENTLY returns the path
     * Immediately in all other cases.
     * <BR><BR>
     * CAUTION: To avoid collecting obsolete fileIDs, in addition to prompting
     * here for it, every file id should be registered! Unregistered file ids
     * will be suggested to the user as obsolete and deletable.
     * 
     * @param fileID     The id that identifies the required path.
     * @param tempDir    The directory where the path is stored in a config file.
     * @param fileSel    If a file is to be returned (or else a directory).
     * @param helpText   Text shown to user before prompting for file path (may not contain '=' or '\n').
     * @param estimator  An optional file location estimator (can be <code>null</code>).
     * @param quiet      Iff the user dialogue should be suppressed. {@code null} will be returned if
     *                   no file path has been provided yet.
     * @return  A file containing the path to the external location (may be null 
     *          if no path is provided by the user).
     */
    public static File retrieveExternalFilePath(
            String fileID, 
            boolean fileSel, 
            String helpText,
            FileLocationEstimator estimator,
            File optionalAdditionalConfFile,
            Integer optionalAdditionalConfFilePos) {
        return retrieveExternalFilePath(
                fileID,
                fileSel,
                helpText,
                estimator,
                optionalAdditionalConfFile,
                optionalAdditionalConfFilePos,
                false);
    }

    public static File retrieveExternalFilePath(
            String fileID, 
            boolean fileSel, 
            String helpText,
            FileLocationEstimator estimator,
            File optionalAdditionalConfFile,
            Integer optionalAdditionalConfFilePos,
            boolean quiet) {
        String tempDir = GlobalVariables.getParameters().getStdDirectory();
        if (tempDir == null) {
            tempDir = ".";
        }

        if (!registeredFileIDs.contains(fileID)) {
            GlobalVariables.getParameters().logWarning("Prompted for unregistered file id: " + fileID);
        }

        if (fileID.contains(separatorString) || helpText.contains(separatorString)) {
            throw new RuntimeException("Neither the file path ID ('" 
                            + fileID + "') nor the help text ('" 
                            + helpText+ "') may contain the following String: '" 
                            + separatorString + "'.");
        }
        
        if (externalPaths.containsKey(fileID)) {
            File file = externalPaths.get(fileID);

            if (file.exists()) {
                storeToAddConfFile(file, optionalAdditionalConfFile, optionalAdditionalConfFilePos);
                return file;
            }
        }
        // File not yet existing.
        fileToStoreExternalFilePaths = new File(tempDir + "/" + externalFilePathsFileName);
        
        String soughtFileString = null;
        
        // Reference to file path (optionally create if non-existing).
        
        if (!fileToStoreExternalFilePaths.exists()) {
            try {
                fileToStoreExternalFilePaths.getParentFile().mkdirs();
                fileToStoreExternalFilePaths.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Could not create file: " + fileToStoreExternalFilePaths);
            }
        }
        
        // Put all stored paths in HashMap.
        LinkedList<String> paths = StaticMethods.readTextArrayFromFile(fileToStoreExternalFilePaths, GlobalVariables.getParameters());

        for (String s : paths) {
            try {
                String storedFileID = s.split(separatorString)[0];
                String storedFilePath = s.split(separatorString)[1];
                String storedIsFileProperty = s.split(separatorString)[2];
                String storedHelpText = s.split(separatorString)[3];

                externalPaths.put(storedFileID, new File(storedFilePath));
                externalPathsAreFiles.put(storedFileID, Boolean.parseBoolean(storedIsFileProperty));
                if (!storedHelpText.equals("null")) {
                    externalPathsHelpTexts.put(storedFileID, StaticMethods.restoreFromSafeString(storedHelpText));
                }
                
                if (storedFileID.equals(fileID)) { 
                    soughtFileString = storedFilePath; // Remember sought file path if matching.
                }
            } catch (Exception e) {}
        }
        
        if (soughtFileString == null) {
            soughtFileString = ""; // If not already stored, initialize file path string.
        }
        
        File soughtFile = new File(soughtFileString);

        if (!soughtFileString.isEmpty() && soughtFile.exists()) {
            externalPaths.put(fileID, soughtFile);
        } else {
            String s = "";

            // Confirm estimated file path.
            if (estimator != null && estimator.getEstimatedLocation() != null) {
                if (GeneralDialog.yesNoAnswer(
                        "Confirm external file path " + fileID, 
                        "A possible location has been found for " + fileID + ":\n \n"
                                + estimator.getEstimatedLocation()
                                + "\n \nIs this correct?")) {
                    soughtFile = estimator.getEstimatedLocation();
                }
            } else if (estimator != null) {
                s = " -- No possible location found. Please browse yourself.";
            }

            if (quiet) {
                return null;
            }
            
            // Prompt file dialog.
            if (!soughtFile.exists()) {
                if (!GeneralDialog.yesNoAnswer("Set path variable '" + fileID
                        + "' from file tree!",
                        helpText + "\n \nCurrent value:            "
                                + "<None provided>"
                                + "\n \nDo you wish to proceed?" + s)) {
                    File file = externalPaths.get(fileID);
                    storeToAddConfFile(file, optionalAdditionalConfFile, optionalAdditionalConfFilePos);
                    return file;
                }
                
                if (fileSel) { // If a file is to be selected.
                    soughtFile = promptForFilePath(fileID, estimator);
                } else { // If a directory is to be selected.
                    soughtFile = promptForDirectoryPath(fileID);
                }
            }
            
            if (soughtFile != null) {
                externalPaths.put(fileID, soughtFile);
                externalPathsHelpTexts.put(fileID, helpText);
                externalPathsAreFiles.put(fileID, fileSel);
            }
            
            storeFilePathsToFile();
        }
        
        storeToAddConfFile(soughtFile, optionalAdditionalConfFile, optionalAdditionalConfFilePos);
        return soughtFile;
    }
    
    /**
     * Stores all known paths to external files/dirs (including information about
     * whether it is a directory or file and the help text for the user).
     */
    private static void storeFilePathsToFile() {
        String tempDir = GlobalVariables.getParameters().getStdDirectory();
        
        LinkedList<String> storeInFile = new LinkedList<>();
        
        for (String id : externalPaths.keySet()) {
            storeInFile.add(
                    id 
                    + separatorString 
                    + externalPaths.get(id)
                    + separatorString
                    + externalPathsAreFiles.get(id)
                    + separatorString
                    + StaticMethods.createSafeString("" + externalPathsHelpTexts.get(id)));
        }
        
        StaticMethods.storeCollectionElementsAsText(tempDir, externalFilePathsFileName, storeInFile);
    }

    /**
     * Prompt user to input a path to a directory.
     * 
     * @param fileID  The ID of the directory path (shown in the window title).
     * 
     * @return  The path selected by the user or <code>null</code> if none.
     */
    private static File promptForDirectoryPath(String fileID) {
        File soughtFile;
        JFileChooser dia = new JFileChooser(".");
        dia.setCurrentDirectory(new File(System.getProperty("user.home")));
        dia.setDialogTitle("Set the path variable '" + fileID + "' (select a DIRECTORY and confirm)");
        dia.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int result = dia.showOpenDialog(null);
        if (result != JFileChooser.APPROVE_OPTION) {
            GeneralDialog.message("'" + fileID + "' is not available, sorry I will not be able to perform the according actions.", fileID + " not available", false);
            return null;
        }
        soughtFile = dia.getSelectedFile();
        return soughtFile;
    }

    /**
     * Prompt user to input a path to a file.
     * 
     * @param fileID  The ID of the file path (shown in the window title).
     * 
     * @return  The path selected by the user or <code>null</code> if none.
     */
    private static File promptForFilePath(String fileID, FileLocationEstimator estimator) {
        File soughtFile;
        FileDialog dia = new FileDialog((JFrame) null, "Set the path variable '" + fileID + "' (select a FILE and confirm)", FileDialog.LOAD);
        dia.setFilenameFilter(new FileNamePostfixFilter(".eas"));
        
        if (estimator != null) {
            estimator.setToEstimatedPath(dia);
        }
        
        dia.setVisible(true);
   
        if (dia.getDirectory() == null || dia.getFile() == null) {
            GeneralDialog.message(
                    "'" + fileID + "' is not available, sorry I will not be able to perform the according actions.", 
                    "'" + fileID + "' not available", 
                    false);
            return null;
        }
        
        soughtFile = new File(dia.getDirectory() + File.separator + dia.getFile());
        return soughtFile;
    }
    
    public static File retrieveDOTPath(final boolean quiet) {
        return retrieveExternalFilePath(ExternalFilePathsManager.PATH_TO_GRAPHVIZ_ID, true,
                "Choose the path to 'dot.exe' which is the graphViz executable file.\n"
                + "It is available in your '*program files*/graphViz/bin' folder after installation of the graphViz package in the 'install' folder.\n \n"
                + "[You can get the newest version from www.graphviz.org]",
                GraphVizEASVersion.ESTIMATOR_DOT_PATH,
                null,
                null,
                quiet);
    }
}
