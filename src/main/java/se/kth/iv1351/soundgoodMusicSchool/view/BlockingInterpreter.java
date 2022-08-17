/*
 * Author: Katrin Kahur
 * Course ID: IV1351
 * Program: TIDAB
 * ****************************************************************************
 * This file is a modified version of Leif Lindbäcks BlockingInterpreter.java:
 * https://github.com/KTH-IV1351/jdbc-bank/blob/master/src/main/java/se/kth/iv1351/bankjdbc/view/BlockingInterpreter.java
 * ****************************************************************************
 * The MIT License
 *
 * Copyright 2017 Leif Lindbäck <leifl@kth.se>.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
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

package se.kth.iv1351.soundgoodMusicSchool.view;


import se.kth.iv1351.soundgoodMusicSchool.controller.Controller;
import se.kth.iv1351.soundgoodMusicSchool.controller.OperationFailedException;
import se.kth.iv1351.soundgoodMusicSchool.integration.MusicSchoolDBException;
import se.kth.iv1351.soundgoodMusicSchool.integration.MusicalInstrumentDTO;
import se.kth.iv1351.soundgoodMusicSchool.integration.RentalDTO;
import se.kth.iv1351.soundgoodMusicSchool.integration.StudentDTO;
import se.kth.iv1351.soundgoodMusicSchool.util.ExceptionLogger;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

/**
 * This class reads and interpreters user commands. The command interpreter is blocking,
 * that is no new user input is being handled while a command is being executed.
 */
public class BlockingInterpreter {
    private static final String PROMPT = "> ";
    private final Scanner console = new Scanner(System.in);
    private final Controller ctrl;
    private final ExceptionLogger logger;
    private boolean keepReceivingCmds = false;
    private String userInput;
    private boolean loggedInStatus = false;

    private static final String FORMATTING_ERROR_MESSAGE = "Formatting error. " +
            "The entered person number has to have the following format: YYMMDD-XXXX";

    /**
     * Constructor, creates a new instance of the class using
     * the specified controller in all its operations
     * @param ctrl The controller used by this instance
     */
    public BlockingInterpreter(Controller ctrl) throws IOException {
        this.ctrl = ctrl;
        this.logger = new ExceptionLogger();
    }

    /**
     * Stops the command interpreter.
     */
    public void stop() {
        keepReceivingCmds = false;
    }

    public void handleCmds() {
        System.out.println("Welcome to Soundgood Music School.");
        keepReceivingCmds = true;
        while(keepReceivingCmds) {
            System.out.println("Please choose a command:");
            System.out.println("LOGIN\nQUIT");
            userInput = readNextLine();
            if(userInput.equalsIgnoreCase("LOGIN")) {
                boolean correctlyFormatted = handlePersonNumberInsertion();
                if(correctlyFormatted) {
                    try {
                        handleLogin();
                        while(loggedInStatus && keepReceivingCmds) {
                            System.out.println("Press HELP to see the commands.");
                            CmdLine cmdLine = new CmdLine(readNextLine());
                            try {
                                switch (cmdLine.getCmd()) {
                                    case HELP:
                                        printAvailableCommands();
                                        break;
                                    case LOGOUT:
                                        handleLogoutCommand();
                                        break;
                                    case RENT:
                                        handleRentInstrumentCommand(cmdLine);
                                        break;
                                    case LIST_INSTRUMENTS:
                                        handleListInstrumentsCommand(cmdLine);
                                        break;
                                    case LIST_RENTALS:
                                        handleListRentalsCommand();
                                        break;
                                    case TERMINATE_RENTAL:
                                        handleTerminateRentalCommand(cmdLine);
                                        break;
                                    case QUIT:
                                        stop();
                                        break;
                                    default:
                                        System.out.println("Illegal command");
                                }
                            } catch (OperationFailedException | MusicSchoolDBException e) {
                                System.out.println("Operation failed.");
                                System.out.println(e.getMessage());
                                System.out.println();
                                logger.logException(e);
                            }
                        }
                    }  catch (Exception e) {
                        System.out.println("Operation failed.");
                        System.out.println(e.getMessage());
                        System.out.println();
                        logger.logException(e);
                    }
                } else {
                    System.out.println(FORMATTING_ERROR_MESSAGE);
                }
            } else if (userInput.equalsIgnoreCase("QUIT")) {
                stop();
            } else {
                System.out.println("Illegal argument. Please try again.");
            }
        }
    }

    private String readNextLine() {
        System.out.print(PROMPT);
        return console.nextLine();
    }

    private boolean handlePersonNumberInsertion() {
        System.out.println("Please enter your person number to log in.");
        System.out.println("The person number should have the following format: YYMMDD-XXXX");
        userInput = readNextLine();
        return checkPersonNumberFormatting(userInput);
    }

    private void handleLogin() throws Exception {
        StudentDTO registeredStudent = ctrl.logInStudent(userInput);
        if(registeredStudent == null) {
            System.out.println("Login failed. Student with this person number not found.");
            return;
        }
        loggedInStatus = true;
        System.out.println("Log in successful! Welcome, " + registeredStudent.getFirstName() + "!");
    }

    private void handleLogoutCommand() {
        ctrl.logOutStudent();
        loggedInStatus = false;
        System.out.println("You are now logged out.");
    }

    private void handleListRentalsCommand() throws MusicSchoolDBException{
        List<RentalDTO> rentals = ctrl.fetchAllOngoingRentals();
        printRentals(rentals);
    }

    private void handleListInstrumentsCommand(CmdLine cmd) throws MusicSchoolDBException {
        String queriedInstrumentType = cmd.getAllParameters();
        if(queriedInstrumentType.equals("")) {
            System.out.println("You haven't specified instrument type. " +
                    "Please try again.");
            return;
        }
        List<MusicalInstrumentDTO> listedInstruments =
                ctrl.listMusicalInstrumentsOfCertainType(queriedInstrumentType);

        if (!listedInstruments.isEmpty()) {
            printInstruments(listedInstruments);
        }
        else
            System.out.println("Instrument of this type is not available for rent " +
                    "or does not exist.");
    }

    private void handleTerminateRentalCommand(CmdLine cmdLine) throws OperationFailedException, MusicSchoolDBException{
        String rentalId = cmdLine.getParameter(0);
        if(rentalId.equals("")) {
            System.out.println("You haven't specified the rental id. Please try again.");
            return;
        }
        ctrl.terminateRental(rentalId);
        System.out.println("Your rental was successfully terminated!");
        System.out.println();
    }
    private void handleRentInstrumentCommand(CmdLine cmdLine) throws OperationFailedException, MusicSchoolDBException {
        String type = cmdLine.getParameter(0);
        String brand = cmdLine.getParameter(1);
        int rentalPeriod = parseToInteger(cmdLine.getParameter(2));
        if((type != null) && (brand != null) && rentalPeriod > 0) {
            if(rentalPeriod > 12) {
                System.out.println("Rental period must not exceed 12 months. " +
                        "Please try again.");
                return;
            }
            int nrOfRentals = ctrl.getNrOfRentals();
            if (nrOfRentals >= 2) {
                System.out.println("Your rental quota is exceeded. " +
                        "You are only allowed to rent at most 2 instruments at the time.");
                return;
            }
            RentalDTO newRental = ctrl.rentInstrument(type, brand, rentalPeriod);
            System.out.println("Your rental is successful!");
            printRental(newRental);

        } else
            System.out.println("Formatting error.");
    }

    private void printAvailableCommands() {
        System.out.println("----------------------------------------------------------------------");
        System.out.println("Press LIST_INSTRUMENTS <instrument type in singular> to be able to see" +
                " the instruments that are available for rent.");
        System.out.println("Example: LIST_INSTRUMENTS saxophone");
        System.out.println("----------------------------------------------------------------------");
        System.out.println("Press LIST_RENTALS to be able to see all ongoing rentals.");
        System.out.println("----------------------------------------------------------------------");
        System.out.println("Press RENT <instrument type> <instrument brand> <rental period in months> to rent the " +
                "instrument.");
        System.out.println("Example: RENT guitar gibson 5");
        System.out.println("----------------------------------------------------------------------");
        System.out.println("Press TERMINATE_RENTAL <rental id> to terminate a rent.");
        System.out.println("Example: TERMINATE_RENTAL RENTAL-0010");
        System.out.println("----------------------------------------------------------------------");
        System.out.println("Press HELP to see all the commands.");
        System.out.println("----------------------------------------------------------------------");
        System.out.println("Press LOGOUT to logout.");
        System.out.println("----------------------------------------------------------------------");
        System.out.println("Press QUIT to exit the program.");
        System.out.println("----------------------------------------------------------------------");
        System.out.println();
    }

    private void printRentals(List<RentalDTO> rentals) {
        if(rentals.size() == 0) {
            System.out.println("You have no rentals.");
            System.out.println();
            return;
        }
        for (RentalDTO rental : rentals) {
            printRental(rental);
        }
    }

    private void printRental(RentalDTO rentalDTO) {
        System.out.println("-----------------------");
        System.out.println("Rental id: " + rentalDTO.getRentalId());
        System.out.println("Instrument type: " + rentalDTO.getInstrumentDTO().getType());
        System.out.println("Instrument brand: " + rentalDTO.getInstrumentDTO().getBrand());
        System.out.println("Monthly rental fee: " + rentalDTO.getInstrumentDTO().getPrice() + " SEK");
        System.out.println("Start date: " + rentalDTO.getStartDate().toString());
        System.out.println("End date: " + rentalDTO.getEndDate().toString());
        System.out.println("-----------------------");
        System.out.println();
    }

    private boolean checkPersonNumberFormatting(String enteredPersonNumber) {
        String[] splitPersonNumber = enteredPersonNumber.split("-");
        if(splitPersonNumber.length == 2) {
            if(splitPersonNumber[0].length() == 6 && splitPersonNumber[1].length() == 4) {
                try {
                    Integer.parseInt(splitPersonNumber[0]);
                    Integer.parseInt(splitPersonNumber[1]);
                    return true;
                } catch (NumberFormatException e) {
                    return false;
                }
            } else
                return false;
        } else
            return false;
    }

    private void printInstruments(List<MusicalInstrumentDTO> instruments) {
        for (MusicalInstrumentDTO instrument : instruments){
            System.out.println(instrument);
        }
    }

    private int parseToInteger(String value) {
        int parsedInt;
        try {
           parsedInt = Integer.parseInt(value);
        } catch (NumberFormatException exc) {
            parsedInt = -1;
        }
        return parsedInt;
    }
}
