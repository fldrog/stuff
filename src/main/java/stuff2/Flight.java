package stuff2;

import java.io.Serializable;

public class Flight implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    String dateTaken;
    String date;
    String hour;
    String departure;
    String arrival;
    String price;
    String flihtNo;

    public Flight(String dateTake, String date, String hour, String departure, String arrival, String price,
            String flihtNo) {
        super();
        this.date = date;
        this.hour = hour;
        this.departure = departure;
        this.arrival = arrival;
        this.price = price;
        this.flihtNo = flihtNo;
    }

}
