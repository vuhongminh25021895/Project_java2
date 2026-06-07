package Scene;

import Util.AlertBox;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SceneManager {

    private static Stage primaryStage;
    private static final String FXML_BASE = "/fxml/";

    // Dùng để truyền dữ liệu giữa các scene (thay cho static fields rải rác)
    private static final Map<String, Object> sceneData = new HashMap<>();

    public static void setPrimaryStage(Stage stage) { primaryStage = stage; }
    public static Stage getPrimaryStage()            { return primaryStage; }



    public static Scene loadScene(SceneName sceneName) {
        try {
            String fxmlPath = FXML_BASE + sceneName.getFxmlName() + ".fxml";
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            return new Scene(loader.load());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /** Chuyển scene đơn giản */
    // ✅ SceneManager.java — switchTo() dùng getFxmlName()
    public static void switchTo(SceneName name) {
        try {
            String fxmlPath = FXML_BASE + name.getFxmlName() + ".fxml";
            FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
            primaryStage.setScene(new Scene(loader.load()));
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
            AlertBox.display("Cannot load scene: " + name);
        }
    }

    /** Chuyển scene kèm dữ liệu — controller đích gọi SceneManager.getData("key") */
    public static void switchTo(SceneName name, Map<String, Object> data){
        try {
            sceneData.clear();
            sceneData.putAll(data);
            switchTo(name);
        } catch (Exception e) {
            e.printStackTrace();
            AlertBox.display("Cannot load scene" + name);
        }
    }

    /** Lấy dữ liệu được truyền từ scene trước */
    @SuppressWarnings("unchecked")
    public static <T> T getData(String key) {
        return (T) sceneData.get(key);
    }
}
