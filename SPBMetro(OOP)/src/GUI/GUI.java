package GUI;

import Calculations.*;
import Structure.*;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import java.util.List;

public class GUI extends Application {
    private final RouteCalculator calculator;
    private final TextArea outputArea;
    private int hehecount = 0;

    public GUI() {
        JsonParser parser = new JsonParser();
        parser.parse();
        calculator = new RouteCalculator(parser.stations, parser.connections);
        outputArea = new TextArea();
        outputArea.setEditable(false);
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Route Finder"); // Установка заголовка окна

        // Создание кнопок и установка обработчиков событий для них
        Button findRouteBtn = new Button("Find route"); // кнопка Find Route
        findRouteBtn.setOnAction(e -> showFindRouteWindow());

        Button historyBtn = new Button("Get history of routes"); // кнопка History
        historyBtn.setOnAction(e -> {
            outputArea.clear();
            outputArea.setText(printHistoryOfRoutes(calculator));
        });

        Button setStatusBtn = new Button("Set status of station"); // кнопка Set Status
        setStatusBtn.setOnAction(e -> showSetStatusWindow());

        Button showImageBtn = new Button("Map"); // кнопка Map
        showImageBtn.setOnAction(e -> showImageWindow());

        Button exitBtn = new Button("Exit"); // кнопка Exit
        exitBtn.setOnAction(e-> primaryStage.close());

        Button hehe = new Button("Click me!");
        hehe.setOnAction(e -> {
            if(hehecount == 100){
                f3();
            }else if(hehecount == 10){
                f2();
            }else{
                f1();
            }
            hehecount++;
        });

        // Создание вертикального контейнера для размещения кнопок и текстового поля
        VBox vbox = new VBox(10, findRouteBtn, historyBtn, setStatusBtn, showImageBtn, outputArea, exitBtn, hehe);
        vbox.setPadding(new Insets(10)); // Установка отступов

        // Создание сцены и установка ее для primaryStage
        Scene scene = new Scene(vbox, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show(); // Отображение главного окна
    }

    private void f1(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Funny");
        alert.setHeaderText("hehe");
        alert.setContentText("Try again and get hehe one more time");
        alert.showAndWait();
    }

    private void f2(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Man");
        alert.setHeaderText("You need to chill, don't you have other things to do?");
        alert.setContentText("...");
        alert.showAndWait();
    }

    private void f3(){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("WOW");
        alert.setHeaderText("I think you have too much free time");
        alert.setContentText("Now u really scare me, go take a shower");
        alert.showAndWait();
    }

    private void showFindRouteWindow() {
        FindRoute findRouteWindow = new FindRoute(calculator, outputArea);
        findRouteWindow.show();
    }

    private void showSetStatusWindow() {
        SetStatus setStatusWindow = new SetStatus(calculator);
        setStatusWindow.show();
    }

    private void showImageWindow() {
        Stage imageStage = new Stage();
        imageStage.setTitle("Map of Saint-Petersburg");

        ImageView imageView = new ImageView();
        try {
            Image image = new Image("http://www.metro.spb.ru/uploads/metromap2023r.jpg"); // ссылка на сайт метро спб
            imageView.setImage(image);
        } catch (Exception e) {
            System.err.println("Error loading image: " + e.getMessage());
        }

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(imageView);

        // Настройка начального масштаба
        double initialScale = 0.75;
        imageView.setScaleX(initialScale);
        imageView.setScaleY(initialScale);

        // Возможность перемещения с помощью мышки
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        scrollPane.setPannable(true);

        Scene scene = new Scene(scrollPane, 1200, 900);
        imageStage.setScene(scene);
        imageStage.show();
    }

    private String printHistoryOfRoutes(RouteCalculator calculator) {
        List<List<Station>> storedRoutes = calculator.getStoredRoutes();

        if (storedRoutes.isEmpty()) {
            return "No routes have been requested.\n";
        }

        int costPerRoute = 70;

        String output = "History of requested routes:\n";

        for (int i = 0; i < storedRoutes.size(); i++) {
            List<Station> route = storedRoutes.get(i);
            int travelTime = calculator.getTravelTime(route);

            output += "Route " + (i + 1) + ": " + route.get(0).getNameOfStation() + "-" + route.get(route.size() - 1).getNameOfStation() + " (Travel time: " + travelTime + " minutes, Cost: " + costPerRoute + " rubles):\n";

            int x = 0;
            for (Station station : route) {
                output += "\t" + station + "\n";
                x++;
            }
            output += "Total stations: " + x + "\n";
        }

        return output;
    }

}
