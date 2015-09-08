package jp.facades;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import jp.entidades.ListaPrecio;

@Stateless
public class ListaPrecioFacade extends AbstractFacade<ListaPrecio> {
    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ListaPrecioFacade() {
        super(ListaPrecio.class);
    }
    
}
