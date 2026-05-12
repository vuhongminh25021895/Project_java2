package StageManager;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SwitchSceneControll {
    public static Scene loadScene(SceneName sceneName) throws IOException {
        FXMLLoader loader = new FXMLLoader(SwitchSceneControll.class.getResource(getFXMLPath(sceneName)));
        return new Scene(loader.load());
    }

    public static void switchScene(SceneName sceneName) throws IOException {
        Stage stage = SceneManager.getStage();
        if (stage == null) {
            throw new IllegalStateException("Primary stage has not been set.");
        }
        stage.setScene(loadScene(sceneName));
        stage.show();
    }

    private static String getFXMLPath(SceneName sceneName) {
        switch (sceneName) {
            case LOGIN:
                return "/Login.fxml";
            case REGISTER:
                return "/Register.fxml";
            case BID:
                return "/Bid.fxml";
            case AUCTION_LIST:
                return "/AutionList.fxml";
            default:
                throw new IllegalArgumentException("Unknown scene");
        }
    }
}
