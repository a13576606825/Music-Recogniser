package ui;

import java.io.File;
import java.util.concurrent.Callable;

import utility.Utils;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.*;
import javafx.scene.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

public class Gui extends Application {
	private Text scenetitle;
	private Button recordBtn;
	private Button fileChooserBtn;
	private Scene scene;
	
	private final FileChooser fileChooser = new FileChooser();
	private  Timeline timeline;
	private final static int maximumColSpan = 8;
	
	private static boolean isRecording = false;
	
   public static void main(String[] args) {
       launch(args);
   }
   
   @Override
   public void start(Stage primaryStage) {
       primaryStage.setTitle("Musical Recogoniser");

       GridPane grid = new GridPane();
       grid.setAlignment(Pos.CENTER);
       grid.setHgap(10);
       grid.setVgap(10);
       grid.setPadding(new Insets(25, 25, 25, 25));

       scene = new Scene(grid, 400, 400);
       primaryStage.setScene(scene);
       
       
       scenetitle = new Text("Choose a method: ");
       scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
       grid.add(scenetitle, 0, 0, maximumColSpan, 1);
      
       

       recordBtn = new Button("");
       Image recordImg = new Image("recording.png");
       recordBtn.setGraphic(new ImageView(recordImg));
       
      // recordBtn.setStyle("-fx-background-color: rgba(0, 0, 0, 0);");
       grid.add(recordBtn, 0, 4, 2, 2);
       
       final Color startColor = Color.TOMATO;
       final Color middleColor = Color.VIOLET;
       final Color endColor = Color.web("#80e090", 0);
		
		final ObjectProperty<Color> color = new SimpleObjectProperty<Color>(startColor);

		// String that represents the color above as a JavaFX CSS function:
		// -fx-body-color: rgb(r, g, b);
		// with r, g, b integers between 0 and 255
		final StringBinding cssColorSpec = Bindings.createStringBinding(new Callable<String>() {
           @Override
           public String call() throws Exception {
               return String.format("-fx-background-color: rgb(%d, %d, %d);", 
                       (int) (256*color.get().getRed()), 
                       (int) (256*color.get().getGreen()), 
                       (int) (256*color.get().getBlue()));
           }
       }, color);
		
		// bind the button's style property
		recordBtn.styleProperty().bind(cssColorSpec);
       
		
		timeline = new Timeline(
				new KeyFrame(Duration.ZERO, new KeyValue(color, startColor)),
				new KeyFrame(Duration.seconds(1), new KeyValue(color, middleColor)),
				new KeyFrame(Duration.seconds(2), new KeyValue(color, endColor)));
		timeline.setCycleCount(timeline.INDEFINITE);
		recordBtn.setOnAction(new EventHandler<ActionEvent>() {
           @Override
           public void handle(ActionEvent event) {
               if(isRecording) {
            	   isRecording = false;
            	   timeline.stop();
            	  
               } else {
            	   isRecording = true;
            	   timeline.play();   
               }
        	   
               
           }
       });

       
       
     
       fileChooserBtn = new Button("");
       fileChooserBtn.setStyle("-fx-background-color: yellow");
       Image fileChooserImg = new Image("document.png");
       fileChooserBtn.setGraphic(new ImageView(fileChooserImg));
       grid.add(fileChooserBtn, maximumColSpan/2, 4, 2, 2);
       
       fileChooserBtn.setOnAction(
               new EventHandler<ActionEvent>() {
                   @Override
                   public void handle(final ActionEvent e) {
                       configureFileChooser(fileChooser);
                       File file = fileChooser.showOpenDialog(primaryStage);
                       if (file != null) {
                           // process file
                    	   Utils.debug("should process input file");
                       }
                   }
               });
       
       scenetitle = new Text("Output:\nThis will be the standard output");
       scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 15));
       grid.add(scenetitle, 0, 8, maximumColSpan, 1);
       
       primaryStage.show();
   }
   
   private static void configureFileChooser(final FileChooser fileChooser){                           
       fileChooser.setTitle("choose an audio clip");
       fileChooser.setInitialDirectory(
           new File(Utils.testFolder)
       ); 
   }
           
}
