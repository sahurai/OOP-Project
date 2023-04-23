package Calculations;

import Structure.*;
import java.util.*;

/**
 * This class represents a calculator for finding the shortest route between two metro stations.
 */
public class RouteCalculator {
    /**List of stations (two-dimensional array).*/
    private List<List<Station>> stations; // станции (двумерное поле)
    /**List of connections (two-dimensional array).*/
    private List<List<Station>> connections; // переходы (двумерное поле)
    /**List of stored routes (two-dimensional array).*/
    private List<List<Station>> storedRoutes = new ArrayList<>(); // Хранение маршрутов

    /**
     * Constructor for creating an instance of the calculator.
     *
     * @param stations    list of stations
     * @param connections list of connections
     */
    public RouteCalculator(List<List<Station>> stations, List<List<Station>> connections){
        this.stations = stations;
        this.connections = connections;
    }

    /**
     * Help function to use calculateRoute
     *
     * @param startName the name of the starting station
     * @param endName   the name of the ending station
     * @return a list of stations representing the shortest route between the starting and ending stations
     */
    public List<Station> findShortestRoute(String startName, String endName) {
        List<Station> route = calculateRoute(startName, endName);
        if(!route.isEmpty()){
            storedRoutes.add(route); // Добавляем маршрут до листа
        }
        return route;
    }

    /**
     * Finds a metro station by its name.
     *
     * @param name the name of the station to find
     * @return the station object with the given name, or null if no station with that name was found
     */
    public Station findStationByName(String name) {
        // Перебираем все линии и станции на каждой линии
        for (List<Station> line : stations) {
            for (Station station : line) {
                // Если название станции совпадает с искомым, возвращаем эту станцию
                if (station.getNameOfStation().equals(name)) {
                    return station;
                }
            }
        }
        // Возвращаем null, если станция с указанным названием не найдена
        return null;
    }

    /**
     * Calculates the travel time for a given route.
     *
     * @param route the route for which to calculate the travel time
     * @return the total travel time for the given route
     */
    public int getTravelTime(List<Station> route) {
        int travelTime = 0; // Общее время путешествия
        for (int i = 0; i < route.size() - 1; i++) {
            Station currentStation = route.get(i); // Текущая станция
            Station nextStation = route.get(i + 1); // Следующая станция

            // Проверяем, является ли переход между текущей и следующей станциями
            boolean isConnection = false;
            for (List<Station> connection : connections) {
                if (connection.contains(currentStation) && connection.contains(nextStation)) {
                    isConnection = true;
                    break;
                }
            }

            // Если это переход между станциями, добавляем 3 минуты, иначе добавляем 2 минуты
            travelTime += isConnection ? 4 : 2;
        }
        return travelTime; // Возвращаем общее время путешествия
    }

    /**
     * Prints the history of requested routes.
     */
    public void printHistoryOfRoutes() {
        if (storedRoutes.isEmpty()) {
            System.out.println("No routes have been requested.");
            return;
        }

        int costPerRoute = 70;

        System.out.println("History of requested routes:");
        for (int i = 0; i < storedRoutes.size(); i++) {
            int x = 0;
            List<Station> route = storedRoutes.get(i);
            int travelTime = getTravelTime(route);
            System.out.println("Route " + (i+1) + ": " + storedRoutes.get(i).get(0).getNameOfStation() +  "-" + storedRoutes.get(i).get(storedRoutes.get(i).size()-1).getNameOfStation() + " (Travel time: " + travelTime + " minutes, Cost: " + costPerRoute + " rubles):");
            for (Station station : route) {
                System.out.println("\t" + station);
                x++;
            }
            System.out.println("Total stations: " + x);
        }

    }

    /**
     * Returns stored routes.
     */
    public List<List<Station>> getStoredRoutes() {
        return storedRoutes;
    }

    /**
     * Calculates shortest route between two stations using BFS method.
     *
     * @param startName start station
     * @param endName end station
     * @return list of all stations in route
     */
    private List<Station> calculateRoute(String startName, String endName) { // (findShortestRoute)Находит кратчайший маршрут между двумя станциями метро по их названиям.
        Station start = findStationByName(startName); // Находим станции по их названиям
        Station end = findStationByName(endName); // Находим станции по их названиям

        // Проверяем, что обе станции найдены
        if (start == null || end == null) {
            System.out.println("Start or end station not found.");
            return new ArrayList<>();
        }

        // Проверяем открыта ли станция прибытия или отбытия
        if (!end.isStatus()) {
            System.out.println("This station: " + end.getNameOfStation() + " is closed! You can't get there.");
            return new ArrayList<>();
        }else if(!start.isStatus()){
            System.out.println("This station: " + start.getNameOfStation() + " is closed! You can't start from here.");
            return new ArrayList<>();
        }

        // Используем алгоритм поиска в ширину для нахождения кратчайшего маршрута(BFS)
        Map<Station, Station> previousStationMap = new HashMap<>();
        Set<Station> visitedStations = new HashSet<>();
        Queue<Station> queue = new LinkedList<>();

        queue.offer(start);

        while (!queue.isEmpty()) {
            Station current = queue.poll();

            // Если текущая станция является конечной, прерываем цикл
            if (current.equals(end)) {
                break;
            }

            // Получаем соседние станции и добавляем их в очередь, если они еще не посещены
            List<Station> neighbors = getNeighbors(current);
            for (Station neighbor : neighbors) {
                if (!visitedStations.contains(neighbor)) {
                    queue.offer(neighbor);
                    visitedStations.add(neighbor);
                    previousStationMap.put(neighbor, current);
                }
            }
        }
        // Восстанавливаем маршрут с помощью карты предыдущих станций
        return reconstructRoute(previousStationMap, start, end);
    }

    /**
     * Returns a list of neighboring stations for a given station, including stations on the same line and stations connected by transfers.
     * @param station the station for which to find neighbors
     * @return a list of neighboring stations
     */
    private List<Station> getNeighbors(Station station) {
        List<Station> neighbors = new ArrayList<>(); // Список соседних станций

        // Станции на той же линии
        List<Station> lineStations = stations.get(station.getLine() - 1);
        int stationIndex = lineStations.indexOf(station); // Индекс текущей станции в списке станций линии
        if (stationIndex > 0) {
            neighbors.add(lineStations.get(stationIndex - 1)); // Добавляем станцию слева от текущей, если она существует
        }
        if (stationIndex < lineStations.size() - 1) {
            neighbors.add(lineStations.get(stationIndex + 1)); // Добавляем станцию справа от текущей, если она существует
        }

        // Станции, связанные пересадками
        for (List<Station> connection : connections) {
            if (connection.contains(station)) {
                for (Station s : connection) {
                    if (!s.equals(station)) {
                        neighbors.add(s); // Добавляем соседнюю станцию, если она связана пересадкой с текущей станцией
                    }
                }
            }
        }

        return neighbors; // Возвращаем список соседних станций
    }

    /**
     * Reconstructs the shortest path between two stations based on the previously calculated map of previous stations.
     * @param previousStationMap a map of stations visited during the search and their previous stations
     * @param start the starting station of the path
     * @param end the ending station of the path
     * @return a list of stations representing the shortest path from start to end
     */
    private List<Station> reconstructRoute(Map<Station, Station> previousStationMap, Station start, Station end) {
        List<Station> route = new ArrayList<>();

        Station current = end;
        // Добавляем станции в маршрут в обратном порядке, начиная с конечной станции
        while (current != null && !current.equals(start)) {
            route.add(current);
            current = previousStationMap.get(current);
        }

        // Добавляем начальную станцию в маршрут
        if (current != null) {
            route.add(start);
        }

        // Разворачиваем маршрут, чтобы он шел от начальной до конечной станции
        Collections.reverse(route);

        return route;
    }
}