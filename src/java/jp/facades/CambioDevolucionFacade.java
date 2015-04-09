package jp.facades;

import jp.entidades.CambioDevolucion;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.entidades.Devolucion;

@Stateless
public class CambioDevolucionFacade extends AbstractFacade<CambioDevolucion> {
    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public CambioDevolucionFacade() {
        super(CambioDevolucion.class);
    }
    
    public CambioDevolucion getCambioDevolucionByDevolucion(Devolucion d){
        CambioDevolucion cambioDevolucionTMP;
        try {
            Query q = em.createQuery("SELECT cd FROM CambioDevolucion cd WHERE cd.devolucion.id= :dev");
            q.setParameter("dev", d.getId());
            cambioDevolucionTMP = (CambioDevolucion) q.getSingleResult();
            return cambioDevolucionTMP;
        } catch (NoResultException e) {
            return null;
        }
    }
    
}
