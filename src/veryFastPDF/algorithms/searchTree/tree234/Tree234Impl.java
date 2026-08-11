/*
 * File name:        TwoThreeFourTreeST.java (package eas.math.fundamentalAlgorithms.graphBased.algorithms.twothreefourtree)
 * Author(s):        Marlon Braun
 * Java version:     8.0 (at generation time)
 * Generation date:  15.10.2014 (14:32:29)
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

package veryFastPDF.algorithms.searchTree.tree234;

import java.util.Iterator;
import java.util.Map.Entry;
import java.util.NoSuchElementException;

import veryFastPDF.algorithms.searchTree.AbstractSearchTree;
import veryFastPDF.algorithms.searchTree.DirtyReflectionHacks;
import veryFastPDF.algorithms.searchTree.IndexedTreeMap;
import veryFastPDF.algorithms.searchTree.IndexedTreeSet;
import veryFastPDF.algorithms.searchTree.redblacktree.RedBlackBST;

/**
 * This is the actual implementation of the 2-3-4 tree in Java. It uses top down
 * partitioning in splitting each 4-node that is traversed while inserting a new
 * key in the tree. The tree stores only keys that have no idea associated with
 * them.
 * 
 * <p>
 * For the time being we allow duplicates
 * 
 * @author marlon.braun
 * @param <Key>
 *            The elements that are inserted in the tree
 */
public class Tree234Impl<Key extends Comparable<Key>, Value> extends
        AbstractSearchTree<Key, Value, Tree234Impl<Key, Value>.Node> {

    /**
     * The order of the tree specifies how many children a {@link Node} can
     * have.
     */
    private static final int ORDER = 4;

    /**
     * A single node in the tree that can hold up to 3 keys and can have up to 4
     * children.
     * 
     * @author marlon.braun
     */
    public class Node extends AbstractSearchTree<Key, Value, Node>.AbstractNode
            implements Comparable<Node> {

        /**
         * Stores the key values pairs of this node.
         */
        private final IndexedTreeMap<Key, Value> keyValues = new IndexedTreeMap<>();

        /**
         * The child nodes of this node. The index of each entry in the list
         * corresponds to its position in the node.
         */
        private final IndexedTreeSet<Node> children = new IndexedTreeSet<>();

        /**
         * The father of this node. We this information when rearranging the
         * tree after deletion.
         */
        private Node parent = null;

        /**
         * Creates a new node that has <code>key</code> as only key.
         * 
         * @param key
         *            A key stored in this node.
         * @param value
         *            The value of the key stored in this node.
         */
        public Node(Key key, Value value, Node parent) {
            keyValues.put(key, value);
            this.parent = parent;
        }

        @Override
        public String getDOT() {
            String nodeName = "\"" + toString() + "\"";

            String dot = nodeName + "[label=\"" + getGraphLabel() + "\"];";

            for (Node child : children) {
                dot += nodeName + " -> " + "\"" + child.toString() + "\";\n";
                dot += child.getDOT();
            }

            return dot;
        }

        @Override
        public String getScript() {
            String ret = "";
            if (hasChildren()) {
                ret = scriptRepresentation() + " => ";
                for (Node child : children) {
                    ret += child.scriptRepresentation() + "|";
                }
                ret = ret.substring(0, ret.length() - 1);
                ret += ";\n";

                for (Node child : children) {
                    ret += child.getScript();
                }
            }
            return ret;
        }

        /**
         * Order is based on a lexicographical comparison of keys. If key sets
         * are of different lengths, the longer key set is considered greater.
         */
        @Override
        public int compareTo(Tree234Impl<Key, Value>.Node o) {
            // Compare all joint indexes with each other
            Iterator<Key> it = o.keyValues.keySet().iterator();
            for (Key thisKey : keyValues.keySet()) {
                Key otherKey = null;
                try {
                    otherKey = it.next();
                } catch (NoSuchElementException e) {
                    // Node o has less keys than this node, however the first k
                    // positions agree. Then this node is larger.
                    return 1;
                }
                int comp = thisKey.compareTo(otherKey);
                if (comp != 0)
                    return comp;
            }
            // Only first k positions agree. Now check for length
            if (o.keyValues.size() > keyValues.size())
                return -1;
            // Nodes have some number of keys and keys all agree
            return 0;
        }

        @Override
        public String toString() {
            String ret = "";
            for (Key key : keyValues.keySet()) {
                ret += key.toString() + " ";
            }
            return ret.trim();
        }

        /**
         * Method for verifying if this node has children.
         * 
         * @return <code>true</code> if, and only if this node has children.
         *         <code>false</code> otherwise.
         */
        public boolean hasChildren() {
            return children.size() > 0;
        }

        /**
         * The size of the node corresponds to the number of keys it holds.
         * 
         * @return The number of keys this node holds.
         */
        public int size() {
            return keyValues.size();
        }

        /**
         * Just a convenience method for fast adding of key value pairs.
         * 
         * @param entry
         *            The entry to add.
         */
        private void put(Entry<Key, Value> entry) {
            keyValues.put(entry.getKey(), entry.getValue());
        }

        /**
         * The order of the tree corresponds to the number of children it holds.
         * 
         * @return The number of children this node holds.
         */
        public int order() {
            return children.size();
        }

        /**
         * The label of the node in the DOT graph. We use a record object to box
         * each value in a node into its custom rectangle.
         * 
         * @return The label of this node in the DOT
         */
        private String getGraphLabel() {
            String ret = "";
            for (Key key : keyValues.keySet())
                ret += key.toString() + "|";
            ret = ret.substring(0, ret.length() - 1);
            return ret;
        }

        /**
         * Returns the script representation of only this node.
         * 
         * @return The script representation of this particular node.
         */
        public String scriptRepresentation() {
            String ret = "[";
            for (Key key : keyValues.keySet()) {
                ret += key + ",";
            }
            ret = ret.substring(0, ret.length() - 1);
            ret += "]";
            return ret;
        }

        /**
         * A method for checking whether the key range of this node intersects
         * with the key range of another node.
         * 
         * @param o
         *            The node which is checked for key range intersection with
         *            this node.
         * @return <code>true</code> if the key ranges of both nodes intersect.
         */
        public boolean intersect(Tree234Impl<Key, Value>.Node o) {
            Key thisSmallestKey = keyValues.firstKey();
            Key otherSmallestKey = o.keyValues.firstKey();
            if (thisSmallestKey.compareTo(otherSmallestKey) < 1) {
                if (keyValues.lastKey().compareTo(otherSmallestKey) > -1)
                    return true;
                else
                    return false;
            } else {
                if (thisSmallestKey.compareTo(o.keyValues.lastKey()) < 1)
                    return true;
                else
                    return false;
            }
        }

        /**
         * Checks whether this node has a left sibling.
         * 
         * @return <code>true</code> if this node has a left sibling.
         */
        public boolean hasLeftSibling() {
            return leftSibling() != null;
        }

        /**
         * Obtains the left sibling of this node.
         * 
         * @return The left sibling of this node or <code>null</code> if the
         *         sibling does not exist.
         */
        public Node leftSibling() {
            if (this.parent == null)
                return null;
            return this.parent.children.lower(this);
        }

        /**
         * Checks whether this node has a right sibling.
         * 
         * @return <code>true</code> if this node has a right sibling.
         */
        public boolean hasRightSibling() {
            return rightSibling() != null;
        }

        /**
         * Obtains the right sibling of this node.
         * 
         * @return The right sibling of this node or <code>null</code> if the
         *         sibling does not exist.
         */
        public Node rightSibling() {
            if (this.parent == null)
                return null;
            return this.parent.children.higher(this);
        }

        /**
         * Obtains the index of this child in the parent node.
         * 
         * @return The index of this child in the parent node or <code>-1</code>
         *         if it does not have a parent.
         */
        public int getIndexInParent() {
            if (this.parent == null) {
                return -1;
            }
            return this.parent.children.getIndex(this);
        }

        private void merge(Node node) {
            if (this.size() + node.size() >= ORDER) {
                throw new RuntimeException(
                        String.format(
                                "Cannot merge nodes as merger would result in overflow. Maximum size: %s. Combined size: %s.",
                                ORDER - 1, this.size() + node.size()));
            }
            this.keyValues.putAll(node.keyValues);
            for (Node child : node.children) {
                child.parent = this.parent;
                children.add(child);
            }
            node.parent.children.remove(node);
        }
    }

    /**
     * TODO: DOCUMENT ME
     * 
     * @param script
     * @return
     */
    public static String[] getKeysFromScriptRepresentation(String script) {
        return script.substring(script.indexOf("[") + 1, script.indexOf("]"))
                .split(",");
    }

    /**
     * A method for setting the parents of the nodes in this tree.
     */
    public void setParents() {
        setParents(root);
    }

    /**
     * A method for recursively setting the parents of the nodes in the provided
     * subtree correctly.
     * 
     * @param node
     *            The current subtree
     */
    private void setParents(Node node) {
        if (node == null)
            return;
        else {
            if (node.hasChildren()) {
                for (Node child : node.children) {
                    child.parent = node;
                    setParents(child);
                }
            }
        }
    }

    @Override
    public void delete(Key key) {
        delete(root, key);
    }

    /**
     * A method for deleting a key from the given subtree.
     * 
     * TODO: This is unreadable gibberish. Think about refactoring the code.
     * 
     * @param node
     *            The subtree from which the key is deleted
     * @param parent
     *            The parent of the current node
     * @param key
     *            The key that is deleted
     */
    private void delete(Node node, Key key) {
        if (node == null)
            return;
        // Key found now delete
        if (node.keyValues.containsKey(key)) {
            int index = node.keyValues.getIndex(key);
            if (node.hasChildren()) {
                // Least element in right subtree
                Node min = min(node.children.getKey(index + 1));
                Key minKey = min.keyValues.firstKey();
                node.keyValues.put(minKey, min.keyValues.get(minKey));
                node.keyValues.remove(key);
                // Start recursion
                delete(min, minKey);
            } else {
                Node ls = node.leftSibling();
                Node rs = node.rightSibling();
                node.keyValues.remove(key);
                // Leaf node
                // Deletion has emptied the node
                if (node.keyValues.isEmpty()) {
                    fixTree(node, ls, rs);
//                    fixTree(node);
                }
            }
        } else {
            delete(nextChild(node, key), key);
        }
    }

//    private void fixTree(Node empty) {
    private void fixTree(Node empty, Node leftSibling, Node rightSibling) {
        // No fixing needed
        if (empty == null || empty.size() != 0)
            return;

        // Check if we're at the root
        if (empty.equals(root)) {
            root = empty.children.first();
            return;
        }
        
        Node ls = null, rs = null;
        if(empty.parent!=null) {
            ls = empty.parent.leftSibling();
            rs = empty.parent.rightSibling();
        }
        
        // Right neighbor is first choice
//        if (empty.hasRightSibling()) {
        if (rightSibling != null) {
//            if (empty.rightSibling().size() > 1) {
            if (rightSibling.size() > 1) {
//                rotateLeft(empty);
                rotateLeft(empty, rightSibling);
            } else {
//                mergeLeft(empty);
                mergeLeft(empty, rightSibling);
            }
        } else {
//            if (empty.leftSibling().size() > 1) {
            if (leftSibling.size() > 1) {
//                rotateRight(empty);
                rotateRight(empty, leftSibling);
            } else {
//                mergeRight(empty);
                mergeRight(empty, leftSibling);
            }
        }
//        fixTree(empty.parent);
        fixTree(empty.parent, ls, rs);
    }

    /**
     * Rotates keys in the tree such that <code>receiver</code> receives the
     * next greater key from its parent. The key in the parent is replaced by
     * the smallest key of the receiver's right sibling.
     * 
     * @param receiver
     *            The node towards which the rotation is performed
     * @return <code>true</code> if rotation was performed, <code>false</code>
     *         otherwise. Rotation is not performed if not left neighbor exists.
     */
//    private boolean rotateLeft(Node receiver) {
    private boolean rotateLeft(Node receiver, Node rightSibling) {
//        if (receiver.hasRightSibling()) {
        if (rightSibling!=null) {
            Node parent = receiver.parent;
//            Node rightSibling = receiver.rightSibling();

            // Entries for rotation
            Entry<Key, Value> parentEntry = parent.keyValues
                    .lowerEntry(rightSibling.keyValues.firstKey());
            Entry<Key, Value> siblingEntry = rightSibling.keyValues
                    .firstEntry();

            // Perform rotation of keys
            receiver.put(parentEntry);
            parent.keyValues.remove(parentEntry.getKey());
            parent.put(siblingEntry);
            rightSibling.keyValues.remove(siblingEntry.getKey());

            // Fix child relations
            if (rightSibling.hasChildren()) {
                Node newChild = rightSibling.children.pollLast();
                newChild.parent = receiver;
                receiver.children.add(newChild);
            }
            return true;
        } else
            return false;
    }

    /**
     * Rotates keys in the tree such that <code>receiver</code> receives the
     * next greater key from its parent. The key in the parent is replaced by
     * the smallest key of the receiver's right sibling.
     * 
     * @param receiver
     *            The node towards which the rotation is performed
     * @return <code>true</code> if rotation was performed, <code>false</code>
     *         otherwise. Rotation is not performed if no right neighbor exists.
     */
//    private boolean rotateRight(Node receiver) {
    private boolean rotateRight(Node receiver, Node leftSibling) {
//        if (receiver.hasLeftSibling()) {
        if (leftSibling != null) {
            Node parent = receiver.parent;
//            Node leftSibling = receiver.leftSibling();

            // Entries for rotation
            Entry<Key, Value> parentEntry = parent.keyValues
                    .higherEntry(leftSibling.keyValues.lastKey());
            Entry<Key, Value> siblingEntry = leftSibling.keyValues.lastEntry();

            // Perform rotation of keys
            receiver.put(parentEntry);
            parent.keyValues.remove(parentEntry.getKey());
            parent.put(siblingEntry);
            leftSibling.keyValues.remove(siblingEntry.getKey());

            // Fix child relations
            if (leftSibling.hasChildren()) {
                Node newChild = leftSibling.children.pollLast();
                newChild.parent = receiver;
                receiver.children.add(newChild);
            }
            return true;
        } else
            return false;
    }

//    private boolean mergeRight(Node merger) {
    private boolean mergeRight(Node merger, Node leftSibling) {
        if (leftSibling != null) {
//        if (merger.hasLeftSibling()) {
            Node parent = merger.parent;
//            Node leftSibling = merger.leftSibling();

            // Entries for rotation
            Entry<Key, Value> parentEntry = parent.keyValues
                    .higherEntry(leftSibling.keyValues.firstKey());

            // Merge
            merger.put(parentEntry);
            parent.keyValues.remove(parentEntry.getKey());
            merger.merge(leftSibling);

            return true;
        }
        return false;
    }

//    private boolean mergeLeft(Node merger) {
    private boolean mergeLeft(Node merger, Node rightSibling) {
//        if (merger.hasRightSibling()) {
        if (rightSibling!=null) {
            Node parent = merger.parent;
//            Node rightSibling = merger.rightSibling();

            // Entries for rotation
            Entry<Key, Value> parentEntry = parent.keyValues
                    .lowerEntry(rightSibling.keyValues.lastKey());

            // Merge
            merger.put(parentEntry);
            parent.keyValues.remove(parentEntry.getKey());
            merger.merge(rightSibling);

            return true;
        }
        return false;
    }

    /**
     * Returns the node harboring the minimum key in the given subtree.
     * 
     * @param node
     *            The current subtree we look at.
     * @return The node having the minimum element in the subtree.
     */
    private Node min(Node node) {
        if (node.hasChildren())
            return min(node.children.first());
        else
            return node;
    }

    /**
     * Returns the node harboring the maximum key in the given subtree.
     * 
     * @param node
     *            The current subtree we look at.
     * @return The node having the maximum element in the subtree
     */
    @SuppressWarnings("unused")
    private Node max(Node node) {
        if (node.hasChildren())
            return max(node.children.last());
        else
            return node;
    }

    /**
     * Method for adding a new key to this tree.
     * 
     * @param key
     *            The key that is added to the tree.
     */
    @Override
    public void insert(Key key, Value value) {
        if (root != null)
            insert(root, key, value);
        else
            root = new Node(key, value, null);
    }

    private void insert(Node subtree, Key key, Value value) {
        // Top down division if node is of size 4
        if (subtree.size() == ORDER - 1) {

            // Middle element of the current 4-node we are looking at.
            Key medianKey = subtree.keyValues.getKey(1);

            // The current node is split into a left and right neighbor
            Key leftKey = subtree.keyValues.getKey(0);
            Key rightKey = subtree.keyValues.getKey(2);
            Node left = new Node(leftKey, subtree.keyValues.get(leftKey),
                    subtree.parent);
            Node right = new Node(rightKey, subtree.keyValues.get(rightKey),
                    subtree.parent);

            // Split children among left and right neighbor
            if (subtree.hasChildren()) {
                left.children.add(subtree.children.getKey(0));
                left.children.add(subtree.children.getKey(1));
                right.children.add(subtree.children.getKey(2));
                right.children.add(subtree.children.getKey(3));
                // Reset parents
                left.children.getKey(0).parent = left;
                left.children.getKey(1).parent = left;
                right.children.getKey(0).parent = right;
                right.children.getKey(1).parent = right;
            }

            // Median key becomes new root
            if (subtree.parent == null) {
                root = new Node(medianKey, subtree.keyValues.get(medianKey),
                        null);
                root.children.add(left);
                root.children.add(right);
                // Reset parents
                left.parent = root;
                right.parent = root;
            } else {
                // Remove 4-node from parent
                subtree.parent.children.remove(subtree);
                // Median key is moved up in parent node
                subtree.parent.keyValues.put(medianKey,
                        subtree.keyValues.get(medianKey));
                // Insert children at correct position
                subtree.parent.children.add(left);
                subtree.parent.children.add(right);
            }

            // Subtree to continue search
            int flag = key.compareTo(medianKey);
            if (flag < 0)
                insert(left, key, value);
            else
                insert(right, key, value);

            return;
        }

        // Traverse tree recursively
        if (subtree.hasChildren()) {
            // Obtain the correct position in the key list
            Key higherKey = subtree.keyValues.higherKey(key);
            int index;
            if (higherKey != null)
                index = subtree.keyValues.getIndex(higherKey);
            else
                index = subtree.children.size() - 1;
            insert(subtree.children.getKey(index), key, value);
        } else {
            subtree.keyValues.put(key, value);
        }
    }

    /**
     * TODO: DOCUMENT ME
     * 
     * @param node
     * @param key
     * @return
     */
    private Node nextChild(Node node, Key key) {
        if (node == null || !node.hasChildren())
            return null;
        int index = 0;
        Iterator<Key> it = node.keyValues.keySet().iterator();
        while (it.hasNext() && it.next().compareTo(key) < 0)
            index++;
        return node.children.getKey(index);
    }

    /**
     * Returns the DOT representation of this 2-3-4 tree
     * 
     * @return The DOT representation of this 2-3-4 tree
     */
    @Override
    public String getDOT() {
        String ret = "digraph G {\n";
        ret += "node [shape=record];";
        if (root.hasChildren())
            ret += root.getDOT();
        else
            ret += "\"" + root.toString() + "\"";

        ret += "}";

        return ret;
    }

    @Override
    public String getScript() {
        if (root.hasChildren())
            return root.getScript();
        else
            return root.scriptRepresentation();
    }

    /**
     * Computes an equivalent left-leaning red-black tree representation of this
     * 2-3-4 tree.
     * 
     * @return An equivalent left-leaning red-black tree of this 2-3-4 tree
     */
    public RedBlackBST<Key, Value> getRedBlackTree() {
        RedBlackBST<Key, Value> redBlack = new RedBlackBST<>();
        RedBlackBST<Key, Value>.Node newRoot = toRedBlackNode(root);
        if (newRoot != null)
            DirtyReflectionHacks.setRoot(redBlack, newRoot);
        return redBlack;
    }

    @SuppressWarnings({ "unchecked" })
    private RedBlackBST<Key, Value>.Node toRedBlackNode(
            Tree234Impl<Key, Value>.Node node) {
        if (node == null)
            return null;

        RedBlackBST<Key, Value>.Node rbNode = null;

        // 4-Node. Median key becomes parent node of left and right key. Left
        // and right key become red nodes.
        if (node.size() == ORDER - 1) {
            // Obtain keys
            Key leftKey = node.keyValues.getKey(0);
            Key medianKey = node.keyValues.getKey(1);
            Key rightKey = node.keyValues.getKey(2);

            // Put keys in nodes
            rbNode = DirtyReflectionHacks.newRedBlackNode(medianKey,
                    RedBlackBST.BLACK, 3);
            RedBlackBST<Key, Value>.Node left = DirtyReflectionHacks
                    .newRedBlackNode(leftKey, RedBlackBST.RED, 1);
            RedBlackBST<Key, Value>.Node right = DirtyReflectionHacks
                    .newRedBlackNode(rightKey, RedBlackBST.RED, 1);

            // Set left and right neighbors
            DirtyReflectionHacks.setLeftChild(rbNode, left);
            DirtyReflectionHacks.setRightChild(rbNode, right);

            if (node.hasChildren()) {
                // Get children of child nodes
                RedBlackBST<Key, Value>.Node leftleft = toRedBlackNode(node.children
                        .getKey(0));
                RedBlackBST<Key, Value>.Node leftright = toRedBlackNode(node.children
                        .getKey(1));
                RedBlackBST<Key, Value>.Node rightleft = toRedBlackNode(node.children
                        .getKey(2));
                RedBlackBST<Key, Value>.Node rightright = toRedBlackNode(node.children
                        .getKey(3));

                // Set children of child nodes
                DirtyReflectionHacks.setLeftChild(left, leftleft);
                DirtyReflectionHacks.setRightChild(left, leftright);
                DirtyReflectionHacks.setLeftChild(right, rightleft);
                DirtyReflectionHacks.setRightChild(right, rightright);

                // Fix subtreeCounts
                DirtyReflectionHacks.fixSubtreeCount(left);
                DirtyReflectionHacks.fixSubtreeCount(right);
                DirtyReflectionHacks.fixSubtreeCount(rbNode);
            }
        }
        // 3-Node. Right element becomes parent. Left element is the new left
        // leaning child. The first two children of the 2-3-4-node become
        // children of the left child and the third child becomes the right
        // child of the parent red-black-node
        if (node.size() == ORDER - 2) {
            // Obtain keys
            Key leftKey = node.keyValues.getKey(0);
            Key medianKey = node.keyValues.getKey(1);

            // Put keys in nodes
            rbNode = DirtyReflectionHacks.newRedBlackNode(medianKey,
                    RedBlackBST.BLACK, 2);
            RedBlackBST<Key, Value>.Node left = DirtyReflectionHacks
                    .newRedBlackNode(leftKey, RedBlackBST.RED, 1);

            // Set child
            DirtyReflectionHacks.setLeftChild(rbNode, left);

            if (node.hasChildren()) {
                // Generate child nodes
                RedBlackBST<Key, Value>.Node leftleft = toRedBlackNode(node.children
                        .getKey(0));
                RedBlackBST<Key, Value>.Node leftright = toRedBlackNode(node.children
                        .getKey(1));
                RedBlackBST<Key, Value>.Node right = toRedBlackNode(node.children
                        .getKey(2));

                // Set child nodes
                DirtyReflectionHacks.setLeftChild(left, leftleft);
                DirtyReflectionHacks.setRightChild(left, leftright);
                DirtyReflectionHacks.setRightChild(rbNode, right);

                // Fix subtree count
                DirtyReflectionHacks.fixSubtreeCount(right);
                DirtyReflectionHacks.fixSubtreeCount(rbNode);
            }
        }

        // 2-Node - it's trivial!
        if (node.size() == ORDER - 3) {
            Key medianKey = node.keyValues.getKey(0);
            rbNode = DirtyReflectionHacks.newRedBlackNode(medianKey,
                    RedBlackBST.BLACK, 1);

            if (node.hasChildren()) {
                // Generate child nodes
                RedBlackBST<Key, Value>.Node left = toRedBlackNode(node.children
                        .getKey(0));
                RedBlackBST<Key, Value>.Node right = toRedBlackNode(node.children
                        .getKey(1));

                // Set child nodes
                DirtyReflectionHacks.setLeftChild(rbNode, left);
                DirtyReflectionHacks.setRightChild(rbNode, right);

                // Fix subtree count
                DirtyReflectionHacks.fixSubtreeCount(rbNode);
            }
        }
        return rbNode;
    }
}
