/*
 * File name:        BDDNode.java (package eas.miscellaneous.fsmToPDF)
 * Author(s):        lko
 * Java version:     7.0
 * Generation date:  05.10.2012 (13:57:13)
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

package veryFastPDF.algorithms.bdd;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import veryFastPDF.pdfProcessors.GraphViz;

/**
 * @author lko
 */
public class BDDNode {

    private BDDNode left;
    private BDDNode right;
    private HashSet<BDDNode> fathers = new HashSet<BDDNode>();
    private String name;
    private int index;
    private boolean marked = false;

    public void mark() {
        this.marked = true;
    }
    
    public boolean getMarked() {
        return this.marked;
    }
        
    public void deMarkAll() {
        this.marked = false;
        if (this.getLeft() != null) {
            this.getLeft().deMarkAll();
        }
        if (this.getRight() != null) {
            this.getRight().deMarkAll();
        }
    }
    
    public void addFather(BDDNode father) {
        this.fathers.add(father);
    }

    public void removeAllFathers() {
        this.fathers.clear();
    }
    
    public boolean removeFather(BDDNode father) {
        return this.fathers.remove(father);
    }
    
    public HashSet<BDDNode> getFathers() {
        return this.fathers;
    }

    public void setLeft(BDDNode left) {
        this.left = left;
    }
    
    public void setRight(BDDNode right) {
        this.right = right;
    }
    
    public BDDNode getLeft() {
        return this.left;
    }
    
    public BDDNode getRight() {
        return this.right;
    }

    public String getName() {
        return this.name;
    }
    
    public BDDNode(BDDNode leftnode, BDDNode rightnode, String nodeName, int index) {
        this.left = leftnode;
        this.right = rightnode;
        this.name = nodeName;
        this.index = index;
    }
    
    public boolean identical(BDDNode other) {
        if (other == null) {
            return false;
        }
        
        Boolean b1 = false, b2 = false;
        
        if (this.name.equals(other.name)) {
            if (left == null || other.left == null) {
                b1 = other.left == this.left;
            }
            if (right == null || other.right == null) {
                b2 = other.right == this.right;
            }
            
            if ((b1 || left.identical(other.left)) 
                    && (b2 || right.identical(other.right))) {
                return true;
            }
        }
        
        return false;
    }
    
    @Override
    public String toString() {
        if (this.left == null) {
            return this.name;
        }
        return "(" + this.left + ") " +  this.name + " (" + this.right + ")";
    }
    
    private String fullname() {
        return this.name + this.index;
    }
    
    public List<String> getGraphVizInstructions() {
        return this.getGraphVizInstructions(new HashSet<BDDNode>());
    }
    
    private LinkedList<String> getGraphVizInstructions(HashSet<BDDNode> alreadyVisited) {
        LinkedList<String> inst = new LinkedList<String>();
        String line;
        
        if (alreadyVisited.contains(this)) {
            return inst;
        } else {
            alreadyVisited.add(this);
        }

        String style = "";
        if (this.marked) {
            style = ",style=filled";
        }
        
        if (this.left != null || this.right != null) {
            line = this.fullname() + " [label=" + this.name + ",shape=circle" + style + "];";
        } else {
            line = this.fullname() + " [label=" + this.name + ",shape=rectangle" + style + "];";
        }
        inst.add(line);
        
        if (this.left != null && this.left == this.right) {
            inst.addAll(this.left.getGraphVizInstructions(alreadyVisited));
//            inst.add(this.left.fullname() + " [label=" + this.left.name + "];");
            inst.add(this.fullname() + GraphViz.edgeSymbolDirected + this.left.fullname() + " [label=\" 0/1 \"];");
        }
        
        if (this.left != null && this.left != this.right) {
            inst.addAll(this.left.getGraphVizInstructions(alreadyVisited));
//            inst.add(this.left.fullname() + " [label=" + this.left.name + "];");
            inst.add(this.fullname() + GraphViz.edgeSymbolDirected + this.left.fullname() + " [label=\" 0 \"];");
        } 
        if (this.right != null && this.left != this.right) {
            inst.addAll(this.right.getGraphVizInstructions(alreadyVisited));
//            inst.add(this.right.fullname() + " [label=" + this.right.name + "];");
            inst.add(this.fullname() + GraphViz.edgeSymbolDirected + this.right.fullname() + " [label=\" 1 \"];");
        }
        
        return inst;
    }
    
    public int getDepth() {
        if (this.left == null && this.right == null) {
            return 1;
        }
        if (this.left == null) {
            return this.right.getDepth() + 1;
        }
        if (this.right == null) {
            return this.left.getDepth() + 1;
        }
        
        return Math.max(this.left.getDepth(), this.right.getDepth()) + 1;
    }
    
    public int getNumNodes() {
        HashSet<BDDNode> allNodes = this.getAllNodes(new HashSet<>());
        return allNodes.size();
    }
    
    private HashSet<BDDNode> getAllNodes(HashSet<BDDNode> alreadyVisited) {
        if (alreadyVisited.contains(this)) {
            return alreadyVisited;
        }
        
        alreadyVisited.add(this);

        if (this.left != null && this.left == this.right) {
            this.left.getAllNodes(alreadyVisited);
        }
        
        if (this.left != null && this.left != this.right) {
            this.left.getAllNodes(alreadyVisited);
        }
        
        if (this.right != null && this.left != this.right) {
            this.right.getAllNodes(alreadyVisited);
        }
        
        return alreadyVisited;
    }
}
