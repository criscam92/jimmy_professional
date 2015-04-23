package jp.controllers;

import java.io.File;
import java.io.IOException;
import jp.entidades.Visita;
import jp.util.JsfUtil;
import jp.util.JsfUtil.PersistAction;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.event.ActionEvent;
import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import jp.entidades.Producto;
import jp.entidades.VisitaProducto;
import jp.facades.ProductoFacade;
import jp.facades.TransactionFacade;
import jp.facades.VisitaFacade;
import jp.facades.VisitaProductoFacade;
import jp.seguridad.UsuarioActual;
import jp.util.EstadoVisita;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.primefaces.event.SelectEvent;

@ManagedBean(name = "visitaController")
@SessionScoped
public class VisitaController implements Serializable {

    @EJB
    private VisitaFacade ejbFacade;
    @EJB
    private VisitaProductoFacade ejbVisitaProductoFacade;
    @Inject
    private UsuarioActual usuarioActual;
    @EJB
    private TransactionFacade transactionFacade;
    @EJB
    private ProductoFacade productoFacade;
    private List<EstadoVisita> estadoVisitasFilter = null;
    private Visita selected;
    private Producto producto;
    private int cantidad;
    private Integer diponible;
    private List<Visita> items = null;
    private List<VisitaProducto> visitaProductos = null;
    private List<VisitaProducto> visitaProductosEliminar;
    private List<VisitaProducto> visitaProductosGuardar;
    private List<VisitaProducto> visitaProductosEditar;
    private boolean create;
    private String header;

    public VisitaController() {
    }

    public List<VisitaProducto> getVisitaProductos() {
        return visitaProductos;
    }

    public Visita getSelected() {
        return selected;
    }

    public void setSelected(Visita selected) {
        this.selected = selected;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public Integer getDiponible() {
        return diponible;
    }

    public void setDiponible(Integer diponible) {
        this.diponible = diponible;
    }

    private VisitaFacade getFacade() {
        return ejbFacade;
    }

    public VisitaProductoFacade getEjbVisitaProductoFacade() {
        return ejbVisitaProductoFacade;
    }

    public TransactionFacade getTransactionFacade() {
        return transactionFacade;
    }

    public ProductoFacade getProductoFacade() {
        return productoFacade;
    }

    public List<EstadoVisita> getEstadoVisitasFilter() {
        return estadoVisitasFilter;
    }

    public void setEstadoVisitasFilter(List<EstadoVisita> estadoVisitasFilter) {
        this.estadoVisitasFilter = estadoVisitasFilter;
    }

    public void create() {
        if (create) {
            selected.setObservacionesCliente("Visita Asignada a " + selected.getEmpleado());
            selected.setEstado(EstadoVisita.PENDIENTE.getValor());
            persist(PersistAction.CREATE, JsfUtil.getMessageBundle(new String[]{"MessageVisita", "CreateSuccessF"}));
            if (!JsfUtil.isValidationFailed()) {
                items = null;
            }
        } else {
            update();
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, JsfUtil.getMessageBundle(new String[]{"MessageVisita", "UpdateSuccessF"}));
    }

    public void destroy() {
        persist(PersistAction.DELETE, JsfUtil.getMessageBundle(new String[]{"MessageVisita", "DeleteSuccessF"}));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Visita> getItems() {
        if (items == null) {
            items = getFacade().findVisitasByEmpleado(usuarioActual.get().getEmpleado(), usuarioActual.isAdmin());
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, JsfUtil.getMessageBundle("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, JsfUtil.getMessageBundle("PersistenceErrorOccured"));
            }
        }
    }

    public List<Visita> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Visita> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    private int getCantidadProductosAgregados(Producto p) {
        for (VisitaProducto vp : visitaProductos) {
            if (vp.getProducto().getId().equals(p.getId())) {
                return vp.getCantidad();
            }
        }
        return 0;
    }

    @FacesConverter(forClass = Visita.class)
    public static class VisitaControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            VisitaController controller = (VisitaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "visitaController");
            return controller.getFacade().find(getKey(value));
        }

        Long getKey(String value) {
            Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Visita) {
                Visita o = (Visita) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Visita.class.getName()});
                return null;
            }
        }
    }

    public EstadoVisita[] getEstadosVisita() {
        return EstadoVisita.values();
    }

    public String getEstadoVisita(int tipo) {
        return EstadoVisita.getFromValue(tipo).getDetalle();
    }

    public String getExpectativasVisita(boolean expectativa) {
        return expectativa ? "Si" : "No";
    }

    public Date getFechaActual() {
        Date date = new Date();
        return date;
    }

    public boolean disableRealizarVisita() {
        if (selected != null && usuarioActual.isAdmin() && selected.getEstado() != EstadoVisita.ANULADA.getValor() && selected.getEstado() != EstadoVisita.CANCELADA.getValor()) {
            return false;
        } else {
            return !(selected != null && !usuarioActual.isAdmin() && selected.getEstado() == EstadoVisita.PENDIENTE.getValor());
        }
    }

    public boolean disableEditarVisita() {
        return !(selected != null && usuarioActual.isAdmin() && selected.getEstado() == EstadoVisita.PENDIENTE.getValor());
    }

    public boolean disableReporteYAnular() {
        return !(selected != null && usuarioActual.isAdmin() && selected.getEstado() == EstadoVisita.REALIZADA.getValor());
    }

    public void anularCancelarVisita(boolean anular) {
        if (getTransactionFacade().anularOCancelarVisitaProducto(selected, anular)) {
            if (!JsfUtil.isValidationFailed()) {
                JsfUtil.addSuccessMessage(JsfUtil.getMessageBundle(new String[]{"MessageVisita", "AnullSuccessF"}));
                selected = null; // Remove selection
                items = null;
            }
        } else {
            if (anular) {
                JsfUtil.addErrorMessage("Ocurrio un error anulando la visita");
            } else {
                JsfUtil.addErrorMessage("Ocurrio un error cancelando la visita");
            }
        }
    }

    public EstadoVisita[] getEstadosVisitas() {
        return EstadoVisita.getFromValue(new Integer[]{0, 2});
    }

    public void generarReporte(ActionEvent actionEvent) {
        Visita visita = getFacade().getVisitaById(selected.getId());

        if (visita != null) {
            File reporte1 = new File(JsfUtil.getRutaReporte("visita.jasper"));
            File reporte2 = new File(JsfUtil.getRutaReporte("subReporteProductos.jasper"));

            if (reporte1.exists() && reporte2.exists()) {
                List<Visita> visitas = new ArrayList<>();
                visitas.add(visita);

                JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(visitas);

                try {
                    JasperPrint jasperPrint = JasperFillManager.fillReport(reporte1.getPath(), new HashMap(), dataSource);

                    HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
                    response.addHeader("Content-disposition", "attachment; filename=Visita.pdf");
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
            JsfUtil.addErrorMessage("Seleccione la Visita que desea imprimir");
        }
    }

    public String cutObservaciones(String texto) {
        return JsfUtil.cutText(texto);
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public Visita prepareCreate() {
        selected = new Visita();
        create = true;
        header = JsfUtil.getMessageBundle("CreateVisitaTitle");
        return selected;
    }

    public void preparaEdit() {
        create = false;
        header = JsfUtil.getMessageBundle("EditVisitaTitle");
    }

    public void preparaRealizar() {
        visitaProductos = new ArrayList<>();
        visitaProductosEliminar = new ArrayList<>();
        visitaProductosGuardar = new ArrayList<>();
        visitaProductosEditar = new ArrayList<>();
        cantidad = 1;
        producto = null;
        diponible = null;
        visitaProductos = getFacade().getProductosByVisita(selected);
    }

    public void updateVisita() {
        if (getTransactionFacade().updateVisitaProducto(selected, visitaProductos, visitaProductosGuardar, visitaProductosEliminar, visitaProductosEditar)) {
            if (!JsfUtil.isValidationFailed()) {
                clean();
                JsfUtil.addSuccessMessage("La visita se a realizado exitosamente");
            }
        } else {
            JsfUtil.addErrorMessage("NO SE A PODIDO EDITAR LA VISITA");
        }
    }

    public void addVisitaProducto() {

        if (visitaProductoValido()) {

            boolean productoExiste = false;
            for (VisitaProducto vp : visitaProductos) {
                if (vp.getProducto().getId().equals(producto.getId())) {
                    productoExiste = true;
                    vp.setCantidad(vp.getCantidad() + cantidad);

                    if (selected.getId() != null) {
                        visitaProductosEditar.add(vp);
                    }
                    break;
                }
            }

            if (!productoExiste) {
                VisitaProducto vp = new VisitaProducto();

                vp.setId(visitaProductos.size() + 9000000000L);
                vp.setProducto(producto);
                vp.setCantidad(cantidad);

                if (selected.getId() != null) {
                    visitaProductosGuardar.add(vp);
                }

                visitaProductos.add(vp);
            }

            producto = null;
            diponible = null;
            cantidad = 1;
        }
    }

    public void removeVisitaProducto(VisitaProducto visitaProducto) {
        if (selected.getId() != null && !visitaProductosGuardar.contains(visitaProducto)) {
            visitaProductosEliminar.add(visitaProducto);
        }
        visitaProductos.remove(visitaProducto);
    }

    private boolean visitaProductoValido() {
        System.out.println("hola");
        boolean productoNulo = false, cantidadMayorCero = true, cantidadMenorADisponible = true;
        if (producto == null) {
            productoNulo = true;
            JsfUtil.addErrorMessage("El campo producto es obligatorio");
        }

        if (cantidad <= 0) {
            cantidadMayorCero = false;
            JsfUtil.addErrorMessage("el campo cantidad debe se mayor a cero");
        }

        if (diponible != null) {
            if (cantidad > diponible) {
                cantidadMenorADisponible = false;
                JsfUtil.addErrorMessage("La cantidad no puede ser mayor a la cantidad disponible");
            }
        }

        return !productoNulo && cantidadMayorCero && cantidadMenorADisponible;
    }

    public void clean() {
        items = null;
        producto = null;
        cantidad = 1;
        diponible = null;
        visitaProductos.clear();
        visitaProductosGuardar.clear();
        visitaProductosEliminar.clear();
        visitaProductosEditar.clear();
    }

    public void onItemSelectProducto(SelectEvent event) {
        Producto p = (Producto) event.getObject();
        diponible = getProductoFacade().getCantidadDisponibleByProducto(p) - getCantidadProductosAgregados(p);
        if (diponible <= 0) {
            diponible = null;
            cantidad = 1;
            producto = null;
            JsfUtil.addErrorMessage("No hay existencias del producto " + p.toString());
        }
    }

}
