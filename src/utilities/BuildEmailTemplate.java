/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import mobiliario.ApplicationConstants;
import org.apache.log4j.Logger;
import parametersVO.DataEmailTemplate;
import parametersVO.ModelTableItem;

/**
 *
 * @author idscomercial
 */
public class BuildEmailTemplate {
     
    // TEXT
    private static final String SUMMARY_UPDATE = "Le notificamos que la actualizaci&oacute;n de su pedido a nuestros sistemas ha quedado guardado exitosamente, le recordamos que verifique todos los datos contenidos en el folio tanto datos personales y de envío así como los servicios contratados para su entera satisfacci&oacute;n";
    private static final String SUMMARY_REGISTER = "Le notificamos que el registro de su pedido a nuestros sistemas ha quedado guardado exitosamente, le recordamos que verifique todos los datos contenidos en el folio tanto datos personales y de envío as&iacute; como los servicios contratados para su entera satisfacci&oacute;n";
    private final Logger LOGGER;
    private final DataEmailTemplate emailTemplate;

    public BuildEmailTemplate(DataEmailTemplate emailTemplate) {
        this.LOGGER = org.apache.log4j.Logger.getLogger(BuildEmailTemplate.class.getName());
        this.emailTemplate = emailTemplate;
    }
    
    public String buildEmail(){
    
        String buildHtml = "<center>" +
"	<table style=\"width:100%;max-width:1024px;margin:0 auto;padding-bottom:20px;padding-top:30px\" border=\"0\" cellpadding=\"0\" cellspacing=\"0\">" +
"        <tbody>" +
"        	<tr style=\"width:100%\">" +
"          		<th colspan=\"3\" width=\"40%\">" +
"          			<a href=\""+emailTemplate.getSiteCompany()+"\">" +
"            			<p style=\"margin-left:20px;text-align:left\">" +
"            				<img src=\""+emailTemplate.getUrlLogoSiteCompany()+"\" style=\"border:0;width:100%;max-width:180px\" " +
"            			</p>" +
"            		</a>" +
"          		</th>" +
"		        <th colspan=\"1\" width=\"60%\" style=\"text-align:right;margin-right:20px\">" +
"		          <p style=\"margin-right:20px\">FOLIO: #"+emailTemplate.getOrderNumber()+"</p>" +
"		        </th>" +
"        	</tr>" +
"	        <tr>" +
"	          <td colspan=\"4\">" +
"	            <div style=\"border-bottom:1px solid #e6e6e6;margin:0px 20px 0px 20px\">" +
"	              &nbsp;" +
"	            </div>" +
"	          </td>" +
"	        </tr>" +
"      </tbody>" +
"  </table>" +
"</center>" +
"<center>" +
"	<table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"padding-bottom:30px;padding-left:0;padding-right:0;padding-top:0;width:100%;max-width:1024px;margin:0 auto\">" +
"        <tbody><tr>" +
"          <td width=\"100%\">" +
"            <table border=\"0\" cellpadding=\"0\" cellspacing=\"0\" style=\"border-collapse:collapse;color:#2b3944;font-family:sans-serif;font-size:16px;line-height:1;width:100%\">" +
"              <tbody>" +
"                <tr><td><table style=\"width:100%;max-width:1024px;margin:0 auto\" align=\"center\" cellspacing=\"0\" cellpadding=\"0\">" +
"                  <tbody><tr style=\"color:#0e2240\">" +
"                    <td colspan=\"3\">" +
"                      <div style=\"margin:20px\">" +
"                        <div style=\"font-size:16px;font-family:Arial;color:#0e2240\">" +
"                          Hola: Gerardo" +
"                        </div>" +
"                        <br>" +
"                        <div style=\"font-size:14px;font-family:Arial;font-weight:bold;color:#0e2240\">" +
                getTypeSummary()+
"                        </div>" +
"                        <br>" +
"                        <br>" +
"                      </div>" +
"                    </td>" +
"                    <td colspan=\"1\">" +
"                      <div style=\"margin:20px\">" +
"                        <img src=\""+emailTemplate.getUrlIconCkeck()+"\" style=\"border:0;width:120px;max-width:120px\" " +
"                      </div>" +
"                    </td>" +
"                  </tr>" +
"                </tbody></table>" +
"" +
"                <table style=\"width:100%;max-width:1024px;margin:0 auto\" align=\"center\" cellspacing=\"0\" cellpadding=\"0\">" +
"                  <tbody><tr style=\"color:#015cb7\">" +
"                    <td>" +
"                      <div style=\"margin:20px 10px 20px 20px\">" +
"                        <img src=\"https://ci4.googleusercontent.com/proxy/oh_top5Er_JcI-EdaitwKFgp0yoNhGZ-EkKuyTboMzK61zJz92NDUZefZoUYgnTuHrLMDDMSiVdJo3kgBhbYIf-DzwPSh6Hmb5vltjhiiWJCYBlbJWl6y0BC=s0-d-e1-ft#https://s3.amazonaws.com/cervezasiempre.images-for-emails/clipboard.png\" class=\"CToWUd\">" +
"                      </div>" +
"                    </td>" +
"                    <td width=\"95%\">" +
"                      <div>" +
"                        <span style=\"font-size:16px;font-family:Arial;font-weight:bold;color:#0e2240\">" +
"                          &nbsp;&nbsp;Resumen de tu orden" +
"                        </span>" +
"                      </div>" +
"                    </td>" +
"                  </tr>" +
"                </tbody>" +
"            	</table>" +
"                <table style=\"width:100%;max-width:1024px;margin:0 auto;background:#f1f7ff\" align=\"center\" cellspacing=\"0\" cellpadding=\"0\">" +
"                  <tbody><tr>" +
"                    <td colspan=\"4\">" +
"                      <div style=\"border-top:2px solid #11366a;margin:0px 20px 0px 20px\">" +
"                        &nbsp;" +
"                      </div>" +
"                    </td>" +
"                  </tr>" +
"                  <tr style=\"color:#0e2240\">" +
"                    <td width=\"90%\">" +
"                      <div style=\"margin:0px 20px 20px 20px\">" +
"                        <table width=\"100%\">" +
"                          <tbody>" +
"                          	<tr style=\"font-size:14px;font-family:Arial;font-weight:bold;color:#0e2240\">" +
"	                            <td>" +
"	                            	Fecha y hora de entrega                            " +
"	                            </td>" +
"	                            <td>" +
	                            	emailTemplate.getDeliveryDate()+
"	                            </td>" +
"	                        </tr>" +
"	                        <tr style=\"font-size:14px;font-family:Arial;font-weight:bold;color:#0e2240\">" +
"	                            <td>" +
"	                            	Fecha y hora de devoluci&oacute;n" +
"	                            </td>" +
"	                            <td>" +
	                            	emailTemplate.getReturnDate()+
"	                            </td>" +
"	                        </tr>" +
"	                        <tr style=\"font-size:14px;font-family:Arial;font-weight:bold;color:#0e2240\">" +
"	                            <td>" +
"	                            	Fecha del evento" +
"	                            </td>" +
"	                            <td>" +
	                            	emailTemplate.getEventDate()+
"	                            </td>" +
"	                        </tr>" +
"	                        <tr style=\"font-size:14px;font-family:Arial;font-weight:bold;color:#0e2240\">" +
"	                            <td>" +
"	                            	Nombre del chofer" +
"	                            </td>" +
"	                            <td>" +
	                            	emailTemplate.getDriversName()+
"	                            </td>" +
"	                        </tr>" +
"	                        <tr style=\"font-size:14px;font-family:Arial;font-weight:bold;color:#0e2240\">" +
"	                            <td>" +
"	                            	Direcci&oacute;n del evento" +
"	                            </td>" +
"	                            <td>" +
	                            	emailTemplate.getAdressEvent()+
"	                            </td>" +
"                        	</tr>" +
"                        	<tr style=\"font-size:14px;font-family:Arial;font-weight:bold;color:#0e2240\">" +
"	                            <td>" +
"	                            	Tipo de registro" +
"	                            </td>" +
"	                            <td>" +
	                            	emailTemplate.getRegisterType()+
"	                            </td>" +
"	                        </tr>" +
"	                        <tr style=\"font-size:14px;font-family:Arial;font-weight:bold;color:#0e2240\">" +
"	                            <td>" +
"	                            	Personal que atendi&oacute;" +
"	                            </td>" +
"	                            <td>" +
                                        emailTemplate.getNameUser()+
"	                            </td>" +
"                            </tr>" +
"                         </tbody>" +
"                     </table>" +
"                     <br/>" +
"                    </td>" +
"                  </tr>" +
"                  <tr style=\"color:#0e2240\">" +
"                    <td width=\"90%\">" +
"                      <div style=\"font-size:16px;font-family:Arial;font-weight:bold;color:#0e2240;margin:0px 0px 0px 20px\">" +
"                        Mis Articulos" +
"                      </div>" +
"                    </td>" +
"                  </tr>" +
"                  <tr>" +
"                    <td colspan=\"4\">" +
"                      <div style=\"border-top:2px solid #cccdcd;margin:20px 20px 0px 20px\">" +
"                        &nbsp;" +
"                      </div>" +
"                    </td>" +
"                  </tr>" +
"                  <tr>" +
"                    <td colspan=\"4\">" +
"                      <div style=\"margin:0px 20px 20px 20px\">" +
"                        <table width=\"100%\">" +
"                          <tbody><tr style=\"font-size:14px;font-family:Arial;font-weight:bold;color:#0e2240\">" +
"                            <th style=\"text-align:left\">" +
"                              &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Cantidad" +
"                            </th>" +
"                            <th style=\"text-align:center\">" +
"                            	Articulo" +
"                            </th>" +
"                            <th style=\"text-align:right\">" +
"                              Precio unitario&nbsp;&nbsp;&nbsp;&nbsp;" +
"                            </th>" +
"                            <th>" +
"                              Descuento" +
"                            </th>" +
"                            <th>" +
"                              Importe" +
"                            </th>" +
"                          </tr>" +
                buildItems()+
"                          <tr>" +
"                            <td colspan=\"6\">" +
"                              <div style=\"border-top:2px solid #cccdcd;margin:20px 0px 0px 0px\">" +
"                                &nbsp;" +
"                              </div>" +
"                            </td>" +
"                          </tr>" +
"                          <tr style=\"font-size:14px;font-family:Arial;font-weight:bold;color:#0e2240\">" +
"                            <td style=\"text-align:left\" colspan=\"3\"></td>" +
"                            <td style=\"text-align:right\">" +
"                              Subtotal:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
"                            </td>" +
"                            <td style=\"text-align:right\">" +
                                emailTemplate.getSubTotal()+
"                            </td>" +
"                          </tr>" +
"                          <tr style=\"font-size:14px;font-family:Arial;font-weight:bold;color:#0e2240\">" +
"                            <td style=\"text-align:left\" colspan=\"3\"></td>" +
"                            <td style=\"text-align:right\">" +
"                              Descuento:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
"                            </td>" +
"                            <td style=\"text-align:right\">" +
                                emailTemplate.getDiscount()+
"                            </td>" +
"                          </tr>" +
"                          <tr style=\"font-size:14px;font-family:Arial;font-weight:bold;color:#0e2240\">" +
"                            <td style=\"text-align:left\" colspan=\"3\"></td>" +
"                            <td style=\"text-align:right\">" +
"                              Envio y recolecci&oacute;n:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
"                            </td>" +
"                            <td style=\"text-align:right\">" +
                                emailTemplate.getSendAndCollection()+
"                            </td>" +
"                          </tr>" +
"                          <tr style=\"font-size:14px;font-family:Arial;font-weight:bold;color:#0e2240\">" +
"                            <td style=\"text-align:left\" colspan=\"3\"></td>" +
"                            <td style=\"text-align:right\">" +
"                              Dep&oacute;sito en garant&iacute;a:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
"                            </td>" +
"                            <td style=\"text-align:right\">" +
                                emailTemplate.getGuaranteeDeposit()+
"                            </td>" +
"                          </tr>" +
"                          <tr style=\"font-size:14px;font-family:Arial;font-weight:bold;color:#0e2240\">" +
"                            <td style=\"text-align:left\" colspan=\"3\"></td>" +
"                            <td style=\"text-align:right\">" +
"                              IVA:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
"                            </td>" +
"                            <td style=\"text-align:right\">" +
                                emailTemplate.getIva()+
"                            </td>" +
"                          </tr>" +
"                          <tr style=\"font-size:14px;font-family:Arial;font-weight:bold;color:#0e2240\">" +
"                            <td style=\"text-align:left\" colspan=\"3\"></td>" +
"                            <td style=\"text-align:right\">" +
"                              Pagos:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
"                            </td>" +
"                            <td style=\"text-align:right\">" +
                                emailTemplate.getPayments()+
"                            </td>" +
"                          </tr>" +
"                          <tr style=\"font-size:14px;font-family:Arial;font-weight:bold;color:#0e2240\">" +
"                            <td style=\"text-align:left\" colspan=\"3\"></td>" +
"                            <td style=\"text-align:right\">" +
"                              Total:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" +
"                            </td>" +
"                            <td style=\"text-align:right\">" +
                                emailTemplate.getTotal()+
"                            </td>" +
"                          </tr>                   " +
"                        </tbody>" +
"                    	</table>" +
"                      </div>" +
"                    </td>" +
"                  </tr>" +
"                  <tr>" +
"                    <td colspan=\"4\">" +
"                      <div style=\"border-bottom:2px solid #11366a;margin:0px 20px 0px 20px\">" +
"                        &nbsp;" +
"                      </div>" +
"                    </td>" +
"                  </tr>" +
"                </tbody></table>" +
"                <table style=\"width:100%;max-width:1024px;margin:0 auto\" align=\"center\" cellspacing=\"0\" cellpadding=\"0\">" +
"                      <div style=\"margin:20px;font-size:15px;font-family:Arial;font-weight:bold;color:#0e2240\">" +
"                        Gracias por tu preferencia." +
"                      </div>" +
"                    </td>" +
"                  </tr>" +
"                </tbody></table>" +
"              </td></tr></tbody>" +
"            </table>" +
"          </td>" +
"        </tr>" +
"      </tbody></table>" +
"</center>";
        return buildHtml;
    }
    
    
    private String getTypeSummary(){
        if(emailTemplate.getRegisterType().equals(ApplicationConstants.DS_ESTADO_APARTADO)){
            return SUMMARY_REGISTER;
        }
        else{
            return SUMMARY_UPDATE ;
        }
    }
    
    private String buildItems(){
        StringBuilder builder = new StringBuilder();
        
        for(ModelTableItem item : emailTemplate.getItems()){
                builder.append("<tr style=\"font-size:12px;font-family:Arial;color:#000000\">");
                    builder.append("<td style=\"text-align:center\">");
                        builder.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;").append(item.getQuantity());
                    builder.append("</td>");
                    builder.append("<td style=\"text-align:left\">");
                        builder.append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;").append(item.getItem());
                    builder.append("</td>");  
                    builder.append("<td style=\"text-align:right\">");
                        builder.append(item.getUnitPrice()).append("&nbsp;&nbsp;&nbsp;&nbsp;");
                    builder.append("</td>");  
                    builder.append("<td style=\"text-align:right\">");
                        builder.append(item.getDiscount()).append("&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
                    builder.append("</td>"); 
                    builder.append("<td style=\"text-align:right\">");
                        builder.append(item.getAmount()).append("&nbsp;&nbsp;&nbsp;&nbsp;");
                    builder.append("</td>");  
                        
        }
    
        return builder+"";
    }
    
}
