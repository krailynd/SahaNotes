package com.jrgs.bloc.blocdenotas;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class App extends Application {

    private static final List<Stage> openWorkspaces = new ArrayList<>();

    @Override
    public void start(Stage stage) {
        Platform.setImplicitExit(false);
        showLogin();
    }

    public void showLogin() {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(App.class.getResource("login.fxml"));
            stage.initStyle(StageStyle.TRANSPARENT);
            Scene scene = new Scene(loader.load());
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void showOnboarding() {
        try {
            Stage stage = new Stage();
            FXMLLoader loader = new FXMLLoader(App.class.getResource("onboarding.fxml"));
            stage.initStyle(StageStyle.TRANSPARENT);
            Scene scene = new Scene(loader.load());
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public void openNewWorkspacePrompt() {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Seleccionar Carpeta para Workspace");
        File selectedDirectory = directoryChooser.showDialog(null);

        if (selectedDirectory != null) {
            // --- CREACIÓN AUTOMÁTICA DE CARPETAS ---
            createDefaultFolders(selectedDirectory);
            launchWorkspaceWindow(selectedDirectory);
        }
    }

    public void openExistingWorkspace() {
        openNewWorkspacePrompt();
    }

    private void createDefaultFolders(File root) {
        String[] folders = {"excalidraw", "notes", "tareas", "to-do list", "img", "resources", "codigo"};
        for (String folderName : folders) {
            new File(root, folderName).mkdirs();
        }
    }

    public void launchWorkspaceWindow(File workspaceDir) {
        try {
            FXMLLoader loader = new FXMLLoader(App.class.getResource("view.fxml"));
            Parent root = loader.load();

            MainController controller = loader.getController();
            controller.initializeWorkspace(workspaceDir);

            Stage stage = new Stage();
            // Estilo transparente para bordes personalizados
            stage.initStyle(StageStyle.TRANSPARENT);

            Scene scene = new Scene(root, 1100, 750);
            scene.setFill(Color.TRANSPARENT);
            stage.setScene(scene);

            // Resize helper o lógica simple de drag ya incluida en controller
            stage.setOnCloseRequest(e -> {
                openWorkspaces.remove(stage);
                if (openWorkspaces.isEmpty()) Platform.exit();
            });

            openWorkspaces.add(stage);
            stage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    public static void main(String[] args) {
        launch();
    }
}