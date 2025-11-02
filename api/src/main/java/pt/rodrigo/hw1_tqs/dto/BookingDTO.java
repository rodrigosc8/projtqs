package pt.rodrigo.hw1_tqs.dto;

public class BookingDTO {
    private String municipality;
    private String date;
    private String timeslot;
    private String description;

    // GETTERS
    public String getMunicipality() { return municipality; }
    public String getDate() { return date; }
    public String getTimeslot() { return timeslot; }
    public String getDescription() { return description; }

    // SETTERS
    public void setMunicipality(String municipality) { this.municipality = municipality; }
    public void setDate(String date) { this.date = date; }
    public void setTimeslot(String timeslot) { this.timeslot = timeslot; }
    public void setDescription(String description) { this.description = description; }
}
