package com.android.iquicker.common;

import java.util.ArrayList;
import java.util.List;

public class PanelButtonConfig {
	public int id;
	public int showflag = 1;
	public int firstconnect = 0;
	public String hideport;
	public String hidekey;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	public String getPingbiduankou() {
		return hideport;
	}
	public void setPingbiduankou(String pingbiduankou) {
		this.hideport = pingbiduankou;
	}
	public String getPingbiguanjianzi() {
		return hidekey;
	}
	public void setPingbiguanjianzi(String pingbiguanjianzi) {
		this.hidekey = pingbiguanjianzi;
	}
	
	public int getShowflag() {
		return showflag;
	}
	public void setShowflag(int showflag) {
		this.showflag = showflag;
	}

	public int getFirstconnect() {
		return firstconnect;
	}
	public void setFirstconnect(int firstconnect) {
		this.firstconnect = firstconnect;
	}


	public String btn0PkgName = "";
	
	public String btn1PkgName = "";
	
	public String btn2PkgName = "";
	
	public String btn3PkgName = "";
	
	public String btn4PkgName = "";
	
	public String btn5PkgName = "";
	
	public String btn6PkgName = "";
	
	public String btn7PkgName = "";
	
	public String btn8PkgName = "";
	
	public String btn9PkgName = "";
	public String btn10PkgName = "";
	public String btn11PkgName = "";
	public String btn12PkgName = "";
	public String btn13PkgName = "";
	public String btn14PkgName = "";
	public String btn15PkgName = "";
	public String btn16PkgName = "";
	public String btn17PkgName = "";

	public long pretick = 0;
	
	public long getPretick() {
		return pretick;
	}
	public void setPretick(long tick) {
		this.pretick = tick;
	}
	public String getBtn0PkgName() {
		return btn0PkgName;
	}
	public void setBtn0PkgName(String btn0PkgName) {
		this.btn0PkgName = btn0PkgName;
	}
	public String getBtn1PkgName() {
		return btn1PkgName;
	}
	public void setBtn1PkgName(String btn1PkgName) {
		this.btn1PkgName = btn1PkgName;
	}
	public String getBtn2PkgName() {
		return btn2PkgName;
	}
	public void setBtn2PkgName(String btn2PkgName) {
		this.btn2PkgName = btn2PkgName;
	}
	public String getBtn3PkgName() {
		return btn3PkgName;
	}
	public void setBtn3PkgName(String btn3PkgName) {
		this.btn3PkgName = btn3PkgName;
	}
	public String getBtn4PkgName() {
		return btn4PkgName;
	}
	public void setBtn4PkgName(String btn4PkgName) {
		this.btn4PkgName = btn4PkgName;
	}
	public String getBtn5PkgName() {
		return btn5PkgName;
	}
	public void setBtn5PkgName(String btn5PkgName) {
		this.btn5PkgName = btn5PkgName;
	}
	public String getBtn6PkgName() {
		return btn6PkgName;
	}
	public void setBtn6PkgName(String btn6PkgName) {
		this.btn6PkgName = btn6PkgName;
	}
	public String getBtn7PkgName() {
		return btn7PkgName;
	}
	public void setBtn7PkgName(String btn7PkgName) {
		this.btn7PkgName = btn7PkgName;
	}
	public String getBtn8PkgName() {
		return btn8PkgName;
	}
	public void setBtn8PkgName(String btn8PkgName) {
		this.btn8PkgName = btn8PkgName;
	}
	
	public String getBtn9PkgName() {
		return btn9PkgName;
	}
	public void setBtn9PkgName(String btn9PkgName) {
		this.btn9PkgName = btn9PkgName;
	}
	public String getBtn10PkgName() {
		return btn10PkgName;
	}
	public void setBtn10PkgName(String btn10PkgName) {
		this.btn10PkgName = btn10PkgName;
	}
	public String getBtn11PkgName() {
		return btn11PkgName;
	}
	public void setBtn11PkgName(String btn11PkgName) {
		this.btn11PkgName = btn11PkgName;
	}
	public String getBtn12PkgName() {
		return btn12PkgName;
	}
	public void setBtn12PkgName(String btn12PkgName) {
		this.btn12PkgName = btn12PkgName;
	}
	public String getBtn13PkgName() {
		return btn13PkgName;
	}
	public void setBtn13PkgName(String btn13PkgName) {
		this.btn13PkgName = btn13PkgName;
	}
	public String getBtn14PkgName() {
		return btn14PkgName;
	}
	public void setBtn14PkgName(String btn14PkgName) {
		this.btn14PkgName = btn14PkgName;
	}
	public String getBtn15PkgName() {
		return btn15PkgName;
	}
	public void setBtn15PkgName(String btn15PkgName) {
		this.btn15PkgName = btn15PkgName;
	}
	public String getBtn16PkgName() {
		return btn16PkgName;
	}
	public void setBtn16PkgName(String btn16PkgName) {
		this.btn16PkgName = btn16PkgName;
	}
	public String getBtn17PkgName() {
		return btn17PkgName;
	}
	public void setBtn17PkgName(String btn17PkgName) {
		this.btn17PkgName = btn17PkgName;
	}
	public ArrayList<String> getAllBtnList()
	{
		ArrayList<String> btnArr = new ArrayList<String>();
		btnArr.add(btn0PkgName);
		btnArr.add(btn1PkgName);
		btnArr.add(btn2PkgName);
		btnArr.add(btn3PkgName);
		btnArr.add(btn4PkgName);
		btnArr.add(btn5PkgName);
		btnArr.add(btn6PkgName);
		btnArr.add(btn7PkgName);
		btnArr.add(btn8PkgName);
		
		btnArr.add(btn10PkgName);
		btnArr.add(btn11PkgName);
		btnArr.add(btn12PkgName);
		btnArr.add(btn13PkgName);
		btnArr.add(btn14PkgName);
		btnArr.add(btn15PkgName);
		btnArr.add(btn16PkgName);
		btnArr.add(btn17PkgName);
		btnArr.add(btn9PkgName);
		
		return btnArr;
	}
	
	public void SetAllBtnList(ArrayList<String> list)
	{
		btn0PkgName = list.get(0);
		btn1PkgName = list.get(1);
		btn2PkgName = list.get(2);
		btn3PkgName = list.get(3);
		btn4PkgName = list.get(4);
		btn5PkgName = list.get(5);
		btn6PkgName = list.get(6);
		btn7PkgName = list.get(7);
		btn8PkgName = list.get(8);
		
		btn10PkgName = list.get(10);
		btn11PkgName = list.get(11);
		btn12PkgName = list.get(12);
		btn13PkgName = list.get(13);
		btn14PkgName = list.get(14);
		btn15PkgName = list.get(15);
		btn16PkgName = list.get(16);
		btn17PkgName = list.get(17);
		btn9PkgName = list.get(9);
	}
}
