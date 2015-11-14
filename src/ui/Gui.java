package ui;

import input.Recorder;

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

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.GridPane;

import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import logic.Analyzer;

public class Gui extends Application {
	private Text scenetitle;
	private Button recordBtn;
	private Button fileChooserBtn;
	private Scene scene;
	private Text output;
	private GridPane grid;
	
	private final FileChooser fileChooser = new FileChooser();
	private  Timeline timeline;
	private final static int maximumColSpan = 8;
	
	private static boolean isRecording = false;
	
	
	private final static String statusRecording = "Recording";
	private final static String statusResult = "Show Result";
	private final static String statusIdle = "Idle";
	private final static String statusCalculaton = "In Calculation";
	private  Analyzer analyzer; 
	
	private Recorder recorder;
	
	
   public static void main(String[] args) {
       launch(args);
   }
   
   @Override
   public void start(Stage primaryStage) {
       primaryStage.setTitle("Musical Recogoniser");
       
       initModel();
       buildScene();
       primaryStage.setScene(scene);
       
       buildScreenTitle();
       buildRecordBtn();
       buildFileChooserBtn(primaryStage);
       buildOutputTitle();
       primaryStage.show();
       
       
   }
   

private void initModel() {
	recorder = new Recorder();
	analyzer = new Analyzer();
}
private void buildOutputTitle() {
	output = new Text("Output:\nThis will be the standard output");
       output.setFill(Color.WHITE);
       output.setFont(Font.font("Tahoma", FontWeight.NORMAL, 15));
       grid.add(output, 0, 8, maximumColSpan, 1);
}

private void buildFileChooserBtn(final Stage primaryStage) {
	fileChooserBtn = new Button("");
       fileChooserBtn.setBackground(null);
       Image fileChooserImg = new Image("document.png");
       fileChooserBtn.setGraphic(new ImageView(fileChooserImg));
       grid.add(fileChooserBtn, maximumColSpan/2, 4, 2, 2);
       
       fileChooserBtn.setOnAction(
               new EventHandler<ActionEvent>() {
                   public void handle(final ActionEvent e) {
                	   
                       configureFileChooser(fileChooser);
                       if(!isRecording) {
                    	   File file = fileChooser.showOpenDialog(primaryStage);
                    	   if (file != null) {
                               // process file
                        	   output.setText(analyzer.analyze(file));
                           }
                       }
                       
                       
                   }
               });
}

private void buildRecordBtn() {
	recordBtn = new Button("");
       Image recordImg = new Image("recording.png");
       recordBtn.setGraphic(new ImageView(recordImg));
       
      // recordBtn.setStyle("-fx-background-color: rgba(0, 0, 0, 0);");
       grid.add(recordBtn, 0, 4, 2, 2);
       
       final Color startColor =  new Color(0.23f,0.321f,0.87f,0f );
       
       final Color middleColor = Color.RED;
       final Color endColor = new Color(0.23f,0.321f,0.87f,0f );
       
       final ObjectProperty<Color> color = new SimpleObjectProperty<Color>(startColor);

		// String that represents the color above as a JavaFX CSS function:
		// -fx-body-color: rgb(r, g, b);
		// with r, g, b integers between 0 and 255
		StringBinding cssColorSpec = Bindings.createStringBinding(new Callable<String>() {
           public String call() throws Exception {
        	  
        		   return String.format("-fx-background-color: rgba(%d, %d, %d, %d);", 
                           (int) (256*color.get().getRed()), 
                           (int) (256*color.get().getGreen()), 
                           (int) (256*color.get().getBlue()),
                           (int) (256*color.get().getOpacity())
                           ); 
        	   
               
           }
       }, color);
		
		// bind the button's style property
		recordBtn.styleProperty().bind(cssColorSpec);
       
		
		timeline = new Timeline(
				new KeyFrame(Duration.seconds(0), new KeyValue(color, startColor)),
				new KeyFrame(Duration.seconds(1), new KeyValue(color, middleColor)),
				new KeyFrame(Duration.seconds(2), new KeyValue(color, endColor)));
		
		timeline.setCycleCount(Timeline.INDEFINITE);
		timeline.setRate(1);
		recordBtn.setOnAction(new EventHandler<ActionEvent>() {
           public void handle(ActionEvent event) {
               if(isRecording) {
            	   recorder.stop();
            	   isRecording = false;
            	   timeline.playFrom(Duration.seconds(2));
            	   timeline.stop();
            	   scenetitle.setText(statusCalculaton);
            	   output.setText(analyzer.analyze(new File(Utils.recordingFilePath)));
            	  
               } else {
            	   recorder.start();
            	   scenetitle.setText(statusRecording);
            	   isRecording = true;
            	   timeline.playFromStart();
            	      
               }
        	   
               
           }
       });
}

private void buildScreenTitle() {
	scenetitle = new Text(statusIdle);
       
       scenetitle.setFont(Font.font("Helvetica", FontWeight.NORMAL, 40));
       scenetitle.setFill(Color.GOLD);
       grid.add(scenetitle, 0, 0, maximumColSpan, 1);
       scenetitle.setTextAlignment(TextAlignment.CENTER);
}

   private void buildScene() {
	   grid = new GridPane();
       grid.setBackground(new Background(new BackgroundImage(new Image("background.jpg",400,400,false,true),
    		   BackgroundRepeat.NO_REPEAT,BackgroundRepeat.NO_REPEAT,BackgroundPosition.CENTER, null )));
       grid.setAlignment(Pos.CENTER);
       grid.setHgap(10);
       grid.setVgap(10);
       grid.setPadding(new Insets(25, 25, 25, 25));

       scene = new Scene(grid, 400, 400);
	
}
   
   private static void configureFileChooser(final FileChooser fileChooser){                           
       fileChooser.setTitle("choose an audio clip");
       fileChooser.setInitialDirectory(
           new File(Utils.testFolder)
       ); 
   }
           
}
