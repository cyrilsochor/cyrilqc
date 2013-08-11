package com.cyrilqc.core;

import java.util.Comparator;

public class CyrilQCTestNameComparator implements Comparator<CyrilQCTest> {

	public int compare(CyrilQCTest o1, CyrilQCTest o2) {
		if (o1 == null) {
			if (o2 == null) {
				return 0;
			} else {
				return -1;
			}
		} else {
			if (o2 == null) {
				return 1;
			} else {
				return o1.getName().compareTo(o2.getName());
			}
		}

	}

}
