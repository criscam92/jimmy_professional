package jp.facades;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;
import jp.entidades.ReciboCaja;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.entidades.Tercero;
import jp.util.EstadoPagoFactura;

@Stateless
public class ReciboCajaFacade extends AbstractFacade<ReciboCaja> {
    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;
    @EJB
    private CajaFacade cajaFacade;

    @Override
    protected EntityManager getEntityManager() {
        return em;
    }

    public ReciboCajaFacade() {
        super(ReciboCaja.class);
    }
    
    public List<ReciboCaja> getRecibosCaja(Date fecha1, Date fecha2){
        List<ReciboCaja> reciboCajas;
        Map<String, Object> parametros = new HashMap<>();
        String consulta = "SELECT rc FROM ReciboCaja rc WHERE rc.fecha >= :fec";
        try {
            if (fecha1 != null && fecha2 != null) {
                consulta += " AND (rc.fecha BETWEEN :fechaInicio AND :fechaFin)";
                parametros.put("fechaInicio", fecha1);
                parametros.put("fechaFin", fecha2);
            }
            consulta += " ORDER BY rc.fecha DESC";
            Query q = em.createQuery(consulta);
            q.setParameter("fec", cajaFacade.getCaja().getFechaActualizacion());
            
            for (Map.Entry<String, Object> entrySet : parametros.entrySet()) {
                String key = entrySet.getKey();
                Object value = entrySet.getValue();
                
                q.setParameter(key, value);
                
            }
            reciboCajas = q.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
            reciboCajas = new ArrayList<>();
        }
        return reciboCajas;
    }
    
}
