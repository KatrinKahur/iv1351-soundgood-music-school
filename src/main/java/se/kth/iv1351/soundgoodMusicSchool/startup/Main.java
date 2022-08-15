/*
 * Author: Katrin Kahur
 * Course ID: IV1351
 * Program: TIDAB
 * ****************************************************************************
 * This file is a sligthly modified version of Leif Lindbäcks Main.java:
 * https://github.com/KTH-IV1351/jdbc-bank/blob/master/src/main/java/se/kth/iv1351/bankjdbc/startup/Main.java
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

package se.kth.iv1351.soundgoodMusicSchool.startup;

import se.kth.iv1351.soundgoodMusicSchool.controller.Controller;
import se.kth.iv1351.soundgoodMusicSchool.integration.MusicSchoolDBException;
import se.kth.iv1351.soundgoodMusicSchool.view.BlockingInterpreter;

import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try {
            Controller ctrl = new Controller();
            new BlockingInterpreter(ctrl).handleCmds();
        } catch (MusicSchoolDBException dbException) {
            System.out.println("Connection to the database failed.");
            dbException.printStackTrace();
        } catch (IOException ioException) {
            System.out.println("The process to start the program failed.");
            ioException.printStackTrace();
        }
    }
}
