package cl.wallet.controlador.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.wallet.servicio.impl.WalletImpl;
import cl.wallet.to.Response;

@RestController
//@RequestMapping("/api")
public class WalletController {

	@Autowired
	WalletImpl service;

	@PostMapping(path = "/wallet-gen", produces = MediaType.APPLICATION_JSON_VALUE)
	private Response test() {
		Response respose = service.walletGen();
		return respose;
	}

}