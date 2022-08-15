/*
 * Author: Katrin Kahur
 * Course ID: IV1351
 * Program: TIDAB
 * ****************************************************************************
 * The MIT License (MIT)
 * Copyright (c) 2020 Leif Lindbäck
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction,including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so,subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */

package se.kth.iv1351.soundgoodMusicSchool.controller;

import se.kth.iv1351.soundgoodMusicSchool.integration.MusicSchoolDAO;
import se.kth.iv1351.soundgoodMusicSchool.integration.MusicSchoolDBException;
import se.kth.iv1351.soundgoodMusicSchool.integration.MusicalInstrumentDTO;
import se.kth.iv1351.soundgoodMusicSchool.integration.RentalDTO;
import se.kth.iv1351.soundgoodMusicSchool.integration.StudentDTO;
import se.kth.iv1351.soundgoodMusicSchool.model.MusicalInstrument;
import se.kth.iv1351.soundgoodMusicSchool.model.MusicalInstrumentException;
import se.kth.iv1351.soundgoodMusicSchool.model.Rental;
import java.time.LocalDate;
import java.util.List;

/**
 * The applications controller
 */
public class Controller {
    private final MusicSchoolDAO musicSchoolDb;
    private StudentDTO student;
    private MusicalInstrument rentedInstrument;

    /**
     * The constructor, creates a new instance of <code>Controller</code>
     * @throws MusicSchoolDBException in case the application fails to connect to the database
     */
    public Controller() throws MusicSchoolDBException {
        musicSchoolDb = new MusicSchoolDAO();
    }

    /**
     * This method lists all available musical instruments to rent of the specified type from Soundgood
     * Music School
     * @param type the specified type of the instrument
     * @return a <code>List</code> with <code>MusicalInstrumentDTO</code> of the specified type where
     * the stock quantity is over 0.
     * @throws MusicSchoolDBException is thrown in case the application fails to find the instruments
     * from the database.
     */
    public List<MusicalInstrumentDTO> listMusicalInstrumentsOfCertainType(String type) throws MusicSchoolDBException {
        return musicSchoolDb.getMusicalInstrumentsToRentOfCertainType(type);
    }

    /**
     * This method logs in the student by fetching his/her data from the database
     * @param personNumber the person number of the student who needs to be registered
     */
    public StudentDTO logInStudent(String personNumber) throws MusicSchoolDBException {
        student = musicSchoolDb.findStudentByPersonNumber(personNumber);
        return student;
    }

    /**
     * This method logs out the student by clearing all the variables related to that student.
     */
    public void logOutStudent(){
        student = null;
        rentedInstrument = null;
    }
    /**
     * This method gets number of ongoing rentals for the specified student.
     * @return the number of ongoing rentals for the specified student
     * @throws MusicSchoolDBException in case the retrieve of the renrals from the database fails
     */
    public int getNrOfRentals() throws MusicSchoolDBException {
       return musicSchoolDb.getNrOfOngoingRentalsForSpecifiedStudent(student.getId());
    }

    /**
     * This method executes the necessary operations that are needed to rent an instrument
     * @param type the type of the instrument that the user wishes to rent
     * @param brand the brand of the instrument the user wishes to rent
     * @param rentalPeriodLengthInMonths the specified length of the rental period
     * @return <code>RentalDTO</code> with the data of the newly created rental
     * @throws OperationFailedException in case the rental process fails
     * @throws MusicSchoolDBException in case the rental process fails
     */
    public RentalDTO rentInstrument(String type, String brand, int rentalPeriodLengthInMonths)
            throws OperationFailedException, MusicSchoolDBException {
        String errorMessage = "Could not rent the instrument.";

        if(type == null || brand == null || (rentalPeriodLengthInMonths <= 0 || rentalPeriodLengthInMonths > 12))
            throw new OperationFailedException(errorMessage);
        MusicalInstrumentDTO instrumentDTO = musicSchoolDb.findAndLockInstrumentByTypeAndBrand(type, brand);

        if(instrumentDTO == null) {
            musicSchoolDb.rollback();
            throw new OperationFailedException(errorMessage);
        }

        rentedInstrument = new MusicalInstrument(instrumentDTO.getType(), instrumentDTO.getBrand(),
                instrumentDTO.getPrice(), instrumentDTO.getStockQuantity(), instrumentDTO.getId());
        try {
            rentedInstrument.decrementStockQuantity();
        } catch (MusicalInstrumentException exc) {
            musicSchoolDb.rollback();
            throw new OperationFailedException(errorMessage, exc);
        }

        String latestRentalId = musicSchoolDb.getLatestRentalId();
        if(latestRentalId == null) {
            musicSchoolDb.rollback();
            throw new OperationFailedException(errorMessage);
        }

        Rental newRental = new Rental(rentalPeriodLengthInMonths, student.getId(), rentedInstrument, latestRentalId);
        RentalDTO rentalDTO = new RentalDTO(newRental.getRentalId(), newRental.getStartDate(),
                newRental.getEndDate(), newRental.getStudentId(), newRental.getRentedInstrument());
        musicSchoolDb.rentInstrument(rentalDTO);
        return rentalDTO;
    }

    /**
     * This method fetches all ongoing rentals for the student.
     * @return A list with ongoing rentals for the specified student
     */
    public List<RentalDTO> fetchAllOngoingRentals() throws MusicSchoolDBException{
        return musicSchoolDb.findOngoingRentalsForSpecifiedStudent(student.getId());
    }

    /**
     * This method terminates the specified rental
     * @param rentalId the id of the rental to be terminated
     * @throws OperationFailedException in case the process of terminating the specified rental fails
     * @throws MusicSchoolDBException in case the process of terminatin the specified rental fails
     */
    public void terminateRental(String rentalId) throws OperationFailedException, MusicSchoolDBException {
        String errorMessage = "Could not terminate the rental.";
        if(rentalId == null)
            throw new OperationFailedException(errorMessage, null);

        RentalDTO rental = musicSchoolDb.findRentalById(rentalId);
        MusicalInstrumentDTO instrumentDTO = musicSchoolDb.findAndLockInstrumentById(rental.getInstrumentDTO().getId());
        MusicalInstrument instrument = new MusicalInstrument(instrumentDTO.getType(), instrumentDTO.getBrand(),
                instrumentDTO.getPrice(), instrumentDTO.getStockQuantity(), instrumentDTO.getId());
        instrument.incrementStockQuantity();
        musicSchoolDb.terminateRental(rentalId, instrument.getId(), instrument.getStockQuantity());
    }
}
