/*
 * File name:        ScriptInstruction.java (package eas.fundamentalAlgorithms.graphBased.fsm.script)
 * Author(s):        Lukas König
 * Java version:     7.0
 * Generation date:  14.11.2013 (21:10:10)
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

import veryFastPDF.algorithms.fsm.FSM;
import veryFastPDF.algorithms.fsm.Transition;

/**
 * @author Lukas König
 */
public class FSMScriptInstruction {

    private String operator;
    private String[] operands;

    public FSMScriptInstruction(String operator, String[] operands) {
        super();
        this.operator = operator;
        this.operands = operands;
    }
    
    private void insertState(FSM aut, String state, String mode) {
        if (mode.equals("")) { // Regular state.
            if (!aut.getAllStates().contains(state)) {
                aut.addSingleState(state);
            }
        } 
        if (mode.contains("" + FSMScriptNames.POSTFIX_FINAL_STATE)) { // Final state.
            aut.addFinalState(state);
        } 
        if (mode.contains("" + FSMScriptNames.POSTFIX_INITIAL_STATE)) { // Initial state.
            aut.setInitialState(state);
        }
    }
    
    private void insertTransition(FSM aut, String state1, String state2, String label) {
        aut.addTransition(new Transition(true, state1, state2, label));
    }
    
    private String getNotation(String fullName) {
        if (fullName.contains("" + FSMScriptNames.TRENNER_POSTFIX)) {
            return fullName.split("" + FSMScriptNames.TRENNER_POSTFIX)[1];
        }
        
        return "";
    }
    
    private String getOperand(String fullName) {
        if (fullName.contains("" + FSMScriptNames.TRENNER_POSTFIX)) {
            return fullName.split("" + FSMScriptNames.TRENNER_POSTFIX)[0];
        }
        
        return fullName;
    }
    
    public void execute(FSM aut) {
        try {
        if (operator.equals(FSMScriptNames.INSTRUCTION_INSERT_STATE)) {
            String state = this.getOperand(operands[0]);
            String mode = this.getNotation(operands[0]);
            this.insertState(aut, state, mode);
        } else if (operator.equals(FSMScriptNames.INSTRUCTION_INSERT_TRANSITION)) {
            String state1 = this.getOperand(operands[0]);
            String mode1 = this.getNotation(operands[0]);
            String[] labels = this.operands[1].split("" + FSMScriptNames.TRENNER_POSTFIX);
            String state2 = this.getOperand(operands[2]);
            String mode2 = this.getNotation(operands[2]);
            
            if (!mode1.equals("")) {
                this.insertState(aut, state1, mode1);
            }
            if (!mode2.equals("")) {
                this.insertState(aut, state2, mode2);
            }
            
            for (String l : labels) {
                this.insertTransition(aut, state1, state2, l);
            }
        }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    public String getFirstState() {
        return this.getOperand(this.operands[0]);
    }
}
