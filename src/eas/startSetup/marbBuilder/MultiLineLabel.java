/*
 * Datei:                MultiLineLabel.java
 * Klasse entnommen aus: http://www.rgagnon.com/javadetails/java-0269.html
 * Datum:                26.06.2007.
 * Java-Version:         1.4
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

package eas.startSetup.marbBuilder;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.util.StringTokenizer;

/**
 * Klasse zum Realisieren eines Labels mit mehreren Zeilen.
 * 
 * @author ?
 */
public class MultiLineLabel extends Canvas {

    /**
     * Die Versions-ID, generiert am 26.06.2007.
     */
    private static final long serialVersionUID = 5694843112368412582L;

    /***/
    public static final int LEFT = 0;
    /***/
    public static final int CENTER = 1;
    /***/
    public static final int RIGHT = 2;
    /***/
    private String text;
    /***/
    private String[] lines;
    /***/
    private int numLines;
    /***/
    private int lineHeight;
    /***/
    private int lineAscent;
    /***/
    private int[] lineWidths;
    /***/
    private int maxWidth;
    /***/
    private int alignment;
    /***/
    private boolean border;
    /***/
    private int topBottomMargin;
    /***/
    private int leftRightMargin;
    /***/
    private int x = 0;
    /***/
    private int y = 0;
    /***/
    Dimension offDimension;
    /***/
    private transient Image offImage;
    /***/
    private transient Graphics offGraphics;
    /***/
    Color borderColor = Color.black;

    /**
     * Konstruktor.
     * 
     * @param s  The label.
     * @param i  alignment MultiLineLabel.CENTER, MultiLineLabel.RIGHT,
        //       MultiLineLabel.LEFT,
        //       default MultiLineLabel.LEFT.
     * @param b  Border present or not.
     */
    public MultiLineLabel(final String s, final int i, final boolean b) {
        setAlignment(i);
        setText(s);
        setBorder(b);
    }

    /**
     * Konstruktor.
     * 
     * @param string  Ein String.
     * @param i       Ein Integer
     */
    public MultiLineLabel(final String string, final int i) {
        this(string, i, false);
    }

    /**
     * Konstruktor.
     * 
     * @param string  Ein String.
     */
    public MultiLineLabel(final String string) {
        this(string, 0);
    }

    /**
     * Konstruktor.
     */
    public MultiLineLabel() {
        this("", 0);
    }

    /**
     * 
     */
    @Override
    public void addNotify() {
        super.addNotify();
        calc();
    }

    /**
     * 
     * @param i  Ein Integer.
     */
    public void setX(final int i) { 
        x = i;  
    }
    
    /**
     * 
     * @param i  Ein Integer.
     */
    public void setY(final int i) { 
        y = i;  
    }

    /**
     * 
     * @return  Die linke Grenze.
     */
    public int getLeftRightMargin() {
        return leftRightMargin;
    }

    /**
     * 
     * @param i  Ein Integer.
     */
    public void setLeftRightMargin(final int i) {
        // make sense only if alignment is MultiLineLabel.LEFT!
        if (i >= 0) {
            leftRightMargin = i;
        }
    }

    /**
     * 
     * @return  Das Alignment.
     */
    public int getAlignment() {
        return alignment;
    }

    /**
     * 
     * @param i  Ein Integer.
     */
    public void setAlignment(final int i) {
        switch (alignment) {
            case 0:
            case 1:
            case 2:
                alignment = i;
                break;
            default:
                throw new IllegalArgumentException();
        }
        repaint();
    }

    /**
     * 
     * @return  Die Top-Bottom-Margin.
     */
    public int getTopBottomMargin() {
        return topBottomMargin;
    }

    /**
     * 
     * @param i  Ein Integer.
     */
    public void setTopBottomMargin(final int i) {
        if (i >= 0) {
            topBottomMargin = i;
        }
    }

    /**
     * 
     * @param font  Die Schriftart.
     */
    @Override
    public void setFont(final Font font) {
        super.setFont(font);
        calc();
        repaint();
    }

    /**
     * 
     * @return  Die Minimalgröße.
     */
    @Override
    public Dimension getMinimumSize() {
        Dimension d = new Dimension(
            maxWidth + leftRightMargin * 2,
            numLines * lineHeight + topBottomMargin * 2);
        if (d.width == 0) {
            d.width = 10;
        }
        if (d.height == 0) {
            d.height = 10;
        }
        return d;
    }

    /**
     * 
     * @return  Die prefferierte Größe.
     */
    @Override
    public Dimension getPreferredSize() {
        return getMinimumSize();
    }
    
    /**
     * 
     * @return  Grenze.
     */
    public boolean getBorder() {
        return border;
    }
    
    /**
     * 
     * @param flag  Das Border-Flag.
     */
    public void setBorder(final boolean flag) {
        border = flag;
    }

    /**
     *  
     * @param s  Ein String.
     */
    public void setText(final String s) {
        // parse the string , "\n" is a the line separator
        StringTokenizer st =
            new StringTokenizer(s, "\n");
        numLines = st.countTokens();
        lines = new String[numLines];
        lineWidths = new int[numLines];
        for (int i = 0; i < numLines; i++) {
            lines[i] = st.nextToken();
        }
        calc();
        repaint();
        text = new String(s);
    }

    /**
     * 
     * @return  Der Text.
     */
    public String getText() {
        return text;
    }

    /**
     * 
     * @return  Die Border-Farbe.
     */
    public Color getBorderColor() {
        return borderColor;
    }

    /**
     * 
     * @param c  Die zu setzende Farbe.
     */
    public void setBorderColor(final Color c) {
        borderColor = c;
    }

    /**
     * 
     */
    private void calc() {
        // calc dimension and extract maximum width
        Font f = getFont();
        if (f != null) {
            FontMetrics fm = getFontMetrics(f);
            if (fm != null) {
                lineHeight = fm.getHeight();
                lineAscent = fm.getAscent();
                maxWidth = 0;
                for (int i = 0; i < numLines; i++) {
                    lineWidths[i] =
                    fm.stringWidth(lines[i]);
                    if (lineWidths[i] > maxWidth) {
                         maxWidth = lineWidths[i];
                    }
                }
            }
        }
    }

    /**
     * 
     * @param g  Das Graphics-Objekt.
     */
    @Override
    public void update(final Graphics g) {
        super.paint(g);
        Dimension d = getSize();
        if ((offGraphics == null) 
              || (d.width != offDimension.width)
              || (d.height != offDimension.height)
           ) {
            offDimension = d;
            offImage = createImage(d.width, d.height);
            offGraphics = offImage.getGraphics();
        }
        offGraphics.setColor(getBackground());
        offGraphics.fillRect(x, y, getSize().width - 1,
                             getSize().height - 1);
        if (border) {
            offGraphics.setColor(borderColor);
            offGraphics.drawRect(x, 
                                 y, 
                                 getSize().width - 1, 
                                 getSize().height - 1);
        }
        int j = lineAscent + (d.height - numLines * lineHeight) / 2;
        for (int k = 0; k < numLines;) {
            int i;
            switch (alignment) {
                case 0:
                    i = 0;
                    break;
                case 2:
                    i = d.width - lineWidths[k];
                    break;
                default:
                    i = (d.width - lineWidths[k]) / 2;
                    break;
            }
            i += leftRightMargin;
            offGraphics.setColor(getForeground());
            offGraphics.drawString(lines[k], i + x, j + y);
            k++;
            j += lineHeight;
        }
        g.drawImage(offImage, 0, 0, this);
    }

    /**
     * 
     * @param g  Das Graphics-Objekt.
     */
    @Override
    public void paint(final Graphics g) {
      update(g);
    }

//    /**
//     * 
//     * @param args  Die Argumente.
//     */
//    public static void main(final String[] args) {
//        Frame f = new Frame("Test MultiLineLabel");
//        f.setSize(200, 200);
//        f.setLayout(new FlowLayout());
//        f.setVisible(true);
//
//        MultiLineLabel mll1 = new MultiLineLabel(
//           "This a test!\nsecond line\nthird line",
//           MultiLineLabel.LEFT, true);
//        // mll1.setBorderColor(new Color(0).blue);
//        mll1.setLeftRightMargin(15);
//        mll1.setTopBottomMargin(15);
//        f.add(mll1);
//
//        Button b = new Button("Dummy");
//        f.add(b);
//
//        MultiLineLabel mll2 = new MultiLineLabel(
//            "123\n4\n567", MultiLineLabel.RIGHT, false);
//        mll2.setForeground(Color.yellow);
//        mll2.setBackground(Color.black);
//        f.add(mll2);
//
//        f.validate();
//    }
}