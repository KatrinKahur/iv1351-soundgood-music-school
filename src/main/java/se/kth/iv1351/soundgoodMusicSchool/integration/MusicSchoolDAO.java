/*
 * Author: Katrin Kahur
 * Course ID: IV1351
 * Program: TIDAB
 * ****************************************************************************
 * This file is heavily influenced by Leif Lindbäck's BankDAO.java:
 * https://github.com/KTH-IV1351/jdbc-bank/blob/master/src/main/java/se/kth/iv1351/bankjdbc/integration/BankDAO.java
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

package se.kth.iv1351.soundgoodMusicSchool.integration;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is a DAO (Data Access Object) class for Soundgood Music School.
 * It contains methods that allow the user to read, update or create new rows in
 * the Soundgood Music School Database.
 */
public class MusicSchoolDAO {
    private static final String SURROGATE_KEY = "id";

    private static final String STUDENT_TABLE_NAME = "student";
    private static final String STUDENT_PK_COLUMN_NAME = SURROGATE_KEY;
    private static final String PERSON_TABLE_NAME = "person";
    private static final String PERSON_PK_COLUMN_NAME = SURROGATE_KEY;
    private static final String PERSON_FIRST_NAME_COLUMN_NAME = "first_name";
    private static final String PERSON_LAST_NAME_COLUMN_NAME = "last_name";
    private static final String PERSON_FK_COLUMN_NAME = "person_id";
    private static final String PERSON_NUMBER_COLUMN_NAME = "person_number";
    private static final String STUDENT_FK_COLUMN_NAME = "student_id";
    private static final String INSTRUMENT_TYPE_TABLE_NAME = "musical_instrument";
    private static final String INSTRUMENT_TYPE_PK_COLUMN_NAME = SURROGATE_KEY;
    private static final String INSTRUMENT_TO_RENT_TABLE_NAME = "musical_instrument_to_rent";
    private static final String INSTRUMENT_TO_RENT_PK_COLUMN_NAME = SURROGATE_KEY;
    private static final String INSTRUMENT_TO_RENT_TYPE_COLUMN_NAME = "type";
    private static final String INSTRUMENT_TO_RENT_BRAND_COLUMN_NAME = "brand";
    private static final String INSTRUMENT_TO_RENT_PRICE_COLUMN_NAME = "monthly_rental_fee";
    private static final String INSTRUMENT_TYPE_FK_COLUMN_NAME = "instrument_type_id";
    private static final String INSTRUMENT_TO_RENT_QUANTITY_COLUMN_NAME = "stock_quantity";
    private static final String INSTRUMENT_TO_RENT_FK_COLUMN_NAME = "instrument_id";
    private static final String RENTAL_TABLE_NAME = "instrument_rental";
    private static final String RENTAL_RENTAL_ID_COLUMN_NAME = "rental_id";
    private static final String RENTAL_START_DATE_COLUMN_NAME = "start_date";
    private static final String RENTAL_END_DATE_COLUMN_NAME = "end_date";
    private static final String RENTAL_PK_COLUMN_NAME = SURROGATE_KEY;
    private static final String NUMBER_OF_RENTALS_COLUMN_NAME = "number_of_rentals";
    private static final String CURRENT_DATE_FUNCTION = "CURRENT_DATE";
    private static final String RENTAL_WITH_INSTRUMENT_TABLE_NAME = "rental_with_instrument";

    private Connection connection;
    private PreparedStatement findInstrumentsByTypeStmt;
    private PreparedStatement findStudentByPersonNumberStmt;
    private PreparedStatement getNrOfOngoingRentalsForSpecifiedStudentStmt;
    private PreparedStatement findAndLockInstrumentByTypeAndBrandStmt;
    private PreparedStatement updateInstrumentQuantityStmt;
    private PreparedStatement createNewRentalStmt;
    private PreparedStatement findLatestRentalStmt;
    private PreparedStatement findOngoingRentalsForSpecifiedStudentStmt;
    private PreparedStatement findRentalByIdStmt;
    private PreparedStatement updateRentalEndDataStmt;
    private PreparedStatement findAndLockInstrumentByIdStmt;


    /**
     * The constructor, connects to the database and prepares SQL query statements that are used
     * when calling the database.
     * @throws MusicSchoolDBException In case the program fails to connect to the database.
     */
    public MusicSchoolDAO() throws MusicSchoolDBException {
        try {
            connectToMusicSchoolDB();
            prepareStatements();
        } catch (SQLException e) {
            throw new MusicSchoolDBException("Connection to the database failed.", e);
        }
    }

    /**
     * This method gets all musical instruments of a specified type that are available for rent
     * @param type The specified musical instrument type, such as guitar, saxophone etc
     * @return A list of <code>MusicalInstrumentDTO</code> of a certain type that are available for rent
     * @throws MusicSchoolDBException In case the search for instruments in the database fails.
     */
    public List<MusicalInstrumentDTO> getMusicalInstrumentsToRentOfCertainType(String type)
            throws MusicSchoolDBException {
        ResultSet result = null;
        String errorMessage = "Search for the specified instruments failed.";
        List<MusicalInstrumentDTO> instruments = new ArrayList<>();

        try {
            findInstrumentsByTypeStmt.setString(1, type);
            result = findInstrumentsByTypeStmt.executeQuery();
            while(result.next()){
                instruments.add(new MusicalInstrumentDTO(type,
                        result.getString(INSTRUMENT_TO_RENT_BRAND_COLUMN_NAME),
                        result.getDouble(INSTRUMENT_TO_RENT_PRICE_COLUMN_NAME),
                        result.getInt(INSTRUMENT_TO_RENT_QUANTITY_COLUMN_NAME)));
            }
            connection.commit();
        } catch (SQLException exc) {
            handleException(errorMessage, exc);
        } finally {
            closeResultSet(result);
        }
        return instruments;
    }

    /**
     * This method uses student person number to find a student from the database.
     * @param personNumber the person number of the specified student
     * @return <code>StudentDTO</code> with the data of the found student
     * @throws MusicSchoolDBException in case the searching process fails
     */
    public StudentDTO findStudentByPersonNumber(String personNumber)
            throws MusicSchoolDBException {
        ResultSet result = null;
        String errorMessage = "Search for the specified student failed.";
        StudentDTO foundStudent = null;

        try {
            findStudentByPersonNumberStmt.setString(1, personNumber);
            result = findStudentByPersonNumberStmt.executeQuery();
            while(result.next()){
                foundStudent = new StudentDTO(result.getString(PERSON_FIRST_NAME_COLUMN_NAME),
                        result.getString(PERSON_LAST_NAME_COLUMN_NAME),
                        result.getString(PERSON_NUMBER_COLUMN_NAME),
                        result.getInt(STUDENT_PK_COLUMN_NAME));
            }
            connection.commit();
        } catch (SQLException exc) {
            handleException(errorMessage, exc);
        } finally {
            closeResultSet(result);
        }
        return foundStudent;
    }

    /**
     * This method gets the number of ongoing rentals for the specified student.
     * @param studentId the specified student whose rentals are retrieved
     * @return the number of ongoing rentals for the specified student
     * @throws MusicSchoolDBException in case the process of retrieving the data from the database fails
     */
    public int getNrOfOngoingRentalsForSpecifiedStudent(int studentId)
            throws MusicSchoolDBException {
        ResultSet result = null;
        String errorMessage = "The process of getting the number of rentals failed.";
        int numberOfRentals = 0;

        try {
            getNrOfOngoingRentalsForSpecifiedStudentStmt.setInt(1, studentId);
            result = getNrOfOngoingRentalsForSpecifiedStudentStmt.executeQuery();
            while(result.next()){
                numberOfRentals = result.getInt(NUMBER_OF_RENTALS_COLUMN_NAME);
            }
            connection.commit();
        } catch (SQLException exc) {
            handleException(errorMessage, exc);
        } finally {
            closeResultSet(result);
        }
        return numberOfRentals;
    }

    /**
     * This method searches for musical instrument to rent by type and brand and sets an
     * exclusive lock on the selected row.
     * @param type the specified type of the instrument
     * @param brand the specified brand of the instrument
     * @return <code>MusicalInstrumentDTO</code> object with the found instrument
     * @throws MusicSchoolDBException In case the search process fails
     */
    public MusicalInstrumentDTO findAndLockInstrumentByTypeAndBrand(String type, String brand)
            throws MusicSchoolDBException {
        ResultSet result = null;
        String errorMessage = "The process of finding the instrument failed.";
        MusicalInstrumentDTO foundInstrument = null;

        try {
            findAndLockInstrumentByTypeAndBrandStmt.setString(1, type);
            findAndLockInstrumentByTypeAndBrandStmt.setString(2, brand);
            result = findAndLockInstrumentByTypeAndBrandStmt.executeQuery();
            while(result.next()){
                foundInstrument = new MusicalInstrumentDTO(type, brand,
                        result.getDouble(INSTRUMENT_TO_RENT_PRICE_COLUMN_NAME),
                        result.getInt(INSTRUMENT_TO_RENT_QUANTITY_COLUMN_NAME),
                        result.getInt(INSTRUMENT_TO_RENT_PK_COLUMN_NAME));
            }
        } catch (SQLException exc) {
            handleException(errorMessage, exc);
        } finally {
            closeResultSet(result);
        }
        return foundInstrument;
    }

    /**
     * This method searches for musical instrument to rent by its id. The method also
     * sets an exclusive lock on the selected row in the database.
     * @param id the specified id the instrument
     * @return <code>MusicalInstrumentDTO</code> with the found instrument
     * @throws MusicSchoolDBException In case the search process fails
     */
    public MusicalInstrumentDTO findAndLockInstrumentById(int id)
            throws MusicSchoolDBException {
        ResultSet result = null;
        String errorMessage = "The process of finding the instrument failed.";
        MusicalInstrumentDTO foundInstrument = null;

        try {
            findAndLockInstrumentByIdStmt.setInt(1, id);
            result = findAndLockInstrumentByIdStmt.executeQuery();
            while(result.next()){
                foundInstrument = new MusicalInstrumentDTO(result.getString(INSTRUMENT_TO_RENT_TYPE_COLUMN_NAME),
                        result.getString(INSTRUMENT_TO_RENT_BRAND_COLUMN_NAME),
                        result.getDouble(INSTRUMENT_TO_RENT_PRICE_COLUMN_NAME),
                        result.getInt(INSTRUMENT_TO_RENT_QUANTITY_COLUMN_NAME),
                        result.getInt(INSTRUMENT_TO_RENT_PK_COLUMN_NAME));
            }
        } catch (SQLException exc) {
            handleException(errorMessage, exc);
        } finally {
            closeResultSet(result);
        }
        return foundInstrument;
    }

    /**
     * This method gets the most recent rental id
     * @return the most resent rental id
     * @throws MusicSchoolDBException In case the retrieval of the rental id from the database fails
     */
    public String getLatestRentalId() throws MusicSchoolDBException {
        ResultSet result = null;
        String errorMessage = "Failed to get the latest rental id.";
        String rentalId = "";
        try {
            result = findLatestRentalStmt.executeQuery();
            while (result.next()){
                rentalId = result.getString(RENTAL_RENTAL_ID_COLUMN_NAME);
            }
            connection.commit();
        } catch (SQLException exc) {
            handleException(errorMessage, exc);
        } finally {
            closeResultSet(result);
        }
        return rentalId;
    }

    /**
     * This method finds all active rentals for the specified student
     * @param studentId the student id whose rentals are to be retrieved
     * @return a list with <code>RentalDTO</code>
     * @throws MusicSchoolDBException in case the retrieval process fails
     */
    public List<RentalDTO> findOngoingRentalsForSpecifiedStudent(int studentId)
            throws MusicSchoolDBException{
        ResultSet result = null;
        String errorMessage = "The process of finding rentals failed.";
        List<RentalDTO> rentals = new ArrayList<>();
        try {
            findOngoingRentalsForSpecifiedStudentStmt.setInt(1, studentId);
            result = findOngoingRentalsForSpecifiedStudentStmt.executeQuery();
            while(result.next()){
                MusicalInstrumentDTO instrumentDTO = new MusicalInstrumentDTO(
                        result.getString(INSTRUMENT_TO_RENT_TYPE_COLUMN_NAME),
                        result.getString(INSTRUMENT_TO_RENT_BRAND_COLUMN_NAME),
                        result.getDouble(INSTRUMENT_TO_RENT_PRICE_COLUMN_NAME),
                        result.getInt(INSTRUMENT_TO_RENT_QUANTITY_COLUMN_NAME),
                        result.getInt(INSTRUMENT_TO_RENT_FK_COLUMN_NAME));
                rentals.add(new RentalDTO(result.getString(RENTAL_RENTAL_ID_COLUMN_NAME),
                        Date.valueOf(result.getObject(RENTAL_START_DATE_COLUMN_NAME).toString()).toLocalDate(),
                        Date.valueOf(result.getObject(RENTAL_END_DATE_COLUMN_NAME).toString()).toLocalDate(),
                        result.getInt(STUDENT_FK_COLUMN_NAME),
                        instrumentDTO));
            }
            connection.commit();
        } catch (SQLException e) {
            handleException(errorMessage, e);
        } finally {
            closeResultSet(result);
        }
        return rentals;
    }

    /**
     * This function searches for rental by its id.
     * @param rentalId the id of the rental to be searched
     * @return the <code>RentalDTO</code> with the info about the found rental
     * @throws MusicSchoolDBException in case the search process fails
     */
    public RentalDTO findRentalById (String rentalId) throws MusicSchoolDBException {
        ResultSet result = null;
        String errorMessage = "The process of finding the rental by its id failed.";
        RentalDTO rental = null;
        try {
            findRentalByIdStmt.setString(1, rentalId);
            result = findRentalByIdStmt.executeQuery();
            while(result.next()){
                MusicalInstrumentDTO instrumentDTO = new MusicalInstrumentDTO(
                        result.getString(INSTRUMENT_TO_RENT_TYPE_COLUMN_NAME),
                        result.getString(INSTRUMENT_TO_RENT_BRAND_COLUMN_NAME),
                        result.getDouble(INSTRUMENT_TO_RENT_PRICE_COLUMN_NAME),
                        result.getInt(INSTRUMENT_TO_RENT_QUANTITY_COLUMN_NAME),
                        result.getInt(INSTRUMENT_TO_RENT_FK_COLUMN_NAME));
                rental = new RentalDTO(result.getString(RENTAL_RENTAL_ID_COLUMN_NAME),
                        Date.valueOf(result.getDate(RENTAL_START_DATE_COLUMN_NAME).toString()).toLocalDate(),
                        Date.valueOf(result.getDate(RENTAL_END_DATE_COLUMN_NAME).toString()).toLocalDate(),
                        result.getInt(STUDENT_FK_COLUMN_NAME),
                        instrumentDTO);
            }
            connection.commit();
        } catch (SQLException e) {
            handleException(errorMessage, e);
        } finally {
            closeResultSet(result);
        }
        return rental;
    }

    /**
     * This method makes the necessary database calls to terminate an ongoing rental.
     * @param rentalId The business id of the rental to be terminated
     * @param instrumentId The id of the instrument in the rental
     * @param instrumentQuantity The incremented quantity of the instrument in the rental that needs
     *                           to be entered to the database after the rental has been terminated
     * @throws MusicSchoolDBException In case the application fails to terminate the rental
     */
    public void terminateRental(String rentalId, int instrumentId, int instrumentQuantity)
            throws MusicSchoolDBException {
        String errorMessage = "Couldn't terminate rental.";
        int updatedRows;
        try {
            updateRentalEndDataStmt.setObject(1, LocalDate.now());
            updateRentalEndDataStmt.setString(2, rentalId);
            updatedRows = updateRentalEndDataStmt.executeUpdate();
            if(updatedRows != 1)
                handleException(errorMessage, null);
            updateInstrumentQuantityStmt.setInt(1, instrumentQuantity);
            updateInstrumentQuantityStmt.setInt(2, instrumentId);
            updatedRows = updateInstrumentQuantityStmt.executeUpdate();
            if(updatedRows != 1)
                handleException(errorMessage, null);
            connection.commit();
        } catch (SQLException sqle) {
            handleException(errorMessage, sqle);
        }
    }

    /**
     * This method makes the necessary database calls to rent an instrument.
     * @param rentalDTO The DTO object with all the necessary data about the new rental
     * @throws MusicSchoolDBException In case the process of renting the instrument fails
     */
    public void rentInstrument(RentalDTO rentalDTO) throws MusicSchoolDBException {
        String errorMessage = "The process of renting an instrument failed.";
        int updatedRows;
        try {
            createNewRentalStmt.setString(1, rentalDTO.getRentalId());
            createNewRentalStmt.setObject(2, rentalDTO.getStartDate());
            createNewRentalStmt.setObject(3, rentalDTO.getEndDate());
            createNewRentalStmt.setInt(4, rentalDTO.getStudentId());
            createNewRentalStmt.setInt(5, rentalDTO.getInstrumentDTO().getId());
            updatedRows = createNewRentalStmt.executeUpdate();
            if(updatedRows != 1)
                handleException(errorMessage, null);
            updateInstrumentQuantityStmt.setInt(1, rentalDTO.getInstrumentDTO().getStockQuantity());
            updateInstrumentQuantityStmt.setInt(2, rentalDTO.getInstrumentDTO().getId());
            updatedRows = updateInstrumentQuantityStmt.executeUpdate();
            if(updatedRows != 1)
                handleException(errorMessage, null);
            connection.commit();
        } catch (SQLException exc) {
            handleException(errorMessage, exc);
        }
    }

    /**
     * This method rolls back the ongoing transaction.
     * @throws MusicSchoolDBException in case the rollback fails
     */
    public void rollback() throws MusicSchoolDBException {
        String errorMessage = "Transaction rollback failed.";
        try {
            connection.rollback();
        } catch (SQLException exc) {
            throw new MusicSchoolDBException(errorMessage, exc);
        }
    }

    private void handleException(String errorMessage, Exception cause)
            throws MusicSchoolDBException {
        String completeErrorMessage = errorMessage;
        String rollbackErrorMsg = " Transaction rollback failed. ";
        try {
            connection.rollback();
        } catch (SQLException sqle) {
            completeErrorMessage = completeErrorMessage + rollbackErrorMsg + sqle.getMessage();
        }

        if(cause == null)
            throw new MusicSchoolDBException(completeErrorMessage);
        else
            throw new MusicSchoolDBException(completeErrorMessage, cause);
    }

    private void connectToMusicSchoolDB() throws SQLException {
        connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/sgms_db",
                "katrin_sgms",
                "katrin_sgms_password");
        connection.setAutoCommit(false);
    }

    private void prepareStatements() throws SQLException {
        findInstrumentsByTypeStmt = connection.prepareStatement("SELECT " + INSTRUMENT_TO_RENT_TYPE_COLUMN_NAME + ", " +
                INSTRUMENT_TO_RENT_BRAND_COLUMN_NAME + ", " + INSTRUMENT_TO_RENT_PRICE_COLUMN_NAME + ", " +
                INSTRUMENT_TO_RENT_QUANTITY_COLUMN_NAME + " FROM " +
                INSTRUMENT_TO_RENT_TABLE_NAME + " INNER JOIN " + INSTRUMENT_TYPE_TABLE_NAME + " ON " +
                INSTRUMENT_TO_RENT_TABLE_NAME + "." + INSTRUMENT_TYPE_FK_COLUMN_NAME + " = " + INSTRUMENT_TYPE_TABLE_NAME +
                "." + INSTRUMENT_TYPE_PK_COLUMN_NAME + " WHERE " + INSTRUMENT_TO_RENT_QUANTITY_COLUMN_NAME + " > 0" +
                " AND " + INSTRUMENT_TO_RENT_TYPE_COLUMN_NAME + " = ? " + " ORDER BY " +
                INSTRUMENT_TO_RENT_PRICE_COLUMN_NAME);

        findStudentByPersonNumberStmt = connection.prepareStatement("SELECT " + STUDENT_TABLE_NAME + "." +
                STUDENT_PK_COLUMN_NAME + ", " + PERSON_FIRST_NAME_COLUMN_NAME + ", " + PERSON_LAST_NAME_COLUMN_NAME +
                ", " + PERSON_NUMBER_COLUMN_NAME + " FROM " + STUDENT_TABLE_NAME + " INNER JOIN " + PERSON_TABLE_NAME +
                " ON " + STUDENT_TABLE_NAME + "." + PERSON_FK_COLUMN_NAME + " = " + PERSON_TABLE_NAME + "." +
                PERSON_PK_COLUMN_NAME + " WHERE " + PERSON_TABLE_NAME + "." + PERSON_NUMBER_COLUMN_NAME + " = ?");

        getNrOfOngoingRentalsForSpecifiedStudentStmt = connection.prepareStatement("SELECT COUNT(*) AS " +
                NUMBER_OF_RENTALS_COLUMN_NAME + " FROM " +
                " ( SELECT * FROM " + RENTAL_TABLE_NAME + " WHERE " + STUDENT_FK_COLUMN_NAME + " = ?" + " AND " +
                RENTAL_END_DATE_COLUMN_NAME + " > " + CURRENT_DATE_FUNCTION + " ) AS student_rentals");

        findAndLockInstrumentByTypeAndBrandStmt = connection.prepareStatement("SELECT " + INSTRUMENT_TO_RENT_TABLE_NAME +
                "." + INSTRUMENT_TO_RENT_PK_COLUMN_NAME + ", " + INSTRUMENT_TO_RENT_TYPE_COLUMN_NAME + ", " +
                INSTRUMENT_TO_RENT_BRAND_COLUMN_NAME + ", " + INSTRUMENT_TO_RENT_PRICE_COLUMN_NAME + ", " +
                INSTRUMENT_TO_RENT_QUANTITY_COLUMN_NAME +  " FROM " + INSTRUMENT_TO_RENT_TABLE_NAME + " INNER JOIN " +
                INSTRUMENT_TYPE_TABLE_NAME + " ON " + INSTRUMENT_TO_RENT_TABLE_NAME + "." +
                INSTRUMENT_TYPE_FK_COLUMN_NAME + " = " + INSTRUMENT_TYPE_TABLE_NAME + "." +
                INSTRUMENT_TYPE_PK_COLUMN_NAME + " WHERE " + INSTRUMENT_TO_RENT_TYPE_COLUMN_NAME + " = ?" +
                " AND " + INSTRUMENT_TO_RENT_BRAND_COLUMN_NAME + " = ?" + " AND " + INSTRUMENT_TO_RENT_QUANTITY_COLUMN_NAME +
                " > 0" + " FOR UPDATE");

        updateInstrumentQuantityStmt = connection.prepareStatement("UPDATE " + INSTRUMENT_TO_RENT_TABLE_NAME +
                " SET " + INSTRUMENT_TO_RENT_QUANTITY_COLUMN_NAME + " = ?" + " WHERE " +
                INSTRUMENT_TYPE_PK_COLUMN_NAME + " = ?");

        createNewRentalStmt = connection.prepareStatement("INSERT INTO " + RENTAL_TABLE_NAME + " (" +
                RENTAL_RENTAL_ID_COLUMN_NAME + ", " + RENTAL_START_DATE_COLUMN_NAME + ", " + RENTAL_END_DATE_COLUMN_NAME
                + ", " + STUDENT_FK_COLUMN_NAME + ", " + INSTRUMENT_TO_RENT_FK_COLUMN_NAME +
                ") VALUES (?, ?, ?, ?, ?)");

        findLatestRentalStmt = connection.prepareStatement("SELECT " + RENTAL_RENTAL_ID_COLUMN_NAME + " FROM " +
                RENTAL_TABLE_NAME + " WHERE " + RENTAL_PK_COLUMN_NAME + " = ( SELECT MAX(" + RENTAL_PK_COLUMN_NAME +
                ") FROM " + RENTAL_TABLE_NAME + ")");

        findOngoingRentalsForSpecifiedStudentStmt = connection.prepareStatement("SELECT * FROM " +
                RENTAL_WITH_INSTRUMENT_TABLE_NAME + " WHERE " + STUDENT_FK_COLUMN_NAME + " = ?" + " AND " +
                RENTAL_END_DATE_COLUMN_NAME + " > CURRENT_DATE");

        updateRentalEndDataStmt = connection.prepareStatement("UPDATE " + RENTAL_TABLE_NAME + " SET " +
                RENTAL_END_DATE_COLUMN_NAME + " = ?" + " WHERE " + RENTAL_RENTAL_ID_COLUMN_NAME + " = ?");

        findRentalByIdStmt = connection.prepareStatement("SELECT * FROM " + RENTAL_WITH_INSTRUMENT_TABLE_NAME + " WHERE " +
                RENTAL_RENTAL_ID_COLUMN_NAME + " = ?");

        findAndLockInstrumentByIdStmt = connection.prepareStatement("SELECT " + INSTRUMENT_TO_RENT_TABLE_NAME +
                "." + INSTRUMENT_TO_RENT_PK_COLUMN_NAME + ", " + INSTRUMENT_TO_RENT_TYPE_COLUMN_NAME + ", " +
                INSTRUMENT_TO_RENT_BRAND_COLUMN_NAME + ", " + INSTRUMENT_TO_RENT_PRICE_COLUMN_NAME + ", " +
                INSTRUMENT_TO_RENT_QUANTITY_COLUMN_NAME +  " FROM " + INSTRUMENT_TO_RENT_TABLE_NAME + " INNER JOIN " +
                INSTRUMENT_TYPE_TABLE_NAME + " ON " + INSTRUMENT_TO_RENT_TABLE_NAME + "." +
                INSTRUMENT_TYPE_FK_COLUMN_NAME + " = " + INSTRUMENT_TYPE_TABLE_NAME + "." +
                INSTRUMENT_TYPE_PK_COLUMN_NAME + " WHERE " + INSTRUMENT_TO_RENT_TABLE_NAME +
                "." + INSTRUMENT_TO_RENT_PK_COLUMN_NAME + " = ?" + " FOR UPDATE");
    }

    private void closeResultSet(ResultSet result) throws MusicSchoolDBException {
        try {
            result.close();
        } catch (SQLException e) {
            throw new MusicSchoolDBException("Failed to close the result set.", e);
        }
    }
}
