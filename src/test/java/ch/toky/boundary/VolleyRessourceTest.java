package ch.toky.boundary;


import static org.hamcrest.Matchers.hasSize;

import io.quarkus.test.common.http.TestHTTPEndpoint;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.security.TestSecurity;
import io.quarkus.test.security.jwt.JwtSecurity;
import io.restassured.RestAssured;
import org.junit.jupiter.api.Test;

@QuarkusTest
@TestHTTPEndpoint(VolleyRessource.class)
class VolleyRessourceTest {

  @Test
  @TestSecurity(user = "rest tester", roles = "keine")
  @JwtSecurity
  void testJwt() {
    RestAssured.get().body();
  }

}