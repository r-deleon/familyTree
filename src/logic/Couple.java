package logic;

import java.io.Serializable;
import java.util.Date;

public class Couple implements Serializable {

    private Person person1;
    private Person person2;
    private int coupleType;
    private Date startDate;
    private Date endDate;

    public Couple() {
    }

    public Person getPerson1() {
        return person2;
    }

    public Person getPerson2() {
        return person1;
    }

    public int getCoupleType() {
        return coupleType;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setPerson1(Person person1) {
        this.person1 = person1;
    }

    public void setPerson2(Person person2) {
        this.person2 = person2;
    }

    public void setCoupleType(int coupleType) {
        this.coupleType = coupleType;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }
}
