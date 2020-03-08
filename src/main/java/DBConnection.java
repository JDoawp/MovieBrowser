import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


//Singleton version of a Database connection handler so there can only be one *actual* connection.
//I don't know if it's needed, i thought it might be but meh.
//Thank you to @jasoet from github for this: https://gist.github.com/jasoet/3843797
public class DBConnection {
    private static DBConnection instance;
    private Connection connection;

    private DBConnection(){
        try{
            String USER = "javauser";
            String PASS = "java";
            String DATABASE = "/sakila";
            String SERVER = "localhost";
            String URL = "jdbc:mysql://" + SERVER + DATABASE;
            this.connection = DriverManager.getConnection(URL, USER, PASS);
        }catch(SQLException e){
            e.getMessage();
        }
    }

    public Connection getConnection(){
        return connection;
    }

    public static DBConnection getInstance() throws SQLException{
        if(instance == null){
            instance = new DBConnection();
        }else if(instance.getConnection().isClosed()){
            instance = new DBConnection();
        }

        return instance;
    }
}
