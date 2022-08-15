/**
 * Author: Katrin Kahur
 * Course ID: IV1351
 * Program: TIDAB
 */

package se.kth.iv1351.soundgoodMusicSchool.integration;

import java.time.LocalDate;

/**
 * This is a DTO class for instrument rental in Soundgood Music School.
 * The class contains following data about rentals:
 * rental id, start date, end date, student id and a <code>MusicalInstrumentDTO</code> of
 * the rented instrument.
 */
public class RentalDTO {
    private String rentalId;
    private LocalDate startDate;
    private LocalDate endDate;
    private int studentId;
    private MusicalInstrumentDTO rentedInstrumentDTO;

    /**
     * The constructor, creates a new instance of <code>RentalDTO</code>
     * @param rentalId the id of the rental
     * @param startDate the start date of the rental
     * @param endDate the end date of the rental
     * @param studentId the id of the student who wishes to rent
     * @param instrumentDTO the DTO object of the rented instrument
     */
    public RentalDTO(String rentalId, LocalDate startDate, LocalDate endDate, int studentId,
                     MusicalInstrumentDTO instrumentDTO) {
        this.rentalId = rentalId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.studentId = studentId;
        this.rentedInstrumentDTO = instrumentDTO;
    }

    /**
     * @return the business rental id of this rental
     */
    public String getRentalId() {
        return rentalId;
    }

    /**
     * @return the start date of this rental
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * @return the end date of this rental
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
     * @return the <code>MusicalInstrumentDTO</code> of the rented instrument
     */
    public MusicalInstrumentDTO getInstrumentDTO() {
        return rentedInstrumentDTO;
    }
}
