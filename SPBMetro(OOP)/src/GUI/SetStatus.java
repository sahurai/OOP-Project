package GUI;

import Calculations.*;
import Structure.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class SetStatus extends Stage {

    public SetStatus(RouteCalculator calculator) {
        // Название окна
        setTitle("Set Status of Station");
        TextField stationField = new TextField(); // Текстовое поле для ввода названия станции
        Label currentStatusLabel = new Label();  // Метка, отображающая текущий статус станции
        Button closeButton = new Button("Close"); // Кнопка "Закрыть", меняющая статус станции на "закрыта"
        Button openButton = new Button("Open"); // Кнопка "Открыть", меняющая статус станции на "открыта"
        Button exitButton = new Button("Exit"); // Кнопка "Выход", закрывающая окно

        // Обработчик нажатия на кнопку "Закрыть"
        closeButton.setOnAction(e -> setStatus(calculator, stationField.getText(), currentStatusLabel, false));

        // Обработчик нажатия на кнопку "Открыть"
        openButton.setOnAction(e -> setStatus(calculator, stationField.getText(), currentStatusLabel, true));

        // Обработчик нажатия на кнопку "Выход"
        exitButton.setOnAction(e -> close());

        // Контейнер для элементов управления
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.add(new Label("Station:"), 0, 0); // Метка для текстового поля
        grid.add(stationField, 1, 0); // Текстовое поле для ввода названия станции
        grid.add(new Label("Current Status:"), 0, 1); // Метка для отображения текущего статуса станции
        grid.add(currentStatusLabel, 1, 1); // Метка для отображения текущего статуса станции
        grid.add(closeButton, 0, 2); // Кнопка "Закрыть"
        grid.add(openButton, 1, 2); // Кнопка "Открыть"
        grid.add(exitButton, 0, 3); // Кнопка "Выход"

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

        Scene scene = new Scene(grid, 300, 150);
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
