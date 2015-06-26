package de.vocab.fx;

import java.util.function.Predicate;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;

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
	private TextField filterField;

	private final ObservableList<Word> words;

	public Controller() {
		words = FXCollections.observableArrayList(new Word("Drachenfrucht",
				"thanh long"));
	}

	@SuppressWarnings("restriction")
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

		// 1. Wrap the ObservableList in a FilteredList (initially display all
		// data).
		FilteredList<Word> filteredData = new FilteredList<>(words, p -> true);
		// 2. Set the filter Predicate whenever the filter changes.
		ObjectBinding<Predicate<Word>> filterBinding = Bindings
				.createObjectBinding(() -> containsWord(filterField
						.textProperty().getValue()), filterField.textProperty());
		filteredData.predicateProperty().bind(filterBinding);

		// 3. Wrap the FilteredList in a SortedList.
		SortedList<Word> sortedData = new SortedList<>(filteredData);
		// 4. Bind the SortedList comparator to the TableView comparator.
		sortedData.comparatorProperty().bind(wordTable.comparatorProperty());
		// 5. Add sorted (and filtered) data to the table.
		wordTable.setItems(sortedData);
	}

	private Predicate<Word> containsWord(String containingWord) {
		return word -> {
			// If filter text is empty, display all persons.
			if (containingWord == null || containingWord.isEmpty()) {
				return true;
			}

			String lowerCaseFilter = containingWord.toLowerCase();

			if (word.getWord().toLowerCase().contains(lowerCaseFilter)) {
				return true; // Filter matches first name.
			} else if (word.getTranslation().toLowerCase()
					.contains(lowerCaseFilter)) {
				return true; // Filter matches last name.
			}
			return false; // Does not match.
		};
	}

	@FXML
	public void deleteWord(ActionEvent event) {
		Word selectedWord = wordTable.getSelectionModel().getSelectedItem();
		words.remove(selectedWord);
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
		words.add(newWord);

		wordField.clear();
		translationField.clear();
	}
}
