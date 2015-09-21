package jp.facades;

import java.util.Collections;
import java.util.Comparator;
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
import jp.entidades.Pago;
import jp.util.TipoTalonario;

@Stateless
public class TalonarioFacade extends AbstractFacade<Talonario> {

    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;
    @EJB
    private FacturaFacade FacturaFacade;
    @EJB
    private PagoFacade pagoFacade;

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

    public PagoFacade getPagoFacade() {
        return pagoFacade;
    }

    /**
     * Obtiene un talonario activo para el empleado y tipo indicados
     *
     * @param tipo tipo de talonario a buscar
     * @param e empleado del cual encontrar el talonario
     * @return Talonario activo para el tipo y empleado indicados, si no
     * encuentra uno activo, retorna valor nulo
     * @see TipoTalonario
     */
    public Talonario getActiveTalonario(TipoTalonario tipo, Empleado e) {
        try {
            Query query = getEntityManager().createQuery("SELECT t FROM Talonario t WHERE t.empleado.id = :empleado AND t.tipo = :tipo AND t.actual <= t.final1");
            query.setParameter("empleado", e.getId());
            query.setParameter("tipo", tipo.getValor());
            List<Talonario> talonarios = query.getResultList();

            Collections.sort(talonarios, new Comparator() {
                @Override
                public int compare(Object o1, Object o2) {
                    String uno = String.valueOf(((Talonario) o1).getFinal1());
                    String dos = String.valueOf(((Talonario) o2).getFinal1());
                    return new Integer(uno).compareTo(new Integer(dos));
                }
            });

            return talonarios.get(0);

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * Obtiene el talonario de remisiones activo para el empleado indicado
     *
     * @param e Empleado del cual se desea obtener el talonario
     * @return talonario activo para remisiones
     * @deprecated use en su lugar getActiveTalonario()
     */
    @Deprecated
    public Talonario getTalonarioByFecha(Empleado e) {
        return getActiveTalonario(TipoTalonario.REMISION, e);
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

    /**
     *
     * @param talonario
     * @param orden
     * @param tipo (1 - Factura y 2 - Pago)
     * @return
     */
    public boolean update(Talonario talonario, Long orden, int tipo) {
        if (talonario.getFinal1() == orden) {
            return true;
        } else {
            Long op;

            switch (tipo) {
                case 1:
                    op = newOrdenPedidoFactura(orden + 1);
                    break;
                case 2:
                    op = newOrdenPedidoPago(orden + 1);
                    break;
                default:
                    return false;
            }

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

    public Long newOrdenPedidoFactura(Long ordenPedido) {
        Factura factura = getFacturaFacade().getFacturaByOrdenPedido(ordenPedido.toString());
        if (factura != null) {
            ordenPedido++;
            return newOrdenPedidoFactura(ordenPedido);
        } else {
            return ordenPedido;
        }
    }

    private Long newOrdenPedidoPago(Long ordenPago) {
        Pago pago = getPagoFacade().getPagosByOrdenPago(ordenPago.toString());
        if (pago != null) {
            ordenPago++;
            return newOrdenPedidoFactura(ordenPago);
        } else {
            return ordenPago;
        }
    }

}
