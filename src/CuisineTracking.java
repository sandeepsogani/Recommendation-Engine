/**
 * Class to represent Cuisine Tracking
 */
public class CuisineTracking {
    private String type;
    private int noOfOrders;

    public CuisineTracking(String type, int noOfOrders) {
        this.type = type;
        this.noOfOrders = noOfOrders;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getNoOfOrders() {
        return noOfOrders;
    }

    public void setNoOfOrders(int noOfOrders) {
        this.noOfOrders = noOfOrders;
    }
}