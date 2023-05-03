package Calculations;

import Structure.Station;

import java.util.List;

/**
 * This class represents Strategy pattern.
 */
public interface RouteFindingStrategy {
    /**
     * This function responsible for creating method to find shortest path.
     * @param start Name of start station.
     * @param end Name of end station station.
     * @return List of stations in route.
     */
    List<Station> findShortestRoute(String start, String end);

    /**
     * This function helps set stations.
     * @param stations List of all stations.
     */
    void setStations(List<List<Station>> stations);

    /**
     * This function helps set connections.
     * @param connections List of all connections.
     */
    void setConnections(List<List<Station>> connections);
}