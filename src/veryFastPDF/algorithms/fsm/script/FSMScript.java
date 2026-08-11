/*
 * File name:        Script.java (package eas.fundamentalAlgorithms.graphBased.fsm.script)
 * Author(s):        Lukas König
 * Java version:     7.0
 * Generation date:  14.11.2013 (21:11:46)
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

package veryFastPDF.algorithms.fsm.script;

import java.util.LinkedList;

import eas.miscellaneous.StaticMethods;
import veryFastPDF.algorithms.fsm.FSM;
import veryFastPDF.script.Exercise;

/**
 * @author Lukas König
 */
public class FSMScript {

    private LinkedList<FSMScriptInstruction> program;
    
    public FSMScript(String scriptString) {
        this.program = new LinkedList<>();
        
        for (String si : StaticMethods.removeWhitespaces(scriptString).split("" + FSMScriptNames.TRENNER_ZEILE)) {
            try {
                this.addInstruction(si);
            } catch (Exception e) {
            }
        }
    }
    
    public void addInstruction(FSMScriptInstruction inst) {
        program.add(inst);
    }
    
    public void addInstruction(String instF) {
        FSMScriptInstruction instruction;
        
        if (instF.contains("" + FSMScriptNames.ARROW)) { // Transition
            String[] sides = instF.split("" + FSMScriptNames.ARROW);
            String[] operanden = new String[3];
            String[] operandenTemp = sides[0].replace("(", "").replace(")", "").split(",");
            operanden[0] = operandenTemp[0];
            operanden[1] = operandenTemp[1];
            operanden[2] = sides[1];
            
            instruction = new FSMScriptInstruction(FSMScriptNames.INSTRUCTION_INSERT_TRANSITION, operanden);
        } else {
            instruction = new FSMScriptInstruction(FSMScriptNames.INSTRUCTION_INSERT_STATE, new String[] {instF});
        }
        
        this.addInstruction(instruction);
    }
    
    /**
     * Erzeugt einen neuen Automaten, wendet das Script an und gibt
     * diesen zurück.
     * 
     * @return  Der neue Automat.
     */
    public FSM execute() {
        FSM aut = new FSM((Exercise) null);
        int z = 0;
        
        for (FSMScriptInstruction i : program) {
            if (z == 0) {
                aut.setInitialState(i.getFirstState());
            }
            i.execute(aut);
            z++;
        }
        
        return aut;
    }
}
