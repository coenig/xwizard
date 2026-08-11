/*
 * Datei: FileCopy.java
 * Autor(en): Lukas König, 
 * http://www.informatik-blog.net/2009/04/20/dateien-verschieben-kopieren/
 * Java-Version:     6.0
 * Erstellt:         03.12.2009
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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.ByteChannel;
import java.nio.channels.FileChannel;

import eas.GlobalVariables;
import eas.miscellaneous.convenience.GeneralDialog;

/**
 * Class for copying files.
 * 
 * @author Unknown.
 */
public class FileCopy {

	private long chunckSizeInBytes;
    
    private boolean verbose;

    public long getChunckSizeInBytes() {
        return this.chunckSizeInBytes;
    }

    public boolean isVerbose() {
        return this.verbose;
    }

    /**
     * Constructor.
     */
    public FileCopy() {
        this.chunckSizeInBytes = 1024 * 1024; // Standard: Buffer 1MB
        this.verbose = false; // Statistics about Copy Process
    }

    /**
     * Constructor.
     */
    public FileCopy(final boolean verbose2) {
        this.chunckSizeInBytes = 1024 * 1024; // Standard: Buffer 1MB
        this.verbose = verbose2; // Statistics about Copy Process
    }

    /**
     * Constructor.
     */
    public FileCopy(final long chunckSizeInBytes2) {
        this.chunckSizeInBytes = chunckSizeInBytes2; // Custom Buffer (Bytes)
        this.verbose = false; // Statistics about Copy Process
    }

    /**
     * Constructor. 
     */
    public FileCopy(
            final long chunckSizeInBytes2, 
            final boolean verbose2) {
        this.chunckSizeInBytes = chunckSizeInBytes2; // Custom Buffer (Bytes)
        this.verbose = verbose2; // Statistics about Copy Process
    }

    /**
     * Kopiert eine Datei in eine Zieldatei.
     * 
     * @param source       Quelldatei.
     * @param destination  Zieldatei.
     */
    public void copy(final File source, final File destination) {
        try {
            FileInputStream fileInputStream = new FileInputStream(source);
            FileOutputStream fileOutputStream = new FileOutputStream(
                    destination);
            FileChannel inputChannel = fileInputStream.getChannel();
            FileChannel outputChannel = fileOutputStream.getChannel();
            transfer(inputChannel, outputChannel, source.length(), false);
            fileInputStream.close();
            fileOutputStream.close();
            destination.setLastModified(source.lastModified());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Unbekannt.
     * 
     * @param fileChannel    Unbekannt.
     * @param byteChannel    Unbekannt.
     * @param lengthInBytes  Unbekannt.
     * @param verbose2        Unbekannt.
     * @throws IOException   Unbekannt.
     */
    public void transfer(
            final FileChannel fileChannel, 
            final ByteChannel byteChannel,
            final long lengthInBytes, 
            final boolean verbose2) throws IOException {
        long overallBytesTransfered = 0L;
        long time = -System.currentTimeMillis();
        while (overallBytesTransfered < lengthInBytes) {
            long bytesTransfered = 0L;
            bytesTransfered = fileChannel.transferTo(overallBytesTransfered,
                    Math.min(chunckSizeInBytes, lengthInBytes
                            - overallBytesTransfered), byteChannel);
            overallBytesTransfered += bytesTransfered;
            if (verbose2) {
                System.out.println("overall bytes transfered: "
                        + overallBytesTransfered
                        + " progress "
                        + (Math.round(overallBytesTransfered
                                / ((double) lengthInBytes) * 100.0)) + "%");
            }
        }
        time += System.currentTimeMillis();
        if (verbose2) {
            System.out.println("Transfered: " + overallBytesTransfered
                    + " bytes in: " + (time / 1000) + " s -> "
                    + (overallBytesTransfered / 1024.0) / (time / 1000.0)
                    + " kbytes/s");
        }
    }
    
    /**
     * Copies a complete folder with all files and optionally, recursively all
     * subfolders.
     * 
     * @param src       The source folder.
     * @param dest      The folder to copy the source folder to.
     * @param deepCopy  Iff the subfolders are copied recursively 
     *                  (or just files).
     * @throws IOException
     */
	public void copyFolder(File src, File dest, boolean deepCopy, boolean askBeforeOverwrite) throws IOException {
		if (src.isDirectory() && deepCopy) {

			// if directory not exists, create it
			if (!dest.exists()) {
				dest.mkdir();
			}

			// list all the directory contents
			String files[] = src.list();

			for (String file : files) {
				// construct the src and dest file structure
				File srcFile = new File(src, file);
				File destFile = new File(dest, file);
				// recursive copy
				copyFolder(srcFile, destFile, deepCopy, askBeforeOverwrite);
			}
        } else if (src.isDirectory() && !deepCopy) {
            // list all the directory contents
            String files[] = src.list();

            for (String file : files) {
                // construct the src and dest file structure
                File srcFile = new File(src, file);
                File destFile = new File(dest, file);
                // recursive copy
                if (!srcFile.isDirectory()) {
                    copyFolder(srcFile, destFile, false, askBeforeOverwrite);
                }
            }
        } else { // If not directory.
            boolean copy = true;
            
            if (dest.exists() && askBeforeOverwrite) {
                GeneralDialog dia = new GeneralDialog(
                        null, 
                        "File " + dest.getAbsolutePath() + " already exists. Should it be overwritten?", 
                        "", 
                        GeneralDialog.YES_NO, 
                        null);
                dia.setVisible(true);
                if (dia.getResult().equals(GeneralDialog.NO)) {
                    copy = false;
                }
            } 
            
            if (copy) {
                // if file, then copy it
                // Use bytes stream to support all file types
    			InputStream in = new FileInputStream(src);
    			OutputStream out = new FileOutputStream(dest);
    
    			byte[] buffer = new byte[1024];
    
    			int length;
    			// copy the file content in bytes
    			while ((length = in.read(buffer)) > 0) {
    				out.write(buffer, 0, length);
    			}
    
    			in.close();
    			out.close();
    			GlobalVariables.getParameters().logOutput("File copied from " + src + " to " + dest);
            }
		}
	}
}
