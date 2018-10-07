package com.tyrsa.splatone.model;

import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Tree {
	
	private Tree parent;
	
	private CopyOnWriteArrayList<Tree> leaves;

	private File node;
	
	private String text;
	
	private volatile int linesReadedEnd;
	
	private volatile int linesReadedBegin;
	
	public Tree() {
		linesReadedEnd = 0;
		linesReadedBegin = 0;
	}
	
	public Tree(File node) {
		this.node = node;
		leaves = new CopyOnWriteArrayList<>();
		linesReadedEnd = 0;
	}
	
	public Tree getParent() {
		return parent;
	}

	public void setParent(Tree parent) {
		this.parent = parent;
	}
	
	public int getLinesReadedEnd() {
		return linesReadedEnd;
	}

	public void setLinesReadedEnd(int linesReaded) {
		this.linesReadedEnd = linesReaded;
	}
	
	public int getLinesReadedBegin() {
		return linesReadedBegin;
	}

	public void setLinesReadedBegin(int linesReadedBegin) {
		this.linesReadedBegin = linesReadedBegin;
	}

	public void addNode(Tree node) {
		leaves.addIfAbsent(node);
	}

	public File getNode() {
		return node;
	}

	public void setNode(File node) {
		this.node = node;
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}
	
	public CopyOnWriteArrayList<Tree> getLeaves() {
		return leaves;
	}
	
	public void remove(Tree node) {
		leaves.remove(node);
	}
	
	public Tree search(List<String> nodes) {
		String node = nodes.get(0);
		Tree result = null;
		for(Tree tree : leaves) {
			if(tree.getNode().getName().equals(node)) {
				nodes.remove(0);
				if(nodes.size() == 0) {
					result = tree;
				}
				else{
					result = tree.search(nodes);
				}
				break;
			}
		}
		return result;
		
	}
}
