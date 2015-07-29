package jp.facades;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.entidades.TipoSalidaProducto;

@Stateless
public class TipoSalidaProductoFacade extends AbstractFacade<TipoSalidaProducto> {
    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TipoSalidaProductoFacade() {
        super(TipoSalidaProducto.class);
    }
    
    public boolean existeDetalle(TipoSalidaProducto tipoSalidaProducto){
        try {
            String jpql = "SELECT COUNT(ts.id) FROM TipoSalidaProducto ts WHERE UPPER(ts.detalle) = :det";
            if(tipoSalidaProducto.getId()!=null){
                jpql += " AND c.id != :id";
            }
            Query query = getEntityManager().createQuery(jpql);
            query.setParameter("det", tipoSalidaProducto.getDetalle().toUpperCase());
            if(tipoSalidaProducto.getId()!=null){
                query.setParameter("id", tipoSalidaProducto.getId());
            }
            Long result = (Long) query.getSingleResult();
            if(result>0l){
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
}
