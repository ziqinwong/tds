package tds.takehome.carmanagement.model;

public enum CarSizes {
    SMALL(1, 10),
    MEDIUM(2, 20),
    LARGE(3, 40);

    private final int id;
    private final int pricePerMinute;

    CarSizes(int id, int pricePerMinute) {
        this.id = id;
        this.pricePerMinute = pricePerMinute;
    }

    public int getPricePerMinute() {
        return pricePerMinute;
    }

    public static CarSizes fromId(int id) {
        for (CarSizes size : values()) {
            if (size.id == id) {
                return size;
            }
        }
        throw new IllegalArgumentException("Invalid vehicle type: " + id);
    }
}