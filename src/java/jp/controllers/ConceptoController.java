package jp.controllers;

import java.io.Serializable;
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
import jp.entidades.Concepto;
import jp.entidades.ReciboCaja;
import jp.facades.ConceptoFacade;
import jp.facades.TransactionFacade;
import jp.util.CondicionConcepto;
import jp.util.JsfUtil;
import jp.util.JsfUtil.PersistAction;
import jp.util.TipoConcepto;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "conceptoController")
@ViewScoped
public class ConceptoController implements Serializable {

    @EJB
    private ConceptoFacade ejbFacade;
    @EJB
    private TransactionFacade ejbTransactionFacade;
    private List<Concepto> items = null;
    private Concepto selected;
    private final String uiError;
    private String error;
    private CondicionConcepto[] condicionesConceptos;

    public ConceptoController() {
        uiError = "ui-state-error";
        condicionesConceptos = CondicionConcepto.values();
    }

    public Concepto getSelected() {
        return selected;
    }

    public void setSelected(Concepto selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private ConceptoFacade getFacade() {
        return ejbFacade;
    }

    public TransactionFacade getEjbTransactionFacade() {
        return ejbTransactionFacade;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public CondicionConcepto[] getCondicionesConceptos() {
        return condicionesConceptos;
    }

    public void setCondicionesConceptos(CondicionConcepto[] condicionesConceptos) {
        this.condicionesConceptos = condicionesConceptos;
    }

    public Concepto prepareCreate() {
        selected = new Concepto();
        Long ultimoCodigo = getEjbTransactionFacade().getLastCodigoByEntity(selected) + 1;
        selected.setCodigo(JsfUtil.rellenar("" + ultimoCodigo, "0", 3, false));
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        if (!existeCodigoConcepto()) {
            persist(PersistAction.CREATE, JsfUtil.getMessageBundle(new String[]{"MessageConcepto", "CreateSuccessM"}));
            if (!JsfUtil.isValidationFailed()) {
                items = null;    // Invalidate list of items to trigger re-query.
                setError("");
                RequestContext.getCurrentInstance().execute("PF('ConceptoCreateDialog').hide()");
            }
        } else {
            setError(uiError);
            JsfUtil.addErrorMessage("Ya existe el Código: " + selected.getCodigo());
        }
    }

    public boolean existeCodigoConcepto() {
        boolean existe = getFacade().getEntityByCodigoOrTipo(selected);
        if (existe) {
            selected.setCodigo("");
            JsfUtil.addErrorMessage("El Código del Concepto ya se encuentra en la base de datos.");
        }
        return existe;
    }

    public void update() {
        if (!getFacade().getEntityByCodigoOrTipo(selected)) {
            persist(PersistAction.UPDATE, JsfUtil.getMessageBundle(new String[]{"MessageConcepto", "UpdateSuccessM"}));
            if (!JsfUtil.isValidationFailed()) {
                items = null;
                setError("");
                RequestContext.getCurrentInstance().execute("PF('ConceptoEditDialog').hide()");
            }
        } else {
            items = null;
            setError(uiError);
            JsfUtil.addErrorMessage("Ya existe el Código: " + selected.getCodigo());
        }
    }

    public void destroy() {
        persist(PersistAction.DELETE, JsfUtil.getMessageBundle(new String[]{"MessageConcepto", "DeleteSuccessM"}));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Concepto> getItems() {
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
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public List<Concepto> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Concepto> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Concepto.class, value = "conceptoconverter")
    public static class ConceptoControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {

            if (value == null || value.length() == 0) {
                return null;
            }

            try {
                Integer.parseInt(value);
            } catch (Exception e) {
                return null;
            }

            ConceptoController controller = (ConceptoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "conceptoController");
            return controller.getFacade().find(getKey(value));
        }

        java.lang.Integer getKey(String value) {
            try {
                java.lang.Integer key;
                key = Integer.valueOf(value);
                return key;
            } catch (Exception e) {
                return null;
            }
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
            if (object instanceof Concepto) {
                Concepto o = (Concepto) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Concepto.class.getName()});
                return null;
            }
        }

    }

    public String getTipoConcepto(Integer tipo) {
        return TipoConcepto.getFromValue(tipo).getDetalle();
    }

    public String getCondicionConcepto(Integer tipo) {
        return CondicionConcepto.getFromValue(tipo).getDetalle();
    }

    public TipoConcepto[] getTiposConceptos() {
        return TipoConcepto.values();
    }

    public void changeTipoConcepto() {
        if (selected != null) {
            int tipo = selected.getTipo2();
            System.out.println("Tipo-> " + tipo);
            if (tipo == TipoConcepto.NEUTRAL.getValor()) {
                condicionesConceptos = CondicionConcepto.getFromValue(new Integer[]{1, 2});
            } else if (tipo == TipoConcepto.INGRESO.getValor()) {
                condicionesConceptos = CondicionConcepto.getFromValue(new Integer[]{0, 2});
            } else if (tipo == TipoConcepto.EGRESO.getValor()) {
                condicionesConceptos = CondicionConcepto.getFromValue(new Integer[]{0, 1});
            }
        }
    }

    public List<Concepto> llenarConceptos(String query) {
        return getFacade().getConceptosByQuery(query);
    }

    public List<Concepto> llenarConceptosCXCCXP(String query) {
        FacesContext context = FacesContext.getCurrentInstance();
        ReciboCaja rc = (ReciboCaja) UIComponent.getCurrentComponent(context).getAttributes().get("filter");
        Boolean isCxc = null;
        if (rc != null) {
            isCxc = rc.getConcepto().getCxccxp() == CondicionConcepto.CXP.getValor();
        }

        return getFacade().getConceptosCXCCXPByQuery(query, isCxc);
    }

}
