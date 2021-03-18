package sw614f21.p6project;
import jdk.jfr.Event;

import java.io.*;
import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;

public class CSVReader {

    public static ArrayList<OccurrenceSequence> GetOccurrenceSequences() throws IOException {

        FileInputStream in = null;
        ArrayList<OccurrenceSequence> output = new ArrayList<OccurrenceSequence>();

/*        try {
            in = new FileInputStream("house_dataset.csv");


        }
        finally {
            if (in != null){
                in.close();
            }
        }*/

        BufferedReader csvReader = new BufferedReader(new FileReader("resources/house_dataset.csv"));
        String row = csvReader.readLine();
        HashMap<EventType, Integer> startEndpoints = new HashMap<EventType, Integer>();
        HashMap<EventType, Boolean> ongoingEvents = new HashMap<EventType, Boolean>();

        while ((row = csvReader.readLine()) != null) {

            String[] data = row.split(",");
            ArrayList<Double> values = new ArrayList<Double>();
            for (int i = 1; i < data.length; i++) {
                double value = 0.0;

                if (!data[i].equals("")){
                    value = Double.parseDouble(data[i]);
                }

                EventType symbol = EventType.values()[i - 1];
                boolean overThreshold = value > symbol.eventThreshold;

                if (overThreshold && ongoingEvents.getOrDefault(symbol, false)) {

                }
                else if (overThreshold && !ongoingEvents.getOrDefault(symbol, false)) {
                    ongoingEvents.put(symbol, true);
                    startEndpoints.put(symbol, i - 1);
                }

            }



        }

        csvReader.close();

        return null;
    }
}
