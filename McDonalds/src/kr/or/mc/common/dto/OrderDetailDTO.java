package kr.or.mc.common.dto;

public class OrderDetailDTO {
	// 주문상세 테이블
	private String product_image;
	private String product_name;
	private int order_amount;

	public String getProduct_image() {
		return product_image;
	}

	public void setProduct_image(String product_image) {
		this.product_image = product_image;
	}

	public String getProduct_name() {
		return product_name;
	}

	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}

	public int getOrder_amount() {
		return order_amount;
	}

	public void setOrder_amount(int order_amount) {
		this.order_amount = order_amount;
	}

	@Override
	public String toString() {
		return "OrderDetailDTO [product_image=" + product_image + ", product_name=" + product_name + ", order_amount="
				+ order_amount + "]";
	}

}
