package jp.controllers;

import java.util.List;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import jp.entidades.Devolucion;
import jp.entidades.DevolucionProducto;
import jp.facades.DevolucionFacade;

@ManagedBean(name = "DevolucionSessionBean")
@SessionScoped
@Stateless
public class DevolucionSessionBean {
    
    private Devolucion devolucion;
    private List<DevolucionProducto> devolucionProductoList;
    @EJB
    private DevolucionFacade EJBdevolucionFacade;
    
    public DevolucionSessionBean() {
        
    }

    public Devolucion getDevolucion() {
        return devolucion;
    }

    public void setDevolucion(Devolucion devolucion) {
        this.devolucion = devolucion;
    }

    public List<DevolucionProducto> getDevolucionProductoList() {
        return devolucionProductoList;
    }

    public void setDevolucionProductoList(List<DevolucionProducto> devolucionProductoList) {
        this.devolucionProductoList = devolucionProductoList;
    }

    public DevolucionFacade getEJBdevolucionFacade() {
        return EJBdevolucionFacade;
    }
    
}
