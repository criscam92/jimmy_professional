package jp.facades;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.entidades.CajaMenor;

@Stateless
public class CajaMenorFacade extends AbstractFacade<CajaMenor>{
    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    public CajaMenorFacade() {
        super(CajaMenor.class);
    }
    
    public CajaMenor getCajaMenor(){
        CajaMenor cajaMenor;
        try {
            Query q = em.createQuery("SELECT cm FROM CajaMenor cm");
            q.setMaxResults(1);
            cajaMenor = (CajaMenor) q.getSingleResult();
        } catch (Exception e) {
            cajaMenor = null;
        }
        return cajaMenor;
    }
}
