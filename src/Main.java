import java.util.Arrays;
import java.util.Date;


public class Main {
    public static void main(String[] args) {

        User user = new User();
        user.setUserId("1");
        CuisineTracking[] cuisines = new CuisineTracking[]{
                new CuisineTracking("SouthIndian", 7),
                new CuisineTracking("NorthIndian", 8),
                new CuisineTracking("Chinese", 5)
        };
        user.setCuisines(cuisines);

        CostTracking[] costBracket = new CostTracking[]{
                new CostTracking("2", 3),
                new CostTracking("3", 7),
                new CostTracking("4", 5)
        };
        user.setCostBracket(costBracket);

        Restaurant[] availableRestaurants = new Restaurant[]{
                new Restaurant("1", Cuisine.SouthIndian, 2, 4.5f, true, new Date(System.currentTimeMillis() - 24 * 3600 * 1000)),
                new Restaurant("2", Cuisine.NorthIndian, 3, 4.2f, true, new Date(System.currentTimeMillis() - 48 * 3600 * 1000)),
                new Restaurant("3", Cuisine.Chinese, 4, 4.7f, false, new Date(System.currentTimeMillis() - 48 * 3600 * 1000)),
                new Restaurant("4", Cuisine.SouthIndian, 4, 3.5f, false, new Date(System.currentTimeMillis() - 48 * 3600 * 1000)),
                new Restaurant("5", Cuisine.NorthIndian, 5, 4.9f, true, new Date(System.currentTimeMillis() - 24 * 3600 * 1000)),
                new Restaurant("6", Cuisine.SouthIndian, 5, 3.0f, false, new Date(System.currentTimeMillis() - 48 * 3600 * 1000)),
                new Restaurant("7", Cuisine.Chinese, 2, 4.0f, false, new Date(System.currentTimeMillis() - 48 * 3600 * 1000))
        };

        String[] restaurantIds = RestaurantRecommendation.getRestaurantRecommendations(user, availableRestaurants);
        System.out.println(Arrays.toString(restaurantIds));

    }
}
