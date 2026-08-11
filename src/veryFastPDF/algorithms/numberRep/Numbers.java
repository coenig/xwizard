/*
 * File name:        Numbers.java (package veryFastPDF.algorithms.numberRep)
 * Author(s):        Lukas König
 * Java version:     8.0 (at generation time)
 * Generation date:  04.02.2015 (19:38:17)
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

package veryFastPDF.algorithms.numberRep;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

import eas.math.MiscMath;
import eas.miscellaneous.StaticMethods;
import veryFastPDF.algorithms.latex.LaTeXCommands;
import veryFastPDF.algorithms.numberRep.representations.Complement;
import veryFastPDF.algorithms.numberRep.representations.ExcessQ;
import veryFastPDF.algorithms.numberRep.representations.FixedPointRational;
import veryFastPDF.algorithms.numberRep.representations.FloatingPointRational;
import veryFastPDF.pdfProcessors.LaTeXPDF;
import veryFastPDF.pdfProcessors.PDFProcessor;
import veryFastPDF.script.Exercise;
import veryFastPDF.script.MethodWrapper;
import veryFastPDF.script.RepresentableAsPDF;
import veryFastPDF.script.RepresentableDefault;
import veryFastPDF.web.Webproof;

/**
 * @author Lukas König
 */
@Webproof(useInProductiveMode = true)
public class Numbers extends RepresentableDefault {

    private static final long serialVersionUID = -5646183348483975227L;
    public static final String SCRIPT_PREFIX = "number:";

    private ArrayList<NumberRepresentable<?>> availableTypes = new ArrayList<>();

    private LinkedList<NumberRepresentable<?>> nrs = new LinkedList<>();
    
    private boolean showOptionsInPlainText = false;
    
    public Numbers(Exercise exercise) {
        super(exercise);
        
        availableTypes.add(new ExcessQ(this));
        availableTypes.add(new Complement(this));
        availableTypes.add(new FixedPointRational(this));
        availableTypes.add(new FloatingPointRational(this));
        
        this.setAllowCollapsingRules(false);
    }
    
    public String getAvailableTypesFormatted() {
        String s = "";
        
        for (NumberRepresentable<?> r : this.availableTypes) {
            s += ", " + r.getScriptPartPrefix();
        }
        
        return s.substring(2);
    }
    
    @Override
    public String[] getExampleScripts() {
        return new String[] {
                SCRIPT_PREFIX + "\n" +
                "floatingpoint[\n" + 
                "code=1:00011001:00000011100000000000000, /* Doppelpunkte optional, kennzeichnen Charakteristik/Mantisse. Ueberschreiben \"Length\"-Variablen. */\n" + 
                "ieee754=true,                            /* true ueberschreibt alle Variablen, ausser \"Length\"-Variablen. */\n" + 
                "*characteristicLength=8,\n" + 
                "*mantissaLength=23,\n" + 
                "*afterDecVal=5,\n" + 
                "*radix=2,\n" + 
                "*q=0,\n" + 
                "*autoQ=true];\n" + 
                "\n" + 
                "--declarations--\n" + 
                "e=#n#;\n" + 
                "--declarations-end--",
                SCRIPT_PREFIX + "\n" +
                "excessq[q=128, value=230, radix=2, *length=10];\n" + 
                "excessq[q=128, value=230, code=101100110, radix=2];\n" + 
                "excessq[q=0, code=marlon, radix=36];\n" + 
                "fixedpoint[code=marlon.braun,radix=36,*afterDecVal=10];\n" + 
                "fixedpoint[value=1348339847.3266214652,radix=36,*afterDec=26];\n" + 
                "fixedpoint[code=friederike.pfeiffer,value=1601011158292670,radix=36];\n" + 
                "complement[value=-11050];\n" + 
                "complement[code=10110101001010101];\n" + 
                "complement[value=-4,complementType=2,*length=6];\n" + 
                "complement[code= 111100,complementType=2];\n" + 
                "--declarations--\n" + 
                "e=#n#;\n" + 
                "showOptionsInPlainText=false;\n" + 
                "--declarations-end--"
                };
    }

    @Override
    public boolean isAcceptableScript(String code) {
        return (code + "").replace(" ", "").replace("\n", "").startsWith(SCRIPT_PREFIX);
    }
    
    @Override
    public void createInstanceFromScript(String script, RepresentableAsPDF father) {
        this.nrs.clear();
        super.applyDeclarationsAndPreprocessors(script, father, 0);
        String plainScript = this.getScriptWithoutPrepAndDeclAndPreamble();
        
        String[] lines = plainScript.split(";");

        for (String s : lines) {
            String line = StaticMethods.removeWhitespaces(s);
            NumberRepresentable<?> createNumberRep = createNumberRep(line);
            if (createNumberRep != null) {
                this.nrs.add(createNumberRep);
            }
        }
    }

    public NumberRepresentable<?> createNumberRep(String line) {
        for (NumberRepresentable<?> nr : availableTypes) {
            if (line.startsWith(nr.getScriptPartPrefix())) {
                String parameters = MiscMath.extractFirstSubstringLevelwise(
                        line, 
                        "[", 
                        "]", 
                        nr.getScriptPartPrefix().length());
                
                NumberRepresentable<?> cloned;
                cloned = (NumberRepresentable<?>) nr.createInstance();
                cloned.setFromParameters(parameters);
                return cloned;
            }
        }
        
        return null;
    }

    @Override
    public PDFProcessor generatePDFscript(String pdfPath) {
        super.generatePDFscript(pdfPath);
        String inlineScripts = "";
        
//        for (NumberRepresentable<?> nr : this.nrs) {
//            String string = nr.toString();
//            String visualisationScript = nr.visualisationScript(null, null, "");
//            
//            inlineScripts += string + "\n"
//                    + (visualisationScript == null
//                    ? ""
//                    :   RepresentableDefault.INSCRIPT_BEGIN_TAG
//                        + "-1|"
//                        + visualisationScript
//                        + RepresentableDefault.INSCRIPT_END_TAG
//                        + "\n")
//                    + "\\par";
//        }

        LinkedList<String> nrStrings = new LinkedList<>();
        for (NumberRepresentable<?> nr : this.nrs) {
            String string = nr.toString();
            String visualisationScript = nr.visualisationScript(null, null, "");
            if (visualisationScript != null) {
                nrStrings.add(
                        "\\begin{minipage}{\\textwidth}"
                        + string
                        + "\n\\par\n"
                        + LaTeXCommands.PLACE_HOLDER_INLINES_CRIPT_BEGIN_TAG
                        + visualisationScript
                        + LaTeXCommands.PLACE_HOLDER_INLINES_CRIPT_END_TAG
                        + "\\end{minipage}"
                        );
            }
        }
        
        inlineScripts = LaTeXCommands.center(
                LaTeXCommands.distributeOnTabular(4, 3, nrStrings.toArray()));
        
        return new LaTeXPDF(
                LaTeXCommands.PREAMBLE_CROP_PAGE
                + inlineScripts
                + LaTeXCommands.POSTAMBLE_STANDARD, 
                pdfPath,
                this);
    }
    
    @Override
    public String getGermanName() {
        return "Zahlen";
    }

    @Override
    public Class<? extends PDFProcessor> getPDFProcessorClass() {
        return LaTeXPDF.class;
    }
    
    @Override
    public HashMap<String, MethodWrapper> getDynamicMethods() {
        HashMap<String, MethodWrapper> methods = super.getDynamicMethods();
        
        for (NumberRepresentable<?> nr : availableTypes) {
            try {
                String createByCode = nr.getRepName() + " => Decimal";
                String createByCode_G = nr.getRepName_G() + " => Dezimal";
                String createByValue = "Decimal => " + nr.getRepName();
                String createByValue_G = "Dezimal => " + nr.getRepName_G();
                
                MethodWrapper mw1 = nr.dynMethodCreateByCode();
                mw1.setTooltip("Creates a new number representation from a representation code");
                mw1.setTooltip_G("Erzeugt eine neue Zahlendarstellung aus einem Zahlencode");
                mw1.setDisplayName(createByCode);
                mw1.setDisplayName_G(createByCode_G);
                mw1.addClassOfTargetScript(this.getClass());

                MethodWrapper mw2 = nr.dynMethodCreateByValue();
                mw2.setTooltip("Creates a new number representation from a decimal number");
                mw2.setTooltip_G("Erzeugt eine neue Zahlendarstellung aus einer Dezimalzahl");
                mw2.setDisplayName(createByValue);
                mw2.setDisplayName_G(createByValue_G);
                mw2.addClassOfTargetScript(this.getClass());

                mw1.setDisplayLevelName(1, "Create new number", "Erstelle neue Zahl");
                mw2.setDisplayLevelName(1, "Create new number", "Erstelle neue Zahl");
                
                methods.put(createByCode, mw1);
                methods.put(createByValue, mw2);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }
        
        return methods;
    }
    
    @Override
    public String createScriptFromInstance() {
        String s = SCRIPT_PREFIX;
        
        for (NumberRepresentable<?> nr : this.nrs) {
            s += "\n" + nr.getScriptPartPrefix() + "[" + nr.exportToParameters(false) + "];";
        }
        
        return s + "\n" + this.generateCompleteDeclarationsBlock();
    }
    
    public void addNumber(NumberRepresentable<?> nr) {
        this.nrs.add(nr);
    }

    public boolean isShowOptions() {
        return showOptionsInPlainText;
    }
}
