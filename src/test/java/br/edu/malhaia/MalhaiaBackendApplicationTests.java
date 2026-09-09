package br.edu.malhaia;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:malhaia;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE",
		"spring.datasource.username=sa",
		"spring.datasource.password=",
		"spring.datasource.driver-class-name=org.h2.Driver",
		"spring.jpa.hibernate.ddl-auto=create-drop",
		"spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
		"spring.flyway.enabled=false",
		"malhaia.jwt.secret=test-secret-that-is-long-enough-for-hs256-algorithm-requirements-ok!!",
		"malhaia.abacus.api-key=",
		"malhaia.gemini.api-key=",
		"malhaia.cors.allowed-origin=http://localhost:5173"
})
@ActiveProfiles("test")
class MalhaiaBackendApplicationTests {

	@Test
	void contextLoads() {
	}
}
