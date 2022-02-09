package cl.wallet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.wallet.servicio.impl.TransactionImpl;

@RestController
@RequestMapping("/api") 

public class TansactionController {

		@Autowired
		TransactionImpl service;

		@PostMapping(path = "/consulta-saldo", produces = MediaType.APPLICATION_JSON_VALUE)
		public String getSaldoBtc() {
			String respose = service.consultaSaldo();
			return respose;
		}
		
		@PostMapping(path = "/send-btc", produces = MediaType.APPLICATION_JSON_VALUE)
		public String sendBtc() {
			String respose = service.sendBtc();
			return respose;
		}
}