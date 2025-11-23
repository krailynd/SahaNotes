package com.jrgs.bloc.blocdenotas;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration;
import javafx.util.StringConverter;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class MainController {

    @FXML private BorderPane mainContainer;
    @FXML private HBox topBar;
    @FXML private VBox leftSidebar, rightSidebar;
    @FXML private HBox sidebarHeader, sidebarActions;
    @FXML private Label workspaceLabel;

    @FXML private TreeView<File> fileTree;
    @FXML private TextField noteTitle;
    @FXML private TextArea contentEditor;
    @FXML private VBox editorContainer;
    @FXML private SplitPane mainSplitPane; // Necesario para ocultar el panel
    @FXML private WebView previewView;

    @FXML private Label clockLabel, dateLabel;
    @FXML private GridPane calendarGrid;
    @FXML private ListView<ItemTarea> todoList;
    @FXML private TextField todoInput;

    private File currentWorkspaceDir;
    private File currentFile;
    private double xOffset, yOffset;
    private boolean isFocusMode = false;
    private boolean isLeftSidebarMini = false;

    private final Parser parser = Parser.builder().build();
    private final HtmlRenderer renderer = HtmlRenderer.builder().build();

    public void initializeWorkspace(File workspaceDir) {
        this.currentWorkspaceDir = workspaceDir;
        workspaceLabel.setText(workspaceDir.getName());

        loadFileSystem(workspaceDir);
        setupMarkdownListener();
        startClock();
        buildCalendar();
        setupTodoList();

        // ESCAPE para salir de modo monje
        Platform.runLater(() -> {
            mainContainer.getScene().addEventFilter(KeyEvent.KEY_PRESSED, event -> {
                if (isFocusMode && event.getCode() == KeyCode.ESCAPE) {
                    onToggleFocusMode();
                    event.consume();
                }
            });
        });
    }

    // --- MODO MONJE ---
    @FXML private void onToggleFocusMode() {
        isFocusMode = !isFocusMode;
        topBar.setVisible(!isFocusMode); topBar.setManaged(!isFocusMode);
        leftSidebar.setVisible(!isFocusMode); leftSidebar.setManaged(!isFocusMode);
        rightSidebar.setVisible(!isFocusMode); rightSidebar.setManaged(!isFocusMode);
        Stage stage = (Stage) mainContainer.getScene().getWindow();
        stage.setFullScreen(isFocusMode);
    }

    // --- PANELES ---
    @FXML private void onToggleLeft() {
        isLeftSidebarMini = !isLeftSidebarMini;
        if (isLeftSidebarMini) {
            leftSidebar.setPrefWidth(50); leftSidebar.setMinWidth(50);
            sidebarHeader.setVisible(false); sidebarHeader.setManaged(false);
            sidebarActions.setVisible(false); sidebarActions.setManaged(false);
        } else {
            leftSidebar.setPrefWidth(250); leftSidebar.setMinWidth(250);
            sidebarHeader.setVisible(true); sidebarHeader.setManaged(true);
            sidebarActions.setVisible(true); sidebarActions.setManaged(true);
        }
        loadFileSystem(currentWorkspaceDir);
    }
    @FXML private void onToggleRight() {
        boolean v = !rightSidebar.isVisible();
        rightSidebar.setVisible(v); rightSidebar.setManaged(v);
    }

    // --- MARKDOWN Y RENDERIZADO ---
    private void setupMarkdownListener() {
        contentEditor.textProperty().addListener((obs, old, text) -> {
            if (currentFile != null && currentFile.getName().endsWith(".md")) {
                updatePreview(text);
            }
        });
    }

    private void updatePreview(String markdown) {
        String processed = markdown
                .replaceAll("- \\[ \\]", "<li style='list-style:none'><input type='checkbox' disabled> ")
                .replaceAll("- \\[x\\]", "<li style='list-style:none'><input type='checkbox' checked disabled> ");

        String html = renderer.render(parser.parse(processed));

        // TRUCO PARA IMAGENES LOCALES: Base tag
        String base = "";
        if(currentFile != null) {
            base = "<base href='" + currentFile.getParentFile().toURI().toString() + "'>";
        }

        String style = "<style>" +
                "body{background:#1e1e1e;color:#dcdcdc;font-family:'Segoe UI';padding:20px;} " +
                "img { max-width: 100%; height: auto; display: block; margin: 10px 0; border-radius: 5px; }" +
                "a{color:#4caf50} code{background:#333;padding:2px 4px;border-radius:3px;} " +
                "table{border-collapse:collapse;width:100%;margin:10px 0;} th,td{border:1px solid #444;padding:8px;} th{background:#333;}" +
                "</style>";

        previewView.getEngine().loadContent("<html><head>" + base + style + "</head><body>" + html + "</body></html>");
        saveCurrentFile();
    }

    // --- FILESYSTEM ---
    private void loadFileSystem(File dir) {
        TreeItem<File> root = new TreeItem<>(dir);
        buildTree(root);
        fileTree.setRoot(root);
        fileTree.setEditable(!isLeftSidebarMini);

        fileTree.setCellFactory(new Callback<TreeView<File>, TreeCell<File>>() {
            @Override public TreeCell<File> call(TreeView<File> param) {
                if (isLeftSidebarMini) {
                    return new TreeCell<File>() {
                        @Override protected void updateItem(File item, boolean empty) {
                            super.updateItem(item, empty);
                            if (empty || item == null) { setGraphic(null); setTooltip(null); }
                            else {
                                setGraphic(new Label(getIcon(item)));
                                setTooltip(new Tooltip(item.getName()));
                                setText(null);
                            }
                        }
                    };
                } else { return new TextFieldTreeCellImpl(); }
            }
        });

        fileTree.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if(n != null && n.getValue().isFile()) openFile(n.getValue());
        });
    }

    private String getIcon(File f) {
        if(f.isDirectory()) return "📁";
        if(f.getName().endsWith(".excalidraw")) return "🎨";
        return "📄";
    }

    private class TextFieldTreeCellImpl extends TextFieldTreeCell<File> {
        public TextFieldTreeCellImpl() {
            super(new StringConverter<File>() {
                @Override public String toString(File object) { return object.getName(); }
                @Override public File fromString(String string) { return new File(currentFile.getParent(), string); }
            });
        }
        @Override public void updateItem(File item, boolean empty) {
            super.updateItem(item, empty);
            if(!empty && item != null) setGraphic(new Label(getIcon(item)));
        }
        @Override public void commitEdit(File newFile) {
            File oldFile = getItem();
            File renamed = new File(oldFile.getParent(), newFile.getName());
            if(oldFile.renameTo(renamed)) { super.commitEdit(renamed); loadFileSystem(currentWorkspaceDir); }
        }
    }

    private void buildTree(TreeItem<File> item) {
        File[] files = item.getValue().listFiles();
        if (files != null) {
            Arrays.sort(files, (f1, f2) -> {
                if (f1.isDirectory() && !f2.isDirectory()) return -1;
                if (!f1.isDirectory() && f2.isDirectory()) return 1;
                return f1.getName().compareToIgnoreCase(f2.getName());
            });
            for (File child : files) {
                if(!child.isHidden()) {
                    TreeItem<File> childItem = new TreeItem<>(child);
                    item.getChildren().add(childItem);
                    if(child.isDirectory()) buildTree(childItem);
                }
            }
        }
    }

    // --- ABRIR ARCHIVOS (LOGICA EXCALIDRAW ARREGLADA) ---
    private void openFile(File file) {
        this.currentFile = file;
        noteTitle.setText(file.getName());

        if (file.getName().endsWith(".excalidraw")) {
            // MODO EXCALIDRAW: OCULTAR EDITOR, SOLO WEBVIEW
            editorContainer.setVisible(false);
            editorContainer.setManaged(false);
            // Eliminar división visual para que ocupe todo
            mainSplitPane.setDividerPositions(0);

            // Cargar Excalidraw
            previewView.getEngine().load("https://excalidraw.com/");
        } else {
            // MODO NOTA NORMAL
            editorContainer.setVisible(true);
            editorContainer.setManaged(true);
            mainSplitPane.setDividerPositions(0.5); // Mitad y mitad

            try {
                String content = Files.readString(file.toPath());
                contentEditor.setText(content);
                updatePreview(content);
            } catch (IOException e) { e.printStackTrace(); }
        }
    }

    private void saveCurrentFile() {
        if (currentFile != null && currentFile.getName().endsWith(".md")) {
            try { Files.writeString(currentFile.toPath(), contentEditor.getText()); } catch (IOException e) {}
        }
    }

    // --- CREACION ---
    @FXML private void onCreateFolder() { createItem("Nueva Carpeta", true); }
    @FXML private void onCreateNote() { createItem("Nota.md", false); }
    @FXML private void onCreateExcalidraw() { createItem("Dibujo.excalidraw", false); }

    private void createItem(String name, boolean isDir) {
        File parent = currentWorkspaceDir;
        TreeItem<File> selected = fileTree.getSelectionModel().getSelectedItem();
        if(selected != null && selected.getValue().isDirectory()) parent = selected.getValue();

        String finalName = name.contains(".") ? "Nuevo " + System.currentTimeMillis() + name.substring(name.lastIndexOf(".")) : name + " " + System.currentTimeMillis();
        File newItem = new File(parent, finalName);

        try {
            if (isDir) newItem.mkdirs(); else newItem.createNewFile();
            loadFileSystem(currentWorkspaceDir);
        } catch (IOException e) { e.printStackTrace(); }
    }
    @FXML private void onRefresh() { loadFileSystem(currentWorkspaceDir); }

    // --- TOOLBAR ---
    @FXML private void mdBold() { insertWrap("**","**"); }
    @FXML private void mdItalic() { insertWrap("*","*"); }
    @FXML private void mdH1() { insertText("# "); }
    @FXML private void mdH2() { insertText("## "); }
    @FXML private void mdCheck() { insertText("- [ ] "); }
    @FXML private void mdHighlight() { insertWrap("<mark>","</mark>"); }
    @FXML private void mdImage() { insertText("![Desc](img/nombre_imagen.png) "); } // Ruta relativa
    private void insertWrap(String s, String e) { String sel = contentEditor.getSelectedText(); contentEditor.replaceSelection(s+(sel.isEmpty()?"":sel)+e); contentEditor.requestFocus(); }
    private void insertText(String t) { contentEditor.insertText(contentEditor.getCaretPosition(), t); contentEditor.requestFocus(); }

    // --- CONTROLES VENTANA ---
    @FXML private void onTitleBarPressed(MouseEvent e) { xOffset = e.getSceneX(); yOffset = e.getSceneY(); }
    @FXML private void onTitleBarDragged(MouseEvent e) {
        Stage s = (Stage)mainContainer.getScene().getWindow();
        if(!s.isMaximized()) { s.setX(e.getScreenX() - xOffset); s.setY(e.getScreenY() - yOffset); }
    }
    @FXML private void onClose() { ((Stage)mainContainer.getScene().getWindow()).close(); }
    @FXML private void onMinimize() { ((Stage)mainContainer.getScene().getWindow()).setIconified(true); }
    @FXML private void onMaximize() { Stage s = (Stage)mainContainer.getScene().getWindow(); s.setMaximized(!s.isMaximized()); }

    // --- WIDGETS ---
    private void startClock() { Timeline c=new Timeline(new KeyFrame(Duration.seconds(1),e->{LocalDateTime n=LocalDateTime.now();clockLabel.setText(n.format(DateTimeFormatter.ofPattern("HH:mm")));dateLabel.setText(n.format(DateTimeFormatter.ofPattern("EEE, d MMM")));}));c.setCycleCount(Animation.INDEFINITE);c.play(); }
    private void buildCalendar() { LocalDate t=LocalDate.now();YearMonth ym=YearMonth.from(t);int d=ym.atDay(1).getDayOfWeek().getValue();String[] h={"L","M","M","J","V","S","D"};for(int i=0;i<7;i++){Label l=new Label(h[i]);l.getStyleClass().add("calendar-day-header");calendarGrid.add(l,i,0);}int r=1,c=d-1;for(int i=1;i<=ym.lengthOfMonth();i++){if(c>6){c=0;r++;}Label l=new Label(String.valueOf(i));l.getStyleClass().add("calendar-day-cell");if(i==t.getDayOfMonth())l.getStyleClass().add("calendar-today");calendarGrid.add(l,c++,r);}}
    private void setupTodoList() { todoList.setCellFactory(CheckBoxListCell.forListView(ItemTarea::onProperty)); }
    @FXML private void addTodo() { if(!todoInput.getText().isEmpty()){todoList.getItems().add(new ItemTarea(todoInput.getText(),false));todoInput.clear();}}
    public static class ItemTarea { private final javafx.beans.property.StringProperty n=new javafx.beans.property.SimpleStringProperty();private final javafx.beans.property.BooleanProperty o=new javafx.beans.property.SimpleBooleanProperty();public ItemTarea(String name,boolean on){n.set(name);o.set(on);}public javafx.beans.property.BooleanProperty onProperty(){return o;}public String toString(){return n.get();}}
}