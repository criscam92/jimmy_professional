/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.util;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import jp.entidades.Factura;
import jp.entidades.FacturaProducto;

/**
 *
 * @author gurzaf
 */
public class ImpresionFactura {
    
    private final DecimalFormat df;
    
    private final Factura factura;
    private final List<FacturaProducto> productos;
    
    private static final int LINEAS_POR_PAGINA = 30;
    
    private static final int LINEAS_HEADER_FACTURA = 4;
    private static final int LINEAS_HEADER_DATOS_CLIENTE = 5;
    private static final int LINEAS_HEADER_DATOS_PRODUCTO = 2;
    private static final int LINEAS_FOOTER_TOTALES = 5;
    private static final int LINEAS_FOOTER_OBSERVACIONES = 3;
    
    private int CANTIDAD_BASE_PRODUCTOS_PRIMERA_PAGINA, CANTIDAD_MAXIMA_PRODUCTOS_PRIMERA_PAGINA, 
            CANTIDAD_BASE_PRODUCTOS_OTRA_PAGINA, CANTIDAD_MAXIMA_PRODUCTOS_OTRA_PAGINA;
    
    public ImpresionFactura(Factura factura, List<FacturaProducto> productos){
        this.factura = factura;
        this.productos = productos;
        this.df = new DecimalFormat("#.#");
        this.df.setMaximumFractionDigits(2);
    }
    
    @Override
    public String toString(){
        try {
            StringBuilder sb = new StringBuilder();
            String linebreak = System.getProperty("line.separator");
            List<List<String>> lineas = getLineasImpresion();
            for (List<String> listaString : lineas) {
                for (String linea : listaString) {
                    sb.append(linea).append(linebreak);
                }
            }
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private List<List<String>> getLineasImpresion() {
        List<List<String>> lineas = new ArrayList<>();
        
        int totalPaginas = getTotalPaginas();
        
        List<String> productosList = getListaProductos(productos, factura);
        
        lineas.add(new ArrayList<String>());
        lineas.get(0).addAll(getHeaderFactura(factura, 0, totalPaginas));
        lineas.get(0).addAll(getHeaderDatosCliente(factura));
        lineas.get(0).addAll(getHeaderListaProductos());
        
        if(totalPaginas==1){
            
            lineas.get(0).addAll(productosList);
            lineas.get(0).addAll(getFooterTotales(factura, productos));
            lineas.get(0).addAll(getFooterObservaciones(factura));
            
        }else{
            
            int indiceProductos = 0;
            boolean imprimirHeaderProductos = true;
            
            while (lineas.get(0).size()<LINEAS_POR_PAGINA) {                
                if(indiceProductos<productosList.size()){
                    lineas.get(0).add(productosList.get(indiceProductos));
                    indiceProductos++;
                }else{
                    imprimirHeaderProductos = false;
                    lineas.get(0).add(" ");
                }
            }
            
            for (int i = 1; i < totalPaginas; i++) {
                lineas.add(i, new ArrayList<String>());
                lineas.get(i).addAll(getHeaderFactura(factura, i, totalPaginas));
                if(imprimirHeaderProductos){
                    lineas.get(i).addAll(getHeaderListaProductos());
                }
                while (lineas.get(i).size()<LINEAS_POR_PAGINA && indiceProductos<productosList.size()) {
                    lineas.get(i).add(productosList.get(indiceProductos));
                    indiceProductos++;
                }
                if(i==totalPaginas-1){
                    lineas.get(i).addAll(getFooterTotales(factura, productos));
                    lineas.get(i).addAll(getFooterObservaciones(factura));
                }
            }
            
        }
        
        return lineas;
        
    }
    
    private List<String> getHeaderFactura(Factura f, int paginaActual, int totalPaginas) {
        paginaActual++;
        List<String> lista = new ArrayList<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd MMM yyyy");
        String idFactura = rellenar("" + f.getId(), " ", 13, false);
        String fechaFactura = rellenar(dateFormat.format(f.getFecha()).toUpperCase(), " ", 13, false);
        String paginado = rellenar(getPaginado(paginaActual, totalPaginas), " ", 42, false);

        lista.add("LINDA INES AREVALO PAMO                      **** J I M M Y   P R O F E S S I O N A L ****" + paginado );
        lista.add("NIT 51.898.276-5                                         CALLE 17 # 83 A - 10                         FACTURA DE VENTA:" + idFactura);
        lista.add("REGIMEN SIMPLIFICADO                                       (+57 2) 372 23 26                                     FECHA:" + fechaFactura);
        lista.add("======================================================================================================================================");
        return lista;
    }
    
    private List<String> getHeaderDatosCliente(Factura f) {
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

        lista.add("                                                *** D A T O S  D E L  C L I E N T E ***                                               ");
        lista.add("Nombre del cliente o establecimiento: " + nombre + "Tipo de cliente: " + tipoCliente + "    ");
        lista.add("Direccion: " + direccion + "Barrio: " + barrio + "Ciudad: " + ciudad + "Telefono: " + telefono + "    ");
        lista.add("                  Codigo Cliente: " + codigoCliente + "Forma de Pago: " + formaPago + "Tipo de moneda: " + tipoMoneda );
        lista.add("======================================================================================================================================");

        return lista;
    }
    
    private List<String> getHeaderListaProductos(){
        List<String> lista = new ArrayList<>();
        lista.add("                                             *** P R O D U C T O S  D E L  P E D I D O ***                                            ");
        lista.add("CODIGO                          DESCRIPCION                                         VENTA      BONIF.                      PRECIO     ");
        return lista;
    }
    
    private String getPaginado(int paginaActual, int paginasTotales) {
        return "Pagina " + paginaActual + " de " + paginasTotales + "";
    }
    
    private List<String> getFooterTotales(Factura factura, List<FacturaProducto> facturaProductos) {
        List<String> lista = new ArrayList<>();
        int unidadVentas = 0, unidadBonificaciones = 0;

        for (FacturaProducto facturaProducto : facturaProductos) {
            unidadVentas += facturaProducto.getUnidadesVenta();
            unidadBonificaciones += facturaProducto.getUnidadesBonificacion();
        }

        String ventas = rellenar("" + unidadVentas, " ", 11, true);
        String bonificaciones = rellenar("" + unidadBonificaciones, " ", 11, true);
        String totalBruto = rellenar("" + df.format(factura.getTotalBruto()), " ", 22, false);
        String recargo = rellenar("%" + df.format(factura.getDescuento()), " ", 44, false);
        String totalPagar = rellenar("" + df.format(factura.getTotalPagar()), " ", 44, false);

        lista.add("--------------------------------------------------------------------------------------------------------------------------------------");
        lista.add("                                                                      TOTAL BRUTO     " + ventas + bonificaciones + totalBruto);
        lista.add("                                                                          RECARGO     " + recargo );
        lista.add("                                                                    TOTAL A PAGAR     " + totalPagar );
        lista.add("======================================================================================================================================");
        return lista;
    }
    
    private List<String> getFooterObservaciones(Factura factura) {
        List<String> lista = new ArrayList<>();
        String observaciones = rellenar(factura.getObservaciones(), " ", 119, true);
        lista.add("Observaciones: " + observaciones);
        lista.add("");
        lista.add("Firma del Vendedor : ________________________ C.C: ______________  Firma del Cliente : __________________________ C.C: ______________ ");
        return lista;
    }
    
    private List<String> getListaProductos(List<FacturaProducto> facturaProductos, Factura f) {
        List<String> lista = new ArrayList<>();

        for (FacturaProducto facturaProducto : facturaProductos) {
            StringBuilder sb = new StringBuilder();
            sb.append(rellenar(facturaProducto.getProducto().getCodigo().trim(), " ", 10, true));
            sb.append(rellenar(facturaProducto.getProducto().getDescripcion().trim(), " ", 76, true));
            sb.append(rellenar("" + facturaProducto.getUnidadesVenta(), " ", 11, true));
            sb.append(rellenar("" + facturaProducto.getUnidadesBonificacion(), " ", 12, true));
            sb.append(rellenar("" + df.format(facturaProducto.getPrecio()), " ", 21, false));
            lista.add(sb.toString());
        }

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

    private int getTotalPaginas() {
        
        calcularCantidadesProductos();
        
        System.out.println("Cantidad Productos: "+productos.size());
        
        if(productos.size()<=CANTIDAD_BASE_PRODUCTOS_PRIMERA_PAGINA){
            
            return 1;
            
        }else{
            if(productos.size() > CANTIDAD_MAXIMA_PRODUCTOS_PRIMERA_PAGINA){
                
                int cantidadProductosAdicionales = productos.size() - CANTIDAD_MAXIMA_PRODUCTOS_PRIMERA_PAGINA;
                int cantidadPaginas = 1;
                
                do{
                    
                    if(cantidadProductosAdicionales<=CANTIDAD_BASE_PRODUCTOS_OTRA_PAGINA){
                        cantidadPaginas++;
                        return cantidadPaginas;
                    }else{
                        cantidadProductosAdicionales -= CANTIDAD_MAXIMA_PRODUCTOS_OTRA_PAGINA;
                        cantidadPaginas++;
                    }
                    
                }while (cantidadProductosAdicionales > CANTIDAD_MAXIMA_PRODUCTOS_OTRA_PAGINA);
                
                return cantidadPaginas;
                
            }
            
            return 2;
            
        }
    }
    
    private void calcularCantidadesProductos(){
        CANTIDAD_BASE_PRODUCTOS_PRIMERA_PAGINA = LINEAS_POR_PAGINA - (LINEAS_HEADER_FACTURA + LINEAS_HEADER_DATOS_CLIENTE + LINEAS_HEADER_DATOS_PRODUCTO + LINEAS_FOOTER_TOTALES + LINEAS_FOOTER_OBSERVACIONES);
        CANTIDAD_MAXIMA_PRODUCTOS_PRIMERA_PAGINA = LINEAS_POR_PAGINA - (LINEAS_HEADER_FACTURA + LINEAS_HEADER_DATOS_CLIENTE + LINEAS_HEADER_DATOS_PRODUCTO);
        CANTIDAD_BASE_PRODUCTOS_OTRA_PAGINA = LINEAS_POR_PAGINA - (LINEAS_HEADER_FACTURA + LINEAS_HEADER_DATOS_PRODUCTO + LINEAS_FOOTER_TOTALES + LINEAS_FOOTER_OBSERVACIONES);
        CANTIDAD_MAXIMA_PRODUCTOS_OTRA_PAGINA = LINEAS_POR_PAGINA - (LINEAS_HEADER_FACTURA + LINEAS_HEADER_DATOS_PRODUCTO);
        System.out.println("Cantidad Lineas Por Página: "+LINEAS_POR_PAGINA);
        System.out.println("Cantidad Productos Base Primera Página: "+CANTIDAD_BASE_PRODUCTOS_PRIMERA_PAGINA);
        System.out.println("Cantidad Productos Maxima Primera Página: "+CANTIDAD_MAXIMA_PRODUCTOS_PRIMERA_PAGINA);
        System.out.println("Cantidad Productos Base Otra Página: "+CANTIDAD_BASE_PRODUCTOS_OTRA_PAGINA);
        System.out.println("Cantidad Productos Maxima Otra Página: "+CANTIDAD_MAXIMA_PRODUCTOS_OTRA_PAGINA);
    }
    
}
