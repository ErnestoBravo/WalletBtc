package cl.wallet.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import cl.wallet.servicio.impl.WalletImpl;
import cl.wallet.to.Request;
import cl.wallet.to.Response;

@RestController
//@RequestMapping("/api")
public class WalletController {

	@Autowired
	WalletImpl service;

	@PostMapping(path = "/wallet-gen", produces = MediaType.APPLICATION_JSON_VALUE)
	public Response test(final HttpServletRequest httpRequest, @RequestBody final Request request) {
		Response respose = service.walletGen(request);
		return respose;
	}

}
