package com.foodrush.food_ordering_system.enums;

public enum CuisineType {
    ITALIAN("Italian"),
    CHINESE("Chinese"),
    INDIAN("Indian"),
    MEXICAN("Mexican"),
    THAI("Thai"),
    JAPANESE("Japanese"),
    AMERICAN("American"),
    FRENCH("French"),
    MEDITERRANEAN("Mediterranean"),
    KOREAN("Korean"),
    VIETNAMESE("Vietnamese"),
    CONTINENTAL("Continental"),
    FAST_FOOD("Fast Food"),
    SEAFOOD("Seafood"),
    VEGETARIAN("Vegetarian"),
    VEGAN("Vegan"),
    DESSERTS("Desserts"),
    BEVERAGES("Beverages"),
    OTHER("Other");

    private final String displayName;

    CuisineType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }                                                                                                                              
}                                                                                                                                   