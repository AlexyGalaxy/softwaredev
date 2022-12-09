package application;

import java.io.*;
import java.net.*;
import java.util.Date;
import java.util.Optional;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

/**
 * When ran, WordServer will deploy a GUI with some information as well as wait for a connection from WordClient via port.
 * There is a quit button that can be used at any time to exit the Server. Once connected, the client will select the 
 * calculate button which finds the data, converts the data, then send the data to the server, the server will take that 
 * data and convert it so it can be displayed via the Server GUI, and then send the original data back to the Client to 
 * display via the Client GUI  
 * 
 * @author Alex
 *
 */
public class WordServer extends Application {
	private static int port = 8000;
	private static int serverUseCount=0;

	@Override // Override the start method in the Application class
	public void start(Stage primaryStage) {
	
	AnchorPane open = new AnchorPane(); //holds textArea and QuitButton
	TextArea ta = new TextArea(); // Text area for displaying contents
	
	// Create a scene and place it in the stage with scroll  active
	Scene scene = new Scene(new ScrollPane(open), 490, 230);
	
	// sceneOpen quit button
	Button bQuit = new Button();
	bQuit.setText("Quit");
	bQuit.setLayoutX(5);
	bQuit.setLayoutY(190);
	
	// Quit button action
	bQuit.setOnAction(new EventHandler<ActionEvent>() {
		public void handle(ActionEvent arg0) {
			Alert alertServer = new Alert(AlertType.CONFIRMATION);
			
			alertServer.setTitle("WARNING: Quit WordServer");
			alertServer.setHeaderText("Are you sure you want to close WordServer?");
			if (serverUseCount == 0) {
				alertServer.setContentText("\t\tYou did not try to calculate the top 20 words.");
			} else if (serverUseCount == 1) { 
				alertServer.setContentText("\t\tYou tried to calculated the top 20 words " + serverUseCount + " time.");
			} else if (serverUseCount >= 10) {
				alertServer.setContentText("I cannot belive you tried to calculate the top 20 words " + serverUseCount
						+ " times!\n\n\t\t\t    *~*~ Congratulations ~*~*\n ");
			} else {
				alertServer.setContentText("\t\tYou tried to calculate the top 20 words " + serverUseCount + " times.");
			}
			Optional<ButtonType> result = alertServer.showAndWait();
			if (result.get() == ButtonType.OK) {
				System.exit(0);
			} else {
				// ... user chose CANCEL or closed the dialog
			}
		}
	});
	open.getChildren().add(ta);
	open.getChildren().add(bQuit);
	
	primaryStage.setTitle("WordServer"); // Set the stage title
	primaryStage.setScene(scene); // Place the scene in the stage
	primaryStage.setResizable(false);
	primaryStage.show(); // Display the stage
	
	new Thread( () -> {
	  try {
	    // Create a server socket
	    ServerSocket serverSocket = new ServerSocket(port);
	    Platform.runLater(() ->
	      ta.appendText("Server started at " + new Date() + " Port: " + port+"\nWating for Client...\n"));
	    
	    // Listen for a connection request
	        Socket socket = serverSocket.accept();
	  
	        // Create data input and output streams
	        DataInputStream inputFromClient = new DataInputStream(
	          socket.getInputStream());
	        DataOutputStream outputToClient = new DataOutputStream(
	          socket.getOutputStream());
	      

	        while (true) {
	          // Receive Data from the client
	    	String WordServer = inputFromClient.readUTF();
	    	long CountServer = inputFromClient.readLong();
	    	int count = inputFromClient.readInt();
	    	
	    	if (count==1) {
	    		ta.appendText("\nData Successfully Received from Client\n\nTop 20 Most Used Words: \n");
	    		serverUseCount++;
	    	}
	    	// Send Data back to the client
	       	outputToClient.writeUTF(WordServer);
	    	outputToClient.writeLong(CountServer);
	    	 
	      Platform.runLater(() -> {
	    	  ta.appendText(count+".\t"+ WordServer + ",\t"+CountServer+'\n'); 
	    	 
	      });
	     
	    }
	         
	  }
	  catch(IOException ex) {
	      ex.printStackTrace();
	      }
	    }).start();
	  }
	
	  /**
	   * The Main function launches the WordServer function.
	   */
	  public static void main(String[] args) {
	    launch(args);
	  }
}
