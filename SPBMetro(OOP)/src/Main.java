import Calculations.*;
import Structure.*;

import java.util.List;
import java.util.Scanner;

public class Main {
    private static Scanner scanner;

    public static void main(String[] args){
        JsonParser parser = new JsonParser();
        parser.parse();
        RouteCalculator calculator = new RouteCalculator(parser.stations, parser.connections);
        scanner = new Scanner(System.in);
        for(;;) {
            System.out.println("\nMenu: " + "\n 1.Find route"
                    + "\n 2.Get history of routes"
                    + "\n 3.Set status of station");
            System.out.print("Write a number: ");
            String button = scanner.nextLine();
            System.out.println();
            if(button.equals("1")){
                System.out.print("Write your start station:");
                String start = scanner.nextLine();
                if(calculator.findStationByName(start) == null){
                    System.out.println("This station doesn't exist");
                    continue;
                }
                System.out.print("Write your final destination:");
                String end = scanner.nextLine();
                if(calculator.findStationByName(end) == null){
                    System.out.println("This station doesn't exist");
                    continue;
                }

                printRoute(calculator.findShortestRoute(start,end), calculator);
            }else if(button.equals("2")) {
                calculator.printHistoryOfRoutes();
            }else if(button.equals("3")){
                System.out.print("Write station:");
                String stationToChangeStatus = scanner.nextLine();
                if(calculator.findStationByName(stationToChangeStatus) != null){
                    System.out.println("Current status: " + calculator.findStationByName(stationToChangeStatus).isStatus());
                    System.out.print("Write new status of this station(true/false): ");
                    String newStatusOfStation = scanner.nextLine();
                    if(newStatusOfStation.equals("true")){
                        calculator.findStationByName(stationToChangeStatus).setStatus(true);
                    }else if(newStatusOfStation.equals("false")){
                        calculator.findStationByName(stationToChangeStatus).setStatus(false);
                    }else{
                        System.out.println("Incorrect input.");
                        continue;
                    }
                }

            }
        }
    }

    public static void printRoute(List<Station> route, RouteCalculator calculator) {
        int travelTime = calculator.getTravelTime(route);
        int costPerRoute = 70;
        if(route.isEmpty()){
            return;
        }
                                                //станция отправления                    //станция прибытия
        System.out.println("Shortest route: " + route.get(0).getNameOfStation() +  "-" + route.get(route.size()-1).getNameOfStation()+ "(Travel time: " + travelTime + " minutes," + " Cost: " + costPerRoute + " rubles):");
        for (Station station : route) {
            System.out.println("\t" + station);
        }

        System.out.println("Total stations: " + route.size());
    }

}
