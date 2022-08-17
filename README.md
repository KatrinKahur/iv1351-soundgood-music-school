# iv1351-soundgood-music-school

## Install
### Clone
Clone the repository

```shell
git clone git@github.com:KatrinKahur/iv1351-soundgood-music-school.git
```
### Database Settings
Modify database settings in MusicSchoolDAO.java to match your configuration:
```java
public class MusicSchoolDAO {
    private static final String DATABASE_URL = "jdbc:postgresql://[DATABASE_URL]";
    private static final String DATABASE_USERNAME = "[USERNAME]";
    private static final String DATABASE_PASSWORD = "[PASSWORD]";
}
```
### Start psql client in the project folder and import the database using the psql client
```shell
$ \i ./src/main/resources/create_database.sql
```
### Import the database data using the psql client
```shell
$ \i ./src/main/resources/insert_data.sql
```
## Application
### Commands
The log in command:
```text
LOGIN
```
Exit:
```text
QUIT
```
#### The following commands can only be reached in the logged in state:
The command to list all instruments of a certain kind available to rent:
```text
LIST_INSTRUMENTS <instrument type in singular>
```
Example: 
```text
LIST_INSTRUMENTS guitar
```
The command to list active rentals for the logged in student:
```text
LIST_RENTALS
```
The command to rent an instrument of a certain type and brand:
```text
RENT <instrument type in singular> <instrument brand> <rental period length in months>
```
Example:
```text
RENT guitar gibson 5
```
The command to terminate a rental:
```text
TERMINATE_RENTAL <rental id>
```
Example:
```text
TERMINATE_RENTAL RENTAL-0001
```
The command to see all existing commands:
```text
HELP
```
The command to log out:
```text
LOGOUT
```
### Recommendations for testing the program
To be able to log in the user has to enter a person number of an existing student.
A recommendation is to use the person numbers of the following students:

- Student with 2 ongoing rentals: 
Gry Lindqvist, 110215-1808
- Student with 0 rentals: 
Oskar Fransson, 120402-2170