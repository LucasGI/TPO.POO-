package modelo;

public class Cliente extends Persona {
    private String email;
    private String paisResidencia;

    public Cliente(String nombre, String apellido, String documento, String telefono,
                   String email, String paisResidencia) {
        super(nombre, apellido, documento, telefono);
        this.email = email;
        this.paisResidencia = paisResidencia;
    }

    public String getEmail() {
        return email;
    }

    public String getPaisResidencia() {
        return paisResidencia;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
