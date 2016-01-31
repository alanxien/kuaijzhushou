package com.android.iquicker.common;

import android.graphics.drawable.Drawable;

public class ListViewData {
	
	private Drawable image;
	
	private String program_name;
	
	private String package_name;
	
	private int button_id;
	
	public ListViewData() {
		super();
	}

	

	public ListViewData(Drawable image, String program_name, String package_name, int button_id) {
		super();
		this.image = image;
		this.program_name = program_name;
		this.package_name = package_name;
		this.button_id = button_id;
	}



	public int getButton_id() {
		return button_id;
	}

	public void setButton_id(int button_id) {
		this.button_id = button_id;
	}

	public Drawable getImage() {
		return image;
	}

	public void setImage(Drawable image) {
		this.image = image;
	}

	public String getProgram_name() {
		return program_name;
	}

	public void setProgram_name(String program_name) {
		this.program_name = program_name;
	}

	public String getPackage_name() {
		return package_name;
	}

	public void setPackage_name(String package_name) {
		this.package_name = package_name;
	}
	
	

}
