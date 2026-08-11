/*
 * Datei:          StaticMethods.java
 * Autor(en):      Lukas König, http://haibo.iteye.com/blog/322239 (unten)
 * Java-Version:   6.0
 * Erstellt (vor): 04.10.2008
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

package eas.miscellaneous;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Desktop;
import java.awt.Graphics2D;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.lang.reflect.Array;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
//import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.jar.JarFile;
import java.util.zip.Deflater;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.Inflater;
import java.util.zip.ZipEntry;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.joda.time.DateTime;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.FontMapper;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfTemplate;
import com.lowagie.text.pdf.PdfWriter;

import eas.GlobalVariables;
import eas.math.geometry.Rectangle2D;
import eas.miscellaneous.convenience.GeneralDialog;
import eas.miscellaneous.convenience.StaticWindow;
import eas.miscellaneous.system.FileNamePostfixFilter;
import eas.plugins.Plugin;
import eas.plugins.standard.eaPlugin.EAPlugin;
import eas.startSetup.ParCollection;
import eas.startSetup.SingleParameter;
import eas.startSetup.logging.AbstractMsg;
import eas.startSetup.logging.MsgDebug;
import eas.startSetup.logging.MsgError;
import eas.startSetup.logging.MsgInfo;
import eas.startSetup.logging.MsgOutput;
import eas.startSetup.logging.MsgStage1;
import eas.startSetup.logging.MsgWarning;
import eas.startSetup.logging.MsgWeb;

/**
 * Implementiert einige nützliche Methoden, die keiner anderen Klasse
 * zugeordnet werden können.
 *
 * @author Lukas König
 */
public class StaticMethods {

    /**
     * Der Bezeichner für das leere Wort.
     */
    public static final String EPSILON = "epsilon";

    private static final String SAFE_CHAR_SET = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String[] SAFE_CHARACTERS_ASCII_LIKE = new String[256];
    private static final HashMap<String, Integer> SAFE_CHARACTERS_ASCII_LIKE_REVERSE = new HashMap<>();
    
    static {
        outer: for (int j = 0; j < SAFE_CHAR_SET.length(); j++) {
            for (int i = j * SAFE_CHAR_SET.length(); i < (j + 1) * SAFE_CHAR_SET.length(); i++) {
                if (i >= SAFE_CHARACTERS_ASCII_LIKE.length) {
                    break outer;
                }
                
                SAFE_CHARACTERS_ASCII_LIKE[i] = 
                        "" 
                    + SAFE_CHAR_SET.charAt(j)
                    + SAFE_CHAR_SET.charAt(i - j * SAFE_CHAR_SET.length());
                
                SAFE_CHARACTERS_ASCII_LIKE_REVERSE.put(SAFE_CHARACTERS_ASCII_LIKE[i], i);
            }
        }
    }
    
    /**
     * Ungültiger Modus, der an Stellen zu verwenden ist, wo eine 
     * Kennzeichnung notwendig ist, dass der Translator in der aktuellen
     * Form nicht benutzt werden sollte.
     */
    public static final int MODUS_UNGUELTIG = -1;
    
    /**
     * Verhaltensautomaten-Modus.
     */
    public static final int MODUS_VERHALTEN = 0;
    
    /**
     * übersetzermodus.
     */
    public static final int MODUS_TRANSLATOR = 1;

    /**
     * Log-Stufe Stage1.
     */
    public static final int LOG_STAGE1 = -1;
    
    /**
     * Log-Stufe Output.
     */
    public static final int LOG_OUTPUT = 0;
    
    /**
     * Log-Stufe Debug.
     */
    public static final int LOG_DEBUG = 1;
    
    /**
     * Log-Stufe Info.
     */
    public static final int LOG_INFO = 2;
    
    /**
     * Log-Stufe Warning.
     */
    public static final int LOG_WARNING = 3;
    
    /**
     * Log-Stufe Error.
     */
    public static final int LOG_ERROR = 4;
    
    /**
     * Highest logging level: WEB.
     */
    public static final int LOG_WEB = 5;
    
    /**
     * Der Zeichensatz zum Zeichnen des Inputfeldes.
     */
    public static final char[] ZEICHEN_INPUT = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', // 0-9
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', // 10-19 
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', // 20-29
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', // 30-39
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', // 40-49
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', // 50-59
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', // 60-69
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', // 70-79
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', // 80-89
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', // 90-99
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', // 100-109
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', // 110-119
        '0', '1', '.', '3', '°', '5', '6', ' ', '#', '9', // 120-129
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', // 130-139
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', // 140-149
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', // 150-159
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', // 160-169
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', // 170-179
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', // 180-189
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', // 190-199
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', // 200-209
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', // 210-219
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', // 220-229
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', // 230-239
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', // 240-249
        '0', '1', '2', '3', '4', '5'};                    // 250-255

    /**
     * Die Prioritätenliste für die verschiedenen Farben des Inputfeldes.
     */
    public static final int[] PRIORITAET_INPUT = {128,
        0, 1, 2, 3, 4, 5, 6, 7, 8, 9,
        10, 11, 12, 13, 14, 15, 16, 17, 18, 19,
        20, 21, 22, 23, 24, 25, 26, 27, 28, 29,
        30, 31, 32, 33, 34, 35, 36, 37, 38, 39,
        40, 41, 42, 43, 44, 45, 46, 47, 48, 49,
        50, 51, 52, 53, 54, 55, 56, 57, 58, 59,
        60, 61, 62, 63, 64, 65, 66, 67, 68, 69,
        70, 71, 72, 73, 74, 75, 76, 77, 78, 79,
        80, 81, 82, 83, 84, 85, 86, 87, 88, 89,
        90, 91, 92, 93, 94, 95, 96, 97, 98, 99,
        100, 101, 102, 103, 104, 105, 106, 107, 108, 109,
        110, 111, 112, 113, 114, 115, 116, 117, 118, 119,
        120, 121, 123, 125, 126, 127, 129,
        130, 131, 132, 133, 134, 135, 136, 137, 138, 139,
        140, 141, 142, 143, 144, 145, 146, 147, 148, 149,
        150, 151, 152, 153, 154, 155, 156, 157, 158, 159,
        160, 161, 162, 163, 164, 165, 166, 167, 168, 169,
        170, 171, 172, 173, 174, 175, 176, 177, 178, 179,
        180, 181, 182, 183, 184, 185, 186, 187, 188, 189,
        190, 191, 192, 193, 194, 195, 196, 197, 198, 199,
        210, 211, 212, 213, 214, 215, 216, 217, 218, 219,
        220, 221, 222, 223, 224, 225, 226, 227, 228, 229,
        230, 231, 232, 233, 234, 235, 236, 237, 238, 239,
        240, 241, 242, 243, 244, 245, 246, 247, 248, 249,
        250, 251, 252, 253, 254, 255,
        124, 122,
        };

    /**
     * Der Zeichensatz zum Zeichnen des Inputfeldes.
     */
    public static final char[] ZEICHEN_FELD = {
        ' ', '§', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', // 0-9
        ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', // 10-19 
        ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', // 20-29
        ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', // 30-39
        ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', // 40-49
        ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', // 50-59
        ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', // 60-69
        ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', // 70-79
        ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', // 80-89
        ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', // 90-99
        ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', // 100-109
        ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', // 110-119
        ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', // 120-129
        ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', // 130-139
        ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', // 140-149
        ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', // 150-159
        ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', // 160-169
        ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', // 170-179
        ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', // 180-189
        ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', // 190-199
        ' ', ' ', ' ', ' ', ' ', ' ', ' ', '°', ' ', ' ', // 200-209
        ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', // 210-219
        ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', // 220-229
        ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', // 230-239
        ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', // 240-249
        ' ', ' ', ' ', ' ', '#', 'R'};                    // 250-255
    
    /**
     * Die Prioritätenliste für die verschiedenen Farben des Inputfeldes.
     */
    public static final int[] PRIORITAET_FELD = {1, 255, 254, 207, 0};
    
    /**
     * überlagerung des Default-Konstruktors.
     */
    public StaticMethods() {
        
    }
    
    public static int intAt(List<Integer> list, int num) {
        if (num < 0 || num >= list.size()) {
            return 0;
        }
        
        return list.get(num);
    }
    
    /**
     * Konvertiert eine Translator-Standardsequenz aus Zeiten, als im Programm
     * noch keine ergänzenden Kanten existiert haben und somit die Befehle
     * verschoben waren, in einen äquivalente Standardsequenz im neuen 
     * Programm.
     * 
     * @return
     */
    public static ArrayList<Integer> convertTransFromFMGWithoutCompletingTransitionsToAequivalentNew(final List<Integer> oldTranslator) {
        int i = 0;
        ArrayList<Integer> result = new ArrayList<Integer>(oldTranslator.size());
        int resSeq;
        
        for (int seq : oldTranslator) {
            resSeq = seq;
            
            if (intAt(oldTranslator, i - 2) == 0 && intAt(oldTranslator, i - 3) == 0 && intAt(oldTranslator, i - 4) == 0) {
                // Wir sind an zweiter Stelle Y am Beginn eines Zustands: 0, 0, 0, X, Y.
                if (seq >= 106) {
                    resSeq = seq + 1;
                }
            }
            
            result.add(resSeq);
            i++;
        }
        
        return result;
    }
    
    /**
     * Schreibt zwei Strings in die Konsole und kennzeichnet ihre Unterschiede.
     * 
     * @param s1      Der erste String.
     * @param s2      Der zweite String.
     * @param params  Die Parameter.
     */
    public static void lhuksDiff(
            final String s1,
            final String s2) {
        ParCollection params = GlobalVariables.getParameters();
        
        String unterschiede = "";

        if (s1.equals(s2)) {
            unterschiede = "Strings sind identisch.";
        } else {
            for (int i = 0; i < s1.length() || i < s2.length(); i++) {
                if (s1.length() <= i || s2.length() <= i) {
                    unterschiede += "-";
                } else {
                    if (s1.charAt(i) == s2.charAt(i)) {
                        unterschiede += " ";
                    } else {
                        unterschiede += "#";
                    }
                }
            }
        }
        
        
        StaticMethods.log(
                StaticMethods.LOG_INFO,
                //"\n(" + s1.length() + "): " + 
                s1 + "\n",
                params,
                "plain",
                null);
        StaticMethods.log(
                StaticMethods.LOG_INFO,
                //"(" + s2.length() + "): " + 
                s2 + "\n",
                params,
                "plain",
                null);
        StaticMethods.log(
                StaticMethods.LOG_INFO,
                unterschiede + "\n",
                params,
                "plain",
                null);
    }
    
    /**
     * Zerlegt einen String in Tokens, wobei <code>trenn</code>
     * als Trennsymbol verwendet wird. Am Beginn und am Ende des Strings
     * müssen (und dürfen!) keine Trennsymbole stehen. Das Trennzeichen
     * darf leer sein (in diesem Ausnahmefall steht das Trennzeichen
     * doch am Anfang und gleichzeitig am Ende des Strings).
     * <P>
     * BSP1: zerlege("a/b/c/d", "/") => [a, b, c, d]
     * BSP2: zerlege("abcdefg", "")  => [a, b, c, d, e, f, g]
     * BSP3: zerlege("a-+-b-+-c-+-d, "-+-") => [a, b, c, d]
     *
     * NUTZE BESSER: String.split(.)
     *
     * @param text   Die zu zerlegende Sequenz.
     * @param trenn  Die Trennsequenz.
     *
     * @return  Die Zerlegung in einer <code>ArrayList</code>.
     */
    @Deprecated
    public static ArrayList<String> zerlege(final String text,
                                    final String trenn) {
        ArrayList<String> seq = new ArrayList<String>();
        int end = 0;
        int anf = 0;
        int trennLen = trenn.length();

        while (end < text.length()) {
            while (end + trennLen < text.length()
                   && !text.substring(end, end + trennLen).equals(trenn)) {
                end++;
            }
            if (end + trennLen >= text.length()) {
                end = end + trennLen;
            }
            if (anf != 0) {
                anf = anf + trennLen;
            }
            if (trennLen == 0 && anf == 0) {
                end++;
            }
            if (text.substring(anf, end).equals(EPSILON)) {
                end = end + 0;
            } else {
                seq.add(text.substring(anf, end));
            }
            anf = end;
            end++;
        }
        if (trenn.equals("") && text.length() > 1) {
            seq.add(text.substring(anf, text.length()));
        }

        return seq;
    }

    /**
     * Erzeugt eine Liste aus Zahlen aus einer bereinigten Stringsequenz.
     * 
     * @param seq  Die bereinigte Stringsequenz.
     * 
     * @return  Die Liste aus Zahlen.
     */
    public static LinkedList<Integer> listSeqAusString(final String seq) {
        ArrayList<String> zerlegt;
        LinkedList<Integer> liste = new LinkedList<Integer>();
        
        if (seq.equals("")) {
            return liste;
        }
        
        zerlegt = StaticMethods.zerlege(seq.substring(1), " ");
        
        for (int i = 0; i < zerlegt.size(); i++) {
            liste.add(Integer.parseInt(zerlegt.get(i)));
        }
        
        return liste;
    }

    /**
     * Erzeugt eine Liste aus Zahlenlisten aus einer Liste bereinigter 
     * Stringsequenzen.
     * 
     * @param seq  Die bereinigten Stringsequenzen.
     * 
     * @return  Die Listen aus Zahlen.
     */
    @SuppressWarnings(value = { "unchecked" })
    public static LinkedList<Integer>[] listSeqAusStrings(final String[] seq) {
        LinkedList<Integer>[] listen = new LinkedList[seq.length];
        
        for (int i = 0; i < seq.length; i++) {
            listen[i] = StaticMethods.listSeqAusString(seq[i]);
        }
        
        return listen;
    }

    /**
     * Erzeugt eine bereinigte Stringsequenz aus einer Liste von Zahlen.
     * 
     * @param seq  Liste von Zahlen.
     * 
     * @return  Die bereinigte Stringsequenz.
     */
    

    /**
     * Klont ein beliebiges (serialisierbares) Objekt.
     *
     * @param o  Das zu klonende Objekt.
     *
     * @return  Das geklonte Objekt.
     */
    @SuppressWarnings("unchecked")
    public static <E> E seriaClone(final E o) {
        try {
            // Serialisieren des Objekts
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream os = new ObjectOutputStream(out);
            os.writeObject(o);
            os.flush();
            // Deserialisieren des Objekts
            ByteArrayInputStream in = new ByteArrayInputStream(out
                    .toByteArray());
            ObjectInputStream is = new ObjectInputStream(in);
            Object ret = is.readObject();
            is.close();
            os.close();
            return (E) ret;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Erzeugt eine normierte Byte-Zahl aus einem Integer. Der Integer muss
     * einen Byte-Wert speichern. Die Ausgabe ist ein String der Länge drei,
     * wobei bei Zahlen der Länge < 3 vorne mit Nullen aufgefüllt wird.
     *
     * @param z  Die zu normierende Zahl als Integer.
     *
     * @return  Die normierte Zahl als String.
     */
    public static String normZahl(final int z) {
        return StaticMethods.normZahl(z, 3);
    }
    
    /**
     * Erzeugt eine normierte Zahl aus einem Integer. Dabei werden führende
     * Nullen eingefügt. Die Zahl darf nicht länger als n sein.
     *
     * @param z  Die zu normierende Zahl als Integer.
     * @param n  Die Anzahl der Stellen.
     *
     * @return  Die normierte Zahl als String.
     */
    public static String normZahl(
            final long z,
            final int n) {
        final String zahl = "" + z;
        String nullen = "";
        
        if (zahl.length() <= n) {
            for (int i = 0; i < n - zahl.length(); i++) {
                nullen = "0" + nullen;
            }
        } else {
            throw new RuntimeException("Zahl kann nicht normiert werden.");
        }

        return nullen + zahl;
    }
    
    /**
     * Testet, ob <code>c</code> eine Ziffer aus {0, ..., 9} ist.
     *
     * @param c  Der zutestende Character.
     *
     * @return  <code>true</code>, gdw. <code>c</code> eine Ziffer ist.
     */
    public static boolean istZiff(final char c) {
        return c == '0' || c == '1' || c == '2' || c == '3' || c == '4'
               || c == '5' || c == '6' || c == '7' || c == '8' || c == '9';
    }

    /**
     * Gibt die (erste) Position eines Strings in einem Array zurück.
     * Führende 0'en und Groß- Kleinschreibung werden ignoriert.
     *
     * @param strArray  Der Array, in dem der String gesucht werden soll.
     * @param str       Der String, der gesucht werden soll.
     *
     * @return  Die (erste) Position des Strings im Array. Falls der String
     *          nicht im Aray vorkommt, wird -1 zurückgegeben.
     */
    public static int posSuch(final String[] strArray,
                              final String   str) {
        boolean zahl = true;

        try {
            for (int i = 0; i < str.length(); i++) {
                if (!istZiff(str.charAt(i))) {
                    zahl = false;
                }
            }

            if (zahl) {
                return Integer.parseInt(str);
            }

            for (int i = 0; i < strArray.length; i++) {
                if ((strArray[i].toLowerCase()).equals(str.toLowerCase())) {
                    return i;
                }
            }

            return -1;
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    /**
     * Gibt zurück, ob zwei Zahlen "ungefähr" gleich sind. Die maximale
     * Unterscheidung zwischen ihnen wird auch übergeben.
     *
     * @param a    Die erste der zu vergleichenden Zahlen.
     * @param b    Die zweite der zu vergleichenden Zahlen.
     * @param max  Die maximale Unterscheidung.
     *
     * @return  Ob "a ~ b".
     */
    public static boolean ungefGleich(final int a,
                                   final int b,
                                   final int max) {
        return Math.abs(a - b) <= max;
    }

    /**
     * Testet, ob ein Zeichen ein Sonderzeichen aus den in der Klasse
     * Konstanten definierten Zeichen ist.
     *
     * @param c  Das zu testende Zeichen.
     *
     * @return  <code>true</code>, gdw. das Zeichen ein Sonderzeichen ist.
     */
    

    /**
     * Testet, ob ein Zeichen ein Operator ist.
     *
     * @param c  Das zu überprüfende Zeichen.
     *
     * @return  Ob c ein Operator ist.
     */
    

    /**
     * Testet, ob ein Zeichen ein Operator ist.
     *
     * @param c  Das zu überprüfende Zeichen.
     *
     * @return  Ob c ein Operator ist.
     */
    

    /**
     * Testet, ob ein Zeichen ein boolescher Operator ist.
     *
     * @param c  Das zu überprüfende Zeichen.
     *
     * @return  Ob c ein boolescher Operator ist.
     */
    

    /**
     * Erzeugt eine <code>Condition</code> aus einer Text-Bedingung, die
     * nicht formatiert sein muss.
     * 
     * @param bedingung  Die (möglicherweise unformatierte) Bedingung.
     * 
     * @return  Die Condition.
     */
    
    
    /**
     * Erzeugt eine <code>Condition</code> aus einer Text-Bedingung.
     * <P>
     * Kommentar: Beizeiten verbessern - läuft bisher mit quadratischer
     *            Laufzeit in der Anzahl der Operatoren.
     *            => Verbesserung durch Umwandeln in Postfix.
     *            
     * @param bedingung  Die Textbedingung.
     * 
     * @return  Die <code>Condition</code>.
     */
    

    /**
     * Erzeugt einen Vektor aus <code>Condition</code>s aus einem Vektor aus
     * Text-Bedingungen.
     *            
     * @param beds  Die Textbedingungen.
     * 
     * @return  Die <code>Condition</code>s.
     */
    

    /**
     * Erzeugt Conditions aus einer Liste von unformatierten Bedingungen.
     * 
     * @param beds  Die Bedingungen.
     * 
     * @return  Die Conditions.
     */
    

    /**
     * Schreibt einen Log-Eintrag in die Konsole (siehe überladene Methode).
     * Der Zusatzparameter wird als "" übergeben.
     * 
     * @param stufe      Die Stufe des Logs.
     * @param s          Der auszugebende String.
     * @param params     Die Parameter.
     */
    public static void log(final int stufe,
                           final String s,
                           final ParCollection params) {
        StaticMethods.log(stufe, s, params, "", null);
    }

    /**
     * Gibt eine als Message gegebene Nachricht aus, wobei der Zeitstempel mit 
     * ausgegeben wird. Das Message-Objekt wird an die erzeugte 
     * Ausgabenachricht angehängt.
     * 
     * @param msg     Die Nachricht.
     * @param params  Der Parametersatz.
     */
    public static void log(
            final AbstractMsg msg,
            final ParCollection params) {
        StaticMethods.log(
                msg.getStufe(),
                "[Nachrichtenstempel: " 
                    + msg.getDatum() 
                    + "] " 
                    + msg.getMessage(),
                params,
                "",
                msg);
    }
    
    public static void logWeb(
            final String s,
            final ParCollection params) {
        StaticMethods.log(StaticMethods.LOG_WEB, s, params);
    }
    
    public static void logError(
            final String s,
            final ParCollection params) {
        StaticMethods.log(StaticMethods.LOG_ERROR, s, params);
    }

    public static void logWarning(
            final String s,
            final ParCollection params) {
        StaticMethods.log(StaticMethods.LOG_WARNING, s, params);
    }

    public static void logInfo(
            final String s,
            final ParCollection params) {
        StaticMethods.log(StaticMethods.LOG_INFO, s, params);
    }

    public static void logDebug(
            final String s,
            final ParCollection params) {
        StaticMethods.log(StaticMethods.LOG_DEBUG, s, params);
    }

    public static void logOutput(
            final String s,
            final ParCollection params) {
        StaticMethods.log(StaticMethods.LOG_OUTPUT, s, params);
    }

    public static void logStage1(
            final String s,
            final ParCollection params) {
        StaticMethods.log(StaticMethods.LOG_STAGE1, s, params);
    }

    /**
     * Produces and stores a logging message and optionally prints it to the 
     * console.
     * 
     * The logging levels:<BR>
     * -1: STAGE1 <BR>
     * 0 : OUTPUT <BR>
     * 1 : DEBUG <BR>
     * 2 : INFO <BR>
     * 3 : WARNING <BR>
     * 4 : ERROR <BR>
     * 5 : WEB (for web mode, only WEB and possibly ERROR and/or WARNING should be output) <BR>
     * 
     * @param stufe      Die Stufe des Logs.
     * @param s          Der auszugebende String.
     * @param params     Die Parameter.
     * @param zusatzPar  Ein Zusatzparameter. Bisher definiert: 
     *                   - "plain" => Ausgabe ohne Logstufe und newline.
     * @param zusatz     Ein zusätzliches Objekt, das die Mitteilung genauer
     *                   spezifiziert.
     */
    public static void log(final int stufe,
                           final String s,
                           final ParCollection params,
                           final String zusatzPar,
                           final Object zusatz) {
        int logParStufe;
        AbstractMsg msg = null;
        
        ParCollection parameters = params;
        
        if (parameters == null) {
            parameters = GlobalVariables.getParameters();
        }
        
        if (parameters != null && parameters.getLogOutputLevel() != null) {
            logParStufe = parameters.getLogOutputLevel().intValue();
        } else {
            logParStufe = 0;
        }
        
        // Ausgabe des Loggings.
        if (stufe >= logParStufe) {
            if (!zusatzPar.toLowerCase().equals("plain")) {
                System.out.println();
                System.out.print(new Date() + " - ");
                
                if (stufe == StaticMethods.LOG_STAGE1) {
                    System.out.print("STAGE1: ");
                } else if (stufe == LOG_OUTPUT) {
                    System.out.print("OUTPUT: ");
                } else if (stufe == LOG_DEBUG) {
                    System.out.print("DEBUG : ");
                } else if (stufe == LOG_INFO) {
                    System.out.print("INFO  : ");
                } else if (stufe == LOG_WARNING) {
                    System.out.print("WARN  : ");
                } else if (stufe == LOG_ERROR) {
                    System.out.print("ERROR : ");
                } else if (stufe == LOG_WEB) {
                    System.out.print("WEB   : ");
                } else {
                    System.out.print("[N.A.]: ");
                }
            }

            System.out.print(s);
        }

        // Speichern des Loggings.
        if (stufe == LOG_OUTPUT) {
            msg = new MsgOutput(s, System.currentTimeMillis(), zusatz);
        } else if (stufe == LOG_DEBUG) {
            msg = new MsgDebug(s, System.currentTimeMillis(), zusatz);
        } else if (stufe == LOG_INFO) {
            msg = new MsgInfo(s, System.currentTimeMillis(), zusatz);
        } else if (stufe == LOG_WARNING) {
            msg = new MsgWarning(s, System.currentTimeMillis(), zusatz);
        } else if (stufe == LOG_ERROR) {
            msg = new MsgError(s, System.currentTimeMillis(), zusatz);
        } else if (stufe == LOG_STAGE1) {
            msg = new MsgStage1(s, System.currentTimeMillis(), zusatz);
        } else if (stufe == LOG_WEB) {
            msg = new MsgWeb(s, System.currentTimeMillis(), zusatz);
        }
        
        parameters.addMsg(msg);
    }
    
    /**
     * Lädt ein Bild und gibt es zurück.
     * 
     * @param datName  Der Dateiname.
     * 
     * @return  Das Bild.
     */
    public static BufferedImage loadImage(final String datName) {
        File dat = new File(datName);
        
        try {
            BufferedImage image = ImageIO.read(dat);
            return image;
        } catch (IOException e) {
//            StackTraceElement[] stack = e.getStackTrace();
//            for (int i = 0; i < stack.length; i++) {
//                SonstMeth.log(SonstMeth.LOG_DEBUG, 
//                              e.getStackTrace()[i].toString(), 
//                              pars);
//            }
            
            StaticMethods.log(StaticMethods.LOG_INFO, 
                          "Bitmapdatei nicht geladen: " + dat, 
                          GlobalVariables.getParameters());
            return new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
//            throw new RuntimeException("\nBitmapdatei nicht geladen.");
        }
    }
    
    /**
     * Liest eine Bitmapdatei in ein Array aus Pixelfarben.
     * 
     * @param pars  Die Parameter.
     * 
     * @return  2D-Array aus Pixelfarben. 
     */
    public static byte[][] readImage(final String datName) {
        try {
            BufferedImage img = loadImage(datName);
            byte[] a = new byte[1];
            byte[][] aa = new byte[img.getWidth()][img.getHeight()];

            for (int j = 0; j < aa[0].length; j++) {
                for (int i = 0; i < aa.length; i++) {
                    img.getRaster().getDataElements(i, j, 1, 1, a);
                    aa[i][j] = a[0];
                }
            }
            
            return aa;
            
        } catch (final Exception e) {
//            SonstMeth.log(
//                    SonstMeth.LOG_ERROR,
//                    "Bitmap-Feld nicht geladen: " 
//                        + pars.getStdPfad() 
//                        + File.separatorChar 
//                        + pars.getUmgebungDatname(),
//                    pars);
            
            byte[][] aa = new byte[1][1];
            return aa;
        }
    }

    public static void saveTextToFile(
            File storePath,
            String text) {
        saveTextToFile(
                storePath.getParentFile().getAbsolutePath(), 
                storePath.getName(), 
                text);
    }
    
    /**
     * Stores a given string in a text file.
     * 
     * @param directory  The directory to store in.
     * @param fileName   The file name within the directory.
     * @param text       The text to store.
     */
    public static void saveTextToFile(
            final String directory,
            final String fileName,
            final String text) {
        FileWriter f1 = null;
        
        try {
            f1 = new FileWriter(directory + File.separator + fileName);
            f1.write(text);
            f1.close();
        } catch (IOException e) {
            GlobalVariables.getParameters().logInfo("Fehler beim Erstellen der Text-Datei: " + directory + File.separator + fileName);
        }
    }

    public static void storeCollectionElementsAsText(
            final File file,
            final Collection<? extends Object> textVector) {
        storeCollectionElementsAsText(file.getParent(), file.getName(), textVector);
    }

    /**
     * Stores the text output ({@link Object#toString()}) of all elements of a collection in a text file.
     * For ordered collections ({@code List}s) the respective order is preserved. 
     * 
     * @param directory   The directory to store in.
     * @param fileName    The file name within the directory.
     * @param textVector  The elements whose string output is to be stored.
     */
    public static void storeCollectionElementsAsText(
            final String directory,
            final String fileName,
            final Collection<? extends Object> textVector) {
        FileWriter f1 = null;
        Iterator<? extends Object> it = textVector.iterator();
        
        try {
            f1 = new FileWriter(directory + File.separator + fileName);
            while (it.hasNext()) {
                f1.write(it.next() + "\r\n");
            }
            f1.close();
        } catch (IOException e) {
            GlobalVariables.getParameters().logInfo("Fehler beim Erstellen der Text-Datei: " + directory + File.separator + fileName);
        }
    }
    
    /**
     * Stores the text output ({@link Object#toString()}) of all elements of an array in a text file.
     * The array order is preserved. 
     * 
     * @param directory   The directory to store in.
     * @param fileName    The file name within the directory.
     * @param textVector  The elements whose string output is to be stored.
     */
    public static void storeArrayElementsAsText(
            final String directory,
            final String fileName,
            final Object[] textVector) {
        ArrayList<String> liste = new ArrayList<String>(textVector.length);
        
        for (Object s : textVector) {
            liste.add(s.toString());
        }
        
        StaticMethods.storeCollectionElementsAsText(directory, fileName, liste);
    }

    public static LinkedList<String> readTextArrayFromFile(
            final File datei) {
        return readTextArrayFromFile(datei, null);
    }

    public static LinkedList<String> readTextArrayFromFile(
            final File datei, 
            final ParCollection params) {
        return readTextArrayFromFile(datei, params, false);
    }

    public static LinkedList<String> readTextArrayFromFile(
            final File datei, 
            final boolean quiet) {
        return readTextArrayFromFile(datei, null, quiet);
    }

    public static LinkedList<String> readTextArrayFromFile(
            final File datei, 
            final ParCollection params,
            final boolean quiet) {
        String pfad = datei.getParent();
        String dateiname = datei.getName();
        LinkedList<String> textArray = StaticMethods.readTextArrayFromFile(pfad, dateiname, params, quiet);
        return textArray;
    }

    public static LinkedList<String> readTextArrayFromFile(
            final String verz,
            final String datName) {
        return readTextArrayFromFile(verz, datName, null);
    }

    public static LinkedList<String> readTextArrayFromFile(
            final String verz,
            final String datName,
            final ParCollection params) {
        return readTextArrayFromFile(verz, datName, params, false);
    }

    public static LinkedList<String> readTextArrayFromFile(
            final String verz,
            final String datName,
            final boolean quiet) {
        return readTextArrayFromFile(verz, datName, null, quiet);
    }

    /**
     * Liest zeilenweise Text aus einer Textdatei und gibt einen String-Array
     * der gesamten Datei zurück.
     * 
     * @param verz     The directory path.
     * @param datName  The file name.
     * @param params   The parameter collection.
     * @param quiet    If NO error message is logged in error case.
     * 
     * @return  Der zeilenweise gelsene Text aus der Datei.
     */
    public static LinkedList<String> readTextArrayFromFile(
            final String verz,
            final String datName,
            final ParCollection params,
            final boolean quiet) {
        String zwisch;
        LinkedList<String> textArray = new LinkedList<String>();
        
        try {
            BufferedReader f1 =
                new BufferedReader(
                        new FileReader(verz + File.separator + datName));
            
            zwisch = f1.readLine();
            while (zwisch != null) {
                textArray.add(zwisch);
                zwisch = f1.readLine();
            }
            f1.close();
        } catch (IOException e) {
            if (!quiet && params != null) {
                log(StaticMethods.LOG_INFO, 
                    "Text file could not be read: " 
                        + verz + File.separator + datName + "\n" + e,
                     params);
                throw new RuntimeException(
                        "Text file could not be read.");
            }
        }
        
        return textArray;
    }

    public static LinkedList<String[]> readCSVFile(
            final File datei,
            final String separator) {
        String pfad = datei.getParent();
        String dateiname = datei.getName();
        return readCSVFile(pfad, dateiname, separator, GlobalVariables.getParameters());
    }

    /**
     * @param verz
     * @param datName
     * @param separator
     * @param params
     * @return
     */
    public static LinkedList<String[]> readCSVFile(
            final String verz,
            final String datName,
            final String separator,
            final ParCollection params) {
        LinkedList<String> rawData = StaticMethods.readTextArrayFromFile(
                verz, datName, params);
        LinkedList<String[]> data 
            = new LinkedList<String[]>();
        for (String s : rawData) {
            data.add(s.split(separator));
        }
        return data;
    }
    
    /**
     * Extrahiert die Fitness einer bestimmten Population aus einer Aufnahme.
     * 
     * @param a       Die Aufnahme.
     * @param popNum  Die Nummer der Population.
     * 
     * @return  Die Fitnesswerte der Roboter der Population als String.
     */
    
    
    /**
     * Extrahiert die Zusatzwerte der aktuellen Aufnahme.
     * 
     * @param aufn  Die Aufnahme.
     * 
     * @return Die gespeicherten Zusatzinformationen (Kollisionen und GP).
     */
    

    /**
     * Bennent eine Datei um.
     * 
     * @param quelle  Name der Quelldatei.
     * @param ziel    Name der Zieldatei.
     * @param params  Die Parameter.
     */
    public static void renameDAT(final String quelle,
                                 final String ziel,
                                 final ParCollection params) {
        String pfad = params.getStdDirectory();
        File quellDatei = new File(pfad + File.separator + quelle);
        File zielDatei = new File(pfad + File.separator + ziel);
        if (quellDatei.renameTo(zielDatei)) {
            return;
        } else {
            throw new RuntimeException("Datei konnte nicht umbenannt werden");
        }
    }
    
    /**
     * Bennent alle Dateien im Standardverzeichnis um. Dabei wird die 
     * Dateinamenerweiterung von ext1 auf ext2 geändert.
     * 
     * @param ext1    Die Quelldateiendung.
     * @param ext2    Die Zieldateiendung.
     * @param params  Die Parameter
     */
    public static void renameALL(final String ext1,
                                 final String ext2,
                                 final ParCollection params) {
        File pfad = new File(params.getStdDirectory());
        String[] alleDat = pfad.list(new FileNamePostfixFilter(ext1));
        String neuName;
        
        if (alleDat == null) {
            return;
        }
        
        for (int i = 0; i < alleDat.length; i++) {
            neuName = alleDat[i].substring(0, 
                                           alleDat[i].length() - 4) 
                                               + "." + ext2;
            
            renameDAT(alleDat[i], neuName, params);
        }
    }
    
    /**
     * löscht den String mit dem angegebenen Namen.
     * 
     * @param datNam  Der Dateiname.
     * @param params  Die Parameter.
     */
    public static boolean deleteDAT(final String datNam) {
        File datei = new File(datNam);
        if (datei.delete()) {
            return true;
        } else {
        	GlobalVariables.getParameters().logDebug("Cannot delete file: '" + datei.getAbsolutePath() + "'.");
        	return false;
        }
    }
    
    /**
     * löscht alle Dateien mit der angegebenen Erweiterung aus dem 
     * Standardverzeichnis.
     * 
     * @param ext     Die Erweiterung.
     * @param params  Die Parameter.
     */
    public static void deleteALL(final String ext,
                                 final ParCollection params) {
        File pfad = new File(params.getStdDirectory());
        String[] alleDat = pfad.list(new FileNamePostfixFilter(ext));
        
        if (alleDat == null) {
            return;
        }

        for (int i = 0; i < alleDat.length; i++) {
            deleteDAT(params.getStdDirectory() + File.separator + alleDat[i]);
        }
    }
    
    /**
     * Gibt zurück, ob ein Element in einem Array enthalten ist.
     *
     * @param array    Das Array, in dem das Element gesucht werden soll.
     * @param element  Das Element, das gesucht werden soll.
     *
     * @return  Ob das Element in dem Array enthalten ist.
     */
    public static boolean enthaelt(final int[] array,
                                   final int   element) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == element) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param feld     Das Feld.
     * @param x        X-Koordinate.
     * @param y        Y-Koordinate.
     * @param rasterX  Zugriffsraster.
     * @param rasterY  Zugriffsraster.
     * @param farben   Array, in dem die Farben zurückgegeben werden.
     * 
     * @return  Die Verteilung der Farben im Feld.
     */
    private static long[] pixGes(final byte[][] feld,
                                 final int      x,
                                 final int      y,
                                 final int      rasterX,
                                 final int      rasterY,
                                 final long[]   farben) {
        int startX = x;
        int startY = y;
        int endeX = x + rasterX;
        int endeY = y + rasterY;
        
        for (int i = 0; i < farben.length; i++) {
            farben[i] = 0;
        }
        
        if (startX < 0) { 
            startX = 0;
        }
        if (startY < 0) {
            startY = 0;
        }
        if (endeX > feld.length) {
            endeX = feld.length;
        }
        if (endeY > feld[0].length) {
            endeY = feld[0].length;
        }

        for (int i = startX; i < endeX; i += 1) {
            for (int j = startY; j < endeY; j += 1) {
                farben[feld[i][j] + 128]++;
            }
        }
        
        return farben;
    }
    
    /**
     * @param feld        Das Feld.
     * @param rasterX     Das Raster, je größer desto enger.
     * @param rasterY     Das Raster, je größer desto enger.
     * @param zeichen     Die den Farben zugeordneten Zeichen (das Array muss 
     *                    256 Felder haben.
     * @param prioritaet  Nach welcher Priorität die Farben genommen werden.
     * 
     * @return  Die Umgebung als Stringausgabe.
     */
    public static String feldAusgabe(final byte[][] feld,
                                     final int      rasterX,
                                     final int      rasterY,
                                     final char[]   zeichen,
                                     final int[]    prioritaet) {
        String s = "";
        boolean gesetzt;
        long[] farben = new long[256];
        
        if (zeichen.length != 256) {
            throw new RuntimeException("Ein Array hat eine falsche Länge: (" 
                                           + zeichen.length);
        }
        
        for (int j = 0; j < feld[0].length; j += rasterY) {
            for (int i = 0; i < feld.length; i += rasterX) {
                pixGes(feld, i, j, rasterX, rasterY, farben);
                    
                gesetzt = false;
                for (int k = 0; k < prioritaet.length; k++) {
                    if (farben[prioritaet[k]] > 0) {
                        s = s + zeichen[prioritaet[k]];
                        gesetzt = true;
                        break;
                    }
                }
                
                if (!gesetzt) {
                    s = s + " ";
                }
            }
            s = s + "\n";
        }
        
        return s;
    }
    
    /**
     * Gibt entweder den übergebenen String-Vektor zurück oder erzeugt einen
     * Vektor gleicher Länge mit leeren Strings abhängig von der Variable
     * <code>aut</code>.
     * 
     * @param origSeqs  Die Originalstrings.
     * @param aut       Ob die Originalstrings zurückgegeben werden sollen.
     * 
     * @return  Die Originalstrings oder leere Strings.
     */
    public static String[] erzeugeSeqs(final String[] origSeqs, 
                                       final boolean aut) {
       if (aut) {
           return origSeqs;
       } else {
           return new String[origSeqs.length];
       }
    }

    /**
     * Gibt entweder den übergebenen Condition-Vektor zurück oder erzeugt 
     * einen Vektor gleicher Länge mit leeren Conditions abhängig von der 
     * Variable <code>aut</code>.
     * 
     * @param origSeqs  Die Originalstrings.
     * @param aut       Ob die Originalstrings zurückgegeben werden sollen.
     * 
     * @return  Die Originalstrings oder leere Strings.
     */
    

    /**
     * Speichert die Datei <code>in</code> in die Datei <code>out</code>,
     * wobei eine GZ-Kompression verwendet wird.
     * 
     * @param in        Der Dateiname der Inputdatei.
     * @param out       Der Dateiname der Outputdatei.
     * @param inLoeschen  Ob die Originaldatei gelöscht werden soll.
     */
    public static void packeDatei(
            final String in, 
            final String out,
            final boolean inLoeschen) {
        int read = 0;
        byte[] data = new byte[1024];
        
        try {
            // Original-Datei mit Stream verbinden
            File f = new File(in);
            FileInputStream inP = new FileInputStream(f);

            // Ausgabedatei erstellen
            GZIPOutputStream outP =
              new GZIPOutputStream(
                new FileOutputStream(out));

            // Alle Daten der Original-Datei in die Ausgabedatei schreiben
            while ((read = inP.read(data, 0, 1024)) != -1) {
              outP.write(data, 0, read);
            }
            inP.close();
            outP.close();
            if (inLoeschen) {
                f.delete();   // Original-Datei löschen
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Speichert die Datei <code>in</code> in die Datei <code>out</code>,
     * wobei eine GZ-Entkompression verwendet wird.
     * 
     * @param in        Der Dateiname der Inputdatei.
     * @param out       Der Dateiname der Outputdatei.
     * @param inLoeschen  Ob die Originaldatei gelöscht werden soll.
     * 
     * @return  Ob die Datei entpackt wurde.
     */
    public static boolean entpackeDatei(final String in, 
                                        final String out,
                                        final boolean inLoeschen) {
        String source = in, destination = out; 
       
        InputStream  is = null; 
        OutputStream os = null; 
      
        try { 
            is = new GZIPInputStream(new FileInputStream(source)); 
            os = new FileOutputStream(destination); 
       
            byte[] buffer = new byte[8192]; 
       
            for (int length = is.read(buffer); 
                 length != -1; 
                 length = is.read(buffer)) { 
                os.write(buffer, 0, length); 
            }
            
            if (inLoeschen) {
                (new File(source)).delete();
            }
            
        } catch (final IOException e) { 
            StaticMethods.log(StaticMethods.LOG_WARNING,
                          "Datei nicht entpackt: " + in,
                          null);
            return false;
        } finally { 
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) { 
                    StaticMethods.log(StaticMethods.LOG_WARNING,
                            "Datei nicht entpackt: " + in,
                            null);
                    return false;
                } 
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) { 
                    StaticMethods.log(StaticMethods.LOG_WARNING,
                            "Datei nicht entpackt: " + in,
                            null);
                    return false;
                } 
            }
        }
        
        return true;
    } 

    /**
     * Erzeugt einen einzigen Automaten aus den übergebenen Einzelautomaten
     * mit Aktivierungsbedingungen.
     * 
     * @param endAuts  Die Automaten.
     * @param conds    Die zugehörigen Bedingungen.
     * 
     * @return  Gesamtautomat, dessen Verhalten zu dem der übergebenen Einzel-
     *          automaten äquivalent ist.
     */
    
    /**
     * Die berechneten Wahrscheinlichkeiten. 
     */
    private static HashMap<String, Double> berConds;

    /**
     * Die Anzahl der Iterationen. 
     */
    private static HashMap<String, Long> berCondsIt;

    /**
     * Gibt eine probabilistische Abschätzung des prozentualen Anteils an
     * wahren Belegungen der Bedingung zurück.
     * 
     * @param cond       Die Bedingung.
     * @param erstmalig  Wie viele Iterationen beim ersten Mal.
     * @param spaeter    Wie viele Iterationen ab dem zweiten Mal.
     * 
     * @return  Prozentualer Anteil an wahren Belegungen.
     */
    
    
    /**
     * @param modus  Der Modus (übersetzer oder Verhalten).
     * 
     * @return  Die minimale Befehlsnummer je nach Modus.
     */
    public static int minBef(final int modus) {
        if (modus == StaticMethods.MODUS_VERHALTEN) {
            return eas.simulation.ConstantsSimulation.MIN_INST_BEH;
        } else if (modus == StaticMethods.MODUS_TRANSLATOR) {
            return eas.simulation.ConstantsSimulation.minInstTranslator();
        } else {
            return -1;
        }
    }

    /**
     * @param modus  Der Modus (Übersetzer oder Verhalten).
     * 
     * @return  Die maximale Befehlsnummer je nach Modus.
     */
    

    public static LinkedList<Integer> prohibitedInstructions(final int modus) {
        LinkedList<Integer> prohibited = new LinkedList<Integer>();
        
        if (modus == StaticMethods.MODUS_TRANSLATOR 
                && !EAPlugin.UseTranslatorWITHCompletingTransitions) {
            prohibited.add(106);
        }
        
        return prohibited;
    }
    
    /**
     * @param modus  Der Modus (Übersetzer oder Verhalten).
     * 
     * @return  Die minimale Variablennummer je nach Modus.
     */
    

    /**
     * @param modus   Der Modus (übersetzer oder Verhalten).
     * 
     * @return  Die maximale Variablennummer je nach Modus.
     */
    
    
    /**
     * Gibt eine gleichverteilte Zufallszahl aus 
     * {min, min + 1, ..., max} 
     * zurück.
     * 
     * @param min   Das Minimum.
     * @param max   Das Maximum.
     * @param rand  Der Zufallsgenerator.
     * @return  Gleichverteilte Zufallszahl zwischen min und max.
     */
    public static int glVertZwischen(
            final int min, 
            final int max,
            final Random rand) {
        if (min > max) {
            throw new RuntimeException(
                    "Fehler in Methode glVertZwischen: min > max");
        }

        return Math.abs(rand.nextInt()) % (max - min + 1) + min;
    }
    
    /**
     * Speichert den als Sequenz übergebenen Automaten in einem
     * Standardverzeichnis und unter einem Standardnamen. Achtung: die Methode
     * erzeugt ein Vis-Objekt mit zugehörigem Roboter und ist daher 
     * ineffizient ==> sparsam gebrauchen.
     * 
     * @param seqOrig    Die Sequenz.
     * @param augabeDat  Der Name der Ausgabedatei.
     * @param params     Die Parameter.
     */
    
    
    /**
     * Gibt den Dateinamen ohne Erweiterung zurück (Internet).
     * 
     * @param fileName
     *            Der Dateiname.
     * 
     * @return Der Dateiname ohne Erweiterung.
     */
    public static String datNamOhneErw(final String fileName) {
        File tmpFile = new File(fileName);
        tmpFile.getName();
        int whereDot = tmpFile.getName().lastIndexOf('.');
        if (0 < whereDot && whereDot <= tmpFile.getName().length() - 2) {
            return StaticMethods.datNamOhneErw(tmpFile.getName()
                    .substring(0, whereDot));
            // extension = filename.substring(whereDot+1);
        }
        return fileName;
    }

    /**
     * Gibt den Dateinamen ohne HINTERSTE Erweiterung zurück (Internet).
     * 
     * @param fileName  Der Dateiname.
     * 
     * @return Der Dateiname ohne HINTERSTE Erweiterung.
     */
    public static String datNamOhneHintErw(final String fileName) {
        File tmpFile = new File(fileName);
        tmpFile.getName();
        int whereDot = tmpFile.getName().lastIndexOf('.');
        if (0 < whereDot && whereDot <= tmpFile.getName().length() - 2) {
            return tmpFile.getName().substring(0, whereDot);
            // extension = filename.substring(whereDot+1);
        }
        return fileName;
    }

    /**
     * Gibt einen String des Stack-Traces einer Exception zurück.
     * 
     * @param e  Die Exception.
     * 
     * @return  Der Stack-Trace.
     */
    public static String getStackTrace(final Exception e) {
        String s = "";
        
        for (StackTraceElement einzEl : e.getStackTrace()) {
            s += "\n" + einzEl.toString();
        }
        
        return s;
    }
    
    /**
     * Kompressor.
     */
    private static Deflater compressor;
    
    /**
     * Dekompressor.
     */
    private static Inflater decompressor;
    
    /**
     * Komprimiert einen Byte-Array.
     * 
     * @param input   Byte-Array.
     * @param params  Der Parametersatz.
     * 
     * @return  Komprimierter Array.
     * @throws Exception  Fehler.
     */
    public static byte[] compressByteStream(
            final byte[] input) throws Exception {
        // Compressor with highest level of compression
        if (compressor == null) {
            compressor = new Deflater();
            compressor.setLevel(Deflater.BEST_COMPRESSION);
        }
        
        // Give the compressor the data to compress
        compressor.setInput(input);
        compressor.finish();
        
        // Create an expandable byte array to hold the compressed data.
        // It is not necessary that the compressed data will be smaller than
        // the uncompressed data.
        ByteArrayOutputStream bos = new ByteArrayOutputStream(input.length);
        
        // Compress the data
        byte[] buf = new byte[input.length + 100];
        while (!compressor.finished()) {
            int count = compressor.deflate(buf);
            bos.write(buf, 0, count);
        }
        try {
            bos.close();
        } catch (Exception e) {
            StaticMethods.log(
                    StaticMethods.LOG_ERROR,
                    "Kompression fehlgeschlagen: " + e,
                    GlobalVariables.getParameters());
            throw e;
        }
        
        // Get the compressed data
        byte[] compressedData = bos.toByteArray();
        
        compressor = null;
        return compressedData;
    }

    /**
     * Dekomprimiert einen Byte-Array.
     * 
     * @param input  Byte-Array.
     * @param params  Der Parametersatz.
     * 
     * @return  Dekomprimierter Array.
     * @throws Exception  Fehlerbehandlung.
     */
    public static byte[] decompressByteStream(
            final byte[] input) throws Exception {
     // Initialize decompressor.
        if (decompressor == null) {
            decompressor = new Inflater();
        }
        decompressor.setInput(input);  // Give the decompressor the data to decompress.
        decompressor.finished();
 
        // Create an expandable byte array to hold the decompressed data.
        // It is not necessary that the decompressed data will be larger than
        // the compressed data.
        ByteArrayOutputStream bos = new ByteArrayOutputStream(input.length);
 
        // Decompress the data
        byte[] buf = new byte[input.length + 100];
        
        try {
        while (!decompressor.finished())
        {
            bos.write(buf, 0, decompressor.inflate(buf));
        }
        bos.close();
        } catch (Exception e) {
            StaticMethods.log(
                    StaticMethods.LOG_ERROR,
                    "Dekompression fehlgeschlagen: " + e,
                    GlobalVariables.getParameters());
            throw e;
        }
 
        decompressor = null;
        // Get the decompressed data.
        return bos.toByteArray();
    }

    private static String getSafeASCIILikeChar(byte b) {
        return SAFE_CHARACTERS_ASCII_LIKE[b - Byte.MIN_VALUE];
    }

    private static byte getFromSafeASCIILikeChar(String character) {
        return (byte) (SAFE_CHARACTERS_ASCII_LIKE_REVERSE.get(character) + Byte.MIN_VALUE);
    }
    
    /**
     * I will first ZIP the string and then encode the result using two safe
     * characters, these are from the SAFE_CHAR_SET, for each zipped character.
     * 
     * @param text  The text to encode.
     * @return  The safe compressed string (which may well be larger than the original
     *          due to making it safe).
     *          
     * @throws Exception  If something goes wrong during zipping.
     */
    public static String compressSafeString(String text) throws Exception {
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        byte[] compressed = compressByteStream(bytes);

        if (bytes.length < compressed.length) {
            GlobalVariables.getParameters().logDebug(
                    "Note that I compressed a string of size " 
                            + bytes.length + " into a string of size "
                            + compressed.length + ". This means that the compression "
                            + "was ineffective, but I will still use the less efficient 'compressed' string. "
                            + "Note further that the string will get even longer when encoded in safe "
                            + "characters, but that's not what I meant by ineffective.");
        }
        
        StringBuffer buff = new StringBuffer(compressed.length * 4);
        
        for (byte b : compressed) {
            buff.append(getSafeASCIILikeChar(b));
        }
        
        return buff.toString();
    }

    /**
     * I will reverse the according compression process.
     * 
     * @param text  The compressed safe text to decompress.
     * @return  The decompressed plain text.
     * 
     * @throws Exception  If something goes wrong during decompression.
     */
    public static String decompressFromSafeString(String text) throws Exception {
        String[] split = text.split("(?<=\\G.{2})");
        byte[] bytes = new byte[split.length];
        
        for (int i = 0; i < split.length; i++) {
            String s = split[i];
            bytes[i] = getFromSafeASCIILikeChar(s);
        }
        
        return new String(decompressByteStream(bytes), "UTF-8");
    }
    
    /**
     * @param dateiname  Der Dateiname.
     * 
     * @return  Die Endung des Dateinamens (nach dem letzten Punkt).
     *          Falls es keinen Punkt gibt: "".
     */
    public static String datEndung(final String dateiname) {
        if (dateiname.split("\\.").length > 0) {
            return dateiname.split("\\.")[dateiname.split("\\.").length - 1];
        } else {
            return "";
        }
    }

    /**
     * Erzeugt eine einfache Ausgabe aus einer Liste:
     * [e1, e2, e3] ==> e1,e2,e3
     * 
     * @param liste  Die Liste.
     * 
     * @return  Die einfache Ausgabe der Liste.
     */
    public static String ListToString(final List<?> liste) {
        String ausgabe = "";
        
        for (int i = 0; i < liste.size() - 1; i++) {
            ausgabe += liste.get(i) + ",";
        }
        
        if (liste.size() > 0) {
            ausgabe += liste.get(liste.size() - 1);
        }
        
        return ausgabe;
    }
    

    /** 
     * Returns an ImageIcon, or null if the path was invalid.
     * 
     * @param path         Pfad der Image-Datei.
     * @param description  Beschreibung der Datei.
     */
    public static ImageIcon createImageIcon(
            final String path,
            final String description) {
        return new ImageIcon(path, description);
    }

    /**
     * Erzeugt einen Roboter aus einem RobCode.
     * 
     * @param robcode  Der Robcode.
     * @param umg      Die Umgebung, die zum Roboter gehört. Achtung: der
     *                 Roboter wird nicht automatisch platziert.
     * @param params   Der Parametersatz.
     * @param rand     Der Zufallsgenerator.
     * 
     * @return  Der Roboter.
     */
    

    /**
     * Löscht ein ganzes Verzeichnis rekursiv mit allem Inhalt 
     * (oder eine Datei).
     * 
     * @param dir  Das zu löschende Verzeichnis-
     * 
     * @return  Ob das Verzeichnis gelöscht werden konnte.
     */
    public static boolean delDir(File dir){
        if (dir.isDirectory()){
                String[] entries = dir.list();
                for (int x=0;x<entries.length;x++){
                    File aktFile = new File(dir.getPath(),entries[x]);
                    delDir(aktFile);
                }
                return dir.delete();
            }
            else{
                return dir.delete();
            }
    }

    public static StaticWindow showImage(
            final BufferedImage img, 
            final String name) {
        return showImage(img, name, true);
    }
    
    public static StaticWindow showImage(
                final BufferedImage img, 
                final String name,
                final boolean display) {
        StaticWindow w = new StaticWindow(
                "Image [" + name + "]",
                img.getWidth(null) + 50, 
                img.getHeight(null) + 50,
                display);
        
        JScrollPane jsp = new JScrollPane(new JLabel(new ImageIcon(img)));
        w.getContentPane().add(jsp);
        
        if (display) {
            w.setVisible(true);
        }
        return w;
    }
    
    /**
     * Normalizes a vector such that the minimal entry is exactly min and the
     * maximal entry is exactly max, while the other entries are scaled in a
     * linear way in between.
     * 
     * @param vector  The vector to normalize.
     * @param desiredMin     The future minimal value.
     * @param desiredMax     The future maximal value.
     * 
     * @return  The normalized vector.
     */
    public static Double[] normalize(Double[] vector, double desiredMin, double desiredMax) {
        double foundMin = Double.POSITIVE_INFINITY;
        double foundMax = Double.NEGATIVE_INFINITY;
        
        for (double d : vector) {
            if (d < foundMin) {
                foundMin = d;
            }
            if (d > foundMax) {
                foundMax = d;
            }
        }
        
        return normalize(vector, foundMin, foundMax, desiredMin, desiredMax);
    }

    /**
     * Normalizes a vector such that the minimal entry is exactly min and the
     * maximal entry is exactly max, while the other entries are scaled in a
     * linear way in between.
     * 
     * @param vector  The vector to normalize.
     * @param desiredMin     The future minimal value.
     * @param desiredMax     The future maximal value.
     * @param vecMin         The minimal value in the vector.
     * @param vecMax         The maximal value in the vector.
     * 
     * @return  The normalized vector.
     */
    public static Double[] normalize(
            Double[] vector, 
            double vecMin, 
            double vecMax, 
            double desiredMin, 
            double desiredMax) {
        Double[] normalized = new Double[vector.length];
        double factor = (desiredMax - desiredMin) / (vecMax - vecMin);
        double newMin = (vecMin + vecMin) * factor - desiredMin;

        if (factor == Double.POSITIVE_INFINITY || factor == Double.NEGATIVE_INFINITY || factor == Double.NaN) {
            for (int i = 0; i < vector.length; i++) {
                normalized[i] = (desiredMax + desiredMin) / 2;
            }
        } else {
            for (int i = 0; i < vector.length; i++) {
                normalized[i] = (vector[i] + vecMin) * factor - newMin;
            }
        }
        
        return normalized;
    }
    
    /* 
     * Im folgenden wird durch einen Code aus dem Internet das "Unmögliche"
     * möglich gemacht: Zur Laufzeit bestimmen, über welche Generics eine
     * Klasse instantiiert worden ist. Für ein Beispiel siehe Methode
     * getActualAgentType der Klasse AbstractEnvironment.
     * Code-Herkunft: http://haibo.iteye.com/blog/322239
     */
    
    public static Class<?> getClass(Type type) {
        if (type instanceof Class) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            return getClass(((ParameterizedType) type).getRawType());
        } else if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) type)
                    .getGenericComponentType();
            Class<?> componentClass = getClass(componentType);
            if (componentClass != null) {
                return Array.newInstance(componentClass, 0).getClass();
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

    /**
     * Get the actual type arguments a child class has used to extend a generic
     * base class.
     * 
     * @param baseClass
     *            the base class
     * @param childClass
     *            the child class
     * @return a list of the raw classes for the actual type arguments.
     */
    public static <T> List<Class<?>> getTypeArguments(
            Class<T> baseClass,
            Class<? extends T> childClass) {
        Map<Type, Type> resolvedTypes = new HashMap<Type, Type>();
        Type type = childClass;
        // start walking up the inheritance hierarchy until we hit baseClass
        while (!baseClass.equals(getClass(type))) {
            if (type instanceof Class) {
                // there is no useful information for us in raw types, so just
                // keep going.
                type = ((Class<?>) type).getGenericSuperclass();
            } else {
                ParameterizedType parameterizedType = (ParameterizedType) type;
                Class<?> rawType = (Class<?>) parameterizedType.getRawType();

                Type[] actualTypeArguments = parameterizedType
                        .getActualTypeArguments();
                TypeVariable<?>[] typeParameters = rawType.getTypeParameters();
                for (int i = 0; i < actualTypeArguments.length; i++) {
                    resolvedTypes.put(typeParameters[i], actualTypeArguments[i]);
                }

                if (!rawType.equals(baseClass)) {
                    type = rawType.getGenericSuperclass();
                }
            }
        }

        // finally, for each actual type argument provided to baseClass,
        // determine (if possible)
        // the raw class for that type argument.
        Type[] actualTypeArguments;
        if (type instanceof Class) {
            actualTypeArguments = ((Class<?>) type).getTypeParameters();
        } else {
            actualTypeArguments = ((ParameterizedType) type)
                    .getActualTypeArguments();
        }
        List<Class<?>> typeArgumentsAsClasses = new ArrayList<Class<?>>();
        // resolve types by chasing down type variables.
        for (Type baseType : actualTypeArguments) {
            while (resolvedTypes.containsKey(baseType)) {
                baseType = resolvedTypes.get(baseType);
            }
            typeArgumentsAsClasses.add(getClass(baseType));
        }
        return typeArgumentsAsClasses;
    }

    /**
     * Konvertiert eine Liste von Plugins in eine Liste von StandardEA-Plugins.
     * Dabei werden 
     * - Plugins, die schon EAPlugins sind, einfach gecastet;
     * - Plugins, die auf EnvironmentEA arbeiten, umgewandelt, indem ein neues
     *   PluginEA erzeugt wird, das die Methoden des alten Plugins aufruft;
     * - alle übrigen Plugins ignoriert und NICHT zur Liste hinzugefügt.
     * 
     * @param plugins  Die Liste der zu konvertierenden Plugins. 
     * @param params   Die Parameter.
     * 
     * @return  Die konvertierte Liste von Plugins.
     */
    

    /**
     * Processes a given string in the (hopefully) same way java does with 
     * command line parameters, i.e., split at every space except for those
     * occuring between two quotation marks.
     * 
     * @param parametersRaw  The raw parameter string.
     * 
     * @return  A list of parameters as processed by java when reading command
     *          line parameters.
     */
    public static String[] processStringAsCommandLineParameters(final String parsRaw) {
        String parametersRaw = parsRaw;
        
        while (parametersRaw.contains("\"\"")) {
            parametersRaw = parametersRaw.replaceAll("\"\"", "\"");
        }

        String[] parameters;
        boolean inQuotMarks = false;
        String specialString = " tY_NE_ESEL ";
        String firstStepProcessedParameters = "";
        int finishedUntil = 0;
        
        for (int i = 0; i < parametersRaw.length(); i++) {
            if (parametersRaw.charAt(i) == '"') {
                inQuotMarks = !inQuotMarks;
                firstStepProcessedParameters += parametersRaw.substring(finishedUntil, i);
                finishedUntil = i + 1;
            } else if (!inQuotMarks && parametersRaw.charAt(i) == ' ') {
                firstStepProcessedParameters += parametersRaw.substring(finishedUntil, i) + specialString;
                finishedUntil = i + 1;
            }
        }
        if (finishedUntil < parametersRaw.length() - 1) {
            firstStepProcessedParameters += parametersRaw.substring(finishedUntil, parametersRaw.length());
        }
        
        parameters = firstStepProcessedParameters.split(specialString);
        
        return parameters;
    }
    
    /**
     * Generates a line chart from data in a cvs file.
     * A list of columns to consider can be given (null considers all columns).
     * Note that all rows have to have the same number of columns. The entries
     * in all non-headings have to be double values, otherwise they are
     * ignored.
     * 
     * @param csvFile          The cvs file.
     * @param columns          The columns (first column denoted by 0).
     * @param separator        The csv separator.
     * @param firstRowHeading  If the first row contains headings.
     * 
     * @return  The chart (null if no data is available).
     */
    public static JFreeChart generateChartFromCVS(
            File csvFile, 
            int[] columns, 
            String separator,
            boolean firstRowHeading) {
        int[] col;
        
        // Preprocessing.
        LinkedList<String[]> csvContent = StaticMethods.readCSVFile(
                csvFile, 
                separator);
        
        if (csvContent == null || csvContent.size() == 0 || (firstRowHeading && csvContent.size() == 1) || csvContent.get(0).length == 0) {
            return null;
        }
        
        col = columns;
        
        if (col == null) {
            col = new int[csvContent.get(0).length];
            for (int i = 0; i < csvContent.get(0).length; i++) {
                col[i] = i;
            }
        }
        
        // Chart generation.
        XYSeries[] series = new XYSeries[col.length];
        XYSeriesCollection dataset = new XYSeriesCollection();
        
        // Create dataset.
        for (int i = 0; i < col.length; i++) {
            if (firstRowHeading) {
                series[i] = new XYSeries(csvContent.get(0)[col[i]]);
            } else {
                series[i] = new XYSeries("Column " + col[i]);
            }
            
            for (int j = 0; j < csvContent.size(); j++) {
                if (!firstRowHeading || j > 0) {
                    try {
                        series[i].add(j, Double.parseDouble(csvContent.get(j)[col[i]]));
                    } catch (Exception e) {
                    }
                }
            }
            
            dataset.addSeries(series[i]);
        }
        
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Content of " + csvFile.getName(), // chart title
                "", // x axis label
                "", // y axis label
                dataset, // data
                PlotOrientation.VERTICAL,
                true, // include legend
                true, // tooltips
                false // urls
                );
        
        return chart;
    }
    
    public static JFreeChart generateLineChartFromYAxisData(
            String chartTitle, 
            String xAxisLabel,
            String yAxisLabel,
            String[] seriesNames, 
            Double[][] seriesInt) {
        XYSeries[] series = new XYSeries[seriesInt.length];
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);
        renderer.setSeriesPaint(1, Color.black);
        renderer.setSeriesStroke(1, new BasicStroke(0.75f));
        renderer.setSeriesPaint(0, Color.red);
        renderer.setSeriesStroke(0, new BasicStroke(1.5f));

        for (int i = 0; i < seriesInt.length; i++) {
            series[i] = new XYSeries(seriesNames[i]);
            
            for (int j = 0; j < seriesInt[i].length; j++) {
                try {
                    series[i].add(j, seriesInt[i][j]);
                } catch (Exception e) {
                }
            }
            
            dataset.addSeries(series[i]);
        }

        NumberAxis domain = new NumberAxis(xAxisLabel);
        NumberAxis range = new NumberAxis(yAxisLabel);
        XYPlot plot = new XYPlot(dataset, domain, range, renderer);
        
        JFreeChart chart = new JFreeChart(chartTitle, plot);
        
        return chart;
    }
    
    public static void saveImage(BufferedImage img, File file, String format) {
        try {
            ImageIO.write(img, format, file);
        } catch (IOException e) {
        }
    }

    
    
    
    
    /**
     * Saves a chart as PDF. This code is taken from the JFreeChart documentation.
     */
    public static void saveChartAsPDF(File file, JFreeChart chart, int width,
            int height, FontMapper mapper) throws IOException {
        OutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        writeChartAsPDF(out, chart, width, height, mapper);
        out.close();
    }

    /**
     * This code is taken from the JFreeChart documentation.
     */
    public static void writeChartAsPDF(OutputStream out, JFreeChart chart,
            int width, int height, FontMapper mapper) {
        Rectangle pagesize = new Rectangle(width, height);
        Document document = new Document(pagesize, 50, 50, 50, 50);
        try {
            PdfWriter writer = PdfWriter.getInstance(document, out);
            document.addAuthor("EAS-ChartPlugin");
            document.addSubject(chart.getTitle().getText());
            document.open();
            PdfContentByte cb = writer.getDirectContent();
            PdfTemplate tp = cb.createTemplate(width, height);
            Graphics2D g2 = tp.createGraphics(width, height, mapper);
            java.awt.geom.Rectangle2D r2D = new java.awt.geom.Rectangle2D.Double(0, 0, width, height);
            chart.draw(g2, r2D);
            g2.dispose();
            cb.addTemplate(tp, 0, 0);
        } catch (DocumentException de) {
            System.err.println(de.getMessage());
        }
        document.close();
    }

    public static void appendLineToFile(String line, RandomAccessFile file) {
        try {
            file.seek(file.length());
            file.writeChars(line + "\n");
//            file.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static String roundedOutput(double wert, int stellen) {
        StringBuilder sb = new StringBuilder(",##0.");
        for (int i = 0; i < stellen; i++)
            sb.append("0");
        DecimalFormat df = new DecimalFormat(sb.toString());
        df.setRoundingMode(RoundingMode.HALF_UP);
        return df.format(wert);
    }
    
    @SuppressWarnings("deprecation")
    public static Double round(double wert, int stellen) {
        try {
            BigDecimal b = new BigDecimal(wert);
            return  b.setScale(stellen, BigDecimal.ROUND_HALF_UP).doubleValue();        
        } catch (Exception e) {
            return wert;
        }
    }
    
    @SuppressWarnings("deprecation")
    public static String roundStr(double wert, int stellen) {
        try {
            BigDecimal b = new BigDecimal(wert);
            return  b.setScale(stellen, BigDecimal.ROUND_HALF_UP).toString();        
        } catch (Exception e) {
            return wert + "";
        }
    }

//    public static void main(String[] args) {
//        File dir = new File("Z:/simGrZahraeder");
//        File[] files = dir.listFiles(new FilenameFilter() {
//            @Override
//            public boolean accept(File dir, String name) {
//                return name.endsWith(".csv");
//            }
//        });
//        
//        for (File f : files) {
//            int[] columns1 = new int[] {100};
//            int[] columns2 = new int[] {102};
//            System.out.println(f);
//            JFreeChart chart = StaticMethods.generateChartFromCVS(
//                    f, 
//                    columns1, 
//                    ";", 
//                    true);
//            StaticMethods.saveImage(chart.createBufferedImage(500, 500), new File(f.getPath() + ".fit.png"), "png");
//            chart = StaticMethods.generateChartFromCVS(
//                    f, 
//                    columns2, 
//                    ";", 
//                    true);
//            StaticMethods.saveImage(chart.createBufferedImage(500, 500), new File(f.getPath() + ".korr.png"), "png");
//        }
//        
//        System.out.println("Fertig (2 x " + files.length + " Dateien).");
//    }

    public static String whoCalledMeInitially() {
        try {
            throw new Exception("Who called me?");
        } catch (Exception e) {
            StackTraceElement[] stackTrace = e.getStackTrace();
            return stackTrace[stackTrace.length - 1].getClassName();
        }
    }
    
    public static Process execCommandUsingProcessBuilder(String command, boolean waitForIt) throws InterruptedException, IOException {
        String[] strArr1 = new String[] {command};
        ProcessBuilder probuilder = new ProcessBuilder( strArr1 );
        Process process = probuilder.start();

        if (waitForIt) {
            process.waitFor();
        }
        
        return process;
    }

//    /**
//     * Executes a command and does NOT wait for the process to terminate.
//     * Also, the file must be an executable. If you instead want to execute
//     * a batch file, use the 3-par method.
//     * (Convenience method.)
//     * 
//     * @param command  The command to execute.
//     * 
//     * @return  The process correpsonding to the executed command.
//     */
//    public static Process execCommand(String command) {
//        return execCommand(command, false, false);
//    }
//    
//    /**
//     * Executes a command and waits for the process to terminate if desired.
//     * (Convenience method.)
//     * 
//     * @param command             The command to execute.
//     * @param waitForIt           If the execution should pause until the process has terminated.
//     * @param executeAsBatchFile  Batch files alone are not executable. They need an application to run them, therefore
//     *                            a different execution command is required.
//     * 
//     * @return  The process correpsonding to the executed command.
//     */
//    public static Process execCommand(
//            String command, 
//            boolean waitForIt, 
//            boolean executeAsBatchFile) {
//        try {
//            if (MainLink.isDebugMode()) {
//                String workingDir = MainLink.getWORKING_DIRECTORY();
//                
//                if (workingDir == null) {
//                    workingDir = VFPWindow.TEMP_DIR.getAbsolutePath();
//                }
//                
//                StaticMethods.saveTextToFile(workingDir, "debug_exec-command_" + command.hashCode() + ".txt", command, null);
//            }
//
//            Process p;
//            if (executeAsBatchFile) {
//                /* 
//                 * Batch files alone are not executable. 
//                 * They need an application to tun them, in this case, cmd. 
//                 * Insert " start " after the "/c" to open terminal window.
//                 */
//                p = Runtime.getRuntime().exec("cmd /c " + command); 
//            } else {
//                p = Runtime.getRuntime().exec(command);
//            }
//
//            StreamGobbler errorGobbler = new StreamGobbler(p.getErrorStream(), "ERROR");            
//            StreamGobbler outputGobbler = new StreamGobbler(p.getInputStream(), "OUTPUT");
//
//            outputGobbler.start();
//            errorGobbler.start();
//
//            if (waitForIt) {
//                p.waitFor();
//            }
//            
//            return p;
//        } catch (Exception e) {return null;}
//    }
    
    public static void openDocument(File document) throws IOException {
        Desktop dt = Desktop.getDesktop();
        dt.open(document);
    }

    public static String readTextFromFile(File datei, ParCollection params) {
        ArrayList<String> daten = new ArrayList<>(readTextArrayFromFile(datei, params));
        String s = "";
        
        s += daten.get(0);
        for (int i = 1; i < daten.size(); i++) {
            s += "\n" + daten.get(i);
        }
        
        return s;
    }

    public static void joinThreads(Collection<Thread> threads) {
        threads.stream().filter(t -> t != null).forEach(t -> 
        {
            try {
                t.join(); 
                GlobalVariables.getParameters().logDebugP(":");
            } catch (Exception e) {}});
    }

    private static HashMap<String, Rectangle2D> windowRectangles = new HashMap<>();
    private static HashSet<String> windowsLoaded = new HashSet<>();
    private static HashMap<String, ComponentListener> windowListeners = new HashMap<>();
    
    /**
     * Stores the position of a JFrame to the disc to be restored at next
     * program run. Use in combination with the main method
     * {@link #loadWindowFramePosition(JFrame, String)}.
     * 
     * After program start, an attempt to load the stored position (even if not
     * existing) has to be performed, otherwise this method will not do anything.
     * 
     * @param window  The window to store.
     */
    public static void storeWindowFramePosition(JFrame window, String id) {
        try {
            String title = id;
            if (!windowsLoaded.contains(title)) {
                return;
            }
            
            Rectangle2D rect = new Rectangle2D(
                    window.getX(), 
                    window.getY(), 
                    window.getX() + window.getWidth(), 
                    window.getY() + window.getHeight());
            windowRectangles.put(title, rect);
            
            String[] lines = new String[windowRectangles.size()];
            
            int i = 0;
            for (String s : windowRectangles.keySet()) {
                lines[i] = s + " === " + windowRectangles.get(s);
                i++;
            }
            
            storeArrayElementsAsText(
                    GlobalVariables.getParameters().getStdDirectory(), 
                    "simFrames.dat", 
                    lines);
        } catch (Exception e) {
        }
    }
    
    /**
     * Loads the position of a JFrame from disc. Use this method
     * in combination with {@link #storeWindowFramePosition(JFrame, String)}
     * to restore the position from the last program start. The
     * load method has to be called first after program start
     * to initialize the storing process. The method  
     * adds a component listener to the window to store position and size
     * changes automatically. At program exit, the store method has to be
     * called manually if desired.
     * 
     * @param window  The window whoes position is to be stored.
     * @param id      An arbitrary - but unique! - string id.
     */
    public static void loadWindowFramePosition(JFrame window, String id) {
        windowsLoaded.add(id);
        ComponentListener listener = new ComponentListener() {
            @Override public void componentShown(ComponentEvent arg0) {}
            @Override public void componentHidden(ComponentEvent arg0) {}
            
            @Override
            public void componentResized(ComponentEvent arg0) {
                storeWindowFramePosition((JFrame) arg0.getComponent(), id);
            }
            
            @Override
            public void componentMoved(ComponentEvent arg0) {
                storeWindowFramePosition((JFrame) arg0.getComponent(), id);
            }
        };
        
        windowListeners.put(
                id, 
                listener);
        
        window.addComponentListener(listener);
        
        try {
            LinkedList<String> lines = readTextArrayFromFile(new File(
                    GlobalVariables.getParameters().getStdDirectory() + "/simFrames.dat"), 
                    GlobalVariables.getParameters());
            
            for (String s : lines) {
                String[] ss = s.split(" === ");
                String title = ss[0];
                Rectangle2D rect = Rectangle2D.parseRectangle2D(ss[1]);
                
                if (title.equals(id)) {
                    for (int i = 0; i < 10; i++) {
                        window.setLocation(
                                (int) rect.upperLeftCorner().x,
                                (int) rect.upperLeftCorner().y);
                        window.setSize(
                                (int) rect.lowerRightCorner().x - (int) rect.upperLeftCorner().x,
                                (int) rect.lowerRightCorner().y - (int) rect.upperLeftCorner().y);
                    }
                }
                
                windowRectangles.put(title, rect);
            }
        } catch (Exception e) {
        }
    }
    
    public static void openWebpage(URI uri) {
        Desktop desktop = Desktop.isDesktopSupported() ? Desktop.getDesktop() : null;
        if (desktop != null && desktop.isSupported(Desktop.Action.BROWSE)) {
            try {
                desktop.browse(uri);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void openWebpage(URL url) {
        try {
            openWebpage(url.toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * A safe string contains only the characters '0' ... '9' and '/'.
     * @param s  The string to be restored from the safe string mode.
     * @return  The restored string.
     */
    public static String restoreFromSafeString(String s) {
        LinkedList<Byte> bbytes = new LinkedList<>();
        byte[] bytes;
        
        for (String b : s.split("/")) {
            bbytes.add(Byte.parseByte(b));
        }
        
        bytes = new byte[bbytes.size()];
        
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = bbytes.get(i);
        }
        
        String converted = new String(bytes, StandardCharsets.UTF_8);
        return converted;
    }

    /**
     * A safe string contains only the characters '0' ... '9' and '/'.
     * @param s  The string to be converted.
     * @return  The converted string.
     */
    public static String createSafeString(String s) {
        return Arrays.toString(s.getBytes(StandardCharsets.UTF_8)).replace("[", "").replace("]", "").replace(", ", "/");
    }

    public static String removeWhitespaces(String s) {
        return s.replaceAll("\\s+", "");
    }
    
    public static String formatCollection(Collection<?> coll2) {
        return formatCollection(coll2, false);
    }

    public static String formatCollection(Collection<?> coll2, boolean removeCommas) {
        LinkedList<Object> coll;
        if (removeCommas) {
            coll = new LinkedList<>();
            coll2.forEach(p -> coll.add(p.toString().replace(",", "")));
        } else {
            coll = new LinkedList<>(coll2);
        }
            
        return removeWhitespaces(coll.toString()).replace("[", "").replace("]", "");
    }

    /**
     * @param someClass  Some class, what else...
     * @return  The package location of the class in OS directory format.
     */
    public static String packageDirOfClass(Class<?> someClass) {
        return someClass.getPackage().getName().replace(".", "/");
    }
    
    public static double similarity(String s1, String s2) {
        if (s1 == s2) {
            return 1;
        }
        
        if (s1 == null || s2 == null) {
            return 0;
        }

        String longer = s1, shorter = s2;
        if (s1.length() < s2.length()) { // longer should always have greater
                                         // length
            longer = s2;
            shorter = s1;
        }
        int longerLength = longer.length();
        if (longerLength == 0) {
            return 1.0;
            /* both strings are zero length */ }
        /*
         * // If you have StringUtils, you can use it to calculate the edit
         * distance: return (longerLength -
         * StringUtils.getLevenshteinDistance(longer, shorter)) / (double)
         * longerLength;
         */
        return (longerLength - editDistance(longer, shorter)) / (double) longerLength;

    }

    // Example implementation of the Levenshtein Edit Distance
    // See http://rosettacode.org/wiki/Levenshtein_distance#Java
    @SuppressWarnings("all")
    private static int editDistance(String s1, String s2) {
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        int[] costs = new int[s2.length() + 1];
        for (int i = 0; i <= s1.length(); i++) {
            int lastValue = i;
            for (int j = 0; j <= s2.length(); j++) {
                if (i == 0)
                    costs[j] = j;
                else {
                    if (j > 0) {
                        int newValue = costs[j - 1];
                        if (s1.charAt(i - 1) != s2.charAt(j - 1))
                            newValue = Math.min(Math.min(newValue, lastValue), costs[j]) + 1;
                        costs[j - 1] = lastValue;
                        lastValue = newValue;
                    }
                }
            }
            if (i > 0)
                costs[s2.length()] = lastValue;
        }
        return costs[s2.length()];
    }

    /**
     * Handles files, jar entries, and deployed jar entries in a zip file (EAR).
     * From http://stackoverflow.com/questions/3336392/java-print-time-of-last-compilation
     * 
     * @return The date if it can be determined, or null if not.
     */
    private static DateTime getClassBuildTime(Class<?> currentClass) {
        DateTime d = null;
        URL resource = currentClass.getResource(currentClass.getSimpleName() + ".class");
        
        if (resource != null) {
            if (resource.getProtocol().equals("file")) {
                try {
                    d = new DateTime(new File(resource.toURI()).lastModified());
                } catch (URISyntaxException ignored) {}
            } else if (resource.getProtocol().equals("jar")) {
                String path = resource.getPath();
                d = new DateTime(new File(path.substring(5, path.indexOf("!")))
                        .lastModified());
            } else if (resource.getProtocol().equals("zip")) {
                String path = resource.getPath();
                File jarFileOnDisk = new File(
                        path.substring(0, path.indexOf("!")));
                try (JarFile jf = new JarFile(jarFileOnDisk)) {
                    ZipEntry ze = jf
                            .getEntry(path.substring(path.indexOf("!") + 2));
                    long zeTimeLong = ze.getTime();
                    DateTime zeTimeDate = new DateTime(zeTimeLong);
                    d = zeTimeDate;
                } catch (IOException | RuntimeException ignored) {}
            }
        }
        
        return d;
    }

    private static DateTime getLatestCompileDate(Collection<Class<?>> classes) {
        DateTime newestDate = new DateTime(0l);
        
        for (Class<?> c : classes) {
            DateTime date = getClassBuildTime(c);
            
            if (date != null && date.isAfter(newestDate)) {
                newestDate = date;
            }
        }
        
        return newestDate;
    }
    
    private static final String VAR_NAME_LAST_COMPILATION_DATE = "$XX-LAST-COMPILATION-DATE-XX$";
    
    public static DateTime storeLatestCompileDate(Collection<Class<?>> classes) {
        DateTime date = getLatestCompileDate(classes);
        GeneralDialog.storeValueOf(VAR_NAME_LAST_COMPILATION_DATE, date.getMillis());
        return date;
    }
    
//    public static DateTime loadLatestCompileDate() {
//        String storedTime = GeneralDialog.loadValue(VAR_NAME_LAST_COMPILATION_DATE);
//        
//        if (storedTime == null) {
//            DateTime date = storeLatestCompileDate(PluginFactory.getAllClasses());
//            storedTime = date.getMillis() + "";
//        }
//        
//        return new DateTime(Long.parseLong(storedTime));
//    }
    
//    public static void main(String[] args) {
//        System.out.println(ScriptConversionMethods.decryptScript("scrypt:401s453o1f3Q1l362k460l2Z2W1a3Y1n2m3k16370J3o13250x3e2c3r1A0S1P3k0s0l1p1B3M3Y2N392j3412031S142T3S1A1m103u3J2W2d3g0G123D0J3z411E0r1N3J0y0b1D40152h2m3b0M1a1i3p3a263N0U182k1j1Z08003G0Z0H2W2c3t451K3w2n3D1b0K0Z2X2v0f1S3Q1e1O1t3z2Z1g0m1R052U2z0R1L0H3T073k2G34214420150a292E0v2c1843042v3d250p1j3P0g2S1D2A1R353g1D2P0G3H0S2D20312j3y083w37280x0z2k3D1i2S3c0M2i3p1d1N1i3p2h1V0Z1e3A1p3I051O1z3h460h3l1z0I0q121s1W3m3P2e0U3w2J3n2Z2P1X2F2S0T0a142l2Z3h0w1J163u3v360d032f25271i0e0Y1L3v042Y3n3i1U0o3I270P3o1c2C30112S2g130h2N3I1F1d0V0U1S2y3t2C1N0P363x2n3C3Y330L3v2H1c41080J1w1I3G1T1K20422O2u0Z0W3k142M2w1a2T0Q2b0X3r2H2o3N1t0F1U2J3B2l1G0N1w0d2R0Z3E35214044360S1q3I2Q0w0y062E2j3v14023l2K2H473D0O2S323D1k0p1i2i3S251f093C3y3j2x0X0k1x1y401N0t2A291u2b2S3f1m193i0A0l1B3W1e0G1g0m0z1Y040s1V3G1R0d2M020A04323I2N1c3I2q0L0A0C1F1L2F0q1v0B1A1N3Z1Q3p2n3b3z121v313U2P1h0g2q3U3T02311T2g3B1l1s3Q1T2x18402d0t1c45452o1s3x2d2d0C0D0R1c1U3n2Z3j2Y3j2H1t1t16032p2M3C1P2j3Z231B1K0w3L0S06161v0Z0j3T0O0t3W1X0C2s0P3J0u0R3C0F"));
//    }

    /**
     * Creates an array of objects corresponding to valid parameters to the
     * given method. The parameters are either obtained by asking the user
     * or by converting the given String array.</BR>
     * </BR>
     * Note that these methods are partially copied from VFP => ScriptConversionMethods. Sorry!
     * 
     * @param m           The method whose parameters are desired.
     * @param parameters  Optionally an array of String values encoding
     *                    the parameters. If {@code null}, the user is
     *                    prompted.
     * @return            Parameter objects for the given method.
     */
    public static Object[] getParametersFor(Method m) {
        Object[] list = new Object[m.getParameterCount()];
        
        boolean stdValBool = false;
        int stdValNum = 5;
        int i = 0;

        try {
            for (Parameter par : m.getParameters()) {
                Object stdVal = par.getType().equals(Boolean.class) || par.getType().equals(Boolean.TYPE)
                        ? stdValBool
                        : stdValNum;
                stdVal = par.getType().equals(String.class) ? "string" : stdVal;
                String methodDescription = "Value for " + par.getType() + " parameter '" + par.getName() + "' (method " + m.getName() + ")";
                
                String t;
                
                t = GeneralDialog.getStringFromUser(
                        methodDescription, 
                        stdVal + "", 
                        "$$" + m.getName() + "--" + par.getName() + "$$");
                
                if (t == null) {
                    return null;
                }
                
                if (par.getType().equals(Boolean.class) || par.getType().equals(Boolean.TYPE)) {
                    t = isStringTrue(t) ? "true" : "false";
                    list[i] = Boolean.parseBoolean(t);
                } else 
                if (par.getType().equals(Double.class) || par.getType().equals(Double.TYPE)) {
                    list[i] = Double.parseDouble(t);
                } else 
                if (par.getType().equals(Float.class) || par.getType().equals(Float.TYPE)) {
                    list[i] = Float.parseFloat(t);
                } else 
                if (par.getType().equals(Integer.class) || par.getType().equals(Integer.TYPE)) {
                    list[i] = Integer.parseInt(t);
                } else 
                if (par.getType().equals(Long.class) || par.getType().equals(Long.TYPE)) {
                    list[i] = Long.parseLong(t);
                } else 
                if (par.getType().equals(String.class)) {
                    list[i] = t;
                }
                
                i++;
            }
        } catch (NumberFormatException e) {
            GlobalVariables.getParameters().logWarning(
                    "Method invokation failed for method '" 
                    + m.getName() + "' due to the following exception: " + e.getMessage());
        }
        
        return list;
    }
    
    /**
     * Note that these methods are partially copied from VFP => ScriptConversionMethods. Sorry!
     */
    public static Boolean isStringTrue(String bool) {
        if (bool == null) {
            return null;
        }
        
        if (StaticMethods.removeWhitespaces(bool).toLowerCase().equals("true")) {
            return true;
        } else if (StaticMethods.removeWhitespaces(bool).toLowerCase().equals("false")) {
            return false;
        } else {
            return null;
        }
    }

    // Ende gut, alles gut.
}
