/*
 * File name:        PatTree.java (package eas.fundamentalAlgorithms.graphBased.patTree)
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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import javax.swing.JComponent;

import eas.GlobalVariables;
import veryFastPDF.pdfProcessors.GraphViz;
import veryFastPDF.pdfProcessors.PDFProcessor;
import veryFastPDF.script.Exercise;
import veryFastPDF.script.RepresentableAsPDF;
import veryFastPDF.script.RepresentableDefault;
import veryFastPDF.web.Webproof;

@Webproof(useInProductiveMode = true)
public class PatTree extends RepresentableDefault {

    private static final long serialVersionUID = 3910404486286716244L;
    private Node root;
	private String searchText;
	private String searchPrefix;
	private boolean ignoreCase;
	private boolean onlyAtWordBeginnings;
	
	/**
	 * Calls the constructor which generates the PAT tree (starting with the root) by passing the siStrings of the 
	 * provided text
	 * @param text The text to be indexed
	 * @param onlyAtWordBeginnings True, if only siStrings at the beginnings of words can be searched for.
	 * 							   False, if all text positions (all siStrings) can be searched for.
	 */
	public PatTree (String searchText, boolean onlyAtWordBeginnings, boolean ignoreCase, Exercise exercise) {
	    super(exercise);
		this.setSearchText(searchText);
		this.setIgnoreCase(ignoreCase);
		this.setOnlyAtWordBeginnings(onlyAtWordBeginnings);
		root = new Node(getSiStrings(searchText, onlyAtWordBeginnings, ignoreCase), "root");
		this.setAllowCollapsingRules(false);
        this.addIgnoredField("root");
        this.addIgnoredField("searchText");
        this.addIgnoredField("searchPrefix");
//        this.addIgnoredField("ignoreCase");
//        this.addIgnoredField("onlyAtWordBeginnings");
	}
	
	public PatTree(Exercise exercise) {
	    this("", false, true, exercise);
	}
	
	public static void mainDummy (String[] args) {
		String searchText = "two witches watched two watches. which witch watched which watch?";
//		String searchText = "blaukraut bleibt blaukraut und brautkleid bleibt brautkleid.";
//		String searchText = "Imagine an imaginary manager imagining managing a menagerie."; 
//		String searchText = "wer weiss wo Peter Peterson wohnt";
//	 	String searchText = "Fischers Fritz fischt frische Fische. Frische Fische fischt Fischers Fritz.";
//		String searchText = "Graph visualization is a way of representing structural information as diagrams of " +
//				"abstract graphs and networks. It has important applications in networking, bioinformatics, software " +
//				"engineering, database and web design, machine learning, and in visual interfaces for other technical " +
//				"domains. Graphviz is open source graph visualization software. It has several main layout programs. " +
//				"See the gallery for sample layouts. It also has web and interactive graphical interfaces, and " +
//				"auxiliary tools, libraries, and language bindings. We're not able to put a lot of work into GUI " +
//				"editors but there are quite a few external projects and even commercial tools that incorporate " +
//				"Graphviz. You can find some of these in the Resources section. " +
//				"The Graphviz layout programs take descriptions of graphs in a simple text language, and make diagrams " +
//				"in useful formats, such as images and SVG for web pages, PDF or Postscript for inclusion in other " +
//				"documents; or display in an interactive graph browser. (Graphviz also supports GXL, an XML dialect.) " +
//				"Graphviz has many useful features for concrete diagrams, such as options for colors, fonts, tabular " +
//				"node layouts, line styles, hyperlinks, roll and custom shapes. " +
//				"In practice, graphs are usually generated from an external data sources, but they can also be created " +
//				"and edited manually, either as raw text files or within a graphical editor. (Graphviz was not intended " +
//				"to be a Visio replacement, so it is probably frustrating to try to use it that way.)";
//		String searchText = "01100100010111";
//		String searchText = "abbabbbabbc";
		PatTree patTree = new PatTree(searchText, false, true, null);
		
		//draw Graphviz graph based on string representation of the PAT tree
		String patTreeString = patTree.root.getStringRep();
		patTree.createGraphVizFile(patTreeString);
		
		//make a prefix search
		boolean stop = false;
		
		while (!stop) {
			patTree.setSearchPrefix(patTree.getSearchPrefixFromUser());
			
			if (patTree.getSearchPrefix().equals("#")) break;
			
			TreeMap<Integer, String> foundResults = patTree.prefixSearch(patTree.getSearchPrefix(), patTree.root);
			GlobalVariables.getParameters().logInfo("Search results for '" + patTree.getSearchPrefix() + "':\n");
			if (foundResults.size() == 0) {
			    GlobalVariables.getParameters().logInfo("Sorry, your search was not successful.");
			} else {
				for (int indexPos : foundResults.keySet()) {
				    GlobalVariables.getParameters().logInfo("- " + indexPos + ": " + foundResults.get(indexPos));
				}
			}
			
			GlobalVariables.getParameters().logInfo("\n------------\n");
		}
	}
	
	
    @SuppressWarnings({"all"})
	private static TreeMap<String, Integer> getSiStrings (String text, boolean onlyAtWordBeginnings, boolean ignoreCase) {
		TreeMap<String, Integer> siStrings = new TreeMap<String, Integer>();
		
		//distinguish between lower and upper case characters or not?
		if (ignoreCase) text = text.toLowerCase();
		
		siStrings.put(text, 0);
		
		for (int i = 1; i < text.length(); i++) {
			if (onlyAtWordBeginnings) {
				if (text.charAt(i) == ' ' && ((i+1) < text.length())) siStrings.put(text.substring(i+1), i+1);
			} else {
				siStrings.put(text.substring(i), i);
			}
		}
		
		return siStrings;
	}
	
	public TreeMap<Integer, String> prefixSearch (String searchPrefix, Node node) {
		TreeMap<Integer, String> positionsFound = new TreeMap<Integer, String>();
		Node nextSearchNode = null;
			
		/* 
		 * if the length of the searchPrefix is at least as long as the value of the node's label, then we can do 
		 * a recursive search;
		 * Note: the local variable "searchPrefix" is a substring of the class variable searchPrefix 
		 * (retrievable by this.getSearchPrefix())
		 */
		if (this.getSearchPrefix().length() >= node.getLabel()) {
			for (String edgeLabel : node.getOutEdgeLabels().keySet()) {
				if (this.getSearchPrefix().charAt(node.getLabel() - 1) == node.getSpecialCharFromGraphvizLabel(edgeLabel)) {
					nextSearchNode = node.getOutEdgeLabels().get(edgeLabel);
					break;
				}
			}
		}
		
		if (nextSearchNode != null && nextSearchNode.isLeaf() && (this.getSearchPrefix().length() <= node.getLabel())) {
			//external node (leaf found) and searchPrefix is not longer than value of the node's label
			int lengthAfterSearchPrefix = this.getSearchText().length() - (nextSearchNode.getLabel() + 1);
			int foundPosTrailer = Math.min(30, lengthAfterSearchPrefix);
			positionsFound.put(nextSearchNode.getLabel(), " ... " + this.getSearchText().substring(nextSearchNode.getLabel() - 1, nextSearchNode.getLabel() + foundPosTrailer) + " ...");
		} else if (nextSearchNode != null && nextSearchNode.isLeaf()) {
			/*
			 * the searchPrefix is longer than the value of the index positions stored in the PAT tree;
			 * this means that a certain substring of the searchPrefix does only occur once in the text, but we are 
			 * searching for a word (or string in general) which is longer than the stored siString; 
			 * ergo, we will have to check in the text itself if the searchPrefix is contained there
			 */
			positionsFound = checkTextForMatch(nextSearchNode, true, this.isIgnoreCase());
		} else if (nextSearchNode != null && searchPrefix.length() > 1) {
			//we are still at an internal node and the search prefix is not exhausted yet -> recursive call
			positionsFound = prefixSearch(searchPrefix.substring(1), nextSearchNode);
		} else if (nextSearchNode != null) {
			//search prefix is exhausted, we reached an inner node and need to check all leafs for a match
			positionsFound = checkTextForMatch(nextSearchNode, false, this.isIgnoreCase());
		} else if (nextSearchNode == null && node.getLabel() > searchPrefix.length()) {
			/* 
			 * the distinguishing character is on a position greater than the length of the searchPrefix;
			 * ergo, we will have to check in the text itself if the searchPrefix is contained there
			 */
			positionsFound = checkTextForMatch(node, false, this.isIgnoreCase());
		} else {
			return positionsFound;
		}
		
		return positionsFound;
	}
	
	
	public TreeMap<Integer, String> checkTextForMatch(Node node, boolean isLeaf, boolean ignoreCase) {
		TreeMap<Integer, String> positionsFound = new TreeMap<Integer, String>();
		String textToSearchIn;
		String searchPrefix;
	
		if (ignoreCase) {
			searchPrefix = this.getSearchPrefix().toLowerCase();
			textToSearchIn = this.getSearchText().toLowerCase();
		}
		else {
			searchPrefix = this.getSearchPrefix();
			textToSearchIn = this.getSearchText();
		}
		
		boolean match = true;

		ArrayList<Integer> positionsToSearch = new ArrayList<Integer>();
		if (isLeaf) {
			positionsToSearch.add(node.getLabel());
		} else {
			positionsToSearch.addAll(node.getAllLeafsOfNode(node, new ArrayList<Integer>()));
		}
		
		for (int pos : positionsToSearch) {
			for (int i = 0; i < searchPrefix.length(); i++) {
				if (textToSearchIn.charAt(pos - 1 + i) != searchPrefix.charAt(i)) {
					match = false; 
					break;
				}
			}
			
			if (!match) {
				break;
			} else {
				int lengthAfterSearchPrefix = this.getSearchText().length() - (pos);
				int foundPosTrailer = Math.min(30, lengthAfterSearchPrefix);
				positionsFound.put(pos, " ... " + textToSearchIn.substring(pos - 1, pos + foundPosTrailer) + " ...");
			}
		}
		
		return positionsFound;
	}
	
	
	public String getSearchPrefixFromUser() {
		//read the prefix to search for from the console
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String input = "";
		GlobalVariables.getParameters().logInfo("Provide a string to search for (enter '#' to stop): ");
		
		try {
			input = in.readLine();
		} catch (IOException e) {
		    GlobalVariables.getParameters().logInfo("Search-prefix has not been provided correctly.");
			e.printStackTrace();
		}
		
		return input;
	}
	
	
	public void createGraphVizFile (String patTree) {
		String graphVizString = "digraph PATTree {\n" + patTree + "}";
		
		File file = new File("PatTree.gv"); 
		
		try {
			FileWriter writer = new FileWriter(file, false);
			writer.write(graphVizString);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	
	public String getSearchText() {
		return searchText;
	}


	public void setSearchText(String searchText) {
		this.searchText = searchText;
	}


	public String getSearchPrefix() {
		return this.searchPrefix;
	}
	
	
	public void setSearchPrefix(String searchPrefix) {
		if (this.isIgnoreCase()) this.searchPrefix = searchPrefix.toLowerCase();
		else this.searchPrefix = searchPrefix;
	}


	public boolean isIgnoreCase() {
		return ignoreCase;
	}


	public void setIgnoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}


	public boolean isOnlyAtWordBeginnings() {
		return onlyAtWordBeginnings;
	}


	public void setOnlyAtWordBeginnings(boolean onlyAtWordBeginnings) {
		this.onlyAtWordBeginnings = onlyAtWordBeginnings;
	}


    @Override
    public String[] getExampleScripts() {
        return new String[] {"pat:two witches watched two watches. which witch watched which watch?"};
    }


    @Override
    public boolean isAcceptableScript(String code) {
        return (code + "").split("\n")[0].toLowerCase().startsWith("pat:");
    }


    @Override
    public void createInstanceFromScript(String codeRaw, RepresentableAsPDF father) {
        this.applyDeclarationsAndPreprocessors(codeRaw, father, 0);
        String code = this.getScriptWithoutPrepAndDeclAndPreamble();
        code = this.decollapseRules(code);

        PatTree other = new PatTree(code, false, false, this.getExercise());
        this.ignoreCase = other.ignoreCase;
        this.onlyAtWordBeginnings = other.ignoreCase;
        this.root = other.root;
        this.searchPrefix = other.searchPrefix;
        this.searchText = other.searchText;
    }

    @Override
    public GraphViz generatePDFscript(String pdfPath) {
        super.generatePDFscript(pdfPath);

        String patTreeString = this.root.getStringRep();
        String graphVizString = "digraph PATTree {\n" + patTreeString + "}";
        GraphViz gv = new GraphViz(pdfPath, this);
        gv.addln(graphVizString);
        return gv;
    }

    @Override
    public JComponent getAdditionalInfo() {
        return super.getAdditionalInfo();
    }

    @Override
    public Class<? extends PDFProcessor> getPDFProcessorClass() {
        return GraphViz.class;
    }
    
    @Override
    public String getGermanName() {
        return "Pat-Baum";
    }
    
    @Override
    public HashMap<String, String> getMetaProperties() {
        return super.getMetaProperties();
    }

    @Override
    public String createScriptFromInstance() {
        // TODO Auto-generated method stub
        return null;
    }
}
