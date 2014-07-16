package stuff2;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RouteData implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    String departure;
    String arrival;
    String dateTaken;
    List<Flight> flights = new ArrayList<Flight>();

    public RouteData(String departure, String arrival, String dateTaken) {
        this.departure = departure;
        this.arrival = arrival;
        this.dateTaken = dateTaken;
    }

    public void addFlight(Flight flight) {
        flights.add(flight);
    }

}
