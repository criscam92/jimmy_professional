package jp.facades;

import java.util.List;
import jp.entidades.FacturaPromocion;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.entidades.Factura;

@Stateless
public class FacturaPromocionFacade extends AbstractFacade<FacturaPromocion> {

    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public FacturaPromocionFacade() {
        super(FacturaPromocion.class);
    }

    public List<FacturaPromocion> getFacturaPromocionByFactura(Factura factura, EntityManager em) {
        if (em == null) {
            em = getEntityManager();
        }
        try {
            Query query = em.createQuery("SELECT fp FROM FacturaPromocion fp WHERE fp.factura.id = :fac");
            query.setParameter("fac", factura.getId());
            return query.getResultList();
        } catch (Exception e) {
        }
        return null;
    }

}
