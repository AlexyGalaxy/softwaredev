package application;
	
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class JavaFxTest extends Application {
	static SortedSet<Word> sortedWords = new TreeSet<Word>(); //how do you use hashmaps with javafx?
		
	public void start(Stage primaryStage) {
		try {
			Pane p = new Pane();
						
			Scene scene1 = new Scene(p,400,400);
			ListView textO = new ListView();
			HBox hbox = new HBox(textO);
			
			Button b1 = new Button();
			b1.setOnAction(e -> primaryStage.setScene(scene1));   
			
			Text textI = new Text();
			
			textI.setText("Press Calculate To Display \nThe 20 Most Words Used");
			textI.setLayoutX(135);
			textI.setLayoutY(20);
			
			b1.setText("Calculate!");
			b1.setLayoutX(170);
			b1.setLayoutY(50);
						
			textO.setLayoutX(80);//output
			textO.setLayoutY(80);
						
			b1.setOnAction(new EventHandler<ActionEvent>() {  
				  
				public void handle(ActionEvent arg0) { 
				
					int i = 0;
			        int maxWords = 20;
			        textO.getItems().add("The " + maxWords + " most used words:");
			        textO.getItems().add("~~~~~~~~~~~~~~~~~");
			        
					for (Word currentWord : sortedWords) { //not working
						
			            if (i >= maxWords) { 
			            	break;
			            }
			   
			            textO.getItems().add(currentWord.word + "\t" + currentWord.count );
			            System.out.println(currentWord.word + "\t" + currentWord.count );
			            i++;
			       }
				}  
			} );
						
			p.getChildren().add(b1);
			p.getChildren().add(textI);
			p.getChildren().add(textO);

			
			scene1.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene1);
			primaryStage.setTitle("UI Design Assignment");
			primaryStage.show();
			
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws IOException {
		launch(args);
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
 
        sortedWords = new TreeSet<Word>(map.values());
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