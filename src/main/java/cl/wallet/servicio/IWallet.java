package cl.wallet.servicio;

import cl.wallet.to.Request;
import cl.wallet.to.Response;

public interface IWallet {

	public Response walletGen(Request request);

}
