/*
 * File name:        Word.java (package eas.grammars.type0grammars)
 * Author(s):        Lukas König
 * Java version:     6.0
 * Generation date:  19.01.2012 (21:09:17)
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import veryFastPDF.pdfProcessors.GraphViz;

/**
 * @author Lukas König
 *
 */
public class Word implements Comparable<Word> {

    private ArrayList<Symbol> word = new ArrayList<Symbol>();
    private boolean terminal = true;
    
    public Word(List<Symbol> symbols) {
        for (Symbol s : symbols) {
            this.addSymbol(s);
        }
    }
    
    public Word(Symbol... symbols) {
        for (Symbol s : symbols) {
            this.addSymbol(s);
        }
    }
    
    public Word(Word w) {
        this.terminal = true; // Will be set to false for first Nonterminal.
        word = new ArrayList<Symbol>();
        for (Symbol s : w.getSymbols()) {
            if (s.isTerminal()) {
                this.addSymbol(new Terminal(s.getSymbolAsString()));
            } else {
                this.addSymbol(new Nonterminal(s.getSymbolAsString()));
            }
        }
    }
    
    public Word(String word, Collection<String> nonTerminals) {
        if (word.equalsIgnoreCase("epsilon")) {
            this.word = new ArrayList<>(0);
            this.terminal = true;
            return;
        }
        
        String[] symbols = word.split(",");
        this.word = new ArrayList<>(symbols.length);
        for (int i = 0; i < symbols.length; i++) {
            if (nonTerminals.contains(symbols[i] + "")) {
                this.word.add(new Nonterminal(symbols[i]));
            } else {
                this.word.add(new Terminal(symbols[i]));
            }
        }
    }

    private void addSymbol(Symbol symb) {
        this.word.add(symb);
        if (!symb.isTerminal()) {
            terminal = false;                   
        }
    }
    
    public List<Symbol> getSymbols() {
        return this.word;
    }
    
    public String[] getWordStrings() {
        String[] list = new String[this.getWordLength()];
        int i = 0;

        for (Symbol symb : this.getSymbols()) {
            list[i] = symb.toString();
            i++;
        }
        
        return list;
    }
    
    public boolean isTerminal() {
        return this.terminal;
    }
    
    @Override
    public String toString() {
        return this.word.toString().replace(",", ":").replace("[", "").replace("]", "");
    }
    
    public String toStringHTML(boolean useIndexForMultiSymbolWords) {
        String s = "";
        
        for (Symbol symb : this.getSymbols()) {
            String sString = GraphViz.replaceSpecialChars(symb.toString());
            
            if (sString.equals("<>")) {
                sString = "&diams;";
            } else if (useIndexForMultiSymbolWords && sString.length() > 1 && sString.charAt(1) != '\''
                    && (!sString.startsWith("&") || !sString.endsWith(";"))) {
                sString = sString.charAt(0) + "<SUB>" + sString.substring(1) + "</SUB>";
            } else if (sString.length() > 1) {
                sString = " " + sString;
            }
            
            s += sString;
        }
        
        return s.trim();
    }
    
    public Word replace(int start, int length, Word replacement) {
        Word newWord = new Word(new Symbol[] {});
        
        for (int i = 0; i < start; i++) {
            newWord.addSymbol(this.word.get(i));
        }
        
        for (int i = 0; i < replacement.getSymbols().size(); i++) {
            newWord.addSymbol(replacement.getSymbols().get(i));
        }
        
        for (int i = start + length; i < word.size(); i++) {
            newWord.addSymbol(this.word.get(i));
        }
        
        return newWord;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (this.terminal ? 1231 : 1237);
        result = prime * result
                + ((this.word == null) ? 0 : this.word.hashCode());
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
        Word other = (Word) obj;
        if (this.terminal != other.terminal)
            return false;
        if (this.word == null) {
            if (other.word != null)
                return false;
        } else if (!this.word.equals(other.word))
            return false;
        return true;
    }
    
    public int countNonTerminals() {
        int i = 0;
        
        for (Symbol s : this.word) {
            if (!s.isTerminal()) {
                i++;
            }
        }
        
        return i;
    }

    public int countTerminals() {
        int i = 0;
        
        for (Symbol s : this.word) {
            if (s.isTerminal()) {
                i++;
            }
        }
        
        return i;
    }

    @Override
    public int compareTo(Word o) {
        int a = this.countNonTerminals();
        int b = o.countNonTerminals();
        
        if (a != b) {
            return a - b;
        } else {
            return this.word.size() - o.word.size();
        }
    }
    
    public int getWordLength() {
        return this.word.size();
    }
}
