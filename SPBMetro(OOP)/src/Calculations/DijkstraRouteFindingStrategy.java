package Calculations;

import Structure.Station;

import java.util.*;

/**
 * This class finding shortest route between two stations depending on time, using Dijkstra's Algorithm.
 */
public class DijkstraRouteFindingStrategy implements RouteFindingStrategy{
    /**List of stations (two-dimensional array).*/
    private List<List<Station>> stations; // станции (двумерное поле)
    /**List of connections (two-dimensional array).*/
    private List<List<Station>> connections; // переходы (двумерное поле)

    @Override
    public List<Station> findShortestRoute(String startName, String endName) {
        // Инициализация
        Map<Station, Integer> distances = new HashMap<>();
        Map<Station, Station> previous = new HashMap<>();
        PriorityQueue<Station> queue = new PriorityQueue<>(Comparator.comparingInt(distances::get));

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

        for (List<Station> line : stations) {
            for (Station station : line) {
                if (station.getNameOfStation().equals(startName)) {
                    distances.put(station, 0);
                } else {
                    distances.put(station, Integer.MAX_VALUE);
                }
                previous.put(station, null);
                queue.add(station);
            }
        }

        // Алгоритм Дейкстры
        while (!queue.isEmpty()) {
            Station currentStation = queue.poll();

            // Если мы нашли конечную станцию, выходим из цикла
            if (currentStation.getNameOfStation().equals(endName)) {
                break;
            }

            // Обновление расстояний для соседних станций
            for (Station neighbor : getNeighbors(currentStation)) {
                int newDistance = distances.get(currentStation) + getTravelTime(Arrays.asList(currentStation, neighbor));

                if (newDistance < distances.get(neighbor)) {
                    queue.remove(neighbor);
                    distances.put(neighbor, newDistance);
                    previous.put(neighbor, currentStation);
                    queue.add(neighbor);
                }
            }
        }

        // Восстановление маршрута
        List<Station> shortestPath = new ArrayList<>();
        Station current = findStationByName(endName);
        while (current != null) {
            shortestPath.add(current);
            current = previous.get(current);
        }
        Collections.reverse(shortestPath);
        return shortestPath;
    }

    @Override
    public void setStations(List<List<Station>> stations) {
        this.stations = stations;
    }

    @Override
    public void setConnections(List<List<Station>> connections) {
        this.connections = connections;
    }

    /**
     * Finds a metro station by its name.
     *
     * @param name the name of the station to find
     * @return the station object with the given name, or null if no station with that name was found
     */
    private Station findStationByName(String name) {
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
    private int getTravelTime(List<Station> route) {
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

            // Если это переход между станциями, добавляем 3 минуты
            if (isConnection) {
                travelTime += 3;
            }

            // Выбираем время пути в зависимости от направления
            if (currentStation.getLine() == nextStation.getLine()) {
                if (currentStation.getTravelTimeForward() == nextStation.getTravelTimeBack()) {
                    travelTime += currentStation.getTravelTimeForward();
                } else {
                    travelTime += currentStation.getTravelTimeBack();
                }
            }
        }
        return travelTime; // Возвращаем общее время путешествия
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
}
