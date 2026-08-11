/*
 * File name:        AbstractSearchTree.java (package veryFastPDF.algorithms.searchTree)
 * Author(s):        bwpc
 * Java version:     8.0 (at generation time)
 * Generation date:  30.07.2015 (16:42:34)
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

package veryFastPDF.algorithms.searchTree;

/**
 * An abstract search tree that serves as generic parent to all search tree
 * implementations for hosting commons methods fields etc.
 * 
 * @author marlon.braun
 * @param <Key>
 *            The type of keys stored in the tree
 * @param <Value>
 *            The type of values stored in the tree
 * @param <Node>
 *            The type of nodes the implementing tree uses. We need this
 *            information for declaring the root correctly.
 */
public abstract class AbstractSearchTree<Key extends Comparable<Key>, Value, Node extends AbstractSearchTree<Key, Value, Node>.AbstractNode> {

    /**
     * The root of the tree.
     */
    protected Node root;

    /**
     * 
     * @author bwpc
     */
    public abstract class AbstractNode {

        /**
         * The DOT representation of this node and the entire subtree it
         * constitutes.
         * 
         * @return The DOT representation of this node and its subtree
         */
        public abstract String getDOT();

        /**
         * The script representation of this node and entire subtree it
         * constitutes
         * 
         * @return The script representation of this node and its subtree
         */
        public abstract String getScript();

    }

    /**
     * Insert a key with the given key and value in the tree.
     * 
     * @param key
     *            The key to insert.
     * @param value
     *            The value of this key.
     */
    public abstract void insert(Key key, Value value);

    /**
     * Inserts the keys and their corresponding values in this tree.
     * 
     * @param keys
     *            The keys to insert.
     * @param values
     *            The corresponding values of the keys.
     */
    public void insertAll(Key[] keys, Value[] values) {
        if (keys.length != values.length) {
            throw new RuntimeException(
                    "The number of keys and corresponding values to insert must be the same.");
        }
        for (int i = 0; i < keys.length; i++) {
            insert(keys[i], values[i]);
        }
    }

    /**
     * Deletes the given key from the search tree.
     * 
     * @param key
     *            The key that is deleted.
     */
    public abstract void delete(Key key);

    /**
     * A method for obtaining the DOT representation of this tree.
     * 
     * @return The DOT representation of this tree.
     */
    public abstract String getDOT();

    /**
     * Get the script representation of this tree.
     * 
     * @return The script representation of this tree.
     */
    public abstract String getScript();
}
