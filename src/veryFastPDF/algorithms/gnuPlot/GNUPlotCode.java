/*
 * File name:        GNUPlot.java (package eas.math.fundamentalAlgorithms.graphBased.algorithms.gnuPlot)
 * Author(s):        Lukas König
 * Java version:     8.0 (at generation time)
 * Generation date:  27.10.2014 (20:12:03)
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

package veryFastPDF.algorithms.gnuPlot;

import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;

import org.leores.util.SysUtil.Command;

import eas.miscellaneous.gnuplot.JGnuplot;
import eas.miscellaneous.gnuplot.JGnuplot.Plot;
import veryFastPDF.pdfProcessors.PDFProcessor;
import veryFastPDF.pdfProcessors.deprecated.GNUPlotPDF;
import veryFastPDF.plugin.FancyJButton;
import veryFastPDF.script.Exercise;
import veryFastPDF.script.RepresentableAsPDF;
import veryFastPDF.script.RepresentableDefault;

/**
 * @author Lukas König
 */
@SuppressWarnings("deprecation")
public class GNUPlotCode extends RepresentableDefault {

    public GNUPlotCode(Exercise exercise) {
        super(exercise);
    }

    private static final long serialVersionUID = 1596990952122229954L;
    private GNUPlotPDF pdfProcessor;
    
    @Override
    public String[] getExampleScripts() {
        this.getPDFProcessor();
        
        String s1 = GNUPlotPDF.GNUPLOT_PREFIX + " plot [-10:10] sin(x),atan(x),cos(atan(x))";
        String s4 = GNUPlotPDF.GNUPLOT_PREFIX + " \n" + 
                "set hidden3d\n" + 
                "set isosamples 40\n" + 
                "set xrange [-2.5:2.5]\n" + 
                "set yrange [-2.5:2.5]\n" + 
                "set zrange [0.0:5.0]\n" + 
                "set xtics 0.5\n" + 
                "set ytics 1.0\n" + 
                "set ztics 0.5\n" + 
                "set view 40,50,1.0,1.5\n" + 
                "f(x,y)=(x**2+2.5*y**2-y)*exp(1-(x**2+y**2))\n" + 
                "splot f(x,y)";
        String s3 = GNUPlotPDF.GNUPLOT_PREFIX + "set border 4095 front linetype -1 linewidth 1.000\n" + 
                "set format cb \"%.01t*10^{%T}\"\n" + 
                "set samples 31, 31\n" + 
                "set isosamples 31, 31\n" + 
                "unset surface\n" + 
                "set ticslevel 0\n" + 
                "set title \"only for enhanced terminals: 'set format cb ...'\" \n" + 
                "set xlabel \"X\" \n" + 
                "set xrange [ -185.000 : 185.000 ] noreverse nowriteback\n" + 
                "set ylabel \"Y\" \n" + 
                "set yrange [ -185.000 : 185.000 ] noreverse nowriteback\n" + 
                "set cblabel \"the colour gradient\" \n" + 
                "set pm3d implicit at s\n" + 
                "set palette positive nops_allcF maxcolors 0 gamma 1.5 gray\n" + 
                "splot abs(x)**3+abs(y)**3";
        String s6 = GNUPlotPDF.GNUPLOT_PREFIX + "set dummy u,v\n" + 
                "set nokey\n" + 
                "set parametric\n" + 
                "set view 50, 30, 1, 2\n" + 
                "set isosamples 150, 50\n" + 
                "set hidden3d offset 1 trianglepattern 3 undefined 1 altdiagonal bentover\n" + 
                "set ticslevel -0.167\n" + 
                "set title \"Interlocking Tori\" \n" + 
                "set urange [ -3.14159 : 3.14159 ] noreverse nowriteback\n" + 
                "set vrange [ -3.14159 : 3.14159 ] noreverse nowriteback\n" + 
                "set zrange [ * : * ] noreverse nowriteback  # (currently [-3.00000:1.50000] )\n" + 
                "splot 1+cos(u)+.5*cos(u)*cos(v),sin(u)+.5*sin(u)*cos(v),.5*sin(v) with lines, 2+cos(u)+.5*cos(u)*cos(v),.5*sin(v),sin(u)+.5*sin(u)*cos(v) with lines";
        
        return new String[] {s1, s4, s3, s6};
    }

    @Override
    public boolean isAcceptableScript(String code) {
        this.getPDFProcessor();
        return (code + "").startsWith(GNUPlotPDF.GNUPLOT_PREFIX);
    }

    @Override
    public void createInstanceFromScript(String code, RepresentableAsPDF father) {
        super.setRawScript(code);
    }

    @Override
    public PDFProcessor generatePDFscript(String pdfPath) {
        this.getPDFProcessor();
        return this.pdfProcessor;
    }

    @Override
    public JComponent getAdditionalInfo() {
        JPanel panel = new JPanel(new GridLayout(3, 1));
        JButton butt1 = new JButton("Execute GNU Plot");
        butt1.setBackground(FancyJButton.NON_WEB_BUTTON_COLOR);
        butt1.addMouseListener(new MouseListener() {
            
            @Override public void mouseReleased(MouseEvent e) {}
            @Override public void mousePressed(MouseEvent e) {}
            @Override public void mouseExited(MouseEvent e) {}
            @Override public void mouseEntered(MouseEvent e) {}
            
            @Override
            public void mouseClicked(MouseEvent e) {
                JGnuplot plot = new JGnuplot();
                Command c = plot.execute(
                        new Plot("test"),
                        GNUPlotCode.this.getRawScript().substring(GNUPlotCode.this
                                .getPDFProcessor().getCodePrefix().length()));
                c.toString();
            }
        });
        
        panel.add(butt1);

        return panel;
    }

    @Override
    public Class<? extends PDFProcessor> getPDFProcessorClass() {
        return GNUPlotPDF.class;
    }

    @Override
    public HashMap<String, String> getMetaProperties() {
        HashMap<String, String> metaProps = new HashMap<>();
        metaProps.put("ScriptLength", this.getRawScript().length() + "");
        return metaProps;
    }

    @Override
    public String getGermanName() {
        return "GNU-Plot-Code";
    }

    @Override
    public String createScriptFromInstance() {
        return null;
    }
}
