import java.util.ArrayList;
import java.util.List;

public class Movie {
    private int id;
    private String title;
    private String description;
    private int releaseYear;
    private double rentalRate;
    private int length;
    private double replacementCost;
    private String rating;
    private List<Boolean> specialFeatures = new ArrayList<>();
    private List<String> actors = new ArrayList<>();

    public Movie(int id, String title, String description, int releaseYear, double rentalRate, int length, double replacementCost, String rating, String features, String actorString) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.releaseYear = releaseYear;
        this.rentalRate = rentalRate;
        this.length = length;
        this.replacementCost = replacementCost;
        this.rating = rating;
        specialFeatures.add(false); specialFeatures.add(false); specialFeatures.add(false); specialFeatures.add(false);
        setFeatures(features+",");
        setActors(actorString+",");
    }

    private void setFeatures(String features){
        switch(features.charAt(0)){
            case 'T':
                specialFeatures.set(0, true);
                System.out.println(title +": Trailers");
                break;
            case 'C':
                specialFeatures.set(1, true);
                System.out.println(title +": Commentaries");
                break;
            case 'D':
                specialFeatures.set(2, true);
                System.out.println(title +": Deleted Scenes");
                break;
            case 'B':
                specialFeatures.set(3, true);
                System.out.println(title +": Behind the Scenes");
                break;
        }
        features = features.substring(features.indexOf(','));
        if(features.length() > 2){
            setFeatures(features.substring(1));
        }
    }

    //Goes through the given actor string and chops it ups into an array
    private void setActors(String actorString){ //Take string filled with actors, formatted as firstName lastName, firstNamex lastNamex,
        while(actorString.length() > 1){    //Make sure the string isn't just a comma
             actors.add(actorString.substring(0, actorString.indexOf(',')));    //Take everything form beginning to the sepperator and add it as name
             actorString = actorString.substring(actorString.indexOf(',')+1);   //Remove that name + sepperator + the space
             System.out.println("Added: " +actors.get(actors.size()-1));
        }
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(int releaseYear) {
        this.releaseYear = releaseYear;
    }

    public double getRentalRate() {
        return rentalRate;
    }

    public void setRentalRate(double rentalRate) {
        this.rentalRate = rentalRate;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public double getReplacementCost() {
        return replacementCost;
    }

    public void setReplacementCost(double replacementCost) {
        this.replacementCost = replacementCost;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public List<Boolean> getSpecialFeatures() {
        return specialFeatures;
    }

    public void setSpecialFeatures(List<Boolean> specialFeatures) {
        this.specialFeatures = specialFeatures;
    }

    public List<String> getActors() {
        return actors;
    }
}
