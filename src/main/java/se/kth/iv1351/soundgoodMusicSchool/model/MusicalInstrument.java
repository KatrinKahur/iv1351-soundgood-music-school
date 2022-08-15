/**
 * Author: Katrin Kahur
 * Course ID: IV1351
 * Program: TIDAB
 */

package se.kth.iv1351.soundgoodMusicSchool.model;

/**
 * This class represents a musical instrument that a student can rent in Soundgood
 * Music School. The class contains methods that increment and decrement the instruments
 * stock quantity.
 */
public class MusicalInstrument {
    private String type;
    private String brand;
    private double price;
    private int stockQuantity;
    private int id;

    /**
     * Creates a new instance of <code>MusicalInstrument</code> with the specified type, brand,
     * price, quantity and surrogare id.
     * @param type the instruments type
     * @param brand the instruments brand
     * @param price tge instruments price
     * @param stockQuantity the instruments quantity
     * @param id the instruments surrogate id
     */
    public MusicalInstrument(String type, String brand, double price, int stockQuantity, int id){
        this.type = type;
        this.brand = brand;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.id = id; // surrogate id
    }

    /**
     * This method decrements stock quantity by 1.
     * @throws MusicalInstrumentException if the user tries to decrement quantity that is 0
     */
    public void decrementStockQuantity() throws MusicalInstrumentException {
        if(this.stockQuantity == 0)
            throw new MusicalInstrumentException("Failed to decrement stock quantity. Negative quantity not allowed. ");
        this.stockQuantity = this.stockQuantity - 1;
    }

    /**
     * This method increments stock quantity by 1.
     */
    public void incrementStockQuantity(){
        this.stockQuantity = this.stockQuantity + 1;
    }

    /**
     * A getter function
     * @return the instruments stock quantity
     */
    public int getStockQuantity(){
        return stockQuantity;
    }

    /**
     * A getter function
     * @return the instruments price
     */
    public double getPrice(){
        return price;
    }

    /**
     * A getter function
     * @return the instruments brand
     */
    public String getBrand(){
        return brand;
    }

    /**
     * A getter function
     * @return the instruments type
     */
    public String getType(){
        return type;
    }

    /**
     * A getter function
     * @return the instruments surrogate id
     */
    public int getId(){
        return id;
    }
}
