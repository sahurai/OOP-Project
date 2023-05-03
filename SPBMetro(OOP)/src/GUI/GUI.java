package GUI;

import Calculations.*;
import Users.*;
import Structure.*;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * This class is responsible for the graphical interface.
 */
public class GUI extends Application {
    /**Route calculator.*/
    private final RouteCalculator calculator;
    /**Output ListView.*/
    private final ListView outputList;
    /**Map of users(login, User).*/
    private Map<String, User> users;
    /**Current strategy in program.*/
    private RouteFindingStrategy currentStrategy = new DijkstraRouteFindingStrategy();

    /**
     * Constructor of GUI.
     */
    public GUI() {
        JsonParser parser = new JsonParser();
        parser.parse();
        calculator = new RouteCalculator(parser.getStations(), parser.getConnections());
        calculator.setRouteFindingStrategy(currentStrategy); // Выберите стратегию
        outputList = new ListView();
        outputList.setEditable(false);
        outputList.setPrefHeight(350);
        outputList.setCellFactory(cell -> new CustomListCell());
        users = new HashMap<>();
        loadUsersFromFile(); // при запуске удаляет пропуски
    }

    /**
     * Main method.
     * @param args arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Start function for JavaFX application.
     * @param primaryStage main stage
     */
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Route Finder"); // Установка заголовка окна
        primaryStage.getIcons().add(new Image("/Images/logo.png")); // Установка логотипа метро
        loadUsersFromFile(); // Загрузка пользователей из файла
        showLoginMenu(primaryStage); // Отображение меню с опциями входа и регистрации
    }

    // Вход
    /**
     * Shows main menu with options to log in.
     * @param primaryStage main stage
     */
    private void showLoginMenu(Stage primaryStage) {
        VBox vbox = new VBox(10);

        vbox.setStyle("-fx-background-color: #ffffff;");

        //картинка лого сверху
        ImageView logo = new ImageView(new Image("Images/logo.png"));
        logo.setFitWidth(200);
        logo.setFitHeight(200);
        logo.setPreserveRatio(true); // сохраняем пропорции изображения
        logo.setSmooth(true); // включаем сглаживание

        //текст к лого
        Text text = new Text("SPB Metro");
        text.setFont(Font.font("Arial", 30));
        text.setFill(Paint.valueOf("#2a4b8a"));
        text.setTextAlignment(TextAlignment.CENTER);

        Button logInAsUserBtn = new Button("Log in as user");
        logInAsUserBtn.setOnAction(e -> showLoginWindow(primaryStage, false));

        Button logInAsAdminBtn = new Button("Log in as Admin");
        logInAsAdminBtn.setOnAction(e -> showLoginWindow(primaryStage, true));

        Button logInAsGuestBtn = new Button("Log in as guest");
        logInAsGuestBtn.setOnAction(e -> {
            User guest = new Guest();
            showMainScreen(primaryStage, guest);
        });

        Button switchStrategyBtn = new Button("Switch algorithm");
        switchStrategyBtn.setOnAction(e -> showChangeAlgorithmWindow());
        HBox hBox = new HBox(switchStrategyBtn);
        hBox.setAlignment(Pos.CENTER_RIGHT);
        // Создаем VBox и размещаем HBox в нижней части
        VBox switchAlgorithmBox = new VBox();
        switchAlgorithmBox.setFillWidth(true);
        switchAlgorithmBox.getChildren().add(hBox);
        switchAlgorithmBox.setAlignment(Pos.BOTTOM_RIGHT);


        // одинаквый размер кнопок
        logInAsAdminBtn.setMinWidth(200);
        logInAsUserBtn.setMinWidth(200);
        logInAsGuestBtn.setMinWidth(200);

        vbox.getChildren().addAll(logo,text, logInAsUserBtn, logInAsGuestBtn, logInAsAdminBtn, switchAlgorithmBox);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(10));

        Scene scene = new Scene(vbox, 300, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    } // Меню выбора входа

    /**
     * Shows menu with login and password fields to log in.
     * @param primaryStage main stage
     * @param isAdmin shows if user is admin
     */
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

            if (user != null && user.getPassword().equals(password) && (isAdmin == (user instanceof Admin))) { // мб сделать, чтобы админ тоже мог заходить, как дефолтный юзер
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

        Text singInText = new Text("Don't have account?");

        VBox singIn = new VBox();
        singIn.getChildren().addAll(singInText,signInBtn);
        singIn.setAlignment(Pos.CENTER);
        singIn.setSpacing(5);

        HBox loginAndBack = new HBox();
        loginAndBack.getChildren().addAll(loginBtn, backBtn);
        loginAndBack.setSpacing(5);

        Scene scene;

        if(isAdmin == false){
            vbox.getChildren().addAll(loginField, passwordField, loginAndBack, singIn);
            vbox.setPadding(new Insets(10));
            scene = new Scene(vbox, 300, 170);
        }else{
            vbox.getChildren().addAll(loginField, passwordField, loginAndBack);
            vbox.setPadding(new Insets(10));
            scene = new Scene(vbox, 300, 120);
        }

        primaryStage.setScene(scene);
        primaryStage.show();
    } // логгин

    /**
     * Shows sing in menu to create new user.
     * @param primaryStage main stage
     */
    private void showSignInWindow(Stage primaryStage) {
        VBox vbox = new VBox(10);

        TextField loginField = new TextField();
        loginField.setPromptText("Login(min. 4 symbols)");

        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password(min. 4 symbols)");

        Button registerBtn = new Button("Register");
        registerBtn.setOnAction(e -> {
            String login = loginField.getText();
            String password = passwordField.getText();

            // проверяем условия логина и пароля
            if (login.isEmpty() || password.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Empty fields");
                alert.setContentText("Please fill in all fields.");
                alert.showAndWait();
            } else if (login.length() < 4 || password.length() < 4) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Invalid login or password");
                alert.setContentText("Login must be at least 4 characters long, and password must be at least 4 characters long.");
                alert.showAndWait();
            } else if (users.containsKey(login)) {
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
        backBtn.setOnAction(e -> showLoginWindow(primaryStage, false));

        HBox registerAndBack = new HBox();
        registerAndBack.getChildren().addAll(registerBtn,backBtn);
        registerAndBack.setSpacing(5);

        vbox.getChildren().addAll(loginField, passwordField, registerAndBack);
        vbox.setPadding(new Insets(10));

        Scene scene = new Scene(vbox, 300, 120);
        primaryStage.setScene(scene);
        primaryStage.show();
    } // регистрация

    /**
     * Shows main functionality depend on user status.
     * @param primaryStage main stage
     * @param user user class
     */
    private void showMainScreen(Stage primaryStage, User user) {
        //очистить вывод
        outputList.getItems().clear();

        // Создание кнопок и установка обработчиков событий для них
        Button findRouteBtn = new Button("Find route"); // кнопка Find Route
        findRouteBtn.setOnAction(e -> showFindRouteWindow(user)); // указываем юзера, чтобы сохранить в него пройденный путь
        findRouteBtn.setDisable(!user.canFindRoute());
        findRouteBtn.setMinWidth(130);

        Button historyBtn = new Button("Get history of routes"); // кнопка History
        historyBtn.setOnAction(e -> {
            outputList.getItems().clear();
            List<String> historyText = printHistoryOfRoutes(calculator, user.getStoredRoutes());
            ObservableList<String> items = FXCollections.observableArrayList(historyText);
            outputList.setItems(items); // берем историю из класса User и калькулятор нужен для подсчета времени
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

        Button changePasswordBtn = new Button("Change password");
        changePasswordBtn.setOnAction(e -> showChangePasswordWindow(user));

        HBox routeAndMap = new HBox();
        routeAndMap.getChildren().addAll(findRouteBtn,showImageBtn);
        routeAndMap.setSpacing(5);

        HBox adminSettingsAndStatus = new HBox();
        adminSettingsAndStatus.getChildren().addAll(adminSettingsBtn, setStatusBtn);
        adminSettingsAndStatus.setSpacing(5);

        HBox exitLogOutAndChangePassword = new HBox();
        exitLogOutAndChangePassword.getChildren().addAll(logOutBtn, exitBtn, changePasswordBtn);
        exitLogOutAndChangePassword.setSpacing(5);

        VBox vbox = new VBox();
        vbox.setSpacing(5);

        Scene scene;

        if (user instanceof Admin){
            vbox.getChildren().addAll(routeAndMap, historyBtn, outputList, adminSettingsAndStatus, exitLogOutAndChangePassword);
            vbox.setPadding(new Insets(10)); // Установка отступов
            scene = new Scene(vbox, 500, 500);
        } else if(user instanceof Guest){
            HBox logOutAndExit = new HBox();
            logOutAndExit.getChildren().addAll(logOutBtn, exitBtn);
            logOutAndExit.setSpacing(5);
            vbox.getChildren().addAll(routeAndMap, outputList, logOutAndExit);
            vbox.setPadding(new Insets(10)); // Установка отступов
            scene = new Scene(vbox, 500, 445);
        } else{
            vbox.getChildren().addAll(routeAndMap, historyBtn, outputList, exitLogOutAndChangePassword);
            vbox.setPadding(new Insets(10)); // Установка отступов
            scene = new Scene(vbox, 500, 470);
        }

        // Создание сцены и установка ее для primaryStage
        primaryStage.setScene(scene);
        primaryStage.show(); // Отображение главного окна
    } // Все остальные методы, когда уже зашел в акк

    //Работа с юзерами и файлами

    /**
     * Loads users from file at start.
     */
    private void loadUsersFromFile() {
        try {
            List<String> text = Files.lines(Paths.get("resources/users.txt"))
                    .filter(line -> !line.isBlank())
                    .collect(Collectors.toList());
            Files.write(Paths.get("resources/users.txt"), text);

            Files.lines(Paths.get("resources/users.txt"))
                    .map(line -> line.split(":"))
                    .forEach(parts -> {
                        String login = parts[0];
                        String password = parts[1];
                        boolean isAdmin = Boolean.parseBoolean(parts[2]);

                        User user = isAdmin ? new Admin(login, password) : new User(login, password);
                        users.put(login, user);
                    });
        } catch (IOException e) {
            System.err.println("Error reading users from file: " + e.getMessage());
        }
    }

    /**
     * Adds new users.
     * @param user user class
     */
    private void addUserToFile(User user) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("resources/users.txt", true))) {
            if (new File("resources/users.txt").length() != 0) {
                writer.write("\n");
            }
            writer.write(user.getLogin() + ":" + user.getPassword() + ":" + (user instanceof Admin));
        } catch (IOException e) {
            System.err.println("Error writing user to file: " + e.getMessage());
        }
    }

    /**
     * Saves all users to file.
     */
    private void saveUsersToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("resources/users.txt", false))) {
            users.values().forEach(user -> {
                try {
                    writer.write(user.getLogin() + ":" + user.getPassword() + ":" + (user instanceof Admin) + "\n");
                } catch (IOException e) {
                    System.err.println("Error writing users to file: " + e.getMessage());
                }
            });
        } catch (IOException e) {
            System.err.println("Error writing users to file: " + e.getMessage());
        }
    }

    //Методы

    /**
     * Shows admin menu(delete user's account, set admin/user).
     */
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
        VBox vbox = new VBox(5, userListView, setAdminButton, removeAdminStatusButton, removeUserButton);
        vbox.setPadding(new Insets(10));

        // Создание сцены и установка ее
        Scene scene = new Scene(vbox, 400, 300);
        setNewAdminStage.setScene(scene);
        setNewAdminStage.show(); // Отображение окна
    } // настройки админа

    /**
     * Help function to use FindRoute class.
     * @param user user class
     */
    private void showFindRouteWindow(User user) {
        FindRoute findRouteWindow = new FindRoute(calculator, outputList, user);
        findRouteWindow.show();
    } //поиск пути

    /**
     * Help function to use SetStatus class.
     */
    private void showSetStatusWindow() {
        SetStatus setStatusWindow = new SetStatus(calculator);
        setStatusWindow.show();
    } // статус станции

    /**
     * Shows metro map of Saint-Petersburg.
     */
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

    /**
     * Shows menu to change algorithm.
     */
    private void showChangeAlgorithmWindow(){
        Stage newStage = new Stage();
        String strategyName = (currentStrategy instanceof DijkstraRouteFindingStrategy) ? "Dijkstra's algorithm" : "BFS algorithm";
        Label label = new Label("Current: " + strategyName);

        // Создаем кнопку "Change"
        Button changeButton = new Button("Change");
        changeButton.setOnAction(e->{
            if (currentStrategy instanceof DijkstraRouteFindingStrategy){
                currentStrategy = new BFSRouteFindingStrategy();
                calculator.setRouteFindingStrategy(currentStrategy);
            }else{
                currentStrategy = new DijkstraRouteFindingStrategy();
                calculator.setRouteFindingStrategy(currentStrategy);
            }
            String newStrategyName = (currentStrategy instanceof DijkstraRouteFindingStrategy) ? "Dijkstra's algorithm" : "BFS algorithm";
            label.setText("Current: " + newStrategyName);
        });

        // Создаем VBox для вертикального размещения элементов
        VBox vbox = new VBox(10, label, changeButton);
        vbox.setAlignment(Pos.CENTER_LEFT); // Выравнивание элементов справа
        vbox.setPadding(new Insets(10, 20, 10, 10));
        // Создаем сцену и добавляем компоновщик VBox
        Scene newScene = new Scene(vbox, 260, 90);

        // Настраиваем новое окно и отображаем его
        newStage.setTitle("Switch algorithm");
        newStage.getIcons().add(new Image("/Images/logo.png")); // Установка логотипа метро
        newStage.setScene(newScene);
        newStage.show();
    } // смена стратегии

    /**
     * Prints history of requested routes.
     * @param calculator route calculator
     * @param storedRoutes list of requested routes
     * @return String of all stations on route.
     */
    private List<String> printHistoryOfRoutes(RouteCalculator calculator, List<List<Station>> storedRoutes) {

        List<String> output = new ArrayList<>();

        if (storedRoutes.isEmpty()) {
            output.add("No routes have been requested.");
            return output;
        }

        int costPerRoute = 70;

        output.add("History of requested routes:");

        for (int i = 0; i < storedRoutes.size(); i++) {
            List<Station> route = storedRoutes.get(i);
            int travelTime = calculator.getTravelTime(route);

            output.add("Route " + (i + 1) + ": " + route.get(0).getNameOfStation() + "-" + route.get(route.size() - 1).getNameOfStation() + " (Travel time: " + travelTime + " minutes, Cost: " + costPerRoute + " rubles):");

            int x = 0;
            for (Station station : route) {
                output.add("\t" + station);
                x++;
            }
            output.add("Total stations: " + x);
        }

        return output;
    }

    /**
     * Shows menu to change current password.
     * @param user user class
     */
    private void showChangePasswordWindow(User user) {
        Stage stage = new Stage();
        VBox vbox = new VBox(10);
        stage.setTitle("Change password");
        stage.getIcons().add(new Image("/Images/logo.png")); // Установка логотипа метро

        PasswordField currentPasswordField = new PasswordField();
        currentPasswordField.setPromptText("Current Password");

        PasswordField newPasswordField = new PasswordField();
        newPasswordField.setPromptText("New Password");

        Button changeBtn = new Button("Change Password");
        changeBtn.setOnAction(e -> {
            String currentPassword = currentPasswordField.getText();
            String newPassword = newPasswordField.getText();

            if (!user.getPassword().equals(currentPassword)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Invalid Password");
                alert.setContentText("Please enter your current password correctly.");
                alert.showAndWait();
            } else if (newPassword.length() < 4) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Invalid Password");
                alert.setContentText("Password must be at least 4 characters long.");
                alert.showAndWait();
            } else {
                String temp = user.getLogin();
                users.remove(temp);
                users.put(temp, new User(temp, newPassword));
                saveUsersToFile();
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Password Changed");
                alert.setHeaderText(null);
                alert.setContentText("Your password has been changed successfully.");
                alert.showAndWait();
                stage.close();
            }
        });

        vbox.getChildren().addAll(currentPasswordField, newPasswordField, changeBtn);
        vbox.setPadding(new Insets(10));

        Scene scene = new Scene(vbox, 300, 120);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * This class handles indents in output area.
     */
    private static class CustomListCell extends ListCell<String> {
        private final GridPane gridPane = new GridPane();
        private final Label firstLabel = new Label();
        private final Label secondLabel = new Label();

        public CustomListCell() {
            super();
            gridPane.add(firstLabel, 0, 0); // Добавляем первую метку в первый столбец
            gridPane.add(secondLabel, 1, 0); // Добавляем вторую метку во второй столбец
            GridPane.setHalignment(firstLabel, HPos.LEFT); // Выравниваем первую метку по левому краю
            GridPane.setHalignment(secondLabel, HPos.LEFT); // Выравниваем вторую метку по левому краю
            GridPane.setMargin(secondLabel, new Insets(0, 0, 0, 10)); // Добавляем отступ слева для второй метки
            firstLabel.setPrefWidth(250); // Устанавливаем предпочтительную ширину для первой метки
        }

        @Override
        protected void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty || item == null || item.trim().isEmpty()) {
                setText(null);
                setGraphic(null);
            } else {
                if (item.contains("Travel time:")) {
                    setText(item);
                    setGraphic(null);
                } else {
                    if (item.contains("|")) {
                        String[] data = item.split("\\|");
                        firstLabel.setText(data[0]);
                        secondLabel.setText(data[1]);
                    } else {
                        firstLabel.setText(item);
                        secondLabel.setText("");
                    }
                    setText(null);
                    setGraphic(gridPane);
                }
            }
        }
    }
}
