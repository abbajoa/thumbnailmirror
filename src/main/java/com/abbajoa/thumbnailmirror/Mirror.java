package com.abbajoa.thumbnailmirror;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import javax.imageio.IIOException;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;

public class Mirror {
	
	public static boolean mirror(final File sourceDir, final File destDir, final int maxBoxSize, final int quality, final EventListener<File> createEventListener) throws IOException, FileNotFoundException {
		if(!sourceDir.isDirectory())
			throw new IOException(sourceDir + " is not a directory");
		final Set<File> absoluteDestFiles = new HashSet<File>();
		final Set<File> destDirectories = new HashSet<File>();
		final Set<File> converted = new HashSet<File>();
		FileUtil.visitAllFiles(sourceDir, new Visitor<File, IOException>() {
			public void visit(File file) throws IOException {
				if(!isConvertableImageFile(file))
					return;
				File destFile = new File(destDir.getAbsolutePath() + "/" + FileUtil.getRelativePath(sourceDir, file) + "." + file.lastModified() + "." + maxBoxSize + "." + quality + ".jpg");
				absoluteDestFiles.add(destFile);
				if(!destFile.exists()) {
					File dir = destFile.getParentFile();
					destDirectories.add(dir);
					FileUtil.ensureDirectoryExist(dir);
					File tempFile = new File(destFile.getAbsolutePath() + ".tmp");
					createEventListener.onEvent(file);
					createJpegThumbnail(file, tempFile, maxBoxSize, quality);
					tempFile.renameTo(destFile);
					converted.add(file);
				}
			}
		});
		deleteUnnecessaryFiles(destDir, absoluteDestFiles);
		deleteUnnecessaryDirectories(destDir, destDirectories);
		return !converted.isEmpty();
	}


	private static boolean isConvertableImageFile(File file) {
		return file.getAbsolutePath().toLowerCase().endsWith(".jpg");
	}

	private static void createJpegThumbnail(File src, File dest, int maxBoxSize, int quality) throws FileNotFoundException, IOException {
		FileImageInputStream fis = new FileImageInputStream(src);
		try {
			ImageReader reader = ImageUtil.createJpegReader();
			try {
				reader.setInput(fis);
				IIOMetadata exifOfNull = null;
				BufferedImage original;
				try {
					exifOfNull = ImageUtil.readJpegExifTags(reader);
				} catch (IIOException e) {
					exifOfNull = null;
					System.out.println("WARNING: " + "cannot read exif from " + src + " (" + e.getMessage() + ")"); // TODO introduce listener
				}
				try {
					original = ImageUtil.readImage(reader);
				} catch (IIOException e) {
					original = loadEmptyImage();
					System.out.println("WARNING: " + "cannot read image (" + e.getMessage() + ")"); // TODO introduce listener
				}
				BufferedImage rescaled = ImageUtil.getRescaled(original, maxBoxSize);
				try {
					ImageUtil.writeJpeg(rescaled, exifOfNull, quality / 100f, dest);
				} catch (IIOException e) {
					System.out.println("WARNING: " + "cannot create image (" + e.getMessage() + ")"); // TODO introduce listener
				}
			} finally {
				reader.dispose();
			}
		} finally {
			fis.close();
		}
	}


	private static BufferedImage loadEmptyImage() throws IOException {
		ImageInputStream fis = new MemoryCacheImageInputStream(Mirror.class.getResourceAsStream("/empty.jpg"));
		try {
			ImageReader reader = ImageUtil.createJpegReader();
			try {
				reader.setInput(fis);
				return ImageUtil.readImage(reader);
			} finally {
				reader.dispose();
			}
		} finally {
			fis.close();
		}
	}

	private static void deleteUnnecessaryFiles(final File destDir, final Set<File> necessaryAbsoluteFiles) throws IOException, FileNotFoundException {
		FileUtil.visitAllFiles(destDir, new Visitor<File, IOException>() {
			public void visit(File file) throws IOException {
				if(!necessaryAbsoluteFiles.contains(file.getAbsoluteFile())) {
					file.delete();
				}
			}
		});
	}

	private static void deleteUnnecessaryDirectories(final File destDir, final Set<File> necessaryDirectories) throws IOException, FileNotFoundException {
		final ArrayList<File> toremove = new ArrayList<File>();
		FileUtil.visitAllSubDirectories(destDir, new Visitor<File, IOException>() {
			public void visit(File data) throws IOException {
				if(!necessaryDirectories.contains(data))
					toremove.add(data);
			}
		});
		Collections.sort(toremove, new Comparator<File>() {
			public int compare(File o1, File o2) {
				Integer l1 = o1.getAbsolutePath().length();
				Integer l2 = o2.getAbsolutePath().length();
				return - l1.compareTo(l2);
			}
		});
		for(File f : toremove)
			f.delete();
	}

}
