/*
 * Author: Katrin Kahur
 * Course ID: IV1351
 * Program: TIDAB
 * ****************************************************************************
 * This file is a modified version of the Leif Lindbäcks BankDBException.java:
 * https://github.com/KTH-IV1351/jdbc-bank/blob/master/src/main/java/se/kth/iv1351/bankjdbc/integration/BankDBException.java
 *******************************************************************************
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

/**
 * This class represents an exception that is being thrown when a call to
 * the Soundgood Music School database fails.
 */
public class MusicSchoolDBException extends Exception {

    /**
     * Constructor, creates a new instance of a thrown exception with the specified reason
     * @param reason The specified reason for why the exception is thrown
     */
    public MusicSchoolDBException(String reason) {
        super(reason);
    }

    /**
     * Constructor, creates a new instance of a thrown exception with the specified reason and
     * the exception that caused it.
     * @param reason The reason why the exception is being thrown
     * @param rootCause The exception that caused this exception to be thrown
     */
    public MusicSchoolDBException(String reason, Throwable rootCause) {
        super(reason, rootCause);
    }
}
