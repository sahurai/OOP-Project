package Calculations;

import Structure.Station;

import java.util.List;

/**
 * This class represents Strategy pattern.
 */
interface RouteFindingStrategy {
    List<Station> findShortestRoute(String start, String end);
    void setStations(List<List<Station>> stations);
    void setConnections(List<List<Station>> connections);
}