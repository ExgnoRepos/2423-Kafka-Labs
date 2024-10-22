package x.utils;

import static org.junit.Assert.*;

import java.text.NumberFormat;

import org.junit.Test;

public class TempTest {

	@Test
	public void test1() {
		NumberFormat format = NumberFormat.getInstance();
		System.out.println(format.format(1000));
		System.out.println(format.format(100000.2423));
	}

}
