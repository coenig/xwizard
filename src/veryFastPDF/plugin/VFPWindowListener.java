/*
 * File name:        MenuListener.java (package eas.math.veryFastPDF.plugin)
 * Author(s):        Lukas König
 * Java version:     8.0 (at generation time)
 * Generation date:  31.01.2015 (09:13:59)
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

package veryFastPDF.plugin;

import java.awt.Cursor;
import java.awt.Event;
import java.awt.FileDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JTextArea;
import javax.swing.KeyStroke;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.plaf.basic.BasicSplitPaneDivider;
import javax.swing.text.BadLocationException;

import eas.GlobalVariables;
import eas.miscellaneous.StaticMethods;
import eas.miscellaneous.convenience.ExternalFilePathsManager;
import eas.miscellaneous.convenience.GeneralDialog;
import eas.miscellaneous.useful.RXTextUtilities;
import mainServlet.WebLink;
import veryFastPDF.HelpTexts;
import veryFastPDF.VFPVariables;
import veryFastPDF.pdfProcessors.PDFProcessor;
import veryFastPDF.script.RepresentableAsPDF;
import veryFastPDF.script.RepresentableDefault;
import veryFastPDF.script.testing.Tester;

/**
 * @author Lukas König
 */
public class VFPWindowListener implements 
        ActionListener, 
        KeyListener, 
        MouseListener, 
        ComponentListener, 
        PropertyChangeListener, 
        Serializable, 
        DocumentListener {

    private static final long serialVersionUID = 4752752886805335147L;

    private JButton lastExample = null;
    private int exampleCounter = 0;
    
    public VFPWindowListener() {
    }
    
    private JMenu fileMenu;
    private JMenuItem fileMenuLoad = new JMenuItem("Load script...");
    private JMenuItem fileMenuSave = new JMenuItem("Save script");
    private JMenuItem fileMenuExport = new JMenuItem("Export script...");
    private JMenuItem fileMenuManageFilePaths = new JMenuItem("Manage file paths...");
    private JMenuItem fileMenuOpenWD = new JMenuItem("Open working directory");
    private JMenuItem fileMenuExit = new JMenuItem("Exit!");

    private JMenu editMenu;
    private JMenuItem editMenuUndo = new JMenuItem("Undo");
    private JMenuItem editMenuRedo = new JMenuItem("Redo");
    private JMenuItem editMenuConvertToPDF = new JMenuItem("=> Convert script to plain PDF code");
    private JMenuItem editMenuRemoveComments = new JMenuItem("Remove comments from script");
    private JMenuItem editMenuRefresh = new JMenuItem("Refresh output");
    private JMenuItem editMenuCommentSelected = new JMenuItem("Comment selected area");
    private JMenuItem editMenuInscriptSelected = new JMenuItem("Sorround selected area with sub-script tags");
    
    private JMenu showMenu;
    private JCheckBoxMenuItem[] pdfTypes;
    private HashMap<JCheckBoxMenuItem, String> pdfTypeNames;
    private JMenuItem showMenuALL = new JMenuItem("Show all");
    private JMenuItem showMenuNone = new JMenuItem("Show none");
    
    private JMenu pdfMenu;
    private JMenuItem pdfMenuReopenSumatra = new JMenuItem("Kill and re-open Sumatra");
    private JMenuItem pdfMenuKillSumatras = new JMenuItem("Kill Sumatras!");
    
    private JMenu helpMenu;
    private JMenuItem helpMenuInfo = new JMenuItem("Info...");

    private JMenuBar menubar;

    private static final String BASE_ID_FOR_PDF_PROCESSORS = "$PDF-PROCESSOR-ACTIVE?$-";
    
    protected void addMenu(String[] pdfTypesString) {
        menubar = new JMenuBar();
        pdfTypeNames = new HashMap<>();
        
        // File menu.
        fileMenu = new JMenu("File");
        fileMenu.setMnemonic('f');
        fileMenu.add(fileMenuLoad); fileMenuLoad.setMnemonic('l'); fileMenuLoad.addActionListener(this); fileMenuLoad.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK));
        fileMenuLoad.setToolTipText("Load script from external file.");
        fileMenu.add(fileMenuSave); fileMenuSave.setMnemonic('s'); fileMenuSave.addActionListener(this); fileMenuSave.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK));
        fileMenuSave.setToolTipText("Save current script in " + VFPWindow.getSINGLETON_INSTANCE().scriptFileName() + ".");
        fileMenu.add(fileMenuExport); fileMenuExport.setMnemonic('e'); fileMenuExport.addActionListener(this); fileMenuExport.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, Event.CTRL_MASK));
        fileMenuExport.setToolTipText("Save current script at a custom location.");
        fileMenu.addSeparator();
        fileMenu.add(fileMenuManageFilePaths); fileMenuManageFilePaths.setMnemonic('m'); fileMenuManageFilePaths.addActionListener(this);
        fileMenuManageFilePaths.setToolTipText("Inspect and change paths to external programs such as SumatraPDF or GraphViz.");
        fileMenu.add(fileMenuOpenWD); fileMenuOpenWD.setMnemonic('w'); fileMenuOpenWD.addActionListener(this);
        fileMenuOpenWD.setToolTipText("Open working directory where all output files are stored.");
        fileMenu.addSeparator();
        fileMenu.add(fileMenuExit); fileMenuExit.setMnemonic('x'); fileMenuExit.addActionListener(this);
        fileMenuExit.setToolTipText("Close the VFP window and optionally kill all SumatraPDF windows.");
        
        // Edit menu.
        editMenu = new JMenu("Edit");
        editMenu.setMnemonic('e');
        editMenu.add(editMenuUndo); editMenuUndo.setMnemonic('u'); editMenuUndo.addActionListener(this); editMenuUndo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, Event.CTRL_MASK));
        editMenuUndo.setToolTipText("Undo last action (STRG+Z)");
        editMenu.add(editMenuRedo); editMenuRedo.setMnemonic('r'); editMenuRedo.addActionListener(this); editMenuRedo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, Event.CTRL_MASK));
        editMenuRedo.setToolTipText("Redo last undone action (STRG+Y)");
        editMenu.addSeparator(); 
        editMenu.add(editMenuConvertToPDF); editMenuConvertToPDF.setMnemonic('c'); editMenuConvertToPDF.addActionListener(this);
        editMenuConvertToPDF.setToolTipText("Create the raw code (in the language of the according PDF processor) for this script.");
        editMenu.add(editMenuRemoveComments); editMenuRemoveComments.setMnemonic('e'); editMenuRemoveComments.addActionListener(this); // editMenuRemoveComments.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, Event.CTRL_MASK));
        editMenuRemoveComments.setToolTipText("Change current script by removing all comments.");
        editMenu.add(editMenuRefresh); editMenuRefresh.setMnemonic('r'); editMenuRefresh.addActionListener(this); editMenuRefresh.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R, Event.CTRL_MASK));
        editMenuRefresh.setToolTipText("Executes a recalculation of the script.");
        editMenu.add(editMenuCommentSelected); editMenuCommentSelected.setMnemonic('o'); editMenuCommentSelected.addActionListener(this); editMenuCommentSelected.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Q, Event.CTRL_MASK));
        editMenuCommentSelected.setToolTipText("Comments out the selected area of the script.");
        editMenu.add(editMenuInscriptSelected); editMenuInscriptSelected.setMnemonic('i'); editMenuInscriptSelected.addActionListener(this); editMenuInscriptSelected.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, Event.CTRL_MASK));
        editMenuInscriptSelected.setToolTipText("Puts inscript tags around the selected area");

        // Show menu.
        showMenu = new JMenu("Show");
        showMenu.setMnemonic('s');
        this.pdfTypes = new JCheckBoxMenuItem[pdfTypesString.length];
        for (int i = 0; i < pdfTypesString.length; i++) {
            String s = pdfTypesString[i];
                    
            this.pdfTypes[i] = new JCheckBoxMenuItem("Show " + s + " examples");
            this.pdfTypes[i].setState(true);
            this.pdfTypes[i].addActionListener(this);
            this.pdfTypes[i].setToolTipText("Show all example buttons for PDF processor '" + s + "'.");
            showMenu.add(this.pdfTypes[i]);
            this.pdfTypeNames.put(this.pdfTypes[i], s);
            
            String storedVal = GeneralDialog.loadValue(BASE_ID_FOR_PDF_PROCESSORS + s + VFPParameters.isStudentVersion());
            if (storedVal != null) {
                boolean checked = Boolean.parseBoolean(storedVal);
                this.pdfTypes[i].setSelected(checked);
            }
        }
        
        showMenu.addSeparator();
        showMenu.add(showMenuALL); showMenuALL.setMnemonic('l'); showMenuALL.addActionListener(this); showMenuALL.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_L, Event.CTRL_MASK));
        showMenu.add(showMenuNone); showMenuNone.setMnemonic('n'); showMenuNone.addActionListener(this); showMenuNone.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK));
        
        // PDF menu.
        pdfMenu = new JMenu("PDF viewer");
        pdfMenu.setMnemonic('p');
        pdfMenu.add(pdfMenuReopenSumatra); pdfMenuReopenSumatra.setMnemonic('o'); pdfMenuReopenSumatra.addActionListener(this);
        pdfMenuReopenSumatra.setToolTipText("Re-open a SumatraPDF window to show the script output in real-time.");
        pdfMenu.add(pdfMenuKillSumatras); pdfMenuKillSumatras.setMnemonic('k'); pdfMenuKillSumatras.addActionListener(this);
        pdfMenuKillSumatras.setToolTipText("Kill all SumatraPDF windows. CAUTION: Sumatra instances not opened by VFP will also be closed.");
        
        // Help menu.
        helpMenu = new JMenu("Help");
        helpMenu.setMnemonic('h');
        helpMenu.add(helpMenuInfo); helpMenuInfo.setMnemonic('i'); helpMenuInfo.addActionListener(this);
        helpMenuInfo.setToolTipText("Show release and copyright notes.");
        
        menubar.add(fileMenu);
        menubar.add(editMenu);
        menubar.add(showMenu);
        menubar.add(pdfMenu);
        menubar.add(helpMenu);
        VFPWindow.getSINGLETON_INSTANCE().setJMenuBar(menubar);
        this.actionPerformed(new ActionEvent(menubar, 0, ""));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        VFPWindow.getSINGLETON_INSTANCE().setCursor(new Cursor(Cursor.WAIT_CURSOR));
        String scriptText = VFPWindow.getSINGLETON_INSTANCE().getScriptWithoutComments(null);

        if (e.getSource() == this.fileMenuOpenWD) {
            VFPWindow.getSINGLETON_INSTANCE().runExplorerInWorkingDirectory();
        } else if (e.getSource() == fileMenuExit) {
            VFPWindow.getSINGLETON_INSTANCE().exit();
        } else if (e.getSource() == fileMenuManageFilePaths) {
            try {
                ExternalFilePathsManager.promptForResettingExternalPaths();
            } catch (Exception e1) {
                GlobalVariables.getParameters().logWarning("At least one working path has not been set.");
            }
        } else if (e.getSource() == fileMenuSave) {
            VFPWindow.getSINGLETON_INSTANCE().storeScriptToFile();
        } else if (e.getSource() == fileMenuLoad) {
            FileDialog dia = new FileDialog(VFPWindow.getSINGLETON_INSTANCE(), "Choose script file to load.", FileDialog.LOAD);
            dia.setVisible(true);
            if (dia.getFile() != null) {
                File loadFile = new File(dia.getDirectory() + "/" + dia.getFile());
                String script = StaticMethods.readTextFromFile(loadFile, GlobalVariables.getParameters());
                VFPWindow.getSINGLETON_INSTANCE().setScriptAndManageUndo(script);
            }
        } else if (e.getSource() == fileMenuExport) {
            FileDialog dia = new FileDialog(VFPWindow.getSINGLETON_INSTANCE(), "Choose location to store script file at.", FileDialog.SAVE);
            dia.setVisible(true);
            if (dia.getFile() != null) {
                String extension = "";
                if (!dia.getFile().endsWith(".txt")) {
                    extension = ".txt";
                }
                File storeFile = new File(dia.getDirectory() + "/" + dia.getFile() + extension);
                StaticMethods.saveTextToFile(
                        storeFile,
                        VFPWindow.getSINGLETON_INSTANCE().getScriptArea().getText());
            }
        } else if (e.getSource() == editMenuConvertToPDF) {
            for (RepresentableAsPDF r : VFPWindow.getSINGLETON_INSTANCE().getAvailableReps()) {
                if (r.isAcceptableScript(scriptText)) {
                    r.createInstanceFromScript(scriptText, null);
                    PDFProcessor pdf = r.generatePDFscript(VFPWindow.getSINGLETON_INSTANCE().getWorkingDirectory().getAbsolutePath());
                    String pdfSourceString = pdf.getSourceString();
                    String pdfCodePrefix = pdf.getCodePrefix();
                    
                    String code = "";
                    if (pdfSourceString != null) {
                        code = pdfSourceString;
                        if (code.endsWith("\n")) {
                            code = code.substring(0, code.length() - 1);
                        }
                        code = pdfCodePrefix + code;
                    }
                    
                    if (pdfSourceString != null) {
                        VFPWindow.getSINGLETON_INSTANCE().getScriptArea().setText(code);
                        VFPWindow.getSINGLETON_INSTANCE().manageUndo();
                        break;
                    } else {
                        GeneralDialog.message("PDF processor '" + pdf.getClass().getSimpleName() + "' does not offer plain source code.", "Raw code not available", false);
                    }
                }
            }
        } else if (e.getSource() == editMenuRedo) {
            VFPWindow.getSINGLETON_INSTANCE().unundo();
        } else if (e.getSource() == editMenuUndo) {
            VFPWindow.getSINGLETON_INSTANCE().undo();
        } else if (e.getSource() == editMenuRemoveComments) {
            VFPWindow.getSINGLETON_INSTANCE().setScriptAndManageUndo(VFPWindow.getSINGLETON_INSTANCE().getScriptWithoutComments(null).trim());
        } else if (e.getSource() == editMenuCommentSelected) {
            VFPWindow.getSINGLETON_INSTANCE().commentSelectedArea(RepresentableDefault.BEGIN_COMMENT, RepresentableDefault.END_COMMENT);
        } else if (e.getSource() == editMenuRefresh) {
            VFPWindow.getSINGLETON_INSTANCE().applyScript();
        } else if (e.getSource() == editMenuInscriptSelected) {
            VFPWindow.getSINGLETON_INSTANCE().commentSelectedArea(RepresentableDefault.INSCR_BEG_TAG, RepresentableDefault.INSCR_END_TAG);
        } else if (e.getSource() == pdfMenuKillSumatras) {
            VFPWindow.getSINGLETON_INSTANCE().getPars().logInfo("Killing Sumatras.");
            VFPWindow.getSINGLETON_INSTANCE().killSumatras();
        } else if (e.getSource() == pdfMenuReopenSumatra) {
            VFPWindow.getSINGLETON_INSTANCE().getPars().logInfo("Killing Sumatras.");
            VFPWindow.getSINGLETON_INSTANCE().killSumatras();
            try {
                Thread.sleep(1000);
            } catch (Exception e2) {}
            VFPWindow.getSINGLETON_INSTANCE().getPars().logInfo("Restarting Sumatra.");
            VFPWindow.getSINGLETON_INSTANCE().runSumatra();
        } else if (e.getSource() == helpMenuInfo) {
            showInfoMessage();
        } else if (e.getSource() == showMenuALL) {
            for (JCheckBoxMenuItem j : this.pdfTypes) {
                j.setSelected(true);
            }
        } else if (e.getSource() == showMenuNone) {
            for (JCheckBoxMenuItem j : this.pdfTypes) {
                j.setSelected(false);
            }
        }
        
        HashSet<String> allowedPDFTypes = new HashSet<>();
        for (int i = 0; i < this.pdfTypes.length; i++) {
            String s = this.pdfTypeNames.get(this.pdfTypes[i]);
            
            if (this.pdfTypes[i].isSelected()) {
                allowedPDFTypes.add(s);
            }
            
            GeneralDialog.storeValueOf(
                    BASE_ID_FOR_PDF_PROCESSORS + s + VFPParameters.isStudentVersion(), 
                    this.pdfTypes[i].isSelected());
        }
        VFPWindow.getSINGLETON_INSTANCE().addExampleButtons(allowedPDFTypes);
        
        VFPWindow.getSINGLETON_INSTANCE().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        VFPWindow.getSINGLETON_INSTANCE().transferFocusBackward();

        VFPWindow.getSINGLETON_INSTANCE().refreshWholeWindow();
    }
    
    @Override
    public void mouseClicked(MouseEvent e) {
        VFPWindow.getSINGLETON_INSTANCE().setCursor(new Cursor(Cursor.WAIT_CURSOR));
        
        if (e.getClickCount() > 1 && BasicSplitPaneDivider.class.isAssignableFrom(e.getSource().getClass())) {
            // TODO: Insert code for adjusting split pane automatically:
//            JScrollPane controllPane = father.getScrollControlPanel();
//            int lastSize;
//            JSplitPane splitPane = father.getSplitPane();
//            
//            do {
//                lastSize = controllPane.getVerticalScrollBar().getMaximum();
//                splitPane.setDividerLocation(splitPane.getDividerLocation() - 1);
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e1) {
//                }
//            } while (lastSize == controllPane.getVerticalScrollBar().getMaximum());
        }
        
        if (e.getSource().equals(VFPWindow.getSINGLETON_INSTANCE().buttonExit)) {
            VFPWindow.getSINGLETON_INSTANCE().exit();
        } else if (e.getSource().equals(VFPWindow.getSINGLETON_INSTANCE().undoButton)) {
            VFPWindow.getSINGLETON_INSTANCE().undo();
        } else if (e.getSource().equals(VFPWindow.getSINGLETON_INSTANCE().unundoButton)) {
            VFPWindow.getSINGLETON_INSTANCE().unundo();
        } else if (e.getSource().equals(VFPWindow.getSINGLETON_INSTANCE().buttException) && VFPWindow.getSINGLETON_INSTANCE().buttException.isEnabled()) {
            GeneralDialog dia = new GeneralDialog(
                    VFPWindow.getSINGLETON_INSTANCE(), 
                    null, 
                    "Could not parse the entered script. Error information:", 
                    new String[] {GeneralDialog.OK, "Throw exception!"}, 
                    VFPWindow.getSINGLETON_INSTANCE().exceptionThrown.toString() + "\n\n" + Arrays.deepToString(VFPWindow.getSINGLETON_INSTANCE().exceptionThrown.getStackTrace()).replace(",", ",\n"));
            dia.setVisible(true);
            if (dia.getResult().equals("Throw exception!")) {
                VFPWindow.getSINGLETON_INSTANCE().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                throw new RuntimeException(VFPWindow.getSINGLETON_INSTANCE().exceptionThrown);
            }
        } else if (e.getSource().equals(VFPWindow.getSINGLETON_INSTANCE().buttonSaveCurrent)) {
            VFPWindow.getSINGLETON_INSTANCE().storeScriptToFile();
        } else if (e.getSource().equals(VFPWindow.getSINGLETON_INSTANCE().buttonInfo)) {
            showInfoMessage();
        } else if (e.getSource().equals(VFPWindow.getSINGLETON_INSTANCE().refreshOnTheFly)) {
            if (VFPWindow.getSINGLETON_INSTANCE().refreshOnTheFly.isSelected()) {
                VFPWindow.getSINGLETON_INSTANCE().manageUndo();
            }
        } else if (e.getSource().equals(VFPWindow.getSINGLETON_INSTANCE().svgMode)) {
            if (VFPWindow.getSINGLETON_INSTANCE().svgMode.isSelected()) {
                VFPWindow.getSINGLETON_INSTANCE().svgVisualizer.display();
            } else {
                VFPWindow.getSINGLETON_INSTANCE().svgVisualizer.hide();
            }
        } else if (e.getSource().equals(VFPWindow.getSINGLETON_INSTANCE().buttonWeb)) {
            if (GeneralDialog.yesNoAnswer(
                    "Open " + VFPVariables.PROG_NAME_XWIZZ + " Webapp?", 
                    "Do you want to open the " + VFPVariables.PROG_NAME_XWIZZ + " Webapp in a browser?\n"
                            + "The current script will be automatically transferred to the Webapp.\n"
                            + "(If it's not too long.)")) {
                String parameter = WebLink.encodeScriptAsURLPar(VFPWindow.getSINGLETON_INSTANCE().getScriptWithoutComments(null), true);
                try {
                    StaticMethods.openWebpage(new URI(VFPVariables.URL_TO_DIRECT_XWIZZ_SERVER + parameter));
                } catch (URISyntaxException e1) {
                    GeneralDialog.message(e1.toString(), "An error occurred when I tried to open a web page", true);
                }
            }
        } else if (e.getSource().equals(VFPWindow.getSINGLETON_INSTANCE().buttonAskQuestionInBrowser)) {
            try {
                if (GeneralDialog.yesNoAnswer(
                        "Ask question to internet community", 
                        "Do you want to ask a question regarding the current script?\n"
                        + "A new question will be opened with your current script already copied in the editor.\n \n"
                        + "(Choosing 'no' will open the already discussed questions.)")) {
                    

                    String parameter = WebLink.encodeScriptAsURLPar(
                            WebLink.INTRO
                            + VFPWindow.getSINGLETON_INSTANCE().getScriptWithoutComments(null).trim() 
                            + WebLink.EXTRO,
                            false);
                    
//                    if (parameter.length() > 512) {
//                        GeneralDialog.message(
//                                "Your script may be too long to be copied to the editor. If so, please copy manually, if desired.", 
//                                "Long script (encoded with " + parameter.length() + " characters)", 
//                                false);
//                    }
                    
                    StaticMethods.openWebpage(new URI(VFPVariables.URL_TO_ASK_QUESTION + parameter));
                } else {
                    StaticMethods.openWebpage(new URI(VFPVariables.URL_TO_QUESTION_CATALOG));
                }
            } catch (URISyntaxException e1) {
                GeneralDialog.message(e1.toString(), "An error occurred when I tried to open a web page", true);
            }
        }
        
        for (JButton butt : VFPWindow.getSINGLETON_INSTANCE().expButtonsToRepsMapping.keySet()) {
            if (e.getSource().equals(butt)) {
                if (VFPWindow.getSINGLETON_INSTANCE().expButtonsToRepsMapping.get(butt).getClass().equals(Tester.class)) {
                    VFPWindow.getSINGLETON_INSTANCE().deleteTempFiles();
                }
                
                if (!butt.equals(lastExample)) {
                    this.exampleCounter = -1;
                }

                if (e.getButton() == 1) {
                    this.exampleCounter++;
                } else {
                    this.exampleCounter--;
                }
                
                this.lastExample = butt;
                RepresentableAsPDF rep = VFPWindow.getSINGLETON_INSTANCE().expButtonsToRepsMapping.get(butt);
                String[] examples = rep.getExampleScripts();

                if (exampleCounter < 0) {
                    this.exampleCounter = examples.length - 1;
                }

//                String ht = rep.veryQuickHelpText();
//                String helpText = "";
//                if (ht != null) {
//                    helpText = "\n\n" + PDFGeneratorWindow.BEGIN_COMMENT
//                            + " Quick help (for the above "
//                            + rep.getClass().getSimpleName() + " script): "
//                            + ht + " " + PDFGeneratorWindow.END_COMMENT;
//                }

                VFPWindow.getSINGLETON_INSTANCE().setScriptAndManageUndo(examples[this.exampleCounter % examples.length]);
                
                try {
                    int caretpos = examples[this.exampleCounter % examples.length].length();
                    int row = VFPWindow.getSINGLETON_INSTANCE().getScriptArea().getLineOfOffset(caretpos);
                    RXTextUtilities.gotoFirstWordOnLine(VFPWindow.getSINGLETON_INSTANCE().getScriptArea(), row + 3);
                } catch (BadLocationException e1) {
                }
                
                HelpTexts.showQuickHelpWindow(rep, false);
                
                butt.setText(VFPWindow.getSINGLETON_INSTANCE().getExpButtonText(rep, this.exampleCounter % examples.length + 1));
            }
        }
        
        VFPWindow.getSINGLETON_INSTANCE().setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
        VFPWindow.getSINGLETON_INSTANCE().transferFocusBackward();
    }

    public void showInfoMessage() {
        String title = VFPVariables.PROG_NAME_PDF_GEN_SHORT + " (V. " 
                + VFPVariables.PROG_VERSION_PDF_GEN_SHORT + ")" 
                + (VFPParameters.isStudentVersion() ? " -- <Info-II student version>" : "");
        
        String htmlText = VFPVariables.VFP_INFO.replace("\n", "<BR/>");
        
        GeneralDialog.showHTML(
                htmlText, 
                title,
                null);
    }
    
    protected boolean isRecentlyTypedCommentAtBeginOfLine(KeyEvent arg0) {
        if (!commentTyped(arg0)) {
            return false;
        }
        
        if (this.getScriptArea().getCaretPosition() == RepresentableDefault.BEGIN_COMMENT.length()) {
            return true;
        }
        
        if (this.getScriptArea().getText().charAt(this.getScriptArea().getCaretPosition() - RepresentableDefault.BEGIN_COMMENT.length() - 1) == '\n') {
            return true;
        }
        
        return false;
    }
    
    private JTextArea getScriptArea() {
        return VFPWindow.getSINGLETON_INSTANCE().getScriptArea();
    }
    
    protected boolean commentTyped(KeyEvent arg0) {
        try {
            String s = this.getScriptArea().getText();
            if (arg0.getKeyChar() == RepresentableDefault.BEGIN_COMMENT.charAt(RepresentableDefault.BEGIN_COMMENT.length() - 1)) {
                if (RepresentableDefault.BEGIN_COMMENT.equals(s.substring(this.getScriptArea().getCaretPosition() - RepresentableDefault.BEGIN_COMMENT.length(), this.getScriptArea().getCaretPosition()))) {
                    return true;
                }
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    @Override 
    public void keyPressed(KeyEvent e) {
        if (!e.isShiftDown() && !e.isControlDown()) {
//            PDFGeneratorWindow.getSINGLETON_INSTANCE().highlightComments();
        }
    }

    @Override 
    public void keyReleased(KeyEvent arg0) {
        String s = VFPWindow.getSINGLETON_INSTANCE().getScriptArea().getText();
        
        if (this.isRecentlyTypedCommentAtBeginOfLine(arg0)) {
            String sNeu = "";
            
            int i;
            for (i = VFPWindow.getSINGLETON_INSTANCE().getScriptArea().getCaretPosition(); i < s.length(); i++) {
                if (s.charAt(i) == '\n') {
                    break;
                }
            }
            
            sNeu = s.substring(0, i) + RepresentableDefault.END_COMMENT + s.substring(i);
            VFPWindow.getSINGLETON_INSTANCE().getScriptArea().setText(sNeu);
            VFPWindow.getSINGLETON_INSTANCE().getScriptArea().select(i, i + RepresentableDefault.END_COMMENT.length());
        }
        
        if (!arg0.isControlDown() 
                && arg0.getKeyCode() != KeyEvent.VK_UP
                && arg0.getKeyCode() != KeyEvent.VK_DOWN
                && arg0.getKeyCode() != KeyEvent.VK_RIGHT
                && arg0.getKeyCode() != KeyEvent.VK_LEFT) {
            VFPWindow.getSINGLETON_INSTANCE().manageUndo();
        }
    }
    
    /**
     * ID for window location storage.
     */
    protected static final String WINDOW_ID = "GRAPH-BASED-WINDOW";
    @Override public void componentResized(ComponentEvent e) {StaticMethods.storeWindowFramePosition(VFPWindow.getSINGLETON_INSTANCE(), WINDOW_ID);}
    @Override public void componentMoved(ComponentEvent e) {StaticMethods.storeWindowFramePosition(VFPWindow.getSINGLETON_INSTANCE(), WINDOW_ID);}
    @Override public void componentShown(ComponentEvent e) {}
    @Override public void componentHidden(ComponentEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}
    @Override public void mousePressed(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}

    @Override public void propertyChange(PropertyChangeEvent arg0) {GeneralDialog.storeValueOf(VFPWindow.SPLITPANE_POSITION_ID, arg0.getNewValue());}

    @Override
    public void insertUpdate(DocumentEvent e) {
        VFPWindow.getSINGLETON_INSTANCE().setScriptChanged();
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        VFPWindow.getSINGLETON_INSTANCE().setScriptChanged();
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        VFPWindow.getSINGLETON_INSTANCE().setScriptChanged();
    }    
}