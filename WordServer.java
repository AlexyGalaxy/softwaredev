package application;

import java.io.*;
import java.net.*;
import java.util.Date;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

public class WordServer extends Application {
  @Override // Override the start method in the Application class
  public void start(Stage primaryStage) {
    // Text area for displaying contents
    TextArea ta = new TextArea();

    // Create a scene and place it in the stage
    Scene scene = new Scene(new ScrollPane(ta), 450, 200);
    primaryStage.setTitle("WordServer"); // Set the stage title
    primaryStage.setScene(scene); // Place the scene in the stage
    primaryStage.show(); // Display the stage
    
    new Thread( () -> {
      try {
        // Create a server socket
    	int port = 8020;
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
          // Receive radius from the client
        	String WordServer = inputFromClient.readUTF();
        	long CountServer = inputFromClient.readLong();
        	int count = inputFromClient.readInt();
        	
        	// Send area back to the client
           	outputToClient.writeUTF(WordServer);
        	outputToClient.writeLong(CountServer);
          
     
          Platform.runLater(() -> {
        	  
           //ta.appendText("Top 20 Words From Client: " + radius + '\n');
  
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
   * The main method is only needed for the IDE with limited
   * JavaFX support. Not needed for running from the command line.
   */
  public static void main(String[] args) {
    launch(args);
  }
}
