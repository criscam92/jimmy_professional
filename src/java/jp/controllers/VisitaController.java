package jp.controllers;

import jp.entidades.Visita;
import jp.util.JsfUtil;
import jp.util.JsfUtil.PersistAction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import jp.entidades.VisitaProducto;
import jp.facades.VisitaFacade;
import jp.facades.VisitaProductoFacade;
import jp.util.EstadoVisita;

@ManagedBean(name = "visitaController")
@SessionScoped
public class VisitaController implements Serializable {

    @EJB
    private jp.facades.VisitaFacade ejbFacade;
    private jp.facades.VisitaProductoFacade ejbVisitaProductoFacade;
    private List<Visita> items = null;
    private List<VisitaProducto> itemsTMP = null;
    private VisitaProducto visitaProducto;

    private Visita selected;

    public VisitaController() {
        itemsTMP = new ArrayList<>();
    }

    @PostConstruct
    public void init() {
        visitaProducto = new VisitaProducto();
    }

    public Visita getSelected() {
        return selected;
    }

    public void setSelected(Visita selected) {
        this.selected = selected;
    }

    public VisitaProducto getVisitaProducto() {
        return visitaProducto;
    }

    public void setVisitaProducto(VisitaProducto visitaProducto) {
        this.visitaProducto = visitaProducto;
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

    public EstadoVisita[] getEstadosVisita() {
        return EstadoVisita.values();
    }

    public String getEstadoVisita(int tipo) {
        System.out.println("Tipo--> " + tipo);
        return EstadoVisita.getFromValue(tipo).getDetalle();
    }

    public String getExpectativasVisita(boolean expectativa) {
        return expectativa ? "Si" : "No";
    }

    public Date getFechaActual() {
        Date date = new Date();
        return date;
    }

    public void addVisitaProducto() {
        VisitaProducto visitaProductoTMP = new VisitaProducto();
        visitaProductoTMP.setCantidad(visitaProducto.getCantidad());
        visitaProductoTMP.setProducto(visitaProducto.getProducto());
        visitaProductoTMP.setObservacion(visitaProducto.getObservacion());
        visitaProductoTMP.setVisita(selected);
        visitaProductoTMP.setId(itemsTMP.size() + 1l);
        itemsTMP.add(visitaProductoTMP);
    }

    public void removeDevolucionProducto(VisitaProducto visitaProductoArg) {
        itemsTMP.remove(visitaProductoArg);        
    }
    
    public void createVisitaProducto() {
        if (!itemsTMP.isEmpty() && selected != null) {
            if (getEjbVisitaProductoFacade().createVisitaProducto(itemsTMP, selected)) {
                JsfUtil.addSuccessMessage(JsfUtil.getMessageBundle("MessageVisitaProducto"));
                if (!JsfUtil.isValidationFailed()) {
                    selected = null; // Remove selection
                    items = null;    // Invalidate list of items to trigger re-query.
                    itemsTMP.clear();
                }
            } else {
                JsfUtil.addErrorMessage(JsfUtil.getMessageBundle("ErrorCreateDevolucionProducto"));
            }
        }
    }
}
