package application;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.SortedSet;
import java.util.TreeSet;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import application.DatabaseJavaFxWordOccurJavaDoc.Word;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class WordClient extends Application {
  // IO streams
	DataOutputStream toServer = null;
  	DataInputStream fromServer = null;

  	private static ObservableList<String> wordPlace = FXCollections.observableArrayList();
	private static ObservableList<String> wordWord = FXCollections.observableArrayList();
	private static int calcButCount = 0;
	private static boolean firstRun = true;
	private static int dataEntered = 0;
  
  @Override // Override the start method in the Application class
  public void start(Stage primaryStage) {
	  try {
			AnchorPane open = new AnchorPane();
			AnchorPane p = new AnchorPane();
			Scene sceneOpen = new Scene(open, 250, 150);
			Scene sceneWord = new Scene(p, 200, 610);

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

			// sceneWord Word Occurrences Button
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

			// sceneWord Clear Button
			Button bClear = new Button();
			bClear.setOnAction(e -> primaryStage.setScene(sceneWord));
			bClear.setText("Clear All");
			bClear.setLayoutX(20);
			bClear.setLayoutY(580);

			// Clear All button action
			bClear.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent arg0) {
					bCalc.setText("Calculate");
					bClear.setText("Cleared!");
					textO.getItems().clear();
					textO1.getItems().clear();
					counter.getItems().clear();

				}
			});

			// Calculated button action
			bCalc.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent arg0) {
					try {
						deleteTable();
						createTable();
						wordFind();
					} catch (Exception e) {
						System.out.print(e);
					}
					calcButCount++;
					textO.getItems().clear();
					textO1.getItems().clear();
					counter.getItems().clear();
					textO.getItems().addAll(wordWord);
					textO1.getItems().addAll(wordPlace);
					bCalc.setText("Calculated!");
					bClear.setText("Clear All");

					for (int i = 1; i < 21; i++) {
						counter.getItems().addAll(i);
					}
				}
			});

			// sceneWord back to SceneOpen(MAIN MENU BUTTON)
			Button bBack = new Button();
			bBack.setText("Main Menu");
			bBack.setLayoutX(100);
			bBack.setLayoutY(580);

			// Main Menu button action
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

			// sceneOpen quit button
			Button bQuit = new Button();
			bQuit.setText("Quit");
			bQuit.setLayoutX(100);
			bQuit.setLayoutY(100);

			// Quit button action
			bQuit.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent arg0) {
					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setTitle("WARNING");
					alert.setHeaderText("Are you sure you want to Quit?");
					if (calcButCount == 0) {
						alert.setContentText("\t\t\tYou did not find the top 20 words.");
					} else if (calcButCount == 1) { // GRAMMAR
						alert.setContentText("\t\t\tYou found the top 20 words " + calcButCount + " time.");
					} else if (calcButCount >= 10) {
						alert.setContentText("\t\tI cannot belive you used the program " + calcButCount
								+ " times!\n\n\t\t\t*~*~ Congratulations ~*~*\n ");
					} else {
						alert.setContentText("\t\t\tYou found the top 20 words " + calcButCount + " times.");
					}
					Optional<ButtonType> result = alert.showAndWait();
					if (result.get() == ButtonType.OK) {
						try {
							deleteTable();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						System.exit(0);
					} else {
						// ... user chose CANCEL or closed the dialog
					}
				}
			});

			// sceneOpen word occurrences button
			Button bWordOccur = new Button();
			bWordOccur.setText("Word Occurrences");
			bWordOccur.setLayoutX(65);
			bWordOccur.setLayoutY(50);

			// Word Occurrences Button action
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

		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
		    // Create a socket to connect to the server
		    Socket socket = new Socket("localhost", 8020);
		    // Socket socket = new Socket("130.254.204.36", 8000);
		    // Socket socket = new Socket("drake.Armstrong.edu", 8000);
	
		    // Create an input stream to receive data from the server
		    fromServer = new DataInputStream(socket.getInputStream());
	
		    // Create an output stream to send data to the server
		    toServer = new DataOutputStream(socket.getOutputStream());
		    }
		    catch (IOException e) {
		    	e.printStackTrace();
		    }	
		}

	/**
	 * The Main function calls start function to launch the GUI and start the
	 * wordFind function.
	 * 
	 * @param args Is used to launch the GUI.
	 * @throws IOException Indicates there is an IOException declared.
	 */
	public static void main(String[] args) throws IOException {
		launch(args);
	}

	/**
	 * The wordFind function searches for all real words on the website within the
	 * poem while counting how often each word is used. A HashMap is created to keep
	 * track of all unique words. A SortedSet is created to display how frequent
	 * unique words are used. The frequency and unique Words are then converted and
	 * stored into Private Strings to be output to the GUI.
	 * 
	 * @return Each individual word that is used in the poem.
	 * @throws Exception
	 */
	public Collection<Word> wordFind() throws Exception {
		Map<String, Word> map = new HashMap<String, Word>();
		Document doc = Jsoup.connect("https://www.gutenberg.org/files/1065/1065-h/1065-h.htm").get();

		String text = doc.body().text();

		BufferedReader reader = new BufferedReader(
				new InputStreamReader(new ByteArrayInputStream(text.getBytes(StandardCharsets.UTF_8))));
		String line;

		while ((line = reader.readLine()) != null) {
			String[] allWords = line.split(" ");
			for (String word : allWords) {
				Word wordFreq = map.get(word);
				if (wordFreq == null) {
					wordFreq = new Word();
					wordFreq.word = word;
					wordFreq.count = 0;
					map.put(word, wordFreq);// hash map storing (each unique word, Occurrences)
				}
				wordFreq.count++;
			}
		}

		reader.close();

		SortedSet<Word> sortedWords = new TreeSet<Word>(map.values());

		int count = 1;
		int maxWords = 21;
		// deleteTable();
		for (Word currentWord : sortedWords) {

			if (count >= maxWords) {
				break;
			}			
			
			//Send server info
			long WordCountServer = currentWord.count;
			toServer.writeUTF(currentWord.word);
			toServer.writeLong(WordCountServer);
			toServer.writeInt(count);
	        toServer.flush();
			
	        //Read Server into client
	        String serverWord = fromServer.readUTF();
	       	Long serverWordCount= fromServer.readLong();
	        int convertServerWordCount = serverWordCount.intValue();
	        
	        //add data to sql
	        addData(serverWord,convertServerWordCount);
	        
	        //display sql data to user via GUI or console
			displayData();
			count++;
		}
		return sortedWords;
	}

	/**
	 * The Word class counts how frequent each unique word is used within the poem
	 * on the website.
	 * 
	 * @author Alex Visco
	 * @version 1.3 Nov 3, 2022
	 */
	public static class Word implements Comparable<Word> {
		String word;
		int count;

		@Override
		/**
		 * The compareTo function counts how frequent a unique word is used within the
		 * poem on the website.
		 * 
		 * @param a The stored unique Word frequency is compared to the current count.
		 * @return Update how frequent each word is used.
		 */
		public int compareTo(Word a) {
			return a.count - count;
		}
	}

	public static Connection sqlConnect() throws Exception {// connect to database

		String driver = "com.mysql.cj.jdbc.Driver";
		String database = "wordOccurrences";// can make variable for user input
		String url = "jdbc:mysql://ALEX-PC:3306/";
		String username = "Alex";
		String password = "admin1";

		Class.forName(driver);

		try {// connect
			Connection con = DriverManager.getConnection(url + database, username, password);
			if (firstRun == true) {
				System.out.println("User: " + username + " has Connected to " + database);
			}

			// System.out.println("Connected"); //test
			return con;

		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}

	public static void createTable() throws Exception { // create table
		try {
			// create table and check for existing table
			Connection con = sqlConnect();
			PreparedStatement create = con.prepareStatement("CREATE TABLE IF NOT EXISTS word(id int(20) NOT NULL AUTO_INCREMENT, Word varchar(255), Occurance int, PRIMARY KEY(id))");
			create.executeUpdate();
			// System.out.println("table created"); //test

			con.close();// close connection
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static void deleteTable() throws Exception { // create table
		try {
			Connection con = sqlConnect();
			PreparedStatement delete = con.prepareStatement("DROP TABLE IF EXISTS word;");// This keeps the user from
																							// repeating values
			delete.executeUpdate();
			firstRun = false;
			// System.out.println("table deleted"); //test

			con.close();// close connection
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static void addData(String word, int Occurance) throws Exception { // add data
		try {
			// add info
			Connection con = sqlConnect();
			PreparedStatement insertInfo = con
					.prepareStatement("INSERT INTO word(Word, Occurance) VALUES ('" + word + "','" + Occurance + "')");
			insertInfo.executeUpdate();
			dataEntered++;
			// System.out.println("add data to table");

			con.close();// close connection
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	public static ObservableList<String> displayData() throws Exception { // Send data to mysql and console
		try {
			// display to console
			Connection con = sqlConnect();
			PreparedStatement statement = con.prepareStatement("SELECT Word, Occurance FROM word "); // Limit 1 will
																										// only display
																										// the first
																										// column
			ResultSet result = statement.executeQuery();
			if (dataEntered == 20) { // stop redundant data
				while (result.next()) {
					// System.out.println(result.getString("Word") + " "
					// +result.getString("Occurance")); //send data to console

					wordWord.add(result.getString("Word")); // send data to mysql
					wordPlace.add(result.getString("Occurance")); // send data to mysql
				}
			}
			// System.out.println("display data"); //test

			con.close();// close connection
			return wordPlace;
		} catch (Exception e) {
			System.out.println(e);
		}
		return null;
	}
}