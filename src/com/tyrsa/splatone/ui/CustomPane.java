package com.tyrsa.splatone.ui;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;

import com.tyrsa.splatone.model.Tree;

public class CustomPane extends JScrollPane {

	private static final long serialVersionUID = 6174880998943055159L;
	private Tree node;
	private static JEditorPane editor = new JEditorPane();
	private int selectedIndex;
	private String lex;
	private String text;
	
	public CustomPane(Tree node, String lex) {
		
		selectedIndex = 0;
		text = node.getText();
		String tmp = "<html><body>" + text + "</body></html>";
		this.editor.setContentType("text/html");
		this.editor.setText(tmp);
		
		this.node = node;
		this.lex = lex;
		editor.setText(text);
		this.setViewportView(editor);
		
	} 
	
	public void update() {
		editor = new JEditorPane();
		String tmp = "<html><body>" + text + "</body></html>";
		this.editor.setContentType("text/html");
		this.editor.setText(tmp);
		this.editor.setText(text);
		this.setViewportView(editor);
		this.repaint();
	}
	
	public Tree getNode() {
		return node;
	}
	
	public void selectAll() {
		editor.requestFocus();
		editor.selectAll();
	}
	public void selectNext() {
		if((selectedIndex + 1) <= countLexemes()) {
			int foundLexemes = 0;
			for (int i = 0; i < text.length() - lex.length() + 1; i++) {
				if(text.substring(i, i + lex.length()).equals(lex)) {
					if(foundLexemes != (selectedIndex)) {
						foundLexemes++;
					}
					else {
						editor.requestFocus();
						editor.setSelectionStart(i+1);
						editor.setSelectionEnd(i + lex.length()+1);
						editor.setSelectedTextColor(Color.CYAN);
						selectedIndex++;
						break;
					}
				}
			}
		}
	}
	
	public void selectPrevious() {
		if((selectedIndex ) > 0) {
			selectedIndex--;
			int foundLexemes = 0;
			for (int i = 0; i < text.length() - lex.length(); i++) {
				if(text.substring(i, i + lex.length()).equals(lex)) {
					if(foundLexemes != (selectedIndex -1)) {
						foundLexemes++;
					}
					else {
						editor.requestFocus();
						editor.setSelectionStart(i+1);
						editor.setSelectionEnd(i + lex.length()+1);
						editor.setSelectedTextColor(Color.CYAN);
						break;
					}
				}
			}
		}
	}
	
	private int countLexemes() {
		String str = editor.getText();
		String findStr = lex;
		int lastIndex = 0;
		int count = 0;

		while(lastIndex != -1){

		    lastIndex = str.indexOf(findStr,lastIndex);

		    if(lastIndex != -1){
		        count ++;
		        lastIndex += findStr.length();
		    }
		}
		return count;
	}
	
	public JEditorPane getEditor() {
		return editor;
	}
}
