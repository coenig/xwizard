/*
 * Datei:          AllgemeinerDialog.java
 * Autor(en):      Lukas König
 * Java-Version:   6.0
 * Erstellt (vor): 26.06.2007
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

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.TextArea;
import java.awt.TextComponent;
import java.awt.TextField;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Utilities;
import javax.swing.text.html.HTMLEditorKit;

import eas.GlobalVariables;
import eas.math.geometry.Vector2D;
import eas.miscellaneous.StaticMethods;
import eas.startSetup.ParCollection;
import eas.startSetup.marbBuilder.MultiLineLabel;
import eas.veryFastPDF.MainLink;
import net.miginfocom.layout.AC;
import net.miginfocom.layout.CC;
import net.miginfocom.layout.LC;
import net.miginfocom.swing.MigLayout;


/**
 * Implementierung eines allgemeinen Dialogsfensters.
 *
 * @author Lukas König
 */
public class GeneralDialog extends JDialog implements ActionListener, KeyListener {
    
    /**
     * Der OK-String.
     */
    public static final String OK = "Ok";
    
    /**
     * Der YES-String.
     */
    public static final String YES = "Yes";

    /**
     * Der YES-Remember-String.
     */
    public static final String YES_REM = YES + " (remember decision)";

    /**
     * Der NO-String.
     */
    public static final String NO = "No";
    
    /**
     * Der YES-Remember-String.
     */
    public static final String NO_REM = NO + " (remember decision)";

    /**
     * Der Cancel-String.
     */
    public static final String CANCEL = "Cancel";

    /**
     * Allgemeine OK-Dialog-Buttons.
     */
    public static final String[] OK_BUTT = {OK};

    /**
     * Allgemeine OK-Abbrechen-Dialog-Buttons.
     */
    public static final String[] OK_CANCEL_BUTT = {OK, CANCEL};

    /**
     * Allgemeine Ja-Nein-Dialog-Buttons.
     */
    public static final String[] YES_NO = {YES, NO};

    /**
     * Allgemeine Ja-Nein-Abbrechen-Dialog-Buttons.
     */
    public static final String[] YES_NO_CANCEL = {YES, NO, CANCEL};

    /**
     * Allgemeine Ja-Nein-Abbrechen-Dialog-Buttons.
     */
    public static final String[] YES_NO_REM = {YES, NO, NO_REM};

    /**
     * Allgemeine Ja-Nein-Abbrechen-Dialog-Buttons.
     */
    public static final String[] YES_NO_REM_SIMPLE = {YES, NO_REM};

    /**
     * Allgemeine Ja-Nein-Abbrechen-Dialog-Buttons.
     */
    public static final String[] YES_REM_NO_SIMPLE = {YES_REM, NO};

    /**
     * Allgemeine Ja-Nein-Abbrechen-Dialog-Buttons.
     */
    public static final String[] YES_REM_NO = {YES, YES_REM, NO};

    /**
     * Allgemeine Ja-Nein-Abbrechen-Dialog-Buttons.
     */
    public static final String[] YES_REM_NO_REM = {YES, YES_REM, NO, NO_REM};

    /**
     * Die Serial-Version-ID vom 28. April 2007
     */
    private static final long serialVersionUID = 2504188077455833733L;

    /**
     * Das gewählte Element.
     */
    private String result;

    /**
     * Die Textbox deren Wert zum Zeitpunkt des Klicks auf einen Button zurück-
     * gegeben wird.
     */
    private TextComponent text;

    /**
     * Eine Liste.
     */
    private java.awt.List liste;
    
    /**
     * zusätzliche Textbox deren Wert zum Zeitpunkt des Klicks auf einen 
     * Button zurückgegeben wird, falls sie angefordert war.
     */
    private TextComponent zusatzText;
    
    /**
     * Konstruktor mit Vektor statt ArrayList für Buttons.
     * 
     * @param owner          Das Vaterfenster.
     * @param msg            Anzuzeigende Nachricht.
     * @param titel          Der Titel des Fensters.
     * @param buttons        Liste von Strings, die von den anzuzeigenden
     *                       Buttons dargestellt werden.
     * @param textBox        In einer Textbox darzustellender String.
     * @param einzeilig      Ob die Textbox einzeilig ist.
     */
    public GeneralDialog(
            final Frame     owner,
            final String    msg,
            final String    titel,
            final String[]  buttons,
            final String    textBox,
            final boolean   moveCaretToFront) {
        this(
                owner, 
                msg, 
                titel, 
                GeneralDialog.arrToArrList(buttons), 
                textBox,
                moveCaretToFront);
    }

    public GeneralDialog(
            final Frame     owner,
            final String    msg,
            final String    titel,
            final String[]  buttons,
            final String    textBox) {
        this(owner, msg, titel, buttons, textBox, false);
    }

    /**
     * Konvertiert einen Vektor aus Strings in eine ArrayList.
     * 
     * @param liste  Die Liste.
     * 
     * @return  Die konvertierte Liste.
     */
    private static ArrayList<String> arrToArrList(final String[] liste) {
        ArrayList<String> arrListe = new ArrayList<String>(liste.length);
        
        for (String s : liste) {
            arrListe.add(s);
        }
        
        return arrListe;
    }

    public GeneralDialog(
            final Frame             owner,
            final String            msg,
            final String            titel,
            final String[]          buttons,
            final String            textBox,
            final int               textBoxHeight,
            final int               textBoxWidth,
            final boolean           einzeilig) {
        this(owner, msg, titel, convert(buttons), textBox, textBoxHeight, textBoxWidth, einzeilig, false);
//        this.text.selectAll();
    }

    private static ArrayList<String> convert(final String[] buttons) {
        ArrayList<String> butts = new ArrayList<String>(buttons.length);
        for (String s : buttons) {
            butts.add(s);
        }
        return butts;
    }
    
    /**
     * Der Konstruktor.
     *
     * @param owner          Das Vaterfenster.
     * @param msg            Anzuzeigende Nachricht.
     * @param titel          Der Titel des Fensters.
     * @param buttons        Liste von Strings, die von den anzuzeigenden
     *                       Buttons dargestellt werden.
     * @param textBox        In einer Textbox darzustellender String.
     * @param textBoxHeight  Höhe der Textbox in ZEILEN!
     * @param textBoxWidth   Breite der Textbox in ZEICHEN!.
     * @param einzeilig      Ob die Textbox einzeilig ist.
     */
    public GeneralDialog(
            final Frame             owner,
            final String            msg,
            final String            titel,
            final ArrayList<String> buttons,
            final String            textBox,
            final int               textBoxHeight,
            final int               textBoxWidth,
            final boolean           einzeilig,
            final boolean           moveCaretToFront) {
        super(owner, titel, true);

        final int locationX = 200;
        final int locationY = 200;
        final int borderX = 4;
        final int borderY = 4;
        final int lrMargin = 20;
        final int tbMargin = 10;
        Iterator<String> it;
        Button butt;

        this.result = "";
        this.zusatzText = null;

        this.setAlwaysOnTop(true);
        
        //Fenster
        this.setLayout(new BorderLayout(borderX, borderY));
        this.setResizable(true);
        this.setLocation(locationX, locationY);
        //Message
        if (textBox == null) {
            MultiLineLabel lab = new MultiLineLabel(msg);
            lab.setLeftRightMargin(lrMargin);
            lab.setTopBottomMargin(tbMargin);
            this.add(lab, BorderLayout.CENTER);
        } else {
            if (einzeilig) {
                this.text = new TextField(textBox,
                                     textBoxWidth);
                this.text.addKeyListener(this);
                this.text.selectAll();
            } else {
                this.text = new TextArea(textBox,
                                         textBoxHeight,
                                         textBoxWidth);
            }
            this.add(this.text, BorderLayout.CENTER);
            
            this.text.setCaretPosition(0);
            if (!moveCaretToFront) {
                this.text.selectAll();
            }
        }
        //Buttons
        Panel panel = new Panel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER));

        this.stdButton = buttons.get(0);
        it = buttons.iterator();
        while (it.hasNext()) {
            butt = new Button(it.next());
            butt.addActionListener(this);
            panel.add(butt);
        }
        this.add(panel, BorderLayout.SOUTH);
        this.pack();
        
        this.setSize(Math.max(this.getWidth(), 400), Math.max(this.getHeight(),  100));
        this.setLocationRelativeTo(null);
    }

    private String stdButton = "";
    
    /**
     * Resets the cursor in the depicted text box to the top left position.
     */
    public void resetTextBoxCaret() {
        this.text.setCaretPosition(0);
    }

    public GeneralDialog(
            final Frame             owner,
            final String            msg,
            final String            titel,
            final ArrayList<String> buttons,
            final String            textBox,
            final int               textBoxHeight,
            final int               textBoxWidth,
            final boolean           einzeilig) {
        this(owner, msg, titel, buttons, textBox, textBoxHeight, textBoxWidth, einzeilig, false);
    }
    
    /**
     * Erzeugt einen Dialog basierend auf einer Liste.
     * 
     * @param owner           Elternfenster.
     * @param title           Der Titel des Fensters.
     * @param message         Anzuzeigende Nachricht.
     * @param elemente        Die Listenelemente.
     * @param button1         Ein Button.
     * @param button2         Ein Button.
     * @param multi           Ob Mehrfachselektion möglich ist.
     * @param zusatzTextFeld  Ob ein zusätzliches Textfeld angezeigt werden 
     *                        soll (>= 0) oder nicht (< 0). Gleichzeitig die
     *                        Voreinstellung des Textfeldes.
     */
    public GeneralDialog(
            final Frame owner,                
            final String title,
            final String message,
            final java.util.List<String> elemente,
            final String button1,
            final String button2,
            final boolean multi,
            final int zusatzTextFeld) {
        super(owner, title, true);
  
        this.setAlwaysOnTop(true);

        if (zusatzTextFeld >= 0) {
            this.zusatzText = new TextField();
            this.zusatzText.setText("" + zusatzTextFeld);
        } else {
            this.zusatzText = null;
        }
        
        this.result = "";
        this.liste = new java.awt.List(elemente.size(), multi);
        Button but1 = new Button(button1);
        Button but2 = new Button(button2);
        MultiLineLabel lab = new MultiLineLabel(message);

        this.setLayout(new GridLayout(2, 2));
        this.add(this.liste);
        if (this.zusatzText == null) {
            this.add(lab);
        } else {
            this.add(this.zusatzText);
            this.setTitle(this.getTitle() + " (" + message + ")");
        }
        lab.setLeftRightMargin(50);
        
        this.add(but1);
        this.add(but2);

        but1.addActionListener(this);
        but2.addActionListener(this);
        
        this.setBounds(100, 100, 500, 150);

        Iterator<String> it = elemente.iterator();
        while (it.hasNext()) {
            this.liste.add(it.next().toString());
        }
        
        this.setLocationRelativeTo(null);
    }
    
    /**
     * Der Konstruktor.
     *
     * @param owner    Das Vaterfenster.
     * @param msg      Anzuzeigende Nachricht.
     * @param titel    Der Titel des Fensters.
     * @param buttons  Liste von Strings, die von den anzuzeigenden Buttons
     *                 dargestellt werden.
     * @param textBox  In einer Textbox darzustellender String.
     */
    public GeneralDialog(
            final Frame owner,
            final String msg,
            final String titel,
            final ArrayList<String> buttons,
            final String textBox,
            final boolean moveCaretToFront) {

        this(owner,
             msg,
             titel,
             buttons,
             textBox,
             20,
             100,
             false,
             moveCaretToFront);
    }

    public GeneralDialog(
            final Frame owner,
            final String msg,
            final String titel,
            final ArrayList<String> buttons,
            final String textBox) {
        this(owner, msg, titel, buttons, textBox, false);
    }

    /**
     * Fängt das Klicken auf einen Button ab.
     *
     * @param event  Das ausgelöste Ereignis.
     */
    @Override
    public void actionPerformed(final ActionEvent event) {
        this.result = event.getActionCommand();
        this.setVisible(false);
        this.dispose();
    }

    /**
     * Gibt die Wahl des Benutzers zurück.
     *
     * @return  Das Ergebnis des Benutzers.
     */
    public String getResult() {
        return this.result;
    }

    public void setResult(final String res) {
        this.result = res;
    }
    
    /**
     * Gibt den Text der Textbox zum Klickzeitpunkt zurück.
     *
     * @return  Text der Textbox zum Klickzeitpunkt.
     */
    public String getText() {
        return this.text.getText();
    }

    /**
     * Gibt den Text der Zusatztextbox zum Klickzeitpunkt zurück.
     * 
     * @return  Text der Zusatztextbox zum Klickzeitpunkt.
     */
    public String getZusatzText() {
        if (this.zusatzText != null) {
            return this.zusatzText.getText();
        } else {
            return null;
        }
    }
    
    /**
     * @return Returns the liste.
     */
    public java.awt.List getListe() {
        return this.liste;
    }
    
    /**
     * Sets the position of the window.
     * 
     * @param pos  Position vector.
     */
    public void setPosition(final Vector2D pos) {
        this.setBounds((int) pos.x, (int) pos.y, 
                       this.getWidth(), this.getHeight());
    }
    
    /**
     * Sets the size of the window.
     * 
     * @param size  Size vector.
     */
    public void setSize(final Vector2D size) {
        this.setBounds(this.getX(), this.getY(), 
                       (int) size.x, (int) size.y);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            this.result = this.stdButton;
            this.setVisible(false);
            this.dispose();
        }
    }
    
    @Override public void keyTyped(KeyEvent e) {}
    @Override public void keyReleased(KeyEvent e) {}

    private static String ONE_TIME_ID_BASE = "ONE-TIME-ID";
    private static String LAST_USED_ONE_TIME_ID = ONE_TIME_ID_BASE;
    private static HashMap<String, String> yesNoRemUserDecisions = loadAllUserYesNoDecisions();
    private static HashSet<String> yesNoRemStoreUserDecisions = new HashSet<>();
    private static HashMap<String, Integer> intUserDecisions = new HashMap<>();
    private static HashMap<String, String> stringUserDecisions = new HashMap<>();
    
    /**
     * @return  A "unique" one-time id string to use for "remember my answer",
     *          long-time calculations and similar purposes. The term "unique"
     *          is a little euphemistic, but it is very, very unlikely to
     *          hit the same string twice.
     */
    public static String getUniqueRandomOneTimeID() {
        LAST_USED_ONE_TIME_ID += new Random().nextInt(10);
        
        if (LAST_USED_ONE_TIME_ID.length() > 50) {
            LAST_USED_ONE_TIME_ID = ONE_TIME_ID_BASE + LAST_USED_ONE_TIME_ID.hashCode();
        }
        
        return LAST_USED_ONE_TIME_ID;
    }

    @SuppressWarnings("unchecked")
    private static HashMap<String, String> loadAllUserYesNoDecisions() {
        Object obj;
        try {
            FileInputStream f_in = new FileInputStream(GlobalVariables.getParameters().getStdDirectory() + "/userDecisions.dat");
            ObjectInputStream objIn = new ObjectInputStream (f_in);
            obj = objIn.readObject();
            objIn.close();
            return (HashMap<String, String>) obj;
        } catch (ClassNotFoundException | IOException e) {
            return new HashMap<>();
        }
    }
    
    private static void storeAllUserYesNoDecisions() {
        HashMap<String, String> allDecisions = loadAllUserYesNoDecisions();
        for (String id : yesNoRemStoreUserDecisions) {
            allDecisions.put(id, yesNoRemUserDecisions.get(id));
        }
        
        FileOutputStream fOut;
        try {
            fOut = new 
                FileOutputStream(GlobalVariables.getParameters().getStdDirectory() + "/userDecisions.dat");
            ObjectOutputStream objOut = new
                    ObjectOutputStream (fOut);
            objOut.writeObject (allDecisions);
            objOut.close();
        } catch (IOException e) {
        }
    }

    /**
     * Prompt a yes-no answer to the user.
     * 
     * @param title     Title of the question.
     * @param question  Text of the question.
     * @return  True or false depending on the user's choice.
     */
    public static boolean yesNoAnswer(String title, String question) {
        GeneralDialog dia = new GeneralDialog(null, question, title, GeneralDialog.YES_NO, null, false);
        dia.setVisible(true);
        return dia.getResult().equals(GeneralDialog.YES);
    }
    
    /**
     * Prompt a yes-no answer to the user, allows a "remember yes" choice.
     * 
     * @param title     Title of the question.
     * @param question  Text of the question.
     * @param id        The id for this remember operation.
     * @param forever   If the user decision is stored on disc.
     * @return  True or false depending on the user's choice.
     */
    public static boolean yesRememberNoAnswer(
            String title, 
            String question, 
            String id, 
            boolean forever, 
            boolean editable) {
        if (yesNoRemUserDecisions.get(id) != null && yesNoRemUserDecisions.get(id).equals(GeneralDialog.YES_REM)) {
            return true;
        }
        
        String[] buttonTexts = GeneralDialog.YES_REM_NO;
        
        if (forever) {
            buttonTexts = GeneralDialog.YES_REM_NO_SIMPLE;
        }
                
        GeneralDialog dia;
        if (editable) {
            dia = new GeneralDialog(null, null, title, buttonTexts, question, false);
        } else {
            dia = new GeneralDialog(null, question, title, buttonTexts, null, false);
        }
        
        dia.setVisible(true);
        
        if (dia.getResult().equals(GeneralDialog.YES_REM)) {
            yesNoRemUserDecisions.put(id, GeneralDialog.YES_REM);
            if (forever) {
                yesNoRemStoreUserDecisions.add(id);
                storeAllUserYesNoDecisions();
            }
        }
        
        return dia.getResult().equals(GeneralDialog.YES) || dia.getResult().equals(GeneralDialog.YES_REM);
    }

    /**
     * Prompt a yes-no answer to the user, allows a "remember no" choice.
     * 
     * @param title     Title of the question.
     * @param question  Text of the question.
     * @param id        The id for this remember operation.
     * @param forever   If the user decision is stored on disc.
     * @return  True or false depending on the user's choice.
     */
    public static boolean yesNoRememberAnswer(
            String title, 
            String question, 
            String id, 
            boolean forever, 
            boolean editable) {
        if (yesNoRemUserDecisions.get(id) != null && yesNoRemUserDecisions.get(id).equals(GeneralDialog.NO_REM)) {
            return false;
        }
        
        String[] buttonTexts = GeneralDialog.YES_NO_REM;
        
        if (forever) {
            buttonTexts = GeneralDialog.YES_NO_REM_SIMPLE;
        }
                
        GeneralDialog dia;
        
        if (editable) {
            dia = new GeneralDialog(null, null, title, buttonTexts, question, false);
        } else {
            dia = new GeneralDialog(null, question, title, buttonTexts, null, false);
        }
        
        dia.setVisible(true);
        
        if (dia.getResult().equals(GeneralDialog.NO_REM)) {
            yesNoRemUserDecisions.put(id, GeneralDialog.NO_REM);
            if (forever) {
                yesNoRemStoreUserDecisions.add(id);
                storeAllUserYesNoDecisions();
            }
        }
        
        return dia.getResult().equals(GeneralDialog.YES) || dia.getResult().equals(GeneralDialog.YES_REM);
    }

    /**
     * Prompt a yes-no answer to the user, allows a "remember" choice for both
     * "yes" and "no".
     * 
     * @param title     Title of the question.
     * @param question  Text of the question.
     * @param id        The id for this remember operation.
     * @param forever   If the user decision is stored on disc.
     * @return  True or false depending on the user's choice.
     */
    public static boolean yesRememberNoRememberAnswer(
            String title, 
            String question, 
            String id, 
            boolean forever, 
            boolean editable) {
        if (yesNoRemUserDecisions.get(id) != null && yesNoRemUserDecisions.get(id).equals(GeneralDialog.NO_REM)) {
            return false;
        }

        if (yesNoRemUserDecisions.get(id) != null && yesNoRemUserDecisions.get(id).equals(GeneralDialog.YES_REM)) {
            return true;
        }
        
        GeneralDialog dia;
        if (editable) {
            dia = new GeneralDialog(null, null, title, GeneralDialog.YES_REM_NO_REM, question, false);
        } else {
            dia = new GeneralDialog(null, question, title, GeneralDialog.YES_REM_NO_REM, null, false);
        }
        
        dia.setVisible(true);
        
        if (dia.getResult().equals(GeneralDialog.NO_REM)) {
            yesNoRemUserDecisions.put(id, GeneralDialog.NO_REM);
            if (forever) {
                yesNoRemStoreUserDecisions.add(id);
                storeAllUserYesNoDecisions();
            }
        }
        
        if (dia.getResult().equals(GeneralDialog.YES_REM)) {
            yesNoRemUserDecisions.put(id, GeneralDialog.YES_REM);
            if (forever) {
                yesNoRemStoreUserDecisions.add(id);
                storeAllUserYesNoDecisions();
            }
        }
        
        return dia.getResult().equals(GeneralDialog.YES) || dia.getResult().equals(GeneralDialog.YES_REM);
    }

    /**
     * Resets the given "remember" id. (Also possible: always use a new one.)
     * 
     * @param id  The id to reset.
     */
    public static void resetRememberedDecision(String id) {
        yesNoRemUserDecisions.put(id, null);
    }
    
    /**
     * Resets all "remember" ids. (Also possible: always use a new one.)
     */
    public static void resetRememberedDecisions() {
        yesNoRemUserDecisions.clear();
    }

    /**
     * Prompts the user to input a number. Remembers the last input number
     * and suggests it to the user.
     * 
     * @param title          Title of the window.
     * @param questionToAsk  Question text to show.
     * @param id             The id for remembering the last user input.
     * @return  The integer input by the user (null if cancelled or so).
     */
    public static Integer getNumberFromUser(String title, String questionToAsk, String id) {
        String question = questionToAsk;
        
        if (intUserDecisions.get(id) != null) {
            question = "" + intUserDecisions.get(id);
        }
        
        try {
            GeneralDialog dia = new GeneralDialog(
                    null, 
                    null,
                    title, 
                    GeneralDialog.OK_CANCEL_BUTT, 
                    question,
                    10,
                    10,
                    true);
            dia.text.selectAll();
            dia.setVisible(true);
            
            if (dia.getResult().equals(GeneralDialog.OK)) {
                intUserDecisions.put(id, Integer.parseInt(dia.getText()));
                return Integer.parseInt(dia.getText());
            }
            
            return null;
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * Prompts the user to input a string. Remembers the last input number
     * and suggests it to the user.
     * 
     * @param title          Title of the window.
     * @param questionToAsk  Question text to show.
     * @param id             The id for remembering the last user input.
     * @return  The string input by the user (null if cancelled or so).
     */
    public static String getStringFromUser(String title, String questionToAsk, String id) {
        String question = questionToAsk;
        
        if (stringUserDecisions.get(id) != null) {
            question = "" + stringUserDecisions.get(id);
        }
        
        GeneralDialog dia = new GeneralDialog(
                null, 
                null,
                title, 
                GeneralDialog.OK_CANCEL_BUTT, 
                question,
                10,
                10,
                true);
        dia.text.selectAll();
        dia.setVisible(true);
        
        if (dia.getResult().equals(GeneralDialog.OK)) {
            stringUserDecisions.put(id, dia.getText());
            return dia.getText();
        }
        
        return null;
    }

    /**
     * Shows a message to the user and waits for confirmation.
     * 
     * @param message  The message text to show.
     * @param title    The window title.
     */
    public static void message(String message, String title, boolean showEditable) {
        message(message, title, GeneralDialog.OK, showEditable);
    }

    /**
     * Shows a message to the user and waits for confirmation.
     * 
     * @param message     The message text to show.
     * @param title       The window title.
     * @param buttonText  The text of the confirmation button.
     */
    public static void message(String message, String title, String buttonText, boolean showEditable) {
        if (MainLink.isApplicationOriginDesktop()) {
            GeneralDialog dia;
            if (showEditable) {
                dia = new GeneralDialog(
                        null, 
                        null,
                        title, 
                        new String[] {buttonText}, 
                        message, 
                        message.length() - message.replace("\n", "").length() + 2,
                        100,
                        false);
            } else {
                dia = new GeneralDialog(
                        null, 
                        message,
                        title, 
                        new String[] {buttonText}, 
                        null, 
                        false);
            }
            
            dia.setVisible(true);
        }
    }

    // From here on: Long time operation check.
    
    private static HashMap<String, Long> maxTimeStartingTimes = new HashMap<String, Long>();
    
    /**
     * Resets the given id to make it reusable. (Also possible: use a new one.)
     * 
     * @param id  The id to reset.
     */
    public static void resetLongTimeOperationID(String id) {
        maxTimeStartingTimes.remove(id);
        GeneralDialog.resetRememberedDecision(id);
    }

    /**
     * If in web mode, this constant defines the time an operation is maximally
     * allowed to last to be continued. After this time, the operation is
     * terminated the next time the continueLongOperation method is called.
     * When in desktop mode, this constant does not have any effect.
     * You might also want to look into 
     * {@link GeneralDialog#DESKTOP_MAXTIME_FOR_LONG_OPERATIONS}.
     */
    public static final int WEB_MAXTIME_FOR_LONG_OPERATIONS = 30_000;
    
    /**
     * You might also want to look into 
     * {@link GeneralDialog#WEB_MAXTIME_FOR_LONG_OPERATIONS}.
     */
    public static final int DESKTOP_MAXTIME_FOR_LONG_OPERATIONS = 10_000;
    
    public static boolean continueLongOperation(String id) {
        return continueLongOperation(
                "Long-time operation", 
                "This appears to be a long-time operation, do you want to continue?", 
                DESKTOP_MAXTIME_FOR_LONG_OPERATIONS, 
                id);
    }
    
    private static boolean longOperationsWatchActivated = true;
    
    /**
     * @param activate  If false, all operations will continue indefinitely.
     */
    public static void activateLongOperationsWatch(boolean activate) {
        longOperationsWatchActivated = activate;
    }
    
    /**
     * Prompts the user if she wants to continue operations whose executing
     * times exceed a given max time. Call resetLongTimeOperationID before
     * using this method.
     * 
     * @param title     The title of the question window.
     * @param question  The question text to ask the user.
     * @param maxTime   The max time after which the question is asked in desktop mode.
     * @param id        The unique id of this long time operation.
     * @return  If the operation is to be continued (i.e. if either max time
     *          is not reached or the user decided to continue the execution).
     */
    public static boolean continueLongOperation(
            String title, String question, long maxTimeMilis, String id) {
        if (!longOperationsWatchActivated) {
            return true;
        }
        
        Long startingTime = maxTimeStartingTimes.get(id);
        long maxTime = maxTimeMilis;
        
        if (!MainLink.isApplicationOriginDesktop()) { // Use global max time when in web mode.
            maxTime = WEB_MAXTIME_FOR_LONG_OPERATIONS;
        }
        
        if (startingTime == null) {
            maxTimeStartingTimes.put(id, System.currentTimeMillis());
        } else {
            long currentTime = System.currentTimeMillis() - startingTime;
            
            if (currentTime > maxTime) {
                if (MainLink.isApplicationOriginDesktop() // Don't ask user in web mode. 
                        && yesRememberNoAnswer(title, question, id, false, false)) {
                    maxTimeStartingTimes.put(id, System.currentTimeMillis());
                } else {
                    resetLongTimeOperationID(id);
                    return false;
                }
            }
        }
        
        return true;
    }
    
    private static final String VALUE_STORE_FILE_NAME = "values.dat";
    private static final String SEPARATOR = "=";
    
    public static void storeValueOf(String idRaw, Object toStoreRaw) {
        String toStore = StaticMethods.createSafeString(toStoreRaw.toString());
        String id = StaticMethods.createSafeString(idRaw);
        
        ParCollection params = GlobalVariables.getParameters();
        String dir = params.getStdDirectory();
        
        if (dir == null) {
            dir = ".";
        }
        
        File valueFile = new File(
                dir
                + "/" 
                + VALUE_STORE_FILE_NAME);
        
        if (!valueFile.exists()) {
            try {
                valueFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        
        List<String> values = StaticMethods.readTextArrayFromFile(valueFile, params);
        boolean found = false;
        
        for (int i = 0; i < values.size(); i++) {
            String[] split = values.get(i).split(SEPARATOR);
            
            if (split[0].equals(id)) {
                values.set(i, id + SEPARATOR + toStore);
                found = true;
            }
        }

        if (!found) {
            values.add(id + SEPARATOR + toStore);
        }
        
        StaticMethods.storeCollectionElementsAsText(
                dir, 
                VALUE_STORE_FILE_NAME, 
                values);
    }
    
    public static String loadValue(String idRaw) {
        String id = StaticMethods.createSafeString(idRaw);
        ParCollection params = GlobalVariables.getParameters();
        String dir = params.getStdDirectory();

        if (dir == null) {
            dir = ".";
        }
        
        File valueFile = new File(
                dir
                + "/" 
                + VALUE_STORE_FILE_NAME);

        if (!valueFile.exists()) {
            return null;
        }
        
        List<String> values = StaticMethods.readTextArrayFromFile(valueFile, params);

        for (String v : values) {
            String[] split = v.split(SEPARATOR);
            
            if (split[0].equals(id)) {
                return StaticMethods.restoreFromSafeString(split[1]);
            }
        }

        return null;
    }
    
    private static JFrame frame;
    
    public static void disposeHTMLWindow() {
        if (frame != null) {
            frame.dispose();
        }
    }
    
    private static HashSet<String> ids = new HashSet<>();
    
    private static int getRowCount(JTextPane textPane) {
        int totalCharacters = textPane.getText().length(); 
        int lineCount = (totalCharacters == 0) ? 1 : 0;

        try {
           int offset = totalCharacters; 
           while (offset > 0) {
              offset = Utilities.getRowStart(textPane, offset) - 1;
              lineCount++;
           }
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        
        return lineCount;
    }
    
    public static void showHTML(
            String html, 
            String title,
            String ID // Set to null to show always, otherwise show only once.
            ) {
        disposeHTMLWindow();
        
        if (ID != null && ids.contains(ID)) {
            return;
        }
        
        ids.add(ID);
        
        MigLayout layout = new MigLayout(
                new LC().fillX(),
                new AC().align("center").gap("rel").grow().fill(),
                new AC().gap("10"));

        JButton butt = new JButton("OK");

        JTextPane jTextPane = new JTextPane();
        HTMLEditorKit eKit = new HTMLEditorKit();
        jTextPane.setEditorKit(eKit);
        jTextPane.setText(html);
        JScrollPane scrollPane = new JScrollPane(jTextPane);
        
        jTextPane.setCaretPosition(0);
        
        frame = new JFrame();
        frame.setTitle(title);
        frame.getContentPane().setLayout(layout);
        frame.setSize(700, getRowCount(jTextPane) * 20);
        frame.setLocationRelativeTo(null);
        
        frame.getContentPane().add(scrollPane, new CC().wrap());
        frame.getContentPane().add(butt);
        frame.setMinimumSize(new Dimension(500, 300));
        frame.setVisible(true);

        butt.addMouseListener(new MouseListener() {
            @Override public void mouseReleased(MouseEvent e) {}
            @Override public void mousePressed(MouseEvent e) {}
            @Override public void mouseExited(MouseEvent e) {}
            @Override public void mouseEntered(MouseEvent e) {}
            
            @Override 
            public void mouseClicked(MouseEvent e) {
                frame.dispose();
            }
        });
    }
}
