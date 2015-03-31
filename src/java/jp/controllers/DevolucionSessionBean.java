package jp.controllers;

import java.util.List;
import javax.ejb.Stateless;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import jp.entidades.Devolucion;
import jp.entidades.DevolucionProducto;

@ManagedBean(name = "DevolucionSessionBean")
@RequestScoped
@Stateless
public class DevolucionSessionBean {
    
    private Devolucion devolucion;
    private List<DevolucionProducto> devolucionProductoList;
    
    public DevolucionSessionBean() {
        
    }

    public Devolucion getDevolucion() {
        return devolucion;
    }

    public void setDevolucion(Devolucion devolucion) {
        System.out.println("Seteando devolucionsession con==> "+devolucion.getValorTotal());
        this.devolucion = devolucion;
    }

    public List<DevolucionProducto> getDevolucionProductoList() {
        return devolucionProductoList;
    }

    public void setDevolucionProductoList(List<DevolucionProducto> devolucionProductoList) {
        this.devolucionProductoList = devolucionProductoList;
    }
}
