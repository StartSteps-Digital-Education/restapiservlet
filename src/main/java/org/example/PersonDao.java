package org.example;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonDao {

    private static final String SELECT_ALL_PERSONS = "SELECT * FROM persons";
    private static final String SELECT_PERSON_BY_NAME = "SELECT * FROM persons WHERE name = ?";
    private static final String INSERT_PERSON = "INSERT INTO persons VALUES (?, ?, ?)";
    private static final String UPDATE_PERSON = "UPDATE persons SET about=?, birthYear=? WHERE name = ?";
    private static final String DELETE_PERSON = "DELETE FROM persons WHERE name=?";

    public Person getPerson(String name) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_PERSON_BY_NAME)) {
            ps.setString(1, name);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return new Person(
                            rs.getString("name"),
                            rs.getString("about"),
                            rs.getInt("birthYear"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Person> getPersons() {
        List<Person> personList = new ArrayList<>();
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(SELECT_ALL_PERSONS);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                personList.add(new Person(
                        rs.getString("name"),
                        rs.getString("about"),
                        rs.getInt("birthYear")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return personList;
    }

    public void addPerson(Person person) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(INSERT_PERSON)) {
            ps.setString(1, person.getName());
            ps.setString(2, person.getAbout());
            ps.setInt(3, person.getBirthYear());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePerson(Person person) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(UPDATE_PERSON)) {
            ps.setString(1, person.getAbout());
            ps.setInt(2, person.getBirthYear());
            ps.setString(3, person.getName());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean deletePersonByName(String name) {
        try (Connection connection = DBConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(DELETE_PERSON)) {
            ps.setString(1, name);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}