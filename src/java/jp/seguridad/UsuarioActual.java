package jp.seguridad;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import jp.entidades.Usuario;
import jp.facades.UsuarioFacade;
import jp.util.TipoUsuario;

@ManagedBean(name = "usuarioActual")
@ViewScoped
@Stateless
public class UsuarioActual implements Serializable {

    private Usuario usuario;
    @EJB
    private UsuarioFacade usuarioFacade;

    @PostConstruct
    public void init() {
        try {            
            Usuario usuarioTMP = (Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(FiltroSeguridad.AUTH_KEY);
            usuario = new Usuario();
            this.usuario = usuarioFacade.find(usuarioTMP.getId());
            System.out.println("USUARIO: " + usuario.getUsuario());
            System.out.println("USUARIO TIPO #: " + usuario.getTipo());
            System.out.println("USUARIO TIPO: " + TipoUsuario.getFromValue(Integer.valueOf(usuario.getTipo() + "")));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
