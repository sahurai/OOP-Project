package GUI;

import Calculations.*;
import Users.*;
import Structure.*;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GUI extends Application {
    private final RouteCalculator calculator;
    private final TextArea outputArea;
    private Map<String, User> users;

    public GUI() {
        JsonParser parser = new JsonParser();
        parser.parse();
        calculator = new RouteCalculator(parser.stations, parser.connections);
        outputArea = new TextArea();
        outputArea.setEditable(false);
        users = new HashMap<>();
        loadUsersFromFile(); // при запуске удаляет пропуски
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Route Finder"); // Установка заголовка окна
        primaryStage.getIcons().add(new Image("/Images/logo.png")); // Установка логотипа метро
        loadUsersFromFile(); // Загрузка пользователей из файла
        showLoginMenu(primaryStage); // Отображение меню с опциями входа и регистрации
    }

    // Вход
    private void showLoginMenu(Stage primaryStage) {
        VBox vbox = new VBox(10);

        Button logInAsUserBtn = new Button("Log in as user");
        logInAsUserBtn.setOnAction(e -> showLoginWindow(primaryStage, false));

        Button logInAsAdminBtn = new Button("Log in as Admin");
        logInAsAdminBtn.setOnAction(e -> showLoginWindow(primaryStage, true));

        Button logInAsGuestBtn = new Button("Log in as guest");
        logInAsGuestBtn.setOnAction(e -> {
            User guest = new Guest();
            showMainScreen(primaryStage, guest);
        });
        // одинаквый размер кнопок
        logInAsAdminBtn.setMinWidth(200);
        logInAsUserBtn.setMinWidth(200);
        logInAsGuestBtn.setMinWidth(200);

        vbox.getChildren().addAll(logInAsUserBtn, logInAsGuestBtn, logInAsAdminBtn);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(10));

        Scene scene = new Scene(vbox, 300, 150);
        primaryStage.setScene(scene);
        primaryStage.show();
    } // Меню выбора входа

    private void showLoginWindow(Stage primaryStage, boolean isAdmin) {
        VBox vbox = new VBox(10);

        TextField loginField = new TextField();
        loginField.setPromptText("Login");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button loginBtn = new Button("Log in");
        loginBtn.setOnAction(e -> {
            String login = loginField.getText();
            String password = passwordField.getText();
            User user = users.get(login);

            if (user != null && user.getPassword().equals(password) && (isAdmin == (user instanceof Admin))) {
                showMainScreen(primaryStage, user);
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Invalid login or password");
                alert.setContentText("Please try again.");
                alert.showAndWait();
            }
        });

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> showLoginMenu(primaryStage));

        Button signInBtn = new Button("Sign in");
        signInBtn.setOnAction(e -> showSignInWindow(primaryStage));

        Label singInLabel = new Label("Don't have account?");
        if(isAdmin == false){
            vbox.getChildren().addAll(loginField, passwordField, loginBtn, singInLabel, signInBtn, backBtn);
            vbox.setPadding(new Insets(10));
        }else{
            vbox.getChildren().addAll(loginField, passwordField, loginBtn, backBtn);
            vbox.setPadding(new Insets(10));
        }

        Scene scene = new Scene(vbox, 300, 220);
        primaryStage.setScene(scene);
        primaryStage.show();
    } // логгин

    private void showSignInWindow(Stage primaryStage) {
        VBox vbox = new VBox(10);

        TextField loginField = new TextField();
        loginField.setPromptText("Login");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        Button registerBtn = new Button("Register");
        registerBtn.setOnAction(e -> {
            String login = loginField.getText();
            String password = passwordField.getText();

            if (users.containsKey(login)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("User already exists");
                alert.setContentText("Please choose a different login.");
                alert.showAndWait();
            } else {
                User newUser = new User(login, password);
                users.put(login, newUser);
                addUserToFile(newUser);
                showMainScreen(primaryStage, newUser);
            }
        });

        Button backBtn = new Button("Back");
        backBtn.setOnAction(e -> showLoginMenu(primaryStage));

        vbox.getChildren().addAll(loginField, passwordField, registerBtn, backBtn);
        vbox.setPadding(new Insets(10));

        Scene scene = new Scene(vbox, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.show();
    } // регистрация

    // Все остальные методы, когда уже зашел в акк
    private void showMainScreen(Stage primaryStage, User user) {
        outputArea.clear(); // очищаем консоль от прошлых выводов

        // Создание кнопок и установка обработчиков событий для них
        Button findRouteBtn = new Button("Find route"); // кнопка Find Route
        findRouteBtn.setOnAction(e -> showFindRouteWindow(user)); // указываем юзера, чтобы сохранить в него пройденный путь
        findRouteBtn.setDisable(!user.canFindRoute());
        findRouteBtn.setMinWidth(130);

        Button historyBtn = new Button("Get history of routes"); // кнопка History
        historyBtn.setOnAction(e -> {
            outputArea.clear();
            outputArea.setText(printHistoryOfRoutes(calculator, user.getStoredRoutes())); // берем историю из класса User и калькулятор нужен для подсчета времени
        });
        historyBtn.setDisable(!user.canGetHistoryOfRoutes());
        historyBtn.setMinWidth(130);

        Button setStatusBtn = new Button("Set status of station"); // кнопка Set Status
        setStatusBtn.setOnAction(e -> showSetStatusWindow());
        setStatusBtn.setDisable(!user.canSetStatusOfStation());
        setStatusBtn.setMinWidth(130);

        Button showImageBtn = new Button("Map"); // кнопка Map
        showImageBtn.setOnAction(e -> showImageWindow());
        showImageBtn.setDisable(!user.canShowMap());

        Button adminSettingsBtn = new Button("Admin settings"); // кнопка админа
        adminSettingsBtn.setOnAction(e -> showAdminSettings());
        adminSettingsBtn.setDisable(!user.canSetNewAdmin());
        adminSettingsBtn.setMinWidth(130);

        Button logOutBtn = new Button("Log out");
        logOutBtn.setOnAction(e -> showLoginMenu(primaryStage));

        Button exitBtn = new Button("Exit"); // кнопка Exit
        exitBtn.setOnAction(e-> primaryStage.close());

        VBox vbox = new VBox();
        vbox.setSpacing(5);

        if (user instanceof Admin){
            vbox.getChildren().addAll(findRouteBtn, historyBtn, setStatusBtn, showImageBtn, outputArea,adminSettingsBtn, logOutBtn, exitBtn);
            vbox.setPadding(new Insets(10)); // Установка отступов
        } else if(user instanceof Guest){
            vbox.getChildren().addAll(findRouteBtn, showImageBtn, outputArea, logOutBtn, exitBtn);
            vbox.setPadding(new Insets(10)); // Установка отступов
        } else{
            vbox.getChildren().addAll(findRouteBtn, historyBtn, showImageBtn, outputArea, logOutBtn, exitBtn);
            vbox.setPadding(new Insets(10)); // Установка отступов
        }

        // Создание сцены и установка ее для primaryStage
        Scene scene = new Scene(vbox, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show(); // Отображение главного окна
    }

    //Работа с юзерами и файлами
    private void loadUsersFromFile(){
        try (BufferedReader reader = new BufferedReader(new FileReader("resources/users.txt"))) {
            // Удаление пустых строк
            List<String> text = Files.readAllLines(Paths.get("resources/users.txt"));
            while (text.contains("")){
                text.remove("");
            }
            Files.write(Paths.get("resources/users.txt"), text);

            // Чтение юзеров
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(":");
                String login = parts[0];
                String password = parts[1];
                boolean isAdmin = Boolean.parseBoolean(parts[2]);

                User user = isAdmin ? new Admin(login, password) : new User(login, password); // агрегация, полиморфизм
                users.put(login, user);
            }
        } catch (IOException e) {
            System.err.println("Error reading users from file: " + e.getMessage());
        }
    }

    private void addUserToFile(User user) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("resources/users.txt", true))) {
            File file = new File("resources/users.txt");
            if (file.length() != 0) {
                // Если файл не пуст, добавьте символ перевода строки перед записью новой информации о пользователе
                writer.write("\n");
            }
            writer.write(user.getLogin() + ":" + user.getPassword() + ":" + (user instanceof Admin));
        } catch (IOException e) {
            System.err.println("Error writing user to file: " + e.getMessage());
        }
    }

    private void saveUsersToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("resources/users.txt", false))) {
            for (User user : users.values()) {
                writer.write(user.getLogin() + ":" + user.getPassword() + ":" + (user instanceof Admin) + "\n");
            }
        } catch (IOException e) {
            System.err.println("Error writing users to file: " + e.getMessage());
        }
    }

    //Методы
    private void showAdminSettings() {
        // Создание нового окна
        Stage setNewAdminStage = new Stage();
        setNewAdminStage.setTitle("Admin settings");
        setNewAdminStage.getIcons().add(new Image("/Images/logo.png")); // Установка логотипа метро

        // Создание списка пользователей
        ListView<User> userListView = new ListView<>();
        userListView.setItems(FXCollections.observableArrayList(users.values()));
        userListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(User item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.toString());
            }
        });

        // Создание кнопки для установки нового администратора
        Button setAdminButton = new Button("Set as admin");
        setAdminButton.setOnAction(e -> {
            User selectedUser = userListView.getSelectionModel().getSelectedItem();
            if (selectedUser != null && !(selectedUser instanceof Admin)) {
                // Изменение статуса выбранного пользователя на администратора
                users.remove(selectedUser.getLogin());
                Admin newAdmin = new Admin(selectedUser.getLogin(), selectedUser.getPassword());
                users.put(selectedUser.getLogin(), newAdmin);

                // Обновление списка пользователей
                userListView.setItems(FXCollections.observableArrayList(users.values()));

                // Обновление данных о пользователях в файле
                saveUsersToFile();
            }
        });

        // Создание кнопки для удаления статуса админа
        Button removeAdminStatusButton = new Button("Set as user");
        removeAdminStatusButton.setOnAction(e -> {
            User selectedUser = userListView.getSelectionModel().getSelectedItem();
            if (selectedUser != null && selectedUser instanceof Admin) {
                // Изменение статуса выбранного пользователя на администратора
                users.remove(selectedUser.getLogin());
                User newUser = new User(selectedUser.getLogin(), selectedUser.getPassword());
                users.put(selectedUser.getLogin(), newUser);

                // Обновление списка пользователей
                userListView.setItems(FXCollections.observableArrayList(users.values()));

                // Обновление данных о пользователях в файле
                saveUsersToFile();
            }
        });

        // удаления юзера
        Button removeUserButton = new Button("Delete user's account");
        removeUserButton.setOnAction(e ->{
            User selectedUser = userListView.getSelectionModel().getSelectedItem();
            users.remove(selectedUser.getLogin());
            userListView.setItems(FXCollections.observableArrayList(users.values()));
            saveUsersToFile();
        });

        // Создание вертикального контейнера для размещения списка пользователей и кнопки
        VBox vbox = new VBox(10, userListView, setAdminButton, removeAdminStatusButton, removeUserButton);
        vbox.setPadding(new Insets(10));

        // Создание сцены и установка ее
        Scene scene = new Scene(vbox, 400, 300);
        setNewAdminStage.setScene(scene);
        setNewAdminStage.show(); // Отображение окна
    } // настройки админа

    private void showFindRouteWindow(User user) {
        FindRoute findRouteWindow = new FindRoute(calculator, outputArea, user);
        findRouteWindow.show();
    } //поиск пути

    private void showSetStatusWindow() {
        SetStatus setStatusWindow = new SetStatus(calculator);
        setStatusWindow.show();
    } // статус станции

    private void showImageWindow() {
        Stage imageStage = new Stage();
        imageStage.setTitle("Map of Saint-Petersburg");
        imageStage.getIcons().add(new Image("/Images/logo.png")); // Установка логотипа метро

        ImageView imageView = new ImageView();
        try {
            Image image = new Image("Images/map.jpg"); // ссылка на сайт метро спб
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
    } // карта метро

    private String printHistoryOfRoutes(RouteCalculator calculator, List<List<Station>> storedRoutes) {

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
