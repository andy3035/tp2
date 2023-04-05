package server.models;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Course implements Serializable {

    private String name;
    private String code;
    private String session;

    /**
     * Constructor for the class Course
     * @param name of the class
     * @param code of the class
     * @param session the class is given
     */
    public Course(String name, String code, String session) {
        this.name = name;
        this.code = code;
        this.session = session;
    }

    /**
     * Method that filters a list of class for a specific semester.
     * @param classes list to be filtered.
     * @param session you want the classes for.
     * @return a filtered list of the classes.
     */
    public static List<Course> filterBySession(List<Course> classes, String session) {
        List<Course> filteredClasses = new ArrayList<Course>();
        for(Course course : classes){
            if(course.getSession().equalsIgnoreCase(session)){
                filteredClasses.add(course);
            }
        }
        return filteredClasses;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSession() {
        return session;
    }

    public void setSession(String session) {
        this.session = session;
    }

    @Override
    public String toString() {
        return "Course{" +
                "name=" + name +
                ", code=" + code +
                ", session=" + session +
                '}';
    }

}
