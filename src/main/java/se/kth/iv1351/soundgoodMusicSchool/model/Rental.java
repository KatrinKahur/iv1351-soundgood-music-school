/**
 * Author: Katrin Kahur
 * Course ID: IV1351
 * Program: TIDAB
 */

package se.kth.iv1351.soundgoodMusicSchool.model;

import se.kth.iv1351.soundgoodMusicSchool.integration.MusicalInstrumentDTO;
import java.time.LocalDate;

/**
 * This class represents an instrument rental in Soundgood Music School.
 */
public class Rental {
    private String rentalId;
    private LocalDate startDate;
    private LocalDate endDate;
    private int studentId;
    private MusicalInstrument rentedInstrument;

    private static String RENTAL_ID_PREFIX = "RENTAL-";

    /**
     * The constructor, creates a new instance of <code>Rental</code> with the specified student id, rented instrument
     * and rental period. The constructor calculates the end time and generates a new (business) rental id.
     * @param rentalPeriodInMonths the specified rental period length in months
     * @param studentId student id of the rentee
     * @param rentedInstrument the rented instrument
     * @param latestRentalId the (business) id of the latest rental, needed to generate a new rental id
     */
    public Rental(int rentalPeriodInMonths, int studentId, MusicalInstrument rentedInstrument, String latestRentalId){
        this.startDate = LocalDate.now();
        this.endDate = calculateRentEndDate(rentalPeriodInMonths);
        this.rentalId = generateRentalId(latestRentalId);
        this.studentId = studentId;
        this.rentedInstrument = rentedInstrument;
    }

    /**
     * @return this rentals business id
     */
    public String getRentalId() {
        return rentalId;
    }

    /**
     * @return this rentals start date
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * @return this rentals end date
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * @return the id of the student who wishes to rent
     */
    public int getStudentId() {
        return studentId;
    }

    /**
     * @return the DTO of the rented instrument
     */
    public MusicalInstrumentDTO getRentedInstrument() {
        return new MusicalInstrumentDTO(rentedInstrument.getType(), rentedInstrument.getBrand(),
                rentedInstrument.getPrice(), rentedInstrument.getStockQuantity(), rentedInstrument.getId());
    }

    private LocalDate calculateRentEndDate(int rentalPeriodInMonths){
        return startDate.plusMonths(rentalPeriodInMonths);
    }

    private String generateRentalId(String latestRentalId){
        String extractedNumbers = extractNumbersFromRentalId(latestRentalId);
        String incrementedId = incrementId(removeLeadingZeros(extractedNumbers));
        return RENTAL_ID_PREFIX + addZeros(incrementedId);
    }

    private String extractNumbersFromRentalId(String id){
        String[] splitId = id.split("-");
        return splitId[1];
    }

    private String removeLeadingZeros(String value) {
        return value.replaceAll("^0+", "").trim();
    }

    private String incrementId(String id){
        int parsedId = Integer.parseInt(id);
        int incrementedId = parsedId + 1;
        return String.valueOf(incrementedId);
    }

    private String addZeros(String id){
        int nrOfZerosToAdd = 4 - id.length();

        if(nrOfZerosToAdd == 0)
            return id;

        String zeros = "";
        for (int i = 0; i < nrOfZerosToAdd; i++)
            zeros = zeros + "0";
        return zeros + id;
    }
}
