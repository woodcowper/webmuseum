package com.webmuseum.museum.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Date;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.web.multipart.MultipartFile;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageConfig;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.webmuseum.museum.service.IStorageService;

@Service
public class StorageServiceImpl implements IStorageService{

    private final String IMG_LOCATION = "files/img";
    private final String QR_LOCATION = "files/qr";
    private final Path rootLocationImg;
    private final Path rootLocationQR;

	private static final int QR_WIDTH = 250;
    private static final int QR_HEIGHT = 250;
    private static final int QR_FG_COLOR = 0xFF000002;
    private static final int QR_BG_COLOR = 0xFFFFC041;


	@Override
	public void init() {
		try {
			Files.createDirectories(rootLocationImg);
			Files.createDirectories(rootLocationQR);
		}
		catch (IOException e) {
			throw new RuntimeException("Could not initialize storage", e);
		}
	}

	@Autowired
	public StorageServiceImpl() {
		this.rootLocationImg = Paths.get(IMG_LOCATION);
		this.rootLocationQR = Paths.get(QR_LOCATION);
		init();
	}

	@Override
	public String storeImg(MultipartFile file, String prefix) {
		try {
			if (file.isEmpty()) {
				throw new RuntimeException("Failed to store empty file.");
			}
			String fileName = prefix + new Date() + file.getOriginalFilename();
			Path destinationFile = this.rootLocationImg.resolve(
					Paths.get(fileName))
					.normalize().toAbsolutePath();
			if (!destinationFile.getParent().equals(this.rootLocationImg.toAbsolutePath())) {
				// This is a security check
				throw new RuntimeException(
						"Cannot store file outside current directory.");
			}
			try (InputStream inputStream = file.getInputStream()) {
				Files.copy(inputStream, destinationFile,
					StandardCopyOption.REPLACE_EXISTING);
			}
			return fileName;
		}
		catch (IOException e) {
			throw new RuntimeException("Failed to store file.", e);
		}
	}

	@Override
	public Stream<Path> loadAllImg() {
		return loadAll(rootLocationImg);
	}

	@Override
	public Path loadImg(String filename) {
		return rootLocationImg.resolve(filename);
	}

	@Override
	public Resource loadImgAsResource(String filename) {
		return loadAsResource(rootLocationImg, filename);
	}

	@Override
	public void deleteAllImg() {
		deleteAll(rootLocationImg);
	}

	@Override
	public void deleteImg(String filename) {
		delete(rootLocationImg, filename);
	}

	@Override
	public String storeQR(String text, String name) {
		String fileName = "QR" + new Date() + name + ".png";
		QRCodeWriter qrCodeWriter = new QRCodeWriter();
        BitMatrix bitMatrix;
		try {
			bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, QR_WIDTH, QR_HEIGHT);
			MatrixToImageConfig conf = new MatrixToImageConfig( QR_FG_COLOR , QR_BG_COLOR ) ;

			Path destinationFile = this.rootLocationQR.resolve(
					Paths.get(fileName))
					.normalize().toAbsolutePath();
			System.out.println("--------DIRECTORY: " + destinationFile);
			if (!destinationFile.getParent().equals(this.rootLocationQR.toAbsolutePath())) {
				throw new RuntimeException(
						"Cannot store file outside current directory.");
			}
			MatrixToImageWriter.writeToPath(bitMatrix, "PNG", destinationFile, conf);
		} catch (WriterException e) {
			throw new RuntimeException("Failed to encode QR.", e);
		} catch (IOException e) {
			throw new RuntimeException("Failed to store QR.", e);
		}
		return fileName;
        
	}

	@Override
	public Stream<Path> loadAllQR() {
		return loadAll(rootLocationQR);
	}

	@Override
	public Path loadQR(String filename) {
		return rootLocationQR.resolve(filename);
	}

	@Override
	public Resource loadQRAsResource(String filename) {
		return loadAsResource(rootLocationQR, filename);
	}

	@Override
	public void deleteAllQR() {
		deleteAll(rootLocationQR);
	}

	@Override
	public void deleteQR(String filename) {
		delete(rootLocationQR, filename);
	}

	private Stream<Path> loadAll(Path rootLocation) {
		try {
			return Files.walk(rootLocation, 1)
				.filter(path -> !path.equals(rootLocation))
				.map(rootLocation::relativize);
		}
		catch (IOException e) {
			throw new RuntimeException("Failed to read stored files", e);
		}
	}

	private Resource loadAsResource(Path rootLocation, String filename) {
		Path file = null;
		try {
			file = rootLocation.resolve(filename);
			Resource resource = new UrlResource(file.toUri());
			if (resource.exists() || resource.isReadable()) {
				return resource;
			}
			else {
				throw new RuntimeException(
						"Could not read file: " + file);

			}
		}
		catch (MalformedURLException e) {
			throw new RuntimeException("Could not read file: " + file, e);
		}
	}

	private void deleteAll(Path rootLocation) {
		FileSystemUtils.deleteRecursively(rootLocation.toFile());
	}

	private void delete(Path rootLocation, String filename) {
		if(filename == null || filename.isEmpty()){
			return;
		}
		Path filePath = rootLocation.resolve(filename).normalize().toAbsolutePath();
		try {
			Files.delete(filePath);
		} catch (IOException e) {
			throw new RuntimeException("Could not delete file: " + filePath, e);
		}
	}
}
