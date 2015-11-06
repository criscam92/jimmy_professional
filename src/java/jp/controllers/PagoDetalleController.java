package jp.controllers;

import jp.entidades.*;
import jp.facades.*;

import java.io.*;
import java.util.*;
import java.util.logging.*;
import javax.ejb.*;
import javax.faces.bean.*;
import javax.faces.component.*;
import javax.faces.context.*;
import javax.faces.convert.*;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;

@ManagedBean(name = "pagoDetalleController")
@SessionScoped
public class PagoDetalleController implements Serializable {

    @EJB
    private PagoDetalleFacade ejbFacade;

    private List<DetallePagoHelper> items = null;
    private DetallePagoHelper selected;

    public PagoDetalleController() {
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

    public List<DetallePagoHelper> getItems() {
        if (items == null || items.isEmpty()) {
            items = getFacade().getListPublicidad();
        }
        return items;
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
        header.setCellValue("Listado de precios");

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
        return items != null && !items.isEmpty();
    }

}
