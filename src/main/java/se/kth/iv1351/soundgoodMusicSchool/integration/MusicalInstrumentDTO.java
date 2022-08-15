/**
 * Author: Katrin Kahur
 * Course ID: IV1351
 * Program: TIDAB
 */
package se.kth.iv1351.soundgoodMusicSchool.integration;

/**
 * This class represents a musical instrument to rent in Soundgood Music School.
 * A musical instrument has a type, a brand and a monthly rental fee (price).
 */
public class MusicalInstrumentDTO {
    private final String type;
    private final String brand;
    private final double price;
    private final int stockQuantity;
    private final int id;

    /**
     * The constructor, creates a new instance of the class with the
     * specified type, brand, price and quantity.
     * @param brand The brand of the instrument
     * @param price The monthly rental fee of the instrument
     * @param price The monthly rental fee of the instrument
     * @param stockQuantity The stock quantity of the instrument
     */
    public MusicalInstrumentDTO(String type, String brand, double price, int stockQuantity){
        this(type, brand, price, stockQuantity, -1);
    }

    /**
     * The constructor, creates a new instance of the class with the
     * specified type, brand, price, quantity and id.
     * @param type The type of the specified instrument
     * @param brand The brand of the instrument
     * @param price The monthly rental fee of the instrument
     * @param stockQuantity The stock quantity of the specified instrument
     * @param id The surrogate id of the instrument
     */
    public MusicalInstrumentDTO(String type, String brand, double price, int stockQuantity, int id){
        this.type = type;
        this.brand = brand;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.id = id;
    }

    /**
     * This method is a string representation of <code>MusicalInstrument</code>
     * @return String representation of <code>MusicalInstrument</code> displaying
     * its type, brand and price.
     */
    @Override
    public String toString(){
        return "Instrument type: " + type + "\n" +
                "Brand: " + brand + "\n" +
                "Price: " + price + " SEK\n" +
                "Quantity: " + stockQuantity + "\n";
    }

    /**
     * @return musical instrument type
     */
    public String getType() {
        return type;
    }

    /**
     * @return musical instrument brand
     */
    public String getBrand() {
        return brand;
    }

    /**
     * @return musical instrument price
     */
    public double getPrice() {
        return price;
    }

    /**
     * @return stock quantity of the instrument
     */
    public int getStockQuantity() { return stockQuantity; }

    /**
     * @return surrogate id of the instrument
     */
    public int getId() { return id; }
}
