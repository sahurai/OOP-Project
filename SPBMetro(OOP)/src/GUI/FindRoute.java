package GUI;

import Calculations.*;
import Structure.*;
import Users.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.Side;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
/**
 *  Help class to simplify code.
 */
public class FindRoute extends Stage {
    private RouteCalculator calculator;
    private TextField startStationField;
    private TextField endStationField;
    private Button findRouteBtn;
    /**
     * List of stations to use autofill feature.
     */
    private String[] stationsAsString = {"Devyatkino", "Grazhdanskiy Prospekt", "Akademicheskaya", "Politekhnicheskaya", "Ploschad Muzhestva", "Lesnaya", "Vyborgskaya", "Ploshchad Lenina", "Chernyshevskaya", "Ploshchad Vosstaniya", "Vladimirskaya", "Pushkinskaya", "Tekhnologicheskiy Institut 1", "Baltiyskaya", "Narvskaya", "Kirovskiy Zavod", "Avtovo", "Leninskiy Prospekt", "Prospekt Veteranov", "Parnas", "Prospekt Prosvescheniya", "Ozerki", "Udelnaya", "Pionerskaya", "Chyornaya Rechka", "Petrogradskaya", "Gorkovskaya", "Nevskiy Prospekt", "Sennaya Ploshchad", "Tekhnologicheskiy Institut 2", "Frunzenskaya", "Moskovskiye Vorota", "Elektrosila", "Park Pobedy", "Moskovskaya", "Zvyozdnaya", "Kupchino", "Begovaya", "Zenit", "Primorskaya", "Vasileostrovskaya", "Gostiny Dvor", "Mayakovskaya", "Ploshchad Aleksandra Nevskogo 1", "Yelizarovskaya", "Lomonosovskaya", "Proletarskaya", "Obukhovo", "Rybatskoye", "Spasskaya", "Dostoyevskaya", "Ligovskiy Prospekt", "Ploshchad Aleksandra Nevskogo 2", "Novocherkasskaya", "Ladozhskaya", "Prospekt Bolshevikov", "Ulitsa Dybenko", "Komendantskiy Prospekt", "Staraya Derevnya", "Krestovskiy Ostrov", "Chkalovskaya", "Sportivnaya", "Admiralteyskaya", "Sadovaya", "Zvenigorodskaya", "Obvodny Kanal", "Volkovskaya", "Bukharestskaya", "Mezhdunarodnaya", "Prospekt Slavy", "Dunayskaya", "Shushary"};

    /**
     * Find route menu.
     * @param calculator calculator
     * @param resultTextArea output area
     * @param user user
     */
    public FindRoute(RouteCalculator calculator, ListView resultTextArea, User user) {
        this.calculator = calculator;
        setTitle("Find Route");
        getIcons().add(new Image("/Images/logo.png")); // Установка логотипа метро
        startStationField = new TextField();
        endStationField = new TextField();
        findRouteBtn = new Button("Find route");
        ContextMenu contextMenu = new ContextMenu();
        List<String> matchingData = new ArrayList<>();

        //автоподстановка
        for (String s : stationsAsString) {
            MenuItem item = new MenuItem(s);
            item.setOnAction(event -> startStationField.setText(s));
            contextMenu.getItems().add(item);
        }
        startStationField.setOnKeyReleased(event -> {
            String enteredText = startStationField.getText().toLowerCase();
            matchingData.clear();
            contextMenu.getItems().clear();
            if (!enteredText.isEmpty()) { // проверяем на пустоту
                for (String s : stationsAsString) {
                    if (s.toLowerCase().startsWith(enteredText)) {
                        matchingData.add(s);
                    }
                }
                for (String s : matchingData) {
                    MenuItem item = new MenuItem(s);
                    item.setOnAction(some -> startStationField.setText(s));
                    contextMenu.getItems().add(item);
                }
                if (!matchingData.isEmpty()) {
                    contextMenu.show(startStationField, Side.BOTTOM, 0, 0);
                } else {
                    contextMenu.hide();
                }
            } else {
                contextMenu.hide(); // скрываем контекстное меню, если введен пустой текст
            }
        });
        endStationField.setOnKeyReleased(event -> {
            String enteredText = endStationField.getText().toLowerCase();
            matchingData.clear();
            contextMenu.getItems().clear();
            if (!enteredText.isEmpty()) { // проверяем на пустоту
                for (String s : stationsAsString) {
                    if (s.toLowerCase().startsWith(enteredText)) {
                        matchingData.add(s);
                    }
                }
                for (String s : matchingData) {
                    MenuItem item = new MenuItem(s);
                    item.setOnAction(some -> endStationField.setText(s));
                    contextMenu.getItems().add(item);
                }
                if (!matchingData.isEmpty()) {
                    contextMenu.show(endStationField, Side.BOTTOM, 0, 0);
                } else {
                    contextMenu.hide();
                }
            } else {
                contextMenu.hide(); // скрываем контекстное меню, если введен пустой текст
            }
        });

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
            String[] lines = result.split("\n");
            ObservableList<String> items = FXCollections.observableArrayList(lines);
            resultTextArea.setItems(items);
            close();
        });

        Scene scene = new Scene(vbox, 300, 120);
        setScene(scene);
    }

    /**
     * Print route method.
     * @param route list of stations
     * @return String of stations on this route
     */
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
