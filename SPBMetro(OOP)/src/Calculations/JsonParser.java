package Calculations;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import Structure.Station;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

/**
 This class is responsible for parsing the stations and connections from a Json file and saving them in lists.
 */
public class JsonParser {
    /**List of stations (two-dimensional array).*/
    public List<List<Station>> stations = new ArrayList<>(); // станции(двумерной поле)
    /**List of connections (two-dimensional array).*/
    public List<List<Station>> connections = new ArrayList<>(); // переходы(двумерное поле)

    /**
     * This method parse stations and connections from Json file and save them in lists.
     */
    public void parse() {
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader("resources/MetroMap.json")) {
            Object obj = parser.parse(reader);
            JSONObject jsonObject = (JSONObject) obj;

            // Разбор станций в отдельном потоке
            Thread parseStationsThread = new Thread(() -> {
                JSONObject stationsObj = (JSONObject) jsonObject.get("stations"); // получаем список линий 1,2,3... и станций внутри
                for (Object lineObj : stationsObj.keySet()) { // проходимся по станциям
                    int line = Integer.parseInt((String) lineObj); // записываем номер станции
                    List<Station> stationList = new ArrayList<>(); // Список станций для определенной линии
                    JSONArray stationsArray = (JSONArray) stationsObj.get(lineObj); // получаем список станций на линии
                    for (Object stationObj : stationsArray) {
                        stationList.add(new Station(stationObj.toString(), line));
                    }
                    stations.add(stationList); // Добавляем список станций линии в общий список линий
                }
            });

            // Разбор пересадок в отдельном потоке
            Thread parseConnectionsThread = new Thread(() -> {
                JSONArray connectionsArray = (JSONArray) jsonObject.get("connections");
                for (Object connectionObj : connectionsArray) {
                    JSONArray connectionArray = (JSONArray) connectionObj;
                    List<Station> connectionList = new ArrayList<>(); // Список двух станций, связанных пересадкой
                    for (Object stationObj : connectionArray) {
                        JSONObject stationJson = (JSONObject) stationObj;
                        int line = Integer.parseInt(stationJson.get("line").toString());
                        String station = (String) stationJson.get("station");
                        connectionList.add(new Station(station, line));
                    }
                    connections.add(connectionList); // Добавляем в общий список пересадок
                }
            });

            // Запуск потоков
            parseStationsThread.start();
            parseConnectionsThread.start();

            // Ожидание завершения потоков
            parseStationsThread.join();
            parseConnectionsThread.join();
        } catch (Exception e) {
            System.out.println("Parsing failed, try again.");
        }
    }
}

