package application;
	
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.control.ListView;

import static org.junit.Assert.assertEquals;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;

import application.JavaFxTest.Word;

public class JavaFxTest extends Application {
	private static ObservableList<String> wordPlace = FXCollections.observableArrayList();
	private static ObservableList<String> wordWord = FXCollections.observableArrayList();
	
	public void start(Stage primaryStage) {
		try {
			AnchorPane p = new AnchorPane();
			//AnchorPane p = FXMLLoader.load(getClass().getResource("Main.fxml"));
			Scene scene1 = new Scene(p,200,610);
			
			Text textI = new Text();
			ListView<String> textO = new ListView<String>();
			ListView<String> textO1 = new ListView<String>();
			ListView<Integer> counter = new ListView<Integer>();
		    
			textI.setText("Press Calculate To Display \nThe 20 Most Words Used");
			textI.setLayoutX(35);
			textI.setLayoutY(20);
			
			Button b1 = new Button();
			b1.setOnAction(e -> primaryStage.setScene(scene1)); 
			b1.setText("Calculate!");
			b1.setLayoutX(70);
			b1.setLayoutY(50);
			
			counter.setLayoutX(15);//counter output
			counter.setLayoutY(90);
			counter.setMaxWidth(100);
			counter.setMinHeight(480);
						
			textO.setLayoutX(50);//word output
			textO.setLayoutY(90);
			textO.setMaxWidth(100);
			textO.setMinHeight(480);
			
			textO1.setLayoutX(125);//# used output
			textO1.setLayoutY(90);
			textO1.setMaxWidth(60);
			textO1.setMinHeight(480);
			
			Button b2 = new Button();
			
			b2.setOnAction(e -> primaryStage.setScene(scene1)); //what do when press b1
			b2.setText("Clear All");
			b2.setLayoutX(70);
			b2.setLayoutY(575);
		
			b1.setOnAction(new EventHandler<ActionEvent>() {  
				public void handle(ActionEvent arg0) {
					textO.getItems().clear();
					textO1.getItems().clear();
					counter.getItems().clear();
					textO.getItems().addAll(wordWord);
					textO1.getItems().addAll(wordPlace);
					for(int i=1; i<21; i++) {
						counter.getItems().addAll(i);
					}
	 			}  
			} );
			
			b2.setOnAction(new EventHandler<ActionEvent>() {  //what do when press b2
				public void handle(ActionEvent arg0) {
					textO.getItems().clear();
					textO1.getItems().clear();
					counter.getItems().clear();
				}
			});
			
			p.getChildren().add(b1);
			p.getChildren().add(textI);
			p.getChildren().add(counter);
			p.getChildren().add(textO);
			p.getChildren().add(textO1);
			p.getChildren().add(b2);
			
			primaryStage.setScene(scene1);
			primaryStage.setTitle("UI Design Assignment");
			primaryStage.show();
			
			wordFind();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws IOException {
		launch(args); }
	
	public Collection<Word> wordFind() throws IOException{
		Map<String, Word> map = new HashMap<String, Word>();
		Document doc = Jsoup.connect("https://www.gutenberg.org/files/1065/1065-h/1065-h.htm").get();

        //Get the text from the page, excluding HTML
        String text = doc.body().text();

        //Create BufferedReader to count the allWords
        BufferedReader reader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8))));
        String line;
        
        while ((line = reader.readLine()) != null) {
            String[] allWords = line.split(" ");
            for (String word : allWords) {
                Word wordFreq = map.get(word);
                if (wordFreq == null) {
                    wordFreq = new Word();
                    wordFreq.word = word;
                    wordFreq.count = 0;
                    map.put(word, wordFreq);
                }
                wordFreq.count++;
            }
        }

        reader.close();
 
        SortedSet<Word> sortedWords = new TreeSet<Word>(map.values());
        
        int i = 0;
        int maxWords = 20;
        
		for (Word currentWord : sortedWords) { 
			
            if (i >= maxWords) { 
            	break;
            }
            String numberUsed=String.valueOf(currentWord.count);//convert num to string
            String listWord=String.valueOf(currentWord.word); // convert 'Map<String,JavaFxTest.Word>' to string

            wordPlace.add(numberUsed);//store values into private hash map gsMap
            wordWord.add(listWord);

            i++;
		}
		return sortedWords;
	}
	
	public static class Word implements Comparable<Word> {
		String word;
		int count;
		@Override
		public int hashCode() {
        	return word.hashCode(); 
		}
		@Override
		public boolean equals(Object obj) { 
    	   return word.equals(((Word)obj).word);
		}
		@Override
		public int compareTo(Word a) { 
    	   return a.count - count;
		}
	}
}