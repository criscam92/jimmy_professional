package jp.facades;

import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.entidades.Salida;
import jp.entidades.SalidaProducto;

@Stateless
public class SalidaFacade extends AbstractFacade<Salida> {

    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public SalidaFacade() {
        super(Salida.class);
    }

    public List<SalidaProducto> getSalidasProductoBySalida(Salida s) {
        List<SalidaProducto> salidaProductos;
        if (s == null) {
            salidaProductos = new ArrayList<>();
            return salidaProductos;
        }
        try {
            Query q = getEntityManager().createQuery("SELECT sp FROM SalidaProducto sp WHERE sp.salida.id= :idsalida");
            q.setParameter("idsalida", s.getId());
            salidaProductos = q.getResultList();
        } catch (Exception e) {
            salidaProductos = new ArrayList<>();
            System.out.println("Salida sin productos");
            e.printStackTrace();
        }
        return salidaProductos;
    }

}
