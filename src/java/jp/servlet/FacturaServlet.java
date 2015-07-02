package jp.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

    private final static int FILAS_DOCUMENTO = 30;
    private final static int FILAS_ENCABEZADO = 4;
    private final static int FILAS_DATOS_CLIENTE = 5;
    private final static int FILAS_FOOTER_TOTALES = 5;
    private final static int FILAS_FOOTER_OBSERVACIONES = 3;
    private final static int PRODUCTOS_POR_PAGINA = 7;
    @Inject
    FacturaFacade facturaFacade;
    @Inject
    ProductoFacade productoFacade;
    int paginaActual, totalPaginas, tamanoListaProductos;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/plain;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */

            Factura f = facturaFacade.getFirstFactura();
            List<FacturaProducto> facturaProductos = productoFacade.getFacturaProductosByFactura(f);

            List<String> listaProductos = getListaProductos(facturaProductos, f);
            tamanoListaProductos = listaProductos.size() / 5;
            System.out.println("Lineas de productos--> " + tamanoListaProductos);
            setTotalPaginas();

            List<String> headerFactura = getHeaderFactura(f);
            System.out.println("Lineas de headerFactura--> " + headerFactura.size());

            List<String> headerDatosCliente = getHeaderDatosCliente(f);
            System.out.println("Lineas de datosCliente--> " + headerDatosCliente.size());

            List<String> footerTotales = getFooterTotales(f, facturaProductos);
            System.out.println("Lineas de datosFooterTotales--> " + footerTotales.size());

            List<String> footerObservaciones = getFooterObservaciones(f);
            System.out.println("Lineas de datosFooterObservaciones--> " + footerObservaciones.size());

            StringBuilder sb = new StringBuilder();
            if (tamanoListaProductos <= PRODUCTOS_POR_PAGINA) {
                sb.append(headerFactura).append(headerDatosCliente).append(listaProductos).append(footerTotales).append(footerObservaciones);
            } else {
                sb.append(headerFactura).append(headerDatosCliente).append(listaProductos).append(footerTotales).append(footerObservaciones);
            }

            out.println(removeAccents(sb.toString()));
        }
    }

    public List<String> getHeaderFactura(Factura f) {
        paginaActual++;
        List<String> lista = new ArrayList<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        String idFactura = rellenar("" + f.getId(), " ", 13, false);
        String fechaFactura = rellenar(dateFormat.format(f.getFecha()).toUpperCase(), " ", 13, false);
        String paginado = rellenar(getNumberPages(paginaActual, totalPaginas), " ", 42, false);

//        StringBuilder cadena = new StringBuilder();
        lista.add("LINDA INES AREVALO PAMO                      **** J I M M Y   P R O F E S S I O N A L ****" + paginado + "\n");
        lista.add("NIT 51.898.276-5                                         CALLE 17 # 83 A - 10                         FACTURA DE VENTA:" + idFactura + "\n");
        lista.add("REGIMEN SIMPLIFICADO                                       (+57 2) 372 23 26                                     FECHA:" + fechaFactura + "\n");
        lista.add("======================================================================================================================================\n");
        return lista;
    }

    public String getNumberPages(int paginaActual, int paginasTotales) {
        return "Pagina " + paginaActual + " de " + paginasTotales + "";
    }

    public List<String> getHeaderDatosCliente(Factura f) {
        List<String> lista = new ArrayList<>();

        String nombre = rellenar(f.getCliente().getNombre(), " ", 62, true).toUpperCase();
        String tipoCliente = rellenar(f.getCliente().getTipo().toString(), " ", 15, false).toUpperCase();
        String direccion = rellenar(f.getCliente().getDireccion(), " ", 23, true).toUpperCase();
        String barrio = rellenar(f.getCliente().getBarrio(), " ", 25, true).toUpperCase();
        String ciudad = rellenar(f.getCliente().getCiudad().toString(), " ", 32, true).toUpperCase();
        String telefono = f.getCliente().getTelefonos().trim();
        telefono = rellenar(telefono, " ", 15, false).toUpperCase();
        String codigoCliente = rellenar(f.getCliente().getDocumento(), " ", 26, true).toUpperCase();
        String formaPago = rellenar(TipoPago.getFromValue(f.getTipoPago()).getDetalle(), " ", 18, true).toUpperCase();
        String tipoMoneda = rellenar(f.getDolar() ? "DOLARES" : "PESOS", " ", 23, true).toUpperCase();

        lista.add("                                                *** D A T O S  D E L  C L I E N T E ***                                               \n");
        lista.add("Nombre del cliente o establecimiento: " + nombre + "Tipo de cliente: " + tipoCliente + "    \n");
        lista.add("Direccion: " + direccion + "Barrio: " + barrio + "Ciudad: " + ciudad + "Telefono: " + telefono + "    \n");
        lista.add("                  Codigo Cliente: " + codigoCliente + "Forma de Pago: " + formaPago + "Tipo de moneda: " + tipoMoneda + "\n");
        lista.add("======================================================================================================================================\n");

        return lista;
    }

    public List<String> getListaProductos(List<FacturaProducto> facturaProductos, Factura f) {
        List<String> lista = new ArrayList<>();

        lista.add("                                             *** P R O D U C T O S  D E L  P E D I D O ***                                            \n");
        lista.add("CODIGO                          DESCRIPCION                                         VENTA      BONIF.                      PRECIO     \n");
        for (FacturaProducto facturaProducto : facturaProductos) {
            lista.add(rellenar(facturaProducto.getProducto().getCodigo(), " ", 32, true));
            lista.add(rellenar(facturaProducto.getProducto().getDescripcion(), " ", 54, true));
            lista.add(rellenar("" + facturaProducto.getUnidadesVenta(), " ", 11, true));
            lista.add(rellenar("" + facturaProducto.getUnidadesBonificacion(), " ", 12, true));
            lista.add(rellenar("" + facturaProducto.getPrecio(), " ", 21, false) + "\n");
        }

//        if (facturaProductos.size() <= PRODUCTOS_POR_PAGINA) {
        return lista;
//        } else {
//            return getHeaderFactura(f).toUpperCase()+cadena;
//        }
    }

    public List<String> getFooterTotales(Factura factura, List<FacturaProducto> facturaProductos) {
        List<String> lista = new ArrayList<>();
        int unidadVentas = 0, unidadBonificaciones = 0;

        for (FacturaProducto facturaProducto : facturaProductos) {
            unidadVentas += facturaProducto.getUnidadesVenta();
            unidadBonificaciones += facturaProducto.getUnidadesBonificacion();
        }

        String ventas = rellenar("" + unidadVentas, " ", 11, true);
        String bonificaciones = rellenar("" + unidadBonificaciones, " ", 11, true);
        String totalBruto = rellenar("" + factura.getTotalBruto(), " ", 22, false);
        String recargo = rellenar("%" + factura.getDescuento(), " ", 44, false);
        String totalPagar = rellenar("" + factura.getTotalPagar(), " ", 44, false);

        lista.add("--------------------------------------------------------------------------------------------------------------------------------------\n");
        lista.add("                                                                      TOTAL BRUTO     " + ventas + bonificaciones + totalBruto + "\n");
        lista.add("                                                                          RECARGO     " + recargo + "\n");
        lista.add("                                                                    TOTAL A PAGAR     " + totalPagar + "\n");
        lista.add("======================================================================================================================================\n");
        return lista;
    }

    private List<String> getFooterObservaciones(Factura factura) {
//        StringBuilder cadena = new StringBuilder();
        List<String> lista = new ArrayList<>();

        String observaciones = rellenar(factura.getObservaciones(), " ", 119, true);
        lista.add("Observaciones: " + observaciones + "\n\n");
        lista.add("Firma del Vendedor : ________________________ C.C: ______________  Firma del Cliente : __________________________ C.C: ______________ ");
        return lista;
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

    private static String removeAccents(String string) {
        string = Normalizer.normalize(string, Normalizer.Form.NFD);
        string = string.replaceAll("[^\\p{ASCII}]", "");
        return string;
    }

    private void setTotalPaginas() {
//        int tamanoListaProductos = listaProductos.size() / 5;
        if (tamanoListaProductos > PRODUCTOS_POR_PAGINA) {
            totalPaginas = tamanoListaProductos / PRODUCTOS_POR_PAGINA;
            if (PRODUCTOS_POR_PAGINA % tamanoListaProductos != 0) {
                totalPaginas += 1;
            }
        } else {
            totalPaginas = 1;
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
        return "Servlet de Factura";
    }// </editor-fold>

}
