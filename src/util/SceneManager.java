package util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;


public class SceneManager {
    private static Stage stage;

    public static void setStage(Stage stage) {
        SceneManager.stage = stage;
    }

    public static void switchToScene(String fxmlPath) {
        try {
            Parent root = FXMLLoader.load(SceneManager.class.getResource(fxmlPath));
            Scene scene = new Scene(root);
            stage.setResizable(true);
            stage.setScene(scene);
            stage.sizeToScene();
            // Maximize for larger views like the dashboard
            if (root.prefWidth(-1) > 800) {
                stage.setMaximized(true);
            }
            stage.centerOnScreen();
        } catch (Exception e) {
            System.err.println("Failed to load scene: " + fxmlPath);
            e.printStackTrace();
        }
    }

    public static Parent loadFXML(String fxmlPath) {
        try {
            return FXMLLoader.load(SceneManager.class.getResource(fxmlPath));
        } catch (Exception e) {
            System.err.println("Failed to load component: " + fxmlPath);
            e.printStackTrace();
            return null;
        }
    }
}
