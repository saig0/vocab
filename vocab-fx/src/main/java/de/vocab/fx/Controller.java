package de.vocab.fx;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.sun.xml.internal.txw2.output.StreamSerializer;

import de.vocab.fx.ui.AbstractTableCell;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Side;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.WindowEvent;
import javafx.util.Callback;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.beans.property.SimpleBooleanProperty;

public class Controller {

	private static final String TAG_SEPARATOR = ",";

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
	private TableColumn<Word, String> tagColumn;

	@FXML
	private TableColumn<Word, Boolean> actionColumn;

	@FXML
	private Button addWordButton;

	@FXML
	private TextField filterField;

	@FXML
	private TextField tagFilterField;

	@FXML
	private Button specialChar1;

	@FXML
	private TextField tagField;

	private final ObservableList<Word> words;

	private final ObservableList<String> tags;

	public Controller() {
		words = FXCollections.observableArrayList(new Word("Drachenfrucht",
				"thanh long"));

		tags = FXCollections.observableArrayList("Lession 1", "Essen");
	}

	@FXML
	private void initialize() {
		wordColumn.setCellValueFactory(cellData -> cellData.getValue()
				.getWordProperty());
		wordColumn.setCellFactory(TextFieldTableCell.forTableColumn());

		translationColumn.setCellValueFactory(cellData -> cellData.getValue()
				.getTranslationProperty());
		translationColumn.setCellFactory(TextFieldTableCell.forTableColumn());

		tagColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
				cellData.getValue().getTags().stream()
						.collect(Collectors.joining(","))));
		tagColumn.setCellFactory(TextFieldTableCell.forTableColumn());

		actionColumn.setCellValueFactory(cellData -> new SimpleBooleanProperty(
				cellData.getValue() != null));
		actionColumn
				.setCellFactory(new Callback<TableColumn<Word, Boolean>, TableCell<Word, Boolean>>() {

					@Override
					public TableCell<Word, Boolean> call(
							TableColumn<Word, Boolean> column) {
						return new AbstractTableCell<Word, Boolean>() {

							@Override
							protected Node createContent(Boolean b) {
								int index = this.getIndex();
								final Word word = wordTable.getItems().get(
										index);

								Button deleteButton = new Button("löschen");
								deleteButton
										.setOnAction(new EventHandler<ActionEvent>() {

											@Override
											public void handle(ActionEvent event) {
												System.out.println("remove "
														+ word.getWord());
												// TODO: Bug see
												// http://stackoverflow.com/questions/11065140
												// words.remove(word);
												words.removeAll(word);
											}
										});
								return deleteButton;
							}
						};
					}
				});

		addWordButton.disableProperty().bind(
				wordField.textProperty().isEmpty()
						.or(translationField.textProperty().isEmpty()));

		// 1. Wrap the ObservableList in a FilteredList (initially display all
		// data).
		FilteredList<Word> filteredData = new FilteredList<>(words, p -> true);
		// 2. Set the filter Predicate whenever the filter changes.
		ObjectBinding<Predicate<Word>> filterBinding = Bindings
				.createObjectBinding(() -> containsWord(filterField.getText())
						.and(containsTag(tagFilterField.getText())),
						filterField.textProperty(), tagFilterField
								.textProperty());
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

	private Predicate<Word> containsTag(String tags) {
		return word -> {
			// If filter text is empty, display all persons.
			if (tags == null || tags.isEmpty()) {
				return true;
			}

			return Arrays.stream(tags.split(TAG_SEPARATOR)).allMatch(
					t -> word.getTags().contains(t));
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
	public void onTagChanged(CellEditEvent<Word, String> event) {
		Word changedWord = getChangedWord(event);
		Set<String> changedTags = Arrays.stream(
				event.getNewValue().split(TAG_SEPARATOR)).collect(
				Collectors.toSet());
		changedWord.setTags(changedTags);

		updateTags(changedTags);
	}

	private void updateTags(Set<String> changedTags) {
		// tags.addAll(changedTags);
		changedTags.stream().filter(tag -> !tags.contains(tag))
				.forEach(tags::add);
	}

	@FXML
	public void addWord(ActionEvent event) {
		addWord();
	}

	private void addWord() {
		Word newWord = new Word(wordField.getText(), translationField.getText());
		Set<String> selectedTags = Arrays.stream(
				tagField.getText().split(TAG_SEPARATOR)).collect(
				Collectors.toSet());
		newWord.setTags(selectedTags);
		words.add(newWord);

		wordField.clear();
		translationField.clear();
		tagField.clear();

		wordField.requestFocus();

		updateTags(selectedTags);
	}

	@FXML
	public void addWordByKey(KeyEvent event) {
		if (event.getCode().equals(KeyCode.ENTER)) {
			addWord();
		}
	}
}
