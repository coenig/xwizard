/*
 * File name:        GrammTree.java (package eas.fundamentalAlgorithms.graphBased.type0grammars)
 * Author(s):        Lukas König
 * Java version:     7.0
 * Generation date:  30.01.2014 (23:06:42)
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

package veryFastPDF.algorithms.grammars;

import java.util.HashSet;
import java.util.LinkedList;

/**
 * @author Lukas König
 */
public class GrammTree {

    private Word word;
    private LinkedList<GrammTree> children;
    private GrammTree father;
    private Grammar embedded;
    
    public GrammTree(Word word, GrammTree father, Grammar embeddedGrammar) {
        this.word = word;
        this.children = new LinkedList<GrammTree>();
        this.father = father;
        this.embedded = embeddedGrammar;
    }
    
    public void addChild(GrammTree child) {
        this.children.add(child);
    }
    
    public Word getWord() {
        return this.word;
    }
    
    public boolean isLeaf() {
        return this.children.isEmpty();
    }
    
    public LinkedList<GrammTree> getChildren() {
        return this.children;
    }
    
    public GrammTree getFather() {
        return this.father;
    }
    
    public void killDoubleTerminals(HashSet<Word> found) {
        LinkedList<GrammTree> toRemove = new LinkedList<GrammTree>();
        for (GrammTree c : this.children) {
            if (c.isLeaf() && c.getWord().isTerminal() && found.contains(c.getWord())) {
                embedded.setRemoved(true);
                toRemove.add(c);
            } else {
                if (c.isLeaf() && c.getWord().isTerminal()) {
                    found.add(c.getWord());
                }
                c.killDoubleTerminals(found);
            }
        }
        this.children.removeAll(toRemove);
    }
    
    public void killNonTerminalBranches() {
        LinkedList<GrammTree> toRemove = new LinkedList<GrammTree>();
        for (GrammTree c : this.children) {
            if (c.isLeaf() && !c.getWord().isTerminal()) {
                embedded.setRemoved(true);
                toRemove.add(c);
            } else {
                c.killNonTerminalBranches();
            }
        }
        this.children.removeAll(toRemove);
    }
    
    public int getSize() {
        int num = 1;
        for (GrammTree g : this.children) {
            num += g.getSize();
        }
        return num;
    }
    
    public int getTermNum() {
        int num = 0;
        if (this.isLeaf()) {
            num = 1;
        }
        for (GrammTree g : this.children) {
            num += g.getTermNum();
        }
        return num;
    }
}
