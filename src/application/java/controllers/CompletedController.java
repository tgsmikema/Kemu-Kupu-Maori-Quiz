package application.java.controllers;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

import application.java.models.Word;
import javafx.animation.FillTransition;
import javafx.animation.PauseTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.SequentialTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javafx.stage.Stage;
import javafx.util.Duration;

public class CompletedController {
	
	// these two variables are used to change scene
	private Stage stage;
	private Scene scene;
	
	@FXML private TableView<Word> summaryTable;
	@FXML private TableColumn<Word, String> wordColumn;
	@FXML private TableColumn<Word, Double> scoreColumn;
	@FXML private Label totalScoreLabel;
	@FXML private AnchorPane stars;
	
	private double totalScore = 0;
	
	// this constant defines the colour of star that transition into.
	private static String STAR_COLOUR_HEX = "#FFD700";

	/*
	 * This method will be invoked when other scene is switching to complete scene, this is to pass
	 * useful data to this controller, for example, the statistics for user answers
	 * The total score will also be calculated in this method and display on the screen as a label
	 * The parameter wordStats is the statistics of the user answers.
	 */
	public void setData(List<Word> wordStats) {
		
		// Write the data to the table
		ObservableList<Word> list = FXCollections.observableArrayList(wordStats);
		
		this.wordColumn.setCellValueFactory(new PropertyValueFactory<Word, String>("word"));
		this.scoreColumn.setCellValueFactory(new PropertyValueFactory<Word, Double>("score"));
		this.summaryTable.setItems(list);
		
		// iterates the list to calculate the total score and set it to the text of label
		for (Word word: wordStats) {
			this.totalScore += word.getScore();
		}
		
		String totalScoreString = String.format("%.2f", totalScore);
		double finalTotalScore = Double.parseDouble(totalScoreString);
		this.totalScoreLabel.setText(Double.toString(finalTotalScore));

		
	}
	
	/**
	 * this method controls the star animation on the completed screen with 
	 * specific score boundary for each star.
	 */
	public void setAnimation() {
		double scoreBoundaryOne = 1.7;
		double scoreBoundaryTwo = 2.5;
		
		if(totalScore == 0) {
			playStarsAnimation(0);
		}
		else if(totalScore <= scoreBoundaryOne) {
			playStarsAnimation(1);
		}
		else if(totalScore > scoreBoundaryOne && totalScore <= scoreBoundaryTwo ) {
			playStarsAnimation(2);	
		}
		else if(totalScore > scoreBoundaryTwo) {
			playStarsAnimation(3);
		}
	}
	
	public void returnHome(ActionEvent e) {
		this.switchScene(e, "Main");
	}
	
	public void playAgain(ActionEvent e) {
		this.switchScene(e, "Quiz");
	}
	
	
	/*
	 * This method is to switch scenes 
	 * The first parameter e is the ActionEvent that is received when user clicked a button
	 * The second parameter sceneName is the name of the fxml file, this string should not include 
	 * file extension and should be case-sensitive
	 */
	private void switchScene(ActionEvent e, String sceneName) {
		
		try {
			// establish the full relative path using the name of the scene
			// this is relative easy to do because all fxml files are stored in the same package
			String path = String.format("../../resources/views/%s.fxml", sceneName);
			
			Parent root = FXMLLoader.load(getClass().getResource(path));
			stage = (Stage)((Node)e.getSource()).getScene().getWindow();
			scene = new Scene(root);
			stage.setScene(scene);
			stage.show();
			
		} catch (IOException exception) {
			exception.printStackTrace();
		}

	}
	
	/**
	 * this method plays the star animation with specific number of 
	 * stars passed in as parameter
	 * @param numOfStars
	 */
	public void playStarsAnimation(int numOfStars) {
		ObservableList<Node> starsObjects = stars.getChildren();
		
		String starColorHex = STAR_COLOUR_HEX;
        
		SequentialTransition sequentialTransition = new SequentialTransition();
        
        // Error handling - max num of stars is 3
        if(numOfStars > 3) {
        	numOfStars = 3;
        }
		
        // Create a simple scale for all three stars
        if(numOfStars == 0) {
            ScaleTransition animation = createScaleAnimation(stars, 400, 0.1);
			sequentialTransition.getChildren().add(animation);
		}
        
        // Create a scale animation for each star
		for (int i = 0; i < numOfStars; i++) {
        	SVGPath shape = (SVGPath)starsObjects.get(i);
            ScaleTransition animation = createScaleAnimation(shape, 400, 0.7);
            FillTransition colorChange = new FillTransition(Duration.millis(200), shape, Color.web("#dddddd"),  Color.web(starColorHex));
            sequentialTransition.getChildren().addAll(colorChange, animation);
        }
        
        // Pause so that it doesn't play right away on initialise
        int transitionDelay = 1500;
        sequentialTransition.getChildren().add(0, new PauseTransition(Duration.millis(transitionDelay)));
        
        
        sequentialTransition.play();
	}

	/*
	 * Creates a scale transition which zooms in and out with specified values. 
	 */
	private ScaleTransition createScaleAnimation(Node shape, int durationInMillis, double scale) {
		ScaleTransition animation = new ScaleTransition(Duration.millis(durationInMillis), shape);
		animation.setByX(scale);
		animation.setByY(scale);	
		animation.setCycleCount(2);
		animation.setAutoReverse(true);
		return animation;
	}

}