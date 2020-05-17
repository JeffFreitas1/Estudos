package br.com.jefffreitas.rest.tests;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;

import br.com.jefffreitas.rest.core.BaseTest;
import br.com.jefffreitas.rest.core.Movimentacao;
import br.com.jefffreitas.rest.utils.DateUtils;
import io.restassured.RestAssured;
import io.restassured.specification.FilterableRequestSpecification;


@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class BarrigaTest extends BaseTest {
	
	
	private static String CONTA_NAME = "Conta " + System.nanoTime();
	private static Integer CONTA_ID;
	private static Integer MOV_ID;
	
	@BeforeClass
	public static void login () {
		Map<String, String> login = new HashMap<>();
		login.put("email", "jeferson_dent@hotmail.com");
		login.put("senha", "jeff1337");
		
		//fazendo o login
	String TOKEN = given()
		    .body(login)
		.when()
		    .post("/signin")
		.then()
		    .statusCode(200)
		    .extract().path("token");
		RestAssured.requestSpecification.header("Authorization", "JWT " + TOKEN);
	}
	
	
	
	@Test
	public void t_02deveIncluirContaComSucesso() {
		//incluindo a conta
		     CONTA_ID = given()
		     .body("{ \"nome\": \""+CONTA_NAME+"\"}")
			.when()
			.post("/contas")
			.then()
			.statusCode(201)
			.extract().path("id")
		;
	}
	
	@Test
	public void t_03deveAlterarContaComSucesso() {
		//incluindo a conta
		     given()
		     .body("{ \"nome\": \""+CONTA_NAME+" alterada\"}")
		     .pathParam("id", CONTA_ID)
			.when()
			.put("/contas/{id}")
			.then()
			.statusCode(200)
			.body("nome", is(CONTA_NAME+" alterada"))
		; 
	}
	
	@Test
	public void t_04naoDeveInserirContaComMesmoNome() {
		//incluindo a conta
		     given()
		     .body("{ \"nome\": \""+CONTA_NAME+" alterada\"}")
			.when()
			.post("/contas")
			.then()
			.statusCode(400)
			.body("error", is("J� existe uma conta com esse nome!"))
		;
	}
	
	@Test
	public void t_05deveInserirMovimentacaoComSucesso() {
		Movimentacao mov = getMovimentacaoValida();
		
		
		  MOV_ID = given()
		     .body(mov)
			.when()
			.post("/transacoes")
			.then()
			.statusCode(201)
			.extract().path("id")
		;
	}
	
	@Test
	public void t_06deveValidarCamposObrigatoriosMovimentacao() {
		     given()
		     .body("{}")
			.when()
			.post("/transacoes")
			.then()
			.statusCode(400)
			.body("$", hasSize(8))
			.body("msg", hasItems(
					"Data da Movimenta��o � obrigat�rio",
					"Data do pagamento � obrigat�rio",
					"Descri��o � obrigat�rio",
					"Interessado � obrigat�rio",
					"Valor � obrigat�rio",
					"Valor deve ser um n�mero",
					"Conta � obrigat�rio",
					"Situa��o � obrigat�rio"
					))
		;
    }
	@Test
	public void t_07naoDeveInserirMovimentacaoComDataFutura() {
		Movimentacao mov = getMovimentacaoValida();
		mov.setData_transacao("18/05/2020");
		
		     given()
		     .body(mov)
			.when()
			.post("/transacoes")
			.then()
			.statusCode(400)
			.body("$", hasSize(1))
			.body("msg", hasItem("Data da Movimenta��o deve ser menor ou igual � data atual"))
		;
	}
	
	@Test
	public void t_08naoDeveRemoverContaComMovimentacao() {
		Movimentacao mov = getMovimentacaoValida();
		mov.setData_transacao(DateUtils.getDataDiferenceDias(2));
		
		     given()
		     .pathParam("id", CONTA_ID)
			.when()
			.delete("/contas/{id}")
			.then()
			.statusCode(500)
			.body("constraint", is("transacoes_conta_id_foreign"))
		;
	}
	
	@Test
	public void t_09deveCalcularSaldoContas() {
		Movimentacao mov = getMovimentacaoValida();
		mov.setData_transacao("18/05/2020");
		     given()
			.when()
			.get("/saldo")
			.then()
			.statusCode(200)
			.body("find{it.conta_id == "+CONTA_ID+"}.saldo", is("100.00"))
		;
	}
	
	@Test
	public void t_10DeveRemoverMovimentacao() {
		Movimentacao mov = getMovimentacaoValida();
		mov.setData_transacao("18/05/2020");
		     given()
		     .pathParam("id", MOV_ID)
			.when()
			.delete("/transacoes/{id}")
			.then()
			.statusCode(204)
		;
	}	
	
	@Test
	public void t_11naoDeveAcessarAPISemToken() {
		FilterableRequestSpecification req = (FilterableRequestSpecification) RestAssured.requestSpecification;
		req.removeHeader("Authorization");
		
		given()
		.when()
		.get("/contas")
		.then()
		    .statusCode(401)
		;
	}
	
	private Movimentacao getMovimentacaoValida() {
		Movimentacao mov = new Movimentacao();
		mov.setConta_id(151580);
		mov.setConta_id(CONTA_ID);
		mov.setDescricao("Descricao da movimentacao");
		mov.setEnvolvido("Envolvido da mov");
		mov.setTipo("REC");
		mov.setData_transacao(DateUtils.getDataDiferenceDias(-1));
		mov.setData_pagamento(DateUtils.getDataDiferenceDias(5));
		mov.setValor(100F);
		mov.setStatus(true);
		return mov;
	}
}