package uk.ac.ncl.cs.esc.reliable;

import java.util.ArrayList;

public class costWF {

	public ArrayList<Object> getOrder(){
		ArrayList<Object> order=new ArrayList<Object> ();
		ArrayList<ArrayList<String>> step1=new ArrayList<ArrayList<String>>();
		ArrayList<String> s1=new ArrayList<String>();
		s1.add("1127");
		s1.add("cloud3");
		step1.add(s1);
		ArrayList<String> s2=new ArrayList<String>();
		s2.add("1115");
		s2.add("cloud3");
		step1.add(s2);
		order.add(step1);
		
		ArrayList<ArrayList<String>> step2=new ArrayList<ArrayList<String>>();
		ArrayList<String> s21=new ArrayList<String>();
		s21.add("1133");
		s21.add("cloud3");
		ArrayList<String> s22=new ArrayList<String>();
		s22.add("1109");
		s22.add("cloud3");
		ArrayList<String> s23=new ArrayList<String>();
		s23.add("1538");
		s23.add("cloud1");
		ArrayList<String> s24=new ArrayList<String>();
		s24.add("1529");
		s24.add("cloud1");
		step2.add(s21);
		step2.add(s22);
		step2.add(s23);
		step2.add(s24);
		order.add(step2);
		ArrayList<ArrayList<String>> step3=new ArrayList<ArrayList<String>>();
		ArrayList<String> s31=new ArrayList<String>();
		s31.add("1115");
		s31.add("cloud3");
		ArrayList<String> s32=new ArrayList<String>();
		s32.add("1535");
		s32.add("cloud1");
		step3.add(s31);
		step3.add(s32);
		order.add(step3);
		ArrayList<ArrayList<String>> step4=new ArrayList<ArrayList<String>>();
		ArrayList<String> s41=new ArrayList<String>();
		s41.add("1119");
		s41.add("cloud3");
		order.add(step4);
		ArrayList<ArrayList<String>> step5=new ArrayList<ArrayList<String>>();
		ArrayList<String> s51=new ArrayList<String>();
		s51.add("1123");
		s51.add("cloud3");
		step5.add(s51);;
		order.add(step5);
		return order;
	}
}
