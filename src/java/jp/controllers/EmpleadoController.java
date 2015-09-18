package jp.controllers;

import jp.entidades.Empleado;
import jp.util.JsfUtil;
import jp.util.JsfUtil.PersistAction;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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
import jp.entidades.Cliente;
import jp.facades.ClienteFacade;
import jp.facades.EmpleadoFacade;
import jp.facades.TransactionFacade;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;

@ManagedBean(name = "empleadoController")
@ViewScoped
public class EmpleadoController implements Serializable {

    @EJB
    private EmpleadoFacade ejbFacade;
    @EJB
    private ClienteFacade clienteFacade;
    @EJB
    private TransactionFacade ejbTransactionFacade;
    private List<Empleado> items = null;
    private Empleado selected;
    private Empleado empleadoDestino;
    private final String uiError;
    private String error;
    private DualListModel listaClientesEmpleadoOrigen;
    private List<Cliente> clientesSource;
    private List<Cliente> clientesTarget;

    public EmpleadoController() {
        uiError = "ui-state-error";
        listaClientesEmpleadoOrigen = new DualListModel();
    }

    private void cargarListas(Empleado empleadoOrigen, Empleado empleadoDestino) {
        if (selected != null) {
            clientesSource = getClienteFacade().getClientesByEmpleado(empleadoOrigen);
            clientesTarget = null;
            if (empleadoDestino == null) {
                clientesTarget = new ArrayList<>();
            } else {
                clientesTarget = getClienteFacade().getClientesByEmpleado(empleadoDestino);
            }
            listaClientesEmpleadoOrigen = new DualListModel<>(clientesSource, clientesTarget);
        } else {
            listaClientesEmpleadoOrigen = new DualListModel();
        }
    }

    public void llenarClientesByEmpleado() {
        empleadoDestino = null;
        cargarListas(selected, null);
    }

    public Empleado getSelected() {
        return selected;
    }

    public void setSelected(Empleado selected) {
        this.selected = selected;
    }

    public Empleado getEmpleadoDestino() {
        return empleadoDestino;
    }

    public void setEmpleadoDestino(Empleado empleadoDestino) {
        this.empleadoDestino = empleadoDestino;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private EmpleadoFacade getFacade() {
        return ejbFacade;
    }

    public ClienteFacade getClienteFacade() {
        return clienteFacade;
    }

    public TransactionFacade getEjbTransactionFacade() {
        return ejbTransactionFacade;
    }

    public DualListModel getClientesByEmpleado() {
        return listaClientesEmpleadoOrigen;
    }

    public void setClientesByEmpleado(DualListModel clientesByEmpleado) {
        this.listaClientesEmpleadoOrigen = clientesByEmpleado;
    }

    public Empleado prepareCreate() {
        selected = new Empleado();
        Long ultimoCodigo = getEjbTransactionFacade().getLastCodigoByEntity(selected) + 1;
        selected.setCodigo(JsfUtil.rellenar("" + ultimoCodigo, "0", 3, false));
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        if (!existeCodigoEmpleado()) {
            if (!existeDocumentoEmpleado()) {
                persist(PersistAction.CREATE, JsfUtil.getMessageBundle(new String[]{"MessageEmpleado", "CreateSuccessM"}));
                if (!JsfUtil.isValidationFailed()) {
                    selected = null; // Remove selection
                    items = null;    // Invalidate list of items to trigger re-query.
                    setError("");
                    RequestContext.getCurrentInstance().execute("PF('EmpleadoCreateDialog').hide()");
                }
            } else {
                setError(uiError);
                JsfUtil.addErrorMessage("El Documento ya se encuentra en la base de datos.");
            }
        } else {
            setError(uiError);
            JsfUtil.addErrorMessage(JsfUtil.getMessageBundle("MessageEmpleadoCodigoExist").replaceAll("%cod%", selected.getCodigo()));
        }
    }

    public boolean existeCodigoEmpleado() {
        boolean existe = getFacade().getEntityByCodigoOrTipo(selected);
        if (existe) {
            selected.setCodigo("");
            JsfUtil.addErrorMessage("El Código del Empleado ya se encuentra en la base de datos.");
        }
        return existe;
    }

    public boolean existeDocumentoEmpleado() {
        boolean existe = getFacade().existeDocumento(selected);
        if (existe) {
            selected.setDocumento("");
            JsfUtil.addErrorMessage("El Documento del Empleado ya se encuentra en la base de datos.");
        }
        return existe;
    }

    public void update() {
        if (!getFacade().getEntityByCodigoOrTipo(selected)) {
            if (!getFacade().existeDocumento(selected)) {
                persist(PersistAction.UPDATE, JsfUtil.getMessageBundle(new String[]{"MessageEmpleado", "UpdateSuccessM"}));
                setError("");
                RequestContext.getCurrentInstance().execute("PF('EmpleadoEditDialog').hide()");
            } else {
                setError(uiError);
                JsfUtil.addErrorMessage("El documento ya se encuentra en la base de datos.");
            }
        } else {
            setError(uiError);
            JsfUtil.addErrorMessage(JsfUtil.getMessageBundle("MessageEmpleadoCodigoExist").replaceAll("%cod%", selected.getCodigo()));
        }
    }

    public void destroy() {
        persist(PersistAction.DELETE, JsfUtil.getMessageBundle(new String[]{"MessageEmpleado", "DeleteSuccessM"}));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
            setError("");
        }
    }

    public List<Empleado> getItems() {
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
                    JsfUtil.addErrorMessage(ex, JsfUtil.getMessageBundle("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, JsfUtil.getMessageBundle("PersistenceErrorOccured"));
            }
        }
    }

    public List<Empleado> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Empleado> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Empleado.class, value = "empleadoconverter")
    public static class EmpleadoControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            EmpleadoController controller = (EmpleadoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "empleadoController");
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
            if (object instanceof Empleado) {
                Empleado o = (Empleado) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Empleado.class.getName()});
                return null;
            }
        }
    }

    public List<Empleado> llenarEmpleado(String query) {
        return getFacade().getEmpleadoByQuery(query);
    }

    public void onItemSelectEmpleado(SelectEvent event) {
        Empleado e = (Empleado) event.getObject();
        if (Objects.equals(e.getId(), selected.getId())) {
            JsfUtil.addWarnMessage("Seleccione un empleado diferente al del Origen");
            empleadoDestino = null;
            return;
        } else {
            cargarListas(selected, e);
        }
    }

    public void actualizarListasEmpleados() {
        boolean update = getEjbTransactionFacade().actualizarListasEmpleados(selected, empleadoDestino, clientesSource, clientesTarget);
        if (update) {
            RequestContext.getCurrentInstance().execute("PF('TranferClienteDialog').hide()");
            JsfUtil.addSuccessMessage("Se actualizaron las listas de Clientes");
        } else {
            JsfUtil.addErrorMessage("No se pudieron actualizar las listas de Clientes");
        }
    }

    public void handleTransfer(TransferEvent event) {
        List<Cliente> clientes = (List<Cliente>) event.getItems();
        
        if (empleadoDestino == null) {
            JsfUtil.addWarnMessage("Seleccione primero un Empleado Destino");
            return;
        }
        
        for (Cliente cliente : clientes) {
            if (event.isAdd()) {
                clientesSource.remove(cliente);
                clientesTarget.add(cliente);
            } else {
                clientesSource.add(cliente);
                clientesTarget.remove(cliente);
            }
        }
    }

}
