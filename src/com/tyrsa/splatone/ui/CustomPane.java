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
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import com.tyrsa.splatone.model.Tree;

public class CustomPane extends JScrollPane {

	private static final long serialVersionUID = 6174880998943055159L;
	private final int SIZE_OF_BLOCK = 1024;
	private final int NUM_OF_SYNMOLS_ON_SCREEN = 5000;
	private Tree node;
	private static JEditorPane editor = new JEditorPane();
	private JScrollPane scroll = new JScrollPane();
	private volatile int selectedIndex;
	private String lex;
	private String text;
	private volatile int startPoint = 0;
	private volatile int endPoint = SIZE_OF_BLOCK;
	private Document document;
	
	private String readPrevBlock() throws Exception{
		String result = "";
		File file = node.getNode();
		String read = "";
		if(endPoint == 0) {
			return "";
		}
		startPoint = endPoint - SIZE_OF_BLOCK;
		endPoint = startPoint;
		BufferedReader reader = new BufferedReader(new FileReader(file));
		reader.skip(startPoint);
		char [] c = new char[SIZE_OF_BLOCK];
		reader.read(c);
		result = new String(c).trim();
		reader.close();
		return result;
	}
	
	private String readNextBlock() throws Exception {
		String result ="";
		File file = node.getNode();
		String read = "";
		BufferedReader reader = new BufferedReader(new FileReader(file));
		reader.skip(startPoint);
		char [] c = new char[1024];
		int readChars;
		readChars = reader.read(c);
		result = new String(c).trim();
		reader.close();
		startPoint = endPoint;
		endPoint = startPoint + SIZE_OF_BLOCK;
		return result;
	}
	
	public CustomPane(Tree node, String lex) throws Exception {
		
		selectedIndex = 0;
		this.node = node;
		this.lex = lex;
		text = readNextBlock();
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
	
	public void selectAll() throws BadLocationException {
		editor.requestFocus();
		//editor.selectAll();
//		Document document2 = editor.getDocument();
//		Style def = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
//	    Style s = document.addStyle("bold", def);
//	    StyleConstants.setBold(s, true);
//		for(int i = 0; i < document.getLength() - lex.length(); i++) {
//			if(text.substring(i, i+lex.length()).equals(lex)) {
//				int last = i + lex.length();
//				String tmp = document.getText(i, last);
//				document.remove(i, last);
//				document.insertString(i, tmp, document.getStyle("bold"));
//			}
//		}
	    
	}
	public void selectNext() throws BadLocationException {
		String text = editor.getText().replaceAll("\\n", "");
		int num = countLexemes(text);
		if((selectedIndex) < num) { // Сверяем индекс текущей лексемы с общим кол-вом лексем
			int foundLexemes = 0;
			for (int i = 0; i < text.length() - lex.length() + 1; i++) {
				if(text.substring(i, i + lex.length()).equals(lex)) {
					if(foundLexemes != (selectedIndex)) {
						foundLexemes++;
					}
					else {
						editor.requestFocus();
						editor.setSelectionStart(i);
						editor.setSelectionEnd(i + lex.length());
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
				if(more ) {
					selectNext();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private boolean tryLoadNext() throws Exception {
		String read = readNextBlock();
		if(!read.equals("")) {
			text = text + read;
			Document document = editor.getDocument();
			SimpleAttributeSet keyWord = new SimpleAttributeSet();
			document.insertString(document.getLength(), read, keyWord);
			if(document.getLength() > NUM_OF_SYNMOLS_ON_SCREEN) {
				int length = 0;
				String string = text.substring(0, SIZE_OF_BLOCK);
				text.replaceFirst(string, "");
				length += string.length();
				node.setLinesReadedBegin(node.getLinesReadedBegin() + SIZE_OF_BLOCK);
				document.remove(0, length);
				selectedIndex -= countLexemes(string);
			}
			return true;
		}
		return false;
	}
	
	private boolean tryLoadPrev() throws Exception{
		String read = readPrevBlock();
		if(!read.equals("")) {
			Document document = editor.getDocument();
			SimpleAttributeSet keyWord = new SimpleAttributeSet();
			int countLexemes = countLexemes(read);
			selectedIndex += countLexemes;
			document.insertString(0, read, keyWord);
			if(document.getLength() > NUM_OF_SYNMOLS_ON_SCREEN) {
				int length = 0;
				String string = text.substring(text.length() - SIZE_OF_BLOCK, text.length() - 1);
				length += string.length();
				node.setLinesReadedBegin(node.getLinesReadedBegin() + SIZE_OF_BLOCK);
				document.remove(document.getLength() - length, length);
			}
			return true;
		}
		return false;
	}

	public void selectPrevious() {
		if((selectedIndex ) > 0) {
			int foundLexemes = 0;
			String text = editor.getText().replaceAll("\\n", "");
			for (int i = 0; i < text.length() - lex.length(); i++) {
				if(text.substring(i, i + lex.length()).equals(lex)) {
					if(foundLexemes != (selectedIndex -1)) {
						foundLexemes++;
					}
					else {
						editor.requestFocus();
						editor.setSelectionStart(i);
						editor.setSelectionEnd(i + lex.length());
						editor.setSelectedTextColor(Color.CYAN);
						selectedIndex--;
						break;
					}
				}
			}
		}
		else {
			boolean more = false;
			try {
				if(startPoint != 0) {
					more = tryLoadPrev();
					if(more) {
						selectPrevious();
					}	
				}
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private int countLexemes(String str) {
	
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
	public static String removeEnd(String str, String remove) {
	      if (isEmpty(str) || isEmpty(remove)) {
	          return str;
	      }
	      if (str.endsWith(remove)) {
	          return str.substring(0, str.length() - remove.length());
	      }
	      return str;
	  }
	 public static boolean isEmpty(String str) {
	      return str == null || str.length() == 0;
	  }
}
