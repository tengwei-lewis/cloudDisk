package com.cloud.disk.util;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.CopyOption;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class FileUtil {

	public static DirectoryStream<Path> getFileList(final String filePath) throws UtilException {
		return getFileList(Paths.get(filePath));
	}

	public static DirectoryStream<Path> getFileList(final Path path) throws UtilException {
		if (!Files.exists(path)) {
			throw new UtilException("file is not found");
		}
		try {
			return Files.newDirectoryStream(path, new DirectoryStream.Filter<Path>() {
				@Override
				public boolean accept(Path entry) throws IOException {
					if (Files.isHidden(entry)) {
						return false;
					}
					return true;
				}
			});
		} catch (IOException e) {
			throw new UtilException(e);
		}
	}

	public static String formetFileSize(final long fileSize) {
		DecimalFormat df = new DecimalFormat("#.00");
		String fileSizeString = "";
		String wrongSize = "0B";
		if (fileSize == 0) {
			return wrongSize;
		}
		if (fileSize < 1024) {
			fileSizeString = df.format((double) fileSize) + "B";
		} else if (fileSize < 1048576) {
			fileSizeString = df.format((double) fileSize / 1024) + "K";
		} else if (fileSize < 1073741824) {
			fileSizeString = df.format((double) fileSize / 1048576) + "M";
		} else {
			fileSizeString = df.format((double) fileSize / 1073741824) + "G";
		}
		return fileSizeString;
	}

	public static byte[] getFileToByte(final String filePath) throws UtilException {
		return getFileToByte(Paths.get(filePath));
	}

	public static byte[] getFileToByte(final Path path) throws UtilException {
		byte[] data = null;
		if (!Files.exists(path)) {
			throw new UtilException("file is not found");
		}
		FileInputStream fis = null;
		FileChannel channel = null;
		try {
			fis = new FileInputStream(path.toFile());
			channel = fis.getChannel();

			ByteBuffer buffer = ByteBuffer.allocate(1024);

			channel.read(buffer);

			data = buffer.array();
			buffer.clear();
		} catch (IOException e) {
			try {
				fis.close();
			} catch (IOException e1) {
				throw new UtilException(e1);
			}
		}
		return data;
	}

	public static List<Path> bfsSearchFileNIOByWalkFileTree(final Path path, final String name) throws UtilException {
		try {
			final List<Path> pathList = new ArrayList<>();
			Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(final Path file, BasicFileAttributes attrs) throws IOException {
					final String fileName = file.getFileName().toString();
					if (fileName.indexOf(name) != -1) {
						pathList.add(file);
					}
					return FileVisitResult.CONTINUE;
				}
			});

			return pathList;
		} catch (IOException e) {
			throw new UtilException(e);
		}
	}

	/**
	 * 复制文件夹
	 * 
	 * @param source
	 * @param target
	 * @param options
	 * @throws IOException
	 * @see {@link #operateDir(boolean, Path, Path, CopyOption...)}
	 */
	public static void copyDir(final Path source, final Path target, final CopyOption... options) throws IOException {
		operateDir(false, source, target, options);
	}

	/**
	 * 移动文件夹
	 * 
	 * @param source
	 * @param target
	 * @param options
	 * @throws IOException
	 * @see {@link #operateDir(boolean, Path, Path, CopyOption...)}
	 */
	public static void moveDir(final Path source, final Path target, final CopyOption... options) throws IOException {
		operateDir(true, source, target, options);
	}

	/**
	 * 复制/移动文件夹
	 * 
	 * @param move
	 *            操作标记，为true时移动文件夹,否则为复制
	 * @param source
	 *            要复制/移动的源文件夹
	 * @param target
	 *            源文件夹要复制/移动到的目标文件夹
	 * @param options
	 *            文件复制选项
	 * @throws IOException
	 * @see Files#move(Path, Path, CopyOption...)
	 * @see Files#copy(Path, Path, CopyOption...)
	 * @see Files#walkFileTree(Path, java.nio.file.FileVisitor)
	 */
	private static void operateDir(final boolean move, final Path source, final Path target, final CopyOption... options) throws IOException {
		if (null == source || !Files.isDirectory(source)) {
			throw new IllegalArgumentException("source must be directory");
		}
		final Path dest = target.resolve(source.getFileName());
		// 如果相同则返回
		if (Files.exists(dest) && Files.isSameFile(source, dest)) {
			return;
		}
		// 目标文件夹不能是源文件夹的子文件夹
		// isSub方法实现参见另一篇博客 http://blog.csdn.net/10km/article/details/54425614
		if (isSub(source, dest)) {
			throw new IllegalArgumentException("dest must not  be sub directory of source");
		}
		boolean clear = true;
		for (CopyOption option : options) {
			if (StandardCopyOption.REPLACE_EXISTING == option) {
				clear = false;
				break;
			}
		}
		// 如果指定了REPLACE_EXISTING选项则不清除目标文件夹
		if (clear) {
			deleteIfExists(dest);
		}
		Files.walkFileTree(source, new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				// 在目标文件夹中创建dir对应的子文件夹
				Path subDir = 0 == dir.compareTo(source) ? dest : dest.resolve(dir.subpath(source.getNameCount(), dir.getNameCount()));
				Files.createDirectories(subDir);
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
				if (move) {
					Files.move(file, dest.resolve(file.subpath(source.getNameCount(), file.getNameCount())), options);
				} else {
					Files.copy(file, dest.resolve(file.subpath(source.getNameCount(), file.getNameCount())), options);
				}
				return FileVisitResult.CONTINUE;
			}

			@Override
			public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
				// 移动操作时删除源文件夹
				if (move) {
					Files.delete(dir);
				}
				return super.postVisitDirectory(dir, exc);
			}
		});
	}

	/**
	 * 强制删除文件/文件夹(含不为空的文件夹)<br>
	 * 
	 * @param dir
	 * @throws IOException
	 * @see Files#deleteIfExists(Path)
	 * @see Files#walkFileTree(Path, java.nio.file.FileVisitor)
	 */
	public static void deleteIfExists(final Path dir) throws IOException {
		try {
			Files.deleteIfExists(dir);
		} catch (DirectoryNotEmptyException e) {
			Files.walkFileTree(dir, new SimpleFileVisitor<Path>() {
				@Override
				public FileVisitResult visitFile(final Path file, final BasicFileAttributes attrs) throws IOException {
					Files.delete(file);
					return FileVisitResult.CONTINUE;
				}

				@Override
				public FileVisitResult postVisitDirectory(final Path dir, final IOException exc) throws IOException {
					Files.delete(dir);
					return super.postVisitDirectory(dir, exc);
				}
			});
		}
	}

	/**
	 * 判断sub是否与parent相等或在其之下<br>
	 * parent必须存在，且必须是directory,否则抛出{@link IllegalArgumentException}
	 * 
	 * @param parent
	 * @param sub
	 * @return
	 * @throws IOException
	 */
	private static boolean sameOrSub(final Path parent, Path sub) throws IOException {
		if (null == parent) {
			throw new NullPointerException("parent is null");
		}
		if (!Files.exists(parent) || !Files.isDirectory(parent)) {
			throw new IllegalArgumentException(String.format("the parent not exist or not directory %s", parent));
		}
		while (null != sub) {
			if (Files.exists(sub) && Files.isSameFile(parent, sub)) {
				return true;
			}
			sub = sub.getParent();
		}
		return false;
	}

	/**
	 * 判断sub是否在parent之下的文件或子文件夹<br>
	 * parent必须存在，且必须是directory,否则抛出{@link IllegalArgumentException}
	 * 
	 * @param parent
	 * @param sub
	 * @return
	 * @throws IOException
	 * @see {@link #sameOrSub(Path, Path)}
	 */
	private static boolean isSub(final Path parent, final Path sub) throws IOException {
		return (null == sub) ? false : sameOrSub(parent, sub.getParent());
	}
}
