import java.util.*;
import java.util.function.Predicate;

import static java.util.stream.Collectors.toList;

public class RestaurantRecommendation {

    private RestaurantRecommendation() {
        throw new IllegalArgumentException("Not implemented");
    }

    private static final int MAX_RECOMMENDATIONS = 100;
    private static final int MAX_NEWLY_CREATED_RECOMMENDATIONS = 4;
    private static final long MS_PER_DAY = 24 * 60 * 60 * 1000L;
    private static final String IS_RECOMMENDED = "isRecommended";
    private static final String IS_PRIMARY_CUISINE = "isPrimaryCuisine";
    private static final String IS_SECONDARY_CUISINE = "isSecondaryCuisine";
    private static final String IS_PRIMARY_COST_BRACKET = "isPrimaryCostBracket";
    private static final String IS_SECONDARY_COST_BRACKET = "isSecondaryCostBracket";

    private static Map<String,Map<String, Predicate<Restaurant>>> userPredicates = new HashMap<>();

    /**
     * Function to get restaurant recommendations based on user and available restaurants
     *
     * @param user                 User
     * @param availableRestaurants array of available restaurants
     * @return array of restaurant Ids
     */

    public static String[] getRestaurantRecommendations(User user, Restaurant[] availableRestaurants) {

        List<Restaurant> recommendations = getRecommendedRestaurantList(user, availableRestaurants);
        List<String> restaurantIds = new ArrayList<>(getRestaurantIds(recommendations, MAX_RECOMMENDATIONS));

        if (restaurantIds.size() < MAX_RECOMMENDATIONS)
            restaurantIds.addAll(getRemainingRestaurantIds(availableRestaurants, restaurantIds));
        return restaurantIds.toArray(new String[0]);
    }

    /**
     * Function to get restaurant recommendations based on user and available restaurants
     *
     * @param user                 User
     * @param availableRestaurants array of available restaurants
     * @return array of restaurant objects
     */
    private static List<Restaurant> getRecommendedRestaurantList(User user, Restaurant[] availableRestaurants) {
        List<Restaurant> restaurants = new ArrayList<>();
        Map<String, Predicate<Restaurant>> predicateMap = getPredicateMap(user);
        userPredicates.put(user.getUserId(), predicateMap);

        List<Restaurant> featuredRestaurants = getFeaturedRestaurants(user, availableRestaurants);
        List<Restaurant> topRatedRestaurants = getTopRatedRestaurants(user,availableRestaurants);
        List<Restaurant> newlyCreatedRestaurants = getNewlyCreatedRestaurants(user,availableRestaurants);
        List<Restaurant> lowerRatedRestaurants = getLowerRatedRestaurants(user,availableRestaurants);

        restaurants.addAll(featuredRestaurants);
        restaurants.addAll(topRatedRestaurants);
        restaurants.addAll(newlyCreatedRestaurants);
        restaurants.addAll(lowerRatedRestaurants);
        return restaurants;
    }

    /**
     * Function to get Featured Restaurants recommendations based on user and available restaurants
     *
     * @param user                 User
     * @param availableRestaurants array of available restaurants
     * @return array of get Featured Restaurants objects
     */
    private static List<Restaurant> getFeaturedRestaurants(User user, Restaurant[] availableRestaurants) {
        List<Restaurant> featuredRestaurants = new ArrayList<>();
        Map<String, Predicate<Restaurant>> predicateMap = userPredicates.get(user.getUserId()) != null ? userPredicates.get(user.getUserId()) : getPredicateMap(user);
        Arrays.stream(availableRestaurants)
                .filter(predicateMap.get(IS_RECOMMENDED).and(predicateMap.get(IS_PRIMARY_CUISINE)).and(predicateMap.get(IS_PRIMARY_COST_BRACKET)))
                .forEach(restaurant -> featuredRestaurants.add(restaurant));

        if (featuredRestaurants.isEmpty()) {
            Arrays.stream(availableRestaurants)
                    .filter(predicateMap.get(IS_RECOMMENDED).and(predicateMap.get(IS_PRIMARY_CUISINE)).and(predicateMap.get(IS_SECONDARY_COST_BRACKET)))
                    .forEach(restaurant -> featuredRestaurants.add(restaurant));
            Arrays.stream(availableRestaurants)
                    .filter(predicateMap.get(IS_RECOMMENDED).and(predicateMap.get(IS_SECONDARY_CUISINE)).and(predicateMap.get(IS_PRIMARY_COST_BRACKET)))
                    .forEach(restaurant -> featuredRestaurants.add(restaurant));
        }
        return featuredRestaurants.subList(0, Math.min(featuredRestaurants.size(), MAX_RECOMMENDATIONS));
    }

    /**
     * Function to get Top Rated Restaurants recommendations based on user and available restaurants
     *
     * @param user                 User
     * @param availableRestaurants array of available restaurants
     * @return array of get Top Rated Restaurants objects
     */
    private static List<Restaurant> getTopRatedRestaurants(User user, Restaurant[] availableRestaurants) {
        List<Restaurant> topRatedRestaurants = new ArrayList<>();
        Map<String, Predicate<Restaurant>> predicateMap = userPredicates.get(user.getUserId()) != null ? userPredicates.get(user.getUserId()) : getPredicateMap(user);
        // add all restaurants of primary cuisine, primary cost bracket with rating >= 4
        Arrays.stream(availableRestaurants)
                .filter(predicateMap.get(IS_PRIMARY_CUISINE).and(predicateMap.get(IS_PRIMARY_COST_BRACKET))
                        .and(restaurant -> restaurant.getRating() >= 4))
                .forEach(restaurant -> topRatedRestaurants.add(restaurant));

        // add all restaurants of primary cuisine, secondary cost bracket with rating >= 4.5
        Arrays.stream(availableRestaurants)
                .filter(predicateMap.get(IS_PRIMARY_CUISINE).and(predicateMap.get(IS_SECONDARY_COST_BRACKET))
                        .and(restaurant -> restaurant.getRating() >= 4.5))
                .forEach(restaurant -> topRatedRestaurants.add(restaurant));

        // add all restaurants of secondary cuisine, primary cost bracket with rating >= 4.5
        Arrays.stream(availableRestaurants)
                .filter(predicateMap.get(IS_SECONDARY_CUISINE).and(predicateMap.get(IS_PRIMARY_COST_BRACKET))
                        .and(restaurant -> restaurant.getRating() >= 4.5))
                .forEach(restaurant -> topRatedRestaurants.add(restaurant));

        return topRatedRestaurants.subList(0, Math.min(topRatedRestaurants.size(), MAX_RECOMMENDATIONS));
    }

    /**
     * Function to get Newly Created Restaurants recommendations based on user and available restaurants
     *
     * @param user                 User
     * @param availableRestaurants array of available restaurants
     * @return array of get newly created Restaurants objects
     */
    private static List<Restaurant> getNewlyCreatedRestaurants(User user, Restaurant[] availableRestaurants) {
        List<Restaurant> newlyCreatedRestaurants = new ArrayList<>();
        // add MAX_NEWLY_CREATED_RECOMMENDATIONS newly created restaurants by rating
        // criteria for new == OnboardedTime less than 48 hours
        Arrays.sort(availableRestaurants, Comparator.comparing(Restaurant::getRating).reversed());

        int count = 0;
        for (Restaurant restaurant : availableRestaurants) {
            if (restaurant.getOnboardedTime().after(new Date(System.currentTimeMillis() - 2 * MS_PER_DAY))) {
                newlyCreatedRestaurants.add(restaurant);
                count++;
                if (count == MAX_NEWLY_CREATED_RECOMMENDATIONS) {
                    break;
                }
            }
        }
        return newlyCreatedRestaurants;
    }

    /**
     * Function to get Lower Rated Restaurants recommendations based on user and available restaurants
     *
     * @param user                 User
     * @param availableRestaurants array of available restaurants
     * @return array of get Lower Rated Restaurants objects
     */
    private static List<Restaurant> getLowerRatedRestaurants(User user, Restaurant[] availableRestaurants) {
        List<Restaurant> lowerRatedRestaurants = new ArrayList<>();
        Map<String, Predicate<Restaurant>> predicateMap = userPredicates.get(user.getUserId()) != null ? userPredicates.get(user.getUserId()) : getPredicateMap(user);
        // add all restaurants of primary cuisine, primary cost bracket with rating < 4
        Arrays.stream(availableRestaurants)
                .filter(predicateMap.get(IS_PRIMARY_CUISINE).and(predicateMap.get(IS_PRIMARY_COST_BRACKET))
                        .and(restaurant -> restaurant.getRating() < 4))
                .forEach(restaurant -> lowerRatedRestaurants.add(restaurant));


        // add all restaurants of primary cuisine, secondary cost bracket with rating < 4.5
        Arrays.stream(availableRestaurants)
                .filter(predicateMap.get(IS_PRIMARY_CUISINE).and(predicateMap.get(IS_SECONDARY_COST_BRACKET))
                        .and(restaurant -> restaurant.getRating() < 4.5))
                .forEach(restaurant -> lowerRatedRestaurants.add(restaurant));


        // add all restaurants of secondary cuisine, primary cost bracket with rating < 4.5
        Arrays.stream(availableRestaurants)
                .filter(predicateMap.get(IS_SECONDARY_CUISINE).and(predicateMap.get(IS_PRIMARY_COST_BRACKET))
                        .and(restaurant -> restaurant.getRating() < 4.5))
                .forEach(restaurant -> lowerRatedRestaurants.add(restaurant));

        return lowerRatedRestaurants.subList(0, Math.min(lowerRatedRestaurants.size(), MAX_RECOMMENDATIONS));
    }

    /**
     * Function to get Remaining unfiltered Restaurants recommendations based on user and available restaurants
     *
     * @param availableRestaurants array of available restaurants
     * @param addedRestaurantIds array of already added restaurants
     * @return array of get remaining unfiltered Restaurants objects
     */
    private static List<String> getRemainingRestaurantIds(Restaurant[] availableRestaurants, List<String> addedRestaurantIds) {

        HashSet<String> addedRestaurantIdsSet = new HashSet<>();
        addedRestaurantIdsSet.addAll(addedRestaurantIds);
        List<String> remainingRestaurantIds = new ArrayList<>();
        int count = addedRestaurantIds.size();

        // add all restaurants of any cuisine, any cost bracket
        for (Restaurant restaurant : availableRestaurants) {
            if (!addedRestaurantIdsSet.contains(restaurant.getRestaurantId())) {
                remainingRestaurantIds.add(restaurant.getRestaurantId());
                count++;
                if (count == MAX_RECOMMENDATIONS)
                    break;
            }
        }
        return remainingRestaurantIds;
    }

    /**
     * Function to get Remaining unfiltered Restaurants recommendations based on user and available restaurants
     *
     * @param restaurants array of recommended restaurants
     * @param count integer number to limit number of recommendations
     * @return array of restaurants ids
     */
    private static List<String> getRestaurantIds(List<Restaurant> restaurants, int count) {
        return restaurants.stream().distinct().limit(count).map(Restaurant::getRestaurantId).collect(toList());
    }

    /**
     * Function to get Remaining unfiltered Restaurants recommendations based on user and available restaurants
     *
     * @param user object to create conditions for filter
     * @return map of userId and Predicate for filtering
     */
    private static Map<String, Predicate<Restaurant>> getPredicateMap(User user) {
        // sort cuisines and costBracket based on the noOfOrders
        Arrays.sort(user.getCuisines(), Comparator.comparing(CuisineTracking::getNoOfOrders));
        Arrays.sort(user.getCostBracket(), Comparator.comparing(CostTracking::getNoOfOrders));

        Cuisine primaryCuisine = Cuisine.valueOf(user.getCuisines()[0].getType());
        Cuisine secondaryCuisine = user.getCuisines().length > 1 ? Cuisine.valueOf(user.getCuisines()[1].getType()) : null;

        int primaryCostBracket = Integer.parseInt(user.getCostBracket()[0].getType());
        int secondaryCostBracket = user.getCostBracket().length > 1 ? Integer.parseInt(user.getCostBracket()[1].getType()) : 0;

        Predicate<Restaurant> isRecommended = i -> i.isRecommended();
        Predicate<Restaurant> isPrimaryCuisine = i -> i.getCuisine() == primaryCuisine;
        Predicate<Restaurant> isSecondaryCuisine = i -> i.getCuisine() == secondaryCuisine;
        Predicate<Restaurant> isPrimaryCostBracket = i -> i.getCostBracket() == primaryCostBracket;
        Predicate<Restaurant> isSecondaryCostBracket = i -> i.getCostBracket() == secondaryCostBracket;

        Map<String, Predicate<Restaurant>> predicateMap = new HashMap<>();
        predicateMap.put(IS_RECOMMENDED, isRecommended);
        predicateMap.put(IS_PRIMARY_CUISINE, isPrimaryCuisine);
        predicateMap.put(IS_SECONDARY_CUISINE, isSecondaryCuisine);
        predicateMap.put(IS_PRIMARY_COST_BRACKET, isPrimaryCostBracket);
        predicateMap.put(IS_SECONDARY_COST_BRACKET, isSecondaryCostBracket);
        return predicateMap;
    }

}