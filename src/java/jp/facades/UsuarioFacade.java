package jp.facades;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import jp.entidades.Empleado;
import jp.entidades.Usuario;
import jp.seguridad.Encrypt;

@Stateless
public class UsuarioFacade extends AbstractFacade<Usuario> {
    
    @PersistenceContext(unitName = "jimmy_professionalPU")
    private EntityManager em;
    
    @Override
    protected EntityManager getEntityManager() {
        return em;
    }
    
    public UsuarioFacade() {
        super(Usuario.class);
    }
    
    public boolean getUsuarioByNombre(Usuario usuario) {
        try {
            Query query = getEntityManager().createNamedQuery("Usuario.findByUsuario");
            query.setParameter("usuario", usuario.getUsuario());
            query.setMaxResults(1);
            
            Usuario user = (Usuario) query.getSingleResult();
            
            if (user != null) {
                if (usuario.getId() != null) {
                    return !user.getId().equals(usuario.getId());
                } else {
                    return true;
                }
            }
            
        } catch (Exception e) {            
        }
        return false;
    }
    
    public Usuario login(String userName, String password) {
        Usuario usuario = null;
        try {
            Query query = getEntityManager().createQuery("SELECT u FROM Usuario u WHERE u.usuario = :usuario AND u.contrasena = :contrasena");
            query.setParameter("usuario", userName);
            query.setParameter("contrasena", Encrypt.getStringMessageDigest(password));
            usuario = (Usuario) query.getSingleResult();
        } catch (NoResultException e) {
            System.out.println("\n======================= ERROR CONSULTANDO EL USUARIO Y CONTRASENA ======================");
//            e.printStackTrace();
            System.out.println("======================= ERROR CONSULTANDO EL USUARIO Y CONTRASENA ======================\n");
        }
        return usuario;
    }
    
    public Empleado empleadoUsuarioExist(Empleado e, Usuario u){
        try {
            Empleado empleado;
            Query q = getEntityManager().createQuery("SELECT u.empleado FROM Usuario u WHERE u.empleado.id= :emp");
            q.setParameter("emp", e.getId());
            q.setMaxResults(1);
            empleado = (Empleado) q.getSingleResult();
            return empleado;
        } catch (NoResultException exc) {
            return null;
        }
    }
}
