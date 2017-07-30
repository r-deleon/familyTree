package logic;

import java.io.Serializable;
import java.util.*;

public class Controller implements Serializable {

    private final ArrayList<Person> peopleList;
    private final ArrayList<Couple> couplesList;

    public Controller() {
        peopleList = new ArrayList<>();
        couplesList = new ArrayList<>();
    }

    public ArrayList<Person> getPeopleList() {
        return peopleList;
    }

    public ArrayList<Couple> getCouplesList() {
        return couplesList;
    }

    public void addPerson(Person person) {
        this.getPeopleList().add(person);
    }

    public void agregarUnion(Couple union) {
        this.getCouplesList().add(union);
    }
}
