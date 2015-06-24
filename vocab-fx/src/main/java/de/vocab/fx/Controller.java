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

	@FXML
	public void addWord(ActionEvent event) {
		System.out.println("add word");
		//wordTable.getItems().add(new Word());
	}
}
