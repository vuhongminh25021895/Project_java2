package StageManager;

import javafx.stage.Stage;

public class SceneManager {
    private static Stage primarystage;
    public static void setStage(Stage stage){
        primarystage = stage;
    }
    public static Stage getStage() {
        return primarystage;
    }
}
