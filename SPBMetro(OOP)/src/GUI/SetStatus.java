package GUI;

import Calculations.*;
import Structure.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class SetStatus extends Stage {

    public SetStatus(RouteCalculator calculator) {
        // Название окна
        setTitle("Set Status of Station");
        getIcons().add(new Image("/Images/logo.png")); // Установка логотипа метро
        TextField stationField = new TextField(); // Текстовое поле для ввода названия станции
        Label currentStatusLabel = new Label();  // Метка, отображающая текущий статус станции
        Button closeButton = new Button("Close"); // Кнопка "Закрыть", меняющая статус станции на "закрыта"
        Button openButton = new Button("Open"); // Кнопка "Открыть", меняющая статус станции на "открыта"

        // Обработчик нажатия на кнопку "Закрыть"
        closeButton.setOnAction(e -> setStatus(calculator, stationField.getText(), currentStatusLabel, false));

        // Обработчик нажатия на кнопку "Открыть"
        openButton.setOnAction(e -> setStatus(calculator, stationField.getText(), currentStatusLabel, true));

        // Контейнер для элементов управления
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10, 10, 10, 10));

        HBox stationFieldBox = new HBox(10); // сюда вводить станцию
        stationFieldBox.setAlignment(Pos.CENTER_LEFT);
        stationFieldBox.getChildren().addAll(new Label("Station:"), stationField);

        HBox currentStatusBox = new HBox(10); // статус станции
        currentStatusBox.setAlignment(Pos.CENTER_LEFT);
        currentStatusBox.getChildren().addAll(new Label("Current Status:"), currentStatusLabel);

        HBox closeButtonBox = new HBox(10); // кнопка закрытия станции
        closeButtonBox.setAlignment(Pos.CENTER_RIGHT);
        closeButtonBox.getChildren().add(closeButton);

        HBox openButtonBox = new HBox(10); // кнопка открытия станции
        openButtonBox.setAlignment(Pos.CENTER_LEFT);
        openButtonBox.getChildren().add(openButton);

        HBox buttonsBox = new HBox();
        buttonsBox.setSpacing(5);
        buttonsBox.getChildren().addAll(closeButtonBox, openButtonBox);
        vbox.getChildren().addAll(stationFieldBox, currentStatusBox, buttonsBox);

        // Обработчик изменения текста в текстовом поле
        stationField.textProperty().addListener((observable, oldValue, newValue) -> {
            Station station = calculator.findStationByName(newValue);
            if (station != null) {
                if(station.isStatus()){
                    currentStatusLabel.setText("Open");
                }else{
                    currentStatusLabel.setText("Closed");
                }
            } else {
                currentStatusLabel.setText("Station not found");
            }
        });

        Scene scene = new Scene(vbox, 300, 120);
        setScene(scene);
    }

    // Метод для изменения статуса станции
    private void setStatus(RouteCalculator calculator, String stationName, Label currentStatusLabel, boolean status) {
        Station station = calculator.findStationByName(stationName);
        if (station != null) {
            station.setStatus(status);
            if(station.isStatus()){
                currentStatusLabel.setText("Open");
            }else{
                currentStatusLabel.setText("Closed");
            }
        }
    }
}
