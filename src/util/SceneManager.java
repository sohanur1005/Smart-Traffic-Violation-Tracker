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
            // Temporarily allow resizing so the stage can adapt to the new scene size
            stage.setResizable(true);
            stage.setScene(scene);
            stage.sizeToScene();
            stage.setResizable(false);
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
