package br.com.service;
import br.com.model.Pessoa;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import java.util.Arrays;
import java.util.List;

public class PessoaService {

    private final String BASE_URL = "http://localhost:8080/api/pessoas";
    private final RestTemplate restTemplate = new RestTemplate();

    public List<Pessoa> listar() {
        ResponseEntity<Pessoa[]> response = restTemplate.getForEntity(BASE_URL, Pessoa[].class);
        assert response.getBody() != null;
        return Arrays.asList(response.getBody());
    }

    public Pessoa buscarPorId(Long id) {
        return restTemplate.getForObject(BASE_URL + "/" + id, Pessoa.class);
    }

    public void salvar(Pessoa pessoa) {
        restTemplate.postForEntity(BASE_URL, pessoa, Pessoa.class);
    }

    public void atualizar(Pessoa pessoa) {
        restTemplate.put(BASE_URL + "/" + Pessoa.getId(), pessoa);
    }

    public void deletar(Long id) {
        restTemplate.delete(BASE_URL + "/" + id);
    }
}
