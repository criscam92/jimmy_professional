package jp.controllers;

import jp.entidades.VisitaProducto;
import jp.util.JsfUtil;
import jp.util.JsfUtil.PersistAction;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
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
import jp.entidades.Visita;
import jp.facades.TransactionFacade;
import jp.facades.VisitaProductoFacade;

@ManagedBean(name = "visitaProductoController")
@SessionScoped
public class VisitaProductoController implements Serializable {

    @EJB
    private jp.facades.VisitaProductoFacade ejbFacade;
    @EJB
    private jp.facades.TransactionFacade ejbTransactionFacade;
    
    private List<VisitaProducto> items = null;
    private List<VisitaProducto> itemsTMP = null;
    private VisitaProducto selected;

    public VisitaProductoController() {
        itemsTMP = new ArrayList<>();
    }

    public VisitaProducto getSelected() {
        return selected;
    }

    public void setSelected(VisitaProducto selected) {
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

    private VisitaProductoFacade getFacade() {
        return ejbFacade;
    }

    public TransactionFacade getEjbTransactionFacade() {
        return ejbTransactionFacade;
    }

    public VisitaProducto prepareCreate() {
        selected = new VisitaProducto();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("languages/Bundle").getString("VisitaProductoCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("languages/Bundle").getString("VisitaProductoUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("languages/Bundle").getString("VisitaProductoDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<VisitaProducto> getItems() {
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

    public List<VisitaProducto> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<VisitaProducto> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = VisitaProducto.class)
    public static class VisitaProductoControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            VisitaProductoController controller = (VisitaProductoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "visitaProductoController");
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
            if (object instanceof VisitaProducto) {
                VisitaProducto o = (VisitaProducto) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), VisitaProducto.class.getName()});
                return null;
            }
        }

    }
    
    public List<VisitaProducto> getProductosByVisita(Visita visita){
        try {
            itemsTMP = getEjbTransactionFacade().getProductosByVisita(visita);
            return getFacade().getProductosByVisita(visita);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public void addDevolucionProducto() {
        VisitaProducto visitaProducto = new VisitaProducto();
        visitaProducto.setVisita(selected.getVisita());
        visitaProducto.setProducto(selected.getProducto());
        visitaProducto.setCantidad(selected.getCantidad());
        visitaProducto.setObservacion(selected.getObservacion());

        System.out.println("====>" + visitaProducto);
        System.out.println("Visita--> " + visitaProducto.getCantidad() + "\nProducto--> " + visitaProducto.getObservacion()
                + "\nCantidad--> " + visitaProducto.getCantidad()+ "\nObservacion--> " + visitaProducto.getObservacion());
        itemsTMP.add(visitaProducto);

    }
    
    public void removeVisitaProducto(VisitaProducto visitaProducto) {
        System.out.println("Tamano de lista antes de eliminar-> " + itemsTMP.size());
        System.out.println("Item a eliminar-> " + visitaProducto.getObservacion());

        itemsTMP.remove(visitaProducto);

    }

}
