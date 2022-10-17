package application;
	
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.event.ActionEvent;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;

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

import application.JavaFxTest.Word;

public class JavaFxTest extends Application {
	private static Map<String, String> gsMap = new HashMap<String, String>();
	
	public void start(Stage primaryStage) {
		try {
			AnchorPane p = new AnchorPane();
			//AnchorPane p = FXMLLoader.load(getClass().getResource("Main.fxml"));
			Scene scene1 = new Scene(p,600,500);
			Text textI = new Text();
			ListView<String> textO = new ListView<String>();
			//TextField textO = new TextField();
			//textO.setId(getMessages());
		    
			textI.setText("Press Calculate To Display \nThe 20 Most Words Used");
			textI.setLayoutX(235);
			textI.setLayoutY(20);
			
			Button b1 = new Button();
			b1.setOnAction(e -> primaryStage.setScene(scene1)); 
			b1.setText("Calculate!");
			b1.setLayoutX(270);
			b1.setLayoutY(50);
			
			textO.setLayoutX(185);//output
			textO.setLayoutY(90);
			
			
			b1.setOnAction(new EventHandler<ActionEvent>() {  
				  
				public void handle(ActionEvent arg0) {
					textO.getItems().clear();
					textO.getItems().add("The 20 most used words:");
			        textO.getItems().add("~~~~~~~~~~~~~~~~~");
			        textO.getItems().add(gsMap.get("")); //display top 20
				}  
			} );
					
			p.getChildren().add(b1);
			p.getChildren().add(textI);
			p.getChildren().add(textO);
			
			//scene1.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
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
	
	public static Collection<Word> wordFind() throws IOException{
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
        
        
		for (Word currentWord : sortedWords) { //not working
			
            if (i >= maxWords) { 
            	break;
            }
   
            String numberUsed=String.valueOf(currentWord.count);//convert num to string
            String listWord=String.valueOf(currentWord.word); // convert 'Map<String,JavaFxTest.Word>' to string
            gsMap.put(numberUsed, listWord);//store values into private hash map gsMap
           
            System.out.println(currentWord.word + "\t" + currentWord.count ); //console print
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

	public Map<String, String> getMap() {
       return this.gsMap;
	}

	public void setMap(HashMap<String, String> input) {
       this.gsMap = input;
	}
}