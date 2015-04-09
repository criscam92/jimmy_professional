package jp.facades;

import jp.entidades.PagoDevolucion;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 *
 * @author arturo
 */
@Stateless
public class PagoDevolucionFacade extends AbstractFacade<PagoDevolucion> {
    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public PagoDevolucionFacade() {
        super(PagoDevolucion.class);
    }
    
}
