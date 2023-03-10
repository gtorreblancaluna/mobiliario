Para poder modificar los reportes jasper, se utiliza el programa iReport 5.6.0, es importante que tambien tengan en las librerias del proyecto el JAR jasperreports-5.6.0.jar para que se generen de manera correcta los PDFs

Projecto common, es importante incluir en las librerias de este projecto

#### PASOS PARA INSTALAR EN WINDOWS
1. Instalar MySQL Server 5.7 
    *** Link de descarga (https://dev.mysql.com/downloads/installer/)
    *** NOTA: antes de editar el archivo, debe de activar la opcion de "mostrar archivos ocultos"
    a) Una vez instalado debera modificar el archivo my.ini ubicado en: (C:\ProgramData\MySQL\MySQL Server 5.7\my.ini) y agregar las siguientes dos lineas:
        [mysqld]
        sql_mode=IGNORE_SPACE,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION
    b) Reiniciar el servicio mysql.service
    c) Puede verificar la variable 'sql_mode' dentro de la linea de comando de mysql.
        *** mysql> SELECT @@sql_mode
        *** El resultado le debera mostrar: IGNORE_SPACE,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_AUTO_CREATE_USER,NO_ENGINE_SUBSTITUTION
        *** NOTA: esta variable "ONLY_FULL_GROUP_BY" no debe de estar presente, ya que nos generara un error al momento de ejecutar el sistema.
2. Abrir la consola mysql para crear la base de datos:
    a) mysql> CREATE DATABASE mobiliario;
    b) mysql> USE mobiliario;
    c) Ejecutar el script ubicado dentro de este proyecto (en la raiz) mobiliario.sql
        *** mysql> source file_path_with_file_name.sql
        *** Ejemplo para ejecutar el archivo: mysql> C:/Users/GT01894/Desktop/mobiliario.sql
    d) Ejecutar el script inserts.sql
3. Instalar ORACLE jre 8
    *** Link de descarga (https://www.java.com/es/download/ie_manual.jsp)