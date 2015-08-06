package jp.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jp.entidades.Parametros;
import jp.entidades.Visita;
import jp.facades.ParametrosFacade;
import jp.facades.VisitaFacade;
import jp.util.EstadoVisita;
import jp.util.Mail;

public class CorreoServlet extends HttpServlet {

    @EJB
    private VisitaFacade visitaFacade;
    @EJB
    private ParametrosFacade parametrosFacade;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            Calendar fecha1 = Calendar.getInstance();
            fecha1.set(Calendar.HOUR_OF_DAY, 0);
            fecha1.set(Calendar.MINUTE, 0);
            fecha1.set(Calendar.SECOND, 0);

            Calendar fecha2 = Calendar.getInstance();
            fecha2.setTime(fecha1.getTime());
            fecha2.set(Calendar.HOUR_OF_DAY, 23);
            fecha2.set(Calendar.MINUTE, 59);
            fecha2.set(Calendar.SECOND, 59);
            fecha2.add(Calendar.DATE, 7);
            List<Visita> visitas = visitaFacade.getVisitasSiguienteSemana(fecha1.getTime(), fecha2.getTime());
            if (!visitas.isEmpty()) {
                final Parametros parametros = parametrosFacade.getParametros();
                String mensajeCorreo = "";
                mensajeCorreo += "<div style='width: 600px; height: auto; margin: 0px auto; font-size:15px;'>";
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMM-yyyy h:mm a");
                for (Visita visita : visitas) {
                    mensajeCorreo += "<p><span style=\"font-weight: bold\">Código de Visita: </span>" + visita.getId() + "</p>";
                    mensajeCorreo += "<p><span style=\"font-weight: bold\">Fecha: </span>" + dateFormat.format(visita.getFecha()) + "</p>";
                    mensajeCorreo += "<p><span style=\"font-weight: bold\">Observaciones: </span>" + visita.getObservacionesCliente() + "</p>";
                    mensajeCorreo += "<p><span style=\"font-weight: bold\">Estado: </span>" + EstadoVisita.getFromValue(visita.getEstado()) + "</p>";
                    mensajeCorreo += "<p><span style=\"font-weight: bold\">Procedimiento: </span>" + visita.getProcedimiento().toString() + "</p>";
                    mensajeCorreo += "<p><span style=\"font-weight: bold\">Empleado: </span>" + visita.getEmpleado().toString() + "</p>";
                    mensajeCorreo += "<p><span style=\"font-weight: bold\">Cliente: </span>" + visita.getCliente().toString() + "</p>";
                    mensajeCorreo += "<p><a href='"+parametros.getUrlBase()+"?redirect="+parametros.getUrlBase()+"content/visita/Realizar.xhtml?id="+visita.getId()+"' style=\"font-weight: bold\">Abrir en la App</a></p><hr/>";
                }
                mensajeCorreo += "</div>";
                Mail.sendMail(parametrosFacade, "Visitas pendientes para esta semana", mensajeCorreo);
            }

        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
