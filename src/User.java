/**
 * Class to represent User
 */
public class User {

    private String userId;
    private CuisineTracking[] cuisines;
    private CostTracking[] costBracket;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public CuisineTracking[] getCuisines() {
        return cuisines;
    }

    public void setCuisines(CuisineTracking[] cuisines) {
        this.cuisines = cuisines;
    }

    public CostTracking[] getCostBracket() {
        return costBracket;
    }

    public void setCostBracket(CostTracking[] costBracket) {
        this.costBracket = costBracket;
    }
}
