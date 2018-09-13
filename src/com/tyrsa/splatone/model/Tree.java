package com.tyrsa.splatone.model;

import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class Tree {
	
	private File node;
	
	private String text;
	
	private Tree parent;
	
	
	public Tree getParent() {
		return parent;
	}

	public void setParent(Tree parent) {
		this.parent = parent;
	}

	private CopyOnWriteArrayList<Tree> leaves;
	
	public Tree(File node) {
		this.node = node;
		leaves = new CopyOnWriteArrayList<>();
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
