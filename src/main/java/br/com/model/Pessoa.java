package br.com.model;

public class Pessoa {
    private static Long id;
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    private String endereco;

    public Pessoa() {
    }

    public Pessoa(long id, String nome, String cpf, String email, String telefone, String endereco) {
       Pessoa.id = id;
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.telefone = telefone;
        this.endereco = endereco;
    }

    public static long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getCpf() {
        return cpf;
    }

    public String getEmail() {
        return email;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setId(long id) {
        Pessoa.id = id;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    @Override
    public String toString() {
        return "Pessoa{" + "id=" + id +
                ",nome='" + nome + '\'' +
                ",cpf='" + cpf + '\'' +
                ",email='" + email + '\'' +
                "telefone='" + telefone + '\'' +
                ",endereco='" + endereco + '\'' + '}';
    }
}
