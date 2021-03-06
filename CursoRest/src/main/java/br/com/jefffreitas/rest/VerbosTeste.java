package br.com.jefffreitas.rest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

public class VerbosTeste {

	
	@Test
	public void deveSalvarUmUsuario() {
		given()
		.log().all()
		.contentType("application/json")
		.body("{ \"name\": \"Jose\",	\"age\": 50 }")
		.when()
		.post("http://restapi.wcaquino.me/users")
		.then()
		.log().all()
		.statusCode(201)
		.body("id", is(notNullValue()))
		.body("name", is("Jose"))
		.body("age", is(50))
		;
	}
	

	@Test
	public void deveSalvarUmUsuarioUsandoMap() {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("name", "usuario via map");
		params.put("age", 25);
		given()
		.log().all()
		.contentType("application/json")
		.body(params)
		.when()
		.post("http://restapi.wcaquino.me/users")
		.then()
		.log().all()
		.statusCode(201)
		.body("id", is(notNullValue()))
		.body("name", is("usuario via map"))
		.body("age", is(25))
		;
	}

	@Test
	public void deveSalvarUmUsuarioUsandoObjeto() {
		User user = new User("Usuario via objeto", 35);
		given()
		.log().all()
		.contentType("application/json")
		.body(user)
		.when()
		.post("http://restapi.wcaquino.me/users")
		.then()
		.log().all()
		.statusCode(201)
		.body("id", is(notNullValue()))
		.body("name", is("Usuario via objeto"))
		.body("age", is(35))
		;
	}
	

	

	@Test
	public void deveDeserealizarObjetoSalvarUsuario() {
		User user = new User("Usuario deserealizado", 35);
		User usuarioInserido = given()
		.log().all()
		.contentType("application/json")
		.body(user)
		.when()
		.post("http://restapi.wcaquino.me/users")
		.then()
		.log().all()
		.statusCode(201)
		.extract().body().as(User.class)
		;
		System.out.println(usuarioInserido);
		Assert.assertThat(usuarioInserido.getId(), is(notNullValue()));
		Assert.assertEquals("Usuario deserealizado", usuarioInserido.getName());
		Assert.assertThat(usuarioInserido.getAge(), is(35));
	}
	
	@Test
	public void naoDeveSalvarUsuarioSemNome() {
		given()
		.log().all()
		.contentType("application/json")
		.body("{ \"age\": 50 }")
		.when()
		.post("http://restapi.wcaquino.me/users")
		.then()
		.log().all()
		.statusCode(400)
		.body("id", is(nullValue()))
		.body("error", is("Name � um atributo obrigat�rio"))
		;
	}
	
	@Test
	public void deveSalvarUmUsuarioXML() {
		given()
		.log().all()
		.contentType(ContentType.XML)
		.body("<user><name>Jose</name><age>50</age></user>")
		.when()
		.post("http://restapi.wcaquino.me/usersXML")
		.then()
		.log().all()
		.statusCode(201)
		.body("user.@id", is(notNullValue()))
		.body("user.name", is("Jose"))
		.body("user.age", is("50"))
		;
	}
	
	@Test
	public void deveSalvarUmUsuarioViaXMLUsandoObjeto() {
		User user = new User("Usuario XML", 40);
		
		given()
		    .log().all()
		    .contentType(ContentType.XML)
		    .body(user)
	   .when()
		    .post("http://restapi.wcaquino.me/usersXML")
		.then()
		    .log().all()
		    .statusCode(201)
		    .body("user.@id", is(notNullValue()))
		    .body("user.name", is("Usuario XML"))
		    .body("user.age", is("40"))
		;
	}
	

	@Test
	public void deveDeserealizarXMLAoSalvarUsuario() {
		User user = new User("Usuario XML", 40);
		
		User usuarioInserido = given()
		.log().all()
		.contentType(ContentType.XML)
		.body(user)
		.when()
		.post("http://restapi.wcaquino.me/usersXML")
		.then()
		.log().all()
		.statusCode(400)
		.extract().body().as(User.class)
		;
		Assert.assertThat(usuarioInserido.getId(), notNullValue());
		Assert.assertThat(usuarioInserido.getName(), is("Usuario XML"));
		Assert.assertThat(usuarioInserido.getAge(), is(40));
	    Assert.assertThat(usuarioInserido.getSalary(), nullValue());
	}
	
	@Test
	public void deveAlterarUsuario() {
		given()
		.log().all()
		.contentType("application/json")
		.body("{ \"name\": \"Usuario alterado\",	\"age\": 80 }")
		.when()
		.put("http://restapi.wcaquino.me/users/1")
		.then()
		.log().all()
		.statusCode(200)
		.body("id", is(1))
		.body("name", is("Usuario alterado"))
		.body("age", is(80))
		.body("salary", is(1234.5678f))
		;
	
	}

	@Test
	public void devoCustomizarURL() {
		given()
		.log().all()
		.contentType("application/json")
		.body("{ \"name\": \"Usuario alterado\",	\"age\": 80 }")
		.when()
		.put("http://restapi.wcaquino.me/{entidade}/{userId}" , "users" , "1")
		.then()
		.log().all()
		.statusCode(200)
		.body("id", is(1))
		.body("name", is("Usuario alterado"))
		.body("age", is(80))
		.body("salary", is(1234.5678f))
		;
	
	}

	@Test
	public void devoCustomizarURLParte2() {
		given()
		.log().all()
		.contentType("application/json")
		.body("{ \"name\": \"Usuario alterado\",	\"age\": 80 }")
		.pathParam("entidade", "users")
		.pathParam("userId", 1)
		.when()
		.put("http://restapi.wcaquino.me/{entidade}/{userId}")
		.then()
		.log().all()
		.statusCode(200)
		.body("id", is(1))
		.body("name", is("Usuario alterado"))
		.body("age", is(80))
		.body("salary", is(1234.5678f))
		;
	
	}

	@Test
   public void deveRemoverUsuario() {
	   given()  
	       .log().all()
	   .when()
	       .delete("http://restapi.wcaquino.me/users/1")
	   .then()
	       .log().all()
	       .statusCode(204)
	   ;
   }
	

	@Test
   public void deveRemoverUsuarioInexistente() {
	   given()  
	       .log().all()
	   .when()
	       .delete("http://restapi.wcaquino.me/users/1000")
	   .then()
	       .log().all()
	       .statusCode(400)
	       .body("error", is("Registro inexistente"))
	   ;
   }
}


