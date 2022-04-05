package clases;

import java.sql.*;
import java.util.Properties;
import services.PropertiesService;

public class conectate {
    
    public conectate() {
        this.connection = null;
        prop = PropertiesService.getInstance();
        this.bd = prop.getProperty("db.database.name");
        this.user = prop.getProperty("db.username");
        this.password = prop.getProperty("db.password");
        this.url = prop.getProperty("db.url");
        this.driver = prop.getProperty("db.driver");
    }
    
    
    private final PropertiesService prop;
    private final String bd;
    private final String user; 
    private final String password;
    private final String url;
    private final String driver;
    private Connection connection;
    /**
     * Constructor de DbConnection
     */
    public void conectate() throws Exception{  
        
        try {
            //obtenemos el driver de para mysql
            
            Class.forName(driver);
            //obtenemos la conexión
            connection = DriverManager.getConnection(url, user, password);
            if (connection != null) {
                System.out.println("Conexion a base de datos " + bd + ". listo");
            }
        
        } catch (SQLNonTransientConnectionException e) {
           System.out.println("la conexion se ha cerrado "+e);
           throw new SQLNonTransientConnectionException(e);
        } catch (SQLException e) {
            System.out.println(e);
            throw new SQLException(e);

        } catch (ClassNotFoundException e) {
            System.out.println(e);
            throw new ClassNotFoundException(e.toString());
        }catch (Exception e) {
            throw new Exception(e.toString());
        }
    }
    
    
    /**
     * Permite retornar la conexión
     */
    public Connection getConnection() {
        return connection;
    }
    
    public void desconectar() {
        connection = null;
        System.out.println("La conexion a la  base de datos " + bd + " a terminado");
    }

}
