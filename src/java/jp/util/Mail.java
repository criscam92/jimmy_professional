package jp.util;

import java.util.Properties;
import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import jp.entidades.Parametros;
import jp.facades.ParametrosFacade;

public class Mail {
    
    private static String getBanner(){
        return "<p style=\"width:250px;max-width: 250px;margin: 0px auto; padding: 5px;\"><img style=\"width:100%;\" src=\"https://app.jimmyprofessional.com/resources/src/logo.png\""
                + " alt=\"Jimmy Professional System\" /><br/>";
    }
    
    public static void sendMail(final ParametrosFacade parametrosFacade, final String asunto, final String mensaje){
        
        Thread hilo = new Thread(new Runnable() {

            @Override
            public void run() {
                System.out.println("Se envia el puto correo");
                try {
                    final Parametros parametros = parametrosFacade.getParametros();
                    Properties props = new Properties();
                    props.put("mail.smtp.auth", "true");
                    props.put("mail.smtp.starttls.enable", parametros.getSsl());
                    props.put("mail.smtp.host", parametros.getServidorCorreo());
                    props.put("mail.smtp.port", parametros.getPuertoSmtp());

                    Session session = Session.getInstance(props,
                            new javax.mail.Authenticator() {
                        @Override
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(parametros.getCorreo(), parametros.getContrasenhaCorreo());
                        }
                    });


                        final String direccionDestino = parametros.getCorreo();
                        
                        System.out.println("La dirección destino es: "+direccionDestino);
                        
                        MimeMessage message = new MimeMessage(session);
                        message.setFrom(new InternetAddress(parametros.getCorreo(),parametros.getNombre()));
                        message.setRecipients(Message.RecipientType.TO,
                                InternetAddress.parse(direccionDestino));
                        message.setSubject(asunto);
                        String contenido = mensaje;
                        System.out.println("Contenido => " + contenido);
                        message.setText(getBanner()+contenido+"</p>", "utf-8", "html");                        
                        
                        Transport.send(message);

                        System.out.println("Se envio el correo => "+asunto+" al correo: "+direccionDestino);


                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, "Hilo envio de mensaje de correo");
        
        hilo.setPriority(Thread.MIN_PRIORITY);
        hilo.start();
        
    }
}
