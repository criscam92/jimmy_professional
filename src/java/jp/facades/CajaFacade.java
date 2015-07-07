package jp.facades;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.entidades.Caja;

@Stateless
public class CajaFacade extends AbstractFacade<Caja>{
    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    public CajaFacade() {
        super(Caja.class);
    }
    
    public Caja getCaja(){
        Caja cajaMenor;
        try {
            Query q = em.createQuery("SELECT c FROM Caja c");
            q.setMaxResults(1);
            cajaMenor = (Caja) q.getSingleResult();
        } catch (Exception e) {
            cajaMenor = null;
        }
        return cajaMenor;
    }
}
