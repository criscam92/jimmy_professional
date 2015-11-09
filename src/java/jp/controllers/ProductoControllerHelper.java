package jp.controllers;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import jp.entidades.Producto;
import java.util.List;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import jp.entidades.ProductoHelper2;
import jp.facades.ProductoFacade;
import jp.facades.ParametrosFacade;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.util.CellRangeAddress;

@ManagedBean(name = "productoControllerHelper")
@ViewScoped
public class ProductoControllerHelper {

    @EJB
    private ProductoFacade productoFacade;
    @EJB
    private ParametrosFacade parametrosFacade;
    private List<ProductoHelper2> items = null;
    private ProductoHelper2 selected;
    private SimpleDateFormat sdf;
    private Date fechaIni, fechaFin;

    public ProductoControllerHelper() {
    }

    @PostConstruct
    public void init() {
        sdf = new SimpleDateFormat("dd/MMM/yyyy");
        Map<String, String> requestMap = FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap();
        FacesContext.getCurrentInstance().getExternalContext().getSession(true);

        String fechaUno = requestMap.get("date1");
        String fechaDos = requestMap.get("date2");

        if ((fechaUno != null && !fechaUno.trim().isEmpty()) && (fechaDos != null && !fechaDos.trim().isEmpty())) {
            try {
                items = getProductoFacade().getProductosHelper2(sdf.parse(fechaUno), sdf.parse(fechaDos));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public ProductoFacade getProductoFacade() {
        return productoFacade;
    }

    public ParametrosFacade getParametrosFacade() {
        return parametrosFacade;
    }

    public List<ProductoHelper2> getItems() {
        if (items == null) {
            items = new ArrayList<>();
        }
        return items;
    }

    public void setItems(List<ProductoHelper2> items) {
        this.items = items;
    }

    public ProductoHelper2 getSelected() {
        if (selected == null) {
            selected = new ProductoHelper2();
        }
        return selected;
    }

    public void setSelected(ProductoHelper2 selected) {
        this.selected = selected;
    }

    public Date getFechaIni() {
        return fechaIni;
    }

    public void setFechaIni(Date fechaIni) {
        this.fechaIni = fechaIni;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
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
        header.setCellValue("Reporte Productos");

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

    public String getNombreByProducto(Producto producto) {
        return producto.getNombre() + " " + (producto.getGramaje() == null ? "" : producto.getGramaje());
    }

    public Long getBonificacionesByProducto(Producto producto) {
        return getProductoFacade().getTotalBonificacionesByProducto(producto);
    }

    /**
     * @param tipo (1 - Total Unit. Bonificaciones, 2 - Total Unit. Vent)
     * @return total
     */
    public int getCantidadTotal(int tipo) {
        int valor = 0;
        switch (tipo) {
            case 1:
                for (ProductoHelper2 ph2 : items) {
                    valor += ph2.getCantBonificacion();
                }
                break;
            case 2:
                for (ProductoHelper2 ph2 : items) {
                    valor += ph2.getCantVenta();
                }
                break;
        }
        return valor;
    }

    /**
     * @param tipo (1 - Total Valor Unit. Bonificacion, 2 - Total Valor Unit.
     * Venta, 3 - Total Valor Total)
     * @return total.
     */
    public double getValorTotal(int tipo) {
        double valor = 0.0;
        switch (tipo) {
            case 1:
                for (ProductoHelper2 ph2 : items) {
                    valor += ph2.getValBonificacion();
                }
                break;
            case 2:
                for (ProductoHelper2 ph2 : items) {
                    valor += ph2.getValVenta();
                }
                break;
            case 3:
                for (ProductoHelper2 ph2 : items) {
                    valor += (ph2.getValBonificacion() + ph2.getValVenta());
                }
                break;
        }
        return valor;
    }

    public void redirect() throws IOException {
        String url = "?";
        if (fechaIni != null) {
            url += "&date1=" + sdf.format(fechaIni);
        }
        if (fechaFin != null) {
            url += "&date2=" + sdf.format(fechaFin);
        }
        FacesContext.getCurrentInstance().getExternalContext().redirect(url);
    }
}
