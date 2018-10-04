package com.tyrsa.splatone.ui;

import java.awt.Color;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.SimpleAttributeSet;

import com.tyrsa.splatone.model.Tree;

public class CustomPane extends JScrollPane {

	private static final long serialVersionUID = 6174880998943055159L;
	private final int NUM_OF_LINES = 5;
	private final int NUM_OF_SYNMOLS_ON_SCREEN = 5000;
	private Tree node;
	private static JEditorPane editor = new JEditorPane();
	private JScrollPane scroll = new JScrollPane();
	private int selectedIndex;
	private String lex;
	private String text;
	private ArrayList<String> lines = new ArrayList<>();
	
	private String readPrevBlock(Tree node) throws Exception{
		String result = "";
		File file = node.getNode();
		String read = "";
		int endPoint = node.getLinesReadedBegin();
		int startPoint = endPoint - NUM_OF_LINES;
		BufferedReader reader = new BufferedReader(new FileReader(file));
		StringBuilder builder = new StringBuilder();
		int i = 0;
		while((result = reader.readLine()) != null) {
			if(i >= startPoint && i < endPoint) {
				lines.add(result);
				builder.append(result);
			}
			if(i >= endPoint) {
				break;
			}
			i++;
		}
		reader.close();
		node.setLinesReadedEnd(i);
		result = builder.toString();
		return result;
	}
	
	private String readNextBlock(Tree node) throws Exception {
		String result ="";
		File file = node.getNode();
		String read = "";
		int startPoint = node.getLinesReadedEnd();
		int endPoint = startPoint + NUM_OF_LINES;
		BufferedReader reader = new BufferedReader(new FileReader(file));
		StringBuilder builder = new StringBuilder();
		int i = 0;
		while((result = reader.readLine()) != null) {
			if(i >= startPoint && i < endPoint) {
				lines.add(result);
				builder.append(result);
			}
			if(i >= endPoint) {
				break;
			}
			i++;
		}
		reader.close();
		node.setLinesReadedEnd(i);
		result = builder.toString();
		return result;
	}
	
	public CustomPane(Tree node, String lex) throws Exception {
		
		selectedIndex = 0;
		text = readNextBlock(node);
		
		this.node = node;
		this.lex = lex;
		
		
		Document document = editor.getDocument();
		SimpleAttributeSet keyWord = new SimpleAttributeSet();
		document.insertString(document.getLength(), text, keyWord);
		

		scroll.setViewportView(editor);
		this.setViewportView(scroll);
		
		
		
	} 
	
	public void update() {
		editor = new JEditorPane();
		
		this.editor.setText(text);
		scroll.setViewportView(editor);
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
		if((selectedIndex + 1) <= countLexemes()) { // Сверяем индекс текущей лексемы с общим кол-вом лексем
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
		else{  // если лексем больше нет,пробуем загрузить документ дальше
			boolean more = false;
			try {
				more = tryLoadNext();
				if(more) {
					selectNext();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private boolean tryLoadNext() throws Exception {
		String read = readNextBlock(node);
		if(!read.equals("")) {
			text = text + read;
			Document document = editor.getDocument();
			SimpleAttributeSet keyWord = new SimpleAttributeSet();
			document.insertString(document.getLength(), read, keyWord);
			if(document.getLength() > NUM_OF_SYNMOLS_ON_SCREEN) {
				int length = 0;
				for(int i = 0; i < NUM_OF_LINES; i++) {
					String string = lines.remove(0);
					length += string.length();
				}
				node.setLinesReadedBegin(node.getLinesReadedBegin() + NUM_OF_LINES);
				document.remove(0, length);
			}
			return true;
		}
		return false;
	}
	
	private boolean tryLoadPrev() throws Exception{
		String read = readPrevBlock(node);
		if(!read.equals("")) {
			text = text + read;
			Document document = editor.getDocument();
			SimpleAttributeSet keyWord = new SimpleAttributeSet();
			document.insertString(0, read, keyWord);
			if(document.getLength() > NUM_OF_SYNMOLS_ON_SCREEN) {
				int length = 0;
				for(int i = 0; i < NUM_OF_LINES; i++) {
					String string = lines.remove(lines.size() - 1);
					length += string.length();
				}
				node.setLinesReadedBegin(node.getLinesReadedBegin() + NUM_OF_LINES);
				document.remove(document.getLength() - length, length);
			}
			return true;
		}
		return false;
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
		else {
			boolean more = false;
			try {
				more = tryLoadPrev();
			}catch(Exception e) {
				e.printStackTrace();
			}
			if(more) {
				selectPrevious();
			}
		}
	}
	
	private int countLexemes() {
		String str = "";
		try {
			str = editor.getDocument().getText(0, editor.getDocument().getLength());
		} catch (BadLocationException e) {
			e.printStackTrace();
		}
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
