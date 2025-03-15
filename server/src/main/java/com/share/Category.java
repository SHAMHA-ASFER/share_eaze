package com.share;

public enum Category {
    ELECTRONICS("Electronics"),
    HOUSEHOLDITEMS("House Hold Items"),
    SPORTSEQUIPMENTS("Sports Equipments"),
    TOOLS("Tools"),
    BOOKSANDMEDIAS("Book And Media"),
    OUTDOORGEARS("Outdoor Gears"),
    MUSICALINSTRUMENTS("Musical Instruments"),

    UNKNOWNCATEGORY("Unknown Category");

    private final String category;
    private Category(String str) {
        this.category = str;
    }

    public String getCategory() {
        return category;
    }

    public static Category fromString(String str) {
        for (Category cat: Category.values()) {
            if (cat.category.equalsIgnoreCase(str)) {
                return cat;
            }
        }
        return UNKNOWNCATEGORY; 
    }
}
