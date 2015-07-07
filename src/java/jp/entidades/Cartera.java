package jp.entidades;

import java.util.List;

public class Cartera {

    private Cliente cliente;
    private List<CarteraFactura> listCarteraFacturas;

    public Cartera() {
    }
    
    public Cartera(Cliente cliente, List<CarteraFactura> listCarteraFacturas) {
        this.cliente = cliente;
        this.listCarteraFacturas = listCarteraFacturas;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public List<CarteraFactura> getListCarteraFacturas() {
        return listCarteraFacturas;
    }

    public void setListCarteraFacturas(List<CarteraFactura> listCarteraFacturas) {
        this.listCarteraFacturas = listCarteraFacturas;
    }   
    
}
