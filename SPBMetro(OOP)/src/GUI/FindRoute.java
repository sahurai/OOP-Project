package GUI;

import Calculations.*;
import Structure.*;
import Users.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.List;

public class FindRoute extends Stage {
    private RouteCalculator calculator;
    private TextField startStationField;
    private TextField endStationField;
    private Button findRouteBtn;
    private TextArea resultTextArea;

    public FindRoute(RouteCalculator calculator, TextArea resultTextArea, User user) {
        this.calculator = calculator;
        this.resultTextArea = resultTextArea;
        setTitle("Find Route");
        getIcons().add(new Image("/Images/logo.png")); // Установка логотипа метро
        startStationField = new TextField();
        endStationField = new TextField();
        findRouteBtn = new Button("Find route");

        // Контейнер для элементов управления
        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10, 10, 10, 10));

        HBox startStationFieldBox = new HBox(10); // станция начала
        startStationFieldBox.setAlignment(Pos.CENTER_LEFT);
        startStationFieldBox.getChildren().addAll(new Label("Start station:"), startStationField);

        HBox endStationFieldBox = new HBox(10); // станция конца
        endStationFieldBox.setAlignment(Pos.CENTER_LEFT);
        endStationFieldBox.getChildren().addAll(new Label("End station: "), endStationField);

        HBox findRouteBtnBox = new HBox(); // кнопка начала поиска
        findRouteBtnBox.getChildren().add(findRouteBtn);

        vbox.getChildren().addAll(startStationFieldBox, endStationFieldBox, findRouteBtnBox);


        findRouteBtn.setOnAction(e -> {
            String start = startStationField.getText();
            String end = endStationField.getText();
            List<Station> route = calculator.findShortestRoute(start, end); // считаем путь
            if(!route.isEmpty()){
                user.getStoredRoutes().add(route); // добавляем путь в список в классе
            }
            String result = printRoute(route);
            resultTextArea.setText(result); // выписываем результат
            close();
        });

        Scene scene = new Scene(vbox, 300, 120);
        setScene(scene);
    }

    private String printRoute(List<Station> route) {
        if (route.isEmpty()) {
            return "No route found. Maybe one of stations is closed or wrong input.";
        }

        int travelTime = calculator.getTravelTime(route);
        int costPerRoute = 70;

        String result = "Shortest route: " + route.get(0).getNameOfStation() + " - " + route.get(route.size() - 1).getNameOfStation() + " (Travel time: " + travelTime + " minutes, Cost: " + costPerRoute + " rubles):\n";
        for (Station station : route) {
            result += "\t" + station + "\n";
        }

        result += "Total stations: " + route.size();

        return result;
    }
}
