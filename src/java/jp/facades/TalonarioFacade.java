package jp.facades;

import java.util.List;
import javax.ejb.EJB;
import jp.entidades.Talonario;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.entidades.Empleado;
import jp.entidades.Factura;
import jp.util.TipoTalonario;

@Stateless
public class TalonarioFacade extends AbstractFacade<Talonario> {

    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;
    @EJB
    private FacturaFacade FacturaFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public TalonarioFacade() {
        super(Talonario.class);
    }

    public FacturaFacade getFacturaFacade() {
        return FacturaFacade;
    }

    public Talonario getTalonarioByFecha(Empleado e) {
        try {
            Query query = getEntityManager().createQuery("SELECT t FROM Talonario t WHERE t.empleado.id = :empleado AND t.tipo = :tipo ORDER BY t.fecha DESC");
            query.setParameter("empleado", e.getId());
            query.setParameter("tipo", TipoTalonario.REMISION.getValor());
            query.setMaxResults(1);
            return (Talonario) query.getSingleResult();
        } catch (NoResultException nre) {
        }
        return null;
    }

    public List<Talonario> getTalonariosByTipo(TipoTalonario tipoTalonario, Empleado empleado) {
        try {
            Query query = getEntityManager().createQuery("SELECT t FROM Talonario t WHERE t.tipo = :tipo AND t.empleado.id = :empleado");
            query.setParameter("tipo", tipoTalonario.getValor());
            query.setParameter("empleado", empleado.getId());
            return query.getResultList();
        } catch (Exception e) {
        }
        return null;
    }

    public boolean update(Talonario talonario, Long ordenPedido) {
        if (talonario.getFinal1() == ordenPedido) {
            return true;
        } else {
            Long op = newOrdenPedido(ordenPedido + 1);
            try {
                talonario.setActual(op);
                getEntityManager().merge(talonario);
                return true;
            } catch (Exception e) {
                System.out.println("====== ERROR ACTUALIZANDO EL TALONARIO ======");
                e.printStackTrace();
                System.out.println("====== ERROR ACTUALIZANDO EL TALONARIO ======");
            }
        }
        return false;
    }

    public Long newOrdenPedido(Long ordenPedido) {
        Factura factura = getFacturaFacade().getFacturaByOrdenPedido(ordenPedido.toString());
        if (factura != null) {
            ordenPedido++;
            return newOrdenPedido(ordenPedido);
        } else {
            return ordenPedido;
        }
    }

}
