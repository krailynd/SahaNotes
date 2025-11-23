module com.jrgs.bloc.blocdenotas {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;        // <--- NUEVO
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.core;
    requires com.google.gson;
    requires org.commonmark;    // <--- NUEVO

    opens com.jrgs.bloc.blocdenotas to javafx.fxml, com.google.gson;
    exports com.jrgs.bloc.blocdenotas;
}