package logic;

import java.io.Serializable;
import java.util.Date;


public class Person implements Serializable {   
    private String firstName;
    private String lastName;
    private int genere; // 0 -> M , 1 -> F
    private Date dateBirth;
    private Person myFather;
    private Person myMother;
        
    public Person() {
        this.firstName = "First Name";
        this.lastName = "Last Name";
    }
    
    public Person(String firstName, String lastName, int genere, 
                   Person father, Person mother) {
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setGenere(genere);
        this.setMyFather(father);
        this.setMyMother(mother);
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

   
    public int getGenere() {
        return genere;
    }
    
    public Date getDateBirth() {
        return dateBirth;
    }  

    public Person getMyFather() {
        return myFather;
    }

    public Person getMyMother() {
        return myMother;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setGenere(int genere) {
        this.genere = genere;
    }
    
    public void setDateBirth(Date dateBirth) {
        this.dateBirth = dateBirth;
    }   

    public void setMyFather(Person myFather) {
        this.myFather = myFather;
    }

    public void setMyMother(Person myMother) {
        this.myMother = myMother;
    }           
   
}
