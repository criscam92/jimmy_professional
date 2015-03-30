package jp.facades;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.entidades.Factura;
import jp.entidades.FacturaProducto;

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
        Long cantidadFP = 0l;
        Long cantidadFPP = 0l;
        Long cantidad = 0l;
        try {
            Query queryFP = getEntityManager().createQuery("SELECT SUM(fp.unidadesVenta) FROM FacturaProducto fp WHERE fp.factura.id=:fact");
            queryFP.setParameter("fact", f.getId());
            cantidadFP = (Long) queryFP.getSingleResult();
            if (cantidadFP == null) {
                cantidadFP = 0l;
            }
            
            Query queryFPP = getEntityManager().createQuery("SELECT SUM(fpp.unidadesVenta) FROM FacturaPromocion fpp WHERE fpp.factura.id=:fact");
            queryFPP.setParameter("fact", f.getId());
            cantidadFPP = (Long) queryFPP.getSingleResult();
            if (cantidadFPP == null) {
                cantidadFPP = 0l;
            }
            
            cantidad = cantidadFP + cantidadFPP;
        } catch (Exception e) {
            cantidad = null;
           e.printStackTrace();
        }
        return cantidad;
    }
    
    public Long getCantidadBonificacionByFactura(Factura f){
        Long cantidadFP = 0l;
        Long cantidadFPP = 0l;
        Long cantidad = 0l;
        try {
            Query queryFP = getEntityManager().createQuery("SELECT SUM(fp.unidadesBonificacion) FROM FacturaProducto fp WHERE fp.factura.id=:fact");
            queryFP.setParameter("fact", f.getId());
            cantidadFP = (Long) queryFP.getSingleResult();
            if (cantidadFP == null) {
                cantidadFP = 0l;
            }
            
            Query queryFPP = getEntityManager().createQuery("SELECT SUM(fpp.unidadesBonificacion) FROM FacturaPromocion fpp WHERE fpp.factura.id=:fact");
            queryFPP.setParameter("fact", f.getId());
            cantidadFPP = (Long) queryFPP.getSingleResult();
            if (cantidadFPP == null) {
                cantidadFPP = 0l;
            }
            
            cantidad = cantidadFP + cantidadFPP;
        } catch (Exception e) {
            cantidad = null;
           e.printStackTrace();
        }
        return cantidad;
    }
    
    public String getNumOrden(){
        try {
            Long numOrden = 0l;
            Query q = em.createNativeQuery("SELECT nextval('factura_num_orden')");
            q.setMaxResults(1);
            numOrden = (Long) q.getSingleResult();
            return ""+numOrden;
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<FacturaProducto> getFacturaProductosByFactura(Factura factura) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
