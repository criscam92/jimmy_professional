package jp.facades;

import java.util.List;
import jp.entidades.DespachoFactura;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.entidades.Factura;

@Stateless
public class DespachoFacturaFacade extends AbstractFacade<DespachoFactura> {

    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public DespachoFacturaFacade() {
        super(DespachoFactura.class);
    }

    public List<DespachoFactura> getDespachosFacturaByFactura(Factura factura, boolean soloRealizadas) {
        try {
            String consulta = "SELECT df FROM DespachoFactura df WHERE df.factura.id = :fac";            
            if (soloRealizadas) {
                 consulta += " AND df.realizado = :rea";
            }
            
            Query query = getEntityManager().createQuery(consulta);
            query.setParameter("fac", factura.getId());
            
            if (soloRealizadas) {
                 query.setParameter("rea", true);
            }            
            
            return query.getResultList();
        } catch (Exception e) {
        }
        return null;
    }

    public long countDespachoFacturaByFactura(Factura factura) {
        try {
            Query query = getEntityManager().createQuery("SELECT COUNT(df) FROM DespachoFactura df WHERE df.factura.id = :fac AND df.realizado = :rea");
            query.setParameter("fac", factura.getId());
            query.setParameter("rea", true);
            return (long) query.getSingleResult();
        } catch (Exception e) {
        }
        return 0;
    }

}
