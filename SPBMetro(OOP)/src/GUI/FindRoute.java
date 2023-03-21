package GUI;

import Calculations.*;
import Structure.*;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.util.List;

public class FindRoute extends Stage {
    private RouteCalculator calculator;
    private TextField startStationField;
    private TextField endStationField;
    private Button findRouteBtn;
    private TextArea resultTextArea;

    public FindRoute(RouteCalculator calculator, TextArea resultTextArea) {
        this.calculator = calculator;
        this.resultTextArea = resultTextArea;
        setTitle("Find Route");

        startStationField = new TextField();
        endStationField = new TextField();
        findRouteBtn = new Button("Find route");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.add(new Label("Start station:"), 0, 0);
        grid.add(startStationField, 1, 0);
        grid.add(new Label("End station:"), 0, 1);
        grid.add(endStationField, 1, 1);
        grid.add(findRouteBtn, 0, 2);

        findRouteBtn.setOnAction(e -> {
            String start = startStationField.getText();
            String end = endStationField.getText();
            List<Station> route = calculator.findShortestRoute(start, end);
            String result = printRoute(route);
            resultTextArea.setText(result);
            close();
        });

        Scene scene = new Scene(grid, 300, 150);
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
