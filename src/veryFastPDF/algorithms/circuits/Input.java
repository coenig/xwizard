/*
 * File name:        Input.java (package veryFastPDF.algorithms.circuits)
 * Author(s):        Lukas König
 * Java version:     8.0 (at generation time)
 * Generation date:  20.11.2015 (10:59:37)
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

package veryFastPDF.algorithms.circuits;

import java.util.HashSet;

/**
 * @author Lukas König
 */
public class Input {

    private HashSet<Output> connections = new HashSet<>();
    private Gate gate;
    private int iNum;
    
    public Input(Gate g, int inputNum) {
        this.gate = g;
        this.iNum = inputNum;
    }
    
    public HashSet<Output> getConnections() {
        return this.connections;
    }
    
    public void addConnection(Output o) {
        connections.add(o);
    }
    
    public Gate getGate() {
        return this.gate;
    }
    
    @Override
    public String toString() {
        return this.gate.getName() + "." + this.iNum;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((this.gate == null) ? 0 : this.gate.hashCode());
        result = prime * result + this.iNum;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Input other = (Input) obj;
        if (this.gate == null) {
            if (other.gate != null)
                return false;
        } else if (!this.gate.equals(other.gate))
            return false;
        if (this.iNum != other.iNum)
            return false;
        return true;
    }

    public int getiNum() {
        return this.iNum;
    }
}
