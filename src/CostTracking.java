/**
 * Class to represent Cost Tracking
 */
public class CostTracking {
    private String type;
    private int noOfOrders;

    public CostTracking(String type, int noOfOrders) {
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