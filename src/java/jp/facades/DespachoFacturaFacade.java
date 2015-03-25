package jp.facades;

import jp.entidades.DespachoFactura;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

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
    
}
