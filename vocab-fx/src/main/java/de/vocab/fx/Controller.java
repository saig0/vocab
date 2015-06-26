package de.vocab.fx;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;

public class Controller {

	@FXML
	private TextField wordField;

	@FXML
	private TextField translationField;

	@FXML
	private TableView<Word> wordTable;

	@FXML
	private TableColumn<Word, String> wordColumn;

	@FXML
	private TableColumn<Word, String> translationColumn;

	@FXML
	private Button addWordButton;

	@FXML
	private Button removeWordButton;

	@FXML
	private void initialize() {
		wordColumn.setCellValueFactory(cellData -> cellData.getValue()
				.getWordProperty());
		wordColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		// wordColumn.setOnEditCommit(event -> event.getTableView().getItems()
		// .get(event.getTablePosition().getRow())
		// .setWord(event.getNewValue()));

		translationColumn.setCellValueFactory(cellData -> cellData.getValue()
				.getTranslationProperty());
		translationColumn.setCellFactory(TextFieldTableCell.forTableColumn());
		// translationColumn.setOnEditCommit(event -> event.getTableView()
		// .getItems().get(event.getTablePosition().getRow())
		// .setTranslation(event.getNewValue()));

		addWordButton.disableProperty().bind(
				wordField.textProperty().isEmpty()
						.or(translationField.textProperty().isEmpty()));

		removeWordButton.disableProperty().bind(
				wordTable.getSelectionModel().selectedItemProperty().isNull());
	}

	@FXML
	public void deleteWord(ActionEvent event) {
		Word selectedWord = wordTable.getSelectionModel().getSelectedItem();
		wordTable.getItems().remove(selectedWord);
	}

	@FXML
	public void onWordChanged(TableColumn.CellEditEvent<Word, String> event) {
		Word editedWord = getChangedWord(event);
		editedWord.setWord(event.getNewValue());
	}

	private Word getChangedWord(TableColumn.CellEditEvent<Word, String> event) {
		return event.getTableView().getItems()
				.get(event.getTablePosition().getRow());
	}

	@FXML
	public void onTranslationChanged(
			TableColumn.CellEditEvent<Word, String> event) {
		Word editedWord = getChangedWord(event);
		editedWord.setTranslation(event.getNewValue());
	}

	@FXML
	public void addWord(ActionEvent event) {
		Word newWord = new Word(wordField.getText(), translationField.getText());
		wordTable.getItems().add(newWord);

		wordField.clear();
		translationField.clear();
	}
}
