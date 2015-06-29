package de.vocab.fx;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
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

	@FXML
	private Button specialChar1;

	@FXML
	private TextField tagField;

	private final ObservableList<Word> words;

	private final FilteredList<String> filteredTags;

	private final ObservableList<String> tags;

	private ContextMenu tagContextMenu;

	public Controller() {
		words = FXCollections.observableArrayList(new Word("Drachenfrucht",
				"thanh long"));

		tags = FXCollections.observableArrayList("Lession 1", "Essen");
		filteredTags = new FilteredList<String>(tags);
	}

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

		// specialChar1.setOnAction(new EventHandler<ActionEvent>() {
		//
		// @Override
		// public void handle(ActionEvent event) {
		// Node focusOwner = wordTable.getScene().getFocusOwner();
		// System.out.println(focusOwner.getId());
		//
		// String specialChar = ((Button) event.getSource()).getText();
		// // wordField.setText(wordField.getText() + specialChar);
		// TextField tf = (TextField) focusOwner;
		// tf.setText(tf.getText() + specialChar);
		// }
		// });

		tagContextMenu = new ContextMenu();
		tagField.setContextMenu(tagContextMenu);
	}

	@FXML
	public void tagTyped(KeyEvent event) {
		if (event.getCode().isLetterKey() || event.getCode().isDigitKey()
				|| event.getCode().equals(KeyCode.BACK_SPACE)) {
			String text = tagField.getText();
			filteredTags.setPredicate(tag -> tag.toLowerCase().contains(
					text.toLowerCase()));

			List<MenuItem> items = filteredTags.stream()
					.map(tag -> createTagMenuItem(tag))
					.collect(Collectors.toList());
			tagContextMenu.getItems().setAll(items);

			if (!tagContextMenu.isShowing()) {
				tagContextMenu.show(tagField, Side.BOTTOM, 0, 0);
			}
		}
	}

	private MenuItem createTagMenuItem(String tag) {
		MenuItem menuItem = new MenuItem(tag);
		menuItem.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				// String text = tagField.getText();
				// text.substring(text.lastIndexOf(","));
				tagField.setText(tag);
			}
		});
		return menuItem;
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
