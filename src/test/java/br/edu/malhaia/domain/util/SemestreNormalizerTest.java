package br.edu.malhaia.domain.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SemestreNormalizerTest {

	@Test
	void normalizaPontosBarrasEHifens() {
		assertEquals("2025.1", SemestreNormalizer.normalizar("2025.1"));
		assertEquals("2025.1", SemestreNormalizer.normalizar("2025/1"));
		assertEquals("2025.2", SemestreNormalizer.normalizar("2025-2"));
		assertEquals("2026.1", SemestreNormalizer.normalizar(" 2026 / 1 "));
	}

	@Test
	void variantesIncluemCanonico() {
		assertTrue(SemestreNormalizer.variantes("2025/1").contains("2025.1"));
		assertTrue(SemestreNormalizer.variantes("2025/1").contains("2025/1"));
	}
}
