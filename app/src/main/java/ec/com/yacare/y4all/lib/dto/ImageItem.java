package ec.com.yacare.y4all.lib.dto;

import android.graphics.Bitmap;

public class ImageItem {
	private Bitmap image;
	private String title;
	private String idEvento;

	public ImageItem(Bitmap image, String title, String idEvento) {
		super();
		this.image = image;
		this.title = title;
		this.idEvento = idEvento;
	}

	public ImageItem(Bitmap image, String title) {
		super();
		this.image = image;
		this.title = title;
	}


	public Bitmap getImage() {
		return image;
	}

	public void setImage(Bitmap image) {
		this.image = image;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIdEvento() {
		return idEvento;
	}

	public void setIdEvento(String idEvento) {
		this.idEvento = idEvento;
	}
}