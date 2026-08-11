package veryFastPDF.algorithms.huffman;

import java.io.Serializable;

/*
 * Huffman Algorithm for the DEI's Programming Contest, 2004
 * (c) Paulo Marques, 2004.
 * pmarques@dei.uc.pt
 *
 * Note: this program only process text characters:
 *       ('a'-'z' / 'A'-'Z'). Everything else is ignored.
 *       [This note is not true anymore since Lhuk made it all better.]
 */

public class HuffmanNode implements Comparable<HuffmanNode>, Serializable {

    private static final long serialVersionUID = 7920560869349687203L;

    public int getValue() {
        return this.value;
    }
    
    public char getContent() {
        return this.content;
    }

    public HuffmanNode getLeft() {
        return this.left;
    }

    public HuffmanNode getRight() {
        return this.right;
    }

    private int value;
    private char content;
    private HuffmanNode left;
    private HuffmanNode right;

    public HuffmanNode(char content, int value) {
        this.content = content;
        this.value = value;
    }

    public HuffmanNode(HuffmanNode left, HuffmanNode right) {
        // Assumes that the left three is always the one that is lowest
        this.content = (left.content < right.content) ? left.content
                : right.content;
        this.value = left.value + right.value;
        this.left = left;
        this.right = right;
    }

    @Override
    public int compareTo(HuffmanNode arg) {
        HuffmanNode other = arg;

        // Content value has priority and then the lowest letter
        if (this.value == other.value)
            return this.content - other.content;
        else
            return this.value - other.value;
    }

    private static String output;
    
    private void printNode(String path) {
        if ((left == null) && (right == null))
            output += content + " " + path + "\n";

        if (left != null)
            left.printNode(path + '0');
        if (right != null)
            right.printNode(path + '1');
    }

    public static String printTree(HuffmanNode tree) {
        output = "";
        tree.printNode("");
        return output;
    }
    
    public String shortToString() {
        return "\"" + this.content + "a" + this.value + "\"";
    }
    
    @Override
    public String toString() {
        String left = "";
        String right = "";
        String content = "";
        String ka = " ";
        String kz = " ";
        
        try {left = this.left.toString();} catch (Exception e) {}
        try {right = this.right.toString();} catch (Exception e) {}
        
        if (left.length() == 0 && right.length() == 0) {
            content = shortToString();
        } else {
            ka = "(";
            kz = ")";
        }
        
        return ka + left + content + right + kz;
    }
    
    public int depth() {
        int depthLeft = 0;
        int depthRight = 0;
        
        if (this.left != null) {
            depthLeft = 1 + this.left.depth();
        }
        
        if (this.right != null) {
            depthRight = 1 + this.right.depth();
        }
        
        return Integer.max(depthLeft, depthRight);
    }
}