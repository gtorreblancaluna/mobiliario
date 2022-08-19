package services;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Writer;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import dao.SystemDAO;
import common.exceptions.BusinessException;
import common.exceptions.DataOriginException;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.FileOutputStream;
import java.sql.Timestamp;
import mobiliario.IndexForm;
import model.DatosGenerales;
import utilities.Utility;


public class SystemService {
    
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(SystemService.class.getName());
    private int contaFila;
    
    private SystemService(){}
    
    private static final SystemService SINGLE_INSTANCE = null;
    
    public static SystemService getInstance(){
        
        if (SINGLE_INSTANCE == null) {
            return new SystemService();
        }
        return SINGLE_INSTANCE;
    } 
    
    private final SystemDAO systemDao = SystemDAO.getInstance();

    public String getDataConfigurationByKey(String key)throws BusinessException{
        String result;
        try{
            result = systemDao.getDataConfigurationByKey(key);
        }catch(DataOriginException e){
            throw new BusinessException(e.getMessage());
        }
        
        return result;
    }
    
    public DatosGenerales getGeneralData(){
        if (IndexForm.generalDataGlobal == null ) {
            DatosGenerales generalData = systemDao.getGeneralData();
            IndexForm.generalDataGlobal = generalData;
            Utility.pushNotification("Datos generales del sistema obtenidos de la base de datos.");
        }
        return IndexForm.generalDataGlobal;
    }
    
    public void saveDatosGenerales(DatosGenerales datosGenerales){
        systemDao.saveDatosGenerales(datosGenerales);
        DatosGenerales generalData = systemDao.getGeneralData();
        IndexForm.generalDataGlobal = generalData;
    }
    
    public void updateInfoPDFSummary(DatosGenerales datosGenerales) throws DataOriginException{
        systemDao.updateInfoPDFSummary(datosGenerales);
    }
    
    public Image generarQR(){
        
        //GENERACION QR
       // Image properties
     int qr_image_width = 400;
     int qr_image_height = 400;
     String IMAGE_FORMAT = "png";
     String IMG_PATH = "c:/temp/qrcode.png";
        
     String data = "CASA GABY :: REPORTE DE ENTREGA :: FECHA ELABORACION :: "+new Timestamp(System.currentTimeMillis());
        
           // Encode URL in QR format
        BitMatrix matrix;
        Writer writer = new QRCodeWriter();
        try { 
            matrix = writer.encode(data, BarcodeFormat.QR_CODE, qr_image_width, qr_image_height); 
        } catch (WriterException e) {
            e.printStackTrace(System.err);
            return null;
        }
 
        // Create buffered image to draw to
        BufferedImage image = new BufferedImage(qr_image_width, qr_image_height, BufferedImage.TYPE_INT_RGB);
        
        // Iterate through the matrix and draw the pixels to the image
        for (int y = 0; y < qr_image_height; y++) {
            for (int x = 0; x < qr_image_width; x++) {
                int grayValue = (matrix.get(x, y) ? 0 : 1) & 0xff;
                image.setRGB(x, y, (grayValue == 0 ? 0 : 0xFFFFFF));
            }
        }
 
        try {        
        // Write the image to a file
        FileOutputStream qrCode = new FileOutputStream(IMG_PATH);
//        ImageIO.write(image, IMAGE_FORMAT, qrCode);
//        map.put("QR",image);        
        qrCode.close();
        return image;
        } catch (Exception e) {
            
            System.out.println(e);
        }
        
        // FIN GENERACION QR
        return null;
    }
    
}
