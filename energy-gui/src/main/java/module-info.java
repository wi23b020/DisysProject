module org.example.energygui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;

    opens org.example.energygui to javafx.fxml, com.fasterxml.jackson.databind;

    exports org.example.energygui;
    exports org.example.energygui.controller;
    opens org.example.energygui.controller to com.fasterxml.jackson.databind, javafx.fxml;
    exports org.example.energygui.model;
    opens org.example.energygui.model to com.fasterxml.jackson.databind, javafx.fxml;
    exports org.example.energygui.api;
    opens org.example.energygui.api to com.fasterxml.jackson.databind, javafx.fxml;
}
