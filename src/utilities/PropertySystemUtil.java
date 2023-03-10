package utilities;

import common.constants.PropertyConstant;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Properties;
import org.apache.log4j.Logger;


public abstract class PropertySystemUtil {
    
    private static final Logger log = Logger.getLogger(PropertySystemUtil.class.getName());
    private static final String NAME_FILE = "/system.properties";
    private static Properties prop=null;
    
    public static String get(final PropertyConstant propertyConstant)throws IOException{
        String value;
        try {
            InputStream input = new FileInputStream(Utility.getPathLocation()+NAME_FILE);

            if(prop == null) {
                prop = new Properties();
            }
            // load a properties file
            prop.load(input);
            value = prop.getProperty(propertyConstant.getKey());
            if (value == null || value.isEmpty()){
                save(propertyConstant.getKey(), propertyConstant.getValue());
                value = propertyConstant.getValue();
            }            
        } catch (FileNotFoundException e) {
            log.error(e);
            buildFirstTimeFile();
            value = propertyConstant.getValue();
        } catch (IOException | URISyntaxException e) {
            log.error(e.getMessage(),e);
            throw new IOException(e);
        }
        return value;
    }
    
    public static void save(final String key, final String value)throws IOException{
        FileOutputStream fr;
      
       try{
           fr = new FileOutputStream(Utility.getPathLocation()+NAME_FILE);
       } catch (URISyntaxException e) {
          log.error(e);
          throw new IOException(e);
       }

        if(prop == null){
            prop = new Properties();
        }
        // load a properties file
        // set the properties value
        prop.setProperty(key, value);      
        // save properties to project root folder
        prop.store(fr, null);
        fr.close();
    }
    
    public static void buildFirstTimeFile(){
        // estos parametros seran declarado y se construiran por primera vez, 
        // aqui se deben de registrar todos los valores que se van a utilizar durante el proyecto
        log.info(NAME_FILE+" created.");
        for (PropertyConstant prop : PropertyConstant.values()) {
            try{
                save(prop.getKey(),prop.getValue());            
            }catch(IOException e){
                log.error(e);
            }
        }        
    }
    
}
