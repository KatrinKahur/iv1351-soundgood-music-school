/*
 * Author: Katrin Kahur
 * Course ID: IV1351
 * Program: TIDAB
 * ****************************************************************************
 * This file is a modified version of Leif Lindbäcks AccountException.java:
 * https://github.com/KTH-IV1351/jdbc-bank/blob/master/src/main/java/se/kth/iv1351/bankjdbc/model/AccountException.java
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

package se.kth.iv1351.soundgoodMusicSchool.model;

/**
 * This class represents an exception that is thrown if an operation related to
 * <code>MusicalInstrument</code> fails.
 */
public class MusicalInstrumentException extends Exception {
    /**
     * A constructor, creates a new instance with the specified reason
     * @param reason the reason why the exception is thrown
     */
    public MusicalInstrumentException (String reason) {
        super(reason);
    }

    /**
     * A constructor, creates a new instance with the specified reason and the exception that caused this exception
     * to be thrown
     * @param reason the specified reason
     * @param cause the specified exception
     */
    public MusicalInstrumentException (String reason, Exception cause) {
        super(reason, cause);
    }
}
