package com.abbajoa.thumbnailmirror;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;



public class MirrorTest {
	
	private static final String TEST_INPUT_DIR = "test-input";
	private static final String TEST_OUTPUT_DIR = "test-output";

	@Test
	public void testCannotReadFile() throws Exception {
		Mirror.mirror(new File(TEST_INPUT_DIR), new File(TEST_OUTPUT_DIR), 100, 90, new EventListener<File>() {
			public void onEvent(File data) {
			}
		});
		Assert.assertTrue(new File(TEST_OUTPUT_DIR + "/" + "invalid.jpg.1374200587122.100.90.jpg").exists());
	}
	
}
