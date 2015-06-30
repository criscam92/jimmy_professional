package jp.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.List;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import jp.entidades.Factura;
import jp.entidades.FacturaProducto;
import jp.facades.FacturaFacade;
import jp.facades.ProductoFacade;
import jp.util.TipoPago;

@WebServlet(name = "FacturaServlet", urlPatterns = {"/FacturaServlet"})
public class FacturaServlet extends HttpServlet {

    private final static int filasDocumento = 30;
    private final static int filasEncabezado = 4;
    private final static int filasDatosCliente = 5;
    private final static int filasFooterTotales = 5;
    private final static int filasFooterObservaciones = 3;
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
            /*String idFactura = rellenar("" + 111l, " ", 13, false);
             Date fechaActual = Calendar.getInstance().getTime();*/

            Factura f = facturaFacade.getFirstFactura();

            String headerFactura = getHeaderFactura(f);
            out.println(headerFactura);

            String headerDatosCliente = getHeaderDatosCliente(f);
            out.println(headerDatosCliente);

            List<FacturaProducto> facturaProductos = productoFacade.getFacturaProductosByFactura(f);
            String listaProductos = getListaProductos(facturaProductos, f);
            out.println(listaProductos);

            String footerTotales = getFooterTotales(f, facturaProductos);
             out.println(footerTotales);
             
            String footerObservaciones = getFooterObservaciones(f);
            out.println(footerObservaciones);
        }
    }

    public String getHeaderFactura(Factura f) {
        paginaActual++;
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        String idFactura = rellenar("" + f.getId(), "#", 13, false);
        String fechaFactura = rellenar(dateFormat.format(f.getFecha()).toUpperCase(), "#", 13, false);
        String paginado = rellenar(getNumberPages(paginaActual, 2), "#", 42, false);

        StringBuilder cadena = new StringBuilder();
        cadena.append("LINDA INES AREVALO PAMO                      **** J I M M Y   P R O F E S S I O N A L ****").append(paginado).append("\n")
                .append("NIT 51.898.276-5                                         CALLE 17 # 83 A - 10                         FACTURA DE VENTA:").append(idFactura).append("\n")
                .append("REGIMEN SIMPLIFICADO                                       (+57 2) 372 23 26                                     FECHA:").append(fechaFactura).append("\n")
                .append("======================================================================================================================================\n");
        return cadena.toString();
    }

    public String getNumberPages(int paginaActual, int paginasTotales) {
        return "Pagina " + paginaActual + " de " + paginasTotales + "";
    }

    public String getHeaderDatosCliente(Factura f) {

        StringBuilder cadena = new StringBuilder();
        String nombre = rellenar(f.getCliente().getNombre(), "#", 62, true).toUpperCase();
        String tipoCliente = rellenar(f.getCliente().getTipo().toString(), "#", 15, false).toUpperCase();
        String direccion = rellenar(f.getCliente().getDireccion(), "#", 23, true).toUpperCase();
        String barrio = rellenar(f.getCliente().getBarrio(), "#", 25, true).toUpperCase();
        String ciudad = rellenar(f.getCliente().getCiudad().toString(), "#", 32, true).toUpperCase();
        String telefono = f.getCliente().getTelefonos().trim();
        telefono = rellenar(telefono, "#", 15, false).toUpperCase();
        String codigoCliente = rellenar(f.getCliente().getDocumento(), "#", 26, true).toUpperCase();
        String formaPago = rellenar(TipoPago.getFromValue(f.getTipoPago()).getDetalle(), "#", 18, true).toUpperCase();
        String tipoMoneda = rellenar(f.getDolar() ? "DOLARES" : "PESOS", "#", 23, true).toUpperCase();

        cadena.append("                                                *** D A T O S  D E L  C L I E N T E ***                                               \n")
                .append("Nombre del cliente o establecimiento: ").append(nombre).append("Tipo de cliente: ").append(tipoCliente).append("    \n")
                .append("Direccion: ").append(direccion).append("Barrio: ").append(barrio).append("Ciudad: ").append(ciudad).append("Telefono: ").append(telefono).append("    \n")
                .append("                  Codigo Cliente: ").append(codigoCliente).append("Forma de Pago: ").append(formaPago).append("Tipo de moneda: ").append(tipoMoneda).append("\n")
                .append("======================================================================================================================================\n");

        return cadena.toString();
    }

    public String getListaProductos(List<FacturaProducto> facturaProductos, Factura f) {
        StringBuilder cadena = new StringBuilder();
        cadena.append("                                             *** P R O D U C T O S  D E L  P E D I D O ***                                            \n")
                .append("CODIGO                          DESCRIPCION                                         VENTA      BONIF.                      PRECIO     \n");
        for (FacturaProducto facturaProducto : facturaProductos) {
            cadena.append(rellenar(facturaProducto.getProducto().getCodigo(), "#", 32, true))
                  .append(rellenar(facturaProducto.getProducto().getDescripcion(), "#", 54, true))
                  .append(rellenar(""+facturaProducto.getUnidadesVenta(), "&", 11, true))
                  .append(rellenar(""+facturaProducto.getUnidadesBonificacion(), "%", 12, true))
                  .append(rellenar(""+facturaProducto.getPrecio(), "$", 21, false)).append("\n");
        }

        if (facturaProductos.size() <= 20) {
            return cadena.toString();
        } else {
            return getHeaderFactura(f).toUpperCase()+cadena;
        }
    }

    public String getFooterTotales(Factura factura, List<FacturaProducto> facturaProductos) {
        StringBuilder cadena = new StringBuilder();
        int unidadVentas = 0, unidadBonificaciones = 0;
        
        for (FacturaProducto facturaProducto : facturaProductos) {
            unidadVentas += facturaProducto.getUnidadesVenta();
            unidadBonificaciones += facturaProducto.getUnidadesBonificacion();
        }
        
        String ventas = rellenar(""+unidadVentas, "#", 11, true);
        String bonificaciones = rellenar(""+unidadBonificaciones, "%", 11, true);
        String totalBruto = rellenar(""+factura.getTotalBruto(), "&", 22, false);
        String totalPagar = rellenar(""+factura.getTotalPagar(), "#", 6, false);
        String recargo = rellenar(""+factura.getDescuento(), "#", 6, false);        

        cadena.append("--------------------------------------------------------------------------------------------------------------------------------------\n")
                .append("                                                                      TOTAL BRUTO     ").append(ventas).append(bonificaciones).append(totalBruto).append("\n")
                .append("                                                                          RECARGO     0%                                       0$     \n")
                .append("                                                                    TOTAL A PAGAR                                         13.000$     \n")
                .append("======================================================================================================================================\n");
        return cadena.toString();
    }

    private String getFooterObservaciones(Factura factura) {
        StringBuilder cadena = new StringBuilder();

        String observaciones = rellenar(factura.getObservaciones(), "#", 119, true);
        cadena.append("Observaciones: ").append(observaciones).append("\n\n")
                .append("Firma del Vendedor : ________________________ C.C: ______________  Firma del Cliente : __________________________ C.C: ______________ ");
        return cadena.toString();
    }

    private static String rellenar(String cadenaInicial, String cadenaRelleno, int tamanoFinal, boolean rellenarAlaDerecha) {
        try {
            if (cadenaInicial.length() == tamanoFinal) {

            } else if (cadenaInicial.length() > tamanoFinal) {
                if (rellenarAlaDerecha) {
                    cadenaInicial = cadenaInicial.substring(0, tamanoFinal);
                } else {
                    int indice = cadenaInicial.length() - tamanoFinal;
                    cadenaInicial = cadenaInicial.substring(indice, cadenaInicial.length());
                }
            } else {
                while (cadenaInicial.length() < tamanoFinal) {
                    if (rellenarAlaDerecha) {
                        cadenaInicial = cadenaInicial.concat(cadenaRelleno);
                    } else {
                        cadenaInicial = cadenaRelleno.concat(cadenaInicial);
                    }
                }
                cadenaInicial = rellenar(cadenaInicial, cadenaRelleno, tamanoFinal, rellenarAlaDerecha);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return cadenaInicial;
    }
    
    public static String removeAccents(String string){
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
