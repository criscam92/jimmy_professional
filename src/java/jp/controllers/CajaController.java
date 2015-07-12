package jp.controllers;

import jp.entidades.Pais;
import jp.util.JsfUtil;
import jp.util.JsfUtil.PersistAction;
import java.io.Serializable;
import java.util.Calendar;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import jp.entidades.ActualizaBaseCaja;
import jp.entidades.Caja;
import jp.facades.ActualizaCajaFacade;
import jp.facades.CajaFacade;
import jp.seguridad.UsuarioActual;

@ManagedBean(name = "cajaController")
@ViewScoped
public class CajaController implements Serializable {

    @EJB
    private CajaFacade ejbFacade;
    @EJB
    private ActualizaCajaFacade ejbActualizarCajaFacade;
    @EJB
    private UsuarioActual usuarioActual;
    private Caja selected;
    private ActualizaBaseCaja actualizaBaseCajaMenor;
    private List<Caja> items = null;
    private List<ActualizaBaseCaja> itemsActualizacionesCaja = null;
    private Long valorAnterior;

    @PostConstruct
    private void init() {
        findCaja();
        valorAnterior = selected.getBase();
        actualizaBaseCajaMenor = new ActualizaBaseCaja();
    }

    public Caja getSelected() {
        return selected;
    }

    public void setSelected(Caja selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private CajaFacade getFacade() {
        return ejbFacade;
    }
    
    private ActualizaCajaFacade getActualizaCajaFacade() {
        return ejbActualizarCajaFacade;
    }

    public List<ActualizaBaseCaja> getItemsActualizacionesCaja() {
        return itemsActualizacionesCaja;
    }

    public void setItemsActualizacionesCaja(List<ActualizaBaseCaja> itemsActualizacionesCaja) {
        this.itemsActualizacionesCaja = itemsActualizacionesCaja;
    }

    public Caja prepareCreate() {
        selected = new Caja();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, JsfUtil.getMessageBundle(new String[]{"MessageCajaMenor", "CreateSuccessF"}));
        if (!JsfUtil.isValidationFailed()) {
//            RequestContext.getCurrentInstance().execute("PF('PaisCreateDialog').hide()");
        }
    }

    public void update() {
        
        if (selected != null) {
            selected.setFechaActualizacion(Calendar.getInstance().getTime());
            actualizaBaseCajaMenor.setCaja(selected);
            actualizaBaseCajaMenor.setFecha(selected.getFechaActualizacion());
            actualizaBaseCajaMenor.setUsuario(usuarioActual.getUsuario());
            actualizaBaseCajaMenor.setValorAnterior(valorAnterior);
            actualizaBaseCajaMenor.setValorNuevo(selected.getBase());
            
            getActualizaCajaFacade().create(actualizaBaseCajaMenor);
        }
        persist(PersistAction.UPDATE, JsfUtil.getMessageBundle(new String[]{"MessageCajaMenor", "UpdateSuccessF"}));
        if (!JsfUtil.isValidationFailed()) {
            //RequestContext.getCurrentInstance().execute("PF('PaisEditDialog').hide()");
            itemsActualizacionesCaja = null;
            init();
        }
    }

    public void destroy() {
        persist(PersistAction.DELETE, JsfUtil.getMessageBundle(new String[]{"MessageCajaMenor", "DeleteSuccessF"}));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
        }
    }
    
    public List<Caja> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }
    
    public List<ActualizaBaseCaja> getActualizacionesCaja() {
        if (itemsActualizacionesCaja == null) {
            itemsActualizacionesCaja = getActualizaCajaFacade().getListaActualizacionesCajaByFecha();
        }
        return itemsActualizacionesCaja;
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

    public List<Caja> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Caja> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Caja.class, value = "cajaconverter")
    public static class CajaMenorControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            CajaController controller = (CajaController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "cajaController");
            return controller.getFacade().find(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            java.lang.Integer key;
            key = Integer.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Integer value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof Caja) {
                Caja o = (Caja) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Caja.class.getName()});
                return null;
            }
        }

    }

    private void findCaja() {
        selected = getFacade().getCaja();
    }

}
