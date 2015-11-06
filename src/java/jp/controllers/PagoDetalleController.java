package jp.controllers;

import java.io.IOException;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import jp.entidades.DetallePagoHelper;
import jp.entidades.PagoDetalle;
import jp.facades.PagoDetalleFacade;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;

@ManagedBean(name = "pagoDetalleController")
@ViewScoped
public class PagoDetalleController implements Serializable {

    @EJB
    private PagoDetalleFacade ejbFacade;

    private List<DetallePagoHelper> items = null;
    private List<DetallePagoHelper> comisiones = null;
    private DetallePagoHelper selected;
    private Date fechaIni, fechaFin;
    private Date fechaIniC, fechaFinC;
    private SimpleDateFormat formatoDelTexto;

    public PagoDetalleController() {
//        fechaIni = new Date();
//        fechaFin = new Date();
//        fechaIniC = new Date();
//        fechaFinC = new Date();
    }
    
    @PostConstruct    
    public void init() {
        formatoDelTexto = new SimpleDateFormat("dd/MMM/yyyy");

        Map<String, String> requestMap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        FacesContext.getCurrentInstance().getExternalContext().getSession(true);
        String date1 = requestMap.get("date1");
        String date2 = requestMap.get("date2");
        String date3 = requestMap.get("date3");
        String date4 = requestMap.get("date4");

        if ((date1 != null && !date1.trim().isEmpty()) && (date2 != null && !date2.trim().isEmpty())) {
            items = getFacade().getListPublicidad(date1, date2);
        } else if ((date3 != null && !date3.trim().isEmpty()) && (date4 != null && !date4.trim().isEmpty())) {
            comisiones = getFacade().getListComisiones(date3, date4);
        }

    }

    public DetallePagoHelper getSelected() {
        return selected;
    }

    public void setSelected(DetallePagoHelper selected) {
        this.selected = selected;
    }

    private PagoDetalleFacade getFacade() {
        return ejbFacade;
    }

    public DetallePagoHelper prepareCreate() {
        selected = new DetallePagoHelper();
        return selected;
    }

    public Date getFechaIni() {
        return fechaIni;
    }

    public void setFechaIni(Date fechaIni) {
        this.fechaIni = fechaIni;
    }

    public Date getFechaIniC() {
        return fechaIniC;
    }

    public void setFechaIniC(Date fechaIniC) {
        this.fechaIniC = fechaIniC;
    }

    public Date getFechaFinC() {
        return fechaFinC;
    }

    public void setFechaFinC(Date fechaFinC) {
        this.fechaFinC = fechaFinC;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public List<DetallePagoHelper> getItems() {
        if (items == null) {
            items = getFacade().getListPublicidad(null, null);
        }
        return items;
    }

    public List<DetallePagoHelper> getComisiones() {
        if (comisiones == null || comisiones.isEmpty()) {
            comisiones = getFacade().getListComisiones(null, null);
        }
        return comisiones;
    }

    public List<PagoDetalle> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<PagoDetalle> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = PagoDetalle.class)
    public static class PagoDetalleControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            PagoDetalleController controller = (PagoDetalleController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "pagoDetalleController");
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
            if (object instanceof PagoDetalle) {
                PagoDetalle o = (PagoDetalle) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), PagoDetalle.class.getName()});
                return null;
            }
        }

    }

    public void postProcessXLS2(Object document) {
        exportExcel(document, "Listado Comisión");
    }

    public void postProcessXLS(Object document) {
        exportExcel(document, "Listado Publicidad");
    }

    private void exportExcel(Object document, String title) {
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
        header.setCellValue(title);

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

    public boolean disable() {
        return (items != null && !items.isEmpty());
    }

    public boolean disableC() {
        return (comisiones != null && !comisiones.isEmpty());
    }

    public void redirect() throws IOException {
        String url = "?";
        if (fechaIni != null) {
            url += "&date1=" + formatoDelTexto.format(fechaIni);
        }
        if (fechaFin != null) {
            url += "&date2=" + formatoDelTexto.format(fechaFin);
        }
        FacesContext.getCurrentInstance().getExternalContext().redirect(url);
    }

    public void redirect2() throws IOException {
        String url = "?";
        if (fechaIniC != null) {
            url += "&date3=" + formatoDelTexto.format(fechaIniC);
        }
        if (fechaFinC != null) {
            url += "&date4=" + formatoDelTexto.format(fechaFinC);
        }
        FacesContext.getCurrentInstance().getExternalContext().redirect(url);
    }
}
