package com.bancom.app.demo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.bancom.app.demo.repository.UsuarioRepository;
import com.bancom.app.demo.entities.Usuario;
import com.bancom.app.demo.service.IServiceUsuario;

@SpringBootTest
class BancomApplicationTests {

	@MockBean
	UsuarioRepository usuarioDao;

	@Autowired
    IServiceUsuario serviceUsuario;

	@Test
	void contextLoads() {
		Usuario usuarioFind1 = serviceUsuario.findById(Long.valueOf(1));

		Usuario usuarioSearch = new Usuario();
		usuarioSearch.setNickname("aquito1");
		usuarioSearch.setPassword("123456");

		Usuario usuarioLogin = serviceUsuario.login(usuarioSearch);

		//assertEquals(usuarioLogin.getId(), usuarioFind1.getId());
		assertEquals(usuarioLogin, usuarioFind1);
	}
}