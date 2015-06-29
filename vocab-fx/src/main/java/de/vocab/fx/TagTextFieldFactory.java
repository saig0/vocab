package de.vocab.fx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.input.ContextMenuEvent;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

public class TagTextFieldFactory {

	private static final String TAG_SEPARATOR = ",";

	public static TextField create(ObservableSet<String> tags) {
		TextField textField = new TextField();
		ContextMenu contextMenu = new ContextMenu();
		textField.setContextMenu(contextMenu);

		textField
				.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {

					@Override
					public void handle(ContextMenuEvent event) {
						String text = textField.getText();

						List<String> previosTags = Arrays.asList(text
								.split(TAG_SEPARATOR));

						List<MenuItem> items = tags.stream()
								.filter(tag -> !previosTags.contains(tag))
								.map(tag -> createTagMenuItem2(tag, textField))
								.collect(Collectors.toList());
						contextMenu.getItems().setAll(items);
					}

				});
		textField.focusedProperty().addListener(new ChangeListener<Boolean>() {

			@Override
			public void changed(ObservableValue<? extends Boolean> observable,
					Boolean oldValue, Boolean newValue) {
				if (newValue == true) {
					filterTags(textField, contextMenu, tags);

					if (!contextMenu.isShowing()) {
						contextMenu.show(textField, Side.BOTTOM, 0, 0);
					}
				} else {
					contextMenu.hide();
				}
			}

		});

		textField.setOnKeyReleased(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				if (event.getCode().isLetterKey()
						|| event.getCode().isDigitKey()
						|| event.getCode().equals(KeyCode.BACK_SPACE)
						|| event.getCode().equals(KeyCode.COMMA)) {
					filterTags(textField, contextMenu, tags);

					if (!contextMenu.isShowing()) {
						contextMenu.show(textField, Side.BOTTOM, 0, 0);
					}
				}
			}
		});

		return textField;
	}

	private static MenuItem createTagMenuItem(String tag, TextField textField) {
		MenuItem menuItem = new MenuItem(tag);
		menuItem.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				String text = textField.getText();
				if (text.contains(TAG_SEPARATOR)) {
					String substring = text.substring(0,
							text.lastIndexOf(TAG_SEPARATOR));
					textField.setText(substring + TAG_SEPARATOR + tag);
				} else {
					textField.setText(tag);
				}
				textField.end();
			}
		});
		return menuItem;
	}

	private static MenuItem createTagMenuItem2(String tag, TextField textField) {
		MenuItem menuItem = new MenuItem(tag);
		menuItem.setOnAction(new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				String text = textField.getText();

				if (lastCharIsEquals(text, TAG_SEPARATOR)
						|| text.trim().isEmpty()) {
					textField.setText(text + tag);
				} else {
					textField.setText(text + TAG_SEPARATOR + tag);
				}
				textField.end();
			}
		});
		return menuItem;
	}

	private static boolean lastCharIsEquals(String text, String character) {
		return text.trim().lastIndexOf(character) == text.trim().length() - 1;
	}

	private static void filterTags(TextField textField, ContextMenu contextMenu,
			ObservableSet<String> tags) {
		String text = textField.getText();

		String t1 = text;
		List<String> ts = new ArrayList<String>();
		if (text.contains(TAG_SEPARATOR)) {
			String lastTag = text
					.substring(text.lastIndexOf(TAG_SEPARATOR) + 1);
			t1 = lastTag;

			List<String> previosTags = Arrays.asList(text.split(TAG_SEPARATOR));
			if (text.trim().lastIndexOf(TAG_SEPARATOR) != text.trim().length() - 1) {
				ts = previosTags.subList(0, previosTags.size() - 1);
			} else {
				ts = previosTags;
			}
		}

		String t = t1;
		List<String> p = ts;

		List<MenuItem> items = tags.stream()
				.filter(tag -> tag.toLowerCase().contains(t.toLowerCase()))
				.filter(tag -> !p.contains(tag))
				.map(tag -> createTagMenuItem(tag, textField))
				.collect(Collectors.toList());
		contextMenu.getItems().setAll(items);
	}
}
