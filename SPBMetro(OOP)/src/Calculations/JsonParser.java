package Calculations;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import Structure.Station;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class JsonParser {
    public List<List<Station>> stations = new ArrayList<>(); // станции(двумерной поле)
    public List<List<Station>> connections = new ArrayList<>(); // переходы(двумерное поле)

    public void parse() {
        JSONParser parser = new JSONParser();
        try (FileReader reader = new FileReader("src/Structure/MetroMap.json")) {
            Object obj = parser.parse(reader);
            JSONObject jsonObject = (JSONObject) obj;

            // Разбор станций
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

            // Разбор пересадок
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

            // Вывод результатов (закомментировано)
            // System.out.println("Stations: " + stations);
            // System.out.println("Connections: " + connections);
            // System.out.println("Lines: " + lines);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
