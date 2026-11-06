package br.edu.malhaia.domain.util;

import java.util.Locale;

public final class TextoSeguro {

	private static final int DEFAULT_MAX = 2000;

	private TextoSeguro() {
	}

	public static String sanitizarParaLlm(String bruto) {
		return sanitizarParaLlm(bruto, DEFAULT_MAX);
	}

	public static String sanitizarParaLlm(String bruto, int maxLen) {
		if (bruto == null) {
			return "";
		}
		String s = bruto
				.replace('\u0000', ' ')
				.replaceAll("[\\p{Cntrl}&&[^\r\n\t]]", " ")
				.replaceAll("[\\r\\n\\t]+", " ")
				.replaceAll("\\s{2,}", " ")
				.trim();
		if (s.length() > maxLen) {
			s = s.substring(0, maxLen);
		}
		return s;
	}

	public static String enveloparComoDado(String texto) {
		String seguro = sanitizarParaLlm(texto);
		return "<<USER_DATA>>\n" + seguro + "\n<</USER_DATA>>";
	}

	public static String normalizarEmail(String email) {
		if (email == null) {
			return "";
		}
		return email.trim().toLowerCase(Locale.ROOT);
	}
}
