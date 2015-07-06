package jp.facades;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import jp.entidades.ActualizaBaseCajaMenor;

@Stateless
public class ActualizaCajaFacade extends AbstractFacade<ActualizaBaseCajaMenor>{
    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    public ActualizaCajaFacade() {
        super(ActualizaBaseCajaMenor.class);
    }
}
