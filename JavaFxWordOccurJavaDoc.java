package application;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.scene.control.ListView;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/** 
 * The JavaFxWordOccurJavaDoc class implements a Main menu with multiple buttons that lead the user to another scene 
 * with an interactive text analyzer that will read a poem from a website and output the most frequently used words 
 * in a list of order pairs or exit the program.
 * 
 * @author Alex Visco
 * @version 1.3 Nov 3, 2022
 */
public class JavaFxWordOccurJavaDoc extends Application {

	private static ObservableList<String> wordPlace = FXCollections.observableArrayList();
	private static ObservableList<String> wordWord = FXCollections.observableArrayList();
	private int calcButCount = 0;
		
	/**
	 * The Start function constructs a stage to hold the GUI to be display to the user. The stage holds two scenes.
	 * The Main Menu scene, sceneOpen, is the primaryStage and has two buttons waiting for the user to select 
	 * "Word Occurrences" or "Quit". "Quit" presents a new pop-up window to the user to verify if their choice was 
	 * correct. If "OK' is selected the program ends. "Word Occurrences" brings the user to a new scene, sceneWord 
	 * with three buttons. "Calculate" will display the top a list of the 20 most used words, how many times each word 
	 * is used, and change the button text. "Clear All" will clear the data entered and change the button text. "Main Menu" 
	 * will bring the user back to the Main Menu, clear all data entered, and reset button text to default.
	 * 
	 * 
	 * @param primaryStage The Graphical User Interface is created and the Main Menu scene is loaded.
	 */
	public void start(Stage primaryStage) {
		try {
			AnchorPane open = new AnchorPane();			
			AnchorPane p = new AnchorPane();
			Scene sceneOpen = new Scene(open,250,150);
			Scene sceneWord = new Scene(p,200,610);
			
			Text textOpen = new Text();
			textOpen.setText("Weclome to Alex's UI Design Assignment");
			textOpen.setLayoutX(20);
			textOpen.setLayoutY(20);

			Text textI = new Text();
			ListView<String> textO = new ListView<String>();
			ListView<String> textO1 = new ListView<String>();
			ListView<Integer> counter = new ListView<Integer>();
		    
			textI.setText("Press Calculate To Display \nThe 20 Most Words Used");
			textI.setLayoutX(35);
			textI.setLayoutY(20);
			
			//sceneWord Calculate Button
			Button bCalc = new Button();
			bCalc.setOnAction(e -> primaryStage.setScene(sceneWord)); 
			bCalc.setText("Calculate");
			bCalc.setLayoutX(60);
			bCalc.setLayoutY(50);
			
			counter.setLayoutX(15);
			counter.setLayoutY(90);
			counter.setMaxWidth(35);
			counter.setMinHeight(480);
						
			textO.setLayoutX(50);
			textO.setLayoutY(90);
			textO.setMaxWidth(75);
			textO.setMinHeight(480);
			
			textO1.setLayoutX(125);
			textO1.setLayoutY(90);
			textO1.setMaxWidth(60);
			textO1.setMinHeight(480);
			
			//sceneWord Clear Button
			Button bClear = new Button();
			bClear.setOnAction(e -> primaryStage.setScene(sceneWord));
			bClear.setText("Clear All");
			bClear.setLayoutX(20);
			bClear.setLayoutY(580);
			
			//Clear All button action
			bClear.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent arg0) {
					bCalc.setText("Calculate");
					bClear.setText("Cleared!");
					textO.getItems().clear();
					textO1.getItems().clear();
					counter.getItems().clear();
				}
			});
			
			//Calculated button action
			bCalc.setOnAction(new EventHandler<ActionEvent>() {  
				public void handle(ActionEvent arg0) {
					calcButCount++;
					textO.getItems().clear();
					textO1.getItems().clear();
					counter.getItems().clear();
					textO.getItems().addAll(wordWord);
					textO1.getItems().addAll(wordPlace);
					bCalc.setText("Calculated!");
					bClear.setText("Clear All");
					
					for(int i=1; i<21; i++) {
						counter.getItems().addAll(i);
					}
	 			}  
			} );
		
			//sceneWord back to SceneOpen(MAIN MENU BUTTON)
			Button bBack = new Button();
			bBack.setText("Main Menu");
			bBack.setLayoutX(100);
			bBack.setLayoutY(580);
			
			//Main Menu button action
			bBack.setOnAction(new EventHandler<ActionEvent>() {  
				public void handle(ActionEvent arg0) {
					primaryStage.setScene(sceneOpen);
					primaryStage.setTitle("UI Design Assignment");
					bCalc.setText("Calculate");
					bClear.setText("Clear All");
					textO.getItems().clear();
					textO1.getItems().clear();
					counter.getItems().clear();
				}
			});
			
			//sceneOpen quit button
			Button bQuit = new Button();
			bQuit.setText("Quit");
			bQuit.setLayoutX(100);
			bQuit.setLayoutY(100);
			
			//Quit button action
			bQuit.setOnAction(new EventHandler<ActionEvent>() {  
				public void handle(ActionEvent arg0) {
					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setTitle("WARNING");
					alert.setHeaderText("Are you sure you want to Quit?");
					if(calcButCount ==0) {
						alert.setContentText("\t\t\tYou did not find the top 20 words.");
					}else if (calcButCount==1) { //GRAMMAR
						alert.setContentText("\t\t\tYou found the top 20 words "+ calcButCount +" time.");
					}else if(calcButCount>=10){
						alert.setContentText("\t\tI cannot belive you used the program "+ calcButCount +" times!\n\n\t\t\t*~*~ Congratulations ~*~*\n ");
					}else {
						alert.setContentText("\t\t\tYou found the top 20 words "+ calcButCount +" times.");
					}
					Optional<ButtonType> result = alert.showAndWait();
					if (result.get() == ButtonType.OK){
						System.exit(0);
					} else {
					    // ... user chose CANCEL or closed the dialog
					}
				}
			});

			//sceneOpen word occurrences button
			Button bWordOccur = new Button();
			bWordOccur.setText("Word Occurrences");
			bWordOccur.setLayoutX(65);
			bWordOccur.setLayoutY(50);
			
			//Word Occurrences Button action
			bWordOccur.setOnAction(new EventHandler<ActionEvent>() {  
				public void handle(ActionEvent arg0) {
					primaryStage.setScene(sceneWord);
					primaryStage.setTitle("Word Occurrences");
					bQuit.setText("Quit");
					bQuit.setLayoutX(100);
					bQuit.setLayoutY(100);
				}
			});

			open.getChildren().add(textOpen);
			open.getChildren().add(bWordOccur);
			open.getChildren().add(bQuit);
			
			p.getChildren().add(bCalc);
			p.getChildren().add(textI);
			p.getChildren().add(counter);
			p.getChildren().add(textO);
			p.getChildren().add(textO1);
			p.getChildren().add(bClear);
			p.getChildren().add(bBack); 
			
			primaryStage.setScene(sceneOpen);
			primaryStage.setTitle("UI Design Assignment");
			primaryStage.setResizable(false);
			primaryStage.show();
			
			wordFind();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * The Main function calls start function to launch the GUI and start the wordFind function.
	 * 
	 * @param args Is used to launch the GUI.
	 * @throws IOException Indicates there is an IOException declared.
	 */
	public static void main(String[] args) throws IOException{
			launch(args);
		}
		
	/**
	 * The wordFind function searches for all real words on the website within the poem while 
	 * counting how often each word is used. A HashMap is created to keep track of all 
	 * unique words. A SortedSet is created to display how frequent unique words are used.  
	 * The frequency and unique Words are then converted and stored into Private Strings to be output to the GUI.
	 * 
	 * @return Each individual word that is used in the poem.
	 * @throws IOException If an input or output exception occurred.
	 */
	public Collection<Word> wordFind() throws IOException{
		Map<String, Word> map = new HashMap<String, Word>();
		Document doc = Jsoup.connect("https://www.gutenberg.org/files/1065/1065-h/1065-h.htm").get();

        String text = doc.body().text();
        
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
            String numberUsed=String.valueOf(currentWord.count);
            String listWord=String.valueOf(currentWord.word);

            wordPlace.add(numberUsed);
            wordWord.add(listWord);

            i++;
		}
		return sortedWords;
	}
	
	/** 
	 * The Word class counts how frequent each unique word is used within the poem on the website.
	 * 
	 * @author Alex Visco
	 * @version 1.3 Nov 3, 2022
	 */
	public static class Word implements Comparable<Word> {
		String word;
		int count;
		@Override
		/** 
		 * The compareTo function counts how frequent a unique word is used within the poem on the website.
		 * 
		 * @param a The stored unique Word frequency is compared to the current count.
		 * @return Update how frequent each word is used.
		 */
		public int compareTo(Word a) { 
    	   return a.count - count;
		}
	}
}