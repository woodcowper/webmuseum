package com.webmuseum.museum.service;

import java.nio.file.Path;
import java.util.stream.Stream;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface IStorageService {

	void init();

	String storeImg(MultipartFile file, String prefix);

	Stream<Path> loadAllImg();

	Path loadImg(String filename);

	Resource loadImgAsResource(String filename);

	void deleteAllImg();

	void deleteImg(String filename);

	String storeQR(String text, String name);

	Stream<Path> loadAllQR();

	Path loadQR(String filename);

	Resource loadQRAsResource(String filename);

	void deleteAllQR();

	void deleteQR(String filename);

}