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
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * When ran, WordClient will attempt to connect to the WordServer via a port and will deploy a GUI that will show the top 20 most used words 
 * on s specific website while ignoring all HTML code. The GUI has interactive buttons that tease another program, that will exit the program,
 * and will bring the user to another window where they can find the top 20 most frequently used words within a poem on a specific website
 * while ignoring all HTML code.
 * 
 * @author Alex Visco
 */

public class WordClient extends Application {
  // IO streams
	DataOutputStream toServer = null;
  	DataInputStream fromServer = null;
	private static int port = 8000;
	private static Socket socket = null;
	
  	private static ObservableList<String> wordPlace = FXCollections.observableArrayList();
	private static ObservableList<String> wordWord = FXCollections.observableArrayList();
	private static int calcButCount = 0;
	private static int dataEntered = 0;
	private static boolean firstRun = true;
	private static boolean displayOnce =true;
	private static boolean errorCheck = true;
  
  @Override // Override the start method in the Application class
  public void start(Stage primaryStage) {
	  try {
			AnchorPane open = new AnchorPane();
			AnchorPane p = new AnchorPane();
			Scene sceneOpen = new Scene(open, 400, 150);
			Scene sceneWord = new Scene(p, 300, 610);

			Text textOpen = new Text();
			textOpen.setText("Weclome to Alex Visco's Final Project");
			textOpen.setLayoutX(100);
			textOpen.setLayoutY(20);
			
			Text textI = new Text();
			ListView<String> textO = new ListView<String>();
			ListView<String> textO1 = new ListView<String>();
			ListView<Integer> counter = new ListView<Integer>();

			textI.setText("Press Calculate To Display \nTop 20 Most Used Words");
			textI.setLayoutX(80);
			textI.setLayoutY(20);

			// sceneWord WordClient Button
			Button bCalc = new Button();
			bCalc.setOnAction(e -> primaryStage.setScene(sceneWord));
			bCalc.setText("Calculate");
			bCalc.setLayoutX(115);
			bCalc.setLayoutY(50);
			
			// # count 1-20
			counter.setLayoutX(65);
			counter.setLayoutY(90);
			counter.setMaxWidth(35);
			counter.setMinHeight(480);
			
			// words
			textO.setLayoutX(100); 
			textO.setLayoutY(90);
			textO.setMaxWidth(75);
			textO.setMinHeight(480);
			
			// wordCount Occurrence
			textO1.setLayoutX(175);
			textO1.setLayoutY(90);
			textO1.setMaxWidth(60);
			textO1.setMinHeight(480);

			// sceneWord WordClient quit button
			Button bQuit2 = new Button();
			bQuit2.setText("Quit");
			bQuit2.setLayoutX(220);
			bQuit2.setLayoutY(580);

			// WordClient Quit button action
			bQuit2.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent arg0) {
					Alert alert = new Alert(AlertType.CONFIRMATION);
					alert.setTitle("WARNING: Quit WordClient");
					alert.setHeaderText("Are you sure you want to close WordClient?");
					if (calcButCount == 0) {
						alert.setContentText("\t\tYou did not try to calculate the top 20 words.");
					} else if (calcButCount == 1) { 
						alert.setContentText("\t\tYou tried to calculated the top 20 words " + calcButCount + " time.");
					} else if (calcButCount >= 10) {
						alert.setContentText("I cannot belive you tried to calculate the top 20 words " + calcButCount
								+ " times!\n\n\t\t\t    *~*~ Congratulations ~*~*\n ");
					} else {
						alert.setContentText("\t\tYou tried to calculate the top 20 words " + calcButCount + " times.");
					}
					Optional<ButtonType> result = alert.showAndWait();
					if (result.get() == ButtonType.OK) {
						try {
							deleteTable();
						} catch (Exception e) {
							e.printStackTrace();
						}
						System.exit(0);
					} else {
						// ... user chose CANCEL or closed the dialog
					}
				}
			});
			
			// sceneWord WordClient Clear All Button
			Button bClear = new Button();
			bClear.setOnAction(e -> primaryStage.setScene(sceneWord));
			bClear.setText("Clear All");
			bClear.setLayoutX(40);
			bClear.setLayoutY(580);

			// WordClient Clear All button action
			bClear.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent arg0) {
					try {
						deleteTable();
					} catch (Exception e) {
						e.printStackTrace();
					}
					errorCheck=true;
					bCalc.setText("Calculate");
					bClear.setText("Cleared!");
					textO.getItems().clear();
					textO1.getItems().clear();
					counter.getItems().clear();
					if (socket ==null) {
					bClear.setText("~Error~");
					}
				}
			});

			// WordClient Calculated button action
			bCalc.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent arg0) {
					try {
						deleteTable();
						createTable();
						wordFind();
						displayData();
					} catch (Exception e) {
						System.out.print(e);
					}
					errorCheck=true;
					calcButCount++;
					textO.getItems().clear();
					textO1.getItems().clear();
					counter.getItems().clear();
					textO.getItems().addAll(wordWord);
					textO1.getItems().addAll(wordPlace);
					bCalc.setText("Calculated!");
					bClear.setText("Clear All");
					if(socket != null){//do not display info if not connected to server
						for (int i = 1; i < 21; i++) {
						counter.getItems().addAll(i);
						}
					}if (socket ==null) {
						bCalc.setText("~Error~");
					}
				}
			});

			// sceneWord WordClient back to SceneOpen(MAIN MENU BUTTON)
			Button bBack = new Button();
			bBack.setText("Main Menu");
			bBack.setLayoutX(120);
			bBack.setLayoutY(580);

			// Main Menu button action
			bBack.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent arg0) {
					try {
						deleteTable();
					} catch (Exception e) {
						e.printStackTrace();
					}
					primaryStage.setScene(sceneOpen);
					primaryStage.setTitle("WordClient: Main Menu");
					bCalc.setText("Calculate");
					bClear.setText("Clear All");
					textO.getItems().clear();
					textO1.getItems().clear();
					counter.getItems().clear();
				}
			});
			
			// sceneOpen (Main Menu) other button
			Button bOther = new Button();
			bOther.setText("Another Program");
			bOther.setLayoutX(210);
			bOther.setLayoutY(50);
			
			// (Main Menu) other action
			bOther.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent arg0) {
					bOther.setText("Coming Soon!");
				}
			});
			
			// sceneOpen (Main Menu) quit button
			Button bQuit = new Button();
			bQuit.setText("Quit");
			bQuit.setLayoutX(175);
			bQuit.setLayoutY(100);

			// (Main Menu) Quit button action
			bQuit.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent arg0) {
					Alert alert = new Alert(AlertType.CONFIRMATION);
					bOther.setText("Another Program");
					alert.setTitle("WARNING: Quit WordClient");
					alert.setHeaderText("Are you sure you want to close WordClient?");
					if (calcButCount == 0) {
						alert.setContentText("\t\tYou did not try to calculate the top 20 words.");
					} else if (calcButCount == 1) { 
						alert.setContentText("\t\tYou tried to calculated the top 20 words " + calcButCount + " time.");
					} else if (calcButCount >= 10) {
						alert.setContentText("I cannot belive you tried to calculate the top 20 words " + calcButCount
								+ " times!\n\n\t\t\t    *~*~ Congratulations ~*~*\n ");
					} else {
						alert.setContentText("\t\tYou tried to calculate the top 20 words " + calcButCount + " times.");
					}
					Optional<ButtonType> result = alert.showAndWait();
					if (result.get() == ButtonType.OK) {
						try {
							deleteTable();
						} catch (Exception e) {
							e.printStackTrace();
						}
						System.exit(0);
					} else {
						// ... user chose CANCEL or closed the dialog
					}
				}
			});
			
			// sceneOpen (Main Menu) WordClient button
			Button bWordOccur = new Button();
			bWordOccur.setText("Word Occurrences");
			bWordOccur.setLayoutX(65);
			bWordOccur.setLayoutY(50);

			// (Main Menu) WordClient Button action
			bWordOccur.setOnAction(new EventHandler<ActionEvent>() {
				public void handle(ActionEvent arg0) {
					primaryStage.setScene(sceneWord);
					primaryStage.setTitle("WordClient");
					bOther.setText("Another Program");
					bQuit.setText("Quit");
				}
			});

			open.getChildren().add(textOpen);
			open.getChildren().add(bWordOccur);
			open.getChildren().add(bOther);
			open.getChildren().add(bQuit);

			p.getChildren().add(bCalc);
			p.getChildren().add(textI);
			p.getChildren().add(counter);
			p.getChildren().add(textO);
			p.getChildren().add(textO1);
			p.getChildren().add(bClear);
			p.getChildren().add(bBack);
			p.getChildren().add(bQuit2);

			primaryStage.setScene(sceneOpen);
			primaryStage.setTitle("WordClient: Main Menu");
			primaryStage.setResizable(false);
			primaryStage.show();

		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
		    // Create a socket to connect to the server
		    socket = new Socket("localhost", port);
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
	 * unique words are used. The frequency is then converted into a long data type, 
	 * then the frequency and word are sent the Server.
	 * 
	 * @return Each individual word that is used in the poem.
	 * @throws Exception
	 */
	public Collection<Word> wordFind() throws Exception {
		wordWord.clear(); //stops repeating data to GUI
		wordPlace.clear();//stops repeating data to GUI
		
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
			
			count++;
		}
		return sortedWords;
	}

	/**
	 * The Word class counts how frequent each unique word is used within the poem
	 * on the website.
	 * 
	 * @author Alex Visco
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
	
	/**
	 * The sqlConnect function connects WordClient to MySQL Database via a localhost connection.
	 * 
	 * @return Null
	 * @throws Exception
	 */
	public static Connection sqlConnect() throws Exception {// connect to database

		String driver = "com.mysql.cj.jdbc.Driver";
		String database = "wordOccurrences";
		String url = "jdbc:mysql://ALEX-PC:3306/";
		String username = "Alex";
		String password = "admin1";

		Class.forName(driver);
		
		try {// connect
			Connection con = DriverManager.getConnection(url + database, username, password);
			
			if (socket == null&&errorCheck == true) {
				System.out.println("\n\nError: Connection Failed\nTo Fix Error: Close WordClient, Start WordServer, Start WordClient");
				errorCheck=false;
			}else if (firstRun == true) {
				System.out.println("\nUser: " + username + " has Connected to MySQL Database: " + database);
			}

			// System.out.println("Connected"); //test
			return con;

		} catch (Exception e) {
			System.out.println("\n"+e+"\n");
		}
		return null;
	}
	
	/**
	 * The createTable function creates a table to hold 2 columns and 20 rows within the MySql Database.
	 * 
	 * @throws Exception
	 */
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
	/**
	 * The deleteTable function deletes/drops a table and all of its stored information stored on the MySql Database.
	 * 
	 * @throws Exception
	 */
	public static void deleteTable() throws Exception { // create table
		try {
			Connection con = sqlConnect();
			PreparedStatement delete = con.prepareStatement("DROP TABLE IF EXISTS word;");// This keeps the user from
																							// repeating values
			delete.executeUpdate();
			
			//System.out.println("\nSuccessfully Deleted Table In Database"); //test
			dataEntered=0;
			firstRun = false;
			con.close();// close connection
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	/**
	 * The addData function adds/inserts info into an already created table in the MySql Database.
	 * 
	 * @param word The most frequently used words that are used within the poem on the website.
	 * @param Occurance The number of times each unique word was used in the poem on the website.
	 * @throws Exception
	 */
	public static void addData(String word, int Occurance) throws Exception { // add data
		
		try {
			// add info
			Connection con = sqlConnect();
			PreparedStatement insertInfo = con.prepareStatement("INSERT INTO word(Word, Occurance) VALUES ('" + word + "','" + Occurance + "')");
			insertInfo.executeUpdate();
			dataEntered++;
			if(displayOnce==true) {
			System.out.println("\nData Successfully Entered into SQL Database:"); //send data to console
			displayOnce=false;
			}
			con.close();// close connection
		} catch (Exception e) {
			System.out.println(e);
		}
	}
	/**
	 * The displayData function displays/selects the information from the MySql Database, 
	 * sends it to the server then back to the user via the GUI.
	 * 
	 * @return Null
	 * @throws Exception
	 */
	public static ObservableList<String> displayData() throws Exception { // Send data to mysql and console
		int count=1;
		try {
			// display to console
			Connection con = sqlConnect();
			PreparedStatement statement = con.prepareStatement("SELECT Word, Occurance FROM word "); // Limit 1 will only display the first column
			ResultSet result = statement.executeQuery();
			if (dataEntered == 20) { // stop redundant data
				//send data to console
				
				System.out.println("\nData Received From Database:"); //send data to console
				System.out.println("\nTop 20 Most Used Words:");
				while (result.next()) {
					
					System.out.println(count+". "+result.getString("Word") + ", " +result.getString("Occurance"));//display data to console
					count++;
					wordWord.add(result.getString("Word")); // send word data to mysql
					wordPlace.add(result.getString("Occurance")); // send Occur data to mysql
					
				}
			}
			con.close();// close connection
			return wordPlace;
		}catch (Exception e) {
			System.out.println("\n~~Error: Cannot Send/Receive Data To Database.~~");
			System.out.println(e);
		}
		return null;
	}
}