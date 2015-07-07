package jp.controllers;

//import java.io.IOException;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
//import java.util.Map;
//import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
//import javax.faces.context.FacesContext;
import jp.entidades.Cartera;
import jp.entidades.CarteraFactura;
import jp.entidades.Cliente;
import jp.entidades.Empleado;
import jp.entidades.Factura;
//import jp.entidades.Factura;
import jp.facades.ClienteFacade;
import jp.facades.EmpleadoFacade;
import jp.facades.FacturaFacade;
import jp.util.JsfUtil;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@ManagedBean(name = "carteraController")
@SessionScoped
public class CarteraController implements Serializable {

    @EJB
    private FacturaFacade facade;
    @EJB
    private ClienteFacade clienteFacade;
    @EJB
    private EmpleadoFacade empleadoFacade;

    private List<Cartera> carteras;
    private Cliente cliente;
    private Empleado empleado;

//    @PostConstruct
//    public void init() {
//       getObjetos();
//        try {
//            if(cliente != null){
//                List<Factura> facturas = getFacade().getFacturasPendientesByCliente(cliente);
//                if(facturas != null && !facturas.isEmpty()){
//                    carteras.add(new Cartera(cliente, facturas));
//                }                
//            }else{
//                if (empleado != null) {
//                    List<Cliente> clientes = getClienteFacade().getClientesByEmpleado(empleado);
//                    for (Cliente c : clientes) {
//                        List<Factura> facturas = getFacade().getFacturasPendientesByCliente(cliente);
//                        if(facturas != null && !facturas.isEmpty()){
//                            carteras.add(new Cartera(c, facturas));
//                        }
//                    }
//                }else{
//                    carteras = new ArrayList<>();
//                }
//            }
//        } catch (Exception e) {
//            System.out.println("==========ERROR==========");
//            e.printStackTrace();
//            System.out.println("==========ERROR==========");
//        }
//    }
    public CarteraController() {
    }

    public FacturaFacade getFacade() {
        return facade;
    }

    public ClienteFacade getClienteFacade() {
        return clienteFacade;
    }

    public EmpleadoFacade getEmpleadoFacade() {
        return empleadoFacade;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Empleado getEmpleado() {
        return empleado;
    }

    public void setEmpleado(Empleado empleado) {
        this.empleado = empleado;
    }

    public List<Cartera> getCarteras() {
        return carteras;
    }

//    public void redirect() throws IOException {
//        String url = "index.xhtml?";
//        if (cliente != null) {
//            url += "&cliente=" + cliente.getId();
//        } else {
//            if (empleado != null) {
//                url += "&empleado=" + empleado.getId();
//            }
//        }
//        FacesContext.getCurrentInstance().getExternalContext().redirect(url);
//    }
//    public void getObjetos() {
//        Map<String, String> requestMap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
//        FacesContext.getCurrentInstance().getExternalContext().getSession(true);
//
//        try {
//            long idCliente = Long.valueOf(requestMap.get("cliente"));
//            cliente = clienteFacade.find(idCliente);
//        } catch (Exception e) {
//            cliente = null;
//            System.out.println("ERROR ==> " + e.getMessage());
//        }
//
//        try {
//            long idEmpleado = Long.valueOf(requestMap.get("empleado"));
//            empleado = empleadoFacade.find(idEmpleado);
//        } catch (Exception e) {
//            empleado = null;
//            System.out.println("ERROR ==> " + e.getMessage());
//        }
//    }
    public void generarReporte() {
        carteras = new ArrayList<>();
        try {
            if (cliente != null) {
                List<Factura> facturas = getFacade().getFacturasPendientesByCliente(cliente);
                if (facturas != null && !facturas.isEmpty()) {
                    List<CarteraFactura> carteraFacturas = getListCarteraFacturaByFacturas(facturas);
                    Cartera cartera = new Cartera();
                    cartera.setCliente(cliente);
                    cartera.setListCarteraFacturas(carteraFacturas);
                    carteras.add(cartera);
                }
            } else {
                if (empleado != null) {
                    List<Cliente> clientes = getClienteFacade().getClientesByEmpleado(empleado);
                    for (Cliente c : clientes) {
                        List<Factura> facturas = getFacade().getFacturasPendientesByCliente(cliente);
                        if (facturas != null && !facturas.isEmpty()) {
                            List<CarteraFactura> carteraFacturas = getListCarteraFacturaByFacturas(facturas);
                            Cartera cartera = new Cartera();
                            cartera.setCliente(cliente);
                            cartera.setListCarteraFacturas(carteraFacturas);
                            carteras.add(cartera);
                        }
                    }
                } else {
                    carteras = new ArrayList<>();
                }
            }

            if (carteras != null && !carteras.isEmpty()) {
                File reporte1 = new File(JsfUtil.getRutaReporte("cartera.jasper"));
                File reporte2 = new File(JsfUtil.getRutaReporte("subReporteClientes.jasper"));
                JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(carteras);
                if (reporte1.exists() && reporte2.exists()) {
                    try {
                        JasperPrint jasperPrint = JasperFillManager.fillReport(reporte1.getPath(), new HashMap(), dataSource);
                        HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
                        response.addHeader("Content-disposition", "attachment; filename=cartera.pdf");
                        ServletOutputStream sos = response.getOutputStream();
                        JasperExportManager.exportReportToPdfStream(jasperPrint, sos);
                        FacesContext.getCurrentInstance().responseComplete();
                    } catch (JRException | IOException e) {
                        JsfUtil.addErrorMessage("Ha ocurrido un error durante la generacion"
                                + " del reporte, Por favor intente de nuevo, si el error persiste"
                                + " comuniquese con el administrador del sistema");

                        e.printStackTrace();
                    }
                } else {
                    if (reporte1.exists()) {
                        JsfUtil.addErrorMessage("No existe el archivo " + reporte1.getName());
                    }
                    if (reporte2.exists()) {
                        JsfUtil.addErrorMessage("No existe el archivo " + reporte2.getName());
                    }
                }
            } else {
                JsfUtil.addErrorMessage("No hay datos para generar el reporte");
            }

        } catch (Exception e) {
            System.out.println("==========ERROR==========");
            e.printStackTrace();
            System.out.println("==========ERROR==========");
        }
    }

    private List<CarteraFactura> getListCarteraFacturaByFacturas(List<Factura> facturas) {
        List<CarteraFactura> carteraFacturas = new ArrayList<>();
        for (Factura factura : facturas) {
            CarteraFactura cf = new CarteraFactura();
            cf.setFactura(factura.getOrdenPedido());

            long dias = getDias(factura);

            if (dias <= 29) {
                cf.setActual(factura.getSaldo());
            } else {
                cf.setActual(0.0);
            }

            if (dias == 30) {
                cf.setDias30(factura.getSaldo());
            } else {
                cf.setDias30(0.0);
            }

            if (dias > 30 && dias < 60) {
                cf.setMas30dias(factura.getSaldo());
            } else {
                cf.setMas30dias(0.0);
            }

            if (dias > 60 && dias < 90) {
                cf.setMas60dias(factura.getSaldo());
            } else {
                cf.setMas60dias(0.0);
            }

            if (dias > 90) {
                cf.setMas90dias(factura.getSaldo());
            } else {
                cf.setMas90dias(0.0);
            }

            carteraFacturas.add(cf);
        }
        return carteraFacturas;
    }

    private long getDias(Factura factura) {
        long milisegundosPorDia = 24 * 60 * 60 * 1000;
        Calendar fechaActual = Calendar.getInstance();
        return (fechaActual.getTime().getTime() - factura.getFecha().getTime()) / milisegundosPorDia;
    }
}
