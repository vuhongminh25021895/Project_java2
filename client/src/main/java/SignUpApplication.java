package com.javaproject.signin;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SignUpApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(SignUpApplication.class.getResource("SignUp.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 400);

        stage.setTitle("Sign Up!");
        stage.setScene(scene);
        stage.show();
    }
}
