/*
 * File name:        TestScripts.java (package veryFastPDF.script)
 * Author(s):        hq0976
 * Java version:     8.0 (at generation time)
 * Generation date:  18.03.2017 (08:50:34)
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

package veryFastPDF.script.testing;

/**
 * @author hq0976
 *
 */
public class TestScripts {

    public static final String[] TEST_STRINGS = new String[] {
            "latex:%varm|gra%\r\n" + 
            "\r\n" + 
            "@{\r\n" + 
            "z=@{5}@*\r\n" + 
            "seedBeg=@{27}@*\r\n" + 
            "seedEnd=@{31}@*\r\n" + 
            "stateRed=@{2}@\r\n" + 
            "}@.nil\r\n" + 
            "\r\n" + 
            "@{@{@{fsm:}@.randD[z, true, #i].minTable (seed: #i)}@.if[this.greq[@{fsm:}@.randD[z, true, #i].states, @{fsm:}@.randD[z, true, #i].min.states.add[stateRed]]]\r\n" + 
            "\r\n" + 
            "}@*.for[#i, seedBeg, seedEnd]",
            
            "latex:%artlet | gra | geo | ger%\n" + 
            "\\newcommand{\\basetext}[6]{\\newpage\\huge Zufaelliger EA mit $#1$ Zustaenden:\\\\ #2\\par Die deterministische Version hat $#3$ Zustaende:\\\\ #4\\par Die minimierte Version hat $#5$ Zustaende:\\\\ #6}\n" + 
            "@{\\basetext{@{x#v}@.states}{@{1|x#v}@}{@{y#v}@.states}{@{1|y#v}@}{@{z#v}@.states}{@{1|z#v}@}}@*.for[#v, 1, x1.inputLength.add[1]]\n" + 
            "\n" + 
            "--declarations--\n" + 
            "prep=#x1=@{fsm:}@**.randD[4,false,20].sim[ababab]#;\n" + 
            "prep=#y1=x1.det#;\n" + 
            "prep=#z1=y1.min#;\n" + 
            "@{prep=#xA=x~{A-1}~.sim#;\n" + 
            "prep=#yA=xA.det#;\n" + 
            "prep=#zA=yA.min#;}@**.for[A,2,x1.inputLength.add[1]]\n" + 
            "animate=#@{pageB->}@.for[B,1,x1.inputLength.add[1]]#;\n" + 
            "--declarations-end--",
            
            "fsm:\n" + 
            "(s0,a)=>s1;\n" + 
            "@{(s#v, a) => s~{#v-1}~ | s~{#v+1}~; (s~{#v-1}~,b)=>s#v;}@.for[#v, 1, @{fsm:}@.randD[3,false,0].states]\n" + 
            "\n" + 
            "--declarations--\n" + 
            "e=#n#;\n" + 
            "animate=this;\n" + 
            "simulateToStep=0;\n" + 
            "input=aabbaa;\n" + 
            "s0=s0;\n" + 
            "F=s0;\n" + 
            "displayMode=0;\n" + 
            "showMinimizedFSM=false;\n" + 
            "showDeterministicFSM=false\n" + 
            "--declarations-end--",
            
            "latex:%varm|gra%\n" + 
            "@{@{fsm: (s0, a) => sv;}@}@.for[v, 0, 2]\\par\n" + 
            "\\verb+@{fsm: (s0, a) => sv;}@.for[v, 0, 2]+\\par\n" + 
            "@{@{fsm: (s0, a) => sv;}@}@*.for[v, 0, 2]",
            
            "latex:\n" + 
            "\\documentclass[tightpage,preview]{standalone}\n" + 
            "\\usepackage{varwidth}\\usepackage{amsmath}\\usepackage[table]{xcolor}\\usepackage{graphicx}\\usepackage[space]{grffile}\n" + 
            "\\begin{document}\n" + 
            "\\huge~\\par\n" + 
            "Some of XWizard's basic object types:\n" + 
            "\\bigbreak\n" + 
            "\\begin{tabular}{|c|c|}\n" + 
            "\\hline\n" + 
            "All of it & FSM\\\\\n" + 
            "@{0.3|\n" + 
            "latex:\n" + 
            "\\documentclass[tightpage,preview]{standalone}\n" + 
            "\\usepackage{varwidth}\\usepackage{amsmath}\\usepackage[table]{xcolor}\\usepackage{graphicx}\\usepackage[space]{grffile}\n" + 
            "\\begin{document}\n" + 
            "\\huge~\\par\n" + 
            "Some of XWizard's basic object types:\n" + 
            "\\bigbreak\n" + 
            "\\begin{tabular}{|c|c|}\n" + 
            "\\hline\n" + 
            "Push-down automata & Finite state machines \\\\\n" + 
            "@{0.75|\n" + 
            "pda:\n" + 
            "(s1, 1, 0) => (s2, lambda);\n" + 
            "(s3, 1, b) => (s3, b1);\n" + 
            "(s3, 0, 1) => (s3, b);\n" + 
            "(s3, 0, b) => (s3, lambda);\n" + 
            "(s1, 0, 0) => (s1, 00);\n" + 
            "(s3, 1, 1) => (s3, 11);\n" + 
            "(s3, lambda, k) => (s0, k);\n" + 
            "(s1, lambda, k) => (s0, k);\n" + 
            "(s2, lambda, k) => (s3, bk);\n" + 
            "(s0, 1, k) => (s3, 1k);\n" + 
            "(s0, 0, k) => (s1, 0k);\n" + 
            "(s2, lambda, 0) => (s1, lambda);\n" + 
            "--declarations--\n" + 
            "e=#n#;\n" + 
            "s0=s0;\n" + 
            "F=s0;\n" + 
            "kSymb=k;\n" + 
            "inputs=000001010;\n" + 
            "simSteps=5\n" + 
            "--declarations-end--\n" + 
            "}@ &\n" + 
            "@{0.9|\n" + 
            "fsm:\n" + 
            "(s0, a) | (s3, a) | (s4, a) => s2;\n" + 
            "(s0, b) | (s3, b) => s1;\n" + 
            "(s1, a) => s0;\n" + 
            "(s1, b) | (s2, a) => s4;\n" + 
            "(s2, b) | (s4, b) => s3;\n" + 
            "--declarations--\n" + 
            "e=#n#;\n" + 
            "simulateToStep=1;\n" + 
            "input=abaabba;\n" + 
            "s0=s0;\n" + 
            "F=s0\n" + 
            "--declarations-end--\n" + 
            "}@ \\\\\n" + 
            "\\hline\n" + 
            "Turing machines & Grammars \\& Parsing \\\\\n" + 
            "@{0.75|\n" + 
            "turing:\n" + 
            "(s0, a) => (s2, a, R) | (s3, a, R);\n" + 
            "(s0, b) => (s1, b, R) | (s4, b, R);\n" + 
            "(s1, a) => (s2, a, R);\n" + 
            "(s1, b) => (s1, b, R);\n" + 
            "(s2, a) => (s1, a, R) | (s5, a, R);\n" + 
            "(s2, b) => (s2, b, R);\n" + 
            "(s3, a) => (s3, a, R);\n" + 
            "(s3, b) => (s4, b, R);\n" + 
            "(s4, a) => (s4, a, R);\n" + 
            "(s4, b) => (s3, b, R) | (s5, b, R);\n" + 
            "(s5, *) => (sf, *, N);\n" + 
            "--declarations--\n" + 
            "s0=s0;\n" + 
            "F=sf;\n" + 
            "blank=*;\n" + 
            "inputs=aabab;\n" + 
            "runStepsScript=120;\n" + 
            "shortTrace=false\n" + 
            "--declarations-end--\n" + 
            "}@ &\n" + 
            "@{1.5|\n" + 
            "grammar parse(a, a, <>, b, b, <>, a, a, <>, b, b)--48:\n" + 
            "S => a, S, b | <> | S, <>, S | a | b;\n" + 
            "--declarations--\n" + 
            "N=S,A;\n" + 
            "T=a,b,c;\n" + 
            "S=S;\n" + 
            "--declarations-end--\n" + 
            "}@ \\\\\n" + 
            "\\hline\n" + 
            "\\end{tabular}\n" + 
            "\\end{document}}@ &\n" + 
            "@{0.9|\n" + 
            "fsm:\n" + 
            "(s0, a) | (s3, a) | (s4, a) => s2;\n" + 
            "(s0, b) | (s3, b) => s1;\n" + 
            "(s1, a) => s0;\n" + 
            "(s1, b) | (s2, a) => s4;\n" + 
            "(s2, b) | (s4, b) => s3;\n" + 
            "--declarations--\n" + 
            "e=#n#;\n" + 
            "simulateToStep=1;\n" + 
            "input=abaabba;\n" + 
            "s0=s0;\n" + 
            "F=s0\n" + 
            "--declarations-end--\n" + 
            "}@ \\\\\n" + 
            "\\hline\n" + 
            "Turing machines & All of all of it \\\\\n" + 
            "@{0.75|\n" + 
            "turing:\n" + 
            "(s0, a) => (s2, a, R) | (s3, a, R);\n" + 
            "(s0, b) => (s1, b, R) | (s4, b, R);\n" + 
            "(s1, a) => (s2, a, R);\n" + 
            "(s1, b) => (s1, b, R);\n" + 
            "(s2, a) => (s1, a, R) | (s5, a, R);\n" + 
            "(s2, b) => (s2, b, R);\n" + 
            "(s3, a) => (s3, a, R);\n" + 
            "(s3, b) => (s4, b, R);\n" + 
            "(s4, a) => (s4, a, R);\n" + 
            "(s4, b) => (s3, b, R) | (s5, b, R);\n" + 
            "(s5, *) => (sf, *, N);\n" + 
            "--declarations--\n" + 
            "s0=s0;\n" + 
            "F=sf;\n" + 
            "blank=*;\n" + 
            "inputs=aabab;\n" + 
            "runStepsScript=120;\n" + 
            "shortTrace=false\n" + 
            "--declarations-end--\n" + 
            "}@ &\n" + 
            "@{0.5|\n" + 
            "latex:\n" + 
            "\\documentclass[tightpage,preview]{standalone}\n" + 
            "\\usepackage{varwidth}\\usepackage{amsmath}\\usepackage[table]{xcolor}\\usepackage{graphicx}\\usepackage[space]{grffile}\n" + 
            "\\begin{document}\n" + 
            "\\huge~\\par\n" + 
            "Some of XWizard's basic object types:\n" + 
            "\\bigbreak\n" + 
            "\\begin{tabular}{|c|c|}\n" + 
            "\\hline\n" + 
            "All of it & FSM\\\\\n" + 
            "@{0.3|\n" + 
            "latex:\n" + 
            "\\documentclass[tightpage,preview]{standalone}\n" + 
            "\\usepackage{varwidth}\\usepackage{amsmath}\\usepackage[table]{xcolor}\\usepackage{graphicx}\\usepackage[space]{grffile}\n" + 
            "\\begin{document}\n" + 
            "\\huge~\\par\n" + 
            "Some of XWizard's basic object types:\n" + 
            "\\bigbreak\n" + 
            "\\begin{tabular}{|c|c|}\n" + 
            "\\hline\n" + 
            "Push-down automata & Finite state machines \\\\\n" + 
            "@{0.75|\n" + 
            "pda:\n" + 
            "(s1, 1, 0) => (s2, lambda);\n" + 
            "(s3, 1, b) => (s3, b1);\n" + 
            "(s3, 0, 1) => (s3, b);\n" + 
            "(s3, 0, b) => (s3, lambda);\n" + 
            "(s1, 0, 0) => (s1, 00);\n" + 
            "(s3, 1, 1) => (s3, 11);\n" + 
            "(s3, lambda, k) => (s0, k);\n" + 
            "(s1, lambda, k) => (s0, k);\n" + 
            "(s2, lambda, k) => (s3, bk);\n" + 
            "(s0, 1, k) => (s3, 1k);\n" + 
            "(s0, 0, k) => (s1, 0k);\n" + 
            "(s2, lambda, 0) => (s1, lambda);\n" + 
            "--declarations--\n" + 
            "e=#n#;\n" + 
            "s0=s0;\n" + 
            "F=s0;\n" + 
            "kSymb=k;\n" + 
            "inputs=000001010;\n" + 
            "simSteps=5\n" + 
            "--declarations-end--\n" + 
            "}@ &\n" + 
            "@{0.9|\n" + 
            "fsm:\n" + 
            "(s0, a) | (s3, a) | (s4, a) => s2;\n" + 
            "(s0, b) | (s3, b) => s1;\n" + 
            "(s1, a) => s0;\n" + 
            "(s1, b) | (s2, a) => s4;\n" + 
            "(s2, b) | (s4, b) => s3;\n" + 
            "--declarations--\n" + 
            "e=#n#;\n" + 
            "simulateToStep=1;\n" + 
            "input=abaabba;\n" + 
            "s0=s0;\n" + 
            "F=s0\n" + 
            "--declarations-end--\n" + 
            "}@ \\\\\n" + 
            "\\hline\n" + 
            "Turing machines & Grammars \\& Parsing \\\\\n" + 
            "@{0.75|\n" + 
            "turing:\n" + 
            "(s0, a) => (s2, a, R) | (s3, a, R);\n" + 
            "(s0, b) => (s1, b, R) | (s4, b, R);\n" + 
            "(s1, a) => (s2, a, R);\n" + 
            "(s1, b) => (s1, b, R);\n" + 
            "(s2, a) => (s1, a, R) | (s5, a, R);\n" + 
            "(s2, b) => (s2, b, R);\n" + 
            "(s3, a) => (s3, a, R);\n" + 
            "(s3, b) => (s4, b, R);\n" + 
            "(s4, a) => (s4, a, R);\n" + 
            "(s4, b) => (s3, b, R) | (s5, b, R);\n" + 
            "(s5, *) => (sf, *, N);\n" + 
            "--declarations--\n" + 
            "s0=s0;\n" + 
            "F=sf;\n" + 
            "blank=*;\n" + 
            "inputs=aabab;\n" + 
            "runStepsScript=120;\n" + 
            "shortTrace=false\n" + 
            "--declarations-end--\n" + 
            "}@ &\n" + 
            "@{1.5|\n" + 
            "grammar parse(a, a, <>, b, b, <>, a, a, <>, b, b)--48:\n" + 
            "S => a, S, b | <> | S, <>, S | a | b;\n" + 
            "--declarations--\n" + 
            "N=S,A;\n" + 
            "T=a,b,c;\n" + 
            "S=S;\n" + 
            "--declarations-end--\n" + 
            "}@ \\\\\n" + 
            "\\hline\n" + 
            "\\end{tabular}\n" + 
            "\\end{document}}@ &\n" + 
            "@{0.9|\n" + 
            "fsm:\n" + 
            "(s0, a) | (s3, a) | (s4, a) => s2;\n" + 
            "(s0, b) | (s3, b) => s1;\n" + 
            "(s1, a) => s0;\n" + 
            "(s1, b) | (s2, a) => s4;\n" + 
            "(s2, b) | (s4, b) => s3;\n" + 
            "--declarations--\n" + 
            "e=#n#;\n" + 
            "simulateToStep=1;\n" + 
            "input=abaabba;\n" + 
            "s0=s0;\n" + 
            "F=s0\n" + 
            "--declarations-end--\n" + 
            "}@ \\\\\n" + 
            "\\hline\n" + 
            "Turing machines & Grammars \\& Parsing \\\\\n" + 
            "@{0.75|\n" + 
            "turing:\n" + 
            "(s0, a) => (s2, a, R) | (s3, a, R);\n" + 
            "(s0, b) => (s1, b, R) | (s4, b, R);\n" + 
            "(s1, a) => (s2, a, R);\n" + 
            "(s1, b) => (s1, b, R);\n" + 
            "(s2, a) => (s1, a, R) | (s5, a, R);\n" + 
            "(s2, b) => (s2, b, R);\n" + 
            "(s3, a) => (s3, a, R);\n" + 
            "(s3, b) => (s4, b, R);\n" + 
            "(s4, a) => (s4, a, R);\n" + 
            "(s4, b) => (s3, b, R) | (s5, b, R);\n" + 
            "(s5, *) => (sf, *, N);\n" + 
            "--declarations--\n" + 
            "s0=s0;\n" + 
            "F=sf;\n" + 
            "blank=*;\n" + 
            "inputs=aabab;\n" + 
            "runStepsScript=120;\n" + 
            "shortTrace=false\n" + 
            "--declarations-end--\n" + 
            "}@ &\n" + 
            "@{1.5|\n" + 
            "grammar parse(a, a, <>, b, b, <>, a, a, <>, b, b)--48:\n" + 
            "S => a, S, b | <> | S, <>, S | a | b;\n" + 
            "--declarations--\n" + 
            "N=S,A;\n" + 
            "T=a,b,c;\n" + 
            "S=S;\n" + 
            "--declarations-end--\n" + 
            "}@ \\\\\n" + 
            "\\hline\n" + 
            "\\end{tabular}\n" + 
            "\\end{document}\n" + 
            "--declarations--\n" + 
            "e=#n#;\n" + 
            "formulaMode=false\n" + 
            "--declarations-end--\n" + 
            "}@ \\\\\n" + 
            "\\hline\n" + 
            "\\end{tabular}\n" + 
            "\\end{document}\n" + 
            "--declarations--\n" + 
            "e=#n#;\n" + 
            "formulaMode=false\n" + 
            "--declarations-end--",
            
            "latex:%varm|gra%" +
            InscriptMethods.getMethod("fak") +
            "\n@{x=@{4}@}@.nil\n" + 
            "\n" + 
            "$@{x}@! = @{x}@.fak$",
            
            "latex:%varm|gra%\n" + 
            InscriptMethods.getMethod("fib") + 
            "@{beg=@{0}@*\r\n" + 
            "end=@{20}@*\r\n" + 
            "}@*.nil\r\n" + 
            "\r\n" + 
            "@{$fib(#i) = @{#i}@.fib$\\\\}@*.for[#i, beg, end]",
            
            "latex:%varm|gra%\n" + 
            InscriptMethods.getMethod("prime") +
            "\n" + 
            "begin=@{0}@*\n" + 
            "end=@{15}@*\n" + 
            "\n" + 
            "}@.nil\n" + 
            "\n" + 
            "@{Ist $#i$ Primzahl? -- @{#i}@.prime\\par }@*.for[#i, begin, end]",
            
            "latex:%varm|ams%\r\n" + 
            "\r\n" + 
            "@{\r\n" + 
            "x1=@{1}@*** y1=@{5}@***\r\n" + 
            "x2=@{90}@*** y2=@{100}@***\r\n" + 
            "op=@{%}@*****\r\n" + 
            "}@.nil\r\n" + 
            "\r\n" + 
            "\\begin{tabular}{c|@{c}@.for[#v, x1, y1]}\r\n" + 
            "\\verb#@{op}@****# @{& $#v$ }@.for[#v, x1, y1]\\\\\\hline\r\n" + 
            "@{$#b$ @{ & $~{#b@{op}@****#a}~$}@*.for[#a, x1, y1]\\\\}@**.for[#b, x2, y2]\r\n" + 
            "\\end{tabular}",
            
            "latex:%varm|gra%\r\n" + 
            "@{\r\n" + 
            "x=@{\r\n" + 
            "@(5)@\r\n" + 
            "@(false)@\r\n" + 
            "@(5)@\r\n" + 
            "}@\r\n" + 
            "}@.nil\r\n" + 
            "\r\n" + 
            "@{fsm:}@.randD[x.element[0], x.element[1], x.element[2]]",
            
            "latex:%varm|gra|ger|bbding%\r\n" + 
            "\r\n" + 
            "@{words=@{\r\n" + 
            "@(tikz)@\r\n" + 
            "@(ist)@\r\n" + 
            "@(keine)@\r\n" + 
            "@(Zeitersparnis!)@\r\n" + 
            "}@*}@.nil\r\n" + 
            "\r\n" + 
            "@{#0#=@{latex:%varm|gra|ger|bbding% {\\normalsize\\HandCuffRight}~~\\verb+#1#+}@}@****.newMethod[frame, 1]\r\n" + 
            "@{@{@{#1#$i}@.frame[@{#0#}@*.substring[0, $i]]}@**.for[$i, 1, @{#0#}@***.len]}@****.newMethod[wordFrames, 1]\r\n" + 
            "@{@{words.element[#i]}@.wordFrames[x#i]}@*.for[#i, 0, words.size.sub[1]]\r\n" + 
            "\r\n" + 
            "--declarations--\r\n" + 
            "animate=#@{@{xji->}@.for[i, 1, words.element[j].len]}@*.for[j, 0, words.size.sub[1]]#;\r\n" + 
            "--declarations-end--",
            
            "bdd: a,b,c,d,e: 011010011\n" + 
            "--declarations--\n" + 
            "e=#n#;\n" + 
            "prep=#x0=this.simp#;\n" + 
            "@{prep=#xA=x~{A-1}~.simp#;}@.for[A,1,this.max]\n" + 
            "animate=#@{xB->}@.for[B,0,this.max]#; \n" + 
            "simplifySteps=-1\n" + 
            "--declarations-end--",

//            "latex:%varm|gra%\\huge\r\n" + 
//            "@{\r\n" + 
//            "min=@{4}@*** max=@{5}@*** sim=@{abaaba}@*** len=@{sim}@***.len seed=@{1}@***\r\n" + 
//            "}@.nil\r\n" + 
//            "\r\n" + 
//            "@{\r\n" + 
//            "f#j=@{latex:%varm|gra%\r\n" + 
//            "\\begin{tabular}{c@{|c}@.for[#i, min, max]}\r\n" + 
//            "@{& x#i#j=@{fsm:}@.randD[#i, false, seed].sim[sim]@{.sim}@*.for[#k, 1, #j] }@**.for[#i, min, max] \\\\\r\n" + 
//            "\\hline\r\n" + 
//            "@{& @{@{x#i#j}@.det}@ }@**.for[#i, min, max] \\\\\r\n" + 
//            "\\hline\r\n" + 
//            "@{& @{@{x#i#j}@.det.min}@ }@**.for[#i, min, max]\r\n" + 
//            "\\end{tabular}}@}@***.for[#j, 0, len]\r\n" + 
//            "--declarations--\r\n" + 
//            "animate=f0@{->f#j}@.for[#j, 0, len]\r\n" + 
//            "--declarations-end--"
    };
}
