package antagonisticapple;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("loader.fxml"));
        primaryStage.getIcons().add(new Image("assets/ico.png"));
        primaryStage.setTitle("Cupcaked Manga Reader");
        primaryStage.setScene(new Scene(root, 292, 312, Color.TRANSPARENT));  //1026
        primaryStage.setX(819);
        primaryStage.setY(323);
        primaryStage.setResizable(false);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
//        primaryStage.setFullScreen(true);
//        primaryStage.setFullScreenExitKeyCombination(new KeyCodeCombination(KeyCode.ESCAPE));
        primaryStage.show();
        root.requestFocus();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
