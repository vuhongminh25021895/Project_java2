import Scene.SceneManager;
import Scene.SceneName;
import javafx.application.Application;

import javafx.scene.Scene;
import javafx.stage.Stage;


public class AuctionApp extends Application {
    @Override
    public void start(Stage mainstage) throws Exception {
        SceneManager.setPrimaryStage(mainstage);
        Scene scene = SceneManager.loadScene(SceneName.LOGIN);
        mainstage.setScene(scene);
        mainstage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
