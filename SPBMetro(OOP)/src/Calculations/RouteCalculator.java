package Calculations;

import Structure.*;
import java.util.*;

/**
 * This class represents a calculator for finding the shortest route between two metro stations.
 */
public class RouteCalculator{
    /**List of stations (two-dimensional array).*/
    private List<List<Station>> stations; // станции (двумерное поле)
    /**List of connections (two-dimensional array).*/
    private List<List<Station>> connections; // переходы (двумерное поле)
    private RouteFindingStrategy RouteFindingStrategy;

    public RouteCalculator(List<List<Station>> stations, List<List<Station>> connections){
        this.stations = stations;
        this.connections = connections;
    }

    public List<Station> findShortestRoute(String start, String end) {
        RouteFindingStrategy.setConnections(connections);
        RouteFindingStrategy.setStations(stations);
        return RouteFindingStrategy.findShortestRoute(start,end);
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

    public void setRouteFindingStrategy(RouteFindingStrategy pathFindingStrategy) {
        this.RouteFindingStrategy = pathFindingStrategy;
    }

}