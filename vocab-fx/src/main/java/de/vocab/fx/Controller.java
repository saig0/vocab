package de.vocab.fx;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.sun.xml.internal.txw2.output.StreamSerializer;

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

		tagColumn.setCellValueFactory(cellData -> new SimpleStringProperty(
				cellData.getValue().getTags().stream()
						.collect(Collectors.joining(","))));
		tagColumn
				.setCellFactory(new Callback<TableColumn<Word, String>, TableCell<Word, String>>() {

					@Override
					public TableCell<Word, String> call(
							TableColumn<Word, String> arg0) {
						return new TableCell<Word, String>() {

							@Override
							protected void updateItem(String item, boolean empty) {
								super.updateItem(item, empty);

								if (empty) {
									setText(null);
									setGraphic(null);
								} else {
									setText(null);
									Node currentNode = getGraphic();

									TextField textField = TagTextFieldFactory.create(FXCollections
											.observableSet(tags
													.toArray(new String[] {})));
									textField.setText(item);
									
									Node newNode = (Node) textField;
									if (currentNode == null
											|| !currentNode.equals(newNode)) {
										setGraphic(newNode);
									}
								}
							}
						};
					}
				});

		// addWordButton.disableProperty().bind(
		// wordField.textProperty().isEmpty()
		// .or(translationField.textProperty().isEmpty()));
		//
		// removeWordButton.disableProperty().bind(
		// wordTable.getSelectionModel().selectedItemProperty().isNull());

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
		tagField.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {

			@Override
			public void handle(ContextMenuEvent event) {
				String text = tagField.getText();

				List<String> previosTags = Arrays.asList(text
						.split(TAG_SEPARATOR));

				filteredTags.setPredicate(tag -> !previosTags.contains(tag));

				List<MenuItem> items = filteredTags.stream()
						.map(tag -> createTagMenuItem2(tag))
						.collect(Collectors.toList());
				tagContextMenu.getItems().setAll(items);
			}

		});
		tagField.focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				if (newValue == true) {
					filterTags();

					if (!tagContextMenu.isShowing()) {
						tagContextMenu.show(tagField, Side.BOTTOM, 0, 0);
					}
				} else {
					tagContextMenu.hide();
				}
			}

		});
	}

	private boolean lastCharIsEquals(String text, String character) {
		return text.trim().lastIndexOf(character) == text.trim().length() - 1;
	}

	@FXML
	public void tagTyped(KeyEvent event) {
		if (event.getCode().isLetterKey() || event.getCode().isDigitKey()
				|| event.getCode().equals(KeyCode.BACK_SPACE)
				|| event.getCode().equals(KeyCode.COMMA)) {
			filterTags();

			if (!tagContextMenu.isShowing()) {
				tagContextMenu.show(tagField, Side.BOTTOM, 0, 0);
			}
		}
	}

	private void filterTags() {
		String text = tagField.getText();
		if (text.contains(TAG_SEPARATOR)) {
			String lastTag = text
					.substring(text.lastIndexOf(TAG_SEPARATOR) + 1);
			List<String> previosTags = Arrays.asList(text.split(TAG_SEPARATOR));
			List<String> t = null;
			if (text.trim().lastIndexOf(TAG_SEPARATOR) != text.trim().length() - 1) {
				t = previosTags.subList(0, previosTags.size() - 1);
			} else {
				t = previosTags;
			}
			List<String> ts = t;

			filteredTags.setPredicate(tag -> tag.toLowerCase().contains(
					lastTag.toLowerCase())
					&& !ts.contains(tag));
		} else {
			filteredTags.setPredicate(tag -> tag.toLowerCase().contains(
					text.toLowerCase()));
		}

		List<MenuItem> items = filteredTags.stream()
				.map(tag -> createTagMenuItem(tag))
				.collect(Collectors.toList());
		tagContextMenu.getItems().setAll(items);
	}

	private MenuItem createTagMenuItem(String tag) {
		MenuItem menuItem = new MenuItem(tag);
		menuItem.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				String text = tagField.getText();
				if (text.contains(TAG_SEPARATOR)) {
					String substring = text.substring(0,
							text.lastIndexOf(TAG_SEPARATOR));
					tagField.setText(substring + TAG_SEPARATOR + tag);
				} else {
					tagField.setText(tag);
				}
				tagField.end();
			}
		});
		return menuItem;
	}

	private MenuItem createTagMenuItem2(String tag) {
		MenuItem menuItem = new MenuItem(tag);
		menuItem.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				String text = tagField.getText();

				if (lastCharIsEquals(text, TAG_SEPARATOR)
						|| text.trim().isEmpty()) {
					tagField.setText(text + tag);
				} else {
					tagField.setText(text + TAG_SEPARATOR + tag);
				}
				tagField.end();
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
		Word newWord = new Word(wordField.getText(), translationField.getText());
		Set<String> selectedTags = Arrays.stream(
				tagField.getText().split(TAG_SEPARATOR)).collect(
				Collectors.toSet());
		newWord.setTags(selectedTags);
		words.add(newWord);

		wordField.clear();
		translationField.clear();
		tagField.clear();

		updateTags(selectedTags);
	}
}
