/**
 * Author: Katrin Kahur
 * Course ID: IV1351
 * Program: TIDAB
 */

package se.kth.iv1351.soundgoodMusicSchool.integration;

/**
 * This class is a DTO class that represents a Soundgood Music School student.
 */
public class StudentDTO {
    private String firstName;
    private String lastName;
    private String personNumber;
    private int id;

    /**
     * A constructor, creates a new instance of <code>StudentDTO</code> with the
     * specified first name, last name and person number.
     * @param firstName the students first name
     * @param lastName the students last name
     * @param personNumber the students person number
     * @param studentId the students surrogate id
     */
    public StudentDTO(String firstName, String lastName, String personNumber, int studentId){
        this.firstName = firstName;
        this.lastName = lastName;
        this.personNumber = personNumber;
        this.id = studentId;
    }

    /**
     * @return the students first name
     */
    public String getFirstName(){
        return firstName;
    }

    /**
     * @return the students last name
     */
    public String getLastName(){
        return lastName;
    }

    /**
     * @return the students person number
     */
    public String getPersonNumber(){
        return personNumber;
    }

    /**
     * @return the students surrogate id
     */
    public int getId() {
        return id;
    }

}
