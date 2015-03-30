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

    public List<DespachoFactura> getDespachosFacturaByFactura(Factura factura) {
        try {
            Query query = getEntityManager().createQuery("SELECT df FROM DespachoFactura df WHERE df.factura.id = :fac");
            query.setParameter("fac", factura.getId());
            return query.getResultList();
        } catch (Exception e) {
        }
        return null;
    }

    public long countDespachoFacturaByFactura(Factura factura) {
        try {
            Query query = getEntityManager().createQuery("SELECT COUNT(df) FROM DespachoFactura df WHERE df.factura.id = :fac");
            query.setParameter("fac", factura.getId());
            return (long) query.getSingleResult();
        } catch (Exception e) {
        }
        return 0;
    }

}
