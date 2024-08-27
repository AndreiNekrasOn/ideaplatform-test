package hr.andreinekrason;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

import com.google.gson.Gson;


public class JsonAggregator {
    private static final String DEPARTURE = "Владивосток";
    private static final String ARRIVAL = "Тель-Авив";

    public static void main(String[] args) {
        List<Ticket> tickets;
        tickets = readTickets(
                JsonAggregator.class.getResourceAsStream("/tickets.json"));

        List<Ticket> filteredTickets = tickets.stream()
            .filter(t -> DEPARTURE.equals(t.getOriginName()) &&
                    ARRIVAL.equals(t.getDestinationName()))
            .collect(Collectors.toList());

        Map<String, List<Ticket>> carrierToTickets = filteredTickets.stream()
            .collect(Collectors.groupingBy(Ticket::getCarrier));

        System.out.println("Min travel time per carrier:");
        for (String carrier: carrierToTickets.keySet()) {
            long travelTime = getMinTravelTime(carrierToTickets.get(carrier));
            System.out.printf("%s: %d minutes\n", carrier, travelTime);
        }

        double meanMedianPriceDiff = getMeanMedianPriceDiff(filteredTickets);
        System.out.printf("Mean and avarage price difference: %f\n",
                meanMedianPriceDiff);
    }

    public static List<Ticket> readTickets(InputStream is) {
        StringBuilder data = new StringBuilder();
        try (Scanner scanner = new Scanner(is)) {
            while (scanner.hasNextLine()) {
                data.append(scanner.nextLine());
            }
        }
        Gson gson = new Gson();
        Tickets tickets = gson.fromJson(data.toString(), Tickets.class);
        return List.of(tickets.tickets);
    }

    public static double getMeanMedianPriceDiff(List<Ticket> tickets) {
        double mean = tickets.stream()
            .mapToInt(Ticket::getPrice)
            .average()
            .orElseGet(() -> 0);
        double median = tickets.stream()
            .mapToInt(Ticket::getPrice)
            .sorted()
            .skip(tickets.size() / 2)
            .findFirst()
            .orElseGet(() -> 0);
        return mean - median;
    }

    public static long getMinTravelTime(List<Ticket> tickets) {
        return tickets.stream()
            .mapToLong(Ticket::getTravelTime)
            .min()
            .orElseGet(() -> 0);
    }
}
