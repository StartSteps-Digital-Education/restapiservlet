package org.example;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.List;

@WebServlet(urlPatterns = "/people/*")
public class PersonServlet extends HttpServlet {

    private static final String PEOPLE_PATH = "/people/";
    private final PersonDao personDao = new PersonDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try {
            String name = extractNameFromRequest(request);
            if (name.isEmpty()) {
                List<Person> personList = personDao.getPersons();
                JSONArray responseArray = new JSONArray();
                for (Person person : personList) {
                    responseArray.put(createJsonFromPerson(person));
                }
                writeResponse(response, responseArray.toString());
            } else {
                Person person = personDao.getPerson(name);
                if (person != null) {
                    writeResponse(response, createJsonFromPerson(person).toString());
                } else {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND, "Person not found");
                }
            }
        } catch (Exception e) {
            throw new ServletException("Error processing GET request", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try {
            JSONObject object = readJsonFromRequest(request);
            personDao.addPerson(new Person(
                    object.getString("name"),
                    object.getString("about"),
                    object.getInt("birthYear")
            ));
        } catch (Exception e) {
            throw new ServletException("Error processing POST request", e);
        }
    }

    @Override
    protected void doDelete(HttpServletRequest request, HttpServletResponse resp) throws ServletException {
        try {
            String name = extractNameFromRequest(request);
            if (!name.isEmpty()) {
                if (!personDao.deletePersonByName(name)) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Couldn't find a user with name: " + name);
                }
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Name not provided");
            }
        } catch (Exception e) {
            throw new ServletException("Error processing DELETE request", e);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse resp) throws ServletException {
        try {
            JSONObject object = readJsonFromRequest(request);
            personDao.updatePerson(new Person(
                    object.getString("name"),
                    object.getString("about"),
                    object.getInt("birthYear")
            ));
        } catch (Exception e) {
            throw new ServletException("Error processing PUT request", e);
        }
    }

    private String extractNameFromRequest(HttpServletRequest request) {
        String requestUrl = request.getRequestURI();
        return requestUrl.substring(PEOPLE_PATH.length());
    }

    private JSONObject createJsonFromPerson(Person person) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("name", person.getName());
        jsonObject.put("about", person.getAbout());
        jsonObject.put("birthYear", person.getBirthYear());
        return jsonObject;
    }

    private void writeResponse(HttpServletResponse response, String content) throws IOException {
        response.getOutputStream().println(content);
    }

    private JSONObject readJsonFromRequest(HttpServletRequest request) throws IOException {
        StringBuilder buffer = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append(System.lineSeparator());
            }
        }
        return new JSONObject(buffer.toString());
    }
}
