package com.project.store;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class ProductServiceApplicationTests {

	@Test
	@DisplayName("should load Spring context successfully")
	void shouldBeAbleToLoadContext() {
		// arrange
		// (Spring Boot test context)

		// act
		// (context loads by running this test)

		// assert
		// (no exception thrown implies context loaded successfully)
	}
}
