module Client {
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.graphics;
    requires javafx.web;
    requires java.desktop;

    opens com.kamiyuri;
    opens com.kamiyuri.view;
    opens com.kamiyuri.controller;
    opens com.kamiyuri.tcp;

}