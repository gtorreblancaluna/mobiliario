package clases;

import java.sql.*;


public class conectate {
    
    // ambiente de desarrollo localhost    
//    public String bd = "mobiliario";
//    public String login = "root"; 
//    public String password = "root";
//    public String url = "jdbc:mysql://localhost/"+bd;
    
    /** 2018.11.15 para conectarnos a la bd que esta en un servidor virtual, este es para el negocio GABY */
//    public String bd = "sql9269415";
//    public String login = "sql9269415"; 
//    public String password = "ikVLki2mYq";
//    public String url = "jdbc:mysql://sql9.freesqldatabase.com/"+bd;
    
//    public String bd = "mobiliario";
//    public String login = "root"; 
//    public String password = "root";
//    public String url = "jdbc:google:mysql://windy-container-237418:us-central1:mobiliario/mobiliario";
    
    
    // GOOGLE CASA GABY
    public String bd = "mobiliario";
    public String login = "usr-gaby-mobiliario"; 
    public String password = "gaby1932&%cloud";
    public String url = "jdbc:mysql://35.202.6.26/mobiliario";
//    
    // >>> GRUPO ALEM
//    public String bd = "sql9302069";
//    public String login = "sql9302069"; 
//    public String password = "guUj16iW27";
//    public String url = "jdbc:mysql://sql9.freesqldatabase.com/"+bd;
    
        // GOOGLE TEST
//    public String bd = "mobiliario_test";
//    public String login = "test-user"; 
//    public String password = "testuser";
//    public String url = "jdbc:mysql://35.202.6.26/mobiliario_test";
    
    // -----------------------------------------------------------------------------
    
//    public String bd = "sql3208192";
//    public String login = "root";
//    public String login = "sql3208192";    
//    public String password = "america";
//     public String password = "root";
//     public String password = "K7rW8ysB5p";
//     public String password = "Pru3b4@s2018";
//    public String password = "@mericaboxes100485";
//    public String url = "jdbc:mysql://localhost/"+bd;
//    public String url = "jdbc:mysql://sql3.freesqldatabase.com/"+bd;
    //private String ip1 = null;
    Connection conn = null;
    /**
     * Constructor de DbConnection
     */
    public void conectate() throws Exception{
        
        /*try {
            //obtenemos el driver de para mysql
            Class.forName("org.sqlite.JDBC");
            String ruta = "C:/restaurante/restaurante.db"; //especificamos la ruta de la base
            File base = new File(ruta);
            //obtenemos la conexión
            if (base.exists()) {       //si la base existe
                conn = DriverManager.getConnection("jdbc:sqlite:" + ruta); //conexion con la base
                JOptionPane.showMessageDialog(null, " Se ha conectado exitosamente!", "Conexion ", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "La base de datos no existe o no se encuentra en la ruta especificada.");
            }

           
            if (conn != null) {
                System.out.println("Conexión a base de datos listo");
            }
        } catch (SQLException e) {
            System.out.println(e);
        } catch (ClassNotFoundException e) {
            System.out.println(e);
        }*/    
        
        //public String url = "jdbc:mysql://localhost/"+bd;
        
        
        
        try {
            //obtenemos el driver de para mysql
            
            Class.forName("com.mysql.jdbc.Driver");
            //obtenemos la conexión
            conn = DriverManager.getConnection(url, login, password);
            if (conn != null) {
                System.out.println("Conexion a base de datos " + bd + ". listo");
            }
        
        } catch (SQLNonTransientConnectionException e) {
           System.out.println("la conexion se ha cerrado "+e);
           throw new SQLNonTransientConnectionException(e);
        } catch (SQLException e) {
//            JOptionPane.showMessageDialog( "No se encuentra el Archivo jasper");
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
        return conn;
    }
    
    public void desconectar() {
        conn = null;
        System.out.println("La conexion a la  base de datos " + bd + " a terminado");
    }

    //public void setIp(String value) {
     //   this.ip1 = value;
    //}

  //  public String getIp() {
  //      return this.ip1;
   // }
}
