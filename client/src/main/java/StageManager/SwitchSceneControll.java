package StageManager;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;

public class SwitchSceneControll {
    public static Scene loadScene(SceneName sceneName) throws Exception {
        FXMLLoader loader = new FXMLLoader(SwitchSceneControll.class.getResource(getFXMLPath(sceneName)));
        return new Scene(loader.load());
    }
    private static String getFXMLPath(SceneName sceneName) {
        switch (sceneName) {

            case LOGIN:
                return "/fxml/Login.fxml";

            case REGISTER:
                return "/fxml/Register.fxml";

            case BID:
                return "/fxml/Bid.fxml";

            case AUCTION_LIST:
                return "/fxml/AuctionList.fxml";

            default:
                throw new IllegalArgumentException("Unknown scene");
        }
    }
}
