/*
 * File name:        PDFGeneratorWindow.java (package veryFastPDF.plugin)
 * Author(s):        Lukas König
 * Java version:     8.0
 * Generation date:  15.11.2013 (08:16:13)
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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.plaf.basic.BasicSplitPaneUI;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter.DefaultHighlightPainter;
import javax.swing.text.Highlighter.HighlightPainter;

import org.apache.commons.io.filefilter.WildcardFileFilter;

import eas.GlobalVariables;
import eas.math.MiscMath;
import eas.miscellaneous.StaticMethods;
import eas.miscellaneous.convenience.ExternalFilePathsManager;
import eas.miscellaneous.convenience.FileLocationEstimator;
import eas.miscellaneous.convenience.GeneralDialog;
import eas.startSetup.ParCollection;
import mainServlet.WebLink;
import net.miginfocom.layout.CC;
import net.miginfocom.swing.MigLayout;
import veryFastPDF.HelpTexts;
import veryFastPDF.SVGVisualizer;
import veryFastPDF.VFPVariables;
import veryFastPDF.pdfProcessors.GraphViz;
import veryFastPDF.pdfProcessors.PDFProcessor;
import veryFastPDF.script.RepresentableAsPDF;
import veryFastPDF.script.RepresentableDefault;
import veryFastPDF.script.RepresentableFactory;
import veryFastPDF.script.ScriptConversionMethods;
import veryFastPDF.web.ConvenienceMethods;

/**
 * @author Lukas König
 */
public class VFPWindow extends JFrame {

    private static VFPWindow SINGLETON_INSTANCE;
    
    private static final long serialVersionUID = -6526082354221356243L;
    private JTextArea scriptArea = new JTextArea("*Insert your code here*");
    private MigLayout migGlobalLayout = new MigLayout("", "center, grow, shrink", "grow");
    private JPanel additionalInformationPanel = new JPanel(new MigLayout("center"));
    private JPanel exercisePanel = new JPanel(new MigLayout("", "center", ""));
    private JPanel exampleButtonsPanel = new JPanel(migGlobalLayout);

    private VFPWindowListener listener;

    private static final String SPLITPANE_POSITION_ID_BASIC_PART = "$$--SPLIT_PANE_VERTICAL_POS--$$";
    protected static String SPLITPANE_POSITION_ID;
    
    protected JButton buttonExit = new JButton("Exit!");
    private JButton buttonCloseSumatra = new JButton("Kill Sumatras!");
    protected HashMap<JButton, RepresentableAsPDF> expButtonsToRepsMapping;
    private List<Class<? extends RepresentableAsPDF>> pdfTypes;
    protected JButton undoButton = new JButton("<<");
    protected JButton unundoButton = new JButton(">>");
    protected String plainPDFButtonText = "Get PDF Source";
    protected JButton buttException = new JButton(plainPDFButtonText);
    protected JButton buttonRestartSumatra = new JButton("Show Sumatra");
    protected JButton buttonOpenWorkingFolder = new JButton("Open working directory");
    protected JButton buttonSaveCurrent = new JButton("Save script");
    protected JButton buttonRemoveComments = new JButton("Remove comments!");
    protected JButton buttonManagePaths = new JButton("Manage file paths...");
    protected JButton buttonInfo = new JButton("Info...");
    private JPanel addInfo = new JPanel();
    private JScrollPane scrollControlPanel;
    JSplitPane splitPane;

    public boolean isPDFtypeAvailable(Class<? extends RepresentableAsPDF> r) {
        for (RepresentableAsPDF r2 : this.getAvailableReps()) {
            if (r2.getClass().equals(r)) {
                return true;
            }
        }
        
        return false;
    }
    
    public JSplitPane getSplitPane() {
        return this.splitPane;
    }
    
    public JScrollPane getScrollControlPanel() {
        return this.scrollControlPanel;
    }
    
    protected JCheckBox refreshOnTheFly = new JCheckBox("Refresh PDF on typing");
    private JCheckBox killSumatrasOnExit = new JCheckBox("Kill Sumatras on exit");
    protected JCheckBox svgMode = new JCheckBox("SVG mode");
    
    public JCheckBox getRefreshOnTheFlyCheckbox() {
        return this.refreshOnTheFly;
    }
    
    protected JButton buttonAskQuestionInBrowser = new JButton("Info II community...");
    protected JButton buttonWeb = new JButton("Web App...");

    private String scriptWithoutComments = null;
    
    public void setScriptChanged() {
        this.scriptWithoutComments = null;
    }
    
    /**
     * @param script  {@code null} to remove comments from script area.
     * @return
     */
    public String getScriptWithoutComments(String script) {
        String scriptToRemoveComments = script;
        
        if (scriptToRemoveComments == null) {
            if (scriptWithoutComments == null) {
                scriptWithoutComments = ScriptConversionMethods.removeComments(this.scriptArea.getText());
            }
            return scriptWithoutComments;
        }

        return ScriptConversionMethods.removeComments(scriptToRemoveComments);
    }

    public JTextArea getScriptArea() {
        return this.scriptArea;
    }
    
    public static File TEMP_DIR;
    
    /**
     * The only constructor.
     * 
     * @param pdfTypes  The available PDF types to be displayed.
     */
    public VFPWindow(List<RepresentableAsPDF> pdfTypes) {
        listener = new VFPWindowListener();
        TEMP_DIR = this.getWorkingDirectory();
        SPLITPANE_POSITION_ID = SPLITPANE_POSITION_ID_BASIC_PART + VFPParameters.isStudentVersion();
        
        if (SINGLETON_INSTANCE != null) {
            throw new RuntimeException("Trying to instantiate more than one " + this.getClass() + ".");
        }
        
        SINGLETON_INSTANCE = this;
        this.runSumatra();
        try {
            Thread.sleep(250); // Wait for sumatra to start up.
        } catch (InterruptedException e1) {
        }

        buttonAskQuestionInBrowser.setForeground(new Color(20, 140, 50));
        buttonWeb.setForeground(new Color(20, 140, 50));
        buttonAskQuestionInBrowser.setToolTipText("Not understanding what's going on? Ask the community for help. (This will open a browser window.)");
        buttonWeb.setToolTipText("Open this script in the " + VFPVariables.PROG_NAME_XWIZZ + " Webapp.");
        buttonSaveCurrent.setToolTipText("Save current script in " + scriptFileName() + ".");
        buttonExit.setToolTipText("Close the VFP window and optionally kill all SumatraPDF windows.");
        buttonInfo.setToolTipText("Show release and copyright notes.");
        
        Font f = new Font("Palatino", 1, 8);
        buttonRestartSumatra.setFont(f);
        buttonOpenWorkingFolder.setFont(f);
        buttonRemoveComments.setFont(f);
        buttonManagePaths.setFont(new Font("Arial", 1, 10));
        buttonManagePaths.setForeground(Color.BLUE);
        buttonCloseSumatra.setFont(f);
        buttonSaveCurrent.setEnabled(false);
        
        this.pdfTypes = new LinkedList<>();
        this.getAvailableReps().addAll(pdfTypes);
        pdfTypes.forEach(t -> this.pdfTypes.add(t.getClass()));
        
        this.setSize(500, 600);
        this.setLocale(null);

        JPanel miniPanel = new JPanel(new MigLayout("", "center", ""));
        miniPanel.add(buttonRestartSumatra);
        miniPanel.add(buttonCloseSumatra);
        
        JPanel undoPanel = new JPanel(new GridLayout(1, 2));
        undoPanel.add(undoButton);
        undoPanel.add(unundoButton);

        JPanel miniPanel1 = new JPanel(new MigLayout());
        
        miniPanel1.add(buttonAskQuestionInBrowser);
        miniPanel1.add(buttonWeb);
        miniPanel1.add(buttonSaveCurrent);
        miniPanel1.add(undoPanel);
        miniPanel1.add(buttonExit);
        miniPanel1.add(buttonInfo);
        miniPanel1.add(buttException);
        this.exercisePanel.add(miniPanel1, new CC().wrap());
        
        JPanel miniPanel2 = new JPanel(new MigLayout());

        miniPanel2.add(refreshOnTheFly);
        miniPanel2.add(killSumatrasOnExit);
        miniPanel2.add(svgMode);
        this.exercisePanel.add(miniPanel2);
        
        refreshOnTheFly.setSelected(true);
        svgMode.setSelected(false);
        killSumatrasOnExit.setSelected(true);
        killSumatrasOnExit.setForeground(Color.RED);
        refreshOnTheFly.addMouseListener(this.listener);
        svgMode.addMouseListener(this.listener);
        JScrollPane scrollScriptPanel = new JScrollPane(this.getScriptArea());
        scrollScriptPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollScriptPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        this.expButtonsToRepsMapping = new HashMap<>();

        String[] pdfTypesString = addExampleButtons(null);
        
        this.additionalInformationPanel.add(this.addInfo);

        // Global positions.
        JPanel controlPanel = new JPanel(migGlobalLayout);
        controlPanel.add(exampleButtonsPanel, new CC().growX().shrink(0).wrap());
        controlPanel.add(additionalInformationPanel, new CC().grow().shrink(0).wrap());
        controlPanel.add(exercisePanel, new CC().shrink(0));

        scrollControlPanel = new JScrollPane(controlPanel);
        scrollControlPanel.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        scrollControlPanel.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);

        splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
                scrollScriptPanel, scrollControlPanel);
        splitPane.setOneTouchExpandable(true);
        ((BasicSplitPaneUI) splitPane.getUI()).getDivider().addMouseListener(this.listener);
        
        String splitpanePos = GeneralDialog.loadValue(SPLITPANE_POSITION_ID);
        if (splitpanePos != null) {
            splitPane.setDividerLocation(Integer.parseInt(splitpanePos));
        } else {
            splitPane.setDividerLocation(300);
        }
        
        splitPane.addPropertyChangeListener("dividerLocation", this.listener);
        
        //Provide minimum sizes for the two components in the split pane
        Dimension minimumSize = new Dimension(50, 50);
        scrollScriptPanel.setMinimumSize(minimumSize);
        controlPanel.setMinimumSize(minimumSize);
        this.getContentPane().add(splitPane);

        this.setVisible(true);
        this.getScriptArea().addKeyListener(this.listener);
        this.buttonRemoveComments.addMouseListener(this.listener);
        this.buttonExit.addMouseListener(this.listener);
        this.buttonCloseSumatra.addMouseListener(this.listener);
        this.undoButton.addMouseListener(this.listener);
        this.undoButton.setToolTipText("Undo last action (STRG+Z)");
        this.unundoButton.addMouseListener(this.listener);
        this.unundoButton.setToolTipText("Redo last undone action (STRG+Y)");
        this.buttException.addMouseListener(this.listener);
        this.buttonSaveCurrent.addMouseListener(this.listener);
        this.buttonRestartSumatra.addMouseListener(this.listener);
        this.buttonOpenWorkingFolder.addMouseListener(this.listener);
        this.buttonManagePaths.addMouseListener(this.listener);
        this.buttonInfo.addMouseListener(this.listener);
        this.buttonAskQuestionInBrowser.addMouseListener(this.listener);
        this.buttonWeb.addMouseListener(this.listener);
        try {
            String stored = StaticMethods.readTextFromFile(
                    new File(this.getWorkingDirectory().getAbsolutePath() + "/" + scriptFileName()), 
                    this.getPars());
            this.getScriptArea().setText(stored);
        } catch (Exception e) {}
        
        this.applyScript();
        
        this.undoList.add(this.getScriptArea().getText());
        this.undoListCarets.add(-1);
        this.positionInUndoList = 0;

        this.requestFocus();
        this.getScriptArea().requestFocus();

        this.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                VFPWindow.this.exit();
            }});
        
        this.setUndoButtonsEnabled();
        
        this.highlightComments();
        
        this.addComponentListener(this.listener);
        StaticMethods.loadWindowFramePosition(this, VFPWindowListener.WINDOW_ID);
        try {
            this.setLabelsAndTitles(this.getRepresentableName(ScriptConversionMethods.getApplicablePDFTypeToplevel(
                    this.getScriptWithoutComments(null),
                    this.getAvailableRepTypes(),
                    null)));
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        this.scriptArea.getDocument().addDocumentListener(this.listener);
        this.listener.addMenu(pdfTypesString);
    }

    protected String getExpButtonText(RepresentableAsPDF r, Integer expNum) {
        String addString = "";
        if (expNum != null) {
            addString = expNum + "/";
        }
        
        return this.getRepresentableName(r) + " (" + addString + r.getExampleScripts().length + ")";
    }
    
    protected String[] addExampleButtons(Collection<String> allowedPDFClassNames) {
        this.exampleButtonsPanel.removeAll();
        
        ArrayList<RepresentableAsPDF> nonZeroExamples 
            = new ArrayList<RepresentableAsPDF>(this.getAvailableReps().size() - 1);
        
        for (RepresentableAsPDF r : this.getAvailableReps()) {
            if (r.getExampleScripts().length > 0) {
                nonZeroExamples.add(r);
            }
        }
        
        List<List<RepresentableAsPDF>> orderedPDFTypes = this.orderTypes(nonZeroExamples);
        int unwrapped = 0;
        for (List<RepresentableAsPDF> gts : orderedPDFTypes) {
            String title = gts.get(0).getPDFProcessorClass().getSimpleName();
            
            if (allowedPDFClassNames == null || allowedPDFClassNames.contains(title)) {
                JComponent repGroup = new JPanel(new GridLayout(3, 5));
                repGroup.setBorder(BorderFactory.createTitledBorder("Examples (" + title + ")"));
                unwrapped += gts.size();
                
                for (RepresentableAsPDF r : gts) {
                    JButton butt = new JButton(this.getExpButtonText(r, null));
                    butt.setToolTipText("Click once or several times to traverse " + r.getClass().getSimpleName() + " examples");
                    repGroup.add(butt);
                    butt.addMouseListener(this.listener);
                    this.expButtonsToRepsMapping.put(butt, r);
                }
                
                if (unwrapped > 7) {
                    this.exampleButtonsPanel.add(repGroup, new CC().grow().width("0").spanX().wrap());
                    unwrapped = 0;
                } else {
                    this.exampleButtonsPanel.add(repGroup, new CC().grow().width("0"));
                }
            }
        }
        
        refreshWholeWindow();
        
        String[] typesStr = new String[orderedPDFTypes.size()];
        int i = 0;
        for (List<RepresentableAsPDF> types : orderedPDFTypes) {
            typesStr[i] = types.get(0).getPDFProcessorClass().getSimpleName();
            i++;
        }
        return typesStr;
    }

    protected void refreshWholeWindow() {
        this.highlightButtonForSelectedType();
        // Pfusch!!
        int pseudRand = this.getWidth() % 2;
        int newSize = this.getWidth() + 1 - 2 * pseudRand;
        this.setSize(newSize, this.getHeight());
        // EO Pfusch.
        this.validate();
        this.repaint();
        StaticMethods.loadWindowFramePosition(this, VFPWindowListener.WINDOW_ID);
        this.scriptArea.requestFocus();
    }

    private List<List<RepresentableAsPDF>> orderTypes(
            List<RepresentableAsPDF> pdfTypesTemp) {
        List<List<RepresentableAsPDF>> orderedList = new LinkedList<List<RepresentableAsPDF>>();
        HashSet<Class<?>> pdfProcessors = new HashSet<>();
        
        for (RepresentableAsPDF rg : pdfTypesTemp) {
            pdfProcessors.add(rg.getPDFProcessorClass());
        }
        
        for (Class<?> pdf : pdfProcessors) {
            LinkedList<RepresentableAsPDF> group = new LinkedList<>();
            
            for (RepresentableAsPDF rg : pdfTypesTemp) {
                if (rg.getPDFProcessorClass().equals(pdf)) {
                    group.add(rg);
                }
            }
            
            orderedList.add(group);
        }
        
        Collections.sort(orderedList, (c1, c2) -> {
                int c = c2.size() - c1.size();
                if (c == 0) {
                    return c1.toString().compareTo(c2.toString());
                } else {
                    return c;
                }
            });
        
        return orderedList;
    }

    private int codeLength;
    
    private void setLabelsAndTitles(String pdfTypeName) {
        this.buttonCloseSumatra.setForeground(Color.red);
        this.setTitle(pdfTypeName + " [" + this.getWorkingDirectory().getAbsolutePath() 
                + "] (Code length: " + this.codeLength + ")");

        String message = "";
        
        if (!message.equals("") && !shown) {
            shown = true;
            GeneralDialog dia = new GeneralDialog(this, message, "Status messages", GeneralDialog.OK_BUTT, null);
            dia.setVisible(true);
        }
    }
    
    private boolean shown = false;
    protected Exception exceptionThrown;
    
    private Color selectedButtonColor = Color.ORANGE;

    private void highlightButtonForSelectedType() {
        this.expButtonsToRepsMapping.keySet().forEach(button -> {
            String text = this.scriptArea.getText();
            RepresentableAsPDF r = this.expButtonsToRepsMapping.get(button);
            if (r.isAcceptableScript(text)) {
                button.setBackground(selectedButtonColor);
                button.setText(this.getExpButtonText(r, null));
            } else {
                button.setBackground(buttonExit.getBackground());
                button.setText(this.getExpButtonText(r, null));
            }
        });
    }

    protected SVGVisualizer svgVisualizer = new SVGVisualizer();

    protected void deleteTempFiles() {
        String[] patterns = new String[] {"*.pdf", "*.tex", "*.log", "*.aux", "*.svg", "*.tmp", "doc_data.txt"};
        for (String s : patterns) {
            deleteFilesMatchingPattern(s);
        }
    }

    private void deleteFilesMatchingPattern(String pattern) {
        File dir = new File(WebLink.getWORKING_DIRECTORY());
        FileFilter fileFilter = new WildcardFileFilter(pattern);
        File[] files = dir.listFiles(fileFilter);
        for (File f : files) {
           f.delete();
        }
    }
    
    public void applyScript() {
        RepresentableDefault.removeKnownStuff();
//        this.deleteTempFiles();
        
        // Checks if decryption necessary first.
        String decryptedScript = ScriptConversionMethods.decryptScript(this.getScriptArea().getText());
        
        highlightButtonForSelectedType();
        processException(null);
        String scriptWithoutComments = this.getScriptWithoutComments(decryptedScript);

        try {
            RepresentableAsPDF r = ScriptConversionMethods.getApplicablePDFTypeToplevel(
                    scriptWithoutComments, 
                    this.getAvailableRepTypes(), 
                    null);
            
            RepresentableAsPDF rNew = ScriptConversionMethods.getConvertedRepresentableIfAny(
                    scriptWithoutComments, 
                    r,
                    this.getAvailableRepTypes());
            
            if (rNew != null) {
                this.getScriptArea().setText(rNew.getRawScript());
                this.applyScript();
                return;
            }
            
            String currentPDFTypeName = VFPWindow.this.getRepresentableName(r);
            VFPWindow.this.setLabelsAndTitles(currentPDFTypeName);
            PDFProcessor pdfProcessor = ScriptConversionMethods.getPDFProcessorFrom(r, this.getWorkingDirectory().getAbsolutePath());
            GlobalVariables.getParameters().logDebug("I finished creating the pdf script, now it's the turn of '" + pdfProcessor.getClass().getSimpleName() + "'...");

            // Ask for very long scripts.
            pdfProcessor = askUserForVeryLongScripts(pdfProcessor);
            if (svgMode.isSelected()) {
                String realScript = pdfProcessor.getPreparedSourceString();
                String svgString = pdfProcessor.getSVGCode(
                        WebLink.defaultFileName(), 
                        WebLink.getWORKING_DIRECTORY(), 
                        realScript,
                        0);

                svgVisualizer.run(svgString);
                svgVisualizer.display();
                this.requestFocus();
                this.getScriptArea().requestFocus();
            } else {
                pdfProcessor.storeAsPDF(
                        WebLink.fileName(WebLink.DEFAULT_OUTPUT_FILE_NAME), 
                        this.getWorkingDirectory().getAbsolutePath());
            }

            JComponent addInfoFromRep = r.getAdditionalInfo();
            
            this.addInfo.removeAll();
            if (addInfoFromRep != null && addInfoFromRep.getComponentCount() != 0) {
                JButton helpButton = new JButton("More help");
                if (r.helpText() == null) {
                    helpButton.setText("Quick help");
                }
                
                addInfoFromRep.add(helpButton);
                helpButton.addMouseListener(new MouseListener() {
                    @Override public void mouseReleased(MouseEvent e) {}
                    @Override public void mousePressed(MouseEvent e) {}
                    @Override public void mouseExited(MouseEvent e) {}
                    @Override public void mouseEntered(MouseEvent e) {}
                    
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        HelpTexts.showLongHelpWindow(r);
                    }
                });
                
                this.addInfo.setBorder(BorderFactory.createTitledBorder(
                        r.getClass().getSimpleName() + " functionality"));
                this.addInfo.add(addInfoFromRep);
            } else {
                this.addInfo.setBorder(null);
            }
            this.revalidate();
            this.repaint();
            
            if (new File(this.getWorkingDirectory().getAbsolutePath() + "/" + WebLink.DEFAULT_OUTPUT_FILE_NAME + "_" + WebLink.getCookieUserName() + ".pdf").length() == 0) {
                throw new RuntimeException("No valid PDF file written.");
            }
            
            GlobalVariables.getParameters().logDebug(pdfProcessor.getClass().getSimpleName() + " drawing terminated successfully.");
        } catch (Exception e) {
            processException(e);
        }
    }

    private PDFProcessor askUserForVeryLongScripts(PDFProcessor pdfProcessor2) {
        PDFProcessor pdfProcessor = pdfProcessor2;
        
        if (pdfProcessor.getSourceString().length() > pdfProcessor.getCodeSizeToBeConsideredLarge()
                && !GeneralDialog.yesRememberNoAnswer(
                    "Large pdf processor code (greater than " 
                        + pdfProcessor.getCodeSizeToBeConsideredLarge() 
                        + " characters)", 
                    "The current script leads to a " 
                        + pdfProcessor.getClass().getSimpleName() 
                        + " code that has " 
                        + pdfProcessor.getSourceString().length() 
                        + " characters.\n"
                        + "It might take " 
                        + pdfProcessor.getClass().getSimpleName() 
                        + " a long time to draw. Proceed?", 
                    "-$-PDF-PROCESSOR-LONG-SCRIPT-$-" + pdfProcessor.getClass().getSimpleName(), 
                    false, 
                    false)) {
            pdfProcessor = new GraphViz(TEMP_DIR.getAbsolutePath(), null);
            pdfProcessor.addln("digraph G {" + 
                    "user aborted" + 
                    "}");
        }
        return pdfProcessor;
    }
    
    private Color exceptionBackground = new Color(255, 222, 222);

    private void processException(Exception e) {
        if (e == null) {
            this.buttException.setText(this.plainPDFButtonText);
            this.buttException.setToolTipText("Create the raw code (in the language of the according PDF processor) for this script.");
            this.buttException.setBackground(this.buttonExit.getBackground());
            this.buttException.setForeground(this.buttonExit.getForeground());
            this.scriptArea.setBackground(Color.WHITE);
            this.buttException.setVisible(false);
        } else { // Exception occurred.
            GlobalVariables.getParameters().logDebug(e.toString());
            this.buttException.setText("Exception occurred!");
            this.buttException.setToolTipText("Show the exception occurred during script evaluation.");
            this.scriptArea.setBackground(exceptionBackground);
            this.buttException.setBackground(Color.RED);
            this.buttException.setForeground(Color.YELLOW);
            this.buttException.setVisible(true);
            this.setTitle("VFP: Exception '" + e + "'");
            
            if (WebLink.EXCEPTION_EXPLANATION != null) {
                RepresentableAsPDF rep = RepresentableFactory.instanceFromScript(WebLink.EXCEPTION_EXPLANATION, null);
                PDFProcessor pdfProcessor = ScriptConversionMethods.getPDFProcessorFrom(rep, this.getWorkingDirectory().getAbsolutePath());
                pdfProcessor.storeAsPDF(
                        WebLink.fileName(WebLink.DEFAULT_OUTPUT_FILE_NAME), 
                        this.getWorkingDirectory().getAbsolutePath());

                WebLink.EXCEPTION_EXPLANATION = null;   
            }
        }
        
        this.exceptionThrown = e;
    }

    private File getSumatraPath() {
        return ExternalFilePathsManager.retrieveExternalFilePath(
                ExternalFilePathsManager.PATH_TO_SUMATRA_ID, 
                true,
                "Choose the path to 'sumatraPDF.exe'.\n"
                + "It is available in your '*program files*/sumatraPDF' folder after installation of the sumatra package from the 'install' folder.\n \n"
                + "[You can get the newest version from www.sumatrapdfreader.org]",
                estimator,
                null,
                null);
    }
    
    private static FileLocationEstimator estimator = new FileLocationEstimator(
            ".*program.*", ".*sumatra.*", ".*sumatra.*.exe.*");

    protected void runSumatra() {
        runSumatra(WebLink.fileName(WebLink.DEFAULT_OUTPUT_FILE_NAME) + ".pdf");
    }
    
    public void runSumatra(String pdfFileName) {
        ConvenienceMethods.execCommand("\"" + this.getSumatraPath().getAbsolutePath() + "\" " 
                + "\"" + this.getWorkingDirectory().getAbsolutePath() + "/" + pdfFileName + "\"");
    }

    public File getWorkingDirectory() {
        return ExternalFilePathsManager.retrieveExternalFilePath(
                ExternalFilePathsManager.PATH_TO_WORKING_DIR_ID, 
                false,
                "Choose a working directory where the PDF generator will store output files in.\n \n"
                + "[This path is arbitrary and you can change it later.]",
                null,
                null,
                null);
    }
    
    protected void runExplorerInWorkingDirectory() {
        try {StaticMethods.openDocument(this.getWorkingDirectory());} catch (IOException e1) {}
    }

    public void setScriptAndManageUndo(String script) {
        this.getScriptArea().setText(script);
        this.manageUndo();
        this.getScriptArea().setCaretPosition(0);
    }

    protected void killSumatras() {
        ConvenienceMethods.execCommand("taskkill.exe /IM sumatrapdf.exe");
    }
    
    protected void exit() {
        if (!this.buttonSaveCurrent.isEnabled() 
                || GeneralDialog.yesNoAnswer(
                        "Store Script?", 
                        "The current script has not been saved - save in " 
                        + scriptFileName() + "?"))
        storeScriptToFile();
        
        if (this.killSumatrasOnExit.isSelected()) {
            this.killSumatras();
        }
        
        svgVisualizer.hide();
        
        System.exit(0);
    }

    protected String scriptFileName() {
        return WebLink.fileName(WebLink.DEFAULT_OUTPUT_FILE_NAME) + "-script.txt";
    }
    
    protected void storeScriptToFile() {
        if (this.buttonSaveCurrent.isEnabled()) {
            String script = this.getScriptArea().getText();
            
            StaticMethods.saveTextToFile(
                    this.getWorkingDirectory().getAbsolutePath(), 
                    scriptFileName(), 
                    script);
//            this.applyScript();
            
            this.buttonSaveCurrent.setEnabled(false);
            this.getPars().logInfo("Script stored in " + this.getWorkingDirectory().getAbsolutePath() + "/" + scriptFileName());
            this.getPars().logInfo("PDF stored in " + this.getWorkingDirectory().getAbsolutePath() + "/" + WebLink.fileName(WebLink.DEFAULT_OUTPUT_FILE_NAME) + ".pdf");
        }
    }
    
    private String getRepresentableName(RepresentableAsPDF r) {
        if (r == null) {
            return "???";
        } else {
            return r.getClass().getSimpleName();
        }
    }
    
    private String oldScript = "";
    
    private LinkedList<String> undoList = new LinkedList<String>();
    private LinkedList<Integer> undoListCarets = new LinkedList<Integer>();
    private int maxUndoLength = 1000;
    private int positionInUndoList = 0;

    private void addHighlightScriptParts(String beginTag, String endTag, Color color) {
        String s = this.getScriptArea().getText();
        HighlightPainter painter = new DefaultHighlightPainter(color);
        int begIndex = s.indexOf(beginTag);
        
        while (begIndex >= 0) {
            int endIndex = MiscMath.findMatchingEndTagLevelwise(s, beginTag, endTag, begIndex);
            
            if (endIndex >= 0) {
                try {
                    this.getScriptArea().getHighlighter().addHighlight(begIndex, endIndex + endTag.length(), painter);
                } catch (BadLocationException e) {
                }
            }
            
            begIndex = s.indexOf(beginTag, begIndex + 1);
        }
    }
    
    public class Highlighter {
        public Highlighter(String beginTag, String endTag, Color color) {
            this.beginTag1 = beginTag;
            this.endTag1 = endTag;
            this.color1 = color;
        }
        
        private String beginTag1;
        private String endTag1;
        private Color color1;
    }
    
    public void addHighlighter(Highlighter highlight) {
        this.additionalHighlights.add(highlight);
    }
    
    private HashSet<Highlighter> additionalHighlights = new HashSet<>();
    
    private void removeAllHighlights() {
        this.getScriptArea().getHighlighter().removeAllHighlights();
    }
    
    private void highlightComments() {
        if (this.scriptArea.getSelectedText() == null) {
            removeAllHighlights();
        }
        
        addHighlightScriptParts(RepresentableDefault.BEGIN_COMMENT, RepresentableDefault.END_COMMENT, new Color(Color.RED.getRed(), Color.RED.getGreen(), Color.RED.getBlue(), 90));
        addHighlightScriptParts(RepresentableDefault.INSCR_BEG_TAG, RepresentableDefault.INSCR_END_TAG, new Color(255, 255, 33, 90));
        addHighlightScriptParts(RepresentableDefault.DECL_BEG_TAG, RepresentableDefault.DECL_END_TAG, new Color(200, 200, 200, 90));
        
        for (Highlighter h : additionalHighlights) {
            addHighlightScriptParts(h.beginTag1, h.endTag1, h.color1);
        }

        this.additionalHighlights.clear();
    }

    protected void manageUndo() {
        this.codeLength = this.getScriptArea().getText().length();
        if (!this.getScriptArea().getText().equals(oldScript)) {
            this.buttonSaveCurrent.setEnabled(true);
            while (this.positionInUndoList < this.undoList.size() - 1) {
                this.undoList.removeLast();
                this.undoListCarets.removeLast();
            }
                
            undoList.add(this.getScriptArea().getText());
            undoListCarets.add(this.getScriptArea().getCaretPosition());
            positionInUndoList = this.undoList.size() - 1;
            if (undoList.size() > maxUndoLength) {
                undoList.removeFirst();
                undoListCarets.removeFirst();
                positionInUndoList--;
            }
            this.oldScript = this.getScriptArea().getText();
            if (refreshOnTheFly.isSelected()) {
                this.applyScript();
            }
        }
        
        setUndoButtonsEnabled();
        this.highlightComments();
    }

    private void setUndoButtonsEnabled() {
        if (this.positionInUndoList == 0) {
            this.undoButton.setEnabled(false);
        } else {
            this.undoButton.setEnabled(true);
        }
        
        if (this.positionInUndoList == this.undoList.size() - 1) {
            this.unundoButton.setEnabled(false);
        } else {
            this.unundoButton.setEnabled(true);
        }
    }
    
    protected void commentSelectedArea(String beginTag, String endTag) {
        String s = this.getScriptArea().getText();
        int beginSel = this.getScriptArea().getSelectionStart();
        int endSel = this.getScriptArea().getSelectionEnd();
        String sNeu = s.substring(0, beginSel) 
                        + beginTag 
                        + s.substring(beginSel, endSel)
                        + endTag
                        + s.substring(endSel);
        this.setScriptAndManageUndo(sNeu);
        this.getScriptArea().setCaretPosition(endSel + RepresentableDefault.BEGIN_COMMENT.length() + RepresentableDefault.END_COMMENT.length());
    }

    protected void unundo() {
        this.positionInUndoList++;
        this.setToPositionInUndoList();
        this.buttonSaveCurrent.setEnabled(true);
        setUndoButtonsEnabled();
    }

    protected void undo() {
        this.positionInUndoList--;
        this.setToPositionInUndoList();
        this.buttonSaveCurrent.setEnabled(true);
        setUndoButtonsEnabled();
    }

    private void setToPositionInUndoList() {
        if (this.positionInUndoList >= this.undoList.size()) {
            this.positionInUndoList = this.undoList.size() - 1;
        }
        if (this.positionInUndoList < 0) {
            this.positionInUndoList = 0;
        }
        
        this.getScriptArea().setText(this.undoList.get(this.positionInUndoList));

        try { // Move caret to correct position.
            int cursorposition = this.undoListCarets.get(this.positionInUndoList);
            if (cursorposition < 0) {
                cursorposition = this.undoListCarets.get(this.positionInUndoList + 1);
            }
            this.getScriptArea().setCaretPosition(cursorposition);
        } catch (Exception e) {}

        this.oldScript = this.getScriptArea().getText();
        this.applyScript();
    }
    
    public static String reomveComments(String script) {
        String script2 = "";
        boolean removing = false;
        
        for (int i = 0; i < script.length(); i++) {
            if (removing) {
                if (script.charAt(i) == '\n') {
                    removing = false;
                }
            } else {
                if (script.charAt(i) == '%') {
                    removing = true;
                } else {
                    script2 += script.charAt(i);
                }
            }
        }
        
        return script2;
    }
    
    public ParCollection getPars() {
        return GlobalVariables.getParameters();
    }

    public List<Class<? extends RepresentableAsPDF>> getAvailableRepTypes() {
        return this.pdfTypes;
    }

    public List<RepresentableAsPDF> getAvailableReps() {
        ArrayList<RepresentableAsPDF> reps = new ArrayList<>(this.pdfTypes.size());
        this.pdfTypes.forEach(t -> reps.add(RepresentableFactory.getRepByClass(t)));
        return reps;
    }
    
    public static VFPWindow getSINGLETON_INSTANCE() {
//        if (SINGLETON_INSTANCE == null) {
//            SINGLETON_INSTANCE = new VFPWindow(new LinkedList<>());
//        }
        
        return SINGLETON_INSTANCE;
    }
}
