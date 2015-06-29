package de.vocab.fx;

import java.io.IOException;

import javafx.beans.property.StringProperty;
import javafx.collections.ObservableSet;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

public class SuggestionTextField extends HBox {

	@FXML
	private TextField textField;

	@FXML
	private ContextMenu contextMenu;

	public SuggestionTextField() { // ObservableSet<String> dataSource
		FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(
				"/suggestion_text_field.fxml"));
		fxmlLoader.setRoot(this);
		fxmlLoader.setController(this);

		try {
			fxmlLoader.load();
		} catch (IOException exception) {
			throw new RuntimeException(exception);
		}

	}

	public void setPromptText(String text) {
		textField.setPromptText(text);
	}

	public StringProperty promptTextProperty() {
		return textField.promptTextProperty();
	}
}
