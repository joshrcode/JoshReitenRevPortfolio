package models;

public class Employee extends User {

    public Employee() {
    }

    public Employee(int id, String username, String password, String email) {
        super(id, username, password, email);
    }
}
