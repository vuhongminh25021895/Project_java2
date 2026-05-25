import StageManager.SceneManager;
import StageManager.SceneName;
import StageManager.SwitchSceneControll;
import javafx.application.Application;

import javafx.scene.Scene;
import javafx.stage.Stage;


public class AuctionApp extends Application {
    @Override
    public void start(Stage mainstage) throws Exception {
        SceneManager.setStage(mainstage);
        Scene scene = SwitchSceneControll.loadScene(SceneName.LOGIN);
        mainstage.setScene(scene);
        mainstage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
