/*
 * File name:        FileLocationEstimator.java (package eas.miscellaneous.windowsConvenience)
 * Author(s):        Lukas König
 * Java version:     8.0 (at generation time)
 * Generation date:  16.01.2015 (16:47:06)
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
import java.util.HashSet;
import java.util.LinkedList;

/**
 * @author Lukas König
 */
public class FileLocationEstimator {

    private HashSet<LinkedList<String>> locationPatterns = new HashSet<>();
    
    public FileLocationEstimator(String... pathFragments) {
        addAlternatePath(pathFragments);
    }
    
    @Override
    public String toString() {
        return locationPatterns.toString().replace(", ", "/").replace("[", "").replace("]", "");
    }

    public void addAlternatePath(String... pathFragments) {
        LinkedList<String> pattern = new LinkedList<>();
        
        // The first is irrelevant.
        pattern.add("");
        for (String s : pathFragments) {
            pattern.add(s);
        }
        
        this.locationPatterns.add(pattern);
    }
    
    public void setToEstimatedPath(FileDialog dia) {
        File estimatedFile = getFirstEstimatedLocation();
        try {
            dia.setDirectory(estimatedFile.getParent());
            dia.setFile(estimatedFile.getName());
        } catch (Exception e) {
        }
    }
    
    public File getEstimatedLocation() {
        return getFirstEstimatedLocation();
    }
    
    private File getFirstEstimatedLocation() {
        for (LinkedList<String> pattern : this.locationPatterns) {
            File estimated = getFirstEstimatedLocation(null, 0, pattern);
            if (estimated != null) {
                return estimated;
            }
        }
        
        return null;
    }
    
    private File getFirstEstimatedLocation(File currentFile, int position, LinkedList<String> pattern) {
        if (position == pattern.size() - 1) {
            File[] files = currentFile.listFiles(
                    f -> f.getName().matches("(?i)" + pattern.get(position))
                    && !f.isDirectory());
            if (files.length > 0) {
                return files[0];
            } else {
                return null;
            }
        }

        // Estimate drive id.
        if (position == 0) {
            for (File f : File.listRoots()) {
                File estimated = getFirstEstimatedLocation(f, position + 1, pattern);
                if (estimated != null) {
                    return estimated;
                }
            }
        } else {
            if (currentFile.exists()) {
                File[] matchingFiles = currentFile.listFiles(
                        f -> f.getName().matches("(?i)" + pattern.get(position))
                            && f.isDirectory());
                if (matchingFiles != null) {
                    for (File f : matchingFiles) {
                        File estimated = getFirstEstimatedLocation(f, position + 1, pattern);
                        if (estimated != null) {
                            return estimated;
                        }
                    }
                }
            }
        }
        
        return null;
    }
//    
//    public static void main(String[] args) {
//        FileLocationEstimator f = new FileLocationEstimator();
//        f.addPathFragment(".*program.*");
//        f.addPathFragment(".*graphViz.*");
//        f.addPathFragment("bin");
//        f.addPathFragment("dot.exe");
//        
////        f.addPathFragment(".*program.*");
////        f.addPathFragment(".*sumatra.*");
////        f.addPathFragment(".*sumatrapdf.exe");
//        
//        FileDialog dia = new FileDialog((Frame) null);
//        f.setToEstimatedPath(dia);
//        dia.setVisible(true);
//    }
}
