module com.ltm.client {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires validatorfx;
    requires org.kordamp.bootstrapfx.core;

    opens com.ltm.client.Controller to javafx.fxml;
    exports com.ltm.client;
    exports com.ltm.client.Utils;
}