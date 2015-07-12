package jp.seguridad;

import java.io.Serializable;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import jp.entidades.Usuario;
import jp.facades.UsuarioFacade;

@ManagedBean(name = "currentUser")
@ViewScoped
public class UsuarioActual implements Serializable {

    private Usuario usuario;

    @EJB
    private UsuarioFacade usuarioFacade;

    @PostConstruct
    public void init() {
        try {
            Usuario usuarioTMP = (Usuario) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(FiltroSeguridad.AUTH_KEY);
            this.usuario = usuarioFacade.find(usuarioTMP.getId());
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
