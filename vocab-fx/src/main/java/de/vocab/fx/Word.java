package de.vocab.fx;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Word {

	private final StringProperty word;
	private final StringProperty translation;
	
	public Word(String word, String translation) {
		this.word = new SimpleStringProperty(word);
		this.translation = new SimpleStringProperty(translation);
	}
	
	public String getWord(){
		return word.getValue();
	}
	
	public void setWord(String word){
		this.word.setValue(word);
	}
	
	public String getTranslation(){
		return translation.getValue();
	}
	
	public void setTranslation(String translation){
		this.translation.setValue(translation);
	}
	
	public StringProperty getWordProperty(){
		return word;
	}
	
	public StringProperty getTranslationProperty(){
		return translation;
	}
}
