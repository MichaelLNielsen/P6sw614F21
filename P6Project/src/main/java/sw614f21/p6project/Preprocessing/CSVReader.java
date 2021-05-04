package sw614f21.p6project.Preprocessing;
import sw614f21.p6project.DataStructures.*;

import java.io.*;
import java.util.*;

public class CSVReader {

    public static ArrayList<OccurrenceSequence> GetBenchmarkSequences() throws IOException {


        ArrayList<OccurrenceSequence> output = new ArrayList<OccurrenceSequence>();
        HashMap<Integer, OccurrenceSequence> SequenceDays = new HashMap<>();
        ArrayList<ArrayList<String>> houses = new ArrayList<>();
        houses.add(GetHouse1Files());
        houses.add(GetHouse2Files());

        // Iterate over each house to be processed.
        for (int m = 0; m < houses.size(); m++){
            ArrayList<String> events = houses.get(m);

            // Iterate over each file for the current house.
            for (int i = 0; i < events.size(); i++) {

                BufferedReader csvReader = new BufferedReader(new FileReader("resources/ukdale/" + events.get(i) + ".csv"));
                String row;
                String symbol = events.get(i);
                SymbolOccurrence activeEvent = null;
                Date startDate = null;

                while ((row = csvReader.readLine()) != null) {

                    String[] data = row.split(",");

                    // Calculate the day number (offset from the Unix Epoch) and the within-day time in seconds.
                    Date date = new Date((int) Math.floor(Integer.parseInt(data[0]) / 86400), Integer.parseInt(data[0]) % 86400);
                    // Create a new occurrence sequence for the day if it doesn't already exist.
                    if (!SequenceDays.containsKey(date.Days)) {
                        OccurrenceSequence daySequence = new OccurrenceSequence(date.Days, new ArrayList<SymbolOccurrence>());
                        SequenceDays.put(date.Days, daySequence);
                    }

                    int value = Integer.parseInt(data[1]);
                    boolean overThreshold = value > 0;

                    // Case where inactive event becomes active:
                    if (overThreshold && activeEvent == null) {
                        startDate = date;
                        activeEvent = new SymbolOccurrence(symbol, date.TimeStamp);
                    }
                    // Case where ongoing event becomes inactive.
                    else if (!overThreshold && activeEvent != null) {
                        // Set Finishing time to the number of days passed since startDate.Days and add the winthin-day offset.
                        activeEvent.FinishingTime = 86400 * (date.Days - startDate.Days) + date.TimeStamp;

                        // Add the finished event to its corresponding occurrence sequence and reset loop variables.
                        SequenceDays.get(startDate.Days).Sequence.add(activeEvent);
                        activeEvent = null;
                        startDate = null;
                    }
                }

                csvReader.close();
            }

            ArrayList<Integer> keyset = new ArrayList<Integer>(SequenceDays.keySet());
            for (int i = 0; i < keyset.size(); i++) {
                output.add(SequenceDays.get(keyset.get(i)));
            }
            
            SequenceDays = new HashMap<>();
        }
        return output;

    }

    public static ArrayList<String> GetHouse1Files(){
        ArrayList<String> events = new ArrayList<String>();
        events.add("ChildLamp");
        events.add("HairDryer");
        events.add("Kettle");
        events.add("KitchenLamp");
        events.add("LEDPrinter");
        events.add("LivingRoomLampTv");
        events.add("Microwave");
        events.add("TVdata");
        events.add("CoffeeMachine");
        events.add("Dishwasher");
        events.add("SubwooferLivingRoom");
        return events;
    }
    public static ArrayList<String> GetHouse2Files(){ 
        ArrayList<String> events = new ArrayList<String>();
        events.add("KettleHus2");
        events.add("MonitorHus2");
        events.add("RiceCookerHus2");
        events.add("RunningMachineHus2");
        events.add("SpeakersHus2");
        events.add("ToasterHus2");
        events.add("MicrowaveHus2");
        events.add("PlaystationHus2");
        events.add("WashingMachineHus2");
        return events;
    }
    
    
    public static ArrayList<OccurrenceSequence> GetOccurrenceSequences() throws IOException {

        ArrayList<OccurrenceSequence> output = new ArrayList<OccurrenceSequence>();


        BufferedReader csvReader = new BufferedReader(new FileReader("resources/house_dataset.csv"));

        // Read the header to skip it.
        String row = csvReader.readLine();

        // Hashmaps to keep track of events during processing.
        HashMap<EventType, Date> startEndpoints = new HashMap<EventType, Date>();
        HashMap<EventType, Boolean> ongoingEvents = new HashMap<EventType, Boolean>();
        HashMap<EventType, SymbolOccurrence> startingSymbols = new HashMap<EventType, SymbolOccurrence>();
        int j = 0;
        int day = 0;

        while ((row = csvReader.readLine()) != null) {

            // Calculate the day nr. in the year along with the hour.
            Date date = new Date((int)Math.floor(j / 24) + 1,j % 24);

            // If a new day has been reached, a new occurrence sequence for the day is created.
            if (day < date.Days) {
                day = date.Days;
                OccurrenceSequence daySequence = new OccurrenceSequence(day, new ArrayList<SymbolOccurrence>());
                output.add(daySequence);

            }
            String[] data = row.split(",");
            ArrayList<Double> values = new ArrayList<Double>();

            // Iterate over each value in the column.
            for (int i = 1; i < data.length; i++) {

                // If non-empty the value is parsed to double.
                double value = -10.0;
                if (!data[i].equals("")){
                    value = Double.parseDouble(data[i]);
                }

                // Implicit correspondence between EventType enumeration and column number is used here
                // to get the enum value and threshold for the event being active.
                EventType symbol = EventType.values()[i - 1];
                boolean overThreshold = value > symbol.eventThreshold;

                // Case where the event isn't ongoing, but is now active.
                if (overThreshold && !ongoingEvents.getOrDefault(symbol, false)) {
                    ongoingEvents.put(symbol, true);
                    startEndpoints.put(symbol, date);
                    SymbolOccurrence occurrence = new SymbolOccurrence(symbol.toString(), date.TimeStamp);
                    // Add the occurrence to day of the corresponding occurrence sequence.
                    output.get(date.Days - 1).Sequence.add(occurrence);
                    startingSymbols.put(symbol, occurrence);
                }
                // Case where the event is ongoing, but no longer active.
                else if (!overThreshold && ongoingEvents.getOrDefault(symbol, false)) {
                    // Get the start date of the corresponding ongoing event type.
                    Date startDate = startEndpoints.get(symbol);

                    // Retrieve the row number of the corresponding event onset.
                    int startRow = 24 * (startDate.Days - 1) + startDate.TimeStamp;

                    // Set finishing time of the occurrence in the corresponding sequence.
                    int finishingTime = j - startRow + startDate.TimeStamp;
                    startingSymbols.get(symbol).FinishingTime = finishingTime;
                    startingSymbols.remove(symbol);
                    ongoingEvents.remove(symbol);
                    startEndpoints.remove(symbol);
                }

            }

            j++;

        }

        csvReader.close();

        return output;
    }
    
    
    public static ArrayList<EndpointSequence> GetEndpointSequences (ArrayList<OccurrenceSequence> OccurrenceSequences){
        
        ArrayList<EndpointSequence> output = new ArrayList<EndpointSequence>();

        // Go through each occurrence sequence.
        for (int i = 0 ; i < OccurrenceSequences.size(); i++ ){
            OccurrenceSequence occurrenceSequence = OccurrenceSequences.get(i);
            ArrayList<Endpoint> endpointList = new ArrayList<Endpoint>();
            EndpointSequence endpointSequence = new EndpointSequence(i, endpointList);

            // Go through each endpoint in the sequence, and split it into a starting and finishing endpoint.
            for (int j = 0; j < occurrenceSequence.Sequence.size(); j++){
                SymbolOccurrence SO = occurrenceSequence.Sequence.get(j);
                Endpoint startEndpoint = new Endpoint(SO.EventID, SO.StartingTime, true, j);
                Endpoint finishingEndpoint = new Endpoint(SO.EventID, SO.FinishingTime, false, j);
                endpointSequence.Sequence.add(startEndpoint);
                endpointSequence.Sequence.add(finishingEndpoint);
            }

            // Sort endpoint sequence based on timestamp etc.
            Collections.sort(endpointSequence.Sequence);
            
            output.add(endpointSequence);
            
        }
        return output;
    }


    public static void FormTuples (ArrayList<EndpointSequence> inputSequences) {

        // Iterate over each endpoint sequence.
        for (int i = 0; i < inputSequences.size(); i++) {
            ArrayList<Endpoint> es = inputSequences.get(i).Sequence;

            // Iterate over each endpoint in the sequence.
            for (int j = 1; j < es.size(); j++) {
                int k = j - 1;
                // Append previous endpoints with same timestamp to the TupleMembers list.
                while (k >= 0 && es.get(k).Timestamp == es.get(j).Timestamp){
                    es.get(j).TupleMembers.add(es.get(k));
                    k--;
                }
            }
        }
    }
}
