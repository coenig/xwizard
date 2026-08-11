/*
 * File name:        SVGVisualizer.java (package webService)
 * Author(s):        Lukas König
 * Java version:     8.0 (at generation time)
 * Generation date:  19.03.2016 (15:38:33)
 * Part of the EAS => VFP => XWizard webapp implementation.
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

package veryFastPDF;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.StringReader;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.batik.anim.dom.SAXSVGDocumentFactory;
import org.apache.batik.swing.JSVGCanvas;
import org.apache.batik.util.XMLResourceDescriptor;
import org.w3c.dom.svg.SVGDocument;

import eas.miscellaneous.StaticMethods;

/**
 * @author Lukas König, http://xmlgraphics.apache.org/batik/using/swing.html
 *
 */
public class SVGVisualizer {
    
    private static final String SVG_VIS_VFP_FOREVER_ID = "SVG-VIS-VFP_FOREVER";

    public SVGVisualizer() {
        frame.setSize(800, 600);
    }
    
    public void run(String svgString) throws IOException {
        frame.getContentPane().removeAll();
        frame.getContentPane().add(this.createComponents(svgString));

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                frame.setVisible(false);
            }
        });
    }

    public void display() {
        frame.setVisible(true);
        frame.toFront();
        StaticMethods.loadWindowFramePosition(frame, SVG_VIS_VFP_FOREVER_ID);
    }

    public void hide() {
        StaticMethods.storeWindowFramePosition(frame, SVG_VIS_VFP_FOREVER_ID);
        frame.setVisible(false);
    }
    
    private static JFrame frame = new JFrame("SVG output");

    private JLabel label = new JLabel();

    private JSVGCanvas svgCanvas = new JSVGCanvas();

    public JComponent createComponents(String svgString) throws IOException {
        final JPanel panel = new JPanel(new BorderLayout());

        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.add(label);

        panel.add("North", p);
        panel.add("Center", svgCanvas);

        StringReader reader = new StringReader(svgString);
        String parser = XMLResourceDescriptor.getXMLParserClassName();
        SAXSVGDocumentFactory f = new SAXSVGDocumentFactory(parser);

        // Do not forget the URI, even if it's fake, eg: c://svg/sample.svg.
        // This is particularly important if your doc references another doc 
        // in a relative URI.
        SVGDocument doc = f.createSVGDocument("http://example.com/", reader);
        svgCanvas.setDocument(doc);
        
        return panel;
    }
}