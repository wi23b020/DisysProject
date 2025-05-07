module org.example.energygui {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;

    opens org.example.energygui to javafx.fxml, com.fasterxml.jackson.databind;

    exports org.example.energygui;
}
