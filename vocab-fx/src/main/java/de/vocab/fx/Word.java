package de.vocab.fx;

import java.util.Set;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

public class Word {

	private final StringProperty word;
	private final StringProperty translation;
	private final ObservableSet<String> tags = FXCollections.observableSet();

	public Word(String word, String translation) {
		this.word = new SimpleStringProperty(word);
		this.translation = new SimpleStringProperty(translation);
	}

	public String getWord() {
		return word.getValue();
	}

	public void setWord(String word) {
		this.word.setValue(word);
	}

	public void setTags(Set<String> tags) {
		this.tags.clear();
		this.tags.addAll(tags);
	}

	public Set<String> getTags() {
		return tags.stream().collect(Collectors.toSet());
	}

	public ObservableSet<String> getTagsProperty() {
		return tags;
	}

	public String getTranslation() {
		return translation.getValue();
	}

	public void setTranslation(String translation) {
		this.translation.setValue(translation);
	}

	public StringProperty getWordProperty() {
		return word;
	}

	public StringProperty getTranslationProperty() {
		return translation;
	}
}
