package jp.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.Normalizer;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jp.entidades.Factura;
import jp.entidades.FacturaProducto;
import jp.facades.FacturaFacade;
import jp.facades.ProductoFacade;
import jp.util.ImpresionFactura;

//@WebServlet(name = "FacturaServlet", urlPatterns = {"/FacturaServlet"})
public class FacturaServlet extends HttpServlet {

    @Inject
    FacturaFacade facturaFacade;
    @Inject
    ProductoFacade productoFacade;
    int paginaActual, totalPaginas;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */

            Factura f = facturaFacade.getFirstFactura();
            List<FacturaProducto> facturaProductos = productoFacade.getFacturaProductosByFactura(f);
            facturaProductos.addAll(facturaProductos);
//            facturaProductos.addAll(facturaProductos);
//            facturaProductos.addAll(facturaProductos);
            

            ImpresionFactura impresionFactura = new ImpresionFactura(f, facturaProductos);

            out.println(removeAccents(impresionFactura.toString()));
        }
    }

    private static String removeAccents(String string) {
        string = Normalizer.normalize(string, Normalizer.Form.NFD);
        string = string.replaceAll("[^\\p{ASCII}]", "");
        return string;
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
        return "Servlet de Factura";
    }// </editor-fold>

}
