package de.vocab.fx.ui;

import javafx.scene.Node;
import javafx.scene.control.TableCell;

public abstract class AbstractTableCell<S, T> extends TableCell<S, T> {

	@Override
	protected void updateItem(T item, boolean empty) {
		super.updateItem(item, empty);

		if (empty) {
			setText(null);
			setGraphic(null);
		} else {
			setText(null);
			Node currentNode = getGraphic();
			Node newNode = createContent(item);

			if (currentNode == null || !currentNode.equals(newNode)) {
				setGraphic(newNode);
			}
		}
	}

	protected abstract Node createContent(T item);
}
