/*
 * File name:        GateLevelsCollection.java (package veryFastPDF.algorithms.circuits)
 * Author(s):        Lukas König
 * Java version:     8.0 (at generation time)
 * Generation date:  20.11.2015 (10:46:50)
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

/**
 * @author Lukas König
 */
public class GateLevelsCollection {

    private ArrayList<GateLevels> gateLevelsCollection;
    
    public GateLevelsCollection(HashMap<String, Gate> allGates) {
        HashSet<Gate> allSoFar = new HashSet<>();
        HashSet<HashSet<Gate>> clusters = new HashSet<>();
        
        for (Gate g : allGates.values()) {
            if (!allSoFar.contains(g)) {
                HashSet<Gate> cluster = new HashSet<>();
                getClusterFromGate(allGates.values(), cluster, g);
                allSoFar.addAll(cluster);
                clusters.add(cluster);
            }
        }
        
        createTrees(clusters);
    }

    private void createTrees(HashSet<HashSet<Gate>> clusters) {
        gateLevelsCollection = new ArrayList<>(clusters.size());
        
        for (HashSet<Gate> cluster : clusters) {
            GateLevels gl = new GateLevels();
            this.createLevels(new LinkedList<>(cluster).get(0), cluster, new HashSet<>(), gl);
            gateLevelsCollection.add(gl);
        }
        
//        System.out.println();
//        System.out.println(gateLevelsCollection);
    }
    
    private void createLevels(Gate g, HashSet<Gate> cluster, HashSet<Gate> soFar, GateLevels levels) {
            soFar.add(g);
            levels.setGate(g);
            
            for (Input in : g.getInputList().values()) {
                for (Output from : in.getConnections()) {
                    Gate gate = from.getGate();
                    if (!soFar.contains(gate)) {
                        GateLevels gateLevels2 = new GateLevels();
                        createLevels(gate, cluster, soFar, gateLevels2);
                        levels.parents.add(gateLevels2);
                    }
                }
            }

            Output out = g.getOutput();
            for (Input to : out.getConnections()) {
                Gate gate = to.getGate();
                if (!soFar.contains(gate)) {
                    GateLevels gateLevels2 = new GateLevels();
                    createLevels(gate, cluster, soFar, gateLevels2);
                    levels.parents.add(gateLevels2);
                }
            }
    }

    public void getClusterFromGate(Collection<Gate> allGates, HashSet<Gate> soFar, Gate g) {
        if (soFar.contains(g)) {
            return;
        }
        
        soFar.add(g);
        
        for (Input input : g.getInputList().values()) {
            for (Output from : input.getConnections()) {
                Gate gate = from.getGate();
                soFar.add(gate);
                getClusterFromGate(allGates, soFar, gate);
            }
        }
        
        Output output = g.getOutput();
        for (Input to : output.getConnections()) {
            Gate gate = to.getGate();
            soFar.add(gate);
            getClusterFromGate(allGates, soFar, gate);
        }
    }
}
