package sw614f21.p6project;
import java.io.*;
import java.util.*;

public class CSVReader {

    public static ArrayList<OccurrenceSequence> GetBenchmarkSequences() throws IOException {


        ArrayList<OccurrenceSequence> output = new ArrayList<OccurrenceSequence>();
        HashMap<Integer, OccurrenceSequence> SequenceDays = new HashMap<>();
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


        for (int i = 0; i < events.size(); i++) {
            
            BufferedReader csvReader = new BufferedReader(new FileReader("resources/ukdale/" + events.get(i) + ".csv"));
            String row;
            String symbol = events.get(i);
            SymbolOccurrence activeEvent = null;
            Date startDate = null;

            while ((row = csvReader.readLine()) != null) {

                String[] data = row.split(",");

                //HOURS SHOULD PROBABLY BE SECONDS
                Date date = new Date((int) Math.floor(Integer.parseInt(data[0]) / 86400), Integer.parseInt(data[0]) % 86400);
                if (!SequenceDays.containsKey(date.Days)) {
                    OccurrenceSequence daySequence = new OccurrenceSequence(date.Days, new ArrayList<SymbolOccurrence>());
                    SequenceDays.put(date.Days, daySequence);
                }

                int value = Integer.parseInt(data[1]);
                boolean overThreshold = value > 0;

                if (overThreshold && activeEvent == null) {
                    startDate = date;
                    activeEvent = new SymbolOccurrence(symbol, date.TimeStamp);
                } else if (!overThreshold && activeEvent != null) {
                    activeEvent.FinishingTime = 86400 * (date.Days - startDate.Days) + date.TimeStamp;
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
        return output;

    }

    public static ArrayList<OccurrenceSequence> GetOccurrenceSequences() throws IOException {

        ArrayList<OccurrenceSequence> output = new ArrayList<OccurrenceSequence>();


        BufferedReader csvReader = new BufferedReader(new FileReader("resources/house_dataset.csv"));

        String row = csvReader.readLine();
        HashMap<EventType, Date> startEndpoints = new HashMap<EventType, Date>();
        HashMap<EventType, Boolean> ongoingEvents = new HashMap<EventType, Boolean>();
        HashMap<EventType, SymbolOccurrence> startingSymbols = new HashMap<EventType, SymbolOccurrence>();
        int j = 0;
        int day = 0;

        while ((row = csvReader.readLine()) != null) {

            Date date = new Date((int)Math.floor(j / 24) + 1,j % 24);
            if (day < date.Days) {
                day = date.Days;
                OccurrenceSequence daySequence = new OccurrenceSequence(day, new ArrayList<SymbolOccurrence>());
                output.add(daySequence);

            }
            String[] data = row.split(",");
            ArrayList<Double> values = new ArrayList<Double>();
            for (int i = 1; i < data.length; i++) {

                double value = -10.0;
                if (!data[i].equals("")){
                    value = Double.parseDouble(data[i]);
                }

                EventType symbol = EventType.values()[i - 1];
                boolean overThreshold = value > symbol.eventThreshold;

                if (overThreshold && !ongoingEvents.getOrDefault(symbol, false)) {
                    ongoingEvents.put(symbol, true);
                    startEndpoints.put(symbol, date);
                    SymbolOccurrence occurrence = new SymbolOccurrence(symbol.toString(), date.TimeStamp);
                    output.get(date.Days - 1).Sequence.add(occurrence);
                    startingSymbols.put(symbol, occurrence);
                }
                else if (!overThreshold && ongoingEvents.getOrDefault(symbol, false)) {
                    Date startDate = startEndpoints.get(symbol);
                    int startRow = 24 * (startDate.Days - 1) + startDate.TimeStamp;
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
        for (int i = 0 ; i < OccurrenceSequences.size(); i++ ){
            OccurrenceSequence occurrenceSequence = OccurrenceSequences.get(i);
            ArrayList<Endpoint> endpointList = new ArrayList<Endpoint>();
            EndpointSequence endpointSequence = new EndpointSequence(i, endpointList);
            
            for (int j = 0; j < occurrenceSequence.Sequence.size(); j++){
                SymbolOccurrence SO = occurrenceSequence.Sequence.get(j);
                Endpoint startEndpoint = new Endpoint(SO.EventID, SO.StartingTime, true, j);
                Endpoint finishingEndpoint = new Endpoint(SO.EventID, SO.FinishingTime, false, j);
                endpointSequence.Sequence.add(startEndpoint);
                endpointSequence.Sequence.add(finishingEndpoint);
            }
            
            Collections.sort(endpointSequence.Sequence);
            
            output.add(endpointSequence);
            
        }
        return output;
    }

    public static void FormTuples (ArrayList<EndpointSequence> inputSequences) {

        for (int i = 0; i < inputSequences.size(); i++) {
            ArrayList<Endpoint> es = inputSequences.get(i).Sequence;
            for (int j = 1; j < es.size(); j++) {
                int k = j - 1;
                while (k >= 0 && es.get(k).Timestamp == es.get(j).Timestamp){
                    es.get(j).TupleMembers.add(es.get(k));
                    k--;
                }
            }
        }
    }
}
