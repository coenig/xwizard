/*
 * File name:        Node.java (package eas.fundamentalAlgorithms.graphBased.patTree)
 * Author(s):        Marc Mültin, Lukas König
 * Java version:     7.0
 * Generation date:  Some time in 2011; in EAS since Dec. 2013
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

package veryFastPDF.algorithms.patTree;

import java.util.ArrayList;
import java.util.TreeMap;

public class Node {

	private boolean leaf;  //0 = internal node, 1 = external node
	private String ID;     //if internal node, it is the position to inspect next in a search pattern
						   //if external node, it is the position in the text where to find the search pattern 
	private int label;
	private TreeMap<String, Node> outEdgeLabels = new TreeMap<String, Node>();
	

	/**
	 * Constructor which generates the PAT tree
	 * @param siStrings A set of siStrings, lexicographically ordered in a TreeMap with the siStrings being the key and
	 * 					the starting position in the text being the value of the respective <key, value> pairs.
	 * 					At first, the siStrings are all siStrings of the text. All further calls are clusters 
	 * 					of siStrings located in a respective subtree of the PAT tree.
	 * @param ID The branching character leading to an inner node (the respective index position of the search pattern 
	 * 		     which is to inspect next is not unique enough)
	 */
	public Node (TreeMap<String, Integer> siStrings, String ID) {
		//special case, here we have an external node, no further subtree
		if (siStrings.size() == 1) {
			this.setID("" + (siStrings.get(siStrings.keySet().iterator().next()) + 1));
			this.setLabel(siStrings.get(siStrings.keySet().iterator().next()) + 1);
			this.setLeaf(true); 

			return;
		}

		int misMatchPos = mismatchPosition(siStrings);
		this.setID(ID + (misMatchPos + 1)); 
		this.setLabel(misMatchPos + 1);
		this.setLeaf(false); 
		
		TreeMap<String, TreeMap<String, Integer>> siStringClusters = getSiStringClusters(siStrings, misMatchPos);
		
		int idCounter = 0;
		for (String c : siStringClusters.keySet()) {
			idCounter++;
			//recursive step which automatically generates the respective subtrees
			this.getOutEdgeLabels().put(c, new Node(siStringClusters.get(c), this.getID() + idCounter));
		}
	}


	/**
	 * Finds the smallest mismatch position between all siStrings
	 * @param siStrings The siStrings which are to be compared with each other
	 * @return The smallest index position in the first siString where a mismatch with all other siStrings occurs
	 */
	private int mismatchPosition (TreeMap<String, Integer> siStrings) {
		int mismatchPos = Integer.MAX_VALUE;
		
		//the smallest string limits the maximum mismatch position
		for (String s : siStrings.keySet()) {
			if (s.length() < mismatchPos) {
				mismatchPos = s.length();
			}
		}
		
		ArrayList<String> siStringList = new ArrayList<String>(siStrings.keySet());
		String s1 = siStringList.get(0);
		String s2 = "";
		
		//compare the first siString with all the other siStrings and find the smallest mismatching position
		for (int j = 1; j < siStringList.size(); j++) {
			s2 = siStringList.get(j);
			
			for (int i = 0; i < mismatchPos; i++) {
				//the fulfillment of the if-statement is the terminating condition for the for-loop
				if (s1.charAt(i) != s2.charAt(i)) mismatchPos = i;
			}
		}
		
		return mismatchPos;
	}
	
	
	/**
	 * Gets for every branching character at the provided mismatch position the siStringClusters (all siStrings 
	 * starting with the same character sequence up to the branching position form a cluster, a subtree)
	 * @param strings All siStrings starting with the same character sequence up to the misMatchPos position
	 * @param misMatchPos The smallest mismatching position between all provided strings
	 * @return siString clusters for a branching character (a subtree)
	 */
	private TreeMap<String, TreeMap<String, Integer>> getSiStringClusters (TreeMap<String, Integer> strings, int misMatchPos) {
		/*
		 * a subtree with the branching character distinguishing further siStrings which all begin with the same
		 * character sequence
		 */
		TreeMap<String, TreeMap<String, Integer>> siStringCluster = new TreeMap<String, TreeMap<String,Integer>>();
		
		for (String s : strings.keySet()) {
			if (s.length() > misMatchPos) {
				String branchingString = convertSpecialCharToGraphvizLabel("" + s.charAt(misMatchPos));
				
				if (siStringCluster.get(branchingString) == null) {
					siStringCluster.put(branchingString, new TreeMap<String, Integer>());	
				}
				siStringCluster.get(branchingString).put(s, strings.get(s));
			} else {
				if (siStringCluster.get("end") == null) {
					siStringCluster.put("end", new TreeMap<String, Integer>());	
				}
				siStringCluster.get("end").put(s, strings.get(s));
			}
		}
		
		return siStringCluster;
	}
	
	
    @SuppressWarnings({"all"})
	public String convertSpecialCharToGraphvizLabel (String special) {
		//graphviz special treatment (some characters can not be used as labels by graphviz)
		if (special.equals(".")) {
			special = "dot";
        } else if (special.equals(",")) {
            special = "comma";
        } else if (special.equals("\n")) {
            special = "newline";
		} else if (special.equals(":")) {
				special = "colon";
		} else if (special.equals(";")) {
			special = "semiCol";
		} else if (special.equals("!")) {
			special = "explMark";
		} else if (special.equals("?")) {
			special = "questMark";
		} else if (special.equals(" ")) {
			special = "space";
		} else if (special.equals("'")) {
			special = "apos";
		} else if (special.equals("\"")) {
			special = "quote";
		} else if (special.equals("(")) {
			special = "openBR";
		} else if (special.equals(")")) {
			special = "closeBR";
		} else if (special.equals("[")) {
			special = "openBS";
		} else if (special.equals("]")) {
			special = "closeBS";
		} else if (special.equals("{")) {
			special = "openBC";
		} else if (special.equals("}")) {
			special = "closeBC";
		}
		
		return special;
	}
	
	
	public char getSpecialCharFromGraphvizLabel (String label) {
		char specialChar;
		
		if (label.equals("dot")) {
			specialChar = '.';
		} else if (label.equals("comma")) {
			specialChar = ',';
		} else if (label.equals("fullstop")) {
				specialChar = '.';
		} else if (label.equals("semicol")) {
			specialChar = ';';
		} else if (label.equals("explMark")) {
			specialChar = '!';
		} else if (label.equals("questMark")) {
			specialChar = '?';
		} else if (label.equals("space")) {
			specialChar = ' ';
		} else if (label.equals("apos")) {
			specialChar = '\'';
		} else if (label.equals("quote")) {
			specialChar = '"';
		} else if (label.equals("openBR")) {
			specialChar = '(';
		} else if (label.equals("closeBR")) {
			specialChar = ')';
		} else if (label.equals("openBS")) {
			specialChar = '[';
		} else if (label.equals("closeBS")) {
			specialChar = ']';
		} else if (label.equals("openBC")) {
			specialChar = '{';
		} else if (label.equals("closeBC")) {
			specialChar = '}';
		} else {
			specialChar = label.charAt(0);
		}
		
		return specialChar;
	}
	
	
	public ArrayList<Integer> getAllLeafsOfNode(Node node, ArrayList<Integer> leafs) {
		for (Node nodeEl : node.getOutEdgeLabels().values()) {
			if (nodeEl.isLeaf()) {
				leafs.add(nodeEl.getLabel());
			} else {
				leafs.addAll(getAllLeafsOfNode(nodeEl, leafs));
			}
		}

		return leafs;
	}
	

	public String getStringRep() {
		String s = "";
		
		for (String edge : this.getOutEdgeLabels().keySet()) {
			Node node = this.getOutEdgeLabels().get(edge);
			
			/*
			 * graph nodes in graphviz need to be unique using an identifier, but the label attribute lets the graph 
			 * look like the way we present it on the slides (several nodes may have the same label); 
			 * furthermore, external nodes (leafs) are marked here, their ID and label is the unique text position
			 */
			if (node.leaf == true) {
				s += node.getID() + "[shape=box]\n";
			} else {
				s += node.getID() + "[label=" + node.getLabel() + "]\n";
			}
			
			//special case for the root node
			if (this.getID().contains("root")) s += this.getID() + "[label=" + this.getLabel() + "]\n";
			
			s += this.getID() + " -> " + node.getID() + " [label=" + edge + "]\n";
			s += node.getStringRep();
		}
		
		return s; 
	}
	

	public TreeMap<String, Node> getOutEdgeLabels() {
		return outEdgeLabels;
	}


	public void setOutEdgeLabels(TreeMap<String, Node> outEdgeLabels) {
		this.outEdgeLabels = outEdgeLabels;
	}


	public String getID() {
		return ID;
	}


	private void setID(String iD) {
		ID = iD;
	}
	

	public boolean isLeaf() {
		return leaf;
	}


	private void setLeaf(boolean type) {
		this.leaf = type;
	}


	public int getLabel() {
		return label;
	}


	public void setLabel(int label) {
		this.label = label;
	}
}
