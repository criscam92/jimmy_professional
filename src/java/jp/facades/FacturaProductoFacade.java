/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jp.facades;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.entidades.Factura;
import jp.entidades.FacturaProducto;

/**
 *
 * @author CRISTIAN
 */
@Stateless
public class FacturaProductoFacade extends AbstractFacade<FacturaProducto> {
    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public FacturaProductoFacade() {
        super(FacturaProducto.class);
    }
    
    public Long getCantidadVentasByFactura(Factura f){
        Long cantidad = 0l;
        try {
            Query query = getEntityManager().createQuery("SELECT SUM(fp.unidadesVenta) FROM FacturaProducto fp WHERE fp.factura.id=:fact");
            query.setParameter("fact", f.getId());
            cantidad = (Long) query.getSingleResult();
        } catch (Exception e) {
            cantidad = null;
           e.printStackTrace();
        }
        return cantidad;
    }
    
    public Long getCantidadBonificacionByFactura(Factura f){
        Long cantidad = 0l;
        try {
            Query query = getEntityManager().createQuery("SELECT SUM(fp.unidadesBonificacion) FROM FacturaProducto fp WHERE fp.factura.id=:fact");
            query.setParameter("fact", f.getId());
            cantidad = (Long) query.getSingleResult();
        } catch (Exception e) {
            cantidad = null;
           e.printStackTrace();
        }
        return cantidad;
    }
}
