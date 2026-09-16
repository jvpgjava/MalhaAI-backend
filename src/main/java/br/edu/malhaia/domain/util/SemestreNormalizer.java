package br.edu.malhaia.domain.util;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Normaliza rótulos de semestre letivo para o canônico {@code YYYY.N}
 * (aceita {@code 2025.1}, {@code 2025/1}, {@code 2025-1}).
 */
public final class SemestreNormalizer {

	private static final Pattern SEMESTRE = Pattern.compile(
			"^\\s*(\\d{4})\\s*[./\\-]?\\s*([12])\\s*$"
	);

	private SemestreNormalizer() {
	}

	public static String normalizar(String bruto) {
		if (bruto == null || bruto.isBlank()) {
			return null;
		}
		Matcher m = SEMESTRE.matcher(bruto.trim());
		if (!m.matches()) {
			// fallback: troca / e - por ponto e limpa espaços
			return bruto.trim()
					.replace('/', '.')
					.replace('-', '.')
					.replaceAll("\\s+", "")
					.toLowerCase(Locale.ROOT);
		}
		return m.group(1) + "." + m.group(2);
	}

	/** Variantes possíveis para lookup em bases legadas. */
	public static Set<String> variantes(String bruto) {
		String canon = normalizar(bruto);
		if (canon == null) {
			return Set.of();
		}
		LinkedHashSet<String> out = new LinkedHashSet<>();
		out.add(canon);
		String[] parts = canon.split("\\.");
		if (parts.length == 2) {
			out.add(parts[0] + "/" + parts[1]);
			out.add(parts[0] + "-" + parts[1]);
		}
		return out;
	}

	public static boolean isValido(String bruto) {
		return bruto != null && SEMESTRE.matcher(bruto.trim()).matches();
	}

	public static List<String> presets() {
		return List.of("2025.1", "2025.2", "2026.1", "2026.2");
	}
}
