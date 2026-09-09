package br.edu.malhaia.adapters.in.web;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
		"spring.datasource.url=jdbc:h2:mem:malhaia_rea;MODE=PostgreSQL;DB_CLOSE_DELAY=-1;DATABASE_TO_LOWER=TRUE",
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
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AlunoReaAccessSecurityTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@WithMockUser(roles = "ALUNO")
	void alunoNaoConsegueConsultarReaDeOutroUsuario() throws Exception {
		UUID outroUsuario = UUID.randomUUID();
		mockMvc.perform(get("/api/aluno/{usuarioId}/rea", outroUsuario))
				.andExpect(status().isForbidden());
	}
}
