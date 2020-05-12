package br.com.jefffreitas.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

import java.util.ArrayList;
import java.util.Arrays;

import org.hamcrest.core.Is;
import org.junit.Assert;
import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;

public class UserJsonTest {
	
	@Test
	public void deveVerVerificarPrimeiroNivel() {
		given()//Pr� condi��es
		.when()//A��es
		     .get("http://restapi.wcaquino.me/USERS/1")
		.then()//Assertivas
		     .statusCode(200)
		     .body("id", is(1))
		     .body("name", containsString("Silva"))
		     .body("age", greaterThan(18))
		;
	}
	
	@Test
	public void deveVerificarPrimeiroNivelOutrasFormas() {
		Response response = RestAssured.request(Method.GET, "http://restapi.wcaquino.me/USERS/1");
		
		//path
		System.out.println(response.path("id"));
		
		//jsonpath
		JsonPath jpath = new JsonPath(response.asString());
		Assert.assertEquals(1, jpath.getInt("id"));
		
	}
	@Test
	public void deveVerificarSegundoNivel() {
		given()//Pr� condi��es
		.when()//A��es
		     .get("http://restapi.wcaquino.me/USERS/2")
		.then()//Assertivas
		     .statusCode(200)
		     .body("name", containsString("Joaquina"))
		     .body("endereco.rua", is("Rua dos bobos"))
		;
	}
	@Test
	public void deveVerificarLista() {
		given()//Pr� condi��es
		.when()//A��es
		     .get("http://restapi.wcaquino.me/USERS/3")
		.then()//Assertivas
		     .statusCode(200)
		     .body("id", is(3))
		     .body("name", containsString("Ana"))
		     .body("filhos[0].name", is("Zezinho"))
		     .body("filhos[1].name", is("Luizinho"))
		     .body("age", greaterThan(18))
		     
		     ;
	}
	@Test
	public void deveRetornarErroUsuarioInesistente() {
		given()//Pr� condi��es
		.when()//A��es
		     .get("http://restapi.wcaquino.me/USERS/4")
		.then()//Assertivas
		     .statusCode(404)
		     .body("error", is("Usu�rio inexistente"))
		     ;
	}
	@Test 
	public void deveVerificarListaRaiz() {
		given()//Pr� condi��es
		.when()//A��es
		     .get("http://restapi.wcaquino.me/USERS")
		.then()//Assertivas
		     .statusCode(200)
		     .body("$", hasSize(3))
		     .body("age[1]", is(25))
		     .body("filhos.name", hasItem(Arrays.asList("Zezinho", "Luizinho")))
		     .body("salary", contains(1234.5678f, 2500, null ))
		     ;
		
	}
	@Test
	public void devoFazerVerificacoesAvancadas() {
		given()//Pr� condi��es
		.when()//A��es
		     .get("http://restapi.wcaquino.me/USERS")
		.then()//Assertivas
		.body("$", hasSize(3))
		.body("age.findAll{it <= 25}.size", is(2))
		.body("age.findAll{it <= 25 && it > 20}.size", is(1))
		.body("findAll{it.age <= 25 && it.age > 20}.name", hasItem("Maria Joaquina"))
		.body("findAll{it.age <= 25}[0].name", is("Maria Joaquina"))
		.body("findAll{it.age <= 25}[-1].name", is("Ana J�lia"))
		.body("find{it.age <= 25}.name", is("Maria Joaquina"))
		.body("findAll{it.name.contains('n')}.name", hasItems("Maria Joaquina", "Ana J�lia"))
		.body("findAll{it.name.length() > 10}.name", hasItems("Jo�o da Silva", "Maria Joaquina"))
		.body("name.collect{it.toUpperCase()}", hasItem("MARIA JOAQUINA"))
		.body("name.findAll{it.startsWith('Maria')}.collect{it.toUpperCase()}.toArray()", allOf(arrayContaining("MARIA JOAQUINA"), arrayWithSize(1)))
		;
	}
	@Test
	public void devoUnirJsonPathComJava() {
		ArrayList<String> names = 
		given()//Pr� condi��es
		.when()//A��es
		     .get("http://restapi.wcaquino.me/USERS")
		.then()//Assertivas
		.statusCode(200)
		.extract().path("name.findAll{it.startsWith('Maria')}")
		;
		Assert.assertEquals(1, names.size());
		Assert.assertTrue(names.get(0).equalsIgnoreCase("mArIa JoAqUiNa"));
		Assert.assertEquals(names.get(0).toUpperCase(), "maria joaquina".toUpperCase());
	}

}
