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
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.event.ActionEvent;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import jp.entidades.Factura;
import jp.entidades.VisitaProducto;
import jp.facades.TransactionFacade;
import jp.facades.VisitaFacade;
import jp.facades.VisitaProductoFacade;
import jp.util.EstadoVisita;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

@ManagedBean(name = "visitaController")
@ViewScoped
public class VisitaController implements Serializable {

    @EJB
    private jp.facades.VisitaFacade ejbFacade;
    @EJB
    private jp.facades.VisitaProductoFacade ejbVisitaProductoFacade;
    @EJB
    private jp.facades.TransactionFacade ejbTransactionFacade;
    private List<Visita> items = null;
    private List<VisitaProducto> itemsTMP = null;

    private List<EstadoVisita> estadoVisitasFilter = null;

    private Visita selected;

    public VisitaController() {
        itemsTMP = new ArrayList<>();
    }

    public Visita getSelected() {
        return selected;
    }

    public void setSelected(Visita selected) {
        this.selected = selected;
    }

    public List<VisitaProducto> getItemsTMP() {
        return itemsTMP;
    }

    public void setItemsTMP(List<VisitaProducto> itemsTMP) {
        this.itemsTMP = itemsTMP;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private VisitaFacade getFacade() {
        return ejbFacade;
    }

    public VisitaProductoFacade getEjbVisitaProductoFacade() {
        return ejbVisitaProductoFacade;
    }

    public TransactionFacade getEjbTransactionFacade() {
        return ejbTransactionFacade;
    }

    public List<EstadoVisita> getEstadoVisitasFilter() {
        return estadoVisitasFilter;
    }

    public void setEstadoVisitasFilter(List<EstadoVisita> estadoVisitasFilter) {
        this.estadoVisitasFilter = estadoVisitasFilter;
    }

    public Visita prepareCreate() {
        selected = new Visita();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        selected.setObservacionesCliente("Visita Asignada a " + selected.getEmpleado());
        selected.setEstado(EstadoVisita.PENDIENTE.getValor());
        System.out.println("Fecha-> " + selected.getFecha());
        System.out.println("Observaciones-> " + selected.getObservacionesCliente());
        System.out.println("calificacion servicio-> " + selected.getCalificacionServicio());
        System.out.println("Puntualidad-> " + selected.getPuntualidadServicio());
        System.out.println("Cumplio expectativas?-> " + selected.getCumplioExpectativas());
        System.out.println("Estado-> " + selected.getEstado());
        System.out.println("cliente-> " + selected.getCliente());
        System.out.println("empleado-> " + selected.getEmpleado());
        System.out.println("Procedimiento-> " + selected.getProcedimiento().getDescripcion());
        persist(PersistAction.CREATE, JsfUtil.getMessageBundle(new String[]{"MessageVisita", "CreateSuccessF"}));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
            itemsTMP.clear();
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
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
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
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("languages/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("languages/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public List<Visita> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Visita> getItemsAvailableSelectOne() {
        return getFacade().findAll();
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

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
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
    
    /**
     * 
     * @param visita 
     * @return Lista de productos incluidos en la visita
     */
    public List<VisitaProducto> getProductosByVisita(Visita visita){
        try {
            itemsTMP = getEjbVisitaProductoFacade().getProductosByVisita(visita);
            return getEjbVisitaProductoFacade().getProductosByVisita(visita);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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
        return !(selected != null && selected.getEstado() == EstadoVisita.PENDIENTE.getValor());
    }

    @Deprecated
    public boolean disableAnularVisita() {
        boolean disable = false;
        if (selected != null && selected.getEstado() == EstadoVisita.REALIZADA.getValor()) {
            disable = false;
        } else {
            disable = true;
        }
        return disable;
    }

    public boolean disableReporteYAnular() {
        return !(selected != null && selected.getEstado() == EstadoVisita.REALIZADA.getValor());
    }

    public void anullVisita() {
        if (!JsfUtil.isValidationFailed() && getEjbTransactionFacade().anullVisitaProducto(selected)) {
            JsfUtil.addSuccessMessage(JsfUtil.getMessageBundle(new String[]{"MessageVisita", "AnullSuccessF"}));
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public EstadoVisita[] getEstadosVisitas() {
        return EstadoVisita.getFromValue(new Integer[]{0, 2});
    }

    public String redireccionarVisita(Visita visita) {
        selected = visita;
        return "AddProductosByVisita.xhtml?visita=" + visita.getId() + "faces-redirect=true";
    }

    @Deprecated
    public void redireccionarFormulario() {
        try {
            FacesContext.getCurrentInstance().getExternalContext().redirect("List.xhtml");
        } catch (Exception e) {
            e.printStackTrace();
        }
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

                HashMap map = new HashMap();
                //map.put("venta", "" + getCantidadVentasOrBonificacionesByFactura(visita, 1));
                //map.put("bonificacion", "" + getCantidadVentasOrBonificacionesByFactura(visita, 2));

                try {
                    JasperPrint jasperPrint = JasperFillManager.fillReport(reporte1.getPath(), map, dataSource);

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

}
