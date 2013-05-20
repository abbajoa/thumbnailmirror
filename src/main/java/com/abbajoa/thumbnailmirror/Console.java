package com.abbajoa.thumbnailmirror;

import java.io.File;
import java.io.IOException;

public class Console {
	
	public static void main(String[] args) throws IOException, InterruptedException {
		// TODO exception 처리하기.
		if(args.length < 4) {
			echoUsage();
			return;
		}					
		final File sourceDir = new File(args[0]);
		File destDir = new File(args[1]);
		int maxBoxSize = Integer.parseInt(args[2]); // TODO validation
		int quality = Integer.parseInt(args[3]); // TODO validation
		
		boolean waitingEchoed = false;
		while(true) {
			boolean convertedAny = Mirror.mirror(sourceDir, destDir, maxBoxSize, quality, new EventListener<File>() {
				@Override
				public void onEvent(File data) {
					System.out.println("Create thumbnail : " + FileUtil.getRelativePath(sourceDir, data));
				}
			});
			if(convertedAny)
				waitingEchoed = false;
			if(!waitingEchoed) {
				System.out.println("Waiting for Changes...");
				waitingEchoed = true;
			}
			Thread.sleep(10000);
		}
	}

	private static void echoUsage() {
		System.out.println("Usage:");
		System.out.println("\tthumbnailmirror [source directory] [destination directory] [max size] [jpeg quality]");
		System.out.println("Example:");
		System.out.println("\ttthumbnailmirror ~/pictures ~/mirror 1280 90");
	}
}
