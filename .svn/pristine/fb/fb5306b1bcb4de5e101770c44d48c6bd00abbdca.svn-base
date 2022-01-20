/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

/**
 *
 * @author gerardo torreblanca luna
 */

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Reader;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.log4j.Logger;
public class MyBatisConnectionFactory {
    private static Logger log = Logger.getLogger(MyBatisConnectionFactory.class.getName());
    private static SqlSessionFactory sqlSessionFactory;
    
 
    static {
        try { 
            String resource = "mybatis-config.xml";
            Reader reader = Resources.getResourceAsReader(resource);
 
            if (sqlSessionFactory == null) {
                sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
            }
        } catch (FileNotFoundException fileNotFoundException) {
            fileNotFoundException.printStackTrace();
            log.error(fileNotFoundException);
        } catch (IOException iOException) {
            iOException.printStackTrace();
             log.error(iOException);
        }
    }
 
    public static SqlSessionFactory getSqlSessionFactory() {
 
        return sqlSessionFactory;
    }
    
}
