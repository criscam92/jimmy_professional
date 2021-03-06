package jp.controllers;

import jp.entidades.Cliente;
import jp.util.JsfUtil;
import jp.util.JsfUtil.PersistAction;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.component.UISelectOne;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.faces.event.AjaxBehaviorEvent;
import jp.facades.ClienteFacade;
import jp.facades.ParametrosFacade;
import jp.seguridad.UsuarioActual;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;
import org.primefaces.context.RequestContext;
import org.primefaces.event.ToggleEvent;
import org.primefaces.model.Visibility;

@ManagedBean(name = "clienteController")
@SessionScoped
public class ClienteController implements Serializable {

    @EJB
    private jp.facades.ClienteFacade ejbFacade;
    @EJB
    private ParametrosFacade ejbRecargoFacade;
    @EJB
    private UsuarioActual ejbUsuarioActualFacade;
    private List<Cliente> items = null;
    private Cliente selected;
    private final String uiError;
    private String error;
    private Boolean disable = false;
    private boolean recargo = false;
    private int valorSelect;
    private String visibility;
    private String header;
    private List<Boolean> list;

    @PostConstruct
    public void init() {
        list = Arrays.asList(true, true, true, true, true, true, true, true, true, true, true);
    }

    public List<Boolean> getList() {
        return list;
    }

    public void setList(List<Boolean> list) {
        this.list = list;
    }

    public ClienteController() {
        uiError = "ui-state-error";
    }

    public Cliente getSelected() {
        return selected;
    }

    public void setSelected(Cliente selected) {
        this.selected = selected;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public Boolean getDisable() {
        return disable;
    }

    public void setDisable(Boolean disable) {
        this.disable = disable;
    }

    public String getVisibility() {
        return visibility;
    }

    public void setVisibility(String visibility) {
        this.visibility = visibility;
    }

    public boolean getRecargo() {
        return recargo;
    }

    public void setRecargo(boolean recargo) {
        this.recargo = recargo;
    }

    public int getValorSelect() {
        return valorSelect;
    }

    public void setValorSelect(int valorSelect) {
        this.valorSelect = valorSelect;
    }

    private ClienteFacade getFacade() {
        return ejbFacade;
    }

    public ParametrosFacade getEjbRecargoFacade() {
        return ejbRecargoFacade;
    }

    public UsuarioActual getEjbUsuarioActualFacade() {
        return ejbUsuarioActualFacade;
    }

    public String getHeader() {
        return header;
    }

    public void setHeader(String header) {
        this.header = header;
    }

    public Cliente prepareCreate() {
        selected = new Cliente();
        disable = true;
        recargo = false;
        visibility = "hidden";
        header = JsfUtil.getMessageBundle("CreateClienteTitle");
        return selected;
    }

    public void prepareEdit() {
        selected = getFacade().find(selected.getId());
        header = JsfUtil.getMessageBundle("EditClienteTitle");
        if (selected.getTarifaEspecial() == null) {
            recargo = false;
            disable = false;
            visibility = "hidden";
        } else if (selected.getTarifaEspecial() >= 0.0f) {
            recargo = true;
            disable = false;
            visibility = "visible";
        } else {
            recargo = false;
            disable = true;
            visibility = "hidden";
        }
    }

    public void createOrEdit() {
        if (!existeDocumentoCliente()) {
            if (selected.getCupoCredito() == 0.0) {
                selected.setCupoCredito(selected.getCategoria().getCupoMaximo());
            }
            String mensaje = (selected.getId() != null ? JsfUtil.getMessageBundle(new String[]{"MessageCliente", "UpdateSuccessM"})
                    : JsfUtil.getMessageBundle(new String[]{"MessageCliente", "CreateSuccessM"}));
            selected.setUsuario(getEjbUsuarioActualFacade().getUsuario());
            persist(PersistAction.CREATE, mensaje);

            if (!JsfUtil.isValidationFailed()) {
                setError("");
                items = null;    // Invalidate list of items to trigger re-query.
                RequestContext.getCurrentInstance().execute("PF('ClienteFormDialog').hide()");
            }
        } else {
            setError(uiError);
            JsfUtil.addErrorMessage("El Documento ya se encuentra en la base de datos.");
        }
    }

    public void destroy() {
        persist(PersistAction.DELETE, JsfUtil.getMessageBundle(new String[]{"MessageCliente", "DeleteSuccessM"}));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public boolean existeDocumentoCliente() {
        boolean existe = getFacade().existeDocumento(selected);
        if (existe) {
            selected.setDocumento("");
            JsfUtil.addErrorMessage("El Documento ya se encuentra en la base de datos.");
        }
        return existe;
    }

    public List<Cliente> getItems() {
        if (items == null) {
            items = getFacade().findAll();
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
                    if (persistAction == PersistAction.DELETE) {
                        msg = "Error eliminando el cliente " + selected.toString() + " verifique que no este siendo utilizado";
                    }
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

    public List<Cliente> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Cliente> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Cliente.class, value = "clienteconverter")
    public static class ClienteControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            try {
                ClienteController controller = (ClienteController) facesContext.getApplication().getELResolver().
                        getValue(facesContext.getELContext(), null, "clienteController");
                return controller.getFacade().find(getKey(value));
            } catch (Exception e) {
                return null;
            }
        }

        Long getKey(String value) {
            Long key;
            try {
                key = Long.valueOf(value);
                return key;
            } catch (NumberFormatException e) {
                return null;
            }

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
            if (object instanceof Cliente) {
                Cliente o = (Cliente) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Cliente.class.getName()});
                return null;
            }
        }

    }

    public void onchange(AjaxBehaviorEvent e) {
        if (e != null) {
            UISelectOne select = (UISelectOne) e.getSource();
            disable = (select.getSubmittedValue() == null || select.getSubmittedValue().toString().equals(JsfUtil.getMessageBundle("SelectOneMessage")));
        } else {
            disable = true;
        }
        recargo = false;
        visibility = "hidden";
        selected.setTarifaEspecial(null);
    }

    public void showTarifaEspecial() {
        if (recargo) {
            visibility = "visible";
            selected.setTarifaEspecial(getEjbRecargoFacade().getParametrosByCiudad(selected.getCiudad()));
        } else {
            visibility = "hidden";
            selected.setTarifaEspecial(null);
        }
    }

    public List<Cliente> llenarCliente(String query) {
        return getFacade().getClienteByQuery(query);
    }

    public String setValorObject(Object ob) {
        return ob == null ? "-----" : ob.toString();
    }

    public String setValorString(String st) {
        return (st == null || st.trim().isEmpty()) == true ? "-----" : st;
    }

    public void onToggle(ToggleEvent e) {
        list.set((Integer) e.getData(), e.getVisibility() == Visibility.VISIBLE);
    }

    public void postProcessXLS(Object document) {
        HSSFWorkbook wb = (HSSFWorkbook) document;

        HSSFSheet sheet = wb.getSheetAt(0);

        int rows = sheet.getLastRowNum();

        int numberOfCells = sheet.getRow(0).getPhysicalNumberOfCells();

        sheet.shiftRows(0, rows, 1);

        HSSFCellStyle cellStyle = wb.createCellStyle();
        cellStyle.setFillForegroundColor(HSSFColor.GREY_25_PERCENT.index);
        cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
        cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
        cellStyle.setVerticalAlignment(HSSFCellStyle.VERTICAL_CENTER);

        HSSFCell header = sheet.createRow(0).createCell(0);
        header.setCellStyle(cellStyle);
        header.setCellValue("Listado de clientes");

        for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
            HSSFRow row = sheet.getRow(i);
            row.setHeightInPoints(25);
        }

        for (short i = 0; i < numberOfCells; i++) {
            sheet.autoSizeColumn(i);
        }

        CellRangeAddress range = new CellRangeAddress(0, 0, 0, numberOfCells - 1);
        sheet.addMergedRegion(range);

    }
}
