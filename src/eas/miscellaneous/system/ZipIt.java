/*
 * Datei:        ZipIt.java
 * Autor(en):    Lukas König, Internet: 
 *   http://blogs.sun.com/CoreJavaTechTips/entry/creating_zip_and_jar_files
 * Java-Version: 6.0
 * Erstellt:     14.09.2010
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

package eas.miscellaneous.system;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

import eas.miscellaneous.system.andreasKamper.StepperSimpleLukasZIP;

public class ZipIt {
    
    /**
     * Creates a JAR stream, adds a directory and all files and subdirectories 
     * (recursively) to it and closes the stream subsequently.
     * 
     * @param sourcePath           The name of the directory to pack into JAR.
     * @param destinationFileName  The name of the JAR file to create.
     * @param mainClass            Optional main class to add to manifest.
     * @throws IOException  Exception.
     * @throws NoExplicitClassPathFound  If there is no class path matching the given root directory
     *                                   for JAR creation. (Most likely, the cause of this is that
     *                                   EAS has been started from a JAR file which means that it
     *                                   already is within the package you are trying to create.)
     */
    public static void createJARfromDirectory(
            final String sourcePath,
            final File destinationFileName,
            final String mainClass) throws IOException, NoExplicitClassPathFound {
        JarOutputStream jos = ZipIt.getJARstream(destinationFileName, mainClass);
        ZipIt.addToJARDirectoryRecursively(jos, sourcePath, destinationFileName);
        jos.close();
    }
    
    /**
     * Generates a jar output stream at the given file position.
     * 
     * @param fileName  The file name of the jar stream.
     * 
     * @return  The jar stream.
     * 
     * @throws IOException  Exception. 
     */
    public static JarOutputStream getJARstream(
            final File fileName,
            final String mainClass) 
            throws IOException {
        Manifest m = new Manifest();
        m.getMainAttributes().putValue("Manifest-Version", "1.0");
        m.getMainAttributes().putValue("Main-Class", mainClass);

        File zipFile = fileName;
        FileOutputStream fos = new FileOutputStream(zipFile);
        JarOutputStream jos = new JarOutputStream(fos, m);
        return jos;
    }

    /**
     * Adds a directory and all files and subdirectories to a jar stream.
     * 
     * @param jos        The zip stream.
     * @param directory  The name of the directory to add.
     * @throws IOException  Exception.
     * @throws NoExplicitClassPathFound  If there is no class path matching the given root directory
     *                                   for JAR creation. (Most likely, the cause of this is that
     *                                   EAS has been started from a JAR file which means that it
     *                                   already is within the package you are trying to create.)
     */
    public static void addToJARDirectoryRecursively(
            JarOutputStream jos, String directory, File file) throws IOException, NoExplicitClassPathFound {
        File dir = new File(directory);
        
        if (!dir.isDirectory()) {
            throw new NoExplicitClassPathFound("No directory given for JAR: "
                    + directory + ". (Stream: " + file + ")");
        }
        
        StepperSimpleLukasZIP stepper = new StepperSimpleLukasZIP();
        
        stepper.goThroughDirectory(directory);
        
        for (String s : stepper.getList()) {
            addFileToJARStream(jos, s);
        }
    }
    
    /**
     * Adds a single file to the zip stream.
     * 
     * @param jos           The zip stream.
     * @param fileName      The name of the file to add.
     * @throws IOException  Exception.
     */
    public static void addFileToJARStream(
            JarOutputStream jos, 
            String fileName) throws IOException {
        int bytesRead;
        byte[] buffer = new byte[10240];
        File file = new File(fileName);
        if (!file.exists()) {
            System.err.println("Skipping: " + fileName);
            return;
        }
        FileInputStream fis = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis);
        
        JarEntry entry = new JarEntry(fileName.replace('\\', '/'));
        entry.setTime(new File(fileName).lastModified());
        jos.putNextEntry(entry);

        while ((bytesRead = bis.read(buffer)) != -1) {
            jos.write(buffer, 0, bytesRead);
        }
        bis.close();
    }

    /**
     * Generates a zip output stream at the given file position.
     * 
     * @param fileName  The file name of the zip stream.
     * 
     * @return  The zip stream.
     * 
     * @throws FileNotFoundException  Cannot be thrown in all regular cases.
     */
    public static ZipOutputStream getZIPstream(final String fileName) 
                                        throws FileNotFoundException {
        File zipFile = new File(fileName);
        FileOutputStream fos = new FileOutputStream(zipFile);
        ZipOutputStream zos = new ZipOutputStream(fos);
        return zos;
    }
    
    /**
     * Adds a directory and all files and subdirectories to a zip stream.
     * 
     * @param zos        The zip stream.
     * @param directory  The name of the directory to add.
     * @throws IOException  Exception.
     */
    public static void addToZIPDirectoryRecursively(
            ZipOutputStream zos, String directory) throws IOException {
        File dir = new File(directory);
        
        if (!dir.isDirectory()) {
            throw new RuntimeException("No directory given for ZIP: "
                    + directory);
        }
        
        StepperSimpleLukasZIP stepper = new StepperSimpleLukasZIP();
        
        stepper.goThroughDirectory(directory);
        
        for (String s : stepper.getList()) {
            addFileToZIPStream(zos, s);
        }
    }
    
    /**
     * Adds a single file to the zip stream.
     * 
     * @param zos           The zip stream.
     * @param fileName      The name of the file to add.
     * @throws IOException  Exception.
     */
    public static void addFileToZIPStream(
            ZipOutputStream zos, 
            String fileName) throws IOException {
        int bytesRead;
        byte[] buffer = new byte[1024];
        CRC32 crc = new CRC32();
        File file = new File(fileName);
        if (!file.exists()) {
            System.err.println("Skipping: " + fileName);
            return;
        }
        BufferedInputStream bis = new BufferedInputStream(
            new FileInputStream(file));
        crc.reset();
        while ((bytesRead = bis.read(buffer)) != -1) {
            crc.update(buffer, 0, bytesRead);
        }
        bis.close();
        bis = new BufferedInputStream(
            new FileInputStream(file));
        ZipEntry entry = new ZipEntry(fileName.replace('\\', '/'));
        zos.putNextEntry(entry);
        while ((bytesRead = bis.read(buffer)) != -1) {
            zos.write(buffer, 0, bytesRead);
        }
        bis.close();
    }

    public static void extractZIPArchive(File archive, File destDir) throws Exception {
        if (!destDir.exists()) {
            destDir.mkdir();
        }
 
        ZipFile zipFile = new ZipFile(archive);
        Enumeration<? extends ZipEntry> entries = zipFile.entries();
 
        byte[] buffer = new byte[16384];
        int len;
        while (entries.hasMoreElements()) {
            ZipEntry entry = entries.nextElement();
 
            String entryFileName = entry.getName();
 
            @SuppressWarnings("all")
            File dir = dir = buildDirectoryHierarchyFor(entryFileName, destDir);
            if (!dir.exists()) {
                dir.mkdirs();
            }
 
            if (!entry.isDirectory()) {
                BufferedOutputStream bos = new BufferedOutputStream(
                        new FileOutputStream(new File(destDir, entryFileName)));
 
                BufferedInputStream bis = new BufferedInputStream(zipFile
                        .getInputStream(entry));
 
                while ((len = bis.read(buffer)) > 0) {
                    bos.write(buffer, 0, len);
                }
 
                bos.flush();
                bos.close();
                bis.close();
            }
        }
                zipFile.close();
    }
 
    private static File buildDirectoryHierarchyFor(String entryName, File destDir) {
        int lastIndex = entryName.lastIndexOf('/');
        @SuppressWarnings("unused")
        String entryFileName = entryName.substring(lastIndex + 1);
        String internalPathToEntry = entryName.substring(0, lastIndex + 1);
        return new File(destDir, internalPathToEntry);
    }
}
