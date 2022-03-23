
/*******************************************************
 * CS4551 Multimedia Software Systems
 * 
 * Spring 2022 Homework #2 Framework
 * 
 * ColorImageDCTCoder.java
 * 
 * By Yi Zhao 03/14/2022
 *******************************************************/

public class ColorImageDCTCoder {
	private int imgWidth, imgHeight; // input image resolution
	private int fullWidth, fullHeight; // full image resolution (multiple of 8)
	private int halfWidth, halfHeight; // half image resolution (Cb/Cr in 420, multiple of 8)
	private int[][] inpR444, inpG444, inpB444; // input R/G/B planes
	private int[][] outR444, outG444, outB444; // coded R/G/B planes
	private double[][] inpY444, inpCb444, inpCr444, inpCb420, inpCr420; // input Y/Cb/Cr planes
	private double[][] outY444, outCb444, outCr444, outCb420, outCr420; // coded Y/Cb/Cr planes
	private int[][] quantY, quantCb, quantCr; // quantized DCT coefficients for Y/Cb/Cr planes
	// TOFIX - add RGB/YCbCr conversion matrix
	private double[][] fwdColorConvMatrix;
	private double[][] invColorConvMatrix;
	// TOFIX - add minimum/maximum DCT coefficient range
	private double dctCoefMinValue, dctCoefMaxValue;

	public ColorImageDCTCoder() {
	}

	// conduct DCT-based coding of one image with specified quality parameter
	public int process(String imgName, double n) {
		// open input image from file
		MImage inpImg = new MImage(imgName);
		// allocate work memory space
		int width = inpImg.getW();
		int height = inpImg.getH();
		imgWidth = width;
		imgHeight = height;
		allocate(width, height);
		
		// create output image
		//MImage outImg = new MImage(width, height); // TODO: uncomment
		
		MImage outImg = new MImage(fullWidth, fullHeight); // REMOVETHIS
		
		// encode image
		encode(inpImg, n);
		// decode image
		decode(outImg, n);
		// write recovered image to files
		String token[] = imgName.split("\\.");
		String outName = token[0] + "-coded.ppm";
		outImg.write2PPM(outName);
		return 0;
	}

	// encode one image
	protected int encode(MImage inpImg, double n) {
		// set work quantization table
		setWorkQuantTable(n);
		// E1. extract R/G/B planes from input image
		extractPlanes(inpImg, inpR444, inpG444, inpB444, imgWidth, imgHeight);
		// E2. RGB -> YCbCr, Cb/Cr 444 -> 420
		convertRGB2YCbCr(inpR444, inpG444, inpB444, inpY444, inpCb444, inpCr444, fullWidth, fullHeight);
		convert444To420(inpCb444, inpCb420, fullWidth, fullHeight);
		convert444To420(inpCr444, inpCr420, fullWidth, fullHeight);
		// E3/4. 8x8-based forward DCT, quantization
		encodePlane(inpY444, quantY, fullWidth, fullHeight, false);
		encodePlane(inpCb420, quantCb, halfWidth, halfHeight, true);
		encodePlane(inpCr420, quantCr, halfWidth, halfHeight, true);
		return 0;
	}

	// decode one image
	protected int decode(MImage outImg, double n) {
		// set work quantization table
		setWorkQuantTable(n);
		// D1/2. 8x8-based dequantization, inverse DCT
		decodePlane(quantY, outY444, fullWidth, fullHeight, false);
		decodePlane(quantCb, outCb420, halfWidth, halfHeight, true);
		decodePlane(quantCr, outCr420, halfWidth, halfHeight, true);
		// D3. Cb/Cr 420 -> 444, YCbCr -> RGB
		convert420To444(outCb420, outCb444, fullWidth, fullHeight);
		convert420To444(outCr420, outCr444, fullWidth, fullHeight);
		convertYCbCr2RGB(outY444, outCb444, outCr444, outR444, outG444, outB444, fullWidth, fullHeight);
		
		// REMOVETHIS
		outR444 = inpR444;
		outG444 = inpG444;
		outB444 = inpB444;
		
		// D4. combine R/G/B planes into output image
		combinePlanes(outImg, outR444, outG444, outB444, imgWidth, imgHeight);
		return 0;
	}

	// TOFIX - add code to set up full/half resolutions and allocate memory space
	// used in DCT-based coding
	protected int allocate(int width, int height) {
		fullWidth = width + (8 - (width % 8));
		fullHeight = height + (8 - (height % 8));
		halfWidth = fullWidth / 2;
		halfHeight = fullHeight / 2;
		
		//REMOVETHIS
		System.out.println("width = " + width);
		System.out.println("fullWidth = " + fullWidth);
		System.out.println("halfWidth = " + halfWidth);
		
		System.out.println("\nheight = " + height);
		System.out.println("fullHeight = " + fullHeight);
		System.out.println("halfHeight = " + halfHeight);
		
		inpR444 = new int[fullHeight][fullWidth];
		inpG444 = new int[fullHeight][fullWidth];
		inpB444 = new int[fullHeight][fullWidth];
		
		return 0;
	}

	// TOFIX - add code to set up work quantization table
	protected void setWorkQuantTable(double n) {
	}

	// TOFIX - add code to extract R/G/B planes from MImage
	protected void extractPlanes(MImage inpImg, int R444[][], int G444[][], int B444[][], int width, int height) {
		int x,y;
		x = 0;
		y = 0;
		int[] rgb = new int[3];
		for (y = 0; y < height; y++) {
			for (x = 0; x < width; x++) {
				inpImg.getPixel(x, y, rgb);
				R444[y][x] =  rgb[0];
				G444[y][x] =  rgb[1];
				B444[y][x] =  rgb[2];
			}
		}
		
		for (y = y; y < fullHeight; y++) {
			for (x = x; x < fullWidth; x++) {
				R444[y][x] =  0;
				G444[y][x] =  0;
				B444[y][x] =  0;
			}
		}
	}

	// TOFIX - add code to combine R/G/B planes to MImage
	protected void combinePlanes(MImage outImg, int R444[][], int G444[][], int B444[][], int width, int height) {
		int x,y;
		x = 0;
		y = 0;
		
		int[] rgb = new int[3];
		for (y = 0; y < height; y++) {
			for (x = 0; x < width; x++) {
				rgb[0] = R444[y][x];
				rgb[1] = G444[y][x];
				rgb[2] = B444[y][x];
				outImg.setPixel(x, y, rgb);
			}
		}
		
		// REMOVETHIS ... the padding should be gone, i'm just testing it out for now
		for (y = y; y < fullHeight; y++) {
			for (x = x; x < fullWidth; x++) {
				rgb[0] = R444[y][x];
				rgb[1] = G444[y][x];
				rgb[2] = B444[y][x];
				outImg.setPixel(x, y, rgb);
			}
		}
	}

	// TOFIX - add code to convert RGB to YCbCr
	protected void convertRGB2YCbCr(int R[][], int G[][], int B[][], double Y[][], double Cb[][], double Cr[][],
			int width, int height) {
	}

	// TOFIX - add code to convert YCbCr to RGB
	protected void convertYCbCr2RGB(double Y[][], double Cb[][], double Cr[][], int R[][], int G[][], int B[][],
			int width, int height) {
	}

	// TOFIX - add code to convert chrominance from 444 to 420
	protected void convert444To420(double CbCr444[][], double CbCr420[][], int width, int height) {
	}

	// TOFIX - add code to convert chrominance from 420 to 444
	protected void convert420To444(double CbCr420[][], double CbCr444[][], int width, int height) {
	}

	// TOFIX - add code to encode one plane with 8x8 FDCT and quantization
	protected void encodePlane(double plane[][], int quant[][], int width, int height, boolean chroma) {
	}

	// TOFIX - add code to decode one plane with 8x8 dequantization and IDCT
	protected void decodePlane(int quant[][], double plane[][], int width, int height, boolean chroma) {
	}

	// clip one integer
	protected int clip(int x, int a, int b) {
		if (x < a)
			return a;
		else if (x > b)
			return b;
		else
			return x;
	}

	// clip one double
	protected double clip(double x, double a, double b) {
		if (x < a)
			return a;
		else if (x > b)
			return b;
		else
			return x;
	}
}
