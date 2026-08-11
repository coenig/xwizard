/*
 * File name:        IndexedTreeMap.java (package veryFastPDF.algorithms.searchTree)
 * Author(s):        bwpc
 * Java version:     8.0 (at generation time)
 * Generation date:  06.08.2015 (10:09:40)
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

import java.util.Map.Entry;
import java.util.TreeMap;

/**
 * A lightweight implementation of an indexed tree map. A key can be retrieved
 * by its index in the list that would result from sorting all keys in this map
 * into a list. Be warned however that all implemented methods have linear
 * runtime, since I've been too lazy to do a proper implementation. The indexed
 * tree map is currently only used in the 2-3-4-tree implementation, which has
 * tree maps of maximum size of 3.
 * 
 * @author marlon.braun
 */
public class IndexedTreeMap<Key, Value> extends TreeMap<Key, Value> {

    /**
     * Serial version of this implementation
     */
    private static final long serialVersionUID = 4483740886955676302L;

    /**
     * Obtains the key and the given index according to the natural order of
     * elements in the map.
     * 
     * @param index
     *            The index of the given key.
     * @return They key according to the given index.
     */
    public Key getKey(int index) {
        return getEntry(index).getKey();
    }

    /**
     * Obtains the entry and the given index according to the natural order of
     * elements in the map.
     * 
     * @param index
     *            The index of the given entry.
     * @return They entry according to the given index.
     */
    public Entry<Key, Value> getEntry(int index) {
        if (index < 0 || index >= size())
            throw new IndexOutOfBoundsException("Invalid index: " + index);

        int i = 0;
        for (Entry<Key, Value> entry : entrySet()) {
            if (i == index)
                return entry;
            i++;
        }
        return null;
    }

    /**
     * Returns the index of the given key according to its position in the
     * natural order of the underlying key set.
     * 
     * @param key
     *            The key whose index is sought.
     * @return The index of the key.
     */
    public int getIndex(Key key) {
        if (!containsKey(key))
            return -1;
        else {
            int index = 0;
            for (Key k : keySet()) {
                if (key.equals(k))
                    return index;
                index++;
            }
            return index;
        }
    }
}
