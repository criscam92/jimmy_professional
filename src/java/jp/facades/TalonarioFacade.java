package jp.facades;

import jp.entidades.Talonario;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.entidades.Empleado;
import jp.util.TipoTalonario;

@Stateless
public class TalonarioFacade extends AbstractFacade<Talonario> {

    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TalonarioFacade() {
        super(Talonario.class);
    }

    public Talonario getTalonarioByFecha(Empleado e) {
        try {
            Query query = getEntityManager().createQuery("SELECT t FROM Talonario t WHERE t.empleado.id = :empleado AND t.tipo = :tipo ORDER BY t.fecha DESC");
            query.setParameter("empleado", e.getId());
            query.setParameter("tipo", TipoTalonario.REMISION.getValor());
            query.setMaxResults(1);
            return (Talonario) query.getSingleResult();
        } catch (Exception ex) {
        }
        return null;
    }

}
