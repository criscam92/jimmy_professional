package jp.controllers;

import jp.entidades.Producto;
import jp.util.JsfUtil;
import jp.util.JsfUtil.PersistAction;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
import jp.entidades.Parametros;
import jp.facades.ProductoFacade;
import jp.facades.ParametrosFacade;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.usermodel.HSSFFontFormatting;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;
import org.primefaces.context.RequestContext;

@ManagedBean(name = "productoController")
@ViewScoped
public class ProductoController implements Serializable {
    
    @EJB
    private jp.facades.ProductoFacade ejbFacade;
    @EJB
    private ParametrosFacade parametrosFacade;
    private List<Producto> items = null;
    private Producto selected;
    private final String uiError;
    private String error;
    private ProductoFacade.TIPO_PRECIO tipoLista;
    
    private Map<ProductoFacade.TIPO_PRECIO,Float> recargosTipoPrecio;

    private Float recargoPublico;

    public ProductoController() {
        uiError = "ui-state-error";
    }
    
    @PostConstruct
    private void init(){
        tipoLista = ProductoFacade.TIPO_PRECIO.LOCALES;
    }

    public ProductoFacade.TIPO_PRECIO[] getTipoListado(){
        return ProductoFacade.TIPO_PRECIO.values();
    }

    public ProductoFacade.TIPO_PRECIO getTipoLista() {
        return tipoLista;
    }

    public void setTipoLista(ProductoFacade.TIPO_PRECIO tipoLista) {
        this.tipoLista = tipoLista;
    }
    
    public Producto getSelected() {
        return selected;
    }

    public void setSelected(Producto selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private ProductoFacade getFacade() {
        return ejbFacade;
    }

    public Producto prepareCreate() {
        selected = new Producto();
        initializeEmbeddableKey();
        return selected;
    }
    
    public void cambiarTipoLista(){
        System.out.println("Tipo de lista: ".concat(tipoLista.toString()));
    }
    
    private float getRecargoDecimal(float recargo){
        return (recargo / 100) + 1;
    }

    public String getValorRecargoPublico(Producto producto, float valor, String currency) {

        if (producto.getVentaPublico()) {

            valor = getValorProducto(valor);
            
            if (recargoPublico == null) {
                recargoPublico = parametrosFacade.getParametros().getPorcentajeVentaPublic();
                recargoPublico = getRecargoDecimal(recargoPublico);
                if (recargoPublico == null) {
                    recargoPublico = 1.0f;
                }
            }
            currency = currency==null?"":currency;
            String resultado = currency.concat(new DecimalFormat("#.##",DecimalFormatSymbols.getInstance(Locale.forLanguageTag("es-co"))).format(valor * recargoPublico));
            return resultado;
        } else {
            return "No aplica";
        }
    }
    
    public Float getValorProducto(Float valor){
        if(tipoLista==null){
            return valor;
        }else{
            
            if(recargosTipoPrecio==null){
                recargosTipoPrecio = new HashMap<>();
                Parametros parametros = null;
                try {
                    parametros = parametrosFacade.getParametros();
                } catch (Exception e) {
                }
                
                if(parametros!=null){
                    if(parametros.getRecargoLocal()==null){
                        parametros.setRecargoLocal(0.0f);
                    }
                    recargosTipoPrecio.put(ProductoFacade.TIPO_PRECIO.LOCALES, getRecargoDecimal(parametros.getRecargoLocal()));
                    
                    if(parametros.getRecargoNacional()==null){
                        parametros.setRecargoNacional(0.0f);
                    }
                    recargosTipoPrecio.put(ProductoFacade.TIPO_PRECIO.NACIONALES, getRecargoDecimal(parametros.getRecargoNacional()));
                    
                    if(parametros.getRecargoInternacional()==null){
                        parametros.setRecargoInternacional(0.0f);
                    }
                    recargosTipoPrecio.put(ProductoFacade.TIPO_PRECIO.EXTRANJEROS, getRecargoDecimal(parametros.getRecargoInternacional()));
                }
            }
            
            return recargosTipoPrecio.get(tipoLista)*valor;
            
        }
    }

    public void create() {
        if (!getFacade().getEntityByCodigoOrTipo(selected)) {
            persist(PersistAction.CREATE, JsfUtil.getMessageBundle(new String[]{"MessageProducto", "CreateSuccessM"}));
            if (!JsfUtil.isValidationFailed()) {
                items = null;    // Invalidate list of items to trigger re-query.
                setError("");
                RequestContext.getCurrentInstance().execute("PF('ProductoCreateDialog').hide()");
            }
        } else {
            setError(uiError);
            JsfUtil.addErrorMessage("Ya existe el Código: " + selected.getCodigo());
        }
    }

    public void update() {
        if (!getFacade().getEntityByCodigoOrTipo(selected)) {
            persist(PersistAction.UPDATE, JsfUtil.getMessageBundle(new String[]{"MessageProducto", "UpdateSuccessM"}));
            if (!JsfUtil.isValidationFailed()) {
                items = null;
                setError("");
                RequestContext.getCurrentInstance().execute("PF('ProductoEditDialog').hide()");
            }

        } else {
            items = null;
            setError(uiError);
            JsfUtil.addErrorMessage("Ya existe el Código: " + selected.getCodigo());
        }
    }

    public void destroy() {
        persist(PersistAction.DELETE, JsfUtil.getMessageBundle(new String[]{"MessageProducto", "DeleteSuccessM"}));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Producto> getItems() {
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

    public List<Producto> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Producto> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    @FacesConverter(forClass = Producto.class, value = "productoconverter")
    public static class ProductoControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }

            ProductoController controller = (ProductoController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "productoController");

            Long key = 0L;
            try {
                key = getKey(value);
                return controller.getFacade().find(key);
            } catch (NumberFormatException nfe) {
                return null;
            }
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
            if (object instanceof Producto) {
                Producto o = (Producto) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Producto.class.getName()});
                return null;
            }
        }

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
        header.setCellValue("Listado de precios ".concat(tipoLista.toString()));
        
        for (int i = 0; i < sheet.getPhysicalNumberOfRows(); i++) {
            HSSFRow row = sheet.getRow(i);
            row.setHeightInPoints(25);
        }
        
        for (short i = 0; i < numberOfCells; i++) {
            sheet.autoSizeColumn(i);
        }
        
        CellRangeAddress range = new CellRangeAddress(0, 0, 0, numberOfCells-1);
        sheet.addMergedRegion(range);
        
    }
}
