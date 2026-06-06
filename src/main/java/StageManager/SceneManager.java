package StageManager;

import javafx.stage.Stage;

public class SceneManager {
    private static Stage primaryStage;
    public static void setStage(Stage stage){
        primaryStage = stage;
    }
    public static Stage getStage() {
        return primaryStage;
    }
}
