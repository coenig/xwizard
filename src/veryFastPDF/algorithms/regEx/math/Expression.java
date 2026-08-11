/*
 * File name:        Expression.java (package eas.math)
 * Author(s):        Lukas König
 * Java version:     8.0 (at generation time)
 * Generation date:  02.06.2015 (15:55:24)
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

package veryFastPDF.algorithms.regEx.math;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
//import java.util.LinkedList;
import java.util.List;

import eas.GlobalVariables;
import eas.miscellaneous.StaticMethods;
import eas.miscellaneous.convenience.GeneralDialog;
import mainServlet.WebLink;
import veryFastPDF.algorithms.regEx.RegEx;
import veryFastPDF.script.exceptionHandling.LongOperationException;

/**
 * @author Lukas König
 */
public class Expression {

    public static final boolean OMIT_UNNECESSARY_BRACKETS_IN_OUTPUT = true;
    
    public static int outputLengthLatex = 0;
    
    private final String opBrack;
    private final String clBrack;
    private final String[] ops1ValPostfix;
    private final String[] ops2ValDesc;

    private HashSet<Character> reservedCharacters = new HashSet<>();
    private ExpressionRaw expression;

    private ArrayList<String >invisibleOp = new ArrayList<>(1);

    public Expression(
            String openingBracket,
            String closingBracket,
            String[] operators1ValPostfix,
            String... operators2ValAscendingStrength) {
        this.opBrack = openingBracket;
        this.clBrack = closingBracket;
        this.ops2ValDesc = operators2ValAscendingStrength;
        this.ops1ValPostfix = operators1ValPostfix;
        
        for (int i = 0; i < openingBracket.length(); i++) {
            reservedCharacters.add(openingBracket.charAt(i));
        }
        
        for (int i = 0; i < closingBracket.length(); i++) {
            reservedCharacters.add(closingBracket.charAt(i));
        }

        for (String s : operators1ValPostfix) {
            for (int i = 0; i < s.length(); i++) {
                reservedCharacters.add(s.charAt(i));
            }
        }

        for (String s : operators2ValAscendingStrength) {
            for (int i = 0; i < s.length(); i++) {
                reservedCharacters.add(s.charAt(i));
            }
        }
        
        logDebug("Expression envelope initiated. Reserved characters: " + reservedCharacters);
    }

    public String getOpBrack() {
        return this.opBrack;
    }
    
    public String getClBrack() {
        return this.clBrack;
    }
    
    public void setInvisibleOp(String op) {
        if (!this.isTwoValOp(op)) {
            throw new RuntimeException("'" + op + "' is not a two-valued operator.");
        }
        
        this.invisibleOp.clear();
        this.invisibleOp.add(op);
        
        logDebug("Operator '" + op + "' set invisible.");
    }
    
    /**
     * @return Returns the ops1ValPostfix.
     */
    public String[] getOps1ValPostfix() {
        return this.ops1ValPostfix;
    }

    /**
     * @return Returns the ops2ValDesc.
     */
    public String[] getOps2ValDesc() {
        return this.ops2ValDesc;
    }

    /**
     * @return Returns the reservedCharacters.
     */
    public HashSet<Character> getReservedCharacters() {
        return this.reservedCharacters;
    }

    /**
     * @return Returns the invisibleOp.
     */
    public ArrayList<String> getInvisibleOp() {
        return this.invisibleOp;
    }

    private int findOpeningBracketOneVal(int pos, List<String> tokens) {
        String mainOp = tokens.get(pos);
        
        if (!this.isOneValOp(mainOp)) {
            throw new RuntimeException("'" + mainOp + "' is not a one-valued operator.");
        }
        
        int bracketCount = 0;
        
        for (int i = pos - 1; i >= 0; i--) {
            String curTok = tokens.get(i);

            if (curTok.equals(opBrack)) {
                bracketCount--;
            }

            if (curTok.equals(clBrack)) {
                bracketCount++; 
            }
        
            if (bracketCount == 0) {
                return i;
            }
        }
        
        return 0;
    }
    
    private int findOpeningBracket(int pos, List<String> exp) {
        int bracketCount = 0;
        String mainOp = exp.get(pos);
        
        for (int i = pos - 1; i >= 0; i--) {
            String op2 = exp.get(i);
            
            if (bracketCount == 0 && (op2.equals(opBrack)
                    || (this.isTwoValOp(op2) && isOp1HigherThanOp2(op2, mainOp)))) {
                return i;
            }
            
            if (op2.equals(opBrack)) {
                bracketCount--;
            }
            
            if (op2.equals(clBrack)) {
                bracketCount++;
            }
        }
        
        return -1;
    }
    
    private int findClosingBracket(int pos, List<String> exp) {
        int bracketCount = 0;
        String mainOp = exp.get(pos);
        
        for (int i = pos + 1; i < exp.size(); i++) {
            String op2 = exp.get(i);
            
            if (bracketCount == 0 && (op2.equals(clBrack)
                    || (this.isTwoValOp(op2) && isOp1HigherThanOp2(op2, mainOp)))) {
                return i;
            }
            
            if (op2.equals(opBrack)) {
                bracketCount--;
            }
            
            if (op2.equals(clBrack)) {
                bracketCount++;
            }
        }
        
        return exp.size();
    }

    private boolean isTwoValOp(String c1) {
        for (String c2 : this.ops2ValDesc) {
            if (c1.equals(c2)) {
                return true;
            }
        }
        
        return false;
    }

    private boolean isSingleSymb(char c1) {
        return !reservedCharacters.contains(c1);
    }

    private boolean isAllSingleSymbs(String c1) {
        for (int i = 0; i < c1.length(); i++) {
            if (!isSingleSymb(c1.charAt(i))) {
                return false;
            }
        }
        
        return true;
    }

    private boolean isOneValOp(String c1) {
        for (String c2 : this.ops1ValPostfix) {
            if (c1.equals(c2)) {
                return true;
            }
        }
        
        return false;
    }

    private boolean isBeginningOfOneValOp(String c1) {
        for (String c2 : this.ops1ValPostfix) {
            if (c2.startsWith(c1)) {
                return true;
            }
        }
        
        return false;
    }
    
    private boolean isBeginningOfTwoValOp(String c1) {
        for (String c2 : this.ops2ValDesc) {
            if (c2.startsWith(c1)) {
                return true;
            }
        }
        
        return false;
    }

    @SuppressWarnings("unused")
    private boolean isBeginningOfAnySpecialSymb(String c1) {
        return this.isBeginningOfOneValOp(c1)
                || this.isBeginningOfTwoValOp(c1)
                || this.opBrack.startsWith(c1)
                || this.clBrack.startsWith(c1);
    }

    public void createFromExpString(String fromRaw) {
        GeneralDialog.resetLongTimeOperationID("REG_EX_VALIDATION_PROZESS_IN_DEBUG_MODE");
        createFromExpString2(fromRaw);
    }

    private void createFromExpString2(String fromRaw) {
        if (!GeneralDialog.continueLongOperation("REG_EX_VALIDATION_PROZESS_IN_DEBUG_MODE")) {
            throw new LongOperationException();
        }
        
        logDebug("Creating expression.");
        List<String> tokenized = tokenize(fromRaw);
        this.expression = this.createFromString(tokenized);
        logDebug("Original string:    " + fromRaw);
        logDebug("Created expression: " + this.toString());
        
        if (WebLink.isDebugMode() && !fromRaw.equals(this.toString())) {
            logDebug("Strings are not equal. Starting validation (debug mode only).");
            createFromExpString2(this.toString());
        }
    }

    public void createFromRegEx(RegEx exp) {
        this.createFromExpString(exp.toString());
    }
    
    private List<String> tokenize(String fromRaw) {
        GeneralDialog.resetLongTimeOperationID("REG_EX_TOKENIZER");

        return new ArrayList<String>(this.insertInvisibleOp(tokenize(
                StaticMethods.removeWhitespaces(fromRaw), 
                new LinkedList<String>())));
    }

    private List<String> tokenize(String from, List<String> currentList) {
        if (from.length() == 0) {
            return currentList;
        }

        if (!GeneralDialog.continueLongOperation("REG_EX_TOKENIZER")) {
            throw new LongOperationException();
        }

        String currentPart = null;
        int i;
        boolean singleChars = isSingleSymb(from.charAt(0));
        for (i = 0; i < from.length(); i++) {
            currentPart = from.substring(0, i + 1);
            
            if (singleChars) {
                if (i + 1 >= from.length() || !isSingleSymb(from.charAt(i + 1))) {
                    break;
                }
            } else {
                if (this.isOneValOp(currentPart)
                        || this.isTwoValOp(currentPart)
                        || this.opBrack.equals(currentPart)
                        || this.clBrack.equals(currentPart)) {
                    break;
                }
            }
        }
        
        currentList.add(currentPart);
        return tokenize(from.substring(i + 1), currentList);
    }
    
    private boolean isOp1HigherThanOp2(String op1, String op2) {
        if (!this.isTwoValOp(op1) || !this.isTwoValOp(op2)) {
            throw new RuntimeException("'" + op1 + "' or '" + op2 + "' is not an operator.");
        }
        
        boolean op1Found = false;
        for (String op : this.ops2ValDesc) {
            if (op1Found && op.equals(op2)) {
                return true;
            }

            if (!op1Found && op.equals(op1)) {
                op1Found = true;
            }
        }
        
        return false;
    }
    
    private int findClosingBracketForOpeningBracket(int opBrackPos, List<String> tokens) {
        int brackCount = 1;
        
        for (int i = opBrackPos + 1; i < tokens.size(); i++) {
            if (tokens.get(i).equals(opBrack)) {
                brackCount++;
            }
            
            if (tokens.get(i).equals(clBrack)) {
                brackCount--;
                
                if (brackCount == 0) {
                    return i;
                }
            }
        }
        
        return -1;
    }
    
    private boolean isEnclosedByBrackets(List<String> tokens) {
        if (tokens.get(0).equals(opBrack) && tokens.get(tokens.size() - 1).equals(clBrack)) {
            return findClosingBracketForOpeningBracket(0, tokens) == tokens.size() - 1;
        }
        
        return false;
    }
    
    private String findNextOperatorLeft(int pos, int leftOperandLength, List<String> tokens) {
        if (!this.isTwoValOp(tokens.get(pos))) {
            throw new RuntimeException("Two-valued operator expected at " + pos);
        }
        
        int brackCount = 0;
        
        for (int i = pos - leftOperandLength; i >= 0; i--) {
            String token = tokens.get(i);
            
            if (token.equals(clBrack)) {
                brackCount++;
            }
            
            if (token.equals(opBrack)) {
                brackCount--;
            }
            
            if (brackCount <= 0 && this.isTwoValOp(token)) {
                return token;
            }
        }
        
        return "";
    }
    
    private String findNextOperatorRight(int pos, int rightOperandLength, List<String> tokens) {
        if (!this.isTwoValOp(tokens.get(pos))) {
            throw new RuntimeException("Two-valued operator expected at " + pos);
        }
        
        int brackCount = 0;
        
        for (int i = pos + rightOperandLength; i < tokens.size(); i++) {
            String token = tokens.get(i);
            
            if (token.equals(clBrack)) {
                brackCount--;
            }
            
            if (token.equals(opBrack)) {
                brackCount++;
            }
            
            if (brackCount <= 0) {
                if (this.isTwoValOp(token)) {
                    return token;
                }
            }
            
            if (brackCount < 0) {
                if (this.isOneValOp(token)) {
                    return "";
                }
            }
        }
        
        return "";
    }
    
    private ExpressionRaw createFromString(List<String> tokens) {
        GeneralDialog.resetLongTimeOperationID("REG_EX_CREATE_FROM_STRING");
        return createFromString2(tokens, tokens, 0);
    }
    
    private ExpressionRaw createFromString2(List<String> tokens, List<String> completeList, int relPos) {
        if (!GeneralDialog.continueLongOperation("REG_EX_CREATE_FROM_STRING")) {
            throw new LongOperationException();
        }
        
        if (this.isEnclosedByBrackets(tokens)) {
            return createFromString2(tokens.subList(1, tokens.size() - 1), completeList, relPos + 1);
        }
        
        for (int i = 0; i < tokens.size(); i++) {
            if (this.isAtTopmostPosition(i, tokens)) {
                logDebug(print(tokens, i));
                
                String token = tokens.get(i);
                if (this.isTwoValOp(token)) {
                    int leftBeg = this.findOpeningBracket(i, tokens) + 1, 
                        leftEnd = i,
                        rightBeg = i + 1, 
                        rightEnd = this.findClosingBracket(i, tokens); 
        
                    boolean bracketsNecessary = true;

                    if (OMIT_UNNECESSARY_BRACKETS_IN_OUTPUT) {
                        String opLeft = this.findNextOperatorLeft(i + relPos, leftEnd - leftBeg, completeList);
                        String opRight = this.findNextOperatorRight(i + relPos, rightEnd - rightBeg, completeList);
                        
                        if ((opLeft.equals("") || !this.isOp1HigherThanOp2(token, opLeft))
                                && (opRight.equals("") || !this.isOp1HigherThanOp2(token, opRight))) {
                            bracketsNecessary = false;
                        }
                    }
                    
                    return new ExpressionTwoVal(
                            this.createFromString2(tokens.subList(leftBeg, leftEnd), completeList, relPos + leftBeg), 
                            this.createFromString2(tokens.subList(rightBeg, rightEnd), completeList, relPos + rightBeg), 
                            token,
                            bracketsNecessary,
                            this);
                } else if (this.isOneValOp(token)) {
                    int beg = this.findOpeningBracketOneVal(i, tokens),
                        end = i;
                    return new ExpressionOneVal(
                            this.createFromString2(tokens.subList(beg, end), completeList, relPos + beg), 
                            token,
                            this);
                }
            }
        }
        
        logDebug(tokens.toString());
        return new ExpressionTerminal(tokens.get(0));
    }

    private String print(List<String> tokens, int i) {
        String s = "[" + (tokens.isEmpty() ? "" : tokens.get(0));

        
        for (int j = 1; j < tokens.size(); j++) {
            s += " " + (i == j ? "<" + tokens.get(j) + ">" : tokens.get(j));
        }
        
        return s + "]";
    }

    private boolean isAtTopmostPosition(int i, List<String> from) {
        return this.isTwoValOp(from.get(i)) 
                && this.findOpeningBracket(i, from) <= 0
                && this.findClosingBracket(i, from) >= from.size() - 1
                || this.isOneValOp(from.get(i)) && i == from.size() - 1;
    }

    public String toString(boolean latex) {
        outputLengthLatex = 0;
        String string = this.expression.toString(latex);

        if (this.invisibleOp.size() > 0) {
            string = string.replace(this.invisibleOp.get(0), "");
        }

        if (latex) {
            return string.replace("*", "^\\star ").replace("O", "\\emptyset ");
        }

        return string;
    }
    
    @Override
    public String toString() {
        return toString(false);
    }
    
    public String toPostfix() {
        return this.expression.toStringPostfix();
    }
    
    public List<String> insertInvisibleOp(List<String> tokens) {
        if (this.invisibleOp.isEmpty()) {
            return tokens;
        }
        
        HashSet<Integer> toInsert = new HashSet<>();
        LinkedList<String> tokenListToInsert = new LinkedList<>();
        int tokenListToInsertAt = 0;
        
        for (int i = 0; i < tokens.size(); i++) {
            String tokBefore = i == 0 ? this.ops2ValDesc[0] : tokens.get(i - 1);
            String token = tokens.get(i);
            String tokenAfter = i == tokens.size() - 1 ? this.ops2ValDesc[0] : tokens.get(i + 1);
            
            // No operator before opening parenthesis.
            if (!isTwoValOp(tokBefore) && !tokBefore.equals(this.opBrack) && token.equals(this.opBrack)) {
                toInsert.add(i - 1);
            }
            
            // No operator after closing parenthesis.
            if (!isTwoValOp(tokenAfter) && !tokenAfter.equals(this.clBrack) && !isOneValOp(tokenAfter)
                    && token.equals(this.clBrack)) {
                toInsert.add(i);
            }
            
            // No two-valued operator after one-valued operator.
            if (this.isOneValOp(token) && !tokenAfter.equals(clBrack) 
                    && !this.isTwoValOp(tokenAfter) && !this.isOneValOp(tokenAfter)) {
                toInsert.add(i);
            }
            
            // Count more than one character in terminal as several terminals.
            if (this.isAllSingleSymbs(token) && token.length() > 1) {
                // Ignore all and start over.
                tokenListToInsertAt = i;
                
                for (int j = 0; j < token.length(); j++) {
                    tokenListToInsert.add("" + token.charAt(j));
                }
                
                break;
            }
        }
        
        if (!tokenListToInsert.isEmpty()) {
            tokens.remove(tokenListToInsertAt);
            
            tokens.add(tokenListToInsertAt, tokenListToInsert.get(tokenListToInsert.size() - 1));

            for (int i = tokenListToInsert.size() - 2; i >= 0; i--) {
                tokens.add(tokenListToInsertAt, this.invisibleOp.get(0));
                tokens.add(tokenListToInsertAt, tokenListToInsert.get(i));
            }
            
            return insertInvisibleOp(tokens);
        }
        
        // Insert.
        ArrayList<Integer> toInsertSorted = new ArrayList<>(toInsert);
        Collections.sort(toInsertSorted);
        int additionalSum = 0;
        for (int pos : toInsertSorted) {
            tokens.add(pos + 1 + additionalSum, this.invisibleOp.get(0));
            additionalSum++;
        }

        return tokens;
    }

    public void logDebug(String logString) {
        GlobalVariables.getParameters().logDebug("<Expression> " + logString);
    }
    
    public static void main(String[] args) {
        Expression exp = new Expression("(", ")", new String[] {"*"}, "+", ".");
        exp.setInvisibleOp(".");
        String s = "a+b";
        exp.createFromExpString(s);
    }
    
    public ExpressionRaw getExpressionRaw() {
        return this.expression;
    }

    public static String manageLineBreak1(boolean latex) {
        String latexLineBreak = "";
        
        if (Expression.outputLengthLatex > 500 && latex) {
            Expression.outputLengthLatex = 0;
            latexLineBreak = "\\\\&";
        }
        return latexLineBreak;
    }

    public static void manageLineBreak2(String shortVersion) {
        Expression.outputLengthLatex += shortVersion.length();
    }
}
