package de.vocab.fx;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class Controller {

	@FXML
	private TextField wordField;

	@FXML
	private TextField translationField;

	@FXML
	private TableView<Word> wordTable;
	
	public void init(){
		System.out.println("init");
		
	}

	@FXML
	public void addWord(ActionEvent event) {
		System.out.println("add word");
		Word newWord = new Word(wordField.getText(), translationField.getText());
		wordTable.getItems().add(newWord);
		
		wordField.clear();
		translationField.clear();
	}
}
