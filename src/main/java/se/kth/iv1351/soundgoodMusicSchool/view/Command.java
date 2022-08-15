/*
 * Author: Katrin Kahur
 * Course ID: IV1351
 * Program: TIDAB
 * ****************************************************************************
 * This file is a slightly modified version of Leif Lindbäcks Command.java:
 * https://github.com/KTH-IV1351/jdbc-bank/blob/master/src/main/java/se/kth/iv1351/bankjdbc/view/Command.java
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

/**
 * Defines all commands that user can enter in the chat application.
 */
public enum Command {
    /**
     * Creates a new rental.
     */
    RENT,
    /**
     * Terminates an ongoing rental
     */
    TERMINATE_RENTAL,
    /**
     * Lists all available instruments of a certain kind that are available for rent.
     */
    LIST_INSTRUMENTS,
    /**
     * Lists all commands.
     */
    LIST_RENTALS,
    /**
     * List all ongoing rentals
     */
    HELP,
    /**
     * Logout
     */
    LOGOUT,
    /**
     * Exits the program
     */
    QUIT,
    /**
     * None of the valid commands above was specified.
     */
    ILLEGAL_COMMAND
}
