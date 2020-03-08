import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Controller {
    private int selectedID;
    public ChoiceBox<String> chbRating;

    public CheckBox cbxTrailer;
    public CheckBox cbxCommentaries;
    public CheckBox cbxDeletedScenes;
    public CheckBox cbxBehindTheScenes;
    List<Movie> movies = new ArrayList<>();
    List<Integer> changedMovies = new ArrayList<>();
    ObservableList<String> movieList = FXCollections.observableArrayList();
    ObservableList<String> actorList = FXCollections.observableArrayList();

    @FXML private ListView<String> lsMovieView;
    @FXML private ListView<String> lsActorView;
    @FXML private TextField txtTitle, txtDesc, txtRelease, txtRent, txtLength, txtReplace;

    public void initialize(){
        //Add event listeners to check if the selected movie is changed, and to limit what can be put in the length and release fields.
        lsMovieView.getSelectionModel().selectedIndexProperty().addListener((observable, oldValue, newValue) -> {
            selectedID = lsMovieView.getSelectionModel().getSelectedIndex();
            System.out.println("Selected index: " +selectedID);

            pickMovie();
        });
        txtRelease.textProperty().addListener(((observable, oldValue, newValue) -> {
            if(txtRelease.getText().length() >= 4){
                txtRelease.setText(txtRelease.getText().substring(0,4));
            }
            if(!newValue.matches("\\d*")){
                txtRelease.setText(newValue.replaceAll("\\D+", ""));
            }
            //This can throw a NumberFormatException, it is harmless.
            if(Integer.parseInt(txtRelease.getText()) > 2155){
                txtRelease.setText("" +2155);
            }
        }));
        //When txtRelease loses focus, check if the number is less than 1901
        txtRelease.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue){
                try{
                    if(Integer.parseInt(txtRelease.getText()) < 1901){
                        txtRelease.setText("" +1901);
                    }
                }catch(NumberFormatException e){
                    txtRelease.setText("" +1901);
                }

            }
        });
        txtTitle.textProperty().addListener((observable, oldValue, newValue) -> {
            if(txtTitle.getText().length() > 127){
                txtTitle.setText(txtTitle.getText().substring(0, 127));
            }
        });
        txtLength.textProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.matches("\\d*")){
                txtRelease.setText(newValue.replaceAll("\\D+", ""));
            }
            if(Integer.parseInt(txtLength.getText()) > 65535){
                txtLength.setText("" +65535);
            }
        });

        //Setup choiceBox
        chbRating.getItems().add("G");
        chbRating.getItems().add("PG");
        chbRating.getItems().add("PG-13");
        chbRating.getItems().add("R");
        chbRating.getItems().add("NC-17");

        //Retrieve all movies and add them to the list
        System.out.println("----ADDING MOVIES----");
        popMovies();
        System.out.println(
                "Added: " +
                "\n   Movies: " +(movies.size()+1));

        for (Movie movie : movies) {
            movieList.add(movie.getTitle());
        }
        lsMovieView.setItems(movieList);
        lsMovieView.getSelectionModel().selectFirst();
    }

    //Establishes a connection to the DB then retrieves all the movies
    private void popMovies(){
        try{
            Connection connection = DBConnection.getInstance().getConnection();

            Statement st = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
            String sql = "" +
                    "SELECT film.*, GROUP_CONCAT(concat(actor.first_name, ' ', actor.last_name)) as actors " +
                    "FROM sakila.film film " +
                    "INNER JOIN sakila.film_actor film_actor on film.film_id = film_actor.film_id " +
                    "INNER JOIN sakila.actor actor on film_actor.actor_id = actor.actor_id GROUP BY film.film_id;";
            ResultSet result = st.executeQuery(sql);
            result.first();
            System.out.println("Got movie list.");
            while(!result.isLast()){
                movies.add(new Movie(result.getInt("film_id"),
                        result.getString("title"),
                        result.getString("description"),
                        result.getInt("release_year"),
                        result.getDouble("rental_rate"),
                        result.getInt("length"),
                        result.getDouble("replacement_cost"),
                        result.getString("rating"),
                        result.getString("special_features"),
                        result.getString("actors")
                        ));
                System.out.println("Added: " +movies.get(movies.size()-1).getTitle()
                        +" ID: " +movies.get(movies.size()-1).getId()
                        +" Actors: " +movies.get(movies.size()-1).getActors().size());
                result.next();
            }
            connection.close();
        }catch(SQLException e){e.printStackTrace();}
    }

    //Whenever a selection change is detected by the event handler this function executes and fills in the fields with the corresponding data.
    private void pickMovie(){
        Movie selectedMovie = movies.get(selectedID);
        List<Boolean> specialFeatures = selectedMovie.getSpecialFeatures();

        txtTitle.setText(selectedMovie.getTitle());
        txtDesc.setText(selectedMovie.getDescription());
        txtRelease.setText("" +selectedMovie.getReleaseYear());
        txtRent.setText("" +selectedMovie.getRentalRate());
        txtLength.setText("" +selectedMovie.getLength());
        txtReplace.setText("" +selectedMovie.getReplacementCost());
        chbRating.getSelectionModel().select(selectedMovie.getRating());
        cbxTrailer.setSelected(specialFeatures.get(0));
        cbxCommentaries.setSelected(specialFeatures.get(1));
        cbxDeletedScenes.setSelected(specialFeatures.get(2));
        cbxBehindTheScenes.setSelected(specialFeatures.get(3));

        actorList.clear();
        lsActorView.setItems(actorList);

        List<String> actors = selectedMovie.getActors();
        actorList.addAll(actors);
        lsActorView.setItems(actorList);
    }

    //Changes a movie locally in the arrayList and saves the change movie ID's to a list
    public void btnCommit() {
        Movie selectedMovie = movies.get(selectedID);

        System.out.println("Committing changes.");
        selectedMovie.setTitle(txtTitle.getText());
        selectedMovie.setDescription(txtDesc.getText());
        selectedMovie.setReleaseYear(Integer.parseInt(txtRelease.getText()));
        selectedMovie.setRentalRate(Double.parseDouble(txtRent.getText()));
        selectedMovie.setLength(Integer.parseInt(txtLength.getText()));
        selectedMovie.setReplacementCost(Double.parseDouble(txtReplace.getText()));
        selectedMovie.setRating("" +chbRating.getSelectionModel().getSelectedItem());

        List<Boolean> specialFeatures = new ArrayList<>();
        specialFeatures.add(cbxTrailer.isSelected());
        specialFeatures.add(cbxCommentaries.isSelected());
        specialFeatures.add(cbxDeletedScenes.isSelected());
        specialFeatures.add(cbxBehindTheScenes.isSelected());
        selectedMovie.setSpecialFeatures(specialFeatures);

        changedMovies.add(selectedID);
        movieList.set(selectedID, selectedMovie.getTitle());
        lsMovieView.refresh();
    }

    //Uses a prepared statement to push the stored changes to the database
    public void btnPush() {
        System.out.println("Pushing changes.");
        try{
            Connection connection = DBConnection.getInstance().getConnection();
            String preparedSQL = "UPDATE sakila.film SET " +
                    "`title` = ?, " +
                    "`description` = ?, " +
                    "`release_year` = ?, " +
                    "`rental_rate` = ?, " +
                    "`length` = ?, " +
                    "`replacement_cost` = ?, " +
                    "`rating` = ?, " +
                    "`special_features` = ?" +
                    " WHERE `film_id` = ?";

            PreparedStatement pstm = connection.prepareStatement(preparedSQL);

            for(int i = 0; i < changedMovies.size(); i++){
                Movie changedMovie = movies.get(changedMovies.get(i));
                List<Boolean> specialFeatures = changedMovie.getSpecialFeatures();

                //Handles the specialFeatures string
                String features = "";
                if(specialFeatures.get(0)){
                    features += "Trailers,";
                }

                if(specialFeatures.get(1)){
                    features += "Commentaries,";
                }

                if(specialFeatures.get(2)){
                    features += "Deleted Scenes,";
                }

                if(specialFeatures.get(3)){
                    features += "Behind the Scenes";
                }

                if(features.charAt(features.length()-1) == ','){
                    features = features.substring(0, features.length()-1);
                }

                System.out.println("Movie Features: " +features);

                pstm.setString(1, changedMovie.getTitle()); //Set title;
                pstm.setString(2, changedMovie.getDescription());
                pstm.setInt(3, changedMovie.getReleaseYear());
                pstm.setDouble(4, changedMovie.getRentalRate());
                pstm.setInt(5, changedMovie.getLength());
                pstm.setDouble(6, changedMovie.getReplacementCost());
                pstm.setString(7, changedMovie.getRating());
                pstm.setString(8, features);
                pstm.setInt(9, changedMovie.getId());

                System.out.println("Statement: " +pstm);
                pstm.executeUpdate();
                System.out.println("Updated movie: " +i +" ID: " +changedMovies.get(i));
            }
            connection.close();
        }catch(SQLException e){e.printStackTrace();}
    }

    //Button handlers
    public void btnFirst() {
        selectedID = 0;
        System.out.println("btnFirst : " +selectedID);
        lsMovieView.getFocusModel().focus(selectedID);
        lsMovieView.getSelectionModel().selectFirst();
    }

    public void btnPrev() {
        if(selectedID > 0){ --selectedID;}
        System.out.println("btnPrev : " +selectedID);
        lsMovieView.getFocusModel().focus(selectedID);
        lsMovieView.getSelectionModel().select(selectedID);
    }

    public void btnNext() {
        if(selectedID < movieList.size()){ ++selectedID;}

        System.out.println("btnNext : " +selectedID);
        lsMovieView.getFocusModel().focus(selectedID);
        lsMovieView.getSelectionModel().select(selectedID);
    }

    public void btnLast() {
        selectedID = movieList.size()-1;
        System.out.println("btnLast : " +selectedID);
        lsMovieView.getFocusModel().focus(selectedID);
        lsMovieView.getSelectionModel().selectLast();
    }
}