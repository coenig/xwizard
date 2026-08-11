/*
 * File name:        RedBlackBST.java (package eas.math.fundamentalAlgorithms.graphBased.algorithms.redblacktree)
 * Author(s):        Marlon Braun
 * Java version:     8.0 (at generation time)
 * Generation date:  18.09.2014 (14:14:05)
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

package veryFastPDF.algorithms.searchTree.redblacktree;

import java.util.LinkedList;
import java.util.NoSuchElementException;
import java.util.Queue;

import eas.GlobalVariables;
import veryFastPDF.algorithms.searchTree.AbstractSearchTree;
import veryFastPDF.algorithms.searchTree.DirtyReflectionHacks;
import veryFastPDF.algorithms.searchTree.tree234.Tree234Impl;

/**
 * This class is a Java implementation of a left-leaning
 * red-black-binary-search-tree. The code was obtained from <a href=
 * "http://algs4.cs.princeton.edu/33balanced/RedBlackBST.java.html">Princeton
 * university</a>. I made some alterations to the code to adopt it to this
 * framework. I also added some code to generate a DOT representation of the
 * tree for GraphViz visualization.
 * 
 * @author marlon.braun
 * @param <Key>
 *            The key by which values in the tree are ordered.
 * @param <Value>
 *            The values that are associated with the keys.
 */
public class RedBlackBST<Key extends Comparable<Key>, Value> extends
        AbstractSearchTree<Key, Value, RedBlackBST<Key, Value>.Node> {

    /**
     * Serves as seed for creating unique IDs for dummy variables.
     */
    private long incrementalRandom = System.currentTimeMillis();

    public static final boolean RED = true;
    public static final boolean BLACK = false;

    // BST helper node data type
    public class Node extends AbstractSearchTree<Key, Value, Node>.AbstractNode
            implements Comparable<Node> {
        private Key key; // key
        private Value val; // associated data
        public Node left, right; // left and right subtrees
        public boolean color; // color of parent link
        public int N; // subtree count

        public Node(Key key, Value val, boolean color, int N) {
            this.key = key;
            this.val = val;
            this.color = color;
            this.N = N;
        }

        @Override
        public int compareTo(Node other) {
            return this.key.compareTo(other.key);
        }

        @Override
        public String toString() {
            String ret = color == RED ? RedBlackTree.RED : RedBlackTree.BLACK;
            return ret + ":" + key.toString();
        }

        /**
         * Method for retrieving the DOT representation of this node and all its
         * child-nodes.
         * 
         * @return The DOT representation of this node and all of its child
         *         nodes.
         */
        @Override
        public String getDOT() {
            String dot = "";
            // Return if there are no children in order to prevent creating
            // dummy variables
            if (left == null && right == null)
                return dot;

            // Left child
            if (left != null) {
                dot += key.toString() + " -> " + left.key.toString();
                if (isRed(left)) {
                    dot += " [color=red style=dotted]";
                }
                dot += ";\n";
                // Recursively get children of left child
                dot += left.getDOT();
            } else {
                dot += getDummy(key.toString());
            }

            // Create a dummy between the left and right child so both children
            // are correctly positioned. Although this is quite hacky, they
            // makers of GViz actually suggest this themselves
            dot += getDummy(key.toString());

            // Right child
            if (right != null) {
                dot += key.toString() + " -> " + right.key.toString();
                if (isRed(right)) {
                    dot += " [color=red style=dotted]";
                }
                dot += ";\n";
                // Recursively get children of right child
                dot += right.getDOT();
            } else {
                dot += getDummy(key.toString());
            }
            return dot;
        }

        @Override
        public String getScript() {
            String ret = "";
            // No children no need for a node
            if (left != null) {
                ret += getGraphLabel() + "=>" + left.getGraphLabel() + ";\n";
                ret += left.getScript();
            }
            if (right != null) {
                ret += getGraphLabel() + "=>" + right.getGraphLabel() + ";\n";
                ret += right.getScript();
            }
            return ret;
        }

        /**
         * Basically does the same as {@link #toString()}, however omits
         * labeling black nodes.
         * 
         * @return The key of this node prefixed by <code>r:</code> if the node
         *         is red.
         */
        public String getGraphLabel() {
            String ret = "";
            if (isRed(this))
                ret += "r:";
            ret += key.toString();
            return ret;
        }

        /**
         * Creates a dummy node that is a child of <code>parent</code>. The
         * dummy is not visible in the final graph and only there for balancing
         * the nodes in the graph, so a right (left) child is actually located
         * to the right (left) of the parent.
         * 
         * @param parent
         *            The parent to which this dummy is attached
         * @return A dummy node in the DOT graph that is not visible.
         */
        public String getDummy(String parent) {
            // The dummy needs a unique ID
            long dummy = incrementalRandom++;
            // We add the parent as prefix so the dummy is placed between the
            // two child nodes
            String nValue = parent + dummy;
            String ret = nValue + " [label=\"\",width=.1,style=invis];\n";
            ret += parent + " -> " + nValue + " [style=invis];\n";
            return ret;
        }
    }

    /**
     * Returns the DOT representation of this red-black-tree
     * 
     * @return The DOT representation of this red-black-tree
     */
    @Override
    public String getDOT() {
        return "digraph G {\n" + root.getDOT() + "}";
    }

    @Override
    public String getScript() {
        if (root.left == null && root.right == null)
            return root.key.toString();
        else
            return root.getScript();
    }

    /**
     * Obtains the color from the script representation of a given node. The
     * syntax of a node is <code>color:key</code>. <code>color</code> may either
     * take the values <code>b</code> or <code>r</code>.
     * 
     * @param node
     *            The script representation of the node to check
     * @return The color of the node.
     */
    public static boolean getColor(String node) {
        String[] split = node.split(":");
        if (split.length > 1) {
            switch (split[0]) {
            case RedBlackTree.RED:
                return RED;
            case RedBlackTree.BLACK:
                return BLACK;
            default:
                GlobalVariables.getParameters().logDebug(
                        "Ignored invalid color and set to BLACK");
                return BLACK;
            }
        } else
            return BLACK;
    }

    /**
     * A method for obtaining the key of a node in script representation, so
     * basically the color definition is removed.
     * 
     * @param node
     *            The node whose key is extracted.
     * @return The key of the node.
     */
    public static String getKey(String node) {
        String[] split = node.split(":");
        if (split.length > 1) {
            // Node is colored
            return split[1];
        } else
            // No color definition
            return split[0];
    }

    /*************************************************************************
     * Node helper methods
     *************************************************************************/
    // is node x red; false if x is null ?
    private boolean isRed(Node x) {
        if (x == null)
            return false;
        return (x.color == RED);
    }

    /**
     * number of node in subtree rooted at x; 0 if x is null
     * 
     * @param x
     * @return
     */
    private int size(Node x) {
        if (x == null)
            return 0;
        return x.N;
    }

    /**
     * In case the red black tree was obtained by transformation of a 2-3-4-tree
     * we do not know the size of the subtrees, since they are determined during
     * runtime. This method resets the sizes to their current values.
     */
    public void resetSizeRecursively() {
        sizeRecursively(root);
    }

    /**
     * Recursively sets the current subtree sizes.
     * 
     * @param node
     *            The node whose subtree size is determined
     * @return The size of the current subtree of <code>node</code>.
     */
    private int sizeRecursively(Node node) {
        int left = node.left != null ? 0 : sizeRecursively(node.left);
        int right = node.right != null ? 0 : sizeRecursively(node.right);
        node.N = left + right + 1;
        return node.N;
    }

    /*************************************************************************
     * Size methods
     *************************************************************************/

    // return number of key-value pairs in this symbol table
    public int size() {
        return size(root);
    }

    // is this symbol table empty?
    public boolean isEmpty() {
        return root == null;
    }

    /*************************************************************************
     * Standard BST search
     *************************************************************************/

    // value associated with the given key; null if no such key
    public Value get(Key key) {
        return get(root, key);
    }

    // value associated with the given key in subtree rooted at x; null if no
    // such key
    @SuppressWarnings("all")
    private Value get(Node x, Key key) {
        while (x != null) {
            int cmp = key.compareTo(x.key);
            if (cmp < 0)
                x = x.left;
            else if (cmp > 0)
                x = x.right;
            else
                return x.val;
        }
        return null;
    }

    // is there a key-value pair with the given key?
    public boolean contains(Key key) {
        return (get(key) != null);
    }

    // is there a key-value pair with the given key in the subtree rooted at x?
    // private boolean contains(Node x, Key key) {
    // return (get(x, key) != null);
    // }

    /*************************************************************************
     * Red-black insertion
     *************************************************************************/

    // insert the key-value pair; overwrite the old value with the new value
    // if the key is already present
    @Override
    public void insert(Key key, Value val) {
        root = put(root, key, val);
        root.color = BLACK;
        // assert check();
    }

    // insert the key-value pair in the subtree rooted at h
    @SuppressWarnings("all")
    private Node put(Node h, Key key, Value val) {
        if (h == null)
            return new Node(key, val, RED, 1);

        if (isRed(h.left) && isRed(h.right))
            flipColors(h);
        
        int cmp = key.compareTo(h.key);
        if (cmp < 0)
            h.left = put(h.left, key, val);
        else if (cmp > 0)
            h.right = put(h.right, key, val);
        else
            h.val = val;

        // fix-up any right-leaning links
        if (isRed(h.right) && !isRed(h.left))
            h = rotateLeft(h);
        if (isRed(h.left) && isRed(h.left.left))
            h = rotateRight(h);
        h.N = size(h.left) + size(h.right) + 1;

        return h;
    }

    /*************************************************************************
     * Red-black deletion
     *************************************************************************/

    // delete the key-value pair with the minimum key
    public void deleteMin() {
        if (isEmpty())
            throw new NoSuchElementException("BST underflow");

        // if both children of root are black, set root to red
        if (!isRed(root.left) && !isRed(root.right))
            root.color = RED;

        root = deleteMin(root);
        if (!isEmpty())
            root.color = BLACK;
        // assert check();
    }

    // delete the key-value pair with the minimum key rooted at h
    @SuppressWarnings("all")
    private Node deleteMin(Node h) {
        if (h.left == null)
            return null;

        if (!isRed(h.left) && !isRed(h.left.left))
            h = moveRedLeft(h);

        h.left = deleteMin(h.left);
        return balance(h);
    }

    // delete the key-value pair with the maximum key
    public void deleteMax() {
        if (isEmpty())
            throw new NoSuchElementException("BST underflow");

        // if both children of root are black, set root to red
        if (!isRed(root.left) && !isRed(root.right))
            root.color = RED;

        root = deleteMax(root);
        if (!isEmpty())
            root.color = BLACK;
        // assert check();
    }

    // delete the key-value pair with the maximum key rooted at h
    @SuppressWarnings("all")
    private Node deleteMax(Node h) {
        if (isRed(h.left))
            h = rotateRight(h);

        if (h.right == null)
            return null;

        if (!isRed(h.right) && !isRed(h.right.left))
            h = moveRedRight(h);

        h.right = deleteMax(h.right);

        return balance(h);
    }

    // delete the key-value pair with the given key
    @Override
    public void delete(Key key) {
        if (!contains(key)) {
            System.err.println("symbol table does not contain " + key);
            return;
        }

        // if both children of root are black, set root to red
        if (!isRed(root.left) && !isRed(root.right))
            root.color = RED;

        root = delete(root, key);
        if (!isEmpty())
            root.color = BLACK;
        // assert check();
    }

    // delete the key-value pair with the given key rooted at h
    @SuppressWarnings("all")
    private Node delete(Node h, Key key) {
        // assert contains(h, key);

        if (key.compareTo(h.key) < 0) {
            if (!isRed(h.left) && !isRed(h.left.left))
                h = moveRedLeft(h);
            h.left = delete(h.left, key);
        } else {
            if (isRed(h.left))
                h = rotateRight(h);
            if (key.compareTo(h.key) == 0 && (h.right == null))
                return null;
            if (!isRed(h.right) && !isRed(h.right.left))
                h = moveRedRight(h);
            if (key.compareTo(h.key) == 0) {
                Node x = min(h.right);
                h.key = x.key;
                h.val = x.val;
                // h.val = get(h.right, min(h.right).key);
                // h.key = min(h.right).key;
                h.right = deleteMin(h.right);
            } else
                h.right = delete(h.right, key);
        }
        return balance(h);
    }

    /*************************************************************************
     * red-black tree helper functions
     *************************************************************************/

    // make a left-leaning link lean to the right
    private Node rotateRight(Node h) {
        // assert (h != null) && isRed(h.left);
        Node x = h.left;
        h.left = x.right;
        x.right = h;
        x.color = x.right.color;
        x.right.color = RED;
        x.N = h.N;
        h.N = size(h.left) + size(h.right) + 1;
        return x;
    }

    // make a right-leaning link lean to the left
    private Node rotateLeft(Node h) {
        // assert (h != null) && isRed(h.right);
        Node x = h.right;
        h.right = x.left;
        x.left = h;
        x.color = x.left.color;
        x.left.color = RED;
        x.N = h.N;
        h.N = size(h.left) + size(h.right) + 1;
        return x;
    }

    // flip the colors of a node and its two children
    private void flipColors(Node h) {
        // h must have opposite color of its two children
        // assert (h != null) && (h.left != null) && (h.right != null);
        // assert (!isRed(h) && isRed(h.left) && isRed(h.right))
        // || (isRed(h) && !isRed(h.left) && !isRed(h.right));
        h.color = !h.color;
        h.left.color = !h.left.color;
        h.right.color = !h.right.color;
    }

    // Assuming that h is red and both h.left and h.left.left
    // are black, make h.left or one of its children red.
    @SuppressWarnings("all")
    private Node moveRedLeft(Node h) {
        // assert (h != null);
        // assert isRed(h) && !isRed(h.left) && !isRed(h.left.left);

        flipColors(h);
        if (isRed(h.right.left)) {
            h.right = rotateRight(h.right);
            h = rotateLeft(h);
        }
        return h;
    }

    // Assuming that h is red and both h.right and h.right.left
    // are black, make h.right or one of its children red.
    @SuppressWarnings("all")
    private Node moveRedRight(Node h) {
        // assert (h != null);
        // assert isRed(h) && !isRed(h.right) && !isRed(h.right.left);
        flipColors(h);
        if (isRed(h.left.left)) {
            h = rotateRight(h);
        }
        return h;
    }

    // restore red-black tree invariant
    @SuppressWarnings("all")
    private Node balance(Node h) {
        // assert (h != null);

        if (isRed(h.right))
            h = rotateLeft(h);
        if (isRed(h.left) && isRed(h.left.left))
            h = rotateRight(h);
        if (isRed(h.left) && isRed(h.right))
            flipColors(h);

        h.N = size(h.left) + size(h.right) + 1;
        return h;
    }

    /*************************************************************************
     * Utility functions
     *************************************************************************/

    // height of tree (1-node tree has height 0)
    public int height() {
        return height(root);
    }

    private int height(Node x) {
        if (x == null)
            return -1;
        return 1 + Math.max(height(x.left), height(x.right));
    }

    /*************************************************************************
     * Ordered symbol table methods.
     *************************************************************************/

    // the smallest key; null if no such key
    public Key min() {
        if (isEmpty())
            return null;
        return min(root).key;
    }

    // the smallest key in subtree rooted at x; null if no such key
    private Node min(Node x) {
        // assert x != null;
        if (x.left == null)
            return x;
        else
            return min(x.left);
    }

    // the largest key; null if no such key
    public Key max() {
        if (isEmpty())
            return null;
        return max(root).key;
    }

    // the largest key in the subtree rooted at x; null if no such key
    private Node max(Node x) {
        // assert x != null;
        if (x.right == null)
            return x;
        else
            return max(x.right);
    }

    // the largest key less than or equal to the given key
    public Key floor(Key key) {
        Node x = floor(root, key);
        if (x == null)
            return null;
        else
            return x.key;
    }

    // the largest key in the subtree rooted at x less than or equal to the
    // given key
    private Node floor(Node x, Key key) {
        if (x == null)
            return null;
        int cmp = key.compareTo(x.key);
        if (cmp == 0)
            return x;
        if (cmp < 0)
            return floor(x.left, key);
        Node t = floor(x.right, key);
        if (t != null)
            return t;
        else
            return x;
    }

    // the smallest key greater than or equal to the given key
    public Key ceiling(Key key) {
        Node x = ceiling(root, key);
        if (x == null)
            return null;
        else
            return x.key;
    }

    // the smallest key in the subtree rooted at x greater than or equal to the
    // given key
    private Node ceiling(Node x, Key key) {
        if (x == null)
            return null;
        int cmp = key.compareTo(x.key);
        if (cmp == 0)
            return x;
        if (cmp > 0)
            return ceiling(x.right, key);
        Node t = ceiling(x.left, key);
        if (t != null)
            return t;
        else
            return x;
    }

    // the key of rank k
    public Key select(int k) {
        if (k < 0 || k >= size())
            return null;
        Node x = select(root, k);
        return x.key;
    }

    // the key of rank k in the subtree rooted at x
    private Node select(Node x, int k) {
        // assert x != null;
        // assert k >= 0 && k < size(x);
        int t = size(x.left);
        if (t > k)
            return select(x.left, k);
        else if (t < k)
            return select(x.right, k - t - 1);
        else
            return x;
    }

    // number of keys less than key
    public int rank(Key key) {
        return rank(key, root);
    }

    // number of keys less than key in the subtree rooted at x
    private int rank(Key key, Node x) {
        if (x == null)
            return 0;
        int cmp = key.compareTo(x.key);
        if (cmp < 0)
            return rank(key, x.left);
        else if (cmp > 0)
            return 1 + size(x.left) + rank(key, x.right);
        else
            return size(x.left);
    }

    /***********************************************************************
     * Range count and range search.
     ***********************************************************************/

    // all of the keys, as an Iterable
    public Iterable<Key> keys() {
        return keys(min(), max());
    }

    // the keys between lo and hi, as an Iterable
    public Iterable<Key> keys(Key lo, Key hi) {
        // Queue<Key> queue = new Queue<Key>(){};
        Queue<Key> queue = new LinkedList<Key>() {
            private static final long serialVersionUID = 8391204791397991174L;
        };
        // if (isEmpty() || lo.compareTo(hi) > 0) return queue;
        keys(root, queue, lo, hi);
        return queue;
    }

    // add the keys between lo and hi in the subtree rooted at x
    // to the queue
    private void keys(Node x, Queue<Key> queue, Key lo, Key hi) {
        if (x == null)
            return;
        int cmplo = lo.compareTo(x.key);
        int cmphi = hi.compareTo(x.key);
        if (cmplo < 0)
            keys(x.left, queue, lo, hi);
        // if (cmplo <= 0 && cmphi >= 0) queue.enqueue(x.key);
        if (cmplo <= 0 && cmphi >= 0)
            queue.add(x.key);
        if (cmphi > 0)
            keys(x.right, queue, lo, hi);

    }

    // number keys between lo and hi
    public int size(Key lo, Key hi) {
        if (lo.compareTo(hi) > 0)
            return 0;
        if (contains(hi))
            return rank(hi) - rank(lo) + 1;
        else
            return rank(hi) - rank(lo);
    }

    /*************************************************************************
     * Check integrity of red-black BST data structure
     *************************************************************************/
    @SuppressWarnings("unused")
    private boolean check() {
        if (!isBST())
            GlobalVariables.getParameters().logInfo("Not in symmetric order");
        if (!isSizeConsistent())
            GlobalVariables.getParameters().logInfo(
                    "Subtree counts not consistent");
        if (!isRankConsistent())
            GlobalVariables.getParameters().logInfo("Ranks not consistent");
        if (!is23())
            GlobalVariables.getParameters().logInfo("Not a 2-3 tree");
        if (!isBalanced())
            GlobalVariables.getParameters().logInfo("Not balanced");
        return isBST() && isSizeConsistent() && isRankConsistent() && is23()
                && isBalanced();
    }

    // does this binary tree satisfy symmetric order?
    // Note: this test also ensures that data structure is a binary tree since
    // order is strict
    private boolean isBST() {
        return isBST(root, null, null);
    }

    // is the tree rooted at x a BST with all keys strictly between min and max
    // (if min or max is null, treat as empty constraint)
    // Credit: Bob Dondero's elegant solution
    private boolean isBST(Node x, Key min, Key max) {
        if (x == null)
            return true;
        if (min != null && x.key.compareTo(min) <= 0)
            return false;
        if (max != null && x.key.compareTo(max) >= 0)
            return false;
        return isBST(x.left, min, x.key) && isBST(x.right, x.key, max);
    }

    // are the size fields correct?
    private boolean isSizeConsistent() {
        return isSizeConsistent(root);
    }

    private boolean isSizeConsistent(Node x) {
        if (x == null)
            return true;
        if (x.N != size(x.left) + size(x.right) + 1)
            return false;
        return isSizeConsistent(x.left) && isSizeConsistent(x.right);
    }

    // check that ranks are consistent
    private boolean isRankConsistent() {
        for (int i = 0; i < size(); i++)
            if (i != rank(select(i)))
                return false;
        for (Key key : keys())
            if (key.compareTo(select(rank(key))) != 0)
                return false;
        return true;
    }

    // Does the tree have no red right links, and at most one (left)
    // red links in a row on any path?
    private boolean is23() {
        return is23(root);
    }

    private boolean is23(Node x) {
        if (x == null)
            return true;
        if (isRed(x.right))
            return false;
        if (x != root && isRed(x) && isRed(x.left))
            return false;
        return is23(x.left) && is23(x.right);
    }

    // do all paths from root to leaf have same number of black edges?
    private boolean isBalanced() {
        int black = 0; // number of black links on path from root to min
        Node x = root;
        while (x != null) {
            if (!isRed(x))
                black++;
            x = x.left;
        }
        return isBalanced(root, black);
    }

    // does every path from the root to a leaf have the given number of black
    // links?
    @SuppressWarnings("all")
    private boolean isBalanced(Node x, int black) {
        if (x == null)
            return black == 0;
        if (!isRed(x))
            black--;
        return isBalanced(x.left, black) && isBalanced(x.right, black);
    }

    /**
     * Returns an equivalent 2-3-4 tree of this red-black tree.
     * 
     * @return An equivalent 2-3-4 tree of this red-black tree.
     */
    public Tree234Impl<Key, Value> get234tree() {
        Tree234Impl<Key, Value> tree = new Tree234Impl<>();
        DirtyReflectionHacks.setRoot(tree, to234node(root, null));
        return tree;
    }

    /**
     * A method for recursively transforming a red-black subtree into a 2-3-4
     * tree.
     * 
     * @param node
     *            The current red-black node that is handled
     * @param parent
     *            The 2-3-4-parent to be of the node that is transformed
     * @return A 2-3-4 subtree of this red-black tree.
     */
    @SuppressWarnings("unchecked")
    public Tree234Impl<Key, Key>.Node to234node(Node node,
            Tree234Impl<Key, Key>.Node parent) {

        // New 2-3-4-note based on current node
        Tree234Impl<Key, Key>.Node current = DirtyReflectionHacks.new234Node(
                parent, node.key);

        // Left subtree
        if (node.left != null) {
            if (node.left.color == RED) {
                DirtyReflectionHacks.addKeys(current, node.left.key);
                if (node.left.left != null)
                    DirtyReflectionHacks.addChild(current,
                            to234node(node.left.left, current));
                if (node.left.right != null)
                    DirtyReflectionHacks.addChild(current,
                            to234node(node.left.right, current));
            } else {
                DirtyReflectionHacks.addChild(current,
                        to234node(node.left, current));
            }
        }

        // Right subtree
        if (node.right != null) {
            if (node.right.color == RED) {
                DirtyReflectionHacks.addKeys(current, node.right.key);
                if (node.right.left != null)
                    DirtyReflectionHacks.addChild(current,
                            to234node(node.right.left, current));
                if (node.right.right != null)
                    DirtyReflectionHacks.addChild(current,
                            to234node(node.right.right, current));
            } else {
                DirtyReflectionHacks.addChild(current,
                        to234node(node.right, current));
            }
        }

        return current;
    }
}
