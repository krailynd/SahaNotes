package com.jrgs.bloc.blocdenotas;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

public class OnboardingController {
    @FXML private VBox contentBox;
    @FXML private Label slideTitle;
    @FXML private Label slideContent;
    @FXML private Button btnNext;
    @FXML private HBox actionButtons;
    @FXML private HBox dotsContainer;

    private int step = 0;

    // Contenido de las slides
    private final String[][] slides = {
            {"Bienvenido a SahaNote", "La evolución del bloc de notas. Minimalista, potente y elegante."},
            {"Markdown Visual", "Escribe con formato, previsualiza en tiempo real y organiza tus ideas sin distracciones."},
            {"Productividad Total", "Gestión de tareas, calendario y reloj integrados en tu flujo de trabajo."},
            {"Creado por...", "Esta app está creada por\nElvis Melchor Carbajal Dall'orso\n\nNombre artístico: Krailynd"}
    };

    @FXML public void initialize() {
        updateSlide();
        animateContent();
    }

    @FXML private void nextSlide() {
        if (step < slides.length - 1) {
            step++;
            animateTransition();
        }
    }

    private void animateTransition() {
        // Fade out
        FadeTransition ft = new FadeTransition(Duration.millis(300), contentBox);
        ft.setFromValue(1);
        ft.setToValue(0);
        ft.setOnFinished(e -> {
            updateSlide();
            // Fade in + Slide Up
            contentBox.setTranslateY(20);
            FadeTransition ftIn = new FadeTransition(Duration.millis(500), contentBox);
            ftIn.setFromValue(0);
            ftIn.setToValue(1);
            TranslateTransition tt = new TranslateTransition(Duration.millis(500), contentBox);
            tt.setToY(0);
            ftIn.play();
            tt.play();
        });
        ft.play();
    }

    private void animateContent() {
        contentBox.setOpacity(0);
        contentBox.setTranslateY(20);
        FadeTransition ft = new FadeTransition(Duration.millis(800), contentBox);
        ft.setToValue(1);
        TranslateTransition tt = new TranslateTransition(Duration.millis(800), contentBox);
        tt.setToY(0);
        ft.play();
        tt.play();
    }

    private void updateSlide() {
        slideTitle.setText(slides[step][0]);
        slideContent.setText(slides[step][1]);

        // Actualizar puntos
        for(int i=0; i<dotsContainer.getChildren().size(); i++) {
            Circle c = (Circle) dotsContainer.getChildren().get(i);
            c.setFill(i == step ? javafx.scene.paint.Color.WHITE : javafx.scene.paint.Color.rgb(50,50,50));
        }

        // Si es el último slide (Créditos)
        if (step == slides.length - 1) {
            btnNext.setVisible(false);
            btnNext.setManaged(false);
            actionButtons.setVisible(true);
            actionButtons.setManaged(true);

            // Estilo especial para los créditos
            slideTitle.setStyle("-fx-text-fill: #4caf50;");
            slideContent.setStyle("-fx-font-style: italic; -fx-text-fill: #aaa;");
        }
    }

    @FXML private void createNew() {
        closeWindow();
        new App().openNewWorkspacePrompt();
    }

    @FXML private void openExisting() {
        closeWindow();
        new App().openExistingWorkspace();
    }

    private void closeWindow() {
        ((Stage) contentBox.getScene().getWindow()).close();
    }
}