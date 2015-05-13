package jp.facades;

import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import jp.entidades.auxiliar.Codificable;


public abstract class AbstractFacade<T> {

    private final Class<T> entityClass;

    public AbstractFacade(Class<T> entityClass) {
        this.entityClass = entityClass;
    }

    protected abstract EntityManager getEntityManager();

    public void create(T entity) {
        getEntityManager().persist(entity);
    }

    public void edit(T entity) {
        getEntityManager().merge(entity);
    }

    public void remove(T entity) {
        getEntityManager().remove(getEntityManager().merge(entity));
    }

    public T find(Object id) {
        return getEntityManager().find(entityClass, id);
    }

    public List<T> findAll() {
        CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        return getEntityManager().createQuery(cq).getResultList();
    }

    public List<T> findRange(int[] range) {
        CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        cq.select(cq.from(entityClass));
        Query q = getEntityManager().createQuery(cq);
        q.setMaxResults(range[1] - range[0] + 1);
        q.setFirstResult(range[0]);
        return q.getResultList();
    }

    public int count() {
        CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
        Root<T> rt = cq.from(entityClass);
        cq.select(getEntityManager().getCriteriaBuilder().count(rt));
        Query q = getEntityManager().createQuery(cq);
        return ((Long) q.getSingleResult()).intValue();
    }

    public <U extends Codificable> Boolean getEntityByCodigoOrTipo(U entity) {
        try {
            String sql = "SELECT e FROM " + entityClass.getSimpleName() + " e WHERE";

            if (entity.getTipo() != null && !entity.getTipo().trim().isEmpty()) {
                sql += " UPPER(e.tipo)= :param";
            } else if (entity.getCodigo() != null && !entity.getCodigo().trim().isEmpty()) {
                sql += " UPPER(e.codigo)= :param";
            } else {
                sql += " UPPER(e.nombre)= :param";
            }

            Query query = getEntityManager().createQuery(sql);

            if (entity.getTipo() != null && !entity.getTipo().trim().isEmpty()) {
                query.setParameter("param", entity.getTipo().toUpperCase());
            } else if (entity.getCodigo() != null && !entity.getCodigo().trim().isEmpty()) {
                query.setParameter("param", entity.getCodigo().toUpperCase());
            } else {
                query.setParameter("param", entity.getNombre().toUpperCase());
            }

            U entidad = (U) query.getSingleResult();

            if (entidad != null) {
                if (entity.getId() != null) {
                    return !entidad.getId().equals(entity.getId());
                } else {
                    return true;
                }
            }
        } catch (NoResultException e) {
//            e.printStackTrace();
        }
        return false;
    }

}
