package sample;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("Tee-Timer");
        primaryStage.getIcons().add(new Image("ProgrammIcon.png"));
        Scene scene = new Scene(root, 960, 640);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
