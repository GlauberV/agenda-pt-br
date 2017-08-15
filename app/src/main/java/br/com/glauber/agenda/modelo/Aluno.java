package br.com.glauber.agenda.modelo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Aluno implements Serializable{

//  @JsonProperty("idCliente") Foi usado como uma artemanha do professor Alex Filipe para percebermos a importância de se melhorar nosso id.
    private String id;

    private String nome;
    private String endereco;
    private String telefone;
    private String site;
    private Double nota;
    private String caminhoFoto;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public Double getNota() {
        return nota;
    }

    public void setNota(Double nota) {
        this.nota = nota;
    }

    public String getCaminhoFoto() {
        return caminhoFoto;
    }

    public void setCaminhoFoto(String caminhoFoto) {
        this.caminhoFoto = caminhoFoto;
    }

    //Sem esse método o android não exibira os alunos como desejamos em nossa lista.
    @Override
    public String toString() {
        return getId() + " - " + getNome();
    }

    //So pra testar as informações do aluno.
    public String detalhesDoAluno() {
        return "Detalhes: \n"
                + "ID: " + getId() + "\n"
                + "Nome: " + getNome() + "\n"
                + "Endereço: " + getEndereco() + "\n"
                + "Telefone: " + getTelefone() + "\n"
                + "Site: " + getSite() + "\n"
                + "Nota: " + getNota();
    }

}

// Linha 9 - Usando o "JsonProperty" estamos resolvendo o problema com nossa aplicação web, já que nela
// nosso atributo "id" se chama "idCliente". Caso nós não estivessímos utilizando o retrofit,
// resolveriamos "apenas" trocando a chave manualmente no JSON.

// Se proseguirmos apenas com isso feito, teremos outro problema. O app web não vai reconhecer que
// o aluno enviado é mesmo aluno que já existe lá, assim criando uma duplicata com, uma com o id
// certo e outra com o id=0. Quando temos um aluno com o id = 0 o servidor vai reconhece-lo como se
// tivesse sido criado pelo próprio servidor. Então teremos que deletar esses alunos e reenviá-los
// com seus respectivos id's.

// Infelizmente agora teremos mais um problema quando um novo aluno for inserido por causa do nosso
// método "postAluno" do SQLite.(continuamos lá, obrigado)