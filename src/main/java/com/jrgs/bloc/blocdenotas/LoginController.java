package com.jrgs.bloc.blocdenotas;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class LoginController {

    @FXML private VBox loginPanel;
    @FXML private VBox registerPanel;

    // Campos Login
    @FXML private TextField emailField;
    @FXML private PasswordField passField;

    // Campos Registro
    @FXML private TextField regName;
    @FXML private TextField regEmail;
    @FXML private PasswordField regPass;

    @FXML private Label errorLabel;

    private static final String USER_FILE = "saha_users.json";
    private List<User> users = new ArrayList<>();

    // --- INICIALIZACIÓN ---
    @FXML
    public void initialize() {
        loadUsers();
        // Animación de entrada elegante al abrir
        animateIn(loginPanel);
    }

    private void animateIn(Node node) {
        node.setOpacity(0);
        node.setTranslateY(30);

        FadeTransition ft = new FadeTransition(Duration.millis(1000), node);
        ft.setToValue(1);

        TranslateTransition tt = new TranslateTransition(Duration.millis(1000), node);
        tt.setToY(0);

        ft.play();
        tt.play();
    }

    // --- NAVEGACIÓN ENTRE PANELES ---
    @FXML
    private void showRegister() {
        fadeSwitch(loginPanel, registerPanel);
        errorLabel.setText("");
    }

    @FXML
    private void showLogin() {
        fadeSwitch(registerPanel, loginPanel);
        errorLabel.setText("");
    }

    private void fadeSwitch(Node outgoing, Node incoming) {
        outgoing.setVisible(false);
        outgoing.setManaged(false);

        incoming.setVisible(true);
        incoming.setManaged(true);
        animateIn(incoming);
    }

    // --- ACCIONES (MÉTODOS QUE FALTABAN O DABAN ERROR) ---

    @FXML
    private void handleLogin() { // <--- ESTE ES EL MÉTODO QUE DABA ERROR
        if (users.isEmpty()) {
            errorLabel.setText("No hay usuarios registrados.");
            return;
        }

        boolean valid = users.stream().anyMatch(u ->
                u.email.equals(emailField.getText()) && u.password.equals(passField.getText()));

        if (valid) {
            closeWindow();
            new App().showOnboarding(); // Pasar al Onboarding
        } else {
            shakeAnimation(emailField);
            shakeAnimation(passField);
            errorLabel.setText("Credenciales incorrectas");
        }
    }

    @FXML
    private void handleRegister() { // <--- ESTE TAMBIÉN ES IMPORTANTE
        if (regName.getText().isEmpty() || regEmail.getText().isEmpty() || regPass.getText().isEmpty()) {
            errorLabel.setText("Todos los campos son obligatorios");
            return;
        }

        users.add(new User(regName.getText(), regEmail.getText(), regPass.getText()));
        saveUsers();

        // Auto-login o ir al login
        errorLabel.setStyle("-fx-text-fill: #4caf50;"); // Verde
        errorLabel.setText("¡Cuenta creada! Inicia sesión.");
        showLogin();
    }

    @FXML
    private void closeApp() {
        System.exit(0);
    }

    private void closeWindow() {
        Stage stage = (Stage) emailField.getScene().getWindow();
        stage.close();
    }

    // --- ANIMACIÓN DE ERROR ---
    private void shakeAnimation(Node node) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(50), node);
        tt.setByX(10);
        tt.setCycleCount(4);
        tt.setAutoReverse(true);
        tt.play();
    }

    // --- GESTIÓN DE DATOS ---
    private void saveUsers() {
        try (Writer writer = new FileWriter(USER_FILE)) {
            new Gson().toJson(users, writer);
        } catch (IOException e) { e.printStackTrace(); }
    }

    private void loadUsers() {
        try {
            if (Files.exists(Paths.get(USER_FILE))) {
                Type listType = new TypeToken<ArrayList<User>>(){}.getType();
                users = new Gson().fromJson(new FileReader(USER_FILE), listType);
                if(users == null) users = new ArrayList<>();
            }
        } catch (IOException e) { e.printStackTrace(); }
    }

    // Clase interna para Gson
    public static class User {
        String name, email, password;
        User(String n, String e, String p) { name=n; email=e; password=p; }
    }
}