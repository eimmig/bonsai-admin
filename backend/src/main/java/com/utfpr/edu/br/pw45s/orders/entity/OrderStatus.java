package com.utfpr.edu.br.pw45s.orders.entity;

public enum OrderStatus {
	AGUARDANDO_PAGAMENTO("Aguardando Pagamento"),
	PAGO("Pago"),
	CANCELADO("Cancelado"),
	EM_TRANSPORTE("Em Transporte"),
	ENTREGUE("Entregue");

	private final String label;

	OrderStatus(String label) {
		this.label = label;
	}

	public String getLabel() {
		return label;
	}
}