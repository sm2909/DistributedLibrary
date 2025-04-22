public class Kiosk {
    private int kioskID;
    private String location;
    private int sectionNo;
    private String status;

    public Kiosk(int kioskID, String location, int sectionNo, String status) {
        this.kioskID = kioskID;
        this.location = location;
        this.sectionNo = sectionNo;
        this.status = status;
    }

    public int getKioskID() {
        return kioskID;
    }

    public void setKioskID(int kioskID) {
        this.kioskID = kioskID;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getSectionNo() {
        return sectionNo;
    }

    public void setSectionNo(int sectionNo) {
        this.sectionNo = sectionNo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
} 