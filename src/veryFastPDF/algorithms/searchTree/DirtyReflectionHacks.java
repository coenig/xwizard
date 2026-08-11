/*
 * File name:        DirtyReflectionHacks.java (package veryFastPDF.algorithms.searchTree)
 * Author(s):        bwpc
 * Java version:     8.0 (at generation time)
 * Generation date:  06.08.2015 (11:19:20)
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

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

import eas.GlobalVariables;
import veryFastPDF.algorithms.searchTree.redblacktree.RedBlackBST;
import veryFastPDF.algorithms.searchTree.redblacktree.RedBlackTree;
import veryFastPDF.algorithms.searchTree.tree234.Tree234;
import veryFastPDF.algorithms.searchTree.tree234.Tree234Impl;
import veryFastPDF.algorithms.searchTree.tree234.Tree234Impl.Node;

/**
 * This class contains many dirty reflection hacks for manipulating search
 * trees. We contain these methods in a separate class to shield the trees from
 * easy manipulations by fools who think they should toy with the trees ;-)
 * These methods should never be used outside the FSM plug-in.
 * 
 * @author marlon.braun
 */
public class DirtyReflectionHacks {

    /**
     * A very simple method for checking access restrictions to this class. Only
     * whitelisted classes are allowed to use methods in this class.
     */
    public static final void checkPermission() {

        /*
         * An array that contains the only classes as string representations
         * that should be allowed to call methods in this class.
         */
        String[] whiteListed = new String[] {
                AbstractSearchTree.class.getName(),
                AbstractTreeRepresentable.class.getName(),
                RedBlackBST.class.getName(), RedBlackTree.class.getName(),
                Tree234.class.getName(), Tree234Impl.class.getName(),
                DirtyReflectionHacks.class.getName() };

        String caller = Thread.currentThread().getStackTrace()[3]
                .getClassName();

        boolean accessAllowed = false;
        for (String clazz : whiteListed) {
            if (clazz.equals(caller)) {
                accessAllowed = true;
                break;
            }
        }
        if (!accessAllowed)
            throw new SecurityException(
                    String.format(
                            "%s is not allowed to invoke methods in DirtyReflectionHacks.",
                            caller));
    }

    /**
     * Sets the node of this {@link AbstractSearchTree} to the provided node.
     * Beware, this method does not check, whether the node is actually
     * compatible with the tree.
     * 
     * @param tree
     *            The tree whose node is changed
     * @param node
     *            The node that is declared as new root
     * @return <code>true</code> if changing the root was successful, which is
     *         mostly the case unless an exception occurs.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static boolean setRoot(AbstractSearchTree tree,
            AbstractSearchTree.AbstractNode node) {
        checkPermission();
        tree.root = node;
        return true;
    }

    /**
     * Adds the provided keys to the given node. Beware that there is no type
     * safety checking whatsoever, so don't horse around with this method.
     * 
     * @param node
     *            The node to which keys are added.
     * @param keys
     *            The keys which are added to the node.
     * @return <code>true</code> if adding was successful.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static boolean addKeys(Tree234Impl.Node node, Comparable... keys) {
        checkPermission();
        try {
            Field nameField = node.getClass().getDeclaredField("keyValues");
            nameField.setAccessible(true);
            IndexedTreeMap keyStore = (IndexedTreeMap) nameField.get(node);
            for (Comparable key : keys) {
                keyStore.put(key, key);
            }
        } catch (IllegalArgumentException | IllegalAccessException
                | NoSuchFieldException | SecurityException e) {
            GlobalVariables.getParameters().logDebug(e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * Adds the provided keys to the given node. Beware that there is no type
     * safety checking whatsoever, so don't horse around with this method.
     * 
     * @param node
     *            The node to which keys are added.
     * @param keys
     *            The keys which are added to the node.
     * @return <code>true</code> if adding was successful.
     */
    @SuppressWarnings("rawtypes")
    public static boolean addKeyCollection(Tree234Impl.Node node,
            Collection<Comparable> c) {
        checkPermission();
        addKeys(node, c.toArray(new Comparable[c.size()]));
        return true;
    }

    /**
     * A method for adding a child to an existing node of a 2-3-4-tree. No
     * type-checking is performed to prevent declaration overhead, so make sure
     * both nodes share common key and value types.
     * 
     * @param node
     *            The parent node
     * @param child
     *            The child node
     * @return <code>true</code> if insertion was successful.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static boolean addChild(Tree234Impl.Node node, Tree234Impl.Node child) {
        checkPermission();
        try {
            // Obtain field
            Field nameField = node.getClass().getDeclaredField("children");
            // Make accessible since field is private
            nameField.setAccessible(true);
            // Add Child to existing node.
            ((IndexedTreeSet<Node>) nameField.get(node)).add(child);
        } catch (NoSuchFieldException | IllegalArgumentException
                | IllegalAccessException e) {
            GlobalVariables.getParameters().logDebug(e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * A method for adding a collection of children to an existing node of a
     * 2-3-4-tree. No type-checking is performed to prevent declaration
     * overhead, so make sure both nodes share common key and value types.
     * 
     * @param node
     *            The parent node
     * @param child
     *            A collection of child nodes
     * @return <code>true</code> if insertion was successful.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public static boolean addChildren(Tree234Impl.Node node,
            Collection<Tree234Impl.Node> children) {
        checkPermission();
        try {
            // Obtain field
            Field nameField = node.getClass().getDeclaredField("children");
            // Make accessible since field is private
            nameField.setAccessible(true);
            // Add Child to existing node.
            ((IndexedTreeSet<Node>) nameField.get(node)).addAll(children);
        } catch (NoSuchFieldException | IllegalArgumentException
                | IllegalAccessException e) {
            GlobalVariables.getParameters().logDebug(e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * Set the left child of a red-black node to the provided red-black-node
     * 
     * @param parent
     *            The parent node.
     * @param rightChild
     *            The left child.
     * @return <code>true</code> if the operation was successful.
     * 
     */
    @SuppressWarnings("rawtypes")
    public static boolean setLeftChild(RedBlackBST.Node parent,
            RedBlackBST.Node leftChild) {
        checkPermission();

        try {
            Field field = parent.getClass().getDeclaredField("left");
            field.setAccessible(true);
            field.set(parent, leftChild);
        } catch (NoSuchFieldException | SecurityException
                | IllegalArgumentException | IllegalAccessException e) {
            GlobalVariables.getParameters().logDebug(e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * Set the right child of a red-black node to the provided red-black-node
     * 
     * @param parent
     *            The parent node.
     * @param rightChild
     *            Its right child.
     * @return <code>true</code> if the operation was successful.
     *         <code>false</code> otherwise.
     */
    @SuppressWarnings("rawtypes")
    public static boolean setRightChild(RedBlackBST.Node parent,
            RedBlackBST.Node rightChild) {
        checkPermission();

        try {
            Field field = parent.getClass().getDeclaredField("right");
            field.setAccessible(true);
            field.set(parent, rightChild);
        } catch (NoSuchFieldException | SecurityException
                | IllegalArgumentException | IllegalAccessException e) {
            GlobalVariables.getParameters().logDebug(e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * A method for setting the correct subtree count for the provided node
     * given that the subtree counts of its children are correct.
     * 
     * @param node
     *            A red-black-tree node whose subtree count is corrected.
     * @return <code>true</code> if the operation was successful.
     *         <code>false</code> otherwise.
     */
    @SuppressWarnings("rawtypes")
    public static boolean fixSubtreeCount(RedBlackBST.Node node) {
        checkPermission();

        try {
            // Left subtree
            int lc = 0;
            try {
                Field left = node.getClass().getDeclaredField("left");
                left.setAccessible(true);
                Field leftCount = node.getClass().getDeclaredField("N");
                leftCount.setAccessible(true);
                lc = leftCount.getInt(node);
            } catch (NullPointerException e) {
                // Left neighbor does not exist
            }

            // Right subtree
            int rc = 0;
            try {
                Field right = node.getClass().getDeclaredField("right");
                right.setAccessible(true);
                Field rightCount = node.getClass().getDeclaredField("N");
                rightCount.setAccessible(true);
                rc = rightCount.getInt(node);
            } catch (NullPointerException e) {
                // right neighbor does not exist
            }

            Field count = node.getClass().getDeclaredField("N");
            count.setAccessible(true);
            count.setInt(node, lc + rc + 1);
        } catch (NoSuchFieldException | SecurityException
                | IllegalArgumentException | IllegalAccessException e) {
            GlobalVariables.getParameters().logDebug(e.getMessage());
            return false;
        }

        return true;
    }

    /**
     * A method for creating a new 2-3-4-tree node that features the keys by
     * which the method was invoked. Beware that there is no type safety
     * checking whatsoever, so don't horse around with this method. Parent node
     * is set to null.
     * 
     * @param keys
     *            The keys of the new node
     * @return A node of a 2-3-4-tree that has the specified keys.
     */
    @SuppressWarnings("rawtypes")
    public static Tree234Impl.Node new234Node(Comparable... keys) {
        checkPermission();
        return new234Node(null, keys);
    }

    /**
     * A method for creating a new 2-3-4-tree node that features the keys by
     * which the method was invoked. Beware that there is no type safety
     * checking whatsoever, so don't horse around with this method.
     * 
     * @param parent
     *            The parent of this node.
     * @param keys
     *            The keys of the new node
     * @return A node of a 2-3-4-tree that has the specified keys.
     */
    @SuppressWarnings("rawtypes")
    public static Tree234Impl.Node new234Node(Tree234Impl.Node parent,
            Comparable... keys) {
        checkPermission();

        if (keys != null && keys.length < 1)
            throw new RuntimeException(
                    "You have not provided any keys. Cannot create empty node.");

        // First create the child
        Tree234Impl.Node child = null;
        for (Constructor<?> c : Tree234Impl.Node.class.getConstructors()) {
            try {
                // Try every constructor till one of them works
                child = (Tree234Impl.Node) c.newInstance(new Tree234Impl(),
                        keys[0], keys[0], parent);
                break;
            } catch (InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException e) {
                GlobalVariables.getParameters().logDebug(e.getMessage());
            }
        }

        // Add remaining keys. We can add the first key one more time, since the
        // underlying data structure is a set, so no duplicates are stored
        addKeys(child, keys);

        return child;
    }

    /**
     * A method for creating a new red-black tree node. The value is the same as
     * the key. The subtree count is set to 0.
     * 
     * @param key
     *            The key stored in the root.
     * @param color
     *            The color of the root.
     * @return A red-black tree node with the given inputs.
     */
    @SuppressWarnings("rawtypes")
    public static RedBlackBST.Node newRedBlackNode(Comparable key, boolean color) {
        checkPermission();
        return newRedBlackNode(key, color, 0);
    }

    /**
     * A method for creating a new red-black tree node. The value is the same as
     * the key.
     * 
     * @param key
     *            The key stored in the root.
     * @param color
     *            The color of the root.
     * @param subtreeCount
     *            Number of nodes in the subtree that this node constitutes.
     * @return A red-black tree node with the given inputs.
     */
    @SuppressWarnings("rawtypes")
    public static RedBlackBST.Node newRedBlackNode(Comparable key,
            boolean color, int subtreeCount) {
        checkPermission();

        if (key == null)
            throw new RuntimeException(
                    "The key you have provided is equal to null.");

        RedBlackBST.Node child = null;
        for (Constructor<?> c : RedBlackBST.Node.class.getConstructors()) {
            try {
                // Try every constructor till one of them works
                child = (RedBlackBST.Node) c.newInstance(new RedBlackBST(),
                        key, key, color, subtreeCount);
                break;
            } catch (InstantiationException | IllegalAccessException
                    | IllegalArgumentException | InvocationTargetException e) {
                GlobalVariables.getParameters().logDebug(e.getMessage());
            }
        }
        return child;
    }

}
