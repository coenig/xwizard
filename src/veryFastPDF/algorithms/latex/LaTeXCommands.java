/*
 * File name:        LaTeXCommands.java (package veryFastPDF.algorithms.latex)
 * Author(s):        Lukas König
 * Java version:     8.0 (at generation time)
 * Generation date:  29.09.2015 (18:10:50)
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

package veryFastPDF.algorithms.latex;

import java.util.HashMap;

import eas.miscellaneous.StaticMethods;
import veryFastPDF.script.RepresentableDefault;

/**
 * @author Lukas König
 */
public class LaTeXCommands {

    public static final int TRIANGLE_TABLE_STANDARD_PAGE_HEIGHT = 10;
    public static final int TRIANGLE_TABLE_STANDARD_PAGE_WIDTH = 18;

    public static final String INSERT_FILE_NAME_PLACEHOLDER = "**XX-INSERT-FILE-NAME-XX**";

    public static final String XWIZZ_WELCOME_MESSAGE = "latex:\n" + 
            "X_{\\mbox{\\tt wizard}}\n" + 
            "--declarations--\n" + 
            "formulaMode=true\n" + 
            "--declarations-end--";
    
    public static final String XWIZZ_WELCOME_MESSAGE_LOGO_2 = "latex:\n" + 
            "\\documentclass{article}\n" + 
            "\\usepackage{graphicx}\n" + 
            "\\usepackage{grffile}\n" + 
            "\\begin{document}\n" + 
            "\\thispagestyle{empty}\n" +
            "\\begin{center}\n" + 
            "\\includegraphics[width=\\textwidth]{" + INSERT_FILE_NAME_PLACEHOLDER + "}\n" + 
            "\\end{center}\n" + 
            "\\end{document}";
    
    public static final String XWIZZ_WELCOME_MESSAGE_LOGO(boolean english) {
//        return english ? "CID-25423" : "CID-25420"; 
        
        /*
         * Don't delete the comments below - just to be secure if something happens to the database.
         */
        
      return english 
//      return true 
                ? "Clatex:%varm|gra|ger|bbding%\n" + 
                "f1=@{latex:\\documentclass[preview]{standalone}\n" + 
                "\\usepackage[utf8]{inputenc}\n" + 
                "\\usepackage{tikz}\n" +
                "\\usepackage{bbding}\n" +
                "\n" + 
                "\\PreviewEnvironment{tikzpicture}\n" + 
                "\n" + 
                "\\begin{document}\n" + 
                "\\definecolor{cffffff}{RGB}{255,255,255}\n" + 
                "\\definecolor{cffcc00}{RGB}{255,204,0}\n" + 
                "\\definecolor{cffd42a}{RGB}{255,212,42}\n" + 
                "\n" + 
                "{\\Large~~\n" + 
                "\n" + 
                "\\def\\wiz{\\begin{tikzpicture}[y=0.80pt, x=0.80pt, yscale=-1.000000, xscale=1.000000, inner sep=0pt, outer sep=0pt]\n" + 
                "\\begin{scope}[cm={{0.83333,0.55277,-0.55277,0.83333,(255.01362,-70.59716)}},fill=black]\n" + 
                "  \\path[fill=black,draw opacity=0.600,line width=1.600pt,rounded corners=0.0000cm]\n" + 
                "    (191.3380,152.6487) rectangle (198.4632,248.9179);\n" + 
                "  \\path[fill=black,draw opacity=0.600,line width=1.600pt] (194.8935,249.4785)\n" + 
                "    ellipse (0.1003cm and 0.0928cm);\n" + 
                "  \\path[fill=black,draw opacity=0.600,line width=1.600pt] (194.9028,152.9608)\n" + 
                "    ellipse (0.1009cm and 0.0928cm);\n" + 
                "\\end{scope}\n" + 
                "\\path[cm={{0.83333,0.55277,-0.55277,0.83333,(0.0,0.0)}},fill=cffffff,draw\n" + 
                "  opacity=0.600,line width=1.600pt,rounded corners=0.0000cm] (365.1172,36.8932)\n" + 
                "  rectangle (371.5422,48.9918);\n" + 
                "\\path[cm={{0.83333,0.55277,-0.55277,0.83333,(0.0,0.0)}},fill=black,draw\n" + 
                "  opacity=0.600,line width=1.600pt] (368.4319,49.5524) ellipse (0.1003cm and\n" + 
                "  0.0928cm);\n" + 
                "\\path[cm={{0.83333,0.55277,-0.55277,0.83333,(0.0,0.0)}},fill=cffffff,draw\n" + 
                "  opacity=0.600,line width=1.600pt] (368.2976,36.6631) ellipse (0.0913cm and\n" + 
                "  0.0839cm);\n" + 
                "\\path[cm={{0.83333,0.55277,-0.55277,0.83333,(0.0,0.0)}},fill=cffffff,draw\n" + 
                "  opacity=0.600,line width=1.600pt,rounded corners=0.0000cm] (365.2303,-46.3353)\n" + 
                "  rectangle (371.6554,-34.2367);\n" + 
                "\\path[cm={{0.83333,0.55277,-0.55277,0.83333,(0.0,0.0)}},fill=black,draw\n" + 
                "  opacity=0.600,line width=1.600pt] (368.4451,-33.6761) ellipse (0.0975cm and\n" + 
                "  0.0928cm);\n" + 
                "\\path[cm={{0.83333,0.55277,-0.55277,0.83333,(0.0,0.0)}},fill=cffffff,draw\n" + 
                "  opacity=0.600,line width=1.600pt] (368.4108,-46.5654) ellipse (0.0913cm and\n" + 
                "  0.0839cm);\n" + 
                "\\path[fill=cffcc00,opacity=0.824,line width=0.666pt] (443.8110,195.0974) --\n" + 
                "  (436.0784,191.3784) -- (428.6311,195.6399) -- (429.7785,187.1366) --\n" + 
                "  (423.4242,181.3706) -- (431.8660,179.8342) -- (435.3861,172.0091) --\n" + 
                "  (439.4560,179.5629) -- (447.9859,180.4927) -- (442.0594,186.6976) -- cycle;\n" + 
                "\\path[cm={{0.94645,0.32285,-0.32285,0.94645,(73.66122,-106.19459)}},fill=cffcc00,line\n" + 
                "  width=0.370pt] (362.8241,136.7560) -- (354.8443,143.7835) --\n" + 
                "  (356.1094,154.3410) -- (346.9600,148.9234) -- (337.3102,153.3891) --\n" + 
                "  (339.6354,143.0133) -- (332.4063,135.2157) -- (342.9927,134.2208) --\n" + 
                "  (348.1747,124.9360) -- (352.3923,134.6968) -- cycle;\n" + 
                "\\path[cm={{1.09715,0.23207,-0.23207,1.09715,(2.7513,-121.83327)}},fill=cffd42a,line\n" + 
                "  width=0.364pt] (412.2756,171.8985) -- (403.4527,169.9855) --\n" + 
                "  (396.8751,176.1692) -- (395.9679,167.1870) -- (388.0543,162.8421) --\n" + 
                "  (396.3166,159.2037) -- (398.0033,150.3348) -- (404.0169,157.0683) --\n" + 
                "  (412.9729,155.9319) -- (408.4272,163.7319) -- cycle;\n" + 
                "\\end{tikzpicture}}\n" + 
                "\\begin{center}\n" + 
                "~~~~~~~~~~~~~~~~~~\\wiz\n" + 
                "\n" + 
                "\\vspace{-1.3cm}\n" + 
                "~~~~~~~~~~~~~~~~~~~~~\\begin{minipage}{0.5\\textwidth}\n" + 
                "\\begin{center}\n" + 
                "\\begin{color}{purple}\n" + 
                "\\textbf{Click} {\\huge\\HandCuffRight} for\n" + 
                "\n" + 
                "a quick tutorial!\n" + 
                "\\end{color}\n" + 
                "\n" + 
                "Or enter a \\textbf{script} \n" + 
                "\n" + 
                "or view an \\textbf{example}\n" + 
                "$$\\Downarrow$$\n" + 
                "\\end{center}\n" + 
                "\\end{minipage}\n" + 
                "\\end{center}\n" + 
                "\\end{document}}@\n" + 
                "f2=@{0.5|latex:%varm|gra|ger|bbding%\n" + 
                "{\\huge\\HandCuffRight} \\begin{center}XWizard can display many objects from computer science, e.\\,g., FSMs:\\end{center}\n" + 
                "\n" + 
                "@{0.5|fsm:}@.randD[5, true, 0]\n" + 
                "}@\n" + 
                "f3=@{0.5|latex:%varm|gra|ger|bbding%\n" + 
                "{\\huge\\HandCuffRight}\\begin{flushleft}Use conversion methods, such as \\fbox{Minimize}, to apply algorithms:\\end{flushleft}\n" + 
                "\n" + 
                "@{0.5|fsm:}@.randD[5, true, 0]\n" + 
                "}@\n" + 
                "f4=@{0.5|latex:%varm|gra|ger|bbding%\n" + 
                "{\\huge\\HandCuffRight}\\begin{flushleft}Use conversion methods, such as \\fbox{Minimize}, to apply algorithms:\\end{flushleft}\n" + 
                "\n" + 
                "\\raisebox{-0.5\\height}{@{0.5|fsm:}@.randD[5, true, 0]} \\raisebox{-0.5\\height}{$\\rightarrow$} \\raisebox{-0.5\\height}{@{0.5|fsm:}@.randD[5, true, 0].min}\n" + 
                "}@\n" + 
                "f5=@{0.5|latex:%varm|gra|ger|bbding%\n" + 
                "{\\huge\\HandCuffRight}\\begin{center}Or use scripts to accomplish the same behavior.\n" + 
                "\n" + 
                "\\verb+    @\"{@{fsm:}@.randD[5, true, 0].min}\"@+ \n" + 
                "\n" + 
                "\\ldots results in:\\end{center}\n" + 
                "\n" + 
                "@{0.5|fsm:}@.randD[5, true, 0].min\n" + 
                "}@\n" + 
                "f6=@{0.5|latex:%varm|gra|ger|bbding%\n" + 
                "{\\huge\\HandCuffRight}\\begin{center}Look at the examples at the bottom of the page\n" + 
                "\n" + 
                "to get the hang of it\\ldots\\end{center}\n" + 
                "\n" + 
                "@{0.5|fsm:}@.randD[5, true, 0].min\n" + 
                "}@\n" + 
                "f7=@{0.5|latex:%varm|gra|ger|bbding%\n" + 
                "{\\huge\\HandCuffRight} \\begin{center}XWizard scripts are Turing-complete.\n" + 
                "\\bigbreak\n" + 
                "Define methods like this:\n" + 
                "\\bigbreak\n" + 
                "\\verb+   @\"{@{}\"@#0# is s@\"{@{}\"@o@\"{}@}\"@.for[#i, 1, #1#] great!@\"{}@}\"@*.newMethod[great, 1]+\n" + 
                "@{#0# is s@{o}@.for[#i, 1, #1#] great!}@*.newMethod[great, 1]\n" + 
                "\\bigbreak\n" + 
                "...and call them as:\n" + 
                "\\bigbreak\n" + 
                "\\verb+   @\"{@{}\"@XWizard@\"{}@}\"@.great[5]+ \n\n$\\rightarrow$ \\fbox{\\ttfamily @{XWizard}@.great[5]}\n" + 
                "\\end{center}\n" + 
                "\n" + 
                "}@\n" + 
                "f8=@{0.5|latex:%varm|gra|ger|bbding%\n" + 
                "{\\huge\\HandCuffRight} \\begin{center}Did you realize that this tutorial is also just a script?\n" + 
                "\n" + 
                "You can analyze it below (hint: read from bottom to top).\n" + 
                "\n" + 
                "@{0.5|fsm:}@.randD[5, true, 0].min\\end{center}\n" + 
                "}@\n" + 
                "f9=@{0.5|latex:%varm|gra|ger|bbding%\n" + 
                "{\\huge\\HandCuffRight} \\begin{center}Finally: You can download PDFs of the XWizard \n" + 
                "\n" + 
                "objects by scrolling down with your mouse wheel $\\Downarrow$\n" + 
                "\n" + 
                "@{0.5|fsm:}@.randD[5, true, 0].min\\end{center}\n" + 
                "}@\n" + 
                "f10=@{0.5|latex:%varm|gra|ger|bbding%\n" + 
                "\\Large~\\bigbreak\\bigbreak \n" + 
                "\\begin{center}And now: \n" + 
                "\n" + 
                "Have fun with XWizard!\n" + 
                "\n" + 
                "{\\normalsize Click here to restart: \n\n{\\huge\\HandCuffRight}}\n" + 
                "\\end{center}\n" + 
                "}@\n" + 
                "--declarations--\n" + 
                "e=#n#;\n" + 
                "animate=f1->f2->f3->f4->f5->f6->f7->f8->f9->f10;\n" + 
                "formulaMode=false\n" + 
                "--declarations-end--" 
                : "Clatex:%varm|gra|ger|bbding%\n" + 
                        "f1=@{latex:\\documentclass[preview]{standalone}\n" + 
                        "\\usepackage[utf8]{inputenc}\n" + 
                        "\\usepackage[ngerman]{babel}\n" + 
                        "\\usepackage[utf8]{fontenc}\n" + 
                        "\\usepackage{tikz}\n" + 
                        "\\usepackage{bbding}\n" +
                        "\\PreviewEnvironment{tikzpicture}\n" + 
                        "\n" + 
                        "\\begin{document}\n" + 
                        "\\definecolor{cffffff}{RGB}{255,255,255}\n" + 
                        "\\definecolor{cffcc00}{RGB}{255,204,0}\n" + 
                        "\\definecolor{cffd42a}{RGB}{255,212,42}\n" + 
                        "\n" + 
                        "{\\Large~\n" + 
                        "\n" + 
                        "\\def\\wiz{\\begin{tikzpicture}[y=0.80pt, x=0.80pt, yscale=-1.000000, xscale=1.000000, inner sep=0pt, outer sep=0pt]\n" + 
                        "\\begin{scope}[cm={{0.83333,0.55277,-0.55277,0.83333,(255.01362,-70.59716)}},fill=black]\n" + 
                        "  \\path[fill=black,draw opacity=0.600,line width=1.600pt,rounded corners=0.0000cm]\n" + 
                        "    (191.3380,152.6487) rectangle (198.4632,248.9179);\n" + 
                        "  \\path[fill=black,draw opacity=0.600,line width=1.600pt] (194.8935,249.4785)\n" + 
                        "    ellipse (0.1003cm and 0.0928cm);\n" + 
                        "  \\path[fill=black,draw opacity=0.600,line width=1.600pt] (194.9028,152.9608)\n" + 
                        "    ellipse (0.1009cm and 0.0928cm);\n" + 
                        "\\end{scope}\n" + 
                        "\\path[cm={{0.83333,0.55277,-0.55277,0.83333,(0.0,0.0)}},fill=cffffff,draw\n" + 
                        "  opacity=0.600,line width=1.600pt,rounded corners=0.0000cm] (365.1172,36.8932)\n" + 
                        "  rectangle (371.5422,48.9918);\n" + 
                        "\\path[cm={{0.83333,0.55277,-0.55277,0.83333,(0.0,0.0)}},fill=black,draw\n" + 
                        "  opacity=0.600,line width=1.600pt] (368.4319,49.5524) ellipse (0.1003cm and\n" + 
                        "  0.0928cm);\n" + 
                        "\\path[cm={{0.83333,0.55277,-0.55277,0.83333,(0.0,0.0)}},fill=cffffff,draw\n" + 
                        "  opacity=0.600,line width=1.600pt] (368.2976,36.6631) ellipse (0.0913cm and\n" + 
                        "  0.0839cm);\n" + 
                        "\\path[cm={{0.83333,0.55277,-0.55277,0.83333,(0.0,0.0)}},fill=cffffff,draw\n" + 
                        "  opacity=0.600,line width=1.600pt,rounded corners=0.0000cm] (365.2303,-46.3353)\n" + 
                        "  rectangle (371.6554,-34.2367);\n" + 
                        "\\path[cm={{0.83333,0.55277,-0.55277,0.83333,(0.0,0.0)}},fill=black,draw\n" + 
                        "  opacity=0.600,line width=1.600pt] (368.4451,-33.6761) ellipse (0.0975cm and\n" + 
                        "  0.0928cm);\n" + 
                        "\\path[cm={{0.83333,0.55277,-0.55277,0.83333,(0.0,0.0)}},fill=cffffff,draw\n" + 
                        "  opacity=0.600,line width=1.600pt] (368.4108,-46.5654) ellipse (0.0913cm and\n" + 
                        "  0.0839cm);\n" + 
                        "\\path[fill=cffcc00,opacity=0.824,line width=0.666pt] (443.8110,195.0974) --\n" + 
                        "  (436.0784,191.3784) -- (428.6311,195.6399) -- (429.7785,187.1366) --\n" + 
                        "  (423.4242,181.3706) -- (431.8660,179.8342) -- (435.3861,172.0091) --\n" + 
                        "  (439.4560,179.5629) -- (447.9859,180.4927) -- (442.0594,186.6976) -- cycle;\n" + 
                        "\\path[cm={{0.94645,0.32285,-0.32285,0.94645,(73.66122,-106.19459)}},fill=cffcc00,line\n" + 
                        "  width=0.370pt] (362.8241,136.7560) -- (354.8443,143.7835) --\n" + 
                        "  (356.1094,154.3410) -- (346.9600,148.9234) -- (337.3102,153.3891) --\n" + 
                        "  (339.6354,143.0133) -- (332.4063,135.2157) -- (342.9927,134.2208) --\n" + 
                        "  (348.1747,124.9360) -- (352.3923,134.6968) -- cycle;\n" + 
                        "\\path[cm={{1.09715,0.23207,-0.23207,1.09715,(2.7513,-121.83327)}},fill=cffd42a,line\n" + 
                        "  width=0.364pt] (412.2756,171.8985) -- (403.4527,169.9855) --\n" + 
                        "  (396.8751,176.1692) -- (395.9679,167.1870) -- (388.0543,162.8421) --\n" + 
                        "  (396.3166,159.2037) -- (398.0033,150.3348) -- (404.0169,157.0683) --\n" + 
                        "  (412.9729,155.9319) -- (408.4272,163.7319) -- cycle;\n" + 
                        "\\end{tikzpicture}}\n" + 
                        "\\begin{center}\n" + 
                        "~~~~~~~~~~~~~~~~~~\\wiz\n" + 
                        "\n" + 
                        "\\vspace{-1.3cm}\n" + 
                        "~~~~~~~~~~~~~~~~~~~~~\\begin{minipage}{0.5\\textwidth}\n" + 
                        "\\begin{center}\n" + 
                        "\\begin{color}{purple}\n" + 
                        "\\textbf{Klicke} {\\huge\\HandCuffRight} f\\\"ur\n" + 
                        "%\n" + 
                        "ein Kurztutorial!\n" + 
                        "\\end{color}\n" + 
                        "\n" + 
                        "Oder gib ein \\textbf{Skript} ein\n" + 
                        "\n" + 
                        "oder betrachte ein \\textbf{Beispiel}\n" + 
                        "$$\\Downarrow$$\n" + 
                        "\\end{center}\n" + 
                        "\\end{minipage}\n" + 
                        "\\end{center}\n" + 
                        "\\end{document}}@\n" + 
                        "f2=@{0.5|latex:%varm|gra|ger|bbding%\n" + 
                        "{\\huge\\HandCuffRight} \\begin{center}XWizard kann viele Objekte aus der Informatik darstellen, etwa EAs:\\end{center}\n" + 
                        "\n" + 
                        "@{0.5|fsm:}@.randD[5, true, 0]\n" + 
                        "}@\n" + 
                        "f3=@{0.5|latex:%varm|gra|ger|bbding%\n" + 
                        "{\\huge\\HandCuffRight}\\begin{flushleft}Nutze Konversionsmethoden wie \\fbox{Minimiere}, um Algorithmen anzuwenden:\\end{flushleft}\n" + 
                        "\n" + 
                        "@{0.5|fsm:}@.randD[5, true, 0]\n" + 
                        "}@\n" + 
                        "f4=@{0.5|latex:%varm|gra|ger|bbding%\n" + 
                        "{\\huge\\HandCuffRight}\\begin{flushleft}Nutze Konversionsmethoden wie \\fbox{Minimiere}, um Algorithmen anzuwenden:\\end{flushleft}\n" + 
                        "\n" + 
                        "\\raisebox{-0.5\\height}{@{0.5|fsm:}@.randD[5, true, 0]} \\raisebox{-0.5\\height}{$\\rightarrow$} \\raisebox{-0.5\\height}{@{0.5|fsm:}@.randD[5, true, 0].min}\n" + 
                        "}@\n" + 
                        "f5=@{0.5|latex:%varm|gra|ger|bbding%\n" + 
                        "{\\huge\\HandCuffRight}\\begin{center}Oder schreibe Skripte, um dasselbe zu erreichen.\n" + 
                        "\n" + 
                        "\\verb+    @\"{@{fsm:}@.randD[5, true, 0].min}\"@+ \n" + 
                        "\n" + 
                        "\\ldots erzeugt:\\end{center}\n" + 
                        "\n" + 
                        "@{0.5|fsm:}@.randD[5, true, 0].min\n" + 
                        "}@\n" + 
                        "f6=@{0.5|latex:%varm|gra|ger|bbding%\n" + 
                        "{\\huge\\HandCuffRight}\\begin{center}Betrachte die Beispiele ganz am Ende der Seite,\n" + 
                        "\n" + 
                        "um ein Gef\\\"uhl zu bekommen\\ldots\\end{center}\n" + 
                        "\n" + 
                        "@{0.5|fsm:}@.randD[5, true, 0].min\n" + 
                        "}@\n" + 
                        "f7=@{0.5|latex:%varm|gra|ger|bbding%\n" + 
                        "{\\huge\\HandCuffRight} \\begin{center}XWizard-Skripte sind Turing-vollst\\\"andig.\n" + 
                        "\\bigbreak\n" + 
                        "Definiere Methoden wie folgt:\n" + 
                        "\\bigbreak\n" + 
                        "\\verb+   @\"{@{}\"@#0# is s@\"{@{}\"@o@\"{}@}\"@.for[#i, 1, #1#] great!@\"{}@}\"@*.newMethod[great, 1]+\n" + 
                        "@{#0# is s@{o}@.for[#i, 1, #1#] great!}@*.newMethod[great, 1]\n" + 
                        "\\bigbreak\n" + 
                        "...und rufe sie auf durch:\n" + 
                        "\\bigbreak\n" + 
                        "\\verb+   @\"{@{}\"@XWizard@\"{}@}\"@.great[5]+ \n\n$\\rightarrow$ \\fbox{\\ttfamily @{XWizard}@.great[5]}\n" + 
                        "\\end{center}\n" + 
                        "\n" + 
                        "}@\n" + 
                        "f8=@{0.5|latex:%varm|gra|ger|bbding%\n" + 
                        "{\\huge\\HandCuffRight} \\begin{center}Ist Dir aufgefallen, dass dieses Tutorial auch nur ein Skript ist?\n" + 
                        "\n" + 
                        "Es kann unten analysiert werden (am besten von unten nach oben).\n" + 
                        "\n" + 
                        "@{0.5|fsm:}@.randD[5, true, 0].min\\end{center}\n" + 
                        "}@\n" + 
                        "f9=@{0.5|latex:%varm|gra|ger|bbding%\n" + 
                        "{\\huge\\HandCuffRight} \\begin{center}Zum Schluss: PDFs der XWizard-Objekte k\\\"onnen durch \n" + 
                        "\n" + 
                        "Herunterscrollen mit dem Mausrad heruntergeladen werden $\\Downarrow$\n" + 
                        "\n" + 
                        "@{0.5|fsm:}@.randD[5, true, 0].min\\end{center}\n" + 
                        "}@\n" + 
                        "f10=@{0.5|latex:%varm|gra|ger|bbding%\n" + 
                        "\\Large~\\bigbreak\\bigbreak \n" + 
                        "\\begin{center}Und nun: \n" + 
                        "\n" + 
                        "Viel Spa\\ss\\ mit XWizard!\n" + 
                        "\n" + 
                        "{\\normalsize Klicke hier zum Neustart: \n\n{\\huge\\HandCuffRight}}\n" + 
                        "\\end{center}\n" + 
                        "}@\n" + 
                        "--declarations--\n" + 
                        "e=#n#;\n" + 
                        "animate=f1->f2->f3->f4->f5->f6->f7->f8->f9->f10;\n" + 
                        "formulaMode=false\n" + 
                        "--declarations-end--";
    }
    
    public static final String WELCOME_HALLOWEEN = "latex:\n" +
            "\\documentclass[tikz,border=0]{standalone}\n" + 
            "\\usetikzlibrary{backgrounds}\n" + 
            "\\begin{document}\n" + 
            "\\tikz[background rectangle/.style={fill=black},show background rectangle]\\foreach\\c[count=\\i from0]in{red!10!black,red!15!black,red!20!black,red!25!black,red!30!black,red!35!black,red!40!black,red!45!black,red!50!black,red!55!black,red!60!black,red!65!black,red!75!black,red!75!black!75!orange,red!75!black!50!orange,red!75!black!25!orange,red!75!black!5!orange,orange,orange!75!yellow,orange!50!yellow,orange!25!yellow,yellow,yellow!75,yellow!50,white}\\clip[preaction={fill=\\c},xscale=1-\\i/100,yshift=(\\i>0)*1.5](-6,-7)(6,3)\\foreach\\x in{1,-1}{[xscale=\\x](0:1)to[bend right]++(315:2)to[bend right]++(60:4)to[bend right,looseness=0.5]cycle}[shift=(270:\\i/15),yscale=1-\\i/50](0:5)arc(360:330:5 and 3)\\foreach\\i in{330,300,270,240}{arc(\\i:\\i-10:5 and 3)--(\\i-15:5 and 5)--(\\i-20:5 and 3)arc(\\i-20:\\i-30:5 and 3)}arc(210:180:5 and 3)arc(180:195:5 and 6)\\foreach\\i in{195,225,255,285,315}{arc(\\i:\\i+10:5 and 6)--(\\i+15:5 and 4)--(\\i+20:5 and 6)arc(\\i+20:\\i+30:5 and 6)}arc(345:360:5 and 6)--cycle;\n" + 
            "\\end{document}";
    
    public static final String WELCOME_EASTER(boolean english) {
        return "latex:\n" +
                "\\documentclass[margin=3mm,preview]{standalone}\n" + 
                "\\usepackage{tikz}\n" + 
                "\\usepackage{quotmark}\n" + 
                "\\begin{document}\n" + 
                "\\begin{center}\n" + 
                "\\begin{tikzpicture}\n" + 
                "  \\def\\eggheight{3cm}\n" + 
                "  \\path[preaction={fill=orange!50!white},\n" + 
                "  ball color=orange!60!gray,fill opacity=.5]\n" + 
                "  plot[domain=-pi:pi,samples=100]\n" + 
                "  ({.78*\\eggheight *cos(\\x/4 r)*sin(\\x r)},{-\\eggheight*(cos(\\x r))})\n" + 
                "  -- cycle;\n" + 
                "\\end{tikzpicture}\n" + 
                "\n" + 
                ( english
                ? "Happy EA-"
                : "Frohe O-")
                + "\\tqt{$\\star$}!\n" + 
                "\\end{center}\n" + 
                "\\end{document}\n" + 
                "/* From: http://tex.stackexchange.com/questions/74168/how-can-i-draw-an-egg-using-tikz */";
    }

    public static final String WELCOME_CHRISTMAS = "latex:\n" +
            "/* " + 
            "From: http://tex.stackexchange.com/questions/39149/how-can-we-draw-a-christmas-tree-with-decorations-using-tikz */\n" +
            "\\documentclass[tightpage]{standalone} \n" + 
            "\\usepackage[dvipsnames,svgnames]{xcolor}  \n" + 
            "\\usepackage{tikz}\n" + 
            "\\usetikzlibrary{shapes,decorations.shapes}\n" + 
            "\\newsavebox{\\mycandle}\n" + 
            "\\savebox{\\mycandle}{ \n" + 
            "\\begin{tikzpicture}[scale=.1]\n" + 
            "\\shade[top color=yellow,bottom color=red] (0,0) .. controls (1,.2) and (1,.5) .. (0,2) .. controls (-1,.5)  and  (-1,.2) .. (0,0);\n" + 
            "\\fill[yellow!90!black] (.8,0) rectangle (-.8,-5); \n" + 
            "\\end{tikzpicture} } \n" + 
            "\n" + 
            "\\tikzset{\n" + 
            "  paint/.style={draw=#1!50!black, fill=#1!50},\n" + 
            "  my star/.style={decorate,decoration={shape backgrounds,shape=star},\n" + 
            "                  star points=#1}\n" + 
            "}  \n" + 
            "\n" + 
            "\\begin{document}\n" + 
            "\\begin{tikzpicture} \n" + 
            "   \\draw[fill=Maroon,ultra thick] \n" + 
            "   (.75,-1)  ..  controls (.5,.5)  and   (.5,3)    .. (0.5,4) \n" + 
            "-- (-0.5,4)  ..  controls (-.5,3) and (-.5,.5)     .. (-.75,-1) ;\n" + 
            "\\draw[ultra thick,fill=green!50!black] \n" + 
            "      (0,10) .. controls  (0,8)     and   (1,7)    .. (1.5,7) \n" + 
            "             ..  controls (1,7)     and   (1,7)    .. (0.5,7.25) \n" + 
            "             ..  controls (1.5,5)   and   (2.5,4)  .. (3,4)\n" + 
            "             ..  controls (2,4)     and   (1.25,4) .. (1,4.5)\n" + 
            "             ..  controls (2,2)     and   (3.5,2)  .. (4,2)\n" + 
            "             ..  controls (1,1)     and   (-1,1)   .. (-4,2) \n" + 
            "             ..  controls (-3.5,2)  and   (-2,2)   .. (-1,4.5)\n" + 
            "             ..  controls (-1.25,4) and   (-2,4)   .. (-3,4) \n" + 
            "             ..  controls (-2.5,4)  and   (-1.5,5) .. (-0.5,7.25) \n" + 
            "             ..  controls  (-1,7)   and   (-1,7)   .. (-1.5,7)\n" + 
            "             ..  controls  (-1,7)   and   (0,8)    .. (0,10)\n" + 
            "              ;\n" + 
            "\\foreach \\candle in {(2,5),(-2,5),(0.5,7.5),(-0.5,7.5),(-3,2.5), (3,2.5),\n" + 
            "                    (1.5,1.75),(-1.5,1.75)}\n" + 
            "\\node at \\candle {\\usebox{\\mycandle}} ; \n" + 
            " \\node [star, star point height=.5cm, minimum size=.5cm, draw,fill=yellow,thick]\n" + 
            "       at (0,10) {};\n" + 
            "       \\begin{scope}[decoration={shape sep=.2cm, shape size=.25cm}] \n" + 
            "    \\draw [my star=6, paint=red]  (-4,2)\n" + 
            "             ..  controls (0,2)     and   (1,3.5)   .. (1,4.5)\n" + 
            "             ..  controls (1,6)     and   (0.5,6)      .. (0.5,7.25); \n" + 
            "    \\draw [my star=6, paint=blue]  (4,2)\n" + 
            "             ..  controls  (0,2) and (-1,3.5)      .. (-1,4.5)             \n" + 
            "             ..  controls (-1,6)     and   (-0.5,6)      .. (-0.5,7.25);    \n" + 
            "       \\end{scope}  \n" + 
            "\\end{tikzpicture}\n" + 
            "\n" + 
            "\\end{document} ";

    /**
     * You'll need to import "graphicx, qrcode, relsize, hyperref".
     */
    public static final String QR_SCRIPT_COMMANDS = "" + 
            "\\newcommand{\\xwiz}{\\textlarger{\\mbox{XWizard}}}\n" + 
            "\\newcommand{\\wizzURLShort}{www.xwizard.de}\n" + 
            "\\newcommand{\\wizzURLForQRCode}{http://\\wizzURLShort:8080/Wizz}\n" + 
            "\\newcommand{\\wizzURLScriptPrefix}{?lang=ger\\&hide=.i2\\&template=}\n" + 
            "\\newcommand{\\wizzURLCompleteInfoII}{\\wizzURLForQRCode\\wizzURLScriptPrefix ID-2611}\n" + 
            "\\newcommand{\\vfpDownloadURL}{https://sourceforge.net/projects/easyagentsimulation}\n" + 
            "\\newcommand{\\wizzEnglishHelpURL}{http://www.xwizard.de:8080/Wizz?help&lang=eng&hide}\n" + 
            "\\newcommand{\\wizzGermanHelpURL}{http://www.xwizard.de:8080/Wizz?help&lang=ger&hide}\n" + 
            "\\newcommand{\\completeURL}[1]{\\wizzURLForQRCode\\wizzURLScriptPrefix#1\\#Output}\n" + 
            "\\newcommand{\\skriptFancy}{\\sffamily\\scshape Skript}\n" + 
            "\n" + 
            "\\newcommand{\\myincludegraphics}[2][]{\n" + 
            "    \\begin{center}\n" + 
            "        \\includegraphics[#1]{#2}\n" + 
            "    \\end{center}\n" + 
            "}\n" + 
            "\n" + 
            "\\newcommand{\\xscriptBasic}[1]{\\href{\\completeURL{#1}}{\\skriptFancy\\ \\ \\small#1}}\n" + 
            "\n" + 
            "\\newcommand{\\xscriptShortPlain}[2][]{\n" + 
            "    \\setlength{\\fboxrule}{0pt}%\n" + 
            "    \\fbox{\n" + 
            "        \\begin{minipage}{3.5cm}\n" + 
            "            \\centering\n" + 
            "            \\sffamily\\scshape \\xscriptBasic{#2}\n" + 
            "            \\bigbreak \n" + 
            "            \\qrcode[height=3cm]{\\completeURL{#2}}\n" + 
            "        \\end{minipage}\n" + 
            "    }\n" + 
            "}\n" + 
            "\n" + 
            "\\newcommand{\\xscriptShort}[2][]{\n" + 
            "    \\setlength{\\fboxrule}{0pt}%\n" + 
            "    \\xscriptShortPlain[#1]{#2}\n" + 
            "}\n" + 
            "\n" + 
            "\\newcommand{\\scriptImg}[3][]{\n" + 
            "    \\bigbreak\n" + 
            "    \\begin{minipage}{\\textwidth}\n" + 
            "        \\myincludegraphics[#1]{#2}\n" + 
            "        \\xscriptShort{#3}\n" + 
            "    \\end{minipage}\n" + 
            "    \\bigbreak\n" + 
            "}\n" + 
            "";
    
    public static String DREIECKS_TABELLE() {
        return DREIECKS_TABELLE(-1, -1);
    }
    
    public static String DREIECKS_TABELLE(int widthCM, int heightCM) {
        return "\\usepackage{etoolbox}\n" +
            (widthCM < 0 || heightCM < 0 ? "" : "\\usepackage[paperwidth=" + widthCM + "cm, paperheight=" + heightCM + "cm]{geometry}\n") + 
            "\\newenvironment{mytabular}[2][]{\n" + 
            "\\begin{center}\n" + 
            "\\begin{tabular}[#1]{#2}\n" + 
            "}\n" + 
            "{\n" + 
            "\\end{tabular}\n" + 
            "\\end{center}\n" + 
            "}\n" + 
            "\n" + 
            "\\def\\dreieckstabelle#1#2#3#4#5#6#7#8#9{%\n" + 
            "  \\def\\ArgI{#1}%\n" + 
            "  \\def\\ArgII{#2}%\n" + 
            "  \\def\\ArgIII{#3}%\n" + 
            "  \\def\\ArgIV{#4}%\n" + 
            "  \\def\\ArgV{#5}%\n" + 
            "  \\def\\ArgVI{#6}%\n" + 
            "  \\def\\ArgVII{#7}%\n" + 
            "  \\def\\ArgVIII{#8}%\n" + 
            "  \\def\\ArgIX{#9}%\n" + 
            "  \\dreckstabelle\n" + 
            "}\n" + 
            "\n" + 
            "\\def\\dreckstabelle#1#2#3#4#5#6#7#8#9{\n" + 
            "   \\ifdefempty{\\ArgIII}{\\begin{mytabular}{|r|*{2}{c|}}} % Starte Tabelle.\n" + 
            "    {\\ifdefempty{\\ArgIV}{\\begin{mytabular}{|r|*{3}{c|}}}\n" + 
            "    {\\ifdefempty{\\ArgV}{\\begin{mytabular}{|r|*{4}{c|}}}\n" + 
            "    {\\ifdefempty{\\ArgVI}{\\begin{mytabular}{|r|*{5}{c|}}}\n" + 
            "    {\\ifdefempty{\\ArgVII}{\\begin{mytabular}{|r|*{6}{c|}}}\n" + 
            "    {\\ifdefempty{\\ArgVIII}{\\begin{mytabular}{|r|*{7}{c|}}}\n" + 
            "    {\\ifdefempty{\\ArgIX}{\\begin{mytabular}{|r|*{8}{c|}}}\n" + 
            "    {\\ifstrempty{#1}{\\begin{mytabular}{|r|*{9}{c|}}}\n" + 
            "    {\\ifstrempty{#2}{\\begin{mytabular}{|r|*{10}{c|}}}\n" + 
            "    {\\ifstrempty{#3}{\\begin{mytabular}{|r|*{11}{c|}}}\n" + 
            "    {\\ifstrempty{#4}{\\begin{mytabular}{|r|*{12}{c|}}}\n" + 
            "    {\\ifstrempty{#5}{\\begin{mytabular}{|r|*{13}{c|}}}\n" + 
            "    {\\ifstrempty{#6}{\\begin{mytabular}{|r|*{14}{c|}}}\n" + 
            "    {\\ifstrempty{#7}{\\begin{mytabular}{|r|*{15}{c|}}}\n" + 
            "    {\\ifstrempty{#8}{\\begin{mytabular}{|r|*{16}{c|}}}\n" + 
            "    {\\ifstrempty{#9}{\\begin{mytabular}{|r|*{17}{c|}}}\n" + 
            "    {                \\begin{mytabular}{|r|*{18}{c|}}}}}}}}}}}}}}}}}}\n" + 
            "    \n" + 
            "     \\cline{1-2} \\ArgI \\\\ \\ifdefempty{\\ArgIII}{\\cline{1-2} \\multicolumn{1}{c|}{~} \\ArgII \\\\ \\cline{2-2}} % Inhaltszeilen.\n" + 
            "    {\\cline{1-3} \\ArgII \\\\ \\ifdefempty{\\ArgIV}{\\cline{1-3} \\multicolumn{1}{c|}{~} \\ArgIII \\\\ \\cline{2-3}}\n" + 
            "    {\\cline{1-4} \\ArgIII \\\\ \\ifdefempty{\\ArgV}{\\cline{1-4} \\multicolumn{1}{c|}{~} \\ArgIV \\\\ \\cline{2-4}}\n" + 
            "    {\\cline{1-5} \\ArgIV \\\\ \\ifdefempty{\\ArgVI}{\\cline{1-5} \\multicolumn{1}{c|}{~} \\ArgV \\\\ \\cline{2-5}}\n" + 
            "    {\\cline{1-6} \\ArgV \\\\ \\ifdefempty{\\ArgVII}{\\cline{1-6} \\multicolumn{1}{c|}{~} \\ArgVI \\\\ \\cline{2-6}}\n" + 
            "    {\\cline{1-7} \\ArgVI \\\\ \\ifdefempty{\\ArgVIII}{\\cline{1-7} \\multicolumn{1}{c|}{~} \\ArgVII \\\\ \\cline{2-7}}\n" + 
            "    {\\cline{1-8} \\ArgVII \\\\ \\ifdefempty{\\ArgIX}{\\cline{1-8} \\multicolumn{1}{c|}{~} \\ArgVIII \\\\ \\cline{2-8}}\n" + 
            "    {\\cline{1-9} \\ArgVIII \\\\ \\ifstrempty{#1}{\\cline{1-9} \\multicolumn{1}{c|}{~} \\ArgIX \\\\ \\cline{2-9}}\n" + 
            "    {\\cline{1-10} \\ArgIX \\\\ \\ifstrempty{#2}{\\cline{1-10} \\multicolumn{1}{c|}{~} #1 \\\\ \\cline{2-10}}\n" + 
            "    {\\cline{1-11} #1 \\\\ \\ifstrempty{#3}{\\cline{1-11} \\multicolumn{1}{c|}{~} #2 \\\\ \\cline{2-11}}\n" + 
            "    {\\cline{1-12} #2 \\\\ \\ifstrempty{#4}{\\cline{1-12} \\multicolumn{1}{c|}{~} #3 \\\\ \\cline{2-12}}\n" + 
            "    {\\cline{1-13} #3 \\\\ \\ifstrempty{#5}{\\cline{1-13} \\multicolumn{1}{c|}{~} #4 \\\\ \\cline{2-13}}\n" + 
            "    {\\cline{1-14} #4 \\\\ \\ifstrempty{#6}{\\cline{1-14} \\multicolumn{1}{c|}{~} #5 \\\\ \\cline{2-14}}\n" + 
            "    {\\cline{1-15} #5 \\\\ \\ifstrempty{#7}{\\cline{1-15} \\multicolumn{1}{c|}{~} #6 \\\\ \\cline{2-15}}\n" + 
            "    {\\cline{1-16} #6 \\\\ \\ifstrempty{#8}{\\cline{1-16} \\multicolumn{1}{c|}{~} #7 \\\\ \\cline{2-16}}\n" + 
            "    {\\cline{1-17} #7 \\\\ \\ifstrempty{#9}{\\cline{1-17} \\multicolumn{1}{c|}{~} #8 \\\\ \\cline{2-17}}\n" + 
            "    {\\cline{1-18} #8 \\\\                 \\cline{1-18} \\multicolumn{1}{c|}{~} #9 \\\\ \\cline{2-18}}}}}}}}}}}}}}}}}\n" + 
            "    \n" + 
            "    \\end{mytabular} % Beende Tabelle.\n" + 
            "}\n" + 
            "\n" + 
            "\\def\\dreieckstabelleReverse#1#2#3#4#5#6#7#8#9{%\n" + 
            "  \\def\\argI{#1}%\n" + 
            "  \\def\\argII{#2}%\n" + 
            "  \\def\\argIII{#3}%\n" + 
            "  \\def\\argIV{#4}%\n" + 
            "  \\def\\argV{#5}%\n" + 
            "  \\def\\argVI{#6}%\n" + 
            "  \\def\\argVII{#7}%\n" + 
            "  \\def\\argVIII{#8}%\n" + 
            "  \\def\\argIX{#9}%\n" + 
            "  \\dreckstabelleReverse\n" + 
            "}\n" + 
            "\n" + 
            "\\newcounter{rowsCount}\n" + 
            "\\def\\dreckstabelleReverse#1#2#3#4#5#6#7#8#9{\n" + 
            "   \\ifdefempty{\\argIII}{\\setcounter{rowsCount}{3}} % Z\\\"ahle Zeilen + 1.\n" + 
            "    {\\ifdefempty{\\argIV}{\\setcounter{rowsCount}{4}}\n" + 
            "    {\\ifdefempty{\\argV}{\\setcounter{rowsCount}{5}}\n" + 
            "    {\\ifdefempty{\\argVI}{\\setcounter{rowsCount}{6}}\n" + 
            "    {\\ifdefempty{\\argVII}{\\setcounter{rowsCount}{7}}\n" + 
            "    {\\ifdefempty{\\argVIII}{\\setcounter{rowsCount}{8}}\n" + 
            "    {\\ifdefempty{\\argIX}{\\setcounter{rowsCount}{9}}\n" + 
            "    {\\ifstrempty{#1}{\\setcounter{rowsCount}{10}}\n" + 
            "    {\\ifstrempty{#2}{\\setcounter{rowsCount}{11}}\n" + 
            "    {\\ifstrempty{#3}{\\setcounter{rowsCount}{12}}\n" + 
            "    {\\ifstrempty{#4}{\\setcounter{rowsCount}{13}}\n" + 
            "    {\\ifstrempty{#5}{\\setcounter{rowsCount}{14}}\n" + 
            "    {\\ifstrempty{#6}{\\setcounter{rowsCount}{15}}\n" + 
            "    {\\ifstrempty{#7}{\\setcounter{rowsCount}{16}}\n" + 
            "    {\\ifstrempty{#8}{\\setcounter{rowsCount}{17}}\n" + 
            "    {\\ifstrempty{#9}{\\setcounter{rowsCount}{18}}\n" + 
            "    {                \\setcounter{rowsCount}{19}}}}}}}}}}}}}}}}}\n" + 
            "\n" + 
            "   \\ifdefempty{\\argIII}{\\begin{mytabular}{|r|*{2}{c|}}} % Starte Tabelle.\n" + 
            "    {\\ifdefempty{\\argIV}{\\begin{mytabular}{|r|*{3}{c|}}}\n" + 
            "    {\\ifdefempty{\\argV}{\\begin{mytabular}{|r|*{4}{c|}}}\n" + 
            "    {\\ifdefempty{\\argVI}{\\begin{mytabular}{|r|*{5}{c|}}}\n" + 
            "    {\\ifdefempty{\\argVII}{\\begin{mytabular}{|r|*{6}{c|}}}\n" + 
            "    {\\ifdefempty{\\argVIII}{\\begin{mytabular}{|r|*{7}{c|}}}\n" + 
            "    {\\ifdefempty{\\argIX}{\\begin{mytabular}{|r|*{8}{c|}}}\n" + 
            "    {\\ifstrempty{#1}{\\begin{mytabular}{|r|*{9}{c|}}}\n" + 
            "    {\\ifstrempty{#2}{\\begin{mytabular}{|r|*{10}{c|}}}\n" + 
            "    {\\ifstrempty{#3}{\\begin{mytabular}{|r|*{11}{c|}}}\n" + 
            "    {\\ifstrempty{#4}{\\begin{mytabular}{|r|*{12}{c|}}}\n" + 
            "    {\\ifstrempty{#5}{\\begin{mytabular}{|r|*{13}{c|}}}\n" + 
            "    {\\ifstrempty{#6}{\\begin{mytabular}{|r|*{14}{c|}}}\n" + 
            "    {\\ifstrempty{#7}{\\begin{mytabular}{|r|*{15}{c|}}}\n" + 
            "    {\\ifstrempty{#8}{\\begin{mytabular}{|r|*{16}{c|}}}\n" + 
            "    {\\ifstrempty{#9}{\\begin{mytabular}{|r|*{17}{c|}}}\n" + 
            "    {                \\begin{mytabular}{|r|*{18}{c|}}}}}}}}}}}}}}}}}}\n" + 
            "    \n" + 
            "     \\ifdefempty{\\argIII}{\\cline{2-2} \\multicolumn{1}{c|}{~} \\argI \\\\ \\cline{1-2}} % Erste Zeile.\n" + 
            "    {\\ifdefempty{\\argIV}{\\cline{2-3} \\multicolumn{1}{c|}{~} \\argI \\\\ \\cline{1-3}}\n" + 
            "    {\\ifdefempty{\\argV}{\\cline{2-4} \\multicolumn{1}{c|}{~} \\argI \\\\ \\cline{1-4}}\n" + 
            "    {\\ifdefempty{\\argVI}{\\cline{2-5} \\multicolumn{1}{c|}{~} \\argI \\\\ \\cline{1-5}}\n" + 
            "    {\\ifdefempty{\\argVII}{\\cline{2-6} \\multicolumn{1}{c|}{~} \\argI \\\\ \\cline{1-6}}\n" + 
            "    {\\ifdefempty{\\argVIII}{\\cline{2-7} \\multicolumn{1}{c|}{~} \\argI \\\\ \\cline{1-7}}\n" + 
            "    {\\ifdefempty{\\argIX}{\\cline{2-8} \\multicolumn{1}{c|}{~} \\argI \\\\ \\cline{1-8}}\n" + 
            "    {\\ifstrempty{#1}{\\cline{2-9} \\multicolumn{1}{c|}{~} \\argI \\\\ \\cline{1-9}}\n" + 
            "    {\\ifstrempty{#2}{\\cline{2-10} \\multicolumn{1}{c|}{~} \\argI \\\\ \\cline{1-10}}\n" + 
            "    {\\ifstrempty{#3}{\\cline{2-11} \\multicolumn{1}{c|}{~} \\argI \\\\ \\cline{1-11}}\n" + 
            "    {\\ifstrempty{#4}{\\cline{2-12} \\multicolumn{1}{c|}{~} \\argI \\\\ \\cline{1-12}}\n" + 
            "    {\\ifstrempty{#5}{\\cline{2-13} \\multicolumn{1}{c|}{~} \\argI \\\\ \\cline{1-13}}\n" + 
            "    {\\ifstrempty{#6}{\\cline{2-14} \\multicolumn{1}{c|}{~} \\argI \\\\ \\cline{1-14}}\n" + 
            "    {\\ifstrempty{#7}{\\cline{2-15} \\multicolumn{1}{c|}{~} \\argI \\\\ \\cline{1-15}}\n" + 
            "    {\\ifstrempty{#8}{\\cline{2-16} \\multicolumn{1}{c|}{~} \\argI \\\\ \\cline{1-16}}\n" + 
            "    {\\ifstrempty{#9}{\\cline{2-17} \\multicolumn{1}{c|}{~} \\argI \\\\ \\cline{1-17}}\n" + 
            "    {                \\cline{2-18} \\multicolumn{1}{c|}{~} \\argI \\\\ \\cline{1-18}}}}}}}}}}}}}}}}}\n" + 
            "\n" + 
            "     \\addtocounter{rowsCount}{-1} \\argII \\\\ \\cline{1-\\value{rowsCount}}\\ifdefempty{\\argIII}{} % Inhaltszeilen.\n" + 
            "    {\\addtocounter{rowsCount}{-1} \\argIII \\\\ \\cline{1-\\value{rowsCount}}\\ifdefempty{\\argIV}{}\n" + 
            "    {\\addtocounter{rowsCount}{-1} \\argIV \\\\ \\cline{1-\\value{rowsCount}}\\ifdefempty{\\argV}{}\n" + 
            "    {\\addtocounter{rowsCount}{-1} \\argV \\\\ \\cline{1-\\value{rowsCount}}\\ifdefempty{\\argVI}{}\n" + 
            "    {\\addtocounter{rowsCount}{-1} \\argVI \\\\ \\cline{1-\\value{rowsCount}}\\ifdefempty{\\argVII}{}\n" + 
            "    {\\addtocounter{rowsCount}{-1} \\argVII \\\\ \\cline{1-\\value{rowsCount}}\\ifdefempty{\\argVIII}{}\n" + 
            "    {\\addtocounter{rowsCount}{-1} \\argVIII \\\\ \\cline{1-\\value{rowsCount}}\\ifdefempty{\\argIX}{}\n" + 
            "    {\\addtocounter{rowsCount}{-1} \\argIX \\\\ \\cline{1-\\value{rowsCount}}\\ifstrempty{#1}{}\n" + 
            "    {\\addtocounter{rowsCount}{-1} #1 \\\\ \\cline{1-\\value{rowsCount}}\\ifstrempty{#2}{}\n" + 
            "    {\\addtocounter{rowsCount}{-1} #2 \\\\ \\cline{1-\\value{rowsCount}}\\ifstrempty{#3}{}\n" + 
            "    {\\addtocounter{rowsCount}{-1} #3 \\\\ \\cline{1-\\value{rowsCount}}\\ifstrempty{#4}{}\n" + 
            "    {\\addtocounter{rowsCount}{-1} #4 \\\\ \\cline{1-\\value{rowsCount}}\\ifstrempty{#5}{}\n" + 
            "    {\\addtocounter{rowsCount}{-1} #5 \\\\ \\cline{1-\\value{rowsCount}}\\ifstrempty{#6}{}\n" + 
            "    {\\addtocounter{rowsCount}{-1} #6 \\\\ \\cline{1-\\value{rowsCount}}\\ifstrempty{#7}{}\n" + 
            "    {\\addtocounter{rowsCount}{-1} #7 \\\\ \\cline{1-\\value{rowsCount}}\\ifstrempty{#8}{}\n" + 
            "    {\\addtocounter{rowsCount}{-1} #8 \\\\ \\cline{1-\\value{rowsCount}}\\ifstrempty{#9}{}\n" + 
            "    {\\addtocounter{rowsCount}{-1} #9 \\\\ \\cline{1-\\value{rowsCount}}}}}}}}}}}}}}}}}}\n" + 
            "\n" + 
            "    \\end{mytabular} % Beende Tabelle.\n" + 
            "}";
    }

    private static String DOCCLASS_CROP_PAGE_PREVIEW(int borderPoints) {
        return "\n\\documentclass[varwidth, border=" + borderPoints + "pt]{standalone}\n";
    }
    
    private static final String DOCCLASS_STANDARD = "\n\\documentclass{article}\n";
    private static final String DOCCLASS_CROP_PAGE = "\n\\documentclass[tightpage]{standalone}\n";
    private static final String DOCCLASS_CROP_PAGE_PREVIEW = DOCCLASS_CROP_PAGE_PREVIEW(15);
    private static final String DOCCLASS_CROP_PAGE_PREVIEW_MAXDIM = "\n\\documentclass[varwidth=\\maxdimen, border=15pt]{standalone}\n";

    public static final String GRAPHICS_PACKAGES = 
            "\n\\RequirePackage{graphicx}\n"
            + "\\RequirePackage[space]{grffile}\n";
    
    public static final String PREAMBLE_CROP_PAGE = DOCCLASS_CROP_PAGE
            + "\\usepackage{url}\n" 
            + "\\usepackage{varwidth}\n" 
            + "\\usepackage{amsmath}\n" 
            + GRAPHICS_PACKAGES
            + "\\begin{document}\n";
    
    public static final String SHORT_CROP_PAGE_PREVIEV = "%varm|gra|ger|\\usepackage[table]{xcolor}|\\usepackage{url}%";
    
    public static final String PREAMBLE_CROP_PAGE_PREVIEW = DOCCLASS_CROP_PAGE_PREVIEW
            + "\\usepackage{url}\n" 
            + "\\usepackage{varwidth}\n" 
            + "\\usepackage{amsmath}\n" 
            + "\\usepackage[table]{xcolor}"
            + GRAPHICS_PACKAGES
            + "\\begin{document}\n";

    public static final String PREAMBLE_CROP_PAGE_PREVIEW_MAXDIM = DOCCLASS_CROP_PAGE_PREVIEW_MAXDIM
            + "\\usepackage{url}\n" 
            + "\\usepackage{varwidth}\n" 
            + "\\usepackage{amsmath}\n" 
            + "\\usepackage[table]{xcolor}"
            + GRAPHICS_PACKAGES
            + "\\begin{document}\n";

    public static final String DRECKS_TABELLE_PACKAGES = 
            "\\usepackage{url}\n" 
            + "\\usepackage{varwidth}\n" 
            + "\\usepackage{amsmath}\n" 
            + "\\usepackage[table]{xcolor}"
            + GRAPHICS_PACKAGES;
    
    public static final String PREAMBLE_CROP_PAGE_PREVIEW_WITH_TRIANGLE_TAB = DOCCLASS_CROP_PAGE_PREVIEW
            + DRECKS_TABELLE_PACKAGES
            + LaTeXCommands.DREIECKS_TABELLE()
            + "\\begin{document}\n";
    
    public static final String PREAMBLE_STANDARD = DOCCLASS_STANDARD
            + "\\usepackage{amsmath}\n"
            + GRAPHICS_PACKAGES
            + "\\begin{document}\n";

    public static final String JK_FLIPFLOP_COMMAND = "\\def\\JKFF(#1)#2#3{%\n" + 
            "  \\begin{scope}[shift={(#1)}]\n" + 
            "    \\draw (0,0) rectangle (1,1);\n" + 
            "    \\draw (0.5,1) -- (0.5,0);\n" + 
            "    \\draw (0.5,0.5) -- (1,0.5);\n" + 
            "    \\node at (0.75,0.75) {$#3$};\n" + 
            "    \\node at (0.75,0.25) {$\\overline{#3}$};\n" + 
            "    \\draw (1,0.8) -- +(0.25,0) coordinate (#2 east);\n" + 
            "    \\draw (0,0.2) node[right] {$K$} -- +(-0.25,0) coordinate (#2 3);\n" + 
            "    \\draw (0,0.5) node[right] {$T$} -- +(-0.25,0) coordinate (#2 2);\n" + 
            "    \\draw (0,0.8) node[right] {$J$} -- +(-0.25,0) coordinate (#2 1);\n" + 
            "  \\end{scope}\n" + 
            "}";

    public static final String RS_FLIPFLOP_COMMAND = "\\def\\RSFF(#1)#2#3{%\n" + 
            "  \\begin{scope}[shift={(#1)}]\n" + 
            "    \\draw (0,0) rectangle (1,1);\n" + 
            "    \\draw (0.5,1) -- (0.5,0);\n" + 
            "    \\draw (0.5,0.5) -- (1,0.5);\n" + 
            "    \\node at (0.75,0.75) {$#3$};\n" + 
            "    \\node at (0.75,0.25) {$\\overline{#3}$};\n" + 
            "    \\draw (1,0.8) -- +(0.25,0) coordinate (#2 east);\n" + 
            "    \\draw (0,0.2) node[right] {$R$} -- +(-0.25,0) coordinate (#2 3);\n" + 
            "    \\draw (0,0.5) node[right] {$T$} -- +(-0.25,0) coordinate (#2 2);\n" + 
            "    \\draw (0,0.8) node[right] {$S$} -- +(-0.25,0) coordinate (#2 1);\n" + 
            "  \\end{scope}\n" + 
            "}";

    public static final String PREAMBLE_TIKZ_EUR = DOCCLASS_CROP_PAGE
            + "\\usepackage{tikz}\n" + 
            "\\usetikzlibrary{circuits.logic.IEC}\n" + 
            "\n" + 
            "\\begin{document}\n" +
            JK_FLIPFLOP_COMMAND +
            RS_FLIPFLOP_COMMAND +
            "\\pagestyle{empty}\n" + 
            "\\begin{tikzpicture}[circuit logic IEC, every circuit symbol/.style={}]"
            ;

    public static final String PREAMBLE_TIKZ_USA = DOCCLASS_CROP_PAGE
            + "\\usepackage{tikz}\n" + 
            "\\usetikzlibrary{circuits.logic.CDH}\n" + 
            "\n" + 
            "\\begin{document}\n" + 
            JK_FLIPFLOP_COMMAND +
            RS_FLIPFLOP_COMMAND +
            "\\pagestyle{empty}\n" + 
            "\\begin{tikzpicture}[circuit logic CDH, every circuit symbol/.style={}]"
            ;
    
    /**
     * When you're using this, you will probably want to append the
     * exercise string after it. Something like this:
     * {@code + this.getUpperClassDeclarationsBlockOnly()}.
     */
    public static final String POSTAMBLE_STANDARD = "\n\\end{document}";
    
    public static final String POSTAMBLE_TIKZ = "\\end{tikzpicture}"
            + POSTAMBLE_STANDARD;
    
    public static String formulaModeOptions(RepresentableDefault caller) {
        return RepresentableDefault.DECL_BEG_TAG + "\n"
                + "formulaMode=true;\n"
                + caller.getExerciseStringForDeclarations()
                + "\n" + RepresentableDefault.DECL_END_TAG;
    }
    
    public static final String PLACE_HOLDER_INLINES_CRIPT_BEGIN_TAG = "${@XX-BEGIN-XX@}$";
    public static final String PLACE_HOLDER_INLINES_CRIPT_END_TAG = "${@XX-END-XX@}$";
    
    public static String distributeOnTabular(double num, double den, Object... object) {
        String string = "\\begin{tabular}";
        double factor = Math.sqrt(object.length / (num * den));

        int cols = (int) Math.ceil(factor * num);

        string += "{";
        
        for (int i = 0; i < cols; i++) {
            string += "c";
        }

        string += "}\n";
        
        int count = 1;
        for (Object cell2 : object) {
            String cell = cell2.toString();
            
            String firstPart = RepresentableDefault.INSCR_BEG_TAG_FOR_INTERNAL_USAGE
                                    + "-"
                                    + 1
                                    + "|";
            String secondPart = RepresentableDefault.INSCR_END_TAG_FOR_INTERNAL_USAGE;
            
            cell = cell.replace(PLACE_HOLDER_INLINES_CRIPT_BEGIN_TAG, firstPart);
            cell = cell.replace(PLACE_HOLDER_INLINES_CRIPT_END_TAG, secondPart);
            
            if (count == cols) {
                string += cell + "\\\\\n";
                count = 1;
            } else {
                string += cell + " & ";
                count++;
            }
        }
        
        
        return string + "\n\\end{tabular}";
    }
    
    public static String center(String string) {
        return "\\begin{center}\n" + string + "\n\\end{center}";
    }
    
    private static HashMap<String, String> standardPreambles = new HashMap<>();
    private static HashMap<String, String> standardPostambles = new HashMap<>();
    private static HashMap<String, String> standardPackages = new HashMap<>();
    
    private static void putStandard(String name, String pre, String post) {
        standardPreambles.put(name, pre);
        standardPostambles.put(name, post);
    }
    
    static {
        putStandard("var", 
                DOCCLASS_CROP_PAGE_PREVIEW + "\\usepackage{varwidth}", 
                "\n\\end{document}");
        
        putStandard("var10", 
                DOCCLASS_CROP_PAGE_PREVIEW(10) + "\\usepackage{varwidth}", 
                "\n\\end{document}");

        putStandard("var25", 
                DOCCLASS_CROP_PAGE_PREVIEW(25) + "\\usepackage{varwidth}", 
                "\n\\end{document}");

        putStandard("var50", 
                DOCCLASS_CROP_PAGE_PREVIEW(25) + "\\usepackage{varwidth}", 
                "\n\\end{document}");

        putStandard("var100", 
                DOCCLASS_CROP_PAGE_PREVIEW(25) + "\\usepackage{varwidth}", 
                "\n\\end{document}");

        putStandard("varm", 
                DOCCLASS_CROP_PAGE_PREVIEW_MAXDIM + "\\usepackage{varwidth}", 
                "\n\\end{document}");

        putStandard(
                "tight", 
                "\\documentclass[tightpage]{standalone}",
                "\n\\end{document}");
        
        putStandard(
                "artlet", 
                "\\documentclass[letter]{article}",
                "\n\\end{document}");

        standardPackages.put("dreck", DRECKS_TABELLE_PACKAGES
                + LaTeXCommands.DREIECKS_TABELLE());
        standardPackages.put("etoolbox", "\n\\usepackage{etoolbox}");
        standardPackages.put("ulem", "\n\\usepackage{ulem}");
        standardPackages.put("ams", "\n\\usepackage{amsmath,amsfonts,amssymb}");
        standardPackages.put("qrcode", "\n\\usepackage{qrcode}");
        standardPackages.put("url", "\n\\usepackage{url}");
        standardPackages.put("hyperref", "\n\\usepackage{hyperref}");
        standardPackages.put("relsize", "\n\\usepackage{relsize}");
        standardPackages.put("geo", "\n\\usepackage[a3paper, margin=1in]{geometry}\n\\pagestyle{empty}");
        standardPackages.put("gra", GRAPHICS_PACKAGES);
        standardPackages.put("fancyvrb", "\\usepackage{fancyvrb}");
        standardPackages.put("fontawesome", "\\usepackage{fontawesome}");
        standardPackages.put("bbding", "\\usepackage{bbding}");
        standardPackages.put("ger", 
                "\n\\usepackage[ngerman]{babel}"
                + "\n\\usepackage[utf8]{inputenc}"
                + "\n\\usepackage[T1]{fontenc}");
        standardPackages.put("loop", "\n\\usepackage{forloop}");
    }
    
    private static String getStandardPreamble(String name) {
        return standardPreambles.get(name);
    }

    private static String getStandardPostamble(String name) {
        return standardPostambles.get(name);
    }
    
    private static String getPackages(String... packs) {
        String packages = "";
        
        for (String pack : packs) {
            if (standardPackages.containsKey(pack)) {
                packages += standardPackages.get(pack);
            } else {
                packages += pack;
            }
        }
        
        return packages;
    }

    /**
     * @param plainLatexDocumentCode  Just the main code between 
     *                                \begin{document} and \end{document}
     * @param conv                    The code specifying the embedding.
     * 
     * @return  A valid Latex document as specified.
     */
    public static String embedLatexCode(String plainLatexDocumentCode, String conv) {
        String[] split = conv.split("\\|");
        String base = StaticMethods.removeWhitespaces(split[0]);
        String[] packages = new String[split.length - 1];
        
        String newCode = plainLatexDocumentCode.substring(conv.length() + 2);
        String pre = LaTeXCommands.getStandardPreamble(base);
        String post = LaTeXCommands.getStandardPostamble(base);
        
        if (pre == null || post == null) {
            throw new RuntimeException("Latex pre/post '" + base + "' not found.");
        }
        
        for (int i = 1; i < split.length; i++) {
            packages[i - 1] = StaticMethods.removeWhitespaces(split[i]);
        }
        String packs = LaTeXCommands.getPackages(packages);
        
        newCode = (pre + packs + "\n\\begin{document}" + newCode + post).trim();

        return newCode;
    }
}
