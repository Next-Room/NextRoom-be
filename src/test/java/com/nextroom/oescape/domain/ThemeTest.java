package com.nextroom.oescape.domain;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ThemeTest {

	@Test
	@DisplayName("테마 생성")
	void createTheme() {
		Theme theme = new Theme();
		assertNotNull(theme);
	}
}