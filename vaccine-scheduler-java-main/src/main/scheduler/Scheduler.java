//Yanbing Xiao

package scheduler;

import scheduler.db.ConnectionManager;
import scheduler.model.Caregiver;
import scheduler.model.Patient;
import scheduler.model.Vaccine;
import scheduler.util.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Date;

public class Scheduler {

    // objects to keep track of the currently logged-in user
    // Note: it is always true that at most one of currentCaregiver and currentPatient is not null
    //       since only one user can be logged-in at a time
    private static Caregiver currentCaregiver = null;
    private static Patient currentPatient = null;

    public static void main(String[] args) {
        // printing greetings text
        System.out.println();
        System.out.println("Welcome to the COVID-19 Vaccine Reservation Scheduling Application!");
        System.out.println("*** Please enter one of the following commands ***");
        System.out.println("> create_patient <username> <password>");  //TODO: implement create_patient (Part 1)
        System.out.println("> create_caregiver <username> <password>");
        System.out.println("> login_patient <username> <password>");  // TODO: implement login_patient (Part 1)
        System.out.println("> login_caregiver <username> <password>");
        System.out.println("> search_caregiver_schedule <date>");  // TODO: implement search_caregiver_schedule (Part 2)
        System.out.println("> reserve <date> <vaccine>");  // TODO: implement reserve (Part 2)
        System.out.println("> upload_availability <date>");
        System.out.println("> cancel <appointment_id>");  // TODO: implement cancel (extra credit)
        System.out.println("> add_doses <vaccine> <number>");
        System.out.println("> show_appointments");  // TODO: implement show_appointments (Part 2)
        System.out.println("> logout");  // TODO: implement logout (Part 2)
        System.out.println("> quit");
        System.out.println();

        // read input from user
        BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            System.out.print("> ");
            String response = "";
            try {
                response = r.readLine();
            } catch (IOException e) {
                System.out.println("Please try again!");
            }
            // split the user input by spaces
            String[] tokens = response.split(" ");
            // check if input exists
            if (tokens.length == 0) {
                System.out.println("Please try again!");
                continue;
            }
            // determine which operation to perform

            String operation = tokens[0];
            System.out.println(operation);
            if (operation.equals("create_patient")) {
                createPatient(tokens);
            } else if (operation.equals("create_caregiver")) {
                createCaregiver(tokens);
            } else if (operation.equals("login_patient")) {
                loginPatient(tokens);
            } else if (operation.equals("login_caregiver")) {
                loginCaregiver(tokens);
            } else if (operation.equals("search_caregiver_schedule")) {
                searchCaregiverSchedule(tokens);
            } else if (operation.equals("reserve")) {
                reserve(tokens);
            } else if (operation.equals("upload_availability")) {
                uploadAvailability(tokens);
            } else if (operation.equals("cancel")) {
                cancel(tokens);
            } else if (operation.equals("add_doses")) {
                addDoses(tokens);
            } else if (operation.equals("show_appointments")) {
                showAppointments(tokens);
            } else if (operation.equals("logout")) {
                logout(tokens);
            } else if (operation.equals("quit")) {
                System.out.println("Bye!");
                return;
            } else {
                System.out.println("Invalid operation name!");
            }
        }
    }

    private static void createPatient(String[] tokens) {
        // TODO: Part 1
        if (tokens.length != 3) {
            System.out.println("Failed to create user.");
            return;
        }
        String username = tokens[1];
        String password = tokens[2];

        if (usernameExistsPatient(username)) {
            System.out.println("Username taken, try again!");
            return;
        }

        byte[] salt = Util.generateSalt();
        byte[] hash = Util.generateHash(password, salt);
        // create the Patient

        try {
            currentPatient = new Patient.PatientBuilder(username, salt, hash).build();
            // save to patients information to our database
            currentPatient.saveToDB();
            System.out.println("Created user " + username);
        } catch (SQLException e) {
            System.out.println("Failed to create user.");
            e.printStackTrace();
        }

    }
//usernameExistsPatient??
    private static boolean usernameExistsPatient(String username) {
        ConnectionManager cm = new ConnectionManager();
        Connection con = cm.createConnection();
        String selectUsername = "SELECT * FROM Patient WHERE Username = ?";
        try {
            PreparedStatement statement = con.prepareStatement(selectUsername);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            // returns false if the cursor is not before the first record or if there are no rows in the ResultSet.
            return resultSet.isBeforeFirst();
        } catch (SQLException e) {
            System.out.println("Error occurred when checking username");
            e.printStackTrace();
        } finally {
            cm.closeConnection();
        }
        return true;
    }




        private static void createCaregiver(String[] tokens) {
        // create_caregiver <username> <password>
        // check 1: the length for tokens need to be exactly 3 to include all information (with the operation name)
        if (tokens.length != 3) {
            System.out.println("Failed to create user.");
            return;
        }
        String username = tokens[1];
        String password = tokens[2];
        // check 2: check if the username has been taken already
        if (usernameExistsCaregiver(username)) {
            System.out.println("Username taken, try again!");
            return;
        }
        byte[] salt = Util.generateSalt();
        byte[] hash = Util.generateHash(password, salt);
        // create the caregiver
        try {
            Caregiver caregiver = new Caregiver.CaregiverBuilder(username, salt, hash).build(); 
            // save to caregiver information to our database
            caregiver.saveToDB();
            System.out.println("Created user " + username);
        } catch (SQLException e) {
            System.out.println("Failed to create user.");
            e.printStackTrace();
        }
    }




    private static boolean usernameExistsCaregiver(String username) {
        ConnectionManager cm = new ConnectionManager();
        Connection con = cm.createConnection();

        String selectUsername = "SELECT * FROM Caregivers WHERE Username = ?";
        try {
            PreparedStatement statement = con.prepareStatement(selectUsername);
            statement.setString(1, username);
            ResultSet resultSet = statement.executeQuery();
            // returns false if the cursor is not before the first record or if there are no rows in the ResultSet.
            return resultSet.isBeforeFirst();

        } catch (SQLException e) {
            System.out.println("Error occurred when checking username");
            e.printStackTrace();
        } finally {
            cm.closeConnection();
        }
        return true;
    }



    private static void loginPatient(String[] tokens) {
        // TODO: Part 1
        if (currentCaregiver != null || currentPatient != null) {
            System.out.println("User already logged in.");
            return;
        }
        // check 2: the length for tokens need to be exactly 3 to include all information (with the operation name)
        if (tokens.length != 3) {
            System.out.println("Login failed.");
            return;
        }
        String username = tokens[1];
        String password = tokens[2];


        Patient patient = null;
        try {
            patient= new Patient.PatientGetter(username, password).get();
        } catch (SQLException e) {
            System.out.println("Login failed.");
            e.printStackTrace();
        }
        // check if the login was successful
        if (patient == null) {
            System.out.println("Login failed.");
        } else {
            System.out.println("Logged in as: " + username);
            currentPatient =patient;
        }


    }

    private static void loginCaregiver(String[] tokens) {
        // login_caregiver <username> <password>
        // check 1: if someone's already logged-in, they need to log out first
        if (currentCaregiver != null || currentPatient != null) {
            System.out.println("User already logged in.");
            return;
        }
        // check 2: the length for tokens need to be exactly 3 to include all information (with the operation name)
        if (tokens.length != 3) {
            System.out.println("Login failed.");
            return;
        }
        String username = tokens[1];
        String password = tokens[2];

        Caregiver caregiver = null;
        try {
            caregiver = new Caregiver.CaregiverGetter(username, password).get();

        } catch (SQLException e) {
            System.out.println("Login failed.");
            e.printStackTrace();
        }
        // check if the login was successful
        if (caregiver == null) {
            System.out.println("Login failed.");
        } else {
            System.out.println("Logged in as: " + username);
            currentCaregiver = caregiver;

        }
    }

    private static void searchCaregiverSchedule(String[] tokens) {
        // TODO: Part 2
        if (tokens.length != 2) {
            System.out.println("Please try again!");
            return;
        }
        if  (currentCaregiver == null && currentPatient == null) {
            System.out.println("Please login first!");
            return;
        }

        String date = tokens[1];//use date again

        ConnectionManager cm = new ConnectionManager();
        Connection con = cm.createConnection();
        String selectDate = "SELECT Username FROM Availabilities WHERE Time = ? ORDER BY Username";//order by



        //String getVaccine = "SELECT Name, Doses FROM Vaccines WHERE Name = ?";
       // System.out.println(username);

        try {
            PreparedStatement statement = con.prepareStatement(selectDate);
            statement.setString(1, date);
            ResultSet resultSet = statement.executeQuery();
            while(resultSet.next()){
                System.out.println("Caregivers that are available for the date: "+
                        resultSet.getString("Username"));
            }
        } catch (SQLException e) {
            System.out.println("Error occurred when checking date");
            e.printStackTrace();
        } finally {
            cm.closeConnection();
        }


        cm = new ConnectionManager();
        con = cm.createConnection();
        String getVaccine = "SELECT * FROM Vaccines ";
        try {
            PreparedStatement statement = con.prepareStatement(getVaccine);

            ResultSet resultSet = statement.executeQuery();
            // System.out.println("The number of the available vaccines is: "
            while(resultSet.next()){
                System.out.println("name of the vaccine is :" + resultSet.getString("Name")
                        + " " + "Storage does" + resultSet.getString("Doses"));
            }

        }catch (SQLException e) {
            System.out.println("Error occurred when checking Name");
            e.printStackTrace();
        } finally {
            cm.closeConnection();
        }


    }

    private static void reserve(String[] tokens){
        if (currentPatient == null) {
            System.out.println("Please login first!");
            return;
        }
        if (tokens.length != 3) {
            System.out.println("Please try again!");
            return;
        }
        if (currentCaregiver != null ) {
            System.out.println("Please login as a patient!");
            return;
        }
        ConnectionManager cm = new ConnectionManager();
        Connection con = cm.createConnection();
     //check avai caregiver
        String resevr_Date = tokens[1];
        String resevrVaccine = tokens[2];
        String available_caregivers = "SELECT Username FROM Availabilities WHERE Time = ? ORDER BY Username";
        try {
            Date d = Date.valueOf(resevr_Date);
            PreparedStatement statement = con.prepareStatement(available_caregivers);
            statement.setDate(1, d);
            ResultSet resultSet = statement.executeQuery();
            if (!resultSet.next()) {
                System.out.println("No Caregiver is available!");
                return;
            }

            //check vaccine
            int availableDoses;
            String check_vaccine = "SELECT V.Name, V.Doses FROM Vaccines AS V WHERE V.Name = ?";

            PreparedStatement statement1 = con.prepareStatement(check_vaccine);
            statement1.setString(1, resevrVaccine);
            ResultSet resultSet1 = statement1.executeQuery();
            if (resultSet1.next()) {
                availableDoses = resultSet1.getInt("Doses");

                if (availableDoses == 0) {
                    System.out.println("Not enough available doses!");
                    return;
                }
            }


            int currentid = 0;

            String MAX_PREVID = "SELECT count(*) FROM appoinment;";

            PreparedStatement statement2 = con.prepareStatement(MAX_PREVID);
            ResultSet resultSet2 = statement2.executeQuery();

            resultSet2.next();
            currentid = resultSet2.getInt("ID") + 1;


            String get_appoinment = "INSERT INTO appoinment VALUES (?, ?, ?, ?,?)";
            String pa_name = currentPatient.getUsername();
            String cargi = resultSet.getString("Username");
            PreparedStatement statement3 = con.prepareStatement(get_appoinment);
            statement3.setInt(1, currentid);
            statement3.setString(2, resevr_Date);
            statement3.setString(3, pa_name);
            statement3.setString(4, cargi);
            statement3.setString(5, check_vaccine);
            statement3.executeUpdate();

//         delete does
            String removeDoes = "UPDATE Vaccines SET Doses = Doses -1 WHERE Name = ?";
            PreparedStatement dele_does = con.prepareStatement(removeDoes);
            dele_does.setString(1, resevrVaccine);
            dele_does.executeUpdate();
            System.out.print("Appointment ID:" + Integer.toString(currentid));
            System.out.println("Caregiver username:" + available_caregivers);
            //Delete availability
            String cancel_availability = "Delete FROM Availabilities WHERE Username = ?";
            PreparedStatement cancel = con.prepareStatement(cancel_availability);
            cancel.setDate(1, d);
            cancel.setString(2, cargi);
            cancel.executeUpdate();
        }catch (SQLException e) {
            System.out.println("Error occurred when update avaibility");
            e.printStackTrace();
        }finally {
            cm.closeConnection();
        }


    }

    private static void uploadAvailability(String[] tokens) {
        // upload_availability <date>
        // check 1: check if the current logged-in user is a caregiver
        if (currentCaregiver == null) {
            System.out.println("Please login as a caregiver first!");
            return;
        }
        // check 2: the length for tokens need to be exactly 2 to include all information (with the operation name)
        if (tokens.length != 2) {
            System.out.println("Please try again!");
            return;
        }
        String date = tokens[1];
        try {
            Date d = Date.valueOf(date);
            currentCaregiver.uploadAvailability(d);
            System.out.println("Availability uploaded!");
        } catch (IllegalArgumentException e) {
            System.out.println("Please enter a valid date!");
        } catch (SQLException e) {
            System.out.println("Error occurred when uploading availability");
            e.printStackTrace();
        }
    }

    private static void cancel(String[] tokens) {
        // TODO: Extra credit
    }

    private static void addDoses(String[] tokens) {
        // add_doses <vaccine> <number>
        // check 1: check if the current logged-in user is a caregiver
        if (currentCaregiver == null) {
            System.out.println("Please login as a caregiver first!");
            return;
        }
        // check 2: the length for tokens need to be exactly 3 to include all information (with the operation name)
        if (tokens.length != 3) {
            System.out.println("Please try again!");
            return;
        }
        String vaccineName = tokens[1];
        int doses = Integer.parseInt(tokens[2]);
        Vaccine vaccine = null;
        try {
            vaccine = new Vaccine.VaccineGetter(vaccineName).get();
        } catch (SQLException e) {
            System.out.println("Error occurred when adding doses");
            e.printStackTrace();
        }
        // check 3: if getter returns null, it means that we need to create the vaccine and insert it into the Vaccines
        //          table
        if (vaccine == null) {
            try {
                vaccine = new Vaccine.VaccineBuilder(vaccineName, doses).build();
                vaccine.saveToDB();
            } catch (SQLException e) {
                System.out.println("Error occurred when adding doses");
                e.printStackTrace();
            }
        } else {
            // if the vaccine is not null, meaning that the vaccine already exists in our table
            try {
                vaccine.increaseAvailableDoses(doses);
            } catch (SQLException e) {
                System.out.println("Error occurred when adding doses");
                e.printStackTrace();
            }
        }
        System.out.println("Doses updated!");
    }

    private static void showAppointments(String[] tokens) {
        // TODO: Part 2
        if (currentPatient == null&& currentCaregiver == null) {
            System.out.println("Please login first!");
            return;
        }
        if(tokens.length!=1){
            System.out.println("Please try again!") ;
            return;
        }
        ConnectionManager cm = new ConnectionManager();
        Connection con = cm.createConnection();

        //current patient
        try {
            if (currentCaregiver != null) {
                String show_appoint = "SELECT ID,ti, Username,Name FROM appoinment WHERE Username = ? ORDER BY ID";

                PreparedStatement statementshow = con.prepareStatement(show_appoint);
                statementshow.setString(1, currentCaregiver.getUsername());
                ResultSet resultSet = statementshow.executeQuery();
                while (resultSet.next()) {
                    int AppointmentID = resultSet.getInt("ID");
                    Date time = resultSet.getDate("ti");
                    String curr_careuser = resultSet.getString("Username");
                    String vaname = resultSet.getString("Name");
                    System.out.print(AppointmentID + " ");
                    System.out.print(time + " ");
                    System.out.print(curr_careuser + " ");
                    System.out.print(vaname + " ");
                }


            } else if (currentPatient != null) {
                String show_appointforPa = "SELECT ID, ti, PUsername,Name FROM appoinment WHERE Username = ? ORDER BY ID";

                PreparedStatement statement = con.prepareStatement(show_appointforPa);
                statement.setString(1, currentPatient.getUsername());
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    int AppointmentIDforPa = resultSet.getInt("ID");
                    Date Patime = resultSet.getDate("ti");
                    String curr_pauser = resultSet.getString("Username");
                    String pavaname = resultSet.getString("Name");
                    System.out.print(AppointmentIDforPa + " ");
                    System.out.print(Patime + " ");
                    System.out.print(curr_pauser + " ");
                    System.out.print(pavaname + " ");
                }


            }

        } catch (SQLException e) {
                System.out.println("Error occurred when show appointment for patient.");
                e.printStackTrace();
            }finally {
                cm.closeConnection();
            }

        }



    private static void logout(String[] tokens) {
        // TODO: Part 2
        if (currentCaregiver == null && currentPatient == null) {
            System.out.println("Please login first");
            return;

        } if (tokens.length != 1) {
            System.out.println("Please try again!");
            return;
        }
        currentCaregiver = null;
        currentPatient = null;

            System.out.println("Successfully logged out!");
            return;


    }
}
